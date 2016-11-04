package virtualRobot.logicThreads.AutonomousLayer2;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;
import virtualRobot.commands.addData;

/**
 * Created by 17osullivand on 11/3/16.
 * goes to whiteLine
 */

public class ToLineUltra extends LogicThread<AutonomousRobot>  {
    public static final double WALL_TRACE_SONAR_THRESHOLD = 8; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_LINE = 1500; //If we've gone this far, it means our line sensor is broken
    AtomicBoolean lineWorks;
    GodThread.Line type;
    public ToLineUltra(AtomicBoolean lineWorks, GodThread.Line type) {
        super();
        this.type = type;
        this.lineWorks = lineWorks;
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
        if (type==GodThread.Line.RED_FIRST_LINE || type==GodThread.Line.BLUE_SECOND_LINE) {
            WallTrace toWhiteLine = new WallTrace(WallTrace.Direction.BACKWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_LINE);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
        else if (type==GodThread.Line.RED_SECOND_LINE || type==GodThread.Line.BLUE_FIRST_LINE) {
            WallTrace toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_LINE);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
    }

}
