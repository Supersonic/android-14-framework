package android.service.contentcapture;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class FlushMetrics implements Parcelable {
    public static final Parcelable.Creator<FlushMetrics> CREATOR = new Parcelable.Creator<FlushMetrics>() { // from class: android.service.contentcapture.FlushMetrics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FlushMetrics createFromParcel(Parcel in) {
            FlushMetrics flushMetrics = new FlushMetrics();
            flushMetrics.sessionStarted = in.readInt();
            flushMetrics.sessionFinished = in.readInt();
            flushMetrics.viewAppearedCount = in.readInt();
            flushMetrics.viewDisappearedCount = in.readInt();
            flushMetrics.viewTextChangedCount = in.readInt();
            return flushMetrics;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FlushMetrics[] newArray(int size) {
            return new FlushMetrics[size];
        }
    };
    public int sessionFinished;
    public int sessionStarted;
    public int viewAppearedCount;
    public int viewDisappearedCount;
    public int viewTextChangedCount;

    public void reset() {
        this.viewAppearedCount = 0;
        this.viewDisappearedCount = 0;
        this.viewTextChangedCount = 0;
        this.sessionStarted = 0;
        this.sessionFinished = 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.sessionStarted);
        out.writeInt(this.sessionFinished);
        out.writeInt(this.viewAppearedCount);
        out.writeInt(this.viewDisappearedCount);
        out.writeInt(this.viewTextChangedCount);
    }
}
