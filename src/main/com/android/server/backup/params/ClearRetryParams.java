package com.android.server.backup.params;
/* loaded from: classes.dex */
public class ClearRetryParams {
    public String packageName;
    public String transportName;

    public ClearRetryParams(String str, String str2) {
        this.transportName = str;
        this.packageName = str2;
    }
}
