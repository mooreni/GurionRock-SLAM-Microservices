package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

//From PoseService to Fusion-SLAM
public class PoseEvent implements Event<Boolean>{
    private String senderName; //Which LiDar worker sent the event
    private Pose pose; //Current Pose
    private int tickTime;

    public PoseEvent(String senderName, Pose pose, int tickTime){
        this.senderName = senderName;
        this.pose = pose;
        this.tickTime = tickTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public Pose getPose() {
        return pose;
    }

    public int getTickTime() {
        return tickTime;
    }
}
