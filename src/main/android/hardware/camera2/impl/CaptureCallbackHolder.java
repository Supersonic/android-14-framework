package android.hardware.camera2.impl;

import android.hardware.camera2.CaptureRequest;
import android.view.Surface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CaptureCallbackHolder {
    private final CaptureCallback mCallback;
    private final Executor mExecutor;
    private final boolean mHasBatchedOutputs;
    private final boolean mRepeating;
    private final List<CaptureRequest> mRequestList;
    private final int mSessionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaptureCallbackHolder(CaptureCallback callback, List<CaptureRequest> requestList, Executor executor, boolean repeating, int sessionId) {
        if (callback == null || executor == null) {
            throw new UnsupportedOperationException("Must have a valid handler and a valid callback");
        }
        this.mRepeating = repeating;
        this.mExecutor = executor;
        this.mRequestList = new ArrayList(requestList);
        this.mCallback = callback;
        this.mSessionId = sessionId;
        boolean hasBatchedOutputs = true;
        int i = 0;
        while (true) {
            if (i >= requestList.size()) {
                break;
            }
            CaptureRequest request = requestList.get(i);
            if (!request.isPartOfCRequestList()) {
                hasBatchedOutputs = false;
                break;
            }
            if (i == 0) {
                Collection<Surface> targets = request.getTargets();
                if (targets.size() != 2) {
                    hasBatchedOutputs = false;
                    break;
                }
            }
            i++;
        }
        this.mHasBatchedOutputs = hasBatchedOutputs;
    }

    public boolean isRepeating() {
        return this.mRepeating;
    }

    public CaptureCallback getCallback() {
        return this.mCallback;
    }

    public CaptureRequest getRequest(int subsequenceId) {
        if (subsequenceId >= this.mRequestList.size()) {
            throw new IllegalArgumentException(String.format("Requested subsequenceId %d is larger than request list size %d.", Integer.valueOf(subsequenceId), Integer.valueOf(this.mRequestList.size())));
        }
        if (subsequenceId < 0) {
            throw new IllegalArgumentException(String.format("Requested subsequenceId %d is negative", Integer.valueOf(subsequenceId)));
        }
        return this.mRequestList.get(subsequenceId);
    }

    public CaptureRequest getRequest() {
        return getRequest(0);
    }

    public Executor getExecutor() {
        return this.mExecutor;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public int getRequestCount() {
        return this.mRequestList.size();
    }

    public boolean hasBatchedOutputs() {
        return this.mHasBatchedOutputs;
    }
}
