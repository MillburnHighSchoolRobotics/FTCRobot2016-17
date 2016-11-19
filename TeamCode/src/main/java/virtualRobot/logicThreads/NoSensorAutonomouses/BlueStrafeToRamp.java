package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 10/28/16.
 * Gets on ramp, deposits balls
 */
@Deprecated
public class BlueStrafeToRamp extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new Translate(7000, Translate.Direction.BACKWARD, 0));
        commands.add(new Pause(500));
        commands.add(new Translate(5500, Translate.Direction.BACKWARD_LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(500));
        commands.add(new Rotate(335)); //Rotate to face ramp
        commands.add(new Pause(500));
        commands.add(new Translate(3000, Translate.Direction.FORWARD,0)); //Get onto Ramp
//        commands.add(new MoveMotor(robot.getReaperMotor())); //Spin the balls into ramp
    }
}
