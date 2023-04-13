package com.android.internal.org.bouncycastle.asn1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes4.dex */
class DateUtil {
    private static Long ZERO = longValueOf(0);
    private static final Map localeCache = new HashMap();
    static Locale EN_Locale = forEN();

    DateUtil() {
    }

    private static Locale forEN() {
        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            return Locale.getDefault();
        }
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i != locales.length; i++) {
            if ("en".equalsIgnoreCase(locales[i].getLanguage())) {
                return locales[i];
            }
        }
        return Locale.getDefault();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Date epochAdjust(Date date) throws ParseException {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return date;
        }
        Map map = localeCache;
        synchronized (map) {
            Long adj = (Long) map.get(locale);
            if (adj == null) {
                SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
                long v = dateF.parse("19700101000000GMT+00:00").getTime();
                if (v == 0) {
                    adj = ZERO;
                } else {
                    adj = longValueOf(v);
                }
                map.put(locale, adj);
            }
            if (adj != ZERO) {
                return new Date(date.getTime() - adj.longValue());
            }
            return date;
        }
    }

    private static Long longValueOf(long v) {
        return Long.valueOf(v);
    }
}
