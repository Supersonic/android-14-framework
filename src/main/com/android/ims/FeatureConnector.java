package com.android.ims;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.ims.ImsService;
import android.telephony.ims.feature.ImsFeature;
import com.android.ims.FeatureConnector;
import com.android.ims.FeatureUpdates;
import com.android.ims.internal.IImsServiceFeatureCallback;
import com.android.telephony.Rlog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class FeatureConnector<U extends FeatureUpdates> {
    private static final boolean DBG = false;
    private static final String TAG = "FeatureConnector";
    public static final int UNAVAILABLE_REASON_DISCONNECTED = 0;
    public static final int UNAVAILABLE_REASON_IMS_UNSUPPORTED = 2;
    public static final int UNAVAILABLE_REASON_NOT_READY = 1;
    public static final int UNAVAILABLE_REASON_SERVER_UNAVAILABLE = 3;
    private final Context mContext;
    private Integer mDisconnectedReason;
    private final Executor mExecutor;
    private final ManagerFactory<U> mFactory;
    private boolean mLastReadyState;
    private final Listener<U> mListener;
    private final String mLogPrefix;
    private U mManager;
    private final int mPhoneId;
    private final List<Integer> mReadyFilter;
    private final IImsServiceFeatureCallback mCallback = new C00001();
    private final Object mLock = new Object();

    /* loaded from: classes.dex */
    public interface Listener<U extends FeatureUpdates> {
        void connectionReady(U u, int i) throws ImsException;

        void connectionUnavailable(int i);
    }

    /* loaded from: classes.dex */
    public interface ManagerFactory<U extends FeatureUpdates> {
        U createManager(Context context, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UnavailableReason {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.ims.FeatureConnector$1 */
    /* loaded from: classes.dex */
    public class C00001 extends IImsServiceFeatureCallback.Stub {
        C00001() {
        }

        public void imsFeatureCreated(ImsFeatureContainer c, int subId) {
            FeatureConnector.this.log("imsFeatureCreated: " + c + ", subId: " + subId);
            synchronized (FeatureConnector.this.mLock) {
                FeatureConnector.this.mManager.associate(c, subId);
                FeatureConnector.this.mManager.updateFeatureCapabilities(c.getCapabilities());
                FeatureConnector.this.mDisconnectedReason = null;
            }
            imsStatusChanged(c.getState(), subId);
        }

        public void imsFeatureRemoved(final int reason) {
            FeatureConnector.this.log("imsFeatureRemoved: reason=" + reason);
            synchronized (FeatureConnector.this.mLock) {
                if (FeatureConnector.this.mDisconnectedReason != null && FeatureConnector.this.mDisconnectedReason.intValue() == reason && FeatureConnector.this.mDisconnectedReason.intValue() != 3) {
                    FeatureConnector.this.log("imsFeatureRemoved: ignore");
                    return;
                }
                FeatureConnector.this.mDisconnectedReason = Integer.valueOf(reason);
                FeatureConnector.this.mLastReadyState = FeatureConnector.DBG;
                FeatureConnector.this.mExecutor.execute(new Runnable() { // from class: com.android.ims.FeatureConnector$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        FeatureConnector.C00001.this.lambda$imsFeatureRemoved$0(reason);
                    }
                });
                FeatureConnector.this.mManager.invalidate();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$imsFeatureRemoved$0(int reason) {
            FeatureConnector.this.mListener.connectionUnavailable(reason);
        }

        public void imsStatusChanged(int status, final int subId) {
            FeatureConnector.this.log("imsStatusChanged: status=" + ((String) ImsFeature.STATE_LOG_MAP.get(Integer.valueOf(status))));
            synchronized (FeatureConnector.this.mLock) {
                if (FeatureConnector.this.mDisconnectedReason != null) {
                    FeatureConnector.this.log("imsStatusChanged: ignore");
                    return;
                }
                FeatureConnector.this.mManager.updateFeatureState(status);
                final FeatureUpdates featureUpdates = FeatureConnector.this.mManager;
                final boolean isReady = FeatureConnector.this.mReadyFilter.contains(Integer.valueOf(status));
                boolean didReadyChange = FeatureConnector.this.mLastReadyState ^ isReady;
                FeatureConnector.this.mLastReadyState = isReady;
                if (!didReadyChange) {
                    FeatureConnector.this.log("imsStatusChanged: ready didn't change, ignore");
                } else {
                    FeatureConnector.this.mExecutor.execute(new Runnable() { // from class: com.android.ims.FeatureConnector$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            FeatureConnector.C00001.this.lambda$imsStatusChanged$1(isReady, featureUpdates, subId);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$imsStatusChanged$1(boolean isReady, FeatureUpdates manager, int subId) {
            try {
                if (isReady) {
                    FeatureConnector.this.notifyReady(manager, subId);
                } else {
                    FeatureConnector.this.notifyNotReady();
                }
            } catch (ImsException e) {
                if (e.getCode() == 150) {
                    FeatureConnector.this.mListener.connectionUnavailable(2);
                } else {
                    FeatureConnector.this.notifyNotReady();
                }
            }
        }

        public void updateCapabilities(long caps) {
            FeatureConnector.this.log("updateCapabilities: capabilities=" + ImsService.getCapabilitiesString(caps));
            synchronized (FeatureConnector.this.mLock) {
                if (FeatureConnector.this.mDisconnectedReason != null) {
                    FeatureConnector.this.log("updateCapabilities: ignore");
                } else {
                    FeatureConnector.this.mManager.updateFeatureCapabilities(caps);
                }
            }
        }
    }

    public FeatureConnector(Context context, int phoneId, ManagerFactory<U> factory, String logPrefix, List<Integer> readyFilter, Listener<U> listener, Executor executor) {
        ArrayList arrayList = new ArrayList();
        this.mReadyFilter = arrayList;
        this.mDisconnectedReason = 0;
        this.mLastReadyState = DBG;
        this.mContext = context;
        this.mPhoneId = phoneId;
        this.mFactory = factory;
        this.mLogPrefix = logPrefix;
        arrayList.addAll(readyFilter);
        this.mListener = listener;
        this.mExecutor = executor;
    }

    public void connect() {
        if (!isSupported()) {
            this.mExecutor.execute(new Runnable() { // from class: com.android.ims.FeatureConnector$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FeatureConnector.this.lambda$connect$0();
                }
            });
            logw("connect: not supported.");
            return;
        }
        synchronized (this.mLock) {
            if (this.mManager == null) {
                this.mManager = this.mFactory.createManager(this.mContext, this.mPhoneId);
            }
        }
        this.mManager.registerFeatureCallback(this.mPhoneId, this.mCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$connect$0() {
        this.mListener.connectionUnavailable(2);
    }

    private boolean isSupported() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony.ims");
    }

    public void disconnect() {
        U manager;
        synchronized (this.mLock) {
            manager = this.mManager;
        }
        if (manager == null) {
            return;
        }
        manager.unregisterFeatureCallback(this.mCallback);
        try {
            this.mCallback.imsFeatureRemoved(0);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyReady(U manager, int subId) throws ImsException {
        try {
            this.mListener.connectionReady(manager, subId);
        } catch (ImsException e) {
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNotReady() {
        this.mListener.connectionUnavailable(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String message) {
        Rlog.d(TAG, "[" + this.mLogPrefix + ", " + this.mPhoneId + "] " + message);
    }

    private void logw(String message) {
        Rlog.w(TAG, "[" + this.mLogPrefix + ", " + this.mPhoneId + "] " + message);
    }
}
