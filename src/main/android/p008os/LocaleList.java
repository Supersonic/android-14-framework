package android.p008os;

import android.icu.util.ULocale;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
/* renamed from: android.os.LocaleList */
/* loaded from: classes3.dex */
public final class LocaleList implements Parcelable {
    private static final int NUM_PSEUDO_LOCALES = 2;
    private static final String STRING_AR_XB = "ar-XB";
    private static final String STRING_EN_XA = "en-XA";
    private final Locale[] mList;
    private final String mStringRepresentation;
    private static final Locale[] sEmptyList = new Locale[0];
    private static final LocaleList sEmptyLocaleList = new LocaleList(new Locale[0]);
    public static final Parcelable.Creator<LocaleList> CREATOR = new Parcelable.Creator<LocaleList>() { // from class: android.os.LocaleList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocaleList createFromParcel(Parcel source) {
            return LocaleList.forLanguageTags(source.readString8());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocaleList[] newArray(int size) {
            return new LocaleList[size];
        }
    };
    private static final Locale LOCALE_EN_XA = new Locale("en", "XA");
    private static final Locale LOCALE_AR_XB = new Locale("ar", "XB");
    private static final Locale EN_LATN = Locale.forLanguageTag("en-Latn");
    private static final Object sLock = new Object();
    private static LocaleList sLastExplicitlySetLocaleList = null;
    private static LocaleList sDefaultLocaleList = null;
    private static LocaleList sDefaultAdjustedLocaleList = null;
    private static Locale sLastDefaultLocale = null;

    public Locale get(int index) {
        if (index >= 0) {
            Locale[] localeArr = this.mList;
            if (index < localeArr.length) {
                return localeArr[index];
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.mList.length == 0;
    }

    public int size() {
        return this.mList.length;
    }

    public int indexOf(Locale locale) {
        int i = 0;
        while (true) {
            Locale[] localeArr = this.mList;
            if (i < localeArr.length) {
                if (!localeArr[i].equals(locale)) {
                    i++;
                } else {
                    return i;
                }
            } else {
                return -1;
            }
        }
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LocaleList)) {
            return false;
        }
        Locale[] otherList = ((LocaleList) other).mList;
        if (this.mList.length != otherList.length) {
            return false;
        }
        int i = 0;
        while (true) {
            Locale[] localeArr = this.mList;
            if (i >= localeArr.length) {
                return true;
            }
            if (!localeArr[i].equals(otherList[i])) {
                return false;
            }
            i++;
        }
    }

    public int hashCode() {
        int result = 1;
        int i = 0;
        while (true) {
            Locale[] localeArr = this.mList;
            if (i < localeArr.length) {
                result = (result * 31) + localeArr[i].hashCode();
                i++;
            } else {
                return result;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NavigationBarInflaterView.SIZE_MOD_START);
        int i = 0;
        while (true) {
            Locale[] localeArr = this.mList;
            if (i < localeArr.length) {
                sb.append(localeArr[i]);
                if (i < this.mList.length - 1) {
                    sb.append(',');
                }
                i++;
            } else {
                sb.append(NavigationBarInflaterView.SIZE_MOD_END);
                return sb.toString();
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString8(this.mStringRepresentation);
    }

    public String toLanguageTags() {
        return this.mStringRepresentation;
    }

    public LocaleList(Locale... list) {
        if (list.length == 0) {
            this.mList = sEmptyList;
            this.mStringRepresentation = "";
            return;
        }
        ArrayList<Locale> localeList = new ArrayList<>();
        HashSet<Locale> seenLocales = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            Locale l = list[i];
            if (l == null) {
                throw new NullPointerException("list[" + i + "] is null");
            }
            if (!seenLocales.contains(l)) {
                Locale localeClone = (Locale) l.clone();
                localeList.add(localeClone);
                sb.append(localeClone.toLanguageTag());
                if (i < list.length - 1) {
                    sb.append(',');
                }
                seenLocales.add(localeClone);
            }
        }
        int i2 = localeList.size();
        this.mList = (Locale[]) localeList.toArray(new Locale[i2]);
        this.mStringRepresentation = sb.toString();
    }

    public LocaleList(Locale topLocale, LocaleList otherLocales) {
        if (topLocale == null) {
            throw new NullPointerException("topLocale is null");
        }
        int inputLength = otherLocales == null ? 0 : otherLocales.mList.length;
        int topLocaleIndex = -1;
        int i = 0;
        while (true) {
            if (i < inputLength) {
                if (!topLocale.equals(otherLocales.mList[i])) {
                    i++;
                } else {
                    topLocaleIndex = i;
                    break;
                }
            } else {
                break;
            }
        }
        int outputLength = (topLocaleIndex == -1 ? 1 : 0) + inputLength;
        Locale[] localeList = new Locale[outputLength];
        localeList[0] = (Locale) topLocale.clone();
        if (topLocaleIndex == -1) {
            for (int i2 = 0; i2 < inputLength; i2++) {
                localeList[i2 + 1] = (Locale) otherLocales.mList[i2].clone();
            }
        } else {
            for (int i3 = 0; i3 < topLocaleIndex; i3++) {
                localeList[i3 + 1] = (Locale) otherLocales.mList[i3].clone();
            }
            for (int i4 = topLocaleIndex + 1; i4 < inputLength; i4++) {
                localeList[i4] = (Locale) otherLocales.mList[i4].clone();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i5 = 0; i5 < outputLength; i5++) {
            sb.append(localeList[i5].toLanguageTag());
            if (i5 < outputLength - 1) {
                sb.append(',');
            }
        }
        this.mList = localeList;
        this.mStringRepresentation = sb.toString();
    }

    public static LocaleList getEmptyLocaleList() {
        return sEmptyLocaleList;
    }

    public static LocaleList forLanguageTags(String list) {
        if (list == null || list.equals("")) {
            return getEmptyLocaleList();
        }
        String[] tags = list.split(",");
        Locale[] localeArray = new Locale[tags.length];
        for (int i = 0; i < localeArray.length; i++) {
            localeArray[i] = Locale.forLanguageTag(tags[i]);
        }
        return new LocaleList(localeArray);
    }

    private static String getLikelyScript(Locale locale) {
        String script = locale.getScript();
        if (!script.isEmpty()) {
            return script;
        }
        return ULocale.addLikelySubtags(ULocale.forLocale(locale)).getScript();
    }

    private static boolean isPseudoLocale(String locale) {
        return STRING_EN_XA.equals(locale) || STRING_AR_XB.equals(locale);
    }

    public static boolean isPseudoLocale(Locale locale) {
        return LOCALE_EN_XA.equals(locale) || LOCALE_AR_XB.equals(locale);
    }

    public static boolean isPseudoLocale(ULocale locale) {
        return isPseudoLocale(locale != null ? locale.toLocale() : null);
    }

    public static boolean matchesLanguageAndScript(Locale supported, Locale desired) {
        if (supported.equals(desired)) {
            return true;
        }
        if (!supported.getLanguage().equals(desired.getLanguage()) || isPseudoLocale(supported) || isPseudoLocale(desired)) {
            return false;
        }
        String supportedScr = getLikelyScript(supported);
        if (supportedScr.isEmpty()) {
            String supportedRegion = supported.getCountry();
            return supportedRegion.isEmpty() || supportedRegion.equals(desired.getCountry());
        }
        String desiredScr = getLikelyScript(desired);
        return supportedScr.equals(desiredScr);
    }

    private int findFirstMatchIndex(Locale supportedLocale) {
        int idx = 0;
        while (true) {
            Locale[] localeArr = this.mList;
            if (idx < localeArr.length) {
                if (!matchesLanguageAndScript(supportedLocale, localeArr[idx])) {
                    idx++;
                } else {
                    return idx;
                }
            } else {
                return Integer.MAX_VALUE;
            }
        }
    }

    private int computeFirstMatchIndex(Collection<String> supportedLocales, boolean assumeEnglishIsSupported) {
        Locale[] localeArr = this.mList;
        if (localeArr.length == 1) {
            return 0;
        }
        if (localeArr.length == 0) {
            return -1;
        }
        int bestIndex = Integer.MAX_VALUE;
        if (assumeEnglishIsSupported) {
            int idx = findFirstMatchIndex(EN_LATN);
            if (idx == 0) {
                return 0;
            }
            if (idx < Integer.MAX_VALUE) {
                bestIndex = idx;
            }
        }
        for (String languageTag : supportedLocales) {
            Locale supportedLocale = Locale.forLanguageTag(languageTag);
            int idx2 = findFirstMatchIndex(supportedLocale);
            if (idx2 == 0) {
                return 0;
            }
            if (idx2 < bestIndex) {
                bestIndex = idx2;
            }
        }
        if (bestIndex == Integer.MAX_VALUE) {
            return 0;
        }
        return bestIndex;
    }

    private Locale computeFirstMatch(Collection<String> supportedLocales, boolean assumeEnglishIsSupported) {
        int bestIndex = computeFirstMatchIndex(supportedLocales, assumeEnglishIsSupported);
        if (bestIndex == -1) {
            return null;
        }
        return this.mList[bestIndex];
    }

    public Locale getFirstMatch(String[] supportedLocales) {
        return computeFirstMatch(Arrays.asList(supportedLocales), false);
    }

    public int getFirstMatchIndex(String[] supportedLocales) {
        return computeFirstMatchIndex(Arrays.asList(supportedLocales), false);
    }

    public Locale getFirstMatchWithEnglishSupported(String[] supportedLocales) {
        return computeFirstMatch(Arrays.asList(supportedLocales), true);
    }

    public int getFirstMatchIndexWithEnglishSupported(Collection<String> supportedLocales) {
        return computeFirstMatchIndex(supportedLocales, true);
    }

    public int getFirstMatchIndexWithEnglishSupported(String[] supportedLocales) {
        return getFirstMatchIndexWithEnglishSupported(Arrays.asList(supportedLocales));
    }

    public static boolean isPseudoLocalesOnly(String[] supportedLocales) {
        if (supportedLocales == null) {
            return true;
        }
        if (supportedLocales.length > 3) {
            return false;
        }
        for (String locale : supportedLocales) {
            if (!locale.isEmpty() && !isPseudoLocale(locale)) {
                return false;
            }
        }
        return true;
    }

    public static LocaleList getDefault() {
        Locale defaultLocale = Locale.getDefault();
        synchronized (sLock) {
            if (!defaultLocale.equals(sLastDefaultLocale)) {
                sLastDefaultLocale = defaultLocale;
                LocaleList localeList = sDefaultLocaleList;
                if (localeList != null && defaultLocale.equals(localeList.get(0))) {
                    return sDefaultLocaleList;
                }
                LocaleList localeList2 = new LocaleList(defaultLocale, sLastExplicitlySetLocaleList);
                sDefaultLocaleList = localeList2;
                sDefaultAdjustedLocaleList = localeList2;
            }
            return sDefaultLocaleList;
        }
    }

    public static LocaleList getAdjustedDefault() {
        LocaleList localeList;
        getDefault();
        synchronized (sLock) {
            localeList = sDefaultAdjustedLocaleList;
        }
        return localeList;
    }

    public static void setDefault(LocaleList locales) {
        setDefault(locales, 0);
    }

    public static void setDefault(LocaleList locales, int localeIndex) {
        if (locales == null) {
            throw new NullPointerException("locales is null");
        }
        if (locales.isEmpty()) {
            throw new IllegalArgumentException("locales is empty");
        }
        synchronized (sLock) {
            Locale locale = locales.get(localeIndex);
            sLastDefaultLocale = locale;
            Locale.setDefault(locale);
            sLastExplicitlySetLocaleList = locales;
            sDefaultLocaleList = locales;
            if (localeIndex == 0) {
                sDefaultAdjustedLocaleList = locales;
            } else {
                sDefaultAdjustedLocaleList = new LocaleList(sLastDefaultLocale, locales);
            }
        }
    }
}
