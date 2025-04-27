package bgu.spl.mics.application.messages;

import java.util.List;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

public class CrashedBroadcast implements Broadcast{
    private String senderName; //Which service sent the message
    private String error; //What error occured
    private String faultySensor; //Which sensor caused the error
    private String sensorName;
    private StampedDetectedObjects lastCamerasFrame;
    private List<TrackedObject> lastLiDarWorkerTrackersFrame;

    public CrashedBroadcast(String senderName){
        this.senderName = senderName;
        this.sensorName = "";
        this.error = "";
        this.faultySensor = "";
        lastCamerasFrame = null;
        lastLiDarWorkerTrackersFrame = null;
    } 

    public CrashedBroadcast(String senderName, String error, String faultySensor){
        this.senderName = senderName;
        this.sensorName = "";
        this.error = error;
        this.faultySensor = faultySensor;
        lastCamerasFrame = null;
        lastLiDarWorkerTrackersFrame = null;
    }

    public String getSenderName(){
        return senderName;
    }

    public String getSensorName(){
        return sensorName;
    }

    public String getError(){
        return error;
    }

    public String getFaultySensor(){
        return faultySensor;
    }

    public StampedDetectedObjects getLastCamerasFrame(){
        return lastCamerasFrame;
    }

    public List<TrackedObject> getLastLiDarWorkerTrackersFrame(){
        return lastLiDarWorkerTrackersFrame;
    }

    public void setLastCamerasFrame(StampedDetectedObjects lastCamerasFrame){
        this.lastCamerasFrame = lastCamerasFrame;
    }

    public void setLastLiDarWorkerTrackersFrame(List<TrackedObject> lastLiDarWorkerTrackersFrame){
        this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public void setFaultySensor(String faultySensor){
        this.faultySensor = faultySensor;
    }

    public void setError(String error){
        this.error = error;
    }
}
