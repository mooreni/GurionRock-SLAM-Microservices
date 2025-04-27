package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private List<List<Double>> cloudPoints;

    public StampedCloudPoints(String id, int time){
        this.id=id;
        this.time=time;
        this.cloudPoints=new ArrayList<>();
    }

    public StampedCloudPoints(String id, int time, List<List<Double>> cloudPoints){
        this.id=id;
        this.time=time;
        this.cloudPoints=cloudPoints;
    }

    public String getId(){
        return id;
    }

    public int getTime(){
        return time;
    }

    public List<List<Double>> getCloudPoints(){
        return cloudPoints;

    }

    public List<CloudPoint> getCloudPointsAsObj(){
        List<CloudPoint> pointsObjects = new ArrayList<>();
        for(List<Double> point : cloudPoints){
            CloudPoint pointObject = new CloudPoint(point.get(0), point.get(1));
            pointsObjects.add(pointObject);
        }
        return pointsObjects;
    }
}
