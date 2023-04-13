package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.telephony.ims.SipMessage;
@SystemApi
/* loaded from: classes3.dex */
public interface SipDelegate {
    void cleanupSession(String str);

    void notifyMessageReceiveError(String str, int i);

    void notifyMessageReceived(String str);

    void sendMessage(SipMessage sipMessage, long j);
}
