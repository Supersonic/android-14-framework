package com.android.server.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;
/* loaded from: classes5.dex */
public class NetworkPinner extends ConnectivityManager.NetworkCallback {
    private static ConnectivityManager sCM;
    private static Callback sCallback;
    protected static Network sNetwork;
    private static final String TAG = NetworkPinner.class.getSimpleName();
    protected static final Object sLock = new Object();

    private static void maybeInitConnectivityManager(Context context) {
        if (sCM == null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            sCM = connectivityManager;
            if (connectivityManager == null) {
                throw new IllegalStateException("Bad luck, ConnectivityService not started.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class Callback extends ConnectivityManager.NetworkCallback {
        private Callback() {
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            synchronized (NetworkPinner.sLock) {
                if (this != NetworkPinner.sCallback) {
                    return;
                }
                if (NetworkPinner.sCM.getBoundNetworkForProcess() == null && NetworkPinner.sNetwork == null) {
                    NetworkPinner.sCM.bindProcessToNetwork(network);
                    NetworkPinner.sNetwork = network;
                    Log.m112d(NetworkPinner.TAG, "Wifi alternate reality enabled on network " + network);
                }
                NetworkPinner.sLock.notify();
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            synchronized (NetworkPinner.sLock) {
                if (this != NetworkPinner.sCallback) {
                    return;
                }
                if (network.equals(NetworkPinner.sNetwork) && network.equals(NetworkPinner.sCM.getBoundNetworkForProcess())) {
                    NetworkPinner.unpin();
                    Log.m112d(NetworkPinner.TAG, "Wifi alternate reality disabled on network " + network);
                }
                NetworkPinner.sLock.notify();
            }
        }
    }

    public static void pin(Context context, NetworkRequest request) {
        synchronized (sLock) {
            if (sCallback == null) {
                maybeInitConnectivityManager(context);
                Callback callback = new Callback();
                sCallback = callback;
                try {
                    sCM.registerNetworkCallback(request, callback);
                } catch (SecurityException e) {
                    Log.m111d(TAG, "Failed to register network callback", e);
                    sCallback = null;
                }
            }
        }
    }

    public static void unpin() {
        synchronized (sLock) {
            if (sCallback != null) {
                try {
                    sCM.bindProcessToNetwork(null);
                    sCM.unregisterNetworkCallback(sCallback);
                } catch (SecurityException e) {
                    Log.m111d(TAG, "Failed to unregister network callback", e);
                }
                sCallback = null;
                sNetwork = null;
            }
        }
    }
}
