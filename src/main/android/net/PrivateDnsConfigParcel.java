package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class PrivateDnsConfigParcel implements Parcelable {
    public static final Parcelable.Creator<PrivateDnsConfigParcel> CREATOR = new Parcelable.Creator<PrivateDnsConfigParcel>() { // from class: android.net.PrivateDnsConfigParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrivateDnsConfigParcel createFromParcel(Parcel parcel) {
            PrivateDnsConfigParcel privateDnsConfigParcel = new PrivateDnsConfigParcel();
            privateDnsConfigParcel.readFromParcel(parcel);
            return privateDnsConfigParcel;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrivateDnsConfigParcel[] newArray(int i) {
            return new PrivateDnsConfigParcel[i];
        }
    };
    public String hostname;
    public String[] ips;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.hostname);
        parcel.writeStringArray(this.ips);
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
                this.hostname = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.ips = parcel.createStringArray();
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
        stringJoiner.add("hostname: " + Objects.toString(this.hostname));
        stringJoiner.add("ips: " + Arrays.toString(this.ips));
        return "android.net.PrivateDnsConfigParcel" + stringJoiner.toString();
    }
}
