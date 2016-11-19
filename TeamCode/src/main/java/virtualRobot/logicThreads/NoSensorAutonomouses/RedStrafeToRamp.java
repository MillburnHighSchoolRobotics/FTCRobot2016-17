package virtualRobot.logicThreads.NoSensorAutonomouses;

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
@Deprecated
public class RedStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(7000, Translate.Direction.FORWARD, 0));
        commands.add(new Pause(500));
        commands.add(new Translate(5500, Translate.Direction.FORWARD_LEFT,0));
        commands.add(new Pause(500));
        commands.add(new Rotate(25)); //Rotate to face ramp
        commands.add(new Pause(500));
        commands.add(new Translate(3000, Translate.Direction.FORWARD,0)); //Get onto Ramp
//        commands.add(new MoveMotor(robot.getReaperMotor())); //Spin balls into ramp
    }
}
