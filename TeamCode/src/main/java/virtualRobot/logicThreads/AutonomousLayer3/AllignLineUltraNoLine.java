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
import virtualRobot.commands.WallTrace;

/**
 * Created by 17osullivand on 11/3/16.
 * Accounts for slight overshoot when going to line
 */

public class AllignLineUltraNoLine extends LogicThread<AutonomousRobot>  {
    public static final double CORRECTION_VALUE = 400; //since we've very much overshot the line, we need to go back;
    GodThread.Line type;
    VuforiaLocalizerImplSubclass vuforia;
    AtomicBoolean redIsLeft;
    public AllignLineUltraNoLine(GodThread.Line type, AtomicBoolean redIsLeft, VuforiaLocalizerImplSubclass vuforia) {
        this.type = type;
        this.redIsLeft = redIsLeft;
        this.vuforia = vuforia;
    }

    @Override
    public void loadCommands() {

        if (type== GodThread.Line.RED_FIRST_LINE || type== GodThread.Line.BLUE_SECOND_LINE) {
            WallTrace toWhiteLine2 =  new WallTrace(WallTrace.Direction.FORWARD, 8, CORRECTION_VALUE);
            commands.add(toWhiteLine2);
            commands.add(new Pause(500));
            commands.add(new Rotate(0, 1));
            commands.add(new Pause(500));
            FTCTakePicture pic = new FTCTakePicture(redIsLeft,vuforia); //Take a picture of beacon
            commands.add(pic);
            commands.add(new Pause(500));

        }
        else if (type== GodThread.Line.RED_SECOND_LINE || type== GodThread.Line.BLUE_FIRST_LINE) {
            WallTrace toWhiteLine2 =  new WallTrace(WallTrace.Direction.BACKWARD, 8, CORRECTION_VALUE);
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
