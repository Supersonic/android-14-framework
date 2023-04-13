package com.android.server.p014wm;

import android.os.IBinder;
/* renamed from: com.android.server.wm.ActivityAssistInfo */
/* loaded from: classes2.dex */
public class ActivityAssistInfo {
    public final IBinder mActivityToken;
    public final IBinder mAssistToken;
    public final int mTaskId;

    public ActivityAssistInfo(ActivityRecord activityRecord) {
        this.mActivityToken = activityRecord.token;
        this.mAssistToken = activityRecord.assistToken;
        this.mTaskId = activityRecord.getTask().mTaskId;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public IBinder getAssistToken() {
        return this.mAssistToken;
    }

    public int getTaskId() {
        return this.mTaskId;
    }
}
