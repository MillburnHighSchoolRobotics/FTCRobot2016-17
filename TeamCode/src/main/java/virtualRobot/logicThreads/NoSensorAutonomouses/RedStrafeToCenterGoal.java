package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 11/18/16.
 */

public class RedStrafeToCenterGoal extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        Translate.setGlobalAngleMod(0);
        commands.add(new Translate(5000, Translate.Direction.LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(100));
        commands.add(new Rotate(160, .5, 1500));
        commands.add(new Pause(100));
        commands.add(new Translate(600, Translate.Direction.BACKWARD, 0, 1, 180, "To Ramp", 1000));
        commands.add(new Pause(100));


    }
}
