package virtualRobot.commands;

import virtualRobot.components.SyncedMotors;

/**
 * Created by ethachu19 on 9/24/2016.
 * Moves SynchronizedMotors
 */
public class MoveSyncedMotors implements Command {

	SyncedMotors synced;
    double power;
	public MoveSyncedMotors(SyncedMotors synced, double power) {
		this.synced = synced; this.power = power;
	}

    @Override
    public boolean changeRobotState() throws InterruptedException {
        synced.setPower(power);
        return Thread.currentThread().isInterrupted();
    }
}