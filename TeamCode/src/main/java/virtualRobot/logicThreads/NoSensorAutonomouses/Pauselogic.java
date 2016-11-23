package virtualRobot.logicThreads.NoSensorAutonomouses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;

/**
 * Created by 17osullivand on 11/19/16.
 */

public class Pauselogic extends LogicThread<AutonomousRobot> {
    private int wait;

    public Pauselogic() {
        wait = 5000;
    }

    public Pauselogic(int wait) {
        this.wait = wait;
    }

    @Override
    public void loadCommands() {
        commands.add(new Pause(wait));
    }
}
