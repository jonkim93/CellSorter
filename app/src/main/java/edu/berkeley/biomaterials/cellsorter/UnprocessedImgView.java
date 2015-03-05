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
import java.util.HashMap;

import org.opencv.core.Mat;
import org.opencv.imgproc.*;
import org.opencv.android.*;


public class UnprocessedImgView extends ActionBarActivity {

    private boolean absoluteInvisible = false;
    private Uri imageURI;

    private void initializeOpenCV(){
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error","Initialization Error");
        }
    }

    private void initializeAdaptiveAbsoluteViews(boolean absoluteIsInvisible){
        if (absoluteIsInvisible) {
            (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.GONE); //set absolute layout invisible
            (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.GONE); //set absolute layout invisible
            (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.VISIBLE);
        }
    }

    private void initializeRangeSeekBars(){
        RangeSeekBar<Integer> rangeSeekBarHue = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarHueRange);
        rangeSeekBarHue.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarSat = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarSatRange);
        rangeSeekBarSat.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarVal = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarValRange);
        rangeSeekBarVal.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAbsoluteCellSize = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAbsoluteCellSize);
        rangeSeekBarAbsoluteCellSize.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAdaptiveCellSize = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAdaptiveCellSize);
        rangeSeekBarAdaptiveCellSize.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAdaptiveThresholdBlockSize = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAdaptiveThresholdBlockSize);
        rangeSeekBarAdaptiveThresholdBlockSize.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAbsoluteErodeDilateBlockSize = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAbsoluteErodeDilateBlockSize);
        rangeSeekBarAbsoluteErodeDilateBlockSize.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAdaptiveErodeDilateIterations = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAdaptiveErodeDilateIterations);
        rangeSeekBarAdaptiveErodeDilateIterations.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

        RangeSeekBar<Integer> rangeSeekBarAbsoluteErodeDilateIterations = (RangeSeekBar<Integer>) findViewById(R.id.rangeSeekBarAbsoluteErodeDilateIterations);
        rangeSeekBarAbsoluteErodeDilateIterations.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                Number big = bar.getSelectedMaxValue();
                Number small = bar.getSelectedMinValue();
                Log.w("range seekbar", "big: "+big.toString()+"\nsmall: "+small.toString());
                refreshImage();
            }
        });

    }

    private Bitmap getBitmapFromURI(Uri image_uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(image_uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bm_image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            return bm_image;
        } catch (FileNotFoundException f) {
            Log.e("error", "FILE NOT FOUND");
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(800,800,conf); //TODO: don't hardcode the empty bitmap's dimensions
        }
    }

    private Bitmap processImage(ParamContainer p){
        Bitmap bm_image = getBitmapFromURI(imageURI);
        if (p.checkValid()) {
            Mat mat_img = new Mat();
            Utils.bitmapToMat(bm_image, mat_img);
            Mat processed_mat_img = new Mat();
            if (p.isAdaptive()) {
                Mat gray_img = new Mat();
                Imgproc.cvtColor(mat_img, gray_img, Imgproc.COLOR_RGB2GRAY);
                Imgproc.adaptiveThreshold(gray_img,    //src
                        processed_mat_img,                //dst
                        255.0,                         //maxValue
                        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,//adaptiveMethod
                        Imgproc.THRESH_BINARY,         //thresholdType
                        p.adaptiveBlockSize,           //blockSize
                        2.0);                          //C

                Bitmap processed_bm_img = Bitmap.createBitmap(bm_image);
                Utils.matToBitmap(processed_mat_img, processed_bm_img);
                return processed_bm_img;
            } else {
                //Imgproc.
                return bm_image; // TODO need to change this!!
            }
        } else {
            Log.w("INVALID IMAGE", "INVALID IMAGE");
            return bm_image;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeOpenCV();
        setContentView(R.layout.activity_unprocessed_img_view);
        initializeAdaptiveAbsoluteViews(absoluteInvisible);
        initializeRangeSeekBars();

        Intent intent = getIntent();
        imageURI = Uri.parse(intent.getStringExtra("image_uri"));
        Bitmap bm_img = getBitmapFromURI(imageURI);
        ImageView imgView = (ImageView) findViewById(R.id.img_view_unprocessed);
        imgView.setImageBitmap(bm_img);
    }



    private ParamContainer getParams(){
        if (!absoluteInvisible){
            int hl   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarHueRange)).getSelectedMinValue().intValue();
            int hu   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarHueRange)).getSelectedMaxValue().intValue();
            int sl   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarSatRange)).getSelectedMinValue().intValue();
            int su   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarSatRange)).getSelectedMaxValue().intValue();
            int vl   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarValRange)).getSelectedMinValue().intValue();
            int vu   = ((RangeSeekBar) findViewById(R.id.rangeSeekBarValRange)).getSelectedMaxValue().intValue();
            int csl  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAbsoluteCellSize)).getSelectedMinValue().intValue();
            int csu  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAbsoluteCellSize)).getSelectedMaxValue().intValue();
            int edks = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAbsoluteErodeDilateBlockSize)).getSelectedMaxValue().intValue();
            int edi  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAbsoluteErodeDilateIterations)).getSelectedMaxValue().intValue();
            return new ParamContainer(hl,hu,sl,su,vl,vu,edks,edi,csl,csu);
        } else {
            int abs  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAdaptiveThresholdBlockSize)).getSelectedMaxValue().intValue();
            int edks = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAdaptiveErodeDilateBlockSize)).getSelectedMaxValue().intValue();
            int edi  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAdaptiveErodeDilateIterations)).getSelectedMaxValue().intValue();
            int csl  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAdaptiveCellSize)).getSelectedMaxValue().intValue();
            int csu  = ((RangeSeekBar) findViewById(R.id.rangeSeekBarAdaptiveCellSize)).getSelectedMaxValue().intValue();
            return new ParamContainer(abs, edks, edi, csl, csu);
        }
    }

    public void refreshImage(){
        Log.w("refresh", "refresh method called");
        ImageView imgView = (ImageView) findViewById(R.id.img_view_unprocessed);
        imgView.setImageBitmap(processImage(getParams()));
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
