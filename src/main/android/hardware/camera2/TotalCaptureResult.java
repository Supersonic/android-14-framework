package android.hardware.camera2;

import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.hardware.camera2.impl.PhysicalCaptureResultInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class TotalCaptureResult extends CaptureResult {
    private final List<CaptureResult> mPartialResults;
    private final HashMap<String, TotalCaptureResult> mPhysicalCaptureResults;
    private final int mSessionId;

    public TotalCaptureResult(String logicalCameraId, CameraMetadataNative results, CaptureRequest parent, CaptureResultExtras extras, List<CaptureResult> partials, int sessionId, PhysicalCaptureResultInfo[] physicalResults) {
        super(logicalCameraId, results, parent, extras);
        if (partials == null) {
            this.mPartialResults = new ArrayList();
        } else {
            this.mPartialResults = partials;
        }
        this.mSessionId = sessionId;
        this.mPhysicalCaptureResults = new HashMap<>();
        for (PhysicalCaptureResultInfo onePhysicalResult : physicalResults) {
            TotalCaptureResult physicalResult = new TotalCaptureResult(onePhysicalResult.getCameraId(), onePhysicalResult.getCameraMetadata(), parent, extras, null, sessionId, new PhysicalCaptureResultInfo[0]);
            this.mPhysicalCaptureResults.put(onePhysicalResult.getCameraId(), physicalResult);
        }
    }

    public TotalCaptureResult(String logicalCameraId, CameraMetadataNative results, CaptureRequest parent, int requestId, long frameNumber, List<CaptureResult> partials, int sessionId, PhysicalCaptureResultInfo[] physicalResults) {
        super(logicalCameraId, results, parent, requestId, frameNumber);
        if (partials == null) {
            this.mPartialResults = new ArrayList();
        } else {
            this.mPartialResults = partials;
        }
        this.mSessionId = sessionId;
        this.mPhysicalCaptureResults = new HashMap<>();
        for (PhysicalCaptureResultInfo onePhysicalResult : physicalResults) {
            TotalCaptureResult physicalResult = new TotalCaptureResult(onePhysicalResult.getCameraId(), onePhysicalResult.getCameraMetadata(), parent, requestId, frameNumber, null, sessionId, new PhysicalCaptureResultInfo[0]);
            this.mPhysicalCaptureResults.put(onePhysicalResult.getCameraId(), physicalResult);
        }
    }

    public TotalCaptureResult(CameraMetadataNative results, int sequenceId) {
        super(results, sequenceId);
        this.mPartialResults = new ArrayList();
        this.mSessionId = -1;
        this.mPhysicalCaptureResults = new HashMap<>();
    }

    public List<CaptureResult> getPartialResults() {
        return Collections.unmodifiableList(this.mPartialResults);
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public Map<String, CaptureResult> getPhysicalCameraResults() {
        return Collections.unmodifiableMap(this.mPhysicalCaptureResults);
    }

    public Map<String, TotalCaptureResult> getPhysicalCameraTotalResults() {
        return Collections.unmodifiableMap(this.mPhysicalCaptureResults);
    }
}
