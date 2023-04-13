package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class DataStallReportParcelable implements Parcelable {
    public static final Parcelable.Creator<DataStallReportParcelable> CREATOR = new Parcelable.Creator<DataStallReportParcelable>() { // from class: android.net.DataStallReportParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataStallReportParcelable createFromParcel(Parcel parcel) {
            DataStallReportParcelable dataStallReportParcelable = new DataStallReportParcelable();
            dataStallReportParcelable.readFromParcel(parcel);
            return dataStallReportParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DataStallReportParcelable[] newArray(int i) {
            return new DataStallReportParcelable[i];
        }
    };
    public long timestampMillis = 0;
    public int detectionMethod = 1;
    public int tcpPacketFailRate = 2;
    public int tcpMetricsCollectionPeriodMillis = 3;
    public int dnsConsecutiveTimeouts = 4;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeLong(this.timestampMillis);
        parcel.writeInt(this.detectionMethod);
        parcel.writeInt(this.tcpPacketFailRate);
        parcel.writeInt(this.tcpMetricsCollectionPeriodMillis);
        parcel.writeInt(this.dnsConsecutiveTimeouts);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
            if (readInt < 4) {
                throw new BadParcelableException("Parcelable too small");
            }
            if (parcel.dataPosition() - dataPosition < readInt) {
                this.timestampMillis = parcel.readLong();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.detectionMethod = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.tcpPacketFailRate = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.tcpMetricsCollectionPeriodMillis = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.dnsConsecutiveTimeouts = parcel.readInt();
                                if (dataPosition > Integer.MAX_VALUE - readInt) {
                                    throw new BadParcelableException("Overflow in the size of parcelable");
                                }
                                parcel.setDataPosition(dataPosition + readInt);
                                return;
                            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                                throw new BadParcelableException("Overflow in the size of parcelable");
                            }
                        } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
        } catch (Throwable th) {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            throw th;
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("timestampMillis: " + this.timestampMillis);
        stringJoiner.add("detectionMethod: " + this.detectionMethod);
        stringJoiner.add("tcpPacketFailRate: " + this.tcpPacketFailRate);
        stringJoiner.add("tcpMetricsCollectionPeriodMillis: " + this.tcpMetricsCollectionPeriodMillis);
        stringJoiner.add("dnsConsecutiveTimeouts: " + this.dnsConsecutiveTimeouts);
        return "android.net.DataStallReportParcelable" + stringJoiner.toString();
    }
}
