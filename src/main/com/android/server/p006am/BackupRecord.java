package com.android.server.p006am;

import android.content.pm.ApplicationInfo;
/* renamed from: com.android.server.am.BackupRecord */
/* loaded from: classes.dex */
public final class BackupRecord {
    public ProcessRecord app;
    public final ApplicationInfo appInfo;
    public final int backupDestination;
    public final int backupMode;
    public String stringName;
    public final int userId;

    public BackupRecord(ApplicationInfo applicationInfo, int i, int i2, int i3) {
        this.appInfo = applicationInfo;
        this.backupMode = i;
        this.userId = i2;
        this.backupDestination = i3;
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackupRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.appInfo.packageName);
        sb.append(' ');
        sb.append(this.appInfo.name);
        sb.append(' ');
        sb.append(this.appInfo.backupAgentName);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}
