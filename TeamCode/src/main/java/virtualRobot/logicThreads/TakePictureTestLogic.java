package virtualRobot.logicThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Command;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;

/**
 * Created by mehme_000 on 10/6/2016.
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

        commands.add(new FTCTakePicture(redIsLeft,vuforia));

    }
}
