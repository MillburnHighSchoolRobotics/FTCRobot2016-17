package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.TeleopLogic;
import virtualRobot.monitorThreads.TimeMonitor;

/**
 * Created by shant on 1/10/2016.
 */
public class TeleopGodThread extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
        MonitorThread watchForTime = new TimeMonitor(System.currentTimeMillis(), 120000);
        Thread tm = new Thread (watchForTime);
        tm.start();
        children.add(tm);


        LogicThread teleop = new TeleopLogic();
        Thread teleopThread = new Thread(teleop);
        teleopThread.start();
        children.add(teleopThread);

        delegateMonitor(teleopThread, new MonitorThread[]{});
    }
}
