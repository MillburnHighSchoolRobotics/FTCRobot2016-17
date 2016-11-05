package virtualRobot.logicThreads.TestingAutonomouses;

import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateLogicThread extends LogicThread {
    @Override
    public void loadCommands() {
        commands.add(new Translate(150, Translate.Direction.LEFT, 0));
        commands.add(new Pause(1000));

    }
}
