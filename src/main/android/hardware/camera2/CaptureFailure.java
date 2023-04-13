package android.hardware.camera2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class CaptureFailure {
    public static final int REASON_ERROR = 0;
    public static final int REASON_FLUSHED = 1;
    private final String mErrorPhysicalCameraId;
    private final long mFrameNumber;
    private final int mReason;
    private final CaptureRequest mRequest;
    private final int mSequenceId;
    private final boolean mWasImageCaptured;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FailureReason {
    }

    public CaptureFailure(CaptureRequest request, int reason, boolean wasImageCaptured, int sequenceId, long frameNumber, String errorPhysicalCameraId) {
        this.mRequest = request;
        this.mReason = reason;
        this.mWasImageCaptured = wasImageCaptured;
        this.mSequenceId = sequenceId;
        this.mFrameNumber = frameNumber;
        this.mErrorPhysicalCameraId = errorPhysicalCameraId;
    }

    public CaptureRequest getRequest() {
        return this.mRequest;
    }

    public long getFrameNumber() {
        return this.mFrameNumber;
    }

    public int getReason() {
        return this.mReason;
    }

    public boolean wasImageCaptured() {
        return this.mWasImageCaptured;
    }

    public int getSequenceId() {
        return this.mSequenceId;
    }

    public String getPhysicalCameraId() {
        return this.mErrorPhysicalCameraId;
    }
}
