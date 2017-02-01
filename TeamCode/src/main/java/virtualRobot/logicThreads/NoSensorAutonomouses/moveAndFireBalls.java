package virtualRobot.logicThreads.NoSensorAutonomouses;

import java.util.ArrayList;
import java.util.List;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.components.Motor;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 11/27/16.
 */

public class moveAndFireBalls extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){

        commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{0})); //move button pusher

        LogicThread<AutonomousRobot> spinFlywheel = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new MoveMotor(robot.getFlywheel(), .79));
            }
        };

        LogicThread<AutonomousRobot> forward = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new Translate(3150, Translate.Direction.LEFT, 0));
                commands.add(new Pause(300));
                commands.add(new Rotate(10, .5, 1000));
            }
        };

        List<LogicThread> threads = new ArrayList<LogicThread>();
        threads.add(forward);
        threads.add(spinFlywheel);


        commands.add(new SpawnNewThread(threads));
        commands.add(new Pause(3000));
        commands.add(new MoveMotor(robot.getReaperMotor(), .21));

        commands.add(new Pause(1000));

    }
}
