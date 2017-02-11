package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.MoveMotorPID;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;

/**
 * Created by Yanjun on 11/28/2015.
 * used for tuning PID
 */
public class PIDTester extends LogicThread<AutonomousRobot> {
    public static boolean forward = true;
    @Override
    public void loadCommands() {
       // Translate.setGlobalMaxPower(1.0);
        //commands.add(new Translate(5000, Translate.Direction.FORWARD, 0));
        /*commands.add(new Pause(2000));
        commands.add(new Translate(3000, Translate.Direction.FORWARD, 0));
        commands.add(new Pause(2000));
        commands.add(new Translate(500, Translate.Direction.FORWARD, 0));*/

        //commands.add(new Translate(3000, Translate.Direction.RIGHT, 0));
        //commands.add(new Pause(3000));
        //commands.add(new Translate(7000, Translate.Direction.FORWARD, 0));
        //commands.add(new Translate(7000, Translate.Direction.FORWARD, 0));
        //commands.add(new Pause(3000));
        //commands.add(new Translate(3000, Translate.Direction.BACKWARD, 0));
        //commands.add(new Pause(3000));
        //commands.add(new Translate(5000, forward ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD, 0));
//        commands.add(new Pause(3000));
//        commands.add(new Translate(7000, Translate.Direction.LEFT, 0));
        //commands.add(new Rotate(90));

        //HIGH: .008125; LOW: .007
        //LOW: .003; HIGH: .0035
       // Translate c = new Translate(.00325,5000,-1,new AtomicBoolean(), Translate.Direction.FORWARD);
       // commands.add(c);
        //Command.AUTO_ROBOT.addToProgress("Translate KP:" + c.translateController.getKp());
//        commands.add(new Rotate(90, 1));

        //commands.add(new MoveMotorPID(50,robot.getFlywheel(),robot.getFlywheelEncoder()));
        Rotate.setDefaultMode(Rotate.RunMode.WITH_ENCODER);
        commands.add(new Rotate(90));
//        commands.add(new Command() {
//
//            @Override
//            public boolean changeRobotState() throws InterruptedException {
//                Log.d("PIDOUTPUTTICKS","" + robot.getLFMotor().getMotorType());
//                return false;
//            }
//        });

    }
}
