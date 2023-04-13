package android.hardware.location;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telecom.Logging.Session;
import java.util.Arrays;
import libcore.util.HexEncoding;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class ContextHubMessage implements Parcelable {
    public static final Parcelable.Creator<ContextHubMessage> CREATOR = new Parcelable.Creator<ContextHubMessage>() { // from class: android.hardware.location.ContextHubMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContextHubMessage createFromParcel(Parcel in) {
            return new ContextHubMessage(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContextHubMessage[] newArray(int size) {
            return new ContextHubMessage[size];
        }
    };
    private static final int DEBUG_LOG_NUM_BYTES = 16;
    private byte[] mData;
    private int mType;
    private int mVersion;

    public int getMsgType() {
        return this.mType;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public byte[] getData() {
        byte[] bArr = this.mData;
        return Arrays.copyOf(bArr, bArr.length);
    }

    public void setMsgType(int msgType) {
        this.mType = msgType;
    }

    public void setVersion(int version) {
        this.mVersion = version;
    }

    public void setMsgData(byte[] data) {
        this.mData = Arrays.copyOf(data, data.length);
    }

    public ContextHubMessage(int msgType, int version, byte[] data) {
        this.mType = msgType;
        this.mVersion = version;
        this.mData = Arrays.copyOf(data, data.length);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private ContextHubMessage(Parcel in) {
        this.mType = in.readInt();
        this.mVersion = in.readInt();
        int bufferLength = in.readInt();
        byte[] bArr = new byte[bufferLength];
        this.mData = bArr;
        in.readByteArray(bArr);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mType);
        out.writeInt(this.mVersion);
        out.writeInt(this.mData.length);
        out.writeByteArray(this.mData);
    }

    public String toString() {
        int length = this.mData.length;
        String ret = "ContextHubMessage[type = " + this.mType + ", length = " + this.mData.length + " bytes](";
        if (length > 0) {
            ret = ret + "data = 0x";
        }
        for (int i = 0; i < Math.min(length, 16); i++) {
            ret = ret + HexEncoding.encodeToString(this.mData[i], true);
            if ((i + 1) % 4 == 0) {
                ret = ret + " ";
            }
        }
        if (length > 16) {
            ret = ret + Session.TRUNCATE_STRING;
        }
        return ret + NavigationBarInflaterView.KEY_CODE_END;
    }
}
