package android.telephony.ims.aidl;

import android.net.Uri;
import android.p008os.RemoteException;
import android.telephony.ims.ImsException;
import android.telephony.ims.RcsContactTerminatedReason;
import android.telephony.ims.SipDetails;
import android.telephony.ims.stub.RcsCapabilityExchangeImplBase;
import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class RcsSubscribeResponseAidlWrapper implements RcsCapabilityExchangeImplBase.SubscribeResponseCallback {
    private final ISubscribeResponseCallback mResponseBinder;

    public RcsSubscribeResponseAidlWrapper(ISubscribeResponseCallback responseBinder) {
        this.mResponseBinder = responseBinder;
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    public void onCommandError(int code) throws ImsException {
        try {
            this.mResponseBinder.onCommandError(code);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    @Deprecated
    public void onNetworkResponse(int code, String reasonPhrase) throws ImsException {
        try {
            this.mResponseBinder.onNetworkResponse(new SipDetails.Builder(3).setSipResponseCode(code, reasonPhrase).build());
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    @Deprecated
    public void onNetworkResponse(int code, String reasonPhrase, int reasonHeaderCause, String reasonHeaderText) throws ImsException {
        try {
            this.mResponseBinder.onNetworkResponse(new SipDetails.Builder(3).setSipResponseCode(code, reasonPhrase).setSipResponseReasonHeader(reasonHeaderCause, reasonHeaderText).build());
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    public void onNetworkResponse(SipDetails details) throws ImsException {
        try {
            this.mResponseBinder.onNetworkResponse(details);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    public void onNotifyCapabilitiesUpdate(List<String> pidfXmls) throws ImsException {
        try {
            this.mResponseBinder.onNotifyCapabilitiesUpdate(pidfXmls);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    public void onResourceTerminated(List<Pair<Uri, String>> uriTerminatedReason) throws ImsException {
        try {
            this.mResponseBinder.onResourceTerminated(getTerminatedReasonList(uriTerminatedReason));
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    private List<RcsContactTerminatedReason> getTerminatedReasonList(List<Pair<Uri, String>> uriTerminatedReason) {
        List<RcsContactTerminatedReason> uriTerminatedReasonList = new ArrayList<>();
        if (uriTerminatedReason != null) {
            for (Pair<Uri, String> pair : uriTerminatedReason) {
                RcsContactTerminatedReason reason = new RcsContactTerminatedReason(pair.first, pair.second);
                uriTerminatedReasonList.add(reason);
            }
        }
        return uriTerminatedReasonList;
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.SubscribeResponseCallback
    public void onTerminated(String reason, long retryAfterMilliseconds) throws ImsException {
        try {
            this.mResponseBinder.onTerminated(reason, retryAfterMilliseconds);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }
}
