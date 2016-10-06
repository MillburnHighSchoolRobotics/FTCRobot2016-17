package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Translate;

/**
 * Created by youarereallypushingitdavid on 10/6/2016.
 */
public class RedStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(10, Translate.Direction.BACKWARD_LEFT,5));
        commands.add(new Translate(10, Translate.Direction.BACKWARD_RIGHT,5));
    }
}
