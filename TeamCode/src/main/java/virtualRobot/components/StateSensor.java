package virtualRobot.components;

import android.util.Log;

import com.kauailabs.navx.ftc.MPU9250;

import virtualRobot.AutonomousRobot;
import virtualRobot.commands.Command;
import virtualRobot.utils.MathUtils;
import virtualRobot.utils.Matrix;
import virtualRobot.utils.Vector2f;
import virtualRobot.utils.Vector3f;

/**
 * Created by ethachu19 on 12/1/16
 * StateSensor detects robots velocity and position
 */
public class StateSensor extends Sensor {
    private long lastUpdateTime;
    private Matrix state;
    private double angle;
    private AutonomousRobot robot = null;

    public StateSensor() {
        lastUpdateTime = System.currentTimeMillis();
        state = new Matrix(6,1);
        angle = 0;
    }

    public StateSensor setRobot(AutonomousRobot robot) {
        this.robot = robot;
        return this;
    }

    public synchronized double getAngle() {
        synchronized (this) {
            return angle;
        }
    }

    public synchronized Vector3f getPosition() {
        return new Vector3f(state.toVector3f(3));
    }

    public synchronized Vector3f getVelocity() {
        return new Vector3f(state.toVector3f(0));
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

    public synchronized void update() {
        Vector3f angleVec = new Vector3f(robot.getRollSensor().getRawValue(), robot.getPitchSensor().getRawValue(),robot.getHeadingSensor().getRawValue());
        Vector3f accel = robot.getRawAccel().getValueVector();
        Matrix rotation = new Matrix(3,3);
        Matrix identity = Matrix.identity(6);
        Matrix variance = Matrix.diagonalFill(6,0.5);
        rotation.arr[0][0] = cos(angleVec.y)*cos(angleVec.z);
        rotation.arr[0][1] = sin(angleVec.x)*sin(angleVec.y)*cos(angleVec.z) - cos(angleVec.x)*sin(angleVec.z);
        rotation.arr[0][2] = cos(angleVec.x)*sin(angleVec.y)*cos(angleVec.z) - sin(angleVec.x)*sin(angleVec.z);
        rotation.arr[1][0] = sin(angleVec.y)*sin(angleVec.z);
        rotation.arr[1][1] = sin(angleVec.x)*sin(angleVec.y)*sin(angleVec.z) + cos(angleVec.x)*cos(angleVec.z);
        rotation.arr[1][2] = cos(angleVec.x)*sin(angleVec.y)*sin(angleVec.z) + sin(angleVec.x)*cos(angleVec.z);
        rotation.arr[2][0] = -sin(angleVec.y);
        rotation.arr[2][1] = sin(angleVec.x)*cos(angleVec.y);
        rotation.arr[2][2] = cos(angleVec.x)*cos(angleVec.y);
        accel = rotation.multiply(accel).toVector3f(0);
        robot.addToTelemetry("Accel: ", accel.toString());
        double delta = System.currentTimeMillis() - lastUpdateTime;
        Vector3f location = state.toVector3f(0);
        Vector3f velocity = state.toVector3f(3);
        location.addEquals(velocity.multiply(delta),accel.multiply(0.5 * delta*delta));
        velocity.addEquals(accel.multiply(delta));
        state.arr[0][0] = location.x;
        state.arr[1][0] = location.y;
        state.arr[2][0] = location.z;
        state.arr[3][0] = velocity.x;
        state.arr[4][0] = velocity.y;
        state.arr[5][0] = velocity.z;
        angle = angleVec.z;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    private static double cos(double x) {return MathUtils.cosDegrees(x);}

    private static double sin(double x) {
        return MathUtils.sinDegrees(x);
    }
}
