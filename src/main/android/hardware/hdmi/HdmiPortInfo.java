package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class HdmiPortInfo implements Parcelable {
    public static final Parcelable.Creator<HdmiPortInfo> CREATOR = new Parcelable.Creator<HdmiPortInfo>() { // from class: android.hardware.hdmi.HdmiPortInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdmiPortInfo createFromParcel(Parcel source) {
            int id = source.readInt();
            int type = source.readInt();
            int address = source.readInt();
            boolean cec = source.readInt() == 1;
            boolean arc = source.readInt() == 1;
            boolean mhl = source.readInt() == 1;
            boolean earc = source.readInt() == 1;
            return new Builder(id, type, address).setCecSupported(cec).setArcSupported(arc).setEarcSupported(earc).setMhlSupported(mhl).build();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdmiPortInfo[] newArray(int size) {
            return new HdmiPortInfo[size];
        }
    };
    public static final int PORT_INPUT = 0;
    public static final int PORT_OUTPUT = 1;
    private final int mAddress;
    private final boolean mArcSupported;
    private final boolean mCecSupported;
    private final boolean mEarcSupported;
    private final int mId;
    private final boolean mMhlSupported;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PortType {
    }

    @Deprecated
    public HdmiPortInfo(int id, int type, int address, boolean cec, boolean mhl, boolean arc) {
        this.mId = id;
        this.mType = type;
        this.mAddress = address;
        this.mCecSupported = cec;
        this.mArcSupported = arc;
        this.mEarcSupported = false;
        this.mMhlSupported = mhl;
    }

    public Builder toBuilder() {
        return new Builder();
    }

    private HdmiPortInfo(Builder builder) {
        this.mId = builder.mId;
        this.mType = builder.mType;
        this.mAddress = builder.mAddress;
        this.mCecSupported = builder.mCecSupported;
        this.mArcSupported = builder.mArcSupported;
        this.mEarcSupported = builder.mEarcSupported;
        this.mMhlSupported = builder.mMhlSupported;
    }

    public int getId() {
        return this.mId;
    }

    public int getType() {
        return this.mType;
    }

    public int getAddress() {
        return this.mAddress;
    }

    public boolean isCecSupported() {
        return this.mCecSupported;
    }

    public boolean isMhlSupported() {
        return this.mMhlSupported;
    }

    public boolean isArcSupported() {
        return this.mArcSupported;
    }

    public boolean isEarcSupported() {
        return this.mEarcSupported;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    @SystemApi
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeInt(this.mType);
        dest.writeInt(this.mAddress);
        dest.writeInt(this.mCecSupported ? 1 : 0);
        dest.writeInt(this.mArcSupported ? 1 : 0);
        dest.writeInt(this.mMhlSupported ? 1 : 0);
        dest.writeInt(this.mEarcSupported ? 1 : 0);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("port_id: ").append(this.mId).append(", ");
        s.append("type: ").append(this.mType == 0 ? "HDMI_IN" : "HDMI_OUT").append(", ");
        s.append("address: ").append(String.format("0x%04x", Integer.valueOf(this.mAddress))).append(", ");
        s.append("cec: ").append(this.mCecSupported).append(", ");
        s.append("arc: ").append(this.mArcSupported).append(", ");
        s.append("mhl: ").append(this.mMhlSupported).append(", ");
        s.append("earc: ").append(this.mEarcSupported);
        return s.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof HdmiPortInfo) {
            HdmiPortInfo other = (HdmiPortInfo) o;
            return this.mId == other.mId && this.mType == other.mType && this.mAddress == other.mAddress && this.mCecSupported == other.mCecSupported && this.mArcSupported == other.mArcSupported && this.mMhlSupported == other.mMhlSupported && this.mEarcSupported == other.mEarcSupported;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mId), Integer.valueOf(this.mType), Integer.valueOf(this.mAddress), Boolean.valueOf(this.mCecSupported), Boolean.valueOf(this.mArcSupported), Boolean.valueOf(this.mMhlSupported), Boolean.valueOf(this.mEarcSupported));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAddress;
        private boolean mArcSupported;
        private boolean mCecSupported;
        private boolean mEarcSupported;
        private int mId;
        private boolean mMhlSupported;
        private int mType;

        public Builder(int id, int type, int address) {
            if (type != 0 && type != 1) {
                throw new IllegalArgumentException("type should be 0 or 1.");
            }
            if (address < 0) {
                throw new IllegalArgumentException("address should be positive.");
            }
            this.mId = id;
            this.mType = type;
            this.mAddress = address;
        }

        private Builder(HdmiPortInfo hdmiPortInfo) {
            this.mId = hdmiPortInfo.mId;
            this.mType = hdmiPortInfo.mType;
            this.mAddress = hdmiPortInfo.mAddress;
            this.mCecSupported = hdmiPortInfo.mCecSupported;
            this.mArcSupported = hdmiPortInfo.mArcSupported;
            this.mEarcSupported = hdmiPortInfo.mEarcSupported;
            this.mMhlSupported = hdmiPortInfo.mMhlSupported;
        }

        public HdmiPortInfo build() {
            return new HdmiPortInfo(this);
        }

        public Builder setCecSupported(boolean supported) {
            this.mCecSupported = supported;
            return this;
        }

        public Builder setArcSupported(boolean supported) {
            this.mArcSupported = supported;
            return this;
        }

        public Builder setEarcSupported(boolean supported) {
            this.mEarcSupported = supported;
            return this;
        }

        public Builder setMhlSupported(boolean supported) {
            this.mMhlSupported = supported;
            return this;
        }
    }
}
