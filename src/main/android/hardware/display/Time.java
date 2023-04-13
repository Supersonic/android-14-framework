package android.hardware.display;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.time.LocalTime;
/* loaded from: classes.dex */
public final class Time implements Parcelable {
    public static final Parcelable.Creator<Time> CREATOR = new Parcelable.Creator<Time>() { // from class: android.hardware.display.Time.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Time createFromParcel(Parcel source) {
            return new Time(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };
    private final int mHour;
    private final int mMinute;
    private final int mNano;
    private final int mSecond;

    public Time(LocalTime localTime) {
        this.mHour = localTime.getHour();
        this.mMinute = localTime.getMinute();
        this.mSecond = localTime.getSecond();
        this.mNano = localTime.getNano();
    }

    public Time(Parcel parcel) {
        this.mHour = parcel.readInt();
        this.mMinute = parcel.readInt();
        this.mSecond = parcel.readInt();
        this.mNano = parcel.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeInt(this.mHour);
        parcel.writeInt(this.mMinute);
        parcel.writeInt(this.mSecond);
        parcel.writeInt(this.mNano);
    }

    public LocalTime getLocalTime() {
        return LocalTime.of(this.mHour, this.mMinute, this.mSecond, this.mNano);
    }
}
