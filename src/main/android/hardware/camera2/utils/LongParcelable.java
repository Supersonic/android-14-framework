package android.hardware.camera2.utils;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class LongParcelable implements Parcelable {
    public static final Parcelable.Creator<LongParcelable> CREATOR = new Parcelable.Creator<LongParcelable>() { // from class: android.hardware.camera2.utils.LongParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LongParcelable createFromParcel(Parcel in) {
            return new LongParcelable(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LongParcelable[] newArray(int size) {
            return new LongParcelable[size];
        }
    };
    private long number;

    public LongParcelable() {
        this.number = 0L;
    }

    public LongParcelable(long number) {
        this.number = number;
    }

    private LongParcelable(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.number);
    }

    public void readFromParcel(Parcel in) {
        this.number = in.readLong();
    }

    public long getNumber() {
        return this.number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
