package virtualRobot.logicThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Translate;
import virtualRobot.components.Sensor;

/**
 * Created by 17osullivand on 10/6/16.
 */
public class BlueAutonomousLogic extends LogicThread<AutonomousRobot> {
    AtomicBoolean redIsLeft = new AtomicBoolean();
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
                if (robot.getLineSensor().getRawValue() > 10) {
                    return true;
                }
                return false;
            }
        };

        //Move to knock ball
        commands.add(new Translate(10, Translate.Direction.FORWARD,0,1));
        commands.add(new Translate(1,Translate.Direction.LEFT, 0,1));
        commands.add(new Translate(1, Translate.Direction.RIGHT, 0,1));


        //Strafe left to move towards wall
        Translate moveToWall = new Translate(1000, Translate.Direction.RIGHT, -10);
        moveToWall.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
//                if (robot.getUltrasonicSensor().getValue() < 10) {
//                    return true;
//                }
                return false;
            }
        });

        //Strafe to first white line
        Translate moveToWhiteLine = new Translate(1000, Translate.Direction.BACKWARD, 0);
        moveToWhiteLine.setExitCondition(atwhiteline);
        commands.add(moveToWhiteLine);

        FTCTakePicture pic = new FTCTakePicture(redIsLeft,this.vuforia);
        commands.add(pic);
    }
}
