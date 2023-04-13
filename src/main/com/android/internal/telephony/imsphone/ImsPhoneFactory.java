package com.android.internal.telephony.imsphone;

import android.content.Context;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneNotifier;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class ImsPhoneFactory {
    public static ImsPhone makePhone(Context context, PhoneNotifier phoneNotifier, Phone phone) {
        try {
            return new ImsPhone(context, phoneNotifier, phone);
        } catch (Exception e) {
            Rlog.e("VoltePhoneFactory", "makePhone", e);
            return null;
        }
    }
}
