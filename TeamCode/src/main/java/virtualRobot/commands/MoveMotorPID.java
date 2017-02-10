package virtualRobot.commands;

import android.util.Log;

import org.firstinspires.ftc.teamcode.UpdateThread;

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
    public final static double MAX_SPEED = 100;

    private double speed;
    Motor motor;
    Sensor encoder;
    private double lastTime;
    private double lastSpeed;
    private double lastEncoder;
    private double currPower = 0;
    private double KP, KI, KD;
    private double PPC, MSC;
   private double maxAS = .06;

    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    //0.0000000000005
    //37.5, 50

    //KU = 48.5
    //TU =
    PIDController speedController;

    //PIDController speedController = new PIDController(29.1,.05197,4073.191,0);
    public MoveMotorPID(double speed, Motor motor, Sensor encoder, MotorType motorType) {
        maxAS = motorType.maxActSpeed;
        this.speed = getMappedSpeed(MathUtils.clamp(speed, 0, 100));
        this.motor = motor;
        this.encoder = encoder;
        lastTime = System.currentTimeMillis();
        lastSpeed = 0;
        lastEncoder = encoder.getRawValue();
        KP = motorType.KP;
        KI = motorType.KI;
        KD = motorType.KD;
        PPC = motorType.PPC;
        MSC = motorType.MSC;
        speedController = new PIDController(KP,KI,KD,0);
        speedController.setTarget(this.speed);

    }

    public void setExitCondition(ExitCondition exitCondition) {
        this.exitCondition = exitCondition;
    }

    //@Deprecated :P
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
        //Command.AUTO_ROBOT.addToTelemetry("DIF: ", a);

        if (System.currentTimeMillis() - lastTime > MSC ) { //1780 RPM = 333 milliseconds/cycle
            //if (!UpdateThread.allDone)
            //Log.d("MotorPID", String.valueOf(currPower));
            //Command.AUTO_ROBOT.addToTelemetry("PENS: ", "Lock");

            double time = lastTime;
            lastEncoder = encoder.getRawValue();
            lastTime = System.currentTimeMillis();
            return (a/PPC)/(System.currentTimeMillis()-time); //25.9 pulses per cycle
        }
       // Command.AUTO_ROBOT.addToTelemetry("PENS: ", "Ban");

        return lastSpeed;
    }
    //shuyd
    @Override
    public boolean changeRobotState() throws InterruptedException {
        double motorSpeed;
        while (!exitCondition.isConditionMet()) {
           lastSpeed = getSpeedOfRev();
            //Command.AUTO_ROBOT.addToTelemetry("Speed: ", lastSpeed);


//            if (Double.isNaN(motorSpeed))
//                continue;
            double oldPower = currPower;
            currPower = speedController.getPIDOutput(lastSpeed);
            currPower = MathUtils.clamp(currPower, -1, 1);
            //Command.AUTO_ROBOT.addToTelemetry("MotorPID: ", currPower);

            //Log.d("MotorPID", speed + " " + currPower + " " + lastSpeed + " " + encoder.getRawValue());
            motor.setPower(currPower == 0 ? oldPower : currPower);

        }
        return false;
    }
    private double getMappedSpeed(double speed) {
        return (maxAS/100)*speed;
    }
    public enum MotorType {
        NeverRest3_7(29.1,17.308,12.232, 25.9, 335, .06); //NevRest 3.7
        private final double KP;
        private final double KI;
        private final double KD;
        private final double PPC;
        private final double MSC;
        private final double maxActSpeed;



        private MotorType(double KP, double KI, double KD, double PPC,  double MSC, double maxAS) {
            this.KP = KP;
            this.KI = KI;
            this.KD = KD;
            this.PPC = PPC;
            this.MSC = MSC;
            this.maxActSpeed = maxAS;
        } //Kp, Ki, Kd, Pulses per cycle of motor, Milliseconds per cycle of motor, max actual speed (e.g. speed w/o mapping)
    }
}
