---

## GurionRock SLAM Microservices

```markdown
# GurionRock SLAM Microservices

Java-based microservices framework and perception-mapping system prototype for a vacuum-cleaner robot.

## 🚀 Features
1. **Future & MessageBus:** Custom Future promises, thread-safe singleton bus with event & broadcast support.  
2. **MicroServices:**  
   - TimeService (ticks)  
   - CameraService → DetectObjectsEvent  
   - LiDarWorkerService → TrackedObjectsEvent  
   - FusionSlamService → landmark fusion & map  
   - PoseService → PoseEvent  
3. **SLAM Fusion:** Transforms and averages tracked objects into global landmarks.  
4. **TDD Ready:** Includes unit tests for core components (JUnit).

## 🛠 Tech Stack
- **Language:** Java 8  
- **Build:** Maven  
- **Concurrency:** Threads, synchronized collections, callbacks
