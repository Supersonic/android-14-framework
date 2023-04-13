package com.android.internal.telephony.imsphone;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.UUSInfo;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
public class ImsExternalConnection extends Connection {
    private ImsExternalCall mCall;
    private int mCallId;
    private final Context mContext;
    private boolean mIsPullable;
    private final Set<Listener> mListeners;
    private Uri mOriginalAddress;

    /* loaded from: classes.dex */
    public interface Listener {
        void onPullExternalCall(ImsExternalConnection imsExternalConnection);
    }

    @Override // com.android.internal.telephony.Connection
    public void cancelPostDial() {
    }

    @Override // com.android.internal.telephony.Connection
    public long getDisconnectTime() {
        return 0L;
    }

    @Override // com.android.internal.telephony.Connection
    public long getHoldDurationMillis() {
        return 0L;
    }

    @Override // com.android.internal.telephony.Connection
    public int getPreciseDisconnectCause() {
        return 0;
    }

    @Override // com.android.internal.telephony.Connection
    public UUSInfo getUUSInfo() {
        return null;
    }

    @Override // com.android.internal.telephony.Connection
    public String getVendorDisconnectCause() {
        return null;
    }

    @Override // com.android.internal.telephony.Connection
    public void hangup() throws CallStateException {
    }

    @Override // com.android.internal.telephony.Connection
    public boolean isMultiparty() {
        return false;
    }

    @Override // com.android.internal.telephony.Connection
    public void proceedAfterWaitChar() {
    }

    @Override // com.android.internal.telephony.Connection
    public void proceedAfterWildChar(String str) {
    }

    @Override // com.android.internal.telephony.Connection
    public void separate() throws CallStateException {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ImsExternalConnection(Phone phone, int i, Uri uri, boolean z) {
        super(phone.getPhoneType());
        this.mListeners = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9f, 1));
        this.mContext = phone.getContext();
        this.mCall = new ImsExternalCall(phone, this);
        this.mCallId = i;
        setExternalConnectionAddress(uri);
        this.mNumberPresentation = 1;
        this.mIsPullable = z;
        rebuildCapabilities();
        setActive();
    }

    public int getCallId() {
        return this.mCallId;
    }

    @Override // com.android.internal.telephony.Connection
    public Call getCall() {
        return this.mCall;
    }

    @Override // com.android.internal.telephony.Connection
    public void deflect(String str) throws CallStateException {
        throw new CallStateException("Deflect is not supported for external calls");
    }

    @Override // com.android.internal.telephony.Connection
    public void transfer(String str, boolean z) throws CallStateException {
        throw new CallStateException("Transfer is not supported for external calls");
    }

    @Override // com.android.internal.telephony.Connection
    public void consultativeTransfer(Connection connection) throws CallStateException {
        throw new CallStateException("Transfer is not supported for external calls");
    }

    @Override // com.android.internal.telephony.Connection
    public int getNumberPresentation() {
        return this.mNumberPresentation;
    }

    @Override // com.android.internal.telephony.Connection
    public void pullExternalCall() {
        for (Listener listener : this.mListeners) {
            listener.onPullExternalCall(this);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setActive() {
        ImsExternalCall imsExternalCall = this.mCall;
        if (imsExternalCall == null) {
            return;
        }
        imsExternalCall.setActive();
    }

    public void setTerminated() {
        ImsExternalCall imsExternalCall = this.mCall;
        if (imsExternalCall == null) {
            return;
        }
        imsExternalCall.setTerminated();
    }

    public void setIsPullable(boolean z) {
        this.mIsPullable = z;
        rebuildCapabilities();
    }

    public void setExternalConnectionAddress(Uri uri) {
        this.mOriginalAddress = uri;
        if ("sip".equals(uri.getScheme()) && uri.getSchemeSpecificPart().startsWith("conf")) {
            this.mCnapName = this.mContext.getString(17039827);
            this.mCnapNamePresentation = 1;
            this.mAddress = PhoneConfigurationManager.SSSS;
            this.mNumberPresentation = 2;
            return;
        }
        this.mAddress = PhoneNumberUtils.convertSipUriToTelUri(uri).getSchemeSpecificPart();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    @Override // com.android.internal.telephony.Connection
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("[ImsExternalConnection dialogCallId:");
        sb.append(this.mCallId);
        sb.append(" state:");
        if (this.mCall.getState() == Call.State.ACTIVE) {
            sb.append("Active");
        } else if (this.mCall.getState() == Call.State.DISCONNECTED) {
            sb.append("Disconnected");
        }
        sb.append("]");
        return sb.toString();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void rebuildCapabilities() {
        setConnectionCapabilities(this.mIsPullable ? 48 : 16);
    }
}
