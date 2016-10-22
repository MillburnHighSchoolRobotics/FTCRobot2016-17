package virtualRobot.commands;


import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.utils.MathUtils;

/**
 * Created by shant on 10/14/2015.
 */
public class Translate implements Command {
    private ExitCondition exitCondition;
    private static double globalMaxPower = 1;
    private RunMode runMode;
    private Direction direction;
    private String name;
    public static double noAngle = Double.MIN_VALUE;

    private PIDController translateController;
    private PIDController headingController;
    private PIDController LFtranslateController, RFtranslateController, LBtranslateController, RBtranslateController;

    private double maxPower;
    private double currentValue;
    private int multiplier[] = {1,1,1,1};  //LF, RF, LB, RB

    private double time;
    private double timeLimit;

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
        headingController = new PIDController(0.2, 0, 0, 0);

        maxPower = globalMaxPower;
        currentValue = 0;
        direction = Direction.FORWARD;
        multiplier = POWER_MATRIX[direction.getCode()];
        timeLimit = -1;
        referenceAngle = Double.MIN_VALUE;
        angleModifier = 0;
        movementAngle = direction.getAngle();
    }

    public Translate(double target) {
        this();

        LFtranslateController.setTarget(target);
        RFtranslateController.setTarget(target);
        LBtranslateController.setTarget(target);
        RBtranslateController.setTarget(target);

    }

    public Translate(double target, Direction direction, double angleModifier) {
        this(target);

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
                case 0:
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    break;
                case 1:
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    multiplier[1] = -1;
                    multiplier[2]= -1;
                    break;
                case 2:
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    multiplier[1] = -1;
                    multiplier[2]= -1;
                    break;
                case 3:
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    multiplier[0] = -1;
                    multiplier[1]= -1;
                    multiplier[2] = -1;
                    multiplier[3] = - 1;
                    break;
                case 4:
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(45+angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(45+angleModifier));
                    multiplier[0] = -1;
                    multiplier[1]= -1;
                    multiplier[2] = -1;
                    multiplier[3] = - 1;
                    break;
                case 5:
                    LFtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*cosDegrees(angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*sinDegrees(angleModifier));
                    multiplier[0] = -1;
                    multiplier[3] = -1;
                    break;
                case 6:
                    LFtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    RFtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    LBtranslateController.setTarget(SQRT_2*target*sinDegrees(45-angleModifier));
                    RBtranslateController.setTarget(SQRT_2*target*cosDegrees(45-angleModifier));
                    multiplier[0] = -1;
                    multiplier[3] = -1;
                    break;
                case 7:
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
                LFtranslateController.setTarget(target);
                RFtranslateController.setTarget(target);
                LBtranslateController.setTarget(target);
                RBtranslateController.setTarget(target);
            }
            else {
                //once again, multiply by sqrt(2) to math up with the end goal of 2*sqrt(2)*target.
                LFtranslateController.setTarget(target *Math.sqrt(2));
                RFtranslateController.setTarget(target * Math.sqrt(2));
                LBtranslateController.setTarget(target* Math.sqrt(2));
                RBtranslateController.setTarget(target*Math.sqrt(2));
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
        headingController.setTarget(this.referenceAngle);
    }

    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name) {
        this (target, direction, angleModifier, maxPower, referenceAngle);
        this.name = name;
    }

    public Translate(double target, Direction direction, double angleModifier, double maxPower, double referenceAngle, String name, double timeLimit) {
        this(target, direction, angleModifier, maxPower, referenceAngle, name);
        this.timeLimit = timeLimit;
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
                if (movementAngle >= 0 && movementAngle <= 90) { //quadrant 1

                    scale = Math.abs(sinDegrees(movementAngle -45) / cosDegrees(movementAngle - 45));

                    robot.getLFMotor().setPower(maxPower * POWER_MATRIX[0][0]);
                    robot.getRFMotor().setPower(maxPower * POWER_MATRIX[0][1] * scale);
                    robot.getLBMotor().setPower(maxPower * POWER_MATRIX[0][2] * scale);
                    robot.getRBMotor().setPower(maxPower * POWER_MATRIX[0][3]);
                } else if (movementAngle > -270 && movementAngle <= -180) { //quadrant 2

                    scale = Math.abs(sinDegrees(movementAngle - 135) / cosDegrees(movementAngle - 135));
                    scale *= -1;
                    robot.getLFMotor().setPower(maxPower * POWER_MATRIX[2][0] * scale);
                    robot.getRFMotor().setPower(maxPower * POWER_MATRIX[2][1]);
                    robot.getLBMotor().setPower(maxPower * POWER_MATRIX[2][2]);
                    robot.getRBMotor().setPower(maxPower * POWER_MATRIX[2][3] * scale );
                } else if (movementAngle > -180 && movementAngle <= -90) { //quadrant 3
                    scale = Math.abs(sinDegrees(movementAngle - 225) / cosDegrees(movementAngle - 225));
                    Log.d("aaa",  "Quadrant 3: " + scale);

                    robot.getLFMotor().setPower(maxPower * POWER_MATRIX[4][0]);
                    robot.getRFMotor().setPower(maxPower * POWER_MATRIX[4][1] * scale );
                    robot.getLBMotor().setPower(maxPower * POWER_MATRIX[4][2] * scale );
                    robot.getRBMotor().setPower(maxPower * POWER_MATRIX[4][3]);
                    Log.d("aaa", robot.getLFMotor().getPower() + " " + robot.getRFMotor().getPower() + " " + robot.getLBMotor().getPower() + " " + robot.getRBMotor().getPower());
                } else if (movementAngle > -90 && movementAngle <= 0) { //quadrant 4

                    scale = Math.abs(sinDegrees(movementAngle - 315) / cosDegrees(movementAngle - 315));
                    scale *= -1;
                    robot.getLFMotor().setPower(maxPower * POWER_MATRIX[6][0] * scale);
                    robot.getRFMotor().setPower(maxPower * POWER_MATRIX[6][1]);
                    robot.getLBMotor().setPower(maxPower * POWER_MATRIX[6][2]);
                    robot.getRBMotor().setPower(maxPower * POWER_MATRIX[6][3] * scale);
                }



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
            boolean LFnotDone = true;
            boolean RFnotDone = true;
            boolean LBnotDone= true;
            boolean RBnotDone = true;
            if (robot.getLFEncoder().getValue() >= LFtranslateController.getTarget()) {
                robot.getLFMotor().setPower(0);
                LFnotDone = false;
            }
            if (robot.getRFEncoder().getValue() >= RFtranslateController.getTarget()) {
                robot.getRFMotor().setPower(0);
                RFnotDone = false;
            }
            if (robot.getLBEncoder().getValue() >= LBtranslateController.getTarget()) {
                robot.getLBMotor().setPower(0);
                LBnotDone = false;
            }
            if (robot.getRBEncoder().getValue() >= RBtranslateController.getTarget()) {
                robot.getRBMotor().setPower(0);
                RBnotDone = false;
            }
            notDone = LFnotDone || RFnotDone || LBnotDone || RBnotDone;

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
       
        while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                && Math.abs(LFvalue - LFtranslateController.getTarget()) > TOLERANCE
                && Math.abs(RFvalue - RFtranslateController.getTarget()) > TOLERANCE
                && Math.abs(LBvalue - LBtranslateController.getTarget()) > TOLERANCE
                && Math.abs(RBvalue - RBtranslateController.getTarget()) > TOLERANCE
                && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

            LFvalue = Math.abs(robot.getLFEncoder().getValue());
            RFvalue = Math.abs(robot.getRFEncoder().getValue());
            LBvalue = Math.abs(robot.getLBEncoder().getValue());
            RBvalue = Math.abs(robot.getRBEncoder().getValue());



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
            boolean[] issueArray = {false,false,false,false}; //if the angle modifier is = 0, and for_right is too far to the right or left will be true, perfect = false. Second element same thing but for back_right, third for Back_left, 4th for Forward_left
            // headingOutput <0 = too far to the left, >0 = too far to the right
            /*if (angleModifier != 0) {
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
            else {
                if (issueArray[0] == true && headingOutput == 0) {
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
                }
                switch(direction) {
                    case FORWARD:
                        if (headingOutput > 0){
                            RFPower+= headingOutput;
                            LBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            RFPower-= Math.abs(headingOutput);
                            LBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case FORWARD_RIGHT:
                        if (headingOutput > 0){ 
                            issueArray[0] = true;
                            multiplier[1] = 1;
                            multiplier[2] = 1;
                            RFPower+= headingOutput;
                            LBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[0] = true;
                            multiplier[1] = -1;
                            multiplier[2] = -1;
                            RFPower+= Math.abs(headingOutput);
                            LBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case RIGHT:
                        if (headingOutput > 0) {
                            RFPower-= headingOutput;
                            LBPower-= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            RFPower+= Math.abs(headingOutput);
                            LBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD_RIGHT:
                        if (headingOutput > 0){
                            issueArray[1] = true;
                            multiplier[0] = -1;
                            multiplier[3] = -1;
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;

                        }
                        else if (headingOutput < 0) {
                            issueArray[1] = true;
                            multiplier[0] = 1;
                            multiplier[3] = 1;
                            LFPower+= Math.abs(headingOutput);
                            RBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD:
                        if (headingOutput > 0) {
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            LFPower-= Math.abs(headingOutput);
                            RBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD_LEFT:
                        if (headingOutput > 0){
                            issueArray[2] = true;
                            multiplier[1] = 1;
                            multiplier[2] = 1;
                            RFPower+=headingOutput;
                            LBPower+=headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[2] = true;
                            multiplier[1] = -1;
                            multiplier[2] = -1;
                            RFPower+= Math.abs(headingOutput);
                            LBPower+=Math.abs(headingOutput);
                        }
                        break;
                    case LEFT:
                        if (headingOutput > 0) {
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            LFPower-= Math.abs(headingOutput);
                            RBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case FORWARD_LEFT:
                        if (headingOutput > 0){
                            issueArray[3] = true;
                            multiplier[0] = -1;
                            multiplier[3] = -1;
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[3] = true;
                            multiplier[0] = 1;
                            multiplier[3] = 1;
                            LFPower+= Math.abs(headingOutput);
                            RBPower+= Math.abs(headingOutput);
                        }
                        break;
                        

                }

            }*/


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

        break;
        //TODO: heading
        case HEADING_ONLY:

        while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

            double headingOutput = headingController.getPIDOutput(robot.getHeadingSensor().getValue());
            headingOutput = MathUtils.clamp(headingOutput, -1, 1);

            double LFPower = maxPower;
            double RFPower = maxPower;
            double LBPower = maxPower;
            double RBPower = maxPower;
              boolean[] issueArray = {false,false,false,false}; //if the angle modifier is = 0, and for_right is too far to the right or left will be true, perfect = false. Second element same thing but for back_right, third for Back_left, 4th for Forward_left
            // headingOutput <0 = too far to the left, >0 = too far to the right
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
            else {
                if (issueArray[0] == true && headingOutput == 0) {
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
                }
                switch(direction) {
                    case FORWARD:
                        if (headingOutput > 0){
                            RFPower+= headingOutput;
                            LBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            RFPower-= Math.abs(headingOutput);
                            LBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case FORWARD_RIGHT:
                        if (headingOutput > 0){ 
                            issueArray[0] = true;
                            multiplier[1] = 1;
                            multiplier[2] = 1;
                            RFPower+= headingOutput;
                            LBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[0] = true;
                            multiplier[1] = -1;
                            multiplier[2] = -1;
                            RFPower+= Math.abs(headingOutput);
                            LBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case RIGHT:
                        if (headingOutput > 0) {
                            RFPower-= headingOutput;
                            LBPower-= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            RFPower+= Math.abs(headingOutput);
                            LBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD_RIGHT:
                        if (headingOutput > 0){
                            issueArray[1] = true;
                            multiplier[0] = -1;
                            multiplier[3] = -1;
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;

                        }
                        else if (headingOutput < 0) {
                            issueArray[1] = true;
                            multiplier[0] = 1;
                            multiplier[3] = 1;
                            LFPower+= Math.abs(headingOutput);
                            RBPower+= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD:
                        if (headingOutput > 0) {
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            LFPower-= Math.abs(headingOutput);
                            RBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case BACKWARD_LEFT:
                        if (headingOutput > 0){
                            issueArray[2] = true;
                            multiplier[1] = 1;
                            multiplier[2] = 1;
                            RFPower+=headingOutput;
                            LBPower+=headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[2] = true;
                            multiplier[1] = -1;
                            multiplier[2] = -1;
                            RFPower+= Math.abs(headingOutput);
                            LBPower+=Math.abs(headingOutput);
                        }
                        break;
                    case LEFT:
                        if (headingOutput > 0) {
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            LFPower-= Math.abs(headingOutput);
                            RBPower-= Math.abs(headingOutput);
                        }
                        break;
                    case FORWARD_LEFT:
                        if (headingOutput > 0){
                            issueArray[3] = true;
                            multiplier[0] = -1;
                            multiplier[3] = -1;
                            LFPower+= headingOutput;
                            RBPower+= headingOutput;
                        }
                        else if (headingOutput < 0) {
                            issueArray[3] = true;
                            multiplier[0] = 1;
                            multiplier[3] = 1;
                            LFPower+= Math.abs(headingOutput);
                            RBPower+= Math.abs(headingOutput);
                        }
                        break;
                        

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
        BACKWARD_RIGHT(3, -45), //-45 degrees
        BACKWARD(4, -90), //-90 degrees
        BACKWARD_LEFT(5, -135), //-135 degrees
        LEFT(6, -180), //-180 degrees
        FORWARD_LEFT(7, -225); //-225 degrees

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
    private double sinDegrees(double  d) {
        return Math.sin(Math.toRadians(d));
    }
    private double cosDegrees(double  d) {
        return Math.cos(Math.toRadians(d));
    }
    //KU: .002993945 .00299414  LowerBound, UpperBound
    public static final double KP = 0.0017964255;
    public static final double KI = 0.00000443561;
    public static final double KD = 0.03031468031;
    public static final double THRESHOLD = 1000;

    public static final double TOLERANCE = 10;
}
