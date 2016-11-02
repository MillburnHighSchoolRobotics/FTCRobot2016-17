package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.TeleopLogic;
import virtualRobot.logicThreads.testingTranslateLogicThread;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateGodThread extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
        LogicThread translate = new testingTranslateLogicThread();
        Thread teleopThread = new Thread(translate);
        teleopThread.start();
        children.add(teleopThread);
        delegateMonitor(teleopThread, new MonitorThread[]{});

    }
}
