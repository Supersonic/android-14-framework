package android.telephony;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.mbms.InternalStreamingServiceCallback;
import android.telephony.mbms.InternalStreamingSessionCallback;
import android.telephony.mbms.MbmsStreamingSessionCallback;
import android.telephony.mbms.MbmsUtils;
import android.telephony.mbms.StreamingService;
import android.telephony.mbms.StreamingServiceCallback;
import android.telephony.mbms.StreamingServiceInfo;
import android.telephony.mbms.vendor.IMbmsStreamingService;
import android.util.ArraySet;
import android.util.Log;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class MbmsStreamingSession implements AutoCloseable {
    private static final String LOG_TAG = "MbmsStreamingSession";
    @SystemApi
    public static final String MBMS_STREAMING_SERVICE_ACTION = "android.telephony.action.EmbmsStreaming";
    public static final String MBMS_STREAMING_SERVICE_OVERRIDE_METADATA = "mbms-streaming-service-override";
    private static AtomicBoolean sIsInitialized = new AtomicBoolean(false);
    private final Context mContext;
    private InternalStreamingSessionCallback mInternalCallback;
    private ServiceConnection mServiceConnection;
    private int mSubscriptionId;
    private AtomicReference<IMbmsStreamingService> mService = new AtomicReference<>(null);
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.telephony.MbmsStreamingSession.1
        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            MbmsStreamingSession.sIsInitialized.set(false);
            MbmsStreamingSession.this.sendErrorToApp(3, "Received death notification");
        }
    };
    private Set<StreamingService> mKnownActiveStreamingServices = new ArraySet();

    private MbmsStreamingSession(Context context, Executor executor, int subscriptionId, MbmsStreamingSessionCallback callback) {
        this.mSubscriptionId = -1;
        this.mContext = context;
        this.mSubscriptionId = subscriptionId;
        this.mInternalCallback = new InternalStreamingSessionCallback(callback, executor);
    }

    public static MbmsStreamingSession create(Context context, Executor executor, int subscriptionId, final MbmsStreamingSessionCallback callback) {
        if (!sIsInitialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot create two instances of MbmsStreamingSession");
        }
        MbmsStreamingSession session = new MbmsStreamingSession(context, executor, subscriptionId, callback);
        final int result = session.bindAndInitialize();
        if (result != 0) {
            sIsInitialized.set(false);
            executor.execute(new Runnable() { // from class: android.telephony.MbmsStreamingSession.2
                @Override // java.lang.Runnable
                public void run() {
                    MbmsStreamingSessionCallback.this.onError(result, null);
                }
            });
            return null;
        }
        return session;
    }

    public static MbmsStreamingSession create(Context context, Executor executor, MbmsStreamingSessionCallback callback) {
        return create(context, executor, SubscriptionManager.getDefaultSubscriptionId(), callback);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        IMbmsStreamingService streamingService;
        try {
            streamingService = this.mService.get();
        } catch (RemoteException e) {
        } catch (Throwable th) {
            this.mService.set(null);
            sIsInitialized.set(false);
            this.mServiceConnection = null;
            this.mInternalCallback.stop();
            throw th;
        }
        if (streamingService != null && this.mServiceConnection != null) {
            streamingService.dispose(this.mSubscriptionId);
            for (StreamingService s : this.mKnownActiveStreamingServices) {
                s.getCallback().stop();
            }
            this.mKnownActiveStreamingServices.clear();
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

    public void requestUpdateStreamingServices(List<String> serviceClassList) {
        IMbmsStreamingService streamingService = this.mService.get();
        if (streamingService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            int returnCode = streamingService.requestUpdateStreamingServices(this.mSubscriptionId, serviceClassList);
            if (returnCode == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (returnCode != 0) {
                sendErrorToApp(returnCode, null);
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote process died");
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public StreamingService startStreaming(StreamingServiceInfo serviceInfo, Executor executor, StreamingServiceCallback callback) {
        IMbmsStreamingService streamingService = this.mService.get();
        if (streamingService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        InternalStreamingServiceCallback serviceCallback = new InternalStreamingServiceCallback(callback, executor);
        StreamingService serviceForApp = new StreamingService(this.mSubscriptionId, streamingService, this, serviceInfo, serviceCallback);
        this.mKnownActiveStreamingServices.add(serviceForApp);
        try {
            int returnCode = streamingService.startStreaming(this.mSubscriptionId, serviceInfo.getServiceId(), serviceCallback);
            if (returnCode == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            } else if (returnCode != 0) {
                sendErrorToApp(returnCode, null);
                return null;
            } else {
                return serviceForApp;
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote process died");
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
            return null;
        }
    }

    public void onStreamingServiceStopped(StreamingService service) {
        this.mKnownActiveStreamingServices.remove(service);
    }

    private int bindAndInitialize() {
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: android.telephony.MbmsStreamingSession.3
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMbmsStreamingService streamingService = IMbmsStreamingService.Stub.asInterface(service);
                try {
                    int result = streamingService.initialize(MbmsStreamingSession.this.mInternalCallback, MbmsStreamingSession.this.mSubscriptionId);
                    if (result == -1) {
                        MbmsStreamingSession.this.close();
                        throw new IllegalStateException("Middleware must not return an unknown error code");
                    } else if (result != 0) {
                        MbmsStreamingSession.this.sendErrorToApp(result, "Error returned during initialization");
                        MbmsStreamingSession.sIsInitialized.set(false);
                    } else {
                        try {
                            streamingService.asBinder().linkToDeath(MbmsStreamingSession.this.mDeathRecipient, 0);
                            MbmsStreamingSession.this.mService.set(streamingService);
                        } catch (RemoteException e) {
                            MbmsStreamingSession.this.sendErrorToApp(3, "Middleware lost during initialization");
                            MbmsStreamingSession.sIsInitialized.set(false);
                        }
                    }
                } catch (RemoteException e2) {
                    Log.m110e(MbmsStreamingSession.LOG_TAG, "Service died before initialization");
                    MbmsStreamingSession.this.sendErrorToApp(103, e2.toString());
                    MbmsStreamingSession.sIsInitialized.set(false);
                } catch (RuntimeException e3) {
                    Log.m110e(MbmsStreamingSession.LOG_TAG, "Runtime exception during initialization");
                    MbmsStreamingSession.this.sendErrorToApp(103, e3.toString());
                    MbmsStreamingSession.sIsInitialized.set(false);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                MbmsStreamingSession.sIsInitialized.set(false);
                MbmsStreamingSession.this.mService.set(null);
            }

            @Override // android.content.ServiceConnection
            public void onNullBinding(ComponentName name) {
                Log.m104w(MbmsStreamingSession.LOG_TAG, "bindAndInitialize: Remote service returned null");
                MbmsStreamingSession.this.sendErrorToApp(3, "Middleware service binding returned null");
                MbmsStreamingSession.sIsInitialized.set(false);
                MbmsStreamingSession.this.mService.set(null);
                MbmsStreamingSession.this.mContext.unbindService(this);
            }
        };
        this.mServiceConnection = serviceConnection;
        return MbmsUtils.startBinding(this.mContext, MBMS_STREAMING_SERVICE_ACTION, serviceConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendErrorToApp(int errorCode, String message) {
        try {
            this.mInternalCallback.onError(errorCode, message);
        } catch (RemoteException e) {
        }
    }
}
