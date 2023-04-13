package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class ScanResultInfoParcelable implements Parcelable {
    public static final Parcelable.Creator<ScanResultInfoParcelable> CREATOR = new Parcelable.Creator<ScanResultInfoParcelable>() { // from class: android.net.ScanResultInfoParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResultInfoParcelable createFromParcel(Parcel parcel) {
            ScanResultInfoParcelable scanResultInfoParcelable = new ScanResultInfoParcelable();
            scanResultInfoParcelable.readFromParcel(parcel);
            return scanResultInfoParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResultInfoParcelable[] newArray(int i) {
            return new ScanResultInfoParcelable[i];
        }
    };
    public String bssid;
    public InformationElementParcelable[] informationElements;
    public String ssid;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.ssid);
        parcel.writeString(this.bssid);
        parcel.writeTypedArray(this.informationElements, i);
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
                this.ssid = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.bssid = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.informationElements = (InformationElementParcelable[]) parcel.createTypedArray(InformationElementParcelable.CREATOR);
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
        stringJoiner.add("ssid: " + Objects.toString(this.ssid));
        stringJoiner.add("bssid: " + Objects.toString(this.bssid));
        stringJoiner.add("informationElements: " + Arrays.toString(this.informationElements));
        return "android.net.ScanResultInfoParcelable" + stringJoiner.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return describeContents(this.informationElements) | 0;
    }

    private int describeContents(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            int i = 0;
            for (Object obj2 : (Object[]) obj) {
                i |= describeContents(obj2);
            }
            return i;
        } else if (obj instanceof Parcelable) {
            return ((Parcelable) obj).describeContents();
        } else {
            return 0;
        }
    }
}
