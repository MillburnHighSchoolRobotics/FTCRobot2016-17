package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;

/**
 * Created by shant on 1/9/2016.
 * Pushes the right button
 */
public class PushRightButton extends LogicThread<AutonomousRobot> {
    public final static double BUTTON_PUSHER_RIGHT = 0.0;
    public void loadCommands () {
        robot.addToProgress("Pushed Right Button");
        commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_RIGHT})); //move button pusher
        commands.add(new Pause(2000));
        commands.add(new Translate(200, Translate.Direction.RIGHT, 0)); //ram beacon to ensure push
        commands.add(new Pause(2000));
        commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //move pusher back to stationary

    }
}
