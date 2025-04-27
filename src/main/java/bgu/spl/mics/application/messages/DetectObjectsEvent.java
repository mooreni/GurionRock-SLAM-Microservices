package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


//From Camera to LiDar worker
public class DetectObjectsEvent implements Event<Boolean> {
    private String senderName; //Which camera sent the event
    private StampedDetectedObjects stampedDetectedObjects; //Which object needs to be detected
    private int tickTime; //Time the event was sent

    public DetectObjectsEvent(String senderName, StampedDetectedObjects stampedDetectedObjects, int tickTime){
        this.senderName = senderName;
        this.stampedDetectedObjects = stampedDetectedObjects;
        this.tickTime = tickTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }

    public int getTickTime() {
        return tickTime;
    }
}
