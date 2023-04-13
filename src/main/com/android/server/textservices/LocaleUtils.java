package com.android.server.textservices;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Locale;
/* loaded from: classes2.dex */
public final class LocaleUtils {
    public static ArrayList<Locale> getSuitableLocalesForSpellChecker(Locale locale) {
        Locale locale2;
        Locale locale3;
        if (locale != null) {
            String language = locale.getLanguage();
            boolean z = !TextUtils.isEmpty(language);
            String country = locale.getCountry();
            boolean z2 = !TextUtils.isEmpty(country);
            String variant = locale.getVariant();
            Locale locale4 = (z && z2 && (TextUtils.isEmpty(variant) ^ true)) ? new Locale(language, country, variant) : null;
            Locale locale5 = (z && z2) ? new Locale(language, country) : null;
            r0 = z ? new Locale(language) : null;
            locale3 = locale5;
            locale2 = r0;
            r0 = locale4;
        } else {
            locale2 = null;
            locale3 = null;
        }
        ArrayList<Locale> arrayList = new ArrayList<>();
        if (r0 != null) {
            arrayList.add(r0);
        }
        Locale locale6 = Locale.ENGLISH;
        if (!locale6.equals(locale2)) {
            if (locale3 != null) {
                arrayList.add(locale3);
            }
            if (locale2 != null) {
                arrayList.add(locale2);
            }
            arrayList.add(Locale.US);
            arrayList.add(Locale.UK);
            arrayList.add(locale6);
        } else if (locale3 != null) {
            arrayList.add(locale3);
            Locale locale7 = Locale.US;
            if (!locale7.equals(locale3)) {
                arrayList.add(locale7);
            }
            if (!Locale.UK.equals(locale3)) {
                arrayList.add(Locale.UK);
            }
            arrayList.add(locale6);
        } else {
            arrayList.add(locale6);
            arrayList.add(Locale.US);
            arrayList.add(Locale.UK);
        }
        return arrayList;
    }
}
