package com.android.server.location.contexthub;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.SensorPrivacyManagerInternal;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubMessage;
import android.hardware.location.ContextHubTransaction;
import android.hardware.location.IContextHubCallback;
import android.hardware.location.IContextHubClient;
import android.hardware.location.IContextHubClientCallback;
import android.hardware.location.IContextHubService;
import android.hardware.location.IContextHubTransactionCallback;
import android.hardware.location.NanoApp;
import android.hardware.location.NanoAppBinary;
import android.hardware.location.NanoAppFilter;
import android.hardware.location.NanoAppInstanceInfo;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppState;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.location.contexthub.IContextHubWrapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class ContextHubService extends IContextHubService.Stub {
    public ContextHubClientManager mClientManager;
    public final Context mContext;
    public Map<Integer, ContextHubInfo> mContextHubIdToInfoMap;
    public List<ContextHubInfo> mContextHubInfoList;
    public final IContextHubWrapper mContextHubWrapper;
    public Map<Integer, IContextHubClient> mDefaultClientMap;
    public SensorPrivacyManagerInternal mSensorPrivacyManagerInternal;
    public List<String> mSupportedContextHubPerms;
    public ContextHubTransactionManager mTransactionManager;
    public final RemoteCallbackList<IContextHubCallback> mCallbacksList = new RemoteCallbackList<>();
    public final NanoAppStateManager mNanoAppStateManager = new NanoAppStateManager();
    public final ScheduledThreadPoolExecutor mDailyMetricTimer = new ScheduledThreadPoolExecutor(1);
    public boolean mIsWifiAvailable = false;
    public boolean mIsWifiScanningEnabled = false;
    public boolean mIsWifiMainEnabled = false;
    public boolean mIsBtScanningEnabled = false;
    public boolean mIsBtMainEnabled = false;
    public Set<Integer> mMetricQueryPendingContextHubIds = Collections.newSetFromMap(new ConcurrentHashMap());
    public final Object mSendWifiSettingUpdateLock = new Object();
    public final Map<Integer, AtomicLong> mLastRestartTimestampMap = new HashMap();

    /* loaded from: classes.dex */
    public class ContextHubServiceCallback implements IContextHubWrapper.ICallback {
        public final int mContextHubId;

        public ContextHubServiceCallback(int i) {
            this.mContextHubId = i;
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleTransactionResult(int i, boolean z) {
            ContextHubService.this.handleTransactionResultCallback(this.mContextHubId, i, z);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleContextHubEvent(int i) {
            ContextHubService.this.handleHubEventCallback(this.mContextHubId, i);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleNanoappAbort(long j, int i) {
            ContextHubService.this.handleAppAbortCallback(this.mContextHubId, j, i);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleNanoappInfo(List<NanoAppState> list) {
            ContextHubService.this.handleQueryAppsCallback(this.mContextHubId, list);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleNanoappMessage(short s, NanoAppMessage nanoAppMessage, List<String> list, List<String> list2) {
            ContextHubService.this.handleClientMessageCallback(this.mContextHubId, s, nanoAppMessage, list, list2);
        }

        @Override // com.android.server.location.contexthub.IContextHubWrapper.ICallback
        public void handleServiceRestart() {
            Log.i("ContextHubService", "Starting Context Hub Service restart");
            ContextHubService.this.initExistingCallbacks();
            ContextHubService.this.resetSettings();
            Log.i("ContextHubService", "Finished Context Hub Service restart");
        }
    }

    public ContextHubService(Context context, IContextHubWrapper iContextHubWrapper) {
        Log.i("ContextHubService", "Starting Context Hub Service init");
        this.mContext = context;
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        this.mContextHubWrapper = iContextHubWrapper;
        if (!initContextHubServiceState(elapsedRealtimeNanos)) {
            Log.e("ContextHubService", "Failed to initialize the Context Hub Service");
            return;
        }
        initDefaultClientMap();
        initLocationSettingNotifications();
        initWifiSettingNotifications();
        initAirplaneModeSettingNotifications();
        initMicrophoneSettingNotifications();
        initBtSettingNotifications();
        scheduleDailyMetricSnapshot();
        Log.i("ContextHubService", "Finished Context Hub Service init");
    }

    public final IContextHubClientCallback createDefaultClientCallback(final int i) {
        return new IContextHubClientCallback.Stub() { // from class: com.android.server.location.contexthub.ContextHubService.1
            public final void finishCallback() {
                try {
                    ((IContextHubClient) ContextHubService.this.mDefaultClientMap.get(Integer.valueOf(i))).callbackFinished();
                } catch (RemoteException e) {
                    Log.e("ContextHubService", "RemoteException while finishing callback for hub (ID = " + i + ")", e);
                }
            }

            public void onMessageFromNanoApp(NanoAppMessage nanoAppMessage) {
                ContextHubService.this.onMessageReceiptOldApi(nanoAppMessage.getMessageType(), i, ContextHubService.this.mNanoAppStateManager.getNanoAppHandle(i, nanoAppMessage.getNanoAppId()), nanoAppMessage.getMessageBody());
                finishCallback();
            }

            public void onHubReset() {
                ContextHubService.this.onMessageReceiptOldApi(7, i, -1, new byte[]{0});
                finishCallback();
            }

            public void onNanoAppAborted(long j, int i2) {
                finishCallback();
            }

            public void onNanoAppLoaded(long j) {
                finishCallback();
            }

            public void onNanoAppUnloaded(long j) {
                finishCallback();
            }

            public void onNanoAppEnabled(long j) {
                finishCallback();
            }

            public void onNanoAppDisabled(long j) {
                finishCallback();
            }

            public void onClientAuthorizationChanged(long j, int i2) {
                finishCallback();
            }
        };
    }

    public final boolean initContextHubServiceState(long j) {
        Pair<List<ContextHubInfo>, List<String>> pair;
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null) {
            this.mTransactionManager = null;
            this.mClientManager = null;
            this.mSensorPrivacyManagerInternal = null;
            this.mDefaultClientMap = Collections.emptyMap();
            this.mContextHubIdToInfoMap = Collections.emptyMap();
            this.mSupportedContextHubPerms = Collections.emptyList();
            this.mContextHubInfoList = Collections.emptyList();
            return false;
        }
        try {
            pair = iContextHubWrapper.getHubs();
        } catch (RemoteException e) {
            Log.e("ContextHubService", "RemoteException while getting Context Hub info", e);
            pair = new Pair<>(Collections.emptyList(), Collections.emptyList());
        }
        ContextHubStatsLog.write(398, SystemClock.elapsedRealtimeNanos() - j, ((List) pair.first).size());
        this.mContextHubIdToInfoMap = Collections.unmodifiableMap(ContextHubServiceUtil.createContextHubInfoMap((List) pair.first));
        this.mSupportedContextHubPerms = (List) pair.second;
        this.mContextHubInfoList = new ArrayList(this.mContextHubIdToInfoMap.values());
        ContextHubClientManager contextHubClientManager = new ContextHubClientManager(this.mContext, this.mContextHubWrapper);
        this.mClientManager = contextHubClientManager;
        this.mTransactionManager = new ContextHubTransactionManager(this.mContextHubWrapper, contextHubClientManager, this.mNanoAppStateManager);
        this.mSensorPrivacyManagerInternal = (SensorPrivacyManagerInternal) LocalServices.getService(SensorPrivacyManagerInternal.class);
        return true;
    }

    public final void initDefaultClientMap() {
        HashMap hashMap = new HashMap();
        for (Map.Entry<Integer, ContextHubInfo> entry : this.mContextHubIdToInfoMap.entrySet()) {
            int intValue = entry.getKey().intValue();
            ContextHubInfo value = entry.getValue();
            this.mLastRestartTimestampMap.put(Integer.valueOf(intValue), new AtomicLong(SystemClock.elapsedRealtimeNanos()));
            try {
                this.mContextHubWrapper.registerCallback(intValue, new ContextHubServiceCallback(intValue));
            } catch (RemoteException e) {
                Log.e("ContextHubService", "RemoteException while registering service callback for hub (ID = " + intValue + ")", e);
            }
            hashMap.put(Integer.valueOf(intValue), this.mClientManager.registerClient(value, createDefaultClientCallback(intValue), (String) null, this.mTransactionManager, this.mContext.getPackageName()));
            queryNanoAppsInternal(intValue);
        }
        this.mDefaultClientMap = Collections.unmodifiableMap(hashMap);
    }

    public final void initExistingCallbacks() {
        for (Integer num : this.mContextHubIdToInfoMap.keySet()) {
            int intValue = num.intValue();
            try {
                this.mContextHubWrapper.registerExistingCallback(intValue);
            } catch (RemoteException e) {
                Log.e("ContextHubService", "RemoteException while registering existing service callback for hub (ID = " + intValue + ")", e);
            }
        }
    }

    public final void initLocationSettingNotifications() {
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null || !iContextHubWrapper.supportsLocationSettingNotifications()) {
            return;
        }
        sendLocationSettingUpdate();
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_mode"), true, new ContentObserver(null) { // from class: com.android.server.location.contexthub.ContextHubService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                ContextHubService.this.sendLocationSettingUpdate();
            }
        }, -1);
    }

    public final void initWifiSettingNotifications() {
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null || !iContextHubWrapper.supportsWifiSettingNotifications()) {
            return;
        }
        sendWifiSettingUpdate(true);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.location.contexthub.ContextHubService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intent.getAction()) || "android.net.wifi.action.WIFI_SCAN_AVAILABILITY_CHANGED".equals(intent.getAction())) {
                    ContextHubService.this.sendWifiSettingUpdate(false);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.action.WIFI_SCAN_AVAILABILITY_CHANGED");
        this.mContext.registerReceiver(broadcastReceiver, intentFilter);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("wifi_scan_always_enabled"), true, new ContentObserver(null) { // from class: com.android.server.location.contexthub.ContextHubService.4
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                ContextHubService.this.sendWifiSettingUpdate(false);
            }
        }, -1);
    }

    public final void initAirplaneModeSettingNotifications() {
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null || !iContextHubWrapper.supportsAirplaneModeSettingNotifications()) {
            return;
        }
        sendAirplaneModeSettingUpdate();
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, new ContentObserver(null) { // from class: com.android.server.location.contexthub.ContextHubService.5
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                ContextHubService.this.sendAirplaneModeSettingUpdate();
            }
        }, -1);
    }

    public final void initMicrophoneSettingNotifications() {
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null || !iContextHubWrapper.supportsMicrophoneSettingNotifications()) {
            return;
        }
        sendMicrophoneDisableSettingUpdateForCurrentUser();
        SensorPrivacyManagerInternal sensorPrivacyManagerInternal = this.mSensorPrivacyManagerInternal;
        if (sensorPrivacyManagerInternal == null) {
            Log.e("ContextHubService", "Unable to add a sensor privacy listener for all users");
        } else {
            sensorPrivacyManagerInternal.addSensorPrivacyListenerForAllUsers(1, new SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda4
                public final void onSensorPrivacyChanged(int i, boolean z) {
                    ContextHubService.this.lambda$initMicrophoneSettingNotifications$0(i, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initMicrophoneSettingNotifications$0(int i, boolean z) {
        if (i == getCurrentUserId()) {
            Log.d("ContextHubService", "User: " + i + "mic privacy: " + z);
            sendMicrophoneDisableSettingUpdate(z);
        }
    }

    public final void initBtSettingNotifications() {
        IContextHubWrapper iContextHubWrapper = this.mContextHubWrapper;
        if (iContextHubWrapper == null || !iContextHubWrapper.supportsBtSettingNotifications()) {
            return;
        }
        sendBtSettingUpdate(true);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.location.contexthub.ContextHubService.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction()) || "android.bluetooth.adapter.action.BLE_STATE_CHANGED".equals(intent.getAction())) {
                    ContextHubService.this.sendBtSettingUpdate(false);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.BLE_STATE_CHANGED");
        this.mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    public final void resetSettings() {
        sendLocationSettingUpdate();
        sendWifiSettingUpdate(true);
        sendAirplaneModeSettingUpdate();
        sendMicrophoneDisableSettingUpdateForCurrentUser();
        sendBtSettingUpdate(true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new ContextHubShellCommand(this.mContext, this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int registerCallback(IContextHubCallback iContextHubCallback) throws RemoteException {
        super.registerCallback_enforcePermission();
        this.mCallbacksList.register(iContextHubCallback);
        Log.d("ContextHubService", "Added callback, total callbacks " + this.mCallbacksList.getRegisteredCallbackCount());
        return 0;
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int[] getContextHubHandles() throws RemoteException {
        super.getContextHubHandles_enforcePermission();
        return ContextHubServiceUtil.createPrimitiveIntArray(this.mContextHubIdToInfoMap.keySet());
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public ContextHubInfo getContextHubInfo(int i) throws RemoteException {
        super.getContextHubInfo_enforcePermission();
        if (!this.mContextHubIdToInfoMap.containsKey(Integer.valueOf(i))) {
            Log.e("ContextHubService", "Invalid Context Hub handle " + i + " in getContextHubInfo");
            return null;
        }
        return this.mContextHubIdToInfoMap.get(Integer.valueOf(i));
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public List<ContextHubInfo> getContextHubs() throws RemoteException {
        super.getContextHubs_enforcePermission();
        return this.mContextHubInfoList;
    }

    public final IContextHubTransactionCallback createLoadTransactionCallback(final int i, final NanoAppBinary nanoAppBinary) {
        return new IContextHubTransactionCallback.Stub() { // from class: com.android.server.location.contexthub.ContextHubService.7
            public void onQueryResponse(int i2, List<NanoAppState> list) {
            }

            public void onTransactionComplete(int i2) {
                ContextHubService.this.handleLoadResponseOldApi(i, i2, nanoAppBinary);
            }
        };
    }

    public final IContextHubTransactionCallback createUnloadTransactionCallback(final int i) {
        return new IContextHubTransactionCallback.Stub() { // from class: com.android.server.location.contexthub.ContextHubService.8
            public void onQueryResponse(int i2, List<NanoAppState> list) {
            }

            public void onTransactionComplete(int i2) {
                ContextHubService.this.handleUnloadResponseOldApi(i, i2);
            }
        };
    }

    public final IContextHubTransactionCallback createQueryTransactionCallback(final int i) {
        return new IContextHubTransactionCallback.Stub() { // from class: com.android.server.location.contexthub.ContextHubService.9
            public void onTransactionComplete(int i2) {
            }

            public void onQueryResponse(int i2, List<NanoAppState> list) {
                ContextHubService.this.onMessageReceiptOldApi(5, i, -1, new byte[]{(byte) i2});
            }
        };
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int loadNanoApp(int i, NanoApp nanoApp) throws RemoteException {
        super.loadNanoApp_enforcePermission();
        if (this.mContextHubWrapper == null) {
            return -1;
        }
        if (!isValidContextHubId(i)) {
            Log.e("ContextHubService", "Invalid Context Hub handle " + i + " in loadNanoApp");
            return -1;
        } else if (nanoApp == null) {
            Log.e("ContextHubService", "NanoApp cannot be null in loadNanoApp");
            return -1;
        } else {
            NanoAppBinary nanoAppBinary = new NanoAppBinary(nanoApp.getAppBinary());
            this.mTransactionManager.addTransaction(this.mTransactionManager.createLoadTransaction(i, nanoAppBinary, createLoadTransactionCallback(i, nanoAppBinary), getCallingPackageName()));
            return 0;
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int unloadNanoApp(int i) throws RemoteException {
        super.unloadNanoApp_enforcePermission();
        if (this.mContextHubWrapper == null) {
            return -1;
        }
        NanoAppInstanceInfo nanoAppInstanceInfo = this.mNanoAppStateManager.getNanoAppInstanceInfo(i);
        if (nanoAppInstanceInfo == null) {
            Log.e("ContextHubService", "Invalid nanoapp handle " + i + " in unloadNanoApp");
            return -1;
        }
        int contexthubId = nanoAppInstanceInfo.getContexthubId();
        this.mTransactionManager.addTransaction(this.mTransactionManager.createUnloadTransaction(contexthubId, nanoAppInstanceInfo.getAppId(), createUnloadTransactionCallback(contexthubId), getCallingPackageName()));
        return 0;
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public NanoAppInstanceInfo getNanoAppInstanceInfo(int i) throws RemoteException {
        super.getNanoAppInstanceInfo_enforcePermission();
        return this.mNanoAppStateManager.getNanoAppInstanceInfo(i);
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int[] findNanoAppOnHub(int i, final NanoAppFilter nanoAppFilter) throws RemoteException {
        super.findNanoAppOnHub_enforcePermission();
        final ArrayList arrayList = new ArrayList();
        if (nanoAppFilter != null) {
            this.mNanoAppStateManager.foreachNanoAppInstanceInfo(new Consumer() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ContextHubService.lambda$findNanoAppOnHub$1(nanoAppFilter, arrayList, (NanoAppInstanceInfo) obj);
                }
            });
        }
        int[] iArr = new int[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
        }
        return iArr;
    }

    public static /* synthetic */ void lambda$findNanoAppOnHub$1(NanoAppFilter nanoAppFilter, ArrayList arrayList, NanoAppInstanceInfo nanoAppInstanceInfo) {
        if (nanoAppFilter.testMatch(nanoAppInstanceInfo)) {
            arrayList.add(Integer.valueOf(nanoAppInstanceInfo.getHandle()));
        }
    }

    public final boolean queryNanoAppsInternal(int i) {
        if (this.mContextHubWrapper == null) {
            return false;
        }
        this.mTransactionManager.addTransaction(this.mTransactionManager.createQueryTransaction(i, createQueryTransactionCallback(i), getCallingPackageName()));
        return true;
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public int sendMessage(int i, int i2, ContextHubMessage contextHubMessage) throws RemoteException {
        boolean z;
        super.sendMessage_enforcePermission();
        if (this.mContextHubWrapper == null) {
            return -1;
        }
        if (contextHubMessage == null) {
            Log.e("ContextHubService", "ContextHubMessage cannot be null in sendMessage");
            return -1;
        } else if (contextHubMessage.getData() == null) {
            Log.e("ContextHubService", "ContextHubMessage message body cannot be null in sendMessage");
            return -1;
        } else if (!isValidContextHubId(i)) {
            Log.e("ContextHubService", "Invalid Context Hub handle " + i + " in sendMessage");
            return -1;
        } else {
            if (i2 == -1) {
                if (contextHubMessage.getMsgType() == 5) {
                    z = queryNanoAppsInternal(i);
                } else {
                    Log.e("ContextHubService", "Invalid OS message params of type " + contextHubMessage.getMsgType());
                    z = false;
                }
            } else {
                NanoAppInstanceInfo nanoAppInstanceInfo = getNanoAppInstanceInfo(i2);
                if (nanoAppInstanceInfo != null) {
                    if (this.mDefaultClientMap.get(Integer.valueOf(i)).sendMessageToNanoApp(NanoAppMessage.createMessageToNanoApp(nanoAppInstanceInfo.getAppId(), contextHubMessage.getMsgType(), contextHubMessage.getData())) == 0) {
                        z = true;
                    }
                } else {
                    Log.e("ContextHubService", "Failed to send nanoapp message - nanoapp with handle " + i2 + " does not exist.");
                }
                z = false;
            }
            return z ? 0 : -1;
        }
    }

    public final void handleClientMessageCallback(int i, short s, NanoAppMessage nanoAppMessage, List<String> list, List<String> list2) {
        this.mClientManager.onMessageFromNanoApp(i, s, nanoAppMessage, list, list2);
    }

    public final void handleLoadResponseOldApi(int i, int i2, NanoAppBinary nanoAppBinary) {
        if (nanoAppBinary == null) {
            Log.e("ContextHubService", "Nanoapp binary field was null for a load transaction");
            return;
        }
        byte[] bArr = new byte[5];
        bArr[0] = (byte) i2;
        ByteBuffer.wrap(bArr, 1, 4).order(ByteOrder.nativeOrder()).putInt(this.mNanoAppStateManager.getNanoAppHandle(i, nanoAppBinary.getNanoAppId()));
        onMessageReceiptOldApi(3, i, -1, bArr);
    }

    public final void handleUnloadResponseOldApi(int i, int i2) {
        onMessageReceiptOldApi(4, i, -1, new byte[]{(byte) i2});
    }

    public final void handleTransactionResultCallback(int i, int i2, boolean z) {
        this.mTransactionManager.onTransactionResponse(i2, z);
    }

    public final void handleHubEventCallback(int i, int i2) {
        if (i2 == 1) {
            long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
            ContextHubStatsLog.write(399, TimeUnit.NANOSECONDS.toMillis(elapsedRealtimeNanos - this.mLastRestartTimestampMap.get(Integer.valueOf(i)).getAndSet(elapsedRealtimeNanos)), i);
            ContextHubEventLogger.getInstance().logContextHubRestart(i);
            resetSettings();
            this.mTransactionManager.onHubReset();
            queryNanoAppsInternal(i);
            this.mClientManager.onHubReset(i);
            return;
        }
        Log.i("ContextHubService", "Received unknown hub event (hub ID = " + i + ", type = " + i2 + ")");
    }

    public final void handleAppAbortCallback(int i, long j, int i2) {
        this.mClientManager.onNanoAppAborted(i, j, i2);
    }

    public final void handleQueryAppsCallback(int i, List<NanoAppState> list) {
        if (this.mMetricQueryPendingContextHubIds.contains(Integer.valueOf(i))) {
            for (NanoAppState nanoAppState : list) {
                ContextHubStatsLog.write(FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, i, nanoAppState.getNanoAppId(), (int) nanoAppState.getNanoAppVersion());
            }
            this.mMetricQueryPendingContextHubIds.remove(Integer.valueOf(i));
            if (this.mMetricQueryPendingContextHubIds.isEmpty()) {
                scheduleDailyMetricSnapshot();
            }
        }
        this.mNanoAppStateManager.updateCache(i, list);
        this.mTransactionManager.onQueryResponse(list);
    }

    public final boolean isValidContextHubId(int i) {
        return this.mContextHubIdToInfoMap.containsKey(Integer.valueOf(i));
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public IContextHubClient createClient(int i, IContextHubClientCallback iContextHubClientCallback, String str, String str2) throws RemoteException {
        super.createClient_enforcePermission();
        if (isValidContextHubId(i)) {
            if (iContextHubClientCallback == null) {
                throw new NullPointerException("Cannot register client with null callback");
            }
            return this.mClientManager.registerClient(this.mContextHubIdToInfoMap.get(Integer.valueOf(i)), iContextHubClientCallback, str, this.mTransactionManager, str2);
        }
        throw new IllegalArgumentException("Invalid context hub ID " + i);
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public IContextHubClient createPendingIntentClient(int i, PendingIntent pendingIntent, long j, String str) throws RemoteException {
        super.createPendingIntentClient_enforcePermission();
        if (!isValidContextHubId(i)) {
            throw new IllegalArgumentException("Invalid context hub ID " + i);
        }
        return this.mClientManager.registerClient(this.mContextHubIdToInfoMap.get(Integer.valueOf(i)), pendingIntent, j, str, this.mTransactionManager);
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public void loadNanoAppOnHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, NanoAppBinary nanoAppBinary) throws RemoteException {
        super.loadNanoAppOnHub_enforcePermission();
        if (checkHalProxyAndContextHubId(i, iContextHubTransactionCallback, 0)) {
            if (nanoAppBinary == null) {
                Log.e("ContextHubService", "NanoAppBinary cannot be null in loadNanoAppOnHub");
                iContextHubTransactionCallback.onTransactionComplete(2);
                return;
            }
            this.mTransactionManager.addTransaction(this.mTransactionManager.createLoadTransaction(i, nanoAppBinary, iContextHubTransactionCallback, getCallingPackageName()));
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public void unloadNanoAppFromHub(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException {
        super.unloadNanoAppFromHub_enforcePermission();
        if (checkHalProxyAndContextHubId(i, iContextHubTransactionCallback, 1)) {
            this.mTransactionManager.addTransaction(this.mTransactionManager.createUnloadTransaction(i, j, iContextHubTransactionCallback, getCallingPackageName()));
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public void enableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException {
        super.enableNanoApp_enforcePermission();
        if (checkHalProxyAndContextHubId(i, iContextHubTransactionCallback, 2)) {
            this.mTransactionManager.addTransaction(this.mTransactionManager.createEnableTransaction(i, j, iContextHubTransactionCallback, getCallingPackageName()));
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public void disableNanoApp(int i, IContextHubTransactionCallback iContextHubTransactionCallback, long j) throws RemoteException {
        super.disableNanoApp_enforcePermission();
        if (checkHalProxyAndContextHubId(i, iContextHubTransactionCallback, 3)) {
            this.mTransactionManager.addTransaction(this.mTransactionManager.createDisableTransaction(i, j, iContextHubTransactionCallback, getCallingPackageName()));
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public void queryNanoApps(int i, IContextHubTransactionCallback iContextHubTransactionCallback) throws RemoteException {
        super.queryNanoApps_enforcePermission();
        if (checkHalProxyAndContextHubId(i, iContextHubTransactionCallback, 4)) {
            this.mTransactionManager.addTransaction(this.mTransactionManager.createQueryTransaction(i, iContextHubTransactionCallback, getCallingPackageName()));
        }
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public long[] getPreloadedNanoAppIds(ContextHubInfo contextHubInfo) throws RemoteException {
        super.getPreloadedNanoAppIds_enforcePermission();
        Objects.requireNonNull(contextHubInfo, "hubInfo cannot be null");
        long[] preloadedNanoappIds = this.mContextHubWrapper.getPreloadedNanoappIds(contextHubInfo.getId());
        return preloadedNanoappIds == null ? new long[0] : preloadedNanoappIds;
    }

    @EnforcePermission("android.permission.ACCESS_CONTEXT_HUB")
    public boolean setTestMode(boolean z) {
        super.setTestMode_enforcePermission();
        boolean testMode = this.mContextHubWrapper.setTestMode(z);
        for (Integer num : this.mDefaultClientMap.keySet()) {
            queryNanoAppsInternal(num.intValue());
        }
        return testMode;
    }

    public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "ContextHubService", printWriter)) {
            for (String str : strArr) {
                if ("--proto".equals(str)) {
                    dump(new ProtoOutputStream(fileDescriptor));
                    return;
                }
            }
            printWriter.println("Dumping ContextHub Service");
            printWriter.println("");
            printWriter.println("=================== CONTEXT HUBS ====================");
            for (ContextHubInfo contextHubInfo : this.mContextHubIdToInfoMap.values()) {
                printWriter.println(contextHubInfo);
            }
            printWriter.println("Supported permissions: " + Arrays.toString(this.mSupportedContextHubPerms.toArray()));
            printWriter.println("");
            printWriter.println("=================== NANOAPPS ====================");
            this.mNanoAppStateManager.foreachNanoAppInstanceInfo(new Consumer() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    printWriter.println((NanoAppInstanceInfo) obj);
                }
            });
            printWriter.println("");
            printWriter.println("=================== PRELOADED NANOAPPS ====================");
            dumpPreloadedNanoapps(printWriter);
            printWriter.println("");
            printWriter.println("=================== CLIENTS ====================");
            printWriter.println(this.mClientManager);
            printWriter.println("");
            printWriter.println("=================== TRANSACTIONS ====================");
            printWriter.println(this.mTransactionManager);
            printWriter.println("");
            printWriter.println("=================== EVENTS ====================");
            printWriter.println(ContextHubEventLogger.getInstance());
        }
    }

    public void denyClientAuthState(int i, final String str, final long j) {
        Log.i("ContextHubService", "Denying " + str + " access to " + Long.toHexString(j) + " on context hub # " + i);
        this.mClientManager.forEachClientOfHub(i, new Consumer() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ContextHubService.lambda$denyClientAuthState$2(str, j, (ContextHubClientBroker) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$denyClientAuthState$2(String str, long j, ContextHubClientBroker contextHubClientBroker) {
        if (contextHubClientBroker.getPackageName().equals(str)) {
            contextHubClientBroker.updateNanoAppAuthState(j, Collections.emptyList(), false, true);
        }
    }

    public final void dump(final ProtoOutputStream protoOutputStream) {
        this.mContextHubIdToInfoMap.values().forEach(new Consumer() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ContextHubService.lambda$dump$3(protoOutputStream, (ContextHubInfo) obj);
            }
        });
        long start = protoOutputStream.start(1146756268034L);
        this.mClientManager.dump(protoOutputStream);
        protoOutputStream.end(start);
        protoOutputStream.flush();
    }

    public static /* synthetic */ void lambda$dump$3(ProtoOutputStream protoOutputStream, ContextHubInfo contextHubInfo) {
        long start = protoOutputStream.start(2246267895809L);
        contextHubInfo.dump(protoOutputStream);
        protoOutputStream.end(start);
    }

    public final void dumpPreloadedNanoapps(PrintWriter printWriter) {
        int intValue;
        long[] preloadedNanoappIds;
        if (this.mContextHubWrapper == null) {
            return;
        }
        Iterator<Integer> it = this.mContextHubIdToInfoMap.keySet().iterator();
        while (it.hasNext() && (preloadedNanoappIds = this.mContextHubWrapper.getPreloadedNanoappIds((intValue = it.next().intValue()))) != null) {
            printWriter.print("Context Hub (id=");
            printWriter.print(intValue);
            printWriter.println("):");
            for (long j : preloadedNanoappIds) {
                printWriter.print("  ID: 0x");
                printWriter.println(Long.toHexString(j));
            }
        }
    }

    public final int onMessageReceiptOldApi(int i, int i2, int i3, byte[] bArr) {
        if (bArr == null) {
            return -1;
        }
        synchronized (this.mCallbacksList) {
            int beginBroadcast = this.mCallbacksList.beginBroadcast();
            if (beginBroadcast < 1) {
                return 0;
            }
            ContextHubMessage contextHubMessage = new ContextHubMessage(i, 0, bArr);
            for (int i4 = 0; i4 < beginBroadcast; i4++) {
                IContextHubCallback broadcastItem = this.mCallbacksList.getBroadcastItem(i4);
                try {
                    broadcastItem.onMessageReceipt(i2, i3, contextHubMessage);
                } catch (RemoteException e) {
                    Log.i("ContextHubService", "Exception (" + e + ") calling remote callback (" + broadcastItem + ").");
                }
            }
            this.mCallbacksList.finishBroadcast();
            return 0;
        }
    }

    public final boolean checkHalProxyAndContextHubId(int i, IContextHubTransactionCallback iContextHubTransactionCallback, int i2) {
        if (this.mContextHubWrapper == null) {
            try {
                iContextHubTransactionCallback.onTransactionComplete(8);
            } catch (RemoteException e) {
                Log.e("ContextHubService", "RemoteException while calling onTransactionComplete", e);
            }
            return false;
        } else if (isValidContextHubId(i)) {
            return true;
        } else {
            Log.e("ContextHubService", "Cannot start " + ContextHubTransaction.typeToString(i2, false) + " transaction for invalid hub ID " + i);
            try {
                iContextHubTransactionCallback.onTransactionComplete(2);
            } catch (RemoteException e2) {
                Log.e("ContextHubService", "RemoteException while calling onTransactionComplete", e2);
            }
            return false;
        }
    }

    public final void sendLocationSettingUpdate() {
        this.mContextHubWrapper.onLocationSettingChanged(((LocationManager) this.mContext.getSystemService(LocationManager.class)).isLocationEnabledForUser(UserHandle.CURRENT));
    }

    public final void sendWifiSettingUpdate(boolean z) {
        boolean z2;
        synchronized (this.mSendWifiSettingUpdateLock) {
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
            boolean isWifiEnabled = wifiManager.isWifiEnabled();
            boolean isScanAlwaysAvailable = wifiManager.isScanAlwaysAvailable();
            if (!isWifiEnabled && !isScanAlwaysAvailable) {
                z2 = false;
                if (!z || this.mIsWifiAvailable != z2) {
                    this.mIsWifiAvailable = z2;
                    this.mContextHubWrapper.onWifiSettingChanged(z2);
                }
                if (!z || this.mIsWifiScanningEnabled != isScanAlwaysAvailable) {
                    this.mIsWifiScanningEnabled = isScanAlwaysAvailable;
                    this.mContextHubWrapper.onWifiScanningSettingChanged(isScanAlwaysAvailable);
                }
                if (!z || this.mIsWifiMainEnabled != isWifiEnabled) {
                    this.mIsWifiMainEnabled = isWifiEnabled;
                    this.mContextHubWrapper.onWifiMainSettingChanged(isWifiEnabled);
                }
            }
            z2 = true;
            if (!z) {
            }
            this.mIsWifiAvailable = z2;
            this.mContextHubWrapper.onWifiSettingChanged(z2);
            if (!z) {
            }
            this.mIsWifiScanningEnabled = isScanAlwaysAvailable;
            this.mContextHubWrapper.onWifiScanningSettingChanged(isScanAlwaysAvailable);
            if (!z) {
            }
            this.mIsWifiMainEnabled = isWifiEnabled;
            this.mContextHubWrapper.onWifiMainSettingChanged(isWifiEnabled);
        }
    }

    public final void sendBtSettingUpdate(boolean z) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            boolean isEnabled = defaultAdapter.isEnabled();
            boolean isBleScanAlwaysAvailable = defaultAdapter.isBleScanAlwaysAvailable();
            if (z || this.mIsBtScanningEnabled != isBleScanAlwaysAvailable) {
                this.mIsBtScanningEnabled = isBleScanAlwaysAvailable;
                this.mContextHubWrapper.onBtScanningSettingChanged(isBleScanAlwaysAvailable);
            }
            if (z || this.mIsBtMainEnabled != isEnabled) {
                this.mIsBtMainEnabled = isEnabled;
                this.mContextHubWrapper.onBtMainSettingChanged(isEnabled);
                return;
            }
            return;
        }
        Log.d("ContextHubService", "BT adapter not available. Defaulting to disabled");
        if (z || this.mIsBtMainEnabled) {
            this.mIsBtMainEnabled = false;
            this.mContextHubWrapper.onBtMainSettingChanged(false);
        }
        if (z || this.mIsBtScanningEnabled) {
            this.mIsBtScanningEnabled = false;
            this.mContextHubWrapper.onBtScanningSettingChanged(false);
        }
    }

    public final void sendAirplaneModeSettingUpdate() {
        this.mContextHubWrapper.onAirplaneModeSettingChanged(Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1);
    }

    public final void sendMicrophoneDisableSettingUpdate(boolean z) {
        Log.d("ContextHubService", "Mic Disabled Setting: " + z);
        this.mContextHubWrapper.onMicrophoneSettingChanged(z ^ true);
    }

    public final void sendMicrophoneDisableSettingUpdateForCurrentUser() {
        SensorPrivacyManagerInternal sensorPrivacyManagerInternal = this.mSensorPrivacyManagerInternal;
        sendMicrophoneDisableSettingUpdate(sensorPrivacyManagerInternal == null ? false : sensorPrivacyManagerInternal.isSensorPrivacyEnabled(getCurrentUserId(), 1));
    }

    public final void scheduleDailyMetricSnapshot() {
        try {
            this.mDailyMetricTimer.schedule(new Runnable() { // from class: com.android.server.location.contexthub.ContextHubService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubService.this.lambda$scheduleDailyMetricSnapshot$4();
                }
            }, 1L, TimeUnit.DAYS);
        } catch (Exception e) {
            Log.e("ContextHubService", "Error when schedule a timer", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleDailyMetricSnapshot$4() {
        for (Integer num : this.mContextHubIdToInfoMap.keySet()) {
            int intValue = num.intValue();
            this.mMetricQueryPendingContextHubIds.add(Integer.valueOf(intValue));
            queryNanoAppsInternal(intValue);
        }
    }

    public final String getCallingPackageName() {
        return this.mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
    }

    public final int getCurrentUserId() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int i = ActivityManager.getService().getCurrentUser().id;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return i;
        } catch (RemoteException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void onUserChanged() {
        Log.d("ContextHubService", "User changed to id: " + getCurrentUserId());
        sendLocationSettingUpdate();
        sendMicrophoneDisableSettingUpdateForCurrentUser();
    }
}
