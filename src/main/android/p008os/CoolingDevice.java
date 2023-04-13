package android.p008os;

import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.os.CoolingDevice */
/* loaded from: classes3.dex */
public final class CoolingDevice implements Parcelable {
    public static final Parcelable.Creator<CoolingDevice> CREATOR = new Parcelable.Creator<CoolingDevice>() { // from class: android.os.CoolingDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CoolingDevice createFromParcel(Parcel p) {
            long value = p.readLong();
            int type = p.readInt();
            String name = p.readString();
            return new CoolingDevice(value, type, name);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CoolingDevice[] newArray(int size) {
            return new CoolingDevice[size];
        }
    };
    public static final int TYPE_BATTERY = 1;
    public static final int TYPE_COMPONENT = 6;
    public static final int TYPE_CPU = 2;
    public static final int TYPE_DISPLAY = 9;
    public static final int TYPE_FAN = 0;
    public static final int TYPE_GPU = 3;
    public static final int TYPE_MODEM = 4;
    public static final int TYPE_NPU = 5;
    public static final int TYPE_POWER_AMPLIFIER = 8;
    public static final int TYPE_SPEAKER = 10;
    public static final int TYPE_TPU = 7;
    private final String mName;
    private final int mType;
    private final long mValue;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.CoolingDevice$Type */
    /* loaded from: classes3.dex */
    public @interface Type {
    }

    public static boolean isValidType(int type) {
        return type >= 0 && type <= 10;
    }

    public CoolingDevice(long value, int type, String name) {
        Preconditions.checkArgument(isValidType(type), "Invalid Type");
        this.mValue = value;
        this.mType = type;
        this.mName = (String) Preconditions.checkStringNotEmpty(name);
    }

    public long getValue() {
        return this.mValue;
    }

    public int getType() {
        return this.mType;
    }

    public String getName() {
        return this.mName;
    }

    public String toString() {
        return "CoolingDevice{mValue=" + this.mValue + ", mType=" + this.mType + ", mName=" + this.mName + "}";
    }

    public int hashCode() {
        int hash = this.mName.hashCode();
        return (((hash * 31) + Long.hashCode(this.mValue)) * 31) + this.mType;
    }

    public boolean equals(Object o) {
        if (o instanceof CoolingDevice) {
            CoolingDevice other = (CoolingDevice) o;
            return other.mValue == this.mValue && other.mType == this.mType && other.mName.equals(this.mName);
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel p, int flags) {
        p.writeLong(this.mValue);
        p.writeInt(this.mType);
        p.writeString(this.mName);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
