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
 * Gets on ramp, deposits balls
 */
public class RedStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(1500, Translate.Direction.FORWARD_LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(2000));
        commands.add(new Rotate(20)); //Rotate to face ramp
        commands.add(new Pause(2000));
        commands.add(new Translate(1500, Translate.Direction.FORWARD,0)); //Get onto Ramp
        commands.add(new MoveMotor(robot.getReaperMotor())); //Spin balls into ramp
    }
}
