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
        PIDController lateral = new PIDController(0.8,0,0,0,0);
        PIDController rotation = new PIDController(0.6,0,0,0,0);
        double lateralPower, rotationPower;
        double curr;
        while (!isInterrupted && !exitCondition.isConditionMet()) {
            curr = robot.getLightSensor1().getValue()*3 + robot.getLightSensor2().getValue() - robot.getLightSensor3().getValue() - robot.getLightSensor4().getValue()*3;
            lateralPower = lateral.getPIDOutput(curr);// - pidController1.getPIDOutput(robot.getHeadingSensor().getValue());
            rotationPower = rotation.getPIDOutput(curr);
            robot.getLFMotor().setPower(lateralPower + rotationPower);
            robot.getLBMotor().setPower(lateralPower + rotationPower);
            robot.getRFMotor().setPower(lateralPower - rotationPower);
            robot.getRBMotor().setPower(lateralPower - rotationPower);

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
