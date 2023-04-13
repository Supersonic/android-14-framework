package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import com.android.server.p014wm.ActivityStarter;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.server.wm.LaunchParamsController */
/* loaded from: classes2.dex */
public class LaunchParamsController {
    public final LaunchParamsPersister mPersister;
    public final ActivityTaskManagerService mService;
    public final List<LaunchParamsModifier> mModifiers = new ArrayList();
    public final LaunchParams mTmpParams = new LaunchParams();
    public final LaunchParams mTmpCurrent = new LaunchParams();
    public final LaunchParams mTmpResult = new LaunchParams();

    /* renamed from: com.android.server.wm.LaunchParamsController$LaunchParamsModifier */
    /* loaded from: classes2.dex */
    public interface LaunchParamsModifier {
        int onCalculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParams launchParams, LaunchParams launchParams2);
    }

    public LaunchParamsController(ActivityTaskManagerService activityTaskManagerService, LaunchParamsPersister launchParamsPersister) {
        this.mService = activityTaskManagerService;
        this.mPersister = launchParamsPersister;
    }

    public void registerDefaultModifiers(ActivityTaskSupervisor activityTaskSupervisor) {
        registerModifier(new TaskLaunchParamsModifier(activityTaskSupervisor));
        if (DesktopModeLaunchParamsModifier.DESKTOP_MODE_SUPPORTED) {
            registerModifier(new DesktopModeLaunchParamsModifier());
        }
    }

    public void calculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParams launchParams) {
        launchParams.reset();
        if (task != null || activityRecord != null) {
            this.mPersister.getLaunchParams(task, activityRecord, launchParams);
        }
        for (int size = this.mModifiers.size() - 1; size >= 0; size--) {
            this.mTmpCurrent.set(launchParams);
            this.mTmpResult.reset();
            int onCalculate = this.mModifiers.get(size).onCalculate(task, windowLayout, activityRecord, activityRecord2, activityOptions, request, i, this.mTmpCurrent, this.mTmpResult);
            if (onCalculate == 1) {
                launchParams.set(this.mTmpResult);
                return;
            }
            if (onCalculate == 2) {
                launchParams.set(this.mTmpResult);
            }
        }
        if (activityRecord != null && activityRecord.requestedVrComponent != null) {
            launchParams.mPreferredTaskDisplayArea = this.mService.mRootWindowContainer.getDefaultTaskDisplayArea();
            return;
        }
        ActivityTaskManagerService activityTaskManagerService = this.mService;
        int i2 = activityTaskManagerService.mVr2dDisplayId;
        if (i2 != -1) {
            launchParams.mPreferredTaskDisplayArea = activityTaskManagerService.mRootWindowContainer.getDisplayContent(i2).getDefaultTaskDisplayArea();
        }
    }

    public boolean layoutTask(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions) {
        calculate(task, windowLayout, activityRecord, activityRecord2, activityOptions, null, 3, this.mTmpParams);
        if (this.mTmpParams.isEmpty()) {
            return false;
        }
        this.mService.deferWindowLayout();
        try {
            if (!this.mTmpParams.mBounds.isEmpty()) {
                if (task.getRootTask().inMultiWindowMode()) {
                    task.setBounds(this.mTmpParams.mBounds);
                    this.mService.continueWindowLayout();
                    return true;
                }
                task.setLastNonFullscreenBounds(this.mTmpParams.mBounds);
            }
            return false;
        } finally {
            this.mService.continueWindowLayout();
        }
    }

    public void registerModifier(LaunchParamsModifier launchParamsModifier) {
        if (this.mModifiers.contains(launchParamsModifier)) {
            return;
        }
        this.mModifiers.add(launchParamsModifier);
    }

    /* renamed from: com.android.server.wm.LaunchParamsController$LaunchParams */
    /* loaded from: classes2.dex */
    public static class LaunchParams {
        public final Rect mBounds = new Rect();
        public TaskDisplayArea mPreferredTaskDisplayArea;
        public int mWindowingMode;

        public void reset() {
            this.mBounds.setEmpty();
            this.mPreferredTaskDisplayArea = null;
            this.mWindowingMode = 0;
        }

        public void set(LaunchParams launchParams) {
            this.mBounds.set(launchParams.mBounds);
            this.mPreferredTaskDisplayArea = launchParams.mPreferredTaskDisplayArea;
            this.mWindowingMode = launchParams.mWindowingMode;
        }

        public boolean isEmpty() {
            return this.mBounds.isEmpty() && this.mPreferredTaskDisplayArea == null && this.mWindowingMode == 0;
        }

        public boolean hasWindowingMode() {
            return this.mWindowingMode != 0;
        }

        public boolean hasPreferredTaskDisplayArea() {
            return this.mPreferredTaskDisplayArea != null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            LaunchParams launchParams = (LaunchParams) obj;
            if (this.mPreferredTaskDisplayArea == launchParams.mPreferredTaskDisplayArea && this.mWindowingMode == launchParams.mWindowingMode) {
                Rect rect = this.mBounds;
                return rect != null ? rect.equals(launchParams.mBounds) : launchParams.mBounds == null;
            }
            return false;
        }

        public int hashCode() {
            Rect rect = this.mBounds;
            int hashCode = (rect != null ? rect.hashCode() : 0) * 31;
            TaskDisplayArea taskDisplayArea = this.mPreferredTaskDisplayArea;
            return ((hashCode + (taskDisplayArea != null ? taskDisplayArea.hashCode() : 0)) * 31) + this.mWindowingMode;
        }
    }
}
