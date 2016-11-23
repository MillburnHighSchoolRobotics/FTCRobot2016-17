package virtualRobot.logicThreads.AutonomousLayer2;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 11/3/16.
 * goes to whiteLine
 */

public class ToWhiteLine extends LogicThread<AutonomousRobot>  {
    //Note that displacement is handled in exitCondition
    public static final double WALL_TRACE_SONAR_THRESHOLD = 15; //How close we want to trace wall
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_LINE = 2350; //If we've gone this far, it means our line sensor is broken
    public static final double MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE = 7250;
    public static final double ESCAPE_WALL = 200;
   private boolean ultraWorks; //Does ultra work
    AtomicBoolean farDisplacedment = new AtomicBoolean(); //has other Line Sensor triggered
    AtomicBoolean exceededMax = new AtomicBoolean();
    GodThread.Line type;


    private static final int whiteTape = 20;
    private final ExitCondition atwhitelineRed= new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape && robot.getColorSensor().getBlue() < 255) ||
                    (robot.getLightSensor3().getRawValue()> .65)||
                    (robot.getLightSensor2().getRawValue()> .65)){
                robot.addToProgress("MiddleSensorsTriggered");
                farDisplacedment.set(false);
                return true;
            }else if((robot.getLightSensor1().getRawValue()> .65)){
                robot.addToProgress("FarSensorTriggered");

                farDisplacedment.set(true);
                return true;
            }else if((getAvgDistance()> MAX_ALLOWABLE_DISPLACEMENT_TO_LINE) && type.getLine()== GodThread.LineType.FIRST){
                robot.addToProgress("OverShotLine");

                exceededMax.set(true);
                return true;
            }
            else if((getAvgDistance()> MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE) && type.getLine()== GodThread.LineType.SECOND){
                robot.addToProgress("OverShotLine");

                exceededMax.set(true);
                return true;
            }
            return false;
        }
    };
    private final ExitCondition atwhitelineBlue= new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one
            if((robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape) ||
                    (robot.getLightSensor3().getRawValue()> .65)||
                    (robot.getLightSensor2().getRawValue()> .65)){
                farDisplacedment.set(false);
                return true;
            }else if((robot.getLightSensor4().getRawValue()> .65)){
                farDisplacedment.set(true);
                return true;
            }else if((getAvgDistance()> MAX_ALLOWABLE_DISPLACEMENT_TO_LINE) && type.getLine()== GodThread.LineType.FIRST){
                exceededMax.set(true);
                return true;
            }
            else if((getAvgDistance()> MAX_ALLOWABLE_DISPLACEMENT_TO_SECOND_LINE) && type.getLine()== GodThread.LineType.SECOND){
                exceededMax.set(true);
                return true;
            }
            return false;
        }
    };

    public ToWhiteLine(boolean ultraworks, GodThread.Line type, AtomicBoolean farDisplacedment,AtomicBoolean mope) {
        super();
        this.type = type;
        this.ultraWorks = ultraworks;
        this.farDisplacedment = farDisplacedment;
        this.exceededMax = mope;
    }


    @Override
    public void loadCommands() {
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        if(this.ultraWorks){
            withUltra();
        }else{
            withoutUltra();
        }

    }

    public void withUltra(){
        robot.addToProgress("Going To Line with Ultra");
        commands.add(new Pause(200));
        commands.add(new Rotate(0,0.5,2000));
        commands.add(new Pause(200));
        if (type.getColor()==GodThread.ColorType.RED) {
            if (type.getLine()==GodThread.LineType.SECOND)
                commands.add(new Translate(500, Translate.Direction.BACKWARD, 0));
            WallTrace toWhiteLine;
                toWhiteLine = new WallTrace(WallTrace.Direction.BACKWARD, WALL_TRACE_SONAR_THRESHOLD);
                toWhiteLine.setExitCondition(atwhitelineRed);
            commands.add(new Pause(500));
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
            commands.add(new Rotate(0, 1, 2000));
            commands.add(new Pause(500));


        }
        else  {
            WallTrace toWhiteLine;
            commands.add(new Pause(500));
            if (type.getLine()==GodThread.LineType.SECOND)
                commands.add(new Translate(500, Translate.Direction.FORWARD, 0));
            commands.add(new Pause(500));

                toWhiteLine = new WallTrace(WallTrace.Direction.FORWARD, WALL_TRACE_SONAR_THRESHOLD);
                toWhiteLine.setExitCondition(atwhitelineBlue);
            commands.add(toWhiteLine);
            commands.add(new Pause(500));
            commands.add(new Rotate(0,1, 2000));

            commands.add(new Pause(500));
        }
    }

    public void withoutUltra(){
        robot.addToProgress("Going To Line with NO Ultra");

        commands.add(new Pause(200));
        commands.add(new Rotate(0,0.5,2000));
        commands.add(new Pause(200));
        if (type.getLine()== GodThread.LineType.FIRST) {
            commands.add(new Translate(ESCAPE_WALL, Translate.Direction.LEFT, 0));
            commands.add(new Pause(200));
        }
        if (type.getColor()==GodThread.ColorType.RED) {
            if (type.getLine()==GodThread.LineType.SECOND)
                commands.add(new Translate(500, Translate.Direction.BACKWARD, 0));
            commands.add(new Pause(200));
            Translate toWhiteLine;
            toWhiteLine = new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.BACKWARD, 0, .2);
            toWhiteLine.setExitCondition(atwhitelineRed);
            commands.add(toWhiteLine);
            commands.add(new Pause(200));
            commands.add(new Rotate(0,1, 2000));
            commands.add(new Pause(200));

        }
        else  {
            if (type.getLine()==GodThread.LineType.SECOND)
                commands.add(new Translate(500, Translate.Direction.FORWARD, 0));
            commands.add(new Pause(200));
            Translate toWhiteLine;
            commands.add(new Translate(2000,Translate.Direction.BACKWARD,0 ));
            commands.add(new Pause(200));
            toWhiteLine = new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.FORWARD, 0, .2);
            toWhiteLine.setExitCondition(atwhitelineBlue);
            commands.add(toWhiteLine);
            commands.add(new Pause(200));
            commands.add(new Rotate(0,1, 2000));
            commands.add(new Pause(200));
        }
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
