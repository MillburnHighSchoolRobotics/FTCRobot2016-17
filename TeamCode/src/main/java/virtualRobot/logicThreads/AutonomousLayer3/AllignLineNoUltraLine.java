package virtualRobot.logicThreads.AutonomousLayer3;



import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.VuforiaLocalizerImplSubclass;
import virtualRobot.commands.FTCTakePicture;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 11/3/16.
 * Accounts for slight overshoot when going to line
 */
@Deprecated
public class AllignLineNoUltraLine extends LogicThread<AutonomousRobot>  {
    GodThread.Line type;
    double currentLine;
    VuforiaLocalizerImplSubclass vuforia;
    AtomicBoolean redIsLeft;
    public AllignLineNoUltraLine(GodThread.Line type, double currentLine, AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia) {
        this.type = type;
        this.currentLine = currentLine;
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }

    @Override
    public void loadCommands() {
        final ExitCondition atwhiteline = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (Math.abs(robot.getLightSensor1().getRawValue() - currentLine) > 1.85 || robot.getLightSensor1().getRawValue() > .73) {
                    return true;
                }
                return false;
            }
        };
        robot.addToProgress("Alligning with Line, with NO Ultra and Line");
        if (type == GodThread.Line.RED_FIRST_LINE || type == GodThread.Line.BLUE_SECOND_LINE) {

            Translate toWhiteLine2 =  new Translate(Translate.RunMode.CUSTOM, Translate.Direction.FORWARD, 0, .15);
            toWhiteLine2.setExitCondition(atwhiteline);
            commands.add(toWhiteLine2);
            commands.add(new Pause(500));
            commands.add(new Rotate(0, 1));
            commands.add(new Pause(500));
            FTCTakePicture pic = new FTCTakePicture(redIsLeft,vuforia); //Take a picture of beacon
            commands.add(pic);
            commands.add(new Pause(500));

        }
        else if (type == GodThread.Line.RED_SECOND_LINE || type == GodThread.Line.BLUE_FIRST_LINE) {
            Translate toWhiteLine2 =  new Translate(Translate.RunMode.CUSTOM, Translate.Direction.BACKWARD, 0, .15);
            toWhiteLine2.setExitCondition(atwhiteline);
            commands.add(toWhiteLine2);
            commands.add(new Pause(500));
            commands.add(new Rotate(0, 1));
            commands.add(new Pause(500));
            FTCTakePicture pic = new FTCTakePicture(redIsLeft,vuforia); //Take a picture of beacon
            commands.add(pic);
            commands.add(new Pause(500));

        }

    }


}
