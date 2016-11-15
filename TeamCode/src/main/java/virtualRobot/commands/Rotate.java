package virtualRobot.commands;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.utils.MathUtils;

/**
 * Created by shant on 10/27/2015.
 * Rotates the Robot in place
 */
public class Rotate implements Command {
    private ExitCondition exitCondition;

    public static final double THRESHOLD = 0 ;
    //KU:  0.0351875, 0.0377188, 0.04025, 0.04102
    //KU: 0.0377188; TU: 106 0.04102 TU = 80
    public static final double KP =  0.024612;
    public static final double KI = 0.0006153; //0.0005131034;
    public static final double KD = 0.24612; //0.24273;

    public static final double MIN_MAX_POWER = .99;

    public static final double TOLERANCE = 0.6;

    private double power;
    private double angleInDegrees;
    private double initAngle;
    private RunMode runMode;
    private static double globalMaxPower = 1;
    private String name;

    private double time;
    private double timeLimit;
    private AtomicBoolean ab = null;
    private boolean testing = false;
    
    private PIDController pidController;

    private static AutonomousRobot robot = Command.AUTO_ROBOT;

    public static void setGlobalMaxPower(double p) {
        globalMaxPower = p;
    }

    public Rotate() {
    	
    	power = globalMaxPower;
    	
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
        
        pidController = new PIDController(KP, KI, KD, THRESHOLD);

        runMode = RunMode.WITH_ANGLE_SENSOR;

        timeLimit = -1;
    }

    public Rotate (double target) {
        this();
        this.angleInDegrees = target;
        
        pidController.setTarget(target);
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
    public Rotate (double kP, double target, double timeLimit, AtomicBoolean ab) {
        this(target, 1.0, timeLimit);
        pidController.setKP(kP);
        this.ab = ab;
        testing = true;
    }

    private Rotate(double angleInDegrees, double power, double timeLimit) {
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

    public void setTHRESHOLD (double THRESHOLD) {

    }

    @Override
    public boolean changeRobotState() throws InterruptedException{
    	boolean isInterrupted = false;
        time = System.currentTimeMillis();
        initAngle = robot.getHeadingSensor().getValue();
        switch (runMode) {
            case WITH_ANGLE_SENSOR:

                while (!exitCondition.isConditionMet() && Math.abs(angleInDegrees - robot.getHeadingSensor().getValue()) > TOLERANCE && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

                    double adjustedPower = pidController.getPIDOutput(robot.getHeadingSensor().getValue());
                    adjustedPower = MathUtils.clamp(adjustedPower, -1, 1);

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

                    if (testing) {
                        ab.set(!exitCondition.isConditionMet());
                    }

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

                while (!exitCondition.isConditionMet() && Math.abs(Math.abs(pidController.getTarget())
                        - (Math.abs(robot.getLFEncoder().getValue()) + Math.abs(robot.getLBEncoder().getValue())
                        + Math.abs(robot.getRFEncoder().getValue()) + Math.abs(robot.getRBEncoder().getValue())) / 4) > 20){//Mehmet: Unsure of relevance of 20, may need to be changed.
                    robot.getLeftRotate().setPower(Math.signum(angleInDegrees)*power);
                    robot.getRightRotate().setPower(-Math.signum(angleInDegrees)*power);
                    

                    if (Thread.currentThread().isInterrupted()) {
                        isInterrupted = true;
                        break;
                    }

                    Thread.currentThread().sleep(10);
                }
                break;
        }

    	robot.stopMotors();
        
        return isInterrupted;
        
    }

    public enum RunMode {
        WITH_ANGLE_SENSOR,
        WITH_ENCODER
    }
}
