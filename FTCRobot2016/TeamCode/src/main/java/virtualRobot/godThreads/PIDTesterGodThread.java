package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.PIDTester;

/**
 * Created by shant on 1/10/2016.
 */
public class PIDTesterGodThread extends GodThread {

    @Override
    public void realRun() throws InterruptedException {
        /*MonitorThread watchForTime = new TimeMonitor(System.currentTimeMillis(), -1);
        Thread tm = new Thread (watchForTime);
        tm.start();
        children.add(tm);*/


        LogicThread PIDTest = new PIDTester();
        Thread pid = new Thread(PIDTest);
        pid.start();
        children.add(pid);

        delegateMonitor(pid, new MonitorThread[]{});
    }
}
