package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.logicThreads.NoSensorAutonomouses.RedStrafeToRamp;
import virtualRobot.logicThreads.TestingAutonomouses.ScrewTesterMax;

/**
 * Created by ethachu19 on 10/27/2016.
 * Used For Testing Out Ethan's algo (located in ScrewTesterMax)
 */

public class PIDLineFollowerGod extends GodThread {
    LogicThread takePicture = new LogicThread() {
        @Override
        public void loadCommands() {
            FTCTakePicture pic = new FTCTakePicture(FTCTakePicture.Mode.TAKING_PICTURE, b,vuforia); //Take a picture of beacon
            commands.add(pic);
        }
    };
    AtomicBoolean b = new AtomicBoolean();
    @Override
    public void realRun() throws InterruptedException {
       // LogicThread moveToFirstBeacon = new ScrewTesterMax();
        //Thread mtfb = new Thread(moveToFirstBeacon);
        //mtfb.start();
        //children.add(mtfb);

        LogicThread rstr = new ScrewTesterMax(vuforia);
        Thread threa = new Thread(rstr);
        threa.start();
        children.add(threa);
        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(threa, new MonitorThread[]{});

        Thread threa2 = new Thread(takePicture);
        threa2.start();
        children.add(threa2);
        //keep the program alive as long as the two monitor threads are still going - should proceed every logicThread addition
        delegateMonitor(threa2, new MonitorThread[]{});
        Command.AUTO_ROBOT.addToProgress("redIsLeft: " + b.get());
    }
}
