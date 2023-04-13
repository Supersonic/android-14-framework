package com.android.server.connectivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.IPacProxyInstalledListener;
import android.net.IPacProxyManager;
import android.net.Network;
import android.net.ProxyInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.URLUtil;
import com.android.internal.annotations.GuardedBy;
import com.android.net.IProxyCallback;
import com.android.net.IProxyPortListener;
import com.android.net.IProxyService;
import com.android.net.module.util.PermissionUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
/* loaded from: classes.dex */
public class PacProxyService extends IPacProxyManager.Stub {
    public AlarmManager mAlarmManager;
    public ServiceConnection mConnection;
    public Context mContext;
    public int mCurrentDelay;
    public String mCurrentPac;
    public volatile boolean mHasDownloaded;
    public volatile boolean mHasSentBroadcast;
    public final Handler mNetThreadHandler;
    public PendingIntent mPacRefreshIntent;
    public ServiceConnection mProxyConnection;
    @GuardedBy({"mProxyLock"})
    public IProxyService mProxyService;
    @GuardedBy({"mProxyLock"})
    public volatile Uri mPacUrl = Uri.EMPTY;
    public final RemoteCallbackList<IPacProxyInstalledListener> mCallbacks = new RemoteCallbackList<>();
    public final Object mProxyLock = new Object();
    public final Object mBroadcastStateLock = new Object();
    public Runnable mPacDownloader = new Runnable() { // from class: com.android.server.connectivity.PacProxyService.1
        @Override // java.lang.Runnable
        public void run() {
            String str;
            Uri uri = PacProxyService.this.mPacUrl;
            if (Uri.EMPTY.equals(uri)) {
                return;
            }
            int andSetThreadStatsTag = TrafficStats.getAndSetThreadStatsTag(-187);
            try {
                try {
                    str = PacProxyService.get(uri);
                } catch (IOException e) {
                    Log.w("PacProxyService", "Failed to load PAC file: " + e);
                    TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
                    str = null;
                }
                if (str != null) {
                    synchronized (PacProxyService.this.mProxyLock) {
                        if (!str.equals(PacProxyService.this.mCurrentPac)) {
                            PacProxyService.this.setCurrentProxyScript(str);
                        }
                    }
                    PacProxyService.this.mHasDownloaded = true;
                    PacProxyService.this.sendProxyIfNeeded();
                    PacProxyService.this.longSchedule();
                    return;
                }
                PacProxyService.this.reschedule();
            } finally {
                TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
            }
        }
    };
    public int mLastPort = -1;

    public final int getNextDelay(int i) {
        int i2 = i + 1;
        if (i2 > 3) {
            return 3;
        }
        return i2;
    }

    /* loaded from: classes.dex */
    public class PacRefreshIntentReceiver extends BroadcastReceiver {
        public PacRefreshIntentReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PacProxyService.this.mNetThreadHandler.post(PacProxyService.this.mPacDownloader);
        }
    }

    public PacProxyService(Context context) {
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("android.pacproxyservice", 0);
        handlerThread.start();
        this.mNetThreadHandler = new Handler(handlerThread.getLooper());
        this.mPacRefreshIntent = PendingIntent.getBroadcast(context, 0, new Intent("android.net.proxy.PAC_REFRESH"), 67108864);
        context.registerReceiver(new PacRefreshIntentReceiver(), new IntentFilter("android.net.proxy.PAC_REFRESH"));
    }

    public final AlarmManager getAlarmManager() {
        if (this.mAlarmManager == null) {
            this.mAlarmManager = (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        }
        return this.mAlarmManager;
    }

    public void addListener(IPacProxyInstalledListener iPacProxyInstalledListener) {
        PermissionUtils.enforceNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.NETWORK_SETTINGS"});
        this.mCallbacks.register(iPacProxyInstalledListener);
    }

    public void removeListener(IPacProxyInstalledListener iPacProxyInstalledListener) {
        PermissionUtils.enforceNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.NETWORK_SETTINGS"});
        this.mCallbacks.unregister(iPacProxyInstalledListener);
    }

    public void setCurrentProxyScriptUrl(ProxyInfo proxyInfo) {
        PermissionUtils.enforceNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.NETWORK_SETTINGS"});
        synchronized (this.mBroadcastStateLock) {
            if (proxyInfo != null) {
                if (!Uri.EMPTY.equals(proxyInfo.getPacFileUrl())) {
                    if (!proxyInfo.getPacFileUrl().equals(this.mPacUrl) || proxyInfo.getPort() <= 0) {
                        this.mPacUrl = proxyInfo.getPacFileUrl();
                        this.mCurrentDelay = 0;
                        this.mHasSentBroadcast = false;
                        this.mHasDownloaded = false;
                        getAlarmManager().cancel(this.mPacRefreshIntent);
                        bind();
                    }
                    return;
                }
            }
            getAlarmManager().cancel(this.mPacRefreshIntent);
            synchronized (this.mProxyLock) {
                this.mPacUrl = Uri.EMPTY;
                this.mCurrentPac = null;
                if (this.mProxyService != null) {
                    unbind();
                }
            }
        }
    }

    public static String get(Uri uri) throws IOException {
        long j;
        if (!URLUtil.isValidUrl(uri.toString())) {
            throw new IOException("Malformed URL:" + uri);
        }
        try {
            URLConnection openConnection = new URL(uri.toString()).openConnection(Proxy.NO_PROXY);
            try {
                j = Long.parseLong(openConnection.getHeaderField("Content-Length"));
            } catch (NumberFormatException unused) {
                j = -1;
            }
            if (j > 20000000) {
                throw new IOException("PAC too big: " + j + " bytes");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            do {
                int read = openConnection.getInputStream().read(bArr);
                if (read != -1) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    return byteArrayOutputStream.toString();
                }
            } while (byteArrayOutputStream.size() <= 20000000);
            throw new IOException("PAC too big");
        } catch (IllegalArgumentException unused2) {
            throw new IOException("Incorrect proxy type for " + uri);
        } catch (UnsupportedOperationException unused3) {
            throw new IOException("Unsupported URL connection type for " + uri);
        }
    }

    public final void longSchedule() {
        this.mCurrentDelay = 0;
        setDownloadIn(4);
    }

    public final void reschedule() {
        int nextDelay = getNextDelay(this.mCurrentDelay);
        this.mCurrentDelay = nextDelay;
        setDownloadIn(nextDelay);
    }

    public final String getPacChangeDelay() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String str = SystemProperties.get("conn.pac_change_delay", "8 32 120 14400 43200");
        String string = Settings.Global.getString(contentResolver, "pac_change_delay");
        return string == null ? str : string;
    }

    public final long getDownloadDelay(int i) {
        String[] split = getPacChangeDelay().split(" ");
        if (i < split.length) {
            return Long.parseLong(split[i]);
        }
        return 0L;
    }

    public final void setDownloadIn(int i) {
        getAlarmManager().set(3, (getDownloadDelay(i) * 1000) + SystemClock.elapsedRealtime(), this.mPacRefreshIntent);
    }

    @GuardedBy({"mProxyLock"})
    public final void setCurrentProxyScript(String str) {
        IProxyService iProxyService = this.mProxyService;
        if (iProxyService == null) {
            Log.e("PacProxyService", "setCurrentProxyScript: no proxy service");
            return;
        }
        try {
            iProxyService.setPacFile(str);
            this.mCurrentPac = str;
        } catch (RemoteException e) {
            Log.e("PacProxyService", "Unable to set PAC file", e);
        }
    }

    public final void bind() {
        if (this.mContext == null) {
            Log.e("PacProxyService", "No context for binding");
            return;
        }
        Intent intent = new Intent();
        intent.setClassName("com.android.pacprocessor", "com.android.pacprocessor.PacService");
        if (this.mProxyConnection != null && this.mConnection != null) {
            this.mNetThreadHandler.post(this.mPacDownloader);
            return;
        }
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.android.server.connectivity.PacProxyService.2
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                synchronized (PacProxyService.this.mProxyLock) {
                    PacProxyService.this.mProxyService = null;
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                synchronized (PacProxyService.this.mProxyLock) {
                    try {
                        Log.d("PacProxyService", "Adding service com.android.net.IProxyService " + iBinder.getInterfaceDescriptor());
                    } catch (RemoteException e) {
                        Log.e("PacProxyService", "Remote Exception", e);
                    }
                    ServiceManager.addService("com.android.net.IProxyService", iBinder);
                    PacProxyService.this.mProxyService = IProxyService.Stub.asInterface(iBinder);
                    if (PacProxyService.this.mProxyService == null) {
                        Log.e("PacProxyService", "No proxy service");
                    } else if (PacProxyService.this.mCurrentPac != null) {
                        PacProxyService pacProxyService = PacProxyService.this;
                        pacProxyService.setCurrentProxyScript(pacProxyService.mCurrentPac);
                    } else {
                        PacProxyService.this.mNetThreadHandler.post(PacProxyService.this.mPacDownloader);
                    }
                }
            }
        };
        this.mConnection = serviceConnection;
        this.mContext.bindServiceAsUser(intent, serviceConnection, 1073741829, UserHandle.SYSTEM);
        Intent intent2 = new Intent();
        intent2.setClassName("com.android.proxyhandler", "com.android.proxyhandler.ProxyService");
        ServiceConnection serviceConnection2 = new ServiceConnection() { // from class: com.android.server.connectivity.PacProxyService.3
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                IProxyCallback asInterface = IProxyCallback.Stub.asInterface(iBinder);
                if (asInterface != null) {
                    try {
                        asInterface.getProxyPort(new IProxyPortListener.Stub() { // from class: com.android.server.connectivity.PacProxyService.3.1
                            public void setProxyPort(int i) {
                                if (PacProxyService.this.mLastPort != -1) {
                                    PacProxyService.this.mHasSentBroadcast = false;
                                }
                                PacProxyService.this.mLastPort = i;
                                if (i != -1) {
                                    Log.d("PacProxyService", "Local proxy is bound on " + i);
                                    PacProxyService.this.sendProxyIfNeeded();
                                    return;
                                }
                                Log.e("PacProxyService", "Received invalid port from Local Proxy, PAC will not be operational");
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.mProxyConnection = serviceConnection2;
        this.mContext.bindServiceAsUser(intent2, serviceConnection2, 1073741829, this.mNetThreadHandler, UserHandle.SYSTEM);
    }

    public final void unbind() {
        ServiceConnection serviceConnection = this.mConnection;
        if (serviceConnection != null) {
            this.mContext.unbindService(serviceConnection);
            this.mConnection = null;
        }
        ServiceConnection serviceConnection2 = this.mProxyConnection;
        if (serviceConnection2 != null) {
            this.mContext.unbindService(serviceConnection2);
            this.mProxyConnection = null;
        }
        this.mProxyService = null;
        this.mLastPort = -1;
    }

    public final void sendPacBroadcast(ProxyInfo proxyInfo) {
        int beginBroadcast = this.mCallbacks.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            IPacProxyInstalledListener broadcastItem = this.mCallbacks.getBroadcastItem(i);
            if (broadcastItem != null) {
                try {
                    broadcastItem.onPacProxyInstalled((Network) null, proxyInfo);
                } catch (RemoteException unused) {
                }
            }
        }
        this.mCallbacks.finishBroadcast();
    }

    public final void sendProxyIfNeeded() {
        synchronized (this.mBroadcastStateLock) {
            if (this.mHasDownloaded && this.mLastPort != -1) {
                if (!this.mHasSentBroadcast) {
                    sendPacBroadcast(ProxyInfo.buildPacProxy(this.mPacUrl, this.mLastPort));
                    this.mHasSentBroadcast = true;
                }
            }
        }
    }
}
