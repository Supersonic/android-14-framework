package android.hardware.camera2.impl;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.utils.SubmitInfo;
import java.util.List;
/* loaded from: classes.dex */
public class RequestLastFrameNumbersHolder {
    private boolean mInflightCompleted;
    private final long mLastRegularFrameNumber;
    private final long mLastReprocessFrameNumber;
    private final long mLastZslStillFrameNumber;
    private final int mRequestId;
    private boolean mSequenceCompleted;

    public RequestLastFrameNumbersHolder(List<CaptureRequest> requestList, SubmitInfo requestInfo) {
        long lastRegularFrameNumber = -1;
        long lastReprocessFrameNumber = -1;
        long lastZslStillFrameNumber = -1;
        long frameNumber = requestInfo.getLastFrameNumber();
        int i = 1;
        if (requestInfo.getLastFrameNumber() < requestList.size() - 1) {
            throw new IllegalArgumentException("lastFrameNumber: " + requestInfo.getLastFrameNumber() + " should be at least " + (requestList.size() - 1) + " for the number of  requests in the list: " + requestList.size());
        }
        int i2 = requestList.size() - 1;
        while (i2 >= 0) {
            CaptureRequest request = requestList.get(i2);
            int requestType = request.getRequestType();
            if (requestType == i && lastReprocessFrameNumber == -1) {
                lastReprocessFrameNumber = frameNumber;
            } else if (requestType == 2 && lastZslStillFrameNumber == -1) {
                lastZslStillFrameNumber = frameNumber;
            } else if (requestType == 0 && lastRegularFrameNumber == -1) {
                lastRegularFrameNumber = frameNumber;
            }
            if (lastReprocessFrameNumber != -1 && lastZslStillFrameNumber != -1 && lastRegularFrameNumber != -1) {
                break;
            }
            frameNumber--;
            i2--;
            i = 1;
        }
        this.mLastRegularFrameNumber = lastRegularFrameNumber;
        this.mLastReprocessFrameNumber = lastReprocessFrameNumber;
        this.mLastZslStillFrameNumber = lastZslStillFrameNumber;
        this.mRequestId = requestInfo.getRequestId();
        this.mSequenceCompleted = false;
        this.mInflightCompleted = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestLastFrameNumbersHolder(int requestId, long lastFrameNumber, int[] repeatingRequestTypes) {
        long lastRegularFrameNumber = -1;
        long lastZslStillFrameNumber = -1;
        if (repeatingRequestTypes == null) {
            throw new IllegalArgumentException("repeatingRequest list must not be null");
        }
        if (lastFrameNumber < repeatingRequestTypes.length - 1) {
            throw new IllegalArgumentException("lastFrameNumber: " + lastFrameNumber + " should be at least " + (repeatingRequestTypes.length - 1) + " for the number of requests in the list: " + repeatingRequestTypes.length);
        }
        long frameNumber = lastFrameNumber;
        int i = repeatingRequestTypes.length;
        while (true) {
            i--;
            if (i >= 0) {
                if (repeatingRequestTypes[i] == 2 && lastZslStillFrameNumber == -1) {
                    lastZslStillFrameNumber = frameNumber;
                } else if (repeatingRequestTypes[i] == 0 && lastRegularFrameNumber == -1) {
                    lastRegularFrameNumber = frameNumber;
                }
                if (lastZslStillFrameNumber != -1 && lastRegularFrameNumber != -1) {
                    break;
                }
                frameNumber--;
            } else {
                break;
            }
        }
        this.mLastRegularFrameNumber = lastRegularFrameNumber;
        this.mLastZslStillFrameNumber = lastZslStillFrameNumber;
        this.mLastReprocessFrameNumber = -1L;
        this.mRequestId = requestId;
        this.mSequenceCompleted = false;
        this.mInflightCompleted = false;
    }

    public long getLastRegularFrameNumber() {
        return this.mLastRegularFrameNumber;
    }

    public long getLastReprocessFrameNumber() {
        return this.mLastReprocessFrameNumber;
    }

    public long getLastZslStillFrameNumber() {
        return this.mLastZslStillFrameNumber;
    }

    public long getLastFrameNumber() {
        return Math.max(this.mLastZslStillFrameNumber, Math.max(this.mLastRegularFrameNumber, this.mLastReprocessFrameNumber));
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public boolean isSequenceCompleted() {
        return this.mSequenceCompleted;
    }

    public void markSequenceCompleted() {
        this.mSequenceCompleted = true;
    }

    public boolean isInflightCompleted() {
        return this.mInflightCompleted;
    }

    public void markInflightCompleted() {
        this.mInflightCompleted = true;
    }
}
