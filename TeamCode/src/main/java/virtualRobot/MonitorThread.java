package virtualRobot;

import android.util.Log;

import virtualRobot.commands.Command;

/**
 * Created by shant on 1/10/2016.
 * Monitor threads are used by delegateMonitor in godthread.
 * They have the abstract method setStatus, where a particular monitor thread is told how to determine whether
 * the monitor thread should be triggered or not.
 */
public abstract class MonitorThread<T extends AutonomousRobot> implements Runnable {
    private boolean status; //usually should be TRUE, if stuff goes wrong, make it FALSE
    public static boolean NORMAL;
    protected T robot;

    public MonitorThread () {
        robot = (T) Command.ROBOT;
        NORMAL = true;
        status = true;
    }

    @Override
    public void run() { //Constantly Runs as long as the status is normal. As soon as it isn't, it stops running. The god Thread in the meantime will have detected that the status is not normal.
        while (!Thread.currentThread().isInterrupted() && (status == NORMAL)) {
            status = setStatus();
            Log.d("Monitor", Boolean.toString(status));
            if (status == false) {
                break;
            }
        }
        if (status != NORMAL) {
            return;
        }

    }

    public boolean getStatus() {
        return status;
    }

    public abstract boolean setStatus ();
}
