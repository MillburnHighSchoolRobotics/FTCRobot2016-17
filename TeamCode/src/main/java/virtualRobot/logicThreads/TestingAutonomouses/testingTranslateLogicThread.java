package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateLogicThread extends LogicThread {
    private final ExitCondition testing= new ExitCondition() {
        @Override
        public boolean isConditionMet() {//checks if tape or light sensors close to tape are triggered, then checks far one

             if((getAvgDistance()> 7250)){

                return true;
            }
            return false;
        }
    };
    @Override
    public void loadCommands() {
        /*robot.getLFEncoder().clearValue();
        robot.getRFEncoder().clearValue();
        robot.getLBEncoder().clearValue();
        robot.getRBEncoder().clearValue();*/
        Translate c = new Translate(Translate.RunMode.CUSTOM, Translate.Direction.FORWARD, 0, 1);
        commands.add(c);


    }
    private double getAvgDistance() {
        double LFvalue = robot.getLFEncoder().getValue();
        double RFvalue = robot.getRFEncoder().getValue();
       // double LBvalue = robot.getLBEncoder().getValue();
        double RBvalue = robot.getRBEncoder().getValue();
        Log.d("AVGDIST", " " + Math.abs((Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(RBvalue))/3));
        return (Math.abs(LFvalue) + Math.abs(RFvalue) + Math.abs(RBvalue))/3;
    }
}
