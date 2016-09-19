package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;

/**
 * Created by shant on 2/25/2016.
 */
public class MoveLift implements Command {
    PIDController liftController = new PIDController(0.005, 0, 0, 0);

    private AutonomousRobot robot = Command.AUTO_ROBOT;
    private ExitCondition exitCondition;
    private RunMode runMode;
    private Direction direction;

    private double targetEncoder;

    public MoveLift () {
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };

        runMode = RunMode.CONTINUOUS;
        direction = Direction.OUT;

    }

    public MoveLift (RunMode runMode, Direction direction) {
        this();
        this.runMode = runMode;
        this.direction = direction;
    }

    public MoveLift (RunMode runMode, Direction direction, double target) {
        this(runMode, direction);
        targetEncoder = target;
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public ExitCondition getExitCondition() {
        return exitCondition;
    }

    public void setTarget(double target) {
        this.targetEncoder = target;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }


    @Override
    public boolean changeRobotState() throws InterruptedException {

        boolean isInterrupted = false;
        double liftPIDOut = liftController.getPIDOutput(robot.getLiftLeftMotorEncoder().getValue() - robot.getLiftRightMotorEncoder().getValue());
        liftPIDOut /= 2;
        switch (runMode) {
            case CONTINUOUS:
                if (direction == Direction.OUT) {
                    robot.getLiftRightMotor().setPower(.6 + liftPIDOut);
                    robot.getLiftLeftMotor().setPower(.6 - liftPIDOut);
                }
                else if (direction == Direction.IN) {
                    robot.getLiftRightMotor().setPower(-.6 + liftPIDOut);
                    robot.getLiftLeftMotor().setPower(-.6 - liftPIDOut);
                }

                break;
            case TO_VALUE:
                if (direction == Direction.OUT) {
                    robot.getLiftRightMotor().setPower(.6 + liftPIDOut);
                    robot.getLiftLeftMotor().setPower(.6 - liftPIDOut);

                    while (!exitCondition.isConditionMet() && ((robot.getLiftRightMotorEncoder().getValue() + robot.getLiftLeftMotorEncoder().getValue())/2) < targetEncoder) {

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
                else if (direction == Direction.IN) {
                    robot.getLiftRightMotor().setPower(-.6 + liftPIDOut);
                    robot.getLiftLeftMotor().setPower(-.6 - liftPIDOut);

                    while (!exitCondition.isConditionMet() && ((robot.getLiftRightMotorEncoder().getValue() + robot.getLiftLeftMotorEncoder().getValue())/2) > targetEncoder) {

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



                robot.getLiftLeftMotor().setPower(0);
                robot.getLiftRightMotor().setPower(0);

                break;
            default:
                break;
        }

        return isInterrupted;
    }

    public enum RunMode {
        CONTINUOUS,
        TO_VALUE
    }

    public enum Direction {
        OUT,
        IN
    }
}

