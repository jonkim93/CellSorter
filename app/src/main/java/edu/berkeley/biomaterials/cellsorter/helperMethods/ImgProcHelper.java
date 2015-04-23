package edu.berkeley.biomaterials.cellsorter.helperMethods;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.biomaterials.cellsorter.customDataStructures.ImageCountContainer;
import edu.berkeley.biomaterials.cellsorter.customDataStructures.ParamContainer;

/**
 * Created by Jon on 3/5/15.
 */
public class ImgProcHelper {

    public static Bitmap getBitmapFromURI(Uri image_uri, Context context){
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(image_uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bm_image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            return bm_image;
        } catch (FileNotFoundException f) {
            Log.e("error", "FILE NOT FOUND");
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(800,800,conf); //TODO: don't hardcode the empty bitmap's dimensions
        }
    }

    public static Bitmap[] subdivideBitmap(Bitmap im, int increment){
        Bitmap[] results = new Bitmap[increment*increment];
        Mat mat_img = new Mat();
        Utils.bitmapToMat(im, mat_img);
        int width = mat_img.width();
        int height = mat_img.height();
        int grid_width = width/increment;
        int grid_height = height/increment;
        for (int x=0; x<increment; x++){
            for (int y=0;y<increment; y++){
                int row_start = x*grid_width;
                int col_start = y*grid_height;
                int row_end = (x+1)*grid_width;//-1;
                int col_end = (y+1)*grid_height;//-1;
                Bitmap tmp = Bitmap.createBitmap(im, row_start, col_start, row_end - row_start, col_end - col_start);
                Mat sub_img = mat_img.submat(col_start, col_end, row_start, row_end);
                Log.w("TMP HEIGHT: ", Integer.toString(tmp.getHeight()));
                Log.w("TMP WIDTH : ", Integer.toString(tmp.getWidth()));
                Log.w("SUB_IMG HEIGHT : ", Integer.toString(sub_img.height()));
                Log.w("SUB_IMG WIDTH  : ", Integer.toString(sub_img.width()));
                Utils.matToBitmap(sub_img, tmp);
                results[x*increment+y] = tmp;
            }
        }
        return results;
    }

    public static int countBlobs(Bitmap binaryMask){
        Mat mat_img = new Mat();
        Utils.bitmapToMat(binaryMask, mat_img);
        Mat formatted_img = new Mat();
        Imgproc.cvtColor(mat_img, formatted_img, Imgproc.COLOR_BGR2GRAY);

        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(formatted_img,contours,hierarchy,Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);
        return contours.size();
    }

    public static Mat ErodeDilate(int ksize, int iterations, Mat img){
        for (int i=0; i<iterations; i++) {
            Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(ksize, ksize)));
            Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(ksize, ksize)));
        }
        return img;
    }

    public static Mat DetectAndDrawCircles(int minRadius, int maxRadius, Mat img){
        Mat circles = new Mat();
        Imgproc.HoughCircles(img, circles, Imgproc.CV_HOUGH_GRADIENT, 1, img.rows()/8, 200, 100, minRadius, maxRadius);
        /// Draw the circles detected
        for(int i = 0; i < circles.cols(); i++){
            double vCircle[] = circles.get(0,i);
            if (vCircle == null)
                break;
            Point center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
            // draw the found circle
            Core.circle(img, center, radius, new Scalar(0,255,0), -1, 8, 0);
            Core.circle(img, center, 3,      new Scalar(0,0,255),  3, 8, 0);
        }
        return img;
    }

    public static Bitmap adaptiveProcessImage(ParamContainer p, Bitmap bm_image){
        Mat mat_img = new Mat();
        Utils.bitmapToMat(bm_image, mat_img);
        Mat processed_mat_img = new Mat();
        Mat gray_img = new Mat();

        // convert img to grayscale
        Imgproc.cvtColor(mat_img, gray_img, Imgproc.COLOR_RGB2GRAY);

        // adaptive threshold
        Imgproc.adaptiveThreshold(gray_img, processed_mat_img, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, p.adaptiveBlockSize, 2.0);

        // erode and dilate
        processed_mat_img = ErodeDilate(p.edKernelSize, p.edIterations, processed_mat_img);

        // detect circles and draw
        processed_mat_img = DetectAndDrawCircles(p.cellSizeLower, p.cellSizeUpper, processed_mat_img);

        Bitmap processed_bm_img = Bitmap.createBitmap(bm_image);
        Utils.matToBitmap(processed_mat_img, processed_bm_img);
        return processed_bm_img;
    }

    public static Bitmap absoluteProcessImage(ParamContainer p, Bitmap bm_image){
        Mat mat_img = new Mat();
        Utils.bitmapToMat(bm_image, mat_img);
        Mat processed_mat_img = new Mat();
        Mat hsv_img = new Mat();

        //convert img to hsv format
        Imgproc.cvtColor(mat_img, hsv_img, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsv_img, new Scalar(p.hueLower, p.satLower, p.valLower), new Scalar(p.hueUpper, p.satUpper, p.valUpper), processed_mat_img);

        // erode and dilate
        processed_mat_img = ErodeDilate(p.edKernelSize, p.edIterations, processed_mat_img);

        // detect circles and draw
        processed_mat_img = DetectAndDrawCircles(p.cellSizeLower, p.cellSizeUpper, processed_mat_img);

        Bitmap processed_bm_img = Bitmap.createBitmap(bm_image);
        Utils.matToBitmap(processed_mat_img, processed_bm_img);
        return processed_bm_img;
    }

    public static ImageCountContainer processImage(ParamContainer p, Uri imageUri, Context context){
        Bitmap bm_image = ImgProcHelper.getBitmapFromURI(imageUri, context);
        ImageCountContainer result = new ImageCountContainer();
        if (p.checkValid()) {
            if (p.isAdaptive()) {
                result.image = ImgProcHelper.adaptiveProcessImage(p, bm_image);
                result.count = ImgProcHelper.countBlobs(result.image);
            } else {
                result.image = ImgProcHelper.absoluteProcessImage(p, bm_image);
                result.count = ImgProcHelper.countBlobs(result.image);
            }
        } else {
            Log.w("INVALID IMAGE", "INVALID IMAGE");
            result.image = bm_image;
            result.count = -1;
        }
        return result;
    }

}
