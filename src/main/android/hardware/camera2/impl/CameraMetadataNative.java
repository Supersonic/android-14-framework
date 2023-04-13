package android.hardware.camera2.impl;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.MarshalRegistry;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.marshal.impl.MarshalQueryableArray;
import android.hardware.camera2.marshal.impl.MarshalQueryableBlackLevelPattern;
import android.hardware.camera2.marshal.impl.MarshalQueryableBoolean;
import android.hardware.camera2.marshal.impl.MarshalQueryableColorSpaceTransform;
import android.hardware.camera2.marshal.impl.MarshalQueryableEnum;
import android.hardware.camera2.marshal.impl.MarshalQueryableHighSpeedVideoConfiguration;
import android.hardware.camera2.marshal.impl.MarshalQueryableMeteringRectangle;
import android.hardware.camera2.marshal.impl.MarshalQueryableNativeByteToInteger;
import android.hardware.camera2.marshal.impl.MarshalQueryablePair;
import android.hardware.camera2.marshal.impl.MarshalQueryableParcelable;
import android.hardware.camera2.marshal.impl.MarshalQueryablePrimitive;
import android.hardware.camera2.marshal.impl.MarshalQueryableRange;
import android.hardware.camera2.marshal.impl.MarshalQueryableRecommendedStreamConfiguration;
import android.hardware.camera2.marshal.impl.MarshalQueryableRect;
import android.hardware.camera2.marshal.impl.MarshalQueryableReprocessFormatsMap;
import android.hardware.camera2.marshal.impl.MarshalQueryableRggbChannelVector;
import android.hardware.camera2.marshal.impl.MarshalQueryableSize;
import android.hardware.camera2.marshal.impl.MarshalQueryableSizeF;
import android.hardware.camera2.marshal.impl.MarshalQueryableStreamConfiguration;
import android.hardware.camera2.marshal.impl.MarshalQueryableStreamConfigurationDuration;
import android.hardware.camera2.marshal.impl.MarshalQueryableString;
import android.hardware.camera2.params.Capability;
import android.hardware.camera2.params.ColorSpaceProfiles;
import android.hardware.camera2.params.DeviceStateSensorOrientationMap;
import android.hardware.camera2.params.DynamicRangeProfiles;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.HighSpeedVideoConfiguration;
import android.hardware.camera2.params.LensShadingMap;
import android.hardware.camera2.params.MandatoryStreamCombination;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.MultiResolutionStreamConfigurationMap;
import android.hardware.camera2.params.OisSample;
import android.hardware.camera2.params.RecommendedStreamConfiguration;
import android.hardware.camera2.params.RecommendedStreamConfigurationMap;
import android.hardware.camera2.params.ReprocessFormatsMap;
import android.hardware.camera2.params.StreamConfiguration;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.params.TonemapCurve;
import android.hardware.camera2.utils.ArrayUtils;
import android.hardware.camera2.utils.TypeReference;
import android.location.Location;
import android.location.LocationManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ServiceSpecificException;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import dalvik.annotation.optimization.FastNative;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public class CameraMetadataNative implements Parcelable {
    private static final String CELLID_PROCESS = "CELLID";
    public static final Parcelable.Creator<CameraMetadataNative> CREATOR = new Parcelable.Creator<CameraMetadataNative>() { // from class: android.hardware.camera2.impl.CameraMetadataNative.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraMetadataNative createFromParcel(Parcel in) {
            CameraMetadataNative metadata = new CameraMetadataNative();
            metadata.readFromParcel(in);
            return metadata;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraMetadataNative[] newArray(int size) {
            return new CameraMetadataNative[size];
        }
    };
    private static final boolean DEBUG = false;
    private static final int FACE_LANDMARK_SIZE = 6;
    private static final String GPS_PROCESS = "GPS";
    private static final int MANDATORY_STREAM_CONFIGURATIONS_10BIT = 3;
    private static final int MANDATORY_STREAM_CONFIGURATIONS_CONCURRENT = 2;
    private static final int MANDATORY_STREAM_CONFIGURATIONS_DEFAULT = 0;
    private static final int MANDATORY_STREAM_CONFIGURATIONS_MAX_RESOLUTION = 1;
    private static final int MANDATORY_STREAM_CONFIGURATIONS_PREVIEW_STABILIZATION = 5;
    private static final int MANDATORY_STREAM_CONFIGURATIONS_USE_CASE = 4;
    public static final int NATIVE_JPEG_FORMAT = 33;
    public static final int NUM_TYPES = 6;
    private static final String TAG = "CameraMetadataJV";
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_DOUBLE = 4;
    public static final int TYPE_FLOAT = 2;
    public static final int TYPE_INT32 = 1;
    public static final int TYPE_INT64 = 3;
    public static final int TYPE_RATIONAL = 5;
    private static final HashMap<Key<?>, GetCommand> sGetCommandMap;
    private static final HashMap<Key<?>, SetCommand> sSetCommandMap;
    private long mMetadataPtr;
    private int mCameraId = -1;
    private boolean mHasMandatoryConcurrentStreams = false;
    private Size mDisplaySize = new Size(0, 0);
    private long mBufferSize = 0;
    private MultiResolutionStreamConfigurationMap mMultiResolutionStreamConfigurationMap = null;

    @FastNative
    private static native long nativeAllocate();

    @FastNative
    private static native long nativeAllocateCopy(long j) throws NullPointerException;

    private static native synchronized void nativeClose(long j);

    private static native synchronized void nativeDump(long j) throws IOException;

    private static native synchronized ArrayList nativeGetAllVendorKeys(long j, Class cls);

    private static native synchronized long nativeGetBufferSize(long j);

    private static native synchronized int nativeGetEntryCount(long j);

    @FastNative
    private static native int nativeGetTagFromKey(String str, long j) throws IllegalArgumentException;

    private static native synchronized int nativeGetTagFromKeyLocal(long j, String str) throws IllegalArgumentException;

    @FastNative
    private static native int nativeGetTypeFromTag(int i, long j) throws IllegalArgumentException;

    private static native synchronized int nativeGetTypeFromTagLocal(long j, int i) throws IllegalArgumentException;

    private static native synchronized boolean nativeIsEmpty(long j);

    private static native synchronized void nativeReadFromParcel(Parcel parcel, long j);

    private static native synchronized byte[] nativeReadValues(int i, long j);

    @FastNative
    private static native void nativeSetVendorId(long j, long j2);

    private static native int nativeSetupGlobalVendorTagDescriptor();

    private static native synchronized void nativeSwap(long j, long j2) throws NullPointerException;

    @FastNative
    private static native void nativeUpdate(long j, long j2);

    private static native synchronized void nativeWriteToParcel(Parcel parcel, long j);

    private static native synchronized void nativeWriteValues(int i, byte[] bArr, long j);

    /* loaded from: classes.dex */
    public static class Key<T> {
        private final String mFallbackName;
        private boolean mHasTag;
        private final int mHash;
        private final String mName;
        private int mTag;
        private final Class<T> mType;
        private final TypeReference<T> mTypeReference;
        private long mVendorId;

        public Key(String name, Class<T> type, long vendorId) {
            this.mVendorId = Long.MAX_VALUE;
            if (name == null) {
                throw new NullPointerException("Key needs a valid name");
            }
            if (type == null) {
                throw new NullPointerException("Type needs to be non-null");
            }
            this.mName = name;
            this.mFallbackName = null;
            this.mType = type;
            this.mVendorId = vendorId;
            TypeReference<T> createSpecializedTypeReference = TypeReference.createSpecializedTypeReference((Class) type);
            this.mTypeReference = createSpecializedTypeReference;
            this.mHash = createSpecializedTypeReference.hashCode() ^ name.hashCode();
        }

        public Key(String name, String fallbackName, Class<T> type) {
            this.mVendorId = Long.MAX_VALUE;
            if (name == null) {
                throw new NullPointerException("Key needs a valid name");
            }
            if (type == null) {
                throw new NullPointerException("Type needs to be non-null");
            }
            this.mName = name;
            this.mFallbackName = fallbackName;
            this.mType = type;
            TypeReference<T> createSpecializedTypeReference = TypeReference.createSpecializedTypeReference((Class) type);
            this.mTypeReference = createSpecializedTypeReference;
            this.mHash = createSpecializedTypeReference.hashCode() ^ name.hashCode();
        }

        public Key(String name, Class<T> type) {
            this.mVendorId = Long.MAX_VALUE;
            if (name == null) {
                throw new NullPointerException("Key needs a valid name");
            }
            if (type == null) {
                throw new NullPointerException("Type needs to be non-null");
            }
            this.mName = name;
            this.mFallbackName = null;
            this.mType = type;
            TypeReference<T> createSpecializedTypeReference = TypeReference.createSpecializedTypeReference((Class) type);
            this.mTypeReference = createSpecializedTypeReference;
            this.mHash = createSpecializedTypeReference.hashCode() ^ name.hashCode();
        }

        public Key(String name, TypeReference<T> typeReference) {
            this.mVendorId = Long.MAX_VALUE;
            if (name == null) {
                throw new NullPointerException("Key needs a valid name");
            }
            if (typeReference == null) {
                throw new NullPointerException("TypeReference needs to be non-null");
            }
            this.mName = name;
            this.mFallbackName = null;
            this.mType = (Class<? super T>) typeReference.getRawType();
            this.mTypeReference = typeReference;
            this.mHash = name.hashCode() ^ typeReference.hashCode();
        }

        public final String getName() {
            return this.mName;
        }

        public final int hashCode() {
            return this.mHash;
        }

        public final boolean equals(Object o) {
            Key<?> lhs;
            if (this == o) {
                return true;
            }
            if (o == null || hashCode() != o.hashCode()) {
                return false;
            }
            if (o instanceof CaptureResult.Key) {
                lhs = ((CaptureResult.Key) o).getNativeKey();
            } else if (o instanceof CaptureRequest.Key) {
                lhs = ((CaptureRequest.Key) o).getNativeKey();
            } else if (o instanceof CameraCharacteristics.Key) {
                lhs = ((CameraCharacteristics.Key) o).getNativeKey();
            } else if (!(o instanceof Key)) {
                return false;
            } else {
                lhs = (Key) o;
            }
            if (this.mName.equals(lhs.mName) && this.mTypeReference.equals(lhs.mTypeReference)) {
                return true;
            }
            return false;
        }

        public final int getTag() {
            if (!this.mHasTag) {
                this.mTag = CameraMetadataNative.getTag(this.mName, this.mVendorId);
                this.mHasTag = true;
            }
            return this.mTag;
        }

        public final boolean hasTag() {
            return this.mHasTag;
        }

        public final void cacheTag(int tag) {
            this.mHasTag = true;
            this.mTag = tag;
        }

        public final Class<T> getType() {
            return this.mType;
        }

        public final long getVendorId() {
            return this.mVendorId;
        }

        public final TypeReference<T> getTypeReference() {
            return this.mTypeReference;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static String translateLocationProviderToProcess(String provider) {
        char c;
        if (provider == null) {
            return null;
        }
        switch (provider.hashCode()) {
            case 102570:
                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1843485230:
                if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return GPS_PROCESS;
            case 1:
                return CELLID_PROCESS;
            default:
                return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static String translateProcessToLocationProvider(String process) {
        char c;
        if (process == null) {
            return null;
        }
        switch (process.hashCode()) {
            case 70794:
                if (process.equals(GPS_PROCESS)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1984215549:
                if (process.equals(CELLID_PROCESS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return LocationManager.GPS_PROVIDER;
            case 1:
                return LocationManager.NETWORK_PROVIDER;
            default:
                return null;
        }
    }

    public CameraMetadataNative() {
        long nativeAllocate = nativeAllocate();
        this.mMetadataPtr = nativeAllocate;
        if (nativeAllocate == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
        updateNativeAllocation();
    }

    public CameraMetadataNative(CameraMetadataNative other) {
        long nativeAllocateCopy = nativeAllocateCopy(other.mMetadataPtr);
        this.mMetadataPtr = nativeAllocateCopy;
        if (nativeAllocateCopy == 0) {
            throw new OutOfMemoryError("Failed to allocate native CameraMetadata");
        }
        updateNativeAllocation();
    }

    public static CameraMetadataNative move(CameraMetadataNative other) {
        CameraMetadataNative newObject = new CameraMetadataNative();
        newObject.swap(other);
        return newObject;
    }

    public static void update(CameraMetadataNative dst, CameraMetadataNative src) {
        nativeUpdate(dst.mMetadataPtr, src.mMetadataPtr);
    }

    static {
        HashMap<Key<?>, GetCommand> hashMap = new HashMap<>();
        sGetCommandMap = hashMap;
        hashMap.put(CameraCharacteristics.SCALER_AVAILABLE_FORMATS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.2
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getAvailableFormats();
            }
        });
        hashMap.put(CaptureResult.STATISTICS_FACES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.3
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getFaces();
            }
        });
        hashMap.put(CaptureResult.STATISTICS_FACE_RECTANGLES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.4
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getFaceRectangles();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.5
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getStreamConfigurationMap();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.6
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getStreamConfigurationMapMaximumResolution();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MANDATORY_STREAM_COMBINATIONS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.7
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatoryStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MANDATORY_CONCURRENT_STREAM_COMBINATIONS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.8
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatoryConcurrentStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MANDATORY_TEN_BIT_OUTPUT_STREAM_COMBINATIONS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.9
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatory10BitStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MANDATORY_MAXIMUM_RESOLUTION_STREAM_COMBINATIONS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.10
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatoryMaximumResolutionStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MANDATORY_USE_CASE_STREAM_COMBINATIONS.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.11
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatoryUseCaseStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.f104x783e32d9.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.12
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMandatoryPreviewStabilizationStreamCombinations();
            }
        });
        hashMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AE.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.13
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxRegions(key);
            }
        });
        hashMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.14
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxRegions(key);
            }
        });
        hashMap.put(CameraCharacteristics.CONTROL_MAX_REGIONS_AF.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.15
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxRegions(key);
            }
        });
        hashMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.16
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxNumOutputs(key);
            }
        });
        hashMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.17
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxNumOutputs(key);
            }
        });
        hashMap.put(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.18
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMaxNumOutputs(key);
            }
        });
        hashMap.put(CaptureRequest.TONEMAP_CURVE.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.19
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getTonemapCurve();
            }
        });
        hashMap.put(CaptureResult.JPEG_GPS_LOCATION.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.20
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getGpsLocation();
            }
        });
        hashMap.put(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.21
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getLensShadingMap();
            }
        });
        hashMap.put(CameraCharacteristics.INFO_DEVICE_STATE_SENSOR_ORIENTATION_MAP.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.22
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getDeviceStateOrientationMap();
            }
        });
        hashMap.put(CameraCharacteristics.REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.23
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getDynamicRangeProfiles();
            }
        });
        hashMap.put(CameraCharacteristics.REQUEST_AVAILABLE_COLOR_SPACE_PROFILES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.24
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getColorSpaceProfiles();
            }
        });
        hashMap.put(CaptureResult.STATISTICS_OIS_SAMPLES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.25
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getOisSamples();
            }
        });
        hashMap.put(CameraCharacteristics.CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_CAPABILITIES.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.26
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getExtendedSceneModeCapabilities();
            }
        });
        hashMap.put(CameraCharacteristics.SCALER_MULTI_RESOLUTION_STREAM_CONFIGURATION_MAP.getNativeKey(), new GetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.27
            @Override // android.hardware.camera2.impl.GetCommand
            public <T> T getValue(CameraMetadataNative metadata, Key<T> key) {
                return (T) metadata.getMultiResolutionStreamConfigurationMap();
            }
        });
        HashMap<Key<?>, SetCommand> hashMap2 = new HashMap<>();
        sSetCommandMap = hashMap2;
        hashMap2.put(CameraCharacteristics.SCALER_AVAILABLE_FORMATS.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.28
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setAvailableFormats((int[]) value);
            }
        });
        hashMap2.put(CaptureResult.STATISTICS_FACE_RECTANGLES.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.29
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setFaceRectangles((Rect[]) value);
            }
        });
        hashMap2.put(CaptureResult.STATISTICS_FACES.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.30
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setFaces((Face[]) value);
            }
        });
        hashMap2.put(CaptureRequest.TONEMAP_CURVE.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.31
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setTonemapCurve((TonemapCurve) value);
            }
        });
        hashMap2.put(CaptureResult.JPEG_GPS_LOCATION.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.32
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setGpsLocation((Location) value);
            }
        });
        hashMap2.put(CaptureRequest.SCALER_CROP_REGION.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.33
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setScalerCropRegion((Rect) value);
            }
        });
        hashMap2.put(CaptureRequest.CONTROL_AWB_REGIONS.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.34
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setAWBRegions(value);
            }
        });
        hashMap2.put(CaptureRequest.CONTROL_AF_REGIONS.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.35
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setAFRegions(value);
            }
        });
        hashMap2.put(CaptureRequest.CONTROL_AE_REGIONS.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.36
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setAERegions(value);
            }
        });
        hashMap2.put(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP.getNativeKey(), new SetCommand() { // from class: android.hardware.camera2.impl.CameraMetadataNative.37
            @Override // android.hardware.camera2.impl.SetCommand
            public <T> void setValue(CameraMetadataNative metadata, T value) {
                metadata.setLensShadingMap((LensShadingMap) value);
            }
        });
        registerAllMarshalers();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        nativeWriteToParcel(dest, this.mMetadataPtr);
    }

    public <T> T get(CameraCharacteristics.Key<T> key) {
        return (T) get(key.getNativeKey());
    }

    public <T> T get(CaptureResult.Key<T> key) {
        return (T) get(key.getNativeKey());
    }

    public <T> T get(CaptureRequest.Key<T> key) {
        return (T) get(key.getNativeKey());
    }

    public <T> T get(Key<T> key) {
        Objects.requireNonNull(key, "key must not be null");
        GetCommand g = sGetCommandMap.get(key);
        if (g != null) {
            return (T) g.getValue(this, key);
        }
        return (T) getBase(key);
    }

    public void readFromParcel(Parcel in) {
        nativeReadFromParcel(in, this.mMetadataPtr);
        updateNativeAllocation();
    }

    public static void setupGlobalVendorTagDescriptor() throws ServiceSpecificException {
        int err = nativeSetupGlobalVendorTagDescriptor();
        if (err != 0) {
            throw new ServiceSpecificException(err, "Failure to set up global vendor tags");
        }
    }

    public <T> void set(Key<T> key, T value) {
        SetCommand s = sSetCommandMap.get(key);
        if (s != null) {
            s.setValue(this, value);
        } else {
            setBase((Key<Key<T>>) key, (Key<T>) value);
        }
    }

    public <T> void set(CaptureRequest.Key<T> key, T value) {
        set((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    public <T> void set(CaptureResult.Key<T> key, T value) {
        set((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    public <T> void set(CameraCharacteristics.Key<T> key, T value) {
        set((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    private void close() {
        nativeClose(this.mMetadataPtr);
        this.mMetadataPtr = 0L;
        if (this.mBufferSize > 0) {
            VMRuntime.getRuntime().registerNativeFree(this.mBufferSize);
        }
        this.mBufferSize = 0L;
    }

    private <T> T getBase(CameraCharacteristics.Key<T> key) {
        return (T) getBase(key.getNativeKey());
    }

    private <T> T getBase(CaptureResult.Key<T> key) {
        return (T) getBase(key.getNativeKey());
    }

    private <T> T getBase(CaptureRequest.Key<T> key) {
        return (T) getBase(key.getNativeKey());
    }

    private <T> T getBase(Key<T> key) {
        int tag;
        if (key.hasTag()) {
            tag = key.getTag();
        } else {
            tag = nativeGetTagFromKeyLocal(this.mMetadataPtr, key.getName());
            key.cacheTag(tag);
        }
        byte[] values = readValues(tag);
        if (values == null && (((Key) key).mFallbackName == null || (values = readValues((tag = nativeGetTagFromKeyLocal(this.mMetadataPtr, ((Key) key).mFallbackName)))) == null)) {
            return null;
        }
        int nativeType = nativeGetTypeFromTagLocal(this.mMetadataPtr, tag);
        Marshaler<T> marshaler = getMarshalerForKey(key, nativeType);
        ByteBuffer buffer = ByteBuffer.wrap(values).order(ByteOrder.nativeOrder());
        return marshaler.unmarshal(buffer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] getAvailableFormats() {
        int[] availableFormats = (int[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_FORMATS);
        if (availableFormats != null) {
            for (int i = 0; i < availableFormats.length; i++) {
                if (availableFormats[i] == 33) {
                    availableFormats[i] = 256;
                }
            }
        }
        return availableFormats;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setFaces(Face[] faces) {
        if (faces == null) {
            return false;
        }
        int numFaces = faces.length;
        boolean fullMode = true;
        for (Face face : faces) {
            if (face == null) {
                numFaces--;
                Log.m104w(TAG, "setFaces - null face detected, skipping");
            } else if (face.getId() == -1) {
                fullMode = false;
            }
        }
        Rect[] faceRectangles = new Rect[numFaces];
        byte[] faceScores = new byte[numFaces];
        int[] faceIds = null;
        int[] faceLandmarks = null;
        if (fullMode) {
            faceIds = new int[numFaces];
            faceLandmarks = new int[numFaces * 6];
        }
        int i = 0;
        for (Face face2 : faces) {
            if (face2 != null) {
                faceRectangles[i] = face2.getBounds();
                faceScores[i] = (byte) face2.getScore();
                if (fullMode) {
                    faceIds[i] = face2.getId();
                    int j = 0 + 1;
                    faceLandmarks[(i * 6) + 0] = face2.getLeftEyePosition().f76x;
                    int j2 = j + 1;
                    faceLandmarks[(i * 6) + j] = face2.getLeftEyePosition().f77y;
                    int j3 = j2 + 1;
                    faceLandmarks[(i * 6) + j2] = face2.getRightEyePosition().f76x;
                    int j4 = j3 + 1;
                    faceLandmarks[(i * 6) + j3] = face2.getRightEyePosition().f77y;
                    int j5 = j4 + 1;
                    faceLandmarks[(i * 6) + j4] = face2.getMouthPosition().f76x;
                    int i2 = j5 + 1;
                    faceLandmarks[(i * 6) + j5] = face2.getMouthPosition().f77y;
                }
                i++;
            }
        }
        set((CaptureResult.Key<CaptureResult.Key<Rect[]>>) CaptureResult.STATISTICS_FACE_RECTANGLES, (CaptureResult.Key<Rect[]>) faceRectangles);
        set((CaptureResult.Key<CaptureResult.Key<int[]>>) CaptureResult.STATISTICS_FACE_IDS, (CaptureResult.Key<int[]>) faceIds);
        set((CaptureResult.Key<CaptureResult.Key<int[]>>) CaptureResult.STATISTICS_FACE_LANDMARKS, (CaptureResult.Key<int[]>) faceLandmarks);
        set((CaptureResult.Key<CaptureResult.Key<byte[]>>) CaptureResult.STATISTICS_FACE_SCORES, (CaptureResult.Key<byte[]>) faceScores);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Face[] getFaces() {
        Integer faceDetectMode = (Integer) get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
        byte[] faceScores = (byte[]) get(CaptureResult.STATISTICS_FACE_SCORES);
        Rect[] faceRectangles = (Rect[]) get(CaptureResult.STATISTICS_FACE_RECTANGLES);
        int[] faceIds = (int[]) get(CaptureResult.STATISTICS_FACE_IDS);
        int[] faceLandmarks = (int[]) get(CaptureResult.STATISTICS_FACE_LANDMARKS);
        if (areValuesAllNull(faceDetectMode, faceScores, faceRectangles, faceIds, faceLandmarks)) {
            return null;
        }
        if (faceDetectMode == null) {
            Log.m104w(TAG, "Face detect mode metadata is null, assuming the mode is SIMPLE");
            faceDetectMode = 1;
        } else if (faceDetectMode.intValue() > 2) {
            faceDetectMode = 2;
        } else if (faceDetectMode.intValue() == 0) {
            return new Face[0];
        } else {
            if (faceDetectMode.intValue() != 1 && faceDetectMode.intValue() != 2) {
                Log.m104w(TAG, "Unknown face detect mode: " + faceDetectMode);
                return new Face[0];
            }
        }
        if (faceScores == null || faceRectangles == null) {
            Log.m104w(TAG, "Expect face scores and rectangles to be non-null");
            return new Face[0];
        }
        if (faceScores.length != faceRectangles.length) {
            Log.m104w(TAG, String.format("Face score size(%d) doesn match face rectangle size(%d)!", Integer.valueOf(faceScores.length), Integer.valueOf(faceRectangles.length)));
        }
        int numFaces = Math.min(faceScores.length, faceRectangles.length);
        if (faceDetectMode.intValue() == 2) {
            if (faceIds == null || faceLandmarks == null) {
                Log.m104w(TAG, "Expect face ids and landmarks to be non-null for FULL mode,fallback to SIMPLE mode");
                faceDetectMode = 1;
            } else {
                if (faceIds.length != numFaces || faceLandmarks.length != numFaces * 6) {
                    Log.m104w(TAG, String.format("Face id size(%d), or face landmark size(%d) don'tmatch face number(%d)!", Integer.valueOf(faceIds.length), Integer.valueOf(faceLandmarks.length * 6), Integer.valueOf(numFaces)));
                }
                numFaces = Math.min(Math.min(numFaces, faceIds.length), faceLandmarks.length / 6);
            }
        }
        ArrayList<Face> faceList = new ArrayList<>();
        if (faceDetectMode.intValue() == 1) {
            for (int i = 0; i < numFaces; i++) {
                if (faceScores[i] <= 100 && faceScores[i] >= 1) {
                    faceList.add(new Face(faceRectangles[i], faceScores[i]));
                }
            }
        } else {
            for (int i2 = 0; i2 < numFaces; i2++) {
                if (faceScores[i2] <= 100 && faceScores[i2] >= 1 && faceIds[i2] >= 0) {
                    Point leftEye = new Point(faceLandmarks[i2 * 6], faceLandmarks[(i2 * 6) + 1]);
                    Point rightEye = new Point(faceLandmarks[(i2 * 6) + 2], faceLandmarks[(i2 * 6) + 3]);
                    Point mouth = new Point(faceLandmarks[(i2 * 6) + 4], faceLandmarks[(i2 * 6) + 5]);
                    Face face = new Face(faceRectangles[i2], faceScores[i2], faceIds[i2], leftEye, rightEye, mouth);
                    faceList.add(face);
                }
            }
        }
        Face[] faces = new Face[faceList.size()];
        faceList.toArray(faces);
        return faces;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Rect[] getFaceRectangles() {
        Rect[] faceRectangles = (Rect[]) getBase(CaptureResult.STATISTICS_FACE_RECTANGLES);
        if (faceRectangles == null) {
            return null;
        }
        Rect[] fixedFaceRectangles = new Rect[faceRectangles.length];
        for (int i = 0; i < faceRectangles.length; i++) {
            fixedFaceRectangles[i] = new Rect(faceRectangles[i].left, faceRectangles[i].top, faceRectangles[i].right - faceRectangles[i].left, faceRectangles[i].bottom - faceRectangles[i].top);
        }
        return fixedFaceRectangles;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setLensShadingMap(LensShadingMap lensShadingMap) {
        if (lensShadingMap == null) {
            return false;
        }
        float[] lsmArray = new float[lensShadingMap.getGainFactorCount()];
        lensShadingMap.copyGainFactors(lsmArray, 0);
        setBase((CaptureResult.Key<CaptureResult.Key<float[]>>) CaptureResult.STATISTICS_LENS_SHADING_MAP, (CaptureResult.Key<float[]>) lsmArray);
        Size s = new Size(lensShadingMap.getRowCount(), lensShadingMap.getColumnCount());
        setBase((CameraCharacteristics.Key<CameraCharacteristics.Key<Size>>) CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE, (CameraCharacteristics.Key<Size>) s);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public LensShadingMap getLensShadingMap() {
        float[] lsmArray = (float[]) getBase(CaptureResult.STATISTICS_LENS_SHADING_MAP);
        Size s = (Size) get(CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE);
        if (lsmArray == null) {
            return null;
        }
        if (s == null) {
            Log.m104w(TAG, "getLensShadingMap - Lens shading map size was null.");
            return null;
        }
        LensShadingMap map = new LensShadingMap(lsmArray, s.getHeight(), s.getWidth());
        return map;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DeviceStateSensorOrientationMap getDeviceStateOrientationMap() {
        long[] mapArray = (long[]) getBase(CameraCharacteristics.INFO_DEVICE_STATE_ORIENTATIONS);
        if (mapArray == null) {
            return null;
        }
        DeviceStateSensorOrientationMap map = new DeviceStateSensorOrientationMap(mapArray);
        return map;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DynamicRangeProfiles getDynamicRangeProfiles() {
        long[] profileArray = (long[]) getBase(CameraCharacteristics.REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP);
        if (profileArray == null) {
            return null;
        }
        return new DynamicRangeProfiles(profileArray);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ColorSpaceProfiles getColorSpaceProfiles() {
        long[] profileArray = (long[]) getBase(CameraCharacteristics.REQUEST_AVAILABLE_COLOR_SPACE_PROFILES_MAP);
        if (profileArray == null) {
            return null;
        }
        return new ColorSpaceProfiles(profileArray);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Location getGpsLocation() {
        String processingMethod = (String) get(CaptureResult.JPEG_GPS_PROCESSING_METHOD);
        double[] coords = (double[]) get(CaptureResult.JPEG_GPS_COORDINATES);
        Long timeStamp = (Long) get(CaptureResult.JPEG_GPS_TIMESTAMP);
        if (areValuesAllNull(processingMethod, coords, timeStamp)) {
            return null;
        }
        Location l = new Location(translateProcessToLocationProvider(processingMethod));
        if (timeStamp == null) {
            Log.m104w(TAG, "getGpsLocation - No timestamp for GPS location.");
        } else {
            l.setTime(timeStamp.longValue() * 1000);
        }
        if (coords == null) {
            Log.m104w(TAG, "getGpsLocation - No coordinates for GPS location");
        } else {
            l.setLatitude(coords[0]);
            l.setLongitude(coords[1]);
            l.setAltitude(coords[2]);
        }
        return l;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setGpsLocation(Location l) {
        if (l == null) {
            setBase((CaptureRequest.Key<CaptureRequest.Key<Long>>) CaptureRequest.JPEG_GPS_TIMESTAMP, (CaptureRequest.Key<Long>) null);
            setBase((CaptureRequest.Key<CaptureRequest.Key<double[]>>) CaptureRequest.JPEG_GPS_COORDINATES, (CaptureRequest.Key<double[]>) null);
            setBase((CaptureRequest.Key<CaptureRequest.Key<String>>) CaptureRequest.JPEG_GPS_PROCESSING_METHOD, (CaptureRequest.Key<String>) null);
            return false;
        }
        double[] coords = {l.getLatitude(), l.getLongitude(), l.getAltitude()};
        String processMethod = translateLocationProviderToProcess(l.getProvider());
        long timestamp = l.getTime() / 1000;
        set((CaptureRequest.Key<CaptureRequest.Key<Long>>) CaptureRequest.JPEG_GPS_TIMESTAMP, (CaptureRequest.Key<Long>) Long.valueOf(timestamp));
        set((CaptureRequest.Key<CaptureRequest.Key<double[]>>) CaptureRequest.JPEG_GPS_COORDINATES, (CaptureRequest.Key<double[]>) coords);
        if (processMethod == null) {
            Log.m104w(TAG, "setGpsLocation - No process method, Location is not from a GPS or NETWORKprovider");
        } else {
            setBase((CaptureRequest.Key<CaptureRequest.Key<String>>) CaptureRequest.JPEG_GPS_PROCESSING_METHOD, (CaptureRequest.Key<String>) processMethod);
        }
        return true;
    }

    private void parseRecommendedConfigurations(RecommendedStreamConfiguration[] configurations, StreamConfigurationMap fullMap, boolean isDepth, ArrayList<ArrayList<StreamConfiguration>> streamConfigList, ArrayList<ArrayList<StreamConfigurationDuration>> streamDurationList, ArrayList<ArrayList<StreamConfigurationDuration>> streamStallList, boolean[] supportsPrivate) {
        int i;
        char c;
        int usecaseBitmap;
        Size sz;
        int publicFormat;
        int internalFormat;
        int width;
        int height;
        StreamConfigurationDuration minDurationConfiguration;
        StreamConfigurationDuration minDurationConfiguration2;
        StreamConfigurationDuration stallDurationConfiguration;
        RecommendedStreamConfiguration[] recommendedStreamConfigurationArr = configurations;
        streamConfigList.ensureCapacity(32);
        streamDurationList.ensureCapacity(32);
        streamStallList.ensureCapacity(32);
        for (int i2 = 0; i2 < 32; i2++) {
            streamConfigList.add(new ArrayList<>());
            streamDurationList.add(new ArrayList<>());
            streamStallList.add(new ArrayList<>());
        }
        int publicFormat2 = recommendedStreamConfigurationArr.length;
        boolean z = false;
        int i3 = 0;
        while (i3 < publicFormat2) {
            RecommendedStreamConfiguration c2 = recommendedStreamConfigurationArr[i3];
            int width2 = c2.getWidth();
            int height2 = c2.getHeight();
            int internalFormat2 = c2.getFormat();
            int publicFormat3 = isDepth ? StreamConfigurationMap.depthFormatToPublic(internalFormat2) : StreamConfigurationMap.imageFormatToPublic(internalFormat2);
            Size sz2 = new Size(width2, height2);
            int usecaseBitmap2 = c2.getUsecaseBitmap();
            if (!c2.isInput()) {
                StreamConfiguration streamConfiguration = new StreamConfiguration(internalFormat2, width2, height2, z);
                long minFrameDuration = fullMap.getOutputMinFrameDuration(publicFormat3, sz2);
                if (minFrameDuration <= 0) {
                    i = publicFormat2;
                    usecaseBitmap = usecaseBitmap2;
                    sz = sz2;
                    publicFormat = publicFormat3;
                    internalFormat = internalFormat2;
                    width = width2;
                    height = height2;
                    minDurationConfiguration = null;
                } else {
                    usecaseBitmap = usecaseBitmap2;
                    sz = sz2;
                    i = publicFormat2;
                    publicFormat = publicFormat3;
                    internalFormat = internalFormat2;
                    width = width2;
                    height = height2;
                    StreamConfigurationDuration minDurationConfiguration3 = new StreamConfigurationDuration(internalFormat2, width2, height2, minFrameDuration);
                    minDurationConfiguration = minDurationConfiguration3;
                }
                long stallDuration = fullMap.getOutputStallDuration(publicFormat, sz);
                if (stallDuration <= 0) {
                    minDurationConfiguration2 = minDurationConfiguration;
                    stallDurationConfiguration = null;
                } else {
                    minDurationConfiguration2 = minDurationConfiguration;
                    StreamConfigurationDuration stallDurationConfiguration2 = new StreamConfigurationDuration(internalFormat, width, height, stallDuration);
                    stallDurationConfiguration = stallDurationConfiguration2;
                }
                int i4 = 0;
                while (true) {
                    c = ' ';
                    if (i4 < 32) {
                        if ((usecaseBitmap & (1 << i4)) != 0) {
                            ArrayList<StreamConfiguration> sc = streamConfigList.get(i4);
                            sc.add(streamConfiguration);
                            if (minFrameDuration > 0) {
                                ArrayList<StreamConfigurationDuration> scd = streamDurationList.get(i4);
                                scd.add(minDurationConfiguration2);
                            }
                            if (stallDuration > 0) {
                                ArrayList<StreamConfigurationDuration> scs = streamStallList.get(i4);
                                scs.add(stallDurationConfiguration);
                            }
                            if (supportsPrivate != null && !supportsPrivate[i4] && publicFormat == 34) {
                                supportsPrivate[i4] = true;
                            }
                        }
                        i4++;
                    }
                }
            } else {
                i = publicFormat2;
                c = ' ';
                if (usecaseBitmap2 != 16) {
                    throw new IllegalArgumentException("Recommended input stream configurations should only be advertised in the ZSL use case!");
                }
                ArrayList<StreamConfiguration> sc2 = streamConfigList.get(4);
                sc2.add(new StreamConfiguration(internalFormat2, width2, height2, true));
            }
            i3++;
            recommendedStreamConfigurationArr = configurations;
            publicFormat2 = i;
            z = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class StreamConfigurationData {
        StreamConfigurationDuration[] minDurationArray;
        StreamConfigurationDuration[] stallDurationArray;
        StreamConfiguration[] streamConfigurationArray;

        private StreamConfigurationData() {
            this.streamConfigurationArray = null;
            this.minDurationArray = null;
            this.stallDurationArray = null;
        }
    }

    public void initializeStreamConfigurationData(ArrayList<StreamConfiguration> sc, ArrayList<StreamConfigurationDuration> scd, ArrayList<StreamConfigurationDuration> scs, StreamConfigurationData scData) {
        if (scData == null || sc == null) {
            return;
        }
        scData.streamConfigurationArray = new StreamConfiguration[sc.size()];
        scData.streamConfigurationArray = (StreamConfiguration[]) sc.toArray(scData.streamConfigurationArray);
        if (scd != null && !scd.isEmpty()) {
            scData.minDurationArray = new StreamConfigurationDuration[scd.size()];
            scData.minDurationArray = (StreamConfigurationDuration[]) scd.toArray(scData.minDurationArray);
        } else {
            scData.minDurationArray = new StreamConfigurationDuration[0];
        }
        if (scs != null && !scs.isEmpty()) {
            scData.stallDurationArray = new StreamConfigurationDuration[scs.size()];
            scData.stallDurationArray = (StreamConfigurationDuration[]) scs.toArray(scData.stallDurationArray);
            return;
        }
        scData.stallDurationArray = new StreamConfigurationDuration[0];
    }

    public ArrayList<RecommendedStreamConfigurationMap> getRecommendedStreamConfigurations() {
        String str;
        boolean[] supportsPrivate;
        ArrayList<ArrayList<StreamConfigurationDuration>> streamStallList;
        ArrayList<ArrayList<StreamConfigurationDuration>> streamDurationList;
        ArrayList<ArrayList<StreamConfigurationDuration>> depthStreamStallList;
        ArrayList<ArrayList<StreamConfigurationDuration>> depthStreamDurationList;
        ArrayList<ArrayList<StreamConfiguration>> depthStreamConfigList;
        ArrayList<ArrayList<StreamConfigurationDuration>> streamStallList2;
        ArrayList<ArrayList<StreamConfigurationDuration>> streamDurationList2;
        ArrayList<ArrayList<StreamConfigurationDuration>> streamDurationList3;
        StreamConfigurationMap map;
        RecommendedStreamConfiguration[] configurations = (RecommendedStreamConfiguration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_RECOMMENDED_STREAM_CONFIGURATIONS);
        RecommendedStreamConfiguration[] depthConfigurations = (RecommendedStreamConfiguration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_RECOMMENDED_DEPTH_STREAM_CONFIGURATIONS);
        if (configurations == null && depthConfigurations == null) {
            return null;
        }
        StreamConfigurationMap fullMap = getStreamConfigurationMap();
        ArrayList<RecommendedStreamConfigurationMap> recommendedConfigurations = new ArrayList<>();
        ArrayList<ArrayList<StreamConfiguration>> streamConfigList = new ArrayList<>();
        ArrayList<ArrayList<StreamConfigurationDuration>> streamDurationList4 = new ArrayList<>();
        ArrayList<ArrayList<StreamConfigurationDuration>> streamStallList3 = new ArrayList<>();
        boolean[] supportsPrivate2 = new boolean[32];
        if (configurations != null) {
            str = TAG;
            supportsPrivate = supportsPrivate2;
            streamStallList = streamStallList3;
            streamDurationList = streamDurationList4;
            try {
                parseRecommendedConfigurations(configurations, fullMap, false, streamConfigList, streamDurationList4, streamStallList3, supportsPrivate);
            } catch (IllegalArgumentException e) {
                Log.m110e(str, "Failed parsing the recommended stream configurations!");
                return null;
            }
        } else {
            str = TAG;
            supportsPrivate = supportsPrivate2;
            streamStallList = streamStallList3;
            streamDurationList = streamDurationList4;
        }
        ArrayList<ArrayList<StreamConfiguration>> depthStreamConfigList2 = new ArrayList<>();
        ArrayList<ArrayList<StreamConfigurationDuration>> depthStreamDurationList2 = new ArrayList<>();
        ArrayList<ArrayList<StreamConfigurationDuration>> depthStreamStallList2 = new ArrayList<>();
        if (depthConfigurations == null) {
            depthStreamStallList = depthStreamStallList2;
            depthStreamDurationList = depthStreamDurationList2;
            depthStreamConfigList = depthStreamConfigList2;
        } else {
            depthStreamStallList = depthStreamStallList2;
            depthStreamDurationList = depthStreamDurationList2;
            depthStreamConfigList = depthStreamConfigList2;
            try {
                parseRecommendedConfigurations(depthConfigurations, fullMap, true, depthStreamConfigList2, depthStreamDurationList2, depthStreamStallList, null);
            } catch (IllegalArgumentException e2) {
                Log.m110e(str, "Failed parsing the recommended depth stream configurations!");
                return null;
            }
        }
        ReprocessFormatsMap inputOutputFormatsMap = (ReprocessFormatsMap) getBase(CameraCharacteristics.SCALER_AVAILABLE_RECOMMENDED_INPUT_OUTPUT_FORMATS_MAP);
        HighSpeedVideoConfiguration[] highSpeedVideoConfigurations = (HighSpeedVideoConfiguration[]) getBase(CameraCharacteristics.CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS);
        boolean listHighResolution = isBurstSupported();
        recommendedConfigurations.ensureCapacity(32);
        int i = 0;
        for (int i2 = 32; i < i2; i2 = 32) {
            StreamConfigurationData scData = new StreamConfigurationData();
            if (configurations == null) {
                streamStallList2 = streamStallList;
                streamDurationList2 = streamDurationList;
            } else {
                streamDurationList2 = streamDurationList;
                streamStallList2 = streamStallList;
                initializeStreamConfigurationData(streamConfigList.get(i), streamDurationList2.get(i), streamStallList2.get(i), scData);
            }
            StreamConfigurationData depthScData = new StreamConfigurationData();
            if (depthConfigurations == null) {
                streamDurationList3 = streamDurationList2;
            } else {
                streamDurationList3 = streamDurationList2;
                initializeStreamConfigurationData(depthStreamConfigList.get(i), depthStreamDurationList.get(i), depthStreamStallList.get(i), depthScData);
            }
            if ((scData.streamConfigurationArray == null || scData.streamConfigurationArray.length == 0) && (depthScData.streamConfigurationArray == null || depthScData.streamConfigurationArray.length == 0)) {
                recommendedConfigurations.add(null);
            } else {
                switch (i) {
                    case 0:
                    case 2:
                    case 5:
                    case 6:
                        map = new StreamConfigurationMap(scData.streamConfigurationArray, scData.minDurationArray, scData.stallDurationArray, null, null, null, null, null, null, null, null, null, null, null, null, null, null, listHighResolution, supportsPrivate[i]);
                        break;
                    case 1:
                        map = new StreamConfigurationMap(scData.streamConfigurationArray, scData.minDurationArray, scData.stallDurationArray, null, null, null, null, null, null, null, null, null, null, null, null, highSpeedVideoConfigurations, null, listHighResolution, supportsPrivate[i]);
                        break;
                    case 3:
                    default:
                        map = new StreamConfigurationMap(scData.streamConfigurationArray, scData.minDurationArray, scData.stallDurationArray, depthScData.streamConfigurationArray, depthScData.minDurationArray, depthScData.stallDurationArray, null, null, null, null, null, null, null, null, null, null, null, listHighResolution, supportsPrivate[i]);
                        break;
                    case 4:
                        map = new StreamConfigurationMap(scData.streamConfigurationArray, scData.minDurationArray, scData.stallDurationArray, depthScData.streamConfigurationArray, depthScData.minDurationArray, depthScData.stallDurationArray, null, null, null, null, null, null, null, null, null, null, inputOutputFormatsMap, listHighResolution, supportsPrivate[i]);
                        break;
                }
                recommendedConfigurations.add(new RecommendedStreamConfigurationMap(map, i, supportsPrivate[i]));
            }
            i++;
            streamStallList = streamStallList2;
            streamDurationList = streamDurationList3;
        }
        return recommendedConfigurations;
    }

    private boolean isCapabilitySupported(int capabilityRequested) {
        int[] capabilities = (int[]) getBase(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        for (int capability : capabilities) {
            if (capabilityRequested == capability) {
                return true;
            }
        }
        return false;
    }

    public boolean isUltraHighResolutionSensor() {
        return isCapabilitySupported(16);
    }

    private boolean isBurstSupported() {
        return isCapabilitySupported(6);
    }

    private boolean isPreviewStabilizationSupported() {
        int[] videoStabilizationModes = (int[]) getBase(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
        if (videoStabilizationModes == null) {
            return false;
        }
        for (int mode : videoStabilizationModes) {
            if (mode == 2) {
                return true;
            }
        }
        return false;
    }

    private boolean isCroppedRawSupported() {
        long[] streamUseCases = (long[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_STREAM_USE_CASES);
        if (streamUseCases == null) {
            return false;
        }
        for (long useCase : streamUseCases) {
            if (useCase == 6) {
                return true;
            }
        }
        return false;
    }

    private MandatoryStreamCombination[] getMandatoryStreamCombinationsHelper(int mandatoryStreamsType) {
        List<MandatoryStreamCombination> combs;
        int[] capabilities = (int[]) getBase(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        ArrayList<Integer> caps = new ArrayList<>();
        caps.ensureCapacity(capabilities.length);
        for (int c : capabilities) {
            caps.add(new Integer(c));
        }
        int hwLevel = ((Integer) getBase(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue();
        MandatoryStreamCombination.Builder build = new MandatoryStreamCombination.Builder(this.mCameraId, hwLevel, this.mDisplaySize, caps, getStreamConfigurationMap(), getStreamConfigurationMapMaximumResolution(), isPreviewStabilizationSupported(), isCroppedRawSupported());
        switch (mandatoryStreamsType) {
            case 1:
                combs = build.getAvailableMandatoryMaximumResolutionStreamCombinations();
                break;
            case 2:
                combs = build.getAvailableMandatoryConcurrentStreamCombinations();
                break;
            case 3:
                combs = build.getAvailableMandatory10BitStreamCombinations();
                break;
            case 4:
                combs = build.getAvailableMandatoryStreamUseCaseCombinations();
                break;
            case 5:
                combs = build.getAvailableMandatoryPreviewStabilizedStreamCombinations();
                break;
            default:
                combs = build.getAvailableMandatoryStreamCombinations();
                break;
        }
        if (combs != null && !combs.isEmpty()) {
            MandatoryStreamCombination[] combArray = new MandatoryStreamCombination[combs.size()];
            return (MandatoryStreamCombination[]) combs.toArray(combArray);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatory10BitStreamCombinations() {
        return getMandatoryStreamCombinationsHelper(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatoryConcurrentStreamCombinations() {
        if (!this.mHasMandatoryConcurrentStreams) {
            return null;
        }
        return getMandatoryStreamCombinationsHelper(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatoryMaximumResolutionStreamCombinations() {
        if (!isUltraHighResolutionSensor()) {
            return null;
        }
        return getMandatoryStreamCombinationsHelper(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatoryStreamCombinations() {
        return getMandatoryStreamCombinationsHelper(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatoryUseCaseStreamCombinations() {
        return getMandatoryStreamCombinationsHelper(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MandatoryStreamCombination[] getMandatoryPreviewStabilizationStreamCombinations() {
        return getMandatoryStreamCombinationsHelper(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public StreamConfigurationMap getStreamConfigurationMap() {
        StreamConfiguration[] configurations = (StreamConfiguration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_STREAM_CONFIGURATIONS);
        StreamConfigurationDuration[] minFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_MIN_FRAME_DURATIONS);
        StreamConfigurationDuration[] stallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_STALL_DURATIONS);
        StreamConfiguration[] depthConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS);
        StreamConfigurationDuration[] depthMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_MIN_FRAME_DURATIONS);
        StreamConfigurationDuration[] depthStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STALL_DURATIONS);
        StreamConfiguration[] dynamicDepthConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DYNAMIC_DEPTH_STREAM_CONFIGURATIONS);
        StreamConfigurationDuration[] dynamicDepthMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DYNAMIC_DEPTH_MIN_FRAME_DURATIONS);
        StreamConfigurationDuration[] dynamicDepthStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DYNAMIC_DEPTH_STALL_DURATIONS);
        StreamConfiguration[] heicConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_STREAM_CONFIGURATIONS);
        StreamConfigurationDuration[] heicMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_MIN_FRAME_DURATIONS);
        StreamConfigurationDuration[] heicStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_STALL_DURATIONS);
        StreamConfiguration[] jpegRConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_STREAM_CONFIGURATIONS);
        StreamConfigurationDuration[] jpegRMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_MIN_FRAME_DURATIONS);
        StreamConfigurationDuration[] jpegRStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_STALL_DURATIONS);
        HighSpeedVideoConfiguration[] highSpeedVideoConfigurations = (HighSpeedVideoConfiguration[]) getBase(CameraCharacteristics.CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS);
        ReprocessFormatsMap inputOutputFormatsMap = (ReprocessFormatsMap) getBase(CameraCharacteristics.SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP);
        boolean listHighResolution = isBurstSupported();
        return new StreamConfigurationMap(configurations, minFrameDurations, stallDurations, depthConfigurations, depthMinFrameDurations, depthStallDurations, dynamicDepthConfigurations, dynamicDepthMinFrameDurations, dynamicDepthStallDurations, heicConfigurations, heicMinFrameDurations, heicStallDurations, jpegRConfigurations, jpegRMinFrameDurations, jpegRStallDurations, highSpeedVideoConfigurations, inputOutputFormatsMap, listHighResolution);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public StreamConfigurationMap getStreamConfigurationMapMaximumResolution() {
        if (isUltraHighResolutionSensor()) {
            StreamConfiguration[] configurations = (StreamConfiguration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] minFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] stallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.SCALER_AVAILABLE_STALL_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfiguration[] depthConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] depthMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] depthStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DEPTH_STALL_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfiguration[] dynamicDepthConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.f103x6f2fa73c);
            StreamConfigurationDuration[] dynamicDepthMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.f102x51945058);
            StreamConfigurationDuration[] dynamicDepthStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.DEPTH_AVAILABLE_DYNAMIC_DEPTH_STALL_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfiguration[] heicConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] heicMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] heicStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.HEIC_AVAILABLE_HEIC_STALL_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfiguration[] jpegRConfigurations = (StreamConfiguration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] jpegRMinFrameDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION);
            StreamConfigurationDuration[] jpegRStallDurations = (StreamConfigurationDuration[]) getBase(CameraCharacteristics.JPEGR_AVAILABLE_JPEG_R_STALL_DURATIONS_MAXIMUM_RESOLUTION);
            HighSpeedVideoConfiguration[] highSpeedVideoConfigurations = (HighSpeedVideoConfiguration[]) getBase(CameraCharacteristics.f101xa18493cc);
            ReprocessFormatsMap inputOutputFormatsMap = (ReprocessFormatsMap) getBase(CameraCharacteristics.SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP_MAXIMUM_RESOLUTION);
            boolean listHighResolution = isBurstSupported();
            return new StreamConfigurationMap(configurations, minFrameDurations, stallDurations, depthConfigurations, depthMinFrameDurations, depthStallDurations, dynamicDepthConfigurations, dynamicDepthMinFrameDurations, dynamicDepthStallDurations, heicConfigurations, heicMinFrameDurations, heicStallDurations, jpegRConfigurations, jpegRMinFrameDurations, jpegRStallDurations, highSpeedVideoConfigurations, inputOutputFormatsMap, listHighResolution, false);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> Integer getMaxRegions(Key<T> key) {
        int[] maxRegions = (int[]) getBase(CameraCharacteristics.CONTROL_MAX_REGIONS);
        if (maxRegions == null) {
            return null;
        }
        if (key.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)) {
            return Integer.valueOf(maxRegions[0]);
        }
        if (key.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB)) {
            return Integer.valueOf(maxRegions[1]);
        }
        if (key.equals(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)) {
            return Integer.valueOf(maxRegions[2]);
        }
        throw new AssertionError("Invalid key " + key);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> Integer getMaxNumOutputs(Key<T> key) {
        int[] maxNumOutputs = (int[]) getBase(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_STREAMS);
        if (maxNumOutputs == null) {
            return null;
        }
        if (key.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_RAW)) {
            return Integer.valueOf(maxNumOutputs[0]);
        }
        if (key.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC)) {
            return Integer.valueOf(maxNumOutputs[1]);
        }
        if (key.equals(CameraCharacteristics.REQUEST_MAX_NUM_OUTPUT_PROC_STALLING)) {
            return Integer.valueOf(maxNumOutputs[2]);
        }
        throw new AssertionError("Invalid key " + key);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> TonemapCurve getTonemapCurve() {
        float[] red = (float[]) getBase(CaptureRequest.TONEMAP_CURVE_RED);
        float[] green = (float[]) getBase(CaptureRequest.TONEMAP_CURVE_GREEN);
        float[] blue = (float[]) getBase(CaptureRequest.TONEMAP_CURVE_BLUE);
        if (areValuesAllNull(red, green, blue)) {
            return null;
        }
        if (red == null || green == null || blue == null) {
            Log.m104w(TAG, "getTonemapCurve - missing tone curve components");
            return null;
        }
        TonemapCurve tc = new TonemapCurve(red, green, blue);
        return tc;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OisSample[] getOisSamples() {
        long[] timestamps = (long[]) getBase(CaptureResult.STATISTICS_OIS_TIMESTAMPS);
        float[] xShifts = (float[]) getBase(CaptureResult.STATISTICS_OIS_X_SHIFTS);
        float[] yShifts = (float[]) getBase(CaptureResult.STATISTICS_OIS_Y_SHIFTS);
        if (timestamps == null) {
            if (xShifts != null) {
                throw new AssertionError("timestamps is null but xShifts is not");
            }
            if (yShifts != null) {
                throw new AssertionError("timestamps is null but yShifts is not");
            }
            return null;
        } else if (xShifts == null) {
            throw new AssertionError("timestamps is not null but xShifts is");
        } else {
            if (yShifts == null) {
                throw new AssertionError("timestamps is not null but yShifts is");
            }
            if (xShifts.length != timestamps.length) {
                throw new AssertionError(String.format("timestamps has %d entries but xShifts has %d", Integer.valueOf(timestamps.length), Integer.valueOf(xShifts.length)));
            }
            if (yShifts.length != timestamps.length) {
                throw new AssertionError(String.format("timestamps has %d entries but yShifts has %d", Integer.valueOf(timestamps.length), Integer.valueOf(yShifts.length)));
            }
            OisSample[] samples = new OisSample[timestamps.length];
            for (int i = 0; i < timestamps.length; i++) {
                samples[i] = new OisSample(timestamps[i], xShifts[i], yShifts[i]);
            }
            return samples;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Capability[] getExtendedSceneModeCapabilities() {
        int[] maxSizes;
        float[] zoomRanges;
        Range<Float> zoomRange;
        int i;
        int[] maxSizes2 = (int[]) getBase(CameraCharacteristics.CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_MAX_SIZES);
        float[] zoomRanges2 = (float[]) getBase(CameraCharacteristics.CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_ZOOM_RATIO_RANGES);
        Range<Float> zoomRange2 = (Range) getBase(CameraCharacteristics.CONTROL_ZOOM_RATIO_RANGE);
        float maxDigitalZoom = ((Float) getBase(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)).floatValue();
        if (maxSizes2 == null) {
            return null;
        }
        if (maxSizes2.length % 3 == 0) {
            int numExtendedSceneModes = maxSizes2.length / 3;
            int numExtendedSceneModeZoomRanges = 0;
            int i2 = 1;
            if (zoomRanges2 != null) {
                if (zoomRanges2.length % 2 != 0) {
                    throw new AssertionError("availableExtendedSceneModeZoomRanges must be tuples of [minZoom, maxZoom]");
                }
                numExtendedSceneModeZoomRanges = zoomRanges2.length / 2;
                if (numExtendedSceneModes - numExtendedSceneModeZoomRanges != 1) {
                    throw new AssertionError("Number of extended scene mode zoom ranges must be 1 less than number of supported modes");
                }
            }
            float modeOffMinZoomRatio = 1.0f;
            float modeOffMaxZoomRatio = maxDigitalZoom;
            if (zoomRange2 != null) {
                modeOffMinZoomRatio = zoomRange2.getLower().floatValue();
                modeOffMaxZoomRatio = zoomRange2.getUpper().floatValue();
            }
            Capability[] capabilities = new Capability[numExtendedSceneModes];
            int i3 = 0;
            int j = 0;
            while (i3 < numExtendedSceneModes) {
                int mode = maxSizes2[i3 * 3];
                int width = maxSizes2[(i3 * 3) + i2];
                int height = maxSizes2[(i3 * 3) + 2];
                if (mode == 0 || j >= numExtendedSceneModeZoomRanges) {
                    maxSizes = maxSizes2;
                    zoomRanges = zoomRanges2;
                    zoomRange = zoomRange2;
                    i = i2;
                    capabilities[i3] = new Capability(mode, new Size(width, height), new Range(Float.valueOf(modeOffMinZoomRatio), Float.valueOf(modeOffMaxZoomRatio)));
                } else {
                    maxSizes = maxSizes2;
                    zoomRange = zoomRange2;
                    i = 1;
                    zoomRanges = zoomRanges2;
                    capabilities[i3] = new Capability(mode, new Size(width, height), new Range(Float.valueOf(zoomRanges2[j * 2]), Float.valueOf(zoomRanges2[(j * 2) + 1])));
                    j++;
                }
                i3++;
                i2 = i;
                maxSizes2 = maxSizes;
                zoomRange2 = zoomRange;
                zoomRanges2 = zoomRanges;
            }
            return capabilities;
        }
        throw new AssertionError("availableExtendedSceneModeMaxSizes must be tuples of [mode, width, height]");
    }

    private <T> void setBase(CameraCharacteristics.Key<T> key, T value) {
        setBase((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    private <T> void setBase(CaptureResult.Key<T> key, T value) {
        setBase((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    private <T> void setBase(CaptureRequest.Key<T> key, T value) {
        setBase((Key<Key<T>>) key.getNativeKey(), (Key<T>) value);
    }

    private <T> void setBase(Key<T> key, T value) {
        int tag;
        if (key.hasTag()) {
            tag = key.getTag();
        } else {
            tag = nativeGetTagFromKeyLocal(this.mMetadataPtr, key.getName());
            key.cacheTag(tag);
        }
        if (value == null) {
            writeValues(tag, null);
            return;
        }
        int nativeType = nativeGetTypeFromTagLocal(this.mMetadataPtr, tag);
        Marshaler<T> marshaler = getMarshalerForKey(key, nativeType);
        int size = marshaler.calculateMarshalSize(value);
        byte[] values = new byte[size];
        ByteBuffer buffer = ByteBuffer.wrap(values).order(ByteOrder.nativeOrder());
        marshaler.marshal(value, buffer);
        writeValues(tag, values);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setAvailableFormats(int[] value) {
        if (value == null) {
            return false;
        }
        int[] newValues = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            newValues[i] = value[i];
            if (value[i] == 256) {
                newValues[i] = 33;
            }
        }
        setBase((CameraCharacteristics.Key<CameraCharacteristics.Key<int[]>>) CameraCharacteristics.SCALER_AVAILABLE_FORMATS, (CameraCharacteristics.Key<int[]>) newValues);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setFaceRectangles(Rect[] faceRects) {
        if (faceRects == null) {
            return false;
        }
        Rect[] newFaceRects = new Rect[faceRects.length];
        for (int i = 0; i < newFaceRects.length; i++) {
            newFaceRects[i] = new Rect(faceRects[i].left, faceRects[i].top, faceRects[i].right + faceRects[i].left, faceRects[i].bottom + faceRects[i].top);
        }
        setBase((CaptureResult.Key<CaptureResult.Key<Rect[]>>) CaptureResult.STATISTICS_FACE_RECTANGLES, (CaptureResult.Key<Rect[]>) newFaceRects);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> boolean setTonemapCurve(TonemapCurve tc) {
        if (tc == null) {
            return false;
        }
        float[][] curve = new float[3];
        for (int i = 0; i <= 2; i++) {
            int pointCount = tc.getPointCount(i);
            curve[i] = new float[pointCount * 2];
            tc.copyColorCurve(i, curve[i], 0);
        }
        setBase((CaptureRequest.Key<CaptureRequest.Key<float[]>>) CaptureRequest.TONEMAP_CURVE_RED, (CaptureRequest.Key<float[]>) curve[0]);
        setBase((CaptureRequest.Key<CaptureRequest.Key<float[]>>) CaptureRequest.TONEMAP_CURVE_GREEN, (CaptureRequest.Key<float[]>) curve[1]);
        setBase((CaptureRequest.Key<CaptureRequest.Key<float[]>>) CaptureRequest.TONEMAP_CURVE_BLUE, (CaptureRequest.Key<float[]>) curve[2]);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> boolean setScalerCropRegion(Rect cropRegion) {
        if (cropRegion == null) {
            return false;
        }
        setBase((CaptureRequest.Key<CaptureRequest.Key<Boolean>>) CaptureRequest.SCALER_CROP_REGION_SET, (CaptureRequest.Key<Boolean>) true);
        setBase((CaptureRequest.Key<CaptureRequest.Key<Rect>>) CaptureRequest.SCALER_CROP_REGION, (CaptureRequest.Key<Rect>) cropRegion);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> boolean setAFRegions(T afRegions) {
        if (afRegions == null) {
            return false;
        }
        setBase((CaptureRequest.Key<CaptureRequest.Key<Boolean>>) CaptureRequest.CONTROL_AF_REGIONS_SET, (CaptureRequest.Key<Boolean>) true);
        setBase((CaptureRequest.Key<CaptureRequest.Key<MeteringRectangle[]>>) CaptureRequest.CONTROL_AF_REGIONS, (CaptureRequest.Key<MeteringRectangle[]>) afRegions);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> boolean setAERegions(T aeRegions) {
        if (aeRegions == null) {
            return false;
        }
        setBase((CaptureRequest.Key<CaptureRequest.Key<Boolean>>) CaptureRequest.CONTROL_AE_REGIONS_SET, (CaptureRequest.Key<Boolean>) true);
        setBase((CaptureRequest.Key<CaptureRequest.Key<MeteringRectangle[]>>) CaptureRequest.CONTROL_AE_REGIONS, (CaptureRequest.Key<MeteringRectangle[]>) aeRegions);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> boolean setAWBRegions(T awbRegions) {
        if (awbRegions == null) {
            return false;
        }
        setBase((CaptureRequest.Key<CaptureRequest.Key<Boolean>>) CaptureRequest.CONTROL_AWB_REGIONS_SET, (CaptureRequest.Key<Boolean>) true);
        setBase((CaptureRequest.Key<CaptureRequest.Key<MeteringRectangle[]>>) CaptureRequest.CONTROL_AWB_REGIONS, (CaptureRequest.Key<MeteringRectangle[]>) awbRegions);
        return true;
    }

    private void updateNativeAllocation() {
        long currentBufferSize = nativeGetBufferSize(this.mMetadataPtr);
        long j = this.mBufferSize;
        if (currentBufferSize != j) {
            if (j > 0) {
                VMRuntime.getRuntime().registerNativeFree(this.mBufferSize);
            }
            this.mBufferSize = currentBufferSize;
            if (currentBufferSize > 0) {
                VMRuntime.getRuntime().registerNativeAllocation(this.mBufferSize);
            }
        }
    }

    public void setCameraId(int cameraId) {
        this.mCameraId = cameraId;
    }

    public void setHasMandatoryConcurrentStreams(boolean hasMandatoryConcurrentStreams) {
        this.mHasMandatoryConcurrentStreams = hasMandatoryConcurrentStreams;
    }

    public void setDisplaySize(Size displaySize) {
        this.mDisplaySize = displaySize;
    }

    public void setMultiResolutionStreamConfigurationMap(Map<String, StreamConfiguration[]> multiResolutionMap) {
        this.mMultiResolutionStreamConfigurationMap = new MultiResolutionStreamConfigurationMap(multiResolutionMap);
    }

    public MultiResolutionStreamConfigurationMap getMultiResolutionStreamConfigurationMap() {
        return this.mMultiResolutionStreamConfigurationMap;
    }

    public void swap(CameraMetadataNative other) {
        nativeSwap(this.mMetadataPtr, other.mMetadataPtr);
        this.mCameraId = other.mCameraId;
        this.mHasMandatoryConcurrentStreams = other.mHasMandatoryConcurrentStreams;
        this.mDisplaySize = other.mDisplaySize;
        this.mMultiResolutionStreamConfigurationMap = other.mMultiResolutionStreamConfigurationMap;
        updateNativeAllocation();
        other.updateNativeAllocation();
    }

    public void setVendorId(long vendorId) {
        nativeSetVendorId(this.mMetadataPtr, vendorId);
    }

    public int getEntryCount() {
        return nativeGetEntryCount(this.mMetadataPtr);
    }

    public boolean isEmpty() {
        return nativeIsEmpty(this.mMetadataPtr);
    }

    public long getMetadataPtr() {
        return this.mMetadataPtr;
    }

    public <K> ArrayList<K> getAllVendorKeys(Class<K> keyClass) {
        if (keyClass == null) {
            throw new NullPointerException();
        }
        return nativeGetAllVendorKeys(this.mMetadataPtr, keyClass);
    }

    public static int getTag(String key) {
        return nativeGetTagFromKey(key, Long.MAX_VALUE);
    }

    public static int getTag(String key, long vendorId) {
        return nativeGetTagFromKey(key, vendorId);
    }

    public static int getNativeType(int tag, long vendorId) {
        return nativeGetTypeFromTag(tag, vendorId);
    }

    public void writeValues(int tag, byte[] src) {
        nativeWriteValues(tag, src, this.mMetadataPtr);
    }

    public byte[] readValues(int tag) {
        return nativeReadValues(tag, this.mMetadataPtr);
    }

    public void dumpToLog() {
        try {
            nativeDump(this.mMetadataPtr);
        } catch (IOException e) {
            Log.wtf(TAG, "Dump logging failed", e);
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private static <T> Marshaler<T> getMarshalerForKey(Key<T> key, int nativeType) {
        return MarshalRegistry.getMarshaler(key.getTypeReference(), nativeType);
    }

    private static void registerAllMarshalers() {
        MarshalQueryable[] queryList = {new MarshalQueryablePrimitive(), new MarshalQueryableEnum(), new MarshalQueryableArray(), new MarshalQueryableBoolean(), new MarshalQueryableNativeByteToInteger(), new MarshalQueryableRect(), new MarshalQueryableSize(), new MarshalQueryableSizeF(), new MarshalQueryableString(), new MarshalQueryableReprocessFormatsMap(), new MarshalQueryableRange(), new MarshalQueryablePair(), new MarshalQueryableMeteringRectangle(), new MarshalQueryableColorSpaceTransform(), new MarshalQueryableStreamConfiguration(), new MarshalQueryableStreamConfigurationDuration(), new MarshalQueryableRggbChannelVector(), new MarshalQueryableBlackLevelPattern(), new MarshalQueryableHighSpeedVideoConfiguration(), new MarshalQueryableRecommendedStreamConfiguration(), new MarshalQueryableParcelable()};
        for (MarshalQueryable query : queryList) {
            MarshalRegistry.registerMarshalQueryable(query);
        }
    }

    private static boolean areValuesAllNull(Object... objs) {
        for (Object o : objs) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }

    public Set<String> getPhysicalCameraIds() {
        int[] availableCapabilities = (int[]) get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        if (availableCapabilities == null) {
            throw new AssertionError("android.request.availableCapabilities must be non-null in the characteristics");
        }
        if (!ArrayUtils.contains(availableCapabilities, 11)) {
            return Collections.emptySet();
        }
        byte[] physicalCamIds = (byte[]) get(CameraCharacteristics.LOGICAL_MULTI_CAMERA_PHYSICAL_IDS);
        try {
            String physicalCamIdString = new String(physicalCamIds, "UTF-8");
            String[] physicalCameraIdArray = physicalCamIdString.split("\u0000");
            return Collections.unmodifiableSet(new HashSet(Arrays.asList(physicalCameraIdArray)));
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("android.logicalCam.physicalIds must be UTF-8 string");
        }
    }
}
