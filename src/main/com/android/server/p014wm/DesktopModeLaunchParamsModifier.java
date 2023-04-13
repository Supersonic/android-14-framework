package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.SystemProperties;
import com.android.server.p014wm.ActivityStarter;
import com.android.server.p014wm.LaunchParamsController;
/* renamed from: com.android.server.wm.DesktopModeLaunchParamsModifier */
/* loaded from: classes2.dex */
public class DesktopModeLaunchParamsModifier implements LaunchParamsController.LaunchParamsModifier {
    public static final boolean DESKTOP_MODE_SUPPORTED = SystemProperties.getBoolean("persist.wm.debug.desktop_mode", false);
    public static final int DESKTOP_MODE_DEFAULT_WIDTH_DP = SystemProperties.getInt("persist.wm.debug.desktop_mode.default_width", 840);
    public static final int DESKTOP_MODE_DEFAULT_HEIGHT_DP = SystemProperties.getInt("persist.wm.debug.desktop_mode.default_height", 630);

    public final void appendLog(String str, Object... objArr) {
    }

    public final void initLogBuilder(Task task, ActivityRecord activityRecord) {
    }

    public final void outputLog() {
    }

    @Override // com.android.server.p014wm.LaunchParamsController.LaunchParamsModifier
    public int onCalculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParamsController.LaunchParams launchParams, LaunchParamsController.LaunchParams launchParams2) {
        initLogBuilder(task, activityRecord);
        int calculate = calculate(task, windowLayout, activityRecord, activityRecord2, activityOptions, request, i, launchParams, launchParams2);
        outputLog();
        return calculate;
    }

    public final int calculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParamsController.LaunchParams launchParams, LaunchParamsController.LaunchParams launchParams2) {
        if (task == null) {
            appendLog("task null, skipping", new Object[0]);
            return 0;
        } else if (i != 3) {
            appendLog("not in bounds phase, skipping", new Object[0]);
            return 0;
        } else if (!task.inFreeformWindowingMode()) {
            appendLog("not a freeform task, skipping", new Object[0]);
            return 0;
        } else if (!launchParams.mBounds.isEmpty()) {
            appendLog("currentParams has bounds set, not overriding", new Object[0]);
            return 0;
        } else {
            launchParams2.set(launchParams);
            float f = task.getConfiguration().densityDpi / 160.0f;
            Rect rect = launchParams2.mBounds;
            rect.right = (int) ((DESKTOP_MODE_DEFAULT_WIDTH_DP * f) + 0.5f);
            rect.bottom = (int) ((DESKTOP_MODE_DEFAULT_HEIGHT_DP * f) + 0.5f);
            Rect bounds = task.getWindowConfiguration().getBounds();
            launchParams2.mBounds.offset(bounds.centerX() - launchParams2.mBounds.centerX(), bounds.centerY() - launchParams2.mBounds.centerY());
            appendLog("setting desktop mode task bounds to %s", launchParams2.mBounds);
            return 1;
        }
    }
}
