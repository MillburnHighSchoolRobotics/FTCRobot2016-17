package virtualRobot.components;

import java.util.ArrayList;
import java.util.List;

import virtualRobot.PIDController;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 9/23/2016.
 */
public class SyncedMotors {
    private volatile static List<SyncedMotors> all = new ArrayList<>();

    SyncType type;
    Motor masterA;
    Motor slaveA;
    long oldTimeA,oldTimeB;
    double oldEncoderA,oldEncoderB;
    Sensor encoderA;
    Sensor encoderB;
    SyncedMotors masterB;
    SyncedMotors slaveB;
    private PIDController pid;
    double ratio;
    double power;

    public static List<SyncedMotors> getList() {
        return all;
    }

    public SyncedMotors(Motor a, Motor b, Sensor eA, Sensor eB ,double KP, double KI, double KD) {
        this.masterA = a;
        this.slaveA = b;
        this.encoderA = eA;
        this.encoderB = eB;
        pid = new PIDController(KP,KI,KD,0.01,1);
        type = SyncType.MOTORS;
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

    public synchronized void move() {
        double adjust = pid.getPIDOutput((getSpeedB()/getSpeedA()) - ratio);
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

    static enum SyncType {
        MOTORS, SIDES
    }
}
