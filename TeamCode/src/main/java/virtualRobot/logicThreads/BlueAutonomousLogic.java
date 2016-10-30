package virtualRobot.logicThreads;

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
 * Created by 17osullivand on 10/6/16.
 * Go Hillary.
 */
public class BlueAutonomousLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft;
    VuforiaLocalizerImplSubclass vuforia;

    public BlueAutonomousLogic(AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia) {
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

        //Move to knock ball

        commands.add(new Pause(2000));
        commands.add(new Translate(10000, Translate.Direction.BACKWARD, 0));
        commands.add(new Pause(2000));
        Translate toWhiteLine =  new Translate(4000, Translate.Direction.BACKWARD_LEFT, 0, .5);
        toWhiteLine.setExitCondition(atwhiteline);
        commands.add(toWhiteLine);
        commands.add(new Pause(2000));
        Translate moveToWall = new Translate(10000, Translate.Direction.LEFT, 0);
        moveToWall.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (robot.getSonarLeft().getValue() < 12) {
                    return true;
                }
                return false;
            }
        });
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
        commands.add(moveToWhiteLine);*/
    }
}
