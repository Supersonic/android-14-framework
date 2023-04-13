package com.android.internal.telephony;

import android.os.Bundle;
/* loaded from: classes.dex */
public class VisualVoicemailSmsParser {
    private static final String[] ALLOWED_ALTERNATIVE_FORMAT_EVENT = {"MBOXUPDATE", "UNRECOGNIZED"};

    /* loaded from: classes.dex */
    public static class WrappedMessageData {
        public final Bundle fields;
        public final String prefix;

        public String toString() {
            return "WrappedMessageData [type=" + this.prefix + " fields=" + this.fields + "]";
        }

        WrappedMessageData(String str, Bundle bundle) {
            this.prefix = str;
            this.fields = bundle;
        }
    }

    public static WrappedMessageData parse(String str, String str2) {
        int i;
        int indexOf;
        try {
            if (str2.startsWith(str)) {
                int length = str.length();
                if (str2.charAt(length) == ':' && (indexOf = str2.indexOf(":", (i = length + 1))) != -1) {
                    String substring = str2.substring(i, indexOf);
                    Bundle parseSmsBody = parseSmsBody(str2.substring(indexOf + 1));
                    if (parseSmsBody == null) {
                        return null;
                    }
                    return new WrappedMessageData(substring, parseSmsBody);
                }
                return null;
            }
            return null;
        } catch (IndexOutOfBoundsException unused) {
            return null;
        }
    }

    private static Bundle parseSmsBody(String str) {
        String[] split;
        Bundle bundle = new Bundle();
        for (String str2 : str.split(";")) {
            if (str2.length() != 0) {
                int indexOf = str2.indexOf("=");
                if (indexOf == -1 || indexOf == 0) {
                    return null;
                }
                bundle.putString(str2.substring(0, indexOf), str2.substring(indexOf + 1));
            }
        }
        return bundle;
    }

    public static WrappedMessageData parseAlternativeFormat(String str) {
        Bundle parseSmsBody;
        try {
            int indexOf = str.indexOf("?");
            if (indexOf == -1) {
                return null;
            }
            String substring = str.substring(0, indexOf);
            if (isAllowedAlternativeFormatEvent(substring) && (parseSmsBody = parseSmsBody(str.substring(indexOf + 1))) != null) {
                return new WrappedMessageData(substring, parseSmsBody);
            }
            return null;
        } catch (IndexOutOfBoundsException unused) {
            return null;
        }
    }

    private static boolean isAllowedAlternativeFormatEvent(String str) {
        for (String str2 : ALLOWED_ALTERNATIVE_FORMAT_EVENT) {
            if (str2.equals(str)) {
                return true;
            }
        }
        return false;
    }
}
