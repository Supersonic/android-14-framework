package android.companion;

import android.annotation.NonNull;
import android.net.MacAddress;
import android.net.wifi.ScanResult;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
import java.util.Objects;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class WifiDeviceFilter implements DeviceFilter<ScanResult> {
    public static final Parcelable.Creator<WifiDeviceFilter> CREATOR;
    static Parcelling<Pattern> sParcellingForNamePattern;
    private final MacAddress mBssid;
    private final MacAddress mBssidMask;
    private final Pattern mNamePattern;

    @Override // android.companion.DeviceFilter
    public boolean matches(ScanResult device) {
        return BluetoothDeviceFilterUtils.matchesName(getNamePattern(), device) && (this.mBssid == null || MacAddress.fromString(device.BSSID).matches(this.mBssid, this.mBssidMask));
    }

    @Override // android.companion.DeviceFilter
    public String getDeviceDisplayName(ScanResult device) {
        return BluetoothDeviceFilterUtils.getDeviceDisplayNameInternal(device);
    }

    @Override // android.companion.DeviceFilter
    public int getMediumType() {
        return 2;
    }

    WifiDeviceFilter(Pattern namePattern, MacAddress bssid, MacAddress bssidMask) {
        this.mNamePattern = namePattern;
        this.mBssid = bssid;
        this.mBssidMask = bssidMask;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) bssidMask);
    }

    public Pattern getNamePattern() {
        return this.mNamePattern;
    }

    public MacAddress getBssid() {
        return this.mBssid;
    }

    public MacAddress getBssidMask() {
        return this.mBssidMask;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WifiDeviceFilter that = (WifiDeviceFilter) o;
        if (Objects.equals(this.mNamePattern, that.mNamePattern) && Objects.equals(this.mBssid, that.mBssid) && Objects.equals(this.mBssidMask, that.mBssidMask)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mNamePattern);
        return (((_hash * 31) + Objects.hashCode(this.mBssid)) * 31) + Objects.hashCode(this.mBssidMask);
    }

    static {
        Parcelling<Pattern> parcelling = Parcelling.Cache.get(Parcelling.BuiltIn.ForPattern.class);
        sParcellingForNamePattern = parcelling;
        if (parcelling == null) {
            sParcellingForNamePattern = Parcelling.Cache.put(new Parcelling.BuiltIn.ForPattern());
        }
        CREATOR = new Parcelable.Creator<WifiDeviceFilter>() { // from class: android.companion.WifiDeviceFilter.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WifiDeviceFilter[] newArray(int size) {
                return new WifiDeviceFilter[size];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public WifiDeviceFilter createFromParcel(Parcel in) {
                return new WifiDeviceFilter(in);
            }
        };
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mNamePattern != null ? (byte) (0 | 1) : (byte) 0;
        if (this.mBssid != null) {
            flg = (byte) (flg | 2);
        }
        dest.writeByte(flg);
        sParcellingForNamePattern.parcel(this.mNamePattern, dest, flags);
        MacAddress macAddress = this.mBssid;
        if (macAddress != null) {
            dest.writeTypedObject(macAddress, flags);
        }
        dest.writeTypedObject(this.mBssidMask, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    WifiDeviceFilter(Parcel in) {
        byte flg = in.readByte();
        Pattern namePattern = sParcellingForNamePattern.unparcel(in);
        MacAddress bssid = (flg & 2) == 0 ? null : (MacAddress) in.readTypedObject(MacAddress.CREATOR);
        MacAddress bssidMask = (MacAddress) in.readTypedObject(MacAddress.CREATOR);
        this.mNamePattern = namePattern;
        this.mBssid = bssid;
        this.mBssidMask = bssidMask;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) bssidMask);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private MacAddress mBssid;
        private MacAddress mBssidMask;
        private long mBuilderFieldsSet = 0;
        private Pattern mNamePattern;

        public Builder setNamePattern(Pattern value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mNamePattern = value;
            return this;
        }

        public Builder setBssid(MacAddress value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mBssid = value;
            return this;
        }

        public Builder setBssidMask(MacAddress value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mBssidMask = value;
            return this;
        }

        public WifiDeviceFilter build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 8;
            this.mBuilderFieldsSet = j;
            if ((1 & j) == 0) {
                this.mNamePattern = null;
            }
            if ((2 & j) == 0) {
                this.mBssid = null;
            }
            if ((j & 4) == 0) {
                this.mBssidMask = MacAddress.BROADCAST_ADDRESS;
            }
            return new WifiDeviceFilter(this.mNamePattern, this.mBssid, this.mBssidMask);
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 8) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
