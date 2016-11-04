package virtualRobot.logicThreads.TestingAutonomouses;

import virtualRobot.LogicThread;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;

/**
 * Created by ethachu19 on 10/11/2016.
 */

public class testingTranslateLogicThread extends LogicThread {
    @Override
    public void loadCommands() {
//        ExitCondition e = new ExitCondition() {
//            @Override
//            public boolean isConditionMet()  {
//                int i = 0;
//                while (i < 2) {
//                    try {
//                        Thread.sleep(1000);
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    i++;
//                }
//                return true;
//            }
//        };
        Translate moveBack = new Translate(300, Translate.Direction.BACKWARD, 0);
//        moveBack.setExitCondition(e);

            commands.add(moveBack);
            commands.add(new Pause(2500));
//            commands.add(new Translate(30, Translate.Direction.FORWARD_RIGHT, 0));
    }
}
