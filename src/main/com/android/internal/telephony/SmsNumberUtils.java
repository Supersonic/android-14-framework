package com.android.internal.telephony;

import android.app.blob.XmlTags;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioSystem;
import android.p008os.Binder;
import android.p008os.PersistableBundle;
import android.p008os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.android.internal.telephony.HbpcdLookup;
import com.android.internal.telephony.util.TelephonyUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes3.dex */
public class SmsNumberUtils {
    private static int[] ALL_COUNTRY_CODES = null;
    private static final int CDMA_HOME_NETWORK = 1;
    private static final int CDMA_ROAMING_NETWORK = 2;
    private static final boolean DBG;
    private static final int GSM_UMTS_NETWORK = 0;
    private static HashMap<String, ArrayList<String>> IDDS_MAPS = null;
    private static int MAX_COUNTRY_CODES_LENGTH = 0;
    private static final int MIN_COUNTRY_AREA_LOCAL_LENGTH = 10;
    private static final int NANP_CC = 1;
    private static final String NANP_IDD = "011";
    private static final int NANP_LONG_LENGTH = 11;
    private static final int NANP_MEDIUM_LENGTH = 10;
    private static final String NANP_NDD = "1";
    private static final int NANP_SHORT_LENGTH = 7;
    private static final int NP_CC_AREA_LOCAL = 104;
    private static final int NP_HOMEIDD_CC_AREA_LOCAL = 101;
    private static final int NP_INTERNATIONAL_BEGIN = 100;
    private static final int NP_LOCALIDD_CC_AREA_LOCAL = 103;
    private static final int NP_NANP_AREA_LOCAL = 2;
    private static final int NP_NANP_BEGIN = 1;
    private static final int NP_NANP_LOCAL = 1;
    private static final int NP_NANP_LOCALIDD_CC_AREA_LOCAL = 5;
    private static final int NP_NANP_NBPCD_CC_AREA_LOCAL = 4;
    private static final int NP_NANP_NBPCD_HOMEIDD_CC_AREA_LOCAL = 6;
    private static final int NP_NANP_NDD_AREA_LOCAL = 3;
    private static final int NP_NBPCD_CC_AREA_LOCAL = 102;
    private static final int NP_NBPCD_HOMEIDD_CC_AREA_LOCAL = 100;
    private static final int NP_NONE = 0;
    private static final String PLUS_SIGN = "+";
    private static final String TAG = "SmsNumberUtils";

    static {
        DBG = SystemProperties.getInt("ro.debuggable", 0) == 1;
        ALL_COUNTRY_CODES = null;
        IDDS_MAPS = new HashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class NumberEntry {
        public String IDD;
        public int countryCode;
        public String number;

        public NumberEntry(String number) {
            this.number = number;
        }
    }

    private static String formatNumber(Context context, String number, String activeMcc, int networkType) {
        if (number == null) {
            throw new IllegalArgumentException("number is null");
        }
        if (activeMcc == null || activeMcc.trim().length() == 0) {
            throw new IllegalArgumentException("activeMcc is null or empty!");
        }
        String networkPortionNumber = PhoneNumberUtils.extractNetworkPortion(number);
        if (networkPortionNumber == null || networkPortionNumber.length() == 0) {
            throw new IllegalArgumentException("Number is invalid!");
        }
        NumberEntry numberEntry = new NumberEntry(networkPortionNumber);
        ArrayList<String> allIDDs = getAllIDDs(context, activeMcc);
        int nanpState = checkNANP(numberEntry, allIDDs);
        boolean z = DBG;
        if (z) {
            Log.m112d(TAG, "NANP type: " + getNumberPlanType(nanpState));
        }
        if (nanpState == 1 || nanpState == 2 || nanpState == 3) {
            return networkPortionNumber;
        }
        if (nanpState == 4) {
            if (networkType == 1 || networkType == 2) {
                return networkPortionNumber.substring(1);
            }
            return networkPortionNumber;
        }
        if (nanpState == 5) {
            if (networkType == 1) {
                return networkPortionNumber;
            }
            if (networkType == 0) {
                int iddLength = numberEntry.IDD != null ? numberEntry.IDD.length() : 0;
                return PLUS_SIGN + networkPortionNumber.substring(iddLength);
            } else if (networkType == 2) {
                int iddLength2 = numberEntry.IDD != null ? numberEntry.IDD.length() : 0;
                return networkPortionNumber.substring(iddLength2);
            }
        }
        int internationalState = checkInternationalNumberPlan(context, numberEntry, allIDDs, NANP_IDD);
        if (z) {
            Log.m112d(TAG, "International type: " + getNumberPlanType(internationalState));
        }
        String returnNumber = null;
        switch (internationalState) {
            case 100:
                if (networkType == 0) {
                    returnNumber = networkPortionNumber.substring(1);
                    break;
                }
                break;
            case 101:
                returnNumber = networkPortionNumber;
                break;
            case 102:
                returnNumber = NANP_IDD + networkPortionNumber.substring(1);
                break;
            case 103:
                if (networkType == 0 || networkType == 2) {
                    int iddLength3 = numberEntry.IDD != null ? numberEntry.IDD.length() : 0;
                    returnNumber = NANP_IDD + networkPortionNumber.substring(iddLength3);
                    break;
                }
            case 104:
                int countryCode = numberEntry.countryCode;
                if (!inExceptionListForNpCcAreaLocal(numberEntry) && networkPortionNumber.length() >= 11 && countryCode != 1) {
                    returnNumber = NANP_IDD + networkPortionNumber;
                    break;
                }
                break;
            default:
                if (networkPortionNumber.startsWith(PLUS_SIGN) && (networkType == 1 || networkType == 2)) {
                    if (!networkPortionNumber.startsWith("+011")) {
                        returnNumber = NANP_IDD + networkPortionNumber.substring(1);
                        break;
                    } else {
                        returnNumber = networkPortionNumber.substring(1);
                        break;
                    }
                }
                break;
        }
        if (returnNumber == null) {
            return networkPortionNumber;
        }
        return returnNumber;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x005f, code lost:
        if (r10 == null) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0062, code lost:
        com.android.internal.telephony.SmsNumberUtils.IDDS_MAPS.put(r12, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0069, code lost:
        if (com.android.internal.telephony.SmsNumberUtils.DBG == false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006b, code lost:
        android.util.Log.m112d(com.android.internal.telephony.SmsNumberUtils.TAG, "MCC = " + r12 + ", all IDDs = " + r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x008b, code lost:
        return r2;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static ArrayList<String> getAllIDDs(Context context, String mcc) {
        ArrayList<String> allIDDs = IDDS_MAPS.get(mcc);
        if (allIDDs != null) {
            return allIDDs;
        }
        ArrayList<String> allIDDs2 = new ArrayList<>();
        String[] projection = {HbpcdLookup.MccIdd.IDD, "MCC"};
        String where = null;
        String[] selectionArgs = null;
        if (mcc != null) {
            where = "MCC=?";
            selectionArgs = new String[]{mcc};
        }
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(HbpcdLookup.MccIdd.CONTENT_URI, projection, where, selectionArgs, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String idd = cursor.getString(0);
                        if (!allIDDs2.contains(idd)) {
                            allIDDs2.add(idd);
                        }
                    }
                }
            } catch (SQLException e) {
                Log.m109e(TAG, "Can't access HbpcdLookup database", e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static int checkNANP(NumberEntry numberEntry, ArrayList<String> allIDDs) {
        String number2;
        boolean isNANP = false;
        String number = numberEntry.number;
        if (number.length() == 7) {
            char firstChar = number.charAt(0);
            if (firstChar >= '2' && firstChar <= '9') {
                isNANP = true;
                int i = 1;
                while (true) {
                    if (i >= 7) {
                        break;
                    }
                    char c = number.charAt(i);
                    if (PhoneNumberUtils.isISODigit(c)) {
                        i++;
                    } else {
                        isNANP = false;
                        break;
                    }
                }
            }
            if (isNANP) {
                return 1;
            }
        } else if (number.length() == 10) {
            if (isNANP(number)) {
                return 2;
            }
        } else if (number.length() == 11) {
            if (isNANP(number)) {
                return 3;
            }
        } else if (number.startsWith(PLUS_SIGN)) {
            String number3 = number.substring(1);
            if (number3.length() == 11) {
                if (isNANP(number3)) {
                    return 4;
                }
            } else if (number3.startsWith(NANP_IDD) && number3.length() == 14 && isNANP(number3.substring(3))) {
                return 6;
            }
        } else {
            Iterator<String> it = allIDDs.iterator();
            while (it.hasNext()) {
                String idd = it.next();
                if (number.startsWith(idd) && (number2 = number.substring(idd.length())) != null && number2.startsWith(String.valueOf(1)) && isNANP(number2)) {
                    numberEntry.IDD = idd;
                    return 5;
                }
            }
        }
        return 0;
    }

    private static boolean isNANP(String number) {
        if (number.length() != 10 && (number.length() != 11 || !number.startsWith(NANP_NDD))) {
            return false;
        }
        if (number.length() == 11) {
            number = number.substring(1);
        }
        if (!isTwoToNine(number.charAt(0)) || !isTwoToNine(number.charAt(3))) {
            return false;
        }
        for (int i = 1; i < 10; i++) {
            char c = number.charAt(i);
            if (!PhoneNumberUtils.isISODigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTwoToNine(char c) {
        if (c >= '2' && c <= '9') {
            return true;
        }
        return false;
    }

    private static int checkInternationalNumberPlan(Context context, NumberEntry numberEntry, ArrayList<String> allIDDs, String homeIDD) {
        int countryCode;
        String number = numberEntry.number;
        if (number.startsWith(PLUS_SIGN)) {
            String numberNoNBPCD = number.substring(1);
            if (numberNoNBPCD.startsWith(homeIDD)) {
                String numberCountryAreaLocal = numberNoNBPCD.substring(homeIDD.length());
                int countryCode2 = getCountryCode(context, numberCountryAreaLocal);
                if (countryCode2 > 0) {
                    numberEntry.countryCode = countryCode2;
                    return 100;
                }
                return 0;
            }
            int countryCode3 = getCountryCode(context, numberNoNBPCD);
            if (countryCode3 > 0) {
                numberEntry.countryCode = countryCode3;
                return 102;
            }
            return 0;
        } else if (number.startsWith(homeIDD)) {
            String numberCountryAreaLocal2 = number.substring(homeIDD.length());
            int countryCode4 = getCountryCode(context, numberCountryAreaLocal2);
            if (countryCode4 > 0) {
                numberEntry.countryCode = countryCode4;
                return 101;
            }
            return 0;
        } else {
            Iterator<String> it = allIDDs.iterator();
            while (it.hasNext()) {
                String exitCode = it.next();
                if (number.startsWith(exitCode)) {
                    String numberNoIDD = number.substring(exitCode.length());
                    int countryCode5 = getCountryCode(context, numberNoIDD);
                    if (countryCode5 > 0) {
                        numberEntry.countryCode = countryCode5;
                        numberEntry.IDD = exitCode;
                        return 103;
                    }
                }
            }
            if (!number.startsWith(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS) && (countryCode = getCountryCode(context, number)) > 0) {
                numberEntry.countryCode = countryCode;
                return 104;
            }
            return 0;
        }
    }

    private static int getCountryCode(Context context, String number) {
        int[] allCCs;
        if (number.length() < 10 || (allCCs = getAllCountryCodes(context)) == null) {
            return -1;
        }
        int[] ccArray = new int[MAX_COUNTRY_CODES_LENGTH];
        for (int i = 0; i < MAX_COUNTRY_CODES_LENGTH; i++) {
            ccArray[i] = Integer.parseInt(number.substring(0, i + 1));
        }
        for (int tempCC : allCCs) {
            for (int j = 0; j < MAX_COUNTRY_CODES_LENGTH; j++) {
                if (tempCC == ccArray[j]) {
                    if (DBG) {
                        Log.m112d(TAG, "Country code = " + tempCC);
                    }
                    return tempCC;
                }
            }
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0060, code lost:
        if (r0 == null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0065, code lost:
        return com.android.internal.telephony.SmsNumberUtils.ALL_COUNTRY_CODES;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int[] getAllCountryCodes(Context context) {
        int[] iArr = ALL_COUNTRY_CODES;
        if (iArr != null) {
            return iArr;
        }
        Cursor cursor = null;
        try {
            try {
                String[] projection = {HbpcdLookup.MccLookup.COUNTRY_CODE};
                cursor = context.getContentResolver().query(HbpcdLookup.MccLookup.CONTENT_URI, projection, null, null, null);
                if (cursor.getCount() > 0) {
                    ALL_COUNTRY_CODES = new int[cursor.getCount()];
                    int length = 0;
                    while (cursor.moveToNext()) {
                        int countryCode = cursor.getInt(0);
                        int i = length + 1;
                        ALL_COUNTRY_CODES[length] = countryCode;
                        int length2 = String.valueOf(countryCode).trim().length();
                        if (length2 > MAX_COUNTRY_CODES_LENGTH) {
                            MAX_COUNTRY_CODES_LENGTH = length2;
                        }
                        length = i;
                    }
                }
            } catch (SQLException e) {
                Log.m109e(TAG, "Can't access HbpcdLookup database", e);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean inExceptionListForNpCcAreaLocal(NumberEntry numberEntry) {
        int countryCode = numberEntry.countryCode;
        return numberEntry.number.length() == 12 && (countryCode == 7 || countryCode == 20 || countryCode == 65 || countryCode == 90);
    }

    private static String getNumberPlanType(int state) {
        String str = "Number Plan type (" + state + "): ";
        if (state == 1) {
            return "NP_NANP_LOCAL";
        }
        if (state == 2) {
            return "NP_NANP_AREA_LOCAL";
        }
        if (state == 3) {
            return "NP_NANP_NDD_AREA_LOCAL";
        }
        if (state == 4) {
            return "NP_NANP_NBPCD_CC_AREA_LOCAL";
        }
        if (state == 5) {
            return "NP_NANP_LOCALIDD_CC_AREA_LOCAL";
        }
        if (state == 6) {
            return "NP_NANP_NBPCD_HOMEIDD_CC_AREA_LOCAL";
        }
        if (state == 100) {
            return "NP_NBPCD_HOMEIDD_CC_AREA_LOCAL";
        }
        if (state == 101) {
            return "NP_HOMEIDD_CC_AREA_LOCAL";
        }
        if (state == 102) {
            return "NP_NBPCD_CC_AREA_LOCAL";
        }
        if (state == 103) {
            return "NP_LOCALIDD_CC_AREA_LOCAL";
        }
        if (state == 104) {
            return "NP_CC_AREA_LOCAL";
        }
        return "Unknown type";
    }

    public static String filterDestAddr(Context context, int subId, String destAddr) {
        int networkType;
        String networkMcc;
        boolean z = DBG;
        if (z) {
            Log.m112d(TAG, "enter filterDestAddr. destAddr=\"" + pii(TAG, destAddr) + "\"");
        }
        if (destAddr == null || !PhoneNumberUtils.isGlobalPhoneNumber(destAddr)) {
            Log.m104w(TAG, "destAddr" + pii(TAG, destAddr) + " is not a global phone number! Nothing changed.");
            return destAddr;
        }
        TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService("phone")).createForSubscriptionId(subId);
        String networkOperator = telephonyManager.getNetworkOperator();
        String result = null;
        if (needToConvert(context, subId) && (networkType = getNetworkType(telephonyManager)) != -1 && !TextUtils.isEmpty(networkOperator) && (networkMcc = networkOperator.substring(0, 3)) != null && networkMcc.trim().length() > 0) {
            result = formatNumber(context, destAddr, networkMcc, networkType);
        }
        if (z) {
            Log.m112d(TAG, "destAddr is " + (result != null ? "formatted." : "not formatted."));
            Log.m112d(TAG, "leave filterDestAddr, new destAddr=\"" + (result != null ? pii(TAG, result) : pii(TAG, destAddr)) + "\"");
        }
        return result != null ? result : destAddr;
    }

    private static int getNetworkType(TelephonyManager telephonyManager) {
        int phoneType = telephonyManager.getPhoneType();
        if (phoneType == 1) {
            return 0;
        }
        if (phoneType == 2) {
            if (isInternationalRoaming(telephonyManager)) {
                return 2;
            }
            return 1;
        } else if (DBG) {
            Log.m104w(TAG, "warning! unknown mPhoneType value=" + phoneType);
            return -1;
        } else {
            return -1;
        }
    }

    private static boolean isInternationalRoaming(TelephonyManager telephonyManager) {
        String operatorIsoCountry = telephonyManager.getNetworkCountryIso();
        String simIsoCountry = telephonyManager.getSimCountryIso();
        boolean internationalRoaming = (TextUtils.isEmpty(operatorIsoCountry) || TextUtils.isEmpty(simIsoCountry) || simIsoCountry.equals(operatorIsoCountry)) ? false : true;
        if (internationalRoaming) {
            if (XmlTags.ATTR_USER_ID.equals(simIsoCountry)) {
                return true ^ "vi".equals(operatorIsoCountry);
            } else if ("vi".equals(simIsoCountry)) {
                return true ^ XmlTags.ATTR_USER_ID.equals(operatorIsoCountry);
            } else {
                return internationalRoaming;
            }
        }
        return internationalRoaming;
    }

    private static boolean needToConvert(Context context, int subId) {
        PersistableBundle bundle;
        long identity = Binder.clearCallingIdentity();
        try {
            CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
            if (configManager != null && (bundle = configManager.getConfigForSubId(subId)) != null) {
                return bundle.getBoolean(CarrierConfigManager.KEY_SMS_REQUIRES_DESTINATION_NUMBER_CONVERSION_BOOL);
            }
            Binder.restoreCallingIdentity(identity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private static String pii(String tag, Object pii) {
        String val = String.valueOf(pii);
        if (pii == null || TextUtils.isEmpty(val) || Log.isLoggable(tag, 2)) {
            return val;
        }
        return NavigationBarInflaterView.SIZE_MOD_START + secureHash(val.getBytes()) + NavigationBarInflaterView.SIZE_MOD_END;
    }

    private static String secureHash(byte[] input) {
        if (TelephonyUtils.IS_USER) {
            return "****";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] result = messageDigest.digest(input);
            return Base64.encodeToString(result, 11);
        } catch (NoSuchAlgorithmException e) {
            return "####";
        }
    }
}
