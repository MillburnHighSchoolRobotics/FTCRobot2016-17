package virtualRobot.logicThreads.AutonomousLayer1;

import android.util.Log;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.MoveMotor;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.SpawnNewThread;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 11/3/16.
 * Knocks ball, strafes to wall
 */

public class RedGoToWall extends LogicThread<AutonomousRobot>  {
    public static final boolean WITH_SONAR = UpdateThread.WITH_SONAR; //Are we using a sonar?
    public static final double CLOSE_TO_WALL = 17; //How close we want to strafe to wall
    public static final double SONAR_ERROR_MAX = CLOSE_TO_WALL+3; //the threshold at which if a sonar is >= than this when at wall, it's wrong
    public static final double SONAR_ERROR_MIN = CLOSE_TO_WALL-3; //the threshold at which if a sonar is <= than this when at wall, it's wrong
    protected static final double INT_ANGLE = -BlueGoToWall.INT_ANGLE;
  AtomicBoolean sonarWorks;

    public RedGoToWall(AtomicBoolean sonarWorks) {
        super();
        this.sonarWorks = sonarWorks;
    }
    @Override
    public void loadCommands() {
        commands.add(new Pause(500));
        Translate escapeWall = new Translate(500, Translate.Direction.BACKWARD_LEFT, 0); //
        commands.add(escapeWall); //Move Away from wall
        commands.add(new Pause(100));
        ///commands.add(new Rotate(INT_ANGLE, 1)); //Rotate In such a way to glance the ball
        commands.add(new Translate(6800, Translate.Direction.BACKWARD_RIGHT, 0));
        commands.add(new Pause(200));
        commands.add(new Translate(500, Translate.Direction.BACKWARD, 0)); //Continue Backward (relative to the angle we just rotated to)
        commands.add(new Pause(200));
        commands.add(new Rotate(0, .5, 1000)); //Straighten out (note that rotate takes in a target value, not a relative value). So this will return us to the angle we started our bot at.
        commands.add(new Pause(200));
        fireBalls();
        Translate strafeRight = new Translate(1950, Translate.Direction.RIGHT, 0, .3); //Strafe towards the wall. Stop at 2000 or when the sonar says, "hey you're too close guy"
        if (WITH_SONAR) {
            strafeRight.setExitCondition(new ExitCondition() {
                @Override
                public boolean isConditionMet() {
                    double sonarRight = robot.getSonarRight().getFilteredValue();
                    double sonarLeft = robot.getSonarLeft().getFilteredValue();
                    Log.d("UltraSOUND", "" + robot.getSonarLeft().getValue() + "" + robot.getSonarRight().getValue());
                    if (sonarRight <= SONAR_ERROR_MIN || sonarLeft <= SONAR_ERROR_MIN || sonarRight >= SONAR_ERROR_MAX || sonarLeft >= SONAR_ERROR_MAX) {
                        sonarWorks.set(false);

                    } else if (sonarRight < CLOSE_TO_WALL || sonarLeft < CLOSE_TO_WALL) {
                        sonarWorks.set(true);
                        robot.addToProgress("SONAR GOOD DATA");
                        return true;
                    } else {
                        sonarWorks.set(true);
                    }
                    return false;
                }
            });
        }
        commands.add(strafeRight);
        commands.add(new Pause(200));
        commands.add(new Rotate(0, .5, 700)); //Straighten out again
        commands.add(new Pause(200));
        robot.addToProgress("Went To Wall");
}
    private void fireBalls() {
        commands.add(new MoveServo(new Servo[]{robot.getFlywheelStopper()}, new double[]{0})); //move button pusher

        LogicThread<AutonomousRobot> spinFlywheel = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new MoveMotor(robot.getFlywheel(), .8));

            }
        };
        LogicThread<AutonomousRobot> moveReaper = new LogicThread<AutonomousRobot>() {
            @Override
            public void loadCommands() {
                commands.add(new MoveMotor(robot.getReaperMotor(), .21));

            }
        };


        List<LogicThread> threads = new ArrayList<LogicThread>();
        threads.add(spinFlywheel);

        SpawnNewThread fly = new SpawnNewThread((threads));

        commands.add(new SpawnNewThread(threads));
        commands.add(new Pause(3000));
        commands.add(new MoveMotor(robot.getReaperMotor(), .21));
        commands.add(new Pause(1000));
        killChildren();
    }
}
