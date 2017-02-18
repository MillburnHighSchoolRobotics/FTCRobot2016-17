package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.PreciseAllign;
import virtualRobot.commands.Translate;

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
        //if (!colorTriggered.get());
      //  {
            commands.add(new CompensateColor(2500));
            commands.add(new Pause(200));
        //}
        if (type == GodThread.Line.RED_FIRST_LINE) {
            commands.add(new Translate(50, Translate.Direction.FORWARD,0).setTolerance(25));
        }
        if (type == GodThread.Line.RED_SECOND_LINE) {
            commands.add(new Translate(75, Translate.Direction.BACKWARD,0).setTolerance(25));
        }
        commands.add(new fastRedIsLeft(redisLeft, vuforia));

        commands.add(new Pause(200));
        //commands.add(new PreciseAllign(timeLimit, redisLeft, sonarWorks, vuforia,  type));

        //commands.add(new CompensateColor(1200, type.getColor() == GodThread.ColorType.RED ? 2 : 2.5));
        //commands.add(new Pause(200));
       // commands.add(new Translate(50, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD,0,0.2).setTolerance(25));
        //commands.add(new Pause(200));


    }
}
