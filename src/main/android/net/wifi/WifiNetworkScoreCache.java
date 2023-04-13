package android.net.wifi;

import android.Manifest;
import android.content.Context;
import android.net.INetworkScoreCache;
import android.net.NetworkKey;
import android.net.ScoredNetwork;
import android.p008os.Handler;
import android.p008os.Process;
import android.util.Log;
import android.util.LruCache;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public class WifiNetworkScoreCache extends INetworkScoreCache.Stub {
    private static final int DEFAULT_MAX_CACHE_SIZE = 100;
    public static final int INVALID_NETWORK_SCORE = -128;
    private final LruCache<String, ScoredNetwork> mCache;
    private final Context mContext;
    private CacheListener mListener;
    private final Object mLock;
    private static final String TAG = "WifiNetworkScoreCache";
    private static final boolean DBG = Log.isLoggable(TAG, 3);

    public WifiNetworkScoreCache(Context context) {
        this(context, null);
    }

    public WifiNetworkScoreCache(Context context, CacheListener listener) {
        this(context, listener, 100);
    }

    public WifiNetworkScoreCache(Context context, CacheListener listener, int maxCacheSize) {
        this.mLock = new Object();
        this.mContext = context.getApplicationContext();
        this.mListener = listener;
        this.mCache = new LruCache<>(maxCacheSize);
    }

    @Override // android.net.INetworkScoreCache
    public final void updateScores(List<ScoredNetwork> networks) {
        if (networks == null || networks.isEmpty()) {
            return;
        }
        if (DBG) {
            Log.m112d(TAG, "updateScores list size=" + networks.size());
        }
        boolean changed = false;
        synchronized (this.mLock) {
            for (ScoredNetwork network : networks) {
                String networkKey = buildNetworkKey(network);
                if (networkKey == null) {
                    if (DBG) {
                        Log.m112d(TAG, "Failed to build network key for ScoredNetwork" + network);
                    }
                } else {
                    this.mCache.put(networkKey, network);
                    changed = true;
                }
            }
            CacheListener cacheListener = this.mListener;
            if (cacheListener != null && changed) {
                cacheListener.post(networks);
            }
        }
    }

    @Override // android.net.INetworkScoreCache
    public final void clearScores() {
        synchronized (this.mLock) {
            this.mCache.evictAll();
        }
    }

    public boolean isScoredNetwork(ScanResult result) {
        return getScoredNetwork(result) != null;
    }

    public boolean hasScoreCurve(ScanResult result) {
        ScoredNetwork network = getScoredNetwork(result);
        return (network == null || network.rssiCurve == null) ? false : true;
    }

    public int getNetworkScore(ScanResult result) {
        int score = -128;
        ScoredNetwork network = getScoredNetwork(result);
        if (network != null && network.rssiCurve != null) {
            score = network.rssiCurve.lookupScore(result.level);
            if (DBG) {
                Log.m112d(TAG, "getNetworkScore found scored network " + network.networkKey + " score " + Integer.toString(score) + " RSSI " + result.level);
            }
        }
        return score;
    }

    public boolean getMeteredHint(ScanResult result) {
        ScoredNetwork network = getScoredNetwork(result);
        return network != null && network.meteredHint;
    }

    public int getNetworkScore(ScanResult result, boolean isActiveNetwork) {
        int score = -128;
        ScoredNetwork network = getScoredNetwork(result);
        if (network != null && network.rssiCurve != null) {
            score = network.rssiCurve.lookupScore(result.level, isActiveNetwork);
            if (DBG) {
                Log.m112d(TAG, "getNetworkScore found scored network " + network.networkKey + " score " + Integer.toString(score) + " RSSI " + result.level + " isActiveNetwork " + isActiveNetwork);
            }
        }
        return score;
    }

    public ScoredNetwork getScoredNetwork(ScanResult result) {
        ScoredNetwork network;
        String key = buildNetworkKey(result);
        if (key == null) {
            return null;
        }
        synchronized (this.mLock) {
            network = this.mCache.get(key);
        }
        return network;
    }

    public ScoredNetwork getScoredNetwork(NetworkKey networkKey) {
        ScoredNetwork scoredNetwork;
        String key = buildNetworkKey(networkKey);
        if (key == null) {
            if (DBG) {
                Log.m112d(TAG, "Could not build key string for Network Key: " + networkKey);
                return null;
            }
            return null;
        }
        synchronized (this.mLock) {
            scoredNetwork = this.mCache.get(key);
        }
        return scoredNetwork;
    }

    private String buildNetworkKey(ScoredNetwork network) {
        if (network == null) {
            return null;
        }
        return buildNetworkKey(network.networkKey);
    }

    private String buildNetworkKey(NetworkKey networkKey) {
        String key;
        if (networkKey == null || networkKey.wifiKey == null || networkKey.type != 1 || (key = networkKey.wifiKey.ssid) == null) {
            return null;
        }
        if (networkKey.wifiKey.bssid != null) {
            return key + networkKey.wifiKey.bssid;
        }
        return key;
    }

    private String buildNetworkKey(ScanResult result) {
        if (result == null || result.SSID == null) {
            return null;
        }
        StringBuilder key = new StringBuilder("\"");
        key.append(result.SSID);
        key.append("\"");
        if (result.BSSID != null) {
            key.append(result.BSSID);
        }
        return key.toString();
    }

    @Override // android.p008os.Binder
    protected final void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.C0000permission.DUMP, TAG);
        String header = String.format("WifiNetworkScoreCache (%s/%d)", this.mContext.getPackageName(), Integer.valueOf(Process.myUid()));
        writer.println(header);
        writer.println("  All score curves:");
        synchronized (this.mLock) {
            for (ScoredNetwork score : this.mCache.snapshot().values()) {
                writer.println("    " + score);
            }
            writer.println("  Network scores for latest ScanResults:");
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            for (ScanResult scanResult : wifiManager.getScanResults()) {
                writer.println("    " + buildNetworkKey(scanResult) + ": " + getNetworkScore(scanResult));
            }
        }
    }

    public void registerListener(CacheListener listener) {
        synchronized (this.mLock) {
            this.mListener = listener;
        }
    }

    public void unregisterListener() {
        synchronized (this.mLock) {
            this.mListener = null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class CacheListener {
        private Handler mHandler;

        public abstract void networkCacheUpdated(List<ScoredNetwork> list);

        public CacheListener(Handler handler) {
            Objects.requireNonNull(handler);
            this.mHandler = handler;
        }

        void post(final List<ScoredNetwork> updatedNetworks) {
            this.mHandler.post(new Runnable() { // from class: android.net.wifi.WifiNetworkScoreCache.CacheListener.1
                @Override // java.lang.Runnable
                public void run() {
                    CacheListener.this.networkCacheUpdated(updatedNetworks);
                }
            });
        }
    }
}
