package virtualRobot.logicThreads;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

import static virtualRobot.GodThread.ColorType.*;

/**
 * Created by 17osullivand on 11/18/16.
 */

public class CompensateForMiss extends LogicThread<AutonomousRobot> {

    final int maxdistance = 1750;

    AtomicBoolean lightTriggered; //hits sensor successfully
    AtomicBoolean exceedMaxDistance; //exceeds max distance
    GodThread.Line line;
    TriggerLevel howtriggered;
    boolean withUltra;
    private static double BLIND_ADJUSTMENT = 500; //if all of our line sensors fail
    private static double LIGHT_ADJUSTMENT = 200; //if we get to a "far displacement"
    private static double SMALL_ADJUSTMENT = 135;
    private static double SMALL_ADJUSTMENT_RED = 150;
    final ExitCondition hitsLineRed = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            if((robot.getLightSensor4().getRawValue()> .65)){
                lightTriggered.set(true);
                return true;
            }
            if(getAvgDistance() > maxdistance){
                exceedMaxDistance.set(true);
                return true;
            }
            return false;
        }
    };
    final ExitCondition hitsLineBlue = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            if((robot.getLightSensor1().getRawValue()> .65)){
                lightTriggered.set(true);
                return true;
            }
            if(getAvgDistance() > maxdistance){
                exceedMaxDistance.set(true);
                return true;
            }
            return false;
        }
    };
    public CompensateForMiss(TriggerLevel t, GodThread.Line line, boolean withUltra){
        this.howtriggered = t;
        this.line = line;
        this.withUltra = withUltra;
    }
    public CompensateForMiss(TriggerLevel t, AtomicBoolean lightTriggered, AtomicBoolean exceedMaxDistance, GodThread.Line line, boolean withUltra){
        this.howtriggered = t;
        this.lightTriggered = lightTriggered;
        this.exceedMaxDistance = exceedMaxDistance;
        this.line = line;
        this.withUltra = withUltra;
    }
    /*
    Switch Reference

    MildlyTriggered: Runs after line sensor 1 is triggered. Hey not bad.

    DavidTriggered: Runs after line sensor 4 is triggered. Pretty triggered.

    AssumedMyGender: Runs after line sensor 1 fails. Kinda triggered at this point.

    Didn't Tell David I was Going to Leave eErly: Runs after Line sensor 4 fails. Blind PID. Worst case scenario.
    Likely to cause pointless arguments that don't go anywhere.

     */
    @Override
    public void loadCommands() {
        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        if(this.withUltra){
            withUltra();
        }else{
            withoutUltra();
        }

    }

    public void withUltra() {

        switch(howtriggered){
            case FIRSTLIGHTTRIGGERED:
                if (line.getColor() == RED)
                    commands.add(new Translate(LIGHT_ADJUSTMENT,Translate.Direction.FORWARD,0));
                else
                    commands.add(new Translate(LIGHT_ADJUSTMENT,Translate.Direction.BACKWARD,0));
                commands.add(new Pause(500));
                break;
            case LASTLIGHTTRIGGERED:
                if (line.getColor() == RED)
                    commands.add(new Translate(LIGHT_ADJUSTMENT,Translate.Direction.BACKWARD,0));
                else
                    commands.add(new Translate(LIGHT_ADJUSTMENT,Translate.Direction.FORWARD,0));
                commands.add(new Pause(500));
                break;

            case FIRSTLIGHTFAILS:
                WallTrace moveLeft;
                if (line.getColor() == RED) {
                    moveLeft = new WallTrace(WallTrace.Direction.FORWARD);
                    moveLeft.setExitCondition(hitsLineRed);
                    commands.add(moveLeft);
                }
                else {
                    moveLeft = new WallTrace(WallTrace.Direction.BACKWARD);
                    moveLeft.setExitCondition(hitsLineBlue);
                    commands.add(moveLeft);
                }
                commands.add(new Pause(500));
                break;

            case LASTLIGHTFAILS:
                if (line.getColor() == RED)
                    commands.add(new Translate(BLIND_ADJUSTMENT,Translate.Direction.BACKWARD,0));
                else
                    commands.add(new Translate(BLIND_ADJUSTMENT,Translate.Direction.FORWARD,0));
                commands.add(new Pause(500));
                break;
            case SMALLCORRECTION:
                if (line.getColor() == BLUE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT, Translate.Direction.BACKWARD, 0));
                }
                else if (line == GodThread.Line.RED_SECOND_LINE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT_RED, Translate.Direction.FORWARD, 0));
                }
                else if(line == GodThread.Line.RED_FIRST_LINE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT, Translate.Direction.FORWARD, 0));
                }
                break;

        }
    }
    public void withoutUltra() {

        robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();
        switch(howtriggered){
            case FIRSTLIGHTTRIGGERED:
                if (line.getColor() == RED)
                    commands.add(new Translate(200,Translate.Direction.FORWARD,0));
                else
                    commands.add(new Translate(200,Translate.Direction.BACKWARD,0));
                break;
            case LASTLIGHTTRIGGERED:
                if (line.getColor() == RED)
                    commands.add(new Translate(200,Translate.Direction.BACKWARD,0));
                else
                    commands.add(new Translate(200,Translate.Direction.FORWARD,0));
                break;
            case FIRSTLIGHTFAILS:
              Translate moveLeft;
                if (line.getColor() == RED) {
                    moveLeft = new Translate(Translate.RunMode.CUSTOM, Translate.Direction.FORWARD, 0, .15);
                    moveLeft.setExitCondition(hitsLineRed);
                    commands.add(moveLeft);
                }
                else {
                    moveLeft = new Translate(Translate.RunMode.CUSTOM, Translate.Direction.BACKWARD, 0, .15);
                    moveLeft.setExitCondition(hitsLineBlue);
                    commands.add(moveLeft);
                }
                break;

            case LASTLIGHTFAILS:
                if (line.getColor() == RED)
                    commands.add(new Translate(BLIND_ADJUSTMENT,Translate.Direction.BACKWARD,0));
                else
                    commands.add(new Translate(BLIND_ADJUSTMENT,Translate.Direction.FORWARD,0));
                break;
            case SMALLCORRECTION:
                if (line.getColor() == BLUE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT, Translate.Direction.BACKWARD, 0));
                }
                else if (line == GodThread.Line.RED_SECOND_LINE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT_RED, Translate.Direction.FORWARD, 0));
                }
                else if(line == GodThread.Line.RED_FIRST_LINE) {
                    commands.add(new Pause(300));
                    robot.addToProgress("Small Correction");
                    commands.add(new Translate(SMALL_ADJUSTMENT, Translate.Direction.FORWARD, 0));
                }
                break;


        }
    }
    public enum TriggerLevel{
        FIRSTLIGHTTRIGGERED,LASTLIGHTTRIGGERED,FIRSTLIGHTFAILS,LASTLIGHTFAILS, SMALLCORRECTION
    };

    //Make this a global bot function perhaps.
    private double getAvgDistance() {
        double LFvalue = robot.getLFEncoder().getValue();
        double RFvalue = robot.getRFEncoder().getValue();
        double LBvalue = robot.getLBEncoder().getValue();
        double RBvalue = robot.getRBEncoder().getValue();
        return (Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(LBvalue) + Math.abs(RBvalue))/4;
    }
}
