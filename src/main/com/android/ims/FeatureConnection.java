package com.android.ims;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.feature.ImsFeature;
import android.util.Log;
import java.util.NoSuchElementException;
/* loaded from: classes.dex */
public abstract class FeatureConnection {
    protected static final String TAG = "FeatureConnection";
    protected static boolean sImsSupportedOnDevice = true;
    protected IBinder mBinder;
    private final IImsConfig mConfigBinder;
    protected Context mContext;
    protected long mFeatureCapabilities;
    private final IImsRegistration mRegistrationBinder;
    private final ISipTransport mSipTransportBinder;
    protected final int mSlotId;
    protected final int mSubId;
    protected volatile boolean mIsAvailable = true;
    protected Integer mFeatureStateCached = null;
    protected final Object mLock = new Object();
    protected final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.ims.FeatureConnection$$ExternalSyntheticLambda1
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            FeatureConnection.this.lambda$new$0();
        }
    };

    protected abstract void onFeatureCapabilitiesUpdated(long j);

    protected abstract Integer retrieveFeatureState();

    public FeatureConnection(Context context, int slotId, int subId, IImsConfig c, IImsRegistration r, ISipTransport s) {
        this.mSlotId = slotId;
        this.mSubId = subId;
        this.mContext = context;
        this.mRegistrationBinder = r;
        this.mConfigBinder = c;
        this.mSipTransportBinder = s;
    }

    protected TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.mContext.getSystemService("phone");
    }

    public void setBinder(IBinder binder) {
        synchronized (this.mLock) {
            this.mBinder = binder;
            if (binder != null) {
                try {
                    binder.linkToDeath(this.mDeathRecipient, 0);
                } catch (RemoteException e) {
                    Log.w(TAG, "setBinder: linkToDeath on already dead Binder, setting null");
                    this.mBinder = null;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Log.w(TAG, "DeathRecipient triggered, binder died.");
        if (this.mContext != null && Looper.getMainLooper() != null) {
            this.mContext.getMainExecutor().execute(new Runnable() { // from class: com.android.ims.FeatureConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FeatureConnection.this.onRemovedOrDied();
                }
            });
        } else {
            onRemovedOrDied();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRemovedOrDied() {
        synchronized (this.mLock) {
            if (this.mIsAvailable) {
                this.mIsAvailable = false;
                try {
                    IBinder iBinder = this.mBinder;
                    if (iBinder != null) {
                        iBinder.unlinkToDeath(this.mDeathRecipient, 0);
                    }
                } catch (NoSuchElementException e) {
                    Log.w(TAG, "onRemovedOrDied: unlinkToDeath called on unlinked Binder.");
                }
            }
        }
    }

    public int getRegistrationTech() throws RemoteException {
        IImsRegistration registration = getRegistration();
        if (registration != null) {
            return registration.getRegistrationTechnology();
        }
        Log.w(TAG, "getRegistrationTech: ImsRegistration is null");
        return -1;
    }

    public IImsRegistration getRegistration() {
        return this.mRegistrationBinder;
    }

    public IImsConfig getConfig() {
        return this.mConfigBinder;
    }

    public ISipTransport getSipTransport() {
        return this.mSipTransportBinder;
    }

    public void checkServiceIsReady() throws RemoteException {
        if (!sImsSupportedOnDevice) {
            throw new RemoteException("IMS is not supported on this device.");
        }
        if (!isBinderReady()) {
            throw new RemoteException("ImsServiceProxy is not ready to accept commands.");
        }
    }

    public boolean isBinderReady() {
        return isBinderAlive() && getFeatureState() == 2;
    }

    public boolean isBinderAlive() {
        IBinder iBinder;
        return this.mIsAvailable && (iBinder = this.mBinder) != null && iBinder.isBinderAlive();
    }

    public void updateFeatureState(int state) {
        synchronized (this.mLock) {
            this.mFeatureStateCached = Integer.valueOf(state);
        }
    }

    public long getFeatureCapabilties() {
        long j;
        synchronized (this.mLock) {
            j = this.mFeatureCapabilities;
        }
        return j;
    }

    public void updateFeatureCapabilities(long caps) {
        synchronized (this.mLock) {
            if (this.mFeatureCapabilities != caps) {
                this.mFeatureCapabilities = caps;
                onFeatureCapabilitiesUpdated(caps);
            }
        }
    }

    public boolean isCapable(long capabilities) throws RemoteException {
        if (isBinderAlive()) {
            return (getFeatureCapabilties() & capabilities) > 0;
        }
        throw new RemoteException("isCapable: ImsService is not alive");
    }

    public int getFeatureState() {
        Integer num;
        synchronized (this.mLock) {
            if (isBinderAlive() && (num = this.mFeatureStateCached) != null) {
                return num.intValue();
            }
            Integer state = retrieveFeatureState();
            synchronized (this.mLock) {
                if (state == null) {
                    return 0;
                }
                this.mFeatureStateCached = state;
                Log.i("FeatureConnection [" + this.mSlotId + "]", "getFeatureState - returning " + ((String) ImsFeature.STATE_LOG_MAP.get(state)));
                return state.intValue();
            }
        }
    }

    public int getSubId() {
        return this.mSubId;
    }
}
