package bgu.spl.mics.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import bgu.spl.mics.application.objects.*;

//Represents the output of a program that crashed
//Used in order to write into a json using gson
public class ErrorOutput {
    private String error;
    private String faultySensor;
    private Map<String, StampedDetectedObjects> lastCamerasFrame;
    private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
    private List<Pose> poses;
    private StatisticalFolder statistics;
    private List<LandMark> landMarks;

    public ErrorOutput(){
        this.error = "";
        this.faultySensor = "";
        this.lastCamerasFrame = new HashMap<>();
        this.lastLiDarWorkerTrackersFrame = new HashMap<>();
        this.poses = null;
        this.statistics = null;
        this.landMarks = null;
    }

    public ErrorOutput(String error, String faultySensor, Map<String, StampedDetectedObjects> lastCamerasFrame, 
                        Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame, 
                        List<Pose> poses, StatisticalFolder statistics, List<LandMark> landMarks){
        this.error = error;
        this.faultySensor = faultySensor;
        this.lastCamerasFrame = lastCamerasFrame;
        this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
        this.poses = poses;
        this.statistics = statistics;
        this.landMarks = landMarks;
    }

    public String getError(){
        return error;
    }

    public String getFaultySensor(){
        return faultySensor;
    }

    public Map<String, StampedDetectedObjects> getLastCamerasFrame(){
        return lastCamerasFrame;
    }   

    public Map<String, List<TrackedObject>> getLastLiDarWorkerTrackersFrame(){
        return lastLiDarWorkerTrackersFrame;
    }

    public List<Pose> getPoses(){
        return poses;
    }

    public StatisticalFolder getStatistics(){
        return statistics;
    }

    public List<LandMark> getLandMarks(){
        return landMarks;
    }

    public void setError(String error){
        this.error = error;
    }

    public void setFaultySensor(String faultySensor){
        this.faultySensor = faultySensor;
    }

    public void addLastCamerasFrame(String name,StampedDetectedObjects lastCamerasFrame){
        this.lastCamerasFrame.put(name, lastCamerasFrame);
    }

    public void addLastLiDarWorkerTrackersFrame(String name,List<TrackedObject> lastLiDarWorkerTrackersFrame){
        this.lastLiDarWorkerTrackersFrame.put(name, lastLiDarWorkerTrackersFrame);
    }

    public void setPoses(List<Pose> poses){
        this.poses = poses;
    }

    public void setStatistics(StatisticalFolder statistics){
        this.statistics = statistics;
    }

    public void setLandMarks(List<LandMark> landMarks){
        this.landMarks = landMarks;
    }
}
