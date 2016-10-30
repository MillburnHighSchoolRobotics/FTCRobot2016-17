package virtualRobot.components;

import com.kauailabs.navx.ftc.MPU9250;
import com.vuforia.Matrix34F;

import virtualRobot.utils.MathUtils;
import virtualRobot.utils.Matrix3f;
import virtualRobot.utils.Vector2f;
import virtualRobot.utils.Vector3f;

/**
 * Created by shant on 2/8/2016.
 * Location Sensor. Keeps track of bot's position on field
 */
public class LocationSensor extends Sensor {

    private Vector3f location;
    private double angle;

    public LocationSensor() {
        location = new Vector3f();
        angle = 0;
    }

    public synchronized double getX () {
        synchronized (this) {
            return location.x;
        }
    }

    public synchronized void setX (double newX) {
        synchronized (this) {
            location.x = newX;
        }
    }

    public synchronized double getY() {
        synchronized (this) {
            return location.y;
        }
    }

    public synchronized void setY (double newY) {
        synchronized (this) {
            location.y = newY;
        }
    }

    public synchronized double getAngle() {
        synchronized (this) {
            return angle;
        }
    }

    public synchronized void setAngle (double newAngle) {
        synchronized (this) {
            angle = newAngle;
        }
    }

    //X:Roll Y:Pitch Z:Yaw
    public synchronized void update(MPU9250 imu) {
        Matrix3f rotation = new Matrix3f();
        double x = imu.getIntegratedRoll(), y = imu.getIntegratedPitch(), z = imu.getIntegratedYaw();
        rotation.m00 = MathUtils.cosDegrees(y)*MathUtils.cosDegrees(z);
        rotation.m01 = MathUtils.sinDegrees(x)*MathUtils.sinDegrees(y)*MathUtils.cosDegrees(z) - MathUtils.cosDegrees(x)*MathUtils.sinDegrees(z);
        rotation.m02 = MathUtils.cosDegrees(x)*MathUtils.sinDegrees(y)*MathUtils.cosDegrees(z) - MathUtils.sinDegrees(x)*MathUtils.sinDegrees(z);
        rotation.m10 = MathUtils.cosDegrees(y)*MathUtils.cosDegrees(z);
        rotation.m11 = MathUtils.sinDegrees(x)*MathUtils.sinDegrees(y)*MathUtils.sinDegrees(z) + MathUtils.cosDegrees(x)*MathUtils.cosDegrees(z);


    }
}
