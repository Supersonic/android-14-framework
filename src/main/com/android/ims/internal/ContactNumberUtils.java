package com.android.ims.internal;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ContactNumberUtils {
    private static final String TAG = "ContactNumberUtils";
    private int NUMBER_LENGTH_MAX = 17;
    private int NUMBER_LENGTH_NORMAL = 10;
    private int NUMBER_LENGTH_NO_AREA_CODE = 7;
    private Context mContext = null;
    public static int NUMBER_VALID = 0;
    public static int NUMBER_EMERGENCY = 1;
    public static int NUMBER_SHORT_CODE = 2;
    public static int NUMBER_PRELOADED_ENTRY = 3;
    public static int NUMBER_FREE_PHONE = 4;
    public static int NUMBER_INVALID = 5;
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static ContactNumberUtils sInstance = null;
    private static ArrayList<String> sExcludes = null;

    public static ContactNumberUtils getDefault() {
        if (sInstance == null) {
            sInstance = new ContactNumberUtils();
        }
        return sInstance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public String format(String phoneNumber) {
        String number = phoneNumber;
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        if (number.startsWith("*67") || number.startsWith("*82")) {
            number = number.substring(3);
            if (TextUtils.isEmpty(number)) {
                return null;
            }
        }
        String number2 = PhoneNumberUtils.stripSeparators(number);
        if (number2.length() == this.NUMBER_LENGTH_NO_AREA_CODE) {
            number2 = addAreaCode(number2);
        }
        String number3 = PhoneNumberUtils.normalizeNumber(number2);
        int len = number3.length();
        int i = this.NUMBER_LENGTH_NORMAL;
        if (len == i) {
            if (!number3.startsWith("+1")) {
                number3 = "+1" + number3;
            }
        } else if (len == i + 1) {
            if (number3.startsWith("1")) {
                number3 = "+" + number3;
            }
        } else if (len >= i + 2) {
            if (len >= i + 4 && number3.startsWith("011")) {
                number3 = "+" + number3.substring(3);
            }
            if (!number3.startsWith("+")) {
                number3 = number3.startsWith("1") ? "+" + number3 : "+1" + number3;
            }
        }
        if (number3.length() > this.NUMBER_LENGTH_MAX) {
            return null;
        }
        return number3;
    }

    public int validate(String phoneNumber) {
        boolean isEmergencyNumber;
        String number = phoneNumber;
        if (TextUtils.isEmpty(number)) {
            return NUMBER_INVALID;
        }
        if (number.startsWith("*67") || number.startsWith("*82")) {
            number = number.substring(3);
            if (TextUtils.isEmpty(number)) {
                return NUMBER_INVALID;
            }
        }
        if (number.contains("*")) {
            return NUMBER_PRELOADED_ENTRY;
        }
        String number2 = PhoneNumberUtils.stripSeparators(number);
        if (!number2.equals(PhoneNumberUtils.convertKeypadLettersToDigits(number2))) {
            return NUMBER_INVALID;
        }
        Context context = this.mContext;
        if (context == null) {
            Log.e(TAG, "context is unexpectedly null to provide emergency identification service");
            isEmergencyNumber = DEBUG;
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            isEmergencyNumber = tm.isEmergencyNumber(number2);
        }
        if (isEmergencyNumber) {
            return NUMBER_EMERGENCY;
        }
        if (number2.startsWith("#")) {
            return NUMBER_PRELOADED_ENTRY;
        }
        if (isInExcludedList(number2)) {
            return NUMBER_FREE_PHONE;
        }
        int len = number2.length();
        if (len < this.NUMBER_LENGTH_NORMAL) {
            return NUMBER_INVALID;
        }
        String number3 = format(number2);
        if (number3.startsWith("+")) {
            int len2 = number3.length();
            if (len2 >= this.NUMBER_LENGTH_NORMAL + 2) {
                return NUMBER_VALID;
            }
        }
        return NUMBER_INVALID;
    }

    public String[] format(List<String> numbers) {
        if (numbers == null || numbers.size() == 0) {
            return null;
        }
        int size = numbers.size();
        String[] outContactsArray = new String[size];
        for (int i = 0; i < size; i++) {
            String number = numbers.get(i);
            outContactsArray[i] = format(number);
            if (DEBUG) {
                Log.d(TAG, "outContactsArray[" + i + "] = " + outContactsArray[i]);
            }
        }
        return outContactsArray;
    }

    public String[] format(String[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return null;
        }
        int length = numbers.length;
        String[] outContactsArray = new String[length];
        for (int i = 0; i < length; i++) {
            String number = numbers[i];
            outContactsArray[i] = format(number);
            if (DEBUG) {
                Log.d(TAG, "outContactsArray[" + i + "] = " + outContactsArray[i]);
            }
        }
        return outContactsArray;
    }

    public int validate(List<String> numbers) {
        if (numbers == null || numbers.size() == 0) {
            int size = NUMBER_INVALID;
            return size;
        }
        int size2 = numbers.size();
        for (int i = 0; i < size2; i++) {
            String number = numbers.get(i);
            int result = validate(number);
            if (result != NUMBER_VALID) {
                return result;
            }
        }
        int i2 = NUMBER_VALID;
        return i2;
    }

    public int validate(String[] numbers) {
        if (numbers == null || numbers.length == 0) {
            int length = NUMBER_INVALID;
            return length;
        }
        for (String number : numbers) {
            int result = validate(number);
            if (result != NUMBER_VALID) {
                return result;
            }
        }
        int i = NUMBER_VALID;
        return i;
    }

    private ContactNumberUtils() {
        if (DEBUG) {
            Log.d(TAG, "ContactNumberUtils constructor");
        }
    }

    private String addAreaCode(String number) {
        Context context = this.mContext;
        if (context == null) {
            if (DEBUG) {
                Log.e(TAG, "mContext is null, please update context.");
            }
            return number;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        String mdn = tm.getLine1Number();
        if (mdn == null || mdn.length() == 0 || mdn.startsWith("00000")) {
            return number;
        }
        String mdn2 = PhoneNumberUtils.stripSeparators(mdn);
        if (mdn2.length() >= this.NUMBER_LENGTH_NORMAL) {
            mdn2 = mdn2.substring(mdn2.length() - this.NUMBER_LENGTH_NORMAL);
        }
        return mdn2.substring(0, 3) + number;
    }

    private boolean isInExcludedList(String number) {
        if (sExcludes == null) {
            ArrayList<String> arrayList = new ArrayList<>();
            sExcludes = arrayList;
            arrayList.add("800");
            sExcludes.add("822");
            sExcludes.add("833");
            sExcludes.add("844");
            sExcludes.add("855");
            sExcludes.add("866");
            sExcludes.add("877");
            sExcludes.add("880882");
            sExcludes.add("888");
            sExcludes.add("900");
            sExcludes.add("911");
        }
        String tempNumber = format(number);
        if (TextUtils.isEmpty(tempNumber)) {
            return true;
        }
        if (tempNumber.startsWith("1")) {
            tempNumber = tempNumber.substring(1);
        } else if (tempNumber.startsWith("+1")) {
            tempNumber = tempNumber.substring(2);
        }
        if (TextUtils.isEmpty(tempNumber)) {
            return true;
        }
        Iterator<String> it = sExcludes.iterator();
        while (it.hasNext()) {
            String num = it.next();
            if (tempNumber.startsWith(num)) {
                return true;
            }
        }
        return DEBUG;
    }
}
