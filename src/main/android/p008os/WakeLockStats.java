package android.p008os;

import android.p008os.Parcelable;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.os.WakeLockStats */
/* loaded from: classes3.dex */
public final class WakeLockStats implements Parcelable {
    public static final Parcelable.Creator<WakeLockStats> CREATOR = new Parcelable.Creator<WakeLockStats>() { // from class: android.os.WakeLockStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WakeLockStats createFromParcel(Parcel in) {
            return new WakeLockStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WakeLockStats[] newArray(int size) {
            return new WakeLockStats[size];
        }
    };
    private final List<WakeLock> mWakeLocks;

    /* renamed from: android.os.WakeLockStats$WakeLock */
    /* loaded from: classes3.dex */
    public static class WakeLock {
        public final String name;
        public final long timeHeldMs;
        public final int timesAcquired;
        public final long totalTimeHeldMs;
        public final int uid;

        public WakeLock(int uid, String name, int timesAcquired, long totalTimeHeldMs, long timeHeldMs) {
            this.uid = uid;
            this.name = name;
            this.timesAcquired = timesAcquired;
            this.totalTimeHeldMs = totalTimeHeldMs;
            this.timeHeldMs = timeHeldMs;
        }

        private WakeLock(Parcel in) {
            this.uid = in.readInt();
            this.name = in.readString();
            this.timesAcquired = in.readInt();
            this.totalTimeHeldMs = in.readLong();
            this.timeHeldMs = in.readLong();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeToParcel(Parcel out) {
            out.writeInt(this.uid);
            out.writeString(this.name);
            out.writeInt(this.timesAcquired);
            out.writeLong(this.totalTimeHeldMs);
            out.writeLong(this.timeHeldMs);
        }

        public String toString() {
            return "WakeLock{uid=" + this.uid + ", name='" + this.name + DateFormat.QUOTE + ", timesAcquired=" + this.timesAcquired + ", totalTimeHeldMs=" + this.totalTimeHeldMs + ", timeHeldMs=" + this.timeHeldMs + '}';
        }
    }

    public WakeLockStats(List<WakeLock> wakeLocks) {
        this.mWakeLocks = wakeLocks;
    }

    public List<WakeLock> getWakeLocks() {
        return this.mWakeLocks;
    }

    private WakeLockStats(Parcel in) {
        int size = in.readInt();
        this.mWakeLocks = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            this.mWakeLocks.add(new WakeLock(in));
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int size = this.mWakeLocks.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            WakeLock stats = this.mWakeLocks.get(i);
            stats.writeToParcel(out);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "WakeLockStats " + this.mWakeLocks;
    }
}
