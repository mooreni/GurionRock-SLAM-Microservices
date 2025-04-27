package bgu.spl.mics.application;

import java.util.List;
import bgu.spl.mics.application.objects.LandMark;


//Represents the output of a program that terminated successfully (without crashing)
//Used in order to write into a json using gson
public class NormalOutput {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    private List<LandMark> landMarks;

    public NormalOutput(){
        this.systemRuntime = 0;
        this.numDetectedObjects = 0;
        this.numTrackedObjects = 0;
        this.numLandmarks = 0;
        this.landMarks = null;
    }

    public NormalOutput(int systemRuntime, int numDetectedObjects, int numTrackedObjects, int numLandmarks, List<LandMark> landMarks){
        this.systemRuntime = systemRuntime;
        this.numDetectedObjects = numDetectedObjects;
        this.numTrackedObjects = numTrackedObjects;
        this.numLandmarks = numLandmarks;
        this.landMarks = landMarks;
    }

    public void setSystemRuntime(int systemRuntime){
        this.systemRuntime = systemRuntime;
    }

    public void setNumDetectedObjects(int numDetectedObjects){
        this.numDetectedObjects = numDetectedObjects;
    }

    public void setNumTrackedObjects(int numTrackedObjects){
        this.numTrackedObjects = numTrackedObjects;
    }

    public void setNumLandmarks(int numLandmarks){
        this.numLandmarks = numLandmarks;
    }

    public void setLandMarks(List<LandMark> landMarks){
        this.landMarks = landMarks;
    }
}
