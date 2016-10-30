package virtualRobot;

/**
 * Created by Yanjun on 11/18/2015.
 * This interface represents the robot when used in Teleop
 */
public interface TeleopRobot extends AutonomousRobot {

    JoystickController getJoystickController1();

    JoystickController getJoystickController2();

}
