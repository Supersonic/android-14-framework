package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public class IccOpenLogicalChannelResponse implements Parcelable {
    public static final Parcelable.Creator<IccOpenLogicalChannelResponse> CREATOR = new Parcelable.Creator<IccOpenLogicalChannelResponse>() { // from class: android.telephony.IccOpenLogicalChannelResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IccOpenLogicalChannelResponse createFromParcel(Parcel in) {
            return new IccOpenLogicalChannelResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public IccOpenLogicalChannelResponse[] newArray(int size) {
            return new IccOpenLogicalChannelResponse[size];
        }
    };
    public static final int INVALID_CHANNEL = -1;
    public static final int STATUS_MISSING_RESOURCE = 2;
    public static final int STATUS_NO_ERROR = 1;
    public static final int STATUS_NO_SUCH_ELEMENT = 3;
    public static final int STATUS_UNKNOWN_ERROR = 4;
    private final int mChannel;
    private final byte[] mSelectResponse;
    private final int mStatus;

    public IccOpenLogicalChannelResponse(int channel, int status, byte[] selectResponse) {
        this.mChannel = channel;
        this.mStatus = status;
        this.mSelectResponse = selectResponse;
    }

    private IccOpenLogicalChannelResponse(Parcel in) {
        this.mChannel = in.readInt();
        this.mStatus = in.readInt();
        int arrayLength = in.readInt();
        if (arrayLength > 0) {
            byte[] bArr = new byte[arrayLength];
            this.mSelectResponse = bArr;
            in.readByteArray(bArr);
            return;
        }
        this.mSelectResponse = null;
    }

    public int getChannel() {
        return this.mChannel;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public byte[] getSelectResponse() {
        return this.mSelectResponse;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mChannel);
        out.writeInt(this.mStatus);
        byte[] bArr = this.mSelectResponse;
        if (bArr != null && bArr.length > 0) {
            out.writeInt(bArr.length);
            out.writeByteArray(this.mSelectResponse);
            return;
        }
        out.writeInt(0);
    }

    public String toString() {
        return "Channel: " + this.mChannel + " Status: " + this.mStatus;
    }
}
