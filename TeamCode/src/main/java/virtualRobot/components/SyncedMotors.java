package virtualRobot.components;

import android.util.Log;

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
    private SyncMode type;
    private SyncAlgo algo;
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

    public SyncedMotors(Motor a, Motor b, Sensor eA, Sensor eB ,double KP, double KI, double KD, SyncAlgo algo) {
        this.masterA = a;
        this.slaveA = b;
        this.encoderA = eA;
        this.encoderB = eB;
        type = SyncMode.MOTORS;
        this.algo = algo;
        if (algo == SyncAlgo.SPEED){
            pid = new PIDController(KP,KI,KD,0.01,1);
        } else {
            pid = new PIDController(KP,KI,KD,10,1);
        }
        this.encoderA.clearValue();
        this.encoderB.clearValue();
    }

    public SyncedMotors(SyncedMotors a, SyncedMotors b, double KP, double KI, double KD, SyncAlgo algo) {
        this.masterB = a;
        this.slaveB = b;
        this.algo = algo;
        if (algo == SyncAlgo.SPEED){
            pid = new PIDController(KP,KI,KD,0.01,1);
        } else {
            pid = new PIDController(KP,KI,KD,10,1);
        }
        type = SyncMode.SIDES;
    }

    public synchronized void setRatio(double ratio) {
        this.ratio = ratio;
        this.pid.setTarget(ratio);
    }

    public synchronized void setPower(double power) {
        this.power = MathUtils.clamp(power,-1,1);
        move();
    }

    public synchronized double getSpeedA() {
        if (type == SyncMode.MOTORS) {
            double temp = encoderA.getValue();
            long tempTime = System.currentTimeMillis();
            double res = (temp - oldEncoderA) * 1000 / ((tempTime - oldTimeA) == 0 ? 1 : (tempTime - oldTimeA));
            oldTimeA = tempTime;
            oldEncoderA = temp;
            return res;
        }
        return masterB.getSpeedA();
    }

    public synchronized double getSpeedB() {
        if (type == SyncMode.MOTORS) {
            double temp = encoderB.getValue();
            long tempTime = System.currentTimeMillis();
            double res = (temp - oldEncoderB) * 1000 / ((tempTime - oldTimeB == 0 ? 1 : (tempTime - oldTimeB )));
            oldTimeB = tempTime;
            oldEncoderB = temp;
            return res;
        }
        return slaveB.getSpeedA();
    }

    public synchronized void move() {
        double adjust = 0;
        if (algo == SyncAlgo.SPEED) {
            double speedA = getSpeedA();
            adjust = pid.getPIDOutput(speedA == 0 ? 0 : getSpeedB() / speedA);
        } else {
            adjust = type == SyncMode.MOTORS ? pid.getPIDOutput(encoderA.getValue() - encoderB.getValue()) : pid.getPIDOutput(masterB.getEncoder().getValue() - slaveB.getEncoder().getValue());
        }
        double slavePower = MathUtils.clamp(power*(ratio+adjust),-1,1);
        double realPower = MathUtils.clamp(power*(ratio-adjust), -1, 1);
        if (type == SyncMode.MOTORS) {
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

    static enum SyncMode {
        MOTORS, SIDES
    }

    public static enum SyncAlgo {
        POSITION, SPEED
    }
}
