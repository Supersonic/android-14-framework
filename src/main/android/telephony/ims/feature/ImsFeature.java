package android.telephony.ims.feature;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.IInterface;
import android.p008os.RemoteException;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.feature.ImsFeature;
import android.util.Log;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.internal.telephony.util.RemoteCallbackListExt;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class ImsFeature {
    @SystemApi
    public static final int CAPABILITY_ERROR_GENERIC = -1;
    @SystemApi
    public static final int CAPABILITY_SUCCESS = 0;
    @SystemApi
    public static final int FEATURE_EMERGENCY_MMTEL = 0;
    public static final int FEATURE_INVALID = -1;
    public static final int FEATURE_MAX = 3;
    @SystemApi
    public static final int FEATURE_MMTEL = 1;
    @SystemApi
    public static final int FEATURE_RCS = 2;
    private static final String LOG_TAG = "ImsFeature";
    @SystemApi
    public static final int STATE_INITIALIZING = 1;
    @SystemApi
    public static final int STATE_READY = 2;
    @SystemApi
    public static final int STATE_UNAVAILABLE = 0;
    protected Context mContext;
    public static final Map<Integer, String> FEATURE_LOG_MAP = Map.of(0, "EMERGENCY_MMTEL", 1, "MMTEL", 2, "RCS");
    public static final Map<Integer, String> STATE_LOG_MAP = Map.of(0, "UNAVAILABLE", 1, "INITIALIZING", 2, "READY");
    protected final Object mLock = new Object();
    private final RemoteCallbackListExt<IImsFeatureStatusCallback> mStatusCallbacks = new RemoteCallbackListExt<>();
    private int mState = 0;
    private int mSlotId = -1;
    private final RemoteCallbackListExt<IImsCapabilityCallback> mCapabilityCallbacks = new RemoteCallbackListExt<>();
    private Capabilities mCapabilityStatus = new Capabilities();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface FeatureType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsCapabilityError {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsState {
    }

    public abstract void changeEnabledCapabilities(CapabilityChangeRequest capabilityChangeRequest, CapabilityCallbackProxy capabilityCallbackProxy);

    protected abstract IInterface getBinder();

    public abstract void onFeatureReady();

    public abstract void onFeatureRemoved();

    public abstract boolean queryCapabilityConfiguration(int i, int i2);

    /* loaded from: classes3.dex */
    protected static class CapabilityCallbackProxy {
        private final IImsCapabilityCallback mCallback;

        public CapabilityCallbackProxy(IImsCapabilityCallback c) {
            this.mCallback = c;
        }

        public void onChangeCapabilityConfigurationError(int capability, int radioTech, int reason) {
            IImsCapabilityCallback iImsCapabilityCallback = this.mCallback;
            if (iImsCapabilityCallback == null) {
                return;
            }
            try {
                iImsCapabilityCallback.onChangeCapabilityConfigurationError(capability, radioTech, reason);
            } catch (RemoteException e) {
                Log.m110e(ImsFeature.LOG_TAG, "onChangeCapabilityConfigurationError called on dead binder.");
            }
        }
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    public static class Capabilities {
        protected int mCapabilities;

        public Capabilities() {
            this.mCapabilities = 0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Capabilities(int capabilities) {
            this.mCapabilities = 0;
            this.mCapabilities = capabilities;
        }

        public void addCapabilities(int capabilities) {
            this.mCapabilities |= capabilities;
        }

        public void removeCapabilities(int capabilities) {
            this.mCapabilities &= ~capabilities;
        }

        public boolean isCapable(int capabilities) {
            return (this.mCapabilities & capabilities) == capabilities;
        }

        public Capabilities copy() {
            return new Capabilities(this.mCapabilities);
        }

        public int getMask() {
            return this.mCapabilities;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Capabilities) {
                Capabilities that = (Capabilities) o;
                return this.mCapabilities == that.mCapabilities;
            }
            return false;
        }

        public int hashCode() {
            return this.mCapabilities;
        }

        public String toString() {
            return "Capabilities: " + Integer.toBinaryString(this.mCapabilities);
        }
    }

    public void initialize(Context context, int slotId) {
        this.mContext = context;
        this.mSlotId = slotId;
    }

    @SystemApi
    public final int getSlotIndex() {
        return this.mSlotId;
    }

    @SystemApi
    public int getFeatureState() {
        int i;
        synchronized (this.mLock) {
            i = this.mState;
        }
        return i;
    }

    @SystemApi
    public final void setFeatureState(int state) {
        boolean isNotify = false;
        synchronized (this.mLock) {
            if (this.mState != state) {
                this.mState = state;
                isNotify = true;
            }
        }
        if (isNotify) {
            notifyFeatureState(state);
        }
    }

    public void addImsFeatureStatusCallback(IImsFeatureStatusCallback c) {
        try {
            synchronized (this.mStatusCallbacks) {
                this.mStatusCallbacks.register(c);
                c.notifyImsFeatureStatus(getFeatureState());
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Couldn't notify feature state: " + e.getMessage());
        }
    }

    public void removeImsFeatureStatusCallback(IImsFeatureStatusCallback c) {
        synchronized (this.mStatusCallbacks) {
            this.mStatusCallbacks.unregister(c);
        }
    }

    private void notifyFeatureState(final int state) {
        synchronized (this.mStatusCallbacks) {
            this.mStatusCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.feature.ImsFeature$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ImsFeature.lambda$notifyFeatureState$0(state, (IImsFeatureStatusCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$notifyFeatureState$0(int state, IImsFeatureStatusCallback c) {
        try {
            c.notifyImsFeatureStatus(state);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + " notifyFeatureState() - Skipping callback.");
        }
    }

    public final void addCapabilityCallback(IImsCapabilityCallback c) {
        this.mCapabilityCallbacks.register(c);
        try {
            c.onCapabilitiesStatusChanged(queryCapabilityStatus().mCapabilities);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "addCapabilityCallback: error accessing callback: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void removeCapabilityCallback(IImsCapabilityCallback c) {
        this.mCapabilityCallbacks.unregister(c);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void queryCapabilityConfigurationInternal(int capability, int radioTech, IImsCapabilityCallback c) {
        boolean enabled = queryCapabilityConfiguration(capability, radioTech);
        if (c != null) {
            try {
                c.onQueryCapabilityConfiguration(capability, radioTech, enabled);
            } catch (RemoteException e) {
                Log.m110e(LOG_TAG, "queryCapabilityConfigurationInternal called on dead binder!");
            }
        }
    }

    public Capabilities queryCapabilityStatus() {
        Capabilities copy;
        synchronized (this.mLock) {
            copy = this.mCapabilityStatus.copy();
        }
        return copy;
    }

    public final void requestChangeEnabledCapabilities(CapabilityChangeRequest request, IImsCapabilityCallback c) {
        if (request == null) {
            throw new IllegalArgumentException("ImsFeature#requestChangeEnabledCapabilities called with invalid params.");
        }
        changeEnabledCapabilities(request, new CapabilityCallbackProxy(c));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void notifyCapabilitiesStatusChanged(final Capabilities caps) {
        synchronized (this.mLock) {
            this.mCapabilityStatus = caps.copy();
        }
        synchronized (this.mCapabilityCallbacks) {
            this.mCapabilityCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.feature.ImsFeature$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ImsFeature.lambda$notifyCapabilitiesStatusChanged$1(ImsFeature.Capabilities.this, (IImsCapabilityCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$notifyCapabilitiesStatusChanged$1(Capabilities caps, IImsCapabilityCallback callback) {
        try {
            Log.m112d(LOG_TAG, "ImsFeature notifyCapabilitiesStatusChanged Capabilities = " + caps.mCapabilities);
            callback.onCapabilitiesStatusChanged(caps.mCapabilities);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + " notifyCapabilitiesStatusChanged() - Skipping callback.");
        }
    }
}
