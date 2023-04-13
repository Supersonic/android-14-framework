package com.android.server.adb;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.debug.AdbNotifications;
import android.debug.PairDevice;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.Base64;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.FgThread;
import com.android.server.backup.BackupManagerConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AdbDebuggingManager {
    public static final Ticker SYSTEM_TICKER = new Ticker() { // from class: com.android.server.adb.AdbDebuggingManager$$ExternalSyntheticLambda0
        @Override // com.android.server.adb.AdbDebuggingManager.Ticker
        public final long currentTimeMillis() {
            long currentTimeMillis;
            currentTimeMillis = System.currentTimeMillis();
            return currentTimeMillis;
        }
    };
    public static final String TAG = "AdbDebuggingManager";
    public AdbConnectionInfo mAdbConnectionInfo;
    public boolean mAdbUsbEnabled;
    public boolean mAdbWifiEnabled;
    public final String mConfirmComponent;
    public final Map<String, Integer> mConnectedKeys;
    public AdbConnectionPortPoller mConnectionPortPoller;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    public String mFingerprints;
    @VisibleForTesting
    final AdbDebuggingHandler mHandler;
    public PairingThread mPairingThread;
    public final PortListenerImpl mPortListener;
    public final File mTempKeysFile;
    public AdbDebuggingThread mThread;
    public final Ticker mTicker;
    public final File mUserKeyFile;
    public final Set<String> mWifiConnectedKeys;

    /* loaded from: classes.dex */
    public interface AdbConnectionPortListener {
        void onPortReceived(int i);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Ticker {
        long currentTimeMillis();
    }

    public AdbDebuggingManager(Context context) {
        this(context, null, getAdbFile("adb_keys"), getAdbFile("adb_temp_keys.xml"), null, SYSTEM_TICKER);
    }

    @VisibleForTesting
    public AdbDebuggingManager(Context context, String str, File file, File file2, AdbDebuggingThread adbDebuggingThread, Ticker ticker) {
        this.mAdbUsbEnabled = false;
        this.mAdbWifiEnabled = false;
        this.mConnectedKeys = new HashMap();
        this.mPairingThread = null;
        this.mWifiConnectedKeys = new HashSet();
        this.mAdbConnectionInfo = new AdbConnectionInfo();
        this.mPortListener = new PortListenerImpl();
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mConfirmComponent = str;
        this.mUserKeyFile = file;
        this.mTempKeysFile = file2;
        this.mThread = adbDebuggingThread;
        this.mTicker = ticker;
        this.mHandler = new AdbDebuggingHandler(FgThread.get().getLooper(), this.mThread);
    }

    public static void sendBroadcastWithDebugPermission(Context context, Intent intent, UserHandle userHandle) {
        context.sendBroadcastAsUser(intent, userHandle, "android.permission.MANAGE_DEBUGGING");
    }

    /* loaded from: classes.dex */
    public class PairingThread extends Thread implements NsdManager.RegistrationListener {
        @VisibleForTesting
        static final String SERVICE_PROTOCOL = "adb-tls-pairing";
        public String mGuid;
        public NsdManager mNsdManager;
        public String mPairingCode;
        public int mPort;
        public String mPublicKey;
        public String mServiceName;
        public final String mServiceType;

        private native void native_pairing_cancel();

        private native int native_pairing_start(String str, String str2);

        private native boolean native_pairing_wait();

        @Override // android.net.nsd.NsdManager.RegistrationListener
        public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
        }

        @Override // android.net.nsd.NsdManager.RegistrationListener
        public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
        }

        public PairingThread(String str, String str2) {
            super(AdbDebuggingManager.TAG);
            this.mServiceType = String.format("_%s._tcp.", SERVICE_PROTOCOL);
            this.mPairingCode = str;
            this.mGuid = SystemProperties.get("persist.adb.wifi.guid");
            this.mServiceName = str2;
            if (str2 == null || str2.isEmpty()) {
                this.mServiceName = this.mGuid;
            }
            this.mPort = -1;
            this.mNsdManager = (NsdManager) AdbDebuggingManager.this.mContext.getSystemService("servicediscovery");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (this.mGuid.isEmpty()) {
                Slog.e(AdbDebuggingManager.TAG, "adbwifi guid was not set");
                return;
            }
            int native_pairing_start = native_pairing_start(this.mGuid, this.mPairingCode);
            this.mPort = native_pairing_start;
            if (native_pairing_start <= 0 || native_pairing_start > 65535) {
                Slog.e(AdbDebuggingManager.TAG, "Unable to start pairing server");
                return;
            }
            NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
            nsdServiceInfo.setServiceName(this.mServiceName);
            nsdServiceInfo.setServiceType(this.mServiceType);
            nsdServiceInfo.setPort(this.mPort);
            this.mNsdManager.registerService(nsdServiceInfo, 1, this);
            Message obtainMessage = AdbDebuggingManager.this.mHandler.obtainMessage(21);
            obtainMessage.obj = Integer.valueOf(this.mPort);
            AdbDebuggingManager.this.mHandler.sendMessage(obtainMessage);
            boolean native_pairing_wait = native_pairing_wait();
            this.mNsdManager.unregisterService(this);
            Bundle bundle = new Bundle();
            bundle.putString("publicKey", native_pairing_wait ? this.mPublicKey : null);
            AdbDebuggingManager.this.mHandler.sendMessage(Message.obtain(AdbDebuggingManager.this.mHandler, 20, bundle));
        }

        public void cancelPairing() {
            native_pairing_cancel();
        }

        @Override // android.net.nsd.NsdManager.RegistrationListener
        public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            String str = AdbDebuggingManager.TAG;
            Slog.e(str, "Failed to register pairing service(err=" + i + "): " + nsdServiceInfo);
            cancelPairing();
        }

        @Override // android.net.nsd.NsdManager.RegistrationListener
        public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
            String str = AdbDebuggingManager.TAG;
            Slog.w(str, "Failed to unregister pairing service(err=" + i + "): " + nsdServiceInfo);
        }
    }

    /* loaded from: classes.dex */
    public static class AdbConnectionPortPoller extends Thread {
        public AdbConnectionPortListener mListener;
        public final String mAdbPortProp = "service.adb.tls.port";
        public final int mDurationSecs = 10;
        public AtomicBoolean mCanceled = new AtomicBoolean(false);

        public AdbConnectionPortPoller(AdbConnectionPortListener adbConnectionPortListener) {
            this.mListener = adbConnectionPortListener;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            for (int i = 0; i < 10; i++) {
                if (this.mCanceled.get()) {
                    return;
                }
                int i2 = SystemProperties.getInt("service.adb.tls.port", Integer.MAX_VALUE);
                if (i2 == -1 || (i2 > 0 && i2 <= 65535)) {
                    this.mListener.onPortReceived(i2);
                    return;
                }
                SystemClock.sleep(1000L);
            }
            Slog.w(AdbDebuggingManager.TAG, "Failed to receive adb connection port");
            this.mListener.onPortReceived(-1);
        }

        public void cancelAndWait() {
            this.mCanceled.set(true);
            if (isAlive()) {
                try {
                    join();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class PortListenerImpl implements AdbConnectionPortListener {
        public PortListenerImpl() {
        }

        @Override // com.android.server.adb.AdbDebuggingManager.AdbConnectionPortListener
        public void onPortReceived(int i) {
            Message obtainMessage = AdbDebuggingManager.this.mHandler.obtainMessage(i > 0 ? 24 : 25);
            obtainMessage.obj = Integer.valueOf(i);
            AdbDebuggingManager.this.mHandler.sendMessage(obtainMessage);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class AdbDebuggingThread extends Thread {
        public Handler mHandler;
        public InputStream mInputStream;
        public OutputStream mOutputStream;
        public LocalSocket mSocket;
        public boolean mStopped;

        @VisibleForTesting
        public AdbDebuggingThread() {
            super(AdbDebuggingManager.TAG);
        }

        @VisibleForTesting
        public void setHandler(Handler handler) {
            this.mHandler = handler;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                synchronized (this) {
                    if (this.mStopped) {
                        return;
                    }
                    try {
                        openSocketLocked();
                    } catch (Exception unused) {
                        SystemClock.sleep(1000L);
                    }
                }
                try {
                    listenToSocket();
                } catch (Exception unused2) {
                    SystemClock.sleep(1000L);
                }
            }
        }

        public final void openSocketLocked() throws IOException {
            try {
                LocalSocketAddress localSocketAddress = new LocalSocketAddress("adbd", LocalSocketAddress.Namespace.RESERVED);
                this.mInputStream = null;
                LocalSocket localSocket = new LocalSocket(3);
                this.mSocket = localSocket;
                localSocket.connect(localSocketAddress);
                this.mOutputStream = this.mSocket.getOutputStream();
                this.mInputStream = this.mSocket.getInputStream();
                this.mHandler.sendEmptyMessage(26);
            } catch (IOException e) {
                String str = AdbDebuggingManager.TAG;
                Slog.e(str, "Caught an exception opening the socket: " + e);
                closeSocketLocked();
                throw e;
            }
        }

        public final void listenToSocket() throws IOException {
            try {
                byte[] bArr = new byte[65536];
                while (true) {
                    int read = this.mInputStream.read(bArr);
                    if (read < 2) {
                        String str = AdbDebuggingManager.TAG;
                        Slog.w(str, "Read failed with count " + read);
                        break;
                    }
                    byte b = bArr[0];
                    if (b == 80 && bArr[1] == 75) {
                        String str2 = new String(Arrays.copyOfRange(bArr, 2, read));
                        String str3 = AdbDebuggingManager.TAG;
                        Slog.d(str3, "Received public key: " + str2);
                        Message obtainMessage = this.mHandler.obtainMessage(5);
                        obtainMessage.obj = str2;
                        this.mHandler.sendMessage(obtainMessage);
                    } else if (b == 68 && bArr[1] == 67) {
                        String str4 = new String(Arrays.copyOfRange(bArr, 2, read));
                        String str5 = AdbDebuggingManager.TAG;
                        Slog.d(str5, "Received disconnected message: " + str4);
                        Message obtainMessage2 = this.mHandler.obtainMessage(7);
                        obtainMessage2.obj = str4;
                        this.mHandler.sendMessage(obtainMessage2);
                    } else if (b == 67 && bArr[1] == 75) {
                        String str6 = new String(Arrays.copyOfRange(bArr, 2, read));
                        String str7 = AdbDebuggingManager.TAG;
                        Slog.d(str7, "Received connected key message: " + str6);
                        Message obtainMessage3 = this.mHandler.obtainMessage(10);
                        obtainMessage3.obj = str6;
                        this.mHandler.sendMessage(obtainMessage3);
                    } else if (b == 87 && bArr[1] == 69) {
                        byte b2 = bArr[2];
                        String str8 = new String(Arrays.copyOfRange(bArr, 3, read));
                        if (b2 == 0) {
                            String str9 = AdbDebuggingManager.TAG;
                            Slog.d(str9, "Received USB TLS connected key message: " + str8);
                            Message obtainMessage4 = this.mHandler.obtainMessage(10);
                            obtainMessage4.obj = str8;
                            this.mHandler.sendMessage(obtainMessage4);
                        } else if (b2 == 1) {
                            String str10 = AdbDebuggingManager.TAG;
                            Slog.d(str10, "Received WIFI TLS connected key message: " + str8);
                            Message obtainMessage5 = this.mHandler.obtainMessage(22);
                            obtainMessage5.obj = str8;
                            this.mHandler.sendMessage(obtainMessage5);
                        } else {
                            String str11 = AdbDebuggingManager.TAG;
                            Slog.e(str11, "Got unknown transport type from adbd (" + ((int) b2) + ")");
                        }
                    } else if (b != 87 || bArr[1] != 70) {
                        break;
                    } else {
                        byte b3 = bArr[2];
                        String str12 = new String(Arrays.copyOfRange(bArr, 3, read));
                        if (b3 == 0) {
                            String str13 = AdbDebuggingManager.TAG;
                            Slog.d(str13, "Received USB TLS disconnect message: " + str12);
                            Message obtainMessage6 = this.mHandler.obtainMessage(7);
                            obtainMessage6.obj = str12;
                            this.mHandler.sendMessage(obtainMessage6);
                        } else if (b3 == 1) {
                            String str14 = AdbDebuggingManager.TAG;
                            Slog.d(str14, "Received WIFI TLS disconnect key message: " + str12);
                            Message obtainMessage7 = this.mHandler.obtainMessage(23);
                            obtainMessage7.obj = str12;
                            this.mHandler.sendMessage(obtainMessage7);
                        } else {
                            String str15 = AdbDebuggingManager.TAG;
                            Slog.e(str15, "Got unknown transport type from adbd (" + ((int) b3) + ")");
                        }
                    }
                }
                String str16 = AdbDebuggingManager.TAG;
                Slog.e(str16, "Wrong message: " + new String(Arrays.copyOfRange(bArr, 0, 2)));
                synchronized (this) {
                    closeSocketLocked();
                }
            } catch (Throwable th) {
                synchronized (this) {
                    closeSocketLocked();
                    throw th;
                }
            }
        }

        public final void closeSocketLocked() {
            try {
                OutputStream outputStream = this.mOutputStream;
                if (outputStream != null) {
                    outputStream.close();
                    this.mOutputStream = null;
                }
            } catch (IOException e) {
                String str = AdbDebuggingManager.TAG;
                Slog.e(str, "Failed closing output stream: " + e);
            }
            try {
                LocalSocket localSocket = this.mSocket;
                if (localSocket != null) {
                    localSocket.close();
                    this.mSocket = null;
                }
            } catch (IOException e2) {
                String str2 = AdbDebuggingManager.TAG;
                Slog.e(str2, "Failed closing socket: " + e2);
            }
            this.mHandler.sendEmptyMessage(27);
        }

        public void stopListening() {
            synchronized (this) {
                this.mStopped = true;
                closeSocketLocked();
            }
        }

        public void sendResponse(String str) {
            OutputStream outputStream;
            synchronized (this) {
                if (!this.mStopped && (outputStream = this.mOutputStream) != null) {
                    try {
                        outputStream.write(str.getBytes());
                    } catch (IOException e) {
                        Slog.e(AdbDebuggingManager.TAG, "Failed to write response:", e);
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AdbConnectionInfo {
        public String mBssid;
        public int mPort;
        public String mSsid;

        public AdbConnectionInfo() {
            this.mBssid = "";
            this.mSsid = "";
            this.mPort = -1;
        }

        public AdbConnectionInfo(String str, String str2) {
            this.mBssid = str;
            this.mSsid = str2;
        }

        public AdbConnectionInfo(AdbConnectionInfo adbConnectionInfo) {
            this.mBssid = adbConnectionInfo.mBssid;
            this.mSsid = adbConnectionInfo.mSsid;
            this.mPort = adbConnectionInfo.mPort;
        }

        public String getBSSID() {
            return this.mBssid;
        }

        public String getSSID() {
            return this.mSsid;
        }

        public int getPort() {
            return this.mPort;
        }

        public void setPort(int i) {
            this.mPort = i;
        }

        public void clear() {
            this.mBssid = "";
            this.mSsid = "";
            this.mPort = -1;
        }
    }

    public final void setAdbConnectionInfo(AdbConnectionInfo adbConnectionInfo) {
        synchronized (this.mAdbConnectionInfo) {
            if (adbConnectionInfo == null) {
                this.mAdbConnectionInfo.clear();
            } else {
                this.mAdbConnectionInfo = adbConnectionInfo;
            }
        }
    }

    public final AdbConnectionInfo getAdbConnectionInfo() {
        AdbConnectionInfo adbConnectionInfo;
        synchronized (this.mAdbConnectionInfo) {
            adbConnectionInfo = new AdbConnectionInfo(this.mAdbConnectionInfo);
        }
        return adbConnectionInfo;
    }

    /* loaded from: classes.dex */
    public class AdbDebuggingHandler extends Handler {
        public int mAdbEnabledRefCount;
        @VisibleForTesting
        AdbKeyStore mAdbKeyStore;
        public boolean mAdbNotificationShown;
        public ContentObserver mAuthTimeObserver;
        public final BroadcastReceiver mBroadcastReceiver;
        public NotificationManager mNotificationManager;

        public final boolean isTv() {
            return AdbDebuggingManager.this.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        }

        public final void setupNotifications() {
            if (this.mNotificationManager != null) {
                return;
            }
            NotificationManager notificationManager = (NotificationManager) AdbDebuggingManager.this.mContext.getSystemService("notification");
            this.mNotificationManager = notificationManager;
            if (notificationManager == null) {
                Slog.e(AdbDebuggingManager.TAG, "Unable to setup notifications for wireless debugging");
            } else if (isTv()) {
                this.mNotificationManager.createNotificationChannel(new NotificationChannel("usbdevicemanager.adb.tv", AdbDebuggingManager.this.mContext.getString(17039639), 4));
            }
        }

        @VisibleForTesting
        public AdbDebuggingHandler(Looper looper, AdbDebuggingThread adbDebuggingThread) {
            super(looper);
            this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.adb.AdbDebuggingManager.AdbDebuggingHandler.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                        if (intent.getIntExtra("wifi_state", 1) == 1) {
                            Slog.i(AdbDebuggingManager.TAG, "Wifi disabled. Disabling adbwifi.");
                            Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                        }
                    } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo", NetworkInfo.class);
                        if (networkInfo.getType() == 1) {
                            if (!networkInfo.isConnected()) {
                                Slog.i(AdbDebuggingManager.TAG, "Network disconnected. Disabling adbwifi.");
                                Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                                return;
                            }
                            WifiInfo connectionInfo = ((WifiManager) AdbDebuggingManager.this.mContext.getSystemService("wifi")).getConnectionInfo();
                            if (connectionInfo == null || connectionInfo.getNetworkId() == -1) {
                                Slog.i(AdbDebuggingManager.TAG, "Not connected to any wireless network. Not enabling adbwifi.");
                                Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                                return;
                            }
                            String bssid = connectionInfo.getBSSID();
                            if (bssid == null || bssid.isEmpty()) {
                                Slog.e(AdbDebuggingManager.TAG, "Unable to get the wifi ap's BSSID. Disabling adbwifi.");
                                Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                                return;
                            }
                            synchronized (AdbDebuggingManager.this.mAdbConnectionInfo) {
                                if (!bssid.equals(AdbDebuggingManager.this.mAdbConnectionInfo.getBSSID())) {
                                    Slog.i(AdbDebuggingManager.TAG, "Detected wifi network change. Disabling adbwifi.");
                                    Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                                }
                            }
                        }
                    }
                }
            };
            this.mAdbEnabledRefCount = 0;
            this.mAuthTimeObserver = new ContentObserver(this) { // from class: com.android.server.adb.AdbDebuggingManager.AdbDebuggingHandler.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z, Uri uri) {
                    String str = AdbDebuggingManager.TAG;
                    Slog.d(str, "Received notification that uri " + uri + " was modified; rescheduling keystore job");
                    AdbDebuggingHandler.this.scheduleJobToUpdateAdbKeyStore();
                }
            };
            AdbDebuggingManager.this.mThread = adbDebuggingThread;
        }

        @VisibleForTesting
        public void initKeyStore() {
            if (this.mAdbKeyStore == null) {
                this.mAdbKeyStore = new AdbKeyStore();
            }
        }

        public void showAdbConnectedNotification(boolean z) {
            if (z == this.mAdbNotificationShown) {
                return;
            }
            setupNotifications();
            if (!this.mAdbNotificationShown) {
                Notification createNotification = AdbNotifications.createNotification(AdbDebuggingManager.this.mContext, (byte) 1);
                this.mAdbNotificationShown = true;
                this.mNotificationManager.notifyAsUser(null, 62, createNotification, UserHandle.ALL);
                return;
            }
            this.mAdbNotificationShown = false;
            this.mNotificationManager.cancelAsUser(null, 62, UserHandle.ALL);
        }

        public final void startAdbDebuggingThread() {
            int i = this.mAdbEnabledRefCount + 1;
            this.mAdbEnabledRefCount = i;
            if (i > 1) {
                return;
            }
            registerForAuthTimeChanges();
            AdbDebuggingManager.this.mThread = new AdbDebuggingThread();
            AdbDebuggingManager.this.mThread.setHandler(AdbDebuggingManager.this.mHandler);
            AdbDebuggingManager.this.mThread.start();
            this.mAdbKeyStore.updateKeyStore();
            scheduleJobToUpdateAdbKeyStore();
        }

        public final void stopAdbDebuggingThread() {
            int i = this.mAdbEnabledRefCount - 1;
            this.mAdbEnabledRefCount = i;
            if (i > 0) {
                return;
            }
            if (AdbDebuggingManager.this.mThread != null) {
                AdbDebuggingManager.this.mThread.stopListening();
                AdbDebuggingManager.this.mThread = null;
            }
            if (!AdbDebuggingManager.this.mConnectedKeys.isEmpty()) {
                for (Map.Entry entry : AdbDebuggingManager.this.mConnectedKeys.entrySet()) {
                    this.mAdbKeyStore.setLastConnectionTime((String) entry.getKey(), AdbDebuggingManager.this.mTicker.currentTimeMillis());
                }
                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                AdbDebuggingManager.this.mConnectedKeys.clear();
                AdbDebuggingManager.this.mWifiConnectedKeys.clear();
            }
            scheduleJobToUpdateAdbKeyStore();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            initKeyStore();
            switch (message.what) {
                case 1:
                    if (AdbDebuggingManager.this.mAdbUsbEnabled) {
                        return;
                    }
                    startAdbDebuggingThread();
                    AdbDebuggingManager.this.mAdbUsbEnabled = true;
                    return;
                case 2:
                    if (AdbDebuggingManager.this.mAdbUsbEnabled) {
                        stopAdbDebuggingThread();
                        AdbDebuggingManager.this.mAdbUsbEnabled = false;
                        return;
                    }
                    return;
                case 3:
                    String str = (String) message.obj;
                    String fingerprints = AdbDebuggingManager.this.getFingerprints(str);
                    if (!fingerprints.equals(AdbDebuggingManager.this.mFingerprints)) {
                        Slog.e(AdbDebuggingManager.TAG, "Fingerprints do not match. Got " + fingerprints + ", expected " + AdbDebuggingManager.this.mFingerprints);
                        return;
                    }
                    r3 = message.arg1 == 1;
                    if (AdbDebuggingManager.this.mThread != null) {
                        AdbDebuggingManager.this.mThread.sendResponse("OK");
                        if (r3) {
                            if (!AdbDebuggingManager.this.mConnectedKeys.containsKey(str)) {
                                AdbDebuggingManager.this.mConnectedKeys.put(str, 1);
                            }
                            this.mAdbKeyStore.setLastConnectionTime(str, AdbDebuggingManager.this.mTicker.currentTimeMillis());
                            AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                            scheduleJobToUpdateAdbKeyStore();
                        }
                        logAdbConnectionChanged(str, 2, r3);
                        return;
                    }
                    return;
                case 4:
                    if (AdbDebuggingManager.this.mThread != null) {
                        Slog.w(AdbDebuggingManager.TAG, "Denying adb confirmation");
                        AdbDebuggingManager.this.mThread.sendResponse("NO");
                        logAdbConnectionChanged(null, 3, false);
                        return;
                    }
                    return;
                case 5:
                    String str2 = (String) message.obj;
                    String fingerprints2 = AdbDebuggingManager.this.getFingerprints(str2);
                    if ("".equals(fingerprints2)) {
                        if (AdbDebuggingManager.this.mThread != null) {
                            AdbDebuggingManager.this.mThread.sendResponse("NO");
                            logAdbConnectionChanged(str2, 5, false);
                            return;
                        }
                        return;
                    }
                    logAdbConnectionChanged(str2, 1, false);
                    AdbDebuggingManager.this.mFingerprints = fingerprints2;
                    AdbDebuggingManager adbDebuggingManager = AdbDebuggingManager.this;
                    adbDebuggingManager.startConfirmationForKey(str2, adbDebuggingManager.mFingerprints);
                    return;
                case 6:
                    Slog.d(AdbDebuggingManager.TAG, "Received a request to clear the adb authorizations");
                    AdbDebuggingManager.this.mConnectedKeys.clear();
                    initKeyStore();
                    AdbDebuggingManager.this.mWifiConnectedKeys.clear();
                    this.mAdbKeyStore.deleteKeyStore();
                    cancelJobToUpdateAdbKeyStore();
                    return;
                case 7:
                    String str3 = (String) message.obj;
                    if (str3 != null && str3.length() > 0) {
                        if (AdbDebuggingManager.this.mConnectedKeys.containsKey(str3)) {
                            int intValue = ((Integer) AdbDebuggingManager.this.mConnectedKeys.get(str3)).intValue() - 1;
                            if (intValue == 0) {
                                this.mAdbKeyStore.setLastConnectionTime(str3, AdbDebuggingManager.this.mTicker.currentTimeMillis());
                                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                                scheduleJobToUpdateAdbKeyStore();
                                AdbDebuggingManager.this.mConnectedKeys.remove(str3);
                            } else {
                                AdbDebuggingManager.this.mConnectedKeys.put(str3, Integer.valueOf(intValue));
                            }
                            r3 = true;
                        }
                    } else {
                        Slog.w(AdbDebuggingManager.TAG, "Received a disconnected key message with an empty key");
                    }
                    logAdbConnectionChanged(str3, 7, r3);
                    return;
                case 8:
                    AdbKeyStore adbKeyStore = this.mAdbKeyStore;
                    if (adbKeyStore != null) {
                        adbKeyStore.persistKeyStore();
                        return;
                    }
                    return;
                case 9:
                    if (!AdbDebuggingManager.this.mConnectedKeys.isEmpty()) {
                        for (Map.Entry entry : AdbDebuggingManager.this.mConnectedKeys.entrySet()) {
                            this.mAdbKeyStore.setLastConnectionTime((String) entry.getKey(), AdbDebuggingManager.this.mTicker.currentTimeMillis());
                        }
                        AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    } else if (this.mAdbKeyStore.isEmpty()) {
                        return;
                    } else {
                        this.mAdbKeyStore.updateKeyStore();
                        scheduleJobToUpdateAdbKeyStore();
                        return;
                    }
                case 10:
                    String str4 = (String) message.obj;
                    if (str4 == null || str4.length() == 0) {
                        Slog.w(AdbDebuggingManager.TAG, "Received a connected key message with an empty key");
                        return;
                    }
                    if (!AdbDebuggingManager.this.mConnectedKeys.containsKey(str4)) {
                        AdbDebuggingManager.this.mConnectedKeys.put(str4, 1);
                    } else {
                        AdbDebuggingManager.this.mConnectedKeys.put(str4, Integer.valueOf(((Integer) AdbDebuggingManager.this.mConnectedKeys.get(str4)).intValue() + 1));
                    }
                    this.mAdbKeyStore.setLastConnectionTime(str4, AdbDebuggingManager.this.mTicker.currentTimeMillis());
                    AdbDebuggingManager.this.sendPersistKeyStoreMessage();
                    scheduleJobToUpdateAdbKeyStore();
                    logAdbConnectionChanged(str4, 4, true);
                    return;
                case 11:
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        return;
                    }
                    AdbConnectionInfo currentWifiApInfo = getCurrentWifiApInfo();
                    if (currentWifiApInfo == null) {
                        Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                        return;
                    } else if (!verifyWifiNetwork(currentWifiApInfo.getBSSID(), currentWifiApInfo.getSSID())) {
                        Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                        return;
                    } else {
                        AdbDebuggingManager.this.setAdbConnectionInfo(currentWifiApInfo);
                        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
                        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
                        AdbDebuggingManager.this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
                        SystemProperties.set("persist.adb.tls_server.enable", "1");
                        AdbDebuggingManager.this.mConnectionPortPoller = new AdbConnectionPortPoller(AdbDebuggingManager.this.mPortListener);
                        AdbDebuggingManager.this.mConnectionPortPoller.start();
                        startAdbDebuggingThread();
                        AdbDebuggingManager.this.mAdbWifiEnabled = true;
                        return;
                    }
                case 12:
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        AdbDebuggingManager.this.mAdbWifiEnabled = false;
                        AdbDebuggingManager.this.setAdbConnectionInfo(null);
                        AdbDebuggingManager.this.mContext.unregisterReceiver(this.mBroadcastReceiver);
                        if (AdbDebuggingManager.this.mThread != null) {
                            AdbDebuggingManager.this.mThread.sendResponse("DA");
                        }
                        onAdbdWifiServerDisconnected(-1);
                        stopAdbDebuggingThread();
                        return;
                    }
                    return;
                case 13:
                default:
                    return;
                case 14:
                    if (AdbDebuggingManager.this.mPairingThread != null) {
                        AdbDebuggingManager.this.mPairingThread.cancelPairing();
                        try {
                            AdbDebuggingManager.this.mPairingThread.join();
                        } catch (InterruptedException e) {
                            Slog.w(AdbDebuggingManager.TAG, "Error while waiting for pairing thread to quit.");
                            e.printStackTrace();
                        }
                        AdbDebuggingManager.this.mPairingThread = null;
                        return;
                    }
                    return;
                case 15:
                    String createPairingCode = createPairingCode(6);
                    updateUIPairCode(createPairingCode);
                    AdbDebuggingManager.this.mPairingThread = new PairingThread(createPairingCode, null);
                    AdbDebuggingManager.this.mPairingThread.start();
                    return;
                case 16:
                    Bundle bundle = (Bundle) message.obj;
                    AdbDebuggingManager.this.mPairingThread = new PairingThread(bundle.getString("password"), bundle.getString("serviceName"));
                    AdbDebuggingManager.this.mPairingThread.start();
                    return;
                case 17:
                    String str5 = (String) message.obj;
                    String findKeyFromFingerprint = this.mAdbKeyStore.findKeyFromFingerprint(str5);
                    if (findKeyFromFingerprint == null || findKeyFromFingerprint.isEmpty()) {
                        Slog.e(AdbDebuggingManager.TAG, "Not a known fingerprint [" + str5 + "]");
                        return;
                    }
                    String str6 = "DD" + findKeyFromFingerprint;
                    if (AdbDebuggingManager.this.mThread != null) {
                        AdbDebuggingManager.this.mThread.sendResponse(str6);
                    }
                    this.mAdbKeyStore.removeKey(findKeyFromFingerprint);
                    sendPairedDevicesToUI(this.mAdbKeyStore.getPairedDevices());
                    return;
                case 18:
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        return;
                    }
                    String str7 = (String) message.obj;
                    if (message.arg1 == 1) {
                        this.mAdbKeyStore.addTrustedNetwork(str7);
                    }
                    AdbConnectionInfo currentWifiApInfo2 = getCurrentWifiApInfo();
                    if (currentWifiApInfo2 == null || !str7.equals(currentWifiApInfo2.getBSSID())) {
                        return;
                    }
                    AdbDebuggingManager.this.setAdbConnectionInfo(currentWifiApInfo2);
                    Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 1);
                    IntentFilter intentFilter2 = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
                    intentFilter2.addAction("android.net.wifi.STATE_CHANGE");
                    AdbDebuggingManager.this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter2);
                    SystemProperties.set("persist.adb.tls_server.enable", "1");
                    AdbDebuggingManager.this.mConnectionPortPoller = new AdbConnectionPortPoller(AdbDebuggingManager.this.mPortListener);
                    AdbDebuggingManager.this.mConnectionPortPoller.start();
                    startAdbDebuggingThread();
                    AdbDebuggingManager.this.mAdbWifiEnabled = true;
                    return;
                case 19:
                    Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                    sendServerConnectionState(false, -1);
                    return;
                case 20:
                    onPairingResult(((Bundle) message.obj).getString("publicKey"));
                    sendPairedDevicesToUI(this.mAdbKeyStore.getPairedDevices());
                    return;
                case 21:
                    sendPairingPortToUI(((Integer) message.obj).intValue());
                    return;
                case 22:
                    if (AdbDebuggingManager.this.mWifiConnectedKeys.add((String) message.obj)) {
                        sendPairedDevicesToUI(this.mAdbKeyStore.getPairedDevices());
                        showAdbConnectedNotification(true);
                        return;
                    }
                    return;
                case 23:
                    if (AdbDebuggingManager.this.mWifiConnectedKeys.remove((String) message.obj)) {
                        sendPairedDevicesToUI(this.mAdbKeyStore.getPairedDevices());
                        if (AdbDebuggingManager.this.mWifiConnectedKeys.isEmpty()) {
                            showAdbConnectedNotification(false);
                            return;
                        }
                        return;
                    }
                    return;
                case 24:
                    int intValue2 = ((Integer) message.obj).intValue();
                    onAdbdWifiServerConnected(intValue2);
                    synchronized (AdbDebuggingManager.this.mAdbConnectionInfo) {
                        AdbDebuggingManager.this.mAdbConnectionInfo.setPort(intValue2);
                    }
                    Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 1);
                    return;
                case 25:
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        onAdbdWifiServerDisconnected(((Integer) message.obj).intValue());
                        Settings.Global.putInt(AdbDebuggingManager.this.mContentResolver, "adb_wifi_enabled", 0);
                        stopAdbDebuggingThread();
                        if (AdbDebuggingManager.this.mConnectionPortPoller != null) {
                            AdbDebuggingManager.this.mConnectionPortPoller.cancelAndWait();
                            AdbDebuggingManager.this.mConnectionPortPoller = null;
                            return;
                        }
                        return;
                    }
                    return;
                case 26:
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        AdbDebuggingManager.this.mConnectionPortPoller = new AdbConnectionPortPoller(AdbDebuggingManager.this.mPortListener);
                        AdbDebuggingManager.this.mConnectionPortPoller.start();
                        return;
                    }
                    return;
                case 27:
                    if (AdbDebuggingManager.this.mConnectionPortPoller != null) {
                        AdbDebuggingManager.this.mConnectionPortPoller.cancelAndWait();
                        AdbDebuggingManager.this.mConnectionPortPoller = null;
                    }
                    if (AdbDebuggingManager.this.mAdbWifiEnabled) {
                        onAdbdWifiServerDisconnected(-1);
                        return;
                    }
                    return;
                case 28:
                    this.mAdbKeyStore.reloadKeyMap();
                    return;
            }
        }

        public void registerForAuthTimeChanges() {
            AdbDebuggingManager.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("adb_allowed_connection_time"), false, this.mAuthTimeObserver);
        }

        public final void logAdbConnectionChanged(String str, int i, boolean z) {
            long lastConnectionTime = this.mAdbKeyStore.getLastConnectionTime(str);
            long allowedConnectionTime = this.mAdbKeyStore.getAllowedConnectionTime();
            String str2 = AdbDebuggingManager.TAG;
            Slog.d(str2, "Logging key " + str + ", state = " + i + ", alwaysAllow = " + z + ", lastConnectionTime = " + lastConnectionTime + ", authWindow = " + allowedConnectionTime);
            FrameworkStatsLog.write(144, lastConnectionTime, allowedConnectionTime, i, z);
        }

        @VisibleForTesting
        public long scheduleJobToUpdateAdbKeyStore() {
            cancelJobToUpdateAdbKeyStore();
            long nextExpirationTime = this.mAdbKeyStore.getNextExpirationTime();
            if (nextExpirationTime == -1) {
                return -1L;
            }
            long max = nextExpirationTime != 0 ? Math.max(Math.min((long) BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS, nextExpirationTime), 60000L) : 0L;
            sendMessageDelayed(obtainMessage(9), max);
            return max;
        }

        public final void cancelJobToUpdateAdbKeyStore() {
            removeMessages(9);
        }

        public final String createPairingCode(int i) {
            SecureRandom secureRandom = new SecureRandom();
            String str = "";
            for (int i2 = 0; i2 < i; i2++) {
                str = str + secureRandom.nextInt(10);
            }
            return str;
        }

        public final void sendServerConnectionState(boolean z, int i) {
            Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_STATUS");
            intent.putExtra("status", z ? 4 : 5);
            intent.putExtra("adb_port", i);
            AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent, UserHandle.ALL);
        }

        public final void onAdbdWifiServerConnected(int i) {
            sendPairedDevicesToUI(this.mAdbKeyStore.getPairedDevices());
            sendServerConnectionState(true, i);
        }

        public final void onAdbdWifiServerDisconnected(int i) {
            AdbDebuggingManager.this.mWifiConnectedKeys.clear();
            showAdbConnectedNotification(false);
            sendServerConnectionState(false, i);
        }

        public final AdbConnectionInfo getCurrentWifiApInfo() {
            String passpointProviderFriendlyName;
            WifiManager wifiManager = (WifiManager) AdbDebuggingManager.this.mContext.getSystemService("wifi");
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo == null || connectionInfo.getNetworkId() == -1) {
                Slog.i(AdbDebuggingManager.TAG, "Not connected to any wireless network. Not enabling adbwifi.");
                return null;
            }
            if (connectionInfo.isPasspointAp() || connectionInfo.isOsuAp()) {
                passpointProviderFriendlyName = connectionInfo.getPasspointProviderFriendlyName();
            } else {
                passpointProviderFriendlyName = connectionInfo.getSSID();
                if (passpointProviderFriendlyName == null || "<unknown ssid>".equals(passpointProviderFriendlyName)) {
                    List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
                    int size = configuredNetworks.size();
                    for (int i = 0; i < size; i++) {
                        if (configuredNetworks.get(i).networkId == connectionInfo.getNetworkId()) {
                            passpointProviderFriendlyName = configuredNetworks.get(i).SSID;
                        }
                    }
                    if (passpointProviderFriendlyName == null) {
                        Slog.e(AdbDebuggingManager.TAG, "Unable to get ssid of the wifi AP.");
                        return null;
                    }
                }
            }
            String bssid = connectionInfo.getBSSID();
            if (bssid == null || bssid.isEmpty()) {
                Slog.e(AdbDebuggingManager.TAG, "Unable to get the wifi ap's BSSID.");
                return null;
            }
            return new AdbConnectionInfo(bssid, passpointProviderFriendlyName);
        }

        public final boolean verifyWifiNetwork(String str, String str2) {
            if (this.mAdbKeyStore.isTrustedNetwork(str)) {
                return true;
            }
            AdbDebuggingManager.this.startConfirmationForNetwork(str2, str);
            return false;
        }

        public final void onPairingResult(String str) {
            if (str == null) {
                Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
                intent.putExtra("status", 0);
                AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent, UserHandle.ALL);
                return;
            }
            Intent intent2 = new Intent("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
            intent2.putExtra("status", 1);
            String fingerprints = AdbDebuggingManager.this.getFingerprints(str);
            String[] split = str.split("\\s+");
            String str2 = split.length > 1 ? split[1] : "nouser@nohostname";
            PairDevice pairDevice = new PairDevice();
            pairDevice.name = fingerprints;
            pairDevice.guid = str2;
            pairDevice.connected = false;
            intent2.putExtra("pair_device", (Parcelable) pairDevice);
            AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent2, UserHandle.ALL);
            this.mAdbKeyStore.setLastConnectionTime(str, AdbDebuggingManager.this.mTicker.currentTimeMillis());
            AdbDebuggingManager.this.sendPersistKeyStoreMessage();
            scheduleJobToUpdateAdbKeyStore();
        }

        public final void sendPairingPortToUI(int i) {
            Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
            intent.putExtra("status", 4);
            intent.putExtra("adb_port", i);
            AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent, UserHandle.ALL);
        }

        public final void sendPairedDevicesToUI(Map<String, PairDevice> map) {
            Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_PAIRED_DEVICES");
            intent.putExtra("devices_map", (HashMap) map);
            AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent, UserHandle.ALL);
        }

        public final void updateUIPairCode(String str) {
            Intent intent = new Intent("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
            intent.putExtra("pairing_code", str);
            intent.putExtra("status", 3);
            AdbDebuggingManager.sendBroadcastWithDebugPermission(AdbDebuggingManager.this.mContext, intent, UserHandle.ALL);
        }
    }

    public final String getFingerprints(String str) {
        StringBuilder sb = new StringBuilder();
        if (str == null) {
            return "";
        }
        try {
            try {
                byte[] digest = MessageDigest.getInstance("MD5").digest(Base64.decode(str.split("\\s+")[0].getBytes(), 0));
                for (int i = 0; i < digest.length; i++) {
                    sb.append("0123456789ABCDEF".charAt((digest[i] >> 4) & 15));
                    sb.append("0123456789ABCDEF".charAt(digest[i] & 15));
                    if (i < digest.length - 1) {
                        sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
                    }
                }
                return sb.toString();
            } catch (IllegalArgumentException e) {
                Slog.e(TAG, "error doing base64 decoding", e);
                return "";
            }
        } catch (Exception e2) {
            Slog.e(TAG, "Error getting digester", e2);
            return "";
        }
    }

    public final void startConfirmationForNetwork(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AbstractMap.SimpleEntry("ssid", str));
        arrayList.add(new AbstractMap.SimpleEntry("bssid", str2));
        int currentUser = ActivityManager.getCurrentUser();
        String string = Resources.getSystem().getString(17039859);
        ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(currentUser);
        if (startConfirmationActivity(unflattenFromString, userInfo.getUserHandle(), arrayList) || startConfirmationService(unflattenFromString, userInfo.getUserHandle(), arrayList)) {
            return;
        }
        String str3 = TAG;
        Slog.e(str3, "Unable to start customAdbWifiNetworkConfirmation[SecondaryUser]Component " + string + " as an Activity or a Service");
    }

    public final void startConfirmationForKey(String str, String str2) {
        String string;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AbstractMap.SimpleEntry("key", str));
        arrayList.add(new AbstractMap.SimpleEntry("fingerprints", str2));
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(ActivityManager.getCurrentUser());
        if (userInfo.isAdmin()) {
            string = this.mConfirmComponent;
            if (string == null) {
                string = Resources.getSystem().getString(17039857);
            }
        } else {
            string = Resources.getSystem().getString(17039858);
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
        if (startConfirmationActivity(unflattenFromString, userInfo.getUserHandle(), arrayList) || startConfirmationService(unflattenFromString, userInfo.getUserHandle(), arrayList)) {
            return;
        }
        String str3 = TAG;
        Slog.e(str3, "unable to start customAdbPublicKeyConfirmation[SecondaryUser]Component " + string + " as an Activity or a Service");
    }

    public final boolean startConfirmationActivity(ComponentName componentName, UserHandle userHandle, List<Map.Entry<String, String>> list) {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent createConfirmationIntent = createConfirmationIntent(componentName, list);
        createConfirmationIntent.addFlags(268435456);
        if (packageManager.resolveActivity(createConfirmationIntent, 65536) != null) {
            try {
                this.mContext.startActivityAsUser(createConfirmationIntent, userHandle);
                return true;
            } catch (ActivityNotFoundException e) {
                String str = TAG;
                Slog.e(str, "unable to start adb whitelist activity: " + componentName, e);
                return false;
            }
        }
        return false;
    }

    public final boolean startConfirmationService(ComponentName componentName, UserHandle userHandle, List<Map.Entry<String, String>> list) {
        try {
            return this.mContext.startServiceAsUser(createConfirmationIntent(componentName, list), userHandle) != null;
        } catch (SecurityException e) {
            String str = TAG;
            Slog.e(str, "unable to start adb whitelist service: " + componentName, e);
            return false;
        }
    }

    public final Intent createConfirmationIntent(ComponentName componentName, List<Map.Entry<String, String>> list) {
        Intent intent = new Intent();
        intent.setClassName(componentName.getPackageName(), componentName.getClassName());
        for (Map.Entry<String, String> entry : list) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        return intent;
    }

    public static File getAdbFile(String str) {
        File file = new File(Environment.getDataDirectory(), "misc/adb");
        if (!file.exists()) {
            Slog.e(TAG, "ADB data directory does not exist");
            return null;
        }
        return new File(file, str);
    }

    public File getAdbTempKeysFile() {
        return this.mTempKeysFile;
    }

    public File getUserKeyFile() {
        return this.mUserKeyFile;
    }

    public final void writeKeys(Iterable<String> iterable) {
        FileOutputStream fileOutputStream;
        if (this.mUserKeyFile == null) {
            return;
        }
        AtomicFile atomicFile = new AtomicFile(this.mUserKeyFile);
        try {
            fileOutputStream = atomicFile.startWrite();
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        }
        try {
            for (String str : iterable) {
                fileOutputStream.write(str.getBytes());
                fileOutputStream.write(10);
            }
            atomicFile.finishWrite(fileOutputStream);
            FileUtils.setPermissions(this.mUserKeyFile.toString(), FrameworkStatsLog.DISPLAY_HBM_STATE_CHANGED, -1, -1);
        } catch (IOException e2) {
            e = e2;
            String str2 = TAG;
            Slog.e(str2, "Error writing keys: " + e);
            atomicFile.failWrite(fileOutputStream);
        }
    }

    public void setAdbEnabled(boolean z, byte b) {
        if (b == 0) {
            this.mHandler.sendEmptyMessage(z ? 1 : 2);
        } else if (b == 1) {
            this.mHandler.sendEmptyMessage(z ? 11 : 12);
        } else {
            throw new IllegalArgumentException("setAdbEnabled called with unimplemented transport type=" + ((int) b));
        }
    }

    public void allowDebugging(boolean z, String str) {
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.arg1 = z ? 1 : 0;
        obtainMessage.obj = str;
        this.mHandler.sendMessage(obtainMessage);
    }

    public void denyDebugging() {
        this.mHandler.sendEmptyMessage(4);
    }

    public void clearDebuggingKeys() {
        this.mHandler.sendEmptyMessage(6);
    }

    public void allowWirelessDebugging(boolean z, String str) {
        Message obtainMessage = this.mHandler.obtainMessage(18);
        obtainMessage.arg1 = z ? 1 : 0;
        obtainMessage.obj = str;
        this.mHandler.sendMessage(obtainMessage);
    }

    public void denyWirelessDebugging() {
        this.mHandler.sendEmptyMessage(19);
    }

    public int getAdbWirelessPort() {
        AdbConnectionInfo adbConnectionInfo = getAdbConnectionInfo();
        if (adbConnectionInfo == null) {
            return 0;
        }
        return adbConnectionInfo.getPort();
    }

    public Map<String, PairDevice> getPairedDevices() {
        return new AdbKeyStore().getPairedDevices();
    }

    public void unpairDevice(String str) {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 17, str));
    }

    public void enablePairingByPairingCode() {
        this.mHandler.sendEmptyMessage(15);
    }

    public void enablePairingByQrCode(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString("serviceName", str);
        bundle.putString("password", str2);
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 16, bundle));
    }

    public void disablePairing() {
        this.mHandler.sendEmptyMessage(14);
    }

    public void notifyKeyFilesUpdated() {
        this.mHandler.sendEmptyMessage(28);
    }

    public final void sendPersistKeyStoreMessage() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8));
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        dualDumpOutputStream.write("connected_to_adb", 1133871366145L, this.mThread != null);
        DumpUtils.writeStringIfNotNull(dualDumpOutputStream, "last_key_received", 1138166333442L, this.mFingerprints);
        try {
            dualDumpOutputStream.write("user_keys", 1138166333443L, FileUtils.readTextFile(new File("/data/misc/adb/adb_keys"), 0, null));
        } catch (IOException e) {
            Slog.i(TAG, "Cannot read user keys", e);
        }
        try {
            dualDumpOutputStream.write("system_keys", 1138166333444L, FileUtils.readTextFile(new File("/adb_keys"), 0, null));
        } catch (IOException e2) {
            Slog.i(TAG, "Cannot read system keys", e2);
        }
        try {
            dualDumpOutputStream.write("keystore", 1138166333445L, FileUtils.readTextFile(this.mTempKeysFile, 0, null));
        } catch (IOException e3) {
            Slog.i(TAG, "Cannot read keystore: ", e3);
        }
        dualDumpOutputStream.end(start);
    }

    /* loaded from: classes.dex */
    public class AdbKeyStore {
        public AtomicFile mAtomicKeyFile;
        public final Set<String> mSystemKeys;
        public final Map<String, Long> mKeyMap = new HashMap();
        public final List<String> mTrustedNetworks = new ArrayList();

        public AdbKeyStore() {
            initKeyFile();
            readTempKeysFile();
            this.mSystemKeys = getSystemKeysFromFile("/adb_keys");
            addExistingUserKeysToKeyStore();
        }

        public void reloadKeyMap() {
            readTempKeysFile();
        }

        public void addTrustedNetwork(String str) {
            this.mTrustedNetworks.add(str);
            AdbDebuggingManager.this.sendPersistKeyStoreMessage();
        }

        public Map<String, PairDevice> getPairedDevices() {
            HashMap hashMap = new HashMap();
            for (Map.Entry<String, Long> entry : this.mKeyMap.entrySet()) {
                String fingerprints = AdbDebuggingManager.this.getFingerprints(entry.getKey());
                String[] split = entry.getKey().split("\\s+");
                String str = split.length > 1 ? split[1] : "nouser@nohostname";
                PairDevice pairDevice = new PairDevice();
                pairDevice.name = str;
                pairDevice.guid = fingerprints;
                pairDevice.connected = AdbDebuggingManager.this.mWifiConnectedKeys.contains(entry.getKey());
                hashMap.put(entry.getKey(), pairDevice);
            }
            return hashMap;
        }

        public String findKeyFromFingerprint(String str) {
            for (Map.Entry<String, Long> entry : this.mKeyMap.entrySet()) {
                if (str.equals(AdbDebuggingManager.this.getFingerprints(entry.getKey()))) {
                    return entry.getKey();
                }
            }
            return null;
        }

        public void removeKey(String str) {
            if (this.mKeyMap.containsKey(str)) {
                this.mKeyMap.remove(str);
                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
            }
        }

        public final void initKeyFile() {
            if (AdbDebuggingManager.this.mTempKeysFile != null) {
                this.mAtomicKeyFile = new AtomicFile(AdbDebuggingManager.this.mTempKeysFile);
            }
        }

        public final Set<String> getSystemKeysFromFile(String str) {
            HashSet hashSet = new HashSet();
            File file = new File(str);
            if (file.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        String trim = readLine.trim();
                        if (trim.length() > 0) {
                            hashSet.add(trim);
                        }
                    }
                    bufferedReader.close();
                } catch (IOException e) {
                    String str2 = AdbDebuggingManager.TAG;
                    Slog.e(str2, "Caught an exception reading " + str + ": " + e);
                }
            }
            return hashSet;
        }

        public boolean isEmpty() {
            return this.mKeyMap.isEmpty();
        }

        public void updateKeyStore() {
            if (filterOutOldKeys()) {
                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
            }
        }

        public final void readTempKeysFile() {
            TypedXmlPullParser resolvePullParser;
            this.mKeyMap.clear();
            this.mTrustedNetworks.clear();
            if (this.mAtomicKeyFile == null) {
                initKeyFile();
                if (this.mAtomicKeyFile == null) {
                    String str = AdbDebuggingManager.TAG;
                    Slog.e(str, "Unable to obtain the key file, " + AdbDebuggingManager.this.mTempKeysFile + ", for reading");
                    return;
                }
            }
            if (this.mAtomicKeyFile.exists()) {
                try {
                    FileInputStream openRead = this.mAtomicKeyFile.openRead();
                    try {
                        resolvePullParser = Xml.resolvePullParser(openRead);
                        com.android.internal.util.XmlUtils.beginDocument(resolvePullParser, "keyStore");
                        int attributeInt = resolvePullParser.getAttributeInt((String) null, "version");
                        if (attributeInt > 1) {
                            String str2 = AdbDebuggingManager.TAG;
                            Slog.e(str2, "Keystore version=" + attributeInt + " not supported (max_supported=1)");
                            if (openRead != null) {
                                openRead.close();
                                return;
                            }
                            return;
                        }
                    } catch (XmlPullParserException unused) {
                        resolvePullParser = Xml.resolvePullParser(openRead);
                    }
                    readKeyStoreContents(resolvePullParser);
                    if (openRead != null) {
                        openRead.close();
                    }
                } catch (IOException e) {
                    Slog.e(AdbDebuggingManager.TAG, "Caught an IOException parsing the XML key file: ", e);
                } catch (XmlPullParserException e2) {
                    Slog.e(AdbDebuggingManager.TAG, "Caught XmlPullParserException parsing the XML key file: ", e2);
                }
            }
        }

        public final void readKeyStoreContents(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
            while (typedXmlPullParser.next() != 1) {
                String name = typedXmlPullParser.getName();
                if ("adbKey".equals(name)) {
                    addAdbKeyToKeyMap(typedXmlPullParser);
                } else if ("wifiAP".equals(name)) {
                    addTrustedNetworkToTrustedNetworks(typedXmlPullParser);
                } else {
                    String str = AdbDebuggingManager.TAG;
                    Slog.w(str, "Ignoring tag '" + name + "'. Not recognized.");
                }
                com.android.internal.util.XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }

        public final void addAdbKeyToKeyMap(TypedXmlPullParser typedXmlPullParser) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "key");
            try {
                this.mKeyMap.put(attributeValue, Long.valueOf(typedXmlPullParser.getAttributeLong((String) null, "lastConnection")));
            } catch (XmlPullParserException e) {
                Slog.e(AdbDebuggingManager.TAG, "Error reading adbKey attributes", e);
            }
        }

        public final void addTrustedNetworkToTrustedNetworks(TypedXmlPullParser typedXmlPullParser) {
            this.mTrustedNetworks.add(typedXmlPullParser.getAttributeValue((String) null, "bssid"));
        }

        public final void addExistingUserKeysToKeyStore() {
            if (AdbDebuggingManager.this.mUserKeyFile == null || !AdbDebuggingManager.this.mUserKeyFile.exists()) {
                return;
            }
            boolean z = false;
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(AdbDebuggingManager.this.mUserKeyFile));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    } else if (!this.mKeyMap.containsKey(readLine)) {
                        this.mKeyMap.put(readLine, Long.valueOf(AdbDebuggingManager.this.mTicker.currentTimeMillis()));
                        z = true;
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                String str = AdbDebuggingManager.TAG;
                Slog.e(str, "Caught an exception reading " + AdbDebuggingManager.this.mUserKeyFile + ": " + e);
            }
            if (z) {
                AdbDebuggingManager.this.sendPersistKeyStoreMessage();
            }
        }

        public void persistKeyStore() {
            filterOutOldKeys();
            if (this.mKeyMap.isEmpty() && this.mTrustedNetworks.isEmpty()) {
                deleteKeyStore();
                return;
            }
            if (this.mAtomicKeyFile == null) {
                initKeyFile();
                if (this.mAtomicKeyFile == null) {
                    String str = AdbDebuggingManager.TAG;
                    Slog.e(str, "Unable to obtain the key file, " + AdbDebuggingManager.this.mTempKeysFile + ", for writing");
                    return;
                }
            }
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream startWrite = this.mAtomicKeyFile.startWrite();
                try {
                    TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                    resolveSerializer.startDocument((String) null, Boolean.TRUE);
                    resolveSerializer.startTag((String) null, "keyStore");
                    resolveSerializer.attributeInt((String) null, "version", 1);
                    for (Map.Entry<String, Long> entry : this.mKeyMap.entrySet()) {
                        resolveSerializer.startTag((String) null, "adbKey");
                        resolveSerializer.attribute((String) null, "key", entry.getKey());
                        resolveSerializer.attributeLong((String) null, "lastConnection", entry.getValue().longValue());
                        resolveSerializer.endTag((String) null, "adbKey");
                    }
                    for (String str2 : this.mTrustedNetworks) {
                        resolveSerializer.startTag((String) null, "wifiAP");
                        resolveSerializer.attribute((String) null, "bssid", str2);
                        resolveSerializer.endTag((String) null, "wifiAP");
                    }
                    resolveSerializer.endTag((String) null, "keyStore");
                    resolveSerializer.endDocument();
                    this.mAtomicKeyFile.finishWrite(startWrite);
                } catch (IOException e) {
                    e = e;
                    fileOutputStream = startWrite;
                    Slog.e(AdbDebuggingManager.TAG, "Caught an exception writing the key map: ", e);
                    this.mAtomicKeyFile.failWrite(fileOutputStream);
                    AdbDebuggingManager.this.writeKeys(this.mKeyMap.keySet());
                }
            } catch (IOException e2) {
                e = e2;
            }
            AdbDebuggingManager.this.writeKeys(this.mKeyMap.keySet());
        }

        public final boolean filterOutOldKeys() {
            long allowedConnectionTime = getAllowedConnectionTime();
            boolean z = false;
            if (allowedConnectionTime == 0) {
                return false;
            }
            long currentTimeMillis = AdbDebuggingManager.this.mTicker.currentTimeMillis();
            Iterator<Map.Entry<String, Long>> it = this.mKeyMap.entrySet().iterator();
            while (it.hasNext()) {
                if (currentTimeMillis > it.next().getValue().longValue() + allowedConnectionTime) {
                    it.remove();
                    z = true;
                }
            }
            if (z) {
                AdbDebuggingManager.this.writeKeys(this.mKeyMap.keySet());
            }
            return z;
        }

        public long getNextExpirationTime() {
            long allowedConnectionTime = getAllowedConnectionTime();
            if (allowedConnectionTime == 0) {
                return -1L;
            }
            long currentTimeMillis = AdbDebuggingManager.this.mTicker.currentTimeMillis();
            long j = -1;
            for (Map.Entry<String, Long> entry : this.mKeyMap.entrySet()) {
                long max = Math.max(0L, (entry.getValue().longValue() + allowedConnectionTime) - currentTimeMillis);
                if (j == -1 || max < j) {
                    j = max;
                }
            }
            return j;
        }

        public void deleteKeyStore() {
            this.mKeyMap.clear();
            this.mTrustedNetworks.clear();
            if (AdbDebuggingManager.this.mUserKeyFile != null) {
                AdbDebuggingManager.this.mUserKeyFile.delete();
            }
            AtomicFile atomicFile = this.mAtomicKeyFile;
            if (atomicFile == null) {
                return;
            }
            atomicFile.delete();
        }

        public long getLastConnectionTime(String str) {
            return this.mKeyMap.getOrDefault(str, 0L).longValue();
        }

        public void setLastConnectionTime(String str, long j) {
            setLastConnectionTime(str, j, false);
        }

        @VisibleForTesting
        public void setLastConnectionTime(String str, long j, boolean z) {
            if ((!this.mKeyMap.containsKey(str) || this.mKeyMap.get(str).longValue() < j || z) && !this.mSystemKeys.contains(str)) {
                this.mKeyMap.put(str, Long.valueOf(j));
            }
        }

        public long getAllowedConnectionTime() {
            return Settings.Global.getLong(AdbDebuggingManager.this.mContext.getContentResolver(), "adb_allowed_connection_time", 604800000L);
        }

        public boolean isTrustedNetwork(String str) {
            return this.mTrustedNetworks.contains(str);
        }
    }
}
