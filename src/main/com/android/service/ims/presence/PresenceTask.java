package com.android.service.ims.presence;

import com.android.service.ims.Task;
/* loaded from: classes.dex */
public class PresenceTask extends Task {
    public String[] mContacts;

    public PresenceTask(int taskId, int cmdId, ContactCapabilityResponse listener, String[] contacts) {
        super(taskId, cmdId, listener);
        this.mContacts = contacts;
        this.mListener = listener;
    }

    @Override // com.android.service.ims.Task
    public String toString() {
        return "PresenceTask: mTaskId=" + this.mTaskId + " mCmdId=" + this.mCmdId + " mContacts=" + PresenceUtils.toContactString(this.mContacts) + " mCmdStatus=" + this.mCmdStatus + " mSipRequestId=" + this.mSipRequestId + " mSipResponseCode=" + this.mSipResponseCode + " mSipReasonPhrase=" + this.mSipReasonPhrase;
    }
}
