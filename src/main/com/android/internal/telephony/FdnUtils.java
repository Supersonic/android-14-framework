package com.android.internal.telephony;

import android.text.TextUtils;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.internal.telephony.uicc.AdnRecordCache;
import com.android.internal.telephony.uicc.IccConstants;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class FdnUtils {
    private static final String LOG_TAG = "FdnUtils";

    public static boolean isNumberBlockedByFDN(int i, String str, String str2) {
        if (isFdnEnabled(i)) {
            return !isFDN(str, str2, getFdnList(i));
        }
        return false;
    }

    public static boolean isFdnEnabled(int i) {
        UiccCardApplication uiccCardApplication = getUiccCardApplication(i);
        if (uiccCardApplication == null || !uiccCardApplication.getIccFdnAvailable()) {
            return false;
        }
        return uiccCardApplication.getIccFdnEnabled();
    }

    public static boolean isSuppServiceRequestBlockedByFdn(int i, ArrayList<String> arrayList, String str) {
        if (isFdnEnabled(i)) {
            ArrayList<AdnRecord> fdnList = getFdnList(i);
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                if (isFDN(it.next(), str, fdnList)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x003a  */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean isFDN(String str, String str2, ArrayList<AdnRecord> arrayList) {
        String str3;
        Iterator<AdnRecord> it;
        Phonenumber.PhoneNumber parse;
        if (arrayList == null || arrayList.isEmpty() || TextUtils.isEmpty(str)) {
            Rlog.w(LOG_TAG, "isFDN: unexpected null value");
            return false;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String str4 = null;
        try {
            parse = phoneNumberUtil.parse(str, str2);
            str3 = phoneNumberUtil.format(parse, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException unused) {
            str3 = null;
        }
        try {
            str4 = String.valueOf(parse.getNationalNumber());
        } catch (NumberParseException unused2) {
            Rlog.w(LOG_TAG, "isFDN: could not parse dialStr");
            it = arrayList.iterator();
            while (it.hasNext()) {
            }
            return false;
        }
        it = arrayList.iterator();
        while (it.hasNext()) {
            String number = it.next().getNumber();
            if (!TextUtils.isEmpty(number)) {
                if (!TextUtils.isEmpty(str3) && str3.startsWith(number)) {
                    return true;
                }
                if ((!TextUtils.isEmpty(str4) && str4.startsWith(number)) || str.startsWith(number)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ArrayList<AdnRecord> getFdnList(int i) {
        IccRecords iccRecords;
        AdnRecordCache adnCache;
        UiccCardApplication uiccCardApplication = getUiccCardApplication(i);
        if (uiccCardApplication == null || (iccRecords = uiccCardApplication.getIccRecords()) == null || (adnCache = iccRecords.getAdnCache()) == null) {
            return null;
        }
        return adnCache.getRecordsIfLoaded(IccConstants.EF_FDN);
    }

    private static UiccCardApplication getUiccCardApplication(int i) {
        UiccProfile uiccProfileForPhone = UiccController.getInstance().getUiccProfileForPhone(i);
        if (uiccProfileForPhone == null) {
            return null;
        }
        return uiccProfileForPhone.getApplication(1);
    }
}
