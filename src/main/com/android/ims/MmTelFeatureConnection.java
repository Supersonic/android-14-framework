package com.android.ims;

import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.MediaThreshold;
import android.telephony.ims.RtpHeaderExtensionType;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsConfigCallback;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsMmTelListener;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.aidl.IImsSmsListener;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.aidl.ISrvccStartedCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import android.telephony.ims.feature.MmTelFeature;
import android.util.Log;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.ims.internal.IImsUt;
import java.util.ArrayList;
import java.util.Set;
/* loaded from: classes.dex */
public class MmTelFeatureConnection extends FeatureConnection {
    protected static final String TAG = "MmTelFeatureConn";
    private final CapabilityCallbackManager mCapabilityCallbackManager;
    private BinderAccessState<ImsEcbm> mEcbm;
    private MmTelFeature.Listener mMmTelFeatureListener;
    private BinderAccessState<ImsMultiEndpoint> mMultiEndpoint;
    private final ProvisioningCallbackManager mProvisioningCallbackManager;
    private final ImsRegistrationCallbackAdapter mRegistrationCallbackManager;
    private boolean mSupportsEmergencyCalling;
    private ImsUt mUt;

    /* loaded from: classes.dex */
    private class ImsRegistrationCallbackAdapter extends ImsCallbackAdapterManager<IImsRegistrationCallback> {
        public ImsRegistrationCallbackAdapter(Context context, Object lock) {
            super(context, lock, MmTelFeatureConnection.this.mSlotId, MmTelFeatureConnection.this.mSubId);
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void registerCallback(IImsRegistrationCallback localCallback) {
            IImsRegistration imsRegistration = MmTelFeatureConnection.this.getRegistration();
            if (imsRegistration != null) {
                try {
                    imsRegistration.addRegistrationCallback(localCallback);
                    return;
                } catch (RemoteException e) {
                    throw new IllegalStateException("ImsRegistrationCallbackAdapter: MmTelFeature binder is dead.");
                }
            }
            Log.e("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ImsRegistrationCallbackAdapter: ImsRegistration is null");
            throw new IllegalStateException("ImsRegistrationCallbackAdapter: MmTelFeature isnot available!");
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void unregisterCallback(IImsRegistrationCallback localCallback) {
            IImsRegistration imsRegistration = MmTelFeatureConnection.this.getRegistration();
            if (imsRegistration == null) {
                Log.e("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ImsRegistrationCallbackAdapter: ImsRegistration is null");
                return;
            }
            try {
                imsRegistration.removeRegistrationCallback(localCallback);
            } catch (RemoteException | IllegalStateException e) {
                Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ImsRegistrationCallbackAdapter - unregisterCallback: couldn't remove registration callback Exception: " + e.getMessage());
            }
        }
    }

    /* loaded from: classes.dex */
    private class CapabilityCallbackManager extends ImsCallbackAdapterManager<IImsCapabilityCallback> {
        public CapabilityCallbackManager(Context context, Object lock) {
            super(context, lock, MmTelFeatureConnection.this.mSlotId, MmTelFeatureConnection.this.mSubId);
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void registerCallback(IImsCapabilityCallback localCallback) {
            IImsMmTelFeature binder;
            synchronized (MmTelFeatureConnection.this.mLock) {
                try {
                    MmTelFeatureConnection.this.checkServiceIsReady();
                    MmTelFeatureConnection mmTelFeatureConnection = MmTelFeatureConnection.this;
                    binder = mmTelFeatureConnection.getServiceInterface(mmTelFeatureConnection.mBinder);
                } catch (RemoteException e) {
                    throw new IllegalStateException("CapabilityCallbackManager - MmTelFeature binder is dead.");
                }
            }
            if (binder != null) {
                try {
                    binder.addCapabilityCallback(localCallback);
                    return;
                } catch (RemoteException e2) {
                    throw new IllegalStateException(" CapabilityCallbackManager - MmTelFeature binder is null.");
                }
            }
            Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "CapabilityCallbackManager, register: Couldn't get binder");
            throw new IllegalStateException("CapabilityCallbackManager: MmTelFeature is not available!");
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void unregisterCallback(IImsCapabilityCallback localCallback) {
            synchronized (MmTelFeatureConnection.this.mLock) {
                if (!MmTelFeatureConnection.this.isBinderAlive()) {
                    Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "CapabilityCallbackManager, unregister: binder is not alive");
                    return;
                }
                MmTelFeatureConnection mmTelFeatureConnection = MmTelFeatureConnection.this;
                IImsMmTelFeature binder = mmTelFeatureConnection.getServiceInterface(mmTelFeatureConnection.mBinder);
                if (binder != null) {
                    try {
                        binder.removeCapabilityCallback(localCallback);
                        return;
                    } catch (RemoteException | IllegalStateException e) {
                        Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "CapabilityCallbackManager, unregister: Binder is dead. Exception: " + e.getMessage());
                        return;
                    }
                }
                Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "CapabilityCallbackManager, unregister: binder is null.");
            }
        }
    }

    /* loaded from: classes.dex */
    private class ProvisioningCallbackManager extends ImsCallbackAdapterManager<IImsConfigCallback> {
        public ProvisioningCallbackManager(Context context, Object lock) {
            super(context, lock, MmTelFeatureConnection.this.mSlotId, MmTelFeatureConnection.this.mSubId);
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void registerCallback(IImsConfigCallback localCallback) {
            IImsConfig binder = MmTelFeatureConnection.this.getConfig();
            if (binder == null) {
                Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ProvisioningCallbackManager - couldn't register, binder is null.");
                throw new IllegalStateException("ImsConfig is not available!");
            }
            try {
                binder.addImsConfigCallback(localCallback);
            } catch (RemoteException e) {
                throw new IllegalStateException("ImsService is not available!");
            }
        }

        @Override // com.android.ims.ImsCallbackAdapterManager
        public void unregisterCallback(IImsConfigCallback localCallback) {
            IImsConfig binder = MmTelFeatureConnection.this.getConfig();
            if (binder == null) {
                Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ProvisioningCallbackManager - couldn't unregister, binder is null.");
                return;
            }
            try {
                binder.removeImsConfigCallback(localCallback);
            } catch (RemoteException | IllegalStateException e) {
                Log.w("MmTelFeatureConn [" + MmTelFeatureConnection.this.mSlotId + "]", "ProvisioningCallbackManager - couldn't unregister, binder is dead. Exception: " + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class BinderAccessState<T> {
        static final int STATE_AVAILABLE = 2;
        static final int STATE_NOT_SET = 0;
        static final int STATE_NOT_SUPPORTED = 1;
        private final T mInterface;
        private final int mState;

        /* renamed from: of */
        public static <T> BinderAccessState<T> m0of(T value) {
            return new BinderAccessState<>(value);
        }

        public BinderAccessState(int state) {
            this.mState = state;
            this.mInterface = null;
        }

        public BinderAccessState(T binderInterface) {
            this.mState = 2;
            this.mInterface = binderInterface;
        }

        public int getState() {
            return this.mState;
        }

        public T getInterface() {
            return this.mInterface;
        }
    }

    public MmTelFeatureConnection(Context context, int slotId, int subId, IImsMmTelFeature f, IImsConfig c, IImsRegistration r, ISipTransport s) {
        super(context, slotId, subId, c, r, s);
        this.mSupportsEmergencyCalling = false;
        this.mEcbm = new BinderAccessState<>(0);
        this.mMultiEndpoint = new BinderAccessState<>(0);
        setBinder(f != null ? f.asBinder() : null);
        this.mRegistrationCallbackManager = new ImsRegistrationCallbackAdapter(context, this.mLock);
        this.mCapabilityCallbackManager = new CapabilityCallbackManager(context, this.mLock);
        this.mProvisioningCallbackManager = new ProvisioningCallbackManager(context, this.mLock);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.ims.FeatureConnection
    public void onRemovedOrDied() {
        this.mRegistrationCallbackManager.close();
        this.mCapabilityCallbackManager.close();
        this.mProvisioningCallbackManager.close();
        synchronized (this.mLock) {
            ImsUt imsUt = this.mUt;
            if (imsUt != null) {
                imsUt.close();
                this.mUt = null;
            }
            closeConnection();
            super.onRemovedOrDied();
        }
    }

    public boolean isEmergencyMmTelAvailable() {
        return this.mSupportsEmergencyCalling;
    }

    public void openConnection(MmTelFeature.Listener mmTelListener, ImsEcbmStateListener ecbmListener, ImsExternalCallStateListener multiEndpointListener) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            this.mMmTelFeatureListener = mmTelListener;
            getServiceInterface(this.mBinder).setListener(mmTelListener);
            setEcbmInterface(ecbmListener);
            setMultiEndpointInterface(multiEndpointListener);
        }
    }

    public void closeConnection() {
        synchronized (this.mLock) {
            if (isBinderAlive()) {
                try {
                    if (this.mMmTelFeatureListener != null) {
                        this.mMmTelFeatureListener = null;
                        getServiceInterface(this.mBinder).setListener((IImsMmTelListener) null);
                    }
                    if (this.mEcbm.getState() == 2) {
                        this.mEcbm.getInterface().setEcbmStateListener(null);
                        this.mEcbm = new BinderAccessState<>(0);
                    }
                    if (this.mMultiEndpoint.getState() == 2) {
                        this.mMultiEndpoint.getInterface().setExternalCallStateListener(null);
                        this.mMultiEndpoint = new BinderAccessState<>(0);
                    }
                } catch (RemoteException | IllegalStateException e) {
                    Log.w("MmTelFeatureConn [" + this.mSlotId + "]", "closeConnection: couldn't remove listeners! Exception: " + e.getMessage());
                }
            }
        }
    }

    public void addRegistrationCallback(IImsRegistrationCallback callback) {
        this.mRegistrationCallbackManager.addCallback(callback);
    }

    public void addRegistrationCallbackForSubscription(IImsRegistrationCallback callback, int subId) {
        this.mRegistrationCallbackManager.addCallbackForSubscription(callback, subId);
    }

    public void removeRegistrationCallback(IImsRegistrationCallback callback) {
        this.mRegistrationCallbackManager.removeCallback(callback);
    }

    public void removeRegistrationCallbackForSubscription(IImsRegistrationCallback callback, int subId) {
        this.mRegistrationCallbackManager.removeCallback(callback);
    }

    public void addCapabilityCallback(IImsCapabilityCallback callback) {
        this.mCapabilityCallbackManager.addCallback(callback);
    }

    public void addCapabilityCallbackForSubscription(IImsCapabilityCallback callback, int subId) {
        this.mCapabilityCallbackManager.addCallbackForSubscription(callback, subId);
    }

    public void removeCapabilityCallback(IImsCapabilityCallback callback) {
        this.mCapabilityCallbackManager.removeCallback(callback);
    }

    public void removeCapabilityCallbackForSubscription(IImsCapabilityCallback callback, int subId) {
        this.mCapabilityCallbackManager.removeCallback(callback);
    }

    public void addProvisioningCallbackForSubscription(IImsConfigCallback callback, int subId) {
        this.mProvisioningCallbackManager.addCallbackForSubscription(callback, subId);
    }

    public void removeProvisioningCallbackForSubscription(IImsConfigCallback callback, int subId) {
        this.mProvisioningCallbackManager.removeCallback(callback);
    }

    public void setMediaThreshold(int sessionType, MediaThreshold threshold) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).setMediaQualityThreshold(sessionType, threshold);
        }
    }

    public MediaQualityStatus queryMediaQualityStatus(int sessionType) throws RemoteException {
        MediaQualityStatus queryMediaQualityStatus;
        synchronized (this.mLock) {
            checkServiceIsReady();
            queryMediaQualityStatus = getServiceInterface(this.mBinder).queryMediaQualityStatus(sessionType);
        }
        return queryMediaQualityStatus;
    }

    public void changeEnabledCapabilities(CapabilityChangeRequest request, IImsCapabilityCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).changeCapabilitiesConfiguration(request, callback);
        }
    }

    public void queryEnabledCapabilities(int capability, int radioTech, IImsCapabilityCallback callback) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).queryCapabilityConfiguration(capability, radioTech, callback);
        }
    }

    public MmTelFeature.MmTelCapabilities queryCapabilityStatus() throws RemoteException {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        synchronized (this.mLock) {
            checkServiceIsReady();
            mmTelCapabilities = new MmTelFeature.MmTelCapabilities(getServiceInterface(this.mBinder).queryCapabilityStatus());
        }
        return mmTelCapabilities;
    }

    public ImsCallProfile createCallProfile(int callServiceType, int callType) throws RemoteException {
        ImsCallProfile createCallProfile;
        synchronized (this.mLock) {
            checkServiceIsReady();
            createCallProfile = getServiceInterface(this.mBinder).createCallProfile(callServiceType, callType);
        }
        return createCallProfile;
    }

    public void changeOfferedRtpHeaderExtensionTypes(Set<RtpHeaderExtensionType> types) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).changeOfferedRtpHeaderExtensionTypes(new ArrayList(types));
        }
    }

    public IImsCallSession createCallSession(ImsCallProfile profile) throws RemoteException {
        IImsCallSession createCallSession;
        synchronized (this.mLock) {
            checkServiceIsReady();
            createCallSession = getServiceInterface(this.mBinder).createCallSession(profile);
        }
        return createCallSession;
    }

    public ImsUt createOrGetUtInterface() throws RemoteException {
        synchronized (this.mLock) {
            ImsUt imsUt = this.mUt;
            if (imsUt != null) {
                return imsUt;
            }
            checkServiceIsReady();
            IImsUt imsUt2 = getServiceInterface(this.mBinder).getUtInterface();
            ImsUt imsUt3 = imsUt2 != null ? new ImsUt(imsUt2, this.mContext.getMainExecutor()) : null;
            this.mUt = imsUt3;
            return imsUt3;
        }
    }

    private void setEcbmInterface(ImsEcbmStateListener ecbmListener) throws RemoteException {
        synchronized (this.mLock) {
            if (this.mEcbm.getState() != 0) {
                throw new IllegalStateException("ECBM interface already open");
            }
            checkServiceIsReady();
            IImsEcbm imsEcbm = getServiceInterface(this.mBinder).getEcbmInterface();
            BinderAccessState<ImsEcbm> m0of = imsEcbm != null ? BinderAccessState.m0of(new ImsEcbm(imsEcbm)) : new BinderAccessState<>(1);
            this.mEcbm = m0of;
            if (m0of.getState() == 2) {
                this.mEcbm.getInterface().setEcbmStateListener(ecbmListener);
            }
        }
    }

    public ImsEcbm getEcbmInterface() {
        ImsEcbm imsEcbm;
        synchronized (this.mLock) {
            if (this.mEcbm.getState() == 0) {
                throw new IllegalStateException("ECBM interface has not been opened");
            }
            imsEcbm = this.mEcbm.getState() == 2 ? this.mEcbm.getInterface() : null;
        }
        return imsEcbm;
    }

    public void setUiTTYMode(int uiTtyMode, Message onComplete) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).setUiTtyMode(uiTtyMode, onComplete);
        }
    }

    private void setMultiEndpointInterface(ImsExternalCallStateListener listener) throws RemoteException {
        BinderAccessState<ImsMultiEndpoint> binderAccessState;
        synchronized (this.mLock) {
            if (this.mMultiEndpoint.getState() != 0) {
                throw new IllegalStateException("multiendpoint interface is already open");
            }
            checkServiceIsReady();
            IImsMultiEndpoint imEndpoint = getServiceInterface(this.mBinder).getMultiEndpointInterface();
            if (imEndpoint != null) {
                binderAccessState = BinderAccessState.m0of(new ImsMultiEndpoint(imEndpoint));
            } else {
                binderAccessState = new BinderAccessState<>(1);
            }
            this.mMultiEndpoint = binderAccessState;
            if (binderAccessState.getState() == 2) {
                this.mMultiEndpoint.getInterface().setExternalCallStateListener(listener);
            }
        }
    }

    public void sendSms(int token, int messageRef, String format, String smsc, boolean isRetry, byte[] pdu) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).sendSms(token, messageRef, format, smsc, isRetry, pdu);
        }
    }

    public void onMemoryAvailable(int token) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).onMemoryAvailable(token);
        }
    }

    public void acknowledgeSms(int token, int messageRef, int result) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).acknowledgeSms(token, messageRef, result);
        }
    }

    public void acknowledgeSms(int token, int messageRef, int result, byte[] pdu) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).acknowledgeSmsWithPdu(token, messageRef, result, pdu);
        }
    }

    public void acknowledgeSmsReport(int token, int messageRef, int result) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).acknowledgeSmsReport(token, messageRef, result);
        }
    }

    public String getSmsFormat() throws RemoteException {
        String smsFormat;
        synchronized (this.mLock) {
            checkServiceIsReady();
            smsFormat = getServiceInterface(this.mBinder).getSmsFormat();
        }
        return smsFormat;
    }

    public void onSmsReady() throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).onSmsReady();
        }
    }

    public void setSmsListener(IImsSmsListener listener) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).setSmsListener(listener);
        }
    }

    public void notifySrvccStarted(ISrvccStartedCallback cb) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).notifySrvccStarted(cb);
        }
    }

    public void notifySrvccCompleted() throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).notifySrvccCompleted();
        }
    }

    public void notifySrvccFailed() throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).notifySrvccFailed();
        }
    }

    public void notifySrvccCanceled() throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).notifySrvccCanceled();
        }
    }

    public void triggerDeregistration(int reason) throws RemoteException {
        IImsRegistration registration = getRegistration();
        if (registration != null) {
            registration.triggerDeregistration(reason);
        } else {
            Log.e("MmTelFeatureConn [" + this.mSlotId + "]", "triggerDeregistration IImsRegistration is null");
        }
    }

    public int shouldProcessCall(boolean isEmergency, String[] numbers) throws RemoteException {
        int shouldProcessCall;
        if (isEmergency && !isEmergencyMmTelAvailable()) {
            Log.i("MmTelFeatureConn [" + this.mSlotId + "]", "MmTel does not support emergency over IMS, fallback to CS.");
            return 1;
        }
        synchronized (this.mLock) {
            checkServiceIsReady();
            shouldProcessCall = getServiceInterface(this.mBinder).shouldProcessCall(numbers);
        }
        return shouldProcessCall;
    }

    @Override // com.android.ims.FeatureConnection
    protected Integer retrieveFeatureState() {
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
        synchronized (this.mLock) {
            this.mSupportsEmergencyCalling = (1 | capabilities) > 0;
        }
    }

    public void setTerminalBasedCallWaitingStatus(boolean enabled) throws RemoteException {
        synchronized (this.mLock) {
            checkServiceIsReady();
            getServiceInterface(this.mBinder).setTerminalBasedCallWaitingStatus(enabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsMmTelFeature getServiceInterface(IBinder b) {
        return IImsMmTelFeature.Stub.asInterface(b);
    }
}
