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
        commands.add(new Translate(5000, Translate.Direction.LEFT,0)); //move away from beacon towards corner of field in front of ramp
        commands.add(new Pause(100));
        commands.add(new Rotate(180, .5, 2000));
        commands.add(new Pause(100));
        commands.add(new Translate(300, Translate.Direction.FORWARD, 90)); //reference angle is different because earlier we added modifier to reference angle
        commands.add(new Pause(100));


    }
}
