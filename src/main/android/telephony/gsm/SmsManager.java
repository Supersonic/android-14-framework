package android.telephony.gsm;

import android.app.PendingIntent;
import java.util.ArrayList;
@Deprecated
/* loaded from: classes.dex */
public final class SmsManager {
    @Deprecated
    public static final int RESULT_ERROR_GENERIC_FAILURE = 1;
    @Deprecated
    public static final int RESULT_ERROR_NO_SERVICE = 4;
    @Deprecated
    public static final int RESULT_ERROR_NULL_PDU = 3;
    @Deprecated
    public static final int RESULT_ERROR_RADIO_OFF = 2;
    @Deprecated
    public static final int STATUS_ON_SIM_FREE = 0;
    @Deprecated
    public static final int STATUS_ON_SIM_READ = 1;
    @Deprecated
    public static final int STATUS_ON_SIM_SENT = 5;
    @Deprecated
    public static final int STATUS_ON_SIM_UNREAD = 3;
    @Deprecated
    public static final int STATUS_ON_SIM_UNSENT = 7;
    private static SmsManager sInstance;
    private android.telephony.SmsManager mSmsMgrProxy = android.telephony.SmsManager.getDefault();

    @Deprecated
    public static final SmsManager getDefault() {
        if (sInstance == null) {
            sInstance = new SmsManager();
        }
        return sInstance;
    }

    @Deprecated
    private SmsManager() {
    }

    @Deprecated
    public final void sendTextMessage(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        this.mSmsMgrProxy.sendTextMessage(str, str2, str3, pendingIntent, pendingIntent2);
    }

    @Deprecated
    public final ArrayList<String> divideMessage(String str) {
        return this.mSmsMgrProxy.divideMessage(str);
    }

    @Deprecated
    public final void sendMultipartTextMessage(String str, String str2, ArrayList<String> arrayList, ArrayList<PendingIntent> arrayList2, ArrayList<PendingIntent> arrayList3) {
        this.mSmsMgrProxy.sendMultipartTextMessage(str, str2, arrayList, arrayList2, arrayList3);
    }

    @Deprecated
    public final void sendDataMessage(String str, String str2, short s, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        this.mSmsMgrProxy.sendDataMessage(str, str2, s, bArr, pendingIntent, pendingIntent2);
    }

    @Deprecated
    public final boolean copyMessageToSim(byte[] bArr, byte[] bArr2, int i) {
        return this.mSmsMgrProxy.copyMessageToIcc(bArr, bArr2, i);
    }

    @Deprecated
    public final boolean deleteMessageFromSim(int i) {
        return this.mSmsMgrProxy.deleteMessageFromIcc(i);
    }

    @Deprecated
    public final boolean updateMessageOnSim(int i, int i2, byte[] bArr) {
        return this.mSmsMgrProxy.updateMessageOnIcc(i, i2, bArr);
    }

    @Deprecated
    public final ArrayList<android.telephony.SmsMessage> getAllMessagesFromSim() {
        return android.telephony.SmsManager.getDefault().getAllMessagesFromIcc();
    }
}
