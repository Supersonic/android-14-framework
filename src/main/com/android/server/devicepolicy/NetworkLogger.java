package com.android.server.devicepolicy;

import android.app.admin.ConnectEvent;
import android.app.admin.DnsEvent;
import android.app.admin.NetworkEvent;
import android.content.p000pm.PackageManagerInternal;
import android.net.IIpConnectivityMetrics;
import android.net.INetdEventCallback;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.android.server.ServiceThread;
import com.android.server.net.BaseNetdEventCallback;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public final class NetworkLogger {
    public static final String TAG = "NetworkLogger";
    public final DevicePolicyManagerService mDpm;
    public ServiceThread mHandlerThread;
    public IIpConnectivityMetrics mIpConnectivityMetrics;
    public final AtomicBoolean mIsLoggingEnabled = new AtomicBoolean(false);
    public final INetdEventCallback mNetdEventCallback = new BaseNetdEventCallback() { // from class: com.android.server.devicepolicy.NetworkLogger.1
        public void onDnsEvent(int i, int i2, int i3, String str, String[] strArr, int i4, long j, int i5) {
            if (NetworkLogger.this.mIsLoggingEnabled.get() && shouldLogNetworkEvent(i5)) {
                sendNetworkEvent(new DnsEvent(str, strArr, i4, NetworkLogger.this.mPm.getNameForUid(i5), j));
            }
        }

        public void onConnectEvent(String str, int i, long j, int i2) {
            if (NetworkLogger.this.mIsLoggingEnabled.get() && shouldLogNetworkEvent(i2)) {
                sendNetworkEvent(new ConnectEvent(str, i, NetworkLogger.this.mPm.getNameForUid(i2), j));
            }
        }

        public final void sendNetworkEvent(NetworkEvent networkEvent) {
            Message obtainMessage = NetworkLogger.this.mNetworkLoggingHandler.obtainMessage(1);
            Bundle bundle = new Bundle();
            bundle.putParcelable("network_event", networkEvent);
            obtainMessage.setData(bundle);
            NetworkLogger.this.mNetworkLoggingHandler.sendMessage(obtainMessage);
        }

        public final boolean shouldLogNetworkEvent(int i) {
            return NetworkLogger.this.mTargetUserId == -1 || NetworkLogger.this.mTargetUserId == UserHandle.getUserId(i);
        }
    };
    public NetworkLoggingHandler mNetworkLoggingHandler;
    public final PackageManagerInternal mPm;
    public final int mTargetUserId;

    public NetworkLogger(DevicePolicyManagerService devicePolicyManagerService, PackageManagerInternal packageManagerInternal, int i) {
        this.mDpm = devicePolicyManagerService;
        this.mPm = packageManagerInternal;
        this.mTargetUserId = i;
    }

    public final boolean checkIpConnectivityMetricsService() {
        if (this.mIpConnectivityMetrics != null) {
            return true;
        }
        IIpConnectivityMetrics iIpConnectivityMetrics = this.mDpm.mInjector.getIIpConnectivityMetrics();
        if (iIpConnectivityMetrics == null) {
            return false;
        }
        this.mIpConnectivityMetrics = iIpConnectivityMetrics;
        return true;
    }

    public boolean startNetworkLogging() {
        String str = TAG;
        Log.d(str, "Starting network logging.");
        if (!checkIpConnectivityMetricsService()) {
            Slog.wtf(str, "Failed to register callback with IIpConnectivityMetrics.");
            return false;
        }
        try {
            if (this.mIpConnectivityMetrics.addNetdEventCallback(1, this.mNetdEventCallback)) {
                ServiceThread serviceThread = new ServiceThread(str, 10, false);
                this.mHandlerThread = serviceThread;
                serviceThread.start();
                NetworkLoggingHandler networkLoggingHandler = new NetworkLoggingHandler(this.mHandlerThread.getLooper(), this.mDpm, this.mTargetUserId);
                this.mNetworkLoggingHandler = networkLoggingHandler;
                networkLoggingHandler.scheduleBatchFinalization();
                this.mIsLoggingEnabled.set(true);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Slog.wtf(TAG, "Failed to make remote calls to register the callback", e);
            return false;
        }
    }

    public boolean stopNetworkLogging() {
        String str = TAG;
        Log.d(str, "Stopping network logging");
        this.mIsLoggingEnabled.set(false);
        discardLogs();
        try {
            try {
                if (checkIpConnectivityMetricsService()) {
                    boolean removeNetdEventCallback = this.mIpConnectivityMetrics.removeNetdEventCallback(1);
                    ServiceThread serviceThread = this.mHandlerThread;
                    if (serviceThread != null) {
                        serviceThread.quitSafely();
                    }
                    return removeNetdEventCallback;
                }
                Slog.wtf(str, "Failed to unregister callback with IIpConnectivityMetrics.");
                ServiceThread serviceThread2 = this.mHandlerThread;
                if (serviceThread2 != null) {
                    serviceThread2.quitSafely();
                }
                return true;
            } catch (RemoteException e) {
                Slog.wtf(TAG, "Failed to make remote calls to unregister the callback", e);
                ServiceThread serviceThread3 = this.mHandlerThread;
                if (serviceThread3 != null) {
                    serviceThread3.quitSafely();
                }
                return true;
            }
        } catch (Throwable th) {
            ServiceThread serviceThread4 = this.mHandlerThread;
            if (serviceThread4 != null) {
                serviceThread4.quitSafely();
            }
            throw th;
        }
    }

    public void pause() {
        NetworkLoggingHandler networkLoggingHandler = this.mNetworkLoggingHandler;
        if (networkLoggingHandler != null) {
            networkLoggingHandler.pause();
        }
    }

    public void resume() {
        NetworkLoggingHandler networkLoggingHandler = this.mNetworkLoggingHandler;
        if (networkLoggingHandler != null) {
            networkLoggingHandler.resume();
        }
    }

    public void discardLogs() {
        NetworkLoggingHandler networkLoggingHandler = this.mNetworkLoggingHandler;
        if (networkLoggingHandler != null) {
            networkLoggingHandler.discardLogs();
        }
    }

    public List<NetworkEvent> retrieveLogs(long j) {
        return this.mNetworkLoggingHandler.retrieveFullLogBatch(j);
    }

    public long forceBatchFinalization() {
        return this.mNetworkLoggingHandler.forceBatchFinalization();
    }
}
