package virtualRobot.components;

import virtualRobot.utils.Vector3f;

/**
 * Created by ethachu19 on 12/5/2016.
 */

public class AxisSensor {
    Vector3f values = new Vector3f();
    Vector3f offset = new Vector3f();

    public synchronized void clearValue() {
        synchronized (this) {
            offset = new Vector3f(values);
        }
    }

    //return the current softValue of the sensor
    public synchronized double getValueX() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.subtract(offset).x;
        }
        return retVal;
    }

    public synchronized double getValueY() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.subtract(offset).y;
        }
        return retVal;
    }

    public synchronized double getValueZ() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.subtract(offset).z;
        }
        return retVal;
    }

    //allows the UpdateThread to set the HardValue
    public synchronized void setRawValue(Vector3f hardValue) {
        synchronized (this) {
            this.values = hardValue;
        }
    }

    public synchronized double getRawValueX() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.x;
        }
        return retVal;
    }

    public synchronized double getRawValueY() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.y;
        }
        return retVal;
    }

    public synchronized double getRawValueZ() {
        double retVal = 0;
        synchronized (this) {
            retVal = values.z;
        }
        return retVal;
    }
}
