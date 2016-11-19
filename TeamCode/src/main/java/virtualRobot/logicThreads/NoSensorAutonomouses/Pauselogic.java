package virtualRobot.logicThreads.NoSensorAutonomouses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;

/**
 * Created by 17osullivand on 11/19/16.
 */

public class Pauselogic extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands() {
        commands.add(new Pause(5000));
    }
}
