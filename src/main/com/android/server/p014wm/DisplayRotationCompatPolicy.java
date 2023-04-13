package com.android.server.p014wm;

import android.app.servertransaction.ClientTransaction;
import android.app.servertransaction.RefreshCallbackItem;
import android.app.servertransaction.ResumeActivityItem;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.UiThread;
import java.util.Map;
import java.util.Set;
/* renamed from: com.android.server.wm.DisplayRotationCompatPolicy */
/* loaded from: classes2.dex */
public final class DisplayRotationCompatPolicy {
    public final CameraManager.AvailabilityCallback mAvailabilityCallback;
    @GuardedBy({"this"})
    public final CameraIdPackageNameBiMap mCameraIdPackageBiMap;
    public final CameraManager mCameraManager;
    public final DisplayContent mDisplayContent;
    public final Handler mHandler;
    public int mLastReportedOrientation;
    @GuardedBy({"this"})
    public final Set<String> mScheduledOrientationUpdateCameraIdSet;
    @GuardedBy({"this"})
    public final Set<String> mScheduledToBeRemovedCameraIdSet;
    public final WindowManagerService mWmService;

    public DisplayRotationCompatPolicy(DisplayContent displayContent) {
        this(displayContent, displayContent.mWmService.f1164mH);
    }

    @VisibleForTesting
    public DisplayRotationCompatPolicy(DisplayContent displayContent, Handler handler) {
        this.mCameraIdPackageBiMap = new CameraIdPackageNameBiMap();
        this.mScheduledToBeRemovedCameraIdSet = new ArraySet();
        this.mScheduledOrientationUpdateCameraIdSet = new ArraySet();
        CameraManager.AvailabilityCallback availabilityCallback = new CameraManager.AvailabilityCallback() { // from class: com.android.server.wm.DisplayRotationCompatPolicy.1
            public void onCameraOpened(String str, String str2) {
                DisplayRotationCompatPolicy.this.notifyCameraOpened(str, str2);
            }

            public void onCameraClosed(String str) {
                DisplayRotationCompatPolicy.this.notifyCameraClosed(str);
            }
        };
        this.mAvailabilityCallback = availabilityCallback;
        this.mLastReportedOrientation = -2;
        this.mHandler = handler;
        this.mDisplayContent = displayContent;
        WindowManagerService windowManagerService = displayContent.mWmService;
        this.mWmService = windowManagerService;
        CameraManager cameraManager = (CameraManager) windowManagerService.mContext.getSystemService(CameraManager.class);
        this.mCameraManager = cameraManager;
        cameraManager.registerAvailabilityCallback(windowManagerService.mContext.getMainExecutor(), availabilityCallback);
    }

    public void dispose() {
        this.mCameraManager.unregisterAvailabilityCallback(this.mAvailabilityCallback);
    }

    public int getOrientation() {
        int orientationInternal = getOrientationInternal();
        this.mLastReportedOrientation = orientationInternal;
        return orientationInternal;
    }

    public final synchronized int getOrientationInternal() {
        if (isTreatmentEnabledForDisplay()) {
            ActivityRecord activityRecord = this.mDisplayContent.topRunningActivity(true);
            if (isTreatmentEnabledForActivity(activityRecord)) {
                boolean z = activityRecord.getRequestedConfigurationOrientation() == 1;
                boolean z2 = this.mDisplayContent.getNaturalOrientation() == 1;
                int i = (!(z && z2) && (z || z2)) ? 0 : 1;
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -1812743677, 241, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.mDisplayId), String.valueOf(ActivityInfo.screenOrientationToString(i)), Boolean.valueOf(z), Boolean.valueOf(z2)});
                }
                return i;
            }
            return -1;
        }
        return -1;
    }

    public void onActivityConfigurationChanging(final ActivityRecord activityRecord, Configuration configuration, Configuration configuration2) {
        if (isTreatmentEnabledForDisplay() && this.mWmService.mLetterboxConfiguration.isCameraCompatRefreshEnabled() && shouldRefreshActivity(activityRecord, configuration, configuration2)) {
            boolean z = this.mWmService.mLetterboxConfiguration.isCameraCompatRefreshCycleThroughStopEnabled() && !activityRecord.mLetterboxUiController.shouldRefreshActivityViaPauseForCameraCompat();
            try {
                activityRecord.mLetterboxUiController.setIsRefreshAfterRotationRequested(true);
                if (ProtoLogCache.WM_DEBUG_STATES_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_STATES, 1967643923, 0, (String) null, new Object[]{String.valueOf(activityRecord)});
                }
                ClientTransaction obtain = ClientTransaction.obtain(activityRecord.app.getThread(), activityRecord.token);
                obtain.addCallback(RefreshCallbackItem.obtain(z ? 5 : 4));
                obtain.setLifecycleStateRequest(ResumeActivityItem.obtain(false, false));
                activityRecord.mAtmService.getLifecycleManager().scheduleTransaction(obtain);
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.wm.DisplayRotationCompatPolicy$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DisplayRotationCompatPolicy.this.lambda$onActivityConfigurationChanging$0(activityRecord);
                    }
                }, 2000L);
            } catch (RemoteException unused) {
                activityRecord.mLetterboxUiController.setIsRefreshAfterRotationRequested(false);
            }
        }
    }

    /* renamed from: onActivityRefreshed */
    public void lambda$onActivityConfigurationChanging$0(ActivityRecord activityRecord) {
        activityRecord.mLetterboxUiController.setIsRefreshAfterRotationRequested(false);
    }

    public void onScreenRotationAnimationFinished() {
        if (isTreatmentEnabledForDisplay() && !this.mCameraIdPackageBiMap.isEmpty() && isTreatmentEnabledForActivity(this.mDisplayContent.topRunningActivity(true))) {
            showToast(17040138);
        }
    }

    public String getSummaryForDisplayRotationHistoryRecord() {
        String str;
        if (isTreatmentEnabledForDisplay()) {
            ActivityRecord activityRecord = this.mDisplayContent.topRunningActivity(true);
            StringBuilder sb = new StringBuilder();
            sb.append(" mLastReportedOrientation=");
            sb.append(ActivityInfo.screenOrientationToString(this.mLastReportedOrientation));
            sb.append(" topActivity=");
            sb.append(activityRecord == null ? "null" : activityRecord.shortComponentName);
            sb.append(" isTreatmentEnabledForActivity=");
            sb.append(isTreatmentEnabledForActivity(activityRecord));
            sb.append(" CameraIdPackageNameBiMap=");
            sb.append(this.mCameraIdPackageBiMap.getSummaryForDisplayRotationHistoryRecord());
            str = sb.toString();
        } else {
            str = "";
        }
        return "DisplayRotationCompatPolicy{ isTreatmentEnabledForDisplay=" + isTreatmentEnabledForDisplay() + str + " }";
    }

    public final boolean shouldRefreshActivity(ActivityRecord activityRecord, Configuration configuration, Configuration configuration2) {
        return configuration.windowConfiguration.getDisplayRotation() != configuration2.windowConfiguration.getDisplayRotation() && isTreatmentEnabledForActivity(activityRecord) && activityRecord.mLetterboxUiController.shouldRefreshActivityForCameraCompat();
    }

    public final boolean isTreatmentEnabledForDisplay() {
        return this.mWmService.mLetterboxConfiguration.isCameraCompatTreatmentEnabled(true) && this.mDisplayContent.getIgnoreOrientationRequest() && this.mDisplayContent.getDisplay().getType() == 1;
    }

    public boolean isActivityEligibleForOrientationOverride(ActivityRecord activityRecord) {
        return isTreatmentEnabledForDisplay() && isCameraActive(activityRecord, true);
    }

    public boolean isTreatmentEnabledForActivity(ActivityRecord activityRecord) {
        return isTreatmentEnabledForActivity(activityRecord, true);
    }

    public final boolean isTreatmentEnabledForActivity(ActivityRecord activityRecord, boolean z) {
        return (activityRecord == null || !isCameraActive(activityRecord, z) || activityRecord.getRequestedConfigurationOrientation() == 0 || activityRecord.getOverrideOrientation() == 5 || activityRecord.getOverrideOrientation() == 14) ? false : true;
    }

    public final boolean isCameraActive(ActivityRecord activityRecord, boolean z) {
        return !(z && activityRecord.inMultiWindowMode()) && this.mCameraIdPackageBiMap.containsPackageName(activityRecord.packageName) && activityRecord.mLetterboxUiController.shouldForceRotateForCameraCompat();
    }

    public final synchronized void notifyCameraOpened(final String str, final String str2) {
        this.mScheduledToBeRemovedCameraIdSet.remove(str);
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -627759820, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.mDisplayId), String.valueOf(str), String.valueOf(str2)});
        }
        this.mScheduledOrientationUpdateCameraIdSet.add(str);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.wm.DisplayRotationCompatPolicy$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DisplayRotationCompatPolicy.this.lambda$notifyCameraOpened$1(str, str2);
            }
        }, 1000L);
    }

    /* renamed from: delayedUpdateOrientationWithWmLock */
    public final void lambda$notifyCameraOpened$1(String str, String str2) {
        synchronized (this) {
            if (this.mScheduledOrientationUpdateCameraIdSet.remove(str)) {
                this.mCameraIdPackageBiMap.put(str2, str);
                synchronized (this.mWmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityRecord activityRecord = this.mDisplayContent.topRunningActivity(true);
                        if (activityRecord != null && activityRecord.getTask() != null) {
                            if (activityRecord.getWindowingMode() == 1) {
                                if (activityRecord.mLetterboxUiController.isOverrideOrientationOnlyForCameraEnabled()) {
                                    activityRecord.recomputeConfiguration();
                                }
                                this.mDisplayContent.updateOrientation();
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                            if (activityRecord.getTask().getWindowingMode() == 6 && isTreatmentEnabledForActivity(activityRecord, false)) {
                                showToast(17040139);
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public void showToast(final int i) {
        UiThread.getHandler().post(new Runnable() { // from class: com.android.server.wm.DisplayRotationCompatPolicy$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayRotationCompatPolicy.this.lambda$showToast$2(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showToast$2(int i) {
        Toast.makeText(this.mWmService.mContext, i, 1).show();
    }

    public final synchronized void notifyCameraClosed(String str) {
        if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -81260230, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.mDisplayId), String.valueOf(str)});
        }
        this.mScheduledToBeRemovedCameraIdSet.add(str);
        this.mScheduledOrientationUpdateCameraIdSet.remove(str);
        scheduleRemoveCameraId(str);
    }

    public final void scheduleRemoveCameraId(final String str) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.wm.DisplayRotationCompatPolicy$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DisplayRotationCompatPolicy.this.lambda$scheduleRemoveCameraId$3(str);
            }
        }, 2000L);
    }

    /* renamed from: removeCameraId */
    public final void lambda$scheduleRemoveCameraId$3(String str) {
        synchronized (this) {
            if (this.mScheduledToBeRemovedCameraIdSet.remove(str)) {
                if (isActivityForCameraIdRefreshing(str)) {
                    if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                        ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -1631991057, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.mDisplayId), String.valueOf(str)});
                    }
                    this.mScheduledToBeRemovedCameraIdSet.add(str);
                    scheduleRemoveCameraId(str);
                    return;
                }
                this.mCameraIdPackageBiMap.removeCameraId(str);
                if (ProtoLogCache.WM_DEBUG_ORIENTATION_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_ORIENTATION, -799396645, 1, (String) null, new Object[]{Long.valueOf(this.mDisplayContent.mDisplayId), String.valueOf(str)});
                }
                synchronized (this.mWmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        ActivityRecord activityRecord = this.mDisplayContent.topRunningActivity(true);
                        if (activityRecord != null && activityRecord.getWindowingMode() == 1) {
                            if (activityRecord.mLetterboxUiController.isOverrideOrientationOnlyForCameraEnabled()) {
                                activityRecord.recomputeConfiguration();
                            }
                            this.mDisplayContent.updateOrientation();
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }
    }

    public final boolean isActivityForCameraIdRefreshing(String str) {
        String cameraId;
        ActivityRecord activityRecord = this.mDisplayContent.topRunningActivity(true);
        if (isTreatmentEnabledForActivity(activityRecord) && (cameraId = this.mCameraIdPackageBiMap.getCameraId(activityRecord.packageName)) != null && cameraId == str) {
            return activityRecord.mLetterboxUiController.isRefreshAfterRotationRequested();
        }
        return false;
    }

    /* renamed from: com.android.server.wm.DisplayRotationCompatPolicy$CameraIdPackageNameBiMap */
    /* loaded from: classes2.dex */
    public static class CameraIdPackageNameBiMap {
        public final Map<String, String> mCameraIdToPackageMap;
        public final Map<String, String> mPackageToCameraIdMap;

        public CameraIdPackageNameBiMap() {
            this.mPackageToCameraIdMap = new ArrayMap();
            this.mCameraIdToPackageMap = new ArrayMap();
        }

        public boolean isEmpty() {
            return this.mCameraIdToPackageMap.isEmpty();
        }

        public void put(String str, String str2) {
            removePackageName(str);
            removeCameraId(str2);
            this.mPackageToCameraIdMap.put(str, str2);
            this.mCameraIdToPackageMap.put(str2, str);
        }

        public boolean containsPackageName(String str) {
            return this.mPackageToCameraIdMap.containsKey(str);
        }

        public String getCameraId(String str) {
            return this.mPackageToCameraIdMap.get(str);
        }

        public void removeCameraId(String str) {
            String str2 = this.mCameraIdToPackageMap.get(str);
            if (str2 == null) {
                return;
            }
            this.mPackageToCameraIdMap.remove(str2, str);
            this.mCameraIdToPackageMap.remove(str, str2);
        }

        public String getSummaryForDisplayRotationHistoryRecord() {
            return "{ mPackageToCameraIdMap=" + this.mPackageToCameraIdMap + " }";
        }

        public final void removePackageName(String str) {
            String str2 = this.mPackageToCameraIdMap.get(str);
            if (str2 == null) {
                return;
            }
            this.mPackageToCameraIdMap.remove(str, str2);
            this.mCameraIdToPackageMap.remove(str2, str);
        }
    }
}
