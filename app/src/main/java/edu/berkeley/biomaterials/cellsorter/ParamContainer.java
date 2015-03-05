package edu.berkeley.biomaterials.cellsorter;

/**
 * Created by Jon on 3/4/15.
 */
public class ParamContainer {
    public int hueLower;
    public int hueUpper;
    public int satLower;
    public int satUpper;
    public int valLower;
    public int valUpper;
    public int adaptiveBlockSize;
    public int edKernelSize;
    public int edIterations;
    public int cellSizeLower;
    public int cellSizeUpper;
    public String type;

    public ParamContainer(int hl, int hu, int sl, int su, int vl, int vu, int edks, int edi, int csl, int csu){
        hueLower = hl;
        hueUpper = hu;
        satLower = sl;
        satUpper = su;
        valLower = vl;
        valUpper = vu;
        edKernelSize = edks*2+1;
        edIterations = edi;
        cellSizeLower = csl;
        cellSizeUpper = csu;
        type = "ABSOLUTE";
    }

    public ParamContainer(int abs, int edks, int edi, int csl, int csu){
        adaptiveBlockSize = abs*2+1;
        edKernelSize = edks*2+1;
        edIterations = edi;
        cellSizeLower = csl;
        cellSizeUpper = csu;
        type = "ADAPTIVE";
    }

    public boolean isAdaptive(){
        return type == "ADAPTIVE";
    }

    public boolean checkValid(){
        return true; // TODO: don't just hardcode this!!
    }
}
