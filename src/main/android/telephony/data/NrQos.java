package android.telephony.data;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.data.Qos;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class NrQos extends Qos implements Parcelable {
    public static final Parcelable.Creator<NrQos> CREATOR = new Parcelable.Creator<NrQos>() { // from class: android.telephony.data.NrQos.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrQos createFromParcel(Parcel source) {
            return new NrQos(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrQos[] newArray(int size) {
            return new NrQos[size];
        }
    };
    int averagingWindowMs;
    int fiveQi;
    int qosFlowId;

    public NrQos(Qos.QosBandwidth downlink, Qos.QosBandwidth uplink, int qosFlowId, int fiveQi, int averagingWindowMs) {
        super(2, downlink, uplink);
        this.qosFlowId = qosFlowId;
        this.fiveQi = fiveQi;
        this.averagingWindowMs = averagingWindowMs;
    }

    private NrQos(Parcel source) {
        super(source);
        this.qosFlowId = source.readInt();
        this.fiveQi = source.readInt();
        this.averagingWindowMs = source.readInt();
    }

    public static NrQos createFromParcelBody(Parcel in) {
        return new NrQos(in);
    }

    public int get5Qi() {
        return this.fiveQi;
    }

    public int getQfi() {
        return this.qosFlowId;
    }

    public int getAveragingWindow() {
        return this.averagingWindowMs;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(2, dest, flags);
        dest.writeInt(this.qosFlowId);
        dest.writeInt(this.fiveQi);
        dest.writeInt(this.averagingWindowMs);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.data.Qos
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.qosFlowId), Integer.valueOf(this.fiveQi), Integer.valueOf(this.averagingWindowMs));
    }

    @Override // android.telephony.data.Qos
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof NrQos)) {
            return false;
        }
        NrQos other = (NrQos) o;
        if (!super.equals(other)) {
            return false;
        }
        if (this.qosFlowId == other.qosFlowId && this.fiveQi == other.fiveQi && this.averagingWindowMs == other.averagingWindowMs) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "NrQos { fiveQi=" + this.fiveQi + " downlink=" + this.downlink + " uplink=" + this.uplink + " qosFlowId=" + this.qosFlowId + " averagingWindowMs=" + this.averagingWindowMs + "}";
    }
}
