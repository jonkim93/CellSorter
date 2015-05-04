package edu.berkeley.biomaterials.cellsorter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.berkeley.biomaterials.cellsorter.customUIElements.RangeSeekBar;
import edu.berkeley.biomaterials.cellsorter.helperMethods.ImgProcHelper;


public class ActivitySubdivide extends ActionBarActivity {

    ArrayList<Uri> uris = new ArrayList<>();
    RangeSeekBar seekbar;
    ImageView image;
    NumberPicker np;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String image_uri;
    private String folder_name = "";

    private void initializeOpenCV(){
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error", "Initialization Error");
        }
    }

    private void startPhotoIntent(){
        File photo;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String filename = ImgProcHelper.generateFileName();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + folder_name);
            if (!dir.exists()){
                dir.mkdirs();
            }
            photo = new File(dir, filename);
        } else {
            photo = new File(getCacheDir(), filename);
        }
        if (photo != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            image_uri = Uri.fromFile(photo).toString();
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showTakeAnotherPictureDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Take another image?");
        alert.setMessage("Would you like to take another image?");
        // Set an EditText view to get user input
        //final EditText input = new EditText(this);
        //alert.setView(input);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                startPhotoIntent();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                initialize();
            }
        });
        alert.show();
    }

    private void initialize(){
        initializeOpenCV();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_subdivide);
        uris = getIntent().getParcelableArrayListExtra("uris");
        folder_name = getIntent().getStringExtra("folder_name");
        if (getIntent().getStringExtra("sourceActivity").equals("imagePicker")) {
            showTakeAnotherPictureDialog();
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.parse(image_uri));
            this.sendBroadcast(mediaScanIntent);
            uris.add(Uri.parse(image_uri));
            showTakeAnotherPictureDialog();
        } else {
            Log.d("image picker", "result not ok");
        }
    }
}
