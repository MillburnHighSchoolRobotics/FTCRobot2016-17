package virtualRobot.monitorThreads;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.MonitorThread;

/**
 * Created by shant on 1/10/2016.
 */
public class DebrisMonitor extends MonitorThread<AutonomousRobot> {


    @Override
    public boolean setStatus() {
        double totalAngle = Math.sqrt(Math.pow(robot.getRollSensor().getValue(), 2) + Math.pow(robot.getPitchSensor().getValue(), 2));
        if (totalAngle > 2.5) {
            Log.d("RoboAngle", robot.getRollSensor().getValue() + " " + robot.getPitchSensor().getValue() + " " + totalAngle);
            Log.d("RoboAngle", "Robot died in debris thread");
            return false;
        }
        Log.d("RoboAngle", "we still in here " + robot.getRollSensor().getValue() + " " + robot.getPitchSensor().getValue());
        return true;
    }

}
