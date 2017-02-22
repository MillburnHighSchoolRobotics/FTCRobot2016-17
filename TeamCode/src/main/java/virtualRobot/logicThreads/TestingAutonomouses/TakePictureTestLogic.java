package virtualRobot.logicThreads.TestingAutonomouses;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.fastRedIsLeft;

/**
 * Created by mehme_000 on 10/6/2016.
 * Used to test the camera
 */
public class TakePictureTestLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft;
    VuforiaLocalizerImplSubclass vuforia;

    public TakePictureTestLogic(AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia) {
        super();
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }

    public void loadCommands() {

        commands.add(new fastRedIsLeft(redIsLeft,vuforia));


    }
}
