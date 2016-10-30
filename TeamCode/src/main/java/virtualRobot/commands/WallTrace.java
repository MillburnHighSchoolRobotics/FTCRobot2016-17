package virtualRobot.commands;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.components.Sensor;

/**
 * Created by ethachu19 on 10/29/2016.
 *
 * Traces a wall to keep completely parallel with wall
 */

public class WallTrace implements Command {
    ExitCondition exitCondition;
    Direction direction;
    private AutonomousRobot robot;
    private double target = 10;
    public WallTrace() {
        robot = Command.AUTO_ROBOT;
        direction = Direction.FORWARD;
    }
    public WallTrace(Direction d) {
        robot = Command.AUTO_ROBOT;
        direction = d;
    }
    public WallTrace(Direction d, double target) {
        robot = Command.AUTO_ROBOT;
        direction = d;
        this.target = target;
    }

    public void setDirection (Direction d) { direction = d; }

    public Direction getDirection() { return direction; }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public ExitCondition getExitCondition () {
        return exitCondition;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        double tp = 0.2;
        boolean isInterrupted = false;
        Sensor sonarLeft = direction == Direction.FORWARD ? robot.getSonarLeft() : robot.getSonarRight();
        Sensor sonarRight = direction == Direction.FORWARD ? robot.getSonarRight() : robot.getSonarLeft();
        PIDController close = new PIDController(0.008,0,0,0,target);
        PIDController allign = new PIDController(0.012,0,0,0,0);
        double currLeft, currRight, errClose = 0, errAllign;
        while (!exitCondition.isConditionMet()) {
            currLeft = sonarLeft.getValue();
            currRight = sonarRight.getValue();

            errClose = close.getPIDOutput(currLeft);
            errAllign = allign.getPIDOutput(currLeft-currRight);

            if (direction == Direction.FORWARD) {
                robot.getLBMotor().setPower(tp - errClose - errAllign);
                robot.getLFMotor().setPower(tp - errClose - errAllign);
                robot.getRFMotor().setPower(tp + errClose + errAllign);
                robot.getRBMotor().setPower(tp + errClose + errAllign);
            } else {
                robot.getLBMotor().setPower((tp - errClose - errAllign)*-1);
                robot.getLFMotor().setPower((tp - errClose - errAllign)*-1);
                robot.getRFMotor().setPower((tp + errClose + errAllign)*-1);
                robot.getRBMotor().setPower((tp + errClose + errAllign)*-1);
            }

            if(Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                isInterrupted = true;
                break;
            }
        }
        robot.stopMotors();
        return isInterrupted;
    }

    public enum Direction {
        FORWARD,
        BACKWARD
    }
}
