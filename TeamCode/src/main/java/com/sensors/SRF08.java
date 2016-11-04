package com.sensors;

import android.util.Log;

import com.I2CUtils.I2CSensor;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.sensors.ftc.SRF08Protocol;
import com.sensors.ftc.SRF08Registers;

/**
 * Created by ethachu19 on 11/1/2016.
 */

public class SRF08 extends I2CSensor {
    int address;
    DistUnit distUnit;

    public SRF08(I2cDevice device) {
        this(device, 0xE0);
    }

    public SRF08(I2cDevice device, int address) {
        super(device);
        this.address = address;
        setDistanceUnit(DistUnit.MICROSECONDS);
    }

    public void setDistanceUnit(DistUnit d) {
        distUnit = d;
        SensorWriteRequest write = new SensorWriteRequest(0x00, 1);
        write.setWriteData(new byte[]{(byte)(SRF08Protocol.DISTINCHES + d.getDisplacement())});
        write.sendRequest();
        while (!write.wasSent()) {}
    }

    public double getEcho(int echo) {
        if (echo < 1 || echo > 17) {
            throw new IllegalArgumentException("Echo Is Too High:"+echo);
        }
        SensorReadRequest read = new SensorReadRequest(SRF08Registers.FIRST_ECHO + echo - 1, 2);
        while (!read.hasReadData()) {
            portIsReady(1);
            Log.d("ErrorTracking", "In set read data");
        }
        byte data[] = read.getReadData();
        Log.d("ErrorTracking", data.length + " ");
        return data[0] << 8 | data [1];
    }

    public double getEchoCM(int echo) {
        if (distUnit == DistUnit.INCHES) {
            setDistanceUnit(DistUnit.MICROSECONDS);
        }
        double ret;
        if (distUnit == DistUnit.CM  ) {
            ret = getEcho(echo);
        } else {
            ret = getEcho(echo) / 29 / 2;
        }
        return ret;
    }

    public double getEchoInches(int echo) {
        if (distUnit == DistUnit.CM) {
            setDistanceUnit(DistUnit.MICROSECONDS);
        }
        double ret;
        if (distUnit == DistUnit.INCHES) {
            ret = getEcho(echo);
        } else {
            ret = getEcho(echo) / 74 / 2;
        }
        return ret;
    }

    public void changeI2CAddress(int address) {
        SensorWriteRequest write = new SensorWriteRequest(0x00, 4);
        write.setWriteData(new byte[]{SRF08Protocol.CHANGEI2C1, SRF08Protocol.CHANGEI2C1, SRF08Protocol.CHANGEI2C1, (byte)address});
        write.sendRequest();
        while (!write.wasSent()){}
        this.address = address;
    }

    public void setMaxRange(double mm) {
        byte num = (byte) (Math.ceil(mm/43) - 1);
        SensorWriteRequest write = new SensorWriteRequest(SRF08Registers.RANGING_DATA, 1);
        write.setWriteData(new byte[]{num});
        write.sendRequest();
        while (!write.wasSent()){}
    }

//    protected void readyCallback() {
//        Log.d("I2C","Call done");
//    }

    @Override
    protected int getAddress() {
        return address;
    }

    public enum DistUnit {
        CM(0),
        INCHES(1),
        MICROSECONDS(2);

        byte displacement;
        private DistUnit(int displacement) {
            this.displacement = (byte)displacement;
        }

        public byte getDisplacement() {
            return displacement;
        }
    }
}
