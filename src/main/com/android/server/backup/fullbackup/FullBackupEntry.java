package com.android.server.backup.fullbackup;
/* loaded from: classes.dex */
public class FullBackupEntry implements Comparable<FullBackupEntry> {
    public long lastBackup;
    public String packageName;

    public FullBackupEntry(String str, long j) {
        this.packageName = str;
        this.lastBackup = j;
    }

    @Override // java.lang.Comparable
    public int compareTo(FullBackupEntry fullBackupEntry) {
        long j = this.lastBackup;
        long j2 = fullBackupEntry.lastBackup;
        if (j < j2) {
            return -1;
        }
        return j > j2 ? 1 : 0;
    }
}
