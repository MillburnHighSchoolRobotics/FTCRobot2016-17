package virtualRobot.logicThreads.AutonomousLayer1;

import android.util.Log;

import org.firstinspires.ftc.teamcode.UpdateThread;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;

/**
 * Created by 17osullivand on 11/3/16.
 * Knocks ball, strafes to wall
 */

public class BlueGoToWall extends LogicThread<AutonomousRobot>  {
    public static final boolean WITH_SONAR = UpdateThread.WITH_SONAR; //Are we using a sonar?
    public static final double CLOSE_TO_WALL = 10; //How close we want to strafe to wall
    public static final double SONAR_ERROR_MAX = CLOSE_TO_WALL+2; //the threshold at which if a sonar is >= than this when at wall, it's wrong
    public static final double SONAR_ERROR_MIN = CLOSE_TO_WALL-2; //the threshold at which if a sonar is <= than this when at wall, it's wrong
    AtomicBoolean sonarWorks;

    public BlueGoToWall(AtomicBoolean sonarWorks) {
        super();
        this.sonarWorks = sonarWorks;
    }
    @Override
    public void loadCommands() {
        Translate escapeWall = new Translate(1500, Translate.Direction.FORWARD, 0);
        commands.add(escapeWall); //Move Away from wall
        commands.add(new Pause(500));
        commands.add(new Rotate(40, 1)); //Rotate In such a way to glance the ball
        commands.add(new Pause(500));
        commands.add(new Pause(500));
        commands.add(new Translate(10000, Translate.Direction.FORWARD, 0, 1, 40)); //Continue forward (relative to the angle we just rotated to)
        commands.add(new Pause(500));
        commands.add(new Rotate(0, 1)); //Straighten out (note that rotate takes in a target value, not a relative value). So this will return us to the angle we started our bot at.
        commands.add(new Pause(500));
        Translate strafeRight = new Translate(3200, Translate.Direction.RIGHT, 0, .3); //Strafe towards the wall. Stop at 3500 or when the sonar says, "hey you're too close guy"
        if (WITH_SONAR) {
            strafeRight.setExitCondition(new ExitCondition() {
                @Override
                public boolean isConditionMet() {
                    Log.d("UltraSOUND", "" + robot.getSonarLeft().getValue() + "" + robot.getSonarRight().getValue());
                    if (robot.getSonarRight().getValue() <= SONAR_ERROR_MIN || robot.getSonarLeft().getValue() <= SONAR_ERROR_MIN || robot.getSonarRight().getValue() >= SONAR_ERROR_MAX || robot.getSonarLeft().getValue() >= SONAR_ERROR_MAX) {
                        sonarWorks.set(false);

                    } else if (robot.getSonarRight().getValue() < CLOSE_TO_WALL || robot.getSonarLeft().getValue() < CLOSE_TO_WALL) {
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
        commands.add(new Pause(1000));
        commands.add(new Rotate(0, 1)); //Straighten out again
        commands.add(new Pause(1000));
    }

    }
