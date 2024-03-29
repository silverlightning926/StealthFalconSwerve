package frc.robot.subsystems.vision;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Transform3d;

public class PhotonVisionCamera {

    private final PhotonCamera camera;
    private final PhotonPoseEstimator poseEstimator;
    private double lastTimestamp = 0.0;

    public PhotonVisionCamera(String cameraName, AprilTagFieldLayout fieldLayout, Transform3d robotTocamera) {
        camera = new PhotonCamera(cameraName);
        poseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, camera,
                robotTocamera);
        poseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
    }

    public PhotonPipelineResult getLatestResult() {
        return camera.getLatestResult();
    }

    public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
        var visionEstimate = poseEstimator.update();
        double timestamp = camera.getLatestResult().getTimestampSeconds();
        boolean newResult = Math.abs(lastTimestamp - timestamp) > 1e-5;
        if (newResult) {
            lastTimestamp = timestamp;
        }
        return visionEstimate;
    }

}