package virtualRobot.logicThreads;

import virtualRobot.LogicThread;
import virtualRobot.commands.MoveSyncedMotors;
import virtualRobot.components.SyncedMotors;

/**
 * Created by ethachu19 on 9/24/2016.
 */
public class MotorSync extends LogicThread {
    @Override
    public void loadCommands() {
        commands.add(new MoveSyncedMotors());
    }
}
