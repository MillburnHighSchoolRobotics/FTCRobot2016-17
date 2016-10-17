package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.BlueAutonomousLogic;
import virtualRobot.logicThreads.PushLeftButton;
import virtualRobot.logicThreads.PushRightButton;
import virtualRobot.logicThreads.RedAutonomousLogic;
import virtualRobot.logicThreads.RedMoveToSecondBeacon;
import virtualRobot.logicThreads.RedStrafeToRamp;
import virtualRobot.monitorThreads.TimeMonitor;

/**
 * Created by shant on 1/10/2016.
 */
public class RedAutoGodThread extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
        AtomicBoolean redIsLeft = new AtomicBoolean();

        MonitorThread watchingForTime = new TimeMonitor(System.currentTimeMillis(), 30000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);

        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD TO THE LIST
        LogicThread moveToFirstBeacon = new RedAutonomousLogic(redIsLeft);
        Thread mtfb = new Thread(moveToFirstBeacon);
        mtfb.start();
        children.add(mtfb);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(mtfb, new MonitorThread[]{watchingForTime});



        Command.ROBOT.addToProgress("red is left /" + Boolean.toString(redIsLeft.get()));
        if (!redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton();
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else if (redIsLeft.get()) {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }

        LogicThread merge = new RedMoveToSecondBeacon(redIsLeft);
        Thread godThread = new Thread(merge);
        godThread.start();
        children.add(godThread);
        delegateMonitor(godThread, new MonitorThread[]{watchingForTime});



        if (!redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton();
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{});
        }

        else if (redIsLeft.get()) {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{});
        }

        LogicThread derp = new RedStrafeToRamp();
        Thread rageatgit = new Thread(merge);
        rageatgit.start();
        children.add(rageatgit);
        delegateMonitor(rageatgit, new MonitorThread[]{watchingForTime});



    }
}
