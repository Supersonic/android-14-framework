package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.security.InvalidParameterException;
@SystemApi
@Deprecated
/* loaded from: classes2.dex */
public class GpsNavigationMessage implements Parcelable {
    public static final short STATUS_PARITY_PASSED = 1;
    public static final short STATUS_PARITY_REBUILT = 2;
    public static final short STATUS_UNKNOWN = 0;
    public static final byte TYPE_CNAV2 = 4;
    public static final byte TYPE_L1CA = 1;
    public static final byte TYPE_L2CNAV = 2;
    public static final byte TYPE_L5CNAV = 3;
    public static final byte TYPE_UNKNOWN = 0;
    private byte[] mData;
    private short mMessageId;
    private byte mPrn;
    private short mStatus;
    private short mSubmessageId;
    private byte mType;
    private static final byte[] EMPTY_ARRAY = new byte[0];
    public static final Parcelable.Creator<GpsNavigationMessage> CREATOR = new Parcelable.Creator<GpsNavigationMessage>() { // from class: android.location.GpsNavigationMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsNavigationMessage createFromParcel(Parcel parcel) {
            GpsNavigationMessage navigationMessage = new GpsNavigationMessage();
            navigationMessage.setType(parcel.readByte());
            navigationMessage.setPrn(parcel.readByte());
            navigationMessage.setMessageId((short) parcel.readInt());
            navigationMessage.setSubmessageId((short) parcel.readInt());
            int dataLength = parcel.readInt();
            byte[] data = new byte[dataLength];
            parcel.readByteArray(data);
            navigationMessage.setData(data);
            int status = parcel.readInt();
            navigationMessage.setStatus((short) status);
            return navigationMessage;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GpsNavigationMessage[] newArray(int size) {
            return new GpsNavigationMessage[size];
        }
    };

    GpsNavigationMessage() {
        initialize();
    }

    public void set(GpsNavigationMessage navigationMessage) {
        this.mType = navigationMessage.mType;
        this.mPrn = navigationMessage.mPrn;
        this.mMessageId = navigationMessage.mMessageId;
        this.mSubmessageId = navigationMessage.mSubmessageId;
        this.mData = navigationMessage.mData;
        this.mStatus = navigationMessage.mStatus;
    }

    public void reset() {
        initialize();
    }

    public byte getType() {
        return this.mType;
    }

    public void setType(byte value) {
        this.mType = value;
    }

    private String getTypeString() {
        switch (this.mType) {
            case 0:
                return "Unknown";
            case 1:
                return "L1 C/A";
            case 2:
                return "L2-CNAV";
            case 3:
                return "L5-CNAV";
            case 4:
                return "CNAV-2";
            default:
                return "<Invalid:" + ((int) this.mType) + ">";
        }
    }

    public byte getPrn() {
        return this.mPrn;
    }

    public void setPrn(byte value) {
        this.mPrn = value;
    }

    public short getMessageId() {
        return this.mMessageId;
    }

    public void setMessageId(short value) {
        this.mMessageId = value;
    }

    public short getSubmessageId() {
        return this.mSubmessageId;
    }

    public void setSubmessageId(short value) {
        this.mSubmessageId = value;
    }

    public byte[] getData() {
        return this.mData;
    }

    public void setData(byte[] value) {
        if (value == null) {
            throw new InvalidParameterException("Data must be a non-null array");
        }
        this.mData = value;
    }

    public short getStatus() {
        return this.mStatus;
    }

    public void setStatus(short value) {
        this.mStatus = value;
    }

    private String getStatusString() {
        switch (this.mStatus) {
            case 0:
                return "Unknown";
            case 1:
                return "ParityPassed";
            case 2:
                return "ParityRebuilt";
            default:
                return "<Invalid:" + ((int) this.mStatus) + ">";
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeByte(this.mType);
        parcel.writeByte(this.mPrn);
        parcel.writeInt(this.mMessageId);
        parcel.writeInt(this.mSubmessageId);
        parcel.writeInt(this.mData.length);
        parcel.writeByteArray(this.mData);
        parcel.writeInt(this.mStatus);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        byte[] bArr;
        StringBuilder builder = new StringBuilder("GpsNavigationMessage:\n");
        builder.append(String.format("   %-15s = %s\n", "Type", getTypeString()));
        builder.append(String.format("   %-15s = %s\n", "Prn", Byte.valueOf(this.mPrn)));
        builder.append(String.format("   %-15s = %s\n", "Status", getStatusString()));
        builder.append(String.format("   %-15s = %s\n", "MessageId", Short.valueOf(this.mMessageId)));
        builder.append(String.format("   %-15s = %s\n", "SubmessageId", Short.valueOf(this.mSubmessageId)));
        builder.append(String.format("   %-15s = %s\n", "Data", "{"));
        String prefix = "        ";
        for (byte value : this.mData) {
            builder.append(prefix);
            builder.append((int) value);
            prefix = ", ";
        }
        builder.append(" }");
        return builder.toString();
    }

    private void initialize() {
        this.mType = (byte) 0;
        this.mPrn = (byte) 0;
        this.mMessageId = (short) -1;
        this.mSubmessageId = (short) -1;
        this.mData = EMPTY_ARRAY;
        this.mStatus = (short) 0;
    }
}
