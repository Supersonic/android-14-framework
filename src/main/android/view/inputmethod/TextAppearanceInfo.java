package android.view.inputmethod;

import android.graphics.Typeface;
import android.p008os.LocaleList;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.text.style.CharacterStyle;
import android.widget.TextView;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextAppearanceInfo implements Parcelable {
    public static final Parcelable.Creator<TextAppearanceInfo> CREATOR = new Parcelable.Creator<TextAppearanceInfo>() { // from class: android.view.inputmethod.TextAppearanceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextAppearanceInfo createFromParcel(Parcel in) {
            return new TextAppearanceInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextAppearanceInfo[] newArray(int size) {
            return new TextAppearanceInfo[size];
        }
    };
    private final boolean mAllCaps;
    private final boolean mElegantTextHeight;
    private final boolean mFallbackLineSpacing;
    private final String mFontFeatureSettings;
    private final String mFontVariationSettings;
    private final int mHighlightTextColor;
    private final int mHintTextColor;
    private final float mLetterSpacing;
    private final int mLineBreakStyle;
    private final int mLineBreakWordStyle;
    private final int mLinkTextColor;
    private final int mShadowColor;
    private final float mShadowDx;
    private final float mShadowDy;
    private final float mShadowRadius;
    private final String mSystemFontFamilyName;
    private final int mTextColor;
    private final int mTextFontWeight;
    private final LocaleList mTextLocales;
    private final float mTextScaleX;
    private final float mTextSize;
    private final int mTextStyle;

    private TextAppearanceInfo(Builder builder) {
        this.mTextSize = builder.mTextSize;
        this.mTextLocales = builder.mTextLocales;
        this.mSystemFontFamilyName = builder.mSystemFontFamilyName;
        this.mTextFontWeight = builder.mTextFontWeight;
        this.mTextStyle = builder.mTextStyle;
        this.mAllCaps = builder.mAllCaps;
        this.mShadowDx = builder.mShadowDx;
        this.mShadowDy = builder.mShadowDy;
        this.mShadowRadius = builder.mShadowRadius;
        this.mShadowColor = builder.mShadowColor;
        this.mElegantTextHeight = builder.mElegantTextHeight;
        this.mFallbackLineSpacing = builder.mFallbackLineSpacing;
        this.mLetterSpacing = builder.mLetterSpacing;
        this.mFontFeatureSettings = builder.mFontFeatureSettings;
        this.mFontVariationSettings = builder.mFontVariationSettings;
        this.mLineBreakStyle = builder.mLineBreakStyle;
        this.mLineBreakWordStyle = builder.mLineBreakWordStyle;
        this.mTextScaleX = builder.mTextScaleX;
        this.mHighlightTextColor = builder.mHighlightTextColor;
        this.mTextColor = builder.mTextColor;
        this.mHintTextColor = builder.mHintTextColor;
        this.mLinkTextColor = builder.mLinkTextColor;
    }

    public static TextAppearanceInfo createFromTextView(TextView textView) {
        int selectionStart = textView.getSelectionStart();
        CharSequence text = textView.getText();
        TextPaint textPaint = new TextPaint();
        textPaint.set(textView.getPaint());
        if ((text instanceof Spanned) && text.length() > 0 && selectionStart > 0) {
            Spanned spannedText = (Spanned) text;
            int lastCh = selectionStart - 1;
            CharacterStyle[] spans = (CharacterStyle[]) spannedText.getSpans(lastCh, lastCh, CharacterStyle.class);
            if (spans != null) {
                for (CharacterStyle span : spans) {
                    if (spannedText.getSpanStart(span) <= lastCh && lastCh < spannedText.getSpanEnd(span)) {
                        span.updateDrawState(textPaint);
                    }
                }
            }
        }
        Typeface typeface = textPaint.getTypeface();
        String systemFontFamilyName = null;
        int textWeight = -1;
        int textStyle = 0;
        if (typeface != null) {
            systemFontFamilyName = typeface.getSystemFontFamilyName();
            textWeight = typeface.getWeight();
            textStyle = typeface.getStyle();
        }
        Builder builder = new Builder();
        builder.setTextSize(textPaint.getTextSize()).setTextLocales(textPaint.getTextLocales()).setSystemFontFamilyName(systemFontFamilyName).setTextFontWeight(textWeight).setTextStyle(textStyle).setShadowDx(textPaint.getShadowLayerDx()).setShadowDy(textPaint.getShadowLayerDy()).setShadowRadius(textPaint.getShadowLayerRadius()).setShadowColor(textPaint.getShadowLayerColor()).setElegantTextHeight(textPaint.isElegantTextHeight()).setLetterSpacing(textPaint.getLetterSpacing()).setFontFeatureSettings(textPaint.getFontFeatureSettings()).setFontVariationSettings(textPaint.getFontVariationSettings()).setTextScaleX(textPaint.getTextScaleX()).setTextColor(textPaint.getColor()).setLinkTextColor(textPaint.linkColor).setAllCaps(textView.isAllCaps()).setFallbackLineSpacing(textView.isFallbackLineSpacing()).setLineBreakStyle(textView.getLineBreakStyle()).setLineBreakWordStyle(textView.getLineBreakWordStyle()).setHighlightTextColor(textView.getHighlightColor()).setHintTextColor(textView.getCurrentHintTextColor());
        return builder.build();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mTextSize);
        this.mTextLocales.writeToParcel(dest, flags);
        dest.writeBoolean(this.mAllCaps);
        dest.writeString8(this.mSystemFontFamilyName);
        dest.writeInt(this.mTextFontWeight);
        dest.writeInt(this.mTextStyle);
        dest.writeFloat(this.mShadowDx);
        dest.writeFloat(this.mShadowDy);
        dest.writeFloat(this.mShadowRadius);
        dest.writeInt(this.mShadowColor);
        dest.writeBoolean(this.mElegantTextHeight);
        dest.writeBoolean(this.mFallbackLineSpacing);
        dest.writeFloat(this.mLetterSpacing);
        dest.writeString8(this.mFontFeatureSettings);
        dest.writeString8(this.mFontVariationSettings);
        dest.writeInt(this.mLineBreakStyle);
        dest.writeInt(this.mLineBreakWordStyle);
        dest.writeFloat(this.mTextScaleX);
        dest.writeInt(this.mHighlightTextColor);
        dest.writeInt(this.mTextColor);
        dest.writeInt(this.mHintTextColor);
        dest.writeInt(this.mLinkTextColor);
    }

    TextAppearanceInfo(Parcel in) {
        this.mTextSize = in.readFloat();
        this.mTextLocales = LocaleList.CREATOR.createFromParcel(in);
        this.mAllCaps = in.readBoolean();
        this.mSystemFontFamilyName = in.readString8();
        this.mTextFontWeight = in.readInt();
        this.mTextStyle = in.readInt();
        this.mShadowDx = in.readFloat();
        this.mShadowDy = in.readFloat();
        this.mShadowRadius = in.readFloat();
        this.mShadowColor = in.readInt();
        this.mElegantTextHeight = in.readBoolean();
        this.mFallbackLineSpacing = in.readBoolean();
        this.mLetterSpacing = in.readFloat();
        this.mFontFeatureSettings = in.readString8();
        this.mFontVariationSettings = in.readString8();
        this.mLineBreakStyle = in.readInt();
        this.mLineBreakWordStyle = in.readInt();
        this.mTextScaleX = in.readFloat();
        this.mHighlightTextColor = in.readInt();
        this.mTextColor = in.readInt();
        this.mHintTextColor = in.readInt();
        this.mLinkTextColor = in.readInt();
    }

    public float getTextSize() {
        return this.mTextSize;
    }

    public LocaleList getTextLocales() {
        return this.mTextLocales;
    }

    public String getSystemFontFamilyName() {
        return this.mSystemFontFamilyName;
    }

    public int getTextFontWeight() {
        return this.mTextFontWeight;
    }

    public int getTextStyle() {
        return this.mTextStyle;
    }

    public boolean isAllCaps() {
        return this.mAllCaps;
    }

    public float getShadowDx() {
        return this.mShadowDx;
    }

    public float getShadowDy() {
        return this.mShadowDy;
    }

    public float getShadowRadius() {
        return this.mShadowRadius;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public boolean isElegantTextHeight() {
        return this.mElegantTextHeight;
    }

    public boolean isFallbackLineSpacing() {
        return this.mFallbackLineSpacing;
    }

    public float getLetterSpacing() {
        return this.mLetterSpacing;
    }

    public String getFontFeatureSettings() {
        return this.mFontFeatureSettings;
    }

    public String getFontVariationSettings() {
        return this.mFontVariationSettings;
    }

    public int getLineBreakStyle() {
        return this.mLineBreakStyle;
    }

    public int getLineBreakWordStyle() {
        return this.mLineBreakWordStyle;
    }

    public float getTextScaleX() {
        return this.mTextScaleX;
    }

    public int getHighlightTextColor() {
        return this.mHighlightTextColor;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public int getHintTextColor() {
        return this.mHintTextColor;
    }

    public int getLinkTextColor() {
        return this.mLinkTextColor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TextAppearanceInfo) {
            TextAppearanceInfo that = (TextAppearanceInfo) o;
            return Float.compare(that.mTextSize, this.mTextSize) == 0 && this.mTextFontWeight == that.mTextFontWeight && this.mTextStyle == that.mTextStyle && this.mAllCaps == that.mAllCaps && Float.compare(that.mShadowDx, this.mShadowDx) == 0 && Float.compare(that.mShadowDy, this.mShadowDy) == 0 && Float.compare(that.mShadowRadius, this.mShadowRadius) == 0 && that.mShadowColor == this.mShadowColor && this.mElegantTextHeight == that.mElegantTextHeight && this.mFallbackLineSpacing == that.mFallbackLineSpacing && Float.compare(that.mLetterSpacing, this.mLetterSpacing) == 0 && this.mLineBreakStyle == that.mLineBreakStyle && this.mLineBreakWordStyle == that.mLineBreakWordStyle && this.mHighlightTextColor == that.mHighlightTextColor && this.mTextColor == that.mTextColor && this.mLinkTextColor == that.mLinkTextColor && this.mHintTextColor == that.mHintTextColor && Objects.equals(this.mTextLocales, that.mTextLocales) && Objects.equals(this.mSystemFontFamilyName, that.mSystemFontFamilyName) && Objects.equals(this.mFontFeatureSettings, that.mFontFeatureSettings) && Objects.equals(this.mFontVariationSettings, that.mFontVariationSettings) && Float.compare(that.mTextScaleX, this.mTextScaleX) == 0;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.mTextSize), this.mTextLocales, this.mSystemFontFamilyName, Integer.valueOf(this.mTextFontWeight), Integer.valueOf(this.mTextStyle), Boolean.valueOf(this.mAllCaps), Float.valueOf(this.mShadowDx), Float.valueOf(this.mShadowDy), Float.valueOf(this.mShadowRadius), Integer.valueOf(this.mShadowColor), Boolean.valueOf(this.mElegantTextHeight), Boolean.valueOf(this.mFallbackLineSpacing), Float.valueOf(this.mLetterSpacing), this.mFontFeatureSettings, this.mFontVariationSettings, Integer.valueOf(this.mLineBreakStyle), Integer.valueOf(this.mLineBreakWordStyle), Float.valueOf(this.mTextScaleX), Integer.valueOf(this.mHighlightTextColor), Integer.valueOf(this.mTextColor), Integer.valueOf(this.mHintTextColor), Integer.valueOf(this.mLinkTextColor));
    }

    public String toString() {
        return "TextAppearanceInfo{mTextSize=" + this.mTextSize + ", mTextLocales=" + this.mTextLocales + ", mSystemFontFamilyName='" + this.mSystemFontFamilyName + DateFormat.QUOTE + ", mTextFontWeight=" + this.mTextFontWeight + ", mTextStyle=" + this.mTextStyle + ", mAllCaps=" + this.mAllCaps + ", mShadowDx=" + this.mShadowDx + ", mShadowDy=" + this.mShadowDy + ", mShadowRadius=" + this.mShadowRadius + ", mShadowColor=" + this.mShadowColor + ", mElegantTextHeight=" + this.mElegantTextHeight + ", mFallbackLineSpacing=" + this.mFallbackLineSpacing + ", mLetterSpacing=" + this.mLetterSpacing + ", mFontFeatureSettings='" + this.mFontFeatureSettings + DateFormat.QUOTE + ", mFontVariationSettings='" + this.mFontVariationSettings + DateFormat.QUOTE + ", mLineBreakStyle=" + this.mLineBreakStyle + ", mLineBreakWordStyle=" + this.mLineBreakWordStyle + ", mTextScaleX=" + this.mTextScaleX + ", mHighlightTextColor=" + this.mHighlightTextColor + ", mTextColor=" + this.mTextColor + ", mHintTextColor=" + this.mHintTextColor + ", mLinkTextColor=" + this.mLinkTextColor + '}';
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private float mTextSize = -1.0f;
        private LocaleList mTextLocales = LocaleList.getAdjustedDefault();
        private String mSystemFontFamilyName = null;
        private int mTextFontWeight = -1;
        private int mTextStyle = 0;
        private boolean mAllCaps = false;
        private float mShadowDx = 0.0f;
        private float mShadowDy = 0.0f;
        private float mShadowRadius = 0.0f;
        private int mShadowColor = 0;
        private boolean mElegantTextHeight = false;
        private boolean mFallbackLineSpacing = false;
        private float mLetterSpacing = 0.0f;
        private String mFontFeatureSettings = null;
        private String mFontVariationSettings = null;
        private int mLineBreakStyle = 0;
        private int mLineBreakWordStyle = 0;
        private float mTextScaleX = 1.0f;
        private int mHighlightTextColor = 0;
        private int mTextColor = 0;
        private int mHintTextColor = 0;
        private int mLinkTextColor = 0;

        public Builder setTextSize(float textSize) {
            this.mTextSize = textSize;
            return this;
        }

        public Builder setTextLocales(LocaleList textLocales) {
            this.mTextLocales = textLocales;
            return this;
        }

        public Builder setSystemFontFamilyName(String systemFontFamilyName) {
            this.mSystemFontFamilyName = systemFontFamilyName;
            return this;
        }

        public Builder setTextFontWeight(int textFontWeight) {
            this.mTextFontWeight = textFontWeight;
            return this;
        }

        public Builder setTextStyle(int textStyle) {
            this.mTextStyle = textStyle;
            return this;
        }

        public Builder setAllCaps(boolean allCaps) {
            this.mAllCaps = allCaps;
            return this;
        }

        public Builder setShadowDx(float shadowDx) {
            this.mShadowDx = shadowDx;
            return this;
        }

        public Builder setShadowDy(float shadowDy) {
            this.mShadowDy = shadowDy;
            return this;
        }

        public Builder setShadowRadius(float shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public Builder setShadowColor(int shadowColor) {
            this.mShadowColor = shadowColor;
            return this;
        }

        public Builder setElegantTextHeight(boolean elegantTextHeight) {
            this.mElegantTextHeight = elegantTextHeight;
            return this;
        }

        public Builder setFallbackLineSpacing(boolean fallbackLineSpacing) {
            this.mFallbackLineSpacing = fallbackLineSpacing;
            return this;
        }

        public Builder setLetterSpacing(float letterSpacing) {
            this.mLetterSpacing = letterSpacing;
            return this;
        }

        public Builder setFontFeatureSettings(String fontFeatureSettings) {
            this.mFontFeatureSettings = fontFeatureSettings;
            return this;
        }

        public Builder setFontVariationSettings(String fontVariationSettings) {
            this.mFontVariationSettings = fontVariationSettings;
            return this;
        }

        public Builder setLineBreakStyle(int lineBreakStyle) {
            this.mLineBreakStyle = lineBreakStyle;
            return this;
        }

        public Builder setLineBreakWordStyle(int lineBreakWordStyle) {
            this.mLineBreakWordStyle = lineBreakWordStyle;
            return this;
        }

        public Builder setTextScaleX(float textScaleX) {
            this.mTextScaleX = textScaleX;
            return this;
        }

        public Builder setHighlightTextColor(int highlightTextColor) {
            this.mHighlightTextColor = highlightTextColor;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.mTextColor = textColor;
            return this;
        }

        public Builder setHintTextColor(int hintTextColor) {
            this.mHintTextColor = hintTextColor;
            return this;
        }

        public Builder setLinkTextColor(int linkTextColor) {
            this.mLinkTextColor = linkTextColor;
            return this;
        }

        public TextAppearanceInfo build() {
            return new TextAppearanceInfo(this);
        }
    }
}
