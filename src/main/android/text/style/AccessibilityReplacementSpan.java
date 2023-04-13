package android.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.ParcelableSpan;
/* loaded from: classes3.dex */
public class AccessibilityReplacementSpan extends ReplacementSpan implements ParcelableSpan {
    public static final Parcelable.Creator<AccessibilityReplacementSpan> CREATOR = new Parcelable.Creator<AccessibilityReplacementSpan>() { // from class: android.text.style.AccessibilityReplacementSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityReplacementSpan createFromParcel(Parcel parcel) {
            return new AccessibilityReplacementSpan(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AccessibilityReplacementSpan[] newArray(int size) {
            return new AccessibilityReplacementSpan[size];
        }
    };

    public AccessibilityReplacementSpan(CharSequence contentDescription) {
        setContentDescription(contentDescription);
    }

    public AccessibilityReplacementSpan(Parcel p) {
        CharSequence contentDescription = p.readCharSequence();
        setContentDescription(contentDescription);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 29;
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
        dest.writeCharSequence(getContentDescription());
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
    }
}
