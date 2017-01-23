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

public class FireBallsOnly extends LogicThread<AutonomousRobot> {
    @Override
    public void loadCommands (){

        LogicThread<AutonomousRobot> spinReaper = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new MoveMotor(robot.getFlywheel(), .45));
            }
        };
        List<LogicThread> threads = new ArrayList<LogicThread>();
        threads.add(spinReaper);


        commands.add(new SpawnNewThread(threads));
        commands.add(new Pause(500));
        commands.add(new Translate(1200, Translate.Direction.LEFT, 0));
        commands.add(new Pause(500));
        commands.add(new Rotate(0, 1, 3000));
        commands.add(new Pause(500));
       commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{.38})); //move button pusher
        commands.add(new MoveMotor(robot.getReaperMotor(), 1));
        commands.add(new Pause(1000));


    }
}
