package virtualRobot.godThreads;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.TestingAutonomouses.PIDTester;
import virtualRobot.logicThreads.TestingAutonomouses.RotateAutoPIDTester;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class RotateAutoPIDGod extends GodThread {
    AtomicBoolean currentToBig = new AtomicBoolean();
    double kP = 0.00223;
    double increment = 0.001;
    boolean lastTimeTooSmall = false;
    @Override
    public void realRun() throws InterruptedException {
        boolean isInterrupted = false;
        while (!isInterrupted) {

            LogicThread PIDTest = new RotateAutoPIDTester(kP,currentToBig);
            Thread pid = new Thread(PIDTest);
            pid.start();
            children.add(pid);
            delegateMonitor(pid, new MonitorThread[]{});
            while (pid.isAlive()) {}

            Log.d("PIDTestOutput", "KP: " + kP + " Increment: " + increment + " Too High: " + currentToBig.get());

            if (lastTimeTooSmall && currentToBig.get()) {
                kP -= increment;
                increment /= 10;
                kP += increment;
            }
            if (!lastTimeTooSmall && !currentToBig.get()) {
                increment /= 10;
                kP += increment;
            }
            if (!lastTimeTooSmall && currentToBig.get()) {
                kP -= increment;
            }
            if (lastTimeTooSmall && !currentToBig.get()) {
                kP += increment;
            }

            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                isInterrupted = true;
                break;
            }
        }
    }
}
