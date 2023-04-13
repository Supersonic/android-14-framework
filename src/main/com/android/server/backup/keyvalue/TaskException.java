package com.android.server.backup.keyvalue;

import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
class TaskException extends BackupException {
    private final boolean mStateCompromised;
    private final int mStatus;

    public static TaskException stateCompromised() {
        return new TaskException(true, -1000);
    }

    public static TaskException stateCompromised(Exception exc) {
        if (exc instanceof TaskException) {
            return new TaskException(exc, true, ((TaskException) exc).getStatus());
        }
        return new TaskException(exc, true, -1000);
    }

    public static TaskException forStatus(int i) {
        Preconditions.checkArgument(i != 0, "Exception based on TRANSPORT_OK");
        return new TaskException(false, i);
    }

    public static TaskException causedBy(Exception exc) {
        if (exc instanceof TaskException) {
            return (TaskException) exc;
        }
        return new TaskException(exc, false, -1000);
    }

    public static TaskException create() {
        return new TaskException(false, -1000);
    }

    public TaskException(Exception exc, boolean z, int i) {
        super(exc);
        this.mStateCompromised = z;
        this.mStatus = i;
    }

    public TaskException(boolean z, int i) {
        this.mStateCompromised = z;
        this.mStatus = i;
    }

    public boolean isStateCompromised() {
        return this.mStateCompromised;
    }

    public int getStatus() {
        return this.mStatus;
    }
}
