package android.text.style;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.p008os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class StyleSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final int mFontWeightAdjustment;
    private final int mStyle;

    public StyleSpan(int style) {
        this(style, Integer.MAX_VALUE);
    }

    public StyleSpan(int style, int fontWeightAdjustment) {
        this.mStyle = style;
        this.mFontWeightAdjustment = fontWeightAdjustment;
    }

    public StyleSpan(Parcel src) {
        this.mStyle = src.readInt();
        this.mFontWeightAdjustment = src.readInt();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 7;
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
        dest.writeInt(this.mStyle);
        dest.writeInt(this.mFontWeightAdjustment);
    }

    public int getStyle() {
        return this.mStyle;
    }

    public int getFontWeightAdjustment() {
        return this.mFontWeightAdjustment;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        apply(ds, this.mStyle, this.mFontWeightAdjustment);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint paint) {
        apply(paint, this.mStyle, this.mFontWeightAdjustment);
    }

    private static void apply(Paint paint, int style, int fontWeightAdjustment) {
        int oldStyle;
        Typeface tf;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }
        int want = oldStyle | style;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }
        if ((style & 1) != 0 && fontWeightAdjustment != 0 && fontWeightAdjustment != Integer.MAX_VALUE) {
            int newWeight = Math.min(Math.max(tf.getWeight() + fontWeightAdjustment, 1), 1000);
            boolean italic = (want & 2) != 0;
            tf = Typeface.create(tf, newWeight, italic);
        }
        int newWeight2 = tf.getStyle();
        int fake = (~newWeight2) & want;
        if ((fake & 1) != 0) {
            paint.setFakeBoldText(true);
        }
        if ((fake & 2) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(tf);
    }

    public String toString() {
        return "StyleSpan{style=" + getStyle() + ", fontWeightAdjustment=" + getFontWeightAdjustment() + '}';
    }
}
