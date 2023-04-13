package com.android.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.INetworkRecommendationProvider;
import android.net.INetworkScoreCache;
import android.net.INetworkScoreService;
import android.net.NetworkKey;
import android.net.NetworkScorerAppData;
import android.net.ScoredNetwork;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiScanner;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.DumpUtils;
import com.android.server.NetworkScoreService;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
/* loaded from: classes.dex */
public class NetworkScoreService extends INetworkScoreService.Stub {
    public static final boolean DBG;
    public static final boolean VERBOSE;
    public final Context mContext;
    public final Handler mHandler;
    public BroadcastReceiver mLocationModeReceiver;
    public final NetworkScorerAppManager mNetworkScorerAppManager;
    @GuardedBy({"mPackageMonitorLock"})
    public NetworkScorerPackageMonitor mPackageMonitor;
    public final Object mPackageMonitorLock;
    public final DispatchingContentObserver mRecommendationSettingsObserver;
    @GuardedBy({"mScoreCaches"})
    public final Map<Integer, RemoteCallbackList<INetworkScoreCache>> mScoreCaches;
    public final Function<NetworkScorerAppData, ScoringServiceConnection> mServiceConnProducer;
    @GuardedBy({"mServiceConnectionLock"})
    public ScoringServiceConnection mServiceConnection;
    public final Object mServiceConnectionLock;
    public final ContentObserver mUseOpenWifiPackageObserver;
    public BroadcastReceiver mUserIntentReceiver;

    static {
        boolean z = true;
        DBG = Build.IS_DEBUGGABLE && Log.isLoggable("NetworkScoreService", 3);
        if (!Build.IS_DEBUGGABLE || !Log.isLoggable("NetworkScoreService", 2)) {
            z = false;
        }
        VERBOSE = z;
    }

    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public final NetworkScoreService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new NetworkScoreService(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            Log.i("NetworkScoreService", "Registering network_score");
            publishBinderService("network_score", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 500) {
                this.mService.systemReady();
            } else if (i == 1000) {
                this.mService.systemRunning();
            }
        }
    }

    /* loaded from: classes.dex */
    public class NetworkScorerPackageMonitor extends PackageMonitor {
        public final String mPackageToWatch;

        public NetworkScorerPackageMonitor(String str) {
            this.mPackageToWatch = str;
        }

        public void onPackageAdded(String str, int i) {
            evaluateBinding(str, true);
        }

        public void onPackageRemoved(String str, int i) {
            evaluateBinding(str, true);
        }

        public void onPackageModified(String str) {
            evaluateBinding(str, false);
        }

        public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
            if (z) {
                for (String str : strArr) {
                    evaluateBinding(str, true);
                }
            }
            return super.onHandleForceStop(intent, strArr, i, z);
        }

        public void onPackageUpdateFinished(String str, int i) {
            evaluateBinding(str, true);
        }

        public final void evaluateBinding(String str, boolean z) {
            if (this.mPackageToWatch.equals(str)) {
                if (NetworkScoreService.DBG) {
                    Log.d("NetworkScoreService", "Evaluating binding for: " + str + ", forceUnbind=" + z);
                }
                NetworkScorerAppData activeScorer = NetworkScoreService.this.mNetworkScorerAppManager.getActiveScorer();
                if (activeScorer == null) {
                    if (NetworkScoreService.DBG) {
                        Log.d("NetworkScoreService", "No active scorers available.");
                    }
                    NetworkScoreService.this.refreshBinding();
                    return;
                }
                if (z) {
                    NetworkScoreService.this.unbindFromScoringServiceIfNeeded();
                }
                if (NetworkScoreService.DBG) {
                    Log.d("NetworkScoreService", "Binding to " + activeScorer.getRecommendationServiceComponent() + " if needed.");
                }
                NetworkScoreService.this.bindToScoringServiceIfNeeded(activeScorer);
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class DispatchingContentObserver extends ContentObserver {
        public final Context mContext;
        public final Handler mHandler;
        public final Map<Uri, Integer> mUriEventMap;

        public DispatchingContentObserver(Context context, Handler handler) {
            super(handler);
            this.mContext = context;
            this.mHandler = handler;
            this.mUriEventMap = new ArrayMap();
        }

        public void observe(Uri uri, int i) {
            this.mUriEventMap.put(uri, Integer.valueOf(i));
            this.mContext.getContentResolver().registerContentObserver(uri, false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            onChange(z, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (NetworkScoreService.DBG) {
                Log.d("NetworkScoreService", String.format("onChange(%s, %s)", Boolean.valueOf(z), uri));
            }
            Integer num = this.mUriEventMap.get(uri);
            if (num != null) {
                this.mHandler.obtainMessage(num.intValue()).sendToTarget();
                return;
            }
            Log.w("NetworkScoreService", "No matching event to send for URI = " + uri);
        }
    }

    public NetworkScoreService(Context context) {
        this(context, new NetworkScorerAppManager(context), new Function() { // from class: com.android.server.NetworkScoreService$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return new NetworkScoreService.ScoringServiceConnection((NetworkScorerAppData) obj);
            }
        }, Looper.myLooper());
    }

    @VisibleForTesting
    public NetworkScoreService(Context context, NetworkScorerAppManager networkScorerAppManager, Function<NetworkScorerAppData, ScoringServiceConnection> function, Looper looper) {
        this.mPackageMonitorLock = new Object();
        this.mServiceConnectionLock = new Object();
        this.mUserIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.NetworkScoreService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (NetworkScoreService.DBG) {
                    Log.d("NetworkScoreService", "Received " + action + " for userId " + intExtra);
                }
                if (intExtra != -10000 && "android.intent.action.USER_UNLOCKED".equals(action)) {
                    NetworkScoreService.this.onUserUnlocked(intExtra);
                }
            }
        };
        this.mLocationModeReceiver = new BroadcastReceiver() { // from class: com.android.server.NetworkScoreService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.location.MODE_CHANGED".equals(intent.getAction())) {
                    NetworkScoreService.this.refreshBinding();
                }
            }
        };
        this.mContext = context;
        this.mNetworkScorerAppManager = networkScorerAppManager;
        this.mScoreCaches = new ArrayMap();
        context.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.SYSTEM, new IntentFilter("android.intent.action.USER_UNLOCKED"), null, null);
        ServiceHandler serviceHandler = new ServiceHandler(looper);
        this.mHandler = serviceHandler;
        context.registerReceiverAsUser(this.mLocationModeReceiver, UserHandle.SYSTEM, new IntentFilter("android.location.MODE_CHANGED"), null, serviceHandler);
        this.mRecommendationSettingsObserver = new DispatchingContentObserver(context, serviceHandler);
        this.mServiceConnProducer = function;
        ContentObserver contentObserver = new ContentObserver(serviceHandler) { // from class: com.android.server.NetworkScoreService.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri, int i) {
                if (Settings.Global.getUriFor("use_open_wifi_package").equals(uri)) {
                    String string = Settings.Global.getString(NetworkScoreService.this.mContext.getContentResolver(), "use_open_wifi_package");
                    if (TextUtils.isEmpty(string)) {
                        return;
                    }
                    ((LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class)).grantDefaultPermissionsToDefaultUseOpenWifiApp(string, i);
                }
            }
        };
        this.mUseOpenWifiPackageObserver = contentObserver;
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("use_open_wifi_package"), false, contentObserver);
        ((LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class)).setUseOpenWifiAppPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.NetworkScoreService$$ExternalSyntheticLambda0
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$new$0;
                lambda$new$0 = NetworkScoreService.this.lambda$new$0(i);
                return lambda$new$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$new$0(int i) {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "use_open_wifi_package");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return new String[]{string};
    }

    public void systemReady() {
        if (DBG) {
            Log.d("NetworkScoreService", "systemReady");
        }
        registerRecommendationSettingsObserver();
    }

    public void systemRunning() {
        if (DBG) {
            Log.d("NetworkScoreService", "systemRunning");
        }
    }

    @VisibleForTesting
    public void onUserUnlocked(int i) {
        if (DBG) {
            Log.d("NetworkScoreService", "onUserUnlocked(" + i + ")");
        }
        refreshBinding();
    }

    public final void refreshBinding() {
        if (DBG) {
            Log.d("NetworkScoreService", "refreshBinding()");
        }
        this.mNetworkScorerAppManager.updateState();
        this.mNetworkScorerAppManager.migrateNetworkScorerAppSettingIfNeeded();
        registerPackageMonitorIfNeeded();
        bindToScoringServiceIfNeeded();
    }

    public final void registerRecommendationSettingsObserver() {
        this.mRecommendationSettingsObserver.observe(Settings.Global.getUriFor("network_recommendations_package"), 1);
        this.mRecommendationSettingsObserver.observe(Settings.Global.getUriFor("network_recommendations_enabled"), 2);
    }

    public final void registerPackageMonitorIfNeeded() {
        boolean z = DBG;
        if (z) {
            Log.d("NetworkScoreService", "registerPackageMonitorIfNeeded()");
        }
        NetworkScorerAppData activeScorer = this.mNetworkScorerAppManager.getActiveScorer();
        synchronized (this.mPackageMonitorLock) {
            if (this.mPackageMonitor != null && (activeScorer == null || !activeScorer.getRecommendationServicePackageName().equals(this.mPackageMonitor.mPackageToWatch))) {
                if (z) {
                    Log.d("NetworkScoreService", "Unregistering package monitor for " + this.mPackageMonitor.mPackageToWatch);
                }
                this.mPackageMonitor.unregister();
                this.mPackageMonitor = null;
            }
            if (activeScorer != null && this.mPackageMonitor == null) {
                NetworkScorerPackageMonitor networkScorerPackageMonitor = new NetworkScorerPackageMonitor(activeScorer.getRecommendationServicePackageName());
                this.mPackageMonitor = networkScorerPackageMonitor;
                networkScorerPackageMonitor.register(this.mContext, (Looper) null, UserHandle.SYSTEM, false);
                if (z) {
                    Log.d("NetworkScoreService", "Registered package monitor for " + this.mPackageMonitor.mPackageToWatch);
                }
            }
        }
    }

    public final void bindToScoringServiceIfNeeded() {
        if (DBG) {
            Log.d("NetworkScoreService", "bindToScoringServiceIfNeeded");
        }
        bindToScoringServiceIfNeeded(this.mNetworkScorerAppManager.getActiveScorer());
    }

    public final void bindToScoringServiceIfNeeded(NetworkScorerAppData networkScorerAppData) {
        if (DBG) {
            Log.d("NetworkScoreService", "bindToScoringServiceIfNeeded(" + networkScorerAppData + ")");
        }
        if (networkScorerAppData != null) {
            synchronized (this.mServiceConnectionLock) {
                ScoringServiceConnection scoringServiceConnection = this.mServiceConnection;
                if (scoringServiceConnection != null && !scoringServiceConnection.getAppData().equals(networkScorerAppData)) {
                    unbindFromScoringServiceIfNeeded();
                }
                if (this.mServiceConnection == null) {
                    this.mServiceConnection = this.mServiceConnProducer.apply(networkScorerAppData);
                }
                this.mServiceConnection.bind(this.mContext);
            }
            return;
        }
        unbindFromScoringServiceIfNeeded();
    }

    public final void unbindFromScoringServiceIfNeeded() {
        boolean z = DBG;
        if (z) {
            Log.d("NetworkScoreService", "unbindFromScoringServiceIfNeeded");
        }
        synchronized (this.mServiceConnectionLock) {
            ScoringServiceConnection scoringServiceConnection = this.mServiceConnection;
            if (scoringServiceConnection != null) {
                scoringServiceConnection.unbind(this.mContext);
                if (z) {
                    Log.d("NetworkScoreService", "Disconnected from: " + this.mServiceConnection.getAppData().getRecommendationServiceComponent());
                }
            }
            this.mServiceConnection = null;
        }
        clearInternal();
    }

    public boolean updateScores(ScoredNetwork[] scoredNetworkArr) {
        RemoteCallbackList<INetworkScoreCache> remoteCallbackList;
        if (!isCallerActiveScorer(INetworkScoreService.Stub.getCallingUid())) {
            throw new SecurityException("Caller with UID " + INetworkScoreService.Stub.getCallingUid() + " is not the active scorer.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ArrayMap arrayMap = new ArrayMap();
            for (ScoredNetwork scoredNetwork : scoredNetworkArr) {
                List list = (List) arrayMap.get(Integer.valueOf(scoredNetwork.networkKey.type));
                if (list == null) {
                    list = new ArrayList();
                    arrayMap.put(Integer.valueOf(scoredNetwork.networkKey.type), list);
                }
                list.add(scoredNetwork);
            }
            Iterator it = arrayMap.entrySet().iterator();
            while (true) {
                boolean z = true;
                if (!it.hasNext()) {
                    return true;
                }
                Map.Entry entry = (Map.Entry) it.next();
                synchronized (this.mScoreCaches) {
                    remoteCallbackList = this.mScoreCaches.get(entry.getKey());
                    if (remoteCallbackList != null && remoteCallbackList.getRegisteredCallbackCount() != 0) {
                        z = false;
                    }
                }
                if (z) {
                    if (Log.isLoggable("NetworkScoreService", 2)) {
                        Log.v("NetworkScoreService", "No scorer registered for type " + entry.getKey() + ", discarding");
                    }
                } else {
                    sendCacheUpdateCallback(FilteringCacheUpdatingConsumer.create(this.mContext, (List) entry.getValue(), ((Integer) entry.getKey()).intValue()), Collections.singleton(remoteCallbackList));
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class FilteringCacheUpdatingConsumer implements BiConsumer<INetworkScoreCache, Object> {
        public final Context mContext;
        public UnaryOperator<List<ScoredNetwork>> mCurrentNetworkFilter;
        public final int mNetworkType;
        public UnaryOperator<List<ScoredNetwork>> mScanResultsFilter;
        public final List<ScoredNetwork> mScoredNetworkList;

        public static FilteringCacheUpdatingConsumer create(Context context, List<ScoredNetwork> list, int i) {
            return new FilteringCacheUpdatingConsumer(context, list, i, null, null);
        }

        @VisibleForTesting
        public FilteringCacheUpdatingConsumer(Context context, List<ScoredNetwork> list, int i, UnaryOperator<List<ScoredNetwork>> unaryOperator, UnaryOperator<List<ScoredNetwork>> unaryOperator2) {
            this.mContext = context;
            this.mScoredNetworkList = list;
            this.mNetworkType = i;
            this.mCurrentNetworkFilter = unaryOperator;
            this.mScanResultsFilter = unaryOperator2;
        }

        @Override // java.util.function.BiConsumer
        public void accept(INetworkScoreCache iNetworkScoreCache, Object obj) {
            try {
                List<ScoredNetwork> filterScores = filterScores(this.mScoredNetworkList, obj instanceof Integer ? ((Integer) obj).intValue() : 0);
                if (filterScores.isEmpty()) {
                    return;
                }
                iNetworkScoreCache.updateScores(filterScores);
            } catch (RemoteException e) {
                if (NetworkScoreService.VERBOSE) {
                    Log.v("NetworkScoreService", "Unable to update scores of type " + this.mNetworkType, e);
                }
            }
        }

        public final List<ScoredNetwork> filterScores(List<ScoredNetwork> list, int i) {
            if (i != 0) {
                if (i == 1) {
                    if (this.mCurrentNetworkFilter == null) {
                        this.mCurrentNetworkFilter = new CurrentNetworkScoreCacheFilter(new WifiInfoSupplier(this.mContext));
                    }
                    return (List) this.mCurrentNetworkFilter.apply(list);
                } else if (i == 2) {
                    if (this.mScanResultsFilter == null) {
                        this.mScanResultsFilter = new ScanResultsScoreCacheFilter(new ScanResultsSupplier(this.mContext));
                    }
                    return (List) this.mScanResultsFilter.apply(list);
                } else {
                    Log.w("NetworkScoreService", "Unknown filter type: " + i);
                    return list;
                }
            }
            return list;
        }
    }

    /* loaded from: classes.dex */
    public static class WifiInfoSupplier implements Supplier<WifiInfo> {
        public final Context mContext;

        public WifiInfoSupplier(Context context) {
            this.mContext = context;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.function.Supplier
        public WifiInfo get() {
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
            if (wifiManager != null) {
                return wifiManager.getConnectionInfo();
            }
            Log.w("NetworkScoreService", "WifiManager is null, failed to return the WifiInfo.");
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class ScanResultsSupplier implements Supplier<List<ScanResult>> {
        public final Context mContext;

        public ScanResultsSupplier(Context context) {
            this.mContext = context;
        }

        @Override // java.util.function.Supplier
        public List<ScanResult> get() {
            WifiScanner wifiScanner = (WifiScanner) this.mContext.getSystemService(WifiScanner.class);
            if (wifiScanner != null) {
                return wifiScanner.getSingleScanResults();
            }
            Log.w("NetworkScoreService", "WifiScanner is null, failed to return scan results.");
            return Collections.emptyList();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class CurrentNetworkScoreCacheFilter implements UnaryOperator<List<ScoredNetwork>> {
        public final NetworkKey mCurrentNetwork;

        public CurrentNetworkScoreCacheFilter(Supplier<WifiInfo> supplier) {
            this.mCurrentNetwork = NetworkKey.createFromWifiInfo(supplier.get());
        }

        @Override // java.util.function.Function
        public List<ScoredNetwork> apply(List<ScoredNetwork> list) {
            if (this.mCurrentNetwork == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            for (int i = 0; i < list.size(); i++) {
                ScoredNetwork scoredNetwork = list.get(i);
                if (scoredNetwork.networkKey.equals(this.mCurrentNetwork)) {
                    return Collections.singletonList(scoredNetwork);
                }
            }
            return Collections.emptyList();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ScanResultsScoreCacheFilter implements UnaryOperator<List<ScoredNetwork>> {
        public final Set<NetworkKey> mScanResultKeys;

        public ScanResultsScoreCacheFilter(Supplier<List<ScanResult>> supplier) {
            List<ScanResult> list = supplier.get();
            int size = list.size();
            this.mScanResultKeys = new ArraySet(size);
            for (int i = 0; i < size; i++) {
                NetworkKey createFromScanResult = NetworkKey.createFromScanResult(list.get(i));
                if (createFromScanResult != null) {
                    this.mScanResultKeys.add(createFromScanResult);
                }
            }
        }

        @Override // java.util.function.Function
        public List<ScoredNetwork> apply(List<ScoredNetwork> list) {
            if (this.mScanResultKeys.isEmpty() || list.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                ScoredNetwork scoredNetwork = list.get(i);
                if (this.mScanResultKeys.contains(scoredNetwork.networkKey)) {
                    arrayList.add(scoredNetwork);
                }
            }
            return arrayList;
        }
    }

    public boolean clearScores() {
        enforceSystemOrIsActiveScorer(INetworkScoreService.Stub.getCallingUid());
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            clearInternal();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public boolean setActiveScorer(String str) {
        enforceSystemOrHasScoreNetworks();
        return this.mNetworkScorerAppManager.setActiveScorer(str);
    }

    public boolean isCallerActiveScorer(int i) {
        boolean z;
        synchronized (this.mServiceConnectionLock) {
            ScoringServiceConnection scoringServiceConnection = this.mServiceConnection;
            z = scoringServiceConnection != null && scoringServiceConnection.getAppData().packageUid == i;
        }
        return z;
    }

    public final void enforceSystemOnly() throws SecurityException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.REQUEST_NETWORK_SCORES", "Caller must be granted REQUEST_NETWORK_SCORES.");
    }

    public final void enforceSystemOrHasScoreNetworks() throws SecurityException {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.REQUEST_NETWORK_SCORES") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.SCORE_NETWORKS") != 0) {
            throw new SecurityException("Caller is neither the system process or a network scorer.");
        }
    }

    public final void enforceSystemOrIsActiveScorer(int i) throws SecurityException {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.REQUEST_NETWORK_SCORES") != 0 && !isCallerActiveScorer(i)) {
            throw new SecurityException("Caller is neither the system process or the active network scorer.");
        }
    }

    public String getActiveScorerPackage() {
        enforceSystemOrHasScoreNetworks();
        NetworkScorerAppData activeScorer = this.mNetworkScorerAppManager.getActiveScorer();
        if (activeScorer == null) {
            return null;
        }
        return activeScorer.getRecommendationServicePackageName();
    }

    public NetworkScorerAppData getActiveScorer() {
        enforceSystemOnly();
        return this.mNetworkScorerAppManager.getActiveScorer();
    }

    public List<NetworkScorerAppData> getAllValidScorers() {
        enforceSystemOnly();
        return this.mNetworkScorerAppManager.getAllValidScorers();
    }

    public void disableScoring() {
        enforceSystemOrIsActiveScorer(INetworkScoreService.Stub.getCallingUid());
    }

    public final void clearInternal() {
        sendCacheUpdateCallback(new BiConsumer<INetworkScoreCache, Object>() { // from class: com.android.server.NetworkScoreService.4
            @Override // java.util.function.BiConsumer
            public void accept(INetworkScoreCache iNetworkScoreCache, Object obj) {
                try {
                    iNetworkScoreCache.clearScores();
                } catch (RemoteException e) {
                    if (Log.isLoggable("NetworkScoreService", 2)) {
                        Log.v("NetworkScoreService", "Unable to clear scores", e);
                    }
                }
            }
        }, getScoreCacheLists());
    }

    public void registerNetworkScoreCache(int i, INetworkScoreCache iNetworkScoreCache, int i2) {
        enforceSystemOnly();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mScoreCaches) {
                RemoteCallbackList<INetworkScoreCache> remoteCallbackList = this.mScoreCaches.get(Integer.valueOf(i));
                if (remoteCallbackList == null) {
                    remoteCallbackList = new RemoteCallbackList<>();
                    this.mScoreCaches.put(Integer.valueOf(i), remoteCallbackList);
                }
                if (!remoteCallbackList.register(iNetworkScoreCache, Integer.valueOf(i2))) {
                    if (remoteCallbackList.getRegisteredCallbackCount() == 0) {
                        this.mScoreCaches.remove(Integer.valueOf(i));
                    }
                    if (Log.isLoggable("NetworkScoreService", 2)) {
                        Log.v("NetworkScoreService", "Unable to register NetworkScoreCache for type " + i);
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unregisterNetworkScoreCache(int i, INetworkScoreCache iNetworkScoreCache) {
        enforceSystemOnly();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mScoreCaches) {
                RemoteCallbackList<INetworkScoreCache> remoteCallbackList = this.mScoreCaches.get(Integer.valueOf(i));
                if (remoteCallbackList != null && remoteCallbackList.unregister(iNetworkScoreCache)) {
                    if (remoteCallbackList.getRegisteredCallbackCount() == 0) {
                        this.mScoreCaches.remove(Integer.valueOf(i));
                    }
                }
                if (Log.isLoggable("NetworkScoreService", 2)) {
                    Log.v("NetworkScoreService", "Unable to unregister NetworkScoreCache for type " + i);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean requestScores(NetworkKey[] networkKeyArr) {
        enforceSystemOnly();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            INetworkRecommendationProvider recommendationProvider = getRecommendationProvider();
            if (recommendationProvider != null) {
                try {
                    recommendationProvider.requestScores(networkKeyArr);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return true;
                } catch (RemoteException e) {
                    Log.w("NetworkScoreService", "Failed to request scores.", e);
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "NetworkScoreService", printWriter)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                NetworkScorerAppData activeScorer = this.mNetworkScorerAppManager.getActiveScorer();
                if (activeScorer == null) {
                    printWriter.println("Scoring is disabled.");
                    return;
                }
                printWriter.println("Current scorer: " + activeScorer);
                synchronized (this.mServiceConnectionLock) {
                    ScoringServiceConnection scoringServiceConnection = this.mServiceConnection;
                    if (scoringServiceConnection != null) {
                        scoringServiceConnection.dump(fileDescriptor, printWriter, strArr);
                    } else {
                        printWriter.println("ScoringServiceConnection: null");
                    }
                }
                printWriter.flush();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final Collection<RemoteCallbackList<INetworkScoreCache>> getScoreCacheLists() {
        ArrayList arrayList;
        synchronized (this.mScoreCaches) {
            arrayList = new ArrayList(this.mScoreCaches.values());
        }
        return arrayList;
    }

    public final void sendCacheUpdateCallback(BiConsumer<INetworkScoreCache, Object> biConsumer, Collection<RemoteCallbackList<INetworkScoreCache>> collection) {
        for (RemoteCallbackList<INetworkScoreCache> remoteCallbackList : collection) {
            synchronized (remoteCallbackList) {
                int beginBroadcast = remoteCallbackList.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    biConsumer.accept(remoteCallbackList.getBroadcastItem(i), remoteCallbackList.getBroadcastCookie(i));
                }
                remoteCallbackList.finishBroadcast();
            }
        }
    }

    public final INetworkRecommendationProvider getRecommendationProvider() {
        synchronized (this.mServiceConnectionLock) {
            ScoringServiceConnection scoringServiceConnection = this.mServiceConnection;
            if (scoringServiceConnection != null) {
                return scoringServiceConnection.getRecommendationProvider();
            }
            return null;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ScoringServiceConnection implements ServiceConnection {
        public final NetworkScorerAppData mAppData;
        public volatile boolean mBound = false;
        public volatile boolean mConnected = false;
        public volatile INetworkRecommendationProvider mRecommendationProvider;

        public ScoringServiceConnection(NetworkScorerAppData networkScorerAppData) {
            this.mAppData = networkScorerAppData;
        }

        @VisibleForTesting
        public void bind(Context context) {
            if (this.mBound) {
                return;
            }
            Intent intent = new Intent("android.net.action.RECOMMEND_NETWORKS");
            intent.setComponent(this.mAppData.getRecommendationServiceComponent());
            this.mBound = context.bindServiceAsUser(intent, this, 67108865, UserHandle.SYSTEM);
            if (!this.mBound) {
                Log.w("NetworkScoreService", "Bind call failed for " + intent);
                context.unbindService(this);
            } else if (NetworkScoreService.DBG) {
                Log.d("NetworkScoreService", "ScoringServiceConnection bound.");
            }
        }

        @VisibleForTesting
        public void unbind(Context context) {
            try {
                if (this.mBound) {
                    this.mBound = false;
                    context.unbindService(this);
                    if (NetworkScoreService.DBG) {
                        Log.d("NetworkScoreService", "ScoringServiceConnection unbound.");
                    }
                }
            } catch (RuntimeException e) {
                Log.e("NetworkScoreService", "Unbind failed.", e);
            }
            this.mConnected = false;
            this.mRecommendationProvider = null;
        }

        @VisibleForTesting
        public NetworkScorerAppData getAppData() {
            return this.mAppData;
        }

        @VisibleForTesting
        public INetworkRecommendationProvider getRecommendationProvider() {
            return this.mRecommendationProvider;
        }

        @VisibleForTesting
        public String getPackageName() {
            return this.mAppData.getRecommendationServiceComponent().getPackageName();
        }

        @VisibleForTesting
        public boolean isAlive() {
            return this.mBound && this.mConnected;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (NetworkScoreService.DBG) {
                Log.d("NetworkScoreService", "ScoringServiceConnection: " + componentName.flattenToString());
            }
            this.mConnected = true;
            this.mRecommendationProvider = INetworkRecommendationProvider.Stub.asInterface(iBinder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (NetworkScoreService.DBG) {
                Log.d("NetworkScoreService", "ScoringServiceConnection, disconnected: " + componentName.flattenToString());
            }
            this.mConnected = false;
            this.mRecommendationProvider = null;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.println("ScoringServiceConnection: " + this.mAppData.getRecommendationServiceComponent() + ", bound: " + this.mBound + ", connected: " + this.mConnected);
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1 || i == 2) {
                NetworkScoreService.this.refreshBinding();
                return;
            }
            Log.w("NetworkScoreService", "Unknown message: " + i);
        }
    }
}
