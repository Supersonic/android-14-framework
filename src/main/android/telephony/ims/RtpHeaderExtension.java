package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class RtpHeaderExtension implements Parcelable {
    public static final Parcelable.Creator<RtpHeaderExtension> CREATOR = new Parcelable.Creator<RtpHeaderExtension>() { // from class: android.telephony.ims.RtpHeaderExtension.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RtpHeaderExtension createFromParcel(Parcel in) {
            return new RtpHeaderExtension(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RtpHeaderExtension[] newArray(int size) {
            return new RtpHeaderExtension[size];
        }
    };
    private byte[] mExtensionData;
    private int mLocalIdentifier;

    public RtpHeaderExtension(int localIdentifier, byte[] extensionData) {
        if (localIdentifier < 1 || localIdentifier > 13) {
            throw new IllegalArgumentException("localIdentifier must be in range 1-14");
        }
        if (extensionData == null) {
            throw new NullPointerException("extensionDa is required.");
        }
        this.mLocalIdentifier = localIdentifier;
        this.mExtensionData = extensionData;
    }

    private RtpHeaderExtension(Parcel in) {
        this.mLocalIdentifier = in.readInt();
        this.mExtensionData = in.createByteArray();
    }

    public int getLocalIdentifier() {
        return this.mLocalIdentifier;
    }

    public byte[] getExtensionData() {
        return this.mExtensionData;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLocalIdentifier);
        dest.writeByteArray(this.mExtensionData);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RtpHeaderExtension that = (RtpHeaderExtension) o;
        if (this.mLocalIdentifier == that.mLocalIdentifier && Arrays.equals(this.mExtensionData, that.mExtensionData)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(Integer.valueOf(this.mLocalIdentifier));
        return (result * 31) + Arrays.hashCode(this.mExtensionData);
    }

    public String toString() {
        byte[] bArr;
        StringBuilder sb = new StringBuilder();
        sb.append("RtpHeaderExtension{mLocalIdentifier=");
        sb.append(this.mLocalIdentifier);
        sb.append(", mData=");
        for (byte b : this.mExtensionData) {
            sb.append(Integer.toBinaryString(b));
            sb.append("b_");
        }
        sb.append("}");
        return sb.toString();
    }
}
