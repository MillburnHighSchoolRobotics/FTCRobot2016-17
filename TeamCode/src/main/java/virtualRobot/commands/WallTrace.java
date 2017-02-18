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
    private static boolean onBlue = false;
    private boolean slow = false;
    private double tp = 0.5;
    private double maxDistance = -1;
    PIDController close = new PIDController(0.075,0,0,0); //0.008
    PIDController allign = new PIDController(0.06,0,0,0, !onBlue ? 90 : -90);

    public WallTrace() {
        robot = Command.AUTO_ROBOT;
        direction = Direction.FORWARD;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
        close.setTarget(target);
    }
    public WallTrace(Direction d) {
        this();
        direction = d;
    }
    public WallTrace(Direction d, double target) {
        this(d);
        close.setTarget(target);
    }

    public WallTrace(Direction d, double target, double maxDistance) {
        this(d, target);
        this.maxDistance = maxDistance;
    }
    public WallTrace(Direction d, double target, double maxDistance, boolean slow) {
        this(d, target);
        this.maxDistance = maxDistance;
        this.slow = slow;
    }


    public WallTrace(Direction d, double target, double tp, double kP1, double kP2) {
        this(d, target);
        close.setKP(kP1);
        allign.setKP(kP2);
        this.tp = tp;
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
        boolean isInterrupted = false;
        UltrasonicSensor sonarLeft = direction == Direction.FORWARD ? robot.getSonarLeft() : robot.getSonarRight();
        UltrasonicSensor sonarRight = direction == Direction.FORWARD ? robot.getSonarRight() : robot.getSonarLeft();
        double currLeft, currRight, errClose = 0, errAllign = 0;
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        while (!exitCondition.isConditionMet()) {
            currLeft = sonarLeft.getFilteredValue();
            currRight = sonarRight.getFilteredValue();
            tp = (slow ? .2 : tp);
            errClose = close.getPIDOutput(currLeft) * (slow ? .2 : 1);
            errAllign = allign.getPIDOutput(robot.getHeadingSensor().getValue()) * (slow ? .2 : 1);
            robot.addToTelemetry("ThisShit", errClose + " " + currLeft + " " + currRight + " " + robot.getHeadingSensor().getValue());

            if (direction == Direction.FORWARD) {
                robot.getLBMotor().setPower(tp - errClose + errAllign);
                robot.getLFMotor().setPower(tp - errClose + errAllign);
                robot.getRFMotor().setPower(tp + errClose - errAllign);
                robot.getRBMotor().setPower(tp + errClose - errAllign);
            } else {
                robot.getLBMotor().setPower((tp - errClose - errAllign)*-1);
                robot.getLFMotor().setPower((tp - errClose - errAllign)*-1);
                robot.getRFMotor().setPower((tp + errClose + errAllign)*-1);
                robot.getRBMotor().setPower((tp + errClose + errAllign)*-1);
            }

            if(getAvgDistance() >= maxDistance && maxDistance != -1) {
                break;
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
    public static void setOnBlueSide(boolean b) {onBlue = b;}

}
