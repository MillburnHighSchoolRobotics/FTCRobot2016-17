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
import virtualRobot.components.Sensor;

/**
 * Created by DOSullivan on 9/14/2016.
 * Go Trump.
 */
public class RedAutonomousLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft = new AtomicBoolean();
    VuforiaLocalizerImplSubclass vuforia;

    public RedAutonomousLogic(AtomicBoolean redIsLeft,VuforiaLocalizerImplSubclass vuforia) {
        super();
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }
    @Override
    public void loadCommands() {

        final ExitCondition atwhiteline = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (robot.getLineSensor().getRawValue() < 3) {
                    return true;
                }
                return false;
            }
        };


        Translate escapeWall = new Translate(1200, Translate.Direction.BACKWARD, 0);
        commands.add(escapeWall);
        commands.add(new Pause(2000));
        commands.add(new Rotate(-36.5, 1));
        commands.add(new Pause(2000));
        commands.add(new Translate(10000, Translate.Direction.BACKWARD, 0, 1, -36.5));
        commands.add(new Pause(2000));
        commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));
        Translate moveToWall =  new Translate(Translate.RunMode.CUSTOM, Translate.Direction.RIGHT, 0, .3);
        moveToWall.setExitCondition(
                new ExitCondition() {
                    @Override
                    public boolean isConditionMet() {
                        Log.d("UltraSOUND", "" + robot.getSonarLeft().getValue() + "" + robot.getSonarRight().getValue());

                        if (robot.getSonarRight().getValue() < 9 ) {
                            return true;
                        }
                        return false;
                    }
                });
        commands.add(moveToWall);
        commands.add(new Pause(2000));
        commands.add(new Rotate(0, 1));
        commands.add(new Pause(2000));
        Translate toWhiteLine =  new Translate(Translate.RunMode.HEADING_ONLY, Translate.Direction.BACKWARD, 0, .1);
        toWhiteLine.setExitCondition(atwhiteline);
        commands.add(toWhiteLine);
        commands.add(new Pause(2000));

        commands.add(moveToWall);
        commands.add(new Pause(2000));
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
        commands.add(moveToWhiteLine);
*/
    }
}
