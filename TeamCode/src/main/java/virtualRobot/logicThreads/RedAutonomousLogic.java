package virtualRobot.logicThreads;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.LogicThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.ColorSensor;
import virtualRobot.components.Sensor;
import virtualRobot.components.Servo;

/**
 * Created by DOSullivan on 9/14/2016.
 */
public class RedAutonomousLogic extends LogicThread<AutonomousRobot> {
    final int whiteTape = 20;

    @Override
    public void loadCommands() {
        final ColorSensor csensor = robot.getColorSensor();

        //Move to knock ball
        commands.add(new Translate(10, Translate.Direction.FORWARD_LEFT, 15));

        //Strafe to first white line
        Translate moveToWhiteLine = new Translate(1000, Translate.Direction.LEFT, 10, 10);
        moveToWhiteLine.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape) {
                    return true;
                }
                return false;
            }
        });
        commands.add(moveToWhiteLine);

        //code to follow line and push button

        Translate moveToSecondWLine = new Translate(1000, Translate.Direction.BACKWARD, 10, 10);
        moveToSecondWLine.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                if (robot.getColorSensor().getRed() >= whiteTape && robot.getColorSensor().getBlue() >= whiteTape && robot.getColorSensor().getGreen() >= whiteTape) {
                    return true;
                }
                return false;
            }
        });
        commands.add(moveToSecondWLine);

        //code to follow line and push button

        //Strafe to ramp
        commands.add(new Translate(10, Translate.Direction.BACKWARD, 0));

    }
}
