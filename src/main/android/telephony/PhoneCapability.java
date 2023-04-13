package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class PhoneCapability implements Parcelable {
    public static final Parcelable.Creator<PhoneCapability> CREATOR;
    public static final PhoneCapability DEFAULT_DSDS_CAPABILITY;
    public static final PhoneCapability DEFAULT_SSSS_CAPABILITY;
    @SystemApi
    public static final int DEVICE_NR_CAPABILITY_NSA = 1;
    @SystemApi
    public static final int DEVICE_NR_CAPABILITY_SA = 2;
    private final int[] mDeviceNrCapabilities;
    private final List<ModemInfo> mLogicalModemList;
    private final int mMaxActiveDataSubscriptions;
    private final int mMaxActiveVoiceSubscriptions;
    private final boolean mNetworkValidationBeforeSwitchSupported;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DeviceNrCapability {
    }

    static {
        ModemInfo modemInfo1 = new ModemInfo(0, 0, true, true);
        ModemInfo modemInfo2 = new ModemInfo(1, 0, true, true);
        List<ModemInfo> logicalModemList = new ArrayList<>();
        logicalModemList.add(modemInfo1);
        logicalModemList.add(modemInfo2);
        int[] deviceNrCapabilities = new int[0];
        DEFAULT_DSDS_CAPABILITY = new PhoneCapability(1, 1, logicalModemList, false, deviceNrCapabilities);
        List<ModemInfo> logicalModemList2 = new ArrayList<>();
        logicalModemList2.add(modemInfo1);
        DEFAULT_SSSS_CAPABILITY = new PhoneCapability(1, 1, logicalModemList2, false, deviceNrCapabilities);
        CREATOR = new Parcelable.Creator() { // from class: android.telephony.PhoneCapability.1
            @Override // android.p008os.Parcelable.Creator
            public PhoneCapability createFromParcel(Parcel in) {
                return new PhoneCapability(in);
            }

            @Override // android.p008os.Parcelable.Creator
            public PhoneCapability[] newArray(int size) {
                return new PhoneCapability[size];
            }
        };
    }

    public PhoneCapability(int maxActiveVoiceSubscriptions, int maxActiveDataSubscriptions, List<ModemInfo> logicalModemList, boolean networkValidationBeforeSwitchSupported, int[] deviceNrCapabilities) {
        this.mMaxActiveVoiceSubscriptions = maxActiveVoiceSubscriptions;
        this.mMaxActiveDataSubscriptions = maxActiveDataSubscriptions;
        this.mLogicalModemList = logicalModemList == null ? new ArrayList<>() : logicalModemList;
        this.mNetworkValidationBeforeSwitchSupported = networkValidationBeforeSwitchSupported;
        this.mDeviceNrCapabilities = deviceNrCapabilities;
    }

    private PhoneCapability(Builder builder) {
        this.mMaxActiveVoiceSubscriptions = builder.mMaxActiveVoiceSubscriptions;
        this.mMaxActiveDataSubscriptions = builder.mMaxActiveDataSubscriptions;
        this.mLogicalModemList = builder.mLogicalModemList == null ? new ArrayList<>() : builder.mLogicalModemList;
        this.mNetworkValidationBeforeSwitchSupported = builder.mNetworkValidationBeforeSwitchSupported;
        this.mDeviceNrCapabilities = builder.mDeviceNrCapabilities;
    }

    public String toString() {
        return "mMaxActiveVoiceSubscriptions=" + this.mMaxActiveVoiceSubscriptions + " mMaxActiveDataSubscriptions=" + this.mMaxActiveDataSubscriptions + " mNetworkValidationBeforeSwitchSupported=" + this.mNetworkValidationBeforeSwitchSupported + " mDeviceNrCapability " + Arrays.toString(this.mDeviceNrCapabilities);
    }

    private PhoneCapability(Parcel in) {
        this.mMaxActiveVoiceSubscriptions = in.readInt();
        this.mMaxActiveDataSubscriptions = in.readInt();
        this.mNetworkValidationBeforeSwitchSupported = in.readBoolean();
        ArrayList arrayList = new ArrayList();
        this.mLogicalModemList = arrayList;
        in.readList(arrayList, ModemInfo.class.getClassLoader(), ModemInfo.class);
        this.mDeviceNrCapabilities = in.createIntArray();
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mMaxActiveVoiceSubscriptions), Integer.valueOf(this.mMaxActiveDataSubscriptions), this.mLogicalModemList, Boolean.valueOf(this.mNetworkValidationBeforeSwitchSupported), Integer.valueOf(Arrays.hashCode(this.mDeviceNrCapabilities)));
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof PhoneCapability) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            PhoneCapability s = (PhoneCapability) o;
            if (this.mMaxActiveVoiceSubscriptions != s.mMaxActiveVoiceSubscriptions || this.mMaxActiveDataSubscriptions != s.mMaxActiveDataSubscriptions || this.mNetworkValidationBeforeSwitchSupported != s.mNetworkValidationBeforeSwitchSupported || !this.mLogicalModemList.equals(s.mLogicalModemList) || !Arrays.equals(this.mDeviceNrCapabilities, s.mDeviceNrCapabilities)) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMaxActiveVoiceSubscriptions);
        dest.writeInt(this.mMaxActiveDataSubscriptions);
        dest.writeBoolean(this.mNetworkValidationBeforeSwitchSupported);
        dest.writeList(this.mLogicalModemList);
        dest.writeIntArray(this.mDeviceNrCapabilities);
    }

    @SystemApi
    public int getMaxActiveVoiceSubscriptions() {
        return this.mMaxActiveVoiceSubscriptions;
    }

    @SystemApi
    public int getMaxActiveDataSubscriptions() {
        return this.mMaxActiveDataSubscriptions;
    }

    public boolean isNetworkValidationBeforeSwitchSupported() {
        return this.mNetworkValidationBeforeSwitchSupported;
    }

    public List<ModemInfo> getLogicalModemList() {
        return this.mLogicalModemList;
    }

    @SystemApi
    public int[] getDeviceNrCapabilities() {
        int[] iArr = this.mDeviceNrCapabilities;
        return iArr == null ? new int[0] : iArr;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private int[] mDeviceNrCapabilities;
        private List<ModemInfo> mLogicalModemList;
        private int mMaxActiveDataSubscriptions;
        private int mMaxActiveVoiceSubscriptions;
        private boolean mNetworkValidationBeforeSwitchSupported;

        public Builder() {
            this.mMaxActiveVoiceSubscriptions = 0;
            this.mMaxActiveDataSubscriptions = 0;
            this.mNetworkValidationBeforeSwitchSupported = false;
            this.mLogicalModemList = new ArrayList();
            this.mDeviceNrCapabilities = new int[0];
        }

        public Builder(PhoneCapability phoneCapability) {
            this.mMaxActiveVoiceSubscriptions = 0;
            this.mMaxActiveDataSubscriptions = 0;
            this.mNetworkValidationBeforeSwitchSupported = false;
            this.mLogicalModemList = new ArrayList();
            this.mDeviceNrCapabilities = new int[0];
            this.mMaxActiveVoiceSubscriptions = phoneCapability.mMaxActiveVoiceSubscriptions;
            this.mMaxActiveDataSubscriptions = phoneCapability.mMaxActiveDataSubscriptions;
            this.mNetworkValidationBeforeSwitchSupported = phoneCapability.mNetworkValidationBeforeSwitchSupported;
            this.mLogicalModemList = phoneCapability.mLogicalModemList;
            this.mDeviceNrCapabilities = phoneCapability.mDeviceNrCapabilities;
        }

        public Builder setMaxActiveVoiceSubscriptions(int maxActiveVoiceSubscriptions) {
            this.mMaxActiveVoiceSubscriptions = maxActiveVoiceSubscriptions;
            return this;
        }

        public Builder setMaxActiveDataSubscriptions(int maxActiveDataSubscriptions) {
            this.mMaxActiveDataSubscriptions = maxActiveDataSubscriptions;
            return this;
        }

        public Builder setNetworkValidationBeforeSwitchSupported(boolean networkValidationBeforeSwitchSupported) {
            this.mNetworkValidationBeforeSwitchSupported = networkValidationBeforeSwitchSupported;
            return this;
        }

        public Builder setLogicalModemList(List<ModemInfo> logicalModemList) {
            this.mLogicalModemList = logicalModemList;
            return this;
        }

        public Builder setDeviceNrCapabilities(int[] deviceNrCapabilities) {
            this.mDeviceNrCapabilities = deviceNrCapabilities;
            return this;
        }

        public PhoneCapability build() {
            return new PhoneCapability(this);
        }
    }
}
