package android.net;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NativeNetworkConfig implements Parcelable {
    public static final Parcelable.Creator<NativeNetworkConfig> CREATOR = new Parcelable.Creator<NativeNetworkConfig>() { // from class: android.net.NativeNetworkConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NativeNetworkConfig createFromParcel(Parcel parcel) {
            return NativeNetworkConfig.internalCreateFromParcel(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NativeNetworkConfig[] newArray(int i) {
            return new NativeNetworkConfig[i];
        }
    };
    public final boolean excludeLocalRoutes;
    public final int netId;
    public final int networkType;
    public final int permission;
    public final boolean secure;
    public final int vpnType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int netId = 0;
        private int networkType = 0;
        private int permission = 0;
        private boolean secure = false;
        private int vpnType = 2;
        private boolean excludeLocalRoutes = false;

        public Builder setNetId(int i) {
            this.netId = i;
            return this;
        }

        public Builder setNetworkType(int i) {
            this.networkType = i;
            return this;
        }

        public Builder setPermission(int i) {
            this.permission = i;
            return this;
        }

        public Builder setSecure(boolean z) {
            this.secure = z;
            return this;
        }

        public Builder setVpnType(int i) {
            this.vpnType = i;
            return this;
        }

        public Builder setExcludeLocalRoutes(boolean z) {
            this.excludeLocalRoutes = z;
            return this;
        }

        public NativeNetworkConfig build() {
            return new NativeNetworkConfig(this.netId, this.networkType, this.permission, this.secure, this.vpnType, this.excludeLocalRoutes);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.netId);
        parcel.writeInt(this.networkType);
        parcel.writeInt(this.permission);
        parcel.writeBoolean(this.secure);
        parcel.writeInt(this.vpnType);
        parcel.writeBoolean(this.excludeLocalRoutes);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }

    public NativeNetworkConfig(int i, int i2, int i3, boolean z, int i4, boolean z2) {
        this.netId = i;
        this.networkType = i2;
        this.permission = i3;
        this.secure = z;
        this.vpnType = i4;
        this.excludeLocalRoutes = z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static NativeNetworkConfig internalCreateFromParcel(Parcel parcel) {
        Builder builder = new Builder();
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        try {
        } finally {
            if (dataPosition > Integer.MAX_VALUE - readInt) {
                BadParcelableException badParcelableException = new BadParcelableException("Overflow in the size of parcelable");
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        if (readInt >= 4) {
            builder.build();
            if (parcel.dataPosition() - dataPosition >= readInt) {
                builder.build();
                if (dataPosition > Integer.MAX_VALUE - readInt) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
            } else {
                builder.setNetId(parcel.readInt());
                if (parcel.dataPosition() - dataPosition >= readInt) {
                    builder.build();
                    int i = Integer.MAX_VALUE - readInt;
                    if (dataPosition > i) {
                        throw new BadParcelableException(r4);
                    }
                } else {
                    builder.setNetworkType(parcel.readInt());
                    if (parcel.dataPosition() - dataPosition >= readInt) {
                        builder.build();
                        if (dataPosition > Integer.MAX_VALUE - readInt) {
                            throw new BadParcelableException("Overflow in the size of parcelable");
                        }
                    } else {
                        builder.setPermission(parcel.readInt());
                        if (parcel.dataPosition() - dataPosition >= readInt) {
                            builder.build();
                            if (dataPosition > Integer.MAX_VALUE - readInt) {
                                throw new BadParcelableException("Overflow in the size of parcelable");
                            }
                        } else {
                            builder.setSecure(parcel.readBoolean());
                            if (parcel.dataPosition() - dataPosition >= readInt) {
                                builder.build();
                                if (dataPosition > Integer.MAX_VALUE - readInt) {
                                    throw new BadParcelableException("Overflow in the size of parcelable");
                                }
                            } else {
                                builder.setVpnType(parcel.readInt());
                                if (parcel.dataPosition() - dataPosition >= readInt) {
                                    builder.build();
                                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                                        throw new BadParcelableException("Overflow in the size of parcelable");
                                    }
                                } else {
                                    builder.setExcludeLocalRoutes(parcel.readBoolean());
                                    if (dataPosition > Integer.MAX_VALUE - readInt) {
                                        throw new BadParcelableException("Overflow in the size of parcelable");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            parcel.setDataPosition(dataPosition + readInt);
            return builder.build();
        }
        throw new BadParcelableException("Parcelable too small");
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        stringJoiner.add("netId: " + this.netId);
        stringJoiner.add("networkType: " + this.networkType);
        stringJoiner.add("permission: " + this.permission);
        stringJoiner.add("secure: " + this.secure);
        stringJoiner.add("vpnType: " + this.vpnType);
        stringJoiner.add("excludeLocalRoutes: " + this.excludeLocalRoutes);
        return "android.net.NativeNetworkConfig" + stringJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof NativeNetworkConfig)) {
            NativeNetworkConfig nativeNetworkConfig = (NativeNetworkConfig) obj;
            return Objects.deepEquals(Integer.valueOf(this.netId), Integer.valueOf(nativeNetworkConfig.netId)) && Objects.deepEquals(Integer.valueOf(this.networkType), Integer.valueOf(nativeNetworkConfig.networkType)) && Objects.deepEquals(Integer.valueOf(this.permission), Integer.valueOf(nativeNetworkConfig.permission)) && Objects.deepEquals(Boolean.valueOf(this.secure), Boolean.valueOf(nativeNetworkConfig.secure)) && Objects.deepEquals(Integer.valueOf(this.vpnType), Integer.valueOf(nativeNetworkConfig.vpnType)) && Objects.deepEquals(Boolean.valueOf(this.excludeLocalRoutes), Boolean.valueOf(nativeNetworkConfig.excludeLocalRoutes));
        }
        return false;
    }

    public int hashCode() {
        return Arrays.deepHashCode(Arrays.asList(Integer.valueOf(this.netId), Integer.valueOf(this.networkType), Integer.valueOf(this.permission), Boolean.valueOf(this.secure), Integer.valueOf(this.vpnType), Boolean.valueOf(this.excludeLocalRoutes)).toArray());
    }
}
