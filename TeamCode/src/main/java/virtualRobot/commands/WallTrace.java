package virtualRobot.commands;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.components.Sensor;
import virtualRobot.components.UltrasonicSensor;

/**
 * Created by ethachu19 on 10/29/2016.
 *
 * Moves the robot such that it traces a wall to keep completely parallel with wall,
 * and stays within "target" cm of the wall
 */

public class WallTrace implements Command {
    ExitCondition exitCondition;
    Direction direction;
    private AutonomousRobot robot;
    private double target = 15;
    public WallTrace() {
        robot = Command.AUTO_ROBOT;
        direction = Direction.FORWARD;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
    }
    public WallTrace(Direction d) {
        this();
        direction = d;
    }
    public WallTrace(Direction d, double target) {
        this(d);
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
        UltrasonicSensor sonarLeft = direction == Direction.FORWARD ? robot.getSonarLeft() : robot.getSonarRight();
        UltrasonicSensor sonarRight = direction == Direction.FORWARD ? robot.getSonarRight() : robot.getSonarLeft();
        PIDController close = new PIDController(0.008,0,0,0,target);
        PIDController allign = new PIDController(0.008,0,0,0,0);
        double currLeft, currRight, errClose = 0, errAllign;
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        while (!exitCondition.isConditionMet()) {
            currLeft = sonarLeft.getFilteredValue();
            currRight = sonarRight.getFilteredValue();

            errClose = close.getPIDOutput(currLeft);
            errAllign = allign.getPIDOutput(robot.getHeadingSensor().getValue());

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
    private double getAvgDistance() {
       double LFvalue = robot.getLFEncoder().getValue();
        double RFvalue = robot.getRFEncoder().getValue();
        double LBvalue = robot.getLBEncoder().getValue();
        double RBvalue = robot.getRBEncoder().getValue();
        Log.d("AVGDIST", " " + Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4));
        return (Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4;
    }
    public enum Direction {
        FORWARD,
        BACKWARD
    }
}
