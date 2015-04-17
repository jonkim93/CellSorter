package edu.berkeley.biomaterials.cellsorter;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;


public class ActivityImagePicker extends ActionBarActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_SELECT = 2;
    private String image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_picker, menu);
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

    private String generateFileName(){
        Time now = new Time();
        return now.format("yy-MM-dd_HHmmss");
    }

    public void takeNewImage(View view){
        File photo;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String filename = generateFileName();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            photo = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
        } else {
            photo = new File(getCacheDir(), filename);
        }
        if (photo != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            image_uri = Uri.fromFile(photo).toString();
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void chooseExistingImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_SELECT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            Log.d("image picker", "result ok");
            ArrayList<Uri> uris = new ArrayList<>();
            ClipData clipDataItems = data.getClipData();
            if (clipDataItems != null) {
                for (int i = 0; i < clipDataItems.getItemCount(); i++) {
                    ClipData.Item item = clipDataItems.getItemAt(i);
                    uris.add(item.getUri());
                }
            } else {
                uris.add(data.getData());
            }
            Intent intent = new Intent(this, ActivityPreview.class);
            intent.putParcelableArrayListExtra("uris", uris);
            startActivity(intent);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.parse(image_uri));
            this.sendBroadcast(mediaScanIntent);
            Intent intent = new Intent(this, ActivityPreview.class);
            intent.putExtra("image_uri", image_uri);
            startActivity(intent);
        } else {
            Log.d("image picker", "result not ok");
        }
    }
}
