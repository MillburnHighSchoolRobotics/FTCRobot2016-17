package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.CompensateColor;
import virtualRobot.commands.MoveMotorPID;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

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
//        commands.add(new Rotate(90));

        //HIGH: .008125; LOW: .007
        //LOW: .003; HIGH: .0035
       // Translate c = new Translate(.00325,5000,-1,new AtomicBoolean(), Translate.Direction.FORWARD);
       // commands.add(c);
        //Command.AUTO_ROBOT.addToProgress("Translate KP:" + c.translateController.getKp());
//        commands.add(new Rotate(90, 1));

        //commands.add(new MoveMotorPID(50,robot.getFlywheel(),robot.getFlywheelEncoder()));
//        Rotate.setDefaultMode(Rotate.RunMode.WITH_ENCODER);
//        commands.add(new Rotate(55,.5,1000));
//        Rotate.setDefaultMode(Rotate.RunMode.WALL_ALIGN);
//        commands.add(new Pause(1000));
//        commands.add(new Rotate(0,0.5));
//        commands.add(new Pause(1000));
//        commands.add(new Rotate(0,0.5));

//        commands.add(new Command() {
//
//            @Override
//            public boolean changeRobotState() throws InterruptedException {
//                Log.d("PIDOUTPUTTICKS","" + robot.getLFMotor().getMotorType());
//                return false;
//            }
//        });
       // commands.add(new WallTrace(WallTrace.Direction.FORWARD));

        commands.add(new WallTrace(WallTrace.Direction.FORWARD,13,0.2,0.04,0.015));
//       commands.add(new CompensateColor());
//        commands.add(new Rotate(0.037,90,40000,new AtomicBoolean(false)));
    }
}
