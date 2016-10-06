package virtualRobot.logicThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Translate;

/**
 * Created by goddamnitdavid on 10/6/2016.
 */
public class RedMoveToSecondBeacon extends LogicThread<AutonomousRobot> {

    AtomicBoolean mychangesareded;

    final ExitCondition atwhiteline = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            if (robot.getLineSensor().getRawValue() > 10) {
                return true;
            }
            return false;
        }
    };

    public RedMoveToSecondBeacon(AtomicBoolean screwpullproblems){
        super();
        mychangesareded = screwpullproblems;
    }
    @Override
    public void loadCommands() {
        Translate moveToSecondWLine = new Translate(1000, Translate.Direction.BACKWARD, 10, 10);
        moveToSecondWLine.setExitCondition(atwhiteline);
        commands.add(moveToSecondWLine);
        FTCTakePicture gitgood = new FTCTakePicture(mychangesareded);
        commands.add(gitgood);
    }
}
