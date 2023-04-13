package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public class PcoData implements Parcelable {
    public static final Parcelable.Creator<PcoData> CREATOR = new Parcelable.Creator() { // from class: android.telephony.PcoData.1
        @Override // android.p008os.Parcelable.Creator
        public PcoData createFromParcel(Parcel in) {
            return new PcoData(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public PcoData[] newArray(int size) {
            return new PcoData[size];
        }
    };
    public final String bearerProto;
    public final int cid;
    public final byte[] contents;
    public final int pcoId;

    public PcoData(int cid, String bearerProto, int pcoId, byte[] contents) {
        this.cid = cid;
        this.bearerProto = bearerProto;
        this.pcoId = pcoId;
        this.contents = contents;
    }

    public PcoData(Parcel in) {
        this.cid = in.readInt();
        this.bearerProto = in.readString();
        this.pcoId = in.readInt();
        this.contents = in.createByteArray();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.cid);
        out.writeString(this.bearerProto);
        out.writeInt(this.pcoId);
        out.writeByteArray(this.contents);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "PcoData(" + this.cid + ", " + this.bearerProto + ", " + this.pcoId + ", contents[" + this.contents.length + "])";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PcoData pcoData = (PcoData) o;
        if (this.cid == pcoData.cid && this.pcoId == pcoData.pcoId && Objects.equals(this.bearerProto, pcoData.bearerProto) && Arrays.equals(this.contents, pcoData.contents)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(Integer.valueOf(this.cid), this.bearerProto, Integer.valueOf(this.pcoId));
        return (result * 31) + Arrays.hashCode(this.contents);
    }
}
