package android.telephony.ims;

import android.annotation.SystemApi;
@SystemApi
/* loaded from: classes3.dex */
public interface DelegateMessageCallback {
    void onMessageReceived(SipMessage sipMessage);

    void onMessageSendFailure(String str, int i);

    void onMessageSent(String str);
}
