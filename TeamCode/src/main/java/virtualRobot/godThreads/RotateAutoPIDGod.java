package virtualRobot.godThreads;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
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
    double kP = 0.01;
    double increment = 0.01;
    boolean lastTimeTooSmall = false;
    long iteration = 1;
    AutonomousRobot robot = Command.ROBOT;
    @Override
    public void realRun() throws InterruptedException {
        boolean isInterrupted = false;
        while (!isInterrupted) {
            Log.d("PIDOUTROTATE","\n-----------------------------------------------------------------\n-");
            LogicThread PIDTest = new RotateAutoPIDTester(kP,currentTooBig,stopThreads);
            Thread pid = new Thread(PIDTest);
            pid.start();
            children.add(pid);
            delegateMonitor(pid, new MonitorThread[]{});
            while (!stopThreads.get()) {robot.addToTelemetry("Stop: ", stopThreads.get());}
            Log.d("AutoPID", "Iteration: " + iteration + " KU: " + kP + " Increment: " + increment + " Too High: " + currentTooBig.get());
            robot.addToTelemetry("KU: ",kP + " Increment: " + increment + " Too High: " + currentTooBig.get());
            robot.addToTelemetry("Iteration #", iteration);
            if (iteration != 1) {
                if (lastTimeTooSmall && currentTooBig.get()) {
                    Log.d("AutoPID", "\n----------------------------------------------------------------------------\n");
                    kP -= increment;
                    increment /= 10;
                    kP += increment;
                }
                if (!lastTimeTooSmall && !currentTooBig.get()) {
                    Log.d("AutoPID", "\ntooBig then tooSmall-----------------------------------------------------------------------\n");
                    increment /= 10;
                    kP += increment;
                }
            }
            if (!lastTimeTooSmall && currentTooBig.get()) {
                kP -= increment;
            }
            if (lastTimeTooSmall && !currentTooBig.get()) {
                kP += increment;
            }

            iteration++;
            lastTimeTooSmall = !currentTooBig.get();
            stopThreads.set(false);
            currentTooBig.set(true);

            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                isInterrupted = true;
                break;
            }

//            if(robot.getVoltageSensor().getValue() <= 13.5) {
//                Log.d("AutoPID", "Stopped due to voltage being below 13.5 : " + Command.ROBOT.getVoltageSensor().getValue());
//                break;
//            }

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
