package android.hardware.camera2;

import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.PublicKey;
import android.hardware.camera2.impl.SyntheticKey;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.Capability;
import android.hardware.camera2.params.ColorSpaceProfiles;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.DeviceStateSensorOrientationMap;
import android.hardware.camera2.params.DynamicRangeProfiles;
import android.hardware.camera2.params.HighSpeedVideoConfiguration;
import android.hardware.camera2.params.MandatoryStreamCombination;
import android.hardware.camera2.params.MultiResolutionStreamConfigurationMap;
import android.hardware.camera2.params.RecommendedStreamConfiguration;
import android.hardware.camera2.params.RecommendedStreamConfigurationMap;
import android.hardware.camera2.params.ReprocessFormatsMap;
import android.hardware.camera2.params.StreamConfiguration;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.TypeReference;
import android.util.Log;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.util.SizeF;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public final class CameraCharacteristics extends CameraMetadata<Key<?>> {
    private static final String TAG = "CameraCharacteristics";
    private List<CaptureRequest.Key<?>> mAvailablePhysicalRequestKeys;
    private List<CaptureRequest.Key<?>> mAvailableRequestKeys;
    private List<CaptureResult.Key<?>> mAvailableResultKeys;
    private List<CaptureRequest.Key<?>> mAvailableSessionKeys;
    private boolean mFoldedDeviceState;
    private List<Key<?>> mKeys;
    private List<Key<?>> mKeysNeedingPermission;
    private final CameraMetadataNative mProperties;
    private ArrayList<RecommendedStreamConfigurationMap> mRecommendedConfigurations;
    @PublicKey
    public static final Key<int[]> COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES = new Key<>("android.colorCorrection.availableAberrationModes", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AE_AVAILABLE_ANTIBANDING_MODES = new Key<>("android.control.aeAvailableAntibandingModes", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AE_AVAILABLE_MODES = new Key<>("android.control.aeAvailableModes", int[].class);
    @PublicKey
    public static final Key<Range<Integer>[]> CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES = new Key<>("android.control.aeAvailableTargetFpsRanges", new TypeReference<Range<Integer>[]>() { // from class: android.hardware.camera2.CameraCharacteristics.2
    });
    @PublicKey
    public static final Key<Range<Integer>> CONTROL_AE_COMPENSATION_RANGE = new Key<>("android.control.aeCompensationRange", new TypeReference<Range<Integer>>() { // from class: android.hardware.camera2.CameraCharacteristics.3
    });
    @PublicKey
    public static final Key<Rational> CONTROL_AE_COMPENSATION_STEP = new Key<>("android.control.aeCompensationStep", Rational.class);
    @PublicKey
    public static final Key<int[]> CONTROL_AF_AVAILABLE_MODES = new Key<>("android.control.afAvailableModes", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AVAILABLE_EFFECTS = new Key<>("android.control.availableEffects", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AVAILABLE_SCENE_MODES = new Key<>("android.control.availableSceneModes", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES = new Key<>("android.control.availableVideoStabilizationModes", int[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AWB_AVAILABLE_MODES = new Key<>("android.control.awbAvailableModes", int[].class);
    public static final Key<int[]> CONTROL_MAX_REGIONS = new Key<>("android.control.maxRegions", int[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> CONTROL_MAX_REGIONS_AE = new Key<>("android.control.maxRegionsAe", Integer.TYPE);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> CONTROL_MAX_REGIONS_AWB = new Key<>("android.control.maxRegionsAwb", Integer.TYPE);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> CONTROL_MAX_REGIONS_AF = new Key<>("android.control.maxRegionsAf", Integer.TYPE);
    public static final Key<HighSpeedVideoConfiguration[]> CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS = new Key<>("android.control.availableHighSpeedVideoConfigurations", HighSpeedVideoConfiguration[].class);
    @PublicKey
    public static final Key<Boolean> CONTROL_AE_LOCK_AVAILABLE = new Key<>("android.control.aeLockAvailable", Boolean.TYPE);
    @PublicKey
    public static final Key<Boolean> CONTROL_AWB_LOCK_AVAILABLE = new Key<>("android.control.awbLockAvailable", Boolean.TYPE);
    @PublicKey
    public static final Key<int[]> CONTROL_AVAILABLE_MODES = new Key<>("android.control.availableModes", int[].class);
    @PublicKey
    public static final Key<Range<Integer>> CONTROL_POST_RAW_SENSITIVITY_BOOST_RANGE = new Key<>("android.control.postRawSensitivityBoostRange", new TypeReference<Range<Integer>>() { // from class: android.hardware.camera2.CameraCharacteristics.4
    });
    public static final Key<int[]> CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_MAX_SIZES = new Key<>("android.control.availableExtendedSceneModeMaxSizes", int[].class);
    public static final Key<float[]> CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_ZOOM_RATIO_RANGES = new Key<>("android.control.availableExtendedSceneModeZoomRatioRanges", float[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<Capability[]> CONTROL_AVAILABLE_EXTENDED_SCENE_MODE_CAPABILITIES = new Key<>("android.control.availableExtendedSceneModeCapabilities", Capability[].class);
    @PublicKey
    public static final Key<Range<Float>> CONTROL_ZOOM_RATIO_RANGE = new Key<>("android.control.zoomRatioRange", new TypeReference<Range<Float>>() { // from class: android.hardware.camera2.CameraCharacteristics.5
    });

    /* renamed from: CONTROL_AVAILABLE_HIGH_SPEED_VIDEO_CONFIGURATIONS_MAXIMUM_RESOLUTION */
    public static final Key<HighSpeedVideoConfiguration[]> f101xa18493cc = new Key<>("android.control.availableHighSpeedVideoConfigurationsMaximumResolution", HighSpeedVideoConfiguration[].class);
    @PublicKey
    public static final Key<int[]> CONTROL_AVAILABLE_SETTINGS_OVERRIDES = new Key<>("android.control.availableSettingsOverrides", int[].class);
    @PublicKey
    public static final Key<Boolean> CONTROL_AUTOFRAMING_AVAILABLE = new Key<>("android.control.autoframingAvailable", Boolean.TYPE);
    @PublicKey
    public static final Key<int[]> EDGE_AVAILABLE_EDGE_MODES = new Key<>("android.edge.availableEdgeModes", int[].class);
    @PublicKey
    public static final Key<Boolean> FLASH_INFO_AVAILABLE = new Key<>("android.flash.info.available", Boolean.TYPE);
    @PublicKey
    public static final Key<Integer> FLASH_INFO_STRENGTH_MAXIMUM_LEVEL = new Key<>("android.flash.info.strengthMaximumLevel", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> FLASH_INFO_STRENGTH_DEFAULT_LEVEL = new Key<>("android.flash.info.strengthDefaultLevel", Integer.TYPE);
    @PublicKey
    public static final Key<int[]> HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES = new Key<>("android.hotPixel.availableHotPixelModes", int[].class);
    @PublicKey
    public static final Key<Size[]> JPEG_AVAILABLE_THUMBNAIL_SIZES = new Key<>("android.jpeg.availableThumbnailSizes", Size[].class);
    @PublicKey
    public static final Key<float[]> LENS_INFO_AVAILABLE_APERTURES = new Key<>("android.lens.info.availableApertures", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_INFO_AVAILABLE_FILTER_DENSITIES = new Key<>("android.lens.info.availableFilterDensities", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_INFO_AVAILABLE_FOCAL_LENGTHS = new Key<>("android.lens.info.availableFocalLengths", float[].class);
    @PublicKey
    public static final Key<int[]> LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION = new Key<>("android.lens.info.availableOpticalStabilization", int[].class);
    @PublicKey
    public static final Key<Float> LENS_INFO_HYPERFOCAL_DISTANCE = new Key<>("android.lens.info.hyperfocalDistance", Float.TYPE);
    @PublicKey
    public static final Key<Float> LENS_INFO_MINIMUM_FOCUS_DISTANCE = new Key<>("android.lens.info.minimumFocusDistance", Float.TYPE);
    public static final Key<Size> LENS_INFO_SHADING_MAP_SIZE = new Key<>("android.lens.info.shadingMapSize", Size.class);
    @PublicKey
    public static final Key<Integer> LENS_INFO_FOCUS_DISTANCE_CALIBRATION = new Key<>("android.lens.info.focusDistanceCalibration", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> LENS_FACING = new Key<>("android.lens.facing", Integer.TYPE);
    @PublicKey
    public static final Key<float[]> LENS_POSE_ROTATION = new Key<>("android.lens.poseRotation", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_POSE_TRANSLATION = new Key<>("android.lens.poseTranslation", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_INTRINSIC_CALIBRATION = new Key<>("android.lens.intrinsicCalibration", float[].class);
    @PublicKey
    @Deprecated
    public static final Key<float[]> LENS_RADIAL_DISTORTION = new Key<>("android.lens.radialDistortion", float[].class);
    @PublicKey
    public static final Key<Integer> LENS_POSE_REFERENCE = new Key<>("android.lens.poseReference", Integer.TYPE);
    @PublicKey
    public static final Key<float[]> LENS_DISTORTION = new Key<>("android.lens.distortion", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_DISTORTION_MAXIMUM_RESOLUTION = new Key<>("android.lens.distortionMaximumResolution", float[].class);
    @PublicKey
    public static final Key<float[]> LENS_INTRINSIC_CALIBRATION_MAXIMUM_RESOLUTION = new Key<>("android.lens.intrinsicCalibrationMaximumResolution", float[].class);
    @PublicKey
    public static final Key<int[]> NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES = new Key<>("android.noiseReduction.availableNoiseReductionModes", int[].class);
    @Deprecated
    public static final Key<Byte> QUIRKS_USE_PARTIAL_RESULT = new Key<>("android.quirks.usePartialResult", Byte.TYPE);
    public static final Key<int[]> REQUEST_MAX_NUM_OUTPUT_STREAMS = new Key<>("android.request.maxNumOutputStreams", int[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> REQUEST_MAX_NUM_OUTPUT_RAW = new Key<>("android.request.maxNumOutputRaw", Integer.TYPE);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> REQUEST_MAX_NUM_OUTPUT_PROC = new Key<>("android.request.maxNumOutputProc", Integer.TYPE);
    @SyntheticKey
    @PublicKey
    public static final Key<Integer> REQUEST_MAX_NUM_OUTPUT_PROC_STALLING = new Key<>("android.request.maxNumOutputProcStalling", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> REQUEST_MAX_NUM_INPUT_STREAMS = new Key<>("android.request.maxNumInputStreams", Integer.TYPE);
    @PublicKey
    public static final Key<Byte> REQUEST_PIPELINE_MAX_DEPTH = new Key<>("android.request.pipelineMaxDepth", Byte.TYPE);
    @PublicKey
    public static final Key<Integer> REQUEST_PARTIAL_RESULT_COUNT = new Key<>("android.request.partialResultCount", Integer.TYPE);
    @PublicKey
    public static final Key<int[]> REQUEST_AVAILABLE_CAPABILITIES = new Key<>("android.request.availableCapabilities", int[].class);
    public static final Key<int[]> REQUEST_AVAILABLE_REQUEST_KEYS = new Key<>("android.request.availableRequestKeys", int[].class);
    public static final Key<int[]> REQUEST_AVAILABLE_RESULT_KEYS = new Key<>("android.request.availableResultKeys", int[].class);
    public static final Key<int[]> REQUEST_AVAILABLE_CHARACTERISTICS_KEYS = new Key<>("android.request.availableCharacteristicsKeys", int[].class);
    public static final Key<int[]> REQUEST_AVAILABLE_SESSION_KEYS = new Key<>("android.request.availableSessionKeys", int[].class);
    public static final Key<int[]> REQUEST_AVAILABLE_PHYSICAL_CAMERA_REQUEST_KEYS = new Key<>("android.request.availablePhysicalCameraRequestKeys", int[].class);
    public static final Key<int[]> REQUEST_CHARACTERISTIC_KEYS_NEEDING_PERMISSION = new Key<>("android.request.characteristicKeysNeedingPermission", int[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<DynamicRangeProfiles> REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES = new Key<>("android.request.availableDynamicRangeProfiles", DynamicRangeProfiles.class);
    public static final Key<long[]> REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES_MAP = new Key<>("android.request.availableDynamicRangeProfilesMap", long[].class);
    @PublicKey
    public static final Key<Long> REQUEST_RECOMMENDED_TEN_BIT_DYNAMIC_RANGE_PROFILE = new Key<>("android.request.recommendedTenBitDynamicRangeProfile", Long.TYPE);
    @SyntheticKey
    @PublicKey
    public static final Key<ColorSpaceProfiles> REQUEST_AVAILABLE_COLOR_SPACE_PROFILES = new Key<>("android.request.availableColorSpaceProfiles", ColorSpaceProfiles.class);
    public static final Key<long[]> REQUEST_AVAILABLE_COLOR_SPACE_PROFILES_MAP = new Key<>("android.request.availableColorSpaceProfilesMap", long[].class);
    @Deprecated
    public static final Key<int[]> SCALER_AVAILABLE_FORMATS = new Key<>("android.scaler.availableFormats", int[].class);
    @Deprecated
    public static final Key<long[]> SCALER_AVAILABLE_JPEG_MIN_DURATIONS = new Key<>("android.scaler.availableJpegMinDurations", long[].class);
    @Deprecated
    public static final Key<Size[]> SCALER_AVAILABLE_JPEG_SIZES = new Key<>("android.scaler.availableJpegSizes", Size[].class);
    @PublicKey
    public static final Key<Float> SCALER_AVAILABLE_MAX_DIGITAL_ZOOM = new Key<>("android.scaler.availableMaxDigitalZoom", Float.TYPE);
    @Deprecated
    public static final Key<long[]> SCALER_AVAILABLE_PROCESSED_MIN_DURATIONS = new Key<>("android.scaler.availableProcessedMinDurations", long[].class);
    @Deprecated
    public static final Key<Size[]> SCALER_AVAILABLE_PROCESSED_SIZES = new Key<>("android.scaler.availableProcessedSizes", Size[].class);
    public static final Key<ReprocessFormatsMap> SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP = new Key<>("android.scaler.availableInputOutputFormatsMap", ReprocessFormatsMap.class);
    public static final Key<StreamConfiguration[]> SCALER_AVAILABLE_STREAM_CONFIGURATIONS = new Key<>("android.scaler.availableStreamConfigurations", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> SCALER_AVAILABLE_MIN_FRAME_DURATIONS = new Key<>("android.scaler.availableMinFrameDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> SCALER_AVAILABLE_STALL_DURATIONS = new Key<>("android.scaler.availableStallDurations", StreamConfigurationDuration[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<StreamConfigurationMap> SCALER_STREAM_CONFIGURATION_MAP = new Key<>("android.scaler.streamConfigurationMap", StreamConfigurationMap.class);
    @PublicKey
    public static final Key<Integer> SCALER_CROPPING_TYPE = new Key<>("android.scaler.croppingType", Integer.TYPE);
    public static final Key<RecommendedStreamConfiguration[]> SCALER_AVAILABLE_RECOMMENDED_STREAM_CONFIGURATIONS = new Key<>("android.scaler.availableRecommendedStreamConfigurations", RecommendedStreamConfiguration[].class);
    public static final Key<ReprocessFormatsMap> SCALER_AVAILABLE_RECOMMENDED_INPUT_OUTPUT_FORMATS_MAP = new Key<>("android.scaler.availableRecommendedInputOutputFormatsMap", ReprocessFormatsMap.class);
    @SyntheticKey
    @PublicKey
    public static final Key<MandatoryStreamCombination[]> SCALER_MANDATORY_STREAM_COMBINATIONS = new Key<>("android.scaler.mandatoryStreamCombinations", MandatoryStreamCombination[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<MandatoryStreamCombination[]> SCALER_MANDATORY_CONCURRENT_STREAM_COMBINATIONS = new Key<>("android.scaler.mandatoryConcurrentStreamCombinations", MandatoryStreamCombination[].class);
    @PublicKey
    public static final Key<int[]> SCALER_AVAILABLE_ROTATE_AND_CROP_MODES = new Key<>("android.scaler.availableRotateAndCropModes", int[].class);
    @PublicKey
    public static final Key<Size> SCALER_DEFAULT_SECURE_IMAGE_SIZE = new Key<>("android.scaler.defaultSecureImageSize", Size.class);
    public static final Key<StreamConfiguration[]> SCALER_PHYSICAL_CAMERA_MULTI_RESOLUTION_STREAM_CONFIGURATIONS = new Key<>("android.scaler.physicalCameraMultiResolutionStreamConfigurations", StreamConfiguration[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<MultiResolutionStreamConfigurationMap> SCALER_MULTI_RESOLUTION_STREAM_CONFIGURATION_MAP = new Key<>("android.scaler.multiResolutionStreamConfigurationMap", MultiResolutionStreamConfigurationMap.class);
    public static final Key<StreamConfiguration[]> SCALER_AVAILABLE_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.scaler.availableStreamConfigurationsMaximumResolution", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> SCALER_AVAILABLE_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.scaler.availableMinFrameDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> SCALER_AVAILABLE_STALL_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.scaler.availableStallDurationsMaximumResolution", StreamConfigurationDuration[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<StreamConfigurationMap> SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION = new Key<>("android.scaler.streamConfigurationMapMaximumResolution", StreamConfigurationMap.class);
    public static final Key<ReprocessFormatsMap> SCALER_AVAILABLE_INPUT_OUTPUT_FORMATS_MAP_MAXIMUM_RESOLUTION = new Key<>("android.scaler.availableInputOutputFormatsMapMaximumResolution", ReprocessFormatsMap.class);
    @SyntheticKey
    @PublicKey
    public static final Key<MandatoryStreamCombination[]> SCALER_MANDATORY_MAXIMUM_RESOLUTION_STREAM_COMBINATIONS = new Key<>("android.scaler.mandatoryMaximumResolutionStreamCombinations", MandatoryStreamCombination[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<MandatoryStreamCombination[]> SCALER_MANDATORY_TEN_BIT_OUTPUT_STREAM_COMBINATIONS = new Key<>("android.scaler.mandatoryTenBitOutputStreamCombinations", MandatoryStreamCombination[].class);
    @SyntheticKey
    @PublicKey

    /* renamed from: SCALER_MANDATORY_PREVIEW_STABILIZATION_OUTPUT_STREAM_COMBINATIONS */
    public static final Key<MandatoryStreamCombination[]> f104x783e32d9 = new Key<>("android.scaler.mandatoryPreviewStabilizationOutputStreamCombinations", MandatoryStreamCombination[].class);
    public static final Key<Boolean> SCALER_MULTI_RESOLUTION_STREAM_SUPPORTED = new Key<>("android.scaler.multiResolutionStreamSupported", Boolean.TYPE);
    @PublicKey
    public static final Key<long[]> SCALER_AVAILABLE_STREAM_USE_CASES = new Key<>("android.scaler.availableStreamUseCases", long[].class);
    @SyntheticKey
    @PublicKey
    public static final Key<MandatoryStreamCombination[]> SCALER_MANDATORY_USE_CASE_STREAM_COMBINATIONS = new Key<>("android.scaler.mandatoryUseCaseStreamCombinations", MandatoryStreamCombination[].class);
    @PublicKey
    public static final Key<Rect> SENSOR_INFO_ACTIVE_ARRAY_SIZE = new Key<>("android.sensor.info.activeArraySize", Rect.class);
    @PublicKey
    public static final Key<Range<Integer>> SENSOR_INFO_SENSITIVITY_RANGE = new Key<>("android.sensor.info.sensitivityRange", new TypeReference<Range<Integer>>() { // from class: android.hardware.camera2.CameraCharacteristics.6
    });
    @PublicKey
    public static final Key<Integer> SENSOR_INFO_COLOR_FILTER_ARRANGEMENT = new Key<>("android.sensor.info.colorFilterArrangement", Integer.TYPE);
    @PublicKey
    public static final Key<Range<Long>> SENSOR_INFO_EXPOSURE_TIME_RANGE = new Key<>("android.sensor.info.exposureTimeRange", new TypeReference<Range<Long>>() { // from class: android.hardware.camera2.CameraCharacteristics.7
    });
    @PublicKey
    public static final Key<Long> SENSOR_INFO_MAX_FRAME_DURATION = new Key<>("android.sensor.info.maxFrameDuration", Long.TYPE);
    @PublicKey
    public static final Key<SizeF> SENSOR_INFO_PHYSICAL_SIZE = new Key<>("android.sensor.info.physicalSize", SizeF.class);
    @PublicKey
    public static final Key<Size> SENSOR_INFO_PIXEL_ARRAY_SIZE = new Key<>("android.sensor.info.pixelArraySize", Size.class);
    @PublicKey
    public static final Key<Integer> SENSOR_INFO_WHITE_LEVEL = new Key<>("android.sensor.info.whiteLevel", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> SENSOR_INFO_TIMESTAMP_SOURCE = new Key<>("android.sensor.info.timestampSource", Integer.TYPE);
    @PublicKey
    public static final Key<Boolean> SENSOR_INFO_LENS_SHADING_APPLIED = new Key<>("android.sensor.info.lensShadingApplied", Boolean.TYPE);
    @PublicKey
    public static final Key<Rect> SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE = new Key<>("android.sensor.info.preCorrectionActiveArraySize", Rect.class);
    @PublicKey
    public static final Key<Rect> SENSOR_INFO_ACTIVE_ARRAY_SIZE_MAXIMUM_RESOLUTION = new Key<>("android.sensor.info.activeArraySizeMaximumResolution", Rect.class);
    @PublicKey
    public static final Key<Size> SENSOR_INFO_PIXEL_ARRAY_SIZE_MAXIMUM_RESOLUTION = new Key<>("android.sensor.info.pixelArraySizeMaximumResolution", Size.class);
    @PublicKey
    public static final Key<Rect> SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE_MAXIMUM_RESOLUTION = new Key<>("android.sensor.info.preCorrectionActiveArraySizeMaximumResolution", Rect.class);
    @PublicKey
    public static final Key<Size> SENSOR_INFO_BINNING_FACTOR = new Key<>("android.sensor.info.binningFactor", Size.class);
    @PublicKey
    public static final Key<Integer> SENSOR_REFERENCE_ILLUMINANT1 = new Key<>("android.sensor.referenceIlluminant1", Integer.TYPE);
    @PublicKey
    public static final Key<Byte> SENSOR_REFERENCE_ILLUMINANT2 = new Key<>("android.sensor.referenceIlluminant2", Byte.TYPE);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_CALIBRATION_TRANSFORM1 = new Key<>("android.sensor.calibrationTransform1", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_CALIBRATION_TRANSFORM2 = new Key<>("android.sensor.calibrationTransform2", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_COLOR_TRANSFORM1 = new Key<>("android.sensor.colorTransform1", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_COLOR_TRANSFORM2 = new Key<>("android.sensor.colorTransform2", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_FORWARD_MATRIX1 = new Key<>("android.sensor.forwardMatrix1", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<ColorSpaceTransform> SENSOR_FORWARD_MATRIX2 = new Key<>("android.sensor.forwardMatrix2", ColorSpaceTransform.class);
    @PublicKey
    public static final Key<BlackLevelPattern> SENSOR_BLACK_LEVEL_PATTERN = new Key<>("android.sensor.blackLevelPattern", BlackLevelPattern.class);
    @PublicKey
    public static final Key<Integer> SENSOR_MAX_ANALOG_SENSITIVITY = new Key<>("android.sensor.maxAnalogSensitivity", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> SENSOR_ORIENTATION = new Key<>(Sensor.STRING_TYPE_ORIENTATION, Integer.TYPE);
    @PublicKey
    public static final Key<int[]> SENSOR_AVAILABLE_TEST_PATTERN_MODES = new Key<>("android.sensor.availableTestPatternModes", int[].class);
    @PublicKey
    public static final Key<Rect[]> SENSOR_OPTICAL_BLACK_REGIONS = new Key<>("android.sensor.opticalBlackRegions", Rect[].class);
    @PublicKey
    public static final Key<Integer> SENSOR_READOUT_TIMESTAMP = new Key<>("android.sensor.readoutTimestamp", Integer.TYPE);
    @PublicKey
    public static final Key<int[]> SHADING_AVAILABLE_MODES = new Key<>("android.shading.availableModes", int[].class);
    @PublicKey
    public static final Key<int[]> STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES = new Key<>("android.statistics.info.availableFaceDetectModes", int[].class);
    @PublicKey
    public static final Key<Integer> STATISTICS_INFO_MAX_FACE_COUNT = new Key<>("android.statistics.info.maxFaceCount", Integer.TYPE);
    @PublicKey
    public static final Key<boolean[]> STATISTICS_INFO_AVAILABLE_HOT_PIXEL_MAP_MODES = new Key<>("android.statistics.info.availableHotPixelMapModes", boolean[].class);
    @PublicKey
    public static final Key<int[]> STATISTICS_INFO_AVAILABLE_LENS_SHADING_MAP_MODES = new Key<>("android.statistics.info.availableLensShadingMapModes", int[].class);
    @PublicKey
    public static final Key<int[]> STATISTICS_INFO_AVAILABLE_OIS_DATA_MODES = new Key<>("android.statistics.info.availableOisDataModes", int[].class);
    @PublicKey
    public static final Key<Integer> TONEMAP_MAX_CURVE_POINTS = new Key<>("android.tonemap.maxCurvePoints", Integer.TYPE);
    @PublicKey
    public static final Key<int[]> TONEMAP_AVAILABLE_TONE_MAP_MODES = new Key<>("android.tonemap.availableToneMapModes", int[].class);
    public static final Key<int[]> LED_AVAILABLE_LEDS = new Key<>("android.led.availableLeds", int[].class);
    @PublicKey
    public static final Key<Integer> INFO_SUPPORTED_HARDWARE_LEVEL = new Key<>("android.info.supportedHardwareLevel", Integer.TYPE);
    @PublicKey
    public static final Key<String> INFO_VERSION = new Key<>("android.info.version", String.class);
    @SyntheticKey
    @PublicKey
    public static final Key<DeviceStateSensorOrientationMap> INFO_DEVICE_STATE_SENSOR_ORIENTATION_MAP = new Key<>("android.info.deviceStateSensorOrientationMap", DeviceStateSensorOrientationMap.class);
    public static final Key<long[]> INFO_DEVICE_STATE_ORIENTATIONS = new Key<>("android.info.deviceStateOrientations", long[].class);
    @PublicKey
    public static final Key<Integer> SYNC_MAX_LATENCY = new Key<>("android.sync.maxLatency", Integer.TYPE);
    @PublicKey
    public static final Key<Integer> REPROCESS_MAX_CAPTURE_STALL = new Key<>("android.reprocess.maxCaptureStall", Integer.TYPE);
    public static final Key<StreamConfiguration[]> DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS = new Key<>("android.depth.availableDepthStreamConfigurations", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DEPTH_MIN_FRAME_DURATIONS = new Key<>("android.depth.availableDepthMinFrameDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DEPTH_STALL_DURATIONS = new Key<>("android.depth.availableDepthStallDurations", StreamConfigurationDuration[].class);
    @PublicKey
    public static final Key<Boolean> DEPTH_DEPTH_IS_EXCLUSIVE = new Key<>("android.depth.depthIsExclusive", Boolean.TYPE);
    public static final Key<RecommendedStreamConfiguration[]> DEPTH_AVAILABLE_RECOMMENDED_DEPTH_STREAM_CONFIGURATIONS = new Key<>("android.depth.availableRecommendedDepthStreamConfigurations", RecommendedStreamConfiguration[].class);
    public static final Key<StreamConfiguration[]> DEPTH_AVAILABLE_DYNAMIC_DEPTH_STREAM_CONFIGURATIONS = new Key<>("android.depth.availableDynamicDepthStreamConfigurations", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DYNAMIC_DEPTH_MIN_FRAME_DURATIONS = new Key<>("android.depth.availableDynamicDepthMinFrameDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DYNAMIC_DEPTH_STALL_DURATIONS = new Key<>("android.depth.availableDynamicDepthStallDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfiguration[]> DEPTH_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.depth.availableDepthStreamConfigurationsMaximumResolution", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DEPTH_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.depth.availableDepthMinFrameDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DEPTH_STALL_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.depth.availableDepthStallDurationsMaximumResolution", StreamConfigurationDuration[].class);

    /* renamed from: DEPTH_AVAILABLE_DYNAMIC_DEPTH_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION */
    public static final Key<StreamConfiguration[]> f103x6f2fa73c = new Key<>("android.depth.availableDynamicDepthStreamConfigurationsMaximumResolution", StreamConfiguration[].class);

    /* renamed from: DEPTH_AVAILABLE_DYNAMIC_DEPTH_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION */
    public static final Key<StreamConfigurationDuration[]> f102x51945058 = new Key<>("android.depth.availableDynamicDepthMinFrameDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> DEPTH_AVAILABLE_DYNAMIC_DEPTH_STALL_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.depth.availableDynamicDepthStallDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<byte[]> LOGICAL_MULTI_CAMERA_PHYSICAL_IDS = new Key<>("android.logicalMultiCamera.physicalIds", byte[].class);
    @PublicKey
    public static final Key<Integer> LOGICAL_MULTI_CAMERA_SENSOR_SYNC_TYPE = new Key<>("android.logicalMultiCamera.sensorSyncType", Integer.TYPE);
    @PublicKey
    public static final Key<int[]> DISTORTION_CORRECTION_AVAILABLE_MODES = new Key<>("android.distortionCorrection.availableModes", int[].class);
    public static final Key<StreamConfiguration[]> HEIC_AVAILABLE_HEIC_STREAM_CONFIGURATIONS = new Key<>("android.heic.availableHeicStreamConfigurations", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> HEIC_AVAILABLE_HEIC_MIN_FRAME_DURATIONS = new Key<>("android.heic.availableHeicMinFrameDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> HEIC_AVAILABLE_HEIC_STALL_DURATIONS = new Key<>("android.heic.availableHeicStallDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfiguration[]> HEIC_AVAILABLE_HEIC_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.heic.availableHeicStreamConfigurationsMaximumResolution", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> HEIC_AVAILABLE_HEIC_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.heic.availableHeicMinFrameDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> HEIC_AVAILABLE_HEIC_STALL_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.heic.availableHeicStallDurationsMaximumResolution", StreamConfigurationDuration[].class);
    @PublicKey
    public static final Key<int[]> AUTOMOTIVE_LENS_FACING = new Key<>("android.automotive.lens.facing", int[].class);
    @PublicKey
    public static final Key<Integer> AUTOMOTIVE_LOCATION = new Key<>("android.automotive.location", Integer.TYPE);
    public static final Key<StreamConfiguration[]> JPEGR_AVAILABLE_JPEG_R_STREAM_CONFIGURATIONS = new Key<>("android.jpegr.availableJpegRStreamConfigurations", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> JPEGR_AVAILABLE_JPEG_R_MIN_FRAME_DURATIONS = new Key<>("android.jpegr.availableJpegRMinFrameDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> JPEGR_AVAILABLE_JPEG_R_STALL_DURATIONS = new Key<>("android.jpegr.availableJpegRStallDurations", StreamConfigurationDuration[].class);
    public static final Key<StreamConfiguration[]> JPEGR_AVAILABLE_JPEG_R_STREAM_CONFIGURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.jpegr.availableJpegRStreamConfigurationsMaximumResolution", StreamConfiguration[].class);
    public static final Key<StreamConfigurationDuration[]> JPEGR_AVAILABLE_JPEG_R_MIN_FRAME_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.jpegr.availableJpegRMinFrameDurationsMaximumResolution", StreamConfigurationDuration[].class);
    public static final Key<StreamConfigurationDuration[]> JPEGR_AVAILABLE_JPEG_R_STALL_DURATIONS_MAXIMUM_RESOLUTION = new Key<>("android.jpegr.availableJpegRStallDurationsMaximumResolution", StreamConfigurationDuration[].class);
    private final Object mLock = new Object();
    private final CameraManager.DeviceStateListener mFoldStateListener = new CameraManager.DeviceStateListener() { // from class: android.hardware.camera2.CameraCharacteristics.1
        @Override // android.hardware.camera2.CameraManager.DeviceStateListener
        public final void onDeviceStateChanged(boolean folded) {
            synchronized (CameraCharacteristics.this.mLock) {
                CameraCharacteristics.this.mFoldedDeviceState = folded;
            }
        }
    };

    /* loaded from: classes.dex */
    public static final class Key<T> {
        private final CameraMetadataNative.Key<T> mKey;

        public Key(String name, Class<T> type, long vendorId) {
            this.mKey = new CameraMetadataNative.Key<>(name, type, vendorId);
        }

        public Key(String name, String fallbackName, Class<T> type) {
            this.mKey = new CameraMetadataNative.Key<>(name, fallbackName, type);
        }

        public Key(String name, Class<T> type) {
            this.mKey = new CameraMetadataNative.Key<>(name, type);
        }

        public Key(String name, TypeReference<T> typeReference) {
            this.mKey = new CameraMetadataNative.Key<>(name, typeReference);
        }

        public String getName() {
            return this.mKey.getName();
        }

        public long getVendorId() {
            return this.mKey.getVendorId();
        }

        public final int hashCode() {
            return this.mKey.hashCode();
        }

        public final boolean equals(Object o) {
            return (o instanceof Key) && ((Key) o).mKey.equals(this.mKey);
        }

        public String toString() {
            return String.format("CameraCharacteristics.Key(%s)", this.mKey.getName());
        }

        public CameraMetadataNative.Key<T> getNativeKey() {
            return this.mKey;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private Key(CameraMetadataNative.Key<?> nativeKey) {
            this.mKey = nativeKey;
        }
    }

    public CameraCharacteristics(CameraMetadataNative properties) {
        CameraMetadataNative move = CameraMetadataNative.move(properties);
        this.mProperties = move;
        setNativeInstance(move);
    }

    public CameraMetadataNative getNativeCopy() {
        return new CameraMetadataNative(this.mProperties);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CameraManager.DeviceStateListener getDeviceStateListener() {
        return this.mFoldStateListener;
    }

    /* JADX WARN: Type inference failed for: r2v5, types: [T, java.lang.Integer] */
    private <T> T overrideProperty(Key<T> key) {
        if (SENSOR_ORIENTATION.equals(key) && this.mFoldStateListener != null && this.mProperties.get(INFO_DEVICE_STATE_ORIENTATIONS) != null) {
            DeviceStateSensorOrientationMap deviceStateSensorOrientationMap = (DeviceStateSensorOrientationMap) this.mProperties.get(INFO_DEVICE_STATE_SENSOR_ORIENTATION_MAP);
            synchronized (this.mLock) {
                ?? r2 = (T) Integer.valueOf(deviceStateSensorOrientationMap.getSensorOrientation(this.mFoldedDeviceState ? 4L : 0L));
                if (r2.intValue() >= 0) {
                    return r2;
                }
                Log.m104w(TAG, "No valid device state to orientation mapping! Using default!");
                return null;
            }
        }
        return null;
    }

    public <T> T get(Key<T> key) {
        T propertyOverride = (T) overrideProperty(key);
        return propertyOverride != null ? propertyOverride : (T) this.mProperties.get(key);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.hardware.camera2.CameraMetadata
    public <T> T getProtected(Key<?> key) {
        return (T) this.mProperties.get(key);
    }

    @Override // android.hardware.camera2.CameraMetadata
    protected Class<Key<?>> getKeyClass() {
        return Key.class;
    }

    @Override // android.hardware.camera2.CameraMetadata
    public List<Key<?>> getKeys() {
        List<Key<?>> list = this.mKeys;
        if (list != null) {
            return list;
        }
        int[] filterTags = (int[]) get(REQUEST_AVAILABLE_CHARACTERISTICS_KEYS);
        if (filterTags == null) {
            throw new AssertionError("android.request.availableCharacteristicsKeys must be non-null in the characteristics");
        }
        List<Key<?>> unmodifiableList = Collections.unmodifiableList(getKeys(getClass(), getKeyClass(), this, filterTags, true));
        this.mKeys = unmodifiableList;
        return unmodifiableList;
    }

    public List<Key<?>> getKeysNeedingPermission() {
        if (this.mKeysNeedingPermission == null) {
            int[] filterTags = (int[]) get(REQUEST_CHARACTERISTIC_KEYS_NEEDING_PERMISSION);
            if (filterTags == null) {
                List<Key<?>> unmodifiableList = Collections.unmodifiableList(new ArrayList());
                this.mKeysNeedingPermission = unmodifiableList;
                return unmodifiableList;
            }
            this.mKeysNeedingPermission = getAvailableKeyList(CameraCharacteristics.class, crKeyTyped, filterTags, false);
        }
        return this.mKeysNeedingPermission;
    }

    public RecommendedStreamConfigurationMap getRecommendedStreamConfigurationMap(int usecase) {
        if ((usecase >= 0 && usecase <= 8) || (usecase >= 24 && usecase < 32)) {
            if (this.mRecommendedConfigurations == null) {
                ArrayList<RecommendedStreamConfigurationMap> recommendedStreamConfigurations = this.mProperties.getRecommendedStreamConfigurations();
                this.mRecommendedConfigurations = recommendedStreamConfigurations;
                if (recommendedStreamConfigurations == null) {
                    return null;
                }
            }
            return this.mRecommendedConfigurations.get(usecase);
        }
        throw new IllegalArgumentException(String.format("Invalid use case: %d", Integer.valueOf(usecase)));
    }

    public List<CaptureRequest.Key<?>> getAvailableSessionKeys() {
        if (this.mAvailableSessionKeys == null) {
            int[] filterTags = (int[]) get(REQUEST_AVAILABLE_SESSION_KEYS);
            if (filterTags == null) {
                return null;
            }
            this.mAvailableSessionKeys = getAvailableKeyList(CaptureRequest.class, crKeyTyped, filterTags, false);
        }
        return this.mAvailableSessionKeys;
    }

    public List<CaptureRequest.Key<?>> getAvailablePhysicalCameraRequestKeys() {
        if (this.mAvailablePhysicalRequestKeys == null) {
            int[] filterTags = (int[]) get(REQUEST_AVAILABLE_PHYSICAL_CAMERA_REQUEST_KEYS);
            if (filterTags == null) {
                return null;
            }
            this.mAvailablePhysicalRequestKeys = getAvailableKeyList(CaptureRequest.class, crKeyTyped, filterTags, false);
        }
        return this.mAvailablePhysicalRequestKeys;
    }

    public List<CaptureRequest.Key<?>> getAvailableCaptureRequestKeys() {
        if (this.mAvailableRequestKeys == null) {
            int[] filterTags = (int[]) get(REQUEST_AVAILABLE_REQUEST_KEYS);
            if (filterTags == null) {
                throw new AssertionError("android.request.availableRequestKeys must be non-null in the characteristics");
            }
            this.mAvailableRequestKeys = getAvailableKeyList(CaptureRequest.class, crKeyTyped, filterTags, true);
        }
        return this.mAvailableRequestKeys;
    }

    public List<CaptureResult.Key<?>> getAvailableCaptureResultKeys() {
        if (this.mAvailableResultKeys == null) {
            int[] filterTags = (int[]) get(REQUEST_AVAILABLE_RESULT_KEYS);
            if (filterTags == null) {
                throw new AssertionError("android.request.availableResultKeys must be non-null in the characteristics");
            }
            this.mAvailableResultKeys = getAvailableKeyList(CaptureResult.class, crKeyTyped, filterTags, true);
        }
        return this.mAvailableResultKeys;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <TKey> List<TKey> getAvailableKeyList(Class<?> metadataClass, Class<TKey> keyClass, int[] filterTags, boolean includeSynthetic) {
        if (metadataClass.equals(CameraMetadata.class)) {
            throw new AssertionError("metadataClass must be a strict subclass of CameraMetadata");
        }
        if (!CameraMetadata.class.isAssignableFrom(metadataClass)) {
            throw new AssertionError("metadataClass must be a subclass of CameraMetadata");
        }
        List<TKey> staticKeyList = getKeys(metadataClass, keyClass, null, filterTags, includeSynthetic);
        return Collections.unmodifiableList(staticKeyList);
    }

    public Set<String> getPhysicalCameraIds() {
        return this.mProperties.getPhysicalCameraIds();
    }
}
