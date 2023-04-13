package android.telephony.ims.aidl;

import android.p008os.RemoteException;
import android.telephony.ims.ImsException;
import android.telephony.ims.SipDetails;
import android.telephony.ims.stub.RcsCapabilityExchangeImplBase;
/* loaded from: classes3.dex */
public class RcsPublishResponseAidlWrapper implements RcsCapabilityExchangeImplBase.PublishResponseCallback {
    private final IPublishResponseCallback mResponseBinder;

    public RcsPublishResponseAidlWrapper(IPublishResponseCallback responseBinder) {
        this.mResponseBinder = responseBinder;
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.PublishResponseCallback
    public void onCommandError(int code) throws ImsException {
        try {
            this.mResponseBinder.onCommandError(code);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.PublishResponseCallback
    @Deprecated
    public void onNetworkResponse(int code, String reasonPhrase) throws ImsException {
        SipDetails details = new SipDetails.Builder(2).setSipResponseCode(code, reasonPhrase).build();
        try {
            this.mResponseBinder.onNetworkResponse(details);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.PublishResponseCallback
    @Deprecated
    public void onNetworkResponse(int code, String reasonPhrase, int reasonHeaderCause, String reasonHeaderText) throws ImsException {
        SipDetails details = new SipDetails.Builder(2).setSipResponseCode(code, reasonPhrase).setSipResponseReasonHeader(reasonHeaderCause, reasonHeaderText).build();
        try {
            this.mResponseBinder.onNetworkResponse(details);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.PublishResponseCallback
    public void onNetworkResponse(SipDetails details) throws ImsException {
        try {
            this.mResponseBinder.onNetworkResponse(details);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }
}
