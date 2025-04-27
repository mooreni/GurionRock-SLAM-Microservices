package bgu.spl.mics.application;

import java.util.List;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

public class LidarConfigurations{
    private List<LiDarWorkerTracker> LidarConfigurations;
    private String lidars_data_path;

    public LidarConfigurations(){
        LidarConfigurations = null;
        lidars_data_path = null;
    }

    public List<LiDarWorkerTracker> getLidarConfigurations(){
        return LidarConfigurations;
    }   

    public String getLidarsDataPath(){
        return lidars_data_path;
    }

}
