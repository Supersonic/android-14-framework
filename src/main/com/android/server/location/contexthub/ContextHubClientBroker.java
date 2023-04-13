package com.android.server.location.contexthub;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.compat.Compatibility;
import android.content.Context;
import android.content.Intent;
import android.hardware.contexthub.HostEndpointInfo;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.IContextHubClient;
import android.hardware.location.IContextHubClientCallback;
import android.hardware.location.IContextHubTransactionCallback;
import android.hardware.location.NanoAppMessage;
import android.hardware.location.NanoAppState;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FunctionalUtils;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ContextHubClientBroker extends IContextHubClient.Stub implements IBinder.DeathRecipient, AppOpsManager.OnOpChangedListener, PendingIntent.OnFinished {
    public final AppOpsManager mAppOpsManager;
    public final ContextHubInfo mAttachedContextHubInfo;
    public String mAttributionTag;
    public final ContextHubClientManager mClientManager;
    public final Context mContext;
    public IContextHubClientCallback mContextHubClientCallback;
    public final IContextHubWrapper mContextHubProxy;
    public final Set<Long> mForceDeniedNapps;
    public final short mHostEndPointId;
    public final AtomicBoolean mIsPendingIntentCancelled;
    public final AtomicBoolean mIsPermQueryIssued;
    @GuardedBy({"mWakeLock"})
    public boolean mIsWakelockUsable;
    public final Map<Long, Integer> mMessageChannelNanoappIdMap;
    public final Map<Long, AuthStateDenialTimer> mNappToAuthTimerMap;
    public final String mPackage;
    public final PendingIntentRequest mPendingIntentRequest;
    public final int mPid;
    public final IContextHubTransactionCallback mQueryPermsCallback;
    public boolean mRegistered;
    public final ContextHubTransactionManager mTransactionManager;
    public final int mUid;
    @GuardedBy({"mWakeLock"})
    public final PowerManager.WakeLock mWakeLock;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface CallbackConsumer {
        void accept(IContextHubClientCallback iContextHubClientCallback) throws RemoteException;
    }

    public final String authStateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "UNKNOWN" : "GRANTED" : "DENIED_GRACE_PERIOD" : "DENIED";
    }

    /* loaded from: classes.dex */
    public static class PendingIntentRequest {
        public long mNanoAppId;
        public PendingIntent mPendingIntent;
        public boolean mValid = false;

        public PendingIntentRequest() {
        }

        public PendingIntentRequest(PendingIntent pendingIntent, long j) {
            this.mPendingIntent = pendingIntent;
            this.mNanoAppId = j;
        }

        public long getNanoAppId() {
            return this.mNanoAppId;
        }

        public PendingIntent getPendingIntent() {
            return this.mPendingIntent;
        }

        public boolean hasPendingIntent() {
            return this.mPendingIntent != null;
        }

        public void clear() {
            this.mPendingIntent = null;
        }

        public boolean isValid() {
            return this.mValid;
        }
    }

    public ContextHubClientBroker(Context context, IContextHubWrapper iContextHubWrapper, ContextHubClientManager contextHubClientManager, ContextHubInfo contextHubInfo, short s, IContextHubClientCallback iContextHubClientCallback, String str, ContextHubTransactionManager contextHubTransactionManager, PendingIntent pendingIntent, long j, String str2) {
        this.mRegistered = true;
        this.mIsWakelockUsable = true;
        this.mIsPendingIntentCancelled = new AtomicBoolean(false);
        this.mIsPermQueryIssued = new AtomicBoolean(false);
        this.mMessageChannelNanoappIdMap = new ConcurrentHashMap();
        this.mForceDeniedNapps = new HashSet();
        this.mNappToAuthTimerMap = new ConcurrentHashMap();
        this.mQueryPermsCallback = new IContextHubTransactionCallback.Stub() { // from class: com.android.server.location.contexthub.ContextHubClientBroker.1
            public void onTransactionComplete(int i) {
            }

            public void onQueryResponse(int i, List<NanoAppState> list) {
                ContextHubClientBroker.this.mIsPermQueryIssued.set(false);
                if (i != 0 && list != null) {
                    Log.e("ContextHubClientBroker", "Permissions query failed, but still received nanoapp state");
                } else if (list != null) {
                    for (NanoAppState nanoAppState : list) {
                        if (ContextHubClientBroker.this.mMessageChannelNanoappIdMap.containsKey(Long.valueOf(nanoAppState.getNanoAppId()))) {
                            ContextHubClientBroker.this.updateNanoAppAuthState(nanoAppState.getNanoAppId(), nanoAppState.getNanoAppPermissions(), false);
                        }
                    }
                }
            }
        };
        this.mContext = context;
        this.mContextHubProxy = iContextHubWrapper;
        this.mClientManager = contextHubClientManager;
        this.mAttachedContextHubInfo = contextHubInfo;
        this.mHostEndPointId = s;
        this.mContextHubClientCallback = iContextHubClientCallback;
        if (pendingIntent == null) {
            this.mPendingIntentRequest = new PendingIntentRequest();
        } else {
            this.mPendingIntentRequest = new PendingIntentRequest(pendingIntent, j);
        }
        if (str2 == null) {
            String[] packagesForUid = context.getPackageManager().getPackagesForUid(Binder.getCallingUid());
            if (packagesForUid != null && packagesForUid.length > 0) {
                str2 = packagesForUid[0];
            }
            Log.e("ContextHubClientBroker", "createClient: Provided package name null. Using first package name " + str2);
        }
        this.mPackage = str2;
        this.mAttributionTag = str;
        this.mTransactionManager = contextHubTransactionManager;
        this.mPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        this.mUid = callingUid;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService(PowerManager.class)).newWakeLock(1, "ContextHubClientBroker");
        this.mWakeLock = newWakeLock;
        newWakeLock.setWorkSource(new WorkSource(callingUid, str2));
        newWakeLock.setReferenceCounted(true);
        startMonitoringOpChanges();
        sendHostEndpointConnectedEvent();
    }

    public ContextHubClientBroker(Context context, IContextHubWrapper iContextHubWrapper, ContextHubClientManager contextHubClientManager, ContextHubInfo contextHubInfo, short s, IContextHubClientCallback iContextHubClientCallback, String str, ContextHubTransactionManager contextHubTransactionManager, String str2) {
        this(context, iContextHubWrapper, contextHubClientManager, contextHubInfo, s, iContextHubClientCallback, str, contextHubTransactionManager, null, 0L, str2);
    }

    public ContextHubClientBroker(Context context, IContextHubWrapper iContextHubWrapper, ContextHubClientManager contextHubClientManager, ContextHubInfo contextHubInfo, short s, PendingIntent pendingIntent, long j, String str, ContextHubTransactionManager contextHubTransactionManager) {
        this(context, iContextHubWrapper, contextHubClientManager, contextHubInfo, s, null, str, contextHubTransactionManager, pendingIntent, j, pendingIntent.getCreatorPackage());
    }

    public final void startMonitoringOpChanges() {
        this.mAppOpsManager.startWatchingMode(-1, this.mPackage, this);
    }

    public int sendMessageToNanoApp(NanoAppMessage nanoAppMessage) {
        int i;
        ContextHubServiceUtil.checkPermissions(this.mContext);
        if (isRegistered()) {
            int intValue = this.mMessageChannelNanoappIdMap.getOrDefault(Long.valueOf(nanoAppMessage.getNanoAppId()), -1).intValue();
            if (intValue == 0) {
                if (Compatibility.isChangeEnabled(181350407L)) {
                    throw new SecurityException("Client doesn't have valid permissions to send message to " + nanoAppMessage.getNanoAppId());
                }
                return 1;
            }
            if (intValue == -1) {
                checkNanoappPermsAsync();
            }
            try {
                i = this.mContextHubProxy.sendMessageToContextHub(this.mHostEndPointId, this.mAttachedContextHubInfo.getId(), nanoAppMessage);
            } catch (RemoteException e) {
                Log.e("ContextHubClientBroker", "RemoteException in sendMessageToNanoApp (target hub ID = " + this.mAttachedContextHubInfo.getId() + ")", e);
                i = 1;
            }
            ContextHubEventLogger.getInstance().logMessageToNanoapp(this.mAttachedContextHubInfo.getId(), nanoAppMessage, i == 0);
            return i;
        }
        Log.e("ContextHubClientBroker", String.format("Failed to send message (connection closed): hostEndpointId= %1$d payload %2$s", Short.valueOf(this.mHostEndPointId), Base64.getEncoder().encodeToString(nanoAppMessage.getMessageBody())));
        return 1;
    }

    public void close() {
        synchronized (this) {
            this.mPendingIntentRequest.clear();
        }
        onClientExit();
    }

    public int getId() {
        return this.mHostEndPointId;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        onClientExit();
    }

    @Override // android.app.AppOpsManager.OnOpChangedListener
    public void onOpChanged(String str, String str2) {
        if (!str2.equals(this.mPackage) || this.mMessageChannelNanoappIdMap.isEmpty()) {
            return;
        }
        checkNanoappPermsAsync();
    }

    public String getPackageName() {
        return this.mPackage;
    }

    @VisibleForTesting
    public boolean isWakelockUsable() {
        boolean z;
        synchronized (this.mWakeLock) {
            z = this.mIsWakelockUsable;
        }
        return z;
    }

    @VisibleForTesting
    public PowerManager.WakeLock getWakeLock() {
        PowerManager.WakeLock wakeLock;
        synchronized (this.mWakeLock) {
            wakeLock = this.mWakeLock;
        }
        return wakeLock;
    }

    public void setAttributionTag(String str) {
        this.mAttributionTag = str;
    }

    public String getAttributionTag() {
        return this.mAttributionTag;
    }

    public int getAttachedContextHubId() {
        return this.mAttachedContextHubInfo.getId();
    }

    public short getHostEndPointId() {
        return this.mHostEndPointId;
    }

    public void sendMessageToClient(final NanoAppMessage nanoAppMessage, List<String> list, List<String> list2) {
        final long nanoAppId = nanoAppMessage.getNanoAppId();
        int updateNanoAppAuthState = updateNanoAppAuthState(nanoAppId, list, false);
        if (updateNanoAppAuthState == 1 && !list2.isEmpty()) {
            Log.e("ContextHubClientBroker", "Dropping message from " + Long.toHexString(nanoAppId) + ". " + this.mPackage + " in grace period and napp msg has permissions");
            return;
        }
        if (updateNanoAppAuthState != 0) {
            if (notePermissions(list2, "NanoappMessageDelivery " + nanoAppId)) {
                invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda5
                    @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
                    public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                        iContextHubClientCallback.onMessageFromNanoApp(nanoAppMessage);
                    }
                });
                sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda6
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        Intent lambda$sendMessageToClient$1;
                        lambda$sendMessageToClient$1 = ContextHubClientBroker.this.lambda$sendMessageToClient$1(nanoAppId, nanoAppMessage);
                        return lambda$sendMessageToClient$1;
                    }
                }, nanoAppId);
                return;
            }
        }
        Log.e("ContextHubClientBroker", "Dropping message from " + Long.toHexString(nanoAppId) + ". " + this.mPackage + " doesn't have permission");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$sendMessageToClient$1(long j, NanoAppMessage nanoAppMessage) {
        return createIntent(5, j).putExtra("android.hardware.location.extra.MESSAGE", (Parcelable) nanoAppMessage);
    }

    public void onNanoAppLoaded(final long j) {
        checkNanoappPermsAsync();
        invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda13
            @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
            public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                iContextHubClientCallback.onNanoAppLoaded(j);
            }
        });
        sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda14
            @Override // java.util.function.Supplier
            public final Object get() {
                Intent lambda$onNanoAppLoaded$3;
                lambda$onNanoAppLoaded$3 = ContextHubClientBroker.this.lambda$onNanoAppLoaded$3(j);
                return lambda$onNanoAppLoaded$3;
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$onNanoAppLoaded$3(long j) {
        return createIntent(0, j);
    }

    public void onNanoAppUnloaded(final long j) {
        invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda11
            @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
            public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                iContextHubClientCallback.onNanoAppUnloaded(j);
            }
        });
        sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda12
            @Override // java.util.function.Supplier
            public final Object get() {
                Intent lambda$onNanoAppUnloaded$5;
                lambda$onNanoAppUnloaded$5 = ContextHubClientBroker.this.lambda$onNanoAppUnloaded$5(j);
                return lambda$onNanoAppUnloaded$5;
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$onNanoAppUnloaded$5(long j) {
        return createIntent(1, j);
    }

    public void onHubReset() {
        invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda7
            @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
            public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                iContextHubClientCallback.onHubReset();
            }
        });
        sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                Intent lambda$onHubReset$6;
                lambda$onHubReset$6 = ContextHubClientBroker.this.lambda$onHubReset$6();
                return lambda$onHubReset$6;
            }
        });
        sendHostEndpointConnectedEvent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$onHubReset$6() {
        return createIntent(6);
    }

    public void onNanoAppAborted(final long j, final int i) {
        invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda9
            @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
            public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                iContextHubClientCallback.onNanoAppAborted(j, i);
            }
        });
        sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda10
            @Override // java.util.function.Supplier
            public final Object get() {
                Intent lambda$onNanoAppAborted$8;
                lambda$onNanoAppAborted$8 = ContextHubClientBroker.this.lambda$onNanoAppAborted$8(j, i);
                return lambda$onNanoAppAborted$8;
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$onNanoAppAborted$8(long j, int i) {
        return createIntent(4, j).putExtra("android.hardware.location.extra.NANOAPP_ABORT_CODE", i);
    }

    public boolean hasPendingIntent(PendingIntent pendingIntent, long j) {
        PendingIntent pendingIntent2;
        long nanoAppId;
        synchronized (this) {
            pendingIntent2 = this.mPendingIntentRequest.getPendingIntent();
            nanoAppId = this.mPendingIntentRequest.getNanoAppId();
        }
        return pendingIntent2 != null && pendingIntent2.equals(pendingIntent) && nanoAppId == j;
    }

    public void attachDeathRecipient() throws RemoteException {
        IContextHubClientCallback iContextHubClientCallback = this.mContextHubClientCallback;
        if (iContextHubClientCallback != null) {
            iContextHubClientCallback.asBinder().linkToDeath(this, 0);
        }
    }

    public boolean hasPermissions(List<String> list) {
        for (String str : list) {
            if (this.mContext.checkPermission(str, this.mPid, this.mUid) != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean notePermissions(List<String> list, String str) {
        for (String str2 : list) {
            int permissionToOpCode = AppOpsManager.permissionToOpCode(str2);
            if (permissionToOpCode != -1) {
                try {
                    if (this.mAppOpsManager.noteOp(permissionToOpCode, this.mUid, this.mPackage, this.mAttributionTag, str) != 0) {
                        return false;
                    }
                } catch (SecurityException e) {
                    Log.e("ContextHubClientBroker", "SecurityException: noteOp for pkg " + this.mPackage + " opcode " + permissionToOpCode + ": " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPendingIntentCancelled() {
        return this.mIsPendingIntentCancelled.get();
    }

    public void handleAuthStateTimerExpiry(long j) {
        AuthStateDenialTimer remove;
        synchronized (this.mMessageChannelNanoappIdMap) {
            remove = this.mNappToAuthTimerMap.remove(Long.valueOf(j));
        }
        if (remove != null) {
            updateNanoAppAuthState(j, Collections.emptyList(), true);
        }
    }

    public final void checkNanoappPermsAsync() {
        if (this.mIsPermQueryIssued.getAndSet(true)) {
            return;
        }
        this.mTransactionManager.addTransaction(this.mTransactionManager.createQueryTransaction(this.mAttachedContextHubInfo.getId(), this.mQueryPermsCallback, this.mPackage));
    }

    public final int updateNanoAppAuthState(long j, List<String> list, boolean z) {
        return updateNanoAppAuthState(j, list, z, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x005c A[Catch: all -> 0x009b, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x001f, B:9:0x0031, B:24:0x005c, B:26:0x006a, B:30:0x0087, B:31:0x0094, B:28:0x0070, B:22:0x0051), top: B:38:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006e  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0087 A[Catch: all -> 0x009b, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x001f, B:9:0x0031, B:24:0x005c, B:26:0x006a, B:30:0x0087, B:31:0x0094, B:28:0x0070, B:22:0x0051), top: B:38:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int updateNanoAppAuthState(long j, List<String> list, boolean z, boolean z2) {
        int intValue;
        int i;
        synchronized (this.mMessageChannelNanoappIdMap) {
            boolean hasPermissions = hasPermissions(list);
            intValue = this.mMessageChannelNanoappIdMap.getOrDefault(Long.valueOf(j), -1).intValue();
            if (intValue == -1) {
                this.mMessageChannelNanoappIdMap.put(Long.valueOf(j), 2);
                intValue = 2;
            }
            i = 0;
            if (!z2 && !this.mForceDeniedNapps.contains(Long.valueOf(j))) {
                if (z) {
                    if (intValue == 1) {
                        if (i == 1) {
                            AuthStateDenialTimer remove = this.mNappToAuthTimerMap.remove(Long.valueOf(j));
                            if (remove != null) {
                                remove.cancel();
                            }
                        } else if (intValue == 2) {
                            AuthStateDenialTimer authStateDenialTimer = new AuthStateDenialTimer(this, j, Looper.getMainLooper());
                            this.mNappToAuthTimerMap.put(Long.valueOf(j), authStateDenialTimer);
                            authStateDenialTimer.start();
                        }
                        if (intValue != i) {
                            this.mMessageChannelNanoappIdMap.put(Long.valueOf(j), Integer.valueOf(i));
                        }
                    }
                    i = intValue;
                    if (i == 1) {
                    }
                    if (intValue != i) {
                    }
                } else {
                    if (intValue != 2 || hasPermissions) {
                        if (intValue != 2 && hasPermissions) {
                            i = 2;
                        }
                        i = intValue;
                    } else {
                        i = 1;
                    }
                    if (i == 1) {
                    }
                    if (intValue != i) {
                    }
                }
            }
            this.mForceDeniedNapps.add(Long.valueOf(j));
            if (i == 1) {
            }
            if (intValue != i) {
            }
        }
        if (intValue != i) {
            sendAuthStateCallback(j, i);
        }
        return i;
    }

    public final void sendAuthStateCallback(final long j, final int i) {
        invokeCallback(new CallbackConsumer() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda3
            @Override // com.android.server.location.contexthub.ContextHubClientBroker.CallbackConsumer
            public final void accept(IContextHubClientCallback iContextHubClientCallback) {
                iContextHubClientCallback.onClientAuthorizationChanged(j, i);
            }
        });
        sendPendingIntent(new Supplier() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                Intent lambda$sendAuthStateCallback$10;
                lambda$sendAuthStateCallback$10 = ContextHubClientBroker.this.lambda$sendAuthStateCallback$10(j, i);
                return lambda$sendAuthStateCallback$10;
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Intent lambda$sendAuthStateCallback$10(long j, int i) {
        return createIntent(7, j).putExtra("android.hardware.location.extra.CLIENT_AUTHORIZATION_STATE", i);
    }

    public final synchronized void invokeCallback(CallbackConsumer callbackConsumer) {
        if (this.mContextHubClientCallback != null) {
            try {
                acquireWakeLock();
                callbackConsumer.accept(this.mContextHubClientCallback);
            } catch (RemoteException e) {
                Log.e("ContextHubClientBroker", "RemoteException while invoking client callback (host endpoint ID = " + ((int) this.mHostEndPointId) + ")", e);
            }
        }
    }

    public final Intent createIntent(int i) {
        Intent intent = new Intent();
        intent.putExtra("android.hardware.location.extra.EVENT_TYPE", i);
        intent.putExtra("android.hardware.location.extra.CONTEXT_HUB_INFO", (Parcelable) this.mAttachedContextHubInfo);
        return intent;
    }

    public final Intent createIntent(int i, long j) {
        Intent createIntent = createIntent(i);
        createIntent.putExtra("android.hardware.location.extra.NANOAPP_ID", j);
        return createIntent;
    }

    public final synchronized void sendPendingIntent(Supplier<Intent> supplier) {
        if (this.mPendingIntentRequest.hasPendingIntent()) {
            doSendPendingIntent(this.mPendingIntentRequest.getPendingIntent(), supplier.get(), this);
        }
    }

    public final synchronized void sendPendingIntent(Supplier<Intent> supplier, long j) {
        if (this.mPendingIntentRequest.hasPendingIntent() && this.mPendingIntentRequest.getNanoAppId() == j) {
            doSendPendingIntent(this.mPendingIntentRequest.getPendingIntent(), supplier.get(), this);
        }
    }

    @VisibleForTesting
    public void doSendPendingIntent(PendingIntent pendingIntent, Intent intent, PendingIntent.OnFinished onFinished) {
        try {
            acquireWakeLock();
            pendingIntent.send(this.mContext, 0, intent, onFinished, null, "android.permission.ACCESS_CONTEXT_HUB", null);
        } catch (PendingIntent.CanceledException unused) {
            this.mIsPendingIntentCancelled.set(true);
            Log.w("ContextHubClientBroker", "PendingIntent has been canceled, unregistering from client (host endpoint ID " + ((int) this.mHostEndPointId) + ")");
            close();
        }
    }

    public final synchronized boolean isRegistered() {
        return this.mRegistered;
    }

    public final synchronized void onClientExit() {
        IContextHubClientCallback iContextHubClientCallback = this.mContextHubClientCallback;
        if (iContextHubClientCallback != null) {
            iContextHubClientCallback.asBinder().unlinkToDeath(this, 0);
            this.mContextHubClientCallback = null;
        }
        if (!this.mPendingIntentRequest.hasPendingIntent() && this.mRegistered) {
            this.mClientManager.unregisterClient(this.mHostEndPointId);
            this.mRegistered = false;
            this.mAppOpsManager.stopWatchingMode(this);
            this.mContextHubProxy.onHostEndpointDisconnected(this.mHostEndPointId);
            releaseWakeLockOnExit();
        }
    }

    public final void sendHostEndpointConnectedEvent() {
        HostEndpointInfo hostEndpointInfo = new HostEndpointInfo();
        hostEndpointInfo.hostEndpointId = (char) this.mHostEndPointId;
        hostEndpointInfo.packageName = this.mPackage;
        hostEndpointInfo.attributionTag = this.mAttributionTag;
        hostEndpointInfo.type = this.mUid == 1000 ? 1 : 2;
        this.mContextHubProxy.onHostEndpointConnected(hostEndpointInfo);
    }

    public void dump(ProtoOutputStream protoOutputStream) {
        protoOutputStream.write(1120986464257L, (int) getHostEndPointId());
        protoOutputStream.write(1120986464258L, getAttachedContextHubId());
        protoOutputStream.write(1138166333443L, this.mPackage);
        if (this.mPendingIntentRequest.isValid()) {
            protoOutputStream.write(1133871366149L, true);
            protoOutputStream.write(1112396529668L, this.mPendingIntentRequest.getNanoAppId());
        }
        protoOutputStream.write(1133871366150L, this.mPendingIntentRequest.hasPendingIntent());
        protoOutputStream.write(1133871366151L, isPendingIntentCancelled());
        protoOutputStream.write(1133871366152L, this.mRegistered);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[ContextHubClient ");
        sb.append("endpointID: ");
        sb.append((int) getHostEndPointId());
        sb.append(", ");
        sb.append("contextHub: ");
        sb.append(getAttachedContextHubId());
        sb.append(", ");
        if (this.mAttributionTag != null) {
            sb.append("attributionTag: ");
            sb.append(getAttributionTag());
            sb.append(", ");
        }
        if (this.mPendingIntentRequest.isValid()) {
            sb.append("intentCreatorPackage: ");
            sb.append(this.mPackage);
            sb.append(", ");
            sb.append("nanoAppId: 0x");
            sb.append(Long.toHexString(this.mPendingIntentRequest.getNanoAppId()));
        } else {
            sb.append("package: ");
            sb.append(this.mPackage);
        }
        if (this.mMessageChannelNanoappIdMap.size() > 0) {
            sb.append(" messageChannelNanoappSet: (");
            Iterator<Map.Entry<Long, Integer>> it = this.mMessageChannelNanoappIdMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, Integer> next = it.next();
                sb.append("0x");
                sb.append(Long.toHexString(next.getKey().longValue()));
                sb.append(" auth state: ");
                sb.append(authStateToString(next.getValue().intValue()));
                if (it.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        synchronized (this.mWakeLock) {
            sb.append("wakelock: ");
            sb.append(this.mWakeLock);
        }
        sb.append("]");
        return sb.toString();
    }

    public void callbackFinished() {
        releaseWakeLock();
    }

    @Override // android.app.PendingIntent.OnFinished
    public void onSendFinished(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle) {
        releaseWakeLock();
    }

    public final void acquireWakeLock() {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda1
            public final void runOrThrow() {
                ContextHubClientBroker.this.lambda$acquireWakeLock$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$acquireWakeLock$11() throws Exception {
        synchronized (this.mWakeLock) {
            if (this.mIsWakelockUsable) {
                this.mWakeLock.acquire(5000L);
            }
        }
    }

    public final void releaseWakeLock() {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda2
            public final void runOrThrow() {
                ContextHubClientBroker.this.lambda$releaseWakeLock$12();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$releaseWakeLock$12() throws Exception {
        synchronized (this.mWakeLock) {
            if (this.mWakeLock.isHeld()) {
                try {
                    this.mWakeLock.release();
                } catch (RuntimeException e) {
                    Log.e("ContextHubClientBroker", "Releasing the wakelock fails - ", e);
                }
            }
        }
    }

    public final void releaseWakeLockOnExit() {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.location.contexthub.ContextHubClientBroker$$ExternalSyntheticLambda0
            public final void runOrThrow() {
                ContextHubClientBroker.this.lambda$releaseWakeLockOnExit$13();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$releaseWakeLockOnExit$13() throws Exception {
        synchronized (this.mWakeLock) {
            this.mIsWakelockUsable = false;
            while (this.mWakeLock.isHeld()) {
                try {
                    this.mWakeLock.release();
                } catch (RuntimeException e) {
                    Log.e("ContextHubClientBroker", "Releasing the wakelock for all acquisitions fails - ", e);
                }
            }
        }
    }
}
