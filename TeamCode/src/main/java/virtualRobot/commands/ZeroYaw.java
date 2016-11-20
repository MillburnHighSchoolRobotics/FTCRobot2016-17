package virtualRobot.commands;

/**
 * Created by ethachu19 on 11/19/2016.
 */

public class ZeroYaw implements Command {

    @Override
    public boolean changeRobotState() throws InterruptedException {
        Command.AUTO_ROBOT.zeroYaw();
        return Thread.currentThread().isInterrupted();
    }
}
