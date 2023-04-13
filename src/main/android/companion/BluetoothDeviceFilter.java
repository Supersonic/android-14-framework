package android.companion;

import android.bluetooth.BluetoothDevice;
import android.p008os.Parcel;
import android.p008os.ParcelUuid;
import android.p008os.Parcelable;
import android.provider.OneTimeUseBuilder;
import android.text.format.DateFormat;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class BluetoothDeviceFilter implements DeviceFilter<BluetoothDevice> {
    public static final Parcelable.Creator<BluetoothDeviceFilter> CREATOR = new Parcelable.Creator<BluetoothDeviceFilter>() { // from class: android.companion.BluetoothDeviceFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothDeviceFilter createFromParcel(Parcel in) {
            return new BluetoothDeviceFilter(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothDeviceFilter[] newArray(int size) {
            return new BluetoothDeviceFilter[size];
        }
    };
    private final String mAddress;
    private final Pattern mNamePattern;
    private final List<ParcelUuid> mServiceUuidMasks;
    private final List<ParcelUuid> mServiceUuids;

    private BluetoothDeviceFilter(Pattern namePattern, String address, List<ParcelUuid> serviceUuids, List<ParcelUuid> serviceUuidMasks) {
        this.mNamePattern = namePattern;
        this.mAddress = address;
        this.mServiceUuids = CollectionUtils.emptyIfNull(serviceUuids);
        this.mServiceUuidMasks = CollectionUtils.emptyIfNull(serviceUuidMasks);
    }

    private BluetoothDeviceFilter(Parcel in) {
        this(BluetoothDeviceFilterUtils.patternFromString(in.readString()), in.readString(), readUuids(in), readUuids(in));
    }

    private static List<ParcelUuid> readUuids(Parcel in) {
        return in.readParcelableList(new ArrayList(), ParcelUuid.class.getClassLoader(), ParcelUuid.class);
    }

    @Override // android.companion.DeviceFilter
    public boolean matches(BluetoothDevice device) {
        return BluetoothDeviceFilterUtils.matchesAddress(this.mAddress, device) && BluetoothDeviceFilterUtils.matchesServiceUuids(this.mServiceUuids, this.mServiceUuidMasks, device) && BluetoothDeviceFilterUtils.matchesName(getNamePattern(), device);
    }

    @Override // android.companion.DeviceFilter
    public String getDeviceDisplayName(BluetoothDevice device) {
        return BluetoothDeviceFilterUtils.getDeviceDisplayNameInternal(device);
    }

    @Override // android.companion.DeviceFilter
    public int getMediumType() {
        return 0;
    }

    public Pattern getNamePattern() {
        return this.mNamePattern;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public List<ParcelUuid> getServiceUuids() {
        return this.mServiceUuids;
    }

    public List<ParcelUuid> getServiceUuidMasks() {
        return this.mServiceUuidMasks;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(BluetoothDeviceFilterUtils.patternToString(getNamePattern()));
        dest.writeString(this.mAddress);
        dest.writeParcelableList(this.mServiceUuids, flags);
        dest.writeParcelableList(this.mServiceUuidMasks, flags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BluetoothDeviceFilter that = (BluetoothDeviceFilter) o;
        if (Objects.equals(this.mNamePattern, that.mNamePattern) && Objects.equals(this.mAddress, that.mAddress) && Objects.equals(this.mServiceUuids, that.mServiceUuids) && Objects.equals(this.mServiceUuidMasks, that.mServiceUuidMasks)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mNamePattern, this.mAddress, this.mServiceUuids, this.mServiceUuidMasks);
    }

    public String toString() {
        return "BluetoothDeviceFilter{mNamePattern=" + this.mNamePattern + ", mAddress='" + this.mAddress + DateFormat.QUOTE + ", mServiceUuids=" + this.mServiceUuids + ", mServiceUuidMasks=" + this.mServiceUuidMasks + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public static final class Builder extends OneTimeUseBuilder<BluetoothDeviceFilter> {
        private String mAddress;
        private Pattern mNamePattern;
        private ArrayList<ParcelUuid> mServiceUuid;
        private ArrayList<ParcelUuid> mServiceUuidMask;

        public Builder setNamePattern(Pattern regex) {
            checkNotUsed();
            this.mNamePattern = regex;
            return this;
        }

        public Builder setAddress(String address) {
            checkNotUsed();
            this.mAddress = address;
            return this;
        }

        public Builder addServiceUuid(ParcelUuid serviceUuid, ParcelUuid serviceUuidMask) {
            checkNotUsed();
            this.mServiceUuid = ArrayUtils.add(this.mServiceUuid, serviceUuid);
            this.mServiceUuidMask = ArrayUtils.add(this.mServiceUuidMask, serviceUuidMask);
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.OneTimeUseBuilder
        public BluetoothDeviceFilter build() {
            markUsed();
            return new BluetoothDeviceFilter(this.mNamePattern, this.mAddress, this.mServiceUuid, this.mServiceUuidMask);
        }
    }
}
