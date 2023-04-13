package android.net.networkstack.aidl.p004ip;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* renamed from: android.net.networkstack.aidl.ip.ReachabilityLossInfoParcelable */
/* loaded from: classes.dex */
public class ReachabilityLossInfoParcelable implements Parcelable {
    public static final Parcelable.Creator<ReachabilityLossInfoParcelable> CREATOR = new Parcelable.Creator<ReachabilityLossInfoParcelable>() { // from class: android.net.networkstack.aidl.ip.ReachabilityLossInfoParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ReachabilityLossInfoParcelable createFromParcel(Parcel parcel) {
            return ReachabilityLossInfoParcelable.internalCreateFromParcel(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ReachabilityLossInfoParcelable[] newArray(int i) {
            return new ReachabilityLossInfoParcelable[i];
        }
    };
    public final String message;
    public final int reason;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: android.net.networkstack.aidl.ip.ReachabilityLossInfoParcelable$Builder */
    /* loaded from: classes.dex */
    public static final class Builder {
        private String message;
        private int reason;

        public Builder setMessage(String str) {
            this.message = str;
            return this;
        }

        public Builder setReason(int i) {
            this.reason = i;
            return this;
        }

        public ReachabilityLossInfoParcelable build() {
            return new ReachabilityLossInfoParcelable(this.message, this.reason);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.message);
        parcel.writeInt(this.reason);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public ReachabilityLossInfoParcelable(String str, int i) {
        this.message = str;
        this.reason = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ReachabilityLossInfoParcelable internalCreateFromParcel(Parcel parcel) {
        Builder builder = new Builder();
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
        } finally {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                BadParcelableException badParcelableException = new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        if (readInt >= 4) {
            builder.build();
            if (parcel.dataPosition() - dataPosition >= readInt) {
                builder.build();
                if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else {
                builder.setMessage(parcel.readString());
                if (parcel.dataPosition() - dataPosition >= readInt) {
                    builder.build();
                    int i = Integer.MAX_VALUE - readInt;
                    if (dataPosition > i) {
                        throw new BadParcelableException(r4);
                    }
                } else {
                    builder.setReason(parcel.readInt());
                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                }
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        throw new BadParcelableException("Parcelable too small");
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("message: " + Objects.toString(this.message));
        stringJoiner.add("reason: " + this.reason);
        return "android.net.networkstack.aidl.ip.ReachabilityLossInfoParcelable" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof ReachabilityLossInfoParcelable)) {
            ReachabilityLossInfoParcelable reachabilityLossInfoParcelable = (ReachabilityLossInfoParcelable) obj;
            return Objects.deepEquals(this.message, reachabilityLossInfoParcelable.message) && Objects.deepEquals(Integer.valueOf(this.reason), Integer.valueOf(reachabilityLossInfoParcelable.reason));
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(this.message, Integer.valueOf(this.reason)).toArray());
    }
}
