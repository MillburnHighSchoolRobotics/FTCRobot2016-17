package virtualRobot.logicThreads.AutonomousLayer2;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by 17osullivand on 12/2/16.
 */

public class ColorCompensator extends LogicThread<AutonomousRobot> {
    GodThread.Line type;
    public ColorCompensator(GodThread.Line type) {this.type = type;}

    @Override
    public void loadCommands() {
        commands.add(new CompensateColor(1200, type.getColor() == GodThread.ColorType.RED ? 2 : 2.5));
        commands.add(new Pause(200));
       // commands.add(new Translate(50, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD,0,0.2).setTolerance(25));
        //commands.add(new Pause(200));


    }
}
