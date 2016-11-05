package virtualRobot.logicThreads.AutonomousLayer2;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 11/3/16.
 * goes to whiteLine
 */

public class ToLineUltra extends LogicThread<AutonomousRobot>  {
    public static final double WALL_TRACE_SONAR_THRESHOLD = 10; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_LINE = 5500; //If we've gone this far, it means our line sensor is broken
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 6500;
    AtomicBoolean lineWorks;
    GodThread.Line type;
    double targetLine;
    boolean linePassed = false;
    public ToLineUltra(AtomicBoolean lineWorks, GodThread.Line type) {
        super();
        this.type = type;
        this.lineWorks = lineWorks;
    }
    public ToLineUltra(AtomicBoolean lineWorks, GodThread.Line type, double targetLine) {
        super();
        this.type = type;
        this.lineWorks = lineWorks;
        this.targetLine = targetLine;
        linePassed = true;
    }

    @Override
    public void loadCommands() {
        if (!linePassed)
        targetLine = robot.getLineSensor().getRawValue(); //The current value of the color sensor
        final ExitCondition atwhiteline = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (Math.abs(robot.getLineSensor().getRawValue() - targetLine) > 2) {
                    lineWorks.set(true);
                    Log.d("LINELINELINE", " GOOD:" + robot.getLineSensor().getRawValue());
                    return true;
                }
                lineWorks.set(false);
                return false;
            }
        }; //if our line sensor detects a change >.7, we're at the line, stop moving!
        Log.d("LINELINELINE", " " + targetLine);
        data.add(targetLine);
        robot.addToProgress("Going To Line with Ultra");
        commands.add(new Pause(500));
        if (type==GodThread.Line.RED_FIRST_LINE || type==GodThread.Line.BLUE_SECOND_LINE) {
            WallTrace toWhiteLine;
            if (type == GodThread.Line.RED_FIRST_LINE )
           toWhiteLine = new WallTrace(WallTrace.Direction.BACKWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_LINE);
            else
                toWhiteLine = new WallTrace(WallTrace.Direction.BACKWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
        else if (type==GodThread.Line.RED_SECOND_LINE || type==GodThread.Line.BLUE_FIRST_LINE) {
            WallTrace toWhiteLine;
            if (type == GodThread.Line.BLUE_FIRST_LINE )
                toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_LINE);
            else
                toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE);
            toWhiteLine.setExitCondition(atwhiteline);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
        }
    }

}
