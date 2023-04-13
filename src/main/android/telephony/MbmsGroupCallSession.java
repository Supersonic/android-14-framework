package android.telephony;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.mbms.GroupCall;
import android.telephony.mbms.GroupCallCallback;
import android.telephony.mbms.InternalGroupCallCallback;
import android.telephony.mbms.InternalGroupCallSessionCallback;
import android.telephony.mbms.MbmsGroupCallSessionCallback;
import android.telephony.mbms.MbmsUtils;
import android.telephony.mbms.vendor.IMbmsGroupCallService;
import android.util.ArraySet;
import android.util.Log;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class MbmsGroupCallSession implements AutoCloseable {
    private static final String LOG_TAG = "MbmsGroupCallSession";
    @SystemApi
    public static final String MBMS_GROUP_CALL_SERVICE_ACTION = "android.telephony.action.EmbmsGroupCall";
    public static final String MBMS_GROUP_CALL_SERVICE_OVERRIDE_METADATA = "mbms-group-call-service-override";
    private static AtomicBoolean sIsInitialized = new AtomicBoolean(false);
    private final Context mContext;
    private InternalGroupCallSessionCallback mInternalCallback;
    private ServiceConnection mServiceConnection;
    private int mSubscriptionId;
    private AtomicReference<IMbmsGroupCallService> mService = new AtomicReference<>(null);
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.telephony.MbmsGroupCallSession.1
        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            MbmsGroupCallSession.sIsInitialized.set(false);
            MbmsGroupCallSession.this.mInternalCallback.onError(3, "Received death notification");
        }
    };
    private Set<GroupCall> mKnownActiveGroupCalls = new ArraySet();

    private MbmsGroupCallSession(Context context, Executor executor, int subscriptionId, MbmsGroupCallSessionCallback callback) {
        this.mContext = context;
        this.mSubscriptionId = subscriptionId;
        this.mInternalCallback = new InternalGroupCallSessionCallback(callback, executor);
    }

    public static MbmsGroupCallSession create(Context context, int subscriptionId, Executor executor, final MbmsGroupCallSessionCallback callback) {
        if (!sIsInitialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot create two instances of MbmsGroupCallSession");
        }
        MbmsGroupCallSession session = new MbmsGroupCallSession(context, executor, subscriptionId, callback);
        final int result = session.bindAndInitialize();
        if (result != 0) {
            sIsInitialized.set(false);
            executor.execute(new Runnable() { // from class: android.telephony.MbmsGroupCallSession.2
                @Override // java.lang.Runnable
                public void run() {
                    MbmsGroupCallSessionCallback.this.onError(result, null);
                }
            });
            return null;
        }
        return session;
    }

    public static MbmsGroupCallSession create(Context context, Executor executor, MbmsGroupCallSessionCallback callback) {
        return create(context, SubscriptionManager.getDefaultSubscriptionId(), executor, callback);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        IMbmsGroupCallService groupCallService;
        try {
            groupCallService = this.mService.get();
        } catch (RemoteException e) {
        } catch (Throwable th) {
            this.mService.set(null);
            sIsInitialized.set(false);
            this.mServiceConnection = null;
            this.mInternalCallback.stop();
            throw th;
        }
        if (groupCallService != null && this.mServiceConnection != null) {
            groupCallService.dispose(this.mSubscriptionId);
            for (GroupCall s : this.mKnownActiveGroupCalls) {
                s.getCallback().stop();
            }
            this.mKnownActiveGroupCalls.clear();
            this.mContext.unbindService(this.mServiceConnection);
            this.mService.set(null);
            sIsInitialized.set(false);
            this.mServiceConnection = null;
            this.mInternalCallback.stop();
            return;
        }
        this.mService.set(null);
        sIsInitialized.set(false);
        this.mServiceConnection = null;
        this.mInternalCallback.stop();
    }

    public GroupCall startGroupCall(long tmgi, List<Integer> saiList, List<Integer> frequencyList, Executor executor, GroupCallCallback callback) {
        IMbmsGroupCallService groupCallService = this.mService.get();
        if (groupCallService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        InternalGroupCallCallback serviceCallback = new InternalGroupCallCallback(callback, executor);
        GroupCall serviceForApp = new GroupCall(this.mSubscriptionId, groupCallService, this, tmgi, serviceCallback);
        this.mKnownActiveGroupCalls.add(serviceForApp);
        try {
            int returnCode = groupCallService.startGroupCall(this.mSubscriptionId, tmgi, saiList, frequencyList, serviceCallback);
            if (returnCode == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            } else if (returnCode != 0) {
                this.mInternalCallback.onError(returnCode, null);
                return null;
            } else {
                return serviceForApp;
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote process died");
            this.mService.set(null);
            sIsInitialized.set(false);
            this.mInternalCallback.onError(3, null);
            return null;
        }
    }

    public void onGroupCallStopped(GroupCall service) {
        this.mKnownActiveGroupCalls.remove(service);
    }

    private int bindAndInitialize() {
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: android.telephony.MbmsGroupCallSession.3
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMbmsGroupCallService groupCallService = IMbmsGroupCallService.Stub.asInterface(service);
                try {
                    int result = groupCallService.initialize(MbmsGroupCallSession.this.mInternalCallback, MbmsGroupCallSession.this.mSubscriptionId);
                    if (result == -1) {
                        MbmsGroupCallSession.this.close();
                        throw new IllegalStateException("Middleware must not return an unknown error code");
                    } else if (result != 0) {
                        MbmsGroupCallSession.this.mInternalCallback.onError(result, "Error returned during initialization");
                        MbmsGroupCallSession.sIsInitialized.set(false);
                    } else {
                        try {
                            groupCallService.asBinder().linkToDeath(MbmsGroupCallSession.this.mDeathRecipient, 0);
                            MbmsGroupCallSession.this.mService.set(groupCallService);
                        } catch (RemoteException e) {
                            MbmsGroupCallSession.this.mInternalCallback.onError(3, "Middleware lost during initialization");
                            MbmsGroupCallSession.sIsInitialized.set(false);
                        }
                    }
                } catch (RemoteException e2) {
                    Log.m110e(MbmsGroupCallSession.LOG_TAG, "Service died before initialization");
                    MbmsGroupCallSession.this.mInternalCallback.onError(103, e2.toString());
                    MbmsGroupCallSession.sIsInitialized.set(false);
                } catch (RuntimeException e3) {
                    Log.m110e(MbmsGroupCallSession.LOG_TAG, "Runtime exception during initialization");
                    MbmsGroupCallSession.this.mInternalCallback.onError(103, e3.toString());
                    MbmsGroupCallSession.sIsInitialized.set(false);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                MbmsGroupCallSession.sIsInitialized.set(false);
                MbmsGroupCallSession.this.mService.set(null);
            }

            @Override // android.content.ServiceConnection
            public void onNullBinding(ComponentName name) {
                Log.m104w(MbmsGroupCallSession.LOG_TAG, "bindAndInitialize: Remote service returned null");
                MbmsGroupCallSession.this.mInternalCallback.onError(3, "Middleware service binding returned null");
                MbmsGroupCallSession.sIsInitialized.set(false);
                MbmsGroupCallSession.this.mService.set(null);
                MbmsGroupCallSession.this.mContext.unbindService(this);
            }
        };
        this.mServiceConnection = serviceConnection;
        return MbmsUtils.startBinding(this.mContext, MBMS_GROUP_CALL_SERVICE_ACTION, serviceConnection);
    }
}
