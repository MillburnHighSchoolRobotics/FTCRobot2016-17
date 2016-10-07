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
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

import java.util.concurrent.atomic.AtomicBoolean;

import virtualRobot.ExitCondition;

public class FTCTakePicture implements Command{
    private ExitCondition exitCondition;
    AtomicBoolean redisLeft;
    
    public FTCTakePicture (AtomicBoolean redisLeft) {
        this.redisLeft = redisLeft;
        exitCondition = new ExitCondition() {
            @Override
            public boolean isConditionMet() {
                return false;
            }
        };
    }

    public boolean changeRobotState() throws InterruptedException {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters();
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AcXbD9X/////AAAAGVpq1gdfDkIPp+j5hv1iV5RZXLWAWV4F7je9gks+8lHhZb6mwCj7xy9mapHP6sKO9OrPv5kVQDXhB+T+Rn7V7GUm4Ub4rmCanqv4frx8gT732qJUnTEj9POMufR9skjlXSEODbpThxrLCPqobHeAeSA5dUmUik3Rck0lcwhElw5yOBN45iklYnvC9GpPRv128ALcgt9Zpw/shit0erKmuyrT62NRUKgoHNMm5xV/Xqj8Vgwke8ESap+nK7v+6lx35vDZ6ISNDVMMM8h0VqeL0745MNPJoI1vgiNRo30R7WwtPYME44koOrWMUIxMXghtqxq7AfFxb6sbin0i5KSUJWtLsqmZOrAXxjxdUwY8f8tw";
        VuforiaLocalizerImplSubclass vuforia = new VuforiaLocalizerImplSubclass(params);

        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS,4);

        if (vuforia.rgb != null){
           Bitmap bm =  Bitmap.createBitmap(vuforia.rgb.getWidth(), vuforia.rgb.getHeight(), Bitmap.Config.RGB_565);
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            boolean analyzed = DavidClass.analyzePic2(bm);
            Log.d("cameraReturn", analyzed + " ");
            redisLeft.set(analyzed);
        }


        return false;
    }

    public void setExitCondition (ExitCondition e) {
        exitCondition = e;
    }


    public class VuforiaLocalizerImplSubclass extends VuforiaLocalizerImpl {
    	
        public Image rgb;

    	class CloseableFrame extends Frame {
            	public CloseableFrame(Frame other) { // clone the frame so we can be useful beyond callback
                		super(other);
                	}	
            	public void close() {
                		super.delete();
                	}
            }


            public class VuforiaCallbackSubclass extends VuforiaLocalizerImpl.VuforiaCallback {

                @Override public synchronized void Vuforia_onUpdate(State state) {
                    super.Vuforia_onUpdate(state);
                    // We wish to accomplish two things: (a) get a clone of the Frame so we can use
                    // it beyond the callback, and (b) get a variant that will allow us to proactively
                    // reduce memory pressure rather than relying on the garbage collector (which here
                    // has been observed to interact poorly with the image data which is allocated on a
                    // non-garbage-collected heap). Note that both of this concerns are independent of
                    // how the Frame is obtained in the first place.
                    CloseableFrame frame = new CloseableFrame(state.getFrame());
                    RobotLog.vv(TAG, "received Vuforia frame#=%d", frame.getIndex());

                    //Important Stuff:
                    long num = frame.getNumImages();

                    for(int i = 0; i < num; i++){
                        if(frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565){
                            rgb = frame.getImage(i);
                        }
                    }
                    //End important stuff.

                    frame.close();
                    }
                }

            public VuforiaLocalizerImplSubclass(VuforiaLocalizer.Parameters parameters) {
                super(parameters);
                stopAR();
                clearGlSurface();

                this.vuforiaCallback = new VuforiaCallbackSubclass();
                startAR();

                // Optional: set the pixel format(s) that you want to have in the callback
                Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
            }

            public void clearGlSurface() {
                if (this.glSurfaceParent != null) {
                    appUtil.synchronousRunOnUiThread(new Runnable() {
                        @Override public void run() {
                            glSurfaceParent.removeAllViews();
                            glSurfaceParent.getOverlay().clear();
                            glSurface = null;
                        }
                    });
                }
            }
        }
}