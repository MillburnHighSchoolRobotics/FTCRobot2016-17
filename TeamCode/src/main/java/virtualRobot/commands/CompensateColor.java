package virtualRobot.commands;

import android.view.ViewDebug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.PIDController;

/**
 * Created by ethachu19 on 11/22/16.
 */

public class CompensateColor implements Command {
    AutonomousRobot robot = Command.AUTO_ROBOT;
    Direction direction = Direction.FORWARD;

    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    public CompensateColor() {}

    public CompensateColor(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    public CompensateColor(Direction direction) {
        this.direction = direction;
    }

    public ExitCondition getExitCondition() {
        return exitCondition;
    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        boolean isInterrupted = false;
        PIDController pidController = new PIDController(0.006,0,0,0,20);
        PIDController pidController1 = new PIDController(0.008,0,0,0,0);
        double adjustedPower;
        while (!isInterrupted && !exitCondition.isConditionMet()) {
            adjustedPower = pidController.getPIDOutput(robot.getColorSensor().getRed());// - pidController1.getPIDOutput(robot.getHeadingSensor().getValue());
//            if (adjustedPower > 0.1) {
//                adjustedPower -= pidController1.getPIDOutput(robot.getHeadingSensor().getValue());
//            }
            robot.getLFMotor().setPower(adjustedPower * direction.getNum());
            robot.getLBMotor().setPower(adjustedPower * direction.getNum());
            robot.getRFMotor().setPower(adjustedPower * direction.getNum());
            robot.getRBMotor().setPower(adjustedPower * direction.getNum());

            if (Thread.currentThread().isInterrupted()) {
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
        return isInterrupted;
    }

    public enum Direction {
        FORWARD(1),
        BACKWARD(-1);

        private int i;
        private Direction(int i) {
            this.i = i;
        }
        public int getNum() {
            return i;
        }
    }
}
