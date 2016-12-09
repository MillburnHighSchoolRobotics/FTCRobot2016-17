package virtualRobot.logicThreads.AutonomousLayer2;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 12/2/16.
 */

public class ToWhiteLineCompensateColor extends LogicThread<AutonomousRobot> {
    //Note that displacement is handled in exitCondition
    public static final double WALL_TRACE_SONAR_THRESHOLD = 15; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE = 2000; //Max Displacement When we correct for momentum; if this fails all sensors are broken
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE = 2000; //Max Displacement To The First Line
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 6550; //Max Displacement To The Second Line
    public static final double ESCAPE_WALL = 200;
    AtomicBoolean allSensorsFail; //has other Line Sensor triggered
    AtomicBoolean lastSensorTriggered, firstSensorTriggered;
    AtomicBoolean sonarWorks;
    GodThread.Line type;


    private static final int whiteTape = 13;
    private final ExitCondition atwhitelineFirst= new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)){
                robot.addToProgress("ColorSensorTriggered");
                return true;
            }
            else if(robot.getLightSensor1().getRawValue()> .61) {
                robot.addToProgress("LightSensor1Triggered");
                return true;
            }
            else if(robot.getLightSensor3().getRawValue()> .61) {
                robot.addToProgress("LightSensor3Triggered");
                return true;
            }
            else if(robot.getLightSensor2().getRawValue()> .61) {
                robot.addToProgress("LightSensor2Triggered");
                return true;
            }else if((robot.getLightSensor4().getRawValue()> .61)){
                robot.addToProgress("LightSensor4Triggered");
                return true;
            }
            return false;
        }
    };
    private final ExitCondition atwhitelineSecond= new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255)){
                allSensorsFail.set(false);

                robot.addToProgress("ColorSensorTriggered");
                return true;
            }
            else if(robot.getLightSensor1().getRawValue()> .61) {
                if (type.getColor() == GodThread.ColorType.RED) {
                    lastSensorTriggered.set(true);
                } else {
                    firstSensorTriggered.set(true);
                }
                allSensorsFail.set(false);
                robot.addToProgress("LightSensor1Triggered");
                return true;
            }
            else if(robot.getLightSensor3().getRawValue()> .61) {
                allSensorsFail.set(false);
                robot.addToProgress("LightSensor3Triggered");
                return true;
            }
            else if(robot.getLightSensor2().getRawValue()> .61) {
                allSensorsFail.set(false);
                robot.addToProgress("LightSensor2Triggered");
                return true;
            }else if((robot.getLightSensor4().getRawValue()> .61)){
                allSensorsFail.set(false);
                if (type.getColor() == GodThread.ColorType.BLUE) {
                    lastSensorTriggered.set(true);
                } else {
                    firstSensorTriggered.set(true);
                }
                robot.addToProgress("LightSensor4Triggered");
                return true;
            }

            return false;
        }
    };

    public ToWhiteLineCompensateColor( GodThread.Line type, AtomicBoolean firstSensorTriggered, AtomicBoolean lastSensorTriggered, AtomicBoolean allSensorsFail, AtomicBoolean sonarWorks) {
        super();
        this.type = type;
        this.allSensorsFail = allSensorsFail;
        this.firstSensorTriggered = firstSensorTriggered;
        this.lastSensorTriggered = lastSensorTriggered;
        this.sonarWorks = sonarWorks;
    }


    @Override
    public void loadCommands() {
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        if (type.getLine()== GodThread.LineType.FIRST && !sonarWorks.get()) {
            commands.add(new Translate(ESCAPE_WALL, Translate.Direction.LEFT, 0));
            commands.add(new Pause(200));
        }
        if (type.getLine() == GodThread.LineType.SECOND) {
            commands.add(new Translate(600, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0)); //so we don't recheck the same line
            commands.add(new Pause(200));
            }
        Translate firstDisplacement;
        if (type.getLine()== GodThread.LineType.FIRST)
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .2);
        else
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .2);
        allSensorsFail.set(true);
        firstDisplacement.setExitCondition(atwhitelineSecond);
        commands.add(firstDisplacement);
       /* commands.add(new Pause(500));
        Translate secondDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .1);
        secondDisplacement.setExitCondition(atwhitelineFirst);
        commands.add(secondDisplacement);
        commands.add(new Pause(500));
        allSensorsFail.set(true);
        Translate thirdDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, .1);
        thirdDisplacement.setExitCondition(atwhitelineSecond);
        commands.add(thirdDisplacement);*/
        commands.add(new Pause(200));

            if(robot.getLightSensor4().getRawValue()> .61)
                commands.add(new Translate(type.getLine() == GodThread.LineType.FIRST ? 75 : 100,Translate.Direction.BACKWARD,0).setTolerance(50));
            else if(robot.getLightSensor1().getRawValue()> .61)
                commands.add(new Translate(type.getLine() == GodThread.LineType.FIRST ? 75 : 100,Translate.Direction.FORWARD,0).setTolerance(50));

        if(type.getLine() == GodThread.LineType.SECOND && robot.getSonarLeft().getValue()> 21)
            commands.add(new Translate(75,Translate.Direction.FORWARD,0).setTolerance(50));


        commands.add(new Pause(200));
    }




    private double getAvgDistance() {
        double LFvalue = robot.getLFEncoder().getValue();
        double RFvalue = robot.getRFEncoder().getValue();
        double LBvalue = robot.getLBEncoder().getValue();
        double RBvalue = robot.getRBEncoder().getValue();
        Log.d("AVGDIST", " " + Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4));
        return (Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4;
    }
}