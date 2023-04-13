package com.android.service.ims.presence;

import android.net.Uri;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class PresenceUtils {
    public static final String LOG_TAG_PREFIX = "rcs_lib";
    private static final String TEL_SCHEME = "tel:";

    public static String toContactString(String[] contacts) {
        if (contacts == null) {
            return null;
        }
        String result = "";
        for (int i = 0; i < contacts.length; i++) {
            result = result + contacts[i];
            if (i != contacts.length - 1) {
                result = result + ";";
            }
        }
        return result;
    }

    public static Uri convertContactNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        Uri possibleNumber = Uri.parse(number);
        if (TEL_SCHEME.equals(possibleNumber.getScheme())) {
            return possibleNumber;
        }
        return Uri.fromParts(TEL_SCHEME, number, null);
    }

    public static String getNumber(Uri numberUri) {
        if (numberUri == null) {
            return null;
        }
        return numberUri.getSchemeSpecificPart();
    }
}
