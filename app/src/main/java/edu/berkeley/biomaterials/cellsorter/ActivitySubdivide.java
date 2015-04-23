package edu.berkeley.biomaterials.cellsorter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import edu.berkeley.biomaterials.cellsorter.customUIElements.RangeSeekBar;
import edu.berkeley.biomaterials.cellsorter.helperMethods.ImgProcHelper;


public class ActivitySubdivide extends ActionBarActivity {

    ArrayList<Uri> uris;
    RangeSeekBar seekbar;
    ImageView image;

    private void initializeOpenCV(){
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error", "Initialization Error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_subdivide);

        initializeOpenCV();

        uris = getIntent().getParcelableArrayListExtra("uris");
        Bitmap bm = ImgProcHelper.getBitmapFromURI(uris.get(0), getApplicationContext());
        image = ((ImageView) findViewById(R.id.imageview_subdivide));
        image.setImageBitmap(bm);
        seekbar = (RangeSeekBar) findViewById(R.id.subdivide_rangeseekbar);
        seekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                updateGrid((Integer) maxValue);
            }
        });
    }

    public void preview(View v){
        Intent intent = new Intent(this, ActivityPreview.class);
        ArrayList<Uri> subdivided_uris = subdivideAndSave();
        intent.putParcelableArrayListExtra("uris", subdivided_uris);
        startActivity(intent);
    }

    private ArrayList<Uri> subdivideAndSave(){
        int increment = seekbar.getSelectedMaxValue().intValue();
        ArrayList<Bitmap> subdivided = new ArrayList<>();
        for (Uri u : uris) {
            Bitmap img = ImgProcHelper.getBitmapFromURI(u, getApplicationContext());
            subdivided.addAll(Arrays.asList(ImgProcHelper.subdivideBitmap(img, increment)));
        }
        ArrayList<Uri> results = new ArrayList<>();
        for (int i = 0; i<subdivided.size(); i++){
            Uri uri = getImageUri(subdivided.get(i), Integer.toString(i));
            results.add(uri);
        }
        return results;
    }

    public Uri getImageUri(Bitmap im, String name) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        im.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), im, name, null);
        return Uri.parse(path);
    }

    public void updateGrid(int increment){
        for(int i = 0; i < increment; i++) {
            for(int j = 0; j < increment; j++) {
                //image.drawRect(new Rect(i*GRID_SIZE + 5, j*GRID_SIZE + 5, GRID_SIZE, GRID_SIZE), paint);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_subdivide, menu);
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
