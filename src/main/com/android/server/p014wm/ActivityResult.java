package com.android.server.p014wm;

import android.app.ResultInfo;
import android.content.Intent;
/* renamed from: com.android.server.wm.ActivityResult */
/* loaded from: classes2.dex */
public final class ActivityResult extends ResultInfo {
    public final ActivityRecord mFrom;

    public ActivityResult(ActivityRecord activityRecord, String str, int i, int i2, Intent intent) {
        super(str, i, i2, intent);
        this.mFrom = activityRecord;
    }
}
