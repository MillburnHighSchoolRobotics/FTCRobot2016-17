package virtualRobot.monitorThreads;

import virtualRobot.MonitorThread;
import virtualRobot.utils.Vector3f;

/**
 * Created by ethachu19 on 12/5/2016.
 */

public class CollisionMonitor extends MonitorThread {
    Vector3f lastAccel = new Vector3f();
    private final double COLLISION_THRESHOLD_DELTA_G = 0.5;

    public CollisionMonitor() {
        lastAccel = new Vector3f(robot.getWorldAccel().getValueX(), robot.getWorldAccel().getValueY(), robot.getWorldAccel().getValueZ());
    }

    @Override
    public boolean setStatus() {
        double curr_world_linear_accel_x = robot.getWorldAccel().getValueX();
        double currentJerkX = curr_world_linear_accel_x - lastAccel.x;
        lastAccel.x = curr_world_linear_accel_x;
        double curr_world_linear_accel_y = robot.getWorldAccel().getValueY();
        double currentJerkY = curr_world_linear_accel_y - lastAccel.y;
        lastAccel.y = curr_world_linear_accel_y;

        if ( ( Math.abs(currentJerkX) > COLLISION_THRESHOLD_DELTA_G ) ||
                ( Math.abs(currentJerkY) > COLLISION_THRESHOLD_DELTA_G) ) {
            return false;
        }
        return true;
    }
}
