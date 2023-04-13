package com.android.server.policy;

import android.app.TaskInfo;
import android.content.Intent;
/* loaded from: classes2.dex */
public abstract class PermissionPolicyInternal {

    /* loaded from: classes2.dex */
    public interface OnInitializedCallback {
        void onInitialized(int i);
    }

    public abstract boolean checkStartActivity(Intent intent, int i, String str);

    public abstract boolean isInitialized(int i);

    public abstract boolean isIntentToPermissionDialog(Intent intent);

    public abstract void setOnInitializedCallback(OnInitializedCallback onInitializedCallback);

    public abstract boolean shouldShowNotificationDialogForTask(TaskInfo taskInfo, String str, String str2, Intent intent, String str3);

    public abstract void showNotificationPromptIfNeeded(String str, int i, int i2);
}
