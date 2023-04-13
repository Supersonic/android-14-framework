package com.android.server.companion.virtual;

import android.annotation.EnforcePermission;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.companion.AssociationInfo;
import android.companion.virtual.IVirtualDevice;
import android.companion.virtual.IVirtualDeviceActivityListener;
import android.companion.virtual.IVirtualDeviceIntentInterceptor;
import android.companion.virtual.IVirtualDeviceSoundEffectListener;
import android.companion.virtual.VirtualDeviceManager;
import android.companion.virtual.VirtualDeviceParams;
import android.companion.virtual.audio.IAudioConfigChangedCallback;
import android.companion.virtual.audio.IAudioRoutingCallback;
import android.companion.virtual.sensor.VirtualSensor;
import android.companion.virtual.sensor.VirtualSensorConfig;
import android.companion.virtual.sensor.VirtualSensorEvent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerGlobal;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplayConfig;
import android.hardware.input.VirtualDpadConfig;
import android.hardware.input.VirtualKeyEvent;
import android.hardware.input.VirtualKeyboardConfig;
import android.hardware.input.VirtualMouseButtonEvent;
import android.hardware.input.VirtualMouseConfig;
import android.hardware.input.VirtualMouseRelativeEvent;
import android.hardware.input.VirtualMouseScrollEvent;
import android.hardware.input.VirtualNavigationTouchpadConfig;
import android.hardware.input.VirtualTouchEvent;
import android.hardware.input.VirtualTouchscreenConfig;
import android.os.Binder;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Looper;
import android.os.PermissionEnforcer;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.BlockedAppStreamingActivity;
import com.android.server.LocalServices;
import com.android.server.companion.virtual.GenericWindowPolicyController;
import com.android.server.companion.virtual.audio.VirtualAudioController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class VirtualDeviceImpl extends IVirtualDevice.Stub implements IBinder.DeathRecipient, GenericWindowPolicyController.RunningAppsChangedListener {
    public final IVirtualDeviceActivityListener mActivityListener;
    public final IBinder mAppToken;
    public final AssociationInfo mAssociationInfo;
    public final CameraAccessController mCameraAccessController;
    public final Context mContext;
    @GuardedBy({"mVirtualDeviceLock"})
    public boolean mDefaultShowPointerIcon;
    public final int mDeviceId;
    public final DisplayManagerGlobal mDisplayManager;
    public final InputController mInputController;
    @GuardedBy({"mVirtualDeviceLock"})
    public final Map<IBinder, IntentFilter> mIntentInterceptors;
    @GuardedBy({"mVirtualDeviceLock"})
    public LocaleList mLocaleList;
    public final int mOwnerUid;
    public final VirtualDeviceParams mParams;
    public final PendingTrampolineCallback mPendingTrampolineCallback;
    public Consumer<ArraySet<Integer>> mRunningAppsChangedCallback;
    public final SensorController mSensorController;
    public final VirtualDeviceManagerService mService;
    public final IVirtualDeviceSoundEffectListener mSoundEffectListener;
    public VirtualAudioController mVirtualAudioController;
    public final Object mVirtualDeviceLock;
    @GuardedBy({"mVirtualDeviceLock"})
    public final SparseArray<VirtualDisplayWrapper> mVirtualDisplays;
    @GuardedBy({"mVirtualDeviceLock"})
    public List<VirtualSensor> mVirtualSensorList;
    @GuardedBy({"mVirtualDeviceLock"})
    public SparseArray<VirtualSensor> mVirtualSensors;

    /* loaded from: classes.dex */
    public interface PendingTrampolineCallback {
        void startWaitingForPendingTrampoline(PendingTrampoline pendingTrampoline);

        void stopWaitingForPendingTrampoline(PendingTrampoline pendingTrampoline);
    }

    public final VirtualDeviceManager.ActivityListener createListenerAdapter() {
        return new VirtualDeviceManager.ActivityListener() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl.1
            public void onTopActivityChanged(int i, ComponentName componentName) {
                try {
                    VirtualDeviceImpl.this.mActivityListener.onTopActivityChanged(i, componentName, -10000);
                } catch (RemoteException e) {
                    Slog.w("VirtualDeviceImpl", "Unable to call mActivityListener", e);
                }
            }

            public void onTopActivityChanged(int i, ComponentName componentName, int i2) {
                try {
                    VirtualDeviceImpl.this.mActivityListener.onTopActivityChanged(i, componentName, i2);
                } catch (RemoteException e) {
                    Slog.w("VirtualDeviceImpl", "Unable to call mActivityListener", e);
                }
            }

            public void onDisplayEmpty(int i) {
                try {
                    VirtualDeviceImpl.this.mActivityListener.onDisplayEmpty(i);
                } catch (RemoteException e) {
                    Slog.w("VirtualDeviceImpl", "Unable to call mActivityListener", e);
                }
            }
        };
    }

    public VirtualDeviceImpl(Context context, AssociationInfo associationInfo, VirtualDeviceManagerService virtualDeviceManagerService, IBinder iBinder, int i, int i2, CameraAccessController cameraAccessController, PendingTrampolineCallback pendingTrampolineCallback, IVirtualDeviceActivityListener iVirtualDeviceActivityListener, IVirtualDeviceSoundEffectListener iVirtualDeviceSoundEffectListener, Consumer<ArraySet<Integer>> consumer, VirtualDeviceParams virtualDeviceParams) {
        this(context, associationInfo, virtualDeviceManagerService, iBinder, i, i2, null, null, cameraAccessController, pendingTrampolineCallback, iVirtualDeviceActivityListener, iVirtualDeviceSoundEffectListener, consumer, virtualDeviceParams, DisplayManagerGlobal.getInstance());
    }

    @VisibleForTesting
    public VirtualDeviceImpl(Context context, AssociationInfo associationInfo, VirtualDeviceManagerService virtualDeviceManagerService, IBinder iBinder, int i, int i2, InputController inputController, SensorController sensorController, CameraAccessController cameraAccessController, PendingTrampolineCallback pendingTrampolineCallback, IVirtualDeviceActivityListener iVirtualDeviceActivityListener, IVirtualDeviceSoundEffectListener iVirtualDeviceSoundEffectListener, Consumer<ArraySet<Integer>> consumer, VirtualDeviceParams virtualDeviceParams, DisplayManagerGlobal displayManagerGlobal) {
        super(PermissionEnforcer.fromContext(context));
        Object obj = new Object();
        this.mVirtualDeviceLock = obj;
        this.mVirtualDisplays = new SparseArray<>();
        this.mIntentInterceptors = new ArrayMap();
        this.mDefaultShowPointerIcon = true;
        this.mLocaleList = null;
        this.mVirtualSensors = new SparseArray<>();
        this.mVirtualSensorList = null;
        this.mContext = context.createContextAsUser(UserHandle.getUserHandleForUid(i), 0);
        this.mAssociationInfo = associationInfo;
        this.mService = virtualDeviceManagerService;
        this.mPendingTrampolineCallback = pendingTrampolineCallback;
        this.mActivityListener = iVirtualDeviceActivityListener;
        this.mSoundEffectListener = iVirtualDeviceSoundEffectListener;
        this.mRunningAppsChangedCallback = consumer;
        this.mOwnerUid = i;
        this.mDeviceId = i2;
        this.mAppToken = iBinder;
        this.mParams = virtualDeviceParams;
        this.mDisplayManager = displayManagerGlobal;
        if (inputController == null) {
            this.mInputController = new InputController(context.getMainThreadHandler(), (WindowManager) context.getSystemService(WindowManager.class));
        } else {
            this.mInputController = inputController;
        }
        if (sensorController == null) {
            this.mSensorController = new SensorController(obj, i2, virtualDeviceParams.getVirtualSensorCallback());
        } else {
            this.mSensorController = sensorController;
        }
        List virtualSensorConfigs = virtualDeviceParams.getVirtualSensorConfigs();
        for (int i3 = 0; i3 < virtualSensorConfigs.size(); i3++) {
            createVirtualSensor((VirtualSensorConfig) virtualSensorConfigs.get(i3));
        }
        this.mCameraAccessController = cameraAccessController;
        cameraAccessController.startObservingIfNeeded();
        try {
            iBinder.linkToDeath(this, 0);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getBaseVirtualDisplayFlags() {
        return this.mParams.getLockState() == 1 ? 20937 : 16841;
    }

    public CameraAccessController getCameraAccessController() {
        return this.mCameraAccessController;
    }

    public CharSequence getDisplayName() {
        return this.mAssociationInfo.getDisplayName();
    }

    public String getDeviceName() {
        return this.mParams.getName();
    }

    public LocaleList getDeviceLocaleList() {
        LocaleList localeList;
        synchronized (this.mVirtualDeviceLock) {
            localeList = this.mLocaleList;
        }
        return localeList;
    }

    public int getDevicePolicy(int i) {
        return this.mParams.getDevicePolicy(i);
    }

    public int getAudioPlaybackSessionId() {
        return this.mParams.getAudioPlaybackSessionId();
    }

    public int getAudioRecordingSessionId() {
        return this.mParams.getAudioRecordingSessionId();
    }

    public int getDeviceId() {
        return this.mDeviceId;
    }

    public int getAssociationId() {
        return this.mAssociationInfo.getId();
    }

    public void launchPendingIntent(int i, PendingIntent pendingIntent, ResultReceiver resultReceiver) {
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(i)) {
                throw new SecurityException("Display ID " + i + " not found for this virtual device");
            }
        }
        if (pendingIntent.isActivity()) {
            try {
                sendPendingIntent(i, pendingIntent);
                resultReceiver.send(0, null);
                return;
            } catch (PendingIntent.CanceledException e) {
                Slog.w("VirtualDeviceImpl", "Pending intent canceled", e);
                resultReceiver.send(1, null);
                return;
            }
        }
        final PendingTrampoline pendingTrampoline = new PendingTrampoline(pendingIntent, resultReceiver, i);
        this.mPendingTrampolineCallback.startWaitingForPendingTrampoline(pendingTrampoline);
        this.mContext.getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VirtualDeviceImpl.this.lambda$launchPendingIntent$0(pendingTrampoline);
            }
        }, 5000L);
        try {
            sendPendingIntent(i, pendingIntent);
        } catch (PendingIntent.CanceledException e2) {
            Slog.w("VirtualDeviceImpl", "Pending intent canceled", e2);
            resultReceiver.send(1, null);
            this.mPendingTrampolineCallback.stopWaitingForPendingTrampoline(pendingTrampoline);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$launchPendingIntent$0(PendingTrampoline pendingTrampoline) {
        pendingTrampoline.mResultReceiver.send(2, null);
        this.mPendingTrampolineCallback.stopWaitingForPendingTrampoline(pendingTrampoline);
    }

    public final void sendPendingIntent(int i, PendingIntent pendingIntent) throws PendingIntent.CanceledException {
        ActivityOptions launchDisplayId = ActivityOptions.makeBasic().setLaunchDisplayId(i);
        launchDisplayId.setPendingIntentBackgroundActivityLaunchAllowed(true);
        launchDisplayId.setPendingIntentBackgroundActivityLaunchAllowedByPermission(true);
        pendingIntent.send(this.mContext, 0, null, null, null, null, launchDisplayId.toBundle());
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void close() {
        int size;
        VirtualDisplayWrapper[] virtualDisplayWrapperArr;
        super.close_enforcePermission();
        this.mService.removeVirtualDevice(this.mDeviceId);
        synchronized (this.mVirtualDeviceLock) {
            VirtualAudioController virtualAudioController = this.mVirtualAudioController;
            if (virtualAudioController != null) {
                virtualAudioController.stopListening();
                this.mVirtualAudioController = null;
            }
            this.mLocaleList = null;
            size = this.mVirtualDisplays.size();
            virtualDisplayWrapperArr = new VirtualDisplayWrapper[size];
            for (int i = 0; i < this.mVirtualDisplays.size(); i++) {
                virtualDisplayWrapperArr[i] = this.mVirtualDisplays.valueAt(i);
            }
            this.mVirtualDisplays.clear();
            this.mVirtualSensorList = null;
            this.mVirtualSensors.clear();
        }
        for (int i2 = 0; i2 < size; i2++) {
            VirtualDisplayWrapper virtualDisplayWrapper = virtualDisplayWrapperArr[i2];
            this.mDisplayManager.releaseVirtualDisplay(virtualDisplayWrapper.getToken());
            releaseOwnedVirtualDisplayResources(virtualDisplayWrapper);
        }
        this.mAppToken.unlinkToDeath(this, 0);
        this.mCameraAccessController.stopObservingIfNeeded();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.close();
            this.mSensorController.close();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        close();
    }

    @Override // com.android.server.companion.virtual.GenericWindowPolicyController.RunningAppsChangedListener
    public void onRunningAppsChanged(ArraySet<Integer> arraySet) {
        this.mCameraAccessController.blockCameraAccessIfNeeded(arraySet);
        this.mRunningAppsChangedCallback.accept(arraySet);
    }

    @VisibleForTesting
    public VirtualAudioController getVirtualAudioControllerForTesting() {
        return this.mVirtualAudioController;
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void onAudioSessionStarting(int i, IAudioRoutingCallback iAudioRoutingCallback, IAudioConfigChangedCallback iAudioConfigChangedCallback) {
        super.onAudioSessionStarting_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(i)) {
                throw new SecurityException("Cannot start audio session for a display not associated with this virtual device");
            }
            if (this.mVirtualAudioController == null) {
                this.mVirtualAudioController = new VirtualAudioController(this.mContext);
                this.mVirtualAudioController.startListening(this.mVirtualDisplays.get(i).getWindowPolicyController(), iAudioRoutingCallback, iAudioConfigChangedCallback);
            }
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void onAudioSessionEnded() {
        super.onAudioSessionEnded_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            VirtualAudioController virtualAudioController = this.mVirtualAudioController;
            if (virtualAudioController != null) {
                virtualAudioController.stopListening();
                this.mVirtualAudioController = null;
            }
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void createVirtualDpad(VirtualDpadConfig virtualDpadConfig, IBinder iBinder) {
        super.createVirtualDpad_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(virtualDpadConfig.getAssociatedDisplayId())) {
                throw new SecurityException("Cannot create a virtual dpad for a display not associated with this virtual device");
            }
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.createDpad(virtualDpadConfig.getInputDeviceName(), virtualDpadConfig.getVendorId(), virtualDpadConfig.getProductId(), iBinder, virtualDpadConfig.getAssociatedDisplayId());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void createVirtualKeyboard(VirtualKeyboardConfig virtualKeyboardConfig, IBinder iBinder) {
        super.createVirtualKeyboard_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(virtualKeyboardConfig.getAssociatedDisplayId())) {
                throw new SecurityException("Cannot create a virtual keyboard for a display not associated with this virtual device");
            }
            this.mLocaleList = LocaleList.forLanguageTags(virtualKeyboardConfig.getLanguageTag());
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.createKeyboard(virtualKeyboardConfig.getInputDeviceName(), virtualKeyboardConfig.getVendorId(), virtualKeyboardConfig.getProductId(), iBinder, virtualKeyboardConfig.getAssociatedDisplayId(), virtualKeyboardConfig.getLanguageTag(), virtualKeyboardConfig.getLayoutType());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void createVirtualMouse(VirtualMouseConfig virtualMouseConfig, IBinder iBinder) {
        super.createVirtualMouse_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(virtualMouseConfig.getAssociatedDisplayId())) {
                throw new SecurityException("Cannot create a virtual mouse for a display not associated with this virtual device");
            }
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.createMouse(virtualMouseConfig.getInputDeviceName(), virtualMouseConfig.getVendorId(), virtualMouseConfig.getProductId(), iBinder, virtualMouseConfig.getAssociatedDisplayId());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void createVirtualTouchscreen(VirtualTouchscreenConfig virtualTouchscreenConfig, IBinder iBinder) {
        super.createVirtualTouchscreen_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(virtualTouchscreenConfig.getAssociatedDisplayId())) {
                throw new SecurityException("Cannot create a virtual touchscreen for a display not associated with this virtual device");
            }
        }
        int height = virtualTouchscreenConfig.getHeight();
        int width = virtualTouchscreenConfig.getWidth();
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("Cannot create a virtual touchscreen, screen dimensions must be positive. Got: (" + width + ", " + height + ")");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.createTouchscreen(virtualTouchscreenConfig.getInputDeviceName(), virtualTouchscreenConfig.getVendorId(), virtualTouchscreenConfig.getProductId(), iBinder, virtualTouchscreenConfig.getAssociatedDisplayId(), height, width);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void createVirtualNavigationTouchpad(VirtualNavigationTouchpadConfig virtualNavigationTouchpadConfig, IBinder iBinder) {
        super.createVirtualNavigationTouchpad_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (!this.mVirtualDisplays.contains(virtualNavigationTouchpadConfig.getAssociatedDisplayId())) {
                throw new SecurityException("Cannot create a virtual navigation touchpad for a display not associated with this virtual device");
            }
        }
        int height = virtualNavigationTouchpadConfig.getHeight();
        int width = virtualNavigationTouchpadConfig.getWidth();
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("Cannot create a virtual navigation touchpad, touchpad dimensions must be positive. Got: (" + height + ", " + width + ")");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.createNavigationTouchpad(virtualNavigationTouchpadConfig.getInputDeviceName(), virtualNavigationTouchpadConfig.getVendorId(), virtualNavigationTouchpadConfig.getProductId(), iBinder, virtualNavigationTouchpadConfig.getAssociatedDisplayId(), height, width);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void unregisterInputDevice(IBinder iBinder) {
        super.unregisterInputDevice_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.unregisterInputDevice(iBinder);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getInputDeviceId(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.getInputDeviceId(iBinder);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendDpadKeyEvent(IBinder iBinder, VirtualKeyEvent virtualKeyEvent) {
        super.sendDpadKeyEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendDpadKeyEvent(iBinder, virtualKeyEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendKeyEvent(IBinder iBinder, VirtualKeyEvent virtualKeyEvent) {
        super.sendKeyEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendKeyEvent(iBinder, virtualKeyEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendButtonEvent(IBinder iBinder, VirtualMouseButtonEvent virtualMouseButtonEvent) {
        super.sendButtonEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendButtonEvent(iBinder, virtualMouseButtonEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendTouchEvent(IBinder iBinder, VirtualTouchEvent virtualTouchEvent) {
        super.sendTouchEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendTouchEvent(iBinder, virtualTouchEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendRelativeEvent(IBinder iBinder, VirtualMouseRelativeEvent virtualMouseRelativeEvent) {
        super.sendRelativeEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendRelativeEvent(iBinder, virtualMouseRelativeEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendScrollEvent(IBinder iBinder, VirtualMouseScrollEvent virtualMouseScrollEvent) {
        super.sendScrollEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.sendScrollEvent(iBinder, virtualMouseScrollEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public PointF getCursorPosition(IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mInputController.getCursorPosition(iBinder);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void setShowPointerIcon(boolean z) {
        super.setShowPointerIcon_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mVirtualDeviceLock) {
                this.mDefaultShowPointerIcon = z;
                for (int i = 0; i < this.mVirtualDisplays.size(); i++) {
                    this.mInputController.setShowPointerIcon(this.mDefaultShowPointerIcon, this.mVirtualDisplays.keyAt(i));
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void createVirtualSensor(VirtualSensorConfig virtualSensorConfig) {
        Binder binder = new Binder("android.hardware.sensor.VirtualSensor:" + virtualSensorConfig.getName());
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int createSensor = this.mSensorController.createSensor(binder, virtualSensorConfig);
            VirtualSensor virtualSensor = new VirtualSensor(createSensor, virtualSensorConfig.getType(), virtualSensorConfig.getName(), this, binder);
            synchronized (this.mVirtualDeviceLock) {
                this.mVirtualSensors.put(createSensor, virtualSensor);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public List<VirtualSensor> getVirtualSensorList() {
        List<VirtualSensor> list;
        super.getVirtualSensorList_enforcePermission();
        synchronized (this.mVirtualDeviceLock) {
            if (this.mVirtualSensorList == null) {
                this.mVirtualSensorList = new ArrayList();
                for (int i = 0; i < this.mVirtualSensors.size(); i++) {
                    this.mVirtualSensorList.add(this.mVirtualSensors.valueAt(i));
                }
                this.mVirtualSensorList = Collections.unmodifiableList(this.mVirtualSensorList);
            }
            list = this.mVirtualSensorList;
        }
        return list;
    }

    public VirtualSensor getVirtualSensorByHandle(int i) {
        VirtualSensor virtualSensor;
        synchronized (this.mVirtualDeviceLock) {
            virtualSensor = this.mVirtualSensors.get(i);
        }
        return virtualSensor;
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public boolean sendSensorEvent(IBinder iBinder, VirtualSensorEvent virtualSensorEvent) {
        super.sendSensorEvent_enforcePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSensorController.sendSensorEvent(iBinder, virtualSensorEvent);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void registerIntentInterceptor(IVirtualDeviceIntentInterceptor iVirtualDeviceIntentInterceptor, IntentFilter intentFilter) {
        super.registerIntentInterceptor_enforcePermission();
        Objects.requireNonNull(iVirtualDeviceIntentInterceptor);
        Objects.requireNonNull(intentFilter);
        synchronized (this.mVirtualDeviceLock) {
            this.mIntentInterceptors.put(iVirtualDeviceIntentInterceptor.asBinder(), intentFilter);
        }
    }

    @EnforcePermission("android.permission.CREATE_VIRTUAL_DEVICE")
    public void unregisterIntentInterceptor(IVirtualDeviceIntentInterceptor iVirtualDeviceIntentInterceptor) {
        super.unregisterIntentInterceptor_enforcePermission();
        Objects.requireNonNull(iVirtualDeviceIntentInterceptor);
        synchronized (this.mVirtualDeviceLock) {
            this.mIntentInterceptors.remove(iVirtualDeviceIntentInterceptor.asBinder());
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("  VirtualDevice: ");
        printWriter.println("    mDeviceId: " + this.mDeviceId);
        printWriter.println("    mAssociationId: " + this.mAssociationInfo.getId());
        printWriter.println("    mParams: " + this.mParams);
        printWriter.println("    mVirtualDisplayIds: ");
        synchronized (this.mVirtualDeviceLock) {
            for (int i = 0; i < this.mVirtualDisplays.size(); i++) {
                printWriter.println("      " + this.mVirtualDisplays.keyAt(i));
            }
            printWriter.println("    mDefaultShowPointerIcon: " + this.mDefaultShowPointerIcon);
        }
        this.mInputController.dump(printWriter);
        this.mSensorController.dump(printWriter);
    }

    public final GenericWindowPolicyController createWindowPolicyController(Set<String> set) {
        GenericWindowPolicyController genericWindowPolicyController = new GenericWindowPolicyController(IInstalld.FLAG_FORCE, 524288, getAllowedUserHandles(), this.mParams.getAllowedCrossTaskNavigations(), this.mParams.getBlockedCrossTaskNavigations(), this.mParams.getAllowedActivities(), this.mParams.getBlockedActivities(), this.mParams.getDefaultActivityPolicy(), createListenerAdapter(), new GenericWindowPolicyController.PipBlockedCallback() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl$$ExternalSyntheticLambda1
            @Override // com.android.server.companion.virtual.GenericWindowPolicyController.PipBlockedCallback
            public final void onEnteringPipBlocked(int i) {
                VirtualDeviceImpl.this.onEnteringPipBlocked(i);
            }
        }, new GenericWindowPolicyController.ActivityBlockedCallback() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl$$ExternalSyntheticLambda2
            @Override // com.android.server.companion.virtual.GenericWindowPolicyController.ActivityBlockedCallback
            public final void onActivityBlocked(int i, ActivityInfo activityInfo) {
                VirtualDeviceImpl.this.onActivityBlocked(i, activityInfo);
            }
        }, new GenericWindowPolicyController.SecureWindowCallback() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl$$ExternalSyntheticLambda3
            @Override // com.android.server.companion.virtual.GenericWindowPolicyController.SecureWindowCallback
            public final void onSecureWindowShown(int i, int i2) {
                VirtualDeviceImpl.this.onSecureWindowShown(i, i2);
            }
        }, new GenericWindowPolicyController.IntentListenerCallback() { // from class: com.android.server.companion.virtual.VirtualDeviceImpl$$ExternalSyntheticLambda4
            @Override // com.android.server.companion.virtual.GenericWindowPolicyController.IntentListenerCallback
            public final boolean shouldInterceptIntent(Intent intent) {
                boolean shouldInterceptIntent;
                shouldInterceptIntent = VirtualDeviceImpl.this.shouldInterceptIntent(intent);
                return shouldInterceptIntent;
            }
        }, set, this.mParams.getDevicePolicy(2) == 0);
        genericWindowPolicyController.registerRunningAppsChangedListener(this);
        return genericWindowPolicyController;
    }

    public int createVirtualDisplay(VirtualDisplayConfig virtualDisplayConfig, IVirtualDisplayCallback iVirtualDisplayCallback, String str) {
        GenericWindowPolicyController createWindowPolicyController = createWindowPolicyController(virtualDisplayConfig.getDisplayCategories());
        int createVirtualDisplay = ((DisplayManagerInternal) LocalServices.getService(DisplayManagerInternal.class)).createVirtualDisplay(virtualDisplayConfig, iVirtualDisplayCallback, this, createWindowPolicyController, str);
        createWindowPolicyController.setDisplayId(createVirtualDisplay);
        synchronized (this.mVirtualDeviceLock) {
            if (this.mVirtualDisplays.contains(createVirtualDisplay)) {
                createWindowPolicyController.unregisterRunningAppsChangedListener(this);
                throw new IllegalStateException("Virtual device already has a virtual display with ID " + createVirtualDisplay);
            }
            this.mVirtualDisplays.put(createVirtualDisplay, new VirtualDisplayWrapper(iVirtualDisplayCallback, createWindowPolicyController, createAndAcquireWakeLockForDisplay(createVirtualDisplay)));
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInputController.setShowPointerIcon(this.mDefaultShowPointerIcon, createVirtualDisplay);
            this.mInputController.setPointerAcceleration(1.0f, createVirtualDisplay);
            this.mInputController.setDisplayEligibilityForPointerCapture(false, createVirtualDisplay);
            this.mInputController.setLocalIme(createVirtualDisplay);
            return createVirtualDisplay;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final PowerManager.WakeLock createAndAcquireWakeLockForDisplay(int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) this.mContext.getSystemService(PowerManager.class)).newWakeLock(10, "VirtualDeviceImpl:" + i, i);
            newWakeLock.acquire();
            return newWakeLock;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void onActivityBlocked(int i, ActivityInfo activityInfo) {
        this.mContext.startActivityAsUser(BlockedAppStreamingActivity.createIntent(activityInfo, this.mAssociationInfo.getDisplayName()).addFlags(268468224), ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle(), this.mContext.getUser());
    }

    public final void onSecureWindowShown(int i, int i2) {
        synchronized (this.mVirtualDeviceLock) {
            if (this.mVirtualDisplays.contains(i)) {
                if ((((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i).getFlags() & IInstalld.FLAG_FORCE) == 0) {
                    showToastWhereUidIsRunning(i2, 17041710, 1, this.mContext.getMainLooper());
                }
            }
        }
    }

    public final ArraySet<UserHandle> getAllowedUserHandles() {
        ArraySet<UserHandle> arraySet = new ArraySet<>();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
            for (UserHandle userHandle : ((UserManager) this.mContext.getSystemService(UserManager.class)).getAllProfiles()) {
                int nearbyAppStreamingPolicy = devicePolicyManager.getNearbyAppStreamingPolicy(userHandle.getIdentifier());
                if (nearbyAppStreamingPolicy != 2 && nearbyAppStreamingPolicy != 0) {
                    if (nearbyAppStreamingPolicy == 3 && this.mParams.getUsersWithMatchingAccounts().contains(userHandle)) {
                        arraySet.add(userHandle);
                    }
                }
                arraySet.add(userHandle);
            }
            return arraySet;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onVirtualDisplayRemoved(int i) {
        VirtualDisplayWrapper virtualDisplayWrapper;
        synchronized (this.mVirtualDeviceLock) {
            virtualDisplayWrapper = (VirtualDisplayWrapper) this.mVirtualDisplays.removeReturnOld(i);
        }
        if (virtualDisplayWrapper == null) {
            throw new IllegalStateException("Virtual device doesn't have a virtual display with ID " + i);
        }
        releaseOwnedVirtualDisplayResources(virtualDisplayWrapper);
    }

    public final void releaseOwnedVirtualDisplayResources(VirtualDisplayWrapper virtualDisplayWrapper) {
        virtualDisplayWrapper.getWakeLock().release();
        virtualDisplayWrapper.getWindowPolicyController().unregisterRunningAppsChangedListener(this);
    }

    public int getOwnerUid() {
        return this.mOwnerUid;
    }

    public ArraySet<Integer> getDisplayIds() {
        ArraySet<Integer> arraySet;
        synchronized (this.mVirtualDeviceLock) {
            int size = this.mVirtualDisplays.size();
            arraySet = new ArraySet<>(size);
            for (int i = 0; i < size; i++) {
                arraySet.append(Integer.valueOf(this.mVirtualDisplays.keyAt(i)));
            }
        }
        return arraySet;
    }

    @VisibleForTesting
    public GenericWindowPolicyController getDisplayWindowPolicyControllerForTest(int i) {
        VirtualDisplayWrapper virtualDisplayWrapper;
        synchronized (this.mVirtualDeviceLock) {
            virtualDisplayWrapper = this.mVirtualDisplays.get(i);
        }
        if (virtualDisplayWrapper != null) {
            return virtualDisplayWrapper.getWindowPolicyController();
        }
        return null;
    }

    public boolean isAppRunningOnVirtualDevice(int i) {
        synchronized (this.mVirtualDeviceLock) {
            for (int i2 = 0; i2 < this.mVirtualDisplays.size(); i2++) {
                if (this.mVirtualDisplays.valueAt(i2).getWindowPolicyController().containsUid(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void showToastWhereUidIsRunning(int i, int i2, int i3, Looper looper) {
        showToastWhereUidIsRunning(i, this.mContext.getString(i2), i3, looper);
    }

    public void showToastWhereUidIsRunning(int i, String str, int i2, Looper looper) {
        Display display;
        synchronized (this.mVirtualDeviceLock) {
            DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            for (int i3 = 0; i3 < this.mVirtualDisplays.size(); i3++) {
                if (this.mVirtualDisplays.valueAt(i3).getWindowPolicyController().containsUid(i) && (display = displayManager.getDisplay(this.mVirtualDisplays.keyAt(i3))) != null && display.isValid()) {
                    Toast.makeText(this.mContext.createDisplayContext(display), looper, str, i2).show();
                }
            }
        }
    }

    public boolean isDisplayOwnedByVirtualDevice(int i) {
        boolean contains;
        synchronized (this.mVirtualDeviceLock) {
            contains = this.mVirtualDisplays.contains(i);
        }
        return contains;
    }

    public void onEnteringPipBlocked(int i) {
        showToastWhereUidIsRunning(i, 17041709, 1, this.mContext.getMainLooper());
    }

    public void playSoundEffect(int i) {
        try {
            this.mSoundEffectListener.onPlaySoundEffect(i);
        } catch (RemoteException e) {
            Slog.w("VirtualDeviceImpl", "Unable to invoke sound effect listener", e);
        }
    }

    public final boolean shouldInterceptIntent(Intent intent) {
        boolean z;
        synchronized (this.mVirtualDeviceLock) {
            z = false;
            for (Map.Entry<IBinder, IntentFilter> entry : this.mIntentInterceptors.entrySet()) {
                if (entry.getValue().match(intent.getAction(), intent.getType(), intent.getScheme(), intent.getData(), intent.getCategories(), "VirtualDeviceImpl") >= 0) {
                    try {
                        IVirtualDeviceIntentInterceptor.Stub.asInterface(entry.getKey()).onIntentIntercepted(new Intent(intent.getAction(), intent.getData()));
                        z = true;
                    } catch (RemoteException e) {
                        Slog.w("VirtualDeviceImpl", "Unable to call mVirtualDeviceIntentInterceptor", e);
                    }
                }
            }
        }
        return z;
    }

    /* loaded from: classes.dex */
    public static class PendingTrampoline {
        public final int mDisplayId;
        public final PendingIntent mPendingIntent;
        public final ResultReceiver mResultReceiver;

        public PendingTrampoline(PendingIntent pendingIntent, ResultReceiver resultReceiver, int i) {
            this.mPendingIntent = pendingIntent;
            this.mResultReceiver = resultReceiver;
            this.mDisplayId = i;
        }

        public String toString() {
            return "PendingTrampoline{pendingIntent=" + this.mPendingIntent + ", resultReceiver=" + this.mResultReceiver + ", displayId=" + this.mDisplayId + "}";
        }
    }

    /* loaded from: classes.dex */
    public static final class VirtualDisplayWrapper {
        public final IVirtualDisplayCallback mToken;
        public final PowerManager.WakeLock mWakeLock;
        public final GenericWindowPolicyController mWindowPolicyController;

        public VirtualDisplayWrapper(IVirtualDisplayCallback iVirtualDisplayCallback, GenericWindowPolicyController genericWindowPolicyController, PowerManager.WakeLock wakeLock) {
            Objects.requireNonNull(iVirtualDisplayCallback);
            this.mToken = iVirtualDisplayCallback;
            Objects.requireNonNull(genericWindowPolicyController);
            this.mWindowPolicyController = genericWindowPolicyController;
            Objects.requireNonNull(wakeLock);
            this.mWakeLock = wakeLock;
        }

        public GenericWindowPolicyController getWindowPolicyController() {
            return this.mWindowPolicyController;
        }

        public PowerManager.WakeLock getWakeLock() {
            return this.mWakeLock;
        }

        public IVirtualDisplayCallback getToken() {
            return this.mToken;
        }
    }
}
