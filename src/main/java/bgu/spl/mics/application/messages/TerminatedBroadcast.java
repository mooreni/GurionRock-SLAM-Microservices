package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast{
    private String senderName; //Which service got terminated

    public TerminatedBroadcast(String senderName){
        this.senderName = senderName;
    }

    public String getSenderName(){
        return senderName;
    }
}
