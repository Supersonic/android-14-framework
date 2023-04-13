package com.android.internal.telephony.util;

import android.content.Context;
import android.icu.util.ULocale;
import android.text.TextUtils;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class LocaleUtils {
    public static Locale getLocaleFromMcc(Context context, int i, String str) {
        boolean z = !TextUtils.isEmpty(str);
        if (!z) {
            str = defaultLanguageForMcc(i);
        }
        String countryCodeForMcc = MccTable.countryCodeForMcc(i);
        Rlog.d("LocaleUtils", "getLocaleFromMcc(" + str + ", " + countryCodeForMcc + ", " + i);
        Locale localeForLanguageCountry = getLocaleForLanguageCountry(context, str, countryCodeForMcc);
        if (localeForLanguageCountry == null && z) {
            String defaultLanguageForMcc = defaultLanguageForMcc(i);
            Rlog.d("LocaleUtils", "[retry ] getLocaleFromMcc(" + defaultLanguageForMcc + ", " + countryCodeForMcc + ", " + i);
            return getLocaleForLanguageCountry(context, defaultLanguageForMcc, countryCodeForMcc);
        }
        return localeForLanguageCountry;
    }

    private static Locale getLocaleForLanguageCountry(Context context, String str, String str2) {
        if (str == null) {
            Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: skipping no language");
            return null;
        }
        if (str2 == null) {
            str2 = PhoneConfigurationManager.SSSS;
        }
        Locale locale = new Locale(str, str2);
        try {
            ArrayList<String> arrayList = new ArrayList(Arrays.asList(context.getAssets().getLocales()));
            arrayList.remove("ar-XB");
            arrayList.remove("en-XA");
            ArrayList arrayList2 = new ArrayList();
            for (String str3 : arrayList) {
                Locale forLanguageTag = Locale.forLanguageTag(str3.replace('_', '-'));
                if (forLanguageTag != null && !"und".equals(forLanguageTag.getLanguage()) && !forLanguageTag.getLanguage().isEmpty() && !forLanguageTag.getCountry().isEmpty() && forLanguageTag.getLanguage().equals(locale.getLanguage())) {
                    if (forLanguageTag.getCountry().equals(locale.getCountry())) {
                        Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: got perfect match: " + forLanguageTag.toLanguageTag());
                        return forLanguageTag;
                    }
                    arrayList2.add(forLanguageTag);
                }
            }
            if (arrayList2.isEmpty()) {
                Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: no locales for language " + str);
                return null;
            }
            Locale lookupFallback = lookupFallback(locale, arrayList2);
            if (lookupFallback != null) {
                Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: got a fallback match: " + lookupFallback.toLanguageTag());
                return lookupFallback;
            } else if (!TextUtils.isEmpty(locale.getCountry()) && isTranslated(context, locale)) {
                Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: target locale is translated: " + locale);
                return locale;
            } else {
                Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: got language-only match: " + str);
                return (Locale) arrayList2.get(0);
            }
        } catch (Exception e) {
            Rlog.d("LocaleUtils", "getLocaleForLanguageCountry: exception", e);
            return null;
        }
    }

    public static String defaultLanguageForMcc(int i) {
        MccTable.MccEntry entryForMcc = MccTable.entryForMcc(i);
        if (entryForMcc == null) {
            Rlog.d("LocaleUtils", "defaultLanguageForMcc(" + i + "): no country for mcc");
            return null;
        }
        String str = entryForMcc.mIso;
        if ("in".equals(str)) {
            return "en";
        }
        String language = ULocale.addLikelySubtags(new ULocale("und", str)).getLanguage();
        Rlog.d("LocaleUtils", "defaultLanguageForMcc(" + i + "): country " + str + " uses " + language);
        return language;
    }

    private static boolean isTranslated(Context context, Locale locale) {
        ULocale addLikelySubtags = ULocale.addLikelySubtags(ULocale.forLocale(locale));
        String language = addLikelySubtags.getLanguage();
        String script = addLikelySubtags.getScript();
        for (String str : context.getAssets().getLocales()) {
            ULocale addLikelySubtags2 = ULocale.addLikelySubtags(new ULocale(str));
            if (language.equals(addLikelySubtags2.getLanguage()) && script.equals(addLikelySubtags2.getScript())) {
                return true;
            }
        }
        return false;
    }

    private static Locale lookupFallback(Locale locale, List<Locale> list) {
        do {
            locale = MccTable.FALLBACKS.get(locale);
            if (locale == null) {
                return null;
            }
        } while (!list.contains(locale));
        return locale;
    }
}
