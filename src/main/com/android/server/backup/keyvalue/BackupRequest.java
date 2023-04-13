package com.android.server.backup.keyvalue;

import java.util.Objects;
/* loaded from: classes.dex */
public class BackupRequest {
    public String packageName;

    public BackupRequest(String str) {
        this.packageName = str;
    }

    public String toString() {
        return "BackupRequest{pkg=" + this.packageName + "}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BackupRequest) {
            return Objects.equals(this.packageName, ((BackupRequest) obj).packageName);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.packageName);
    }
}
