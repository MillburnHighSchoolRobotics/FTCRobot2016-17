package virtualRobot.commands;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;

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

    private double maxPower;
    private double currentValue;
    private double multiplier;

    private double time;
    private double timeLimit;

    private double referenceAngle;

    private static final AutonomousRobot robot = Command.AUTO_ROBOT;

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

        translateController = new PIDController(KP, KI, KD, THRESHOLD);
        headingController = new PIDController(0.2, 0, 0, 0);

        maxPower = globalMaxPower;
        currentValue = 0;
        direction = Direction.FORWARD;
        multiplier = 1;
        timeLimit = -1;
        referenceAngle = Double.MIN_VALUE;
    }

    public Translate(double target) {
        this();

        translateController.setTarget(target);
    }

    public Translate(double target, Direction direction) {
        this(target);

        this.direction = direction;

        multiplier = (direction == Direction.FORWARD ? 1 : -1);
    }

    public Translate(double target, Direction direction, double maxPower) {
        this(target, direction);

        this.maxPower = maxPower;
    }

    public Translate(double target, Direction direction, double maxPower, double referenceAngle) {
        this (target, direction, maxPower);

        this.referenceAngle = referenceAngle;
        headingController.setTarget(this.referenceAngle);
    }

    public Translate(double target, Direction direction, double maxPower, double referenceAngle, String name) {
        this (target, direction, maxPower, referenceAngle);
        this.name = name;
    }

    public Translate(double target, Direction direction, double maxPower, double referenceAngle, String name, double timeLimit) {
        this(target, direction, maxPower, referenceAngle, name);
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
    	
        switch (runMode) {
            case CUSTOM:
            	
            	robot.getDriveLeftMotor().setPower(maxPower * multiplier);
            	robot.getDriveRightMotor().setPower(maxPower * multiplier);

                while (!exitCondition.isConditionMet() && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {
                	
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
            	
            	robot.getDriveLeftMotorEncoder().clearValue();
            	robot.getDriveRightMotorEncoder().clearValue();
            	
            	robot.getDriveLeftMotor().setPower(maxPower * multiplier);
            	robot.getDriveRightMotor().setPower(maxPower * multiplier);
            	
            	while (!exitCondition.isConditionMet() && currentValue < translateController.getTarget() && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {
            		
            		currentValue = Math.abs((Math.abs(robot.getDriveLeftMotorEncoder().getValue()) + Math.abs(robot.getDriveRightMotorEncoder().getValue())) / 2);
            		
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

            	robot.getDriveLeftMotorEncoder().clearValue();
            	robot.getDriveRightMotorEncoder().clearValue();

                while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                        && Math.abs(currentValue - translateController.getTarget()) > TOLERANCE
                        && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

                    double left = Math.abs(robot.getDriveLeftMotorEncoder().getValue());
                    double right = Math.abs(robot.getDriveRightMotorEncoder().getValue());

                    currentValue = Math.abs((left + right) / 2);

                    double pidOutput = translateController.getPIDOutput(currentValue);
                    pidOutput = Math.min(Math.max(pidOutput, -1), 1);
                    pidOutput *= maxPower;

                    double headingOutput = headingController.getPIDOutput(robot.getHeadingSensor().getValue());
                    headingOutput = Math.min(Math.max(headingOutput, -1), 1);

                    double leftPower = pidOutput;
                    double rightPower = pidOutput;

                    if (multiplier > 0 && headingOutput > 0) {
                        rightPower -= headingOutput;
                        leftPower += headingOutput;
                    }

                    if (multiplier > 0 && headingOutput < 0) {
                        leftPower += headingOutput;
                        rightPower -= headingOutput;
                    }

                    if (multiplier < 0 && headingOutput > 0) {
                        leftPower -= headingOutput;
                        rightPower += headingOutput;
                    }

                    if (multiplier < 0 && headingOutput < 0) {
                        rightPower += headingOutput;
                        leftPower -= headingOutput;
                    }

                    Log.d("pidoutput", Double.toString(pidOutput));

                    robot.getDriveRightMotor().setPower(rightPower * multiplier);
                    robot.getDriveLeftMotor().setPower(leftPower * multiplier);
                    
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
            case HEADING_ONLY:

                while (!Thread.currentThread().isInterrupted() && !exitCondition.isConditionMet()
                        && (timeLimit == -1 || (System.currentTimeMillis() - time) < timeLimit)) {

                    double headingOutput = headingController.getPIDOutput(robot.getHeadingSensor().getValue());
                    headingOutput = Math.min(Math.max(headingOutput, -1), 1);

                    double leftPower = maxPower;
                    double rightPower = maxPower;

                    if (multiplier > 0 && headingOutput > 0) {
                        rightPower -= headingOutput;
                        leftPower += headingOutput;
                    }

                    if (multiplier > 0 && headingOutput < 0) {
                        leftPower += headingOutput;
                        rightPower -= headingOutput;
                    }

                    if (multiplier < 0 && headingOutput > 0) {
                        leftPower -= headingOutput;
                        rightPower += headingOutput;
                    }

                    if (multiplier < 0 && headingOutput < 0) {
                        rightPower += headingOutput;
                        leftPower -= headingOutput;
                    }

                    robot.getDriveRightMotor().setPower(rightPower * multiplier);
                    robot.getDriveLeftMotor().setPower(leftPower * multiplier);

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
        
        robot.getDriveLeftMotor().setPower(0);
        robot.getDriveRightMotor().setPower(0);
        
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
    	this.multiplier = (direction == Direction.FORWARD ? 1 : -1);
    }

    public enum RunMode {
        CUSTOM,
        WITH_ENCODERS,
        WITH_PID,
        HEADING_ONLY
    }
    
    public enum Direction {
    	FORWARD,
    	BACKWARD
    }

    public static final double KP = 0.010125;
    public static final double KI = 0.0000;
    public static final double KD = 0.031641;
    public static final double THRESHOLD = 1000;
    
    public static final double TOLERANCE = 10;
}
