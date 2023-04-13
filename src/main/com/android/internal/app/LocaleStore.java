package com.android.internal.app;

import android.app.LocaleManager;
import android.content.Context;
import android.p008os.LocaleList;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.content.NativeLibraryHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes4.dex */
public class LocaleStore {
    private static final HashMap<String, LocaleInfo> sLocaleCache = new HashMap<>();
    private static final String TAG = LocaleStore.class.getSimpleName();
    private static boolean sFullyInitialized = false;

    /* loaded from: classes4.dex */
    public static class LocaleInfo implements Serializable {
        static final int SUGGESTION_TYPE_CFG = 2;
        static final int SUGGESTION_TYPE_CURRENT = 4;
        static final int SUGGESTION_TYPE_NONE = 0;
        static final int SUGGESTION_TYPE_OTHER_APP_LANGUAGE = 16;
        static final int SUGGESTION_TYPE_SIM = 1;
        static final int SUGGESTION_TYPE_SYSTEM_LANGUAGE = 8;
        private String mFullCountryNameNative;
        private String mFullNameNative;
        private final String mId;
        private boolean mIsChecked;
        private boolean mIsPseudo;
        private boolean mIsTranslated;
        private String mLangScriptKey;
        private final Locale mLocale;
        private final Locale mParent;
        public int mSuggestionFlags;

        private LocaleInfo(Locale locale) {
            this.mLocale = locale;
            this.mId = locale.toLanguageTag();
            this.mParent = getParent(locale);
            this.mIsChecked = false;
            this.mSuggestionFlags = 0;
            this.mIsTranslated = false;
            this.mIsPseudo = false;
        }

        private LocaleInfo(String localeId) {
            this(Locale.forLanguageTag(localeId));
        }

        private static Locale getParent(Locale locale) {
            if (locale.getCountry().isEmpty()) {
                return null;
            }
            return new Locale.Builder().setLocale(locale).setRegion("").setExtension('u', "").build();
        }

        public String toString() {
            return this.mId;
        }

        public Locale getLocale() {
            return this.mLocale;
        }

        public Locale getParent() {
            return this.mParent;
        }

        public String getId() {
            return this.mId;
        }

        public boolean isTranslated() {
            return this.mIsTranslated;
        }

        public void setTranslated(boolean isTranslated) {
            this.mIsTranslated = isTranslated;
        }

        public boolean isSuggested() {
            return this.mIsTranslated && this.mSuggestionFlags != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isSuggestionOfType(int suggestionMask) {
            return this.mIsTranslated && (this.mSuggestionFlags & suggestionMask) == suggestionMask;
        }

        public String getFullNameNative() {
            if (this.mFullNameNative == null) {
                Locale locale = this.mLocale.stripExtensions();
                this.mFullNameNative = LocaleHelper.getDisplayName(locale, locale, true);
            }
            return this.mFullNameNative;
        }

        public String getFullCountryNameNative() {
            if (this.mFullCountryNameNative == null) {
                Locale locale = this.mLocale;
                this.mFullCountryNameNative = LocaleHelper.getDisplayCountry(locale, locale);
            }
            return this.mFullCountryNameNative;
        }

        String getFullCountryNameInUiLanguage() {
            return LocaleHelper.getDisplayCountry(this.mLocale);
        }

        public String getFullNameInUiLanguage() {
            Locale locale = this.mLocale.stripExtensions();
            return LocaleHelper.getDisplayName(locale, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getLangScriptKey() {
            String languageTag;
            if (this.mLangScriptKey == null) {
                Locale baseLocale = new Locale.Builder().setLocale(this.mLocale).setExtension('u', "").build();
                Locale parentWithScript = getParent(LocaleHelper.addLikelySubtags(baseLocale));
                if (parentWithScript == null) {
                    languageTag = this.mLocale.toLanguageTag();
                } else {
                    languageTag = parentWithScript.toLanguageTag();
                }
                this.mLangScriptKey = languageTag;
            }
            return this.mLangScriptKey;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getLabel(boolean countryMode) {
            if (countryMode) {
                return getFullCountryNameNative();
            }
            return getFullNameNative();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getContentDescription(boolean countryMode) {
            if (countryMode) {
                return getFullCountryNameInUiLanguage();
            }
            return getFullNameInUiLanguage();
        }

        public boolean getChecked() {
            return this.mIsChecked;
        }

        public void setChecked(boolean checked) {
            this.mIsChecked = checked;
        }

        public boolean isAppCurrentLocale() {
            return (this.mSuggestionFlags & 4) > 0;
        }

        public boolean isSystemLocale() {
            return (this.mSuggestionFlags & 8) > 0;
        }
    }

    private static Set<String> getSimCountries(Context context) {
        Set<String> result = new HashSet<>();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (tm != null) {
            String iso = tm.getSimCountryIso().toUpperCase(Locale.US);
            if (!iso.isEmpty()) {
                result.add(iso);
            }
            String iso2 = tm.getNetworkCountryIso().toUpperCase(Locale.US);
            if (!iso2.isEmpty()) {
                result.add(iso2);
            }
        }
        return result;
    }

    public static void updateSimCountries(Context context) {
        Set<String> simCountries = getSimCountries(context);
        for (LocaleInfo li : sLocaleCache.values()) {
            if (simCountries.contains(li.getLocale().getCountry())) {
                li.mSuggestionFlags |= 1;
            }
        }
    }

    public static LocaleInfo getAppActivatedLocaleInfo(Context context, String appPackageName, boolean isAppSelected) {
        LocaleList localeList;
        if (appPackageName == null) {
            return null;
        }
        LocaleManager localeManager = (LocaleManager) context.getSystemService(LocaleManager.class);
        if (localeManager == null) {
            localeList = null;
        } else {
            try {
                localeList = localeManager.getApplicationLocales(appPackageName);
            } catch (IllegalArgumentException e) {
                Log.m111d(TAG, "IllegalArgumentException ", e);
            }
        }
        Locale locale = localeList == null ? null : localeList.get(0);
        if (locale != null) {
            LocaleInfo localeInfo = new LocaleInfo(locale);
            if (isAppSelected) {
                localeInfo.mSuggestionFlags |= 4;
            } else {
                localeInfo.mSuggestionFlags |= 16;
            }
            localeInfo.mIsTranslated = true;
            return localeInfo;
        }
        return null;
    }

    public static Set<LocaleInfo> transformImeLanguageTagToLocaleInfo(List<InputMethodSubtype> list) {
        Set<LocaleInfo> imeLocales = new HashSet<>();
        for (InputMethodSubtype subtype : list) {
            LocaleInfo localeInfo = new LocaleInfo(subtype.getLanguageTag());
            localeInfo.mSuggestionFlags |= 16;
            localeInfo.mIsTranslated = true;
            imeLocales.add(localeInfo);
        }
        return imeLocales;
    }

    public static List<LocaleInfo> getSystemCurrentLocaleInfo() {
        List<LocaleInfo> localeList = new ArrayList<>();
        LocaleList systemLangList = LocaleList.getDefault();
        for (int i = 0; i < systemLangList.size(); i++) {
            LocaleInfo systemLocaleInfo = new LocaleInfo(systemLangList.get(i));
            systemLocaleInfo.mSuggestionFlags |= 1;
            systemLocaleInfo.mIsTranslated = true;
            localeList.add(systemLocaleInfo);
        }
        return localeList;
    }

    public static LocaleInfo getSystemDefaultLocaleInfo(boolean hasAppLanguage) {
        LocaleInfo systemDefaultInfo = new LocaleInfo("");
        systemDefaultInfo.mSuggestionFlags |= 8;
        if (hasAppLanguage) {
            systemDefaultInfo.mSuggestionFlags |= 4;
        }
        systemDefaultInfo.mIsTranslated = true;
        return systemDefaultInfo;
    }

    private static void addSuggestedLocalesForRegion(Locale locale) {
        if (locale == null) {
            return;
        }
        String country = locale.getCountry();
        if (country.isEmpty()) {
            return;
        }
        for (LocaleInfo li : sLocaleCache.values()) {
            if (country.equals(li.getLocale().getCountry())) {
                li.mSuggestionFlags |= 1;
            }
        }
    }

    public static void fillCache(Context context) {
        String[] supportedLocales;
        if (sFullyInitialized) {
            return;
        }
        Set<String> simCountries = getSimCountries(context);
        boolean isInDeveloperMode = Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 0;
        for (String localeId : LocalePicker.getSupportedLocales(context)) {
            if (localeId.isEmpty()) {
                throw new IllformedLocaleException("Bad locale entry in locale_config.xml");
            }
            LocaleInfo li = new LocaleInfo(localeId);
            if (LocaleList.isPseudoLocale(li.getLocale())) {
                if (isInDeveloperMode) {
                    li.setTranslated(true);
                    li.mIsPseudo = true;
                    li.mSuggestionFlags |= 1;
                }
            }
            if (simCountries.contains(li.getLocale().getCountry())) {
                li.mSuggestionFlags |= 1;
            }
            HashMap<String, LocaleInfo> hashMap = sLocaleCache;
            hashMap.put(li.getId(), li);
            Locale parent = li.getParent();
            if (parent != null) {
                String parentId = parent.toLanguageTag();
                if (!hashMap.containsKey(parentId)) {
                    hashMap.put(parentId, new LocaleInfo(parent));
                }
            }
        }
        HashSet<String> localizedLocales = new HashSet<>();
        for (String localeId2 : LocalePicker.getSystemAssetLocales()) {
            LocaleInfo li2 = new LocaleInfo(localeId2);
            String country = li2.getLocale().getCountry();
            if (!country.isEmpty()) {
                LocaleInfo cachedLocale = null;
                HashMap<String, LocaleInfo> hashMap2 = sLocaleCache;
                if (hashMap2.containsKey(li2.getId())) {
                    LocaleInfo cachedLocale2 = hashMap2.get(li2.getId());
                    cachedLocale = cachedLocale2;
                } else {
                    String langScriptCtry = li2.getLangScriptKey() + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + country;
                    if (hashMap2.containsKey(langScriptCtry)) {
                        LocaleInfo cachedLocale3 = hashMap2.get(langScriptCtry);
                        cachedLocale = cachedLocale3;
                    }
                }
                if (cachedLocale != null) {
                    cachedLocale.mSuggestionFlags |= 2;
                }
            }
            localizedLocales.add(li2.getLangScriptKey());
        }
        for (LocaleInfo li3 : sLocaleCache.values()) {
            li3.setTranslated(localizedLocales.contains(li3.getLangScriptKey()));
        }
        addSuggestedLocalesForRegion(Locale.getDefault());
        sFullyInitialized = true;
    }

    private static int getLevel(Set<String> ignorables, LocaleInfo li, boolean translatedOnly) {
        if (ignorables.contains(li.getId())) {
            return 0;
        }
        if (li.mIsPseudo) {
            return 2;
        }
        return ((!translatedOnly || li.isTranslated()) && li.getParent() != null) ? 2 : 0;
    }

    public static Set<LocaleInfo> getLevelLocales(Context context, Set<String> ignorables, LocaleInfo parent, boolean translatedOnly) {
        return getLevelLocales(context, ignorables, parent, translatedOnly, null);
    }

    public static Set<LocaleInfo> getLevelLocales(Context context, Set<String> ignorables, LocaleInfo parent, boolean translatedOnly, LocaleList explicitLocales) {
        HashMap<String, LocaleInfo> supportedLcoaleInfos;
        fillCache(context);
        String parentId = parent == null ? null : parent.getId();
        HashSet<LocaleInfo> result = new HashSet<>();
        if (explicitLocales == null) {
            supportedLcoaleInfos = sLocaleCache;
        } else {
            supportedLcoaleInfos = convertExplicitLocales(explicitLocales, sLocaleCache.values());
        }
        for (LocaleInfo li : supportedLcoaleInfos.values()) {
            int level = getLevel(ignorables, li, translatedOnly);
            if (level == 2) {
                if (parent != null) {
                    if (parentId.equals(li.getParent().toLanguageTag())) {
                        result.add(li);
                    }
                } else if (li.isSuggestionOfType(1)) {
                    result.add(li);
                } else {
                    result.add(getLocaleInfo(li.getParent()));
                }
            }
        }
        return result;
    }

    public static HashMap<String, LocaleInfo> convertExplicitLocales(LocaleList explicitLocales, Collection<LocaleInfo> localeinfo) {
        LocaleList localeList = matchLocaleFromSupportedLocaleList(explicitLocales, localeinfo);
        HashMap<String, LocaleInfo> localeInfos = new HashMap<>();
        for (int i = 0; i < localeList.size(); i++) {
            Locale locale = localeList.get(i);
            if (locale.toString().isEmpty()) {
                throw new IllformedLocaleException("Bad locale entry");
            }
            LocaleInfo li = new LocaleInfo(locale);
            if (!localeInfos.containsKey(li.getId())) {
                localeInfos.put(li.getId(), li);
                Locale parent = li.getParent();
                if (parent != null) {
                    String parentId = parent.toLanguageTag();
                    if (!localeInfos.containsKey(parentId)) {
                        localeInfos.put(parentId, new LocaleInfo(parent));
                    }
                }
            }
        }
        return localeInfos;
    }

    private static LocaleList matchLocaleFromSupportedLocaleList(LocaleList explicitLocales, Collection<LocaleInfo> localeinfo) {
        Locale[] resultLocales = new Locale[explicitLocales.size()];
        for (int i = 0; i < explicitLocales.size(); i++) {
            Locale locale = explicitLocales.get(i).stripExtensions();
            if (!TextUtils.isEmpty(locale.getCountry())) {
                for (LocaleInfo localeInfo : localeinfo) {
                    if (LocaleList.matchesLanguageAndScript(locale, localeInfo.getLocale()) && TextUtils.equals(locale.getCountry(), localeInfo.getLocale().getCountry())) {
                        resultLocales[i] = localeInfo.getLocale();
                    }
                }
            }
            if (resultLocales[i] == null) {
                resultLocales[i] = locale;
            }
        }
        return new LocaleList(resultLocales);
    }

    public static LocaleInfo getLocaleInfo(Locale locale) {
        String id = locale.toLanguageTag();
        HashMap<String, LocaleInfo> hashMap = sLocaleCache;
        if (!hashMap.containsKey(id)) {
            Locale filteredLocale = new Locale.Builder().setLocale(locale.stripExtensions()).setUnicodeLocaleKeyword("nu", locale.getUnicodeLocaleType("nu")).build();
            if (hashMap.containsKey(filteredLocale.toLanguageTag())) {
                LocaleInfo result = new LocaleInfo(locale);
                LocaleInfo localeInfo = hashMap.get(filteredLocale.toLanguageTag());
                result.mIsPseudo = localeInfo.mIsPseudo;
                result.mIsTranslated = localeInfo.mIsTranslated;
                result.mSuggestionFlags = localeInfo.mSuggestionFlags;
                return result;
            }
            LocaleInfo result2 = new LocaleInfo(locale);
            hashMap.put(id, result2);
            return result2;
        }
        return hashMap.get(id);
    }

    public static LocaleInfo fromLocale(Locale locale) {
        return new LocaleInfo(locale);
    }
}
