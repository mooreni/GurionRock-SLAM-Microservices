package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates 
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private Camera camera;
    private int currentTick;
    private CountDownLatch latch;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService");
        this.camera = camera;
        currentTick=0;
    }

    public CameraService(Camera camera, CountDownLatch latch) {
        super("CameraService");
        this.camera = camera;
        this.latch = latch;
        currentTick=0;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if(camera.getStatus()==STATUS.ERROR){
                sendBroadcast(createCrashedBroadcast(camera.getLastStampedDetectedObjects(), camera.getError(), camera.getCameraKey()));
                terminate();
            }
            else{
                currentTick = tickMessage.getTickTime();
                StampedDetectedObjects obj = camera.checkCurrentTick(currentTick);
                //If there is an error at current tick, crash
                if(obj==null && camera.getStatus()==STATUS.ERROR){
                    sendBroadcast(createCrashedBroadcast(camera.getLastStampedDetectedObjects(), camera.getError(), camera.getCameraKey()));
                    terminate();
                    return;
                }
                //if there is an object that was detected at the current tick, send it
                else if(obj!=null){
                    sendEvent(new DetectObjectsEvent(getName(), obj, currentTick));
                    camera.setLastStampedDetectedObjects(obj); // Save the last stamped detected objects
                    camera.increaseSentObjectsCount();
                    StatisticalFolder.getInstance().increaseNumDetectedObjects(obj.getDetectedObjects().size());
                }
                //If we sent all the objects, terminate
                if (camera.getSentObjectsCount() == camera.getStampedDetectedObjects().size()) {
                    camera.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminatedBroadcast(getName()));
                    terminate();
                }
            }
        });
        //Terminate if TimeService or FusionSlam terminated
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            if((terminateMessage.getSenderName().compareTo("TimeService") ==0) ||
                (terminateMessage.getSenderName().compareTo("FusionSlam") ==0)){
                terminate();
            }
        });
        //If one of the services crashed, terminate too
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{
            sendBroadcast(createResponseCrashedBroadcast(camera.getLastStampedDetectedObjects()));
            terminate();
        });
        latch.countDown();
    }


    //Methods used to create crashed broadcasts
    private CrashedBroadcast createResponseCrashedBroadcast(StampedDetectedObjects stampedDetectedObjects){
        CrashedBroadcast b = new CrashedBroadcast(getName());
        b.setSensorName(camera.getCameraKey());
        b.setLastCamerasFrame(stampedDetectedObjects);
        return b;
    }

    private CrashedBroadcast createCrashedBroadcast(StampedDetectedObjects stampedDetectedObjects, String error, String faultySensor){
        CrashedBroadcast b = createResponseCrashedBroadcast(stampedDetectedObjects);
        b.setFaultySensor(faultySensor);
        b.setError(error);
        return b;
    }
}
