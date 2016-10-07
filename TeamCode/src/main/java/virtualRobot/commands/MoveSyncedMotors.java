package virtualRobot.commands;

import virtualRobot.components.SyncedMotors;

/**
 * Created by ethachu19 on 9/24/2016.
 */
public class MoveSyncedMotors implements Command {

    public void setSynced(SyncedMotors synced) {
        this.synced = synced;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    SyncedMotors synced;
    double power;
    double ratio;

    public MoveSyncedMotors(SyncedMotors synced, double power, double ratio) {
        this.synced = synced;
        this.power = power;
        this.ratio = ratio;
    }

    @Override
    public boolean changeRobotState() throws InterruptedException {
        synced.setRatio(ratio);
        synced.setPower(power);
        return Thread.currentThread().isInterrupted();
    }
}