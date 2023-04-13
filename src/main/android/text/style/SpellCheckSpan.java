package android.text.style;

import android.p008os.Parcel;
import android.text.ParcelableSpan;
/* loaded from: classes3.dex */
public class SpellCheckSpan implements ParcelableSpan {
    private boolean mSpellCheckInProgress;

    public SpellCheckSpan() {
        this.mSpellCheckInProgress = false;
    }

    public SpellCheckSpan(Parcel src) {
        this.mSpellCheckInProgress = src.readInt() != 0;
    }

    public void setSpellCheckInProgress(boolean inProgress) {
        this.mSpellCheckInProgress = inProgress;
    }

    public boolean isSpellCheckInProgress() {
        return this.mSpellCheckInProgress;
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
        dest.writeInt(this.mSpellCheckInProgress ? 1 : 0);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 20;
    }
}
