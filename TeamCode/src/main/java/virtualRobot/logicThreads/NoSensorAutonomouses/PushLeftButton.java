package virtualRobot.logicThreads.NoSensorAutonomouses;

import virtualRobot.AutonomousRobot;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;
import virtualRobot.logicThreads.TeleopLogic;

/**
 * Created by shant on 1/9/2016.
 * Pushes the left button
 */
public class PushLeftButton extends LogicThread<AutonomousRobot> {
    public final static double BUTTON_PUSHER_LEFT = 0.25;
    public final static double BEACON_RAM_TRANSLATE = 600; //translate to get the robot to hit button
    sonarStatus status;
    public PushLeftButton(sonarStatus status) {
        this.status = status;
    }
    public PushLeftButton(boolean sonarWorks) {
        if (sonarWorks)
            this.status = sonarStatus.SONAR_WORKS;
        else
            this.status = sonarStatus.SONAR_BROKEN;
    }
    @Override
    public void loadCommands() {
        if (status == sonarStatus.SONAR_WORKS) {
            robot.addToProgress("Pushed Left Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_LEFT})); //Move buttonpusher
            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE, Translate.Direction.RIGHT, 0)); //ram beacon to ensure pushed button
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //set button back to stationary
            commands.add(new Pause(500));
        }
        else {
            robot.addToProgress("Pushed Left Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_LEFT})); //Move buttonpusher
            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE, Translate.Direction.RIGHT, 0)); //ram beacon to ensure pushed button
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //set button back to stationary
            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE, Translate.Direction.LEFT, 0)); //ram beacon to ensure pushed button
            commands.add(new Pause(500));
        }

    }
    public enum sonarStatus {
        SONAR_WORKS,
        SONAR_BROKEN
    }
}
