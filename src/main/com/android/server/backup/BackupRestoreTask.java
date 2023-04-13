package com.android.server.backup;
/* loaded from: classes.dex */
public interface BackupRestoreTask {
    void execute();

    void handleCancel(boolean z);

    void operationComplete(long j);
}
