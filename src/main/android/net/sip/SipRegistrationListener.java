package android.net.sip;
/* loaded from: classes.dex */
public interface SipRegistrationListener {
    void onRegistering(String str);

    void onRegistrationDone(String str, long j);

    void onRegistrationFailed(String str, int i, String str2);
}
