package bgu.spl.mics.application.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    private CountDownLatch latch;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data. 
     */
    public PoseService(GPSIMU gpsimu) {
        super("Pose");
        this.gpsimu = gpsimu;
    }

    public PoseService(GPSIMU gpsimu, CountDownLatch latch) {
        super("Pose");
        this.gpsimu = gpsimu;
        this.latch = latch;
    }
    
    public void setLatch(CountDownLatch latch){
        this.latch = latch;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tickMessage ->{
            if(gpsimu.getStatus()==STATUS.ERROR){
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
            else{
                int currentTick = tickMessage.getTickTime();
                List<Pose> poseList = gpsimu.getPoseList();
                sendEvent(new PoseEvent(getName(), poseList.get(currentTick-1), currentTick));
                gpsimu.incrementSentPosesCounter();
            }
            
            if(gpsimu.getSentPosesCounter() >= gpsimu.getPoseList().size()){
                sendBroadcast(new TerminatedBroadcast(getName()));
                gpsimu.setStatus(STATUS.DOWN);
                terminate();
            }
            
        });
        subscribeBroadcast(TerminatedBroadcast.class, terminateMessage ->{
            //If the service that terminates was the time service, terminate too
            if((terminateMessage.getSenderName().compareTo("TimeService") ==0) ||
                (terminateMessage.getSenderName().compareTo("FusionSlam") ==0)){
                terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, crashedMessage ->{
            sendBroadcast(new CrashedBroadcast(getName())); 
            terminate();
        });
        latch.countDown();
    }
}
