package android.net.metrics;

import android.annotation.SystemApi;
import android.net.ConnectivityMetricsEvent;
import android.net.IIpConnectivityMetrics;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
import com.android.internal.util.BitUtils;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class IpConnectivityLog {
    private static final boolean DBG = false;
    public static final String SERVICE_NAME = "connmetrics";
    private static final String TAG = IpConnectivityLog.class.getSimpleName();
    private IIpConnectivityMetrics mService;

    /* loaded from: classes2.dex */
    public interface Event extends Parcelable {
    }

    @SystemApi
    public IpConnectivityLog() {
    }

    public IpConnectivityLog(IIpConnectivityMetrics service) {
        this.mService = service;
    }

    private boolean checkLoggerService() {
        if (this.mService != null) {
            return true;
        }
        IIpConnectivityMetrics service = IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService(SERVICE_NAME));
        if (service == null) {
            return false;
        }
        this.mService = service;
        return true;
    }

    public boolean log(ConnectivityMetricsEvent ev) {
        if (checkLoggerService()) {
            if (ev.timestamp == 0) {
                ev.timestamp = System.currentTimeMillis();
            }
            try {
                int left = this.mService.logEvent(ev);
                return left >= 0;
            } catch (RemoteException e) {
                Log.m109e(TAG, "Error logging event", e);
                return false;
            }
        }
        return false;
    }

    public boolean log(long timestamp, Event data) {
        ConnectivityMetricsEvent ev = makeEv(data);
        ev.timestamp = timestamp;
        return log(ev);
    }

    public boolean log(String ifname, Event data) {
        ConnectivityMetricsEvent ev = makeEv(data);
        ev.ifname = ifname;
        return log(ev);
    }

    public boolean log(Network network, int[] transports, Event data) {
        return log(network.getNetId(), transports, data);
    }

    public boolean log(int netid, int[] transports, Event data) {
        ConnectivityMetricsEvent ev = makeEv(data);
        ev.netId = netid;
        ev.transports = BitUtils.packBits(transports);
        return log(ev);
    }

    public boolean log(Event data) {
        return log(makeEv(data));
    }

    public boolean logDefaultNetworkValidity(boolean valid) {
        if (!checkLoggerService()) {
            return false;
        }
        try {
            this.mService.logDefaultNetworkValidity(valid);
            return true;
        } catch (RemoteException e) {
            return true;
        }
    }

    public boolean logDefaultNetworkEvent(Network defaultNetwork, int score, boolean validated, LinkProperties lp, NetworkCapabilities nc, Network previousDefaultNetwork, int previousScore, LinkProperties previousLp, NetworkCapabilities previousNc) {
        if (!checkLoggerService()) {
            return false;
        }
        try {
            this.mService.logDefaultNetworkEvent(defaultNetwork, score, validated, lp, nc, previousDefaultNetwork, previousScore, previousLp, previousNc);
            return true;
        } catch (RemoteException e) {
            return true;
        }
    }

    private static ConnectivityMetricsEvent makeEv(Event data) {
        ConnectivityMetricsEvent ev = new ConnectivityMetricsEvent();
        ev.data = data;
        return ev;
    }
}
