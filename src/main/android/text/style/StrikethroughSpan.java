package android.text.style;

import android.p008os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public class StrikethroughSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan {
    public StrikethroughSpan() {
    }

    public StrikethroughSpan(Parcel src) {
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 5;
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
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        ds.setStrikeThruText(true);
    }

    public String toString() {
        return "StrikethroughSpan{}";
    }
}
