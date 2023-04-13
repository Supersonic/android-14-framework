package com.android.ims;

import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.ims.aidl.ICapabilityExchangeEventListener;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRcsFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import android.telephony.ims.aidl.IPublishResponseCallback;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import com.android.telephony.Rlog;
import java.util.List;
/* loaded from: classes.dex */
public class RcsFeatureConnection extends FeatureConnection {
    private static final String TAG = "RcsFeatureConnection";
    public AvailabilityCallbackManager mAvailabilityCallbackManager;
    public RegistrationCallbackManager mRegistrationCallbackManager;

    /* loaded from: classes.dex */
    public class AvailabilityCallbackManager extends ImsCallbackAdapterManager<IImsCapabilityCallback> {
        AvailabilityCallbackManager(Context context) {
            super(context, new Object(), RcsFeatureConnection.this.mSlotId, RcsFeatureConnection.this.mSubId);
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void registerCallback(IImsCapabilityCallback localCallback) {
            try {
                RcsFeatureConnection.this.addCapabilityCallback(localCallback);
            } catch (RemoteException e) {
                RcsFeatureConnection.this.loge("Register capability callback error: " + e);
                throw new IllegalStateException(" CapabilityCallbackManager: Register callback error");
            }
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void unregisterCallback(IImsCapabilityCallback localCallback) {
            try {
                RcsFeatureConnection.this.removeCapabilityCallback(localCallback);
            } catch (RemoteException e) {
                RcsFeatureConnection.this.loge("Cannot remove capability callback: " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RegistrationCallbackManager extends ImsCallbackAdapterManager<IImsRegistrationCallback> {
        public RegistrationCallbackManager(Context context) {
            super(context, new Object(), RcsFeatureConnection.this.mSlotId, RcsFeatureConnection.this.mSubId);
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void registerCallback(IImsRegistrationCallback localCallback) {
            IImsRegistration imsRegistration = RcsFeatureConnection.this.getRegistration();
            if (imsRegistration == null) {
                RcsFeatureConnection.this.loge("Register IMS registration callback: ImsRegistration is null");
                throw new IllegalStateException("RegistrationCallbackAdapter: RcsFeature is not available!");
            }
            try {
                imsRegistration.addRegistrationCallback(localCallback);
            } catch (RemoteException e) {
                throw new IllegalStateException("RegistrationCallbackAdapter: RcsFeature binder is dead.");
            }
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void unregisterCallback(IImsRegistrationCallback localCallback) {
            IImsRegistration imsRegistration = RcsFeatureConnection.this.getRegistration();
            if (imsRegistration == null) {
                RcsFeatureConnection.this.logi("Unregister IMS registration callback: ImsRegistration is null");
                return;
            }
            try {
                imsRegistration.removeRegistrationCallback(localCallback);
            } catch (RemoteException e) {
                RcsFeatureConnection.this.loge("Cannot remove registration callback: " + e);
            }
        }
    }

    public RcsFeatureConnection(Context context, int slotId, int subId, IImsRcsFeature feature, IImsConfig c, IImsRegistration r, ISipTransport s) {
        super(context, slotId, subId, c, r, s);
        setBinder(feature != null ? feature.asBinder() : null);
        this.mAvailabilityCallbackManager = new AvailabilityCallbackManager(this.mContext);
        this.mRegistrationCallbackManager = new RegistrationCallbackManager(this.mContext);
    }

    public void close() {
        removeCapabilityExchangeEventListener();
        this.mAvailabilityCallbackManager.close();
        this.mRegistrationCallbackManager.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.ims.FeatureConnection
    public void onRemovedOrDied() {
        close();
        super.onRemovedOrDied();
    }

    public void setCapabilityExchangeEventListener(ICapabilityExchangeEventListener listener) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsAlive();
            getServiceInterface(this.mBinder).setCapabilityExchangeEventListener(listener);
        }
    }

    public void removeCapabilityExchangeEventListener() {
        try {
            setCapabilityExchangeEventListener(null);
        } catch (RemoteException e) {
        }
    }

    private void checkServiceIsAlive() throws RemoteException {
        if (!sImsSupportedOnDevice) {
            throw new RemoteException("IMS is not supported on this device.");
        }
        if (!isBinderAlive()) {
            throw new RemoteException("ImsServiceProxy is not alive.");
        }
    }

    public int queryCapabilityStatus() throws RemoteException {
        int queryCapabilityStatus;
        synchronized (this.mLock) {
            checkServiceIsReady();
            queryCapabilityStatus = getServiceInterface(this.mBinder).queryCapabilityStatus();
        }
        return queryCapabilityStatus;
    }

    public void addCallbackForSubscription(int subId, IImsCapabilityCallback cb) {
        this.mAvailabilityCallbackManager.addCallbackForSubscription(cb, subId);
    }

    public void addCallbackForSubscription(int subId, IImsRegistrationCallback cb) {
        this.mRegistrationCallbackManager.addCallbackForSubscription(cb, subId);
    }

    public void addCallback(IImsRegistrationCallback cb) {
        this.mRegistrationCallbackManager.addCallback(cb);
    }

    public void removeCallbackForSubscription(int subId, IImsCapabilityCallback cb) {
        this.mAvailabilityCallbackManager.removeCallback(cb);
    }

    public void removeCallbackForSubscription(int subId, IImsRegistrationCallback cb) {
        this.mRegistrationCallbackManager.removeCallback(cb);
    }

    public void removeCallback(IImsRegistrationCallback cb) {
        this.mRegistrationCallbackManager.removeCallback(cb);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCapabilityCallback(IImsCapabilityCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).addCapabilityCallback(callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeCapabilityCallback(IImsCapabilityCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).removeCapabilityCallback(callback);
        }
    }

    public void queryCapabilityConfiguration(int capability, int radioTech, IImsCapabilityCallback c) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).queryCapabilityConfiguration(capability, radioTech, c);
        }
    }

    public void changeEnabledCapabilities(CapabilityChangeRequest request, IImsCapabilityCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).changeCapabilitiesConfiguration(request, callback);
        }
    }

    public void requestPublication(String pidfXml, IPublishResponseCallback responseCallback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).publishCapabilities(pidfXml, responseCallback);
        }
    }

    public void requestCapabilities(List<Uri> uris, ISubscribeResponseCallback c) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).subscribeForCapabilities(uris, c);
        }
    }

    public void sendOptionsCapabilityRequest(Uri contactUri, List<String> myCapabilities, IOptionsResponseCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).sendOptionsCapabilityRequest(contactUri, myCapabilities, callback);
        }
    }

    @Override // com.android.ims.FeatureConnection
    public Integer retrieveFeatureState() {
        if (this.mBinder != null) {
            try {
                return Integer.valueOf(getServiceInterface(this.mBinder).getFeatureState());
            } catch (RemoteException e) {
                return null;
            }
        }
        return null;
    }

    @Override // com.android.ims.FeatureConnection
    public void onFeatureCapabilitiesUpdated(long capabilities) {
    }

    public IImsRcsFeature getServiceInterface(IBinder b) {
        return IImsRcsFeature.Stub.asInterface(b);
    }

    private void log(String s) {
        Rlog.d("RcsFeatureConnection [" + this.mSlotId + "]", s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String s) {
        Rlog.i("RcsFeatureConnection [" + this.mSlotId + "]", s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s) {
        Rlog.e("RcsFeatureConnection [" + this.mSlotId + "]", s);
    }
}
