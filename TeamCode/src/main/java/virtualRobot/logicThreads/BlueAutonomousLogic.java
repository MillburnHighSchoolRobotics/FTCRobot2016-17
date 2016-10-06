package virtualRobot.logicThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;

/**
 * Created by 17osullivand on 10/6/16.
 */
public class BlueAutonomousLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft = new AtomicBoolean();

    public BlueAutonomousLogic(AtomicBoolean redIsLeft) {
        super();
        this.redIsLeft = redIsLeft;
    }
    @Override
    public void loadCommands() {
        //TODO
    }
}
