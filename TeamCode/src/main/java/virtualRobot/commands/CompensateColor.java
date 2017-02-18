package virtualRobot.commands;

import android.util.Log;
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
    PIDController lateral = new PIDController(1.22,0,0,0,0);
//    Direction direction = Direction.FORWARD;
    double referenceAngle;
    double timeLimit;
    double multiplier;
    private static final double whiteTape = 13;

    ExitCondition exitCondition;

    public CompensateColor() {
        referenceAngle = 0; timeLimit = 10000; multiplier=2.5;
         exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
    }
    public CompensateColor(double timeLimit) {
        this();
        this.timeLimit = timeLimit;
    }

    public CompensateColor(double timeLimit, double multiplier) {
        this(timeLimit);
        this.multiplier = multiplier;
    }

    public CompensateColor(double timeLimit, double multiplier, double referenceAngle) {
        this(timeLimit, multiplier);
        this.referenceAngle = referenceAngle;
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
        PIDController rotation = new PIDController(0.008,0,0,0,referenceAngle);
        double lateralPower = 0, rotationPower = 0;
        double curr;
        double startTime = System.currentTimeMillis();
        boolean colorTriggered = false;
        while (! colorTriggered && !isInterrupted && !exitCondition.isConditionMet() && System.currentTimeMillis() - startTime < timeLimit) {

            curr = robot.getLightSensor1().getValue()*multiplier + robot.getLightSensor2().getValue() - robot.getLightSensor3().getValue() - robot.getLightSensor4().getValue()*(2.55);
            lateralPower = lateral.getPIDOutput(curr)*-1;// - pidController1.getPIDOutput(robot.getHeadingSensor().getValue());
            Log.d("CompensateColor", curr + " " + lateralPower);
            robot.addToTelemetry("CompensateColor: ", curr + " " + lateralPower);
            //rotationPower = rotation.getPIDOutput(robot.getHeadingSensor().getValue());
            rotationPower =0 ;
            robot.getLFMotor().setPower(lateralPower + rotationPower);
            robot.getLBMotor().setPower(lateralPower + rotationPower);
            robot.getRFMotor().setPower(lateralPower - rotationPower);
            robot.getRBMotor().setPower(lateralPower - rotationPower);

            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }
            if ((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)) {
                colorTriggered = true;
                robot.addToProgress("Color Triggered while compensating");
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
