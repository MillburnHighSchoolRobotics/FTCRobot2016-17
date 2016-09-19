package virtualRobot.commands;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

import java.io.IOException;

/**
 * Created by shant on 1/29/2016.
 */
public class PlayMusic implements Command {
    private String fileName;

    public PlayMusic (String fileName) {
        this.fileName = fileName;
    }
    @Override
    public boolean changeRobotState() throws InterruptedException {
        final MediaPlayer mp = new MediaPlayer();

        if (mp.isPlaying()) {
            mp.stop();
        }



        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = FtcRobotControllerActivity.context.getAssets().openFd(fileName);
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
