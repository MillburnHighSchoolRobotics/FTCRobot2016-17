package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;
import virtualRobot.logicThreads.TeleopLogic;

/**
 * Created by shant on 1/9/2016.
 * Pushes the right button
 */
public class PushRightButton extends LogicThread<AutonomousRobot> {
    public final static double BUTTON_PUSHER_RIGHT = 0.0;
    public final static double BEACON_RAM_TRANSLATE = 750; //translate to get the robot to hit button
    sonarStatus status;
    public PushRightButton(sonarStatus status) {
        this.status = status;
    }
    public PushRightButton(boolean sonarWorks) {
        if (sonarWorks)
            this.status = sonarStatus.SONAR_WORKS;
        else
            this.status = sonarStatus.SONAR_BROKEN;
    }
    public void loadCommands () {
        commands.add(new Rotate(0,0.5,2000));
        if (status == sonarStatus.SONAR_BROKEN) {
            robot.addToProgress("Pushed Right Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_RIGHT})); //move button pusher
            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE+500, Translate.Direction.RIGHT, 0)); //ram beacon to ensure push
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //move pusher back to stationary
            commands.add(new Translate(BEACON_RAM_TRANSLATE+300, Translate.Direction.LEFT, 0)); //ram beacon to ensure push

        }
        else {
            robot.addToProgress("Pushed Right Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_RIGHT})); //move button pusher
            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE, Translate.Direction.RIGHT, 0)); //ram beacon to ensure push
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //move pusher back to stationary
            commands.add(new Translate(BEACON_RAM_TRANSLATE-200, Translate.Direction.LEFT, 0)); //ram beacon to ensure push
        }
    }
    public enum sonarStatus {
        SONAR_WORKS,
        SONAR_BROKEN
    }
}
