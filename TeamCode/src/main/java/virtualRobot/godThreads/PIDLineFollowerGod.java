package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;

/**
 * Created by ethachu19 on 10/27/2016.
 * Used For Testing Out Ethan's algo (located in ScrewTesterMax)
 */

public class PIDLineFollowerGod extends GodThread {
    @Override
    public void realRun() throws InterruptedException {
       // LogicThread moveToFirstBeacon = new ScrewTesterMax();
        //Thread mtfb = new Thread(moveToFirstBeacon);
        //mtfb.start();
        //children.add(mtfb);
        LogicThread rstr = new RedStrafeToRamp();
        Thread threa = new Thread(rstr);
        threa.start();
        children.add(threa);
        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(threa, new MonitorThread[]{});
    }
}
