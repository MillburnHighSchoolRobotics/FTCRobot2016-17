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
        commands.add(new Translate(10, Translate.Direction.FORWARD_LEFT, 15));

        //Strafe to first white line
        Translate moveToWhiteLine = new Translate(1000, Translate.Direction.LEFT, 10, 10);
        moveToWhiteLine.setExitCondition(atwhiteline);
        commands.add(moveToWhiteLine);

        //Insert placeholder code to align with wall

        //Strafe left to move towards wall
        Translate moveToWall = new Translate(1000, Translate.Direction.LEFT, 10, 0);
        moveToWall.setExitCondition(new ExitCondition() {
            @Override
            public boolean isConditionMet() {
               if (robot.getUltrasonicSensor().getValue() < 10) {
                    return true;
                }
                return false;
            }
        });

        //get picture and press button

        //Reverse to second white line
        Translate moveToSecondWLine = new Translate(1000, Translate.Direction.BACKWARD, 10, 10);
        moveToSecondWLine.setExitCondition(atwhiteline);
        commands.add(moveToSecondWLine);

        //get picture and press button

        //Strafe to ramp
        commands.add(new Translate(10, Translate.Direction.BACKWARD, 0));

    }
}
