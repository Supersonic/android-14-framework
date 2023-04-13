package android.hardware;

import android.annotation.SystemApi;
import android.hardware.input.InputSensorInfo;
import java.util.UUID;
/* loaded from: classes.dex */
public final class Sensor {
    private static final int ADDITIONAL_INFO_MASK = 64;
    private static final int ADDITIONAL_INFO_SHIFT = 6;
    private static final int DATA_INJECTION_MASK = 16;
    private static final int DATA_INJECTION_SHIFT = 4;
    private static final int DIRECT_CHANNEL_MASK = 3072;
    private static final int DIRECT_CHANNEL_SHIFT = 10;
    private static final int DIRECT_REPORT_MASK = 896;
    private static final int DIRECT_REPORT_SHIFT = 7;
    private static final int DYNAMIC_SENSOR_MASK = 32;
    private static final int DYNAMIC_SENSOR_SHIFT = 5;
    public static final int REPORTING_MODE_CONTINUOUS = 0;
    private static final int REPORTING_MODE_MASK = 14;
    public static final int REPORTING_MODE_ONE_SHOT = 2;
    public static final int REPORTING_MODE_ON_CHANGE = 1;
    private static final int REPORTING_MODE_SHIFT = 1;
    public static final int REPORTING_MODE_SPECIAL_TRIGGER = 3;
    private static final int SENSOR_FLAG_WAKE_UP_SENSOR = 1;
    public static final String SENSOR_STRING_TYPE_TILT_DETECTOR = "android.sensor.tilt_detector";
    public static final String STRING_TYPE_ACCELEROMETER = "android.sensor.accelerometer";
    public static final String STRING_TYPE_ACCELEROMETER_LIMITED_AXES = "android.sensor.accelerometer_limited_axes";
    public static final String STRING_TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED = "android.sensor.accelerometer_limited_axes_uncalibrated";
    public static final String STRING_TYPE_ACCELEROMETER_UNCALIBRATED = "android.sensor.accelerometer_uncalibrated";
    public static final String STRING_TYPE_AMBIENT_TEMPERATURE = "android.sensor.ambient_temperature";
    public static final String STRING_TYPE_DEVICE_ORIENTATION = "android.sensor.device_orientation";
    @SystemApi
    public static final String STRING_TYPE_DYNAMIC_SENSOR_META = "android.sensor.dynamic_sensor_meta";
    public static final String STRING_TYPE_GAME_ROTATION_VECTOR = "android.sensor.game_rotation_vector";
    public static final String STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR = "android.sensor.geomagnetic_rotation_vector";
    public static final String STRING_TYPE_GLANCE_GESTURE = "android.sensor.glance_gesture";
    public static final String STRING_TYPE_GRAVITY = "android.sensor.gravity";
    public static final String STRING_TYPE_GYROSCOPE = "android.sensor.gyroscope";
    public static final String STRING_TYPE_GYROSCOPE_LIMITED_AXES = "android.sensor.gyroscope_limited_axes";
    public static final String STRING_TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED = "android.sensor.gyroscope_limited_axes_uncalibrated";
    public static final String STRING_TYPE_GYROSCOPE_UNCALIBRATED = "android.sensor.gyroscope_uncalibrated";
    public static final String STRING_TYPE_HEADING = "android.sensor.heading";
    public static final String STRING_TYPE_HEAD_TRACKER = "android.sensor.head_tracker";
    public static final String STRING_TYPE_HEART_BEAT = "android.sensor.heart_beat";
    public static final String STRING_TYPE_HEART_RATE = "android.sensor.heart_rate";
    public static final String STRING_TYPE_HINGE_ANGLE = "android.sensor.hinge_angle";
    public static final String STRING_TYPE_LIGHT = "android.sensor.light";
    public static final String STRING_TYPE_LINEAR_ACCELERATION = "android.sensor.linear_acceleration";
    public static final String STRING_TYPE_LOW_LATENCY_OFFBODY_DETECT = "android.sensor.low_latency_offbody_detect";
    public static final String STRING_TYPE_MAGNETIC_FIELD = "android.sensor.magnetic_field";
    public static final String STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED = "android.sensor.magnetic_field_uncalibrated";
    public static final String STRING_TYPE_MOTION_DETECT = "android.sensor.motion_detect";
    @Deprecated
    public static final String STRING_TYPE_ORIENTATION = "android.sensor.orientation";
    public static final String STRING_TYPE_PICK_UP_GESTURE = "android.sensor.pick_up_gesture";
    public static final String STRING_TYPE_POSE_6DOF = "android.sensor.pose_6dof";
    public static final String STRING_TYPE_PRESSURE = "android.sensor.pressure";
    public static final String STRING_TYPE_PROXIMITY = "android.sensor.proximity";
    public static final String STRING_TYPE_RELATIVE_HUMIDITY = "android.sensor.relative_humidity";
    public static final String STRING_TYPE_ROTATION_VECTOR = "android.sensor.rotation_vector";
    public static final String STRING_TYPE_SIGNIFICANT_MOTION = "android.sensor.significant_motion";
    public static final String STRING_TYPE_STATIONARY_DETECT = "android.sensor.stationary_detect";
    public static final String STRING_TYPE_STEP_COUNTER = "android.sensor.step_counter";
    public static final String STRING_TYPE_STEP_DETECTOR = "android.sensor.step_detector";
    @Deprecated
    public static final String STRING_TYPE_TEMPERATURE = "android.sensor.temperature";
    public static final String STRING_TYPE_WAKE_GESTURE = "android.sensor.wake_gesture";
    @SystemApi
    public static final String STRING_TYPE_WRIST_TILT_GESTURE = "android.sensor.wrist_tilt_gesture";
    public static final int TYPE_ACCELEROMETER = 1;
    public static final int TYPE_ACCELEROMETER_LIMITED_AXES = 38;
    public static final int TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED = 40;
    public static final int TYPE_ACCELEROMETER_UNCALIBRATED = 35;
    public static final int TYPE_ALL = -1;
    public static final int TYPE_AMBIENT_TEMPERATURE = 13;
    public static final int TYPE_DEVICE_ORIENTATION = 27;
    public static final int TYPE_DEVICE_PRIVATE_BASE = 65536;
    @SystemApi
    public static final int TYPE_DYNAMIC_SENSOR_META = 32;
    public static final int TYPE_GAME_ROTATION_VECTOR = 15;
    public static final int TYPE_GEOMAGNETIC_ROTATION_VECTOR = 20;
    public static final int TYPE_GLANCE_GESTURE = 24;
    public static final int TYPE_GRAVITY = 9;
    public static final int TYPE_GYROSCOPE = 4;
    public static final int TYPE_GYROSCOPE_LIMITED_AXES = 39;
    public static final int TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED = 41;
    public static final int TYPE_GYROSCOPE_UNCALIBRATED = 16;
    public static final int TYPE_HEADING = 42;
    public static final int TYPE_HEAD_TRACKER = 37;
    public static final int TYPE_HEART_BEAT = 31;
    public static final int TYPE_HEART_RATE = 21;
    public static final int TYPE_HINGE_ANGLE = 36;
    public static final int TYPE_LIGHT = 5;
    public static final int TYPE_LINEAR_ACCELERATION = 10;
    public static final int TYPE_LOW_LATENCY_OFFBODY_DETECT = 34;
    public static final int TYPE_MAGNETIC_FIELD = 2;
    public static final int TYPE_MAGNETIC_FIELD_UNCALIBRATED = 14;
    public static final int TYPE_MOTION_DETECT = 30;
    @Deprecated
    public static final int TYPE_ORIENTATION = 3;
    public static final int TYPE_PICK_UP_GESTURE = 25;
    public static final int TYPE_POSE_6DOF = 28;
    public static final int TYPE_PRESSURE = 6;
    public static final int TYPE_PROXIMITY = 8;
    public static final int TYPE_RELATIVE_HUMIDITY = 12;
    public static final int TYPE_ROTATION_VECTOR = 11;
    public static final int TYPE_SIGNIFICANT_MOTION = 17;
    public static final int TYPE_STATIONARY_DETECT = 29;
    public static final int TYPE_STEP_COUNTER = 19;
    public static final int TYPE_STEP_DETECTOR = 18;
    @Deprecated
    public static final int TYPE_TEMPERATURE = 7;
    public static final int TYPE_TILT_DETECTOR = 22;
    public static final int TYPE_WAKE_GESTURE = 23;
    @SystemApi
    public static final int TYPE_WRIST_TILT_GESTURE = 26;
    private static final int[] sSensorReportingModes = {0, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 5, 1, 1, 6, 4, 6, 1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 16, 1, 1, 1, 2, 16, 1, 6, 1, 6, 6, 6, 9, 9, 2};
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
    private UUID mUuid;
    private String mVendor;
    private int mVersion;

    public int getReportingMode() {
        return (this.mFlags & 14) >> 1;
    }

    public int getHighestDirectReportRateLevel() {
        int rateLevel = (this.mFlags & 896) >> 7;
        if (rateLevel > 3) {
            return 3;
        }
        return rateLevel;
    }

    public boolean isDirectChannelTypeSupported(int sharedMemType) {
        switch (sharedMemType) {
            case 1:
                return (this.mFlags & 1024) > 0;
            case 2:
                return (this.mFlags & 2048) > 0;
            default:
                return false;
        }
    }

    public static int getMaxLengthValuesArray(Sensor sensor, int sdkLevel) {
        if (sensor.mType == 11 && sdkLevel <= 17) {
            return 3;
        }
        int offset = sensor.mType;
        int[] iArr = sSensorReportingModes;
        if (offset >= iArr.length) {
            return 16;
        }
        return iArr[offset];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Sensor() {
    }

    public Sensor(InputSensorInfo sensorInfo) {
        this.mName = sensorInfo.getName();
        this.mVendor = sensorInfo.getVendor();
        this.mVersion = sensorInfo.getVersion();
        this.mHandle = sensorInfo.getHandle();
        this.mType = sensorInfo.getType();
        this.mMaxRange = sensorInfo.getMaxRange();
        this.mResolution = sensorInfo.getResolution();
        this.mPower = sensorInfo.getPower();
        this.mMinDelay = sensorInfo.getMinDelay();
        this.mFifoReservedEventCount = sensorInfo.getFifoReservedEventCount();
        this.mFifoMaxEventCount = sensorInfo.getFifoMaxEventCount();
        this.mStringType = sensorInfo.getStringType();
        this.mRequiredPermission = sensorInfo.getRequiredPermission();
        this.mMaxDelay = sensorInfo.getMaxDelay();
        this.mFlags = sensorInfo.getFlags();
        this.mId = sensorInfo.getId();
        this.mUuid = new UUID(this.mId, 0L);
    }

    public String getName() {
        return this.mName;
    }

    public String getVendor() {
        return this.mVendor;
    }

    public int getType() {
        return this.mType;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public float getMaximumRange() {
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

    @SystemApi
    public UUID getUuid() {
        return this.mUuid;
    }

    public int getId() {
        return this.mId;
    }

    public String getRequiredPermission() {
        return this.mRequiredPermission;
    }

    public int getHandle() {
        return this.mHandle;
    }

    public int getMaxDelay() {
        return this.mMaxDelay;
    }

    public boolean isWakeUpSensor() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isDynamicSensor() {
        return (this.mFlags & 32) != 0;
    }

    public boolean isAdditionalInfoSupported() {
        return (this.mFlags & 64) != 0;
    }

    @SystemApi
    public boolean isDataInjectionSupported() {
        return ((this.mFlags & 16) >> 4) != 0;
    }

    void setRange(float max, float res) {
        this.mMaxRange = max;
        this.mResolution = res;
    }

    public String toString() {
        return "{Sensor name=\"" + this.mName + "\", vendor=\"" + this.mVendor + "\", version=" + this.mVersion + ", type=" + this.mType + ", maxRange=" + this.mMaxRange + ", resolution=" + this.mResolution + ", power=" + this.mPower + ", minDelay=" + this.mMinDelay + "}";
    }

    private boolean setType(int value) {
        this.mType = value;
        switch (value) {
            case 1:
                this.mStringType = STRING_TYPE_ACCELEROMETER;
                return true;
            case 2:
                this.mStringType = STRING_TYPE_MAGNETIC_FIELD;
                return true;
            case 3:
                this.mStringType = STRING_TYPE_ORIENTATION;
                return true;
            case 4:
                this.mStringType = STRING_TYPE_GYROSCOPE;
                return true;
            case 5:
                this.mStringType = STRING_TYPE_LIGHT;
                return true;
            case 6:
                this.mStringType = STRING_TYPE_PRESSURE;
                return true;
            case 7:
                this.mStringType = STRING_TYPE_TEMPERATURE;
                return true;
            case 8:
                this.mStringType = STRING_TYPE_PROXIMITY;
                return true;
            case 9:
                this.mStringType = STRING_TYPE_GRAVITY;
                return true;
            case 10:
                this.mStringType = STRING_TYPE_LINEAR_ACCELERATION;
                return true;
            case 11:
                this.mStringType = STRING_TYPE_ROTATION_VECTOR;
                return true;
            case 12:
                this.mStringType = STRING_TYPE_RELATIVE_HUMIDITY;
                return true;
            case 13:
                this.mStringType = STRING_TYPE_AMBIENT_TEMPERATURE;
                return true;
            case 14:
                this.mStringType = STRING_TYPE_MAGNETIC_FIELD_UNCALIBRATED;
                return true;
            case 15:
                this.mStringType = STRING_TYPE_GAME_ROTATION_VECTOR;
                return true;
            case 16:
                this.mStringType = STRING_TYPE_GYROSCOPE_UNCALIBRATED;
                return true;
            case 17:
                this.mStringType = STRING_TYPE_SIGNIFICANT_MOTION;
                return true;
            case 18:
                this.mStringType = STRING_TYPE_STEP_DETECTOR;
                return true;
            case 19:
                this.mStringType = STRING_TYPE_STEP_COUNTER;
                return true;
            case 20:
                this.mStringType = STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR;
                return true;
            case 21:
                this.mStringType = STRING_TYPE_HEART_RATE;
                return true;
            case 22:
                this.mStringType = SENSOR_STRING_TYPE_TILT_DETECTOR;
                return true;
            case 23:
                this.mStringType = STRING_TYPE_WAKE_GESTURE;
                return true;
            case 24:
                this.mStringType = STRING_TYPE_GLANCE_GESTURE;
                return true;
            case 25:
                this.mStringType = STRING_TYPE_PICK_UP_GESTURE;
                return true;
            case 26:
            case 28:
            case 29:
            case 30:
            case 31:
            case 33:
            default:
                return false;
            case 27:
                this.mStringType = STRING_TYPE_DEVICE_ORIENTATION;
                return true;
            case 32:
                this.mStringType = STRING_TYPE_DYNAMIC_SENSOR_META;
                return true;
            case 34:
                this.mStringType = STRING_TYPE_LOW_LATENCY_OFFBODY_DETECT;
                return true;
            case 35:
                this.mStringType = STRING_TYPE_ACCELEROMETER_UNCALIBRATED;
                return true;
            case 36:
                this.mStringType = STRING_TYPE_HINGE_ANGLE;
                return true;
            case 37:
                this.mStringType = STRING_TYPE_HEAD_TRACKER;
                return true;
            case 38:
                this.mStringType = STRING_TYPE_ACCELEROMETER_LIMITED_AXES;
                return true;
            case 39:
                this.mStringType = STRING_TYPE_GYROSCOPE_LIMITED_AXES;
                return true;
            case 40:
                this.mStringType = STRING_TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED;
                return true;
            case 41:
                this.mStringType = STRING_TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED;
                return true;
            case 42:
                this.mStringType = STRING_TYPE_HEADING;
                return true;
        }
    }

    private void setUuid(long msb, long lsb) {
        this.mUuid = new UUID(msb, lsb);
    }

    private void setId(int id) {
        this.mId = id;
    }
}
