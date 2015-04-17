package edu.berkeley.biomaterials.cellsorter.customDataStructures;

import android.graphics.Bitmap;

/**
 * Created by jonkim93 on 4/10/2015.
 */
public class ImageCountContainer {
    public Bitmap image;
    public int count;
    public int index;

    public ImageCountContainer(){}

    public ImageCountContainer(Bitmap i, int c){
        image = i;
        count = c;
    }
}
