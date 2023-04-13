package android.text.style;

import android.p008os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class ForegroundColorSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {
    private final int mColor;

    public ForegroundColorSpan(int color) {
        this.mColor = color;
    }

    public ForegroundColorSpan(Parcel src) {
        this.mColor = src.readInt();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 2;
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
        dest.writeInt(this.mColor);
    }

    public int getForegroundColor() {
        return this.mColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setColor(this.mColor);
    }

    public String toString() {
        return "ForegroundColorSpan{color=#" + String.format("%08X", Integer.valueOf(getForegroundColor())) + '}';
    }
}
