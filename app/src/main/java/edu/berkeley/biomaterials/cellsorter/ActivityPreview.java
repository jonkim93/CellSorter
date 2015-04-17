package edu.berkeley.biomaterials.cellsorter;

/**
 * Created by jonkim93 on 4/3/2015.
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.HashMap;

import edu.berkeley.biomaterials.cellsorter.customDataStructures.ImageCountContainer;
import edu.berkeley.biomaterials.cellsorter.customDataStructures.ParamContainer;
import edu.berkeley.biomaterials.cellsorter.customUIElements.RangeSeekBar;
import edu.berkeley.biomaterials.cellsorter.helperMethods.ImgProcHelper;

public class ActivityPreview extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private boolean absoluteInvisible = false;
    private ArrayList<Uri> imageURIs;
    private HashMap<Integer, Fragment> fragmentMap;

    private void initializeOpenCV(){
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error", "Initialization Error");
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
        if (((ToggleButton) (findViewById(R.id.toggleButtonAutoRefresh))).isChecked()) {
            Log.w("refresh", "refresh method (no args) called");
            (new PreviewImageTask()).execute(imageURIs.get(mPager.getCurrentItem()));
        }
    }

    public void refreshImage(View v){
        Log.w("refresh", "refresh method (view v) called");
        (new PreviewImageTask()).execute(imageURIs.get(mPager.getCurrentItem()));
    }

    public void process(View v){
        try {
            //ImageCountContainer[] results = (new ProcessImageTask()).execute(args).get();
            ParamContainer params = getParams();
            Intent intent = new Intent(this, ActivityResults.class);
            intent.putParcelableArrayListExtra("uris", imageURIs);
            intent.putExtra("params", params);
            startActivity(intent);
        } catch (Exception e){
            Log.d(this.toString(), e.toString());
        }
    }

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        imageURIs = intent.getParcelableArrayListExtra("uris");

        // set up openCV
        initializeOpenCV();

        // set up other views
        initializeAdaptiveAbsoluteViews(absoluteInvisible);
        initializeRangeSeekBars();

        //make sure progress bar is invisible
        (findViewById(R.id.linearLayoutProgressBar)).setVisibility(View.GONE);


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        fragmentMap = new HashMap<>();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private static String makeFragmentName(int viewPagerId, int index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment s = new ScreenSlidePageFragment();
            Bundle args = new Bundle();
            args.putString("uri", imageURIs.get(position).toString());
            s.setArguments(args);
            Log.w(this.toString(), Integer.toString(position));
            Log.w(this.toString(), s.toString());
            fragmentMap.put(position, s);
            return s;
        }

        @Override
        public int getCount() {
            return imageURIs.size();
        }
    }


    private class PreviewImageTask extends AsyncTask<Uri, Void, ImageCountContainer> { // TODO how to call this async task with a uri arg
        private Uri originalURI;

        @Override
        protected ImageCountContainer doInBackground(Uri... arg) {
            originalURI = arg[0];
            return ImgProcHelper.processImage(getParams(), arg[0], getApplicationContext());
            //return processImage(getParams());
        }

        @Override
        protected void onPreExecute(){
            Log.w("PreExecute", "In preexecute method");
            (findViewById(R.id.linearLayoutProgressBar)).setVisibility(View.VISIBLE);
            (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.GONE);
            (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(ImageCountContainer result) {
            Log.w("PostExecute", "In postexecute method");
            (findViewById(R.id.linearLayoutProgressBar)).setVisibility(View.GONE);
            if (absoluteInvisible){
                (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.GONE);
            } else {
                (findViewById(R.id.linearLayoutAdaptive)).setVisibility(View.GONE);
                (findViewById(R.id.linearLayoutAbsolute)).setVisibility(View.VISIBLE);
            }

            Fragment frag = fragmentMap.get(mPager.getCurrentItem());
            ((ScreenSlidePageFragment) frag).setImage(result.image);
            //((ImageView) findViewById(R.id.fragment_img_view)).setImageBitmap(result.image); //TODO this doesn't work properly
            //ImageView processed_view = ((ImageView) findViewById(R.id.img_view_processed));
            //processed_view.setVisibility(View.VISIBLE);
            //processed_view.setImageBitmap(ImgProcHelper.getBitmapFromURI(originalURI, getApplicationContext())); // TODO this won't work...gotta pass in a specific uri
            //processed_view.setAlpha(0.5f);
            //processed_view.bringToFront();
        }
    }

}