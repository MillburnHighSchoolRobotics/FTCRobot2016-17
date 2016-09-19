package virtualRobot;

/**
 * Created by Yanjun on 11/18/2015.
 */
public interface TeleopRobot extends AutonomousRobot {

    JoystickController getJoystickController1();

    JoystickController getJoystickController2();

}
