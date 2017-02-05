package virtualRobot.commands;

import android.util.Log;

import virtualRobot.ExitCondition;
import virtualRobot.PIDController;
import virtualRobot.commands.Command;
import virtualRobot.components.Motor;
import virtualRobot.components.Sensor;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 2/4/17.
 */

public class MoveMotorPID implements Command {

    private double speed;
    Motor motor;
    Sensor encoder;
    private double lastTime;
    private double lastSpeed;
    private double lastEncoder;
    private double currPower = 0;

    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    //0.0000000000005
    //37.5, 50
    PIDController speedController = new PIDController(70,0,0,40,0);

    public MoveMotorPID(double speed, Motor motor, Sensor encoder) {
        this.speed = speed;
        this.motor = motor;
        this.encoder = encoder;
        lastTime = System.currentTimeMillis();
        lastSpeed = 0;
        lastEncoder = encoder.getRawValue();
        speedController.setTarget(speed);
    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    private double getMotorSpeed() {
        double timeDiff = System.currentTimeMillis() - lastTime;
        double encoderDiff = encoder.getRawValue() - lastEncoder;
        while (!(timeDiff > 5 && encoderDiff > 20)) {}
        lastSpeed = encoderDiff/timeDiff * 1000;
        lastEncoder = encoder.getRawValue();
        lastTime = System.currentTimeMillis();
        return lastSpeed;
    }
    private double getSpeedOfRev() {
        double a = encoder.getRawValue()-lastEncoder;
        Command.AUTO_ROBOT.addToTelemetry("DIF: ", a);

        if (System.currentTimeMillis() - lastTime > 335 ) { //1780 RPM = 333 milliseconds/cycle
            Log.d("MotorPID", String.valueOf(currPower));
            Command.AUTO_ROBOT.addToTelemetry("PENS: ", "Lock");

            double time = lastTime;
            lastEncoder = encoder.getRawValue();
            lastTime = System.currentTimeMillis();
            return (a/25.9)/(System.currentTimeMillis()-time); //25.9 pulses per cycle


        }
        Command.AUTO_ROBOT.addToTelemetry("PENS: ", "Ban");

        return lastSpeed;
    }
    //shuyd
    @Override
    public boolean changeRobotState() throws InterruptedException {
        double motorSpeed;
        while (!exitCondition.isConditionMet()) {
           lastSpeed = getSpeedOfRev();
            Command.AUTO_ROBOT.addToTelemetry("Speed: ", lastSpeed);


//            if (Double.isNaN(motorSpeed))
//                continue;
            double oldPower = currPower;
            currPower = speedController.getPIDOutput(lastSpeed);
            currPower = MathUtils.clamp(currPower, -1, 1);
            Command.AUTO_ROBOT.addToTelemetry("MotorPID: ", currPower);

            //Log.d("MotorPID", speed + " " + currPower + " " + lastSpeed + " " + encoder.getRawValue());
            motor.setPower(currPower == 0 ? oldPower : currPower);

        }
        return false;
    }
}
