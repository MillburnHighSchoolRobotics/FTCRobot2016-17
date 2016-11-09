package virtualRobot.logicThreads.AutonomousLayer2;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 11/3/16.
 * goes to whiteLine
 */

public class ToLineUltra extends LogicThread<AutonomousRobot>  {
    public static final double WALL_TRACE_SONAR_THRESHOLD = 11; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_LINE = 5500; //If we've gone this far, it means our line sensor is broken
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 6500;
    AtomicBoolean lineWorks;
    GodThread.Line type;
    double targetLine;
    boolean linePassed = false;
    boolean lineAlreadyWorks = false;
    boolean lineEntered = false; //checks if they entered wheter or not line works
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
    public ToLineUltra(AtomicBoolean lineWorks, GodThread.Line type, double targetLine, boolean lineAlreadyWorks) {
        super();
        this.type = type;
        this.lineWorks = lineWorks;
        this.targetLine = targetLine;
        linePassed = true;
        this.lineAlreadyWorks = lineAlreadyWorks;
        lineEntered = true;
    }
    @Override
    public void loadCommands() {
        if (!linePassed)
            targetLine = robot.getLightSensor1().getRawValue();//The current value of the color sensor
        final AtomicBoolean farDisplacedment = new AtomicBoolean(false);
        final int whiteTape = 5;
        final ExitCondition atwhitelineFIRST = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                //This checks the right1 light sensor or the color sensor
                //Second Conditional check right2 light sensor.
                if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape) ||
                        (Math.abs(robot.getLightSensor3().getRawValue() - targetLine) > 1.85)){
                    lineWorks.set(true);
                    Log.d("LINELINELINE", " GOOD:" + robot.getLightSensor3().getRawValue());
                    return true;
                }else if((Math.abs(robot.getLightSensor4().getRawValue() - targetLine) > 1.85)){
                    farDisplacedment.set(true);
                    lineWorks.set(true);
                    Log.d("LINELINELINE", " GOOD:" + robot.getLightSensor4().getRawValue());
                    return true;
                }
                lineWorks.set(false);
                return false;
            }
        }; //if our line sensor detects a change >.7, we're at the line, stop moving!
        final ExitCondition atwhitelineSECOND = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                //This checks the left1 light sensor or the color sensor
                //Second Conditional check left2 light sensor.
                if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape) ||
                        (Math.abs(robot.getLightSensor2().getRawValue() - targetLine) > 1.85)){
                    lineWorks.set(true);
                    Log.d("LINELINELINE", " GOOD:" + robot.getLightSensor2().getRawValue());
                    return true;
                }else if((Math.abs(robot.getLightSensor1().getRawValue() - targetLine) > 1.85)){
                    farDisplacedment.set(true);
                    lineWorks.set(true);
                    Log.d("LINELINELINE", " GOOD:" + robot.getLightSensor1().getRawValue());
                    return true;
                }
                lineWorks.set(false);
                return false;
            }
        };
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
            if ((lineAlreadyWorks && lineEntered) || !lineEntered)
            toWhiteLine.setExitCondition(atwhitelineFIRST);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
            if(farDisplacedment.get()){
                //random value needs adjustment
                commands.add(new Translate(400, Translate.Direction.BACKWARD, 0));
                commands.add(new Pause(500));
            }

        }
        else if (type==GodThread.Line.RED_SECOND_LINE || type==GodThread.Line.BLUE_FIRST_LINE) {
            WallTrace toWhiteLine;
            if (type == GodThread.Line.BLUE_FIRST_LINE )
                toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_LINE);
            else
                toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD, MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE);
            if ((lineAlreadyWorks && lineEntered) || !lineEntered)
            toWhiteLine.setExitCondition(atwhitelineSECOND);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
            if(farDisplacedment.get()){
                //random value needs adjustment
                commands.add(new Translate(400, Translate.Direction.BACKWARD, 0));
                commands.add(new Pause(500));
            }
        }
    }

}
