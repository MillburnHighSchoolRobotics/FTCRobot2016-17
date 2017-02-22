package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.commands.Command;
import virtualRobot.logicThreads.TestingAutonomouses.TakePictureTestLogic;

/**
 * Created by mehme_000 on 10/7/2016.
 * Used for testing the camera
 */
public class TakePictureTestGod extends GodThread {
    AtomicBoolean redIsLeft = new AtomicBoolean();

    @Override
    public void realRun() throws InterruptedException {
        LogicThread takePicture = new TakePictureTestLogic(redIsLeft,super.vuforia);
        Thread tp = new Thread(takePicture);
        tp.start();
        children.add(tp);
        delegateMonitor(tp, new MonitorThread[]{});
        Command.ROBOT.addToProgress("RedIsLeft: " + redIsLeft.get());
    }

    public AtomicBoolean getRedIsLeft(){return redIsLeft;}

}
