package com.android.server.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.IDisplayPortAltModeInfoListener;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbOperationInternal;
import android.hardware.usb.ParcelableUsbPort;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.FgThread;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.SystemService;
import com.android.server.usb.UsbService;
import dalvik.annotation.optimization.NeverCompile;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes2.dex */
public class UsbService extends IUsbManager.Stub {
    public final UsbAlsaManager mAlsaManager;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public int mCurrentUserId;
    public UsbDeviceManager mDeviceManager;
    public UsbHostManager mHostManager;
    public final Object mLock = new Object();
    public final UsbPermissionManager mPermissionManager;
    public UsbPortManager mPortManager;
    public final UsbSettingsManager mSettingsManager;
    public final UserManager mUserManager;

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public final CompletableFuture<Void> mOnActivityManagerPhaseFinished;
        public final CompletableFuture<Void> mOnStartFinished;
        public UsbService mUsbService;

        public Lifecycle(Context context) {
            super(context);
            this.mOnStartFinished = new CompletableFuture<>();
            this.mOnActivityManagerPhaseFinished = new CompletableFuture<>();
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.usb.UsbService$Lifecycle$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    UsbService.Lifecycle.this.lambda$onStart$0();
                }
            }, "UsbService$Lifecycle#onStart");
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.usb.UsbService, android.os.IBinder] */
        public /* synthetic */ void lambda$onStart$0() {
            ?? usbService = new UsbService(getContext());
            this.mUsbService = usbService;
            publishBinderService("usb", usbService);
            this.mOnStartFinished.complete(null);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 550) {
                SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.usb.UsbService$Lifecycle$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        UsbService.Lifecycle.this.lambda$onBootPhase$1();
                    }
                }, "UsbService$Lifecycle#onBootPhase");
            } else if (i == 1000) {
                this.mOnActivityManagerPhaseFinished.join();
                this.mUsbService.bootCompleted();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBootPhase$1() {
            this.mOnStartFinished.join();
            this.mUsbService.systemReady();
            this.mOnActivityManagerPhaseFinished.complete(null);
        }

        @Override // com.android.server.SystemService
        public void onUserSwitching(SystemService.TargetUser targetUser, final SystemService.TargetUser targetUser2) {
            FgThread.getHandler().postAtFrontOfQueue(new Runnable() { // from class: com.android.server.usb.UsbService$Lifecycle$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UsbService.Lifecycle.this.lambda$onUserSwitching$2(targetUser2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUserSwitching$2(SystemService.TargetUser targetUser) {
            this.mUsbService.onSwitchUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mUsbService.onStopUser(targetUser.getUserHandle());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mUsbService.onUnlockUser(targetUser.getUserIdentifier());
        }
    }

    public UsbUserSettingsManager getSettingsForUser(int i) {
        return this.mSettingsManager.getSettingsForUser(i);
    }

    public UsbUserPermissionManager getPermissionsForUser(int i) {
        return this.mPermissionManager.getPermissionsForUser(i);
    }

    public UsbService(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        UsbSettingsManager usbSettingsManager = new UsbSettingsManager(context, this);
        this.mSettingsManager = usbSettingsManager;
        UsbPermissionManager usbPermissionManager = new UsbPermissionManager(context, this);
        this.mPermissionManager = usbPermissionManager;
        UsbAlsaManager usbAlsaManager = new UsbAlsaManager(context);
        this.mAlsaManager = usbAlsaManager;
        if (context.getPackageManager().hasSystemFeature("android.hardware.usb.host")) {
            this.mHostManager = new UsbHostManager(context, usbAlsaManager, usbPermissionManager);
        }
        if (new File("/sys/class/android_usb").exists()) {
            this.mDeviceManager = new UsbDeviceManager(context, usbAlsaManager, usbSettingsManager, usbPermissionManager);
        }
        if (this.mHostManager != null || this.mDeviceManager != null) {
            this.mPortManager = new UsbPortManager(context);
        }
        onSwitchUser(0);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.usb.UsbService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (!"android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(intent.getAction()) || UsbService.this.mDeviceManager == null) {
                    return;
                }
                UsbService.this.mDeviceManager.updateUserRestrictions();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, null);
    }

    public final void onSwitchUser(int i) {
        synchronized (this.mLock) {
            this.mCurrentUserId = i;
            UsbProfileGroupSettingsManager settingsForProfileGroup = this.mSettingsManager.getSettingsForProfileGroup(UserHandle.of(i));
            UsbHostManager usbHostManager = this.mHostManager;
            if (usbHostManager != null) {
                usbHostManager.setCurrentUserSettings(settingsForProfileGroup);
            }
            UsbDeviceManager usbDeviceManager = this.mDeviceManager;
            if (usbDeviceManager != null) {
                usbDeviceManager.setCurrentUser(i, settingsForProfileGroup);
            }
        }
    }

    public final void onStopUser(UserHandle userHandle) {
        this.mSettingsManager.remove(userHandle);
    }

    public void systemReady() {
        this.mAlsaManager.systemReady();
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.systemReady();
        }
        UsbHostManager usbHostManager = this.mHostManager;
        if (usbHostManager != null) {
            usbHostManager.systemReady();
        }
        UsbPortManager usbPortManager = this.mPortManager;
        if (usbPortManager != null) {
            usbPortManager.systemReady();
        }
    }

    public void bootCompleted() {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.bootCompleted();
        }
    }

    public void onUnlockUser(int i) {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.onUnlockUser(i);
        }
    }

    public void getDeviceList(Bundle bundle) {
        UsbHostManager usbHostManager = this.mHostManager;
        if (usbHostManager != null) {
            usbHostManager.getDeviceList(bundle);
        }
    }

    public ParcelFileDescriptor openDevice(String str, String str2) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        if (this.mHostManager != null && str != null) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            int userId = UserHandle.getUserId(callingUid);
            long clearCallingIdentity = IUsbManager.Stub.clearCallingIdentity();
            try {
                synchronized (this.mLock) {
                    if (this.mUserManager.isSameProfileGroup(userId, this.mCurrentUserId)) {
                        parcelFileDescriptor = this.mHostManager.openDevice(str, getPermissionsForUser(userId), str2, callingPid, callingUid);
                    } else {
                        Slog.w("UsbService", "Cannot open " + str + " for user " + userId + " as user is not active.");
                    }
                }
            } finally {
                IUsbManager.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return parcelFileDescriptor;
    }

    public UsbAccessory getCurrentAccessory() {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            return usbDeviceManager.getCurrentAccessory();
        }
        return null;
    }

    public ParcelFileDescriptor openAccessory(UsbAccessory usbAccessory) {
        if (this.mDeviceManager != null) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            int userId = UserHandle.getUserId(callingUid);
            long clearCallingIdentity = IUsbManager.Stub.clearCallingIdentity();
            try {
                synchronized (this.mLock) {
                    if (this.mUserManager.isSameProfileGroup(userId, this.mCurrentUserId)) {
                        return this.mDeviceManager.openAccessory(usbAccessory, getPermissionsForUser(userId), callingPid, callingUid);
                    }
                    Slog.w("UsbService", "Cannot open " + usbAccessory + " for user " + userId + " as user is not active.");
                    return null;
                }
            } finally {
                IUsbManager.Stub.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public ParcelFileDescriptor getControlFd(long j) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_MTP", null);
        return this.mDeviceManager.getControlFd(j);
    }

    public void setDevicePackage(UsbDevice usbDevice, String str, int i) {
        Objects.requireNonNull(usbDevice);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        UserHandle of = UserHandle.of(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(of).setDevicePackage(usbDevice, str, of);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setAccessoryPackage(UsbAccessory usbAccessory, String str, int i) {
        Objects.requireNonNull(usbAccessory);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        UserHandle of = UserHandle.of(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(of).setAccessoryPackage(usbAccessory, str, of);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addDevicePackagesToPreferenceDenied(UsbDevice usbDevice, String[] strArr, UserHandle userHandle) {
        Objects.requireNonNull(usbDevice);
        String[] strArr2 = (String[]) Preconditions.checkArrayElementsNotNull(strArr, "packageNames");
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(userHandle).addDevicePackagesToDenied(usbDevice, strArr2, userHandle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addAccessoryPackagesToPreferenceDenied(UsbAccessory usbAccessory, String[] strArr, UserHandle userHandle) {
        Objects.requireNonNull(usbAccessory);
        String[] strArr2 = (String[]) Preconditions.checkArrayElementsNotNull(strArr, "packageNames");
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(userHandle).addAccessoryPackagesToDenied(usbAccessory, strArr2, userHandle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeDevicePackagesFromPreferenceDenied(UsbDevice usbDevice, String[] strArr, UserHandle userHandle) {
        Objects.requireNonNull(usbDevice);
        String[] strArr2 = (String[]) Preconditions.checkArrayElementsNotNull(strArr, "packageNames");
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(userHandle).removeDevicePackagesFromDenied(usbDevice, strArr2, userHandle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeAccessoryPackagesFromPreferenceDenied(UsbAccessory usbAccessory, String[] strArr, UserHandle userHandle) {
        Objects.requireNonNull(usbAccessory);
        String[] strArr2 = (String[]) Preconditions.checkArrayElementsNotNull(strArr, "packageNames");
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(userHandle).removeAccessoryPackagesFromDenied(usbAccessory, strArr2, userHandle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setDevicePersistentPermission(UsbDevice usbDevice, int i, UserHandle userHandle, boolean z) {
        Objects.requireNonNull(usbDevice);
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mPermissionManager.getPermissionsForUser(userHandle).setDevicePersistentPermission(usbDevice, i, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setAccessoryPersistentPermission(UsbAccessory usbAccessory, int i, UserHandle userHandle, boolean z) {
        Objects.requireNonNull(usbAccessory);
        Objects.requireNonNull(userHandle);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mPermissionManager.getPermissionsForUser(userHandle).setAccessoryPersistentPermission(usbAccessory, i, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean hasDevicePermission(UsbDevice usbDevice, String str) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int userId = UserHandle.getUserId(callingUid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getPermissionsForUser(userId).hasPermission(usbDevice, str, callingPid, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean hasDevicePermissionWithIdentity(UsbDevice usbDevice, String str, int i, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        return getPermissionsForUser(UserHandle.getUserId(i2)).hasPermission(usbDevice, str, i, i2);
    }

    public boolean hasAccessoryPermission(UsbAccessory usbAccessory) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int userId = UserHandle.getUserId(callingUid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getPermissionsForUser(userId).hasPermission(usbAccessory, callingPid, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean hasAccessoryPermissionWithIdentity(UsbAccessory usbAccessory, int i, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        return getPermissionsForUser(UserHandle.getUserId(i2)).hasPermission(usbAccessory, i, i2);
    }

    public void requestDevicePermission(UsbDevice usbDevice, String str, PendingIntent pendingIntent) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int userId = UserHandle.getUserId(callingUid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getPermissionsForUser(userId).requestPermission(usbDevice, str, pendingIntent, callingPid, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void requestAccessoryPermission(UsbAccessory usbAccessory, String str, PendingIntent pendingIntent) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        int userId = UserHandle.getUserId(callingUid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getPermissionsForUser(userId).requestPermission(usbAccessory, str, pendingIntent, callingPid, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void grantDevicePermission(UsbDevice usbDevice, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        int userId = UserHandle.getUserId(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getPermissionsForUser(userId).grantDevicePermission(usbDevice, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void grantAccessoryPermission(UsbAccessory usbAccessory, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        int userId = UserHandle.getUserId(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getPermissionsForUser(userId).grantAccessoryPermission(usbAccessory, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean hasDefaults(String str, int i) {
        String str2 = (String) Preconditions.checkStringNotEmpty(str);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        UserHandle of = UserHandle.of(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSettingsManager.getSettingsForProfileGroup(of).hasDefaults(str2, of);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void clearDefaults(String str, int i) {
        String str2 = (String) Preconditions.checkStringNotEmpty(str);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        UserHandle of = UserHandle.of(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(of).clearDefaults(str2, of);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setCurrentFunctions(long j, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkArgument(UsbManager.areSettableFunctions(j));
        Preconditions.checkState(this.mDeviceManager != null);
        this.mDeviceManager.setCurrentFunctions(j, i);
    }

    public void setCurrentFunction(String str, boolean z, int i) {
        setCurrentFunctions(UsbManager.usbFunctionsFromString(str), i);
    }

    public boolean isFunctionEnabled(String str) {
        return (UsbManager.usbFunctionsFromString(str) & getCurrentFunctions()) != 0;
    }

    public long getCurrentFunctions() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkState(this.mDeviceManager != null);
        return this.mDeviceManager.getCurrentFunctions();
    }

    public void setScreenUnlockedFunctions(long j) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkArgument(UsbManager.areSettableFunctions(j));
        Preconditions.checkState(this.mDeviceManager != null);
        this.mDeviceManager.setScreenUnlockedFunctions(j);
    }

    public long getScreenUnlockedFunctions() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkState(this.mDeviceManager != null);
        return this.mDeviceManager.getScreenUnlockedFunctions();
    }

    public int getCurrentUsbSpeed() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkNotNull(this.mDeviceManager, "DeviceManager must not be null");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDeviceManager.getCurrentUsbSpeed();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getGadgetHalVersion() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkNotNull(this.mDeviceManager, "DeviceManager must not be null");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDeviceManager.getGadgetHalVersion();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void resetUsbGadget() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        Preconditions.checkNotNull(this.mDeviceManager, "DeviceManager must not be null");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDeviceManager.resetUsbGadget();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void resetUsbPort(String str, int i, IUsbOperationInternal iUsbOperationInternal) {
        Objects.requireNonNull(str, "resetUsbPort: portId must not be null. opId:" + i);
        Objects.requireNonNull(iUsbOperationInternal, "resetUsbPort: callback must not be null. opId:" + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.resetUsbPort(str, i, iUsbOperationInternal, null);
            } else {
                try {
                    iUsbOperationInternal.onOperationComplete(1);
                } catch (RemoteException e) {
                    Slog.e("UsbService", "resetUsbPort: Failed to call onOperationComplete", e);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<ParcelableUsbPort> getPorts() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager == null) {
                return null;
            }
            UsbPort[] ports = usbPortManager.getPorts();
            ArrayList arrayList = new ArrayList();
            for (UsbPort usbPort : ports) {
                arrayList.add(ParcelableUsbPort.of(usbPort));
            }
            return arrayList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public UsbPortStatus getPortStatus(String str) {
        Objects.requireNonNull(str, "portId must not be null");
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            return usbPortManager != null ? usbPortManager.getPortStatus(str) : null;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setPortRoles(String str, int i, int i2) {
        Objects.requireNonNull(str, "portId must not be null");
        UsbPort.checkRoles(i, i2);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.setPortRoles(str, i, i2, null);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void enableLimitPowerTransfer(String str, boolean z, int i, IUsbOperationInternal iUsbOperationInternal) {
        Objects.requireNonNull(str, "portId must not be null. opID:" + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.enableLimitPowerTransfer(str, z, i, iUsbOperationInternal, null);
            } else {
                try {
                    iUsbOperationInternal.onOperationComplete(1);
                } catch (RemoteException e) {
                    Slog.e("UsbService", "enableLimitPowerTransfer: Failed to call onOperationComplete", e);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void enableContaminantDetection(String str, boolean z) {
        Objects.requireNonNull(str, "portId must not be null");
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.enableContaminantDetection(str, z, null);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getUsbHalVersion() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                return usbPortManager.getUsbHalVersion();
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean enableUsbData(String str, boolean z, int i, IUsbOperationInternal iUsbOperationInternal) {
        boolean z2;
        Objects.requireNonNull(str, "enableUsbData: portId must not be null. opId:" + i);
        Objects.requireNonNull(iUsbOperationInternal, "enableUsbData: callback must not be null. opId:" + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                z2 = usbPortManager.enableUsbData(str, z, i, iUsbOperationInternal, null);
            } else {
                try {
                    iUsbOperationInternal.onOperationComplete(1);
                } catch (RemoteException e) {
                    Slog.e("UsbService", "enableUsbData: Failed to call onOperationComplete", e);
                }
                z2 = false;
            }
            return z2;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void enableUsbDataWhileDocked(String str, int i, IUsbOperationInternal iUsbOperationInternal) {
        Objects.requireNonNull(str, "enableUsbDataWhileDocked: portId must not be null. opId:" + i);
        Objects.requireNonNull(iUsbOperationInternal, "enableUsbDataWhileDocked: callback must not be null. opId:" + i);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.enableUsbDataWhileDocked(str, i, iUsbOperationInternal, null);
            } else {
                try {
                    iUsbOperationInternal.onOperationComplete(1);
                } catch (RemoteException e) {
                    Slog.e("UsbService", "enableUsbData: Failed to call onOperationComplete", e);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setUsbDeviceConnectionHandler(ComponentName componentName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        synchronized (this.mLock) {
            if (this.mCurrentUserId == UserHandle.getCallingUserId()) {
                UsbHostManager usbHostManager = this.mHostManager;
                if (usbHostManager != null) {
                    usbHostManager.setUsbDeviceConnectionHandler(componentName);
                }
            } else {
                throw new IllegalArgumentException("Only the current user can register a usb connection handler");
            }
        }
    }

    public boolean registerForDisplayPortEvents(IDisplayPortAltModeInfoListener iDisplayPortAltModeInfoListener) {
        Objects.requireNonNull(iDisplayPortAltModeInfoListener, "registerForDisplayPortEvents: listener must not be null.");
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                return usbPortManager.registerForDisplayPortEvents(iDisplayPortAltModeInfoListener);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unregisterForDisplayPortEvents(IDisplayPortAltModeInfoListener iDisplayPortAltModeInfoListener) {
        Objects.requireNonNull(iDisplayPortAltModeInfoListener, "unregisterForDisplayPortEvents: listener must not be null.");
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UsbPortManager usbPortManager = this.mPortManager;
            if (usbPortManager != null) {
                usbPortManager.unregisterForDisplayPortEvents(iDisplayPortAltModeInfoListener);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:280:0x0692, code lost:
        r1 = new com.android.internal.util.dump.DualDumpOutputStream(new android.util.proto.ProtoOutputStream(r25));
     */
    /* JADX WARN: Removed duplicated region for block: B:125:0x01d8  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x01f3  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x0263  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x0284  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x028e A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:164:0x0295 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:167:0x02a0  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x02ae A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x02ba  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x02d9  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x02e3 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:188:0x02ea A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x02f5  */
    /* JADX WARN: Removed duplicated region for block: B:197:0x0305 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0311  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x0331  */
    /* JADX WARN: Removed duplicated region for block: B:211:0x0336 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00c4  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00e3 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0112  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0117 A[Catch: all -> 0x0706, TryCatch #0 {all -> 0x0706, blocks: (B:6:0x0020, B:8:0x0030, B:10:0x0033, B:14:0x0042, B:17:0x0061, B:19:0x0064, B:39:0x009b, B:46:0x00b9, B:66:0x00f3, B:72:0x0113, B:74:0x0117, B:53:0x00cf, B:56:0x00d9, B:59:0x00e3, B:26:0x0079, B:29:0x0081, B:32:0x008b, B:75:0x0130, B:78:0x0143, B:80:0x0147, B:81:0x0150, B:102:0x0184, B:110:0x01ac, B:112:0x01af, B:126:0x01da, B:129:0x01f4, B:117:0x01c0, B:120:0x01ca, B:130:0x01f7, B:132:0x01fb, B:83:0x0154, B:86:0x015e, B:89:0x0168, B:92:0x0170, B:133:0x0219, B:135:0x0226, B:137:0x0229, B:139:0x0235, B:141:0x023e, B:156:0x0266, B:161:0x0286, B:163:0x028e, B:165:0x0297, B:180:0x02bd, B:185:0x02db, B:187:0x02e3, B:189:0x02ec, B:204:0x0314, B:209:0x0332, B:211:0x0336, B:194:0x02fb, B:197:0x0305, B:188:0x02ea, B:170:0x02a6, B:173:0x02ae, B:164:0x0295, B:146:0x024f, B:149:0x0257, B:140:0x023c, B:212:0x0355, B:214:0x0363, B:216:0x0367, B:218:0x036e, B:219:0x0385, B:221:0x0391, B:223:0x0395, B:225:0x039c, B:226:0x03b3, B:228:0x03bf, B:230:0x03c3, B:232:0x03c7, B:233:0x03de, B:235:0x03ea, B:237:0x03ee, B:239:0x0400, B:240:0x041b, B:242:0x0427, B:244:0x042b, B:246:0x0435, B:247:0x044c, B:249:0x0457, B:251:0x045b, B:253:0x0462, B:254:0x0479, B:256:0x0485, B:258:0x0489, B:260:0x04b2, B:261:0x04cd, B:263:0x04d9, B:265:0x04dd, B:267:0x04e4, B:268:0x0503, B:270:0x0510, B:272:0x0514, B:274:0x0518, B:275:0x0527, B:277:0x0532, B:278:0x0539, B:280:0x0692, B:282:0x06ae, B:284:0x06b2, B:285:0x06bc, B:287:0x06c0, B:288:0x06ca, B:290:0x06ce, B:291:0x06d9, B:281:0x069f), top: B:297:0x0020 }] */
    @NeverCompile
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        boolean z;
        char c;
        int i;
        int hashCode;
        boolean z2;
        char c2;
        int i2;
        int hashCode2;
        boolean z3;
        int i3;
        UsbPortManager usbPortManager;
        char c3;
        int i4;
        boolean z4;
        boolean z5;
        int i5;
        int hashCode3;
        boolean z6;
        int i6;
        UsbPortManager usbPortManager2;
        if (DumpUtils.checkDumpPermission(this.mContext, "UsbService", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ArraySet arraySet = new ArraySet();
                Collections.addAll(arraySet, strArr);
                boolean contains = arraySet.contains("--proto");
                if (strArr != null && strArr.length != 0 && !strArr[0].equals("-a") && !contains) {
                    if ("set-port-roles".equals(strArr[0]) && strArr.length == 4) {
                        String str = strArr[1];
                        String str2 = strArr[2];
                        int hashCode4 = str2.hashCode();
                        if (hashCode4 == -896505829) {
                            if (str2.equals("source")) {
                                z5 = false;
                                if (z5) {
                                }
                                String str3 = strArr[3];
                                hashCode3 = str3.hashCode();
                                if (hashCode3 != -1335157162) {
                                }
                            }
                            z5 = true;
                            if (z5) {
                            }
                            String str32 = strArr[3];
                            hashCode3 = str32.hashCode();
                            if (hashCode3 != -1335157162) {
                            }
                        } else if (hashCode4 != -440560135) {
                            if (hashCode4 == 3530387 && str2.equals("sink")) {
                                z5 = true;
                                if (z5) {
                                    i5 = 1;
                                } else if (z5) {
                                    i5 = 2;
                                } else if (!z5) {
                                    indentingPrintWriter.println("Invalid power role: " + strArr[2]);
                                    return;
                                } else {
                                    i5 = 0;
                                }
                                String str322 = strArr[3];
                                hashCode3 = str322.hashCode();
                                if (hashCode3 != -1335157162) {
                                    if (str322.equals("device")) {
                                        z6 = true;
                                        if (z6) {
                                        }
                                        usbPortManager2 = this.mPortManager;
                                        if (usbPortManager2 != null) {
                                        }
                                    }
                                    z6 = true;
                                    if (z6) {
                                    }
                                    usbPortManager2 = this.mPortManager;
                                    if (usbPortManager2 != null) {
                                    }
                                } else if (hashCode3 != 3208616) {
                                    if (hashCode3 == 2063627318 && str322.equals("no-data")) {
                                        z6 = true;
                                        if (z6) {
                                            i6 = 1;
                                        } else if (z6) {
                                            i6 = 2;
                                        } else if (!z6) {
                                            indentingPrintWriter.println("Invalid data role: " + strArr[3]);
                                            return;
                                        } else {
                                            i6 = 0;
                                        }
                                        usbPortManager2 = this.mPortManager;
                                        if (usbPortManager2 != null) {
                                            usbPortManager2.setPortRoles(str, i5, i6, indentingPrintWriter);
                                            indentingPrintWriter.println();
                                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                                        }
                                    }
                                    z6 = true;
                                    if (z6) {
                                    }
                                    usbPortManager2 = this.mPortManager;
                                    if (usbPortManager2 != null) {
                                    }
                                } else {
                                    if (str322.equals("host")) {
                                        z6 = false;
                                        if (z6) {
                                        }
                                        usbPortManager2 = this.mPortManager;
                                        if (usbPortManager2 != null) {
                                        }
                                    }
                                    z6 = true;
                                    if (z6) {
                                    }
                                    usbPortManager2 = this.mPortManager;
                                    if (usbPortManager2 != null) {
                                    }
                                }
                            }
                            z5 = true;
                            if (z5) {
                            }
                            String str3222 = strArr[3];
                            hashCode3 = str3222.hashCode();
                            if (hashCode3 != -1335157162) {
                            }
                        } else {
                            if (str2.equals("no-power")) {
                                z5 = true;
                                if (z5) {
                                }
                                String str32222 = strArr[3];
                                hashCode3 = str32222.hashCode();
                                if (hashCode3 != -1335157162) {
                                }
                            }
                            z5 = true;
                            if (z5) {
                            }
                            String str322222 = strArr[3];
                            hashCode3 = str322222.hashCode();
                            if (hashCode3 != -1335157162) {
                            }
                        }
                    } else if ("add-port".equals(strArr[0]) && strArr.length >= 3) {
                        String str4 = strArr[1];
                        String str5 = strArr[2];
                        switch (str5.hashCode()) {
                            case 99374:
                                if (str5.equals("dfp")) {
                                    c3 = 1;
                                    break;
                                }
                                c3 = 65535;
                                break;
                            case 115711:
                                if (str5.equals("ufp")) {
                                    c3 = 0;
                                    break;
                                }
                                c3 = 65535;
                                break;
                            case 3094652:
                                if (str5.equals("dual")) {
                                    c3 = 2;
                                    break;
                                }
                                c3 = 65535;
                                break;
                            case 3387192:
                                if (str5.equals("none")) {
                                    c3 = 3;
                                    break;
                                }
                                c3 = 65535;
                                break;
                            default:
                                c3 = 65535;
                                break;
                        }
                        if (c3 == 0) {
                            i4 = 1;
                        } else if (c3 == 1) {
                            i4 = 2;
                        } else if (c3 == 2) {
                            i4 = 3;
                        } else if (c3 != 3) {
                            indentingPrintWriter.println("Invalid mode: " + strArr[2]);
                            return;
                        } else {
                            i4 = 0;
                        }
                        boolean z7 = false;
                        boolean z8 = false;
                        for (int i7 = 3; i7 < strArr.length; i7++) {
                            String str6 = strArr[i7];
                            int hashCode5 = str6.hashCode();
                            if (hashCode5 != -1384270173) {
                                if (hashCode5 == 407165993 && str6.equals("--compliance-warnings")) {
                                    z4 = false;
                                    if (z4) {
                                        z7 = true;
                                    } else if (!z4) {
                                        indentingPrintWriter.println("Invalid Identifier: " + strArr[i7]);
                                    } else {
                                        z8 = true;
                                    }
                                }
                                z4 = true;
                                if (z4) {
                                }
                            } else {
                                if (str6.equals("--displayport")) {
                                    z4 = true;
                                    if (z4) {
                                    }
                                }
                                z4 = true;
                                if (z4) {
                                }
                            }
                        }
                        UsbPortManager usbPortManager3 = this.mPortManager;
                        if (usbPortManager3 != null) {
                            usbPortManager3.addSimulatedPort(str4, i4, z7, z8, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("connect-port".equals(strArr[0]) && strArr.length == 5) {
                        String str7 = strArr[1];
                        boolean endsWith = strArr[2].endsWith("?");
                        String removeLastChar = endsWith ? removeLastChar(strArr[2]) : strArr[2];
                        int hashCode6 = removeLastChar.hashCode();
                        if (hashCode6 != 99374) {
                            if (hashCode6 == 115711 && removeLastChar.equals("ufp")) {
                                z = false;
                                if (z) {
                                    c = 3;
                                    i = 1;
                                } else if (!z) {
                                    indentingPrintWriter.println("Invalid mode: " + strArr[2]);
                                    return;
                                } else {
                                    c = 3;
                                    i = 2;
                                }
                                boolean endsWith2 = strArr[c].endsWith("?");
                                String removeLastChar2 = !endsWith2 ? removeLastChar(strArr[c]) : strArr[c];
                                hashCode = removeLastChar2.hashCode();
                                if (hashCode == -896505829) {
                                    if (hashCode == 3530387 && removeLastChar2.equals("sink")) {
                                        z2 = true;
                                        if (z2) {
                                            c2 = 4;
                                            i2 = 1;
                                        } else if (!z2) {
                                            indentingPrintWriter.println("Invalid power role: " + strArr[3]);
                                            return;
                                        } else {
                                            c2 = 4;
                                            i2 = 2;
                                        }
                                        boolean endsWith3 = strArr[c2].endsWith("?");
                                        String removeLastChar3 = !endsWith3 ? removeLastChar(strArr[c2]) : strArr[c2];
                                        hashCode2 = removeLastChar3.hashCode();
                                        if (hashCode2 == -1335157162) {
                                            if (hashCode2 == 3208616 && removeLastChar3.equals("host")) {
                                                z3 = false;
                                                if (z3) {
                                                    i3 = 1;
                                                } else if (!z3) {
                                                    indentingPrintWriter.println("Invalid data role: " + strArr[4]);
                                                    return;
                                                } else {
                                                    i3 = 2;
                                                }
                                                usbPortManager = this.mPortManager;
                                                if (usbPortManager != null) {
                                                    usbPortManager.connectSimulatedPort(str7, i, endsWith, i2, endsWith2, i3, endsWith3, indentingPrintWriter);
                                                    indentingPrintWriter.println();
                                                    this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                                                }
                                            }
                                            z3 = true;
                                            if (z3) {
                                            }
                                            usbPortManager = this.mPortManager;
                                            if (usbPortManager != null) {
                                            }
                                        } else {
                                            if (removeLastChar3.equals("device")) {
                                                z3 = true;
                                                if (z3) {
                                                }
                                                usbPortManager = this.mPortManager;
                                                if (usbPortManager != null) {
                                                }
                                            }
                                            z3 = true;
                                            if (z3) {
                                            }
                                            usbPortManager = this.mPortManager;
                                            if (usbPortManager != null) {
                                            }
                                        }
                                    }
                                    z2 = true;
                                    if (z2) {
                                    }
                                    boolean endsWith32 = strArr[c2].endsWith("?");
                                    if (!endsWith32) {
                                    }
                                    hashCode2 = removeLastChar3.hashCode();
                                    if (hashCode2 == -1335157162) {
                                    }
                                } else {
                                    if (removeLastChar2.equals("source")) {
                                        z2 = false;
                                        if (z2) {
                                        }
                                        boolean endsWith322 = strArr[c2].endsWith("?");
                                        if (!endsWith322) {
                                        }
                                        hashCode2 = removeLastChar3.hashCode();
                                        if (hashCode2 == -1335157162) {
                                        }
                                    }
                                    z2 = true;
                                    if (z2) {
                                    }
                                    boolean endsWith3222 = strArr[c2].endsWith("?");
                                    if (!endsWith3222) {
                                    }
                                    hashCode2 = removeLastChar3.hashCode();
                                    if (hashCode2 == -1335157162) {
                                    }
                                }
                            }
                            z = true;
                            if (z) {
                            }
                            boolean endsWith22 = strArr[c].endsWith("?");
                            if (!endsWith22) {
                            }
                            hashCode = removeLastChar2.hashCode();
                            if (hashCode == -896505829) {
                            }
                        } else {
                            if (removeLastChar.equals("dfp")) {
                                z = true;
                                if (z) {
                                }
                                boolean endsWith222 = strArr[c].endsWith("?");
                                if (!endsWith222) {
                                }
                                hashCode = removeLastChar2.hashCode();
                                if (hashCode == -896505829) {
                                }
                            }
                            z = true;
                            if (z) {
                            }
                            boolean endsWith2222 = strArr[c].endsWith("?");
                            if (!endsWith2222) {
                            }
                            hashCode = removeLastChar2.hashCode();
                            if (hashCode == -896505829) {
                            }
                        }
                    } else if ("disconnect-port".equals(strArr[0]) && strArr.length == 2) {
                        String str8 = strArr[1];
                        UsbPortManager usbPortManager4 = this.mPortManager;
                        if (usbPortManager4 != null) {
                            usbPortManager4.disconnectSimulatedPort(str8, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("remove-port".equals(strArr[0]) && strArr.length == 2) {
                        String str9 = strArr[1];
                        UsbPortManager usbPortManager5 = this.mPortManager;
                        if (usbPortManager5 != null) {
                            usbPortManager5.removeSimulatedPort(str9, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("reset".equals(strArr[0]) && strArr.length == 1) {
                        UsbPortManager usbPortManager6 = this.mPortManager;
                        if (usbPortManager6 != null) {
                            usbPortManager6.resetSimulation(indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("set-contaminant-status".equals(strArr[0]) && strArr.length == 3) {
                        String str10 = strArr[1];
                        Boolean valueOf = Boolean.valueOf(Boolean.parseBoolean(strArr[2]));
                        UsbPortManager usbPortManager7 = this.mPortManager;
                        if (usbPortManager7 != null) {
                            usbPortManager7.simulateContaminantStatus(str10, valueOf.booleanValue(), indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("set-compliance-reasons".equals(strArr[0]) && strArr.length == 3) {
                        String str11 = strArr[1];
                        String str12 = strArr[2];
                        UsbPortManager usbPortManager8 = this.mPortManager;
                        if (usbPortManager8 != null) {
                            usbPortManager8.simulateComplianceWarnings(str11, str12, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("clear-compliance-reasons".equals(strArr[0]) && strArr.length == 2) {
                        String str13 = strArr[1];
                        UsbPortManager usbPortManager9 = this.mPortManager;
                        if (usbPortManager9 != null) {
                            usbPortManager9.simulateComplianceWarnings(str13, "", indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("set-displayport-status".equals(strArr[0]) && strArr.length == 7) {
                        String str14 = strArr[1];
                        int parseInt = Integer.parseInt(strArr[2]);
                        int parseInt2 = Integer.parseInt(strArr[3]);
                        int parseInt3 = Integer.parseInt(strArr[4]);
                        boolean parseBoolean = Boolean.parseBoolean(strArr[5]);
                        int parseInt4 = Integer.parseInt(strArr[6]);
                        UsbPortManager usbPortManager10 = this.mPortManager;
                        if (usbPortManager10 != null) {
                            usbPortManager10.simulateDisplayPortAltModeInfo(str14, parseInt, parseInt2, parseInt3, parseBoolean, parseInt4, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("reset-displayport-status".equals(strArr[0]) && strArr.length == 2) {
                        String str15 = strArr[1];
                        UsbPortManager usbPortManager11 = this.mPortManager;
                        if (usbPortManager11 != null) {
                            usbPortManager11.simulateDisplayPortAltModeInfo(str15, 0, 0, 0, false, 0, indentingPrintWriter);
                            indentingPrintWriter.println();
                            this.mPortManager.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("ports".equals(strArr[0]) && strArr.length == 1) {
                        UsbPortManager usbPortManager12 = this.mPortManager;
                        if (usbPortManager12 != null) {
                            usbPortManager12.dump(new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  ")), "", 0L);
                        }
                    } else if ("dump-descriptors".equals(strArr[0])) {
                        this.mHostManager.dumpDescriptors(indentingPrintWriter, strArr);
                    } else {
                        indentingPrintWriter.println("Dump current USB state or issue command:");
                        indentingPrintWriter.println("  ports");
                        indentingPrintWriter.println("  set-port-roles <id> <source|sink|no-power> <host|device|no-data>");
                        indentingPrintWriter.println("  add-port <id> <ufp|dfp|dual|none> <optional args>");
                        indentingPrintWriter.println("    <optional args> include:");
                        indentingPrintWriter.println("      --compliance-warnings: enables compliance warnings on port");
                        indentingPrintWriter.println("      --displayport: enables DisplayPort Alt Mode on port");
                        indentingPrintWriter.println("  connect-port <id> <ufp|dfp><?> <source|sink><?> <host|device><?>");
                        indentingPrintWriter.println("    (add ? suffix if mode, power role, or data role can be changed)");
                        indentingPrintWriter.println("  disconnect-port <id>");
                        indentingPrintWriter.println("  remove-port <id>");
                        indentingPrintWriter.println("  reset");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB type C port role switch:");
                        indentingPrintWriter.println("  dumpsys usb set-port-roles \"default\" source device");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB type C port simulation with full capabilities:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" dual --compliance-warnings --displayport");
                        indentingPrintWriter.println("  dumpsys usb connect-port \"matrix\" ufp? sink? device?");
                        indentingPrintWriter.println("  dumpsys usb ports");
                        indentingPrintWriter.println("  dumpsys usb disconnect-port \"matrix\"");
                        indentingPrintWriter.println("  dumpsys usb remove-port \"matrix\"");
                        indentingPrintWriter.println("  dumpsys usb reset");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB type C port where only power role can be changed:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" dual");
                        indentingPrintWriter.println("  dumpsys usb connect-port \"matrix\" dfp source? host");
                        indentingPrintWriter.println("  dumpsys usb reset");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB OTG port where id pin determines function:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" dual");
                        indentingPrintWriter.println("  dumpsys usb connect-port \"matrix\" dfp source host");
                        indentingPrintWriter.println("  dumpsys usb reset");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB device-only port:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" ufp");
                        indentingPrintWriter.println("  dumpsys usb connect-port \"matrix\" ufp sink device");
                        indentingPrintWriter.println("  dumpsys usb reset");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example simulate contaminant status:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" ufp");
                        indentingPrintWriter.println("  dumpsys usb set-contaminant-status \"matrix\" true");
                        indentingPrintWriter.println("  dumpsys usb set-contaminant-status \"matrix\" false");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example simulate compliance warnings:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" dual --compliance-warnings");
                        indentingPrintWriter.println("  dumpsys usb set-compliance-reasons \"matrix\" <reason-list>");
                        indentingPrintWriter.println("  dumpsys usb clear-compliance-reasons \"matrix\"");
                        indentingPrintWriter.println("<reason-list> is expected to be formatted as \"1, ..., 4\"");
                        indentingPrintWriter.println("with reasons that need to be simulated.");
                        indentingPrintWriter.println("  1: other");
                        indentingPrintWriter.println("  2: debug accessory");
                        indentingPrintWriter.println("  3: bc12");
                        indentingPrintWriter.println("  4: missing rp");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example simulate DisplayPort Alt Mode Changes:");
                        indentingPrintWriter.println("  dumpsys usb add-port \"matrix\" dual --displayport");
                        indentingPrintWriter.println("  dumpsys usb set-displayport-status \"matrix\" <partner-sink> <cable> <num-lanes> <hpd> <link-training-status>");
                        indentingPrintWriter.println("The required fields are as followed:");
                        indentingPrintWriter.println("    <partner-sink>: type DisplayPortAltModeStatus");
                        indentingPrintWriter.println("    <cable>: type DisplayPortAltModeStatus");
                        indentingPrintWriter.println("    <num-lanes>: type int, expected 0, 2, or 4");
                        indentingPrintWriter.println("    <hpd>: type boolean, expected true or false");
                        indentingPrintWriter.println("    <link-training-status>: type LinkTrainingStatus");
                        indentingPrintWriter.println("  dumpsys usb reset-displayport-status \"matrix\"");
                        indentingPrintWriter.println("reset-displayport-status can also be used in order to set");
                        indentingPrintWriter.println("the DisplayPortInfo to default values.");
                        indentingPrintWriter.println();
                        indentingPrintWriter.println("Example USB device descriptors:");
                        indentingPrintWriter.println("  dumpsys usb dump-descriptors -dump-short");
                        indentingPrintWriter.println("  dumpsys usb dump-descriptors -dump-tree");
                        indentingPrintWriter.println("  dumpsys usb dump-descriptors -dump-list");
                        indentingPrintWriter.println("  dumpsys usb dump-descriptors -dump-raw");
                    }
                }
                indentingPrintWriter.println("USB MANAGER STATE (dumpsys usb):");
                DualDumpOutputStream dualDumpOutputStream = new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  "));
                UsbDeviceManager usbDeviceManager = this.mDeviceManager;
                if (usbDeviceManager != null) {
                    usbDeviceManager.dump(dualDumpOutputStream, "device_manager", 1146756268033L);
                }
                UsbHostManager usbHostManager = this.mHostManager;
                if (usbHostManager != null) {
                    usbHostManager.dump(dualDumpOutputStream, "host_manager", 1146756268034L);
                }
                UsbPortManager usbPortManager13 = this.mPortManager;
                if (usbPortManager13 != null) {
                    usbPortManager13.dump(dualDumpOutputStream, "port_manager", 1146756268035L);
                }
                this.mAlsaManager.dump(dualDumpOutputStream, "alsa_manager", 1146756268036L);
                this.mSettingsManager.dump(dualDumpOutputStream, "settings_manager", 1146756268037L);
                this.mPermissionManager.dump(dualDumpOutputStream, "permissions_manager", 1146756268038L);
                dualDumpOutputStream.flush();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}
