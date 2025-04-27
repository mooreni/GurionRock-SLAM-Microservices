import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;

//Test unit for the Camera class
public class CameraTest {
    private Camera camera;
    private List<StampedDetectedObjects> stampedDetectedObjects;

    @BeforeEach
    public void setUp() {
        this.camera = new Camera(1);
        this.stampedDetectedObjects = new ArrayList<StampedDetectedObjects>();
        this.stampedDetectedObjects.add(new StampedDetectedObjects(2, new ArrayList<DetectedObject>(){{
            add(new DetectedObject("Wall_1", "Wall"));
        }}));
        this.stampedDetectedObjects.add(new StampedDetectedObjects(4, new ArrayList<DetectedObject>(){{
            add(new DetectedObject("ERROR", "Camera Disconnected"));
        }}));
        camera.setStampedDetectedObjects(stampedDetectedObjects);
    }

    @Test
    public void testCheckCurrentTickError(){
        StampedDetectedObjects result = camera.checkCurrentTick(4);
        assertEquals(result, null);
        assertEquals(camera.getStatus(), STATUS.ERROR);
    }

    @Test
    public void testCheckCurrentTickValid(){
        StampedDetectedObjects result = camera.checkCurrentTick(3);
        assertFalse(result == null);
        assertEquals(result.getDetectionTime(), 2);
    }
}
