package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.godThreads.RotateAutoPIDGod;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 11/14/2016.
 */

public class TranslateAutoPIDTester extends LogicThread {
    double kP;
    private AtomicBoolean isTime;
    private AtomicBoolean shouldStop;
    private long iter;

    public TranslateAutoPIDTester(double kP, AtomicBoolean ab, AtomicBoolean ab2, long iter) {
        this.kP = kP;
        this.isTime = ab;
        this.shouldStop = ab2;
        this.iter = iter;
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
                        double lastEncoder = Double.NaN;
                        double curr;
                        isTime.set(true);
                        while (!shouldStop.get()) {
                            curr = robot.getHeadingSensor().getValue();
                            robot.addToTelemetry("Thread: ",curr);
                            if (MathUtils.equals(curr, lastEncoder,10)) {
                                isTime.set(false);
                                shouldStop.set(true);
                                return;
                            }
                            lastEncoder = curr;

                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }

                            try {
                                Thread.currentThread().sleep(200);
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
        Translate r = new Translate(kP,90,35000,shouldStop,iter % 2 == 0 ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD);
        robot.getHeadingSensor().clearValue();
        commands.add(r);
    }
}
