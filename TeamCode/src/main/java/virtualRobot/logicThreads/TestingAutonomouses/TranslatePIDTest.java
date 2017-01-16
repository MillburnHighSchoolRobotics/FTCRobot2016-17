package virtualRobot.logicThreads.TestingAutonomouses;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.Translate;
import virtualRobot.utils.MathUtils;

/**
 * Created by ethachu19 on 1/15/2017.
 */

public class TranslatePIDTest extends LogicThread {
    Translate.Direction dir;
    double kP;

    public TranslatePIDTest(long iter){
        dir = iter % 2 == 0 ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD;
        this.kP = KPModifier.kP;
    }

    @Override
    public void loadCommands() {
        commands.add(new Translate(kP,7000,-1,new AtomicBoolean(), dir));
    }
}
