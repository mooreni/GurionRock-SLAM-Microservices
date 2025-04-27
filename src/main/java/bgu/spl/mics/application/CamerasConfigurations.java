package bgu.spl.mics.application;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

public class CamerasConfigurations{

    private List<Camera> CamerasConfigurations;
    private Map<String, List<StampedDetectedObjects>> map;
    private String camera_datas_path;

    public CamerasConfigurations(){
        CamerasConfigurations = null;
        camera_datas_path = null;
    }

    public List<Camera> getCamerasConfigurations(){
        return CamerasConfigurations;
    }

    public String getCameraDatasPath(){
        return camera_datas_path;
    }

    public void setFilePath(String path)
    {
        camera_datas_path = path;
    }

    public void updateCameras(){

        //Parses the camera_data.json
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(camera_datas_path)) {
            // Define the type for the list
            Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>(){}.getType();

            // Deserialize JSON to list of cloudpoints
            map = gson.fromJson(reader,mapType);

        } catch (IOException e) {
            e.printStackTrace();
        }


        for(Camera camera : CamerasConfigurations){
            camera.setStampedDetectedObjects(map.get(camera.getCameraKey()));
        }
    }
}
