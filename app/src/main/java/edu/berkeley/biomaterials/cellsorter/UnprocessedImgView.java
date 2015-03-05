package edu.berkeley.biomaterials.cellsorter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import org.opencv.core.Mat;
import org.opencv.imgproc.*;
import org.opencv.android.*;


public class UnprocessedImgView extends ActionBarActivity {

    private boolean absoluteInvisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error","Initialization Error");
        }
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
        setContentView(R.layout.activity_unprocessed_img_view);
        Intent intent = getIntent();
        Uri image_uri = Uri.parse(intent.getStringExtra("image_uri"));

        LinearLayout absoluteLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutAbsolute); //set absolute layout invisible
        absoluteLinearLayout.setVisibility(View.GONE);

        Log.w("warning", image_uri.toString());

        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(image_uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bm_image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            ImageView imgView = (ImageView) findViewById(R.id.img_view_unprocessed);
            Mat mat_img = new Mat();
            Utils.bitmapToMat(bm_image, mat_img);

            Mat gray_img = new Mat();
            Imgproc.cvtColor(mat_img, gray_img, Imgproc.COLOR_RGB2GRAY);

            Mat thresh_mat_img = new Mat();

            Imgproc.adaptiveThreshold(gray_img,    //src
                    thresh_mat_img,                //dst
                    255.0,                         //maxValue
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,//adaptiveMethod
                    Imgproc.THRESH_BINARY,         //thresholdType
                    11,                            //blockSize
                    2.0);                          //C

            Bitmap processed_bm_img = Bitmap.createBitmap(bm_image);
            Utils.matToBitmap(thresh_mat_img, processed_bm_img);

            imgView.setImageBitmap(processed_bm_img);

            /**
            IplImage cv_image = IplImage.create(bm_image.getWidth(), bm_image.getHeight(), IPL_DEPTH_8U, 4);
            bm_image.copyPixelsToBuffer(cv_image.getByteBuffer());

            IplImage thresholded_image = new IplImage();
            cvAdaptiveThreshold(cv_image, thresholded_image, 255);

            Bitmap bm_thresh_image = Bitmap.createBitmap(thresholded_image.width(), thresholded_image.height(), Bitmap.Config.ALPHA_8);
            bm_thresh_image.copyPixelsFromBuffer(thresholded_image.getByteBuffer());**/

        } catch (FileNotFoundException f) {
            Log.e("error", "FILE NOT FOUND");
        }

    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("Base Loader Callback", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void toggleAdaptiveAbsolute(View view){
        absoluteInvisible = !absoluteInvisible;
        if (absoluteInvisible){
            (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.GONE);
            (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.GONE);
            (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unprocessed_img_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
