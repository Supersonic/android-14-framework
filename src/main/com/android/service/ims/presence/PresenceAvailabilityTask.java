package com.android.service.ims.presence;

import com.android.ims.internal.Logger;
/* loaded from: classes.dex */
public class PresenceAvailabilityTask extends PresenceTask {
    private Logger logger;
    private long mCreateTimestamp;
    private long mNotifyTimeStamp;

    public PresenceAvailabilityTask(int taskId, int cmdId, ContactCapabilityResponse listener, String[] contacts) {
        super(taskId, cmdId, listener, contacts);
        this.logger = Logger.getLogger(getClass().getName());
        this.mCreateTimestamp = 0L;
        this.mNotifyTimeStamp = 0L;
        this.mCreateTimestamp = System.currentTimeMillis();
        this.mNotifyTimeStamp = 0L;
    }

    public void updateNotifyTimestamp() {
        this.mNotifyTimeStamp = System.currentTimeMillis();
        this.logger.debug("updateNotifyTimestamp mNotifyTimeStamp=" + this.mNotifyTimeStamp);
    }

    public long getNotifyTimestamp() {
        return this.mNotifyTimeStamp;
    }

    public long getCreateTimestamp() {
        return this.mCreateTimestamp;
    }

    @Override // com.android.service.ims.presence.PresenceTask, com.android.service.ims.Task
    public String toString() {
        return super.toString() + " mNotifyTimeStamp=" + this.mNotifyTimeStamp;
    }
}
