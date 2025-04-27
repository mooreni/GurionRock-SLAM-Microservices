package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class LiDARDataBaseHolder {
		private static volatile LiDarDataBase instance = new LiDarDataBase();
	}

    private List<StampedCloudPoints> cloudPoints;
    private List<TrackedObject> lastTrackedObjects;
    private AtomicInteger sentObjectsCount;


    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    private LiDarDataBase(){
        cloudPoints = new ArrayList<StampedCloudPoints>();
        lastTrackedObjects = new ArrayList<TrackedObject>();
        sentObjectsCount = new AtomicInteger(0);

    }


    public static void LoadData(String filePath){
        Gson gson = new Gson();
        List<StampedCloudPoints> parsedCloudPoints = null;
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type for the list
            Type stampedCloudPointType = new TypeToken<List<StampedCloudPoints>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            parsedCloudPoints = gson.fromJson(reader,stampedCloudPointType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        LiDARDataBaseHolder.instance.cloudPoints = parsedCloudPoints;
    }

    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase.LoadData(filePath);
        return LiDARDataBaseHolder.instance;
    }

    public static LiDarDataBase getInstance() {
        return LiDARDataBaseHolder.instance;
    }


    public List<StampedCloudPoints> getStampedCloudPoints(){
        return cloudPoints;
    } 

    public List<CloudPoint> searchCoordinates(DetectedObject obj, int time){ // Should return the object's coordinates
        List<CloudPoint> coordinates = new ArrayList<CloudPoint>();
        for(StampedCloudPoints stampedCloudPoints : cloudPoints){
            if(stampedCloudPoints.getTime() == time && stampedCloudPoints.getId().compareTo(obj.getID()) == 0){
                coordinates = stampedCloudPoints.getCloudPointsAsObj();
            }
        }
        return coordinates;
    }

    public boolean checkForError(int time){
        for(StampedCloudPoints stampedCloudPoints : cloudPoints){
            if(stampedCloudPoints.getTime() > time){
                break;
            }
            if(stampedCloudPoints.getTime() == time && stampedCloudPoints.getId().compareTo("ERROR") == 0){
                return true;
            }
        }
        return false;
    }

    public List<TrackedObject> getLastTrackedObjects(){
        return lastTrackedObjects;
    }

    public void setLastTrackedObjects(List<TrackedObject> trackedObjects){
        lastTrackedObjects = trackedObjects;
    }

    public void increaseSentObjectsCount(){
        int oldVal;
        int newVal;
        do{
            oldVal = sentObjectsCount.get();
            newVal = oldVal + 1;
        }while(!sentObjectsCount.compareAndSet(oldVal, newVal));
    }

    public int getSentObjectsCount(){
        return sentObjectsCount.get();
    }

    public boolean isLidarFinished(){
        return sentObjectsCount.get() == cloudPoints.size();
    }
}
