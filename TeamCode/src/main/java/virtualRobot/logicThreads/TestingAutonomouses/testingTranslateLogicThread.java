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
            Translate c = new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.BACKWARD, 0, .2);
        commands.add(c);


    }
}
