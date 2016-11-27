package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 11/27/16.
 */

public class FireBallsOnly extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){
        commands.add(new MoveServo(new Servo[]{robot.getBallLauncherServo()}, new double[]{1})); //move button pusher

    }
}
