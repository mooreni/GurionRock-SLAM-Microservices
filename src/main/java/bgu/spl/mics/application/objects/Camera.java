package bgu.spl.mics.application.objects;


import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> stampedDetectedObjects;    //List of objects it will detect while its running
    private String camera_key;                                      //Name of the camera in the jsons
    private int sentObjectsCount; 

    private String error; 
    private StampedDetectedObjects lastStampedDetectedObjects;

    //Constructors
    public Camera(){
        this.id=0;
        this.frequency=0;
        this.status=STATUS.UP;
        this.camera_key="";
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        this.sentObjectsCount=0;
        this.lastStampedDetectedObjects = null;
        this.error="";
    }

    public Camera(int frequency){
        this.id=0;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.camera_key="";
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        this.sentObjectsCount=0;
        this.lastStampedDetectedObjects = null;
        this.error="";
    }

    public Camera (int id, int frequency, String filePath){
        this.id=id;
        this.frequency=frequency;
        this.status=STATUS.UP;
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        loadData(filePath);
        this.sentObjectsCount=0;
        this.lastStampedDetectedObjects = null;
        this.error="";
    }


    //@return: null if there is no object to send, 
    //         else returns the object to send
    //@pre: none
    //@post: none
    //Recieves the current tick and checks if there is an error, or if it needs to send a DetectObjectsEvent
    public StampedDetectedObjects checkCurrentTick(int currentTick){
        StampedDetectedObjects result = null;
        for(StampedDetectedObjects obj : stampedDetectedObjects){
            if(obj.getDetectionTime() > currentTick){
                return result;
            }
            //Checks for error in current tick
            if(obj.getDetectionTime() == currentTick){
                for(DetectedObject detectedObject : obj.getDetectedObjects()){
                    if(detectedObject.getID().equals("ERROR")){
                        setStatus(STATUS.ERROR);
                        setError(detectedObject.getDescription());
                        return null;
                    }
                }
            }
            //Checks if it needs to send DetectObjectsEvent
            if(obj.getDetectionTime()+getFrequency() == currentTick){
                result =  obj;
            }
        }
        return result;
    }

    //Fills stampedDetectedObjects with all the data from the json
    public void loadData(String filePath){
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            // Define the type for the list
            Type detectedType = new TypeToken<List<StampedDetectedObjects>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            stampedDetectedObjects = gson.fromJson(reader,detectedType);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Setters
    public void setStatus(STATUS status){
        this.status = status;
    }

    public void setStampedDetectedObjects(List<StampedDetectedObjects> objs){
        stampedDetectedObjects = objs;
    }

    public void increaseSentObjectsCount(){
        sentObjectsCount++;
    }

    public void setError(String error){
        this.error = error;
    }

    public void setLastStampedDetectedObjects(StampedDetectedObjects lastStampedDetectedObjects){
        this.lastStampedDetectedObjects = lastStampedDetectedObjects;
    }


    //Getters
    public int getID(){
        return id;
    }

    public int getFrequency(){
        return frequency;
    }

    public STATUS getStatus(){
        return status;
    }

    public List<StampedDetectedObjects> getStampedDetectedObjects(){
        return stampedDetectedObjects;
    }

    public String getCameraKey(){
        return camera_key;
    }

    public int getSentObjectsCount(){
        return sentObjectsCount;
    }   

    public String getError(){
        return error;
    }

    public StampedDetectedObjects getLastStampedDetectedObjects(){
        return lastStampedDetectedObjects;
    }
}
