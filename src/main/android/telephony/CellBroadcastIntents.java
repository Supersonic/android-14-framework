package android.telephony;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.UserHandle;
import android.provider.Telephony;
import com.android.internal.telephony.PhoneConstants;
@SystemApi
/* loaded from: classes3.dex */
public class CellBroadcastIntents {
    public static final String ACTION_AREA_INFO_UPDATED = "android.telephony.action.AREA_INFO_UPDATED";
    private static final String EXTRA_MESSAGE = "message";
    private static final String LOG_TAG = "CellBroadcastIntents";

    private CellBroadcastIntents() {
    }

    public static void sendSmsCbReceivedBroadcast(Context context, UserHandle user, SmsCbMessage smsCbMessage, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, int slotIndex) {
        Intent backgroundIntent = new Intent(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION);
        backgroundIntent.putExtra("message", smsCbMessage);
        putPhoneIdAndSubIdExtra(context, backgroundIntent, slotIndex);
        if (user != null) {
            context.createContextAsUser(user, 0).sendOrderedBroadcast(backgroundIntent, Manifest.C0000permission.RECEIVE_SMS, AppOpsManager.OPSTR_RECEIVE_SMS, resultReceiver, scheduler, initialCode, (String) null, (Bundle) null);
        } else {
            context.sendOrderedBroadcast(backgroundIntent, Manifest.C0000permission.RECEIVE_SMS, AppOpsManager.OPSTR_RECEIVE_SMS, resultReceiver, scheduler, initialCode, (String) null, (Bundle) null);
        }
    }

    private static void putPhoneIdAndSubIdExtra(Context context, Intent intent, int phoneId) {
        int subId = SubscriptionManager.getSubscriptionId(phoneId);
        if (subId != -1) {
            intent.putExtra(PhoneConstants.SUBSCRIPTION_KEY, subId);
            intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", subId);
        }
        intent.putExtra("phone", phoneId);
        intent.putExtra("android.telephony.extra.SLOT_INDEX", phoneId);
    }
}
