package virtualRobot.godThreads;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.R;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.MonitorThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.logicThreads.TakePictureTestLogic;

/**
 * Created by mehme_000 on 10/7/2016.
 */
public class TakePictureTestGod extends GodThread {
    AtomicBoolean redIsLeft = new AtomicBoolean();
    VuforiaLocalizerImplSubclass vuforia;
    @Override
    public void realRun() throws InterruptedException {

        LogicThread takePicture = new TakePictureTestLogic(redIsLeft,vuforia);
        Thread tp = new Thread(takePicture);
        tp.start();
        children.add(tp);
        delegateMonitor(tp, new MonitorThread[]{});
    }


    public AtomicBoolean getRedIsLeft(){return redIsLeft;}
    public void setVuforia(VuforiaLocalizerImplSubclass vuforia) {
        this.vuforia = vuforia;
    }
}
