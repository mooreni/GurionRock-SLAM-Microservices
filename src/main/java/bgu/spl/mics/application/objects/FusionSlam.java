package bgu.spl.mics.application.objects;

import java.util.List;
import bgu.spl.mics.application.NormalOutput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import bgu.spl.mics.application.ErrorOutput;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance = new FusionSlam();
    }

    private List<LandMark> globalMap;
    private List<Pose> previousRobotPoses;
    private List<TrackedObject> waitingTrackedObjects;
    private int sensorsCount;                           //Number of active sensors
    private String outputPath;                          //Path to output folder
    private List<Object> lastFrames;                    //The last frames of the sensors

    /**
     * Retrieves the single instance of FusionSlam.
     *
     * @return The FusionSlam instance.
     */ 
    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    /**
     * Initializes the FusionSlam object.
     */
    private FusionSlam() {
        previousRobotPoses = new ArrayList<>();
        globalMap = new ArrayList<>();
        waitingTrackedObjects = new ArrayList<>();
        sensorsCount = 0;
        lastFrames = new ArrayList<>();
        outputPath = "";
    }

    /**
     * Updates the global map with new landmarks.
     *
     * @param landmarks The landmarks to add to the global map.
     */ 
    public void addLandmark(LandMark landmarks) {
        globalMap.add(landmarks);
    }

    //Calculations
    public CloudPoint calculateCoordinates(Pose pose, CloudPoint cloudPoint) {
        // Step 1: Convert yaw from degrees to radians
        double yawRadians = Math.toRadians(pose.getYaw());  // Convert to radians
        
        // Step 2: Compute cosine and sine of the yaw angle
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);
        
        // Step 3: Apply the transformation
        double xGlobal = cosTheta * cloudPoint.getX() - sinTheta * cloudPoint.getY() + pose.getX();
        double yGlobal = sinTheta * cloudPoint.getX() + cosTheta * cloudPoint.getY() + pose.getY();
        
        return new CloudPoint(xGlobal, yGlobal);
    }
    
    public List<CloudPoint> averageCloudPoints(List<CloudPoint> oldList, List<CloudPoint> newList) { 
        // Create a new list to store the averaged CloudPoints
        List<CloudPoint> averagedList = new ArrayList<>();
    
        int minSize = Math.min(oldList.size(), newList.size());  // Determine the minimum size of the two lists
    
        // Iterate through both lists, average the corresponding points, and add to the result list
        for (int i = 0; i < minSize; i++) {
            CloudPoint point1 = oldList.get(i);
            CloudPoint point2 = newList.get(i);
    
            // Calculate the average x and y coordinates
            Double avgX = (point1.getX() + point2.getX()) / 2;
            Double avgY = (point1.getY() + point2.getY()) / 2;
    
            // Add the averaged point to the result list
            averagedList.add(new CloudPoint(avgX, avgY));
        }
    
        // If newList is larger, add the remaining points from newList
        if (newList.size() > oldList.size()) {
            for (int i = minSize; i < newList.size(); i++) {
                averagedList.add(newList.get(i));  // Add the extra points from newList as is
            }
        }
    
        // If oldList is larger, add the remaining points from oldList
        if (oldList.size() > newList.size()) {
            for (int i = minSize; i < oldList.size(); i++) {
                averagedList.add(oldList.get(i));  // Add the extra points from oldList as is
            }
        }
    
        return averagedList;
    }
    

    //@pre: trackedObject != null
    //@post: StatisticalFolder.getInstance().getNumLandmarks() == @pre + 1
    //@post: getLandMark(trackedObject.getID()) == 1
    public void updateGlobalMap(TrackedObject trackedObject) {
        List<CloudPoint> cloudPoints = trackedObject.getCloudCoordinates();
        List<CloudPoint> transformedCloudPoints = new ArrayList<>();
        for(CloudPoint cloudPoint : cloudPoints){
            Pose pose = getPreviousRobotPoses().get(trackedObject.getTime()-1);
            transformedCloudPoints.add(calculateCoordinates(pose, cloudPoint));
        }
        Iterator<LandMark> landMarkIterator = getGlobalMap().iterator();
        boolean exists = false;
        LandMark newLandMark = new LandMark(trackedObject.getID(), trackedObject.getDescription(), transformedCloudPoints);
        LandMark landMark;
        while(landMarkIterator.hasNext()&&!exists){
            landMark = landMarkIterator.next();
            if(landMark.getId().equals(newLandMark.getId())){
                newLandMark.setCloudPoints(averageCloudPoints(landMark.getCloudPoints(), transformedCloudPoints));
                exists = true;
                landMarkIterator.remove();  
            }
        }
        //If it didnt exist before, add to the statistical folder
        if(!exists){
            StatisticalFolder.getInstance().increaseNumLandmarks(1);;
        }
        addLandmark(newLandMark);
    }

    //Output creation
    //Creates a file that represents a terminated program
    public void createNormalOutput(){
        StatisticalFolder folder = StatisticalFolder.getInstance();
        NormalOutput output = new NormalOutput(folder.getSystemRuntime(), folder.getNumDetectedObjects(), folder.getNumTrackedObjects(), folder.getNumLandmarks(), getGlobalMap());
        //Creates the json and writes it
        toJson(output);
    }

    //Creates a file that represents a crashed program
    public void createErrorOutput(ErrorOutput output){
        output.setStatistics(StatisticalFolder.getInstance());
        output.setLandMarks(getGlobalMap());
        output.setPoses(previousRobotPoses);
        toJson(output);
    }

    //Creates the output json - of either type
    public void toJson(Object output) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(outputPath + "/output_file.json")) {
            // Serialize Java objects to JSON file
            gson.toJson(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Getters
    public List<LandMark> getGlobalMap() {
        return globalMap;
    }
    public List<Pose> getPreviousRobotPoses() {
        return previousRobotPoses;
    }

    public List<TrackedObject> getWaitingTrackedObjects() {
        return waitingTrackedObjects;
    }

    public int getSensorsCount() {
        return sensorsCount;
    }

    public List<Object> getLastFrames() {
        return lastFrames;
    }

    public LandMark getLandMark(String id) {
        for (LandMark landMark : globalMap) {
            if (landMark.getId().equals(id)) {
                return landMark;
            }
        }
        return null;
    }

    //Should always return 1 or 0 - otherwise there is a problem
    public int numOfInstances(String id) {
        int count = 0;
        for (LandMark landMark : globalMap) {
            if (landMark.getId().equals(id)) {
                count++;
            }
        }
        return count;
    }

    //Setters and adders
    public void addPose(Pose pose) {
        previousRobotPoses.add(pose);
    }

    public void addWaitingTrackedObject(TrackedObject trackedObject) {
        waitingTrackedObjects.add(trackedObject);
    }
    
    public void setSensorsCount(int num) {
        sensorsCount = num;
    }

    public void decrementSensorsCount() {
        sensorsCount--;
    }

    public static void setOutputPath(String outputPath) {
        FusionSlamHolder.instance.outputPath = outputPath;
    }
}
