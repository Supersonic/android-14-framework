package android.net.sip;

import android.net.sip.ISipSessionListener;
/* loaded from: classes.dex */
public class SipSessionAdapter extends ISipSessionListener.Stub {
    @Override // android.net.sip.ISipSessionListener
    public void onCalling(ISipSession session) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRinging(ISipSession session, SipProfile caller, String sessionDescription) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onRingingBack(ISipSession session) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallEstablished(ISipSession session, String sessionDescription) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallEnded(ISipSession session) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallBusy(ISipSession session) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallTransferring(ISipSession session, String sessionDescription) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onCallChangeFailed(ISipSession session, int errorCode, String message) {
    }

    @Override // android.net.sip.ISipSessionListener
    public void onError(ISipSession session, int errorCode, String message) {
    }

    public void onRegistering(ISipSession session) {
    }

    public void onRegistrationDone(ISipSession session, int duration) {
    }

    public void onRegistrationFailed(ISipSession session, int errorCode, String message) {
    }

    public void onRegistrationTimeout(ISipSession session) {
    }
}
