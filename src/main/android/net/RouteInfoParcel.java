package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class RouteInfoParcel implements Parcelable {
    public static final Parcelable.Creator<RouteInfoParcel> CREATOR = new Parcelable.Creator<RouteInfoParcel>() { // from class: android.net.RouteInfoParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RouteInfoParcel createFromParcel(Parcel parcel) {
            RouteInfoParcel routeInfoParcel = new RouteInfoParcel();
            routeInfoParcel.readFromParcel(parcel);
            return routeInfoParcel;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RouteInfoParcel[] newArray(int i) {
            return new RouteInfoParcel[i];
        }
    };
    public String destination;
    public String ifName;
    public int mtu = 0;
    public String nextHop;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeString(this.destination);
        parcel.writeString(this.ifName);
        parcel.writeString(this.nextHop);
        parcel.writeInt(this.mtu);
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
                this.destination = parcel.readString();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.ifName = parcel.readString();
                    if (parcel.dataPosition() - dataPosition < readInt) {
                        this.nextHop = parcel.readString();
                        if (parcel.dataPosition() - dataPosition < readInt) {
                            this.mtu = parcel.readInt();
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
            parcel.setDataPosition(dataPosition + readInt);
        } catch (Throwable th) {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                throw new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            throw th;
        }
    }
}
