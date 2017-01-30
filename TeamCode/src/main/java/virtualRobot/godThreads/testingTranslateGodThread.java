package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.AutonomousLayer1.BlueGoToWall;
import virtualRobot.logicThreads.AutonomousLayer1.RedGoToWall;
import virtualRobot.logicThreads.AutonomousLayer2.ToWhiteLine;
import virtualRobot.logicThreads.TestingAutonomouses.testingTranslateLogicThread;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateGodThread extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
       /* LogicThread translate = new BlueGoToWall(new AtomicBoolean());
        Thread teleopThread = new Thread(translate);
        teleopThread.start();
        children.add(teleopThread);
        delegateMonitor(teleopThread, new MonitorThread[]{});*/
        LogicThread toFirstLine = new testingTranslateLogicThread();
        Thread tfl = new Thread(toFirstLine);
        tfl.start();
        children.add(tfl);
        delegateMonitor(tfl, new MonitorThread[]{});

        /*LogicThread translate2 = new testingTranslateLogicThread();
        Thread t2 = new Thread(translate2);
        t2.start();
        children.add(t2);
        delegateMonitor(t2, new MonitorThread[]{});*/

    }
}
