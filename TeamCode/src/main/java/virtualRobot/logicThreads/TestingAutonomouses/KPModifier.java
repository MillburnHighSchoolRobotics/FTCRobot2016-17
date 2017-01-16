package virtualRobot.logicThreads.TestingAutonomouses;

import android.util.Log;

import virtualRobot.JoystickController;
import virtualRobot.LogicThread;
import virtualRobot.TeleopRobot;
import virtualRobot.commands.Command;

/**
 * Created by ethachu19 on 1/15/2017.
 */

public class KPModifier extends LogicThread<TeleopRobot> {
    public static double kP = 0.01, increment = 0.01;
    JoystickController controller;
    public KPModifier() {
        controller = robot.getJoystickController1();
    }

    @Override
    public void loadCommands() {
        commands.add(new Command() {
            @Override
            public boolean changeRobotState() throws InterruptedException {
                boolean isInterrupted = false;
                while (controller.isPressed(JoystickController.BUTTON_A)) {
                    controller.logicalRefresh();
                    if(controller.isPressed(JoystickController.BUTTON_LB)) {
                        kP -= increment;
                    }
                    if(controller.isPressed(JoystickController.BUTTON_RB)) {
                        kP += increment;
                    }
                    if(controller.isPressed(JoystickController.BUTTON_RT)) {
                        increment /= 10;
                    }
                    if(controller.isPressed(JoystickController.BUTTON_LT)) {
                        increment *= 10;
                    }
                    robot.addToTelemetry("KP: ", kP + " Increment: " + increment);
                    if (Thread.currentThread().isInterrupted()){
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
                Log.d("AutoPID","KP: " + kP);
                return isInterrupted;
            }
        });
    }
}
