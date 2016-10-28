package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 10/28/16.
 */

public class BlueStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(1500, Translate.Direction.FORWARD_RIGHT,0));
        commands.add(new Pause(2000));
        commands.add(new Rotate(-10));
        commands.add(new Pause(2000));
        commands.add(new Translate(1000, Translate.Direction.FORWARD,0));
        commands.add(new MoveMotor(robot.getReaperMotor()));
    }
}
