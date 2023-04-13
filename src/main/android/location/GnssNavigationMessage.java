package android.location;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.InvalidParameterException;
/* loaded from: classes2.dex */
public final class GnssNavigationMessage implements Parcelable {
    public static final int STATUS_PARITY_PASSED = 1;
    public static final int STATUS_PARITY_REBUILT = 2;
    public static final int STATUS_UNKNOWN = 0;
    public static final int TYPE_BDS_CNAV1 = 1283;
    public static final int TYPE_BDS_CNAV2 = 1284;
    public static final int TYPE_BDS_D1 = 1281;
    public static final int TYPE_BDS_D2 = 1282;
    public static final int TYPE_GAL_F = 1538;
    public static final int TYPE_GAL_I = 1537;
    public static final int TYPE_GLO_L1CA = 769;
    public static final int TYPE_GPS_CNAV2 = 260;
    public static final int TYPE_GPS_L1CA = 257;
    public static final int TYPE_GPS_L2CNAV = 258;
    public static final int TYPE_GPS_L5CNAV = 259;
    public static final int TYPE_IRN_L5CA = 1793;
    public static final int TYPE_QZS_L1CA = 1025;
    public static final int TYPE_SBS = 513;
    public static final int TYPE_UNKNOWN = 0;
    private byte[] mData;
    private int mMessageId;
    private int mStatus;
    private int mSubmessageId;
    private int mSvid;
    private int mType;
    private static final byte[] EMPTY_ARRAY = new byte[0];
    public static final Parcelable.Creator<GnssNavigationMessage> CREATOR = new Parcelable.Creator<GnssNavigationMessage>() { // from class: android.location.GnssNavigationMessage.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssNavigationMessage createFromParcel(Parcel parcel) {
            GnssNavigationMessage navigationMessage = new GnssNavigationMessage();
            navigationMessage.setType(parcel.readInt());
            navigationMessage.setSvid(parcel.readInt());
            navigationMessage.setMessageId(parcel.readInt());
            navigationMessage.setSubmessageId(parcel.readInt());
            int dataLength = parcel.readInt();
            byte[] data = new byte[dataLength];
            parcel.readByteArray(data);
            navigationMessage.setData(data);
            navigationMessage.setStatus(parcel.readInt());
            return navigationMessage;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssNavigationMessage[] newArray(int size) {
            return new GnssNavigationMessage[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface GnssNavigationMessageStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface GnssNavigationMessageType {
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        @Deprecated
        public static final int STATUS_LOCATION_DISABLED = 2;
        @Deprecated
        public static final int STATUS_NOT_SUPPORTED = 0;
        @Deprecated
        public static final int STATUS_READY = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface GnssNavigationMessageStatus {
        }

        public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
        }

        @Deprecated
        public void onStatusChanged(int status) {
        }
    }

    public GnssNavigationMessage() {
        initialize();
    }

    public void set(GnssNavigationMessage navigationMessage) {
        this.mType = navigationMessage.mType;
        this.mSvid = navigationMessage.mSvid;
        this.mMessageId = navigationMessage.mMessageId;
        this.mSubmessageId = navigationMessage.mSubmessageId;
        this.mData = navigationMessage.mData;
        this.mStatus = navigationMessage.mStatus;
    }

    public void reset() {
        initialize();
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int value) {
        this.mType = value;
    }

    private String getTypeString() {
        switch (this.mType) {
            case 0:
                return "Unknown";
            case 257:
                return "GPS L1 C/A";
            case 258:
                return "GPS L2-CNAV";
            case 259:
                return "GPS L5-CNAV";
            case 260:
                return "GPS CNAV2";
            case 513:
                return "SBS";
            case 769:
                return "Glonass L1 C/A";
            case 1025:
                return "QZSS L1 C/A";
            case 1281:
                return "Beidou D1";
            case 1282:
                return "Beidou D2";
            case 1283:
                return "Beidou CNAV1";
            case 1284:
                return "Beidou CNAV2";
            case 1537:
                return "Galileo I";
            case 1538:
                return "Galileo F";
            case 1793:
                return "IRNSS L5 C/A";
            default:
                return "<Invalid:" + this.mType + ">";
        }
    }

    public int getSvid() {
        return this.mSvid;
    }

    public void setSvid(int value) {
        this.mSvid = value;
    }

    public int getMessageId() {
        return this.mMessageId;
    }

    public void setMessageId(int value) {
        this.mMessageId = value;
    }

    public int getSubmessageId() {
        return this.mSubmessageId;
    }

    public void setSubmessageId(int value) {
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

    public int getStatus() {
        return this.mStatus;
    }

    public void setStatus(int value) {
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
                return "<Invalid:" + this.mStatus + ">";
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mSvid);
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
        StringBuilder builder = new StringBuilder("GnssNavigationMessage:\n");
        builder.append(String.format("   %-15s = %s\n", "Type", getTypeString()));
        builder.append(String.format("   %-15s = %s\n", "Svid", Integer.valueOf(this.mSvid)));
        builder.append(String.format("   %-15s = %s\n", "Status", getStatusString()));
        builder.append(String.format("   %-15s = %s\n", "MessageId", Integer.valueOf(this.mMessageId)));
        builder.append(String.format("   %-15s = %s\n", "SubmessageId", Integer.valueOf(this.mSubmessageId)));
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
        this.mType = 0;
        this.mSvid = 0;
        this.mMessageId = -1;
        this.mSubmessageId = -1;
        this.mData = EMPTY_ARRAY;
        this.mStatus = 0;
    }
}
