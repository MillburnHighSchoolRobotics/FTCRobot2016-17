package virtualRobot.monitorThreads;

import android.util.Log;

import virtualRobot.AutonomousRobot;
import virtualRobot.MonitorThread;

/**
 * Created by shant on 1/10/2016.
 * Automatically stops robot after a certain number of seconds
 */
public class TimeMonitor extends MonitorThread<AutonomousRobot> {
    private double startingTime;
    private double endTime;
    //IF YOU WANT TO NOT HAVE ANY TIME LIMIT, PUT -1 FOR ENDTIME
    public TimeMonitor (double endTime) {
        this.startingTime = System.currentTimeMillis();
        this.endTime = endTime;
    }

    @Override
    public boolean setStatus() {

        double curTime = System.currentTimeMillis() - startingTime;
        Log.d("roboTime", Double.toString(curTime));
        if (curTime < endTime) {
            return true;
        }

        return false;
    }


}
