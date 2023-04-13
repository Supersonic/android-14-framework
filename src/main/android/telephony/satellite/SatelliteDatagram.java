package android.telephony.satellite;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public final class SatelliteDatagram implements Parcelable {
    public static final Parcelable.Creator<SatelliteDatagram> CREATOR = new Parcelable.Creator<SatelliteDatagram>() { // from class: android.telephony.satellite.SatelliteDatagram.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SatelliteDatagram createFromParcel(Parcel in) {
            return new SatelliteDatagram(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SatelliteDatagram[] newArray(int size) {
            return new SatelliteDatagram[size];
        }
    };
    private byte[] mData;

    public SatelliteDatagram(byte[] data) {
        this.mData = data;
    }

    private SatelliteDatagram(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeByteArray(this.mData);
    }

    public byte[] getSatelliteDatagram() {
        return this.mData;
    }

    private void readFromParcel(Parcel in) {
        this.mData = in.createByteArray();
    }
}
