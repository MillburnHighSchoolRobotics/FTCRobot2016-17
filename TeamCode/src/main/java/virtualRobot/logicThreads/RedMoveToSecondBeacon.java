package virtualRobot.logicThreads;

import com.vuforia.Vuforia;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Translate;

/**
 * Created by Warren on 10/6/2016.
 */
public class RedMoveToSecondBeacon extends LogicThread<AutonomousRobot> {

    AtomicBoolean redIsLeft;
    VuforiaLocalizerImplSubclass vuforia;

    final ExitCondition atwhiteline = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            if (robot.getLineSensor().getRawValue() > 10) {
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
        Translate moveToSecondWLine = new Translate(1000, Translate.Direction.BACKWARD, 0);
        moveToSecondWLine.setExitCondition(atwhiteline);
        commands.add(moveToSecondWLine);
        FTCTakePicture gitgood = new FTCTakePicture(this.redIsLeft,this.vuforia);
        commands.add(gitgood);
    }
}
