package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;


public class TickBroadcast implements Broadcast {

    private int tickTime;

    public TickBroadcast(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getTickTime() {
        return tickTime;
    }

}
