package com.android.internal.telephony.metrics;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.data.ApnSetting;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccSlot;
import java.util.Optional;
import java.util.function.Function;
/* loaded from: classes.dex */
public class PerSimStatus {
    public final boolean advancedCallingSettingEnabled;
    public final int carrierId;
    public final boolean dataRoamingEnabled;
    public final boolean disabled2g;
    public final int minimumVoltageClass;
    public final int phoneNumberSourceCarrier;
    public final int phoneNumberSourceIms;
    public final int phoneNumberSourceUicc;
    public final boolean pin1Enabled;
    public final long preferredNetworkType;
    public final long unmeteredNetworks;
    public final int userModifiedApnTypes;
    public final int voWiFiModeSetting;
    public final int voWiFiRoamingModeSetting;
    public final boolean voWiFiSettingEnabled;
    public final boolean vtSettingEnabled;

    private static int wifiCallingModeToProtoEnum(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? 0 : 3;
            }
            return 2;
        }
        return 1;
    }

    public static PerSimStatus getCurrentState(Phone phone) {
        int[] numberIds = getNumberIds(phone);
        if (numberIds == null) {
            return null;
        }
        int carrierId = phone.getCarrierId();
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(phone);
        IccCard iccCard = phone.getIccCard();
        PersistAtomsStorage atomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
        return new PerSimStatus(carrierId, numberIds[0], numberIds[1], numberIds[2], imsMmTelManager == null ? false : imsMmTelManager.isAdvancedCallingSettingEnabled(), imsMmTelManager == null ? false : imsMmTelManager.isVoWiFiSettingEnabled(), imsMmTelManager == null ? 0 : wifiCallingModeToProtoEnum(imsMmTelManager.getVoWiFiModeSetting()), imsMmTelManager == null ? 0 : wifiCallingModeToProtoEnum(imsMmTelManager.getVoWiFiRoamingModeSetting()), imsMmTelManager == null ? false : imsMmTelManager.isVtSettingEnabled(), phone.getDataRoamingEnabled(), phone.getAllowedNetworkTypes(0), is2gDisabled(phone), iccCard == null ? false : iccCard.getIccLockEnabled(), getMinimumVoltageClass(phone), getUserModifiedApnTypes(phone), atomsStorage.getUnmeteredNetworks(phone.getPhoneId(), carrierId));
    }

    private PerSimStatus(int i, int i2, int i3, int i4, boolean z, boolean z2, int i5, int i6, boolean z3, boolean z4, long j, boolean z5, boolean z6, int i7, int i8, long j2) {
        this.carrierId = i;
        this.phoneNumberSourceUicc = i2;
        this.phoneNumberSourceCarrier = i3;
        this.phoneNumberSourceIms = i4;
        this.advancedCallingSettingEnabled = z;
        this.voWiFiSettingEnabled = z2;
        this.voWiFiModeSetting = i5;
        this.voWiFiRoamingModeSetting = i6;
        this.vtSettingEnabled = z3;
        this.dataRoamingEnabled = z4;
        this.preferredNetworkType = j;
        this.disabled2g = z5;
        this.pin1Enabled = z6;
        this.minimumVoltageClass = i7;
        this.userModifiedApnTypes = i8;
        this.unmeteredNetworks = j2;
    }

    private static ImsMmTelManager getImsMmTelManager(Phone phone) {
        ImsManager imsManager = (ImsManager) phone.getContext().getSystemService(ImsManager.class);
        if (imsManager == null) {
            return null;
        }
        try {
            return imsManager.getImsMmTelManager(phone.getSubId());
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    private static int[] getNumberIds(Phone phone) {
        String[] strArr;
        boolean isSubscriptionManagerServiceEnabled = PhoneFactory.isSubscriptionManagerServiceEnabled();
        int i = 1;
        String str = PhoneConfigurationManager.SSSS;
        if (isSubscriptionManagerServiceEnabled) {
            if (SubscriptionManagerService.getInstance() == null) {
                return null;
            }
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(phone.getSubId());
            if (subscriptionInfoInternal != null) {
                str = subscriptionInfoInternal.getCountryIso();
            }
            strArr = new String[]{SubscriptionManagerService.getInstance().getPhoneNumber(phone.getSubId(), 1, null, null), SubscriptionManagerService.getInstance().getPhoneNumber(phone.getSubId(), 2, null, null), SubscriptionManagerService.getInstance().getPhoneNumber(phone.getSubId(), 3, null, null)};
        } else {
            SubscriptionController subscriptionController = SubscriptionController.getInstance();
            if (subscriptionController == null) {
                return null;
            }
            int subId = phone.getSubId();
            str = (String) Optional.ofNullable(subscriptionController.getSubscriptionInfo(subId)).map(new Function() { // from class: com.android.internal.telephony.metrics.PerSimStatus$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((SubscriptionInfo) obj).getCountryIso();
                }
            }).orElse(PhoneConfigurationManager.SSSS);
            strArr = new String[]{subscriptionController.getPhoneNumber(subId, 1, null, null), subscriptionController.getPhoneNumber(subId, 2, null, null), subscriptionController.getPhoneNumber(subId, 3, null, null)};
        }
        int length = strArr.length;
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            if (!TextUtils.isEmpty(strArr[i2])) {
                for (int i3 = 0; i3 < i2; i3++) {
                    if (!TextUtils.isEmpty(strArr[i3]) && PhoneNumberUtils.areSamePhoneNumber(strArr[i2], strArr[i3], str)) {
                        iArr[i2] = iArr[i3];
                    }
                }
                if (iArr[i2] == 0) {
                    iArr[i2] = i;
                    i++;
                }
            }
        }
        return iArr;
    }

    private static boolean is2gDisabled(Phone phone) {
        return (phone.getAllowedNetworkTypes(3) & 32843) == 0;
    }

    private static int getMinimumVoltageClass(Phone phone) {
        UiccSlot uiccSlotForPhone = UiccController.getInstance().getUiccSlotForPhone(phone.getPhoneId());
        if (uiccSlotForPhone == null) {
            return 0;
        }
        int minimumVoltageClass = uiccSlotForPhone.getMinimumVoltageClass();
        int i = 1;
        if (minimumVoltageClass != 1) {
            i = 2;
            if (minimumVoltageClass != 2) {
                i = 3;
                if (minimumVoltageClass != 3) {
                    return 0;
                }
            }
        }
        return i;
    }

    private static int getUserModifiedApnTypes(Phone phone) {
        String[] strArr = {Integer.toString(1)};
        ContentResolver contentResolver = phone.getContext().getContentResolver();
        Uri uri = Telephony.Carriers.CONTENT_URI;
        Cursor query = contentResolver.query(Uri.withAppendedPath(uri, "subId/" + phone.getSubId()), new String[]{"type"}, "edited=?", strArr, null);
        int i = 0;
        while (query != null) {
            try {
                if (!query.moveToNext()) {
                    break;
                }
                i |= ApnSetting.getApnTypesBitmaskFromString(query.getString(0));
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (query != null) {
            query.close();
        }
        return i;
    }
}
