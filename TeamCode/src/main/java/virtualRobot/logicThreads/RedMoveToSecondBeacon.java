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
 * Created by goddamnitdavid on 10/6/2016.
 */
public class RedMoveToSecondBeacon extends LogicThread<AutonomousRobot> {

    AtomicBoolean mychangesareded;
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

    public RedMoveToSecondBeacon(AtomicBoolean screwpullproblems, VuforiaLocalizerImplSubclass cancer){
        super();
        mychangesareded = screwpullproblems;
        this.vuforia = cancer;
    }
    @Override
    public void loadCommands() {
        Translate moveToSecondWLine = new Translate(1000, Translate.Direction.BACKWARD, 0);
        moveToSecondWLine.setExitCondition(atwhiteline);
        commands.add(moveToSecondWLine);
        FTCTakePicture gitgood = new FTCTakePicture(mychangesareded,vuforia);
        commands.add(gitgood);
    }
}
