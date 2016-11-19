package virtualRobot.commands;

import android.graphics.Bitmap;
import android.util.Log;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Frame;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;
import org.firstinspires.ftc.teamcode.R;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.ExitCondition;
import virtualRobot.VuforiaLocalizerImplSubclass;
/**
 * Takes a Picture Using Vuforia
 */
public class FTCTakePicture implements Command{
    private ExitCondition exitCondition;
    AtomicBoolean redisLeft;
    AtomicBoolean isRed;
    VuforiaLocalizerImplSubclass vuforia;
    Mode mode;
    public FTCTakePicture (Mode mode, AtomicBoolean red, VuforiaLocalizerImplSubclass vuforia) {
        this.mode=mode;
        if (mode==Mode.TAKING_PICTURE)
        this.redisLeft = red;
        else
        this.isRed = red;
        this.vuforia = vuforia;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
    }

    public boolean changeRobotState() throws InterruptedException {

        //Converts VuforiaLocalizerImplSubclass' picture to a bitmap for analysis by DavidClass
        if (vuforia.rgb != null){
            Bitmap bm =  Bitmap.createBitmap(vuforia.rgb.getWidth(), vuforia.rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            if (mode==Mode.TAKING_PICTURE) {
                boolean analyzed = DavidClass.analyzePic2(bm);
                Log.d("cameraReturn ", analyzed + " ");
                redisLeft.set(analyzed);
            }
            else {
                boolean analyzed = DavidClass.checkIfAllRed(bm);
                Log.d("cameraReturn ", analyzed + " ");
                redisLeft.set(analyzed);
            }
       }

        return Thread.currentThread().isInterrupted();
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

    public enum Mode{
       TAKING_PICTURE,
        CHECKING_PICTURE;
    }
}