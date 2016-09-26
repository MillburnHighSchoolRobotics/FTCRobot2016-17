package virtualRobot.commands;

import virtualRobot.components.SyncedMotors;

/**
 * Created by ethachu19 on 9/24/2016.
 */
public class MoveSyncedMotors implements Command {

    @Override
    public boolean changeRobotState() throws InterruptedException {
        for(SyncedMotors a: SyncedMotors.getList())
            a.move();
        return false;
    }
}