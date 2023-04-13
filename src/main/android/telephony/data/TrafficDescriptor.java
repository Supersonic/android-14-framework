package android.telephony.data;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class TrafficDescriptor implements Parcelable {
    public static final Parcelable.Creator<TrafficDescriptor> CREATOR = new Parcelable.Creator<TrafficDescriptor>() { // from class: android.telephony.data.TrafficDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TrafficDescriptor createFromParcel(Parcel source) {
            return new TrafficDescriptor(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TrafficDescriptor[] newArray(int size) {
            return new TrafficDescriptor[size];
        }
    };
    private final String mDnn;
    private final OsAppId mOsAppId;

    /* loaded from: classes3.dex */
    public static final class OsAppId {
        private final String mAppId;
        private final int mDifferentiator;
        private final UUID mOsId;
        public static final UUID ANDROID_OS_ID = UUID.fromString("97a498e3-fc92-5c94-8986-0333d06e4e47");
        private static final Set<String> ALLOWED_APP_IDS = Set.of("ENTERPRISE", "PRIORITIZE_LATENCY", "PRIORITIZE_BANDWIDTH", "CBS");

        public OsAppId(UUID osId, String appId) {
            this(osId, appId, 1);
        }

        public OsAppId(UUID osId, String appId, int differentiator) {
            Objects.requireNonNull(osId);
            Objects.requireNonNull(appId);
            if (differentiator < 1) {
                throw new IllegalArgumentException("Invalid differentiator " + differentiator);
            }
            this.mOsId = osId;
            this.mAppId = appId;
            this.mDifferentiator = differentiator;
        }

        public OsAppId(byte[] rawOsAppId) {
            try {
                ByteBuffer bb = ByteBuffer.wrap(rawOsAppId);
                this.mOsId = new UUID(bb.getLong(), bb.getLong());
                int appIdLen = bb.get();
                byte[] appIdAndDifferentiator = new byte[appIdLen];
                bb.get(appIdAndDifferentiator, 0, appIdLen);
                String appIdAndDifferentiatorStr = new String(appIdAndDifferentiator);
                Pattern pattern = Pattern.compile("[^0-9]+([0-9]+)$");
                Matcher matcher = pattern.matcher(new String(appIdAndDifferentiator));
                if (matcher.find()) {
                    this.mDifferentiator = Integer.parseInt(matcher.group(1));
                    this.mAppId = appIdAndDifferentiatorStr.replace(matcher.group(1), "");
                    return;
                }
                this.mDifferentiator = 1;
                this.mAppId = appIdAndDifferentiatorStr;
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to decode " + (rawOsAppId != null ? new BigInteger(1, rawOsAppId).toString(16) : null));
            }
        }

        public UUID getOsId() {
            return this.mOsId;
        }

        public String getAppId() {
            return this.mAppId;
        }

        public int getDifferentiator() {
            return this.mDifferentiator;
        }

        public byte[] getBytes() {
            StringBuilder append = new StringBuilder().append(this.mAppId);
            int i = this.mDifferentiator;
            byte[] osAppId = append.append(i > 1 ? Integer.valueOf(i) : "").toString().getBytes();
            ByteBuffer bb = ByteBuffer.allocate(osAppId.length + 17);
            bb.putLong(this.mOsId.getMostSignificantBits());
            bb.putLong(this.mOsId.getLeastSignificantBits());
            bb.put((byte) osAppId.length);
            bb.put(osAppId);
            return bb.array();
        }

        public String toString() {
            return "[OsAppId: OS=" + this.mOsId + ", App=" + this.mAppId + ", differentiator=" + this.mDifferentiator + ", raw=" + new BigInteger(1, getBytes()).toString(16) + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OsAppId osAppId = (OsAppId) o;
            if (this.mDifferentiator == osAppId.mDifferentiator && this.mOsId.equals(osAppId.mOsId) && this.mAppId.equals(osAppId.mAppId)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mOsId, this.mAppId, Integer.valueOf(this.mDifferentiator));
        }
    }

    private TrafficDescriptor(Parcel in) {
        this.mDnn = in.readString();
        byte[] osAppIdBytes = in.createByteArray();
        OsAppId osAppId = osAppIdBytes != null ? new OsAppId(osAppIdBytes) : null;
        this.mOsAppId = osAppId;
        enforceAllowedIds();
    }

    public TrafficDescriptor(String dnn, byte[] osAppIdRawBytes) {
        this.mDnn = dnn;
        OsAppId osAppId = osAppIdRawBytes != null ? new OsAppId(osAppIdRawBytes) : null;
        this.mOsAppId = osAppId;
        enforceAllowedIds();
    }

    private void enforceAllowedIds() {
        OsAppId osAppId = this.mOsAppId;
        if (osAppId != null && !osAppId.getOsId().equals(OsAppId.ANDROID_OS_ID)) {
            throw new IllegalArgumentException("OS id " + this.mOsAppId.getOsId() + " does not match " + OsAppId.ANDROID_OS_ID);
        }
        if (this.mOsAppId != null && !OsAppId.ALLOWED_APP_IDS.contains(this.mOsAppId.getAppId())) {
            throw new IllegalArgumentException("Illegal app id " + this.mOsAppId.getAppId() + ". Only allowing one of the following " + OsAppId.ALLOWED_APP_IDS);
        }
    }

    public String getDataNetworkName() {
        return this.mDnn;
    }

    public byte[] getOsAppId() {
        OsAppId osAppId = this.mOsAppId;
        if (osAppId != null) {
            return osAppId.getBytes();
        }
        return null;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "TrafficDescriptor={mDnn=" + this.mDnn + ", " + this.mOsAppId + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDnn);
        OsAppId osAppId = this.mOsAppId;
        dest.writeByteArray(osAppId != null ? osAppId.getBytes() : null);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrafficDescriptor that = (TrafficDescriptor) o;
        if (Objects.equals(this.mDnn, that.mDnn) && Objects.equals(this.mOsAppId, that.mOsAppId)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mDnn, this.mOsAppId);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mDnn = null;
        private byte[] mOsAppId = null;

        public Builder setDataNetworkName(String dnn) {
            this.mDnn = dnn;
            return this;
        }

        public Builder setOsAppId(byte[] osAppId) {
            this.mOsAppId = osAppId;
            return this;
        }

        public TrafficDescriptor build() {
            if (this.mDnn == null && this.mOsAppId == null) {
                throw new IllegalArgumentException("DNN and OS App ID are null");
            }
            return new TrafficDescriptor(this.mDnn, this.mOsAppId);
        }
    }
}
