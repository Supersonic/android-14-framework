package com.android.server.p014wm;

import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.ArraySet;
import android.util.Slog;
import android.window.DisplayWindowPolicyController;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;
/* renamed from: com.android.server.wm.DisplayWindowPolicyControllerHelper */
/* loaded from: classes2.dex */
public class DisplayWindowPolicyControllerHelper {
    public final DisplayContent mDisplayContent;
    public DisplayWindowPolicyController mDisplayWindowPolicyController;
    public ActivityRecord mTopRunningActivity = null;
    public ArraySet<Integer> mRunningUid = new ArraySet<>();

    public DisplayWindowPolicyControllerHelper(DisplayContent displayContent) {
        this.mDisplayContent = displayContent;
        this.mDisplayWindowPolicyController = displayContent.mWmService.mDisplayManagerInternal.getDisplayWindowPolicyController(displayContent.mDisplayId);
    }

    public boolean canContainActivities(List<ActivityInfo> list, @WindowConfiguration.WindowingMode int i) {
        DisplayWindowPolicyController displayWindowPolicyController = this.mDisplayWindowPolicyController;
        if (displayWindowPolicyController == null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                String str = list.get(size).requiredDisplayCategory;
                if (str != null) {
                    Slog.e("DisplayWindowPolicyControllerHelper", String.format("Activity with requiredDisplayCategory='%s' cannot be displayed on display %d because that display does not have a matching category", str, Integer.valueOf(this.mDisplayContent.mDisplayId)));
                    return false;
                }
            }
            return true;
        }
        return displayWindowPolicyController.canContainActivities(list, i);
    }

    public boolean canActivityBeLaunched(ActivityInfo activityInfo, Intent intent, @WindowConfiguration.WindowingMode int i, int i2, boolean z) {
        DisplayWindowPolicyController displayWindowPolicyController = this.mDisplayWindowPolicyController;
        if (displayWindowPolicyController == null) {
            String str = activityInfo.requiredDisplayCategory;
            if (str != null) {
                Slog.e("DisplayWindowPolicyControllerHelper", String.format("Activity with requiredDisplayCategory='%s' cannot be launched on display %d because that display does not have a matching category", str, Integer.valueOf(this.mDisplayContent.mDisplayId)));
                return false;
            }
            return true;
        }
        return displayWindowPolicyController.canActivityBeLaunched(activityInfo, intent, i, i2, z);
    }

    public boolean keepActivityOnWindowFlagsChanged(ActivityInfo activityInfo, int i, int i2) {
        DisplayWindowPolicyController displayWindowPolicyController = this.mDisplayWindowPolicyController;
        if (displayWindowPolicyController != null && displayWindowPolicyController.isInterestedWindowFlags(i, i2)) {
            return this.mDisplayWindowPolicyController.keepActivityOnWindowFlagsChanged(activityInfo, i, i2);
        }
        return true;
    }

    public void onRunningActivityChanged() {
        if (this.mDisplayWindowPolicyController == null) {
            return;
        }
        ActivityRecord topActivity = this.mDisplayContent.getTopActivity(false, true);
        if (topActivity != this.mTopRunningActivity) {
            this.mTopRunningActivity = topActivity;
            if (topActivity == null) {
                this.mDisplayWindowPolicyController.onTopActivityChanged((ComponentName) null, -1, -10000);
            } else {
                this.mDisplayWindowPolicyController.onTopActivityChanged(topActivity.info.getComponentName(), topActivity.info.applicationInfo.uid, topActivity.mUserId);
            }
        }
        final boolean[] zArr = {false};
        final ArraySet<Integer> arraySet = new ArraySet<>();
        this.mDisplayContent.forAllActivities(new Consumer() { // from class: com.android.server.wm.DisplayWindowPolicyControllerHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DisplayWindowPolicyControllerHelper.lambda$onRunningActivityChanged$0(zArr, arraySet, (ActivityRecord) obj);
            }
        });
        if (zArr[0] || this.mRunningUid.size() != arraySet.size()) {
            this.mRunningUid = arraySet;
            this.mDisplayWindowPolicyController.onRunningAppsChanged(arraySet);
        }
    }

    public static /* synthetic */ void lambda$onRunningActivityChanged$0(boolean[] zArr, ArraySet arraySet, ActivityRecord activityRecord) {
        if (activityRecord.finishing) {
            return;
        }
        zArr[0] = arraySet.add(Integer.valueOf(activityRecord.getUid())) | zArr[0];
    }

    public final boolean canShowTasksInHostDeviceRecents() {
        DisplayWindowPolicyController displayWindowPolicyController = this.mDisplayWindowPolicyController;
        if (displayWindowPolicyController == null) {
            return true;
        }
        return displayWindowPolicyController.canShowTasksInHostDeviceRecents();
    }

    public final boolean isEnteringPipAllowed(int i) {
        DisplayWindowPolicyController displayWindowPolicyController = this.mDisplayWindowPolicyController;
        if (displayWindowPolicyController == null) {
            return true;
        }
        return displayWindowPolicyController.isEnteringPipAllowed(i);
    }

    public void dump(String str, PrintWriter printWriter) {
        if (this.mDisplayWindowPolicyController != null) {
            printWriter.println();
            this.mDisplayWindowPolicyController.dump(str, printWriter);
        }
    }
}
