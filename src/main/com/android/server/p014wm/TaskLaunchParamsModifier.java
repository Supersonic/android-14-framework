package com.android.server.p014wm;

import android.app.ActivityOptions;
import android.app.WindowConfiguration;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.util.Size;
import android.window.WindowContainerToken;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p014wm.ActivityStarter;
import com.android.server.p014wm.LaunchParamsController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* renamed from: com.android.server.wm.TaskLaunchParamsModifier */
/* loaded from: classes2.dex */
public class TaskLaunchParamsModifier implements LaunchParamsController.LaunchParamsModifier {
    public final ActivityTaskSupervisor mSupervisor;
    public TaskDisplayArea mTmpDisplayArea;
    public final Rect mTmpBounds = new Rect();
    public final Rect mTmpStableBounds = new Rect();
    public final int[] mTmpDirections = new int[2];

    public final void appendLog(String str) {
    }

    public final int convertOrientationToScreenOrientation(int i) {
        if (i != 1) {
            return i != 2 ? -1 : 0;
        }
        return 1;
    }

    public final void initLogBuilder(Task task, ActivityRecord activityRecord) {
    }

    public final void outputLog() {
    }

    public TaskLaunchParamsModifier(ActivityTaskSupervisor activityTaskSupervisor) {
        this.mSupervisor = activityTaskSupervisor;
    }

    @Override // com.android.server.p014wm.LaunchParamsController.LaunchParamsModifier
    public int onCalculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParamsController.LaunchParams launchParams, LaunchParamsController.LaunchParams launchParams2) {
        initLogBuilder(task, activityRecord);
        int calculate = calculate(task, windowLayout, activityRecord, activityRecord2, activityOptions, request, i, launchParams, launchParams2);
        outputLog();
        return calculate;
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x018d  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x0195 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0197  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x021d  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0251  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x00f4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0135  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int calculate(Task task, ActivityInfo.WindowLayout windowLayout, ActivityRecord activityRecord, ActivityRecord activityRecord2, ActivityOptions activityOptions, ActivityStarter.Request request, int i, LaunchParamsController.LaunchParams launchParams, LaunchParamsController.LaunchParams launchParams2) {
        ActivityRecord activityRecord3;
        boolean z;
        boolean z2;
        boolean z3;
        DisplayContent displayContent;
        TaskDisplayArea taskDisplayArea;
        int i2;
        int i3;
        boolean z4;
        int i4;
        int i5;
        TaskDisplayArea taskDisplayArea2;
        TaskDisplayArea taskDisplayArea3;
        if (task != null) {
            activityRecord3 = task.getRootActivity() == null ? activityRecord : task.getRootActivity();
        } else {
            activityRecord3 = activityRecord;
        }
        if (activityRecord3 == null) {
            return 0;
        }
        TaskDisplayArea preferredLaunchTaskDisplayArea = getPreferredLaunchTaskDisplayArea(task, activityOptions, activityRecord2, launchParams, activityRecord, request);
        launchParams2.mPreferredTaskDisplayArea = preferredLaunchTaskDisplayArea;
        DisplayContent displayContent2 = preferredLaunchTaskDisplayArea.mDisplayContent;
        if (i == 0) {
            return 2;
        }
        int launchWindowingMode = activityOptions != null ? activityOptions.getLaunchWindowingMode() : 0;
        if (launchWindowingMode == 0 && canInheritWindowingModeFromSource(displayContent2, preferredLaunchTaskDisplayArea, activityRecord2)) {
            launchWindowingMode = activityRecord2.getTask().getWindowingMode();
        }
        if (launchWindowingMode == 0 && task != null && task.getTaskDisplayArea() == preferredLaunchTaskDisplayArea && !task.getRootTask().mReparentLeafTaskIfRelaunch) {
            launchWindowingMode = task.getWindowingMode();
        }
        boolean canCalculateBoundsForFullscreenTask = canCalculateBoundsForFullscreenTask(preferredLaunchTaskDisplayArea, launchWindowingMode);
        boolean canApplyFreeformWindowPolicy = canApplyFreeformWindowPolicy(preferredLaunchTaskDisplayArea, launchWindowingMode);
        boolean z5 = windowLayout != null && (canApplyFreeformWindowPolicy || canCalculateBoundsForFullscreenTask);
        if (this.mSupervisor.canUseActivityOptionsLaunchBounds(activityOptions) && (canApplyFreeformWindowPolicy || canApplyPipWindowPolicy(launchWindowingMode) || canCalculateBoundsForFullscreenTask)) {
            if (launchWindowingMode == 0 && canApplyFreeformWindowPolicy) {
                launchWindowingMode = 5;
            }
            launchParams2.mBounds.set(activityOptions.getLaunchBounds());
        } else {
            if (z5) {
                this.mTmpBounds.set(launchParams.mBounds);
                getLayoutBounds(preferredLaunchTaskDisplayArea, activityRecord3, windowLayout, this.mTmpBounds);
                if (!this.mTmpBounds.isEmpty()) {
                    if (canApplyFreeformWindowPolicy) {
                        launchWindowingMode = 5;
                    }
                    launchParams2.mBounds.set(this.mTmpBounds);
                    z = true;
                    z2 = true;
                    if (launchParams.isEmpty() && !z && ((taskDisplayArea3 = launchParams.mPreferredTaskDisplayArea) == null || taskDisplayArea3.getDisplayId() == displayContent2.getDisplayId())) {
                        boolean z6 = launchParams.hasWindowingMode() && preferredLaunchTaskDisplayArea.inFreeformWindowingMode() && (launchWindowingMode = launchParams.mWindowingMode) != 5;
                        if (launchParams.mBounds.isEmpty()) {
                            z3 = z6;
                        } else {
                            launchParams2.mBounds.set(launchParams.mBounds);
                            z3 = true;
                        }
                    } else {
                        z3 = false;
                    }
                    if (preferredLaunchTaskDisplayArea.inFreeformWindowingMode() || launchWindowingMode == 2 || activityRecord3.isResizeable()) {
                        displayContent = displayContent2;
                        taskDisplayArea = preferredLaunchTaskDisplayArea;
                        i2 = 1;
                        i3 = launchWindowingMode;
                    } else if (shouldLaunchUnresizableAppInFreeform(activityRecord3, preferredLaunchTaskDisplayArea, activityOptions)) {
                        if (launchParams2.mBounds.isEmpty()) {
                            i2 = 1;
                            i3 = 5;
                            displayContent = displayContent2;
                            taskDisplayArea = preferredLaunchTaskDisplayArea;
                            getTaskBounds(activityRecord3, preferredLaunchTaskDisplayArea, windowLayout, 5, z, launchParams2.mBounds);
                            z4 = true;
                        } else {
                            displayContent = displayContent2;
                            taskDisplayArea = preferredLaunchTaskDisplayArea;
                            i2 = 1;
                            z4 = false;
                            i3 = 5;
                        }
                        launchParams2.mWindowingMode = i3 != taskDisplayArea.getWindowingMode() ? 0 : i3;
                        if (i != i2) {
                            return 2;
                        }
                        final int windowingMode = i3 != 0 ? i3 : taskDisplayArea.getWindowingMode();
                        if (activityOptions == null || (activityOptions.getLaunchTaskDisplayArea() == null && activityOptions.getLaunchTaskDisplayAreaFeatureId() == -1)) {
                            final int resolveActivityType = this.mSupervisor.mRootWindowContainer.resolveActivityType(activityRecord3, activityOptions, task);
                            displayContent.forAllTaskDisplayAreas(new Predicate() { // from class: com.android.server.wm.TaskLaunchParamsModifier$$ExternalSyntheticLambda0
                                @Override // java.util.function.Predicate
                                public final boolean test(Object obj) {
                                    boolean lambda$calculate$0;
                                    lambda$calculate$0 = TaskLaunchParamsModifier.this.lambda$calculate$0(windowingMode, resolveActivityType, (TaskDisplayArea) obj);
                                    return lambda$calculate$0;
                                }
                            });
                            TaskDisplayArea taskDisplayArea4 = this.mTmpDisplayArea;
                            if (taskDisplayArea4 != null) {
                                TaskDisplayArea taskDisplayArea5 = taskDisplayArea;
                                if (taskDisplayArea4 != taskDisplayArea5) {
                                    launchParams2.mWindowingMode = i3 == taskDisplayArea4.getWindowingMode() ? 0 : i3;
                                    if (z2) {
                                        launchParams2.mBounds.setEmpty();
                                        getLayoutBounds(this.mTmpDisplayArea, activityRecord3, windowLayout, launchParams2.mBounds);
                                        z = !launchParams2.mBounds.isEmpty();
                                    } else if (z4) {
                                        launchParams2.mBounds.setEmpty();
                                        i4 = windowingMode;
                                        taskDisplayArea = taskDisplayArea5;
                                        i5 = 2;
                                        getTaskBounds(activityRecord3, this.mTmpDisplayArea, windowLayout, i3, z, launchParams2.mBounds);
                                        taskDisplayArea2 = this.mTmpDisplayArea;
                                        if (taskDisplayArea2 == null) {
                                            this.mTmpDisplayArea = null;
                                            appendLog("overridden-display-area=[" + WindowConfiguration.activityTypeToString(resolveActivityType) + ", " + WindowConfiguration.windowingModeToString(i4) + ", " + taskDisplayArea2 + "]");
                                        } else {
                                            taskDisplayArea2 = taskDisplayArea;
                                        }
                                    }
                                }
                                i4 = windowingMode;
                                taskDisplayArea = taskDisplayArea5;
                            } else {
                                i4 = windowingMode;
                            }
                            i5 = 2;
                            taskDisplayArea2 = this.mTmpDisplayArea;
                            if (taskDisplayArea2 == null) {
                            }
                        } else {
                            taskDisplayArea2 = taskDisplayArea;
                            i4 = windowingMode;
                            i5 = 2;
                        }
                        appendLog("display-area=" + taskDisplayArea2);
                        launchParams2.mPreferredTaskDisplayArea = taskDisplayArea2;
                        if (i == i5) {
                            return i5;
                        }
                        if (!z3) {
                            int i6 = i4;
                            if (activityRecord2 != null && activityRecord2.inFreeformWindowingMode() && i6 == 5 && launchParams2.mBounds.isEmpty() && activityRecord2.getDisplayArea() == taskDisplayArea2) {
                                cascadeBounds(activityRecord2.getConfiguration().windowConfiguration.getBounds(), taskDisplayArea2, launchParams2.mBounds);
                            }
                            getTaskBounds(activityRecord3, taskDisplayArea2, windowLayout, i6, z, launchParams2.mBounds);
                        } else if (i4 == 5) {
                            if (launchParams.mPreferredTaskDisplayArea != taskDisplayArea2) {
                                adjustBoundsToFitInDisplayArea(taskDisplayArea2, windowLayout, launchParams2.mBounds);
                            }
                            adjustBoundsToAvoidConflictInDisplayArea(taskDisplayArea2, launchParams2.mBounds);
                        }
                        return i5;
                    } else {
                        displayContent = displayContent2;
                        taskDisplayArea = preferredLaunchTaskDisplayArea;
                        i2 = 1;
                        launchParams2.mBounds.setEmpty();
                        i3 = 1;
                    }
                    z4 = false;
                    launchParams2.mWindowingMode = i3 != taskDisplayArea.getWindowingMode() ? 0 : i3;
                    if (i != i2) {
                    }
                }
            } else if (launchWindowingMode == 6 && activityOptions != null && activityOptions.getLaunchBounds() != null) {
                launchParams2.mBounds.set(activityOptions.getLaunchBounds());
            }
            z = false;
            z2 = false;
            if (launchParams.isEmpty()) {
            }
            z3 = false;
            if (preferredLaunchTaskDisplayArea.inFreeformWindowingMode()) {
            }
            displayContent = displayContent2;
            taskDisplayArea = preferredLaunchTaskDisplayArea;
            i2 = 1;
            i3 = launchWindowingMode;
            z4 = false;
            launchParams2.mWindowingMode = i3 != taskDisplayArea.getWindowingMode() ? 0 : i3;
            if (i != i2) {
            }
        }
        z2 = false;
        z = true;
        if (launchParams.isEmpty()) {
        }
        z3 = false;
        if (preferredLaunchTaskDisplayArea.inFreeformWindowingMode()) {
        }
        displayContent = displayContent2;
        taskDisplayArea = preferredLaunchTaskDisplayArea;
        i2 = 1;
        i3 = launchWindowingMode;
        z4 = false;
        launchParams2.mWindowingMode = i3 != taskDisplayArea.getWindowingMode() ? 0 : i3;
        if (i != i2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$calculate$0(int i, int i2, TaskDisplayArea taskDisplayArea) {
        if (taskDisplayArea.getLaunchRootTask(i, i2, null, null, 0) == null) {
            return false;
        }
        this.mTmpDisplayArea = taskDisplayArea;
        return true;
    }

    public final TaskDisplayArea getPreferredLaunchTaskDisplayArea(Task task, ActivityOptions activityOptions, ActivityRecord activityRecord, LaunchParamsController.LaunchParams launchParams, ActivityRecord activityRecord2, ActivityStarter.Request request) {
        DisplayContent displayContent;
        final int launchTaskDisplayAreaFeatureId;
        Task task2 = null;
        WindowContainerToken launchTaskDisplayArea = activityOptions != null ? activityOptions.getLaunchTaskDisplayArea() : null;
        TaskDisplayArea taskDisplayArea = launchTaskDisplayArea != null ? (TaskDisplayArea) WindowContainer.fromBinder(launchTaskDisplayArea.asBinder()) : null;
        if (taskDisplayArea == null && activityOptions != null && (launchTaskDisplayAreaFeatureId = activityOptions.getLaunchTaskDisplayAreaFeatureId()) != -1) {
            DisplayContent displayContent2 = this.mSupervisor.mRootWindowContainer.getDisplayContent(activityOptions.getLaunchDisplayId() == -1 ? 0 : activityOptions.getLaunchDisplayId());
            if (displayContent2 != null) {
                taskDisplayArea = (TaskDisplayArea) displayContent2.getItemFromTaskDisplayAreas(new Function() { // from class: com.android.server.wm.TaskLaunchParamsModifier$$ExternalSyntheticLambda1
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        TaskDisplayArea lambda$getPreferredLaunchTaskDisplayArea$1;
                        lambda$getPreferredLaunchTaskDisplayArea$1 = TaskLaunchParamsModifier.lambda$getPreferredLaunchTaskDisplayArea$1(launchTaskDisplayAreaFeatureId, (TaskDisplayArea) obj);
                        return lambda$getPreferredLaunchTaskDisplayArea$1;
                    }
                });
            }
        }
        if (taskDisplayArea == null) {
            int launchDisplayId = activityOptions != null ? activityOptions.getLaunchDisplayId() : -1;
            if (launchDisplayId != -1 && (displayContent = this.mSupervisor.mRootWindowContainer.getDisplayContent(launchDisplayId)) != null) {
                taskDisplayArea = displayContent.getDefaultTaskDisplayArea();
            }
        }
        if (taskDisplayArea == null && activityRecord != null && activityRecord.noDisplay && (taskDisplayArea = activityRecord.mHandoverTaskDisplayArea) == null) {
            DisplayContent displayContent3 = this.mSupervisor.mRootWindowContainer.getDisplayContent(activityRecord.mHandoverLaunchDisplayId);
            if (displayContent3 != null) {
                taskDisplayArea = displayContent3.getDefaultTaskDisplayArea();
            }
        }
        if (taskDisplayArea == null && activityRecord != null) {
            taskDisplayArea = activityRecord.getDisplayArea();
        }
        if (taskDisplayArea == null && task != null) {
            task2 = task.getRootTask();
        }
        if (task2 != null) {
            taskDisplayArea = task2.getDisplayArea();
        }
        if (taskDisplayArea == null && activityOptions != null) {
            DisplayContent displayContent4 = this.mSupervisor.mRootWindowContainer.getDisplayContent(activityOptions.getCallerDisplayId());
            if (displayContent4 != null) {
                taskDisplayArea = displayContent4.getDefaultTaskDisplayArea();
            }
        }
        if (taskDisplayArea == null) {
            taskDisplayArea = launchParams.mPreferredTaskDisplayArea;
        }
        if (taskDisplayArea != null && !this.mSupervisor.mService.mSupportsMultiDisplay && taskDisplayArea.getDisplayId() != 0) {
            taskDisplayArea = this.mSupervisor.mRootWindowContainer.getDefaultTaskDisplayArea();
        }
        if (taskDisplayArea != null && activityRecord2.isActivityTypeHome() && !this.mSupervisor.mRootWindowContainer.canStartHomeOnDisplayArea(activityRecord2.info, taskDisplayArea, false)) {
            taskDisplayArea = this.mSupervisor.mRootWindowContainer.getDefaultTaskDisplayArea();
        }
        return taskDisplayArea != null ? taskDisplayArea : getFallbackDisplayAreaForActivity(activityRecord2, request);
    }

    public static /* synthetic */ TaskDisplayArea lambda$getPreferredLaunchTaskDisplayArea$1(int i, TaskDisplayArea taskDisplayArea) {
        if (taskDisplayArea.mFeatureId == i) {
            return taskDisplayArea;
        }
        return null;
    }

    public final TaskDisplayArea getFallbackDisplayAreaForActivity(ActivityRecord activityRecord, ActivityStarter.Request request) {
        WindowProcessController processController = this.mSupervisor.mService.getProcessController(activityRecord.launchedFromPid, activityRecord.launchedFromUid);
        TaskDisplayArea topActivityDisplayArea = processController == null ? null : processController.getTopActivityDisplayArea();
        if (topActivityDisplayArea != null) {
            return topActivityDisplayArea;
        }
        WindowProcessController processController2 = this.mSupervisor.mService.getProcessController(activityRecord.getProcessName(), activityRecord.getUid());
        TaskDisplayArea topActivityDisplayArea2 = processController2 == null ? null : processController2.getTopActivityDisplayArea();
        if (topActivityDisplayArea2 != null) {
            return topActivityDisplayArea2;
        }
        WindowProcessController processController3 = request == null ? null : this.mSupervisor.mService.getProcessController(request.realCallingPid, request.realCallingUid);
        TaskDisplayArea topActivityDisplayArea3 = processController3 != null ? processController3.getTopActivityDisplayArea() : null;
        return topActivityDisplayArea3 != null ? topActivityDisplayArea3 : this.mSupervisor.mRootWindowContainer.getDefaultTaskDisplayArea();
    }

    public final boolean canInheritWindowingModeFromSource(DisplayContent displayContent, TaskDisplayArea taskDisplayArea, ActivityRecord activityRecord) {
        if (activityRecord == null || taskDisplayArea.inFreeformWindowingMode()) {
            return false;
        }
        int windowingMode = activityRecord.getWindowingMode();
        return (windowingMode == 1 || windowingMode == 5) && displayContent.getDisplayId() == activityRecord.getDisplayId();
    }

    public final boolean canCalculateBoundsForFullscreenTask(TaskDisplayArea taskDisplayArea, int i) {
        return this.mSupervisor.mService.mSupportsFreeformWindowManagement && ((taskDisplayArea.getWindowingMode() == 1 && i == 0) || i == 1);
    }

    public final boolean canApplyFreeformWindowPolicy(TaskDisplayArea taskDisplayArea, int i) {
        return this.mSupervisor.mService.mSupportsFreeformWindowManagement && ((taskDisplayArea.inFreeformWindowingMode() && i == 0) || i == 5);
    }

    public final boolean canApplyPipWindowPolicy(int i) {
        return this.mSupervisor.mService.mSupportsPictureInPicture && i == 2;
    }

    public final void getLayoutBounds(TaskDisplayArea taskDisplayArea, ActivityRecord activityRecord, ActivityInfo.WindowLayout windowLayout, Rect rect) {
        int i;
        int i2;
        int i3 = windowLayout.gravity;
        int i4 = i3 & 112;
        int i5 = i3 & 7;
        if (!windowLayout.hasSpecifiedSize() && i4 == 0 && i5 == 0) {
            rect.setEmpty();
            return;
        }
        Rect rect2 = this.mTmpStableBounds;
        taskDisplayArea.getStableRect(rect2);
        int width = rect2.width();
        int height = rect2.height();
        float f = 1.0f;
        if (!windowLayout.hasSpecifiedSize()) {
            if (!rect.isEmpty()) {
                i = rect.width();
                i2 = rect.height();
            } else {
                getTaskBounds(activityRecord, taskDisplayArea, windowLayout, 5, false, rect);
                i = rect.width();
                i2 = rect.height();
            }
        } else {
            i = windowLayout.width;
            if (i <= 0 || i >= width) {
                float f2 = windowLayout.widthFraction;
                i = (f2 <= 0.0f || f2 >= 1.0f) ? width : (int) (width * f2);
            }
            i2 = windowLayout.height;
            if (i2 <= 0 || i2 >= height) {
                float f3 = windowLayout.heightFraction;
                i2 = (f3 <= 0.0f || f3 >= 1.0f) ? height : (int) (height * f3);
            }
        }
        float f4 = i5 != 3 ? i5 != 5 ? 0.5f : 1.0f : 0.0f;
        if (i4 == 48) {
            f = 0.0f;
        } else if (i4 != 80) {
            f = 0.5f;
        }
        rect.set(0, 0, i, i2);
        rect.offset(rect2.left, rect2.top);
        rect.offset((int) (f4 * (width - i)), (int) (f * (height - i2)));
    }

    public final boolean shouldLaunchUnresizableAppInFreeform(ActivityRecord activityRecord, TaskDisplayArea taskDisplayArea, ActivityOptions activityOptions) {
        if ((activityOptions == null || activityOptions.getLaunchWindowingMode() != 1) && activityRecord.supportsFreeformInDisplayArea(taskDisplayArea) && !activityRecord.isResizeable()) {
            int orientationFromBounds = orientationFromBounds(taskDisplayArea.getBounds());
            int resolveOrientation = resolveOrientation(activityRecord, taskDisplayArea, taskDisplayArea.getBounds());
            if (taskDisplayArea.getWindowingMode() == 5 && orientationFromBounds != resolveOrientation) {
                return true;
            }
        }
        return false;
    }

    public final int resolveOrientation(ActivityRecord activityRecord) {
        int i = activityRecord.info.screenOrientation;
        if (i != 0) {
            if (i == 1) {
                return 1;
            }
            if (i != 11) {
                if (i != 12) {
                    if (i != 14) {
                        switch (i) {
                            case 5:
                                break;
                            case 6:
                            case 8:
                                break;
                            case 7:
                            case 9:
                                return 1;
                            default:
                                return -1;
                        }
                    }
                    return 14;
                }
                return 1;
            }
        }
        return 0;
    }

    public final void cascadeBounds(Rect rect, TaskDisplayArea taskDisplayArea, Rect rect2) {
        rect2.set(rect);
        int i = (int) (((taskDisplayArea.getConfiguration().densityDpi / 160.0f) * 75.0f) + 0.5f);
        taskDisplayArea.getBounds(this.mTmpBounds);
        rect2.offset(Math.min(i, Math.max(0, this.mTmpBounds.right - rect.right)), Math.min(i, Math.max(0, this.mTmpBounds.bottom - rect.bottom)));
    }

    public final void getTaskBounds(ActivityRecord activityRecord, TaskDisplayArea taskDisplayArea, ActivityInfo.WindowLayout windowLayout, int i, boolean z, Rect rect) {
        if (i == 5 || i == 1) {
            int resolveOrientation = resolveOrientation(activityRecord, taskDisplayArea, rect);
            if (resolveOrientation != 1 && resolveOrientation != 0) {
                throw new IllegalStateException("Orientation must be one of portrait or landscape, but it's " + ActivityInfo.screenOrientationToString(resolveOrientation));
            }
            taskDisplayArea.getStableRect(this.mTmpStableBounds);
            Size defaultFreeformSize = LaunchParamsUtil.getDefaultFreeformSize(activityRecord, taskDisplayArea, windowLayout, resolveOrientation, this.mTmpStableBounds);
            this.mTmpBounds.set(0, 0, defaultFreeformSize.getWidth(), defaultFreeformSize.getHeight());
            if (z || sizeMatches(rect, this.mTmpBounds)) {
                if (resolveOrientation != orientationFromBounds(rect)) {
                    LaunchParamsUtil.centerBounds(taskDisplayArea, rect.height(), rect.width(), rect);
                }
            } else {
                adjustBoundsToFitInDisplayArea(taskDisplayArea, windowLayout, this.mTmpBounds);
                rect.setEmpty();
                LaunchParamsUtil.centerBounds(taskDisplayArea, this.mTmpBounds.width(), this.mTmpBounds.height(), rect);
            }
            adjustBoundsToAvoidConflictInDisplayArea(taskDisplayArea, rect);
        }
    }

    public final int resolveOrientation(ActivityRecord activityRecord, TaskDisplayArea taskDisplayArea, Rect rect) {
        int orientationFromBounds;
        int resolveOrientation = resolveOrientation(activityRecord);
        if (resolveOrientation == 14) {
            if (rect.isEmpty()) {
                orientationFromBounds = convertOrientationToScreenOrientation(taskDisplayArea.getConfiguration().orientation);
            } else {
                orientationFromBounds = orientationFromBounds(rect);
            }
            resolveOrientation = orientationFromBounds;
        }
        if (resolveOrientation == -1) {
            return rect.isEmpty() ? 1 : orientationFromBounds(rect);
        }
        return resolveOrientation;
    }

    public final void adjustBoundsToFitInDisplayArea(TaskDisplayArea taskDisplayArea, ActivityInfo.WindowLayout windowLayout, Rect rect) {
        LaunchParamsUtil.adjustBoundsToFitInDisplayArea(taskDisplayArea, this.mSupervisor.mRootWindowContainer.getConfiguration().getLayoutDirection(), windowLayout, rect);
    }

    public final void adjustBoundsToAvoidConflictInDisplayArea(TaskDisplayArea taskDisplayArea, Rect rect) {
        final ArrayList arrayList = new ArrayList();
        taskDisplayArea.forAllRootTasks(new Consumer() { // from class: com.android.server.wm.TaskLaunchParamsModifier$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TaskLaunchParamsModifier.lambda$adjustBoundsToAvoidConflictInDisplayArea$2(arrayList, (Task) obj);
            }
        }, false);
        adjustBoundsToAvoidConflict(taskDisplayArea.getBounds(), arrayList, rect);
    }

    public static /* synthetic */ void lambda$adjustBoundsToAvoidConflictInDisplayArea$2(List list, Task task) {
        if (task.inFreeformWindowingMode()) {
            for (int i = 0; i < task.getChildCount(); i++) {
                list.add(task.getChildAt(i).getBounds());
            }
        }
    }

    @VisibleForTesting
    public void adjustBoundsToAvoidConflict(Rect rect, List<Rect> list, Rect rect2) {
        int[] iArr;
        if (rect.contains(rect2) && boundsConflict(list, rect2)) {
            calculateCandidateShiftDirections(rect, rect2);
            for (int i : this.mTmpDirections) {
                if (i == 0) {
                    return;
                }
                this.mTmpBounds.set(rect2);
                while (boundsConflict(list, this.mTmpBounds) && rect.contains(this.mTmpBounds)) {
                    shiftBounds(i, rect, this.mTmpBounds);
                }
                if (!boundsConflict(list, this.mTmpBounds) && rect.contains(this.mTmpBounds)) {
                    rect2.set(this.mTmpBounds);
                    return;
                }
            }
        }
    }

    public final void calculateCandidateShiftDirections(Rect rect, Rect rect2) {
        int i = 0;
        while (true) {
            int[] iArr = this.mTmpDirections;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = 0;
            i++;
        }
        int i2 = rect.left;
        int i3 = rect.right;
        int i4 = ((i2 * 2) + i3) / 3;
        int i5 = (i2 + (i3 * 2)) / 3;
        int centerX = rect2.centerX();
        if (centerX < i4) {
            this.mTmpDirections[0] = 5;
        } else if (centerX > i5) {
            this.mTmpDirections[0] = 3;
        } else {
            int i6 = rect.top;
            int i7 = rect.bottom;
            int i8 = ((i6 * 2) + i7) / 3;
            int i9 = (i6 + (i7 * 2)) / 3;
            int centerY = rect2.centerY();
            if (centerY < i8 || centerY > i9) {
                int[] iArr2 = this.mTmpDirections;
                iArr2[0] = 5;
                iArr2[1] = 3;
                return;
            }
            int[] iArr3 = this.mTmpDirections;
            iArr3[0] = 85;
            iArr3[1] = 51;
        }
    }

    public final boolean boundsConflict(List<Rect> list, Rect rect) {
        Iterator<Rect> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                return false;
            }
            Rect next = it.next();
            boolean z = Math.abs(next.left - rect.left) < 4;
            boolean z2 = Math.abs(next.top - rect.top) < 4;
            boolean z3 = Math.abs(next.right - rect.right) < 4;
            boolean z4 = Math.abs(next.bottom - rect.bottom) < 4;
            if ((!z || !z2) && ((!z || !z4) && ((!z3 || !z2) && (!z3 || !z4)))) {
            }
        }
        return true;
    }

    public final void shiftBounds(int i, Rect rect, Rect rect2) {
        int i2;
        int i3 = i & 7;
        int i4 = 0;
        if (i3 == 3) {
            i2 = -Math.max(1, rect.width() / 16);
        } else {
            i2 = i3 != 5 ? 0 : Math.max(1, rect.width() / 16);
        }
        int i5 = i & 112;
        if (i5 == 48) {
            i4 = -Math.max(1, rect.height() / 16);
        } else if (i5 == 80) {
            i4 = Math.max(1, rect.height() / 16);
        }
        rect2.offset(i2, i4);
    }

    public static int orientationFromBounds(Rect rect) {
        return rect.width() > rect.height() ? 0 : 1;
    }

    public static boolean sizeMatches(Rect rect, Rect rect2) {
        return Math.abs(rect2.width() - rect.width()) < 2 && Math.abs(rect2.height() - rect.height()) < 2;
    }
}
