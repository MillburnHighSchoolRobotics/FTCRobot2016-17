package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateLogicThread extends LogicThread {

    @Override
    public void loadCommands() {
        /*robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();*/
        Translate a = new Translate(10000, Translate.Direction.FORWARD, 0,1,0,null,5000);
        Translate b = new Translate(5000, Translate.Direction.BACKWARD, 0,1,0,null,5000);
        Translate c = new Translate(5000, Translate.Direction.FORWARD_LEFT, 0,1,0,null,5000);
        Translate d = new Translate(5000, Translate.Direction.BACKWARD_RIGHT, 0,1,0,null,5000);
        commands.add(a);
//        commands.add(new Pause(2000));
//        commands.add(b);
//        commands.add(new Pause(2000));
//        commands.add(c);
//        commands.add(new Pause(2000));
//        commands.add(d);
//

    }
}
