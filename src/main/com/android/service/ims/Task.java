package com.android.service.ims;

import com.android.ims.internal.Logger;
import com.android.service.ims.presence.ContactCapabilityResponse;
/* loaded from: classes.dex */
public class Task {
    private Logger logger = Logger.getLogger(getClass().getName());
    public int mCmdId;
    public int mCmdStatus;
    public ContactCapabilityResponse mListener;
    public String mSipReasonPhrase;
    public int mSipRequestId;
    public int mSipResponseCode;
    public int mTaskId;

    public Task(int taskId, int cmdId, ContactCapabilityResponse listener) {
        this.mTaskId = taskId;
        this.mCmdId = cmdId;
        this.mListener = listener;
    }

    public String toString() {
        return "Task: mTaskId=" + this.mTaskId + " mCmdId=" + this.mCmdId + " mCmdStatus=" + this.mCmdStatus + " mSipRequestId=" + this.mSipRequestId + " mSipResponseCode=" + this.mSipResponseCode + " mSipReasonPhrase=" + this.mSipReasonPhrase;
    }
}
