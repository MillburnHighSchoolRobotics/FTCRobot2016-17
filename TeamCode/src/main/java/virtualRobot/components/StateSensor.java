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
public class StateSensor extends Sensor {

    private long lastUpdateTime;
    private Vector3f location;
    private Vector3f velocity;
    private double angle;

    public StateSensor() {
        lastUpdateTime = System.currentTimeMillis();
        location = new Vector3f();
        velocity = new Vector3f();
        angle = 0;
    }

    public synchronized double getAngle() {
        synchronized (this) {
            return angle;
        }
    }

    public synchronized Vector3f getPosition() {
        return new Vector3f(location);
    }

    public synchronized Vector3f getVelocity() {
        return new Vector3f(velocity);
    }

    //X:Roll Y:Pitch Z:Yaw
//    public synchronized void update(MPU9250 imu) {
//        Matrix3f rotation = new Matrix3f();
//        double x = imu.getIntegratedRoll(), y = imu.getIntegratedPitch(), z = imu.getIntegratedYaw();
//        Vector3f accel = new Vector3f(imu.getIntegratedAccelX(),imu.getIntegratedAccelY(),imu.getIntegratedAccelZ());
//        Vector3f gyro = new Vector3f(imu.getRawGyroX(),imu.getRawGyroY(),imu.getRawGyroZ());
//        rotation.m00 = cos(y)*cos(z);
//        rotation.m01 = sin(x)*sin(y)*cos(z) - cos(x)*sin(z);
//        rotation.m02 = cos(x)*sin(y)*cos(z) - sin(x)*sin(z);
//        rotation.m10 = sin(y)*sin(z);
//        rotation.m11 = sin(x)*sin(y)*sin(z) + cos(x)*cos(z);
//        rotation.m12 = cos(x)*sin(y)*sin(z) + sin(x)*cos(z);
//        rotation.m20 = -sin(y);
//        rotation.m21 = sin(x)*cos(y);
//        rotation.m22 = cos(x)*cos(y);
//        Vector3f step = rotation.multiply(accel);

//    }

    public synchronized void update(MPU9250 imu) {
        Vector3f accel = new Vector3f(imu.getIntegratedAccelX(),imu.getIntegratedAccelY(),imu.getIntegratedAccelZ()).multiply(9.8);
        double delta = System.currentTimeMillis() - lastUpdateTime;
        location.addEquals(velocity.multiply(delta),accel.multiply(0.5 * delta*delta));
        velocity.addEquals(accel.multiply(delta));
        angle = imu.getIntegratedYaw();
    }
    
    private static double cos(double x) {
        return MathUtils.cosDegrees(x);
    }

    private static double sin(double x) {
        return MathUtils.sinDegrees(x);
    }
}
