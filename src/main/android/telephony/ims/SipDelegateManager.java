package android.telephony.ims;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.telephony.BinderCacheManager;
import android.telephony.ims.aidl.IImsRcsController;
import android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper;
import android.telephony.ims.stub.DelegateConnectionMessageCallback;
import android.telephony.ims.stub.DelegateConnectionStateCallback;
import android.util.ArrayMap;
import com.android.internal.telephony.ITelephony;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class SipDelegateManager {
    public static final int DENIED_REASON_INVALID = 4;
    public static final int DENIED_REASON_IN_USE_BY_ANOTHER_DELEGATE = 1;
    public static final int DENIED_REASON_NOT_ALLOWED = 2;
    public static final int DENIED_REASON_SINGLE_REGISTRATION_NOT_ALLOWED = 3;
    public static final int DENIED_REASON_UNKNOWN = 0;
    public static final int MESSAGE_FAILURE_REASON_DELEGATE_CLOSED = 2;
    public static final int MESSAGE_FAILURE_REASON_DELEGATE_DEAD = 1;
    public static final int MESSAGE_FAILURE_REASON_INTERNAL_DELEGATE_STATE_TRANSITION = 11;
    public static final int MESSAGE_FAILURE_REASON_INVALID_BODY_CONTENT = 5;
    public static final int MESSAGE_FAILURE_REASON_INVALID_FEATURE_TAG = 6;
    public static final int MESSAGE_FAILURE_REASON_INVALID_HEADER_FIELDS = 4;
    public static final int MESSAGE_FAILURE_REASON_INVALID_START_LINE = 3;
    public static final int MESSAGE_FAILURE_REASON_NETWORK_NOT_AVAILABLE = 8;
    public static final int MESSAGE_FAILURE_REASON_NOT_REGISTERED = 9;
    public static final int MESSAGE_FAILURE_REASON_STALE_IMS_CONFIGURATION = 10;
    public static final ArrayMap<Integer, String> MESSAGE_FAILURE_REASON_STRING_MAP;
    public static final int MESSAGE_FAILURE_REASON_TAG_NOT_ENABLED_FOR_DELEGATE = 7;
    public static final int MESSAGE_FAILURE_REASON_UNKNOWN = 0;
    public static final int SIP_DELEGATE_DESTROY_REASON_REQUESTED_BY_APP = 2;
    public static final int SIP_DELEGATE_DESTROY_REASON_SERVICE_DEAD = 1;
    public static final int SIP_DELEGATE_DESTROY_REASON_SUBSCRIPTION_TORN_DOWN = 4;
    public static final int SIP_DELEGATE_DESTROY_REASON_UNKNOWN = 0;
    public static final int SIP_DELEGATE_DESTROY_REASON_USER_DISABLED_RCS = 3;
    private final BinderCacheManager<IImsRcsController> mBinderCache;
    private final Context mContext;
    private final int mSubId;
    private final BinderCacheManager<ITelephony> mTelephonyBinderCache;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeniedReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MessageFailureReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SipDelegateDestroyReason {
    }

    static {
        ArrayMap<Integer, String> arrayMap = new ArrayMap<>(11);
        MESSAGE_FAILURE_REASON_STRING_MAP = arrayMap;
        arrayMap.append(0, "MESSAGE_FAILURE_REASON_UNKNOWN");
        arrayMap.append(1, "MESSAGE_FAILURE_REASON_DELEGATE_DEAD");
        arrayMap.append(2, "MESSAGE_FAILURE_REASON_DELEGATE_CLOSED");
        arrayMap.append(4, "MESSAGE_FAILURE_REASON_INVALID_HEADER_FIELDS");
        arrayMap.append(5, "MESSAGE_FAILURE_REASON_INVALID_BODY_CONTENT");
        arrayMap.append(6, "MESSAGE_FAILURE_REASON_INVALID_FEATURE_TAG");
        arrayMap.append(7, "MESSAGE_FAILURE_REASON_TAG_NOT_ENABLED_FOR_DELEGATE");
        arrayMap.append(8, "MESSAGE_FAILURE_REASON_NETWORK_NOT_AVAILABLE");
        arrayMap.append(9, "MESSAGE_FAILURE_REASON_NOT_REGISTERED");
        arrayMap.append(10, "MESSAGE_FAILURE_REASON_STALE_IMS_CONFIGURATION");
        arrayMap.append(11, "MESSAGE_FAILURE_REASON_INTERNAL_DELEGATE_STATE_TRANSITION");
    }

    public SipDelegateManager(Context context, int subId, BinderCacheManager<IImsRcsController> binderCache, BinderCacheManager<ITelephony> telephonyBinderCache) {
        this.mContext = context;
        this.mSubId = subId;
        this.mBinderCache = binderCache;
        this.mTelephonyBinderCache = telephonyBinderCache;
    }

    public boolean isSupported() throws ImsException {
        try {
            IImsRcsController controller = this.mBinderCache.getBinder();
            if (controller == null) {
                throw new ImsException("Telephony server is down", 1);
            }
            return controller.isSipDelegateSupported(this.mSubId);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    public void createSipDelegate(DelegateRequest request, Executor executor, DelegateConnectionStateCallback dc, DelegateConnectionMessageCallback mc) throws ImsException {
        Objects.requireNonNull(request, "The DelegateRequest must not be null.");
        Objects.requireNonNull(executor, "The Executor must not be null.");
        Objects.requireNonNull(dc, "The DelegateConnectionStateCallback must not be null.");
        Objects.requireNonNull(mc, "The DelegateConnectionMessageCallback must not be null.");
        try {
            final SipDelegateConnectionAidlWrapper wrapper = new SipDelegateConnectionAidlWrapper(executor, dc, mc);
            BinderCacheManager<IImsRcsController> binderCacheManager = this.mBinderCache;
            Objects.requireNonNull(wrapper);
            IImsRcsController controller = binderCacheManager.listenOnBinder(wrapper, new Runnable() { // from class: android.telephony.ims.SipDelegateManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SipDelegateConnectionAidlWrapper.this.binderDied();
                }
            });
            if (controller == null) {
                throw new ImsException("Telephony server is down", 1);
            }
            controller.createSipDelegate(this.mSubId, request, this.mContext.getOpPackageName(), wrapper.getStateCallbackBinder(), wrapper.getMessageCallbackBinder());
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    public void destroySipDelegate(SipDelegateConnection delegateConnection, int reason) {
        Objects.requireNonNull(delegateConnection, "SipDelegateConnection can not be null.");
        if (delegateConnection instanceof SipDelegateConnectionAidlWrapper) {
            SipDelegateConnectionAidlWrapper w = (SipDelegateConnectionAidlWrapper) delegateConnection;
            try {
                IImsRcsController c = this.mBinderCache.removeRunnable(w);
                c.destroySipDelegate(this.mSubId, w.getSipDelegateBinder(), reason);
                return;
            } catch (RemoteException e) {
                try {
                    w.getStateCallbackBinder().onDestroyed(2);
                    return;
                } catch (RemoteException e2) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Unknown SipDelegateConnection implementation passed into this method");
    }

    public void triggerFullNetworkRegistration(SipDelegateConnection connection, int sipCode, String sipReason) {
        Objects.requireNonNull(connection, "SipDelegateConnection can not be null.");
        if (connection instanceof SipDelegateConnectionAidlWrapper) {
            SipDelegateConnectionAidlWrapper w = (SipDelegateConnectionAidlWrapper) connection;
            try {
                IImsRcsController controller = this.mBinderCache.getBinder();
                controller.triggerNetworkRegistration(this.mSubId, w.getSipDelegateBinder(), sipCode, sipReason);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        throw new IllegalArgumentException("Unknown SipDelegateConnection implementation passed into this method");
    }

    public void registerImsStateCallback(Executor executor, ImsStateCallback callback) throws ImsException {
        Objects.requireNonNull(callback, "Must include a non-null ImsStateCallback.");
        Objects.requireNonNull(executor, "Must include a non-null Executor.");
        callback.init(executor);
        BinderCacheManager<ITelephony> binderCacheManager = this.mTelephonyBinderCache;
        Objects.requireNonNull(callback);
        ITelephony telephony = binderCacheManager.listenOnBinder(callback, new ImsMmTelManager$$ExternalSyntheticLambda2(callback));
        if (telephony == null) {
            throw new ImsException("Telephony server is down", 1);
        }
        try {
            telephony.registerImsStateCallback(this.mSubId, 2, callback.getCallbackBinder(), this.mContext.getOpPackageName());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    public void unregisterImsStateCallback(ImsStateCallback callback) {
        Objects.requireNonNull(callback, "Must include a non-null ImsStateCallback.");
        ITelephony telephony = this.mTelephonyBinderCache.removeRunnable(callback);
        if (telephony != null) {
            try {
                telephony.unregisterImsStateCallback(callback.getCallbackBinder());
            } catch (RemoteException e) {
            }
        }
    }

    public void registerSipDialogStateCallback(Executor executor, final SipDialogStateCallback callback) throws ImsException {
        Objects.requireNonNull(callback, "Must include a non-null SipDialogStateCallback.");
        Objects.requireNonNull(executor, "Must include a non-null Executor.");
        callback.attachExecutor(executor);
        try {
            BinderCacheManager<IImsRcsController> binderCacheManager = this.mBinderCache;
            Objects.requireNonNull(callback);
            IImsRcsController controller = binderCacheManager.listenOnBinder(callback, new Runnable() { // from class: android.telephony.ims.SipDelegateManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SipDialogStateCallback.this.binderDied();
                }
            });
            if (controller == null) {
                throw new ImsException("Telephony server is down", 1);
            }
            controller.registerSipDialogStateCallback(this.mSubId, callback.getCallbackBinder());
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        } catch (IllegalStateException e3) {
            throw new IllegalStateException(e3.getMessage());
        }
    }

    public void unregisterSipDialogStateCallback(SipDialogStateCallback callback) throws ImsException {
        Objects.requireNonNull(callback, "Must include a non-null SipDialogStateCallback.");
        IImsRcsController controller = this.mBinderCache.removeRunnable(callback);
        try {
            if (controller == null) {
                throw new ImsException("Telephony server is down", 1);
            }
            controller.unregisterSipDialogStateCallback(this.mSubId, callback.getCallbackBinder());
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        } catch (IllegalStateException e3) {
            throw new IllegalStateException(e3.getMessage());
        }
    }
}
