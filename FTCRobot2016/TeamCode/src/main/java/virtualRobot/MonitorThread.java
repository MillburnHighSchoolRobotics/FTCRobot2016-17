package virtualRobot;

import android.util.Log;

import virtualRobot.commands.Command;

/**
 * Created by shant on 1/10/2016.
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
    public void run() {
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
