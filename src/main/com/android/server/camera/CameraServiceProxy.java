package com.android.server.camera;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.admin.DevicePolicyManager;
import android.app.compat.CompatChanges;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.CameraSessionStats;
import android.hardware.CameraStreamStats;
import android.hardware.ICameraService;
import android.hardware.ICameraServiceProxy;
import android.hardware.devicestate.DeviceStateManager;
import android.hardware.display.DisplayManager;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.nfc.IAppCallback;
import android.nfc.INfcAdapter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.stats.camera.nano.CameraProtos;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.IDisplayWindowListener;
import android.view.WindowManagerGlobal;
import com.android.framework.protobuf.nano.MessageNano;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class CameraServiceProxy extends SystemService implements Handler.Callback, IBinder.DeathRecipient {
    public static final IBinder nfcInterfaceToken = new Binder();
    public final ArrayMap<String, CameraUsageEvent> mActiveCameraUsage;
    public final ICameraServiceProxy.Stub mCameraServiceProxy;
    public ICameraService mCameraServiceRaw;
    public final List<CameraUsageEvent> mCameraUsageHistory;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public int mDeviceState;
    public final DisplayWindowListener mDisplayWindowListener;
    public Set<Integer> mEnabledCameraUsers;
    public final DeviceStateManager.FoldStateListener mFoldStateListener;
    public final Handler mHandler;
    public final ServiceThread mHandlerThread;
    public final BroadcastReceiver mIntentReceiver;
    @GuardedBy({"mLock"})
    public int mLastReportedDeviceState;
    public int mLastUser;
    public final Object mLock;
    public ScheduledThreadPoolExecutor mLogWriterService;
    public final boolean mNotifyNfc;
    public UserManager mUserManager;

    /* loaded from: classes.dex */
    public static final class TaskInfo {
        public int displayId;
        public int frontTaskId;
        public boolean isFixedOrientationLandscape;
        public boolean isFixedOrientationPortrait;
        public boolean isResizeable;
        public int userId;
    }

    public static String cameraFacingToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "CAMERA_FACING_UNKNOWN" : "CAMERA_FACING_EXTERNAL" : "CAMERA_FACING_FRONT" : "CAMERA_FACING_BACK";
    }

    public static String cameraStateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "CAMERA_STATE_UNKNOWN" : "CAMERA_STATE_CLOSED" : "CAMERA_STATE_IDLE" : "CAMERA_STATE_ACTIVE" : "CAMERA_STATE_OPEN";
    }

    /* loaded from: classes.dex */
    public static class CameraUsageEvent {
        public final int mAPILevel;
        public final int mAction;
        public final int mCameraFacing;
        public final String mCameraId;
        public final String mClientName;
        public boolean mDeviceError;
        public int mInternalReconfigure;
        public final boolean mIsNdk;
        public final int mLatencyMs;
        public final int mOperatingMode;
        public long mRequestCount;
        public long mResultErrorCount;
        public List<CameraStreamStats> mStreamStats;
        public String mUserTag;
        public int mVideoStabilizationMode;
        public long mDurationOrStartTimeMs = SystemClock.elapsedRealtime();
        public boolean mCompleted = false;

        public CameraUsageEvent(String str, int i, String str2, int i2, boolean z, int i3, int i4, int i5, boolean z2) {
            this.mCameraId = str;
            this.mCameraFacing = i;
            this.mClientName = str2;
            this.mAPILevel = i2;
            this.mIsNdk = z;
            this.mAction = i3;
            this.mLatencyMs = i4;
            this.mOperatingMode = i5;
            this.mDeviceError = z2;
        }

        public void markCompleted(int i, long j, long j2, boolean z, List<CameraStreamStats> list, String str, int i2) {
            if (this.mCompleted) {
                return;
            }
            this.mCompleted = true;
            this.mDurationOrStartTimeMs = SystemClock.elapsedRealtime() - this.mDurationOrStartTimeMs;
            this.mInternalReconfigure = i;
            this.mRequestCount = j;
            this.mResultErrorCount = j2;
            this.mDeviceError = z;
            this.mStreamStats = list;
            this.mUserTag = str;
            this.mVideoStabilizationMode = i2;
        }

        public long getDuration() {
            if (this.mCompleted) {
                return this.mDurationOrStartTimeMs;
            }
            return 0L;
        }
    }

    /* loaded from: classes.dex */
    public final class DisplayWindowListener extends IDisplayWindowListener.Stub {
        public void onDisplayAdded(int i) {
        }

        public void onDisplayRemoved(int i) {
        }

        public void onFixedRotationFinished(int i) {
        }

        public void onFixedRotationStarted(int i, int i2) {
        }

        public void onKeepClearAreasChanged(int i, List<Rect> list, List<Rect> list2) {
        }

        public DisplayWindowListener() {
        }

        public void onDisplayConfigurationChanged(int i, Configuration configuration) {
            ICameraService cameraServiceRawLocked = CameraServiceProxy.this.getCameraServiceRawLocked();
            if (cameraServiceRawLocked == null) {
                return;
            }
            try {
                cameraServiceRawLocked.notifyDisplayConfigurationChange();
            } catch (RemoteException e) {
                Slog.w("CameraService_proxy", "Could not notify cameraserver, remote exception: " + e);
            }
        }
    }

    public static boolean isMOrBelow(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0).applicationInfo.targetSdkVersion <= 23;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.e("CameraService_proxy", "Package name not found!");
            return false;
        }
    }

    public static int getCropRotateScale(Context context, String str, TaskInfo taskInfo, int i, int i2, boolean z) {
        int i3;
        if (taskInfo == null) {
            return 0;
        }
        if (context.getResources().getBoolean(17891711)) {
            Slog.v("CameraService_proxy", "Disable Rotate and Crop to avoid conflicts with WM force rotation treatment.");
            return 0;
        } else if (i2 != 0 && i2 != 1) {
            Log.v("CameraService_proxy", "lensFacing=" + i2 + ". Crop-rotate-scale is disabled.");
            return 0;
        } else if (!z && !isMOrBelow(context, str) && taskInfo.isResizeable) {
            Slog.v("CameraService_proxy", "The activity is N or above and claims to support resizeable-activity. Crop-rotate-scale is disabled.");
            return 0;
        } else if (!taskInfo.isFixedOrientationPortrait && !taskInfo.isFixedOrientationLandscape) {
            Log.v("CameraService_proxy", "Non-fixed orientation activity. Crop-rotate-scale is disabled.");
            return 0;
        } else {
            if (i == 0) {
                i3 = 0;
            } else if (i == 1) {
                i3 = 90;
            } else if (i == 2) {
                i3 = 180;
            } else if (i != 3) {
                Log.e("CameraService_proxy", "Unsupported display rotation: " + i);
                return 0;
            } else {
                i3 = 270;
            }
            Slog.v("CameraService_proxy", "Display.getRotation()=" + i3 + " isFixedOrientationPortrait=" + taskInfo.isFixedOrientationPortrait + " isFixedOrientationLandscape=" + taskInfo.isFixedOrientationLandscape);
            if (i3 == 0) {
                return 0;
            }
            if (i2 == 0) {
                i3 = 360 - i3;
            }
            if (i3 != 90) {
                if (i3 != 180) {
                    return i3 != 270 ? 0 : 3;
                }
                return 2;
            }
            return 1;
        }
    }

    public CameraServiceProxy(Context context) {
        super(context);
        this.mLock = new Object();
        this.mActiveCameraUsage = new ArrayMap<>();
        this.mCameraUsageHistory = new ArrayList();
        this.mLogWriterService = new ScheduledThreadPoolExecutor(1);
        this.mDisplayWindowListener = new DisplayWindowListener();
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.camera.CameraServiceProxy.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                char c = 65535;
                switch (action.hashCode()) {
                    case -2114103349:
                        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -2061058799:
                        if (action.equals("android.intent.action.USER_REMOVED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1608292967:
                        if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -385593787:
                        if (action.equals("android.intent.action.MANAGED_PROFILE_ADDED")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -201513518:
                        if (action.equals("android.intent.action.USER_INFO_CHANGED")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 1051477093:
                        if (action.equals("android.intent.action.MANAGED_PROFILE_REMOVED")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 1121780209:
                        if (action.equals("android.intent.action.USER_ADDED")) {
                            c = 6;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 2:
                        synchronized (CameraServiceProxy.this.mLock) {
                            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra("device", UsbDevice.class);
                            if (usbDevice != null) {
                                CameraServiceProxy.this.notifyUsbDeviceHotplugLocked(usbDevice, action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED"));
                            }
                        }
                        return;
                    case 1:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        synchronized (CameraServiceProxy.this.mLock) {
                            if (CameraServiceProxy.this.mEnabledCameraUsers == null) {
                                return;
                            }
                            CameraServiceProxy cameraServiceProxy = CameraServiceProxy.this;
                            cameraServiceProxy.switchUserLocked(cameraServiceProxy.mLastUser);
                            return;
                        }
                    default:
                        return;
                }
            }
        };
        this.mCameraServiceProxy = new ICameraServiceProxy.Stub() { // from class: com.android.server.camera.CameraServiceProxy.2
            public int getAutoframingOverride(String str) {
                return 0;
            }

            public int getRotateAndCropOverride(String str, int i, int i2) {
                TaskInfo taskInfo;
                boolean z;
                if (Binder.getCallingUid() != 1047) {
                    Slog.e("CameraService_proxy", "Calling UID: " + Binder.getCallingUid() + " doesn't match expected  camera service UID!");
                    return 0;
                }
                try {
                    ParceledListSlice recentTasks = ActivityTaskManager.getService().getRecentTasks(2, 0, i2);
                    if (recentTasks != null && !recentTasks.getList().isEmpty()) {
                        Iterator it = recentTasks.getList().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                taskInfo = null;
                                break;
                            }
                            ActivityManager.RecentTaskInfo recentTaskInfo = (ActivityManager.RecentTaskInfo) it.next();
                            if (str.equals(recentTaskInfo.topActivityInfo.packageName)) {
                                taskInfo = new TaskInfo();
                                taskInfo.frontTaskId = recentTaskInfo.taskId;
                                ActivityInfo activityInfo = recentTaskInfo.topActivityInfo;
                                taskInfo.isResizeable = activityInfo.resizeMode != 0;
                                taskInfo.displayId = recentTaskInfo.displayId;
                                taskInfo.userId = recentTaskInfo.userId;
                                taskInfo.isFixedOrientationLandscape = ActivityInfo.isFixedOrientationLandscape(activityInfo.screenOrientation);
                                taskInfo.isFixedOrientationPortrait = ActivityInfo.isFixedOrientationPortrait(recentTaskInfo.topActivityInfo.screenOrientation);
                            }
                        }
                        TaskInfo taskInfo2 = taskInfo;
                        if (taskInfo2 == null) {
                            Log.e("CameraService_proxy", "Recent tasks don't include camera client package name: " + str);
                            return 0;
                        } else if (CompatChanges.isChangeEnabled(189229956L, str, UserHandle.getUserHandleForUid(taskInfo2.userId))) {
                            Slog.v("CameraService_proxy", "OVERRIDE_CAMERA_ROTATE_AND_CROP_DEFAULTS enabled!");
                            return 0;
                        } else {
                            if (CompatChanges.isChangeEnabled(191513214L, str, UserHandle.getUserHandleForUid(taskInfo2.userId))) {
                                Slog.v("CameraService_proxy", "OVERRIDE_CAMERA_RESIZABLE_AND_SDK_CHECK enabled!");
                                z = true;
                            } else {
                                z = false;
                            }
                            DisplayManager displayManager = (DisplayManager) CameraServiceProxy.this.mContext.getSystemService(DisplayManager.class);
                            if (displayManager != null) {
                                Display display = displayManager.getDisplay(taskInfo2.displayId);
                                if (display == null) {
                                    Slog.e("CameraService_proxy", "Invalid display id: " + taskInfo2.displayId);
                                    return 0;
                                }
                                return CameraServiceProxy.getCropRotateScale(CameraServiceProxy.this.mContext, str, taskInfo2, display.getRotation(), i, z);
                            }
                            Slog.e("CameraService_proxy", "Failed to query display manager!");
                            return 0;
                        }
                    }
                    Log.e("CameraService_proxy", "Recent task list is empty!");
                    return 0;
                } catch (RemoteException unused) {
                    Log.e("CameraService_proxy", "Failed to query recent tasks!");
                    return 0;
                }
            }

            public void pingForUserUpdate() {
                if (Binder.getCallingUid() != 1047) {
                    Slog.e("CameraService_proxy", "Calling UID: " + Binder.getCallingUid() + " doesn't match expected  camera service UID!");
                    return;
                }
                CameraServiceProxy.this.notifySwitchWithRetries(60);
                CameraServiceProxy.this.notifyDeviceStateWithRetries(60);
            }

            public void notifyCameraState(CameraSessionStats cameraSessionStats) {
                if (Binder.getCallingUid() != 1047) {
                    Slog.e("CameraService_proxy", "Calling UID: " + Binder.getCallingUid() + " doesn't match expected  camera service UID!");
                    return;
                }
                CameraServiceProxy.cameraStateToString(cameraSessionStats.getNewCameraState());
                CameraServiceProxy.cameraFacingToString(cameraSessionStats.getFacing());
                CameraServiceProxy.this.updateActivityCount(cameraSessionStats);
            }

            public boolean isCameraDisabled(int i) {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) CameraServiceProxy.this.mContext.getSystemService(DevicePolicyManager.class);
                if (devicePolicyManager == null) {
                    Slog.e("CameraService_proxy", "Failed to get the device policy manager service");
                    return false;
                }
                try {
                    return devicePolicyManager.getCameraDisabled(null, i);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        this.mContext = context;
        ServiceThread serviceThread = new ServiceThread("CameraService_proxy", -4, false);
        this.mHandlerThread = serviceThread;
        serviceThread.start();
        this.mHandler = new Handler(serviceThread.getLooper(), this);
        this.mNotifyNfc = SystemProperties.getInt("ro.camera.notify_nfc", 0) > 0;
        this.mLogWriterService.setKeepAliveTime(1L, TimeUnit.SECONDS);
        this.mLogWriterService.allowCoreThreadTimeOut(true);
        this.mFoldStateListener = new DeviceStateManager.FoldStateListener(context, new Consumer() { // from class: com.android.server.camera.CameraServiceProxy$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CameraServiceProxy.this.lambda$new$0((Boolean) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Boolean bool) {
        if (bool.booleanValue()) {
            setDeviceStateFlags(4);
        } else {
            clearDeviceStateFlags(4);
        }
    }

    public final void setDeviceStateFlags(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2);
            int i2 = i | this.mDeviceState;
            this.mDeviceState = i2;
            if (i2 != this.mLastReportedDeviceState) {
                notifyDeviceStateWithRetriesLocked(60);
            }
        }
    }

    public final void clearDeviceStateFlags(int i) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2);
            int i2 = (~i) & this.mDeviceState;
            this.mDeviceState = i2;
            if (i2 != this.mLastReportedDeviceState) {
                notifyDeviceStateWithRetriesLocked(60);
            }
        }
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            notifySwitchWithRetries(message.arg1);
        } else if (i == 2) {
            notifyDeviceStateWithRetries(message.arg1);
        } else {
            Slog.e("CameraService_proxy", "CameraServiceProxy error, invalid message: " + message.what);
        }
        return true;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        UserManager userManager = UserManager.get(this.mContext);
        this.mUserManager = userManager;
        if (userManager == null) {
            throw new IllegalStateException("UserManagerService must start before CameraServiceProxy!");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.mContext.registerReceiver(this.mIntentReceiver, intentFilter);
        publishBinderService("media.camera.proxy", this.mCameraServiceProxy);
        publishLocalService(CameraServiceProxy.class, this);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 1000) {
            CameraStatsJobService.schedule(this.mContext);
            try {
                for (int i2 : WindowManagerGlobal.getWindowManagerService().registerDisplayWindowListener(this.mDisplayWindowListener)) {
                    this.mDisplayWindowListener.onDisplayAdded(i2);
                }
            } catch (RemoteException unused) {
                Log.e("CameraService_proxy", "Failed to register display window listener!");
            }
            ((DeviceStateManager) this.mContext.getSystemService(DeviceStateManager.class)).registerCallback(new HandlerExecutor(this.mHandler), this.mFoldStateListener);
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStarting(SystemService.TargetUser targetUser) {
        synchronized (this.mLock) {
            if (this.mEnabledCameraUsers == null) {
                switchUserLocked(targetUser.getUserIdentifier());
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        synchronized (this.mLock) {
            switchUserLocked(targetUser2.getUserIdentifier());
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mLock) {
            this.mCameraServiceRaw = null;
            boolean isEmpty = this.mActiveCameraUsage.isEmpty();
            this.mActiveCameraUsage.clear();
            if (this.mNotifyNfc && !isEmpty) {
                notifyNfcService(true);
            }
        }
    }

    /* loaded from: classes.dex */
    public class EventWriterTask implements Runnable {
        public ArrayList<CameraUsageEvent> mEventList;

        public EventWriterTask(ArrayList<CameraUsageEvent> arrayList) {
            this.mEventList = arrayList;
        }

        @Override // java.lang.Runnable
        public void run() {
            ArrayList<CameraUsageEvent> arrayList = this.mEventList;
            if (arrayList != null) {
                Iterator<CameraUsageEvent> it = arrayList.iterator();
                while (it.hasNext()) {
                    logCameraUsageEvent(it.next());
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException unused) {
                    }
                }
                this.mEventList.clear();
            }
        }

        public final void logCameraUsageEvent(CameraUsageEvent cameraUsageEvent) {
            int i;
            int i2 = cameraUsageEvent.mCameraFacing;
            if (i2 == 0) {
                i = 1;
            } else if (i2 == 1) {
                i = 2;
            } else if (i2 != 2) {
                Slog.w("CameraService_proxy", "Unknown camera facing: " + cameraUsageEvent.mCameraFacing);
                i = 0;
            } else {
                i = 3;
            }
            List<CameraStreamStats> list = cameraUsageEvent.mStreamStats;
            int size = list != null ? list.size() : 0;
            CameraProtos.CameraStreamProto[] cameraStreamProtoArr = new CameraProtos.CameraStreamProto[5];
            for (int i3 = 0; i3 < 5; i3++) {
                cameraStreamProtoArr[i3] = new CameraProtos.CameraStreamProto();
                if (i3 < size) {
                    CameraStreamStats cameraStreamStats = cameraUsageEvent.mStreamStats.get(i3);
                    cameraStreamProtoArr[i3].width = cameraStreamStats.getWidth();
                    cameraStreamProtoArr[i3].height = cameraStreamStats.getHeight();
                    cameraStreamProtoArr[i3].format = cameraStreamStats.getFormat();
                    cameraStreamProtoArr[i3].dataSpace = cameraStreamStats.getDataSpace();
                    cameraStreamProtoArr[i3].usage = cameraStreamStats.getUsage();
                    cameraStreamProtoArr[i3].requestCount = cameraStreamStats.getRequestCount();
                    cameraStreamProtoArr[i3].errorCount = cameraStreamStats.getErrorCount();
                    cameraStreamProtoArr[i3].firstCaptureLatencyMillis = cameraStreamStats.getStartLatencyMs();
                    cameraStreamProtoArr[i3].maxHalBuffers = cameraStreamStats.getMaxHalBuffers();
                    cameraStreamProtoArr[i3].maxAppBuffers = cameraStreamStats.getMaxAppBuffers();
                    cameraStreamProtoArr[i3].histogramType = cameraStreamStats.getHistogramType();
                    cameraStreamProtoArr[i3].histogramBins = cameraStreamStats.getHistogramBins();
                    cameraStreamProtoArr[i3].histogramCounts = cameraStreamStats.getHistogramCounts();
                    cameraStreamProtoArr[i3].dynamicRangeProfile = cameraStreamStats.getDynamicRangeProfile();
                    cameraStreamProtoArr[i3].streamUseCase = cameraStreamStats.getStreamUseCase();
                    cameraStreamProtoArr[i3].colorSpace = cameraStreamStats.getColorSpace();
                }
            }
            FrameworkStatsLog.write(FrameworkStatsLog.CAMERA_ACTION_EVENT, cameraUsageEvent.getDuration(), cameraUsageEvent.mAPILevel, cameraUsageEvent.mClientName, i, cameraUsageEvent.mCameraId, cameraUsageEvent.mAction, cameraUsageEvent.mIsNdk, cameraUsageEvent.mLatencyMs, cameraUsageEvent.mOperatingMode, cameraUsageEvent.mInternalReconfigure, cameraUsageEvent.mRequestCount, cameraUsageEvent.mResultErrorCount, cameraUsageEvent.mDeviceError, size, MessageNano.toByteArray(cameraStreamProtoArr[0]), MessageNano.toByteArray(cameraStreamProtoArr[1]), MessageNano.toByteArray(cameraStreamProtoArr[2]), MessageNano.toByteArray(cameraStreamProtoArr[3]), MessageNano.toByteArray(cameraStreamProtoArr[4]), cameraUsageEvent.mUserTag, cameraUsageEvent.mVideoStabilizationMode);
        }
    }

    public void dumpUsageEvents() {
        synchronized (this.mLock) {
            Collections.shuffle(this.mCameraUsageHistory);
            this.mLogWriterService.execute(new EventWriterTask(new ArrayList(this.mCameraUsageHistory)));
            this.mCameraUsageHistory.clear();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            CameraStatsJobService.schedule(this.mContext);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final ICameraService getCameraServiceRawLocked() {
        if (this.mCameraServiceRaw == null) {
            IBinder binderService = getBinderService("media.camera");
            if (binderService == null) {
                return null;
            }
            try {
                binderService.linkToDeath(this, 0);
                this.mCameraServiceRaw = ICameraService.Stub.asInterface(binderService);
            } catch (RemoteException unused) {
                Slog.w("CameraService_proxy", "Could not link to death of native camera service");
                return null;
            }
        }
        return this.mCameraServiceRaw;
    }

    public final void switchUserLocked(int i) {
        Set<Integer> enabledUserHandles = getEnabledUserHandles(i);
        this.mLastUser = i;
        Set<Integer> set = this.mEnabledCameraUsers;
        if (set == null || !set.equals(enabledUserHandles)) {
            this.mEnabledCameraUsers = enabledUserHandles;
            notifySwitchWithRetriesLocked(60);
        }
    }

    public final Set<Integer> getEnabledUserHandles(int i) {
        int[] enabledProfileIds = this.mUserManager.getEnabledProfileIds(i);
        ArraySet arraySet = new ArraySet(enabledProfileIds.length);
        for (int i2 : enabledProfileIds) {
            arraySet.add(Integer.valueOf(i2));
        }
        return arraySet;
    }

    public final void notifySwitchWithRetries(int i) {
        synchronized (this.mLock) {
            notifySwitchWithRetriesLocked(i);
        }
    }

    public final void notifySwitchWithRetriesLocked(int i) {
        Set<Integer> set = this.mEnabledCameraUsers;
        if (set == null) {
            return;
        }
        if (notifyCameraserverLocked(1, set)) {
            i = 0;
        }
        if (i <= 0) {
            return;
        }
        Slog.i("CameraService_proxy", "Could not notify camera service of user switch, retrying...");
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1, i - 1, 0, null), 20L);
    }

    public final boolean notifyCameraserverLocked(int i, Set<Integer> set) {
        if (getCameraServiceRawLocked() == null) {
            Slog.w("CameraService_proxy", "Could not notify cameraserver, camera service not available.");
            return false;
        }
        try {
            this.mCameraServiceRaw.notifySystemEvent(i, toArray(set));
            return true;
        } catch (RemoteException e) {
            Slog.w("CameraService_proxy", "Could not notify cameraserver, remote exception: " + e);
            return false;
        }
    }

    public final void notifyDeviceStateWithRetries(int i) {
        synchronized (this.mLock) {
            notifyDeviceStateWithRetriesLocked(i);
        }
    }

    public final void notifyDeviceStateWithRetriesLocked(int i) {
        if (!notifyDeviceStateChangeLocked(this.mDeviceState) && i > 0) {
            Slog.i("CameraService_proxy", "Could not notify camera service of device state change, retrying...");
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(2, i - 1, 0, null), 20L);
        }
    }

    public final boolean notifyDeviceStateChangeLocked(int i) {
        if (getCameraServiceRawLocked() == null) {
            Slog.w("CameraService_proxy", "Could not notify cameraserver, camera service not available.");
            return false;
        }
        try {
            this.mCameraServiceRaw.notifyDeviceStateChange(i);
            this.mLastReportedDeviceState = i;
            return true;
        } catch (RemoteException e) {
            Slog.w("CameraService_proxy", "Could not notify cameraserver, remote exception: " + e);
            return false;
        }
    }

    public final boolean notifyUsbDeviceHotplugLocked(UsbDevice usbDevice, boolean z) {
        if (usbDevice.getHasVideoCapture()) {
            if (getCameraServiceRawLocked() == null) {
                Slog.w("CameraService_proxy", "Could not notify cameraserver, camera service not available.");
                return false;
            }
            try {
                this.mCameraServiceRaw.notifySystemEvent(z ? 2 : 3, new int[]{usbDevice.getDeviceId()});
                return true;
            } catch (RemoteException e) {
                Slog.w("CameraService_proxy", "Could not notify cameraserver, remote exception: " + e);
            }
        }
        return false;
    }

    public final float getMinFps(CameraSessionStats cameraSessionStats) {
        return Math.max(Math.min(cameraSessionStats.getMaxPreviewFps(), 60.0f), 30.0f);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r14v0, types: [long] */
    /* JADX WARN: Type inference failed for: r14v1 */
    /* JADX WARN: Type inference failed for: r14v2 */
    /* JADX WARN: Type inference failed for: r2v25, types: [java.lang.Object, com.android.server.camera.CameraServiceProxy$CameraUsageEvent] */
    public final void updateActivityCount(CameraSessionStats cameraSessionStats) {
        boolean z;
        Object obj;
        boolean z2;
        boolean z3;
        String cameraId = cameraSessionStats.getCameraId();
        int newCameraState = cameraSessionStats.getNewCameraState();
        int facing = cameraSessionStats.getFacing();
        String clientName = cameraSessionStats.getClientName();
        int apiLevel = cameraSessionStats.getApiLevel();
        boolean isNdk = cameraSessionStats.isNdk();
        int sessionType = cameraSessionStats.getSessionType();
        int internalReconfigureCount = cameraSessionStats.getInternalReconfigureCount();
        int latencyMs = cameraSessionStats.getLatencyMs();
        ?? requestCount = cameraSessionStats.getRequestCount();
        long resultErrorCount = cameraSessionStats.getResultErrorCount();
        boolean deviceErrorFlag = cameraSessionStats.getDeviceErrorFlag();
        List<CameraStreamStats> streamStats = cameraSessionStats.getStreamStats();
        String userTag = cameraSessionStats.getUserTag();
        int videoStabilizationMode = cameraSessionStats.getVideoStabilizationMode();
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                try {
                    boolean isEmpty = this.mActiveCameraUsage.isEmpty();
                    if (newCameraState == 0) {
                        z = isEmpty;
                        obj = obj2;
                        AudioManager audioManager = (AudioManager) getContext().getSystemService(AudioManager.class);
                        if (audioManager != null) {
                            audioManager.setParameters("cameraFacing=" + (facing == 0 ? "back" : "front"));
                        }
                        this.mCameraUsageHistory.add(new CameraUsageEvent(cameraId, facing, clientName, apiLevel, isNdk, 1, latencyMs, sessionType, deviceErrorFlag));
                    } else if (newCameraState == 1) {
                        z = isEmpty;
                        obj = obj2;
                        int i = 0;
                        while (true) {
                            if (i >= this.mActiveCameraUsage.size()) {
                                z2 = false;
                                break;
                            } else if (this.mActiveCameraUsage.valueAt(i).mClientName.equals(clientName)) {
                                z2 = true;
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (!z2) {
                            ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).addRefreshRateRangeForPackage(clientName, getMinFps(cameraSessionStats), 60.0f);
                        }
                        CameraUsageEvent put = this.mActiveCameraUsage.put(cameraId, new CameraUsageEvent(cameraId, facing, clientName, apiLevel, isNdk, 3, latencyMs, sessionType, deviceErrorFlag));
                        if (put != null) {
                            Slog.w("CameraService_proxy", "Camera " + cameraId + " was already marked as active");
                            put.markCompleted(0, 0L, 0L, false, streamStats, "", -1);
                            this.mCameraUsageHistory.add(put);
                        }
                    } else if (newCameraState == 2 || newCameraState == 3) {
                        CameraUsageEvent remove = this.mActiveCameraUsage.remove(cameraId);
                        if (remove != 0) {
                            remove.markCompleted(internalReconfigureCount, requestCount, resultErrorCount, deviceErrorFlag, streamStats, userTag, videoStabilizationMode);
                            this.mCameraUsageHistory.add(remove);
                            int i2 = 0;
                            while (true) {
                                if (i2 >= this.mActiveCameraUsage.size()) {
                                    z3 = false;
                                    break;
                                } else if (this.mActiveCameraUsage.valueAt(i2).mClientName.equals(clientName)) {
                                    z3 = true;
                                    break;
                                } else {
                                    i2++;
                                }
                            }
                            if (!z3) {
                                ((WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class)).removeRefreshRateRangeForPackage(clientName);
                            }
                            deviceErrorFlag = false;
                        }
                        if (newCameraState == 3) {
                            z = isEmpty;
                            obj = obj2;
                            this.mCameraUsageHistory.add(new CameraUsageEvent(cameraId, facing, clientName, apiLevel, isNdk, 2, latencyMs, sessionType, deviceErrorFlag));
                        } else {
                            obj = obj2;
                            z = isEmpty;
                        }
                        if (this.mCameraUsageHistory.size() > 20) {
                            dumpUsageEvents();
                        }
                    } else {
                        obj = obj2;
                        z = isEmpty;
                    }
                    boolean isEmpty2 = this.mActiveCameraUsage.isEmpty();
                    if (this.mNotifyNfc && z != isEmpty2) {
                        notifyNfcService(isEmpty2);
                    }
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                requestCount = obj2;
                throw th;
            }
        }
    }

    public final void notifyNfcService(boolean z) {
        IBinder binderService = getBinderService("nfc");
        if (binderService == null) {
            Slog.w("CameraService_proxy", "Could not connect to NFC service to notify it of camera state");
            return;
        }
        try {
            INfcAdapter.Stub.asInterface(binderService).setReaderMode(nfcInterfaceToken, (IAppCallback) null, z ? 0 : IInstalld.FLAG_USE_QUOTA, (Bundle) null);
        } catch (RemoteException e) {
            Slog.w("CameraService_proxy", "Could not notify NFC service, remote exception: " + e);
        }
    }

    public static int[] toArray(Collection<Integer> collection) {
        int[] iArr = new int[collection.size()];
        int i = 0;
        for (Integer num : collection) {
            iArr[i] = num.intValue();
            i++;
        }
        return iArr;
    }
}
