package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;

/**
 * Created by 17osullivand on 11/3/16.
 */

public class addData implements Command {
    private LogicThread<AutonomousRobot> logicThread;
   private Object[] myData;
    public addData(LogicThread<AutonomousRobot> logicThread, Object... data) {
        this.logicThread = logicThread;
        this.myData = data;
    }
    @Override
    public boolean changeRobotState() throws InterruptedException {
        return false;
    }
    public LogicThread<AutonomousRobot> getLogicThread() {
        return logicThread;
    }
    public Object[] getMyData() {
        return myData;
    }
}
