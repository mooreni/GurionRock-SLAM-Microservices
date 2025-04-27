package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;


/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    private String error = "LiDar sensor disconnected";

    public LiDarWorkerTracker(){
        this.id=0;
        this.frequency=0;
        this.status=STATUS.UP;
        this.lastTrackedObjects = new ArrayList<TrackedObject>();
    }

    public LiDarWorkerTracker (int id, int frequency){
        this.id=id;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.lastTrackedObjects = new ArrayList<TrackedObject>();
    }


    public int getID(){
        return id;
    }

    public int getFrequency(){
        return frequency;
    }

    public STATUS getStatus(){
        return status;
    }

    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> trackedObjects){
        this.lastTrackedObjects = trackedObjects;
    }


    public void setStatus(STATUS status){
        this.status = status;
    }

    public String getSensorName(){
        return "LiDarWorkerTracker" + id;
    }

    public String getError(){
        return error;
    }

}
