package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.fastRedIsLeft;

/**
 * Created by 17osullivand on 12/2/16.
 */

public class ColorCompensator extends LogicThread<AutonomousRobot> {
    GodThread.Line type;
    double timeLimit;
    AtomicBoolean redisLeft, sonarWorks, colorTriggered;
    VuforiaLocalizerImplSubclass vuforia;
    public ColorCompensator(GodThread.Line type, double timeLimit, AtomicBoolean redisLeft, AtomicBoolean sonarWorks, AtomicBoolean colorTriggered, VuforiaLocalizerImplSubclass vuforia ) {this.type = type;
    this.timeLimit = timeLimit; this.redisLeft = redisLeft; this.sonarWorks = sonarWorks; this.colorTriggered = colorTriggered; this.vuforia = vuforia;}

    @Override
    public void loadCommands() {
        if (type == GodThread.Line.BLUE_SECOND_LINE) {
            commands.add(new Translate(50, Translate.Direction.FORWARD,0).setTolerance(25));
        }
            commands.add(new CompensateColor(timeLimit));
            commands.add(new Pause(200));

        commands.add(new fastRedIsLeft(redisLeft, vuforia));
        commands.add(new Pause(200));



    }
}
