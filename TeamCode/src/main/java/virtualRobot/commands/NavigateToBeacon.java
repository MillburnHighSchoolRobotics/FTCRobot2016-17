package virtualRobot.commands;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import virtualRobot.AutonomousRobot;
import virtualRobot.ExitCondition;
import virtualRobot.SallyJoeBot;

/**
 * Created by ethachu19 on 12/11/2016.
 */

public class NavigateToBeacon implements Command {
    VuforiaTrackable redTarget;
    VuforiaTrackable blueTarget;
    VuforiaTrackables allTrackables;
    OpenGLMatrix lastLocation = null;
    VuforiaLocalizer vuforia;
    AutonomousRobot robot = Command.AUTO_ROBOT;
    ExitCondition exitCondition = new ExitCondition() {
        @Override
        public boolean isConditionMet() {
            return false;
        }
    };

    public NavigateToBeacon(VuforiaLocalizer vuforia, String asset) {
        this.vuforia = vuforia;
        VuforiaTrackables trackables = this.vuforia.loadTrackablesFromAsset(asset);
        blueTarget = trackables.get(0);
        blueTarget.setName("Blue Target");
        redTarget = trackables.get(1);
        redTarget.setName("Red Target");
    }

    public NavigateToBeacon(VuforiaLocalizer vuforia) {
        this (vuforia, "Beacons");
    }

    public void setExitCondition(ExitCondition exitCondition) { this.exitCondition = exitCondition; }

    @Override
    public boolean changeRobotState(){
        OpenGLMatrix redTargetLocationOnField = OpenGLMatrix
                .translation(-SallyJoeBot.mmFTCFieldWidth / 2, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        redTarget.setLocation(redTargetLocationOnField);
        robot.addToProgress("Red Target: " + redTargetLocationOnField.formatAsTransform());

        OpenGLMatrix blueTargetLocationOnField = OpenGLMatrix
                .translation(0, SallyJoeBot.mmFTCFieldWidth / 2, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));
        blueTarget.setLocation(blueTargetLocationOnField);
        robot.addToProgress("Blue Target: " + blueTargetLocationOnField.formatAsTransform());

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(SallyJoeBot.mmBotWidth / 2, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));
        robot.addToProgress("Phone Location: " + phoneLocationOnRobot.formatAsTransform());

        ((VuforiaTrackableDefaultListener) redTarget.getListener()).setPhoneInformation(phoneLocationOnRobot, VuforiaLocalizer.CameraDirection.BACK);
        ((VuforiaTrackableDefaultListener) blueTarget.getListener()).setPhoneInformation(phoneLocationOnRobot, VuforiaLocalizer.CameraDirection.BACK);

        /** Start tracking the data sets we care about. */
        allTrackables.activate();

        boolean isInterrupted = false;

        while (!exitCondition.isConditionMet() && !isInterrupted) {
            for (VuforiaTrackable trackable : allTrackables) {
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
                if (robotLocationTransform != null) {
                    lastLocation = robotLocationTransform;
                }
            }
            if (lastLocation != null)
                robot.addToTelemetry("Current Location: ", lastLocation.formatAsTransform());
            else
                robot.addToTelemetry("Current Location: ", "Unknown");

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
        return isInterrupted;
    }
}
