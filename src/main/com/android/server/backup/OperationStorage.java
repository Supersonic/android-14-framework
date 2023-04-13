package com.android.server.backup;

import java.util.Set;
/* loaded from: classes.dex */
public interface OperationStorage {
    void registerOperation(int i, int i2, BackupRestoreTask backupRestoreTask, int i3);

    void registerOperationForPackages(int i, int i2, Set<String> set, BackupRestoreTask backupRestoreTask, int i3);

    void removeOperation(int i);
}
