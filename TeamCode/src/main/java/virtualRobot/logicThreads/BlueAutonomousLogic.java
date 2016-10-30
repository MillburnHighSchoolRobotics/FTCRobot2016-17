package virtualRobot.logicThreads;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;
import virtualRobot.components.Sensor;

/**
 * Created by 17osullivand on 10/6/16.
 * Go Hillary.
 */
public class BlueAutonomousLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft;
    VuforiaLocalizerImplSubclass vuforia;
    public static double Line = 4;
    public BlueAutonomousLogic(AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia) {
        super();
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }
    @Override
    public void loadCommands() {
        final double currentLine = robot.getLineSensor().getRawValue();
        Line = currentLine;
        final ExitCondition atwhiteline = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (Math.abs(robot.getLineSensor().getRawValue() - currentLine) > .7) {
                    return true;
                }
                return false;
            }
        };

        Translate escapeWall = new Translate(1200, Translate.Direction.FORWARD, 0);
        commands.add(escapeWall);
        commands.add(new Pause(2000));
        commands.add(new Rotate(36.5, 1));
        commands.add(new Pause(2000));
        commands.add(new Translate(10000, Translate.Direction.FORWARD, 0, 1, 36.5));
        commands.add(new Pause(2000));
        commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));
        Translate strafeRight = new Translate(3500, Translate.Direction.RIGHT, 0);
        strafeRight.setExitCondition( new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                Log.d("UltraSOUND", "" + robot.getSonarLeft().getValue() + "" + robot.getSonarRight().getValue());

                if (robot.getSonarRight().getValue() < 10 ) {
                    return true;
                }
                return false;
            }
        });
        commands.add(strafeRight);
        commands.add(new Pause(2000));
        commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));
        WallTrace toWhiteLine =  new WallTrace(WallTrace.Direction.FORWARD, 8);
        toWhiteLine.setExitCondition(atwhiteline);
        commands.add(toWhiteLine);
        commands.add(new Pause(2000));
        WallTrace toWhiteLine2 =  new WallTrace(WallTrace.Direction.BACKWARD, 8);
        toWhiteLine2.setExitCondition(atwhiteline);
        commands.add(toWhiteLine2);
        robot.addToProgress("Went to Line");
        /*commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));
        Translate moveToWall2 =  new Translate(Translate.RunMode.CUSTOM, Translate.Direction.RIGHT, 0, .2);
        moveToWall2.setExitCondition(
                new ExitCondition() {
                    @Override
                    public boolean isConditionMet() {
                        Log.d("UltraSOUND", "" + robot.getSonarLeft().getValue() + "" + robot.getSonarRight().getValue());

                        if (robot.getSonarRight().getValue() < 9  ) {
                            return true;
                        }
                        return false;
                    }
                });
        commands.add(new Pause(2000));
        commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));*/
        FTCTakePicture pic = new FTCTakePicture(redIsLeft,vuforia);
        commands.add(pic);
        //Strafe left to move towards wall
        /*
        Translate moveToWall = new Translate(1000, Translate.Direction.RIGHT, -10);
        moveToWall.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
               if (robot.getUltrasonicSensor().getValue() < 10) {
                    return true;
                }
                return false;
            }
        });

        //Strafe to first white line
        Translate moveToWhiteLine = new Translate(1000, Translate.Direction.BACKWARD, 0);
        moveToWhiteLine.setExitCondition(atwhiteline);
        commands.add(moveToWhiteLine);*/
    }
}
