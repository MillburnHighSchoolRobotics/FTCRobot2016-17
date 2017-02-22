package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Command;
import virtualRobot.commands.Pause;
import virtualRobot.commands.fastRedIsLeft;

/**
 * Created by 17osullivand on 2/18/17.
 */

public class justRedIsLeft extends LogicThread<AutonomousRobot> {
    GodThread.Line type;
    VuforiaLocalizerImplSubclass vuforia;
    AtomicBoolean redIsLeft;
    public justRedIsLeft(GodThread.Line type, VuforiaLocalizerImplSubclass vuforia, AtomicBoolean redIsLeft) {
        this.type = type;
        this.vuforia = vuforia;
        this.redIsLeft = redIsLeft;
    }
    @Override
    public void loadCommands() {
        commands.add(new fastRedIsLeft(redIsLeft, vuforia));
        commands.add(new Pause(200));
    }
}
