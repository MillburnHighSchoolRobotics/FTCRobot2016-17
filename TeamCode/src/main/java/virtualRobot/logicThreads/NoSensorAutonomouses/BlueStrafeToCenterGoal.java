package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 11/18/16.
 */

public class BlueStrafeToCenterGoal extends LogicThread<AutonomousRobot>{
    @Override
    public void loadCommands (){
        Translate.setGlobalAngleMod(0);
        commands.add(new Translate(7700, Translate.Direction.LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(200));
        commands.add(new Rotate(-180,0.5,1000));
        commands.add(new Pause(200));
        commands.add(new Translate(600, Translate.Direction.FORWARD, 0, 1, -180, "To Ramp", 1000));
    }

}
