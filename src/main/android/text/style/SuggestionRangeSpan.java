package android.text.style;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.ParcelableSpan;
import android.text.TextPaint;
/* loaded from: classes3.dex */
public final class SuggestionRangeSpan extends CharacterStyle implements ParcelableSpan {
    public static final Parcelable.Creator<SuggestionRangeSpan> CREATOR = new Parcelable.Creator<SuggestionRangeSpan>() { // from class: android.text.style.SuggestionRangeSpan.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuggestionRangeSpan createFromParcel(Parcel source) {
            return new SuggestionRangeSpan(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SuggestionRangeSpan[] newArray(int size) {
            return new SuggestionRangeSpan[size];
        }
    };
    private int mBackgroundColor;

    public SuggestionRangeSpan() {
        this.mBackgroundColor = 0;
    }

    public SuggestionRangeSpan(Parcel src) {
        this.mBackgroundColor = src.readInt();
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
        dest.writeInt(this.mBackgroundColor);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 21;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint tp) {
        tp.bgColor = this.mBackgroundColor;
    }
}
