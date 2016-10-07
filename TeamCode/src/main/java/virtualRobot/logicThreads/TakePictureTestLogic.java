package virtualRobot.logicThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.FTCTakePicture;

/**
 * Created by mehme_000 on 10/6/2016.
 */
public class TakePictureTestLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft = new AtomicBoolean();

    public TakePictureTestLogic(AtomicBoolean redIsLeft) {
        super();
        this.redIsLeft = redIsLeft;
    }

    public void loadCommands() {
        FTCTakePicture tp = new FTCTakePicture(redIsLeft);
        commands.add(tp);
    }
}
