package android.hardware.radio;

import android.annotation.SystemApi;
import android.app.admin.PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2;
import android.content.Context;
import android.hardware.radio.Announcement;
import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.IRadioService;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioTuner;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Handler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
@SystemApi
/* loaded from: classes2.dex */
public class RadioManager {
    public static final int BAND_AM = 0;
    public static final int BAND_AM_HD = 3;
    public static final int BAND_FM = 1;
    public static final int BAND_FM_HD = 2;
    public static final int BAND_INVALID = -1;
    public static final int CLASS_AM_FM = 0;
    public static final int CLASS_DT = 2;
    public static final int CLASS_SAT = 1;
    public static final int CONFIG_DAB_DAB_LINKING = 6;
    public static final int CONFIG_DAB_DAB_SOFT_LINKING = 8;
    public static final int CONFIG_DAB_FM_LINKING = 7;
    public static final int CONFIG_DAB_FM_SOFT_LINKING = 9;
    public static final int CONFIG_FORCE_ANALOG = 2;
    public static final int CONFIG_FORCE_DIGITAL = 3;
    public static final int CONFIG_FORCE_MONO = 1;
    public static final int CONFIG_RDS_AF = 4;
    public static final int CONFIG_RDS_REG = 5;
    public static final int REGION_ITU_1 = 0;
    public static final int REGION_ITU_2 = 1;
    public static final int REGION_JAPAN = 3;
    public static final int REGION_KOREA = 4;
    public static final int REGION_OIRT = 2;
    public static final int STATUS_BAD_VALUE = -22;
    public static final int STATUS_DEAD_OBJECT = -32;
    public static final int STATUS_ERROR = Integer.MIN_VALUE;
    public static final int STATUS_INVALID_OPERATION = -38;
    public static final int STATUS_NO_INIT = -19;
    public static final int STATUS_OK = 0;
    public static final int STATUS_PERMISSION_DENIED = -1;
    public static final int STATUS_TIMED_OUT = -110;
    private static final String TAG = "BroadcastRadio.manager";
    private final Map<Announcement.OnListUpdatedListener, ICloseHandle> mAnnouncementListeners;
    private final Context mContext;
    private final IRadioService mService;
    private final int mTargetSdkVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Band {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ConfigFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RadioStatusType {
    }

    private native int nativeListModules(List<ModuleProperties> list);

    /* loaded from: classes2.dex */
    public static class ModuleProperties implements Parcelable {
        public static final Parcelable.Creator<ModuleProperties> CREATOR = new Parcelable.Creator<ModuleProperties>() { // from class: android.hardware.radio.RadioManager.ModuleProperties.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModuleProperties createFromParcel(Parcel in) {
                return new ModuleProperties(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ModuleProperties[] newArray(int size) {
                return new ModuleProperties[size];
            }
        };
        private final BandDescriptor[] mBands;
        private final int mClassId;
        private final Map<String, Integer> mDabFrequencyTable;
        private final int mId;
        private final String mImplementor;
        private final boolean mIsBgScanSupported;
        private final boolean mIsCaptureSupported;
        private final boolean mIsInitializationRequired;
        private final int mNumAudioSources;
        private final int mNumTuners;
        private final String mProduct;
        private final String mSerial;
        private final String mServiceName;
        private final Set<Integer> mSupportedIdentifierTypes;
        private final Set<Integer> mSupportedProgramTypes;
        private final Map<String, String> mVendorInfo;
        private final String mVersion;

        public ModuleProperties(int id, String serviceName, int classId, String implementor, String product, String version, String serial, int numTuners, int numAudioSources, boolean isInitializationRequired, boolean isCaptureSupported, BandDescriptor[] bands, boolean isBgScanSupported, int[] supportedProgramTypes, int[] supportedIdentifierTypes, Map<String, Integer> dabFrequencyTable, Map<String, String> vendorInfo) {
            this.mId = id;
            this.mServiceName = TextUtils.isEmpty(serviceName) ? "default" : serviceName;
            this.mClassId = classId;
            this.mImplementor = implementor;
            this.mProduct = product;
            this.mVersion = version;
            this.mSerial = serial;
            this.mNumTuners = numTuners;
            this.mNumAudioSources = numAudioSources;
            this.mIsInitializationRequired = isInitializationRequired;
            this.mIsCaptureSupported = isCaptureSupported;
            this.mBands = bands;
            this.mIsBgScanSupported = isBgScanSupported;
            this.mSupportedProgramTypes = arrayToSet(supportedProgramTypes);
            this.mSupportedIdentifierTypes = arrayToSet(supportedIdentifierTypes);
            if (dabFrequencyTable != null) {
                for (Map.Entry<String, Integer> entry : dabFrequencyTable.entrySet()) {
                    Objects.requireNonNull(entry.getKey());
                    Objects.requireNonNull(entry.getValue());
                }
            }
            this.mDabFrequencyTable = (dabFrequencyTable == null || dabFrequencyTable.isEmpty()) ? null : dabFrequencyTable;
            this.mVendorInfo = vendorInfo == null ? new HashMap<>() : vendorInfo;
        }

        private static Set<Integer> arrayToSet(int[] arr) {
            return (Set) Arrays.stream(arr).boxed().collect(Collectors.toSet());
        }

        private static int[] setToArray(Set<Integer> set) {
            return set.stream().mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).toArray();
        }

        public int getId() {
            return this.mId;
        }

        public String getServiceName() {
            return this.mServiceName;
        }

        public int getClassId() {
            return this.mClassId;
        }

        public String getImplementor() {
            return this.mImplementor;
        }

        public String getProduct() {
            return this.mProduct;
        }

        public String getVersion() {
            return this.mVersion;
        }

        public String getSerial() {
            return this.mSerial;
        }

        public int getNumTuners() {
            return this.mNumTuners;
        }

        public int getNumAudioSources() {
            return this.mNumAudioSources;
        }

        public boolean isInitializationRequired() {
            return this.mIsInitializationRequired;
        }

        public boolean isCaptureSupported() {
            return this.mIsCaptureSupported;
        }

        public boolean isBackgroundScanningSupported() {
            return this.mIsBgScanSupported;
        }

        public boolean isProgramTypeSupported(int type) {
            return this.mSupportedProgramTypes.contains(Integer.valueOf(type));
        }

        public boolean isProgramIdentifierSupported(int type) {
            return this.mSupportedIdentifierTypes.contains(Integer.valueOf(type));
        }

        public Map<String, Integer> getDabFrequencyTable() {
            return this.mDabFrequencyTable;
        }

        public Map<String, String> getVendorInfo() {
            return this.mVendorInfo;
        }

        public BandDescriptor[] getBands() {
            return this.mBands;
        }

        private ModuleProperties(Parcel in) {
            this.mId = in.readInt();
            String serviceName = in.readString();
            this.mServiceName = TextUtils.isEmpty(serviceName) ? "default" : serviceName;
            this.mClassId = in.readInt();
            this.mImplementor = in.readString();
            this.mProduct = in.readString();
            this.mVersion = in.readString();
            this.mSerial = in.readString();
            this.mNumTuners = in.readInt();
            this.mNumAudioSources = in.readInt();
            this.mIsInitializationRequired = in.readInt() == 1;
            this.mIsCaptureSupported = in.readInt() == 1;
            Parcelable[] tmp = (Parcelable[]) in.readParcelableArray(BandDescriptor.class.getClassLoader(), BandDescriptor.class);
            this.mBands = new BandDescriptor[tmp.length];
            for (int i = 0; i < tmp.length; i++) {
                this.mBands[i] = (BandDescriptor) tmp[i];
            }
            int i2 = in.readInt();
            this.mIsBgScanSupported = i2 == 1;
            this.mSupportedProgramTypes = arrayToSet(in.createIntArray());
            this.mSupportedIdentifierTypes = arrayToSet(in.createIntArray());
            Map<String, Integer> dabFrequencyTableIn = Utils.readStringIntMap(in);
            this.mDabFrequencyTable = dabFrequencyTableIn.isEmpty() ? null : dabFrequencyTableIn;
            this.mVendorInfo = Utils.readStringMap(in);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mId);
            dest.writeString(this.mServiceName);
            dest.writeInt(this.mClassId);
            dest.writeString(this.mImplementor);
            dest.writeString(this.mProduct);
            dest.writeString(this.mVersion);
            dest.writeString(this.mSerial);
            dest.writeInt(this.mNumTuners);
            dest.writeInt(this.mNumAudioSources);
            dest.writeInt(this.mIsInitializationRequired ? 1 : 0);
            dest.writeInt(this.mIsCaptureSupported ? 1 : 0);
            dest.writeParcelableArray(this.mBands, flags);
            dest.writeInt(this.mIsBgScanSupported ? 1 : 0);
            dest.writeIntArray(setToArray(this.mSupportedProgramTypes));
            dest.writeIntArray(setToArray(this.mSupportedIdentifierTypes));
            Utils.writeStringIntMap(dest, this.mDabFrequencyTable);
            Utils.writeStringMap(dest, this.mVendorInfo);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "ModuleProperties [mId=" + this.mId + ", mServiceName=" + this.mServiceName + ", mClassId=" + this.mClassId + ", mImplementor=" + this.mImplementor + ", mProduct=" + this.mProduct + ", mVersion=" + this.mVersion + ", mSerial=" + this.mSerial + ", mNumTuners=" + this.mNumTuners + ", mNumAudioSources=" + this.mNumAudioSources + ", mIsInitializationRequired=" + this.mIsInitializationRequired + ", mIsCaptureSupported=" + this.mIsCaptureSupported + ", mIsBgScanSupported=" + this.mIsBgScanSupported + ", mBands=" + Arrays.toString(this.mBands) + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mId), this.mServiceName, Integer.valueOf(this.mClassId), this.mImplementor, this.mProduct, this.mVersion, this.mSerial, Integer.valueOf(this.mNumTuners), Integer.valueOf(this.mNumAudioSources), Boolean.valueOf(this.mIsInitializationRequired), Boolean.valueOf(this.mIsCaptureSupported), Integer.valueOf(Arrays.hashCode(this.mBands)), Boolean.valueOf(this.mIsBgScanSupported), this.mDabFrequencyTable, this.mVendorInfo);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ModuleProperties) {
                ModuleProperties other = (ModuleProperties) obj;
                return this.mId == other.getId() && TextUtils.equals(this.mServiceName, other.mServiceName) && this.mClassId == other.mClassId && Objects.equals(this.mImplementor, other.mImplementor) && Objects.equals(this.mProduct, other.mProduct) && Objects.equals(this.mVersion, other.mVersion) && Objects.equals(this.mSerial, other.mSerial) && this.mNumTuners == other.mNumTuners && this.mNumAudioSources == other.mNumAudioSources && this.mIsInitializationRequired == other.mIsInitializationRequired && this.mIsCaptureSupported == other.mIsCaptureSupported && Arrays.equals(this.mBands, other.mBands) && this.mIsBgScanSupported == other.mIsBgScanSupported && Objects.equals(this.mDabFrequencyTable, other.mDabFrequencyTable) && Objects.equals(this.mVendorInfo, other.mVendorInfo);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class BandDescriptor implements Parcelable {
        public static final Parcelable.Creator<BandDescriptor> CREATOR = new Parcelable.Creator<BandDescriptor>() { // from class: android.hardware.radio.RadioManager.BandDescriptor.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BandDescriptor createFromParcel(Parcel in) {
                int type = BandDescriptor.lookupTypeFromParcel(in);
                switch (type) {
                    case 0:
                    case 3:
                        return new AmBandDescriptor(in);
                    case 1:
                    case 2:
                        return new FmBandDescriptor(in);
                    default:
                        throw new IllegalArgumentException("Unsupported band: " + type);
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BandDescriptor[] newArray(int size) {
                return new BandDescriptor[size];
            }
        };
        private final int mLowerLimit;
        private final int mRegion;
        private final int mSpacing;
        private final int mType;
        private final int mUpperLimit;

        BandDescriptor(int region, int type, int lowerLimit, int upperLimit, int spacing) {
            if (type != 0 && type != 1 && type != 2 && type != 3) {
                throw new IllegalArgumentException("Unsupported band: " + type);
            }
            this.mRegion = region;
            this.mType = type;
            this.mLowerLimit = lowerLimit;
            this.mUpperLimit = upperLimit;
            this.mSpacing = spacing;
        }

        public int getRegion() {
            return this.mRegion;
        }

        public int getType() {
            return this.mType;
        }

        public boolean isAmBand() {
            int i = this.mType;
            return i == 0 || i == 3;
        }

        public boolean isFmBand() {
            int i = this.mType;
            return i == 1 || i == 2;
        }

        public int getLowerLimit() {
            return this.mLowerLimit;
        }

        public int getUpperLimit() {
            return this.mUpperLimit;
        }

        public int getSpacing() {
            return this.mSpacing;
        }

        private BandDescriptor(Parcel in) {
            this.mRegion = in.readInt();
            this.mType = in.readInt();
            this.mLowerLimit = in.readInt();
            this.mUpperLimit = in.readInt();
            this.mSpacing = in.readInt();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int lookupTypeFromParcel(Parcel in) {
            int pos = in.dataPosition();
            in.readInt();
            int type = in.readInt();
            in.setDataPosition(pos);
            return type;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mRegion);
            dest.writeInt(this.mType);
            dest.writeInt(this.mLowerLimit);
            dest.writeInt(this.mUpperLimit);
            dest.writeInt(this.mSpacing);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "BandDescriptor [mRegion=" + this.mRegion + ", mType=" + this.mType + ", mLowerLimit=" + this.mLowerLimit + ", mUpperLimit=" + this.mUpperLimit + ", mSpacing=" + this.mSpacing + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public int hashCode() {
            int result = (1 * 31) + this.mRegion;
            return (((((((result * 31) + this.mType) * 31) + this.mLowerLimit) * 31) + this.mUpperLimit) * 31) + this.mSpacing;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BandDescriptor) {
                BandDescriptor other = (BandDescriptor) obj;
                return this.mRegion == other.getRegion() && this.mType == other.getType() && this.mLowerLimit == other.getLowerLimit() && this.mUpperLimit == other.getUpperLimit() && this.mSpacing == other.getSpacing();
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class FmBandDescriptor extends BandDescriptor {
        public static final Parcelable.Creator<FmBandDescriptor> CREATOR = new Parcelable.Creator<FmBandDescriptor>() { // from class: android.hardware.radio.RadioManager.FmBandDescriptor.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public FmBandDescriptor createFromParcel(Parcel in) {
                return new FmBandDescriptor(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public FmBandDescriptor[] newArray(int size) {
                return new FmBandDescriptor[size];
            }
        };
        private final boolean mAf;
        private final boolean mEa;
        private final boolean mRds;
        private final boolean mStereo;
        private final boolean mTa;

        public FmBandDescriptor(int region, int type, int lowerLimit, int upperLimit, int spacing, boolean stereo, boolean rds, boolean ta, boolean af, boolean ea) {
            super(region, type, lowerLimit, upperLimit, spacing);
            this.mStereo = stereo;
            this.mRds = rds;
            this.mTa = ta;
            this.mAf = af;
            this.mEa = ea;
        }

        public boolean isStereoSupported() {
            return this.mStereo;
        }

        public boolean isRdsSupported() {
            return this.mRds;
        }

        public boolean isTaSupported() {
            return this.mTa;
        }

        public boolean isAfSupported() {
            return this.mAf;
        }

        public boolean isEaSupported() {
            return this.mEa;
        }

        private FmBandDescriptor(Parcel in) {
            super(in);
            this.mStereo = in.readByte() == 1;
            this.mRds = in.readByte() == 1;
            this.mTa = in.readByte() == 1;
            this.mAf = in.readByte() == 1;
            this.mEa = in.readByte() == 1;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(this.mStereo ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mRds ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mTa ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mAf ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mEa ? (byte) 1 : (byte) 0);
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public String toString() {
            return "FmBandDescriptor [ " + super.toString() + " mStereo=" + this.mStereo + ", mRds=" + this.mRds + ", mTa=" + this.mTa + ", mAf=" + this.mAf + ", mEa =" + this.mEa + NavigationBarInflaterView.SIZE_MOD_END;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public int hashCode() {
            int result = super.hashCode();
            return (((((((((result * 31) + (this.mStereo ? 1 : 0)) * 31) + (this.mRds ? 1 : 0)) * 31) + (this.mTa ? 1 : 0)) * 31) + (this.mAf ? 1 : 0)) * 31) + (this.mEa ? 1 : 0);
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && (obj instanceof FmBandDescriptor)) {
                FmBandDescriptor other = (FmBandDescriptor) obj;
                return this.mStereo == other.isStereoSupported() && this.mRds == other.isRdsSupported() && this.mTa == other.isTaSupported() && this.mAf == other.isAfSupported() && this.mEa == other.isEaSupported();
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class AmBandDescriptor extends BandDescriptor {
        public static final Parcelable.Creator<AmBandDescriptor> CREATOR = new Parcelable.Creator<AmBandDescriptor>() { // from class: android.hardware.radio.RadioManager.AmBandDescriptor.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AmBandDescriptor createFromParcel(Parcel in) {
                return new AmBandDescriptor(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AmBandDescriptor[] newArray(int size) {
                return new AmBandDescriptor[size];
            }
        };
        private final boolean mStereo;

        public AmBandDescriptor(int region, int type, int lowerLimit, int upperLimit, int spacing, boolean stereo) {
            super(region, type, lowerLimit, upperLimit, spacing);
            this.mStereo = stereo;
        }

        public boolean isStereoSupported() {
            return this.mStereo;
        }

        private AmBandDescriptor(Parcel in) {
            super(in);
            this.mStereo = in.readByte() == 1;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(this.mStereo ? (byte) 1 : (byte) 0);
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public String toString() {
            return "AmBandDescriptor [ " + super.toString() + " mStereo=" + this.mStereo + NavigationBarInflaterView.SIZE_MOD_END;
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public int hashCode() {
            int result = super.hashCode();
            return (result * 31) + (this.mStereo ? 1 : 0);
        }

        @Override // android.hardware.radio.RadioManager.BandDescriptor
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && (obj instanceof AmBandDescriptor)) {
                AmBandDescriptor other = (AmBandDescriptor) obj;
                return this.mStereo == other.isStereoSupported();
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class BandConfig implements Parcelable {
        public static final Parcelable.Creator<BandConfig> CREATOR = new Parcelable.Creator<BandConfig>() { // from class: android.hardware.radio.RadioManager.BandConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BandConfig createFromParcel(Parcel in) {
                int type = BandDescriptor.lookupTypeFromParcel(in);
                switch (type) {
                    case 0:
                    case 3:
                        return new AmBandConfig(in);
                    case 1:
                    case 2:
                        return new FmBandConfig(in);
                    default:
                        throw new IllegalArgumentException("Unsupported band: " + type);
                }
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BandConfig[] newArray(int size) {
                return new BandConfig[size];
            }
        };
        final BandDescriptor mDescriptor;

        BandConfig(BandDescriptor descriptor) {
            Objects.requireNonNull(descriptor, "Descriptor cannot be null");
            this.mDescriptor = new BandDescriptor(descriptor.getRegion(), descriptor.getType(), descriptor.getLowerLimit(), descriptor.getUpperLimit(), descriptor.getSpacing());
        }

        BandConfig(int region, int type, int lowerLimit, int upperLimit, int spacing) {
            this.mDescriptor = new BandDescriptor(region, type, lowerLimit, upperLimit, spacing);
        }

        private BandConfig(Parcel in) {
            this.mDescriptor = new BandDescriptor(in);
        }

        BandDescriptor getDescriptor() {
            return this.mDescriptor;
        }

        public int getRegion() {
            return this.mDescriptor.getRegion();
        }

        public int getType() {
            return this.mDescriptor.getType();
        }

        public int getLowerLimit() {
            return this.mDescriptor.getLowerLimit();
        }

        public int getUpperLimit() {
            return this.mDescriptor.getUpperLimit();
        }

        public int getSpacing() {
            return this.mDescriptor.getSpacing();
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            this.mDescriptor.writeToParcel(dest, flags);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "BandConfig [ " + this.mDescriptor.toString() + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public int hashCode() {
            int result = (1 * 31) + this.mDescriptor.hashCode();
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BandConfig) {
                BandConfig other = (BandConfig) obj;
                BandDescriptor otherDesc = other.getDescriptor();
                BandDescriptor bandDescriptor = this.mDescriptor;
                if ((bandDescriptor == null) != (otherDesc == null)) {
                    return false;
                }
                return bandDescriptor == null || bandDescriptor.equals(otherDesc);
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public static class FmBandConfig extends BandConfig {
        public static final Parcelable.Creator<FmBandConfig> CREATOR = new Parcelable.Creator<FmBandConfig>() { // from class: android.hardware.radio.RadioManager.FmBandConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public FmBandConfig createFromParcel(Parcel in) {
                return new FmBandConfig(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public FmBandConfig[] newArray(int size) {
                return new FmBandConfig[size];
            }
        };
        private final boolean mAf;
        private final boolean mEa;
        private final boolean mRds;
        private final boolean mStereo;
        private final boolean mTa;

        public FmBandConfig(FmBandDescriptor descriptor) {
            super(descriptor);
            this.mStereo = descriptor.isStereoSupported();
            this.mRds = descriptor.isRdsSupported();
            this.mTa = descriptor.isTaSupported();
            this.mAf = descriptor.isAfSupported();
            this.mEa = descriptor.isEaSupported();
        }

        FmBandConfig(int region, int type, int lowerLimit, int upperLimit, int spacing, boolean stereo, boolean rds, boolean ta, boolean af, boolean ea) {
            super(region, type, lowerLimit, upperLimit, spacing);
            this.mStereo = stereo;
            this.mRds = rds;
            this.mTa = ta;
            this.mAf = af;
            this.mEa = ea;
        }

        public boolean getStereo() {
            return this.mStereo;
        }

        public boolean getRds() {
            return this.mRds;
        }

        public boolean getTa() {
            return this.mTa;
        }

        public boolean getAf() {
            return this.mAf;
        }

        public boolean getEa() {
            return this.mEa;
        }

        private FmBandConfig(Parcel in) {
            super(in);
            this.mStereo = in.readByte() == 1;
            this.mRds = in.readByte() == 1;
            this.mTa = in.readByte() == 1;
            this.mAf = in.readByte() == 1;
            this.mEa = in.readByte() == 1;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(this.mStereo ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mRds ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mTa ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mAf ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mEa ? (byte) 1 : (byte) 0);
        }

        @Override // android.hardware.radio.RadioManager.BandConfig, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public String toString() {
            return "FmBandConfig [" + super.toString() + ", mStereo=" + this.mStereo + ", mRds=" + this.mRds + ", mTa=" + this.mTa + ", mAf=" + this.mAf + ", mEa =" + this.mEa + NavigationBarInflaterView.SIZE_MOD_END;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public int hashCode() {
            int result = super.hashCode();
            return (((((((((result * 31) + (this.mStereo ? 1 : 0)) * 31) + (this.mRds ? 1 : 0)) * 31) + (this.mTa ? 1 : 0)) * 31) + (this.mAf ? 1 : 0)) * 31) + (this.mEa ? 1 : 0);
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && (obj instanceof FmBandConfig)) {
                FmBandConfig other = (FmBandConfig) obj;
                return this.mStereo == other.mStereo && this.mRds == other.mRds && this.mTa == other.mTa && this.mAf == other.mAf && this.mEa == other.mEa;
            }
            return false;
        }

        /* loaded from: classes2.dex */
        public static class Builder {
            private boolean mAf;
            private final BandDescriptor mDescriptor;
            private boolean mEa;
            private boolean mRds;
            private boolean mStereo;
            private boolean mTa;

            public Builder(FmBandDescriptor descriptor) {
                this.mDescriptor = new BandDescriptor(descriptor.getRegion(), descriptor.getType(), descriptor.getLowerLimit(), descriptor.getUpperLimit(), descriptor.getSpacing());
                this.mStereo = descriptor.isStereoSupported();
                this.mRds = descriptor.isRdsSupported();
                this.mTa = descriptor.isTaSupported();
                this.mAf = descriptor.isAfSupported();
                this.mEa = descriptor.isEaSupported();
            }

            public Builder(FmBandConfig config) {
                this.mDescriptor = new BandDescriptor(config.getRegion(), config.getType(), config.getLowerLimit(), config.getUpperLimit(), config.getSpacing());
                this.mStereo = config.getStereo();
                this.mRds = config.getRds();
                this.mTa = config.getTa();
                this.mAf = config.getAf();
                this.mEa = config.getEa();
            }

            public FmBandConfig build() {
                FmBandConfig config = new FmBandConfig(this.mDescriptor.getRegion(), this.mDescriptor.getType(), this.mDescriptor.getLowerLimit(), this.mDescriptor.getUpperLimit(), this.mDescriptor.getSpacing(), this.mStereo, this.mRds, this.mTa, this.mAf, this.mEa);
                return config;
            }

            public Builder setStereo(boolean state) {
                this.mStereo = state;
                return this;
            }

            public Builder setRds(boolean state) {
                this.mRds = state;
                return this;
            }

            public Builder setTa(boolean state) {
                this.mTa = state;
                return this;
            }

            public Builder setAf(boolean state) {
                this.mAf = state;
                return this;
            }

            public Builder setEa(boolean state) {
                this.mEa = state;
                return this;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class AmBandConfig extends BandConfig {
        public static final Parcelable.Creator<AmBandConfig> CREATOR = new Parcelable.Creator<AmBandConfig>() { // from class: android.hardware.radio.RadioManager.AmBandConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AmBandConfig createFromParcel(Parcel in) {
                return new AmBandConfig(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public AmBandConfig[] newArray(int size) {
                return new AmBandConfig[size];
            }
        };
        private final boolean mStereo;

        public AmBandConfig(AmBandDescriptor descriptor) {
            super(descriptor);
            this.mStereo = descriptor.isStereoSupported();
        }

        AmBandConfig(int region, int type, int lowerLimit, int upperLimit, int spacing, boolean stereo) {
            super(region, type, lowerLimit, upperLimit, spacing);
            this.mStereo = stereo;
        }

        public boolean getStereo() {
            return this.mStereo;
        }

        private AmBandConfig(Parcel in) {
            super(in);
            this.mStereo = in.readByte() == 1;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(this.mStereo ? (byte) 1 : (byte) 0);
        }

        @Override // android.hardware.radio.RadioManager.BandConfig, android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public String toString() {
            return "AmBandConfig [" + super.toString() + ", mStereo=" + this.mStereo + NavigationBarInflaterView.SIZE_MOD_END;
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public int hashCode() {
            int result = super.hashCode();
            return (result * 31) + (this.mStereo ? 1 : 0);
        }

        @Override // android.hardware.radio.RadioManager.BandConfig
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (super.equals(obj) && (obj instanceof AmBandConfig)) {
                AmBandConfig other = (AmBandConfig) obj;
                return this.mStereo == other.getStereo();
            }
            return false;
        }

        /* loaded from: classes2.dex */
        public static class Builder {
            private final BandDescriptor mDescriptor;
            private boolean mStereo;

            public Builder(AmBandDescriptor descriptor) {
                this.mDescriptor = new BandDescriptor(descriptor.getRegion(), descriptor.getType(), descriptor.getLowerLimit(), descriptor.getUpperLimit(), descriptor.getSpacing());
                this.mStereo = descriptor.isStereoSupported();
            }

            public Builder(AmBandConfig config) {
                this.mDescriptor = new BandDescriptor(config.getRegion(), config.getType(), config.getLowerLimit(), config.getUpperLimit(), config.getSpacing());
                this.mStereo = config.getStereo();
            }

            public AmBandConfig build() {
                AmBandConfig config = new AmBandConfig(this.mDescriptor.getRegion(), this.mDescriptor.getType(), this.mDescriptor.getLowerLimit(), this.mDescriptor.getUpperLimit(), this.mDescriptor.getSpacing(), this.mStereo);
                return config;
            }

            public Builder setStereo(boolean state) {
                this.mStereo = state;
                return this;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ProgramInfo implements Parcelable {
        public static final Parcelable.Creator<ProgramInfo> CREATOR = new Parcelable.Creator<ProgramInfo>() { // from class: android.hardware.radio.RadioManager.ProgramInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ProgramInfo createFromParcel(Parcel in) {
                return new ProgramInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ProgramInfo[] newArray(int size) {
                return new ProgramInfo[size];
            }
        };
        private static final int FLAG_LIVE = 1;
        private static final int FLAG_MUTED = 2;
        private static final int FLAG_STEREO = 32;
        private static final int FLAG_TRAFFIC_ANNOUNCEMENT = 8;
        private static final int FLAG_TRAFFIC_PROGRAM = 4;
        private static final int FLAG_TUNED = 16;
        private final int mInfoFlags;
        private final ProgramSelector.Identifier mLogicallyTunedTo;
        private final RadioMetadata mMetadata;
        private final ProgramSelector.Identifier mPhysicallyTunedTo;
        private final Collection<ProgramSelector.Identifier> mRelatedContent;
        private final ProgramSelector mSelector;
        private final int mSignalQuality;
        private final Map<String, String> mVendorInfo;

        public ProgramInfo(ProgramSelector selector, ProgramSelector.Identifier logicallyTunedTo, ProgramSelector.Identifier physicallyTunedTo, Collection<ProgramSelector.Identifier> relatedContent, int infoFlags, int signalQuality, RadioMetadata metadata, Map<String, String> vendorInfo) {
            this.mSelector = (ProgramSelector) Objects.requireNonNull(selector);
            this.mLogicallyTunedTo = logicallyTunedTo;
            this.mPhysicallyTunedTo = physicallyTunedTo;
            if (relatedContent == null) {
                this.mRelatedContent = Collections.emptyList();
            } else {
                Preconditions.checkCollectionElementsNotNull(relatedContent, "relatedContent");
                this.mRelatedContent = relatedContent;
            }
            this.mInfoFlags = infoFlags;
            this.mSignalQuality = signalQuality;
            this.mMetadata = metadata;
            this.mVendorInfo = vendorInfo == null ? new HashMap<>() : vendorInfo;
        }

        public ProgramSelector getSelector() {
            return this.mSelector;
        }

        public ProgramSelector.Identifier getLogicallyTunedTo() {
            return this.mLogicallyTunedTo;
        }

        public ProgramSelector.Identifier getPhysicallyTunedTo() {
            return this.mPhysicallyTunedTo;
        }

        public Collection<ProgramSelector.Identifier> getRelatedContent() {
            return this.mRelatedContent;
        }

        @Deprecated
        public int getChannel() {
            try {
                return (int) this.mSelector.getFirstId(1);
            } catch (IllegalArgumentException e) {
                Log.m104w(RadioManager.TAG, "Not an AM/FM program");
                return 0;
            }
        }

        @Deprecated
        public int getSubChannel() {
            try {
                return ((int) this.mSelector.getFirstId(4)) + 1;
            } catch (IllegalArgumentException e) {
                return 0;
            }
        }

        public boolean isTuned() {
            return (this.mInfoFlags & 16) != 0;
        }

        public boolean isStereo() {
            return (this.mInfoFlags & 32) != 0;
        }

        @Deprecated
        public boolean isDigital() {
            ProgramSelector.Identifier id = this.mLogicallyTunedTo;
            if (id == null) {
                id = this.mSelector.getPrimaryId();
            }
            int type = id.getType();
            return (type == 1 || type == 2) ? false : true;
        }

        public boolean isLive() {
            return (this.mInfoFlags & 1) != 0;
        }

        public boolean isMuted() {
            return (this.mInfoFlags & 2) != 0;
        }

        public boolean isTrafficProgram() {
            return (this.mInfoFlags & 4) != 0;
        }

        public boolean isTrafficAnnouncementActive() {
            return (this.mInfoFlags & 8) != 0;
        }

        public int getSignalStrength() {
            return this.mSignalQuality;
        }

        public RadioMetadata getMetadata() {
            return this.mMetadata;
        }

        public Map<String, String> getVendorInfo() {
            return this.mVendorInfo;
        }

        private ProgramInfo(Parcel in) {
            this.mSelector = (ProgramSelector) Objects.requireNonNull((ProgramSelector) in.readTypedObject(ProgramSelector.CREATOR));
            this.mLogicallyTunedTo = (ProgramSelector.Identifier) in.readTypedObject(ProgramSelector.Identifier.CREATOR);
            this.mPhysicallyTunedTo = (ProgramSelector.Identifier) in.readTypedObject(ProgramSelector.Identifier.CREATOR);
            this.mRelatedContent = in.createTypedArrayList(ProgramSelector.Identifier.CREATOR);
            this.mInfoFlags = in.readInt();
            this.mSignalQuality = in.readInt();
            this.mMetadata = (RadioMetadata) in.readTypedObject(RadioMetadata.CREATOR);
            this.mVendorInfo = Utils.readStringMap(in);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedObject(this.mSelector, flags);
            dest.writeTypedObject(this.mLogicallyTunedTo, flags);
            dest.writeTypedObject(this.mPhysicallyTunedTo, flags);
            Utils.writeTypedCollection(dest, this.mRelatedContent);
            dest.writeInt(this.mInfoFlags);
            dest.writeInt(this.mSignalQuality);
            dest.writeTypedObject(this.mMetadata, flags);
            Utils.writeStringMap(dest, this.mVendorInfo);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "ProgramInfo [selector=" + this.mSelector + ", logicallyTunedTo=" + Objects.toString(this.mLogicallyTunedTo) + ", physicallyTunedTo=" + Objects.toString(this.mPhysicallyTunedTo) + ", relatedContent=" + this.mRelatedContent.size() + ", infoFlags=" + this.mInfoFlags + ", mSignalQuality=" + this.mSignalQuality + ", mMetadata=" + Objects.toString(this.mMetadata) + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public int hashCode() {
            return Objects.hash(this.mSelector, this.mLogicallyTunedTo, this.mPhysicallyTunedTo, this.mRelatedContent, Integer.valueOf(this.mInfoFlags), Integer.valueOf(this.mSignalQuality), this.mMetadata, this.mVendorInfo);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ProgramInfo) {
                ProgramInfo other = (ProgramInfo) obj;
                return this.mSelector.strictEquals(other.mSelector) && Objects.equals(this.mLogicallyTunedTo, other.mLogicallyTunedTo) && Objects.equals(this.mPhysicallyTunedTo, other.mPhysicallyTunedTo) && Objects.equals(this.mRelatedContent, other.mRelatedContent) && this.mInfoFlags == other.mInfoFlags && this.mSignalQuality == other.mSignalQuality && Objects.equals(this.mMetadata, other.mMetadata) && Objects.equals(this.mVendorInfo, other.mVendorInfo);
            }
            return false;
        }
    }

    public int listModules(List<ModuleProperties> modules) {
        if (modules == null) {
            Log.m110e(TAG, "the output list must not be empty");
            return -22;
        }
        Log.m112d(TAG, "Listing available tuners...");
        try {
            List<ModuleProperties> returnedList = this.mService.listModules();
            if (returnedList == null) {
                Log.m110e(TAG, "Returned list was a null");
                return Integer.MIN_VALUE;
            }
            modules.addAll(returnedList);
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Failed listing available tuners", e);
            return -32;
        }
    }

    public RadioTuner openTuner(int moduleId, BandConfig config, boolean withAudio, RadioTuner.Callback callback, Handler handler) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be empty");
        }
        Log.m112d(TAG, "Opening tuner " + moduleId + Session.TRUNCATE_STRING);
        TunerCallbackAdapter halCallback = new TunerCallbackAdapter(callback, handler);
        try {
            ITuner tuner = this.mService.openTuner(moduleId, config, withAudio, halCallback, this.mTargetSdkVersion);
            if (tuner == null) {
                Log.m110e(TAG, "Failed to open tuner");
                return null;
            }
            return new TunerAdapter(tuner, halCallback, config != null ? config.getType() : -1);
        } catch (RemoteException | IllegalArgumentException | IllegalStateException ex) {
            Log.m109e(TAG, "Failed to open tuner", ex);
            return null;
        }
    }

    public void addAnnouncementListener(Set<Integer> enabledAnnouncementTypes, Announcement.OnListUpdatedListener listener) {
        addAnnouncementListener(new Executor() { // from class: android.hardware.radio.RadioManager$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                runnable.run();
            }
        }, enabledAnnouncementTypes, listener);
    }

    public void addAnnouncementListener(Executor executor, Set<Integer> enabledAnnouncementTypes, Announcement.OnListUpdatedListener listener) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(listener);
        int[] types = enabledAnnouncementTypes.stream().mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).toArray();
        IAnnouncementListener listenerIface = new BinderC12261(executor, listener);
        synchronized (this.mAnnouncementListeners) {
            ICloseHandle closeHandle = null;
            try {
                closeHandle = this.mService.addAnnouncementListener(types, listenerIface);
            } catch (RemoteException ex) {
                ex.rethrowFromSystemServer();
            }
            Objects.requireNonNull(closeHandle);
            ICloseHandle oldCloseHandle = this.mAnnouncementListeners.put(listener, closeHandle);
            if (oldCloseHandle != null) {
                Utils.close(oldCloseHandle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.hardware.radio.RadioManager$1 */
    /* loaded from: classes2.dex */
    public class BinderC12261 extends IAnnouncementListener.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ Announcement.OnListUpdatedListener val$listener;

        BinderC12261(Executor executor, Announcement.OnListUpdatedListener onListUpdatedListener) {
            this.val$executor = executor;
            this.val$listener = onListUpdatedListener;
        }

        @Override // android.hardware.radio.IAnnouncementListener
        public void onListUpdated(final List<Announcement> activeAnnouncements) {
            Executor executor = this.val$executor;
            final Announcement.OnListUpdatedListener onListUpdatedListener = this.val$listener;
            executor.execute(new Runnable() { // from class: android.hardware.radio.RadioManager$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Announcement.OnListUpdatedListener.this.onListUpdated(activeAnnouncements);
                }
            });
        }
    }

    public void removeAnnouncementListener(Announcement.OnListUpdatedListener listener) {
        Objects.requireNonNull(listener);
        synchronized (this.mAnnouncementListeners) {
            ICloseHandle closeHandle = this.mAnnouncementListeners.remove(listener);
            if (closeHandle != null) {
                Utils.close(closeHandle);
            }
        }
    }

    public RadioManager(Context context) throws ServiceManager.ServiceNotFoundException {
        this(context, IRadioService.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.RADIO_SERVICE)));
    }

    public RadioManager(Context context, IRadioService service) {
        this.mAnnouncementListeners = new HashMap();
        this.mContext = context;
        this.mService = service;
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
    }
}
