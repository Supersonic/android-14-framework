package android.text.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.util.Log;
import com.android.internal.C4057R;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes3.dex */
public class SuggestionSpan extends CharacterStyle implements ParcelableSpan {
    @Deprecated
    public static final String ACTION_SUGGESTION_PICKED = "android.text.style.SUGGESTION_PICKED";
    public static final Parcelable.Creator<SuggestionSpan> CREATOR = new Parcelable.Creator<SuggestionSpan>() { // from class: android.text.style.SuggestionSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuggestionSpan createFromParcel(Parcel source) {
            return new SuggestionSpan(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuggestionSpan[] newArray(int size) {
            return new SuggestionSpan[size];
        }
    };
    public static final int FLAG_AUTO_CORRECTION = 4;
    public static final int FLAG_EASY_CORRECT = 1;
    public static final int FLAG_GRAMMAR_ERROR = 8;
    public static final int FLAG_MISSPELLED = 2;
    public static final int SUGGESTIONS_MAX_SIZE = 5;
    @Deprecated
    public static final String SUGGESTION_SPAN_PICKED_AFTER = "after";
    @Deprecated
    public static final String SUGGESTION_SPAN_PICKED_BEFORE = "before";
    @Deprecated
    public static final String SUGGESTION_SPAN_PICKED_HASHCODE = "hashcode";
    private static final String TAG = "SuggestionSpan";
    private int mAutoCorrectionUnderlineColor;
    private float mAutoCorrectionUnderlineThickness;
    private int mEasyCorrectUnderlineColor;
    private float mEasyCorrectUnderlineThickness;
    private int mFlags;
    private int mGrammarErrorUnderlineColor;
    private float mGrammarErrorUnderlineThickness;
    private final int mHashCode;
    private final String mLanguageTag;
    private final String mLocaleStringForCompatibility;
    private int mMisspelledUnderlineColor;
    private float mMisspelledUnderlineThickness;
    private final String[] mSuggestions;

    public SuggestionSpan(Context context, String[] suggestions, int flags) {
        this(context, null, suggestions, flags, null);
    }

    public SuggestionSpan(Locale locale, String[] suggestions, int flags) {
        this(null, locale, suggestions, flags, null);
    }

    public SuggestionSpan(Context context, Locale locale, String[] suggestions, int flags, Class<?> notificationTargetClass) {
        Locale sourceLocale;
        int N = Math.min(5, suggestions.length);
        String[] strArr = (String[]) Arrays.copyOf(suggestions, N);
        this.mSuggestions = strArr;
        this.mFlags = flags;
        if (locale != null) {
            sourceLocale = locale;
        } else if (context != null) {
            sourceLocale = context.getResources().getConfiguration().locale;
        } else {
            Log.m110e(TAG, "No locale or context specified in SuggestionSpan constructor");
            sourceLocale = null;
        }
        String locale2 = sourceLocale == null ? "" : sourceLocale.toString();
        this.mLocaleStringForCompatibility = locale2;
        String languageTag = sourceLocale != null ? sourceLocale.toLanguageTag() : "";
        this.mLanguageTag = languageTag;
        this.mHashCode = hashCodeInternal(strArr, languageTag, locale2);
        initStyle(context);
    }

    private void initStyle(Context context) {
        if (context == null) {
            this.mMisspelledUnderlineThickness = 0.0f;
            this.mGrammarErrorUnderlineThickness = 0.0f;
            this.mEasyCorrectUnderlineThickness = 0.0f;
            this.mAutoCorrectionUnderlineThickness = 0.0f;
            this.mMisspelledUnderlineColor = -16777216;
            this.mGrammarErrorUnderlineColor = -16777216;
            this.mEasyCorrectUnderlineColor = -16777216;
            this.mAutoCorrectionUnderlineColor = -16777216;
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(null, C4057R.styleable.SuggestionSpan, C4057R.attr.textAppearanceMisspelledSuggestion, 0);
        this.mMisspelledUnderlineThickness = typedArray.getDimension(1, 0.0f);
        this.mMisspelledUnderlineColor = typedArray.getColor(0, -16777216);
        typedArray.recycle();
        TypedArray typedArray2 = context.obtainStyledAttributes(null, C4057R.styleable.SuggestionSpan, C4057R.attr.textAppearanceGrammarErrorSuggestion, 0);
        this.mGrammarErrorUnderlineThickness = typedArray2.getDimension(1, 0.0f);
        this.mGrammarErrorUnderlineColor = typedArray2.getColor(0, -16777216);
        typedArray2.recycle();
        TypedArray typedArray3 = context.obtainStyledAttributes(null, C4057R.styleable.SuggestionSpan, C4057R.attr.textAppearanceEasyCorrectSuggestion, 0);
        this.mEasyCorrectUnderlineThickness = typedArray3.getDimension(1, 0.0f);
        this.mEasyCorrectUnderlineColor = typedArray3.getColor(0, -16777216);
        typedArray3.recycle();
        TypedArray typedArray4 = context.obtainStyledAttributes(null, C4057R.styleable.SuggestionSpan, C4057R.attr.textAppearanceAutoCorrectionSuggestion, 0);
        this.mAutoCorrectionUnderlineThickness = typedArray4.getDimension(1, 0.0f);
        this.mAutoCorrectionUnderlineColor = typedArray4.getColor(0, -16777216);
        typedArray4.recycle();
    }

    public SuggestionSpan(Parcel src) {
        this.mSuggestions = src.readStringArray();
        this.mFlags = src.readInt();
        this.mLocaleStringForCompatibility = src.readString();
        this.mLanguageTag = src.readString();
        this.mHashCode = src.readInt();
        this.mEasyCorrectUnderlineColor = src.readInt();
        this.mEasyCorrectUnderlineThickness = src.readFloat();
        this.mMisspelledUnderlineColor = src.readInt();
        this.mMisspelledUnderlineThickness = src.readFloat();
        this.mAutoCorrectionUnderlineColor = src.readInt();
        this.mAutoCorrectionUnderlineThickness = src.readFloat();
        this.mGrammarErrorUnderlineColor = src.readInt();
        this.mGrammarErrorUnderlineThickness = src.readFloat();
    }

    public String[] getSuggestions() {
        return this.mSuggestions;
    }

    @Deprecated
    public String getLocale() {
        return this.mLocaleStringForCompatibility;
    }

    public Locale getLocaleObject() {
        if (this.mLanguageTag.isEmpty()) {
            return null;
        }
        return Locale.forLanguageTag(this.mLanguageTag);
    }

    @Deprecated
    public String getNotificationTargetClassName() {
        return null;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel dest, int flags) {
        dest.writeStringArray(this.mSuggestions);
        dest.writeInt(this.mFlags);
        dest.writeString(this.mLocaleStringForCompatibility);
        dest.writeString(this.mLanguageTag);
        dest.writeInt(this.mHashCode);
        dest.writeInt(this.mEasyCorrectUnderlineColor);
        dest.writeFloat(this.mEasyCorrectUnderlineThickness);
        dest.writeInt(this.mMisspelledUnderlineColor);
        dest.writeFloat(this.mMisspelledUnderlineThickness);
        dest.writeInt(this.mAutoCorrectionUnderlineColor);
        dest.writeFloat(this.mAutoCorrectionUnderlineThickness);
        dest.writeInt(this.mGrammarErrorUnderlineColor);
        dest.writeFloat(this.mGrammarErrorUnderlineThickness);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 19;
    }

    public boolean equals(Object o) {
        return (o instanceof SuggestionSpan) && ((SuggestionSpan) o).hashCode() == this.mHashCode;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    private static int hashCodeInternal(String[] suggestions, String languageTag, String localeStringForCompatibility) {
        return Arrays.hashCode(new Object[]{Long.valueOf(SystemClock.uptimeMillis()), suggestions, languageTag, localeStringForCompatibility});
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        int i = this.mFlags;
        boolean misspelled = (i & 2) != 0;
        boolean easy = (i & 1) != 0;
        boolean autoCorrection = (i & 4) != 0;
        boolean grammarError = (i & 8) != 0;
        if (easy) {
            if (!misspelled && !grammarError) {
                tp.setUnderlineText(this.mEasyCorrectUnderlineColor, this.mEasyCorrectUnderlineThickness);
            } else if (tp.underlineColor == 0) {
                if (grammarError) {
                    tp.setUnderlineText(this.mGrammarErrorUnderlineColor, this.mGrammarErrorUnderlineThickness);
                } else {
                    tp.setUnderlineText(this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
                }
            }
        } else if (autoCorrection) {
            tp.setUnderlineText(this.mAutoCorrectionUnderlineColor, this.mAutoCorrectionUnderlineThickness);
        } else if (misspelled) {
            tp.setUnderlineText(this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
        } else if (grammarError) {
            tp.setUnderlineText(this.mGrammarErrorUnderlineColor, this.mGrammarErrorUnderlineThickness);
        }
    }

    public int getUnderlineColor() {
        int i = this.mFlags;
        boolean misspelled = (i & 2) != 0;
        boolean easy = (i & 1) != 0;
        boolean autoCorrection = (i & 4) != 0;
        boolean grammarError = (i & 8) != 0;
        if (easy) {
            if (grammarError) {
                return this.mGrammarErrorUnderlineColor;
            }
            if (misspelled) {
                return this.mMisspelledUnderlineColor;
            }
            return this.mEasyCorrectUnderlineColor;
        } else if (autoCorrection) {
            return this.mAutoCorrectionUnderlineColor;
        } else {
            if (misspelled) {
                return this.mMisspelledUnderlineColor;
            }
            if (grammarError) {
                return this.mGrammarErrorUnderlineColor;
            }
            return 0;
        }
    }

    @Deprecated
    public void notifySelection(Context context, String original, int index) {
        Log.m104w(TAG, "notifySelection() is deprecated.  Does nothing.");
    }
}
