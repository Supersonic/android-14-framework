package android.hardware.location;

import android.annotation.SystemApi;
import android.hardware.hdmi.HdmiControlManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telecom.Logging.Session;
import java.util.Arrays;
import libcore.util.HexEncoding;
@SystemApi
/* loaded from: classes2.dex */
public final class NanoAppMessage implements Parcelable {
    public static final Parcelable.Creator<NanoAppMessage> CREATOR = new Parcelable.Creator<NanoAppMessage>() { // from class: android.hardware.location.NanoAppMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppMessage createFromParcel(Parcel in) {
            return new NanoAppMessage(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppMessage[] newArray(int size) {
            return new NanoAppMessage[size];
        }
    };
    private static final int DEBUG_LOG_NUM_BYTES = 16;
    private boolean mIsBroadcasted;
    private byte[] mMessageBody;
    private int mMessageType;
    private long mNanoAppId;

    private NanoAppMessage(long nanoAppId, int messageType, byte[] messageBody, boolean broadcasted) {
        this.mNanoAppId = nanoAppId;
        this.mMessageType = messageType;
        this.mMessageBody = messageBody;
        this.mIsBroadcasted = broadcasted;
    }

    public static NanoAppMessage createMessageToNanoApp(long targetNanoAppId, int messageType, byte[] messageBody) {
        return new NanoAppMessage(targetNanoAppId, messageType, messageBody, false);
    }

    public static NanoAppMessage createMessageFromNanoApp(long sourceNanoAppId, int messageType, byte[] messageBody, boolean broadcasted) {
        return new NanoAppMessage(sourceNanoAppId, messageType, messageBody, broadcasted);
    }

    public long getNanoAppId() {
        return this.mNanoAppId;
    }

    public int getMessageType() {
        return this.mMessageType;
    }

    public byte[] getMessageBody() {
        return this.mMessageBody;
    }

    public boolean isBroadcastMessage() {
        return this.mIsBroadcasted;
    }

    private NanoAppMessage(Parcel in) {
        this.mNanoAppId = in.readLong();
        this.mIsBroadcasted = in.readInt() == 1;
        this.mMessageType = in.readInt();
        int msgSize = in.readInt();
        byte[] bArr = new byte[msgSize];
        this.mMessageBody = bArr;
        in.readByteArray(bArr);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mNanoAppId);
        out.writeInt(this.mIsBroadcasted ? 1 : 0);
        out.writeInt(this.mMessageType);
        out.writeInt(this.mMessageBody.length);
        out.writeByteArray(this.mMessageBody);
    }

    public String toString() {
        int length = this.mMessageBody.length;
        String ret = "NanoAppMessage[type = " + this.mMessageType + ", length = " + this.mMessageBody.length + " bytes, " + (this.mIsBroadcasted ? HdmiControlManager.POWER_CONTROL_MODE_BROADCAST : "unicast") + ", nanoapp = 0x" + Long.toHexString(this.mNanoAppId) + "](";
        if (length > 0) {
            ret = ret + "data = 0x";
        }
        for (int i = 0; i < Math.min(length, 16); i++) {
            ret = ret + HexEncoding.encodeToString(this.mMessageBody[i], true);
            if ((i + 1) % 4 == 0) {
                ret = ret + " ";
            }
        }
        if (length > 16) {
            ret = ret + Session.TRUNCATE_STRING;
        }
        return ret + NavigationBarInflaterView.KEY_CODE_END;
    }

    public boolean equals(Object object) {
        boolean z = true;
        if (object == this) {
            return true;
        }
        if (!(object instanceof NanoAppMessage)) {
            return false;
        }
        NanoAppMessage other = (NanoAppMessage) object;
        boolean isEqual = (other.getNanoAppId() == this.mNanoAppId && other.getMessageType() == this.mMessageType && other.isBroadcastMessage() == this.mIsBroadcasted && Arrays.equals(other.getMessageBody(), this.mMessageBody)) ? false : false;
        return isEqual;
    }
}
