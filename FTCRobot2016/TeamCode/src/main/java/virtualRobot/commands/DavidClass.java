package virtualRobot.commands;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOSullivan on 11/4/15.
 */

public class DavidClass {

    public static final long RED = Color.red(Color.RED); //note that Color.RED is negative
    //returns true if left is red (right is blue)
    //returns false if left if blue (right is red)
    public static boolean analyzePic2(Bitmap bmp) {
        Log.d("zzz", Long.toString(RED));
        Bitmap image= bmp;
        image= Bitmap.createScaledBitmap(bmp, image.getWidth() / 2, image.getHeight() / 2, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
        image = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);



        int[] pixels = new int[image.getWidth() * image.getHeight()];

        int height = image.getHeight(), width = image.getWidth();
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight()); //gets pixels in pixel array

        final int startX = (int) ((0.4) * width);
        final int endX = (int) (0.9*width);
        final int startY = (int) (0.55*height);
        final int endY = (int) (0.77*height);

        final int midX = (startX + endX) / 2;

        List<Integer> leftPixels = new ArrayList<Integer>();
        List<Integer> rightPixels = new ArrayList<Integer>();

        for (int i = startY; i < endY; i++) {
            for (int j = startX; j < midX; j++) {
                leftPixels.add(pixels[width*i + j]);
            }

            for (int j = midX; j < endX; j++) {
                rightPixels.add(pixels[width*i + j]);

            }

        }

        int lNum = leftPixels.size(), rNum = rightPixels.size();
        long lSum = 0, rSum = 0;
        long lAvg, rAvg;

        for (int i = 0; i < lNum;i++){
            lSum+= Color.red(leftPixels.get(i));
        }
        for (int i = 0; i <rNum;i++) {
            rSum+=  Color.red(rightPixels.get(i));
        }
        lAvg = roundUp(lSum, lNum);
        rAvg = roundUp(rSum, rNum);
        Log.d("qqq", Long.toString(lAvg) + " " + Long.toString(rAvg));


        OutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/FIRST/" + Boolean.toString(lAvg>rAvg)+Long.toString(System.currentTimeMillis()) + ".jpg"));
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return (lAvg-RED > rAvg-RED);}
    /*public static boolean analyzePic(Bitmap bmp) {
        Log.d("zzz", Long.toString(RED));
        Bitmap image= bmp;
        image= Bitmap.createScaledBitmap(bmp, image.getWidth() / 2, image.getHeight() / 2, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
        image = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        int[] pixels = new int[image.getWidth() * image.getHeight()];

        int height = image.getHeight(), width = image.getWidth();
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight()); //gets pixels in pixel array

        final int startX = roundUp(width,2)-1;
        final int endX = width;
        final int endY = roundUp(height, 100/77)-1;
        final int startY = roundUp(height, 100/55)-1;
        final int widthX = endX - startX;
        final int heightY = endY - startY;

        int MidX = roundUp(widthX, 2)-1, Midy = roundUp(heightY,2)-1;
        int Q1x = roundUp(MidX, 2), Q3x = roundUp(MidX+widthX, 2), Q1y = roundUp(Midy,2), Q3y = roundUp(Midy+heightY,2);
        List<Integer> leftPixels = new ArrayList<Integer>(), rightPixels = new ArrayList<Integer>();
        /*for (int i = Q1y; i < Q1y+Q3y; i++){
            int z1 = (widthX * i)+1;
            int z2 = (widthX*i)+Midx+1;
            for (int x = z1; x<(z1+Q1x); x++){
                leftPixels.add(pixels[x]);
            }
            for (int x = z2; x<(z2+(widthX-Q3x));x++) {
                rightPixels.add(pixels[x]);
            }
        }
        for (int i = startY; i < endY; i++) {
            int z1 = (width * i) +1+startX;
            int z2 = z1+MidX;
           for (int x = z1; x < z2; x++) {
                leftPixels.add(pixels[x]);
            }
            for (int x = z2; x < (z2+(widthX-MidX-1)); x++) {
                rightPixels.add(pixels[x]);
            }
        }
        int lNum = leftPixels.size(), rNum = rightPixels.size();
        long lSum = 0, rSum = 0;
        long lAvg, rAvg;

        for (int i = 0; i < lNum;i++){
            lSum+= Color.red(leftPixels.get(i));
        }
        for (int i = 0; i <rNum;i++) {
            rSum+=  Color.red(rightPixels.get(i));
        }
        lAvg = roundUp(lSum, lNum);
        rAvg = roundUp(rSum, rNum);
        Log.d("qqq", Long.toString(lAvg) + " " + Long.toString(rAvg));
        return (lAvg-RED > rAvg-RED);





    }*/
    //ceiling division, assumes both numbers are positive
    private static int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }
    private static long roundUp(long num, long divisor) {
        return (num + divisor - 1) / divisor;
    }


}
/* NON-ANDROID VERSION: (USES IMAGEIO)
public class DavidClass {
    public static final long RED = Color.RED.getRed();
    //returns true if left is red (right is blue)
    //returns false if left if blue (right is red)
    public static boolean analyzePic(File f) throws IOException {
       BufferedImage image;
        image = ImageIO.read(f);
        int[] pixels = null;
        int height = image.getHeight(), width = image.getWidth();
       pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()); //gets pixels in pixel array

        int Midx = roundUp(width, 2)-1, Midy = roundUp(height,2)-1;
        int Q1x = roundUp(Midx, 2), Q3x = roundUp(Midx+width, 2), Q1y = roundUp(Midy,2), Q3y = roundUp(Midy+height,2);
        List<Integer> leftPixels = new ArrayList<Integer>(), rightPixels = new ArrayList<Integer>();
        for (int i = Q1y; i < Q1y+Q3y; i++){
            int z1 = (width * i)+1;
            int z2 = (width*i)+Midx+1;
            for (int x = z1; x<(z1+Q1x); x++){
               leftPixels.add(pixels[x]);
            }
            for (int x = z2; x<(z2+(width-Q3x));x++) {
                rightPixels.add(pixels[x]);
            }
        }
        int lNum = leftPixels.size(), rNum = rightPixels.size();
        long lSum = 0, rSum = 0;
        long lAvg, rAvg;

        for (int i = 0; i < lNum;i++){
           lSum+= new Color(leftPixels.get(i)).getRed();
        }
        for (int i = 0; i <rNum;i++) {
            rSum+= new Color(rightPixels.get(i)).getRed();
        }
        lAvg = roundUp(lSum, lNum);
        rAvg = roundUp(rSum, rNum);
       return (lAvg-RED > rAvg-RED);

 */