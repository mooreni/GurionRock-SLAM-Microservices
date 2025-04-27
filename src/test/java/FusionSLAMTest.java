import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;


public class FusionSLAMTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.addPose(new Pose(0.0, 0.0, 0.0, 1));
        fusionSlam.addPose(new Pose(-2.366, 0.9327, -28.08, 2));
        fusionSlam.addPose(new Pose(-3.2076, 0.0755, -87.48, 3));
        fusionSlam.addPose(new Pose(0.0, 3.6, 57.3, 4));

        fusionSlam.addLandmark(new LandMark("Wall_1", "Wall", new ArrayList<CloudPoint>(){{
            add(new CloudPoint(0.49089550692574013, 0.12055946349116727));
            add(new CloudPoint(0.39781045088976974, 0.12044657956762475));
        }}));
    }

    @Test
    public void testUpdateGlobalMap_New(){
        int i = StatisticalFolder.getInstance().getNumLandmarks();
        fusionSlam.updateGlobalMap(new TrackedObject("Door", 2, "Door", new ArrayList<CloudPoint>(){{
            add(new CloudPoint(0.5, -2.1));
            add(new CloudPoint(0.8, -2.3));
        }}));
        LandMark result = fusionSlam.getLandMark("Door");
        assertTrue("-2.913".equals(String.format("%.3f", result.getCloudPoints().get(0).getX())));
        assertTrue("-1.155".equals(String.format("%.3f", result.getCloudPoints().get(0).getY())));
        assertTrue("-2.743".equals(String.format("%.3f", result.getCloudPoints().get(1).getX())));
        assertTrue("-1.473".equals(String.format("%.3f", result.getCloudPoints().get(1).getY())));
        assertEquals(fusionSlam.numOfInstances("Door"), 1);
        assertEquals(StatisticalFolder.getInstance().getNumLandmarks(), i+1);
    }

    @Test
    public void testUpdateGlobalMap_Exists(){
        int i = StatisticalFolder.getInstance().getNumLandmarks();
        fusionSlam.updateGlobalMap(new TrackedObject("Wall_1", 4, "Wall", new ArrayList<CloudPoint>(){{
                add(new CloudPoint(0.5, 3.9));
                add(new CloudPoint(0.2, 3.7));
        }}));
        LandMark result = fusionSlam.getLandMark("Wall_1");
        assertTrue("-1.260".equals(String.format("%.3f", result.getCloudPoints().get(0).getX())));
        assertTrue("3.124".equals(String.format("%.3f", result.getCloudPoints().get(0).getY())));
        assertTrue("-1.304".equals(String.format("%.3f", result.getCloudPoints().get(1).getX())));
        assertTrue("2.944".equals(String.format("%.3f", result.getCloudPoints().get(1).getY())));
        assertEquals(fusionSlam.numOfInstances("Wall_1"), 1);
        assertEquals(StatisticalFolder.getInstance().getNumLandmarks(), i);
        
    }


}
