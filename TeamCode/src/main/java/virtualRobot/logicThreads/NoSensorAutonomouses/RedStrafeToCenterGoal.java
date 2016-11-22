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
        commands.add(new Translate(7500, Translate.Direction.FORWARD_LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(500));
        commands.add(new Rotate(90,0.5,2000));
        commands.add(new Pause(500));
        commands.add(new Translate(1500, Translate.Direction.BACKWARD,0,1,90));
;
    }
}
