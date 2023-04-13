package com.android.server.backup;
/* loaded from: classes.dex */
public class JobIdManager {
    public static int getJobIdForUserId(int i, int i2, int i3) {
        int i4 = i + i3;
        if (i4 <= i2) {
            return i4;
        }
        throw new RuntimeException("No job IDs available in the given range");
    }
}
