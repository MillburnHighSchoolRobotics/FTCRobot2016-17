package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 12/2/16.
 */

public class ColorCompensator extends LogicThread<AutonomousRobot> {
    GodThread.Line type;
    double timeLimit;
    AtomicBoolean redisLeft;
    VuforiaLocalizerImplSubclass vuforia;
    public ColorCompensator(GodThread.Line type, double timeLimit, AtomicBoolean redisLeft, VuforiaLocalizerImplSubclass vuforia ) {this.type = type;
    this.timeLimit = timeLimit; this.redisLeft = redisLeft; this.vuforia = vuforia;}

    @Override
    public void loadCommands() {
        commands.add(new Pause(500));
        commands.add(new PreciseAllign(timeLimit, redisLeft, vuforia));
        commands.add(new Pause(200));

        //commands.add(new CompensateColor(1200, type.getColor() == GodThread.ColorType.RED ? 2 : 2.5));
        //commands.add(new Pause(200));
       // commands.add(new Translate(50, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD,0,0.2).setTolerance(25));
        //commands.add(new Pause(200));


    }
}
