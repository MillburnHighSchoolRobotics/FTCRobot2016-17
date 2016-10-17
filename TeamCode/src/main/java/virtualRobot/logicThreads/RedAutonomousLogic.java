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
 * Created by DOSullivan on 9/14/2016.
 */
public class RedAutonomousLogic extends LogicThread<AutonomousRobot> {
    final int whiteTape = 20;
    AtomicBoolean redIsLeft = new AtomicBoolean();
    VuforiaLocalizerImplSubclass iloveinterfaces;

    public RedAutonomousLogic(AtomicBoolean redIsLeft,VuforiaLocalizerImplSubclass quallcom) {
        super();
        this.redIsLeft = redIsLeft;
        this.iloveinterfaces = quallcom;
    }
    @Override
    public void loadCommands() {
        final Sensor csensor = robot.getLineSensor();

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
        commands.add(new Translate(10, Translate.Direction.FORWARD_RIGHT, 5,5));

        //Strafe left to move towards wall
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

        FTCTakePicture pic = new FTCTakePicture(redIsLeft,iloveinterfaces);
        commands.add(pic);
    }
}
