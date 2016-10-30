package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;

/**
 * Created by shant on 1/9/2016.
 */
public class PushLeftButton extends LogicThread<AutonomousRobot> {
    public final static double BUTTON_PUSHER_LEFT = 0.25;
    @Override
    public void loadCommands() {

        robot.addToProgress("Pushed Left Button");
        commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_LEFT}));
        commands.add(new Pause(2000));
        commands.add(new Translate(200, Translate.Direction.RIGHT, 0));
        commands.add(new Pause(2000));
        commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY}));
    }
}
