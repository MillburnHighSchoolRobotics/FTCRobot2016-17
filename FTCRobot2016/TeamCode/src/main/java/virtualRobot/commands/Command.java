package virtualRobot.commands;

import virtualRobot.AutonomousRobot;
import virtualRobot.SallyJoeBot;
import virtualRobot.TeleopRobot;

/**
 * Created by shant on 10/8/2015.
 */
public interface Command  {
    /*
        changeRobotState should manipulate the AutonomousRobot through the LogicThread
     */
    boolean changeRobotState () throws InterruptedException;

    SallyJoeBot ROBOT = new SallyJoeBot();
    AutonomousRobot AUTO_ROBOT = ROBOT;
    TeleopRobot TELEOP_ROBOT = ROBOT;
}
