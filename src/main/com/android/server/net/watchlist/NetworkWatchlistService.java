package com.android.server.net.watchlist;

import android.content.Context;
import android.net.IIpConnectivityMetrics;
import android.net.INetdEventCallback;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.net.INetworkWatchlistManager;
import com.android.internal.util.DumpUtils;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.net.BaseNetdEventCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class NetworkWatchlistService extends INetworkWatchlistManager.Stub {
    public static final String TAG = NetworkWatchlistService.class.getSimpleName();
    public final Context mContext;
    public final ServiceThread mHandlerThread;
    @VisibleForTesting
    IIpConnectivityMetrics mIpConnectivityMetrics;
    @VisibleForTesting
    WatchlistLoggingHandler mNetworkWatchlistHandler;
    @GuardedBy({"mLoggingSwitchLock"})
    public volatile boolean mIsLoggingEnabled = false;
    public final Object mLoggingSwitchLock = new Object();
    public final INetdEventCallback mNetdEventCallback = new BaseNetdEventCallback() { // from class: com.android.server.net.watchlist.NetworkWatchlistService.1
        public void onDnsEvent(int i, int i2, int i3, String str, String[] strArr, int i4, long j, int i5) {
            if (NetworkWatchlistService.this.mIsLoggingEnabled) {
                NetworkWatchlistService.this.mNetworkWatchlistHandler.asyncNetworkEvent(str, strArr, i5);
            }
        }

        public void onConnectEvent(String str, int i, long j, int i2) {
            if (NetworkWatchlistService.this.mIsLoggingEnabled) {
                NetworkWatchlistService.this.mNetworkWatchlistHandler.asyncNetworkEvent(null, new String[]{str}, i2);
            }
        }
    };
    public final WatchlistConfig mConfig = WatchlistConfig.getInstance();

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public NetworkWatchlistService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v3, types: [com.android.server.net.watchlist.NetworkWatchlistService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            if (Settings.Global.getInt(getContext().getContentResolver(), "network_watchlist_enabled", 1) == 0) {
                Slog.i(NetworkWatchlistService.TAG, "Network Watchlist service is disabled");
                return;
            }
            ?? networkWatchlistService = new NetworkWatchlistService(getContext());
            this.mService = networkWatchlistService;
            publishBinderService("network_watchlist", networkWatchlistService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 550) {
                if (Settings.Global.getInt(getContext().getContentResolver(), "network_watchlist_enabled", 1) == 0) {
                    Slog.i(NetworkWatchlistService.TAG, "Network Watchlist service is disabled");
                    return;
                }
                try {
                    this.mService.init();
                    this.mService.initIpConnectivityMetrics();
                    this.mService.startWatchlistLogging();
                } catch (RemoteException unused) {
                }
                ReportWatchlistJobService.schedule(getContext());
            }
        }
    }

    public NetworkWatchlistService(Context context) {
        this.mContext = context;
        ServiceThread serviceThread = new ServiceThread(TAG, 10, false);
        this.mHandlerThread = serviceThread;
        serviceThread.start();
        WatchlistLoggingHandler watchlistLoggingHandler = new WatchlistLoggingHandler(context, serviceThread.getLooper());
        this.mNetworkWatchlistHandler = watchlistLoggingHandler;
        watchlistLoggingHandler.reportWatchlistIfNecessary();
    }

    @VisibleForTesting
    public NetworkWatchlistService(Context context, ServiceThread serviceThread, WatchlistLoggingHandler watchlistLoggingHandler, IIpConnectivityMetrics iIpConnectivityMetrics) {
        this.mContext = context;
        this.mHandlerThread = serviceThread;
        this.mNetworkWatchlistHandler = watchlistLoggingHandler;
        this.mIpConnectivityMetrics = iIpConnectivityMetrics;
    }

    public final void init() {
        this.mConfig.removeTestModeConfig();
    }

    public final void initIpConnectivityMetrics() {
        this.mIpConnectivityMetrics = IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
    }

    public final boolean isCallerShell() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        if (!isCallerShell()) {
            Slog.w(TAG, "Only shell is allowed to call network watchlist shell commands");
        } else {
            new NetworkWatchlistShellCommand(this, this.mContext).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    @VisibleForTesting
    public boolean startWatchlistLoggingImpl() throws RemoteException {
        synchronized (this.mLoggingSwitchLock) {
            if (this.mIsLoggingEnabled) {
                Slog.w(TAG, "Watchlist logging is already running");
                return true;
            }
            try {
                if (this.mIpConnectivityMetrics.addNetdEventCallback(2, this.mNetdEventCallback)) {
                    this.mIsLoggingEnabled = true;
                    return true;
                }
                return false;
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    public boolean startWatchlistLogging() throws RemoteException {
        enforceWatchlistLoggingPermission();
        return startWatchlistLoggingImpl();
    }

    @VisibleForTesting
    public boolean stopWatchlistLoggingImpl() {
        synchronized (this.mLoggingSwitchLock) {
            if (!this.mIsLoggingEnabled) {
                Slog.w(TAG, "Watchlist logging is not running");
                return true;
            }
            this.mIsLoggingEnabled = false;
            try {
                return this.mIpConnectivityMetrics.removeNetdEventCallback(2);
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    public boolean stopWatchlistLogging() throws RemoteException {
        enforceWatchlistLoggingPermission();
        return stopWatchlistLoggingImpl();
    }

    public byte[] getWatchlistConfigHash() {
        return this.mConfig.getWatchlistConfigHash();
    }

    public final void enforceWatchlistLoggingPermission() {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000) {
            throw new SecurityException(String.format("Uid %d has no permission to change watchlist setting.", Integer.valueOf(callingUid)));
        }
    }

    public void reloadWatchlist() throws RemoteException {
        enforceWatchlistLoggingPermission();
        Slog.i(TAG, "Reloading watchlist");
        this.mConfig.reloadConfig();
    }

    public void reportWatchlistIfNecessary() {
        this.mNetworkWatchlistHandler.reportWatchlistIfNecessary();
    }

    public boolean forceReportWatchlistForTest(long j) {
        if (this.mConfig.isConfigSecure()) {
            return false;
        }
        this.mNetworkWatchlistHandler.forceReportWatchlistForTest(j);
        return true;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, printWriter)) {
            this.mConfig.dump(fileDescriptor, printWriter, strArr);
        }
    }
}
