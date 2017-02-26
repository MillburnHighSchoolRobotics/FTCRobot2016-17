package virtualRobot.commands;


import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.utils.MathUtils;

/**
 * Created by shant on 10/14/2015.
 * Transaltes the Robot In any direction
 */
public class Translate implements Command {
    private ExitCondition exitCondition;
    private static double globalMaxPower = 1;
    private double TOLERANCE = 50;
   static double globalReferenceAngleModifier = 0;
    static boolean gloablAngleSet = false;
    final static double blueAngle = -180;
    private static boolean blueSide = false;
    private RunMode runMode;
    private Direction direction;
    private String name;
    public static double noAngle = Double.MIN_VALUE;

    public PIDController translateController;
    private PIDController headingController, headingOnlyController;
    private PIDController LFtranslateController, RFtranslateController, LBtranslateController, RBtranslateController;

    private double maxPower;
    private double currentValue;
    private int multiplier[] = {1,1,1,1};  //LF, RF, LB, RB

    private AtomicBoolean stop = new AtomicBoolean(false);
    private boolean testing = false;

    private double time;
    private double timeLimit = -1;
    private double myTarget;
    private double referenceAngle;

    private double angleModifier; //(0-45) degrees, subtracts that angle from current movement (e.g. FORWARD_RIGTHT with angleModifier of 10, would move at 35 degrees, FORWARD_LEFT with same modifier would move at 125 degrees)
    private double movementAngle; //represents the actual angle the robot moves at
    private static final double SQRT_2 = Math.sqrt(2);
    private static final AutonomousRobot robot = Command.AUTO_ROBOT;

    private static final int POWER_MATRIX[][] = { //for each of the directions

            { 1, 1, 1, 1 },
            { 1, 0, 0, 1 },
            { 1, -1, -1, 1 },
            { 0, -1, -1, 0 },
            { -1, -1, -1, -1 },
            { -1, 0, 0, -1 },
            { -1, 1, 1, -1 },
            { 0, 1, 1,   0 }
    };

    public static void setGlobalMaxPower(double p) {
        globalMaxPower = p;
    }
    public static void setOnBlueSide(boolean onBlue) {blueSide= onBlue;}

    public Translate() {
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };

        runMode = RunMode.WITH_PID;

        LFtranslateController = new PIDController(KP, KI, KD, THRESHOLD);
        RFtranslateController = new PIDController(KP, KI, KD, THRESHOLD);
        LBtranslateController = new PIDController(KP, KI, KD, THRESHOLD);
        RBtranslateController = new PIDController(KP, KI, KD, THRESHOLD);
        headingController = new PIDController(0.11, 0, 0, 0); //.04
        headingOnlyController = new PIDController(0.11, 0, 0, 0); //.04
        if (blueSide) {
            headingController.setTarget(blueAngle);
            headingOnlyController.setTarget(blueAngle);
        }
        translateController = new PIDController(KPt, KIt, KDt, THRESHOLDt);
        maxPower = globalMaxPower;
        currentValue = 0;
        direction = Direction.FORWARD;
        multiplier = POWER_MATRIX[direction.getCode()]; //When Direction is FORWARD, code is 0
        timeLimit = -1;
        if (!gloablAngleSet)
        referenceAngle = Double.MIN_VALUE;
        else {
            referenceAngle = globalReferenceAngleModifier;
            headingOnlyController.setTarget(referenceAngle);
            headingController.setTarget(referenceAngle);
        }
        angleModifier = 0;
        movementAngle = direction.getAngle();
    }


    public Translate(RunMode runMode, Direction direction,double angleModifier, double maxPower) {
        this();
        this.runMode = runMode;
        this.maxPower = maxPower;
        this.direction = direction;
        movementAngle = this.direction.getAngle()-angleModifier;
        multiplier = POWER_MATRIX[direction.getCode()];

    }
    public Translate(double target, Direction direction, double angleModifier) {
        this();
        this.myTarget = target;
        this.direction = direction;
        this.angleModifier = MathUtils.clamp(angleModifier, 0, 45);
        if (angleModifier == 45) {
            this.direction = this.direction.getNext();
            angleModifier = 0;
        }

        movementAngle = this.direction.getAngle()-angleModifier;
        if (angleModifier != 0) { //some trig, based on angleModifier. Basically the goal is to get the resultant between two wheels to be sqrt(2)*target, for a total resultant of 2*sqrt(2)*target. This is done to match up the distance with a translate forward/back/side (which would have a net movement of 2*sqrt(2)*target)
            multiplier = POWER_MATRIX[0];
            switch(direction.getCode()){
                case 0: //Forward
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    break;
                case 1: //Forward Right
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    multiplier[1] = -1;
                    multiplier[2]= -1;
                    break;
                case 2: //Right
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    multiplier[1] = -1;
                    multiplier[2]= -1;
                    break;
                case 3: //Backward Right
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    multiplier[0] = -1;
                    multiplier[1]= -1;
                    multiplier[2] = -1;
                    multiplier[3] = -1;
                    break;
                case 4: //Backward
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    multiplier[0] = -1;
                    multiplier[1]= -1;
                    multiplier[2] = -1;
                    multiplier[3] = -1;
                    break;
                case 5: //Backward Left
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    multiplier[0] = -1;
                    multiplier[3] = -1;
                    break;
                case 6: //Left
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    multiplier[0] = -1;
                    multiplier[3] = -1;
                    break;
                case 7: //Forward Left
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    break;
            }
        }
        else {
            multiplier = POWER_MATRIX[direction.getCode()];
            if (direction.getCode() % 2 == 0) {
                translateController.setTarget(target);
                LFtranslateController.setTarget(target);
                RFtranslateController.setTarget(target);
                LBtranslateController.setTarget(target);
                RBtranslateController.setTarget(target);

            }
            else {
                //once again, multiply by sqrt(2) to math up with the end goal of 2*sqrt(2)*target.
                translateController.setTarget(target * SQRT_2);
                LFtranslateController.setTarget(target * SQRT_2);
                RFtranslateController.setTarget(target * SQRT_2);
                LBtranslateController.setTarget(target * SQRT_2);
                RBtranslateController.setTarget(target * SQRT_2);

            }

        }
    }

    public Translate(double target, Direction direction, double angleModifier, double maxPower) {
        this(target, direction, angleModifier);

        this.maxPower = maxPower;
    }

    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle) {
        this (target, direction, angleModifier, maxPower);

        this.referenceAngle = referenceAngle;
        headingController.setTarget(this.referenceAngle  + (blueSide ? -180 : 0) + (globalReferenceAngleModifier));
        headingOnlyController.setTarget(this.referenceAngle+(blueSide ? -180 : 0) + (globalReferenceAngleModifier));

    }
    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, boolean useGlobalAngle) {
        this (target, direction, angleModifier, maxPower);

        this.referenceAngle = referenceAngle;
        headingController.setTarget(this.referenceAngle  + (blueSide ? -180 : 0) + (useGlobalAngle ? globalReferenceAngleModifier : 0));
        headingOnlyController.setTarget(this.referenceAngle+(blueSide ? -180 : 0) + (useGlobalAngle ? globalReferenceAngleModifier: 0));

    }

    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name) {
        this (target, direction, angleModifier, maxPower, referenceAngle);
        this.name = name;
    }
    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name, boolean useGlobalAngle) {
        this (target, direction, angleModifier, maxPower, referenceAngle, useGlobalAngle);
        this.name = name;
    }
    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name, double timeLimit) {
        this(target, direction, angleModifier, maxPower, referenceAngle, name);
        this.timeLimit = timeLimit;
    }
    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name, boolean useGlobalAngle, double timeLimit) {
        this(target, direction, angleModifier, maxPower, referenceAngle, name, useGlobalAngle);
        this.timeLimit = timeLimit;
    }

    public Translate(double kP, int i, int timeLimit, AtomicBoolean shouldStop, Translate.Direction dir) {
        this(i,dir,0,1,0,"AutoPID",timeLimit);
        headingController.setKP(0);
        setKPRotate(0);
        translateController.setKP(kP);
        translateController.setKI(0);
        translateController.setKD(0);
        testing = true;
        stop = shouldStop;
        THRESHOLDt = 0;
    }


    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public ExitCondition getExitCondition () {
        return exitCondition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {


        boolean isInterrupted = false;
        time = System.currentTimeMillis();
        double scale = 0;
        switch (runMode) {
            case CUSTOM:
                double LF; double RF; double LB; double RB;
                LF= 0;
                RF=0;
                LB=0;
                RB=0;
                double power = maxPower;
                if (movementAngle >= 0 && movementAngle <= 90) { //quadrant 1
                    scale = MathUtils.sinDegrees(movementAngle-45) / MathUtils.cosDegrees(movementAngle-45);
                    LF = power * POWER_MATRIX[0][0];
                    RF = power * POWER_MATRIX[0][1] * scale;
                    LB = power * POWER_MATRIX[0][2] * scale;
                    RB = power * POWER_MATRIX[0][3];
                } else if (movementAngle  > 90 && movementAngle <= 180 ) { //quadrant 2
                    power *= -1;
                    scale = MathUtils.sinDegrees(movementAngle - 135) / MathUtils.cosDegrees(movementAngle - 135);
                    LF = (power * POWER_MATRIX[2][0] * scale);
                    RF = (power * POWER_MATRIX[2][1]);
                    LB = (power * POWER_MATRIX[2][2]);
                    RB = (power * POWER_MATRIX[2][3] * scale );
                } else if (movementAngle > 180 && movementAngle <= 270) { //quadrant 3
                    scale = MathUtils.sinDegrees(movementAngle-225) / MathUtils.cosDegrees(movementAngle-225);
                    LF = (power * POWER_MATRIX[4][0]);
                    RF = (power * POWER_MATRIX[4][1] * scale );
                    LB = (power * POWER_MATRIX[4][2] * scale );
                    RB = (power * POWER_MATRIX[4][3]);
                    Log.d("aaa", robot.getLFMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRBMotor().getPower());
                } else if (movementAngle > 270 && movementAngle < 360) { //quadrant 4
                    power *= -1;
                    scale = MathUtils.sinDegrees(movementAngle - 315) / MathUtils.cosDegrees(movementAngle-315);
                    LF = (power * POWER_MATRIX[6][0] * scale);
                    RF = (power * POWER_MATRIX[6][1]);
                    LB = (power * POWER_MATRIX[6][2]);
                    RB = (power * POWER_MATRIX[6][3] * scale);
                }

                robot.getLFMotor().setPower(LF);
                robot.getLBMotor().setPower(LB);
                robot.getRBMotor().setPower(RB);
                robot.getRFMotor().setPower(RF);


        while (!exitCondition.isConditionMet() && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

            Log.d("bbb", robot.getLFMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRBMotor().getPower());
            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.currentThread().sleep(25);
            } catch (InterruptedException e) {
                isInterrupted = true;
                break;
            }
        }

        break;

        case WITH_ENCODERS:

        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();


        robot.getLFMotor().setPower(maxPower * multiplier[0]);
        robot.getRFMotor().setPower(maxPower * multiplier[1]);
        robot.getLBMotor().setPower(maxPower * multiplier[2]);
        robot.getRBMotor().setPower(maxPower * multiplier[3]);
        boolean notDone = true;
        while (!exitCondition.isConditionMet() && notDone && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {
            double LFvalue = robot.getLFEncoder().getValue();
            double RFvalue = robot.getRFEncoder().getValue();
            double LBvalue = robot.getLBEncoder().getValue();
            double RBvalue = robot.getRBEncoder().getValue();
            double position= 0;
            if (angleModifier == 0) {
                if (direction.getCode() % 2 == 0)
                    position = (((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue)) / 4));
                else
                    position = ((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue)) / 2);
            }
            else{
               //TODO: implement encoder only non-basic movement
            }
            if (position >= myTarget) {
                notDone = true;
                robot.getLFMotor().setPower(0);
                robot.getRFMotor().setPower(0);
                robot.getLBMotor().setPower(0);
                robot.getRBMotor().setPower(0);
            }
            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.currentThread().sleep(25);
            } catch (InterruptedException e) {
                isInterrupted = true;
                break;
            }
        }

        break;
        case WITH_PID:


        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
            double LFvalue = 0;
            double RFvalue = 0;
            double LBvalue = 0;
            double RBvalue = 0;
      if (angleModifier != 0) {
        while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
               && Math.abs(LFvalue - LFtranslateController.getTarget()) > TOLERANCE
                && Math.abs(RFvalue - RFtranslateController.getTarget()) > TOLERANCE
               && Math.abs(LBvalue - LBtranslateController.getTarget()) > TOLERANCE
               && Math.abs(RBvalue - RBtranslateController.getTarget()) > TOLERANCE
                && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

            LFvalue = robot.getLFEncoder().getValue();
            RFvalue = robot.getRFEncoder().getValue();
            LBvalue =robot.getLBEncoder().getValue();
            RBvalue = robot.getRBEncoder().getValue();



            double LFpidOutput = LFtranslateController.getPIDOutput(LFvalue);
            double RFpidOutput = RFtranslateController.getPIDOutput(RFvalue);
            double LBpidOutput = LBtranslateController.getPIDOutput(LBvalue);
            double RBpidOutput = RBtranslateController.getPIDOutput(RBvalue);
            LFpidOutput = MathUtils.clamp(LFpidOutput, -1,1); 
            RFpidOutput = MathUtils.clamp(RFpidOutput, -1,1); 
            LBpidOutput = MathUtils.clamp(LBpidOutput, -1,1); 
            RBpidOutput = MathUtils.clamp(RBpidOutput, -1,1); 
            Log.d("PIDOUTLF", "" + LFpidOutput);
            Log.d("PIDOUTRF", "" + RFpidOutput);
            Log.d("PIDOUTLB", "" + LBpidOutput);
            Log.d("PIDOUTRB", "" + RBpidOutput);
            LFpidOutput *= maxPower;
            RFpidOutput *= maxPower;
            LBpidOutput *= maxPower;
            RBpidOutput *= maxPower;

            double headingOutput = headingController.getPIDOutput(robot.getHeadingSensor().getValue());
            headingOutput = MathUtils.clamp(headingOutput, -1, 1);


            double LFPower = LFpidOutput;
            double RFPower = RFpidOutput;
            double LBPower = LBpidOutput;
            double RBPower = RBpidOutput;
            boolean[] issueArray = {false,false,false,false}; //if the angle modifier is = 0, and forward_right is too far to the right or to the left, this will be true, perfect = false. Second element same thing but for back_right, third for Back_left, 4th for Forward_left
            // headingOutput < 0 = too far to the left , > 0 = too far to the right
            if (angleModifier != 0) {
                if ((direction.getCode() == 0 || direction.getCode() == 5)) {
                    if (headingOutput > 0){
                        RFPower+= headingOutput;
                        LBPower+= headingOutput;
                    }
                    else if (headingOutput < 0) {
                        RFPower-= Math.abs(headingOutput);
                        LBPower-= Math.abs(headingOutput);
                    }

                }

                if ((direction.getCode() == 1 || direction.getCode() == 2)) {
                    if (headingOutput > 0) {
                        RFPower-= headingOutput;
                        LBPower-= headingOutput;
                    }
                    else if (headingOutput < 0) {
                        RFPower+= Math.abs(headingOutput);
                        LBPower+= Math.abs(headingOutput);
                    }
                }

                if ((direction.getCode() == 3 || direction.getCode() == 4 || direction.getCode() == 6 || direction.getCode() == 7)) {
                    if (headingOutput > 0) {
                        LFPower+= headingOutput;
                        RBPower+= headingOutput;
                    }
                    else if (headingOutput < 0) {
                        LFPower-= Math.abs(headingOutput);
                        RBPower-= Math.abs(headingOutput);
                    }
                }
            }

            robot.getLFMotor().setPower(LFPower * multiplier[0]);
            robot.getRFMotor().setPower(RFPower * multiplier[1]);
            robot.getLBMotor().setPower(LBPower * multiplier[2]);
            robot.getRBMotor().setPower(RBPower * multiplier[3]);



            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                isInterrupted = true;
                break;
            }

        }
      }
        else { //If angleModifier = 0
          //boolean[] issueArray = {false, false, false, false};
          while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                  && (testing || shouldKeepLooping(LFvalue, RFvalue, LBvalue, RBvalue, translateController.getTarget()) )
                  && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)
                  && !stop.get()) {
          LFvalue = robot.getLFEncoder().getValue();
          RFvalue = robot.getRFEncoder().getValue();
          LBvalue = robot.getLBEncoder().getValue();
          RBvalue = robot.getRBEncoder().getValue();
            double pidOutput;
                Log.d("direction", "" + direction.getCode() % 2);
              if (direction.getCode() %2 == 0)
                  pidOutput = translateController.getPIDOutput((((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4)));
              else
                 pidOutput = translateController.getPIDOutput(((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/2));
          pidOutput = MathUtils.clamp(pidOutput, -1, 1);
          Log.d("PIDOUT", "" + pidOutput + " Controller: " +  translateController.toString() + " Encoder Values" + Math.abs(LFvalue) + " " + Math.abs(RFvalue) + " " + Math.abs(LBvalue) + " " + Math.abs(RBvalue) + "Passing IN: " + Double.toString((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4));
          pidOutput *= maxPower;

          double headingOutput = headingController.getPIDOutput(robot.getHeadingSensor().getValue());
              headingOutput *= maxPower;
          headingOutput = MathUtils.clamp(headingOutput, -1, 1);


              double LFPower = pidOutput;
              double LBPower = pidOutput;
              double RFPower = pidOutput;
              double RBPower = pidOutput;




             /* if (issueArray[0] == true && headingOutput == 0) {
              issueArray[0] = false;
              multiplier[0] = POWER_MATRIX[direction.getCode()][0];
          }
          if (issueArray[1] == true && headingOutput == 0) {
              issueArray[1] = false;
              multiplier[1] = POWER_MATRIX[direction.getCode()][1];
          }
          if (issueArray[2] == true && headingOutput == 0) {
              issueArray[2] = false;
              multiplier[2] = POWER_MATRIX[direction.getCode()][2];
          }
          if (issueArray[3] == true && headingOutput == 0) {
              issueArray[3] = false;
              multiplier[3] = POWER_MATRIX[direction.getCode()][3];
          }*/

              double absHead = Math.abs(headingOutput);


              //Multiplier: LF, RF, LB, RB
              switch (direction) {
                  case FORWARD:
                      if (headingOutput > 0) { //Left skew
                          RFPower -= absHead;
                          RBPower -= absHead;
                          LBPower += absHead;
                          LFPower += absHead;
                      } else if (headingOutput < 0) { //Right skew
                          RFPower += absHead;
                          RBPower += absHead;
                          LBPower -= absHead;
                          LFPower -= absHead;
                      }
                      break;
                  case FORWARD_RIGHT:
                      if (headingOutput > 0) {
                          RBPower -= (absHead*2);
                          LFPower += (absHead*2);
                      } else if (headingOutput < 0) {
                          RBPower += (absHead*2);
                          LFPower -= (absHead*2);
                      }
                      break;
                  /*
                  case RIGHT:
                      if (headingOutput > 0) {
                          RFPower -= absHead;
                          RBPower += absHead;
                          LBPower -= absHead;
                          LFPower += absHead;

                      } else if (headingOutput < 0) {
                          RFPower += absHead;
                          RBPower -= absHead;
                          LBPower += absHead;
                          LFPower -= absHead;

                      }
                      break;
                      */
                  case BACKWARD_RIGHT:
                      if (headingOutput > 0) {
                          RFPower += (absHead*2);
                          LBPower -= (absHead*2);
                      } else if (headingOutput < 0) {
                          RFPower -= (absHead*2);
                          LBPower += (absHead*2);
                      }
                      break;
                  case BACKWARD:
                      if (headingOutput > 0) {
                          RFPower += absHead;
                          RBPower += absHead;
                          LBPower -= absHead;
                          LFPower -= absHead;
                      } else if (headingOutput < 0) {
                          RFPower -= absHead;
                          RBPower -= absHead;
                          LBPower += absHead;
                          LFPower += absHead;
                      }
                      break;
                  case BACKWARD_LEFT:

                      if (headingOutput > 0) {
                          RBPower += (absHead*2);
                          LFPower -= (absHead*2);
                      } else if (headingOutput < 0) {
                          RFPower -= (absHead*2);
                          LBPower += (absHead*2);
                      }
                      break;
                  /*
                  case LEFT:
                      if (headingOutput > 0) {
                          RFPower += absHead;
                          RBPower -= absHead;
                          LBPower += absHead;
                          LFPower -= absHead;

                      } else if (headingOutput < 0) {
                          RFPower -= absHead;
                          RBPower += absHead;
                          LBPower -= absHead;
                          LFPower += absHead;

                      }
                      break;
                      */
                  case FORWARD_LEFT:
                      if (headingOutput > 0) {
                          RFPower -= (absHead*2);
                          LBPower += (absHead*2);
                      } else if (headingOutput < 0) {
                          RFPower += (absHead*2);
                          LBPower -= (absHead*2);
                      }
                      break;
              }
              Log.d("TRANSLATE DIAGNOSTICS", "" + "Pid Output" + pidOutput + " Encoder Values" + Math.abs(LFvalue) + " " + Math.abs(RFvalue) + " " + Math.abs(LBvalue) + " " + Math.abs(RBvalue) + "HEADING CORRECTED POWERS" + LFPower + " " + RFPower + " "  + LBPower + " " +RBPower + "HEADINGOUTPUT: " + headingOutput + " ANGLE: " + robot.getHeadingSensor().getValue());

              Log.d("MULTIPLIED POWERS", LFPower * multiplier[0] + " " + RFPower * multiplier[1] + LBPower * multiplier[2] + RBPower * multiplier[3]);
              robot.getLFMotor().setPower(LFPower * multiplier[0]);
              robot.getRFMotor().setPower(RFPower * multiplier[1]);
              robot.getLBMotor().setPower(LBPower * multiplier[2]);
              robot.getRBMotor().setPower(RBPower * multiplier[3]);

              if (Thread.currentThread().isInterrupted()) {
                  isInterrupted = true;
                  break;
              }

              try {
                  Thread.currentThread().sleep(10);
              } catch (InterruptedException e) {
                  isInterrupted = true;
                  break;
              }
          }
      }
        break;
        //TODO: heading
        case HEADING_ONLY:
            if (angleModifier == 0) {
                while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                        && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {


                    double headingOutput = headingOnlyController.getPIDOutput(robot.getHeadingSensor().getValue());
                    headingOutput = MathUtils.clamp(headingOutput, -1, 1);


                    double LFPower = maxPower;
                    double LBPower = maxPower;
                    double RFPower = maxPower;
                    double RBPower = maxPower;


                    double absHead = Math.abs(headingOutput);


                    //Multiplier: LF, RF, LB, RB
                    switch (direction) {
                        case FORWARD:
                            if (headingOutput > 0) { //Right skew
                                RFPower -= absHead;
                                RBPower -= absHead;
                                LBPower += absHead;
                                LFPower += absHead;
                            } else if (headingOutput < 0) { //Left skew
                                RFPower += absHead;
                                RBPower += absHead;
                                LBPower -= absHead;
                                LFPower -= absHead;
                            }
                            break;
                        case FORWARD_RIGHT:
                            if (headingOutput > 0) {
                                RBPower -= absHead;
                                LFPower += absHead;
                            } else if (headingOutput < 0) {
                                RBPower += absHead;
                                LFPower -= absHead;
                            }
                            break;
                        /*case RIGHT:
                            if (headingOutput > 0) {
                                RFPower += absHead;
                                RBPower -= absHead;
                                LBPower -= absHead;
                                LFPower += absHead;

                            } else if (headingOutput < 0) {
                                RFPower -= absHead;
                                RBPower += absHead;
                                LBPower += absHead;
                                LFPower -= absHead;
                            }
                            break;*/
                        case BACKWARD_RIGHT:
                            if (headingOutput > 0) {
                                RFPower += absHead;
                                LBPower -= absHead;
                            } else if (headingOutput < 0) {
                                RFPower -= absHead;
                                LBPower += absHead;
                            }
                            break;
                        case BACKWARD:
                            if (headingOutput > 0) {
                                RFPower += absHead;
                                RBPower += absHead;
                                LBPower -= absHead;
                                LFPower -= absHead;
                            } else if (headingOutput < 0) {
                                RFPower -= absHead;
                                RBPower -= absHead;
                                LBPower += absHead;
                                LFPower += absHead;
                            }
                            break;
                        case BACKWARD_LEFT:

                            if (headingOutput > 0) {
                                RBPower += absHead;
                                LFPower -= absHead;
                            } else if (headingOutput < 0) {
                                RBPower -= absHead;
                                LFPower += absHead;
                            }
                            break;
                        /*case LEFT:
                            if (headingOutput > 0) {
                                RFPower -= absHead;
                                RBPower += absHead;
                                LBPower += absHead;
                                LFPower -= absHead;

                            } else if (headingOutput < 0) {
                                RFPower += absHead;
                                RBPower -= absHead;
                                LBPower -= absHead;
                                LFPower += absHead;
                            }
                            break;*/
                        case FORWARD_LEFT:
                            if (headingOutput > 0) {
                                RFPower -= absHead;
                                LBPower += absHead;
                            } else if (headingOutput < 0) {
                                RFPower += absHead;
                                LBPower -= absHead;
                            }
                            break;
                    }

                    Log.d("MULTIPLIED POWERS", LFPower * multiplier[0] + " " + RFPower * multiplier[1] + LBPower * multiplier[2] + RBPower * multiplier[3]);
                    double multipliedLF = LFPower*multiplier[0];
                    double multipliedRF = RFPower*multiplier[1];
                    double multipliedLB = RFPower*multiplier[2];
                    double multipliedRB = RFPower*multiplier[3];

                    robot.getLFMotor().setPower(multipliedLF);
                    robot.getRFMotor().setPower(multipliedRF);
                    robot.getLBMotor().setPower(multipliedLB);
                    robot.getRBMotor().setPower(multipliedRB);

                    if (Thread.currentThread().isInterrupted()) {
                        isInterrupted = true;
                        break;
                    }

                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                        break;
                    }
                }
            }
            else {
                //TODO: Heading_Only when angle modifier != 0
            }
        break;

        default:
        break;
    }

    robot.getLFMotor().setPower(0);
    robot.getRFMotor().setPower(0);
    robot.getLBMotor().setPower(0);
    robot.getRBMotor().setPower(0);

    return isInterrupted;

    }
    public Translate setTolerance(double tolerance) { TOLERANCE = tolerance; return this; }

    public Translate setKPRotate(double Kp) {headingController.setKP(Kp); headingOnlyController.setKP(Kp); return this;}

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public void setTarget(double target) {
        translateController.setTarget(target);
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    public void setDirection(Direction direction) {
    	this.direction = direction;
    	this.multiplier = POWER_MATRIX[direction.getCode()];
    }

    public enum RunMode {
        CUSTOM,
        WITH_ENCODERS,
        WITH_PID,
        HEADING_ONLY
    }

    public enum Direction {
        FORWARD(0, 90), //90 degrees
        FORWARD_RIGHT(1, 45), //45 degrees
        RIGHT(2, 0), //0 degrees
        BACKWARD_RIGHT(3, 315), //-45 degrees
        BACKWARD(4, 270), //-90 degrees
        BACKWARD_LEFT(5, 225), //-135 degrees
        LEFT(6, 180), //-180 degrees
        FORWARD_LEFT(7, 135); //-225 degrees

        private static Direction[] vals = values();
        private final int code;
        private final int angle;
        private Direction(int code, int angle) {
            this.code = code;
            this.angle = angle;
        }


        public int getCode() {
            return code;
        }
        public int getAngle(){
            return angle;
        }
        public Direction getNext() {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }
    public static void setGlobalAngleMod(double d) {
        globalReferenceAngleModifier = d;
        gloablAngleSet = true;
    }
    public static void resetGlobalAngleMod() {
        globalReferenceAngleModifier = 0;
        gloablAngleSet = false;
    }
    private double sinDegrees(double  d) {
        return Math.sin(Math.toRadians(d));
    }
    private double cosDegrees(double  d) {
        return Math.cos(Math.toRadians(d));
    }
    private boolean shouldKeepLooping(double LFvalue, double RFvalue, double LBvalue, double RBvalue, double target) {
        //
        if (direction.getCode() % 2 == 0)
            return Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue)  + Math.abs(LBvalue)+ Math.abs(RBvalue))/4 - translateController.getTarget()) > TOLERANCE;
        else
            return Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/2 - translateController.getTarget()) > TOLERANCE;

    }
    //KU: .002993945 .00299414  LowerBound, UpperBound
    public static final double KP = 0.0017964255;
    public static final double KI = 0.00000443561;
    public static final double KD = 0.03031468031;
    public static final double THRESHOLD = 1000;


    //0.00296875,  .003125 LowerBound, UpperBound
    //TU: 83
    //KU: .003125
    public static final double KPt  =0.00194;
    public static final double  KIt = .000061904761;
    public static final double KDt = .01535625;
    public static double THRESHOLDt = 460; //TBD

    //OLD //0.0031006, .003125
    //OLD KU: .003125
    //OLD TU; 54
    //OLDER  KU, TU, KP, THRESHOLD, TOLERANCE: .00131562, 83, .001086096, 964, 100

}
