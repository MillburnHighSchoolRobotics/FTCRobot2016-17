package virtualRobot.logicThreads.NoSensorAutonomouses;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.MoveMotorPID;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.commands.killChildren;
import virtualRobot.components.Motor;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 11/27/16.
 */

public class moveAndFireBalls extends LogicThread<AutonomousRobot> {
    private AtomicBoolean reapStart;


    LogicThread<AutonomousRobot> forward = new LogicThread<AutonomousRobot>() {
        @Override
        public void loadCommands() {
            commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{0})); //move button pusher

            commands.add(new Translate(2000, Translate.Direction.LEFT, 0));
            commands.add(new Pause(500));
            commands.add(new Rotate(0, .5, 500));
        }
    };
    LogicThread<AutonomousRobot> spinFlywheel = new LogicThread<AutonomousRobot>() {
        @Override
        public void loadCommands() {
            commands.add(new MoveMotorPID(87,robot.getFlywheel(),robot.getFlywheelEncoder()));
            commands.add(new Pause(1000));

        }
    };
    LogicThread<AutonomousRobot> moveReaper = new LogicThread<AutonomousRobot>() {
        @Override
        public void loadCommands() {
            commands.add(new Pause(2000));
            commands.add(new MoveMotor(robot.getReaperMotor(), .21));

        }
    };



    @Override
    public void loadCommands (){

        List<LogicThread> threads = new ArrayList<LogicThread>();
        threads.add(forward);
        threads.add(spinFlywheel);
        threads.add(moveReaper);

        SpawnNewThread fly = new SpawnNewThread((threads));

        commands.add(fly);
        commands.add(new Pause(5000));
        commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{.6})); //move button pusher

        commands.add(new killChildren(this));

    }
}
