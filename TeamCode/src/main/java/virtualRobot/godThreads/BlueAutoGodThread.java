package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
//import virtualRobot.logicThreads.BlueDumpPeople;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.BlueAutonomousLogic;
import virtualRobot.logicThreads.MoveToSecondBeacon;
import virtualRobot.logicThreads.PushLeftButton;
import virtualRobot.logicThreads.PushRightButton;
import virtualRobot.logicThreads.RedStrafeToRamp;
import virtualRobot.monitorThreads.TimeMonitor;

//import virtualRobot.logicThreads.BlueDumpPeople;

/**
 * Created by shant on 1/5/2016.
 */
public class BlueAutoGodThread extends GodThread {
    AtomicBoolean redIsLeft = new AtomicBoolean();
    @Override

    public void realRun() throws InterruptedException {



        MonitorThread watchingForTime = new TimeMonitor(System.currentTimeMillis(), 30000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);

        // THIS IS THE STANDARD FORMAT FOR ADDING A LOGICTHREAD TO THE LIST
        LogicThread moveToFirstBeacon = new BlueAutonomousLogic(redIsLeft, vuforia);
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
            delegateMonitor(pl, new MonitorThread[]{watchingForTime});
        }

        else {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{watchingForTime});
        }

        LogicThread rmtscb = new MoveToSecondBeacon(redIsLeft, super.vuforia);
        Thread godThread = new Thread(rmtscb);
        godThread.start();
        children.add(godThread);
        delegateMonitor(godThread, new MonitorThread[]{watchingForTime});



        if (!redIsLeft.get()) {
            LogicThread pushLeft = new PushLeftButton();
            Thread pl = new Thread(pushLeft);
            pl.start();
            children.add(pl);
            delegateMonitor(pl, new MonitorThread[]{watchingForTime});
        }

        else {
            LogicThread pushRight = new PushRightButton();
            Thread pr = new Thread(pushRight);
            pr.start();
            children.add(pr);
            delegateMonitor(pr, new MonitorThread[]{watchingForTime});
        }

        LogicThread rstr = new RedStrafeToRamp();
        Thread rst = new Thread(rstr);
        rst.start();
        children.add(rst);
        delegateMonitor(rst, new MonitorThread[]{watchingForTime});



    }


}

