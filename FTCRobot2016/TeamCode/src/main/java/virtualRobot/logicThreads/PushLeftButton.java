package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveLift;
import virtualRobot.commands.MoveServo;
import virtualRobot.components.Servo;

/**
 * Created by shant on 1/9/2016.
 */
public class PushLeftButton extends LogicThread<AutonomousRobot> {
    final double BUTTON_PUSHER_LEFT = 0.45;
    @Override
    public void loadCommands() {

        robot.addToProgress("Pushed Left Button");
        commands.add(new MoveServo(new Servo[]{robot.getFlipperLeftServo()}, new double[]{0}));
        commands.add(new MoveLift(MoveLift.RunMode.TO_VALUE, MoveLift.Direction.OUT, 3400));
        //commands.add(new Pause(500));
        //commands.add(new MoveLift(MoveLift.RunMode.TO_VALUE, MoveLift.Direction.IN, 0));
    }
}
