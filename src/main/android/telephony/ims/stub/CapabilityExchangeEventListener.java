package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.net.Uri;
import android.telephony.ims.ImsException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import java.util.Set;
@SystemApi
/* loaded from: classes3.dex */
public interface CapabilityExchangeEventListener {

    /* loaded from: classes3.dex */
    public interface OptionsRequestCallback {
        void onRespondToCapabilityRequest(RcsContactUceCapability rcsContactUceCapability, boolean z);

        void onRespondToCapabilityRequestWithError(int i, String str);
    }

    void onRemoteCapabilityRequest(Uri uri, Set<String> set, OptionsRequestCallback optionsRequestCallback) throws ImsException;

    void onRequestPublishCapabilities(int i) throws ImsException;

    void onUnpublish() throws ImsException;

    @Deprecated
    default void onPublishUpdated(int reasonCode, String reasonPhrase, int reasonHeaderCause, String reasonHeaderText) throws ImsException {
        onPublishUpdated(new SipDetails.Builder(2).setSipResponseCode(reasonCode, reasonPhrase).setSipResponseReasonHeader(reasonHeaderCause, reasonHeaderText).build());
    }

    default void onPublishUpdated(SipDetails details) throws ImsException {
    }
}
