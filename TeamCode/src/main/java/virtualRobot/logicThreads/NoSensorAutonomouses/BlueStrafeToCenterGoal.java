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
        commands.add(new Translate(5500, Translate.Direction.BACKWARD_LEFT,0)); //move away from beacon towards corner of field in front of ramp

    }

}
