package com.android.server.location.gnss;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.p005os.IInstalld;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseCallState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.location.GpsNetInitiatedHandler;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class GnssNetworkConnectivityHandler {
    public static final boolean DEBUG = Log.isLoggable("GnssNetworkConnectivityHandler", 3);
    public static final boolean VERBOSE = Log.isLoggable("GnssNetworkConnectivityHandler", 2);
    public InetAddress mAGpsDataConnectionIpAddr;
    public int mAGpsDataConnectionState;
    public int mAGpsType;
    public final ConnectivityManager mConnMgr;
    public final Context mContext;
    public final GnssNetworkListener mGnssNetworkListener;
    public final Handler mHandler;
    public ConnectivityManager.NetworkCallback mNetworkConnectivityCallback;
    public final GpsNetInitiatedHandler mNiHandler;
    public final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener;
    public HashMap<Integer, SubIdPhoneStateListener> mPhoneStateListeners;
    public ConnectivityManager.NetworkCallback mSuplConnectivityCallback;
    public final PowerManager.WakeLock mWakeLock;
    public HashMap<Network, NetworkAttributes> mAvailableNetworkAttributes = new HashMap<>(5);
    public int mActiveSubId = -1;

    /* loaded from: classes.dex */
    public interface GnssNetworkListener {
        void onNetworkAvailable();
    }

    private native void native_agps_data_conn_closed();

    private native void native_agps_data_conn_failed();

    private native void native_agps_data_conn_open(long j, String str, int i);

    private static native boolean native_is_agps_ril_supported();

    private native void native_update_network_state(boolean z, int i, boolean z2, boolean z3, String str, long j, short s);

    /* loaded from: classes.dex */
    public static class NetworkAttributes {
        public String mApn;
        public NetworkCapabilities mCapabilities;
        public int mType;

        public NetworkAttributes() {
            this.mType = -1;
        }

        public static boolean hasCapabilitiesChanged(NetworkCapabilities networkCapabilities, NetworkCapabilities networkCapabilities2) {
            return networkCapabilities == null || networkCapabilities2 == null || hasCapabilityChanged(networkCapabilities, networkCapabilities2, 18) || hasCapabilityChanged(networkCapabilities, networkCapabilities2, 11);
        }

        public static boolean hasCapabilityChanged(NetworkCapabilities networkCapabilities, NetworkCapabilities networkCapabilities2, int i) {
            return networkCapabilities.hasCapability(i) != networkCapabilities2.hasCapability(i);
        }

        public static short getCapabilityFlags(NetworkCapabilities networkCapabilities) {
            short s = networkCapabilities.hasCapability(18) ? (short) 2 : (short) 0;
            return networkCapabilities.hasCapability(11) ? (short) (s | 1) : s;
        }
    }

    public GnssNetworkConnectivityHandler(Context context, GnssNetworkListener gnssNetworkListener, Looper looper, GpsNetInitiatedHandler gpsNetInitiatedHandler) {
        SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                TelephonyManager createForSubscriptionId;
                if (GnssNetworkConnectivityHandler.this.mPhoneStateListeners == null) {
                    GnssNetworkConnectivityHandler.this.mPhoneStateListeners = new HashMap(2, 1.0f);
                }
                SubscriptionManager subscriptionManager = (SubscriptionManager) GnssNetworkConnectivityHandler.this.mContext.getSystemService(SubscriptionManager.class);
                TelephonyManager telephonyManager = (TelephonyManager) GnssNetworkConnectivityHandler.this.mContext.getSystemService(TelephonyManager.class);
                if (subscriptionManager == null || telephonyManager == null) {
                    return;
                }
                List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                HashSet hashSet = new HashSet();
                if (activeSubscriptionInfoList != null) {
                    if (GnssNetworkConnectivityHandler.DEBUG) {
                        Log.d("GnssNetworkConnectivityHandler", "Active Sub List size: " + activeSubscriptionInfoList.size());
                    }
                    for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                        hashSet.add(Integer.valueOf(subscriptionInfo.getSubscriptionId()));
                        if (!GnssNetworkConnectivityHandler.this.mPhoneStateListeners.containsKey(Integer.valueOf(subscriptionInfo.getSubscriptionId())) && (createForSubscriptionId = telephonyManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId())) != null) {
                            if (GnssNetworkConnectivityHandler.DEBUG) {
                                Log.d("GnssNetworkConnectivityHandler", "Listener sub" + subscriptionInfo.getSubscriptionId());
                            }
                            SubIdPhoneStateListener subIdPhoneStateListener = new SubIdPhoneStateListener(Integer.valueOf(subscriptionInfo.getSubscriptionId()));
                            GnssNetworkConnectivityHandler.this.mPhoneStateListeners.put(Integer.valueOf(subscriptionInfo.getSubscriptionId()), subIdPhoneStateListener);
                            createForSubscriptionId.listen(subIdPhoneStateListener, IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES);
                        }
                    }
                }
                Iterator it = GnssNetworkConnectivityHandler.this.mPhoneStateListeners.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (!hashSet.contains(entry.getKey())) {
                        TelephonyManager createForSubscriptionId2 = telephonyManager.createForSubscriptionId(((Integer) entry.getKey()).intValue());
                        if (createForSubscriptionId2 != null) {
                            if (GnssNetworkConnectivityHandler.DEBUG) {
                                Log.d("GnssNetworkConnectivityHandler", "unregister listener sub " + entry.getKey());
                            }
                            createForSubscriptionId2.listen((PhoneStateListener) entry.getValue(), 0);
                            it.remove();
                        } else {
                            Log.e("GnssNetworkConnectivityHandler", "Telephony Manager for Sub " + entry.getKey() + " null");
                        }
                    }
                }
                if (hashSet.contains(Integer.valueOf(GnssNetworkConnectivityHandler.this.mActiveSubId))) {
                    return;
                }
                GnssNetworkConnectivityHandler.this.mActiveSubId = -1;
            }
        };
        this.mOnSubscriptionsChangeListener = onSubscriptionsChangedListener;
        this.mContext = context;
        this.mGnssNetworkListener = gnssNetworkListener;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager != null) {
            subscriptionManager.addOnSubscriptionsChangedListener(onSubscriptionsChangedListener);
        }
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "GnssNetworkConnectivityHandler");
        this.mHandler = new Handler(looper);
        this.mNiHandler = gpsNetInitiatedHandler;
        this.mConnMgr = (ConnectivityManager) context.getSystemService("connectivity");
        this.mSuplConnectivityCallback = null;
    }

    /* loaded from: classes.dex */
    public final class SubIdPhoneStateListener extends PhoneStateListener {
        public Integer mSubId;

        public SubIdPhoneStateListener(Integer num) {
            this.mSubId = num;
        }

        public void onPreciseCallStateChanged(PreciseCallState preciseCallState) {
            if (1 == preciseCallState.getForegroundCallState() || 3 == preciseCallState.getForegroundCallState()) {
                GnssNetworkConnectivityHandler.this.mActiveSubId = this.mSubId.intValue();
                if (GnssNetworkConnectivityHandler.DEBUG) {
                    Log.d("GnssNetworkConnectivityHandler", "mActiveSubId: " + GnssNetworkConnectivityHandler.this.mActiveSubId);
                }
            }
        }
    }

    public void registerNetworkCallbacks() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(12);
        builder.addCapability(16);
        builder.removeCapability(15);
        NetworkRequest build = builder.build();
        ConnectivityManager.NetworkCallback createNetworkConnectivityCallback = createNetworkConnectivityCallback();
        this.mNetworkConnectivityCallback = createNetworkConnectivityCallback;
        this.mConnMgr.registerNetworkCallback(build, createNetworkConnectivityCallback, this.mHandler);
    }

    public void unregisterNetworkCallbacks() {
        this.mConnMgr.unregisterNetworkCallback(this.mNetworkConnectivityCallback);
    }

    public boolean isDataNetworkConnected() {
        NetworkInfo activeNetworkInfo = this.mConnMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getActiveSubId() {
        return this.mActiveSubId;
    }

    public void onReportAGpsStatus(final int i, int i2, final byte[] bArr) {
        if (DEBUG) {
            Log.d("GnssNetworkConnectivityHandler", "AGPS_DATA_CONNECTION: " + agpsDataConnStatusAsString(i2));
        }
        if (i2 == 1) {
            runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GnssNetworkConnectivityHandler.this.lambda$onReportAGpsStatus$0(i, bArr);
                }
            });
        } else if (i2 == 2) {
            runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    GnssNetworkConnectivityHandler.this.lambda$onReportAGpsStatus$1();
                }
            });
        } else if (i2 == 3 || i2 == 4 || i2 == 5) {
        } else {
            Log.w("GnssNetworkConnectivityHandler", "Received unknown AGPS status: " + i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onReportAGpsStatus$1() {
        handleReleaseSuplConnection(2);
    }

    public final ConnectivityManager.NetworkCallback createNetworkConnectivityCallback() {
        return new ConnectivityManager.NetworkCallback() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler.2
            public HashMap<Network, NetworkCapabilities> mAvailableNetworkCapabilities = new HashMap<>(5);

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                if (!NetworkAttributes.hasCapabilitiesChanged(this.mAvailableNetworkCapabilities.get(network), networkCapabilities)) {
                    if (GnssNetworkConnectivityHandler.VERBOSE) {
                        Log.v("GnssNetworkConnectivityHandler", "Relevant network capabilities unchanged. Capabilities: " + networkCapabilities);
                        return;
                    }
                    return;
                }
                this.mAvailableNetworkCapabilities.put(network, networkCapabilities);
                if (GnssNetworkConnectivityHandler.DEBUG) {
                    Log.d("GnssNetworkConnectivityHandler", "Network connected/capabilities updated. Available networks count: " + this.mAvailableNetworkCapabilities.size());
                }
                GnssNetworkConnectivityHandler.this.mGnssNetworkListener.onNetworkAvailable();
                GnssNetworkConnectivityHandler.this.handleUpdateNetworkState(network, true, networkCapabilities);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                if (this.mAvailableNetworkCapabilities.remove(network) == null) {
                    Log.w("GnssNetworkConnectivityHandler", "Incorrectly received network callback onLost() before onCapabilitiesChanged() for network: " + network);
                    return;
                }
                Log.i("GnssNetworkConnectivityHandler", "Network connection lost. Available networks count: " + this.mAvailableNetworkCapabilities.size());
                GnssNetworkConnectivityHandler.this.handleUpdateNetworkState(network, false, null);
            }
        };
    }

    public final ConnectivityManager.NetworkCallback createSuplConnectivityCallback() {
        return new ConnectivityManager.NetworkCallback() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler.3
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                if (GnssNetworkConnectivityHandler.DEBUG) {
                    Log.d("GnssNetworkConnectivityHandler", "SUPL network connection available.");
                }
                GnssNetworkConnectivityHandler.this.handleSuplConnectionAvailable(network, linkProperties);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                Log.i("GnssNetworkConnectivityHandler", "SUPL network connection lost.");
                GnssNetworkConnectivityHandler.this.handleReleaseSuplConnection(2);
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onUnavailable() {
                Log.i("GnssNetworkConnectivityHandler", "SUPL network connection request timed out.");
                GnssNetworkConnectivityHandler.this.handleReleaseSuplConnection(5);
            }
        };
    }

    public final void runOnHandler(Runnable runnable) {
        this.mWakeLock.acquire(60000L);
        if (this.mHandler.post(runEventAndReleaseWakeLock(runnable))) {
            return;
        }
        this.mWakeLock.release();
    }

    public final Runnable runEventAndReleaseWakeLock(final Runnable runnable) {
        return new Runnable() { // from class: com.android.server.location.gnss.GnssNetworkConnectivityHandler$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                GnssNetworkConnectivityHandler.this.lambda$runEventAndReleaseWakeLock$2(runnable);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runEventAndReleaseWakeLock$2(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            this.mWakeLock.release();
        }
    }

    public final void handleUpdateNetworkState(Network network, boolean z, NetworkCapabilities networkCapabilities) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        boolean z2 = false;
        if (telephonyManager != null && z && telephonyManager.getDataEnabled()) {
            z2 = true;
        }
        boolean z3 = z2;
        NetworkAttributes updateTrackedNetworksState = updateTrackedNetworksState(z, network, networkCapabilities);
        String str = updateTrackedNetworksState.mApn;
        int i = updateTrackedNetworksState.mType;
        NetworkCapabilities networkCapabilities2 = updateTrackedNetworksState.mCapabilities;
        Log.i("GnssNetworkConnectivityHandler", String.format("updateNetworkState, state=%s, connected=%s, network=%s, capabilities=%s, availableNetworkCount: %d", agpsDataConnStateAsString(), Boolean.valueOf(z), network, networkCapabilities2, Integer.valueOf(this.mAvailableNetworkAttributes.size())));
        if (native_is_agps_ril_supported()) {
            boolean z4 = !networkCapabilities2.hasTransport(18);
            if (str == null) {
                str = "";
            }
            native_update_network_state(z, i, z4, z3, str, network.getNetworkHandle(), NetworkAttributes.getCapabilityFlags(networkCapabilities2));
        } else if (DEBUG) {
            Log.d("GnssNetworkConnectivityHandler", "Skipped network state update because GPS HAL AGPS-RIL is not  supported");
        }
    }

    public final NetworkAttributes updateTrackedNetworksState(boolean z, Network network, NetworkCapabilities networkCapabilities) {
        if (!z) {
            return this.mAvailableNetworkAttributes.remove(network);
        }
        NetworkAttributes networkAttributes = this.mAvailableNetworkAttributes.get(network);
        if (networkAttributes != null) {
            networkAttributes.mCapabilities = networkCapabilities;
            return networkAttributes;
        }
        NetworkAttributes networkAttributes2 = new NetworkAttributes();
        networkAttributes2.mCapabilities = networkCapabilities;
        NetworkInfo networkInfo = this.mConnMgr.getNetworkInfo(network);
        if (networkInfo != null) {
            networkAttributes2.mApn = networkInfo.getExtraInfo();
            networkAttributes2.mType = networkInfo.getType();
        }
        this.mAvailableNetworkAttributes.put(network, networkAttributes2);
        return networkAttributes2;
    }

    public final void handleSuplConnectionAvailable(Network network, LinkProperties linkProperties) {
        NetworkInfo networkInfo = this.mConnMgr.getNetworkInfo(network);
        String extraInfo = networkInfo != null ? networkInfo.getExtraInfo() : null;
        boolean z = DEBUG;
        if (z) {
            Log.d("GnssNetworkConnectivityHandler", String.format("handleSuplConnectionAvailable: state=%s, suplNetwork=%s, info=%s", agpsDataConnStateAsString(), network, networkInfo));
        }
        if (this.mAGpsDataConnectionState == 1) {
            if (extraInfo == null) {
                extraInfo = "dummy-apn";
            }
            if (this.mAGpsDataConnectionIpAddr != null) {
                setRouting();
            }
            int linkIpType = getLinkIpType(linkProperties);
            if (z) {
                Log.d("GnssNetworkConnectivityHandler", String.format("native_agps_data_conn_open: mAgpsApn=%s, mApnIpType=%s", extraInfo, Integer.valueOf(linkIpType)));
            }
            native_agps_data_conn_open(network.getNetworkHandle(), extraInfo, linkIpType);
            this.mAGpsDataConnectionState = 2;
        }
    }

    /* renamed from: handleRequestSuplConnection */
    public final void lambda$onReportAGpsStatus$0(int i, byte[] bArr) {
        this.mAGpsDataConnectionIpAddr = null;
        this.mAGpsType = i;
        if (bArr != null) {
            if (VERBOSE) {
                Log.v("GnssNetworkConnectivityHandler", "Received SUPL IP addr[]: " + Arrays.toString(bArr));
            }
            try {
                this.mAGpsDataConnectionIpAddr = InetAddress.getByAddress(bArr);
                if (DEBUG) {
                    Log.d("GnssNetworkConnectivityHandler", "IP address converted to: " + this.mAGpsDataConnectionIpAddr);
                }
            } catch (UnknownHostException e) {
                Log.e("GnssNetworkConnectivityHandler", "Bad IP Address: " + Arrays.toString(bArr), e);
            }
        }
        boolean z = DEBUG;
        if (z) {
            Log.d("GnssNetworkConnectivityHandler", String.format("requestSuplConnection, state=%s, agpsType=%s, address=%s", agpsDataConnStateAsString(), agpsTypeAsString(i), this.mAGpsDataConnectionIpAddr));
        }
        if (this.mAGpsDataConnectionState != 0) {
            return;
        }
        this.mAGpsDataConnectionState = 1;
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(getNetworkCapability(this.mAGpsType));
        builder.addTransportType(0);
        if (this.mNiHandler.getInEmergency() && this.mActiveSubId >= 0) {
            if (z) {
                Log.d("GnssNetworkConnectivityHandler", "Adding Network Specifier: " + Integer.toString(this.mActiveSubId));
            }
            builder.setNetworkSpecifier(Integer.toString(this.mActiveSubId));
            builder.removeCapability(13);
        }
        NetworkRequest build = builder.build();
        ConnectivityManager.NetworkCallback networkCallback = this.mSuplConnectivityCallback;
        if (networkCallback != null) {
            this.mConnMgr.unregisterNetworkCallback(networkCallback);
        }
        ConnectivityManager.NetworkCallback createSuplConnectivityCallback = createSuplConnectivityCallback();
        this.mSuplConnectivityCallback = createSuplConnectivityCallback;
        try {
            this.mConnMgr.requestNetwork(build, createSuplConnectivityCallback, this.mHandler, 20000);
        } catch (RuntimeException e2) {
            Log.e("GnssNetworkConnectivityHandler", "Failed to request network.", e2);
            this.mSuplConnectivityCallback = null;
            handleReleaseSuplConnection(5);
        }
    }

    public final int getNetworkCapability(int i) {
        if (i == 1 || i == 2) {
            return 1;
        }
        if (i != 3) {
            if (i == 4) {
                return 4;
            }
            throw new IllegalArgumentException("agpsType: " + i);
        }
        return 10;
    }

    public final void handleReleaseSuplConnection(int i) {
        if (DEBUG) {
            Log.d("GnssNetworkConnectivityHandler", String.format("releaseSuplConnection, state=%s, status=%s", agpsDataConnStateAsString(), agpsDataConnStatusAsString(i)));
        }
        if (this.mAGpsDataConnectionState == 0) {
            return;
        }
        this.mAGpsDataConnectionState = 0;
        ConnectivityManager.NetworkCallback networkCallback = this.mSuplConnectivityCallback;
        if (networkCallback != null) {
            this.mConnMgr.unregisterNetworkCallback(networkCallback);
            this.mSuplConnectivityCallback = null;
        }
        if (i == 2) {
            native_agps_data_conn_closed();
        } else if (i == 5) {
            native_agps_data_conn_failed();
        } else {
            Log.e("GnssNetworkConnectivityHandler", "Invalid status to release SUPL connection: " + i);
        }
    }

    public final void setRouting() {
        if (!this.mConnMgr.requestRouteToHostAddress(3, this.mAGpsDataConnectionIpAddr)) {
            Log.e("GnssNetworkConnectivityHandler", "Error requesting route to host: " + this.mAGpsDataConnectionIpAddr);
        } else if (DEBUG) {
            Log.d("GnssNetworkConnectivityHandler", "Successfully requested route to host: " + this.mAGpsDataConnectionIpAddr);
        }
    }

    public final void ensureInHandlerThread() {
        if (this.mHandler == null || Looper.myLooper() != this.mHandler.getLooper()) {
            throw new IllegalStateException("This method must run on the Handler thread.");
        }
    }

    public final String agpsDataConnStateAsString() {
        int i = this.mAGpsDataConnectionState;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return "<Unknown>(" + this.mAGpsDataConnectionState + ")";
                }
                return "OPEN";
            }
            return "OPENING";
        }
        return "CLOSED";
    }

    public final String agpsDataConnStatusAsString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            return "<Unknown>(" + i + ")";
                        }
                        return "FAILED";
                    }
                    return "DONE";
                }
                return "CONNECTED";
            }
            return "RELEASE";
        }
        return "REQUEST";
    }

    public final String agpsTypeAsString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        return "<Unknown>(" + i + ")";
                    }
                    return "IMS";
                }
                return "EIMS";
            }
            return "C2K";
        }
        return "SUPL";
    }

    public final int getLinkIpType(LinkProperties linkProperties) {
        ensureInHandlerThread();
        boolean z = false;
        boolean z2 = false;
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            InetAddress address = linkAddress.getAddress();
            if (address instanceof Inet4Address) {
                z = true;
            } else if (address instanceof Inet6Address) {
                z2 = true;
            }
            if (DEBUG) {
                Log.d("GnssNetworkConnectivityHandler", "LinkAddress : " + address.toString());
            }
        }
        if (z && z2) {
            return 3;
        }
        if (z) {
            return 1;
        }
        return z2 ? 2 : 0;
    }

    public boolean isNativeAgpsRilSupported() {
        return native_is_agps_ril_supported();
    }
}
