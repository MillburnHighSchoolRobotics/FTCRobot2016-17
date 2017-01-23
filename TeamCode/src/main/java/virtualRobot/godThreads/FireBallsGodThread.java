package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.AutonomousLayer1.BlueGoToWall;
import virtualRobot.logicThreads.NoSensorAutonomouses.FireBallsOnly;
import virtualRobot.monitorThreads.TimeMonitor;

/**
 * Created by 17osullivand on 11/27/16.
 * Fires the Balls and that's about it
 */

public class FireBallsGodThread extends GodThread {

    @Override
    public void realRun() throws InterruptedException {

        MonitorThread watchingForTime = new TimeMonitor(10000);
        Thread tm = new Thread(watchingForTime);
        tm.start();
        children.add(tm);
        LogicThread fireBalls = new FireBallsOnly();
        Thread fb = new Thread(fireBalls);
        fb.start();
        children.add(fb);
        delegateMonitor(fb, new MonitorThread[]{watchingForTime});
    }
}
