package android.p008os;

import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* renamed from: android.os.BluetoothBatteryStats */
/* loaded from: classes3.dex */
public class BluetoothBatteryStats implements Parcelable {
    public static final Parcelable.Creator<BluetoothBatteryStats> CREATOR = new Parcelable.Creator<BluetoothBatteryStats>() { // from class: android.os.BluetoothBatteryStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothBatteryStats createFromParcel(Parcel in) {
            return new BluetoothBatteryStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothBatteryStats[] newArray(int size) {
            return new BluetoothBatteryStats[size];
        }
    };
    private final List<UidStats> mUidStats;

    /* renamed from: android.os.BluetoothBatteryStats$UidStats */
    /* loaded from: classes3.dex */
    public static class UidStats {
        public final long rxTimeMs;
        public final int scanResultCount;
        public final long scanTimeMs;
        public final long txTimeMs;
        public final int uid;
        public final long unoptimizedScanTimeMs;

        public UidStats(int uid, long scanTimeMs, long unoptimizedScanTimeMs, int scanResultCount, long rxTimeMs, long txTimeMs) {
            this.uid = uid;
            this.scanTimeMs = scanTimeMs;
            this.unoptimizedScanTimeMs = unoptimizedScanTimeMs;
            this.scanResultCount = scanResultCount;
            this.rxTimeMs = rxTimeMs;
            this.txTimeMs = txTimeMs;
        }

        private UidStats(Parcel in) {
            this.uid = in.readInt();
            this.scanTimeMs = in.readLong();
            this.unoptimizedScanTimeMs = in.readLong();
            this.scanResultCount = in.readInt();
            this.rxTimeMs = in.readLong();
            this.txTimeMs = in.readLong();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void writeToParcel(Parcel out) {
            out.writeInt(this.uid);
            out.writeLong(this.scanTimeMs);
            out.writeLong(this.unoptimizedScanTimeMs);
            out.writeInt(this.scanResultCount);
            out.writeLong(this.rxTimeMs);
            out.writeLong(this.txTimeMs);
        }

        public String toString() {
            return "UidStats{uid=" + this.uid + ", scanTimeMs=" + this.scanTimeMs + ", unoptimizedScanTimeMs=" + this.unoptimizedScanTimeMs + ", scanResultCount=" + this.scanResultCount + ", rxTimeMs=" + this.rxTimeMs + ", txTimeMs=" + this.txTimeMs + '}';
        }
    }

    public BluetoothBatteryStats(List<UidStats> uidStats) {
        this.mUidStats = uidStats;
    }

    public List<UidStats> getUidStats() {
        return this.mUidStats;
    }

    protected BluetoothBatteryStats(Parcel in) {
        int size = in.readInt();
        this.mUidStats = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            this.mUidStats.add(new UidStats(in));
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        int size = this.mUidStats.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            UidStats stats = this.mUidStats.get(i);
            stats.writeToParcel(out);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
