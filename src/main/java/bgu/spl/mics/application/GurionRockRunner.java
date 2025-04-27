package bgu.spl.mics.application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;


/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        //Parse the configuration file into the ApplicationConfig class
        Path configAbsolutePath = Paths.get(args[0]).toAbsolutePath();
        ApplicationConfig config = ParserConfig.parseConfig(configAbsolutePath.toString());

        //Setup the paths
        Path configDirectory = configAbsolutePath.getParent();
        Path lidarDataPath = configDirectory.resolve(config.getLidarWorkers().getLidarsDataPath()).normalize();
        Path camerasDataPath = configDirectory.resolve(config.getCameras().getCameraDatasPath()).normalize();
        Path poseDataPath = configDirectory.resolve(config.getPoseJsonFile()).normalize();
        config.getCameras().setFilePath(camerasDataPath.toString());


        //Initialize the objects, read jsons
        config.updateCameras(); 
        List<Camera> cameras = config.getCameras().getCamerasConfigurations();
        List<LiDarWorkerTracker> lidarWorkers = config.getLidarWorkers().getLidarConfigurations();
        LiDarDataBase.LoadData(lidarDataPath.toString());
        GPSIMU gpsimu = new GPSIMU(poseDataPath.toString());
        FusionSlam.setOutputPath(configDirectory.toString());
        FusionSlam.getInstance().setSensorsCount(cameras.size() + lidarWorkers.size() + 1);


        CountDownLatch latch = new CountDownLatch(cameras.size() + lidarWorkers.size() + 2);
        List<Thread> threadList = new ArrayList<>();


        //Initialize the services
        TimeService timeService = new TimeService(config.getTickTime() * 1000, config.getDuration());
        PoseService poseService = new PoseService(gpsimu);
        poseService.setLatch(latch);
        FusionSlamService fusionSlamService = new FusionSlamService();
        fusionSlamService.setLatch(latch);
        for(Camera camera: cameras){
            CameraService camService = new CameraService(camera, latch);
            threadList.add(new Thread(camService));
        }
        for(LiDarWorkerTracker lidarWorker: lidarWorkers){
            LiDarService liDarService = new LiDarService(lidarWorker, latch);
            threadList.add(new Thread(liDarService));
        }

        //Initialize the threads
        threadList.add(new Thread(poseService));
        threadList.add(new Thread(fusionSlamService));


        //Start the threads
        for(Thread t: threadList){
            t.start();
        }

        //Start the time service
        try{
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        Thread t2 = new Thread(timeService);
        t2.start();

    }

    
}
