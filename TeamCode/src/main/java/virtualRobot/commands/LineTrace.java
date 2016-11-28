package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.components.Sensor;

/**
 * Created by ethachu19 on 10/31/2016.
 *
 * Precondition: White line must be between sensors 2 and 3
 */
public class LineTrace implements Command {
    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };
    private AutonomousRobot robot;
    public LineTrace() {
        robot = Command.AUTO_ROBOT;
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public ExitCondition getExitCondition () {
        return exitCondition;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        double basePower = 0.3, adjustedPower;
        boolean isInterrupted = false;
        PIDController allign = new PIDController(2,1,0,0.15,0,true);
        double curr = 0;
        while (!exitCondition.isConditionMet()) {
            curr = robot.getLightSensor1().getValue() - robot.getLightSensor3().getValue();//*1.5 - robot.getLightSensor4().getValue()*4;
            adjustedPower = allign.getPIDOutput(curr);
            robot.getLFMotor().setPower(basePower + adjustedPower);
            robot.getLBMotor().setPower(-basePower + adjustedPower);
            robot.getRFMotor().setPower(-basePower - adjustedPower);
            robot.getRBMotor().setPower(basePower - adjustedPower);

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
