package android.text.style;

import android.p008os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class AbsoluteSizeSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final boolean mDip;
    private final int mSize;

    public AbsoluteSizeSpan(int size) {
        this(size, false);
    }

    public AbsoluteSizeSpan(int size, boolean dip) {
        this.mSize = size;
        this.mDip = dip;
    }

    public AbsoluteSizeSpan(Parcel src) {
        this.mSize = src.readInt();
        this.mDip = src.readInt() != 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 16;
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
        dest.writeInt(this.mSize);
        dest.writeInt(this.mDip ? 1 : 0);
    }

    public int getSize() {
        return this.mSize;
    }

    public boolean getDip() {
        return this.mDip;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        if (this.mDip) {
            ds.setTextSize(this.mSize * ds.density);
        } else {
            ds.setTextSize(this.mSize);
        }
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint ds) {
        if (this.mDip) {
            ds.setTextSize(this.mSize * ds.density);
        } else {
            ds.setTextSize(this.mSize);
        }
    }

    public String toString() {
        return "AbsoluteSizeSpan{size=" + getSize() + ", isDip=" + getDip() + '}';
    }
}
