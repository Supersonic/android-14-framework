package android.p008os.health;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.os.health.TimerStat */
/* loaded from: classes3.dex */
public final class TimerStat implements Parcelable {
    public static final Parcelable.Creator<TimerStat> CREATOR = new Parcelable.Creator<TimerStat>() { // from class: android.os.health.TimerStat.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimerStat createFromParcel(Parcel in) {
            return new TimerStat(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimerStat[] newArray(int size) {
            return new TimerStat[size];
        }
    };
    private int mCount;
    private long mTime;

    public TimerStat() {
    }

    public TimerStat(int count, long time) {
        this.mCount = count;
        this.mTime = time;
    }

    public TimerStat(Parcel in) {
        this.mCount = in.readInt();
        this.mTime = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mCount);
        out.writeLong(this.mTime);
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public int getCount() {
        return this.mCount;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public long getTime() {
        return this.mTime;
    }
}
