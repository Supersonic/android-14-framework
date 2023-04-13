package android.p008os;

import android.p008os.Parcelable;
/* renamed from: android.os.CpuUsageInfo */
/* loaded from: classes3.dex */
public final class CpuUsageInfo implements Parcelable {
    public static final Parcelable.Creator<CpuUsageInfo> CREATOR = new Parcelable.Creator<CpuUsageInfo>() { // from class: android.os.CpuUsageInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CpuUsageInfo createFromParcel(Parcel in) {
            return new CpuUsageInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CpuUsageInfo[] newArray(int size) {
            return new CpuUsageInfo[size];
        }
    };
    private long mActive;
    private long mTotal;

    public CpuUsageInfo(long activeTime, long totalTime) {
        this.mActive = activeTime;
        this.mTotal = totalTime;
    }

    private CpuUsageInfo(Parcel in) {
        readFromParcel(in);
    }

    public long getActive() {
        return this.mActive;
    }

    public long getTotal() {
        return this.mTotal;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mActive);
        out.writeLong(this.mTotal);
    }

    private void readFromParcel(Parcel in) {
        this.mActive = in.readLong();
        this.mTotal = in.readLong();
    }
}
