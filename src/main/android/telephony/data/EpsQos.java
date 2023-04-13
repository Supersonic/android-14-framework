package android.telephony.data;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.data.Qos;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class EpsQos extends Qos implements Parcelable {
    public static final Parcelable.Creator<EpsQos> CREATOR = new Parcelable.Creator<EpsQos>() { // from class: android.telephony.data.EpsQos.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EpsQos createFromParcel(Parcel source) {
            return new EpsQos(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public EpsQos[] newArray(int size) {
            return new EpsQos[size];
        }
    };
    int qosClassId;

    public EpsQos(Qos.QosBandwidth downlink, Qos.QosBandwidth uplink, int qosClassId) {
        super(1, downlink, uplink);
        this.qosClassId = qosClassId;
    }

    private EpsQos(Parcel source) {
        super(source);
        this.qosClassId = source.readInt();
    }

    public int getQci() {
        return this.qosClassId;
    }

    public static EpsQos createFromParcelBody(Parcel in) {
        return new EpsQos(in);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(1, dest, flags);
        dest.writeInt(this.qosClassId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.data.Qos
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.qosClassId));
    }

    @Override // android.telephony.data.Qos
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof EpsQos)) {
            return false;
        }
        EpsQos other = (EpsQos) o;
        if (this.qosClassId == other.qosClassId && super.equals(other)) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "EpsQos { qosClassId=" + this.qosClassId + " downlink=" + this.downlink + " uplink=" + this.uplink + "}";
    }
}
