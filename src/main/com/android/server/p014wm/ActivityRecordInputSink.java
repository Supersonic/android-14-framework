package com.android.server.p014wm;

import android.app.compat.CompatChanges;
import android.view.InputApplicationHandle;
import android.view.InputWindowHandle;
import android.view.SurfaceControl;
/* renamed from: com.android.server.wm.ActivityRecordInputSink */
/* loaded from: classes2.dex */
public class ActivityRecordInputSink {
    public final ActivityRecord mActivityRecord;
    public InputWindowHandleWrapper mInputWindowHandleWrapper;
    public final boolean mIsCompatEnabled;
    public final String mName;
    public SurfaceControl mSurfaceControl;

    public ActivityRecordInputSink(ActivityRecord activityRecord, ActivityRecord activityRecord2) {
        this.mActivityRecord = activityRecord;
        this.mIsCompatEnabled = CompatChanges.isChangeEnabled(194480991L, activityRecord.getUid());
        this.mName = Integer.toHexString(System.identityHashCode(this)) + " ActivityRecordInputSink " + activityRecord.mActivityComponent.flattenToShortString();
        if (activityRecord2 != null) {
            activityRecord2.mAllowedTouchUid = activityRecord.getUid();
        }
    }

    public void applyChangesToSurfaceIfChanged(SurfaceControl.Transaction transaction) {
        InputWindowHandleWrapper inputWindowHandleWrapper = getInputWindowHandleWrapper();
        if (this.mSurfaceControl == null) {
            this.mSurfaceControl = createSurface(transaction);
        }
        if (inputWindowHandleWrapper.isChanged()) {
            inputWindowHandleWrapper.applyChangesToSurface(transaction, this.mSurfaceControl);
        }
    }

    public final SurfaceControl createSurface(SurfaceControl.Transaction transaction) {
        SurfaceControl build = this.mActivityRecord.makeChildSurface(null).setName(this.mName).setHidden(false).setCallsite("ActivityRecordInputSink.createSurface").build();
        transaction.setLayer(build, Integer.MIN_VALUE);
        return build;
    }

    public final InputWindowHandleWrapper getInputWindowHandleWrapper() {
        if (this.mInputWindowHandleWrapper == null) {
            this.mInputWindowHandleWrapper = new InputWindowHandleWrapper(createInputWindowHandle());
        }
        ActivityRecord activityBelow = this.mActivityRecord.getTask() != null ? this.mActivityRecord.getTask().getActivityBelow(this.mActivityRecord) : null;
        if ((activityBelow != null && (activityBelow.mAllowedTouchUid == this.mActivityRecord.getUid() || activityBelow.isUid(this.mActivityRecord.getUid()))) || !this.mIsCompatEnabled || this.mActivityRecord.isInTransition()) {
            this.mInputWindowHandleWrapper.setInputConfigMasked(8, 8);
        } else {
            this.mInputWindowHandleWrapper.setInputConfigMasked(0, 8);
        }
        return this.mInputWindowHandleWrapper;
    }

    public final InputWindowHandle createInputWindowHandle() {
        InputWindowHandle inputWindowHandle = new InputWindowHandle((InputApplicationHandle) null, this.mActivityRecord.getDisplayId());
        inputWindowHandle.replaceTouchableRegionWithCrop = true;
        inputWindowHandle.name = this.mName;
        inputWindowHandle.layoutParamsType = 2022;
        inputWindowHandle.ownerPid = WindowManagerService.MY_PID;
        inputWindowHandle.ownerUid = WindowManagerService.MY_UID;
        inputWindowHandle.inputConfig = 5;
        return inputWindowHandle;
    }

    public void releaseSurfaceControl() {
        SurfaceControl surfaceControl = this.mSurfaceControl;
        if (surfaceControl != null) {
            surfaceControl.release();
            this.mSurfaceControl = null;
        }
    }
}
