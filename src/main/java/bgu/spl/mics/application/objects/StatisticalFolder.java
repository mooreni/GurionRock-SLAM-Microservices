package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    private static class StatisticalFolderHolder{
        private static volatile StatisticalFolder instance = new StatisticalFolder();
    }
    private AtomicInteger systemRuntime;
    private AtomicInteger numDetectedObjects;
    private AtomicInteger numTrackedObjects;
    private AtomicInteger numLandmarks;

    private StatisticalFolder(){
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    public static StatisticalFolder getInstance(){
        return StatisticalFolderHolder.instance;
    }

    //These arent synchronized
    public int getSystemRuntime(){
        return systemRuntime.get();
    }

    public int getNumDetectedObjects(){
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects(){
        return numTrackedObjects.get();
    }

    public int getNumLandmarks(){
        return numLandmarks.get();
    }
    //

    public void increaseSystemRuntime(){
        int oldVal;
        int newVal;
        do{
            oldVal = systemRuntime.get();
            newVal = oldVal + 1;
        }while(!systemRuntime.compareAndSet(oldVal, newVal));
    }

    public void increaseNumDetectedObjects(int numOfObjects){
        int oldVal;
        int newVal;
        do{
            oldVal = numDetectedObjects.get();
            newVal = oldVal + numOfObjects;
        }while(!numDetectedObjects.compareAndSet(oldVal, newVal));
    }

    public void increaseNumTrackedObjects(int numOfObjects){
        int oldVal;
        int newVal;
        do{
            oldVal = numTrackedObjects.get();
            newVal = oldVal + numOfObjects;
        }while(!numTrackedObjects.compareAndSet(oldVal, newVal));
    }

    public void increaseNumLandmarks(int numOfLandmarks){
        int oldVal;
        int newVal;
        do{
            oldVal = numLandmarks.get();
            newVal = oldVal + numOfLandmarks;
        }while(!numLandmarks.compareAndSet(oldVal, newVal));
    }
}
