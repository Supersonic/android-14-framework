package com.android.service.ims.presence;
/* loaded from: classes.dex */
public class PresencePublishTask extends PresenceTask {
    private long mCreateTimestamp;
    private int mRetryCount;

    public PresencePublishTask(int taskId, int cmdId, ContactCapabilityResponse listener, String[] contacts) {
        super(taskId, cmdId, listener, contacts);
        this.mCreateTimestamp = 0L;
        this.mRetryCount = 0;
        this.mCreateTimestamp = System.currentTimeMillis();
    }

    public long getCreateTimestamp() {
        return this.mCreateTimestamp;
    }

    public int getRetryCount() {
        return this.mRetryCount;
    }

    public void setRetryCount(int retryCount) {
        this.mRetryCount = retryCount;
    }

    @Override // com.android.service.ims.presence.PresenceTask, com.android.service.ims.Task
    public String toString() {
        return super.toString() + " mCreateTimestamp=" + this.mCreateTimestamp + " mRetryCount=" + this.mRetryCount;
    }
}
