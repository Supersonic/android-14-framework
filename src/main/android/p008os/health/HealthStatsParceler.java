package android.p008os.health;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.os.health.HealthStatsParceler */
/* loaded from: classes3.dex */
public class HealthStatsParceler implements Parcelable {
    public static final Parcelable.Creator<HealthStatsParceler> CREATOR = new Parcelable.Creator<HealthStatsParceler>() { // from class: android.os.health.HealthStatsParceler.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HealthStatsParceler createFromParcel(Parcel in) {
            return new HealthStatsParceler(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HealthStatsParceler[] newArray(int size) {
            return new HealthStatsParceler[size];
        }
    };
    private HealthStats mHealthStats;
    private HealthStatsWriter mWriter;

    public HealthStatsParceler(HealthStatsWriter writer) {
        this.mWriter = writer;
    }

    public HealthStatsParceler(Parcel in) {
        this.mHealthStats = new HealthStats(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        HealthStatsWriter healthStatsWriter = this.mWriter;
        if (healthStatsWriter != null) {
            healthStatsWriter.flattenToParcel(out);
            return;
        }
        throw new RuntimeException("Can not re-parcel HealthStatsParceler that was constructed from a Parcel");
    }

    public HealthStats getHealthStats() {
        if (this.mWriter != null) {
            Parcel parcel = Parcel.obtain();
            this.mWriter.flattenToParcel(parcel);
            parcel.setDataPosition(0);
            this.mHealthStats = new HealthStats(parcel);
            parcel.recycle();
        }
        return this.mHealthStats;
    }
}
