package edu.berkeley.biomaterials.cellsorter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.berkeley.biomaterials.cellsorter.helperMethods.ImgProcHelper;


public class ActivityImagePicker extends ActionBarActivity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_SELECT = 2;
    private String image_uri;
    private DirectoryChooserFragment mDialog;
    private final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
    private String folder_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        mDialog = DirectoryChooserFragment.newInstance("DialogSample", null);

        findViewById(R.id.buttonFolders)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.show(getFragmentManager(), null);
                    }
                });
    }

    private boolean validateImageFile(File f){
        for (String extension : okFileExtensions){
            if (f.getName().toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSelectDirectory(String path) {
        mDialog.dismiss();
        File dir = new File(path);
        File[] files = dir.listFiles();
        ArrayList<Uri> uris = new ArrayList<>();
        for (File f:files){
            if (validateImageFile(f)) {
                uris.add(Uri.fromFile(f));
            }
        }
        Intent intent = new Intent(this, ActivitySubdivide.class);
        intent.putParcelableArrayListExtra("uris", uris);
        startActivity(intent);
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
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



    private void showFolderNameDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Pick folder name");
        alert.setMessage("Please pick a folder name for this collection of images.");
        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText(); //TODO make sure the images you take go into this folder
                folder_name = value.toString();
                startPhotoIntent();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void startPhotoIntent(){
        File photo;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String filename = ImgProcHelper.generateFileName();

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
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
            intent.putExtra("folder_name", folder_name);
            image_uri = Uri.fromFile(photo).toString();
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void takeNewImage(View view){
        showFolderNameDialog();
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
            Intent intent = new Intent(this, ActivitySubdivide.class);
            intent.putParcelableArrayListExtra("uris", uris);
            intent.putExtra("sourceActivity", "imagePicker");
            startActivity(intent);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.parse(image_uri));
            this.sendBroadcast(mediaScanIntent);
            ArrayList<Uri> uris = new ArrayList<>();
            Intent intent = new Intent(this, ActivitySubdivide.class);
            uris.add(Uri.parse(image_uri));
            intent.putParcelableArrayListExtra("uris", uris);
            //intent.putExtra("image_uri", image_uri);
            intent.putExtra("sourceActivity", "imagePicker");
            startActivity(intent);
        } else {
            Log.d("image picker", "result not ok");
        }
    }
}
