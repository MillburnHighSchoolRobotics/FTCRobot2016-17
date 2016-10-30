package virtualRobot.logicThreads;

import com.vuforia.Vuforia;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

/**
 * Created by Warren on 10/6/2016.
 */
public class RedMoveToSecondBeacon extends LogicThread<AutonomousRobot> {

    AtomicBoolean redIsLeft;
    VuforiaLocalizerImplSubclass vuforia;

    final double currentLine = RedAutonomousLogic.Line;
    final ExitCondition atwhiteline = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            if (Math.abs(robot.getLineSensor().getRawValue() - currentLine) > .7) {
                return true;
            }
            return false;
        }
    };

    public RedMoveToSecondBeacon(AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia){
        super();
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }
    @Override
    public void loadCommands() {
        WallTrace toWhiteLine =  new WallTrace(WallTrace.Direction.FORWARD, 9);
        toWhiteLine.setExitCondition(atwhiteline);
        commands.add(toWhiteLine);
        commands.add(new Pause(2000));
        WallTrace toWhiteLine2 =  new WallTrace(WallTrace.Direction.FORWARD, 9);
        toWhiteLine2.setExitCondition(atwhiteline);
        commands.add(toWhiteLine2);
        robot.addToProgress("Went to Line");
        commands.add(new Pause(2000));
        FTCTakePicture pic = new FTCTakePicture(this.redIsLeft,this.vuforia);
        commands.add(pic);
    }
}
