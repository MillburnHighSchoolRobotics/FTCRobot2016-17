package virtualRobot.godThreads;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.TestingAutonomouses.PIDTester;
import virtualRobot.logicThreads.TestingAutonomouses.RotateAutoPIDTester;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class RotateAutoPIDGod extends GodThread {
    AtomicBoolean currentTooBig = new AtomicBoolean(true);
    AtomicBoolean stopThreads = new AtomicBoolean(false);
    double kP = 0.02332;
    double increment = 0.01;
    boolean lastTimeTooSmall = false;
    long iteration = 1;
    @Override
    public void realRun() throws InterruptedException {
        boolean isInterrupted = false;
        while (!isInterrupted) {
            LogicThread PIDTest = new RotateAutoPIDTester(kP,currentTooBig,stopThreads);
            Thread pid = new Thread(PIDTest);
            pid.start();
            children.add(pid);
            delegateMonitor(pid, new MonitorThread[]{});
            while (!stopThreads.get()) {}

            Log.d("PIDTestOutput", "KP: " + kP + " Increment: " + increment + " Too High: " + currentTooBig.get());
            Command.ROBOT.addToTelemetry("KP: ",kP + " Increment: " + increment + " Too High: " + currentTooBig.get());
            Command.ROBOT.addToTelemetry("Iteration #", iteration);

            if (lastTimeTooSmall && currentTooBig.get()) {
                kP -= increment;
                increment /= 10;
                kP += increment;
            }
            if (!lastTimeTooSmall && !currentTooBig.get()) {
                increment /= 10;
                kP += increment;
            }
            if (!lastTimeTooSmall && currentTooBig.get()) {
                kP -= increment;
            }
            if (lastTimeTooSmall && !currentTooBig.get()) {
                kP += increment;
            }

            iteration++;
            stopThreads.set(false);
            currentTooBig.set(true);

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
