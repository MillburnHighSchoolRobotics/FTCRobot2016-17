package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Translate;

/**
 * Created by Yanjun on 11/28/2015.
 */
public class PIDTester extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands() {
        Translate.setGlobalMaxPower(1.0);
        commands.add(new Translate(1500, Translate.Direction.FORWARD));
    }
}
