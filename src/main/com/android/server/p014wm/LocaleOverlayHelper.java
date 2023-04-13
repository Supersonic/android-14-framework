package com.android.server.p014wm;

import android.os.LocaleList;
import java.util.Locale;
/* renamed from: com.android.server.wm.LocaleOverlayHelper */
/* loaded from: classes2.dex */
public final class LocaleOverlayHelper {
    public static LocaleList combineLocalesIfOverlayExists(LocaleList localeList, LocaleList localeList2) {
        return (localeList == null || localeList.isEmpty()) ? localeList : combineLocales(localeList, localeList2);
    }

    public static LocaleList combineLocales(LocaleList localeList, LocaleList localeList2) {
        Locale[] localeArr = new Locale[localeList.size() + localeList2.size()];
        for (int i = 0; i < localeList.size(); i++) {
            localeArr[i] = localeList.get(i);
        }
        for (int i2 = 0; i2 < localeList2.size(); i2++) {
            localeArr[localeList.size() + i2] = localeList2.get(i2);
        }
        return new LocaleList(localeArr);
    }
}
