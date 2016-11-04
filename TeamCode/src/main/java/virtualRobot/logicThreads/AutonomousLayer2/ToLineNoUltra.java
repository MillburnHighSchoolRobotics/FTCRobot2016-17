package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.addData;

/**
 * Created by 17osullivand on 11/3/16.
 * Goes to Line
 */

public class ToLineNoUltra extends LogicThread<AutonomousRobot>  {
    public static final double ESCAPE_WALL = 400; //we know that we crashed into to the wall on account of our broken sonar, so we need to escape
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_LINE = ToLineUltra.MAX_ALLOWABLE_DISPLACEMENT_TO_LINE; //if we've gone this far our line sensor is broken as well! Oh no :(
    AtomicBoolean  lineWorks;
    GodThread.Line type;
    public ToLineNoUltra(AtomicBoolean lineWorks, GodThread.Line type) {
        super();
        this.lineWorks = lineWorks;
        this.type = type;
    }
    @Override
    public void loadCommands() {

        final double currentLine = robot.getLineSensor().getRawValue(); //The current value of the color sensor
        final ExitCondition atwhiteline = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (Math.abs(robot.getLineSensor().getRawValue() - currentLine) > .7) {
                    lineWorks.set(true);
                    return true;
                }
                lineWorks.set(false);
                return false;
            }
        }; //if our line sensor detects a change >.7, we're at the line, stop moving!
        commands.add(new addData(this, currentLine));
        commands.add(new Pause(500));
        commands.add(new Translate(ESCAPE_WALL, Translate.Direction.LEFT, 0)); //Welp we've just missed our sonar so lets get outttt of here
        commands.add(new Pause(500));
        if (type == GodThread.Line.RED_FIRST_LINE || type == GodThread.Line.BLUE_SECOND_LINE) {
            Translate toWhiteLine = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_LINE, Translate.Direction.BACKWARD, 0, .15);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
        else if (type == GodThread.Line.RED_SECOND_LINE || type == GodThread.Line.BLUE_FIRST_LINE) {
            Translate toWhiteLine = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_LINE, Translate.Direction.FORWARD, 0, .15);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
    }

}