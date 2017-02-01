package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;

/**
 * Created by 17osullivand on 1/31/17.
 * KIlls all the babies of the thread (aka threads spanwed with spawn new thread)
 */

public class killChildren implements Command {
    private LogicThread<AutonomousRobot> logicThread;
    public killChildren(LogicThread<AutonomousRobot> logicThread) {
        this.logicThread = logicThread;
    }
    @Override
    public boolean changeRobotState() throws InterruptedException {
        logicThread.killChildren();
        return false;
    }
}
