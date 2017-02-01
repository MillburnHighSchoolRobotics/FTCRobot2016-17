package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.NoSensorAutonomouses.moveAndFireBalls;
import virtualRobot.monitorThreads.TimeMonitor;

/**
 * Created by 17osullivand on 1/30/17.
 */

public class RedStatesGodThread extends GodThread{



    @Override
    public void realRun() throws InterruptedException {

        MonitorThread watchingForTime = new TimeMonitor(10000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);

        LogicThread fireBalls = new moveAndFireBalls();
        Thread fb = new Thread(fireBalls);
        fb.start();
        children.add(fb);

        delegateMonitor(fb, new MonitorThread[]{watchingForTime});

    }
}
