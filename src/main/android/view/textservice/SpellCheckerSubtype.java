package android.view.textservice;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.inputmethod.SubtypeLocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes4.dex */
public final class SpellCheckerSubtype implements Parcelable {
    private static final String EXTRA_VALUE_KEY_VALUE_SEPARATOR = "=";
    private static final String EXTRA_VALUE_PAIR_SEPARATOR = ",";
    public static final int SUBTYPE_ID_NONE = 0;
    private static final String SUBTYPE_LANGUAGE_TAG_NONE = "";
    private HashMap<String, String> mExtraValueHashMapCache;
    private final String mSubtypeExtraValue;
    private final int mSubtypeHashCode;
    private final int mSubtypeId;
    private final String mSubtypeLanguageTag;
    private final String mSubtypeLocale;
    private final int mSubtypeNameResId;
    private static final String TAG = SpellCheckerSubtype.class.getSimpleName();
    public static final Parcelable.Creator<SpellCheckerSubtype> CREATOR = new Parcelable.Creator<SpellCheckerSubtype>() { // from class: android.view.textservice.SpellCheckerSubtype.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SpellCheckerSubtype createFromParcel(Parcel source) {
            return new SpellCheckerSubtype(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SpellCheckerSubtype[] newArray(int size) {
            return new SpellCheckerSubtype[size];
        }
    };

    public SpellCheckerSubtype(int nameId, String locale, String languageTag, String extraValue, int subtypeId) {
        this.mSubtypeNameResId = nameId;
        String str = locale != null ? locale : "";
        this.mSubtypeLocale = str;
        this.mSubtypeLanguageTag = languageTag != null ? languageTag : "";
        String str2 = extraValue != null ? extraValue : "";
        this.mSubtypeExtraValue = str2;
        this.mSubtypeId = subtypeId;
        this.mSubtypeHashCode = subtypeId != 0 ? subtypeId : hashCodeInternal(str, str2);
    }

    @Deprecated
    public SpellCheckerSubtype(int nameId, String locale, String extraValue) {
        this(nameId, locale, "", extraValue, 0);
    }

    SpellCheckerSubtype(Parcel source) {
        this.mSubtypeNameResId = source.readInt();
        String s = source.readString();
        String str = s != null ? s : "";
        this.mSubtypeLocale = str;
        String s2 = source.readString();
        this.mSubtypeLanguageTag = s2 != null ? s2 : "";
        String s3 = source.readString();
        String str2 = s3 != null ? s3 : "";
        this.mSubtypeExtraValue = str2;
        int readInt = source.readInt();
        this.mSubtypeId = readInt;
        this.mSubtypeHashCode = readInt == 0 ? hashCodeInternal(str, str2) : readInt;
    }

    public int getNameResId() {
        return this.mSubtypeNameResId;
    }

    @Deprecated
    public String getLocale() {
        return this.mSubtypeLocale;
    }

    public String getLanguageTag() {
        return this.mSubtypeLanguageTag;
    }

    public String getExtraValue() {
        return this.mSubtypeExtraValue;
    }

    private HashMap<String, String> getExtraValueHashMap() {
        if (this.mExtraValueHashMapCache == null) {
            this.mExtraValueHashMapCache = new HashMap<>();
            String[] pairs = this.mSubtypeExtraValue.split(",");
            for (String str : pairs) {
                String[] pair = str.split(EXTRA_VALUE_KEY_VALUE_SEPARATOR);
                if (pair.length == 1) {
                    this.mExtraValueHashMapCache.put(pair[0], null);
                } else if (pair.length > 1) {
                    if (pair.length > 2) {
                        Slog.m90w(TAG, "ExtraValue has two or more '='s");
                    }
                    this.mExtraValueHashMapCache.put(pair[0], pair[1]);
                }
            }
        }
        return this.mExtraValueHashMapCache;
    }

    public boolean containsExtraValueKey(String key) {
        return getExtraValueHashMap().containsKey(key);
    }

    public String getExtraValueOf(String key) {
        return getExtraValueHashMap().get(key);
    }

    public int hashCode() {
        return this.mSubtypeHashCode;
    }

    public boolean equals(Object o) {
        if (o instanceof SpellCheckerSubtype) {
            SpellCheckerSubtype subtype = (SpellCheckerSubtype) o;
            return (subtype.mSubtypeId == 0 && this.mSubtypeId == 0) ? subtype.hashCode() == hashCode() && subtype.getNameResId() == getNameResId() && subtype.getLocale().equals(getLocale()) && subtype.getLanguageTag().equals(getLanguageTag()) && subtype.getExtraValue().equals(getExtraValue()) : subtype.hashCode() == hashCode();
        }
        return false;
    }

    public Locale getLocaleObject() {
        if (!TextUtils.isEmpty(this.mSubtypeLanguageTag)) {
            return Locale.forLanguageTag(this.mSubtypeLanguageTag);
        }
        return SubtypeLocaleUtils.constructLocaleFromString(this.mSubtypeLocale);
    }

    public CharSequence getDisplayName(Context context, String packageName, ApplicationInfo appInfo) {
        Locale locale = getLocaleObject();
        String localeStr = locale != null ? locale.getDisplayName() : this.mSubtypeLocale;
        if (this.mSubtypeNameResId == 0) {
            return localeStr;
        }
        CharSequence subtypeName = context.getPackageManager().getText(packageName, this.mSubtypeNameResId, appInfo);
        if (!TextUtils.isEmpty(subtypeName)) {
            return String.format(subtypeName.toString(), localeStr);
        }
        return localeStr;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.mSubtypeNameResId);
        dest.writeString(this.mSubtypeLocale);
        dest.writeString(this.mSubtypeLanguageTag);
        dest.writeString(this.mSubtypeExtraValue);
        dest.writeInt(this.mSubtypeId);
    }

    private static int hashCodeInternal(String locale, String extraValue) {
        return Arrays.hashCode(new Object[]{locale, extraValue});
    }

    public static List<SpellCheckerSubtype> sort(Context context, int flags, SpellCheckerInfo sci, List<SpellCheckerSubtype> subtypeList) {
        if (sci == null) {
            return subtypeList;
        }
        HashSet<SpellCheckerSubtype> subtypesSet = new HashSet<>(subtypeList);
        ArrayList<SpellCheckerSubtype> sortedList = new ArrayList<>();
        int N = sci.getSubtypeCount();
        for (int i = 0; i < N; i++) {
            SpellCheckerSubtype subtype = sci.getSubtypeAt(i);
            if (subtypesSet.contains(subtype)) {
                sortedList.add(subtype);
                subtypesSet.remove(subtype);
            }
        }
        Iterator<SpellCheckerSubtype> it = subtypesSet.iterator();
        while (it.hasNext()) {
            sortedList.add(it.next());
        }
        return sortedList;
    }
}
