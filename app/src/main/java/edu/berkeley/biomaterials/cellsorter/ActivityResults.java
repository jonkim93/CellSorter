package edu.berkeley.biomaterials.cellsorter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.biomaterials.cellsorter.customDataStructures.ImageCountContainer;
import edu.berkeley.biomaterials.cellsorter.customDataStructures.ParamContainer;
import edu.berkeley.biomaterials.cellsorter.helperMethods.ImgProcHelper;


public class ActivityResults extends ListActivity {

    private ParamContainer params;
    private ArrayList<Uri> uris;
    private int current_index = 0;
    private ResultListAdapter result_list_adapter;
    private int final_total = 0;

    private void startNextProcessTask(){
        if (current_index < uris.size()){
            new ProcessImageTask().execute(current_index);
            current_index++;
        } else {
            TextView t = (TextView) findViewById(R.id.textview_total_count);
            t.setText(Integer.toString(final_total));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_results);

        uris = getIntent().getParcelableArrayListExtra("uris");
        params = (ParamContainer) getIntent().getSerializableExtra("params");

        result_list_adapter = new ResultListAdapter(getApplicationContext(), uris);
        setListAdapter(result_list_adapter);
        startNextProcessTask();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_results, menu);
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

    private class ProcessImageTask extends AsyncTask<Integer, Void, ImageCountContainer> {

        @Override
        protected ImageCountContainer doInBackground(Integer... args) {
            ImageCountContainer result = ImgProcHelper.processImage(params, uris.get(args[0]), getApplicationContext());
            result.index = args[0];
            return result;
        }

        @Override
        protected void onPreExecute(){
            Log.w(this.toString(), "pre execute");
        }

        @Override
        protected void onPostExecute(ImageCountContainer result) {
            Log.w(this.toString(), "post execute");
            //View v = result_list_adapter.getView(); //TODO fix this
            View current_list_item = getListView().getChildAt(result.index);
            (current_list_item.findViewById(R.id.progressbar_result_list_item)).setVisibility(View.GONE);
            TextView t = (TextView) current_list_item.findViewById(R.id.textview_result_count);
            t.setVisibility(View.VISIBLE);
            t.setText(Integer.toString(result.count));
            final_total += result.count;
            startNextProcessTask();
        }
    }

    public class ResultListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<Uri> mURIs;

        public ResultListAdapter(Context context, List<Uri> uris) {
            mInflater = LayoutInflater.from(context);
            mURIs = uris;
        }

        @Override
        public int getCount() {
            return mURIs.size();
        }

        @Override
        public Object getItem(int position) {
            return mURIs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView == null) {
                view = mInflater.inflate(R.layout.result_list_item, parent, false);
                holder = new ViewHolder();
                holder.thumbnail = (ImageView)view.findViewById(R.id.thumbnail_result); //TODO do i want a preview thumbnail???
                holder.text = (TextView)view.findViewById(R.id.textview_result_list_item);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder)view.getTag();
            }

            Uri uri = mURIs.get(position);
            holder.thumbnail.setImageBitmap(ImgProcHelper.getBitmapFromURI(uri, getApplicationContext()));
            Log.w(this.toString(), uri.toString());
            holder.text.setText("Image " + Integer.toString(position+1));

            return view;
        }

        private class ViewHolder {
            public ImageView thumbnail; //TODO do i want a preview thumbnail???
            public TextView text;
        }
    }

}
