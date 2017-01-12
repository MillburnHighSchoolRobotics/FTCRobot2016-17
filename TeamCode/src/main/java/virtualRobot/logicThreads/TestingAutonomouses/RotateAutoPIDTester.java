package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.godThreads.RotateAutoPIDGod;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class RotateAutoPIDTester extends LogicThread {
    double kP;
    private AtomicBoolean isTime;
    private AtomicBoolean shouldStop;

    public RotateAutoPIDTester(double kP, AtomicBoolean ab, AtomicBoolean ab2) {
        this.kP = kP;
        this.isTime = ab;
        this.shouldStop = ab2;
    }

    @Override
    public void loadCommands() {
        commands.add(new Command() {

            @Override
            public boolean changeRobotState() throws InterruptedException {
                Thread x = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                        double lastYaw = -400;
                        double curr;
                        isTime.set(true);
                        while (!shouldStop.get()) {
                            curr = robot.getHeadingSensor().getValue();
                            robot.addToTelemetry("Thread: ",curr);
                            if (MathUtils.equals(curr, lastYaw,0.3)) {
                                isTime.set(false);
                                shouldStop.set(true);
                                return;
                            }
                            lastYaw = curr;

                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }

                            try {
                                Thread.currentThread().sleep(500);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                };
                robot.addToProgress("Thread started");
                x.start();
                children.add(x);
                return Thread.currentThread().isInterrupted();
            }
        });
        Rotate r = new Rotate(kP,90,35000,shouldStop);
        robot.getHeadingSensor().clearValue();
        commands.add(r);
    }
}
