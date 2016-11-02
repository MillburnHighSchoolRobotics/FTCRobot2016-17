package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.components.Sensor;

/**
 * Created by ethachu19 on 10/31/2016.
 */

public class LineTrace implements Command {
    ExitCondition exitCondition;
    private AutonomousRobot robot;
    private double target;
    public LineTrace() {
        robot = Command.AUTO_ROBOT;
        target = robot.getLineSensor().getValue() - 0.7;
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public ExitCondition getExitCondition () {
        return exitCondition;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        double basePower = 0.2, adjustedPower;
        boolean isInterrupted = false;
        PIDController allign = new PIDController(0.5,0,0,0,target);
        while (!exitCondition.isConditionMet()) {
            adjustedPower = allign.getPIDOutput(robot.getLineSensor().getValue());
            robot.getLFMotor().setPower(-basePower - adjustedPower);
            robot.getLBMotor().setPower(basePower - adjustedPower);
            robot.getRFMotor().setPower(basePower + adjustedPower);
            robot.getRBMotor().setPower(-basePower + adjustedPower);

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
}
