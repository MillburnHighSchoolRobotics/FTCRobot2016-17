package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.PIDController;
import virtualRobot.SallyJoeBot;
import virtualRobot.logicThreads.RedAutonomousLogic;
import virtualRobot.logicThreads.ScrewTesterMax;

/**
 * Created by ethachu19 on 10/27/2016.
 * Used For Testing Out Ethan's algo (located in ScrewTesterMax)
 */

public class PIDLineFollowerGod extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
        LogicThread moveToFirstBeacon = new ScrewTesterMax();
        Thread mtfb = new Thread(moveToFirstBeacon);
        mtfb.start();
        children.add(mtfb);

        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(mtfb, new MonitorThread[]{});
    }
}
