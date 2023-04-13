package com.android.server.inputmethod;

import android.content.Context;
import android.content.res.Resources;
import android.icu.util.ULocale;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public final class LocaleUtils {

    /* loaded from: classes.dex */
    public interface LocaleExtractor<T> {
        Locale get(T t);
    }

    public static byte calculateMatchingSubScore(ULocale uLocale, ULocale uLocale2) {
        if (uLocale.equals(uLocale2)) {
            return (byte) 3;
        }
        String script = uLocale.getScript();
        if (script.isEmpty() || !script.equals(uLocale2.getScript())) {
            return (byte) 1;
        }
        String country = uLocale.getCountry();
        return (country.isEmpty() || !country.equals(uLocale2.getCountry())) ? (byte) 2 : (byte) 3;
    }

    /* loaded from: classes.dex */
    public static final class ScoreEntry implements Comparable<ScoreEntry> {
        public int mIndex = -1;
        public final byte[] mScore;

        public ScoreEntry(byte[] bArr, int i) {
            this.mScore = new byte[bArr.length];
            set(bArr, i);
        }

        public final void set(byte[] bArr, int i) {
            int i2 = 0;
            while (true) {
                byte[] bArr2 = this.mScore;
                if (i2 < bArr2.length) {
                    bArr2[i2] = bArr[i2];
                    i2++;
                } else {
                    this.mIndex = i;
                    return;
                }
            }
        }

        public void updateIfBetter(byte[] bArr, int i) {
            if (compare(this.mScore, bArr) == -1) {
                set(bArr, i);
            }
        }

        public static int compare(byte[] bArr, byte[] bArr2) {
            for (int i = 0; i < bArr.length; i++) {
                byte b = bArr[i];
                byte b2 = bArr2[i];
                if (b > b2) {
                    return 1;
                }
                if (b < b2) {
                    return -1;
                }
            }
            return 0;
        }

        @Override // java.lang.Comparable
        public int compareTo(ScoreEntry scoreEntry) {
            return compare(this.mScore, scoreEntry.mScore) * (-1);
        }
    }

    public static <T> void filterByLanguage(List<T> list, LocaleExtractor<T> localeExtractor, LocaleList localeList, ArrayList<T> arrayList) {
        if (localeList.isEmpty()) {
            return;
        }
        int size = localeList.size();
        ArrayMap arrayMap = new ArrayMap();
        byte[] bArr = new byte[size];
        ULocale[] uLocaleArr = new ULocale[size];
        int size2 = list.size();
        for (int i = 0; i < size2; i++) {
            Locale locale = localeExtractor.get(list.get(i));
            if (locale != null) {
                boolean z = true;
                for (int i2 = 0; i2 < size; i2++) {
                    Locale locale2 = localeList.get(i2);
                    if (!TextUtils.equals(locale.getLanguage(), locale2.getLanguage())) {
                        bArr[i2] = 0;
                    } else {
                        if (uLocaleArr[i2] == null) {
                            uLocaleArr[i2] = ULocale.addLikelySubtags(ULocale.forLocale(locale2));
                        }
                        byte calculateMatchingSubScore = calculateMatchingSubScore(uLocaleArr[i2], ULocale.addLikelySubtags(ULocale.forLocale(locale)));
                        bArr[i2] = calculateMatchingSubScore;
                        if (z && calculateMatchingSubScore != 0) {
                            z = false;
                        }
                    }
                }
                if (!z) {
                    String language = locale.getLanguage();
                    ScoreEntry scoreEntry = (ScoreEntry) arrayMap.get(language);
                    if (scoreEntry == null) {
                        arrayMap.put(language, new ScoreEntry(bArr, i));
                    } else {
                        scoreEntry.updateIfBetter(bArr, i);
                    }
                }
            }
        }
        int size3 = arrayMap.size();
        ScoreEntry[] scoreEntryArr = new ScoreEntry[size3];
        for (int i3 = 0; i3 < size3; i3++) {
            scoreEntryArr[i3] = (ScoreEntry) arrayMap.valueAt(i3);
        }
        Arrays.sort(scoreEntryArr);
        for (int i4 = 0; i4 < size3; i4++) {
            arrayList.add(list.get(scoreEntryArr[i4].mIndex));
        }
    }

    public static String getLanguageFromLocaleString(String str) {
        int indexOf = str.indexOf(95);
        return indexOf < 0 ? str : str.substring(0, indexOf);
    }

    public static Locale getSystemLocaleFromContext(Context context) {
        try {
            return context.getResources().getConfiguration().locale;
        } catch (Resources.NotFoundException unused) {
            return null;
        }
    }
}
