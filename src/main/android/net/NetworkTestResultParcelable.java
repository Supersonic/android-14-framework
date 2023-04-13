package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NetworkTestResultParcelable implements Parcelable {
    public static final Parcelable.Creator<NetworkTestResultParcelable> CREATOR = new Parcelable.Creator<NetworkTestResultParcelable>() { // from class: android.net.NetworkTestResultParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkTestResultParcelable createFromParcel(Parcel parcel) {
            NetworkTestResultParcelable networkTestResultParcelable = new NetworkTestResultParcelable();
            networkTestResultParcelable.readFromParcel(parcel);
            return networkTestResultParcelable;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NetworkTestResultParcelable[] newArray(int i) {
            return new NetworkTestResultParcelable[i];
        }
    };
    public String redirectUrl;
    public long timestampMillis = 0;
    public int result = 0;
    public int probesSucceeded = 0;
    public int probesAttempted = 0;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeLong(this.timestampMillis);
        parcel.writeInt(this.result);
        parcel.writeInt(this.probesSucceeded);
        parcel.writeInt(this.probesAttempted);
        parcel.writeString(this.redirectUrl);
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
                    this.result = parcel.readInt();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.probesSucceeded = parcel.readInt();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.probesAttempted = parcel.readInt();
                            if (parcel.dataPosition() - dataPosition < readInt) {
                                this.redirectUrl = parcel.readString();
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
        stringJoiner.add("result: " + this.result);
        stringJoiner.add("probesSucceeded: " + this.probesSucceeded);
        stringJoiner.add("probesAttempted: " + this.probesAttempted);
        stringJoiner.add("redirectUrl: " + Objects.toString(this.redirectUrl));
        return "android.net.NetworkTestResultParcelable" + stringJoiner.toString();
    }
}
