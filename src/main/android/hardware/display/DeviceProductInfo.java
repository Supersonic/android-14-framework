package android.hardware.display;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DeviceProductInfo implements Parcelable {
    public static final int CONNECTION_TO_SINK_BUILT_IN = 1;
    public static final int CONNECTION_TO_SINK_DIRECT = 2;
    public static final int CONNECTION_TO_SINK_TRANSITIVE = 3;
    public static final int CONNECTION_TO_SINK_UNKNOWN = 0;
    public static final Parcelable.Creator<DeviceProductInfo> CREATOR = new Parcelable.Creator<DeviceProductInfo>() { // from class: android.hardware.display.DeviceProductInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceProductInfo createFromParcel(Parcel in) {
            return new DeviceProductInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceProductInfo[] newArray(int size) {
            return new DeviceProductInfo[size];
        }
    };
    private final int mConnectionToSinkType;
    private final ManufactureDate mManufactureDate;
    private final String mManufacturerPnpId;
    private final Integer mModelYear;
    private final String mName;
    private final String mProductId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ConnectionToSinkType {
    }

    public DeviceProductInfo(String name, String manufacturerPnpId, String productId, Integer modelYear, ManufactureDate manufactureDate, int connectionToSinkType) {
        this.mName = name;
        this.mManufacturerPnpId = manufacturerPnpId;
        this.mProductId = productId;
        this.mModelYear = modelYear;
        this.mManufactureDate = manufactureDate;
        this.mConnectionToSinkType = connectionToSinkType;
    }

    public DeviceProductInfo(String name, String manufacturerPnpId, String productId, int modelYear, int connectionToSinkType) {
        this.mName = name;
        this.mManufacturerPnpId = (String) Objects.requireNonNull(manufacturerPnpId);
        this.mProductId = (String) Objects.requireNonNull(productId);
        this.mModelYear = Integer.valueOf(modelYear);
        this.mManufactureDate = null;
        this.mConnectionToSinkType = connectionToSinkType;
    }

    private DeviceProductInfo(Parcel in) {
        this.mName = in.readString();
        this.mManufacturerPnpId = in.readString();
        this.mProductId = (String) in.readValue(null);
        this.mModelYear = (Integer) in.readValue(null);
        this.mManufactureDate = (ManufactureDate) in.readValue(null);
        this.mConnectionToSinkType = in.readInt();
    }

    public String getName() {
        return this.mName;
    }

    public String getManufacturerPnpId() {
        return this.mManufacturerPnpId;
    }

    public String getProductId() {
        return this.mProductId;
    }

    public int getModelYear() {
        Integer num = this.mModelYear;
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    public int getManufactureYear() {
        ManufactureDate manufactureDate = this.mManufactureDate;
        if (manufactureDate == null || manufactureDate.mYear == null) {
            return -1;
        }
        return this.mManufactureDate.mYear.intValue();
    }

    public int getManufactureWeek() {
        ManufactureDate manufactureDate = this.mManufactureDate;
        if (manufactureDate == null || manufactureDate.mWeek == null) {
            return -1;
        }
        return this.mManufactureDate.mWeek.intValue();
    }

    public ManufactureDate getManufactureDate() {
        return this.mManufactureDate;
    }

    public int getConnectionToSinkType() {
        return this.mConnectionToSinkType;
    }

    public String toString() {
        return "DeviceProductInfo{name=" + this.mName + ", manufacturerPnpId=" + this.mManufacturerPnpId + ", productId=" + this.mProductId + ", modelYear=" + this.mModelYear + ", manufactureDate=" + this.mManufactureDate + ", connectionToSinkType=" + this.mConnectionToSinkType + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceProductInfo that = (DeviceProductInfo) o;
        if (Objects.equals(this.mName, that.mName) && Objects.equals(this.mManufacturerPnpId, that.mManufacturerPnpId) && Objects.equals(this.mProductId, that.mProductId) && Objects.equals(this.mModelYear, that.mModelYear) && Objects.equals(this.mManufactureDate, that.mManufactureDate) && this.mConnectionToSinkType == that.mConnectionToSinkType) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mName, this.mManufacturerPnpId, this.mProductId, this.mModelYear, this.mManufactureDate, Integer.valueOf(this.mConnectionToSinkType));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mManufacturerPnpId);
        dest.writeValue(this.mProductId);
        dest.writeValue(this.mModelYear);
        dest.writeValue(this.mManufactureDate);
        dest.writeInt(this.mConnectionToSinkType);
    }

    /* loaded from: classes.dex */
    public static class ManufactureDate implements Parcelable {
        public static final Parcelable.Creator<ManufactureDate> CREATOR = new Parcelable.Creator<ManufactureDate>() { // from class: android.hardware.display.DeviceProductInfo.ManufactureDate.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ManufactureDate createFromParcel(Parcel in) {
                return new ManufactureDate(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ManufactureDate[] newArray(int size) {
                return new ManufactureDate[size];
            }
        };
        private final Integer mWeek;
        private final Integer mYear;

        public ManufactureDate(Integer week, Integer year) {
            this.mWeek = week;
            this.mYear = year;
        }

        protected ManufactureDate(Parcel in) {
            this.mWeek = (Integer) in.readValue(null);
            this.mYear = (Integer) in.readValue(null);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.mWeek);
            dest.writeValue(this.mYear);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Integer getYear() {
            return this.mYear;
        }

        public Integer getWeek() {
            return this.mWeek;
        }

        public String toString() {
            return "ManufactureDate{week=" + this.mWeek + ", year=" + this.mYear + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ManufactureDate that = (ManufactureDate) o;
            if (Objects.equals(this.mWeek, that.mWeek) && Objects.equals(this.mYear, that.mYear)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mWeek, this.mYear);
        }
    }
}
