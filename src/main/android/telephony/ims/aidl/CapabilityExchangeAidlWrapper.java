package android.telephony.ims.aidl;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.telephony.ims.ImsException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import android.telephony.ims.stub.CapabilityExchangeEventListener;
import android.util.Log;
import java.util.ArrayList;
import java.util.Set;
/* loaded from: classes3.dex */
public class CapabilityExchangeAidlWrapper implements CapabilityExchangeEventListener {
    private static final String LOG_TAG = "CapExchangeListener";
    private final ICapabilityExchangeEventListener mListenerBinder;

    public CapabilityExchangeAidlWrapper(ICapabilityExchangeEventListener listener) {
        this.mListenerBinder = listener;
    }

    @Override // android.telephony.ims.stub.CapabilityExchangeEventListener
    public void onRequestPublishCapabilities(int publishTriggerType) throws ImsException {
        ICapabilityExchangeEventListener listener = this.mListenerBinder;
        if (listener == null) {
            return;
        }
        try {
            listener.onRequestPublishCapabilities(publishTriggerType);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "request publish capabilities exception: " + e);
            throw new ImsException("Remote is not available", 1);
        }
    }

    @Override // android.telephony.ims.stub.CapabilityExchangeEventListener
    public void onUnpublish() throws ImsException {
        ICapabilityExchangeEventListener listener = this.mListenerBinder;
        if (listener == null) {
            return;
        }
        try {
            listener.onUnpublish();
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Unpublish exception: " + e);
            throw new ImsException("Remote is not available", 1);
        }
    }

    @Override // android.telephony.ims.stub.CapabilityExchangeEventListener
    @Deprecated
    public void onPublishUpdated(int reasonCode, String reasonPhrase, int reasonHeaderCause, String reasonHeaderText) throws ImsException {
        ICapabilityExchangeEventListener listener = this.mListenerBinder;
        if (listener == null) {
            return;
        }
        try {
            SipDetails details = new SipDetails.Builder(2).setSipResponseCode(reasonCode, reasonPhrase).setSipResponseReasonHeader(reasonHeaderCause, reasonHeaderText).build();
            listener.onPublishUpdated(details);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "onPublishUpdated exception: " + e);
            throw new ImsException("Remote is not available", 1);
        }
    }

    @Override // android.telephony.ims.stub.CapabilityExchangeEventListener
    public void onPublishUpdated(SipDetails details) throws ImsException {
        ICapabilityExchangeEventListener listener = this.mListenerBinder;
        if (listener == null) {
            return;
        }
        try {
            listener.onPublishUpdated(details);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "onPublishUpdated exception: " + e);
            throw new ImsException("Remote is not available", 1);
        }
    }

    @Override // android.telephony.ims.stub.CapabilityExchangeEventListener
    public void onRemoteCapabilityRequest(Uri contactUri, Set<String> remoteCapabilities, final CapabilityExchangeEventListener.OptionsRequestCallback callback) throws ImsException {
        ICapabilityExchangeEventListener listener = this.mListenerBinder;
        if (listener == null) {
            return;
        }
        IOptionsRequestCallback internalCallback = new IOptionsRequestCallback.Stub() { // from class: android.telephony.ims.aidl.CapabilityExchangeAidlWrapper.1
            @Override // android.telephony.ims.aidl.IOptionsRequestCallback
            public void respondToCapabilityRequest(RcsContactUceCapability ownCapabilities, boolean isBlocked) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    callback.onRespondToCapabilityRequest(ownCapabilities, isBlocked);
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            @Override // android.telephony.ims.aidl.IOptionsRequestCallback
            public void respondToCapabilityRequestWithError(int code, String reason) {
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    callback.onRespondToCapabilityRequestWithError(code, reason);
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }
        };
        try {
            listener.onRemoteCapabilityRequest(contactUri, new ArrayList(remoteCapabilities), internalCallback);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote capability request exception: " + e);
            throw new ImsException("Remote is not available", 1);
        }
    }
}
