package android.view.inputmethod;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Configuration;
import android.icu.text.DisplayContext;
import android.icu.text.LocaleDisplayNames;
import android.icu.util.ULocale;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.inputmethod.SubtypeLocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class InputMethodSubtype implements Parcelable {
    private static final String EXTRA_KEY_UNTRANSLATABLE_STRING_IN_SUBTYPE_NAME = "UntranslatableReplacementStringInSubtypeName";
    private static final String EXTRA_VALUE_KEY_VALUE_SEPARATOR = "=";
    private static final String EXTRA_VALUE_PAIR_SEPARATOR = ",";
    private static final String LANGUAGE_TAG_NONE = "";
    public static final int SUBTYPE_ID_NONE = 0;
    private static final String SUBTYPE_MODE_KEYBOARD = "keyboard";
    private static final String UNDEFINED_LANGUAGE_TAG = "und";
    private volatile String mCachedCanonicalizedLanguageTag;
    private volatile Locale mCachedLocaleObj;
    private volatile HashMap<String, String> mExtraValueHashMapCache;
    private final boolean mIsAsciiCapable;
    private final boolean mIsAuxiliary;
    private final Object mLock;
    private final boolean mOverridesImplicitlyEnabledSubtype;
    private final String mPkLanguageTag;
    private final String mPkLayoutType;
    private final String mSubtypeExtraValue;
    private final int mSubtypeHashCode;
    private final int mSubtypeIconResId;
    private final int mSubtypeId;
    private final String mSubtypeLanguageTag;
    private final String mSubtypeLocale;
    private final String mSubtypeMode;
    private final CharSequence mSubtypeNameOverride;
    private final int mSubtypeNameResId;
    private static final String TAG = InputMethodSubtype.class.getSimpleName();
    public static final Parcelable.Creator<InputMethodSubtype> CREATOR = new Parcelable.Creator<InputMethodSubtype>() { // from class: android.view.inputmethod.InputMethodSubtype.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMethodSubtype createFromParcel(Parcel source) {
            return new InputMethodSubtype(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputMethodSubtype[] newArray(int size) {
            return new InputMethodSubtype[size];
        }
    };

    /* loaded from: classes4.dex */
    public static class InputMethodSubtypeBuilder {
        private boolean mIsAuxiliary = false;
        private boolean mOverridesImplicitlyEnabledSubtype = false;
        private boolean mIsAsciiCapable = false;
        private int mSubtypeIconResId = 0;
        private int mSubtypeNameResId = 0;
        private CharSequence mSubtypeNameOverride = "";
        private String mPkLanguageTag = "";
        private String mPkLayoutType = "";
        private int mSubtypeId = 0;
        private String mSubtypeLocale = "";
        private String mSubtypeLanguageTag = "";
        private String mSubtypeMode = "";
        private String mSubtypeExtraValue = "";

        public InputMethodSubtypeBuilder setIsAuxiliary(boolean isAuxiliary) {
            this.mIsAuxiliary = isAuxiliary;
            return this;
        }

        public InputMethodSubtypeBuilder setOverridesImplicitlyEnabledSubtype(boolean overridesImplicitlyEnabledSubtype) {
            this.mOverridesImplicitlyEnabledSubtype = overridesImplicitlyEnabledSubtype;
            return this;
        }

        public InputMethodSubtypeBuilder setIsAsciiCapable(boolean isAsciiCapable) {
            this.mIsAsciiCapable = isAsciiCapable;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeIconResId(int subtypeIconResId) {
            this.mSubtypeIconResId = subtypeIconResId;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeNameResId(int subtypeNameResId) {
            this.mSubtypeNameResId = subtypeNameResId;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeNameOverride(CharSequence nameOverride) {
            this.mSubtypeNameOverride = nameOverride;
            return this;
        }

        public InputMethodSubtypeBuilder setPhysicalKeyboardHint(ULocale languageTag, String layoutType) {
            Objects.requireNonNull(layoutType, "layoutType cannot be null");
            this.mPkLanguageTag = languageTag == null ? "" : languageTag.toLanguageTag();
            this.mPkLayoutType = layoutType;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeId(int subtypeId) {
            this.mSubtypeId = subtypeId;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeLocale(String subtypeLocale) {
            this.mSubtypeLocale = subtypeLocale == null ? "" : subtypeLocale;
            return this;
        }

        public InputMethodSubtypeBuilder setLanguageTag(String languageTag) {
            this.mSubtypeLanguageTag = languageTag == null ? "" : languageTag;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeMode(String subtypeMode) {
            this.mSubtypeMode = subtypeMode == null ? "" : subtypeMode;
            return this;
        }

        public InputMethodSubtypeBuilder setSubtypeExtraValue(String subtypeExtraValue) {
            this.mSubtypeExtraValue = subtypeExtraValue == null ? "" : subtypeExtraValue;
            return this;
        }

        public InputMethodSubtype build() {
            return new InputMethodSubtype(this);
        }
    }

    private static InputMethodSubtypeBuilder getBuilder(int nameId, int iconId, String locale, String mode, String extraValue, boolean isAuxiliary, boolean overridesImplicitlyEnabledSubtype, int id, boolean isAsciiCapable) {
        InputMethodSubtypeBuilder builder = new InputMethodSubtypeBuilder();
        builder.mSubtypeNameResId = nameId;
        builder.mSubtypeIconResId = iconId;
        builder.mSubtypeLocale = locale;
        builder.mSubtypeMode = mode;
        builder.mSubtypeExtraValue = extraValue;
        builder.mIsAuxiliary = isAuxiliary;
        builder.mOverridesImplicitlyEnabledSubtype = overridesImplicitlyEnabledSubtype;
        builder.mSubtypeId = id;
        builder.mIsAsciiCapable = isAsciiCapable;
        return builder;
    }

    @Deprecated
    public InputMethodSubtype(int nameId, int iconId, String locale, String mode, String extraValue, boolean isAuxiliary, boolean overridesImplicitlyEnabledSubtype) {
        this(nameId, iconId, locale, mode, extraValue, isAuxiliary, overridesImplicitlyEnabledSubtype, 0);
    }

    @Deprecated
    public InputMethodSubtype(int nameId, int iconId, String locale, String mode, String extraValue, boolean isAuxiliary, boolean overridesImplicitlyEnabledSubtype, int id) {
        this(getBuilder(nameId, iconId, locale, mode, extraValue, isAuxiliary, overridesImplicitlyEnabledSubtype, id, false));
    }

    private InputMethodSubtype(InputMethodSubtypeBuilder builder) {
        this.mLock = new Object();
        this.mSubtypeNameResId = builder.mSubtypeNameResId;
        this.mSubtypeNameOverride = builder.mSubtypeNameOverride;
        this.mPkLanguageTag = builder.mPkLanguageTag;
        this.mPkLayoutType = builder.mPkLayoutType;
        this.mSubtypeIconResId = builder.mSubtypeIconResId;
        String str = builder.mSubtypeLocale;
        this.mSubtypeLocale = str;
        this.mSubtypeLanguageTag = builder.mSubtypeLanguageTag;
        String str2 = builder.mSubtypeMode;
        this.mSubtypeMode = str2;
        String str3 = builder.mSubtypeExtraValue;
        this.mSubtypeExtraValue = str3;
        boolean z = builder.mIsAuxiliary;
        this.mIsAuxiliary = z;
        boolean z2 = builder.mOverridesImplicitlyEnabledSubtype;
        this.mOverridesImplicitlyEnabledSubtype = z2;
        int i = builder.mSubtypeId;
        this.mSubtypeId = i;
        boolean z3 = builder.mIsAsciiCapable;
        this.mIsAsciiCapable = z3;
        if (i != 0) {
            this.mSubtypeHashCode = i;
        } else {
            this.mSubtypeHashCode = hashCodeInternal(str, str2, str3, z, z2, z3);
        }
    }

    InputMethodSubtype(Parcel source) {
        this.mLock = new Object();
        this.mSubtypeNameResId = source.readInt();
        CharSequence cs = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.mSubtypeNameOverride = cs != null ? cs : "";
        String s = source.readString8();
        this.mPkLanguageTag = s != null ? s : "";
        String s2 = source.readString8();
        this.mPkLayoutType = s2 != null ? s2 : "";
        this.mSubtypeIconResId = source.readInt();
        String s3 = source.readString();
        this.mSubtypeLocale = s3 != null ? s3 : "";
        String s4 = source.readString();
        this.mSubtypeLanguageTag = s4 != null ? s4 : "";
        String s5 = source.readString();
        this.mSubtypeMode = s5 != null ? s5 : "";
        String s6 = source.readString();
        this.mSubtypeExtraValue = s6 != null ? s6 : "";
        this.mIsAuxiliary = source.readInt() == 1;
        this.mOverridesImplicitlyEnabledSubtype = source.readInt() == 1;
        this.mSubtypeHashCode = source.readInt();
        this.mSubtypeId = source.readInt();
        this.mIsAsciiCapable = source.readInt() == 1;
    }

    public int getNameResId() {
        return this.mSubtypeNameResId;
    }

    public CharSequence getNameOverride() {
        return this.mSubtypeNameOverride;
    }

    public ULocale getPhysicalKeyboardHintLanguageTag() {
        if (TextUtils.isEmpty(this.mPkLanguageTag)) {
            return null;
        }
        return ULocale.forLanguageTag(this.mPkLanguageTag);
    }

    public String getPhysicalKeyboardHintLayoutType() {
        return this.mPkLayoutType;
    }

    public int getIconResId() {
        return this.mSubtypeIconResId;
    }

    @Deprecated
    public String getLocale() {
        return this.mSubtypeLocale;
    }

    public String getLanguageTag() {
        return this.mSubtypeLanguageTag;
    }

    public Locale getLocaleObject() {
        if (this.mCachedLocaleObj != null) {
            return this.mCachedLocaleObj;
        }
        synchronized (this.mLock) {
            if (this.mCachedLocaleObj != null) {
                return this.mCachedLocaleObj;
            }
            if (!TextUtils.isEmpty(this.mSubtypeLanguageTag)) {
                this.mCachedLocaleObj = Locale.forLanguageTag(this.mSubtypeLanguageTag);
            } else {
                this.mCachedLocaleObj = SubtypeLocaleUtils.constructLocaleFromString(this.mSubtypeLocale);
            }
            return this.mCachedLocaleObj;
        }
    }

    public String getCanonicalizedLanguageTag() {
        String cachedValue = this.mCachedCanonicalizedLanguageTag;
        if (cachedValue != null) {
            return cachedValue;
        }
        String result = null;
        Locale locale = getLocaleObject();
        if (locale != null) {
            String langTag = locale.toLanguageTag();
            if (!TextUtils.isEmpty(langTag)) {
                result = ULocale.createCanonical(ULocale.forLanguageTag(langTag)).toLanguageTag();
            }
        }
        String result2 = TextUtils.emptyIfNull(result);
        this.mCachedCanonicalizedLanguageTag = result2;
        return result2;
    }

    public boolean isSuitableForPhysicalKeyboardLayoutMapping() {
        if (hashCode() != 0 && TextUtils.equals(getMode(), SUBTYPE_MODE_KEYBOARD)) {
            return !isAuxiliary();
        }
        return false;
    }

    public String getMode() {
        return this.mSubtypeMode;
    }

    public String getExtraValue() {
        return this.mSubtypeExtraValue;
    }

    public boolean isAuxiliary() {
        return this.mIsAuxiliary;
    }

    public boolean overridesImplicitlyEnabledSubtype() {
        return this.mOverridesImplicitlyEnabledSubtype;
    }

    public boolean isAsciiCapable() {
        return this.mIsAsciiCapable;
    }

    public CharSequence getDisplayName(Context context, String packageName, ApplicationInfo appInfo) {
        DisplayContext displayContext;
        String replacementString;
        if (this.mSubtypeNameResId == 0) {
            if (TextUtils.isEmpty(this.mSubtypeNameOverride)) {
                return getLocaleDisplayName(getLocaleFromContext(context), getLocaleObject(), DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            }
            return this.mSubtypeNameOverride;
        }
        CharSequence subtypeName = context.getPackageManager().getText(packageName, this.mSubtypeNameResId, appInfo);
        if (TextUtils.isEmpty(subtypeName)) {
            return "";
        }
        String subtypeNameString = subtypeName.toString();
        if (containsExtraValueKey(EXTRA_KEY_UNTRANSLATABLE_STRING_IN_SUBTYPE_NAME)) {
            replacementString = getExtraValueOf(EXTRA_KEY_UNTRANSLATABLE_STRING_IN_SUBTYPE_NAME);
        } else {
            if (TextUtils.equals(subtypeNameString, "%s")) {
                displayContext = DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU;
            } else if (subtypeNameString.startsWith("%s")) {
                displayContext = DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE;
            } else {
                displayContext = DisplayContext.CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE;
            }
            replacementString = getLocaleDisplayName(getLocaleFromContext(context), getLocaleObject(), displayContext);
        }
        if (replacementString == null) {
            replacementString = "";
        }
        try {
            return String.format(subtypeNameString, replacementString);
        } catch (IllegalFormatException e) {
            Slog.m90w(TAG, "Found illegal format in subtype name(" + ((Object) subtypeName) + "): " + e);
            return "";
        }
    }

    private static Locale getLocaleFromContext(Context context) {
        Configuration configuration;
        if (context == null || context.getResources() == null || (configuration = context.getResources().getConfiguration()) == null) {
            return null;
        }
        return configuration.getLocales().get(0);
    }

    private static String getLocaleDisplayName(Locale displayLocale, Locale localeToDisplay, DisplayContext displayContext) {
        if (localeToDisplay == null) {
            return "";
        }
        Locale nonNullDisplayLocale = displayLocale != null ? displayLocale : Locale.getDefault();
        return LocaleDisplayNames.getInstance(nonNullDisplayLocale, displayContext).localeDisplayName(localeToDisplay);
    }

    private HashMap<String, String> getExtraValueHashMap() {
        synchronized (this) {
            HashMap<String, String> extraValueMap = this.mExtraValueHashMapCache;
            if (extraValueMap != null) {
                return extraValueMap;
            }
            HashMap<String, String> extraValueMap2 = new HashMap<>();
            String[] pairs = this.mSubtypeExtraValue.split(",");
            for (String str : pairs) {
                String[] pair = str.split(EXTRA_VALUE_KEY_VALUE_SEPARATOR);
                if (pair.length == 1) {
                    extraValueMap2.put(pair[0], null);
                } else if (pair.length > 1) {
                    if (pair.length > 2) {
                        Slog.m90w(TAG, "ExtraValue has two or more '='s");
                    }
                    extraValueMap2.put(pair[0], pair[1]);
                }
            }
            this.mExtraValueHashMapCache = extraValueMap2;
            return extraValueMap2;
        }
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

    public final boolean hasSubtypeId() {
        return this.mSubtypeId != 0;
    }

    public final int getSubtypeId() {
        return this.mSubtypeId;
    }

    public boolean equals(Object o) {
        if (o instanceof InputMethodSubtype) {
            InputMethodSubtype subtype = (InputMethodSubtype) o;
            return (subtype.mSubtypeId == 0 && this.mSubtypeId == 0) ? subtype.hashCode() == hashCode() && subtype.getLocale().equals(getLocale()) && subtype.getLanguageTag().equals(getLanguageTag()) && subtype.getMode().equals(getMode()) && subtype.getExtraValue().equals(getExtraValue()) && subtype.isAuxiliary() == isAuxiliary() && subtype.overridesImplicitlyEnabledSubtype() == overridesImplicitlyEnabledSubtype() && subtype.isAsciiCapable() == isAsciiCapable() : subtype.hashCode() == hashCode();
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.mSubtypeNameResId);
        TextUtils.writeToParcel(this.mSubtypeNameOverride, dest, parcelableFlags);
        dest.writeString8(this.mPkLanguageTag);
        dest.writeString8(this.mPkLayoutType);
        dest.writeInt(this.mSubtypeIconResId);
        dest.writeString(this.mSubtypeLocale);
        dest.writeString(this.mSubtypeLanguageTag);
        dest.writeString(this.mSubtypeMode);
        dest.writeString(this.mSubtypeExtraValue);
        dest.writeInt(this.mIsAuxiliary ? 1 : 0);
        dest.writeInt(this.mOverridesImplicitlyEnabledSubtype ? 1 : 0);
        dest.writeInt(this.mSubtypeHashCode);
        dest.writeInt(this.mSubtypeId);
        dest.writeInt(this.mIsAsciiCapable ? 1 : 0);
    }

    private static int hashCodeInternal(String locale, String mode, String extraValue, boolean isAuxiliary, boolean overridesImplicitlyEnabledSubtype, boolean isAsciiCapable) {
        boolean needsToCalculateCompatibleHashCode = !isAsciiCapable;
        if (needsToCalculateCompatibleHashCode) {
            return Arrays.hashCode(new Object[]{locale, mode, extraValue, Boolean.valueOf(isAuxiliary), Boolean.valueOf(overridesImplicitlyEnabledSubtype)});
        }
        return Arrays.hashCode(new Object[]{locale, mode, extraValue, Boolean.valueOf(isAuxiliary), Boolean.valueOf(overridesImplicitlyEnabledSubtype), Boolean.valueOf(isAsciiCapable)});
    }

    public static List<InputMethodSubtype> sort(InputMethodInfo imi, List<InputMethodSubtype> subtypeList) {
        if (imi == null) {
            return subtypeList;
        }
        HashSet<InputMethodSubtype> inputSubtypesSet = new HashSet<>(subtypeList);
        ArrayList<InputMethodSubtype> sortedList = new ArrayList<>();
        int N = imi.getSubtypeCount();
        for (int i = 0; i < N; i++) {
            InputMethodSubtype subtype = imi.getSubtypeAt(i);
            if (inputSubtypesSet.contains(subtype)) {
                sortedList.add(subtype);
                inputSubtypesSet.remove(subtype);
            }
        }
        Iterator<InputMethodSubtype> it = inputSubtypesSet.iterator();
        while (it.hasNext()) {
            sortedList.add(it.next());
        }
        return sortedList;
    }
}
