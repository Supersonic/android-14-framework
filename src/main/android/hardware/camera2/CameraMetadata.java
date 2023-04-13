package android.hardware.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.PublicKey;
import android.hardware.camera2.impl.SyntheticKey;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public abstract class CameraMetadata<TKey> {
    public static final int AUTOMOTIVE_LENS_FACING_EXTERIOR_FRONT = 1;
    public static final int AUTOMOTIVE_LENS_FACING_EXTERIOR_LEFT = 3;
    public static final int AUTOMOTIVE_LENS_FACING_EXTERIOR_OTHER = 0;
    public static final int AUTOMOTIVE_LENS_FACING_EXTERIOR_REAR = 2;
    public static final int AUTOMOTIVE_LENS_FACING_EXTERIOR_RIGHT = 4;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_OTHER = 5;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_1_CENTER = 7;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_1_LEFT = 6;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_1_RIGHT = 8;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_2_CENTER = 10;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_2_LEFT = 9;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_2_RIGHT = 11;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_3_CENTER = 13;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_3_LEFT = 12;
    public static final int AUTOMOTIVE_LENS_FACING_INTERIOR_SEAT_ROW_3_RIGHT = 14;
    public static final int AUTOMOTIVE_LOCATION_EXTERIOR_FRONT = 2;
    public static final int AUTOMOTIVE_LOCATION_EXTERIOR_LEFT = 4;
    public static final int AUTOMOTIVE_LOCATION_EXTERIOR_OTHER = 1;
    public static final int AUTOMOTIVE_LOCATION_EXTERIOR_REAR = 3;
    public static final int AUTOMOTIVE_LOCATION_EXTERIOR_RIGHT = 5;
    public static final int AUTOMOTIVE_LOCATION_EXTRA_FRONT = 7;
    public static final int AUTOMOTIVE_LOCATION_EXTRA_LEFT = 9;
    public static final int AUTOMOTIVE_LOCATION_EXTRA_OTHER = 6;
    public static final int AUTOMOTIVE_LOCATION_EXTRA_REAR = 8;
    public static final int AUTOMOTIVE_LOCATION_EXTRA_RIGHT = 10;
    public static final int AUTOMOTIVE_LOCATION_INTERIOR = 0;
    public static final int COLOR_CORRECTION_ABERRATION_MODE_FAST = 1;
    public static final int COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY = 2;
    public static final int COLOR_CORRECTION_ABERRATION_MODE_OFF = 0;
    public static final int COLOR_CORRECTION_MODE_FAST = 1;
    public static final int COLOR_CORRECTION_MODE_HIGH_QUALITY = 2;
    public static final int COLOR_CORRECTION_MODE_TRANSFORM_MATRIX = 0;
    public static final int CONTROL_AE_ANTIBANDING_MODE_50HZ = 1;
    public static final int CONTROL_AE_ANTIBANDING_MODE_60HZ = 2;
    public static final int CONTROL_AE_ANTIBANDING_MODE_AUTO = 3;
    public static final int CONTROL_AE_ANTIBANDING_MODE_OFF = 0;
    public static final int CONTROL_AE_MODE_OFF = 0;
    public static final int CONTROL_AE_MODE_ON = 1;
    public static final int CONTROL_AE_MODE_ON_ALWAYS_FLASH = 3;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH = 2;
    public static final int CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE = 4;
    public static final int CONTROL_AE_MODE_ON_EXTERNAL_FLASH = 5;
    public static final int CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL = 2;
    public static final int CONTROL_AE_PRECAPTURE_TRIGGER_IDLE = 0;
    public static final int CONTROL_AE_PRECAPTURE_TRIGGER_START = 1;
    public static final int CONTROL_AE_STATE_CONVERGED = 2;
    public static final int CONTROL_AE_STATE_FLASH_REQUIRED = 4;
    public static final int CONTROL_AE_STATE_INACTIVE = 0;
    public static final int CONTROL_AE_STATE_LOCKED = 3;
    public static final int CONTROL_AE_STATE_PRECAPTURE = 5;
    public static final int CONTROL_AE_STATE_SEARCHING = 1;
    public static final int CONTROL_AF_MODE_AUTO = 1;
    public static final int CONTROL_AF_MODE_CONTINUOUS_PICTURE = 4;
    public static final int CONTROL_AF_MODE_CONTINUOUS_VIDEO = 3;
    public static final int CONTROL_AF_MODE_EDOF = 5;
    public static final int CONTROL_AF_MODE_MACRO = 2;
    public static final int CONTROL_AF_MODE_OFF = 0;
    public static final int CONTROL_AF_SCENE_CHANGE_DETECTED = 1;
    public static final int CONTROL_AF_SCENE_CHANGE_NOT_DETECTED = 0;
    public static final int CONTROL_AF_STATE_ACTIVE_SCAN = 3;
    public static final int CONTROL_AF_STATE_FOCUSED_LOCKED = 4;
    public static final int CONTROL_AF_STATE_INACTIVE = 0;
    public static final int CONTROL_AF_STATE_NOT_FOCUSED_LOCKED = 5;
    public static final int CONTROL_AF_STATE_PASSIVE_FOCUSED = 2;
    public static final int CONTROL_AF_STATE_PASSIVE_SCAN = 1;
    public static final int CONTROL_AF_STATE_PASSIVE_UNFOCUSED = 6;
    public static final int CONTROL_AF_TRIGGER_CANCEL = 2;
    public static final int CONTROL_AF_TRIGGER_IDLE = 0;
    public static final int CONTROL_AF_TRIGGER_START = 1;
    public static final int CONTROL_AUTOFRAMING_AUTO = 2;
    public static final int CONTROL_AUTOFRAMING_OFF = 0;
    public static final int CONTROL_AUTOFRAMING_ON = 1;
    public static final int CONTROL_AUTOFRAMING_STATE_CONVERGED = 2;
    public static final int CONTROL_AUTOFRAMING_STATE_FRAMING = 1;
    public static final int CONTROL_AUTOFRAMING_STATE_INACTIVE = 0;
    public static final int CONTROL_AWB_MODE_AUTO = 1;
    public static final int CONTROL_AWB_MODE_CLOUDY_DAYLIGHT = 6;
    public static final int CONTROL_AWB_MODE_DAYLIGHT = 5;
    public static final int CONTROL_AWB_MODE_FLUORESCENT = 3;
    public static final int CONTROL_AWB_MODE_INCANDESCENT = 2;
    public static final int CONTROL_AWB_MODE_OFF = 0;
    public static final int CONTROL_AWB_MODE_SHADE = 8;
    public static final int CONTROL_AWB_MODE_TWILIGHT = 7;
    public static final int CONTROL_AWB_MODE_WARM_FLUORESCENT = 4;
    public static final int CONTROL_AWB_STATE_CONVERGED = 2;
    public static final int CONTROL_AWB_STATE_INACTIVE = 0;
    public static final int CONTROL_AWB_STATE_LOCKED = 3;
    public static final int CONTROL_AWB_STATE_SEARCHING = 1;
    public static final int CONTROL_CAPTURE_INTENT_CUSTOM = 0;
    public static final int CONTROL_CAPTURE_INTENT_MANUAL = 6;
    public static final int CONTROL_CAPTURE_INTENT_MOTION_TRACKING = 7;
    public static final int CONTROL_CAPTURE_INTENT_PREVIEW = 1;
    public static final int CONTROL_CAPTURE_INTENT_STILL_CAPTURE = 2;
    public static final int CONTROL_CAPTURE_INTENT_VIDEO_RECORD = 3;
    public static final int CONTROL_CAPTURE_INTENT_VIDEO_SNAPSHOT = 4;
    public static final int CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG = 5;
    public static final int CONTROL_EFFECT_MODE_AQUA = 8;
    public static final int CONTROL_EFFECT_MODE_BLACKBOARD = 7;
    public static final int CONTROL_EFFECT_MODE_MONO = 1;
    public static final int CONTROL_EFFECT_MODE_NEGATIVE = 2;
    public static final int CONTROL_EFFECT_MODE_OFF = 0;
    public static final int CONTROL_EFFECT_MODE_POSTERIZE = 5;
    public static final int CONTROL_EFFECT_MODE_SEPIA = 4;
    public static final int CONTROL_EFFECT_MODE_SOLARIZE = 3;
    public static final int CONTROL_EFFECT_MODE_WHITEBOARD = 6;
    public static final int CONTROL_EXTENDED_SCENE_MODE_BOKEH_CONTINUOUS = 2;
    public static final int CONTROL_EXTENDED_SCENE_MODE_BOKEH_STILL_CAPTURE = 1;
    public static final int CONTROL_EXTENDED_SCENE_MODE_DISABLED = 0;
    public static final int CONTROL_EXTENDED_SCENE_MODE_VENDOR_START = 64;
    public static final int CONTROL_MODE_AUTO = 1;
    public static final int CONTROL_MODE_OFF = 0;
    public static final int CONTROL_MODE_OFF_KEEP_STATE = 3;
    public static final int CONTROL_MODE_USE_EXTENDED_SCENE_MODE = 4;
    public static final int CONTROL_MODE_USE_SCENE_MODE = 2;
    public static final int CONTROL_SCENE_MODE_ACTION = 2;
    public static final int CONTROL_SCENE_MODE_BARCODE = 16;
    public static final int CONTROL_SCENE_MODE_BEACH = 8;
    public static final int CONTROL_SCENE_MODE_CANDLELIGHT = 15;
    public static final int CONTROL_SCENE_MODE_DEVICE_CUSTOM_END = 127;
    public static final int CONTROL_SCENE_MODE_DEVICE_CUSTOM_START = 100;
    public static final int CONTROL_SCENE_MODE_DISABLED = 0;
    public static final int CONTROL_SCENE_MODE_FACE_PRIORITY = 1;
    public static final int CONTROL_SCENE_MODE_FACE_PRIORITY_LOW_LIGHT = 19;
    public static final int CONTROL_SCENE_MODE_FIREWORKS = 12;
    public static final int CONTROL_SCENE_MODE_HDR = 18;
    @Deprecated
    public static final int CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO = 17;
    public static final int CONTROL_SCENE_MODE_LANDSCAPE = 4;
    public static final int CONTROL_SCENE_MODE_NIGHT = 5;
    public static final int CONTROL_SCENE_MODE_NIGHT_PORTRAIT = 6;
    public static final int CONTROL_SCENE_MODE_PARTY = 14;
    public static final int CONTROL_SCENE_MODE_PORTRAIT = 3;
    public static final int CONTROL_SCENE_MODE_SNOW = 9;
    public static final int CONTROL_SCENE_MODE_SPORTS = 13;
    public static final int CONTROL_SCENE_MODE_STEADYPHOTO = 11;
    public static final int CONTROL_SCENE_MODE_SUNSET = 10;
    public static final int CONTROL_SCENE_MODE_THEATRE = 7;
    public static final int CONTROL_SETTINGS_OVERRIDE_OFF = 0;
    public static final int CONTROL_SETTINGS_OVERRIDE_VENDOR_START = 16384;
    public static final int CONTROL_SETTINGS_OVERRIDE_ZOOM = 1;
    public static final int CONTROL_VIDEO_STABILIZATION_MODE_OFF = 0;
    public static final int CONTROL_VIDEO_STABILIZATION_MODE_ON = 1;
    public static final int CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION = 2;
    private static final boolean DEBUG = false;
    public static final int DISTORTION_CORRECTION_MODE_FAST = 1;
    public static final int DISTORTION_CORRECTION_MODE_HIGH_QUALITY = 2;
    public static final int DISTORTION_CORRECTION_MODE_OFF = 0;
    public static final int EDGE_MODE_FAST = 1;
    public static final int EDGE_MODE_HIGH_QUALITY = 2;
    public static final int EDGE_MODE_OFF = 0;
    public static final int EDGE_MODE_ZERO_SHUTTER_LAG = 3;
    public static final int FLASH_MODE_OFF = 0;
    public static final int FLASH_MODE_SINGLE = 1;
    public static final int FLASH_MODE_TORCH = 2;
    public static final int FLASH_STATE_CHARGING = 1;
    public static final int FLASH_STATE_FIRED = 3;
    public static final int FLASH_STATE_PARTIAL = 4;
    public static final int FLASH_STATE_READY = 2;
    public static final int FLASH_STATE_UNAVAILABLE = 0;
    public static final int HOT_PIXEL_MODE_FAST = 1;
    public static final int HOT_PIXEL_MODE_HIGH_QUALITY = 2;
    public static final int HOT_PIXEL_MODE_OFF = 0;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_3 = 3;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL = 4;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_FULL = 1;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY = 2;
    public static final int INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED = 0;
    public static final int LED_AVAILABLE_LEDS_TRANSMIT = 0;
    public static final int LENS_FACING_BACK = 1;
    public static final int LENS_FACING_EXTERNAL = 2;
    public static final int LENS_FACING_FRONT = 0;
    public static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_APPROXIMATE = 1;
    public static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_CALIBRATED = 2;
    public static final int LENS_INFO_FOCUS_DISTANCE_CALIBRATION_UNCALIBRATED = 0;
    public static final int LENS_OPTICAL_STABILIZATION_MODE_OFF = 0;
    public static final int LENS_OPTICAL_STABILIZATION_MODE_ON = 1;
    public static final int LENS_POSE_REFERENCE_AUTOMOTIVE = 3;
    public static final int LENS_POSE_REFERENCE_GYROSCOPE = 1;
    public static final int LENS_POSE_REFERENCE_PRIMARY_CAMERA = 0;
    public static final int LENS_POSE_REFERENCE_UNDEFINED = 2;
    public static final int LENS_STATE_MOVING = 1;
    public static final int LENS_STATE_STATIONARY = 0;
    public static final int LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_APPROXIMATE = 0;
    public static final int LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE_CALIBRATED = 1;
    public static final int NOISE_REDUCTION_MODE_FAST = 1;
    public static final int NOISE_REDUCTION_MODE_HIGH_QUALITY = 2;
    public static final int NOISE_REDUCTION_MODE_MINIMAL = 3;
    public static final int NOISE_REDUCTION_MODE_OFF = 0;
    public static final int NOISE_REDUCTION_MODE_ZERO_SHUTTER_LAG = 4;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE = 0;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE = 6;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES = 20;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO = 9;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT = 8;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT = 18;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA = 11;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING = 2;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR = 1;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME = 12;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING = 10;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING = 15;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING = 4;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_RAW = 3;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS = 5;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING = 17;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA = 13;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE = 19;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA = 14;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR = 16;
    public static final int REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING = 7;
    public static final int REQUEST_AVAILABLE_COLOR_SPACE_PROFILES_MAP_UNSPECIFIED = -1;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_10B_HDR_OEM */
    public static final int f105x3e699e52 = 64;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_10B_HDR_OEM_PO */
    public static final int f106x4f0570c = 128;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_10B_HDR_REF */
    public static final int f107x3e69a98e = 16;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_10B_HDR_REF_PO */
    public static final int f108xa0bb250 = 32;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_8B_HDR_OEM */
    public static final int f109x6d3447d9 = 1024;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_8B_HDR_OEM_PO */
    public static final int f110x36f66c25 = 2048;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_8B_HDR_REF */
    public static final int f111x6d345315 = 256;

    /* renamed from: REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_DOLBY_VISION_8B_HDR_REF_PO */
    public static final int f112x3c11c769 = 512;
    public static final int REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_HDR10 = 4;
    public static final int REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_HDR10_PLUS = 8;
    public static final int REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_HLG10 = 2;
    public static final int REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_MAX = 4096;
    public static final int REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP_STANDARD = 1;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_CROPPED_RAW = 6;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_DEFAULT = 0;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_PREVIEW = 1;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_PREVIEW_VIDEO_STILL = 4;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_STILL_CAPTURE = 2;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_VENDOR_START = 65536;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL = 5;
    public static final int SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_RECORD = 3;
    public static final int SCALER_CROPPING_TYPE_CENTER_ONLY = 0;
    public static final int SCALER_CROPPING_TYPE_FREEFORM = 1;
    public static final int SCALER_ROTATE_AND_CROP_180 = 2;
    public static final int SCALER_ROTATE_AND_CROP_270 = 3;
    public static final int SCALER_ROTATE_AND_CROP_90 = 1;
    public static final int SCALER_ROTATE_AND_CROP_AUTO = 4;
    public static final int SCALER_ROTATE_AND_CROP_NONE = 0;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR = 3;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG = 2;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG = 1;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_MONO = 5;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_NIR = 6;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGB = 4;
    public static final int SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB = 0;
    public static final int SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME = 1;
    public static final int SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN = 0;
    public static final int SENSOR_PIXEL_MODE_DEFAULT = 0;
    public static final int SENSOR_PIXEL_MODE_MAXIMUM_RESOLUTION = 1;
    public static final int SENSOR_READOUT_TIMESTAMP_HARDWARE = 1;
    public static final int SENSOR_READOUT_TIMESTAMP_NOT_SUPPORTED = 0;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_CLOUDY_WEATHER = 10;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_COOL_WHITE_FLUORESCENT = 14;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_D50 = 23;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_D55 = 20;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_D65 = 21;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_D75 = 22;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT = 1;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_DAYLIGHT_FLUORESCENT = 12;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_DAY_WHITE_FLUORESCENT = 13;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_FINE_WEATHER = 9;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_FLASH = 4;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_FLUORESCENT = 2;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_ISO_STUDIO_TUNGSTEN = 24;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_SHADE = 11;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_A = 17;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_B = 18;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_STANDARD_C = 19;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_TUNGSTEN = 3;
    public static final int SENSOR_REFERENCE_ILLUMINANT1_WHITE_FLUORESCENT = 15;
    public static final int SENSOR_TEST_PATTERN_MODE_BLACK = 5;
    public static final int SENSOR_TEST_PATTERN_MODE_COLOR_BARS = 2;
    public static final int SENSOR_TEST_PATTERN_MODE_COLOR_BARS_FADE_TO_GRAY = 3;
    public static final int SENSOR_TEST_PATTERN_MODE_CUSTOM1 = 256;
    public static final int SENSOR_TEST_PATTERN_MODE_OFF = 0;
    public static final int SENSOR_TEST_PATTERN_MODE_PN9 = 4;
    public static final int SENSOR_TEST_PATTERN_MODE_SOLID_COLOR = 1;
    public static final int SHADING_MODE_FAST = 1;
    public static final int SHADING_MODE_HIGH_QUALITY = 2;
    public static final int SHADING_MODE_OFF = 0;
    public static final int STATISTICS_FACE_DETECT_MODE_FULL = 2;
    public static final int STATISTICS_FACE_DETECT_MODE_OFF = 0;
    public static final int STATISTICS_FACE_DETECT_MODE_SIMPLE = 1;
    public static final int STATISTICS_LENS_SHADING_MAP_MODE_OFF = 0;
    public static final int STATISTICS_LENS_SHADING_MAP_MODE_ON = 1;
    public static final int STATISTICS_OIS_DATA_MODE_OFF = 0;
    public static final int STATISTICS_OIS_DATA_MODE_ON = 1;
    public static final int STATISTICS_SCENE_FLICKER_50HZ = 1;
    public static final int STATISTICS_SCENE_FLICKER_60HZ = 2;
    public static final int STATISTICS_SCENE_FLICKER_NONE = 0;
    public static final int SYNC_FRAME_NUMBER_CONVERGING = -1;
    public static final int SYNC_FRAME_NUMBER_UNKNOWN = -2;
    public static final int SYNC_MAX_LATENCY_PER_FRAME_CONTROL = 0;
    public static final int SYNC_MAX_LATENCY_UNKNOWN = -1;
    private static final String TAG = "CameraMetadataAb";
    public static final int TONEMAP_MODE_CONTRAST_CURVE = 0;
    public static final int TONEMAP_MODE_FAST = 1;
    public static final int TONEMAP_MODE_GAMMA_VALUE = 3;
    public static final int TONEMAP_MODE_HIGH_QUALITY = 2;
    public static final int TONEMAP_MODE_PRESET_CURVE = 4;
    public static final int TONEMAP_PRESET_CURVE_REC709 = 1;
    public static final int TONEMAP_PRESET_CURVE_SRGB = 0;
    private CameraMetadataNative mNativeInstance = null;

    protected abstract Class<TKey> getKeyClass();

    protected abstract <T> T getProtected(TKey tkey);

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNativeInstance(CameraMetadataNative nativeInstance) {
        this.mNativeInstance = nativeInstance;
    }

    public long getNativeMetadataPtr() {
        CameraMetadataNative cameraMetadataNative = this.mNativeInstance;
        if (cameraMetadataNative == null) {
            return 0L;
        }
        return cameraMetadataNative.getMetadataPtr();
    }

    public CameraMetadataNative getNativeMetadata() {
        return this.mNativeInstance;
    }

    public List<TKey> getKeys() {
        return Collections.unmodifiableList(getKeys(getClass(), getKeyClass(), this, null, true));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public <TKey> ArrayList<TKey> getKeys(Class<?> type, Class<TKey> keyClass, CameraMetadata<TKey> instance, int[] filterTags, boolean includeSynthetic) {
        String keyName;
        long vendorId;
        if (type.equals(TotalCaptureResult.class)) {
            type = CaptureResult.class;
        }
        if (filterTags != null) {
            Arrays.sort(filterTags);
        }
        ArrayList<TKey> keyList = (ArrayList<TKey>) new ArrayList();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(keyClass) && (field.getModifiers() & 1) != 0) {
                try {
                    Object obj = field.get(instance);
                    if ((instance == 0 || instance.getProtected(obj) != null) && shouldKeyBeAdded(obj, field, filterTags, includeSynthetic)) {
                        keyList.add(obj);
                    }
                } catch (IllegalAccessException e) {
                    throw new AssertionError("Can't get IllegalAccessException", e);
                } catch (IllegalArgumentException e2) {
                    throw new AssertionError("Can't get IllegalArgumentException", e2);
                }
            }
        }
        CameraMetadataNative cameraMetadataNative = this.mNativeInstance;
        if (cameraMetadataNative == null) {
            return keyList;
        }
        ArrayList<TKey> vendorKeys = cameraMetadataNative.getAllVendorKeys(keyClass);
        if (vendorKeys != null) {
            Iterator<TKey> it = vendorKeys.iterator();
            while (it.hasNext()) {
                TKey k = it.next();
                if (k instanceof CaptureRequest.Key) {
                    keyName = ((CaptureRequest.Key) k).getName();
                    vendorId = ((CaptureRequest.Key) k).getVendorId();
                } else if (k instanceof CaptureResult.Key) {
                    keyName = ((CaptureResult.Key) k).getName();
                    vendorId = ((CaptureResult.Key) k).getVendorId();
                } else if (k instanceof CameraCharacteristics.Key) {
                    keyName = ((CameraCharacteristics.Key) k).getName();
                    vendorId = ((CameraCharacteristics.Key) k).getVendorId();
                }
                if (filterTags == null || Arrays.binarySearch(filterTags, CameraMetadataNative.getTag(keyName, vendorId)) >= 0) {
                    if (instance == 0 || instance.getProtected(k) != null) {
                        keyList.add(k);
                    }
                }
            }
        }
        return keyList;
    }

    private static <TKey> boolean shouldKeyBeAdded(TKey key, Field field, int[] filterTags, boolean includeSynthetic) {
        CameraMetadataNative.Key nativeKey;
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        if (key instanceof CameraCharacteristics.Key) {
            nativeKey = ((CameraCharacteristics.Key) key).getNativeKey();
        } else if (key instanceof CaptureResult.Key) {
            nativeKey = ((CaptureResult.Key) key).getNativeKey();
        } else if (key instanceof CaptureRequest.Key) {
            nativeKey = ((CaptureRequest.Key) key).getNativeKey();
        } else {
            throw new IllegalArgumentException("key type must be that of a metadata key");
        }
        if (field.getAnnotation(PublicKey.class) == null) {
            return false;
        }
        if (filterTags == null) {
            return true;
        }
        if (field.getAnnotation(SyntheticKey.class) != null) {
            return includeSynthetic;
        }
        int keyTag = nativeKey.getTag();
        return Arrays.binarySearch(filterTags, keyTag) >= 0;
    }
}
