package virtualRobot.godThreads;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import virtualRobot.GodThread;
import virtualRobot.commands.Command;
import virtualRobot.commands.DavidClass;
import virtualRobot.utils.Vector2i;

/**
 * Created by ethachu19 on 1/4/2017.
 */

public class RGBCalTestGod extends GodThread {

    private int closestTo11(double num) {
        int res = -1;
        double leastDist = Double.MAX_VALUE;
        for (int i = 0; i < 12; i++) {
            if (Math.abs(i/11 - num) < leastDist) {
                res = i;
                leastDist = Math.abs(i/11 - num);
            }
        }
        return res;
    }

    @Override
    public void realRun() throws InterruptedException {
        int width = vuforia.rgb.getWidth(), height = vuforia.rgb.getHeight();
        Vector2i start1;
        Vector2i end1;
        Vector2i start2;
        Vector2i end2;
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        if (DavidClass.currentMode == DavidClass.Mode.MIDSPLIT) {
            start1 = new Vector2i((int) DavidClass.startXPercent*width, (int) DavidClass.startYPercent*height);
            end2 = new Vector2i((int) DavidClass.endXPercent*width, (int) DavidClass.endYPercent*height);
            end1 = new Vector2i((start1.x + end2.x)/2, end2.y);
            start2 = new Vector2i(end1.x,start1.y);
        } else {
            start1 = new Vector2i((int) DavidClass.start1XPercent*width, (int) DavidClass.start1YPercent*height);
            end2 = new Vector2i((int) DavidClass.end2XPercent*width, (int) DavidClass.end2YPercent*height);
            start2 = new Vector2i((int) DavidClass.start2XPercent*width, (int) DavidClass.start2YPercent*height);
            end1 = new Vector2i((int) DavidClass.end1XPercent*width, (int) DavidClass.end1YPercent*height);
        }
        Command.AUTO_ROBOT.addToProgress("Start1: " + start1.toString());
        Command.AUTO_ROBOT.addToProgress("Start2: " + start2.toString());
        Command.AUTO_ROBOT.addToProgress("End1: " + end1.toString());
        Command.AUTO_ROBOT.addToProgress("End2: " + end2.toString());
        Vector2i slope1 = new Vector2i(11,closestTo11((end1.y - start1.y)/(end1.x - start1.x)));
        Vector2i slope2 = new Vector2i(11,closestTo11((end2.y - start2.y)/(end2.x - start2.x)));
        Vector2i currentPos;
        int leftCovered, rightCovered;
        double currLeft = 0, currRight = 0, red, blue;
        while (!Thread.currentThread().isInterrupted()) {
            currLeft = 0;
            currRight = 0;
            bm.copyPixelsFromBuffer(vuforia.rgb.getPixels());
            currentPos = new Vector2i(start1);
            for (leftCovered = 0; currentPos.x < end1.x && currentPos.y < end1.y; leftCovered++) {
                red = Color.red(bm.getPixel(currentPos.x,currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x,currentPos.y));
                currLeft += red/blue;
                currentPos.x += slope1.x;
                currentPos.y += slope1.y;
            }
            currentPos = new Vector2i(start2);
            for (rightCovered = 0; currentPos.x < end2.x && currentPos.y < end2.y; rightCovered++) {
                red = Color.red(bm.getPixel(currentPos.x,currentPos.y));
                blue = Color.blue(bm.getPixel(currentPos.x,currentPos.y));
                currRight += red/blue;
                currentPos.x += slope2.x;
                currentPos.y += slope2.y;
            }
            Command.AUTO_ROBOT.addToTelemetry("Sum: ",currLeft + " " + currRight);
            currLeft /= leftCovered;
            currRight /= rightCovered;
            Command.AUTO_ROBOT.addToTelemetry("RGB: ", currLeft + " " + currRight);
            Command.AUTO_ROBOT.addToTelemetry("Covered: ", leftCovered + " " + rightCovered);
            Log.d("RGBCal", currLeft + " " + currRight + " " + leftCovered + " " + rightCovered);

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
