package com.android.server.usb;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.debug.AdbManagerInternal;
import android.debug.AdbNotifications;
import android.debug.IAdbTransport;
import android.hardware.usb.ParcelableUsbPort;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.hardware.usb.gadget.V1_0.GadgetFunction;
import android.hidl.manager.V1_0.IServiceNotification;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.SomeArgs;
import com.android.internal.usb.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.usb.hal.gadget.UsbGadgetHal;
import com.android.server.usb.hal.gadget.UsbGadgetHalInstance;
import com.android.server.utils.EventLogger;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes2.dex */
public class UsbDeviceManager implements ActivityTaskManagerInternal.ScreenObserver {
    public static final String TAG = "UsbDeviceManager";
    public static UsbGadgetHal mUsbGadgetHal;
    public static Set<Integer> sDenyInterfaces;
    public static EventLogger sEventLogger;
    public static final AtomicInteger sUsbOperationCount = new AtomicInteger();
    @GuardedBy({"mLock"})
    public String[] mAccessoryStrings;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    public HashMap<Long, FileDescriptor> mControlFds;
    @GuardedBy({"mLock"})
    public UsbProfileGroupSettingsManager mCurrentSettings;
    public UsbHandler mHandler;
    public final boolean mHasUsbAccessory;
    public final Object mLock = new Object();
    public final UEventObserver mUEventObserver;

    private native String[] nativeGetAccessoryStrings();

    private native int nativeGetAudioMode();

    private native boolean nativeIsStartRequested();

    private native ParcelFileDescriptor nativeOpenAccessory();

    private native FileDescriptor nativeOpenControl(String str);

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.ScreenObserver
    public void onAwakeStateChanged(boolean z) {
    }

    static {
        HashSet hashSet = new HashSet();
        sDenyInterfaces = hashSet;
        hashSet.add(1);
        sDenyInterfaces.add(2);
        sDenyInterfaces.add(3);
        sDenyInterfaces.add(7);
        sDenyInterfaces.add(8);
        sDenyInterfaces.add(9);
        sDenyInterfaces.add(10);
        sDenyInterfaces.add(11);
        sDenyInterfaces.add(13);
        sDenyInterfaces.add(14);
        sDenyInterfaces.add(224);
    }

    /* loaded from: classes2.dex */
    public final class UsbUEventObserver extends UEventObserver {
        public UsbUEventObserver() {
        }

        public void onUEvent(UEventObserver.UEvent uEvent) {
            if (UsbDeviceManager.sEventLogger != null) {
                EventLogger eventLogger = UsbDeviceManager.sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("USB UEVENT: " + uEvent.toString()));
            }
            String str = uEvent.get("USB_STATE");
            String str2 = uEvent.get("ACCESSORY");
            if (str != null) {
                UsbDeviceManager.this.mHandler.updateState(str);
            } else if ("GETPROTOCOL".equals(str2)) {
                UsbDeviceManager.this.mHandler.setAccessoryUEventTime(SystemClock.elapsedRealtime());
                UsbDeviceManager.this.resetAccessoryHandshakeTimeoutHandler();
            } else if ("SENDSTRING".equals(str2)) {
                UsbDeviceManager.this.mHandler.sendEmptyMessage(21);
                UsbDeviceManager.this.resetAccessoryHandshakeTimeoutHandler();
            } else if ("START".equals(str2)) {
                UsbDeviceManager.this.mHandler.removeMessages(20);
                UsbDeviceManager.this.mHandler.setStartAccessoryTrue();
                UsbDeviceManager.this.startAccessoryMode();
            }
        }
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.ScreenObserver
    public void onKeyguardStateChanged(boolean z) {
        this.mHandler.sendMessage(13, z && ((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isDeviceSecure(ActivityManager.getCurrentUser()));
    }

    public void onUnlockUser(int i) {
        onKeyguardStateChanged(false);
    }

    public UsbDeviceManager(Context context, UsbAlsaManager usbAlsaManager, UsbSettingsManager usbSettingsManager, UsbPermissionManager usbPermissionManager) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mHasUsbAccessory = context.getPackageManager().hasSystemFeature("android.hardware.usb.accessory");
        initRndisAddress();
        int incrementAndGet = sUsbOperationCount.incrementAndGet();
        mUsbGadgetHal = UsbGadgetHalInstance.getInstance(this, null);
        String str = TAG;
        Slog.d(str, "getInstance done");
        this.mControlFds = new HashMap<>();
        FileDescriptor nativeOpenControl = nativeOpenControl("mtp");
        if (nativeOpenControl == null) {
            Slog.e(str, "Failed to open control for mtp");
        }
        this.mControlFds.put(4L, nativeOpenControl);
        FileDescriptor nativeOpenControl2 = nativeOpenControl("ptp");
        if (nativeOpenControl2 == null) {
            Slog.e(str, "Failed to open control for ptp");
        }
        this.mControlFds.put(16L, nativeOpenControl2);
        if (mUsbGadgetHal == null) {
            this.mHandler = new UsbHandlerLegacy(FgThread.get().getLooper(), context, this, usbAlsaManager, usbPermissionManager);
        } else {
            this.mHandler = new UsbHandlerHal(FgThread.get().getLooper(), context, this, usbAlsaManager, usbPermissionManager);
        }
        this.mHandler.handlerInitDone(incrementAndGet);
        if (nativeIsStartRequested()) {
            startAccessoryMode();
        }
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.usb.UsbDeviceManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                UsbDeviceManager.this.mHandler.updateHostState(((ParcelableUsbPort) intent.getParcelableExtra("port", ParcelableUsbPort.class)).getUsbPort((UsbManager) context2.getSystemService(UsbManager.class)), (UsbPortStatus) intent.getParcelableExtra("portStatus", UsbPortStatus.class));
            }
        };
        BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() { // from class: com.android.server.usb.UsbDeviceManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                UsbDeviceManager.this.mHandler.sendMessage(9, intent.getIntExtra("plugged", -1) == 2);
            }
        };
        BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() { // from class: com.android.server.usb.UsbDeviceManager.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Iterator<Map.Entry<String, UsbDevice>> it = ((UsbManager) context2.getSystemService("usb")).getDeviceList().entrySet().iterator();
                if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                    UsbDeviceManager.this.mHandler.sendMessage(10, (Object) it, true);
                } else {
                    UsbDeviceManager.this.mHandler.sendMessage(10, (Object) it, false);
                }
            }
        };
        BroadcastReceiver broadcastReceiver4 = new BroadcastReceiver() { // from class: com.android.server.usb.UsbDeviceManager.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                UsbDeviceManager.this.mHandler.sendEmptyMessage(11);
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter("android.hardware.usb.action.USB_PORT_CHANGED"));
        context.registerReceiver(broadcastReceiver2, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        context.registerReceiver(broadcastReceiver3, intentFilter);
        context.registerReceiver(broadcastReceiver4, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        UsbUEventObserver usbUEventObserver = new UsbUEventObserver();
        this.mUEventObserver = usbUEventObserver;
        usbUEventObserver.startObserving("DEVPATH=/devices/virtual/android_usb/android0");
        usbUEventObserver.startObserving("DEVPATH=/devices/virtual/misc/usb_accessory");
        sEventLogger = new EventLogger(200, "UsbDeviceManager activity");
    }

    public UsbProfileGroupSettingsManager getCurrentSettings() {
        UsbProfileGroupSettingsManager usbProfileGroupSettingsManager;
        synchronized (this.mLock) {
            usbProfileGroupSettingsManager = this.mCurrentSettings;
        }
        return usbProfileGroupSettingsManager;
    }

    public String[] getAccessoryStrings() {
        String[] strArr;
        synchronized (this.mLock) {
            strArr = this.mAccessoryStrings;
        }
        return strArr;
    }

    public void systemReady() {
        ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).registerScreenObserver(this);
        this.mHandler.sendEmptyMessage(3);
    }

    public void bootCompleted() {
        this.mHandler.sendEmptyMessage(4);
    }

    public void setCurrentUser(int i, UsbProfileGroupSettingsManager usbProfileGroupSettingsManager) {
        synchronized (this.mLock) {
            this.mCurrentSettings = usbProfileGroupSettingsManager;
            this.mHandler.obtainMessage(5, i, 0).sendToTarget();
        }
    }

    public void updateUserRestrictions() {
        this.mHandler.sendEmptyMessage(6);
    }

    public final void resetAccessoryHandshakeTimeoutHandler() {
        if ((getCurrentFunctions() & 2) == 0) {
            this.mHandler.removeMessages(20);
            UsbHandler usbHandler = this.mHandler;
            usbHandler.sendMessageDelayed(usbHandler.obtainMessage(20), 10000L);
        }
    }

    public final void startAccessoryMode() {
        if (this.mHasUsbAccessory) {
            int incrementAndGet = sUsbOperationCount.incrementAndGet();
            this.mAccessoryStrings = nativeGetAccessoryStrings();
            boolean z = false;
            boolean z2 = nativeGetAudioMode() == 1;
            String[] strArr = this.mAccessoryStrings;
            if (strArr != null && strArr[0] != null && strArr[1] != null) {
                z = true;
            }
            long j = z ? 2L : 0L;
            if (z2) {
                j |= 64;
            }
            if (j != 0) {
                UsbHandler usbHandler = this.mHandler;
                usbHandler.sendMessageDelayed(usbHandler.obtainMessage(8), 10000L);
                UsbHandler usbHandler2 = this.mHandler;
                usbHandler2.sendMessageDelayed(usbHandler2.obtainMessage(20), 10000L);
                setCurrentFunctions(j, incrementAndGet);
            }
        }
    }

    public static void initRndisAddress() {
        int[] iArr = new int[6];
        iArr[0] = 2;
        String str = SystemProperties.get("ro.serialno", "1234567890ABCDEF");
        int length = str.length();
        for (int i = 0; i < length; i++) {
            int i2 = (i % 5) + 1;
            iArr[i2] = iArr[i2] ^ str.charAt(i);
        }
        try {
            FileUtils.stringToFile("/sys/class/android_usb/android0/f_rndis/ethaddr", String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", Integer.valueOf(iArr[0]), Integer.valueOf(iArr[1]), Integer.valueOf(iArr[2]), Integer.valueOf(iArr[3]), Integer.valueOf(iArr[4]), Integer.valueOf(iArr[5])));
        } catch (IOException unused) {
            Slog.i(TAG, "failed to write to /sys/class/android_usb/android0/f_rndis/ethaddr");
        }
    }

    public static void logAndPrint(int i, IndentingPrintWriter indentingPrintWriter, String str) {
        Slog.println(i, TAG, str);
        if (indentingPrintWriter != null) {
            indentingPrintWriter.println(str);
        }
    }

    public static void logAndPrintException(IndentingPrintWriter indentingPrintWriter, String str, Exception exc) {
        Slog.e(TAG, str, exc);
        if (indentingPrintWriter != null) {
            indentingPrintWriter.println(str + exc);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class UsbHandler extends Handler {
        public long mAccessoryConnectionStartTime;
        public boolean mAdbNotificationShown;
        public boolean mAudioAccessoryConnected;
        public boolean mAudioAccessorySupported;
        public boolean mBootCompleted;
        public Intent mBroadcastedIntent;
        public boolean mConfigured;
        public boolean mConnected;
        public boolean mConnectedToDataDisabledPort;
        public final ContentResolver mContentResolver;
        public final Context mContext;
        public UsbAccessory mCurrentAccessory;
        public long mCurrentFunctions;
        public boolean mCurrentFunctionsApplied;
        public int mCurrentGadgetHalVersion;
        public boolean mCurrentUsbFunctionsReceived;
        public int mCurrentUser;
        public boolean mHideUsbNotification;
        public boolean mHostConnected;
        public boolean mInHostModeWithNoAccessoryConnected;
        public int mMidiCard;
        public int mMidiDevice;
        public boolean mMidiEnabled;
        public NotificationManager mNotificationManager;
        public boolean mPendingBootAccessoryHandshakeBroadcast;
        public boolean mPendingBootBroadcast;
        public final UsbPermissionManager mPermissionManager;
        public int mPowerBrickConnectionStatus;
        public boolean mResetUsbGadgetDisableDebounce;
        public boolean mScreenLocked;
        public long mScreenUnlockedFunctions;
        public int mSendStringCount;
        public SharedPreferences mSettings;
        public boolean mSinkPower;
        public boolean mSourcePower;
        public boolean mStartAccessory;
        public boolean mSupportsAllCombinations;
        public boolean mSystemReady;
        public boolean mUsbAccessoryConnected;
        public final UsbAlsaManager mUsbAlsaManager;
        public boolean mUsbCharging;
        public final UsbDeviceManager mUsbDeviceManager;
        public int mUsbNotificationId;
        public int mUsbSpeed;
        public boolean mUseUsbNotification;

        public abstract void getUsbSpeedCb(int i);

        public abstract void handlerInitDone(int i);

        public boolean isUsbDataTransferActive(long j) {
            return ((4 & j) == 0 && (j & 16) == 0) ? false : true;
        }

        public abstract void resetCb(int i);

        public abstract void setCurrentUsbFunctionsCb(long j, int i, int i2, long j2, boolean z);

        public abstract void setEnabledFunctions(long j, boolean z, int i);

        public UsbHandler(Looper looper, Context context, UsbDeviceManager usbDeviceManager, UsbAlsaManager usbAlsaManager, UsbPermissionManager usbPermissionManager) {
            super(looper);
            this.mAccessoryConnectionStartTime = 0L;
            boolean z = false;
            this.mSendStringCount = 0;
            this.mStartAccessory = false;
            this.mContext = context;
            this.mUsbDeviceManager = usbDeviceManager;
            this.mUsbAlsaManager = usbAlsaManager;
            this.mPermissionManager = usbPermissionManager;
            this.mContentResolver = context.getContentResolver();
            this.mCurrentUser = ActivityManager.getCurrentUser();
            this.mScreenLocked = true;
            SharedPreferences pinnedSharedPrefs = getPinnedSharedPrefs(context);
            this.mSettings = pinnedSharedPrefs;
            if (pinnedSharedPrefs == null) {
                Slog.e(UsbDeviceManager.TAG, "Couldn't load shared preferences");
            } else {
                this.mScreenUnlockedFunctions = UsbManager.usbFunctionsFromString(pinnedSharedPrefs.getString(String.format(Locale.ENGLISH, "usb-screen-unlocked-config-%d", Integer.valueOf(this.mCurrentUser)), ""));
            }
            StorageManager from = StorageManager.from(context);
            StorageVolume primaryVolume = from != null ? from.getPrimaryVolume() : null;
            if (!(primaryVolume != null && primaryVolume.allowMassStorage()) && context.getResources().getBoolean(17891853)) {
                z = true;
            }
            this.mUseUsbNotification = z;
        }

        public void sendMessage(int i, boolean z) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.arg1 = z ? 1 : 0;
            sendMessage(obtain);
        }

        public void sendMessage(int i, Object obj) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.obj = obj;
            sendMessage(obtain);
        }

        public void sendMessage(int i, Object obj, int i2) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.obj = obj;
            obtain.arg1 = i2;
            sendMessage(obtain);
        }

        public void sendMessage(int i, boolean z, int i2) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.arg1 = z ? 1 : 0;
            obtain.arg2 = i2;
            sendMessage(obtain);
        }

        public void sendMessage(int i, Object obj, boolean z) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.obj = obj;
            obtain.arg1 = z ? 1 : 0;
            sendMessage(obtain);
        }

        public void sendMessageDelayed(int i, boolean z, long j) {
            removeMessages(i);
            Message obtain = Message.obtain(this, i);
            obtain.arg1 = z ? 1 : 0;
            sendMessageDelayed(obtain, j);
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x0026  */
        /* JADX WARN: Removed duplicated region for block: B:17:0x0035  */
        /* JADX WARN: Removed duplicated region for block: B:20:0x003d  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void updateState(String str) {
            int i;
            int i2;
            long j;
            if ("DISCONNECTED".equals(str)) {
                i = 0;
            } else if (!"CONNECTED".equals(str)) {
                if (!"CONFIGURED".equals(str)) {
                    Slog.e(UsbDeviceManager.TAG, "unknown state " + str);
                    return;
                }
                i = 1;
            } else {
                i = 1;
                i2 = 0;
                if (i == 1) {
                    removeMessages(17);
                }
                Message obtain = Message.obtain(this, 0);
                obtain.arg1 = i;
                obtain.arg2 = i2;
                if (!this.mResetUsbGadgetDisableDebounce) {
                    sendMessage(obtain);
                    if (i == 1) {
                        this.mResetUsbGadgetDisableDebounce = false;
                        return;
                    }
                    return;
                }
                if (i2 == 0) {
                    removeMessages(0);
                }
                if (i == 1) {
                    removeMessages(17);
                }
                if (i == 0) {
                    j = this.mScreenLocked ? 1000 : 3000;
                } else {
                    j = 0;
                }
                sendMessageDelayed(obtain, j);
                return;
            }
            i2 = i;
            if (i == 1) {
            }
            Message obtain2 = Message.obtain(this, 0);
            obtain2.arg1 = i;
            obtain2.arg2 = i2;
            if (!this.mResetUsbGadgetDisableDebounce) {
            }
        }

        public void updateHostState(UsbPort usbPort, UsbPortStatus usbPortStatus) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = usbPort;
            obtain.arg2 = usbPortStatus;
            removeMessages(7);
            sendMessageDelayed(obtainMessage(7, obtain), 1000L);
        }

        public final void setAdbEnabled(boolean z, int i) {
            if (z) {
                setSystemProperty("persist.sys.usb.config", "adb");
            } else {
                setSystemProperty("persist.sys.usb.config", "");
            }
            setEnabledFunctions(this.mCurrentFunctions, true, i);
            updateAdbNotification(false);
        }

        public boolean isUsbTransferAllowed() {
            return !((UserManager) this.mContext.getSystemService("user")).hasUserRestriction("no_usb_file_transfer");
        }

        public final void updateCurrentAccessory() {
            int incrementAndGet = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
            boolean hasMessages = hasMessages(8);
            if (!this.mConfigured || !hasMessages) {
                if (hasMessages) {
                    return;
                }
                notifyAccessoryModeExit(incrementAndGet);
                return;
            }
            String[] accessoryStrings = this.mUsbDeviceManager.getAccessoryStrings();
            if (accessoryStrings != null) {
                UsbSerialReader usbSerialReader = new UsbSerialReader(this.mContext, this.mPermissionManager, accessoryStrings[5]);
                UsbAccessory usbAccessory = new UsbAccessory(accessoryStrings[0], accessoryStrings[1], accessoryStrings[2], accessoryStrings[3], accessoryStrings[4], usbSerialReader);
                this.mCurrentAccessory = usbAccessory;
                usbSerialReader.setDevice(usbAccessory);
                String str = UsbDeviceManager.TAG;
                Slog.d(str, "entering USB accessory mode: " + this.mCurrentAccessory);
                if (this.mBootCompleted) {
                    this.mUsbDeviceManager.getCurrentSettings().accessoryAttached(this.mCurrentAccessory);
                    removeMessages(20);
                    broadcastUsbAccessoryHandshake();
                    return;
                }
                return;
            }
            Slog.e(UsbDeviceManager.TAG, "nativeGetAccessoryStrings failed");
        }

        public final void notifyAccessoryModeExit(int i) {
            Slog.d(UsbDeviceManager.TAG, "exited USB accessory mode");
            setEnabledFunctions(0L, false, i);
            UsbAccessory usbAccessory = this.mCurrentAccessory;
            if (usbAccessory != null) {
                if (this.mBootCompleted) {
                    this.mPermissionManager.usbAccessoryRemoved(usbAccessory);
                }
                this.mCurrentAccessory = null;
            }
        }

        public SharedPreferences getPinnedSharedPrefs(Context context) {
            return context.createDeviceProtectedStorageContext().getSharedPreferences(new File(Environment.getDataSystemDeDirectory(0), "UsbDeviceManagerPrefs.xml"), 0);
        }

        public final boolean isUsbStateChanged(Intent intent) {
            Set<String> keySet = intent.getExtras().keySet();
            Intent intent2 = this.mBroadcastedIntent;
            if (intent2 == null) {
                for (String str : keySet) {
                    if (intent.getBooleanExtra(str, false)) {
                        return true;
                    }
                }
            } else if (!keySet.equals(intent2.getExtras().keySet())) {
                return true;
            } else {
                for (String str2 : keySet) {
                    if (intent.getBooleanExtra(str2, false) != this.mBroadcastedIntent.getBooleanExtra(str2, false)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public final void broadcastUsbAccessoryHandshake() {
            sendStickyBroadcast(new Intent("android.hardware.usb.action.USB_ACCESSORY_HANDSHAKE").addFlags(285212672).putExtra("android.hardware.usb.extra.ACCESSORY_UEVENT_TIME", this.mAccessoryConnectionStartTime).putExtra("android.hardware.usb.extra.ACCESSORY_STRING_COUNT", this.mSendStringCount).putExtra("android.hardware.usb.extra.ACCESSORY_START", this.mStartAccessory).putExtra("android.hardware.usb.extra.ACCESSORY_HANDSHAKE_END", SystemClock.elapsedRealtime()));
            resetUsbAccessoryHandshakeDebuggingInfo();
        }

        public void updateUsbStateBroadcastIfNeeded(long j) {
            Intent intent = new Intent("android.hardware.usb.action.USB_STATE");
            intent.addFlags(822083584);
            intent.putExtra("connected", this.mConnected);
            intent.putExtra("host_connected", this.mHostConnected);
            intent.putExtra("configured", this.mConfigured);
            intent.putExtra("unlocked", isUsbTransferAllowed() && isUsbDataTransferActive(this.mCurrentFunctions));
            while (j != 0) {
                intent.putExtra(UsbManager.usbFunctionsToString(Long.highestOneBit(j)), true);
                j -= Long.highestOneBit(j);
            }
            if (isUsbStateChanged(intent)) {
                sendStickyBroadcast(intent);
                this.mBroadcastedIntent = intent;
            }
        }

        public void sendStickyBroadcast(Intent intent) {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            EventLogger eventLogger = UsbDeviceManager.sEventLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("USB intent: " + intent));
        }

        public final void updateUsbFunctions() {
            updateMidiFunction();
        }

        public final void updateMidiFunction() {
            Scanner scanner;
            boolean z = true;
            boolean z2 = (this.mCurrentFunctions & 8) != 0;
            if (z2 != this.mMidiEnabled) {
                if (z2) {
                    Scanner scanner2 = null;
                    try {
                        try {
                            scanner = new Scanner(new File("/sys/class/android_usb/android0/f_midi/alsa"));
                        } catch (Throwable th) {
                            th = th;
                        }
                    } catch (FileNotFoundException e) {
                        e = e;
                    }
                    try {
                        this.mMidiCard = scanner.nextInt();
                        int nextInt = scanner.nextInt();
                        this.mMidiDevice = nextInt;
                        scanner.close();
                        scanner2 = nextInt;
                    } catch (FileNotFoundException e2) {
                        e = e2;
                        scanner2 = scanner;
                        Slog.e(UsbDeviceManager.TAG, "could not open MIDI file", e);
                        if (scanner2 != null) {
                            scanner2.close();
                        }
                        z2 = false;
                        scanner2 = scanner2;
                        this.mMidiEnabled = z2;
                        UsbAlsaManager usbAlsaManager = this.mUsbAlsaManager;
                        if (this.mMidiEnabled) {
                        }
                        z = false;
                        usbAlsaManager.setPeripheralMidiState(z, this.mMidiCard, this.mMidiDevice);
                    } catch (Throwable th2) {
                        th = th2;
                        scanner2 = scanner;
                        if (scanner2 != null) {
                            scanner2.close();
                        }
                        throw th;
                    }
                }
                this.mMidiEnabled = z2;
            }
            UsbAlsaManager usbAlsaManager2 = this.mUsbAlsaManager;
            if (this.mMidiEnabled || !this.mConfigured) {
                z = false;
            }
            usbAlsaManager2.setPeripheralMidiState(z, this.mMidiCard, this.mMidiDevice);
        }

        public final void setScreenUnlockedFunctions(int i) {
            setEnabledFunctions(this.mScreenUnlockedFunctions, false, i);
        }

        /* loaded from: classes2.dex */
        public static class AdbTransport extends IAdbTransport.Stub {
            public final UsbHandler mHandler;

            public AdbTransport(UsbHandler usbHandler) {
                this.mHandler = usbHandler;
            }

            public void onAdbEnabled(boolean z, byte b) {
                if (b == 0) {
                    this.mHandler.sendMessage(1, z, UsbDeviceManager.sUsbOperationCount.incrementAndGet());
                }
            }
        }

        public long getAppliedFunctions(long j) {
            if (j == 0) {
                return getChargingFunctions();
            }
            return isAdbEnabled() ? j | 1 : j;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 20) {
                if (this.mBootCompleted) {
                    broadcastUsbAccessoryHandshake();
                } else {
                    this.mPendingBootAccessoryHandshakeBroadcast = true;
                }
            } else if (i != 21) {
                switch (i) {
                    case 0:
                        int incrementAndGet = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        this.mConnected = message.arg1 == 1;
                        this.mConfigured = message.arg2 == 1;
                        updateUsbNotification(false);
                        updateAdbNotification(false);
                        if (this.mBootCompleted) {
                            updateUsbStateBroadcastIfNeeded(getAppliedFunctions(this.mCurrentFunctions));
                        }
                        if ((2 & this.mCurrentFunctions) != 0) {
                            updateCurrentAccessory();
                        }
                        if (this.mBootCompleted) {
                            if (!this.mConnected && !hasMessages(8) && !hasMessages(17)) {
                                if (!this.mScreenLocked && this.mScreenUnlockedFunctions != 0) {
                                    setScreenUnlockedFunctions(incrementAndGet);
                                } else {
                                    setEnabledFunctions(0L, false, incrementAndGet);
                                }
                            }
                            updateUsbFunctions();
                        } else {
                            this.mPendingBootBroadcast = true;
                        }
                        updateUsbSpeed();
                        return;
                    case 1:
                        setAdbEnabled(message.arg1 == 1, message.arg2);
                        return;
                    case 2:
                        setEnabledFunctions(((Long) message.obj).longValue(), false, message.arg1);
                        return;
                    case 3:
                        int incrementAndGet2 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService("notification");
                        ((AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class)).registerTransport(new AdbTransport(this));
                        if (isTv()) {
                            this.mNotificationManager.createNotificationChannel(new NotificationChannel("usbdevicemanager.adb.tv", this.mContext.getString(17039639), 4));
                        }
                        this.mSystemReady = true;
                        finishBoot(incrementAndGet2);
                        return;
                    case 4:
                        int incrementAndGet3 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        this.mBootCompleted = true;
                        finishBoot(incrementAndGet3);
                        return;
                    case 5:
                        int incrementAndGet4 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        int i2 = this.mCurrentUser;
                        int i3 = message.arg1;
                        if (i2 != i3) {
                            this.mCurrentUser = i3;
                            this.mScreenLocked = true;
                            this.mScreenUnlockedFunctions = 0L;
                            SharedPreferences sharedPreferences = this.mSettings;
                            if (sharedPreferences != null) {
                                this.mScreenUnlockedFunctions = UsbManager.usbFunctionsFromString(sharedPreferences.getString(String.format(Locale.ENGLISH, "usb-screen-unlocked-config-%d", Integer.valueOf(i3)), ""));
                            }
                            setEnabledFunctions(0L, false, incrementAndGet4);
                            return;
                        }
                        return;
                    case 6:
                        int incrementAndGet5 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        if (!isUsbDataTransferActive(this.mCurrentFunctions) || isUsbTransferAllowed()) {
                            return;
                        }
                        setEnabledFunctions(0L, true, incrementAndGet5);
                        return;
                    case 7:
                        SomeArgs someArgs = (SomeArgs) message.obj;
                        boolean z = this.mHostConnected;
                        UsbPort usbPort = (UsbPort) someArgs.arg1;
                        UsbPortStatus usbPortStatus = (UsbPortStatus) someArgs.arg2;
                        if (usbPortStatus != null) {
                            this.mHostConnected = usbPortStatus.getCurrentDataRole() == 1;
                            this.mSourcePower = usbPortStatus.getCurrentPowerRole() == 1;
                            this.mSinkPower = usbPortStatus.getCurrentPowerRole() == 2;
                            this.mAudioAccessoryConnected = usbPortStatus.getCurrentMode() == 4;
                            this.mSupportsAllCombinations = usbPortStatus.isRoleCombinationSupported(1, 1) && usbPortStatus.isRoleCombinationSupported(2, 1) && usbPortStatus.isRoleCombinationSupported(1, 2) && usbPortStatus.isRoleCombinationSupported(2, 2);
                            this.mConnectedToDataDisabledPort = usbPortStatus.isConnected() && (usbPortStatus.getUsbDataStatus() != 1);
                            this.mPowerBrickConnectionStatus = usbPortStatus.getPowerBrickConnectionStatus();
                        } else {
                            this.mHostConnected = false;
                            this.mSourcePower = false;
                            this.mSinkPower = false;
                            this.mAudioAccessoryConnected = false;
                            this.mSupportsAllCombinations = false;
                            this.mConnectedToDataDisabledPort = false;
                            this.mPowerBrickConnectionStatus = 0;
                        }
                        if (this.mHostConnected) {
                            if (!this.mUsbAccessoryConnected) {
                                this.mInHostModeWithNoAccessoryConnected = true;
                            } else {
                                this.mInHostModeWithNoAccessoryConnected = false;
                            }
                        } else {
                            this.mInHostModeWithNoAccessoryConnected = false;
                        }
                        this.mAudioAccessorySupported = usbPort.isModeSupported(4);
                        someArgs.recycle();
                        updateUsbNotification(false);
                        if (this.mBootCompleted) {
                            if (this.mHostConnected || z) {
                                updateUsbStateBroadcastIfNeeded(getAppliedFunctions(this.mCurrentFunctions));
                                return;
                            }
                            return;
                        }
                        this.mPendingBootBroadcast = true;
                        return;
                    case 8:
                        int incrementAndGet6 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        if (!this.mConnected || (this.mCurrentFunctions & 2) == 0) {
                            notifyAccessoryModeExit(incrementAndGet6);
                            return;
                        }
                        return;
                    case 9:
                        this.mUsbCharging = message.arg1 == 1;
                        updateUsbNotification(false);
                        return;
                    case 10:
                        Iterator it = (Iterator) message.obj;
                        this.mUsbAccessoryConnected = message.arg1 == 1;
                        if (!it.hasNext()) {
                            this.mInHostModeWithNoAccessoryConnected = true;
                        } else {
                            this.mInHostModeWithNoAccessoryConnected = false;
                        }
                        this.mHideUsbNotification = false;
                        while (it.hasNext()) {
                            UsbDevice usbDevice = (UsbDevice) ((Map.Entry) it.next()).getValue();
                            int configurationCount = usbDevice.getConfigurationCount() - 1;
                            while (configurationCount >= 0) {
                                UsbConfiguration configuration = usbDevice.getConfiguration(configurationCount);
                                configurationCount--;
                                int interfaceCount = configuration.getInterfaceCount() - 1;
                                while (true) {
                                    if (interfaceCount >= 0) {
                                        UsbInterface usbInterface = configuration.getInterface(interfaceCount);
                                        interfaceCount--;
                                        if (UsbDeviceManager.sDenyInterfaces.contains(Integer.valueOf(usbInterface.getInterfaceClass()))) {
                                            this.mHideUsbNotification = true;
                                        }
                                    }
                                }
                            }
                        }
                        updateUsbNotification(false);
                        return;
                    case 11:
                        updateAdbNotification(true);
                        updateUsbNotification(true);
                        return;
                    case 12:
                        int incrementAndGet7 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        this.mScreenUnlockedFunctions = ((Long) message.obj).longValue();
                        SharedPreferences sharedPreferences2 = this.mSettings;
                        if (sharedPreferences2 != null) {
                            SharedPreferences.Editor edit = sharedPreferences2.edit();
                            edit.putString(String.format(Locale.ENGLISH, "usb-screen-unlocked-config-%d", Integer.valueOf(this.mCurrentUser)), UsbManager.usbFunctionsToString(this.mScreenUnlockedFunctions));
                            edit.commit();
                        }
                        if (!this.mScreenLocked && this.mScreenUnlockedFunctions != 0) {
                            setScreenUnlockedFunctions(incrementAndGet7);
                            return;
                        } else {
                            setEnabledFunctions(0L, false, incrementAndGet7);
                            return;
                        }
                    case 13:
                        int incrementAndGet8 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                        int i4 = message.arg1;
                        if ((i4 == 1) == this.mScreenLocked) {
                            return;
                        }
                        boolean z2 = i4 == 1;
                        this.mScreenLocked = z2;
                        if (this.mBootCompleted) {
                            if (z2) {
                                if (this.mConnected) {
                                    return;
                                }
                                setEnabledFunctions(0L, false, incrementAndGet8);
                                return;
                            } else if (this.mScreenUnlockedFunctions == 0 || this.mCurrentFunctions != 0) {
                                return;
                            } else {
                                setScreenUnlockedFunctions(incrementAndGet8);
                                return;
                            }
                        }
                        return;
                    default:
                        return;
                }
            } else {
                this.mSendStringCount++;
            }
        }

        public void finishBoot(int i) {
            if (this.mBootCompleted && this.mCurrentUsbFunctionsReceived && this.mSystemReady) {
                if (this.mPendingBootBroadcast) {
                    updateUsbStateBroadcastIfNeeded(getAppliedFunctions(this.mCurrentFunctions));
                    this.mPendingBootBroadcast = false;
                }
                if (!this.mScreenLocked && this.mScreenUnlockedFunctions != 0) {
                    setScreenUnlockedFunctions(i);
                } else {
                    setEnabledFunctions(0L, false, i);
                }
                if (this.mCurrentAccessory != null) {
                    this.mUsbDeviceManager.getCurrentSettings().accessoryAttached(this.mCurrentAccessory);
                    broadcastUsbAccessoryHandshake();
                } else if (this.mPendingBootAccessoryHandshakeBroadcast) {
                    broadcastUsbAccessoryHandshake();
                }
                this.mPendingBootAccessoryHandshakeBroadcast = false;
                updateUsbNotification(false);
                updateAdbNotification(false);
                updateUsbFunctions();
            }
        }

        public UsbAccessory getCurrentAccessory() {
            return this.mCurrentAccessory;
        }

        public void updateUsbGadgetHalVersion() {
            sendMessage(23, (Object) null);
        }

        public void updateUsbSpeed() {
            if (this.mCurrentGadgetHalVersion < 10) {
                this.mUsbSpeed = -1;
            } else if (this.mConnected && this.mConfigured) {
                sendMessage(22, (Object) null);
            } else {
                this.mUsbSpeed = -1;
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:52:0x00cf, code lost:
            if (r6 == 0) goto L57;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void updateUsbNotification(boolean z) {
            int i;
            int i2;
            int i3;
            int i4;
            String str;
            PendingIntent pendingIntent;
            if (this.mNotificationManager == null || !this.mUseUsbNotification || "0".equals(getSystemProperty("persist.charging.notify", ""))) {
                return;
            }
            if ((this.mHideUsbNotification || this.mInHostModeWithNoAccessoryConnected) && !this.mSupportsAllCombinations) {
                int i5 = this.mUsbNotificationId;
                if (i5 != 0) {
                    this.mNotificationManager.cancelAsUser(null, i5, UserHandle.ALL);
                    this.mUsbNotificationId = 0;
                    Slog.d(UsbDeviceManager.TAG, "Clear notification");
                    return;
                }
                return;
            }
            Resources resources = this.mContext.getResources();
            CharSequence text = resources.getText(17041692);
            if (!this.mAudioAccessoryConnected || this.mAudioAccessorySupported) {
                i = 17041695;
                if (this.mConnected) {
                    long j = this.mCurrentFunctions;
                    if (j == 4) {
                        i3 = 17041691;
                        i4 = 27;
                    } else if (j == 16) {
                        i3 = 17041694;
                        i4 = 28;
                    } else if (j == 8) {
                        i3 = 17041685;
                        i4 = 29;
                    } else if (j == 32 || j == 1024) {
                        i3 = 17041696;
                        i4 = 47;
                    } else if (j == 128) {
                        i3 = 17041699;
                        i4 = 75;
                    } else if (j == 2) {
                        i3 = 17041678;
                        i4 = 30;
                    } else {
                        i3 = 0;
                        i4 = 0;
                    }
                    if (this.mSourcePower) {
                        if (i3 != 0) {
                            text = resources.getText(17041693);
                        }
                        i2 = 31;
                    }
                    i = i3;
                    i2 = i4;
                } else {
                    if (!this.mSourcePower) {
                        if ((!this.mHostConnected || !this.mSinkPower || (!this.mUsbCharging && !this.mUsbAccessoryConnected)) && (!this.mSinkPower || !this.mConnectedToDataDisabledPort || this.mPowerBrickConnectionStatus == 1)) {
                            i2 = 0;
                            i = 0;
                        }
                        i2 = 32;
                        i = 17041679;
                    }
                    i2 = 31;
                }
            } else {
                i2 = 41;
                i = 17041698;
            }
            int i6 = this.mUsbNotificationId;
            if (i2 != i6 || z) {
                if (i6 != 0) {
                    this.mNotificationManager.cancelAsUser(null, i6, UserHandle.ALL);
                    Slog.d(UsbDeviceManager.TAG, "Clear notification");
                    this.mUsbNotificationId = 0;
                }
                if ((this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive") || this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch")) && i2 == 32) {
                    this.mUsbNotificationId = 0;
                } else if (i2 != 0) {
                    CharSequence text2 = resources.getText(i);
                    if (i != 17041698) {
                        pendingIntent = PendingIntent.getActivityAsUser(this.mContext, 0, Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.Settings$UsbDetailsActivity")), 67108864, null, UserHandle.CURRENT);
                        str = SystemNotificationChannels.USB;
                    } else {
                        Intent intent = new Intent();
                        intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
                        intent.putExtra("android.intent.extra.TEXT", "help_url_audio_accessory_not_supported");
                        PendingIntent activity = this.mContext.getPackageManager().resolveActivity(intent, 0) != null ? PendingIntent.getActivity(this.mContext, 0, intent, 67108864) : null;
                        str = SystemNotificationChannels.ALERTS;
                        PendingIntent pendingIntent2 = activity;
                        text = resources.getText(17041697);
                        pendingIntent = pendingIntent2;
                    }
                    Notification.Builder visibility = new Notification.Builder(this.mContext, str).setSmallIcon(17303596).setWhen(0L).setOngoing(true).setTicker(text2).setDefaults(0).setColor(this.mContext.getColor(17170460)).setContentTitle(text2).setContentText(text).setContentIntent(pendingIntent).setVisibility(1);
                    if (i == 17041698) {
                        visibility.setStyle(new Notification.BigTextStyle().bigText(text));
                    }
                    this.mNotificationManager.notifyAsUser(null, i2, visibility.build(), UserHandle.ALL);
                    Slog.d(UsbDeviceManager.TAG, "push notification:" + ((Object) text2));
                    this.mUsbNotificationId = i2;
                }
            }
        }

        public boolean isAdbEnabled() {
            return ((AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class)).isAdbEnabled((byte) 0);
        }

        public void updateAdbNotification(boolean z) {
            if (this.mNotificationManager == null) {
                return;
            }
            if (isAdbEnabled() && this.mConnected) {
                if ("0".equals(getSystemProperty("persist.adb.notify", ""))) {
                    return;
                }
                if (z && this.mAdbNotificationShown) {
                    this.mAdbNotificationShown = false;
                    this.mNotificationManager.cancelAsUser(null, 26, UserHandle.ALL);
                }
                if (this.mAdbNotificationShown) {
                    return;
                }
                Notification createNotification = AdbNotifications.createNotification(this.mContext, (byte) 0);
                this.mAdbNotificationShown = true;
                this.mNotificationManager.notifyAsUser(null, 26, createNotification, UserHandle.ALL);
            } else if (this.mAdbNotificationShown) {
                this.mAdbNotificationShown = false;
                this.mNotificationManager.cancelAsUser(null, 26, UserHandle.ALL);
            }
        }

        public final boolean isTv() {
            return this.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        }

        public long getChargingFunctions() {
            return isAdbEnabled() ? 1L : 4L;
        }

        public void setSystemProperty(String str, String str2) {
            SystemProperties.set(str, str2);
        }

        public String getSystemProperty(String str, String str2) {
            return SystemProperties.get(str, str2);
        }

        public long getEnabledFunctions() {
            return this.mCurrentFunctions;
        }

        public long getScreenUnlockedFunctions() {
            return this.mScreenUnlockedFunctions;
        }

        public int getUsbSpeed() {
            return this.mUsbSpeed;
        }

        public int getGadgetHalVersion() {
            return this.mCurrentGadgetHalVersion;
        }

        public final void dumpFunctions(DualDumpOutputStream dualDumpOutputStream, String str, long j, long j2) {
            for (int i = 0; i < 63; i++) {
                long j3 = 1 << i;
                if ((j2 & j3) != 0) {
                    if (dualDumpOutputStream.isProto()) {
                        dualDumpOutputStream.write(str, j, j3);
                    } else {
                        dualDumpOutputStream.write(str, j, GadgetFunction.toString(j3));
                    }
                }
            }
        }

        public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
            long start = dualDumpOutputStream.start(str, j);
            dumpFunctions(dualDumpOutputStream, "current_functions", 2259152797697L, this.mCurrentFunctions);
            dualDumpOutputStream.write("current_functions_applied", 1133871366146L, this.mCurrentFunctionsApplied);
            dumpFunctions(dualDumpOutputStream, "screen_unlocked_functions", 2259152797699L, this.mScreenUnlockedFunctions);
            dualDumpOutputStream.write("screen_locked", 1133871366148L, this.mScreenLocked);
            dualDumpOutputStream.write("connected", 1133871366149L, this.mConnected);
            dualDumpOutputStream.write("configured", 1133871366150L, this.mConfigured);
            UsbAccessory usbAccessory = this.mCurrentAccessory;
            if (usbAccessory != null) {
                DumpUtils.writeAccessory(dualDumpOutputStream, "current_accessory", 1146756268039L, usbAccessory);
            }
            dualDumpOutputStream.write("host_connected", 1133871366152L, this.mHostConnected);
            dualDumpOutputStream.write("source_power", 1133871366153L, this.mSourcePower);
            dualDumpOutputStream.write("sink_power", 1133871366154L, this.mSinkPower);
            dualDumpOutputStream.write("usb_charging", 1133871366155L, this.mUsbCharging);
            dualDumpOutputStream.write("hide_usb_notification", 1133871366156L, this.mHideUsbNotification);
            dualDumpOutputStream.write("audio_accessory_connected", 1133871366157L, this.mAudioAccessoryConnected);
            try {
                com.android.internal.util.dump.DumpUtils.writeStringIfNotNull(dualDumpOutputStream, "kernel_state", 1138166333455L, FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 0, null).trim());
            } catch (FileNotFoundException unused) {
                Slog.w(UsbDeviceManager.TAG, "Ignore missing legacy kernel path in bugreport dump: kernel state:/sys/class/android_usb/android0/state");
            } catch (Exception e) {
                Slog.e(UsbDeviceManager.TAG, "Could not read kernel state", e);
            }
            try {
                com.android.internal.util.dump.DumpUtils.writeStringIfNotNull(dualDumpOutputStream, "kernel_function_list", 1138166333456L, FileUtils.readTextFile(new File("/sys/class/android_usb/android0/functions"), 0, null).trim());
            } catch (FileNotFoundException unused2) {
                Slog.w(UsbDeviceManager.TAG, "Ignore missing legacy kernel path in bugreport dump: kernel function list:/sys/class/android_usb/android0/functions");
            } catch (Exception e2) {
                Slog.e(UsbDeviceManager.TAG, "Could not read kernel function list", e2);
            }
            dualDumpOutputStream.end(start);
        }

        public void setAccessoryUEventTime(long j) {
            this.mAccessoryConnectionStartTime = j;
        }

        public void setStartAccessoryTrue() {
            this.mStartAccessory = true;
        }

        public void resetUsbAccessoryHandshakeDebuggingInfo() {
            this.mAccessoryConnectionStartTime = 0L;
            this.mSendStringCount = 0;
            this.mStartAccessory = false;
        }
    }

    /* loaded from: classes2.dex */
    public static final class UsbHandlerLegacy extends UsbHandler {
        public String mCurrentFunctionsStr;
        public String mCurrentOemFunctions;
        public int mCurrentRequest;
        public HashMap<String, HashMap<String, Pair<String, String>>> mOemModeMap;
        public boolean mUsbDataUnlocked;

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void getUsbSpeedCb(int i) {
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void handlerInitDone(int i) {
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void resetCb(int i) {
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void setCurrentUsbFunctionsCb(long j, int i, int i2, long j2, boolean z) {
        }

        public UsbHandlerLegacy(Looper looper, Context context, UsbDeviceManager usbDeviceManager, UsbAlsaManager usbAlsaManager, UsbPermissionManager usbPermissionManager) {
            super(looper, context, usbDeviceManager, usbAlsaManager, usbPermissionManager);
            this.mCurrentRequest = 0;
            try {
                readOemUsbOverrideConfig(context);
                this.mCurrentOemFunctions = getSystemProperty(getPersistProp(false), "none");
                if (isNormalBoot()) {
                    String systemProperty = getSystemProperty("sys.usb.config", "none");
                    this.mCurrentFunctionsStr = systemProperty;
                    this.mCurrentFunctionsApplied = systemProperty.equals(getSystemProperty("sys.usb.state", "none"));
                } else {
                    this.mCurrentFunctionsStr = getSystemProperty(getPersistProp(true), "none");
                    this.mCurrentFunctionsApplied = getSystemProperty("sys.usb.config", "none").equals(getSystemProperty("sys.usb.state", "none"));
                }
                this.mCurrentFunctions = 0L;
                this.mCurrentUsbFunctionsReceived = true;
                this.mUsbSpeed = -1;
                this.mCurrentGadgetHalVersion = -1;
                updateState(FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 0, null).trim());
            } catch (Exception e) {
                Slog.e(UsbDeviceManager.TAG, "Error initializing UsbHandler", e);
            }
        }

        public final void readOemUsbOverrideConfig(Context context) {
            String[] stringArray = context.getResources().getStringArray(17236112);
            if (stringArray != null) {
                for (String str : stringArray) {
                    String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR);
                    if (split.length == 3 || split.length == 4) {
                        if (this.mOemModeMap == null) {
                            this.mOemModeMap = new HashMap<>();
                        }
                        HashMap<String, Pair<String, String>> hashMap = this.mOemModeMap.get(split[0]);
                        if (hashMap == null) {
                            hashMap = new HashMap<>();
                            this.mOemModeMap.put(split[0], hashMap);
                        }
                        if (!hashMap.containsKey(split[1])) {
                            if (split.length == 3) {
                                hashMap.put(split[1], new Pair<>(split[2], ""));
                            } else {
                                hashMap.put(split[1], new Pair<>(split[2], split[3]));
                            }
                        }
                    }
                }
            }
        }

        public final String applyOemOverrideFunction(String str) {
            String str2;
            if (str != null && this.mOemModeMap != null) {
                String systemProperty = getSystemProperty("ro.bootmode", "unknown");
                String str3 = UsbDeviceManager.TAG;
                Slog.d(str3, "applyOemOverride usbfunctions=" + str + " bootmode=" + systemProperty);
                HashMap<String, Pair<String, String>> hashMap = this.mOemModeMap.get(systemProperty);
                if (hashMap != null && !systemProperty.equals("normal") && !systemProperty.equals("unknown")) {
                    Pair<String, String> pair = hashMap.get(str);
                    if (pair != null) {
                        String str4 = UsbDeviceManager.TAG;
                        Slog.d(str4, "OEM USB override: " + str + " ==> " + ((String) pair.first) + " persist across reboot " + ((String) pair.second));
                        if (!((String) pair.second).equals("")) {
                            if (isAdbEnabled()) {
                                str2 = addFunction((String) pair.second, "adb");
                            } else {
                                str2 = (String) pair.second;
                            }
                            String str5 = UsbDeviceManager.TAG;
                            Slog.d(str5, "OEM USB override persisting: " + str2 + "in prop: " + getPersistProp(false));
                            setSystemProperty(getPersistProp(false), str2);
                        }
                        return (String) pair.first;
                    } else if (isAdbEnabled()) {
                        setSystemProperty(getPersistProp(false), addFunction("none", "adb"));
                    } else {
                        setSystemProperty(getPersistProp(false), "none");
                    }
                }
            }
            return str;
        }

        public final boolean waitForState(String str) {
            String str2 = null;
            for (int i = 0; i < 20; i++) {
                str2 = getSystemProperty("sys.usb.state", "");
                if (str.equals(str2)) {
                    return true;
                }
                SystemClock.sleep(50L);
            }
            Slog.e(UsbDeviceManager.TAG, "waitForState(" + str + ") FAILED: got " + str2);
            return false;
        }

        public final void setUsbConfig(String str) {
            setSystemProperty("sys.usb.config", str);
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void setEnabledFunctions(long j, boolean z, int i) {
            boolean isUsbDataTransferActive = isUsbDataTransferActive(j);
            if (isUsbDataTransferActive != this.mUsbDataUnlocked) {
                this.mUsbDataUnlocked = isUsbDataTransferActive;
                updateUsbNotification(false);
                z = true;
            }
            long j2 = this.mCurrentFunctions;
            boolean z2 = this.mCurrentFunctionsApplied;
            if (trySetEnabledFunctions(j, z)) {
                return;
            }
            if (z2 && j2 != j) {
                Slog.e(UsbDeviceManager.TAG, "Failsafe 1: Restoring previous USB functions.");
                if (trySetEnabledFunctions(j2, false)) {
                    return;
                }
            }
            Slog.e(UsbDeviceManager.TAG, "Failsafe 2: Restoring default USB functions.");
            if (trySetEnabledFunctions(0L, false)) {
                return;
            }
            Slog.e(UsbDeviceManager.TAG, "Failsafe 3: Restoring empty function list (with ADB if enabled).");
            if (trySetEnabledFunctions(0L, false)) {
                return;
            }
            Slog.e(UsbDeviceManager.TAG, "Unable to set any USB functions!");
        }

        public final boolean isNormalBoot() {
            String systemProperty = getSystemProperty("ro.bootmode", "unknown");
            return systemProperty.equals("normal") || systemProperty.equals("unknown");
        }

        public String applyAdbFunction(String str) {
            if (str == null) {
                str = "";
            }
            if (isAdbEnabled()) {
                return addFunction(str, "adb");
            }
            return removeFunction(str, "adb");
        }

        public final boolean trySetEnabledFunctions(long j, boolean z) {
            String usbFunctionsToString = j != 0 ? UsbManager.usbFunctionsToString(j) : null;
            this.mCurrentFunctions = j;
            if (usbFunctionsToString == null || applyAdbFunction(usbFunctionsToString).equals("none")) {
                usbFunctionsToString = UsbManager.usbFunctionsToString(getChargingFunctions());
            }
            String applyAdbFunction = applyAdbFunction(usbFunctionsToString);
            String applyOemOverrideFunction = applyOemOverrideFunction(applyAdbFunction);
            if (!isNormalBoot() && !this.mCurrentFunctionsStr.equals(applyAdbFunction)) {
                setSystemProperty(getPersistProp(true), applyAdbFunction);
            }
            if ((!applyAdbFunction.equals(applyOemOverrideFunction) && !this.mCurrentOemFunctions.equals(applyOemOverrideFunction)) || !this.mCurrentFunctionsStr.equals(applyAdbFunction) || !this.mCurrentFunctionsApplied || z) {
                this.mCurrentFunctionsStr = applyAdbFunction;
                this.mCurrentOemFunctions = applyOemOverrideFunction;
                this.mCurrentFunctionsApplied = false;
                setUsbConfig("none");
                if (!waitForState("none")) {
                    Slog.e(UsbDeviceManager.TAG, "Failed to kick USB config");
                    return false;
                }
                setUsbConfig(applyOemOverrideFunction);
                if (this.mBootCompleted && (containsFunction(applyAdbFunction, "mtp") || containsFunction(applyAdbFunction, "ptp"))) {
                    updateUsbStateBroadcastIfNeeded(getAppliedFunctions(this.mCurrentFunctions));
                }
                if (!waitForState(applyOemOverrideFunction)) {
                    String str = UsbDeviceManager.TAG;
                    Slog.e(str, "Failed to switch USB config to " + applyAdbFunction);
                    return false;
                }
                this.mCurrentFunctionsApplied = true;
            }
            return true;
        }

        public final String getPersistProp(boolean z) {
            String systemProperty = getSystemProperty("ro.bootmode", "unknown");
            if (systemProperty.equals("normal") || systemProperty.equals("unknown")) {
                return "persist.sys.usb.config";
            }
            if (z) {
                return "persist.sys.usb." + systemProperty + ".func";
            }
            return "persist.sys.usb." + systemProperty + ".config";
        }

        public static String addFunction(String str, String str2) {
            if ("none".equals(str)) {
                return str2;
            }
            if (containsFunction(str, str2)) {
                return str;
            }
            if (str.length() > 0) {
                str = str + ",";
            }
            return str + str2;
        }

        public static String removeFunction(String str, String str2) {
            String[] split = str.split(",");
            for (int i = 0; i < split.length; i++) {
                if (str2.equals(split[i])) {
                    split[i] = null;
                }
            }
            if (split.length == 1 && split[0] == null) {
                return "none";
            }
            StringBuilder sb = new StringBuilder();
            for (String str3 : split) {
                if (str3 != null) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(str3);
                }
            }
            return sb.toString();
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
    }

    /* loaded from: classes2.dex */
    public static final class UsbHandlerHal extends UsbHandler {
        public int mCurrentRequest;
        public boolean mCurrentUsbFunctionsRequested;
        public final Object mGadgetProxyLock;

        public UsbHandlerHal(Looper looper, Context context, UsbDeviceManager usbDeviceManager, UsbAlsaManager usbAlsaManager, UsbPermissionManager usbPermissionManager) {
            super(looper, context, usbDeviceManager, usbAlsaManager, usbPermissionManager);
            Object obj = new Object();
            this.mGadgetProxyLock = obj;
            this.mCurrentRequest = 0;
            UsbDeviceManager.sUsbOperationCount.incrementAndGet();
            try {
                synchronized (obj) {
                    this.mCurrentFunctions = 0L;
                    this.mCurrentUsbFunctionsRequested = true;
                    this.mUsbSpeed = -1;
                    this.mCurrentGadgetHalVersion = 10;
                    updateUsbGadgetHalVersion();
                }
                updateState(FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 0, null).trim());
            } catch (NoSuchElementException e) {
                Slog.e(UsbDeviceManager.TAG, "Usb gadget hal not found", e);
            } catch (Exception e2) {
                Slog.e(UsbDeviceManager.TAG, "Error initializing UsbHandler", e2);
            }
        }

        /* loaded from: classes2.dex */
        public final class ServiceNotification extends IServiceNotification.Stub {
            public final /* synthetic */ UsbHandlerHal this$0;

            @Override // android.hidl.manager.V1_0.IServiceNotification
            public void onRegistration(String str, String str2, boolean z) {
                String str3 = UsbDeviceManager.TAG;
                Slog.i(str3, "Usb gadget hal service started " + str + " " + str2);
                if (!str.equals("android.hardware.usb.gadget@1.0::IUsbGadget")) {
                    Slog.e(UsbDeviceManager.TAG, "fqName does not match");
                } else {
                    this.this$0.sendMessage(18, z);
                }
            }
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler, android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 14:
                    setEnabledFunctions(0L, false, UsbDeviceManager.sUsbOperationCount.incrementAndGet());
                    return;
                case 15:
                    int incrementAndGet = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                    String str = UsbDeviceManager.TAG;
                    Slog.e(str, "Set functions timed out! no reply from usb hal ,operationId:" + incrementAndGet);
                    if (message.arg1 != 1) {
                        setEnabledFunctions(this.mScreenUnlockedFunctions, false, incrementAndGet);
                        return;
                    }
                    return;
                case 16:
                    Slog.i(UsbDeviceManager.TAG, "processing MSG_GET_CURRENT_USB_FUNCTIONS");
                    this.mCurrentUsbFunctionsReceived = true;
                    int i = message.arg2;
                    if (this.mCurrentUsbFunctionsRequested) {
                        Slog.i(UsbDeviceManager.TAG, "updating mCurrentFunctions");
                        this.mCurrentFunctions = ((Long) message.obj).longValue() & (-2);
                        String str2 = UsbDeviceManager.TAG;
                        Slog.i(str2, "mCurrentFunctions:" + this.mCurrentFunctions + "applied:" + message.arg1);
                        this.mCurrentFunctionsApplied = message.arg1 == 1;
                    }
                    finishBoot(i);
                    return;
                case 17:
                    int incrementAndGet2 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                    if (message.arg1 != 1) {
                        setEnabledFunctions(this.mScreenUnlockedFunctions, false, incrementAndGet2);
                        return;
                    }
                    return;
                case 18:
                    boolean z = message.arg1 == 1;
                    int incrementAndGet3 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                    synchronized (this.mGadgetProxyLock) {
                        try {
                            UsbDeviceManager.mUsbGadgetHal = UsbGadgetHalInstance.getInstance(this.mUsbDeviceManager, null);
                            if (!this.mCurrentFunctionsApplied && !z) {
                                setEnabledFunctions(this.mCurrentFunctions, false, incrementAndGet3);
                            }
                        } catch (NoSuchElementException e) {
                            Slog.e(UsbDeviceManager.TAG, "Usb gadget hal not found", e);
                        }
                    }
                    return;
                case 19:
                    int incrementAndGet4 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                    synchronized (this.mGadgetProxyLock) {
                        if (UsbDeviceManager.mUsbGadgetHal == null) {
                            Slog.e(UsbDeviceManager.TAG, "reset Usb Gadget mUsbGadgetHal is null");
                            return;
                        }
                        try {
                            removeMessages(8);
                            if (this.mConfigured) {
                                this.mResetUsbGadgetDisableDebounce = true;
                            }
                            UsbDeviceManager.mUsbGadgetHal.reset(incrementAndGet4);
                        } catch (Exception e2) {
                            Slog.e(UsbDeviceManager.TAG, "reset Usb Gadget failed", e2);
                            this.mResetUsbGadgetDisableDebounce = false;
                        }
                        return;
                    }
                case 20:
                case 21:
                default:
                    super.handleMessage(message);
                    return;
                case 22:
                    int incrementAndGet5 = UsbDeviceManager.sUsbOperationCount.incrementAndGet();
                    if (UsbDeviceManager.mUsbGadgetHal == null) {
                        String str3 = UsbDeviceManager.TAG;
                        Slog.e(str3, "mGadgetHal is null, operationId:" + incrementAndGet5);
                        return;
                    }
                    try {
                        UsbDeviceManager.mUsbGadgetHal.getUsbSpeed(incrementAndGet5);
                        return;
                    } catch (Exception e3) {
                        Slog.e(UsbDeviceManager.TAG, "get UsbSpeed failed", e3);
                        return;
                    }
                case 23:
                    if (UsbDeviceManager.mUsbGadgetHal == null) {
                        Slog.e(UsbDeviceManager.TAG, "mUsbGadgetHal is null");
                        return;
                    }
                    try {
                        this.mCurrentGadgetHalVersion = UsbDeviceManager.mUsbGadgetHal.getGadgetHalVersion();
                        return;
                    } catch (RemoteException e4) {
                        Slog.e(UsbDeviceManager.TAG, "update Usb gadget version failed", e4);
                        return;
                    }
            }
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void setCurrentUsbFunctionsCb(long j, int i, int i2, long j2, boolean z) {
            if (this.mCurrentRequest == i2 && hasMessages(15) && j2 == j) {
                removeMessages(15);
                String str = UsbDeviceManager.TAG;
                Slog.i(str, "notifyCurrentFunction request:" + i2 + " status:" + i);
                if (i == 0) {
                    this.mCurrentFunctionsApplied = true;
                } else if (z) {
                } else {
                    Slog.e(UsbDeviceManager.TAG, "Setting default fuctions");
                    sendEmptyMessage(14);
                }
            }
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void getUsbSpeedCb(int i) {
            this.mUsbSpeed = i;
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void resetCb(int i) {
            if (i != 0) {
                Slog.e(UsbDeviceManager.TAG, "resetCb fail");
            }
        }

        public final void setUsbConfig(long j, boolean z, int i) {
            String str = UsbDeviceManager.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("setUsbConfig(");
            sb.append(j);
            sb.append(") request:");
            int i2 = this.mCurrentRequest + 1;
            this.mCurrentRequest = i2;
            sb.append(i2);
            Slog.d(str, sb.toString());
            removeMessages(17);
            removeMessages(15);
            removeMessages(14);
            synchronized (this.mGadgetProxyLock) {
                if (UsbDeviceManager.mUsbGadgetHal == null) {
                    Slog.e(UsbDeviceManager.TAG, "setUsbConfig mUsbGadgetHal is null");
                    return;
                }
                try {
                    if ((1 & j) != 0) {
                        ((AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class)).startAdbdForTransport((byte) 0);
                    } else {
                        ((AdbManagerInternal) LocalServices.getService(AdbManagerInternal.class)).stopAdbdForTransport((byte) 0);
                    }
                    UsbDeviceManager.mUsbGadgetHal.setCurrentUsbFunctions(this.mCurrentRequest, j, z, 2500, i);
                    sendMessageDelayed(15, z, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
                    if (this.mConnected) {
                        sendMessageDelayed(17, z, 5000L);
                    }
                } catch (Exception e) {
                    Slog.e(UsbDeviceManager.TAG, "Remoteexception while calling setCurrentUsbFunctions", e);
                }
            }
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void setEnabledFunctions(long j, boolean z, int i) {
            if (this.mCurrentGadgetHalVersion < 12 && (1024 & j) != 0) {
                Slog.e(UsbDeviceManager.TAG, "Could not set unsupported function for the GadgetHal");
            } else if (this.mCurrentFunctions == j && this.mCurrentFunctionsApplied && !z) {
            } else {
                String str = UsbDeviceManager.TAG;
                Slog.i(str, "Setting USB config to " + UsbManager.usbFunctionsToString(j));
                this.mCurrentFunctions = j;
                this.mCurrentFunctionsApplied = false;
                this.mCurrentUsbFunctionsRequested = false;
                boolean z2 = j == 0;
                long appliedFunctions = getAppliedFunctions(j);
                setUsbConfig(appliedFunctions, z2, i);
                if (this.mBootCompleted && isUsbDataTransferActive(appliedFunctions)) {
                    updateUsbStateBroadcastIfNeeded(appliedFunctions);
                }
            }
        }

        @Override // com.android.server.usb.UsbDeviceManager.UsbHandler
        public void handlerInitDone(int i) {
            UsbDeviceManager.mUsbGadgetHal.getCurrentUsbFunctions(i);
        }
    }

    public UsbAccessory getCurrentAccessory() {
        return this.mHandler.getCurrentAccessory();
    }

    public ParcelFileDescriptor openAccessory(UsbAccessory usbAccessory, UsbUserPermissionManager usbUserPermissionManager, int i, int i2) {
        UsbAccessory currentAccessory = this.mHandler.getCurrentAccessory();
        if (currentAccessory == null) {
            throw new IllegalArgumentException("no accessory attached");
        }
        if (!currentAccessory.equals(usbAccessory)) {
            throw new IllegalArgumentException(usbAccessory.toString() + " does not match current accessory " + currentAccessory);
        }
        usbUserPermissionManager.checkPermission(usbAccessory, i, i2);
        return nativeOpenAccessory();
    }

    public long getCurrentFunctions() {
        return this.mHandler.getEnabledFunctions();
    }

    public int getCurrentUsbSpeed() {
        return this.mHandler.getUsbSpeed();
    }

    public int getGadgetHalVersion() {
        return this.mHandler.getGadgetHalVersion();
    }

    public void setCurrentUsbFunctionsCb(long j, int i, int i2, long j2, boolean z) {
        this.mHandler.setCurrentUsbFunctionsCb(j, i, i2, j2, z);
    }

    public void getCurrentUsbFunctionsCb(long j, int i) {
        this.mHandler.sendMessage(16, Long.valueOf(j), i == 2);
    }

    public void getUsbSpeedCb(int i) {
        this.mHandler.getUsbSpeedCb(i);
    }

    public void resetCb(int i) {
        this.mHandler.resetCb(i);
    }

    public ParcelFileDescriptor getControlFd(long j) {
        FileDescriptor fileDescriptor = this.mControlFds.get(Long.valueOf(j));
        if (fileDescriptor == null) {
            return null;
        }
        try {
            return ParcelFileDescriptor.dup(fileDescriptor);
        } catch (IOException unused) {
            String str = TAG;
            Slog.e(str, "Could not dup fd for " + j);
            return null;
        }
    }

    public long getScreenUnlockedFunctions() {
        return this.mHandler.getScreenUnlockedFunctions();
    }

    public void setCurrentFunctions(long j, int i) {
        if (j == 0) {
            MetricsLogger.action(this.mContext, 1275);
        } else if (j == 4) {
            MetricsLogger.action(this.mContext, 1276);
        } else if (j == 16) {
            MetricsLogger.action(this.mContext, 1277);
        } else if (j == 8) {
            MetricsLogger.action(this.mContext, 1279);
        } else if (j == 32) {
            MetricsLogger.action(this.mContext, 1278);
        } else if (j == 2) {
            MetricsLogger.action(this.mContext, 1280);
        }
        this.mHandler.sendMessage(2, Long.valueOf(j), i);
    }

    public void setScreenUnlockedFunctions(long j) {
        this.mHandler.sendMessage(12, Long.valueOf(j));
    }

    public void resetUsbGadget() {
        this.mHandler.sendMessage(19, (Object) null);
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        UsbHandler usbHandler = this.mHandler;
        if (usbHandler != null) {
            usbHandler.dump(dualDumpOutputStream, "handler", 1146756268033L);
            sEventLogger.dump(new DualOutputStreamDumpSink(dualDumpOutputStream, 1138166333457L));
        }
        dualDumpOutputStream.end(start);
    }
}
