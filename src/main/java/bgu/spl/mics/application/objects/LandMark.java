package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String description;
    private List<CloudPoint> cloudPoints;

    /**
     * Constructor for LandMark.
     *
     * @param id          The unique identifier for the landmark.
     * @param description A description of the landmark.
     * @param cloudPoints The cloud points that define the landmark.
     */
    public LandMark(String id, String description, List<CloudPoint> cloudPoints) {
        this.id = id;
        this.description = description;
        this.cloudPoints = cloudPoints;
    }

    public LandMark(String id, String description) {
        this.id = id;
        this.description = description;
        this.cloudPoints = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }

    public void addCloudPoint(CloudPoint cloudPoint) {
        cloudPoints.add(cloudPoint);
    }

    public void setCloudPoints(List<CloudPoint> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

}
