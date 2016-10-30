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
    VuforiaLocalizerImplSubclass vuforia;

    public FTCTakePicture (AtomicBoolean redisLeft, VuforiaLocalizerImplSubclass vuforia) {
        this.redisLeft = redisLeft;
        this.vuforia = vuforia;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
    }

    public boolean changeRobotState() throws InterruptedException {

        if (vuforia.rgb != null){
            Bitmap bm =  Bitmap.createBitmap(vuforia.rgb.getWidth(), vuforia.rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            boolean analyzed = DavidClass.analyzePic2(bm);
            Log.d("cameraReturn ", analyzed + " ");
            redisLeft.set(analyzed);

       }

        return Thread.currentThread().isInterrupted();
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }

}