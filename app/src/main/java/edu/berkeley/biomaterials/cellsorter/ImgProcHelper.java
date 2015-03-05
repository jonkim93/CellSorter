package edu.berkeley.biomaterials.cellsorter;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jon on 3/5/15.
 */
public class ImgProcHelper {

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

}
