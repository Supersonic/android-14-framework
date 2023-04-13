package com.android.internal.telephony.metrics;

import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
/* loaded from: classes.dex */
public class DataStallRecoveryStats {
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0041, code lost:
        if (r0 == 4) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void onDataStallEvent(int i, Phone phone, boolean z, int i2, int i3, boolean z2) {
        int i4;
        int i5;
        int i6 = i;
        Phone defaultPhone = phone.getPhoneType() == 5 ? phone.getDefaultPhone() : phone;
        int carrierId = defaultPhone.getCarrierId();
        int rat = getRat(defaultPhone);
        int band = rat == 18 ? 0 : ServiceStateStats.getBand(defaultPhone);
        int level = defaultPhone.getSignalStrength().getLevel();
        boolean isOpportunistic = getIsOpportunistic(defaultPhone);
        boolean z3 = SimSlotState.getCurrentState().numActiveSims > 1;
        int i7 = i6 != 3 ? 4 : 3;
        i6 = i7;
        Phone[] phones = PhoneFactory.getPhones();
        int length = phones.length;
        int i8 = 0;
        while (true) {
            if (i8 >= length) {
                i4 = 0;
                break;
            }
            Phone phone2 = phones[i8];
            if (phone2.getPhoneId() == defaultPhone.getPhoneId() || getIsOpportunistic(phone2)) {
                i8++;
            } else {
                int level2 = phone2.getSignalStrength().getLevel();
                NetworkRegistrationInfo networkRegistrationInfo = phone2.getServiceState().getNetworkRegistrationInfo(2, 1);
                if (networkRegistrationInfo != null) {
                    i4 = level2;
                    i5 = networkRegistrationInfo.getRegistrationState();
                } else {
                    i4 = level2;
                }
            }
        }
        i5 = 0;
        NetworkRegistrationInfo networkRegistrationInfo2 = defaultPhone.getServiceState().getNetworkRegistrationInfo(2, 1);
        TelephonyStatsLog.write(315, carrierId, rat, level, i6, isOpportunistic, z3, band, z, i2, i3, i4, i5, networkRegistrationInfo2 != null ? networkRegistrationInfo2.getRegistrationState() : 0, z2, defaultPhone.getPhoneId() + 1);
    }

    private static int getRat(Phone phone) {
        ServiceStateTracker serviceStateTracker = phone.getServiceStateTracker();
        ServiceState serviceState = serviceStateTracker != null ? serviceStateTracker.getServiceState() : null;
        if (serviceState != null) {
            return serviceState.getDataNetworkType();
        }
        return 0;
    }

    private static boolean getIsOpportunistic(Phone phone) {
        if (phone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(phone.getSubId());
            return subscriptionInfoInternal != null && subscriptionInfoInternal.isOpportunistic();
        }
        SubscriptionController subscriptionController = SubscriptionController.getInstance();
        return subscriptionController != null && subscriptionController.isOpportunistic(phone.getSubId());
    }
}
