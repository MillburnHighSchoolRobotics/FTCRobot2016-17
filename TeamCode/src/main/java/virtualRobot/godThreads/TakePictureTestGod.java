package virtualRobot.godThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.logicThreads.TakePictureTestLogic;

/**
 * Created by mehme_000 on 10/7/2016.
 */
public class TakePictureTestGod extends GodThread {
    AtomicBoolean redIsLeft = new AtomicBoolean();

    @Override
    public void realRun() throws InterruptedException {
        LogicThread takePicture = new TakePictureTestLogic(redIsLeft);
        Thread tp = new Thread(takePicture);
        tp.start();
        children.add(tp);

    }
}
