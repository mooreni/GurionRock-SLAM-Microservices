package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDarWorkerTracker;
    private List<DetectObjectsEvent> oldEvents; //Saves up old DetectObject messages it got
    private int currentTick;
    private LiDarDataBase liDarDataBase = LiDarDataBase.getInstance(); 
    private CountDownLatch latch;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService");
        this.liDarWorkerTracker = LiDarWorkerTracker;
        oldEvents = new ArrayList<>();
        currentTick = 0;
    }

    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, CountDownLatch latch) {
        super("LiDarService");
        this.liDarWorkerTracker = LiDarWorkerTracker;
        oldEvents = new ArrayList<>();
        currentTick = 0;
        this.latch = latch;
    }
    

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if(liDarWorkerTracker.getStatus()==STATUS.ERROR){
                sendBroadcast(createCrashedBroadcast(liDarWorkerTracker.getLastTrackedObjects(), liDarWorkerTracker.getError(), liDarWorkerTracker.getSensorName()));
                terminate();
            }
            else{
                currentTick = tickMessage.getTickTime();
                //Checks if the LiDarDataBase has an error at current time
                if(liDarDataBase.checkForError(currentTick)){
                    liDarWorkerTracker.setStatus(STATUS.ERROR);
                    // Before crashing, sends to the fusionSlam the last tracked objects
                    sendBroadcast(createCrashedBroadcast(liDarWorkerTracker.getLastTrackedObjects(), liDarWorkerTracker.getError(), liDarWorkerTracker.getSensorName()));
                    terminate();
                    return;
                }

                //Checks if the LiDarDataBase has new data at current time
                trackObjects();

            }        
        });
        subscribeEvent(DetectObjectsEvent.class, detectObjectMessage ->{
            if(liDarWorkerTracker.getStatus()==STATUS.ERROR){
                // Before crashing, sends to the fusionSlam the last tracked objects
                sendBroadcast(createCrashedBroadcast(liDarWorkerTracker.getLastTrackedObjects(), liDarWorkerTracker.getError(), liDarWorkerTracker.getSensorName()));
                terminate();
            }
            else{
                oldEvents.add(detectObjectMessage);
                trackObjects();

            }
        }); 
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            if((terminateMessage.getSenderName().compareTo("TimeService") ==0) ||
                (terminateMessage.getSenderName().compareTo("FusionSlam") ==0)){
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{ 
            sendBroadcast(createResponseCrashedBroadcast(liDarWorkerTracker.getLastTrackedObjects()));
            terminate();
        });    
        latch.countDown();
    }

    private CrashedBroadcast createCrashedBroadcast(List<TrackedObject> lastTrackedObjects, String error, String faultySensor){
        CrashedBroadcast b = createResponseCrashedBroadcast(lastTrackedObjects);
        b.setFaultySensor(faultySensor);
        b.setError(error);
        return b;
    }

    private CrashedBroadcast createResponseCrashedBroadcast(List<TrackedObject> lastTrackedObjects){
        CrashedBroadcast b = new CrashedBroadcast(getName());
        b.setSensorName(liDarWorkerTracker.getSensorName());
        b.setLastLiDarWorkerTrackersFrame(lastTrackedObjects);
        return b;
    }



    //Goes over all events and completes the relevant ones
    private void trackObjects(){
        List<TrackedObject> trackedObjects = new ArrayList<>();

        //For every event the camera sent, we check if it needs to be processed based on the currentTick
        for(DetectObjectsEvent currentEvent : oldEvents){
            StampedDetectedObjects stampedDetectedObjects = currentEvent.getStampedDetectedObjects();
            //Checks if the event we process is relevant to our tick - check if its + or - here: page 15
            if(stampedDetectedObjects.getDetectionTime()+liDarWorkerTracker.getFrequency() <= currentTick){
                //For every object in the event, we create a TrackedObject and add it to the list
                for(DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()){
                    TrackedObject trackedObject = new TrackedObject(detectedObject.getID(), currentTick, detectedObject.getDescription(),
                                                    liDarDataBase.searchCoordinates(detectedObject, stampedDetectedObjects.getDetectionTime()));
                    trackedObjects.add(trackedObject);
                    liDarDataBase.increaseSentObjectsCount();
                }
                //Completes the event the camera sent
                complete(currentEvent, true);
            }
        }
        //removes processed events
        for (int i=0; i<oldEvents.size(); i++){
            if(oldEvents.get(i).getStampedDetectedObjects().getDetectionTime()+liDarWorkerTracker.getFrequency() <= currentTick){
                oldEvents.remove(i);
                i--;
            }
        }

        if(trackedObjects.size() > 0){
            sendEvent(new TrackedObjectsEvent(getName(), trackedObjects, currentTick));
            liDarWorkerTracker.setLastTrackedObjects(trackedObjects);
            liDarDataBase.setLastTrackedObjects(trackedObjects);
            StatisticalFolder.getInstance().increaseNumTrackedObjects(trackedObjects.size());
        }
        //If we sent all the objects, terminate
        if(liDarDataBase.isLidarFinished()){
            sendBroadcast(new TerminatedBroadcast(getName()));
            liDarWorkerTracker.setStatus(STATUS.DOWN);
            terminate();
        }
    }
    
}

