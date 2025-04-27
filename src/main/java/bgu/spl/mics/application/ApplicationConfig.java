package bgu.spl.mics.application;

public class ApplicationConfig {
    private CamerasConfigurations Cameras;
    private LidarConfigurations LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    public ApplicationConfig(){
        Cameras = new CamerasConfigurations();
        LiDarWorkers = new LidarConfigurations();
    }

    public CamerasConfigurations getCameras(){
        return Cameras;
    }

    public LidarConfigurations getLidarWorkers(){
        return LiDarWorkers;
    }   

    public String getPoseJsonFile(){
        return poseJsonFile;
    }

    public int getTickTime(){
        return TickTime;
    }

    public int getDuration(){
        return Duration;
    }

    public void updateCameras(){
        Cameras.updateCameras();
    }

    


}

