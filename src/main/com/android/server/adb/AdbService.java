package com.android.server.adb;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.debug.AdbManagerInternal;
import android.debug.FingerprintAndPairDevice;
import android.debug.IAdbCallback;
import android.debug.IAdbManager;
import android.debug.IAdbTransport;
import android.debug.PairDevice;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.sysprop.AdbProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.adb.AdbDebuggingManager;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AdbService extends IAdbManager.Stub {
    public final RemoteCallbackList<IAdbCallback> mCallbacks;
    public AtomicInteger mConnectionPort;
    public AdbDebuggingManager.AdbConnectionPortPoller mConnectionPortPoller;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    public AdbDebuggingManager mDebuggingManager;
    public boolean mIsAdbUsbEnabled;
    public boolean mIsAdbWifiEnabled;
    public ContentObserver mObserver;
    public final AdbConnectionPortListener mPortListener;
    public final ArrayMap<IBinder, IAdbTransport> mTransports;

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public AdbService mAdbService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.adb.AdbService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? adbService = new AdbService(getContext());
            this.mAdbService = adbService;
            publishBinderService("adb", adbService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 550) {
                this.mAdbService.systemReady();
            } else if (i == 1000) {
                FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.adb.AdbService$Lifecycle$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((AdbService) obj).bootCompleted();
                    }
                }, this.mAdbService));
            }
        }
    }

    /* loaded from: classes.dex */
    public class AdbManagerInternalImpl extends AdbManagerInternal {
        public AdbManagerInternalImpl() {
            AdbService.this = r1;
        }

        public void registerTransport(IAdbTransport iAdbTransport) {
            AdbService.this.mTransports.put(iAdbTransport.asBinder(), iAdbTransport);
        }

        public void unregisterTransport(IAdbTransport iAdbTransport) {
            AdbService.this.mTransports.remove(iAdbTransport.asBinder());
        }

        public boolean isAdbEnabled(byte b) {
            if (b == 0) {
                return AdbService.this.mIsAdbUsbEnabled;
            }
            if (b == 1) {
                return AdbService.this.mIsAdbWifiEnabled;
            }
            throw new IllegalArgumentException("isAdbEnabled called with unimplemented transport type=" + ((int) b));
        }

        public File getAdbKeysFile() {
            if (AdbService.this.mDebuggingManager == null) {
                return null;
            }
            return AdbService.this.mDebuggingManager.getUserKeyFile();
        }

        public File getAdbTempKeysFile() {
            if (AdbService.this.mDebuggingManager == null) {
                return null;
            }
            return AdbService.this.mDebuggingManager.getAdbTempKeysFile();
        }

        public void notifyKeyFilesUpdated() {
            if (AdbService.this.mDebuggingManager == null) {
                return;
            }
            AdbService.this.mDebuggingManager.notifyKeyFilesUpdated();
        }

        public void startAdbdForTransport(byte b) {
            FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new AdbService$AdbManagerInternalImpl$$ExternalSyntheticLambda0(), AdbService.this, Boolean.TRUE, Byte.valueOf(b)));
        }

        public void stopAdbdForTransport(byte b) {
            FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new AdbService$AdbManagerInternalImpl$$ExternalSyntheticLambda0(), AdbService.this, Boolean.FALSE, Byte.valueOf(b)));
        }
    }

    public final void registerContentObservers() {
        try {
            this.mObserver = new AdbSettingsObserver();
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("adb_enabled"), false, this.mObserver);
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("adb_wifi_enabled"), false, this.mObserver);
        } catch (Exception e) {
            Slog.e("AdbService", "Error in registerContentObservers", e);
        }
    }

    public static boolean containsFunction(String str, String str2) {
        int indexOf = str.indexOf(str2);
        if (indexOf < 0) {
            return false;
        }
        if (indexOf <= 0 || str.charAt(indexOf - 1) == ',') {
            int length = indexOf + str2.length();
            return length >= str.length() || str.charAt(length) == ',';
        }
        return false;
    }

    /* loaded from: classes.dex */
    public class AdbSettingsObserver extends ContentObserver {
        public final Uri mAdbUsbUri;
        public final Uri mAdbWifiUri;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AdbSettingsObserver() {
            super(null);
            AdbService.this = r1;
            this.mAdbUsbUri = Settings.Global.getUriFor("adb_enabled");
            this.mAdbWifiUri = Settings.Global.getUriFor("adb_wifi_enabled");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.mAdbUsbUri.equals(uri)) {
                FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.adb.AdbService$AdbSettingsObserver$$ExternalSyntheticLambda0
                    public final void accept(Object obj, Object obj2, Object obj3) {
                        ((AdbService) obj).setAdbEnabled(((Boolean) obj2).booleanValue(), ((Byte) obj3).byteValue());
                    }
                }, AdbService.this, Boolean.valueOf(Settings.Global.getInt(AdbService.this.mContentResolver, "adb_enabled", 0) > 0), (byte) 0));
            } else if (this.mAdbWifiUri.equals(uri)) {
                FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.adb.AdbService$AdbSettingsObserver$$ExternalSyntheticLambda0
                    public final void accept(Object obj, Object obj2, Object obj3) {
                        ((AdbService) obj).setAdbEnabled(((Boolean) obj2).booleanValue(), ((Byte) obj3).byteValue());
                    }
                }, AdbService.this, Boolean.valueOf(Settings.Global.getInt(AdbService.this.mContentResolver, "adb_wifi_enabled", 0) > 0), (byte) 1));
            }
        }
    }

    public AdbService(Context context) {
        this.mConnectionPort = new AtomicInteger(-1);
        this.mPortListener = new AdbConnectionPortListener();
        this.mCallbacks = new RemoteCallbackList<>();
        this.mTransports = new ArrayMap<>();
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mDebuggingManager = new AdbDebuggingManager(context);
        registerContentObservers();
        LocalServices.addService(AdbManagerInternal.class, new AdbManagerInternalImpl());
    }

    public void systemReady() {
        boolean containsFunction = containsFunction(SystemProperties.get("persist.sys.usb.config", ""), "adb");
        this.mIsAdbUsbEnabled = containsFunction;
        int i = 1;
        boolean z = containsFunction || SystemProperties.getBoolean("persist.sys.test_harness", false);
        this.mIsAdbWifiEnabled = "1".equals(SystemProperties.get("persist.adb.tls_server.enable", "0"));
        try {
            Settings.Global.putInt(this.mContentResolver, "adb_enabled", z ? 1 : 0);
            ContentResolver contentResolver = this.mContentResolver;
            if (!this.mIsAdbWifiEnabled) {
                i = 0;
            }
            Settings.Global.putInt(contentResolver, "adb_wifi_enabled", i);
        } catch (SecurityException unused) {
            Slog.d("AdbService", "ADB_ENABLED is restricted.");
        }
    }

    public void bootCompleted() {
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.setAdbEnabled(this.mIsAdbUsbEnabled, (byte) 0);
            this.mDebuggingManager.setAdbEnabled(this.mIsAdbWifiEnabled, (byte) 1);
        }
    }

    public void allowDebugging(boolean z, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        Preconditions.checkStringNotEmpty(str);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.allowDebugging(z, str);
        }
    }

    public void denyDebugging() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.denyDebugging();
        }
    }

    public void clearDebuggingKeys() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.clearDebuggingKeys();
            return;
        }
        throw new RuntimeException("Cannot clear ADB debugging keys, AdbDebuggingManager not enabled");
    }

    public boolean isAdbWifiSupported() {
        this.mContext.enforceCallingPermission("android.permission.MANAGE_DEBUGGING", "AdbService");
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi") || this.mContext.getPackageManager().hasSystemFeature("android.hardware.ethernet");
    }

    public boolean isAdbWifiQrSupported() {
        this.mContext.enforceCallingPermission("android.permission.MANAGE_DEBUGGING", "AdbService");
        return isAdbWifiSupported() && this.mContext.getPackageManager().hasSystemFeature("android.hardware.camera.any");
    }

    public void allowWirelessDebugging(boolean z, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        Preconditions.checkStringNotEmpty(str);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.allowWirelessDebugging(z, str);
        }
    }

    public void denyWirelessDebugging() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.denyWirelessDebugging();
        }
    }

    public FingerprintAndPairDevice[] getPairedDevices() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager == null) {
            return null;
        }
        Map<String, PairDevice> pairedDevices = adbDebuggingManager.getPairedDevices();
        FingerprintAndPairDevice[] fingerprintAndPairDeviceArr = new FingerprintAndPairDevice[pairedDevices.size()];
        int i = 0;
        for (Map.Entry<String, PairDevice> entry : pairedDevices.entrySet()) {
            FingerprintAndPairDevice fingerprintAndPairDevice = new FingerprintAndPairDevice();
            fingerprintAndPairDeviceArr[i] = fingerprintAndPairDevice;
            fingerprintAndPairDevice.keyFingerprint = entry.getKey();
            fingerprintAndPairDeviceArr[i].device = entry.getValue();
            i++;
        }
        return fingerprintAndPairDeviceArr;
    }

    public void unpairDevice(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        Preconditions.checkStringNotEmpty(str);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.unpairDevice(str);
        }
    }

    public void enablePairingByPairingCode() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.enablePairingByPairingCode();
        }
    }

    public void enablePairingByQrCode(String str, String str2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        Preconditions.checkStringNotEmpty(str);
        Preconditions.checkStringNotEmpty(str2);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.enablePairingByQrCode(str, str2);
        }
    }

    public void disablePairing() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.disablePairing();
        }
    }

    public int getAdbWirelessPort() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_DEBUGGING", null);
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            return adbDebuggingManager.getAdbWirelessPort();
        }
        return this.mConnectionPort.get();
    }

    public void registerCallback(IAdbCallback iAdbCallback) throws RemoteException {
        this.mCallbacks.register(iAdbCallback);
    }

    public void unregisterCallback(IAdbCallback iAdbCallback) throws RemoteException {
        this.mCallbacks.unregister(iAdbCallback);
    }

    /* loaded from: classes.dex */
    public class AdbConnectionPortListener implements AdbDebuggingManager.AdbConnectionPortListener {
        public AdbConnectionPortListener() {
            AdbService.this = r1;
        }

        @Override // com.android.server.adb.AdbDebuggingManager.AdbConnectionPortListener
        public void onPortReceived(int i) {
            if (i > 0 && i <= 65535) {
                AdbService.this.mConnectionPort.set(i);
            } else {
                AdbService.this.mConnectionPort.set(-1);
                try {
                    Settings.Global.putInt(AdbService.this.mContentResolver, "adb_wifi_enabled", 0);
                } catch (SecurityException unused) {
                    Slog.d("AdbService", "ADB_ENABLED is restricted.");
                }
            }
            AdbService adbService = AdbService.this;
            adbService.broadcastPortInfo(adbService.mConnectionPort.get());
        }
    }

    public final void broadcastPortInfo(int i) {
        Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_STATUS");
        intent.putExtra("status", i >= 0 ? 4 : 5);
        intent.putExtra("adb_port", i);
        AdbDebuggingManager.sendBroadcastWithDebugPermission(this.mContext, intent, UserHandle.ALL);
        Slog.i("AdbService", "sent port broadcast port=" + i);
    }

    public final void startAdbd() {
        SystemProperties.set("ctl.start", "adbd");
    }

    public final void stopAdbd() {
        if (this.mIsAdbUsbEnabled || this.mIsAdbWifiEnabled) {
            return;
        }
        SystemProperties.set("ctl.stop", "adbd");
    }

    public final void setAdbdEnabledForTransport(boolean z, byte b) {
        if (b == 0) {
            this.mIsAdbUsbEnabled = z;
        } else if (b == 1) {
            this.mIsAdbWifiEnabled = z;
        }
        if (z) {
            startAdbd();
        } else {
            stopAdbd();
        }
    }

    public final void setAdbEnabled(final boolean z, final byte b) {
        if (b == 0 && z != this.mIsAdbUsbEnabled) {
            this.mIsAdbUsbEnabled = z;
        } else if (b != 1 || z == this.mIsAdbWifiEnabled) {
            return;
        } else {
            this.mIsAdbWifiEnabled = z;
            if (z) {
                if (!((Boolean) AdbProperties.secure().orElse(Boolean.FALSE)).booleanValue() && this.mDebuggingManager == null) {
                    SystemProperties.set("persist.adb.tls_server.enable", "1");
                    AdbDebuggingManager.AdbConnectionPortPoller adbConnectionPortPoller = new AdbDebuggingManager.AdbConnectionPortPoller(this.mPortListener);
                    this.mConnectionPortPoller = adbConnectionPortPoller;
                    adbConnectionPortPoller.start();
                }
            } else {
                SystemProperties.set("persist.adb.tls_server.enable", "0");
                AdbDebuggingManager.AdbConnectionPortPoller adbConnectionPortPoller2 = this.mConnectionPortPoller;
                if (adbConnectionPortPoller2 != null) {
                    adbConnectionPortPoller2.cancelAndWait();
                    this.mConnectionPortPoller = null;
                }
            }
        }
        if (z) {
            startAdbd();
        } else {
            stopAdbd();
        }
        for (IAdbTransport iAdbTransport : this.mTransports.values()) {
            try {
                iAdbTransport.onAdbEnabled(z, b);
            } catch (RemoteException unused) {
                Slog.w("AdbService", "Unable to send onAdbEnabled to transport " + iAdbTransport.toString());
            }
        }
        AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
        if (adbDebuggingManager != null) {
            adbDebuggingManager.setAdbEnabled(z, b);
        }
        this.mCallbacks.broadcast(new Consumer() { // from class: com.android.server.adb.AdbService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AdbService.lambda$setAdbEnabled$0(z, b, (IAdbCallback) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$setAdbEnabled$0(boolean z, byte b, IAdbCallback iAdbCallback) {
        try {
            iAdbCallback.onDebuggingChanged(z, b);
        } catch (RemoteException unused) {
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int handleShellCommand(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, String[] strArr) {
        return new AdbShellCommand(this).exec(this, parcelFileDescriptor.getFileDescriptor(), parcelFileDescriptor2.getFileDescriptor(), parcelFileDescriptor3.getFileDescriptor(), strArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x0042, code lost:
        r7 = new com.android.internal.util.dump.DualDumpOutputStream(new android.util.proto.ProtoOutputStream(r6));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "AdbService", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ArraySet arraySet = new ArraySet();
                Collections.addAll(arraySet, strArr);
                boolean contains = arraySet.contains("--proto");
                if (arraySet.size() != 0 && !arraySet.contains("-a") && !contains) {
                    indentingPrintWriter.println("Dump current ADB state");
                    indentingPrintWriter.println("  No commands available");
                }
                indentingPrintWriter.println("ADB MANAGER STATE (dumpsys adb):");
                DualDumpOutputStream dualDumpOutputStream = new DualDumpOutputStream(new IndentingPrintWriter(indentingPrintWriter, "  "));
                AdbDebuggingManager adbDebuggingManager = this.mDebuggingManager;
                if (adbDebuggingManager != null) {
                    adbDebuggingManager.dump(dualDumpOutputStream, "debugging_manager", 1146756268033L);
                }
                dualDumpOutputStream.flush();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
