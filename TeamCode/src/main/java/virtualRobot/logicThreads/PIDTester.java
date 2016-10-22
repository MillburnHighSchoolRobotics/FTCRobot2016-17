package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by Yanjun on 11/28/2015.
 */
public class PIDTester extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands() {
        Translate.setGlobalMaxPower(1.0);
        //commands.add(new Translate(7000, Translate.Direction.FORWARD, 0));
        //commands.add(new Pause(3000));
        commands.add(new Translate(4000, Translate.Direction.FORWARD, 0));
        //commands.add(new Translate(7000, Translate.Direction.FORWARD, 0));
       // commands.add(new Rotate(30, 1));


    }
}
