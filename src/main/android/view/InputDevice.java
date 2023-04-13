package android.view;

import android.hardware.BatteryState;
import android.hardware.SensorManager;
import android.hardware.input.HostUsiVersion;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.lights.LightsManager;
import android.icu.util.ULocale;
import android.p008os.NullVibrator;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Vibrator;
import android.p008os.VibratorManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public final class InputDevice implements Parcelable {
    public static final Parcelable.Creator<InputDevice> CREATOR = new Parcelable.Creator<InputDevice>() { // from class: android.view.InputDevice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputDevice createFromParcel(Parcel in) {
            return new InputDevice(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputDevice[] newArray(int size) {
            return new InputDevice[size];
        }
    };
    public static final int KEYBOARD_TYPE_ALPHABETIC = 2;
    public static final int KEYBOARD_TYPE_NONE = 0;
    public static final int KEYBOARD_TYPE_NON_ALPHABETIC = 1;
    private static final int MAX_RANGES = 1000;
    @Deprecated
    public static final int MOTION_RANGE_ORIENTATION = 8;
    @Deprecated
    public static final int MOTION_RANGE_PRESSURE = 2;
    @Deprecated
    public static final int MOTION_RANGE_SIZE = 3;
    @Deprecated
    public static final int MOTION_RANGE_TOOL_MAJOR = 6;
    @Deprecated
    public static final int MOTION_RANGE_TOOL_MINOR = 7;
    @Deprecated
    public static final int MOTION_RANGE_TOUCH_MAJOR = 4;
    @Deprecated
    public static final int MOTION_RANGE_TOUCH_MINOR = 5;
    @Deprecated
    public static final int MOTION_RANGE_X = 0;
    @Deprecated
    public static final int MOTION_RANGE_Y = 1;
    public static final int SOURCE_ANY = -256;
    public static final int SOURCE_BLUETOOTH_STYLUS = 49154;
    public static final int SOURCE_CLASS_BUTTON = 1;
    public static final int SOURCE_CLASS_JOYSTICK = 16;
    public static final int SOURCE_CLASS_MASK = 255;
    public static final int SOURCE_CLASS_NONE = 0;
    public static final int SOURCE_CLASS_POINTER = 2;
    public static final int SOURCE_CLASS_POSITION = 8;
    public static final int SOURCE_CLASS_TRACKBALL = 4;
    public static final int SOURCE_DPAD = 513;
    public static final int SOURCE_GAMEPAD = 1025;
    public static final int SOURCE_HDMI = 33554433;
    public static final int SOURCE_JOYSTICK = 16777232;
    public static final int SOURCE_KEYBOARD = 257;
    public static final int SOURCE_MOUSE = 8194;
    public static final int SOURCE_MOUSE_RELATIVE = 131076;
    public static final int SOURCE_ROTARY_ENCODER = 4194304;
    public static final int SOURCE_SENSOR = 67108864;
    public static final int SOURCE_STYLUS = 16386;
    public static final int SOURCE_TOUCHPAD = 1048584;
    public static final int SOURCE_TOUCHSCREEN = 4098;
    public static final int SOURCE_TOUCH_NAVIGATION = 2097152;
    public static final int SOURCE_TRACKBALL = 65540;
    public static final int SOURCE_UNKNOWN = 0;
    private static final int VIBRATOR_ID_ALL = -1;
    private final int mAssociatedDisplayId;
    private final int mControllerNumber;
    private final String mDescriptor;
    private final int mGeneration;
    private final boolean mHasBattery;
    private final boolean mHasButtonUnderPad;
    private final boolean mHasMicrophone;
    private final boolean mHasSensor;
    private final boolean mHasVibrator;
    private final HostUsiVersion mHostUsiVersion;
    private final int mId;
    private final InputDeviceIdentifier mIdentifier;
    private final boolean mIsExternal;
    private final KeyCharacterMap mKeyCharacterMap;
    private final String mKeyboardLanguageTag;
    private final String mKeyboardLayoutType;
    private final int mKeyboardType;
    private LightsManager mLightsManager;
    private final ArrayList<MotionRange> mMotionRanges;
    private final String mName;
    private final int mProductId;
    private SensorManager mSensorManager;
    private final int mSources;
    private final int mVendorId;
    private Vibrator mVibrator;
    private VibratorManager mVibratorManager;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    @interface InputSourceClass {
    }

    private InputDevice(int id, int generation, int controllerNumber, String name, int vendorId, int productId, String descriptor, boolean isExternal, int sources, int keyboardType, KeyCharacterMap keyCharacterMap, String keyboardLanguageTag, String keyboardLayoutType, boolean hasVibrator, boolean hasMicrophone, boolean hasButtonUnderPad, boolean hasSensor, boolean hasBattery, int usiVersionMajor, int usiVersionMinor, int associatedDisplayId) {
        this.mMotionRanges = new ArrayList<>();
        this.mId = id;
        this.mGeneration = generation;
        this.mControllerNumber = controllerNumber;
        this.mName = name;
        this.mVendorId = vendorId;
        this.mProductId = productId;
        this.mDescriptor = descriptor;
        this.mIsExternal = isExternal;
        this.mSources = sources;
        this.mKeyboardType = keyboardType;
        this.mKeyCharacterMap = keyCharacterMap;
        if (keyboardLanguageTag != null) {
            this.mKeyboardLanguageTag = ULocale.createCanonical(ULocale.forLanguageTag(keyboardLanguageTag)).toLanguageTag();
        } else {
            this.mKeyboardLanguageTag = null;
        }
        this.mKeyboardLayoutType = keyboardLayoutType;
        this.mHasVibrator = hasVibrator;
        this.mHasMicrophone = hasMicrophone;
        this.mHasButtonUnderPad = hasButtonUnderPad;
        this.mHasSensor = hasSensor;
        this.mHasBattery = hasBattery;
        this.mIdentifier = new InputDeviceIdentifier(descriptor, vendorId, productId);
        this.mHostUsiVersion = new HostUsiVersion(usiVersionMajor, usiVersionMinor);
        this.mAssociatedDisplayId = associatedDisplayId;
    }

    private InputDevice(Parcel in) {
        this.mMotionRanges = new ArrayList<>();
        this.mKeyCharacterMap = KeyCharacterMap.CREATOR.createFromParcel(in);
        this.mId = in.readInt();
        this.mGeneration = in.readInt();
        this.mControllerNumber = in.readInt();
        this.mName = in.readString();
        int readInt = in.readInt();
        this.mVendorId = readInt;
        int readInt2 = in.readInt();
        this.mProductId = readInt2;
        String readString = in.readString();
        this.mDescriptor = readString;
        this.mIsExternal = in.readInt() != 0;
        this.mSources = in.readInt();
        this.mKeyboardType = in.readInt();
        this.mKeyboardLanguageTag = in.readString8();
        this.mKeyboardLayoutType = in.readString8();
        this.mHasVibrator = in.readInt() != 0;
        this.mHasMicrophone = in.readInt() != 0;
        this.mHasButtonUnderPad = in.readInt() != 0;
        this.mHasSensor = in.readInt() != 0;
        this.mHasBattery = in.readInt() != 0;
        this.mHostUsiVersion = HostUsiVersion.CREATOR.createFromParcel(in);
        this.mAssociatedDisplayId = in.readInt();
        this.mIdentifier = new InputDeviceIdentifier(readString, readInt, readInt2);
        int numRanges = in.readInt();
        numRanges = numRanges > 1000 ? 1000 : numRanges;
        for (int i = 0; i < numRanges; i++) {
            addMotionRange(in.readInt(), in.readInt(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
        }
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private int mId = 0;
        private int mGeneration = 0;
        private int mControllerNumber = 0;
        private String mName = "";
        private int mVendorId = 0;
        private int mProductId = 0;
        private String mDescriptor = "";
        private boolean mIsExternal = false;
        private int mSources = 0;
        private int mKeyboardType = 0;
        private KeyCharacterMap mKeyCharacterMap = null;
        private boolean mHasVibrator = false;
        private boolean mHasMicrophone = false;
        private boolean mHasButtonUnderPad = false;
        private boolean mHasSensor = false;
        private boolean mHasBattery = false;
        private String mKeyboardLanguageTag = null;
        private String mKeyboardLayoutType = null;
        private int mUsiVersionMajor = -1;
        private int mUsiVersionMinor = -1;
        private int mAssociatedDisplayId = -1;
        private List<MotionRange> mMotionRanges = new ArrayList();

        public Builder setId(int id) {
            this.mId = id;
            return this;
        }

        public Builder setGeneration(int generation) {
            this.mGeneration = generation;
            return this;
        }

        public Builder setControllerNumber(int controllerNumber) {
            this.mControllerNumber = controllerNumber;
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setVendorId(int vendorId) {
            this.mVendorId = vendorId;
            return this;
        }

        public Builder setProductId(int productId) {
            this.mProductId = productId;
            return this;
        }

        public Builder setDescriptor(String descriptor) {
            this.mDescriptor = descriptor;
            return this;
        }

        public Builder setExternal(boolean external) {
            this.mIsExternal = external;
            return this;
        }

        public Builder setSources(int sources) {
            this.mSources = sources;
            return this;
        }

        public Builder setKeyboardType(int keyboardType) {
            this.mKeyboardType = keyboardType;
            return this;
        }

        public Builder setKeyCharacterMap(KeyCharacterMap keyCharacterMap) {
            this.mKeyCharacterMap = keyCharacterMap;
            return this;
        }

        public Builder setHasVibrator(boolean hasVibrator) {
            this.mHasVibrator = hasVibrator;
            return this;
        }

        public Builder setHasMicrophone(boolean hasMicrophone) {
            this.mHasMicrophone = hasMicrophone;
            return this;
        }

        public Builder setHasButtonUnderPad(boolean hasButtonUnderPad) {
            this.mHasButtonUnderPad = hasButtonUnderPad;
            return this;
        }

        public Builder setHasSensor(boolean hasSensor) {
            this.mHasSensor = hasSensor;
            return this;
        }

        public Builder setHasBattery(boolean hasBattery) {
            this.mHasBattery = hasBattery;
            return this;
        }

        public Builder setKeyboardLanguageTag(String keyboardLanguageTag) {
            this.mKeyboardLanguageTag = keyboardLanguageTag;
            return this;
        }

        public Builder setKeyboardLayoutType(String keyboardLayoutType) {
            this.mKeyboardLayoutType = keyboardLayoutType;
            return this;
        }

        public Builder setUsiVersion(HostUsiVersion usiVersion) {
            this.mUsiVersionMajor = usiVersion != null ? usiVersion.getMajorVersion() : -1;
            this.mUsiVersionMinor = usiVersion != null ? usiVersion.getMinorVersion() : -1;
            return this;
        }

        public Builder setAssociatedDisplayId(int displayId) {
            this.mAssociatedDisplayId = displayId;
            return this;
        }

        public Builder addMotionRange(int axis, int source, float min, float max, float flat, float fuzz, float resolution) {
            this.mMotionRanges.add(new MotionRange(axis, source, min, max, flat, fuzz, resolution));
            return this;
        }

        public InputDevice build() {
            InputDevice device = new InputDevice(this.mId, this.mGeneration, this.mControllerNumber, this.mName, this.mVendorId, this.mProductId, this.mDescriptor, this.mIsExternal, this.mSources, this.mKeyboardType, this.mKeyCharacterMap, this.mKeyboardLanguageTag, this.mKeyboardLayoutType, this.mHasVibrator, this.mHasMicrophone, this.mHasButtonUnderPad, this.mHasSensor, this.mHasBattery, this.mUsiVersionMajor, this.mUsiVersionMinor, this.mAssociatedDisplayId);
            int numRanges = this.mMotionRanges.size();
            for (int i = 0; i < numRanges; i++) {
                MotionRange range = this.mMotionRanges.get(i);
                device.addMotionRange(range.getAxis(), range.getSource(), range.getMin(), range.getMax(), range.getFlat(), range.getFuzz(), range.getResolution());
            }
            return device;
        }
    }

    public static InputDevice getDevice(int id) {
        return InputManager.getInstance().getInputDevice(id);
    }

    public static int[] getDeviceIds() {
        return InputManager.getInstance().getInputDeviceIds();
    }

    public int getId() {
        return this.mId;
    }

    public int getControllerNumber() {
        return this.mControllerNumber;
    }

    public InputDeviceIdentifier getIdentifier() {
        return this.mIdentifier;
    }

    public int getGeneration() {
        return this.mGeneration;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getProductId() {
        return this.mProductId;
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public boolean isVirtual() {
        return this.mId < 0;
    }

    public boolean isExternal() {
        return this.mIsExternal;
    }

    public boolean isFullKeyboard() {
        return (this.mSources & 257) == 257 && this.mKeyboardType == 2;
    }

    public String getName() {
        return this.mName;
    }

    public int getSources() {
        return this.mSources;
    }

    public boolean supportsSource(int source) {
        return (this.mSources & source) == source;
    }

    public int getKeyboardType() {
        return this.mKeyboardType;
    }

    public KeyCharacterMap getKeyCharacterMap() {
        return this.mKeyCharacterMap;
    }

    public String getKeyboardLanguageTag() {
        return this.mKeyboardLanguageTag;
    }

    public String getKeyboardLayoutType() {
        return this.mKeyboardLayoutType;
    }

    public boolean[] hasKeys(int... keys) {
        return InputManager.getInstance().deviceHasKeys(this.mId, keys);
    }

    public int getKeyCodeForKeyLocation(int locationKeyCode) {
        return InputManager.getInstance().getKeyCodeForKeyLocation(this.mId, locationKeyCode);
    }

    public MotionRange getMotionRange(int axis) {
        int numRanges = this.mMotionRanges.size();
        for (int i = 0; i < numRanges; i++) {
            MotionRange range = this.mMotionRanges.get(i);
            if (range.mAxis == axis) {
                return range;
            }
        }
        return null;
    }

    public MotionRange getMotionRange(int axis, int source) {
        int numRanges = this.mMotionRanges.size();
        for (int i = 0; i < numRanges; i++) {
            MotionRange range = this.mMotionRanges.get(i);
            if (range.mAxis == axis && range.mSource == source) {
                return range;
            }
        }
        return null;
    }

    public List<MotionRange> getMotionRanges() {
        return this.mMotionRanges;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addMotionRange(int axis, int source, float min, float max, float flat, float fuzz, float resolution) {
        this.mMotionRanges.add(new MotionRange(axis, source, min, max, flat, fuzz, resolution));
    }

    public String getBluetoothAddress() {
        return InputManager.getInstance().getInputDeviceBluetoothAddress(this.mId);
    }

    @Deprecated
    public Vibrator getVibrator() {
        Vibrator vibrator;
        synchronized (this.mMotionRanges) {
            if (this.mVibrator == null) {
                if (this.mHasVibrator) {
                    this.mVibrator = InputManager.getInstance().getInputDeviceVibrator(this.mId, -1);
                } else {
                    this.mVibrator = NullVibrator.getInstance();
                }
            }
            vibrator = this.mVibrator;
        }
        return vibrator;
    }

    public VibratorManager getVibratorManager() {
        synchronized (this.mMotionRanges) {
            if (this.mVibratorManager == null) {
                this.mVibratorManager = InputManager.getInstance().getInputDeviceVibratorManager(this.mId);
            }
        }
        return this.mVibratorManager;
    }

    public BatteryState getBatteryState() {
        return InputManager.getInstance().getInputDeviceBatteryState(this.mId, this.mHasBattery);
    }

    public LightsManager getLightsManager() {
        if (this.mLightsManager == null) {
            this.mLightsManager = InputManager.getInstance().getInputDeviceLightsManager(this.mId);
        }
        return this.mLightsManager;
    }

    public SensorManager getSensorManager() {
        synchronized (this.mMotionRanges) {
            if (this.mSensorManager == null) {
                this.mSensorManager = InputManager.getInstance().getInputDeviceSensorManager(this.mId);
            }
        }
        return this.mSensorManager;
    }

    public boolean isEnabled() {
        return InputManager.getInstance().isInputDeviceEnabled(this.mId);
    }

    public void enable() {
        InputManager.getInstance().enableInputDevice(this.mId);
    }

    public void disable() {
        InputManager.getInstance().disableInputDevice(this.mId);
    }

    public boolean hasMicrophone() {
        return this.mHasMicrophone;
    }

    public boolean hasButtonUnderPad() {
        return this.mHasButtonUnderPad;
    }

    public boolean hasSensor() {
        return this.mHasSensor;
    }

    public void setPointerType(int pointerType) {
        InputManager.getInstance().setPointerIconType(pointerType);
    }

    public void setCustomPointerIcon(PointerIcon icon) {
        InputManager.getInstance().setCustomPointerIcon(icon);
    }

    public boolean hasBattery() {
        return this.mHasBattery;
    }

    public HostUsiVersion getHostUsiVersion() {
        if (this.mHostUsiVersion.isValid()) {
            return this.mHostUsiVersion;
        }
        return null;
    }

    public int getAssociatedDisplayId() {
        return this.mAssociatedDisplayId;
    }

    /* loaded from: classes4.dex */
    public static final class MotionRange {
        private int mAxis;
        private float mFlat;
        private float mFuzz;
        private float mMax;
        private float mMin;
        private float mResolution;
        private int mSource;

        private MotionRange(int axis, int source, float min, float max, float flat, float fuzz, float resolution) {
            this.mAxis = axis;
            this.mSource = source;
            this.mMin = min;
            this.mMax = max;
            this.mFlat = flat;
            this.mFuzz = fuzz;
            this.mResolution = resolution;
        }

        public int getAxis() {
            return this.mAxis;
        }

        public int getSource() {
            return this.mSource;
        }

        public boolean isFromSource(int source) {
            return (getSource() & source) == source;
        }

        public float getMin() {
            return this.mMin;
        }

        public float getMax() {
            return this.mMax;
        }

        public float getRange() {
            return this.mMax - this.mMin;
        }

        public float getFlat() {
            return this.mFlat;
        }

        public float getFuzz() {
            return this.mFuzz;
        }

        public float getResolution() {
            return this.mResolution;
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        this.mKeyCharacterMap.writeToParcel(out, flags);
        out.writeInt(this.mId);
        out.writeInt(this.mGeneration);
        out.writeInt(this.mControllerNumber);
        out.writeString(this.mName);
        out.writeInt(this.mVendorId);
        out.writeInt(this.mProductId);
        out.writeString(this.mDescriptor);
        out.writeInt(this.mIsExternal ? 1 : 0);
        out.writeInt(this.mSources);
        out.writeInt(this.mKeyboardType);
        out.writeString8(this.mKeyboardLanguageTag);
        out.writeString8(this.mKeyboardLayoutType);
        out.writeInt(this.mHasVibrator ? 1 : 0);
        out.writeInt(this.mHasMicrophone ? 1 : 0);
        out.writeInt(this.mHasButtonUnderPad ? 1 : 0);
        out.writeInt(this.mHasSensor ? 1 : 0);
        out.writeInt(this.mHasBattery ? 1 : 0);
        this.mHostUsiVersion.writeToParcel(out, flags);
        out.writeInt(this.mAssociatedDisplayId);
        int numRanges = this.mMotionRanges.size();
        int numRanges2 = numRanges <= 1000 ? numRanges : 1000;
        out.writeInt(numRanges2);
        for (int i = 0; i < numRanges2; i++) {
            MotionRange range = this.mMotionRanges.get(i);
            out.writeInt(range.mAxis);
            out.writeInt(range.mSource);
            out.writeFloat(range.mMin);
            out.writeFloat(range.mMax);
            out.writeFloat(range.mFlat);
            out.writeFloat(range.mFuzz);
            out.writeFloat(range.mResolution);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder description = new StringBuilder();
        description.append("Input Device ").append(this.mId).append(": ").append(this.mName).append("\n");
        description.append("  Descriptor: ").append(this.mDescriptor).append("\n");
        description.append("  Generation: ").append(this.mGeneration).append("\n");
        description.append("  Location: ").append(this.mIsExternal ? "external" : "built-in").append("\n");
        description.append("  Keyboard Type: ");
        switch (this.mKeyboardType) {
            case 0:
                description.append("none");
                break;
            case 1:
                description.append("non-alphabetic");
                break;
            case 2:
                description.append("alphabetic");
                break;
        }
        description.append("\n");
        description.append("  Has Vibrator: ").append(this.mHasVibrator).append("\n");
        description.append("  Has Sensor: ").append(this.mHasSensor).append("\n");
        description.append("  Has battery: ").append(this.mHasBattery).append("\n");
        description.append("  Has mic: ").append(this.mHasMicrophone).append("\n");
        description.append("  USI Version: ").append(getHostUsiVersion()).append("\n");
        if (this.mKeyboardLanguageTag != null) {
            description.append(" Keyboard language tag: ").append(this.mKeyboardLanguageTag).append("\n");
        }
        if (this.mKeyboardLayoutType != null) {
            description.append(" Keyboard layout type: ").append(this.mKeyboardLayoutType).append("\n");
        }
        description.append("  Sources: 0x").append(Integer.toHexString(this.mSources)).append(" (");
        appendSourceDescriptionIfApplicable(description, 257, "keyboard");
        appendSourceDescriptionIfApplicable(description, 513, "dpad");
        appendSourceDescriptionIfApplicable(description, 4098, "touchscreen");
        appendSourceDescriptionIfApplicable(description, 8194, "mouse");
        appendSourceDescriptionIfApplicable(description, 16386, "stylus");
        appendSourceDescriptionIfApplicable(description, 65540, "trackball");
        appendSourceDescriptionIfApplicable(description, SOURCE_MOUSE_RELATIVE, "mouse_relative");
        appendSourceDescriptionIfApplicable(description, SOURCE_TOUCHPAD, "touchpad");
        appendSourceDescriptionIfApplicable(description, SOURCE_JOYSTICK, "joystick");
        appendSourceDescriptionIfApplicable(description, 1025, "gamepad");
        description.append(" )\n");
        int numAxes = this.mMotionRanges.size();
        for (int i = 0; i < numAxes; i++) {
            MotionRange range = this.mMotionRanges.get(i);
            description.append("    ").append(MotionEvent.axisToString(range.mAxis));
            description.append(": source=0x").append(Integer.toHexString(range.mSource));
            description.append(" min=").append(range.mMin);
            description.append(" max=").append(range.mMax);
            description.append(" flat=").append(range.mFlat);
            description.append(" fuzz=").append(range.mFuzz);
            description.append(" resolution=").append(range.mResolution);
            description.append("\n");
        }
        return description.toString();
    }

    private void appendSourceDescriptionIfApplicable(StringBuilder description, int source, String sourceName) {
        if ((this.mSources & source) == source) {
            description.append(" ");
            description.append(sourceName);
        }
    }
}
