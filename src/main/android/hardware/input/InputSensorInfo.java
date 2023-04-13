package android.hardware.input;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes2.dex */
public class InputSensorInfo implements Parcelable {
    public static final Parcelable.Creator<InputSensorInfo> CREATOR = new Parcelable.Creator<InputSensorInfo>() { // from class: android.hardware.input.InputSensorInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputSensorInfo[] newArray(int size) {
            return new InputSensorInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputSensorInfo createFromParcel(Parcel in) {
            return new InputSensorInfo(in);
        }
    };
    private int mFifoMaxEventCount;
    private int mFifoReservedEventCount;
    private int mFlags;
    private int mHandle;
    private int mId;
    private int mMaxDelay;
    private float mMaxRange;
    private int mMinDelay;
    private String mName;
    private float mPower;
    private String mRequiredPermission;
    private float mResolution;
    private String mStringType;
    private int mType;
    private String mVendor;
    private int mVersion;

    public InputSensorInfo(String name, String vendor, int version, int handle, int type, float maxRange, float resolution, float power, int minDelay, int fifoReservedEventCount, int fifoMaxEventCount, String stringType, String requiredPermission, int maxDelay, int flags, int id) {
        this.mName = name;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) name);
        this.mVendor = vendor;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) vendor);
        this.mVersion = version;
        this.mHandle = handle;
        this.mType = type;
        this.mMaxRange = maxRange;
        this.mResolution = resolution;
        this.mPower = power;
        this.mMinDelay = minDelay;
        this.mFifoReservedEventCount = fifoReservedEventCount;
        this.mFifoMaxEventCount = fifoMaxEventCount;
        this.mStringType = stringType;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) stringType);
        this.mRequiredPermission = requiredPermission;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requiredPermission);
        this.mMaxDelay = maxDelay;
        this.mFlags = flags;
        this.mId = id;
    }

    public String getName() {
        return this.mName;
    }

    public String getVendor() {
        return this.mVendor;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public int getHandle() {
        return this.mHandle;
    }

    public int getType() {
        return this.mType;
    }

    public float getMaxRange() {
        return this.mMaxRange;
    }

    public float getResolution() {
        return this.mResolution;
    }

    public float getPower() {
        return this.mPower;
    }

    public int getMinDelay() {
        return this.mMinDelay;
    }

    public int getFifoReservedEventCount() {
        return this.mFifoReservedEventCount;
    }

    public int getFifoMaxEventCount() {
        return this.mFifoMaxEventCount;
    }

    public String getStringType() {
        return this.mStringType;
    }

    public String getRequiredPermission() {
        return this.mRequiredPermission;
    }

    public int getMaxDelay() {
        return this.mMaxDelay;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getId() {
        return this.mId;
    }

    public String toString() {
        return "InputSensorInfo { name = " + this.mName + ", vendor = " + this.mVendor + ", version = " + this.mVersion + ", handle = " + this.mHandle + ", type = " + this.mType + ", maxRange = " + this.mMaxRange + ", resolution = " + this.mResolution + ", power = " + this.mPower + ", minDelay = " + this.mMinDelay + ", fifoReservedEventCount = " + this.mFifoReservedEventCount + ", fifoMaxEventCount = " + this.mFifoMaxEventCount + ", stringType = " + this.mStringType + ", requiredPermission = " + this.mRequiredPermission + ", maxDelay = " + this.mMaxDelay + ", flags = " + this.mFlags + ", id = " + this.mId + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mVendor);
        dest.writeInt(this.mVersion);
        dest.writeInt(this.mHandle);
        dest.writeInt(this.mType);
        dest.writeFloat(this.mMaxRange);
        dest.writeFloat(this.mResolution);
        dest.writeFloat(this.mPower);
        dest.writeInt(this.mMinDelay);
        dest.writeInt(this.mFifoReservedEventCount);
        dest.writeInt(this.mFifoMaxEventCount);
        dest.writeString(this.mStringType);
        dest.writeString(this.mRequiredPermission);
        dest.writeInt(this.mMaxDelay);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected InputSensorInfo(Parcel in) {
        String name = in.readString();
        String vendor = in.readString();
        int version = in.readInt();
        int handle = in.readInt();
        int type = in.readInt();
        float maxRange = in.readFloat();
        float resolution = in.readFloat();
        float power = in.readFloat();
        int minDelay = in.readInt();
        int fifoReservedEventCount = in.readInt();
        int fifoMaxEventCount = in.readInt();
        String stringType = in.readString();
        String requiredPermission = in.readString();
        int maxDelay = in.readInt();
        int flags = in.readInt();
        int id = in.readInt();
        this.mName = name;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) name);
        this.mVendor = vendor;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) vendor);
        this.mVersion = version;
        this.mHandle = handle;
        this.mType = type;
        this.mMaxRange = maxRange;
        this.mResolution = resolution;
        this.mPower = power;
        this.mMinDelay = minDelay;
        this.mFifoReservedEventCount = fifoReservedEventCount;
        this.mFifoMaxEventCount = fifoMaxEventCount;
        this.mStringType = stringType;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) stringType);
        this.mRequiredPermission = requiredPermission;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) requiredPermission);
        this.mMaxDelay = maxDelay;
        this.mFlags = flags;
        this.mId = id;
    }

    @Deprecated
    private void __metadata() {
    }
}
