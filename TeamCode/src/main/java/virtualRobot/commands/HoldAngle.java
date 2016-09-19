package virtualRobot.commands;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;

/**
 * Created by shant on 2/16/2016.
 */
public class HoldAngle implements Command {
    private ExitCondition exitCondition;

    AutonomousRobot robot = Command.AUTO_ROBOT;

    public static final double THRESHOLD = 1.0;
    public static final double KP = 0.4212;
    public static final double KI = 0;
    public static final double KD = 1.8954;

    private double angleToHold;

    private PIDController pidController;

    public HoldAngle (double angleToHold) {
        this.angleToHold = angleToHold;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };

        pidController = new PIDController(KP, KI, KD, THRESHOLD);
        pidController.setTarget(0);
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        boolean isInterrupted = false;
        while (!exitCondition.isConditionMet() && !isInterrupted) {
            double adjustedPower = pidController.getPIDOutput(robot.getHeadingSensor().getValue() - angleToHold);
            adjustedPower = Math.min(Math.max(adjustedPower, -1), 1);

            robot.getDriveLeftMotor().setPower(adjustedPower);
            robot.getDriveRightMotor().setPower(-adjustedPower);

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

        return isInterrupted;
    }
}
