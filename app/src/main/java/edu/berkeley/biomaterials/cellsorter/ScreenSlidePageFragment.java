package edu.berkeley.biomaterials.cellsorter;

/**
 * Created by jonkim93 on 4/3/2015.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScreenSlidePageFragment extends Fragment {

    private Bitmap image;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        uri = Uri.parse((String) getArguments().get("uri"));
        image = ImgProcHelper.getBitmapFromURI(uri, getActivity().getApplicationContext());
        ImageView imgView = (ImageView) rootView.findViewById(R.id.fragment_img_view);
        imgView.setImageBitmap(image);
        return rootView;
    }
}