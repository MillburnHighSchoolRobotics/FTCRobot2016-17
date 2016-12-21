package virtualRobot.godThreads;

import virtualRobot.GodThread;
import virtualRobot.SallyJoeBot;
import virtualRobot.commands.Command;

/**
 * Created by ethachu19 on 12/21/2016.
 */

public class FPSGodThread extends GodThread {
    SallyJoeBot robot = Command.ROBOT;
    @Override
    public void realRun() throws InterruptedException {
        boolean isInterrupted = false;
        while (!isInterrupted) {
            robot.addToTelemetry("Position: ",robot.getStateSensor().getPosition().toString());
            robot.addToTelemetry("Velocity: ",robot.getStateSensor().getVelocity().toString());

            if (Thread.currentThread().isInterrupted()) {
                isInterrupted = true;
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                isInterrupted = true;
                break;
            }
        }
    }
}
