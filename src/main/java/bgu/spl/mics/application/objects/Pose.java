package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    private int time;
    private Double x;
    private Double y;
    private Double yaw;

    public Pose(Double x, Double y, Double yaw, int time){
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
    }

    public Pose(Double x, Double y, Double yaw){
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = 0;
    }

    public void setTime(int time){
        this.time = time;
    }

    public Double getX(){
        return x;
    }

    public Double getY(){
        return y;
    }

    public Double getYaw(){
        return yaw;
    }

    public int getTime(){
        return time;
    }
}
