package virtualRobot.commands;

import virtualRobot.components.SyncedMotors;

/**
 * Created by ethachu19 on 9/24/2016.
 */
public class MoveSyncedMotors implements Command {

	SyncedMotors synced;

	public MoveSyncedMotors(SyncedMotors synced) {
		this.synced = synced;
	}

    @Override
    public boolean changeRobotState() throws InterruptedException {
        synced.move();
    }
}