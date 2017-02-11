package virtualRobot.commands;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.SallyJoeBot;
import virtualRobot.utils.MathUtils;

/**
 * Created by shant on 10/27/2015.
 * Rotates the Robot in place
 */
public class Rotate implements Command {
    private ExitCondition exitCondition;

    public static final double THRESHOLD = 0 ;
    public static RunMode defaultMode = RunMode.WITH_ANGLE_SENSOR;

    public static void setDefaultMode(RunMode defaultMode) {
        Rotate.defaultMode = defaultMode;
    }
    //KU:  0.0351875, 0.0377188, 0.04025, 0.04102
    //KU: 0.0377188; TU: 106 0.04102 TU = 80
    public static final double KP =  0.02719146;
    public static final double KI = 0.0005724517895; //0.0005131034;
    public static final double KD = 0.3228985875; //0.24273;

    public static final double TOLERANCE = 0.6;

    private double power;
    private double angleInDegrees;
    private double initAngle;
    private RunMode runMode;
    private static double globalMaxPower = 1;
    public static boolean onBlue = false;
    private String name;

    private double time;
    private double timeLimit;
    private boolean isTesting = false;
    private AtomicBoolean stop = new AtomicBoolean(false);
    private static double currentAngle = 0;
    
    private PIDController pidController;

    private static AutonomousRobot robot = Command.AUTO_ROBOT;

    public static void setGlobalMaxPower(double p) {
        globalMaxPower = p;
    }
    public static void setOnBlueSide(boolean b) {onBlue = b;}

    public Rotate() {
    	
    	power = globalMaxPower;
    	
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
        
        pidController = new PIDController(KP, KI, KD, THRESHOLD);

        runMode = defaultMode;

        timeLimit = -1;
    }

    public Rotate (double target) {
        this();
        this.angleInDegrees = !onBlue ? target : target-180;
        
        pidController.setTarget(!onBlue ? target : target-180);
    }

    public Rotate (double angleInDegrees, double power) {
        this(angleInDegrees);
        this.power = power;
    }

    public Rotate (double angleInDegrees, double power, String name) {
        this (angleInDegrees, power);
        this.name = name;
    }

    //boolean is just to differentiate from the previous constructor
    public Rotate (double kP, double target, double timeLimit, AtomicBoolean sS) {
        this(target, 1.0, timeLimit);
        pidController.setKP(kP);
        pidController.setKD(0);
        pidController.setKI(0);
        this.stop = sS;
        this.isTesting = true;
    }

    public Rotate(double angleInDegrees, double power, double timeLimit) {
        this(angleInDegrees, power);
        this.timeLimit = timeLimit;
    }

    private Rotate (double angleInDegrees, double power, double timeLimit, String name) {
        this(angleInDegrees, power, timeLimit);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }


    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getAngleInDegrees() {
        return angleInDegrees;
    }

    public void setAngleInDegrees(double angleInDegrees) {
        this.angleInDegrees = angleInDegrees;
    }


    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }


    public ExitCondition getExitCondition () {
        return exitCondition;
    }

    public synchronized static double getCurrentAngle() {
        double ret;
        if (defaultMode == RunMode.WITH_ANGLE_SENSOR)
            throw new RuntimeException("ROTATE IS ON ANGLE SENSOR, NO USE OF CURRENT ANGLE");
        ret = currentAngle;
        return ret;
    }

    public synchronized static void resetAngle() {
        currentAngle = 0;
    }

    public void setTHRESHOLD (double THRESHOLD) {

    }

    @Override
    public boolean changeRobotState() throws InterruptedException{
    	boolean isInterrupted = false;
        time = System.currentTimeMillis();
        double adjustedPower;
        switch (runMode) {
            case WITH_ANGLE_SENSOR:
                initAngle = robot.getHeadingSensor().getValue();
                while (!exitCondition.isConditionMet() && (Math.abs(angleInDegrees - robot.getHeadingSensor().getValue()) > TOLERANCE || isTesting) && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

                    if (stop.get()) {
                        robot.stopMotors();
                        return isInterrupted;
                    }
                    adjustedPower = MathUtils.clamp(pidController.getPIDOutput(robot.getHeadingSensor().getValue()),-1,1);

                    /*double ratio = Math.abs(angleInDegrees - robot.getHeadingSensor().getValue()) / (Math.abs(angleInDegrees - initAngle));
                    double powerScaler = power;
                    if (power > MIN_MAX_POWER) {
                        powerScaler = (power-MIN_MAX_POWER)*ratio + MIN_MAX_POWER;
                    }
                    adjustedPower *= powerScaler;
*/                  //robot.getLeftRotate().setPower(adjustedPower);
                    //robot.getRightRotate().setPower(-adjustedPower);
                    robot.getLBMotor().setPower(adjustedPower);
                    robot.getLFMotor().setPower(adjustedPower);
                    robot.getRFMotor().setPower(-adjustedPower);
                    robot.getRBMotor().setPower(-adjustedPower);
                    Log.d("PIDOUTROTATE", "" + adjustedPower + " " + robot.getHeadingSensor().getValue());

                    if (Thread.currentThread().isInterrupted()) {
                        isInterrupted = true;
                        break;
                    }

                    Log.e("PIDOUTPUT", "PID OUTPUT: " + Double.toString(adjustedPower) + "HEADING: " + Double.toString(robot.getHeadingSensor().getValue()));

                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                        break;
                    }
                }
                break;
            case WITH_ENCODER:
                robot.getLFEncoder().clearValue();
                robot.getLBEncoder().clearValue();
                robot.getRFEncoder().clearValue();
                robot.getRBEncoder().clearValue();
                robot.addToProgress("Rotate Angle: " + currentAngle);
                double angle = currentAngle;
                while (!exitCondition.isConditionMet() && (Math.abs(angleInDegrees - currentAngle) > TOLERANCE || isTesting) && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)){//Mehmet: Unsure of relevance of 20, may need to be changed.
                    currentAngle = angle + ((robot.getRFEncoder().getValue()/robot.getRFMotor().getMotorType().getTicksPerRevolution()) * SallyJoeBot.wheelDiameter * Math.PI) / (Math.sqrt((Math.pow(SallyJoeBot.botWidth,2) + Math.pow(SallyJoeBot.botLength,2))) * Math.PI) * 360;
                    robot.addToTelemetry("Rotate: ", currentAngle);
                    adjustedPower = MathUtils.clamp(pidController.getPIDOutput(currentAngle),-1,1);
                    robot.getLBMotor().setPower(adjustedPower);
                    robot.getLFMotor().setPower(adjustedPower);
                    robot.getRFMotor().setPower(-adjustedPower);
                    robot.getRBMotor().setPower(-adjustedPower);

                    if (Thread.currentThread().isInterrupted()) {
                        isInterrupted = true;
                        break;
                    }

                    Thread.currentThread().sleep(10);
                }
                break;
        }
        stop.set(true);
    	robot.stopMotors();
        
        return isInterrupted;
        
    }

    public enum RunMode {
        WITH_ANGLE_SENSOR,
        WITH_ENCODER
    }
}
