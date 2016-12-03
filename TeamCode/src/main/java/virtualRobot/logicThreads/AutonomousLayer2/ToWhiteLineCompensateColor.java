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
    public static final double MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE = 1750; //Max Displacement When we correct for momentum; if this fails all sensors are broken
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE = 2350; //Max Displacement To The First Line
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 7250; //Max Displacement To The Second Line
    public static final double ESCAPE_WALL = 200;
    AtomicBoolean allSensorsFail = new AtomicBoolean(); //has other Line Sensor triggered
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
            else if((getAvgDistance()> MAX_ALLOWABLE_DISPLACEMENT_BACK_TO_LINE) && type.getLine()== GodThread.LineType.SECOND){
                allSensorsFail.set(false);
                robot.addToProgress("AllSensorsFailed");
                return true;
            }
            return false;
        }
    };

    public ToWhiteLineCompensateColor( GodThread.Line type, AtomicBoolean allSensorsFail) {
        super();
        this.type = type;
        this.allSensorsFail = allSensorsFail;
    }


    @Override
    public void loadCommands() {
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        if (type.getLine() == GodThread.LineType.SECOND) {
            commands.add(new Translate(600, type.getColor() == GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0)); //so we don't recheck the same line
            commands.add(new Pause(200));
        }
        Translate firstDisplacement;
        if (type.getLine()== GodThread.LineType.FIRST)
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_FIRST_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, 1);
        else
            firstDisplacement = new Translate(MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0, 1);
        firstDisplacement.setExitCondition(atwhitelineFirst);
        commands.add(firstDisplacement);
        commands.add(new Pause(200));
        Translate secondDisplacement = new Translate(Translate.RunMode.HEADING_ONLY, type.getColor()== GodThread.ColorType.BLUE ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD, 0, .2);
        secondDisplacement.setExitCondition(atwhitelineSecond);
        commands.add(secondDisplacement);
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
