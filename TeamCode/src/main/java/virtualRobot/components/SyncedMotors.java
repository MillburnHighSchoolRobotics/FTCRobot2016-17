package virtualRobot.components;

import java.util.ArrayList;
import java.util.List;

import virtualRobot.PIDController;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 9/23/2016.
 *
 * A class to sync two motors or two sets of motors
 */
public class SyncedMotors {
    private SyncType type;
    Motor masterA;
    Motor slaveA;
    private long oldTimeA,oldTimeB;
    private double oldEncoderA,oldEncoderB;
    private Sensor encoderA;
    private Sensor encoderB;
    SyncedMotors masterB;
    SyncedMotors slaveB;
    private PIDController pid;
    private double ratio;
    private double power;

    public SyncedMotors(Motor a, Motor b, Sensor eA, Sensor eB ,double KP, double KI, double KD) {
        this.masterA = a;
        this.slaveA = b;
        this.encoderA = eA;
        this.encoderB = eB;
        pid = new PIDController(KP,KI,KD,0.01,1);
        type = SyncType.MOTORS;
        this.encoderA.clearValue();
        this.encoderB.clearValue();
    }

    public SyncedMotors(SyncedMotors a, SyncedMotors b, double KP, double KI, double KD) {
        this.masterB = a;
        this.slaveB = b;
        pid = new PIDController(KP,KI,KD,0.01,1);
        type = SyncType.SIDES;
    }

    public synchronized void setRatio(double ratio) {
        this.ratio = ratio;
        this.pid.setTarget(ratio);
    }

    public synchronized void setPower(double power) {
        this.power = MathUtils.clamp(power,-1,1);
        move();
    }

    private synchronized double getSpeedA() {
        if (type == SyncType.MOTORS) {
            double temp = encoderA.getValue();
            long tempTime = System.currentTimeMillis();
            double res = (temp - oldEncoderA) * 1000 / (tempTime - oldTimeA);
            oldTimeA = tempTime;
            oldEncoderA = temp;
            return res;
        }
        return masterB.getSpeedA();
    }

    private synchronized double getSpeedB() {
        if (type == SyncType.MOTORS) {
            double temp = encoderB.getValue();
            long tempTime = System.currentTimeMillis();
            double res = (temp - oldEncoderB) * 1000 / (tempTime - oldTimeB);
            oldTimeB = tempTime;
            oldEncoderB = temp;
            return res;
        }
        return slaveB.getSpeedA();
    }

    private synchronized void move() {
        double speedA = getSpeedA();
        double adjust = pid.getPIDOutput(speedA == 0 ? 0 : getSpeedB() / speedA);
        double slavePower = MathUtils.clamp(power*(ratio + adjust),-1,1);
        double realPower = MathUtils.clamp(power * (ratio - adjust), -1, 1);
        if (type == SyncType.MOTORS) {
            masterA.setPower(realPower);
            slaveA.setPower(slavePower);
        }else {
            masterB.setPower(realPower);
            slaveB.setPower(slavePower);
            masterB.move();
            slaveB.move();
        }
    }

    public Sensor getEncoder() {
        return encoderA;
    }

    public synchronized void desync() {
        
    }

    static enum SyncType {
        MOTORS, SIDES
    }
}
