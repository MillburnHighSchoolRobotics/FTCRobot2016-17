package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by Warren on 10/6/2016.
 * For Red autonomous. Legalize it (still).
 */
public class RedStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(1500, Translate.Direction.FORWARD_LEFT,0));
        commands.add(new Pause(2000));
        commands.add(new Rotate(20));
        commands.add(new Pause(2000));
        commands.add(new Translate(1500, Translate.Direction.FORWARD,0));
        commands.add(new MoveMotor(robot.getReaperMotor()));
    }
}
