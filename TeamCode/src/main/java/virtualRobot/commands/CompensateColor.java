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
//    Direction direction = Direction.FORWARD;
    double referenceAngle;
    double timeLimit;

    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    public CompensateColor() { referenceAngle = 0; timeLimit = 10; }

    public CompensateColor(double referenceAngle) { this(); this.referenceAngle = referenceAngle; }

    public CompensateColor(double referenceAngle, double timeLimit) { this.timeLimit = timeLimit; this.referenceAngle = referenceAngle; }

    public ExitCondition getExitCondition() {
        return exitCondition;
    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        boolean isInterrupted = false;
        PIDController lateral = new PIDController(1,0.1,0,0,0.1);
        PIDController rotation = new PIDController(0.08,0,0,0,referenceAngle);
        double lateralPower, rotationPower = 0;
        double curr;
        double startTime = System.currentTimeMillis();
        while (!isInterrupted && !exitCondition.isConditionMet() && System.currentTimeMillis() - startTime < timeLimit*1000) {
            curr = robot.getLightSensor1().getValue()*3 + robot.getLightSensor2().getValue() - robot.getLightSensor3().getValue() - robot.getLightSensor4().getValue()*3;
            lateralPower = lateral.getPIDOutput(curr)*-1;// - pidController1.getPIDOutput(robot.getHeadingSensor().getValue());
            rotationPower = rotation.getPIDOutput(robot.getHeadingSensor().getValue());
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

//    public enum Direction {
//        FORWARD(1),
//        BACKWARD(-1);
//
//        private int i;
//        private Direction(int i) {
//            this.i = i;
//        }
//        public int getNum() {
//            return i;
//        }
//    }
}
