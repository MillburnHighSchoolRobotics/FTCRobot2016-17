package virtualRobot.logicThreads.NoSensorAutonomouses;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.AutonomousRobot;
import virtualRobot.GodThread;
import virtualRobot.LogicThread;
import virtualRobot.commands.MoveServo;
import virtualRobot.commands.Pause;
import virtualRobot.commands.Rotate;
import virtualRobot.commands.Translate;
import virtualRobot.components.Servo;
import virtualRobot.logicThreads.TeleopLogic;

/**
 * Created by shant on 1/9/2016.
 * Pushes the left button
 */
public class PushLeftButton extends LogicThread<AutonomousRobot> {
    public final static double BUTTON_PUSHER_LEFT = 0.6;
    public final static double BEACON_RAM_TRANSLATE = PushRightButton.BEACON_RAM_TRANSLATE; //translate to get the robot to hit button
    sonarStatus status;
    GodThread.Line type;
    AtomicBoolean allSensorsFail;
    AtomicBoolean colorTriggered;
    public PushLeftButton(sonarStatus status) {
        this.status = status;
    }
    public PushLeftButton(boolean sonarWorks, GodThread.Line type, AtomicBoolean allSensorsFail, AtomicBoolean colorTriggered) {
        if (sonarWorks)
            this.status = sonarStatus.SONAR_WORKS;
        else
            this.status = sonarStatus.SONAR_BROKEN;
        this.type = type;
        this.allSensorsFail = allSensorsFail;
        this.colorTriggered = colorTriggered;
    }
    @Override
    public void loadCommands() {
       // commands.add(new Rotate(90,0.5,1500));
        commands.add(new Pause(500));
        if (type == GodThread.Line.RED_FIRST_LINE) {
            commands.add(new Translate(50, colorTriggered.get() ? Translate.Direction.FORWARD : Translate.Direction.BACKWARD,0).setTolerance(25));
        }
        if (type == GodThread.Line.RED_SECOND_LINE && !allSensorsFail.get()) {
            commands.add(new Translate(75, colorTriggered.get() ? Translate.Direction.BACKWARD : Translate.Direction.FORWARD,0).setTolerance(25));
        }
        if (type == GodThread.Line.BLUE_FIRST_LINE && !allSensorsFail.get()) {
            commands.add(new Translate(75, Translate.Direction.BACKWARD,0).setTolerance(25));
        }
        if (type == GodThread.Line.BLUE_SECOND_LINE && !allSensorsFail.get()) {
            commands.add(new Translate(26, Translate.Direction.FORWARD,0).setTolerance(25));
        }
        commands.add(new Pause(250));
        if (status == sonarStatus.SONAR_WORKS) {
            robot.addToProgress("Pushed Left Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_LEFT})); //Move buttonpusher
            commands.add(new Pause(250));
            commands.add(new Translate(BEACON_RAM_TRANSLATE+500, Translate.Direction.RIGHT, 0, .5)); //ram beacon to ensure pushed button
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //set button back to stationary
//            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE-300, Translate.Direction.LEFT, 0)); //ram beacon to ensure push

        }
        else {
            robot.addToProgress("Pushed Left Button");
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{BUTTON_PUSHER_LEFT})); //Move buttonpusher
            commands.add(new Pause(250));
            commands.add(new Translate(BEACON_RAM_TRANSLATE+500, Translate.Direction.RIGHT, 0, .5)); //ram beacon to ensure pushed button
            commands.add(new Pause(500));
            commands.add(new MoveServo(new Servo[]{robot.getButtonServo()}, new double[]{TeleopLogic.BUTTON_PUSHER_STATIONARY})); //set button back to stationary
//            commands.add(new Pause(500));
            commands.add(new Translate(BEACON_RAM_TRANSLATE-300, Translate.Direction.LEFT, 0)); //ram beacon to ensure pushed button
        }
        commands.add(new Rotate(90,0.5,500));
        commands.add(new Pause(400));
        commands.add(new Translate(350, Translate.Direction.LEFT, 0, .5).setTolerance(25));
        commands.add(new Pause(200));

    }
    public enum sonarStatus {
        SONAR_WORKS,
        SONAR_BROKEN
    }
}
