package android.hardware.camera2.params;

import android.annotation.SystemApi;
import android.graphics.ColorSpace;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.MultiResolutionImageReader;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.hardware.camera2.utils.SurfaceUtils;
import android.media.ImageReader;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class OutputConfiguration implements Parcelable {
    private static final int MAX_SURFACES_COUNT = 4;
    public static final int MIRROR_MODE_AUTO = 0;
    public static final int MIRROR_MODE_H = 2;
    public static final int MIRROR_MODE_NONE = 1;
    public static final int MIRROR_MODE_V = 3;
    @SystemApi
    public static final int ROTATION_0 = 0;
    @SystemApi
    public static final int ROTATION_180 = 2;
    @SystemApi
    public static final int ROTATION_270 = 3;
    @SystemApi
    public static final int ROTATION_90 = 1;
    public static final int SURFACE_GROUP_ID_NONE = -1;
    private static final String TAG = "OutputConfiguration";
    public static final int TIMESTAMP_BASE_CHOREOGRAPHER_SYNCED = 4;
    public static final int TIMESTAMP_BASE_DEFAULT = 0;
    public static final int TIMESTAMP_BASE_MONOTONIC = 2;
    public static final int TIMESTAMP_BASE_READOUT_SENSOR = 5;
    public static final int TIMESTAMP_BASE_REALTIME = 3;
    public static final int TIMESTAMP_BASE_SENSOR = 1;
    private final int SURFACE_TYPE_SURFACE_TEXTURE;
    private final int SURFACE_TYPE_SURFACE_VIEW;
    private final int SURFACE_TYPE_UNKNOWN;
    private int mColorSpace;
    private final int mConfiguredDataspace;
    private final int mConfiguredFormat;
    private final int mConfiguredGenerationId;
    private final Size mConfiguredSize;
    private long mDynamicRangeProfile;
    private final boolean mIsDeferredConfig;
    private boolean mIsMultiResolution;
    private boolean mIsReadoutSensorTimestampBase;
    private boolean mIsShared;
    private int mMirrorMode;
    private String mPhysicalCameraId;
    private boolean mReadoutTimestampEnabled;
    private final int mRotation;
    private ArrayList<Integer> mSensorPixelModesUsed;
    private long mStreamUseCase;
    private final int mSurfaceGroupId;
    private final int mSurfaceType;
    private ArrayList<Surface> mSurfaces;
    private int mTimestampBase;
    public static final Parcelable.Creator<OutputConfiguration> CREATOR = new Parcelable.Creator<OutputConfiguration>() { // from class: android.hardware.camera2.params.OutputConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OutputConfiguration createFromParcel(Parcel source) {
            return new OutputConfiguration(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OutputConfiguration[] newArray(int size) {
            return new OutputConfiguration[size];
        }
    };
    private static int MULTI_RESOLUTION_GROUP_ID_COUNTER = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MirrorMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SensorPixelMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StreamUseCase {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface TimestampBase {
    }

    public OutputConfiguration(Surface surface) {
        this(-1, surface, 0);
    }

    public OutputConfiguration(int surfaceGroupId, Surface surface) {
        this(surfaceGroupId, surface, 0);
    }

    void setMultiResolutionOutput() {
        if (this.mIsShared) {
            throw new IllegalStateException("Multi-resolution output flag must not be set for configuration with surface sharing");
        }
        if (this.mSurfaceGroupId == -1) {
            throw new IllegalStateException("Multi-resolution output flag should only be set for surface with non-negative group ID");
        }
        this.mIsMultiResolution = true;
    }

    public void setDynamicRangeProfile(long profile) {
        this.mDynamicRangeProfile = profile;
    }

    public long getDynamicRangeProfile() {
        return this.mDynamicRangeProfile;
    }

    public void setColorSpace(ColorSpace.Named colorSpace) {
        this.mColorSpace = colorSpace.ordinal();
    }

    public void clearColorSpace() {
        this.mColorSpace = -1;
    }

    public ColorSpace getColorSpace() {
        if (this.mColorSpace != -1) {
            return ColorSpace.get(ColorSpace.Named.values()[this.mColorSpace]);
        }
        return null;
    }

    @SystemApi
    public OutputConfiguration(Surface surface, int rotation) {
        this(-1, surface, rotation);
    }

    @SystemApi
    public OutputConfiguration(int surfaceGroupId, Surface surface, int rotation) {
        this.SURFACE_TYPE_UNKNOWN = -1;
        this.SURFACE_TYPE_SURFACE_VIEW = 0;
        this.SURFACE_TYPE_SURFACE_TEXTURE = 1;
        Preconditions.checkNotNull(surface, "Surface must not be null");
        Preconditions.checkArgumentInRange(rotation, 0, 3, "Rotation constant");
        this.mSurfaceGroupId = surfaceGroupId;
        this.mSurfaceType = -1;
        ArrayList<Surface> arrayList = new ArrayList<>();
        this.mSurfaces = arrayList;
        arrayList.add(surface);
        this.mRotation = rotation;
        this.mConfiguredSize = SurfaceUtils.getSurfaceSize(surface);
        this.mConfiguredFormat = SurfaceUtils.getSurfaceFormat(surface);
        this.mConfiguredDataspace = SurfaceUtils.getSurfaceDataspace(surface);
        this.mConfiguredGenerationId = surface.getGenerationId();
        this.mIsDeferredConfig = false;
        this.mIsShared = false;
        this.mPhysicalCameraId = null;
        this.mIsMultiResolution = false;
        this.mSensorPixelModesUsed = new ArrayList<>();
        this.mDynamicRangeProfile = 1L;
        this.mColorSpace = -1;
        this.mStreamUseCase = 0L;
        this.mTimestampBase = 0;
        this.mMirrorMode = 0;
        this.mReadoutTimestampEnabled = false;
        this.mIsReadoutSensorTimestampBase = false;
    }

    public static Collection<OutputConfiguration> createInstancesForMultiResolutionOutput(MultiResolutionImageReader multiResolutionImageReader) {
        Preconditions.checkNotNull(multiResolutionImageReader, "Multi-resolution image reader must not be null");
        int groupId = MULTI_RESOLUTION_GROUP_ID_COUNTER;
        int i = MULTI_RESOLUTION_GROUP_ID_COUNTER + 1;
        MULTI_RESOLUTION_GROUP_ID_COUNTER = i;
        if (i == -1) {
            MULTI_RESOLUTION_GROUP_ID_COUNTER = i + 1;
        }
        ImageReader[] imageReaders = multiResolutionImageReader.getReaders();
        ArrayList<OutputConfiguration> configs = new ArrayList<>();
        for (int i2 = 0; i2 < imageReaders.length; i2++) {
            MultiResolutionStreamInfo streamInfo = multiResolutionImageReader.getStreamInfoForImageReader(imageReaders[i2]);
            OutputConfiguration config = new OutputConfiguration(groupId, imageReaders[i2].getSurface());
            config.setPhysicalCameraId(streamInfo.getPhysicalCameraId());
            config.setMultiResolutionOutput();
            configs.add(config);
        }
        return configs;
    }

    public <T> OutputConfiguration(Size surfaceSize, Class<T> klass) {
        this.SURFACE_TYPE_UNKNOWN = -1;
        this.SURFACE_TYPE_SURFACE_VIEW = 0;
        this.SURFACE_TYPE_SURFACE_TEXTURE = 1;
        Preconditions.checkNotNull(klass, "surfaceSize must not be null");
        Preconditions.checkNotNull(klass, "klass must not be null");
        if (klass == SurfaceHolder.class) {
            this.mSurfaceType = 0;
        } else if (klass == SurfaceTexture.class) {
            this.mSurfaceType = 1;
        } else {
            this.mSurfaceType = -1;
            throw new IllegalArgumentException("Unknow surface source class type");
        }
        if (surfaceSize.getWidth() == 0 || surfaceSize.getHeight() == 0) {
            throw new IllegalArgumentException("Surface size needs to be non-zero");
        }
        this.mSurfaceGroupId = -1;
        this.mSurfaces = new ArrayList<>();
        this.mRotation = 0;
        this.mConfiguredSize = surfaceSize;
        this.mConfiguredFormat = StreamConfigurationMap.imageFormatToInternal(34);
        this.mConfiguredDataspace = StreamConfigurationMap.imageFormatToDataspace(34);
        this.mConfiguredGenerationId = 0;
        this.mIsDeferredConfig = true;
        this.mIsShared = false;
        this.mPhysicalCameraId = null;
        this.mIsMultiResolution = false;
        this.mSensorPixelModesUsed = new ArrayList<>();
        this.mDynamicRangeProfile = 1L;
        this.mColorSpace = -1;
        this.mStreamUseCase = 0L;
        this.mReadoutTimestampEnabled = false;
        this.mIsReadoutSensorTimestampBase = false;
    }

    public void enableSurfaceSharing() {
        if (this.mIsMultiResolution) {
            throw new IllegalStateException("Cannot enable surface sharing on multi-resolution output configurations");
        }
        this.mIsShared = true;
    }

    public void setPhysicalCameraId(String physicalCameraId) {
        this.mPhysicalCameraId = physicalCameraId;
    }

    public void addSensorPixelModeUsed(int sensorPixelModeUsed) {
        if (sensorPixelModeUsed != 0 && sensorPixelModeUsed != 1) {
            throw new IllegalArgumentException("Not a valid sensor pixel mode " + sensorPixelModeUsed);
        }
        if (this.mSensorPixelModesUsed.contains(Integer.valueOf(sensorPixelModeUsed))) {
            return;
        }
        this.mSensorPixelModesUsed.add(Integer.valueOf(sensorPixelModeUsed));
    }

    public void removeSensorPixelModeUsed(int sensorPixelModeUsed) {
        if (!this.mSensorPixelModesUsed.remove(Integer.valueOf(sensorPixelModeUsed))) {
            throw new IllegalArgumentException("sensorPixelMode " + sensorPixelModeUsed + "is not part of this output configuration");
        }
    }

    public boolean isForPhysicalCamera() {
        return this.mPhysicalCameraId != null;
    }

    public boolean isDeferredConfiguration() {
        return this.mIsDeferredConfig;
    }

    public void addSurface(Surface surface) {
        Preconditions.checkNotNull(surface, "Surface must not be null");
        if (this.mSurfaces.contains(surface)) {
            throw new IllegalStateException("Surface is already added!");
        }
        if (this.mSurfaces.size() == 1 && !this.mIsShared) {
            throw new IllegalStateException("Cannot have 2 surfaces for a non-sharing configuration");
        }
        if (this.mSurfaces.size() + 1 > 4) {
            throw new IllegalArgumentException("Exceeds maximum number of surfaces");
        }
        Size surfaceSize = SurfaceUtils.getSurfaceSize(surface);
        if (!surfaceSize.equals(this.mConfiguredSize)) {
            Log.m104w(TAG, "Added surface size " + surfaceSize + " is different than pre-configured size " + this.mConfiguredSize + ", the pre-configured size will be used.");
        }
        if (this.mConfiguredFormat != SurfaceUtils.getSurfaceFormat(surface)) {
            throw new IllegalArgumentException("The format of added surface format doesn't match");
        }
        if (this.mConfiguredFormat != 34 && this.mConfiguredDataspace != SurfaceUtils.getSurfaceDataspace(surface)) {
            throw new IllegalArgumentException("The dataspace of added surface doesn't match");
        }
        this.mSurfaces.add(surface);
    }

    public void removeSurface(Surface surface) {
        if (getSurface() == surface) {
            throw new IllegalArgumentException("Cannot remove surface associated with this output configuration");
        }
        if (!this.mSurfaces.remove(surface)) {
            throw new IllegalArgumentException("Surface is not part of this output configuration");
        }
    }

    public void setStreamUseCase(long streamUseCase) {
        if (streamUseCase > 6 && streamUseCase < 65536) {
            throw new IllegalArgumentException("Not a valid stream use case value " + streamUseCase);
        }
        this.mStreamUseCase = streamUseCase;
    }

    public long getStreamUseCase() {
        return this.mStreamUseCase;
    }

    public void setTimestampBase(int timestampBase) {
        if (timestampBase < 0 || timestampBase > 5) {
            throw new IllegalArgumentException("Not a valid timestamp base value " + timestampBase);
        }
        if (timestampBase == 5) {
            this.mTimestampBase = 1;
            this.mReadoutTimestampEnabled = true;
            this.mIsReadoutSensorTimestampBase = true;
            return;
        }
        this.mTimestampBase = timestampBase;
        this.mIsReadoutSensorTimestampBase = false;
    }

    public int getTimestampBase() {
        if (this.mIsReadoutSensorTimestampBase) {
            return 5;
        }
        return this.mTimestampBase;
    }

    public void setMirrorMode(int mirrorMode) {
        if (mirrorMode < 0 || mirrorMode > 3) {
            throw new IllegalArgumentException("Not a valid mirror mode " + mirrorMode);
        }
        this.mMirrorMode = mirrorMode;
    }

    public int getMirrorMode() {
        return this.mMirrorMode;
    }

    public void setReadoutTimestampEnabled(boolean on) {
        this.mReadoutTimestampEnabled = on;
    }

    public boolean isReadoutTimestampEnabled() {
        return this.mReadoutTimestampEnabled;
    }

    public OutputConfiguration(OutputConfiguration other) {
        this.SURFACE_TYPE_UNKNOWN = -1;
        this.SURFACE_TYPE_SURFACE_VIEW = 0;
        this.SURFACE_TYPE_SURFACE_TEXTURE = 1;
        if (other == null) {
            throw new IllegalArgumentException("OutputConfiguration shouldn't be null");
        }
        this.mSurfaces = other.mSurfaces;
        this.mRotation = other.mRotation;
        this.mSurfaceGroupId = other.mSurfaceGroupId;
        this.mSurfaceType = other.mSurfaceType;
        this.mConfiguredDataspace = other.mConfiguredDataspace;
        this.mConfiguredFormat = other.mConfiguredFormat;
        this.mConfiguredSize = other.mConfiguredSize;
        this.mConfiguredGenerationId = other.mConfiguredGenerationId;
        this.mIsDeferredConfig = other.mIsDeferredConfig;
        this.mIsShared = other.mIsShared;
        this.mPhysicalCameraId = other.mPhysicalCameraId;
        this.mIsMultiResolution = other.mIsMultiResolution;
        this.mSensorPixelModesUsed = other.mSensorPixelModesUsed;
        this.mDynamicRangeProfile = other.mDynamicRangeProfile;
        this.mColorSpace = other.mColorSpace;
        this.mStreamUseCase = other.mStreamUseCase;
        this.mTimestampBase = other.mTimestampBase;
        this.mMirrorMode = other.mMirrorMode;
        this.mReadoutTimestampEnabled = other.mReadoutTimestampEnabled;
    }

    private OutputConfiguration(Parcel source) {
        boolean isDeferred;
        boolean isShared;
        boolean isMultiResolutionOutput;
        this.SURFACE_TYPE_UNKNOWN = -1;
        this.SURFACE_TYPE_SURFACE_VIEW = 0;
        this.SURFACE_TYPE_SURFACE_TEXTURE = 1;
        int rotation = source.readInt();
        int surfaceSetId = source.readInt();
        int surfaceType = source.readInt();
        int width = source.readInt();
        int height = source.readInt();
        if (source.readInt() != 1) {
            isDeferred = false;
        } else {
            isDeferred = true;
        }
        if (source.readInt() != 1) {
            isShared = false;
        } else {
            isShared = true;
        }
        ArrayList<Surface> surfaces = new ArrayList<>();
        source.readTypedList(surfaces, Surface.CREATOR);
        String physicalCameraId = source.readString();
        if (source.readInt() != 1) {
            isMultiResolutionOutput = false;
        } else {
            isMultiResolutionOutput = true;
        }
        int[] sensorPixelModesUsed = source.createIntArray();
        long streamUseCase = source.readLong();
        Preconditions.checkArgumentInRange(rotation, 0, 3, "Rotation constant");
        long dynamicRangeProfile = source.readLong();
        DynamicRangeProfiles.checkProfileValue(dynamicRangeProfile);
        int colorSpace = source.readInt();
        int timestampBase = source.readInt();
        int mirrorMode = source.readInt();
        int mirrorMode2 = source.readInt();
        boolean readoutTimestampEnabled = mirrorMode2 == 1;
        this.mSurfaceGroupId = surfaceSetId;
        this.mRotation = rotation;
        this.mSurfaces = surfaces;
        this.mConfiguredSize = new Size(width, height);
        this.mIsDeferredConfig = isDeferred;
        this.mIsShared = isShared;
        this.mSurfaces = surfaces;
        if (surfaces.size() > 0) {
            this.mSurfaceType = -1;
            this.mConfiguredFormat = SurfaceUtils.getSurfaceFormat(this.mSurfaces.get(0));
            this.mConfiguredDataspace = SurfaceUtils.getSurfaceDataspace(this.mSurfaces.get(0));
            this.mConfiguredGenerationId = this.mSurfaces.get(0).getGenerationId();
        } else {
            this.mSurfaceType = surfaceType;
            this.mConfiguredFormat = StreamConfigurationMap.imageFormatToInternal(34);
            this.mConfiguredDataspace = StreamConfigurationMap.imageFormatToDataspace(34);
            this.mConfiguredGenerationId = 0;
        }
        this.mPhysicalCameraId = physicalCameraId;
        this.mIsMultiResolution = isMultiResolutionOutput;
        this.mSensorPixelModesUsed = convertIntArrayToIntegerList(sensorPixelModesUsed);
        this.mDynamicRangeProfile = dynamicRangeProfile;
        this.mColorSpace = colorSpace;
        this.mStreamUseCase = streamUseCase;
        this.mTimestampBase = timestampBase;
        this.mMirrorMode = mirrorMode;
        this.mReadoutTimestampEnabled = readoutTimestampEnabled;
    }

    public int getMaxSharedSurfaceCount() {
        return 4;
    }

    public Surface getSurface() {
        if (this.mSurfaces.size() == 0) {
            return null;
        }
        return this.mSurfaces.get(0);
    }

    public List<Surface> getSurfaces() {
        return Collections.unmodifiableList(this.mSurfaces);
    }

    @SystemApi
    public int getRotation() {
        return this.mRotation;
    }

    public int getSurfaceGroupId() {
        return this.mSurfaceGroupId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static int[] convertIntegerToIntList(List<Integer> integerList) {
        int[] integerArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            integerArray[i] = integerList.get(i).intValue();
        }
        return integerArray;
    }

    private static ArrayList<Integer> convertIntArrayToIntegerList(int[] intArray) {
        ArrayList<Integer> integerList = new ArrayList<>();
        if (intArray == null) {
            return integerList;
        }
        for (int i : intArray) {
            integerList.add(Integer.valueOf(i));
        }
        return integerList;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (dest == null) {
            throw new IllegalArgumentException("dest must not be null");
        }
        dest.writeInt(this.mRotation);
        dest.writeInt(this.mSurfaceGroupId);
        dest.writeInt(this.mSurfaceType);
        dest.writeInt(this.mConfiguredSize.getWidth());
        dest.writeInt(this.mConfiguredSize.getHeight());
        dest.writeInt(this.mIsDeferredConfig ? 1 : 0);
        dest.writeInt(this.mIsShared ? 1 : 0);
        dest.writeTypedList(this.mSurfaces);
        dest.writeString(this.mPhysicalCameraId);
        dest.writeInt(this.mIsMultiResolution ? 1 : 0);
        dest.writeIntArray(convertIntegerToIntList(this.mSensorPixelModesUsed));
        dest.writeLong(this.mDynamicRangeProfile);
        dest.writeInt(this.mColorSpace);
        dest.writeLong(this.mStreamUseCase);
        dest.writeInt(this.mTimestampBase);
        dest.writeInt(this.mMirrorMode);
        dest.writeInt(this.mReadoutTimestampEnabled ? 1 : 0);
    }

    public boolean equals(Object obj) {
        int i;
        int i2;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OutputConfiguration)) {
            return false;
        }
        OutputConfiguration other = (OutputConfiguration) obj;
        if (this.mRotation != other.mRotation || !this.mConfiguredSize.equals(other.mConfiguredSize) || (i = this.mConfiguredFormat) != (i2 = other.mConfiguredFormat) || this.mSurfaceGroupId != other.mSurfaceGroupId || this.mSurfaceType != other.mSurfaceType || this.mIsDeferredConfig != other.mIsDeferredConfig || this.mIsShared != other.mIsShared || i != i2 || this.mConfiguredDataspace != other.mConfiguredDataspace || this.mConfiguredGenerationId != other.mConfiguredGenerationId || !Objects.equals(this.mPhysicalCameraId, other.mPhysicalCameraId) || this.mIsMultiResolution != other.mIsMultiResolution || this.mStreamUseCase != other.mStreamUseCase || this.mTimestampBase != other.mTimestampBase || this.mMirrorMode != other.mMirrorMode || this.mReadoutTimestampEnabled != other.mReadoutTimestampEnabled || this.mSensorPixelModesUsed.size() != other.mSensorPixelModesUsed.size()) {
            return false;
        }
        for (int j = 0; j < this.mSensorPixelModesUsed.size(); j++) {
            if (!Objects.equals(this.mSensorPixelModesUsed.get(j), other.mSensorPixelModesUsed.get(j))) {
                return false;
            }
        }
        int minLen = Math.min(this.mSurfaces.size(), other.mSurfaces.size());
        for (int i3 = 0; i3 < minLen; i3++) {
            if (this.mSurfaces.get(i3) != other.mSurfaces.get(i3)) {
                return false;
            }
        }
        if (this.mDynamicRangeProfile != other.mDynamicRangeProfile || this.mColorSpace != other.mColorSpace) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.mIsDeferredConfig) {
            float[] fArr = new float[16];
            fArr[0] = this.mRotation;
            fArr[1] = this.mConfiguredSize.hashCode();
            fArr[2] = this.mConfiguredFormat;
            fArr[3] = this.mConfiguredDataspace;
            fArr[4] = this.mSurfaceGroupId;
            fArr[5] = this.mSurfaceType;
            fArr[6] = this.mIsShared ? 1.0f : 0.0f;
            String str = this.mPhysicalCameraId;
            fArr[7] = str == null ? 0.0f : str.hashCode();
            fArr[8] = this.mIsMultiResolution ? 1.0f : 0.0f;
            fArr[9] = this.mSensorPixelModesUsed.hashCode();
            fArr[10] = (float) this.mDynamicRangeProfile;
            fArr[11] = this.mColorSpace;
            fArr[12] = (float) this.mStreamUseCase;
            fArr[13] = this.mTimestampBase;
            fArr[14] = this.mMirrorMode;
            fArr[15] = this.mReadoutTimestampEnabled ? 1.0f : 0.0f;
            return HashCodeHelpers.hashCode(fArr);
        }
        float[] fArr2 = new float[17];
        fArr2[0] = this.mRotation;
        fArr2[1] = this.mSurfaces.hashCode();
        fArr2[2] = this.mConfiguredGenerationId;
        fArr2[3] = this.mConfiguredSize.hashCode();
        fArr2[4] = this.mConfiguredFormat;
        fArr2[5] = this.mConfiguredDataspace;
        fArr2[6] = this.mSurfaceGroupId;
        fArr2[7] = this.mIsShared ? 1.0f : 0.0f;
        String str2 = this.mPhysicalCameraId;
        fArr2[8] = str2 == null ? 0.0f : str2.hashCode();
        fArr2[9] = this.mIsMultiResolution ? 1.0f : 0.0f;
        fArr2[10] = this.mSensorPixelModesUsed.hashCode();
        fArr2[11] = (float) this.mDynamicRangeProfile;
        fArr2[12] = this.mColorSpace;
        fArr2[13] = (float) this.mStreamUseCase;
        fArr2[14] = this.mTimestampBase;
        fArr2[15] = this.mMirrorMode;
        fArr2[16] = this.mReadoutTimestampEnabled ? 1.0f : 0.0f;
        return HashCodeHelpers.hashCode(fArr2);
    }
}
