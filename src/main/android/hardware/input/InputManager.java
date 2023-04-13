package android.hardware.input;

import android.app.ActivityThread;
import android.content.Context;
import android.hardware.BatteryState;
import android.hardware.SensorManager;
import android.hardware.input.IInputDeviceBatteryListener;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IKeyboardBacklightListener;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.input.InputManager;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.hardware.lights.LightsManager;
import android.hardware.lights.LightsRequest;
import android.p008os.Binder;
import android.p008os.CombinedVibration;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.IVibratorStateListener;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.VibrationEffect;
import android.p008os.Vibrator;
import android.p008os.VibratorManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InputMonitor;
import android.view.PointerIcon;
import android.view.VerifiedInputEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.C4057R;
import com.android.internal.p028os.SomeArgs;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public final class InputManager {
    public static final String ACTION_QUERY_KEYBOARD_LAYOUTS = "android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS";
    public static final long BLOCK_UNTRUSTED_TOUCHES = 158002302;
    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final String META_DATA_KEYBOARD_LAYOUTS = "android.hardware.input.metadata.KEYBOARD_LAYOUTS";
    private static final int MSG_DEVICE_ADDED = 1;
    private static final int MSG_DEVICE_CHANGED = 3;
    private static final int MSG_DEVICE_REMOVED = 2;
    public static final int SWITCH_STATE_OFF = 0;
    public static final int SWITCH_STATE_ON = 1;
    public static final int SWITCH_STATE_UNKNOWN = -1;
    private static InputManager sInstance;
    private static String sVelocityTrackerStrategy;
    private SparseArray<RegisteredBatteryListeners> mBatteryListeners;
    private InputManagerGlobal mGlobal;
    private final IInputManager mIm;
    private IInputDeviceBatteryListener mInputDeviceBatteryListener;
    private InputDeviceSensorManager mInputDeviceSensorManager;
    private SparseArray<InputDevice> mInputDevices;
    private InputDevicesChangedListener mInputDevicesChangedListener;
    private IKeyboardBacklightListener mKeyboardBacklightListener;
    private ArrayList<KeyboardBacklightListenerDelegate> mKeyboardBacklightListeners;
    private ArrayList<OnTabletModeChangedListenerDelegate> mOnTabletModeChangedListeners;
    private TabletModeChangedListener mTabletModeChangedListener;
    private WeakReference<Context> mWeakContext;
    private static final String TAG = "InputManager";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private Boolean mIsStylusPointerIconEnabled = null;
    private final Object mInputDevicesLock = new Object();
    private final ArrayList<InputDeviceListenerDelegate> mInputDeviceListeners = new ArrayList<>();
    private final Object mTabletModeLock = new Object();
    private final Object mBatteryListenersLock = new Object();
    private final Object mKeyboardBacklightListenerLock = new Object();

    /* loaded from: classes2.dex */
    public interface InputDeviceBatteryListener {
        void onBatteryStateChanged(int i, long j, BatteryState batteryState);
    }

    /* loaded from: classes2.dex */
    public interface InputDeviceListener {
        void onInputDeviceAdded(int i);

        void onInputDeviceChanged(int i);

        void onInputDeviceRemoved(int i);
    }

    /* loaded from: classes2.dex */
    public interface KeyboardBacklightListener {
        void onKeyboardBacklightChanged(int i, KeyboardBacklightState keyboardBacklightState, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface OnTabletModeChangedListener {
        void onTabletModeChanged(long j, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RemappableModifierKey {
        public static final int REMAPPABLE_MODIFIER_KEY_ALT_LEFT = 57;
        public static final int REMAPPABLE_MODIFIER_KEY_ALT_RIGHT = 58;
        public static final int REMAPPABLE_MODIFIER_KEY_CAPS_LOCK = 115;
        public static final int REMAPPABLE_MODIFIER_KEY_CTRL_LEFT = 113;
        public static final int REMAPPABLE_MODIFIER_KEY_CTRL_RIGHT = 114;
        public static final int REMAPPABLE_MODIFIER_KEY_META_LEFT = 117;
        public static final int REMAPPABLE_MODIFIER_KEY_META_RIGHT = 118;
        public static final int REMAPPABLE_MODIFIER_KEY_SHIFT_LEFT = 59;
        public static final int REMAPPABLE_MODIFIER_KEY_SHIFT_RIGHT = 60;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SwitchState {
    }

    private InputManager() {
        InputManagerGlobal inputManagerGlobal = InputManagerGlobal.getInstance();
        this.mGlobal = inputManagerGlobal;
        IInputManager inputManagerService = inputManagerGlobal.getInputManagerService();
        this.mIm = inputManagerService;
        try {
            sVelocityTrackerStrategy = inputManagerService.getVelocityTrackerStrategy();
        } catch (RemoteException ex) {
            Log.m104w(TAG, "Could not get VelocityTracker strategy: " + ex);
        }
    }

    public static InputManager resetInstance(IInputManager inputManagerService) {
        InputManager inputManager;
        synchronized (InputManager.class) {
            InputManagerGlobal.resetInstance(inputManagerService);
            inputManager = new InputManager();
            sInstance = inputManager;
        }
        return inputManager;
    }

    public static void clearInstance() {
        synchronized (InputManager.class) {
            InputManagerGlobal.clearInstance();
            sInstance = null;
        }
    }

    @Deprecated
    public static InputManager getInstance() {
        return getInstance(ActivityThread.currentApplication());
    }

    public static InputManager getInstance(Context context) {
        InputManager inputManager;
        synchronized (InputManager.class) {
            if (sInstance == null) {
                sInstance = new InputManager();
            }
            WeakReference<Context> weakReference = sInstance.mWeakContext;
            if (weakReference == null || weakReference.get() == null) {
                sInstance.mWeakContext = new WeakReference<>(context);
            }
            inputManager = sInstance;
        }
        return inputManager;
    }

    private Context getContext() {
        WeakReference<Context> weakContext = (WeakReference) Objects.requireNonNull(this.mWeakContext, "A context is required for InputManager. Get the InputManager instance using Context#getSystemService before calling this method.");
        return (Context) Objects.requireNonNull(weakContext.get(), "missing Context");
    }

    public String getVelocityTrackerStrategy() {
        return sVelocityTrackerStrategy;
    }

    public InputDevice getInputDevice(int id) {
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int index = this.mInputDevices.indexOfKey(id);
            if (index < 0) {
                return null;
            }
            InputDevice inputDevice = this.mInputDevices.valueAt(index);
            if (inputDevice == null) {
                try {
                    inputDevice = this.mIm.getInputDevice(id);
                    if (inputDevice != null) {
                        this.mInputDevices.setValueAt(index, inputDevice);
                    }
                } catch (RemoteException ex) {
                    throw ex.rethrowFromSystemServer();
                }
            }
            return inputDevice;
        }
    }

    public InputDevice getInputDeviceByDescriptor(String descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("descriptor must not be null.");
        }
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int numDevices = this.mInputDevices.size();
            for (int i = 0; i < numDevices; i++) {
                InputDevice inputDevice = this.mInputDevices.valueAt(i);
                if (inputDevice == null) {
                    int id = this.mInputDevices.keyAt(i);
                    try {
                        inputDevice = this.mIm.getInputDevice(id);
                        if (inputDevice == null) {
                            continue;
                        } else {
                            this.mInputDevices.setValueAt(i, inputDevice);
                        }
                    } catch (RemoteException ex) {
                        throw ex.rethrowFromSystemServer();
                    }
                }
                if (descriptor.equals(inputDevice.getDescriptor())) {
                    return inputDevice;
                }
            }
            return null;
        }
    }

    public int[] getInputDeviceIds() {
        int[] ids;
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int count = this.mInputDevices.size();
            ids = new int[count];
            for (int i = 0; i < count; i++) {
                ids[i] = this.mInputDevices.keyAt(i);
            }
        }
        return ids;
    }

    public boolean isInputDeviceEnabled(int id) {
        try {
            return this.mIm.isInputDeviceEnabled(id);
        } catch (RemoteException ex) {
            Log.m104w(TAG, "Could not check enabled status of input device with id = " + id);
            throw ex.rethrowFromSystemServer();
        }
    }

    public void enableInputDevice(int id) {
        try {
            this.mIm.enableInputDevice(id);
        } catch (RemoteException ex) {
            Log.m104w(TAG, "Could not enable input device with id = " + id);
            throw ex.rethrowFromSystemServer();
        }
    }

    public void disableInputDevice(int id) {
        try {
            this.mIm.disableInputDevice(id);
        } catch (RemoteException ex) {
            Log.m104w(TAG, "Could not disable input device with id = " + id);
            throw ex.rethrowFromSystemServer();
        }
    }

    public void registerInputDeviceListener(InputDeviceListener listener, Handler handler) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int index = findInputDeviceListenerLocked(listener);
            if (index < 0) {
                this.mInputDeviceListeners.add(new InputDeviceListenerDelegate(listener, handler));
            }
        }
    }

    public void unregisterInputDeviceListener(InputDeviceListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            int index = findInputDeviceListenerLocked(listener);
            if (index >= 0) {
                InputDeviceListenerDelegate d = this.mInputDeviceListeners.get(index);
                d.removeCallbacksAndMessages(null);
                this.mInputDeviceListeners.remove(index);
            }
        }
    }

    private int findInputDeviceListenerLocked(InputDeviceListener listener) {
        int numListeners = this.mInputDeviceListeners.size();
        for (int i = 0; i < numListeners; i++) {
            if (this.mInputDeviceListeners.get(i).mListener == listener) {
                return i;
            }
        }
        return -1;
    }

    public int isInTabletMode() {
        try {
            return this.mIm.isInTabletMode();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void registerOnTabletModeChangedListener(OnTabletModeChangedListener listener, Handler handler) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mTabletModeLock) {
            if (this.mOnTabletModeChangedListeners == null) {
                initializeTabletModeListenerLocked();
            }
            int idx = findOnTabletModeChangedListenerLocked(listener);
            if (idx < 0) {
                OnTabletModeChangedListenerDelegate d = new OnTabletModeChangedListenerDelegate(listener, handler);
                this.mOnTabletModeChangedListeners.add(d);
            }
        }
    }

    public void unregisterOnTabletModeChangedListener(OnTabletModeChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mTabletModeLock) {
            int idx = findOnTabletModeChangedListenerLocked(listener);
            if (idx >= 0) {
                OnTabletModeChangedListenerDelegate d = this.mOnTabletModeChangedListeners.remove(idx);
                d.removeCallbacksAndMessages(null);
            }
        }
    }

    private void initializeTabletModeListenerLocked() {
        TabletModeChangedListener listener = new TabletModeChangedListener();
        try {
            this.mIm.registerTabletModeChangedListener(listener);
            this.mTabletModeChangedListener = listener;
            this.mOnTabletModeChangedListeners = new ArrayList<>();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    private int findOnTabletModeChangedListenerLocked(OnTabletModeChangedListener listener) {
        int N = this.mOnTabletModeChangedListeners.size();
        for (int i = 0; i < N; i++) {
            if (this.mOnTabletModeChangedListeners.get(i).mListener == listener) {
                return i;
            }
        }
        return -1;
    }

    public int isMicMuted() {
        try {
            return this.mIm.isMicMuted();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        try {
            return this.mIm.getKeyboardLayouts();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public List<String> getKeyboardLayoutDescriptorsForInputDevice(InputDevice device) {
        KeyboardLayout[] layouts = getKeyboardLayoutsForInputDevice(device.getIdentifier());
        List<String> res = new ArrayList<>();
        for (KeyboardLayout kl : layouts) {
            res.add(kl.getDescriptor());
        }
        return res;
    }

    public String getKeyboardLayoutTypeForLayoutDescriptor(String layoutDescriptor) {
        KeyboardLayout[] layouts = getKeyboardLayouts();
        for (KeyboardLayout kl : layouts) {
            if (layoutDescriptor.equals(kl.getDescriptor())) {
                return kl.getLayoutType();
            }
        }
        return "";
    }

    public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        try {
            return this.mIm.getKeyboardLayoutsForInputDevice(identifier);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) {
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayout(keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier) {
        try {
            return this.mIm.getCurrentKeyboardLayoutForInputDevice(identifier);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.setCurrentKeyboardLayoutForInputDevice(identifier, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getEnabledKeyboardLayoutsForInputDevice(identifier);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (identifier == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.addKeyboardLayoutForInputDevice(identifier, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) {
        if (identifier == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.removeKeyboardLayoutForInputDevice(identifier, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void remapModifierKey(int fromKey, int toKey) {
        try {
            this.mIm.remapModifierKey(fromKey, toKey);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void clearAllModifierKeyRemappings() {
        try {
            this.mIm.clearAllModifierKeyRemappings();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Map<Integer, Integer> getModifierKeyRemapping() {
        try {
            return this.mIm.getModifierKeyRemapping();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public TouchCalibration getTouchCalibration(String inputDeviceDescriptor, int surfaceRotation) {
        try {
            return this.mIm.getTouchCalibrationForInputDevice(inputDeviceDescriptor, surfaceRotation);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setTouchCalibration(String inputDeviceDescriptor, int surfaceRotation, TouchCalibration calibration) {
        try {
            this.mIm.setTouchCalibrationForInputDevice(inputDeviceDescriptor, surfaceRotation, calibration);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) {
        try {
            return this.mIm.getKeyboardLayoutForInputDevice(identifier, userId, imeInfo, imeSubtype);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype, String keyboardLayoutDescriptor) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.setKeyboardLayoutForInputDevice(identifier, userId, imeInfo, imeSubtype, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) {
        if (identifier == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayoutListForInputDevice(identifier, userId, imeInfo, imeSubtype);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void tryPointerSpeed(int speed) {
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        try {
            this.mIm.tryPointerSpeed(speed);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public float getMaximumObscuringOpacityForTouch() {
        Context context = ActivityThread.currentApplication();
        return InputSettings.getMaximumObscuringOpacityForTouch(context);
    }

    public boolean[] deviceHasKeys(int[] keyCodes) {
        return deviceHasKeys(-1, keyCodes);
    }

    public boolean[] deviceHasKeys(int id, int[] keyCodes) {
        boolean[] ret = new boolean[keyCodes.length];
        try {
            this.mIm.hasKeys(id, -256, keyCodes, ret);
            return ret;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getKeyCodeForKeyLocation(int deviceId, int locationKeyCode) {
        try {
            return this.mIm.getKeyCodeForKeyLocation(deviceId, locationKeyCode);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean injectInputEvent(InputEvent event, int mode, int targetUid) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (mode != 0 && mode != 2 && mode != 1) {
            throw new IllegalArgumentException("mode is invalid");
        }
        try {
            return this.mIm.injectInputEventToTarget(event, mode, targetUid);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean injectInputEvent(InputEvent event, int mode) {
        return injectInputEvent(event, mode, -1);
    }

    public VerifiedInputEvent verifyInputEvent(InputEvent event) {
        try {
            return this.mIm.verifyInputEvent(event);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setPointerIconType(int iconId) {
        try {
            this.mIm.setPointerIconType(iconId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setCustomPointerIcon(PointerIcon icon) {
        try {
            this.mIm.setCustomPointerIcon(icon);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean isStylusPointerIconEnabled() {
        if (this.mIsStylusPointerIconEnabled == null) {
            this.mIsStylusPointerIconEnabled = Boolean.valueOf(getContext().getResources().getBoolean(C4057R.bool.config_enableStylusPointerIcon));
        }
        return this.mIsStylusPointerIconEnabled.booleanValue();
    }

    public void requestPointerCapture(IBinder windowToken, boolean enable) {
        try {
            this.mIm.requestPointerCapture(windowToken, enable);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public InputMonitor monitorGestureInput(String name, int displayId) {
        try {
            return this.mIm.monitorGestureInput(new Binder(), name, displayId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public InputSensorInfo[] getSensorList(int deviceId) {
        try {
            return this.mIm.getSensorList(deviceId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean enableSensor(int deviceId, int sensorType, int samplingPeriodUs, int maxBatchReportLatencyUs) {
        try {
            return this.mIm.enableSensor(deviceId, sensorType, samplingPeriodUs, maxBatchReportLatencyUs);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void disableSensor(int deviceId, int sensorType) {
        try {
            this.mIm.disableSensor(deviceId, sensorType);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean flushSensor(int deviceId, int sensorType) {
        try {
            return this.mIm.flushSensor(deviceId, sensorType);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean registerSensorListener(IInputSensorEventListener listener) {
        try {
            return this.mIm.registerSensorListener(listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void unregisterSensorListener(IInputSensorEventListener listener) {
        try {
            this.mIm.unregisterSensorListener(listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void addPortAssociation(String inputPort, int displayPort) {
        try {
            this.mIm.addPortAssociation(inputPort, displayPort);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void removePortAssociation(String inputPort) {
        try {
            this.mIm.removePortAssociation(inputPort);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void addUniqueIdAssociation(String inputPort, String displayUniqueId) {
        try {
            this.mIm.addUniqueIdAssociation(inputPort, displayUniqueId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeUniqueIdAssociation(String inputPort) {
        try {
            this.mIm.removeUniqueIdAssociation(inputPort);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public HostUsiVersion getHostUsiVersion(Display display) {
        Objects.requireNonNull(display, "display should not be null");
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            for (int i = 0; i < this.mInputDevices.size(); i++) {
                InputDevice device = getInputDevice(this.mInputDevices.keyAt(i));
                if (device != null && device.getAssociatedDisplayId() == display.getDisplayId() && device.getHostUsiVersion() != null) {
                    return device.getHostUsiVersion();
                }
            }
            try {
                return this.mIm.getHostUsiVersionFromDisplayConfig(display.getDisplayId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    private void populateInputDevicesLocked() {
        if (this.mInputDevicesChangedListener == null) {
            InputDevicesChangedListener listener = new InputDevicesChangedListener();
            try {
                this.mIm.registerInputDevicesChangedListener(listener);
                this.mInputDevicesChangedListener = listener;
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }
        if (this.mInputDevices == null) {
            try {
                int[] ids = this.mIm.getInputDeviceIds();
                this.mInputDevices = new SparseArray<>();
                for (int id : ids) {
                    this.mInputDevices.put(id, null);
                }
            } catch (RemoteException ex2) {
                throw ex2.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputDevicesChanged(int[] deviceIdAndGeneration) {
        if (DEBUG) {
            Log.m112d(TAG, "Received input devices changed.");
        }
        synchronized (this.mInputDevicesLock) {
            int i = this.mInputDevices.size();
            while (true) {
                i--;
                if (i <= 0) {
                    break;
                }
                int deviceId = this.mInputDevices.keyAt(i);
                if (!containsDeviceId(deviceIdAndGeneration, deviceId)) {
                    if (DEBUG) {
                        Log.m112d(TAG, "Device removed: " + deviceId);
                    }
                    this.mInputDevices.removeAt(i);
                    sendMessageToInputDeviceListenersLocked(2, deviceId);
                }
            }
            for (int i2 = 0; i2 < deviceIdAndGeneration.length; i2 += 2) {
                int deviceId2 = deviceIdAndGeneration[i2];
                int index = this.mInputDevices.indexOfKey(deviceId2);
                if (index >= 0) {
                    InputDevice device = this.mInputDevices.valueAt(index);
                    if (device != null) {
                        int generation = deviceIdAndGeneration[i2 + 1];
                        if (device.getGeneration() != generation) {
                            if (DEBUG) {
                                Log.m112d(TAG, "Device changed: " + deviceId2);
                            }
                            this.mInputDevices.setValueAt(index, null);
                            sendMessageToInputDeviceListenersLocked(3, deviceId2);
                        }
                    }
                } else {
                    if (DEBUG) {
                        Log.m112d(TAG, "Device added: " + deviceId2);
                    }
                    this.mInputDevices.put(deviceId2, null);
                    sendMessageToInputDeviceListenersLocked(1, deviceId2);
                }
            }
        }
    }

    private void sendMessageToInputDeviceListenersLocked(int what, int deviceId) {
        int numListeners = this.mInputDeviceListeners.size();
        for (int i = 0; i < numListeners; i++) {
            InputDeviceListenerDelegate listener = this.mInputDeviceListeners.get(i);
            listener.sendMessage(listener.obtainMessage(what, deviceId, 0));
        }
    }

    private static boolean containsDeviceId(int[] deviceIdAndGeneration, int deviceId) {
        for (int i = 0; i < deviceIdAndGeneration.length; i += 2) {
            if (deviceIdAndGeneration[i] == deviceId) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTabletModeChanged(long whenNanos, boolean inTabletMode) {
        if (DEBUG) {
            Log.m112d(TAG, "Received tablet mode changed: whenNanos=" + whenNanos + ", inTabletMode=" + inTabletMode);
        }
        synchronized (this.mTabletModeLock) {
            int numListeners = this.mOnTabletModeChangedListeners.size();
            for (int i = 0; i < numListeners; i++) {
                OnTabletModeChangedListenerDelegate listener = this.mOnTabletModeChangedListeners.get(i);
                listener.sendTabletModeChanged(whenNanos, inTabletMode);
            }
        }
    }

    public String getInputDeviceBluetoothAddress(int deviceId) {
        try {
            return this.mIm.getInputDeviceBluetoothAddress(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Vibrator getInputDeviceVibrator(int deviceId, int vibratorId) {
        return new InputDeviceVibrator(this, deviceId, vibratorId);
    }

    public VibratorManager getInputDeviceVibratorManager(int deviceId) {
        return new InputDeviceVibratorManager(this, deviceId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getVibratorIds(int deviceId) {
        try {
            return this.mIm.getVibratorIds(deviceId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void vibrate(int deviceId, VibrationEffect effect, IBinder token) {
        try {
            this.mIm.vibrate(deviceId, effect, token);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void vibrate(int deviceId, CombinedVibration effect, IBinder token) {
        try {
            this.mIm.vibrateCombined(deviceId, effect, token);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelVibrate(int deviceId, IBinder token) {
        try {
            this.mIm.cancelVibrate(deviceId, token);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVibrating(int deviceId) {
        try {
            return this.mIm.isVibrating(deviceId);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean registerVibratorStateListener(int deviceId, IVibratorStateListener listener) {
        try {
            return this.mIm.registerVibratorStateListener(deviceId, listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean unregisterVibratorStateListener(int deviceId, IVibratorStateListener listener) {
        try {
            return this.mIm.unregisterVibratorStateListener(deviceId, listener);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public SensorManager getInputDeviceSensorManager(int deviceId) {
        if (this.mInputDeviceSensorManager == null) {
            this.mInputDeviceSensorManager = new InputDeviceSensorManager(this);
        }
        return this.mInputDeviceSensorManager.getSensorManager(deviceId);
    }

    public BatteryState getInputDeviceBatteryState(int deviceId, boolean hasBattery) {
        if (!hasBattery) {
            return new LocalBatteryState();
        }
        try {
            IInputDeviceBatteryState state = this.mIm.getBatteryState(deviceId);
            return new LocalBatteryState(state.isPresent, state.status, state.capacity);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public LightsManager getInputDeviceLightsManager(int deviceId) {
        return new InputDeviceLightsManager(this, deviceId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Light> getLights(int deviceId) {
        try {
            return this.mIm.getLights(deviceId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LightState getLightState(int deviceId, Light light) {
        try {
            return this.mIm.getLightState(deviceId, light.getId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestLights(int deviceId, LightsRequest request, IBinder token) {
        try {
            List<Integer> lightIdList = request.getLights();
            int[] lightIds = new int[lightIdList.size()];
            for (int i = 0; i < lightIds.length; i++) {
                lightIds[i] = lightIdList.get(i).intValue();
            }
            List<LightState> lightStateList = request.getLightStates();
            this.mIm.setLightStates(deviceId, lightIds, (LightState[]) lightStateList.toArray(new LightState[0]), token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openLightSession(int deviceId, String opPkg, IBinder token) {
        try {
            this.mIm.openLightSession(deviceId, opPkg, token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeLightSession(int deviceId, IBinder token) {
        try {
            this.mIm.closeLightSession(deviceId, token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void cancelCurrentTouch() {
        try {
            this.mIm.cancelCurrentTouch();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void pilferPointers(IBinder inputChannelToken) {
        try {
            this.mIm.pilferPointers(inputChannelToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addInputDeviceBatteryListener(int deviceId, Executor executor, InputDeviceBatteryListener listener) {
        Objects.requireNonNull(executor, "executor should not be null");
        Objects.requireNonNull(listener, "listener should not be null");
        synchronized (this.mBatteryListenersLock) {
            if (this.mBatteryListeners == null) {
                this.mBatteryListeners = new SparseArray<>();
                this.mInputDeviceBatteryListener = new LocalInputDeviceBatteryListener();
            }
            RegisteredBatteryListeners listenersForDevice = this.mBatteryListeners.get(deviceId);
            if (listenersForDevice == null) {
                listenersForDevice = new RegisteredBatteryListeners();
                this.mBatteryListeners.put(deviceId, listenersForDevice);
                try {
                    this.mIm.registerBatteryListener(deviceId, this.mInputDeviceBatteryListener);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } else {
                int numDelegates = listenersForDevice.mDelegates.size();
                for (int i = 0; i < numDelegates; i++) {
                    InputDeviceBatteryListener registeredListener = listenersForDevice.mDelegates.get(i).mListener;
                    if (Objects.equals(listener, registeredListener)) {
                        throw new IllegalArgumentException("Attempting to register an InputDeviceBatteryListener that has already been registered for deviceId: " + deviceId);
                    }
                }
            }
            InputDeviceBatteryListenerDelegate delegate = new InputDeviceBatteryListenerDelegate(listener, executor);
            listenersForDevice.mDelegates.add(delegate);
            if (listenersForDevice.mInputDeviceBatteryState != null) {
                delegate.notifyBatteryStateChanged(listenersForDevice.mInputDeviceBatteryState);
            }
        }
    }

    public void removeInputDeviceBatteryListener(int deviceId, InputDeviceBatteryListener listener) {
        Objects.requireNonNull(listener, "listener should not be null");
        synchronized (this.mBatteryListenersLock) {
            SparseArray<RegisteredBatteryListeners> sparseArray = this.mBatteryListeners;
            if (sparseArray == null) {
                return;
            }
            RegisteredBatteryListeners listenersForDevice = sparseArray.get(deviceId);
            if (listenersForDevice == null) {
                return;
            }
            List<InputDeviceBatteryListenerDelegate> delegates = listenersForDevice.mDelegates;
            int i = 0;
            while (i < delegates.size()) {
                if (Objects.equals(listener, delegates.get(i).mListener)) {
                    delegates.remove(i);
                } else {
                    i++;
                }
            }
            if (delegates.isEmpty()) {
                this.mBatteryListeners.remove(deviceId);
                try {
                    this.mIm.unregisterBatteryListener(deviceId, this.mInputDeviceBatteryListener);
                    if (this.mBatteryListeners.size() == 0) {
                        this.mBatteryListeners = null;
                        this.mInputDeviceBatteryListener = null;
                    }
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    public boolean areTouchpadGesturesAvailable(Context context) {
        return true;
    }

    public void registerKeyboardBacklightListener(Executor executor, KeyboardBacklightListener listener) throws IllegalArgumentException {
        Objects.requireNonNull(executor, "executor should not be null");
        Objects.requireNonNull(listener, "listener should not be null");
        synchronized (this.mKeyboardBacklightListenerLock) {
            if (this.mKeyboardBacklightListener == null) {
                this.mKeyboardBacklightListeners = new ArrayList<>();
                LocalKeyboardBacklightListener localKeyboardBacklightListener = new LocalKeyboardBacklightListener();
                this.mKeyboardBacklightListener = localKeyboardBacklightListener;
                try {
                    this.mIm.registerKeyboardBacklightListener(localKeyboardBacklightListener);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            int numListeners = this.mKeyboardBacklightListeners.size();
            for (int i = 0; i < numListeners; i++) {
                if (this.mKeyboardBacklightListeners.get(i).mListener == listener) {
                    throw new IllegalArgumentException("Listener has already been registered!");
                }
            }
            KeyboardBacklightListenerDelegate delegate = new KeyboardBacklightListenerDelegate(listener, executor);
            this.mKeyboardBacklightListeners.add(delegate);
        }
    }

    public void unregisterKeyboardBacklightListener(final KeyboardBacklightListener listener) {
        Objects.requireNonNull(listener, "listener should not be null");
        synchronized (this.mKeyboardBacklightListenerLock) {
            ArrayList<KeyboardBacklightListenerDelegate> arrayList = this.mKeyboardBacklightListeners;
            if (arrayList == null) {
                return;
            }
            arrayList.removeIf(new Predicate() { // from class: android.hardware.input.InputManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return InputManager.lambda$unregisterKeyboardBacklightListener$0(InputManager.KeyboardBacklightListener.this, (InputManager.KeyboardBacklightListenerDelegate) obj);
                }
            });
            if (this.mKeyboardBacklightListeners.isEmpty()) {
                try {
                    this.mIm.unregisterKeyboardBacklightListener(this.mKeyboardBacklightListener);
                    this.mKeyboardBacklightListeners = null;
                    this.mKeyboardBacklightListener = null;
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$unregisterKeyboardBacklightListener$0(KeyboardBacklightListener listener, KeyboardBacklightListenerDelegate delegate) {
        return delegate.mListener == listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class InputDevicesChangedListener extends IInputDevicesChangedListener.Stub {
        private InputDevicesChangedListener() {
        }

        @Override // android.hardware.input.IInputDevicesChangedListener
        public void onInputDevicesChanged(int[] deviceIdAndGeneration) throws RemoteException {
            InputManager.this.onInputDevicesChanged(deviceIdAndGeneration);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class InputDeviceListenerDelegate extends Handler {
        public final InputDeviceListener mListener;

        public InputDeviceListenerDelegate(InputDeviceListener listener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper());
            this.mListener = listener;
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.mListener.onInputDeviceAdded(msg.arg1);
                    return;
                case 2:
                    this.mListener.onInputDeviceRemoved(msg.arg1);
                    return;
                case 3:
                    this.mListener.onInputDeviceChanged(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class TabletModeChangedListener extends ITabletModeChangedListener.Stub {
        private TabletModeChangedListener() {
        }

        @Override // android.hardware.input.ITabletModeChangedListener
        public void onTabletModeChanged(long whenNanos, boolean inTabletMode) {
            InputManager.this.onTabletModeChanged(whenNanos, inTabletMode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class OnTabletModeChangedListenerDelegate extends Handler {
        private static final int MSG_TABLET_MODE_CHANGED = 0;
        public final OnTabletModeChangedListener mListener;

        public OnTabletModeChangedListenerDelegate(OnTabletModeChangedListener listener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper());
            this.mListener = listener;
        }

        public void sendTabletModeChanged(long whenNanos, boolean inTabletMode) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = (int) whenNanos;
            args.argi2 = (int) (whenNanos >> 32);
            args.arg1 = Boolean.valueOf(inTabletMode);
            obtainMessage(0, args).sendToTarget();
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                SomeArgs args = (SomeArgs) msg.obj;
                long whenNanos = (args.argi1 & 4294967295L) | (args.argi2 << 32);
                boolean inTabletMode = ((Boolean) args.arg1).booleanValue();
                this.mListener.onTabletModeChanged(whenNanos, inTabletMode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class LocalBatteryState extends BatteryState {
        private final float mCapacity;
        private final boolean mIsPresent;
        private final int mStatus;

        LocalBatteryState() {
            this(false, 1, Float.NaN);
        }

        LocalBatteryState(boolean isPresent, int status, float capacity) {
            this.mIsPresent = isPresent;
            this.mStatus = status;
            this.mCapacity = capacity;
        }

        @Override // android.hardware.BatteryState
        public boolean isPresent() {
            return this.mIsPresent;
        }

        @Override // android.hardware.BatteryState
        public int getStatus() {
            return this.mStatus;
        }

        @Override // android.hardware.BatteryState
        public float getCapacity() {
            return this.mCapacity;
        }
    }

    /* loaded from: classes2.dex */
    private static final class RegisteredBatteryListeners {
        final List<InputDeviceBatteryListenerDelegate> mDelegates;
        IInputDeviceBatteryState mInputDeviceBatteryState;

        private RegisteredBatteryListeners() {
            this.mDelegates = new ArrayList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class InputDeviceBatteryListenerDelegate {
        final Executor mExecutor;
        final InputDeviceBatteryListener mListener;

        InputDeviceBatteryListenerDelegate(InputDeviceBatteryListener listener, Executor executor) {
            this.mListener = listener;
            this.mExecutor = executor;
        }

        void notifyBatteryStateChanged(final IInputDeviceBatteryState state) {
            this.mExecutor.execute(new Runnable() { // from class: android.hardware.input.InputManager$InputDeviceBatteryListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InputManager.InputDeviceBatteryListenerDelegate.this.lambda$notifyBatteryStateChanged$0(state);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyBatteryStateChanged$0(IInputDeviceBatteryState state) {
            this.mListener.onBatteryStateChanged(state.deviceId, state.updateTime, new LocalBatteryState(state.isPresent, state.status, state.capacity));
        }
    }

    /* loaded from: classes2.dex */
    private class LocalInputDeviceBatteryListener extends IInputDeviceBatteryListener.Stub {
        private LocalInputDeviceBatteryListener() {
        }

        @Override // android.hardware.input.IInputDeviceBatteryListener
        public void onBatteryStateChanged(IInputDeviceBatteryState state) {
            synchronized (InputManager.this.mBatteryListenersLock) {
                if (InputManager.this.mBatteryListeners == null) {
                    return;
                }
                RegisteredBatteryListeners entry = (RegisteredBatteryListeners) InputManager.this.mBatteryListeners.get(state.deviceId);
                if (entry == null) {
                    return;
                }
                entry.mInputDeviceBatteryState = state;
                int numDelegates = entry.mDelegates.size();
                for (int i = 0; i < numDelegates; i++) {
                    entry.mDelegates.get(i).notifyBatteryStateChanged(entry.mInputDeviceBatteryState);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class LocalKeyboardBacklightState extends KeyboardBacklightState {
        private final int mBrightnessLevel;
        private final int mMaxBrightnessLevel;

        LocalKeyboardBacklightState(int brightnessLevel, int maxBrightnessLevel) {
            this.mBrightnessLevel = brightnessLevel;
            this.mMaxBrightnessLevel = maxBrightnessLevel;
        }

        @Override // android.hardware.input.KeyboardBacklightState
        public int getBrightnessLevel() {
            return this.mBrightnessLevel;
        }

        @Override // android.hardware.input.KeyboardBacklightState
        public int getMaxBrightnessLevel() {
            return this.mMaxBrightnessLevel;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class KeyboardBacklightListenerDelegate {
        final Executor mExecutor;
        final KeyboardBacklightListener mListener;

        KeyboardBacklightListenerDelegate(KeyboardBacklightListener listener, Executor executor) {
            this.mListener = listener;
            this.mExecutor = executor;
        }

        void notifyKeyboardBacklightChange(final int deviceId, final IKeyboardBacklightState state, final boolean isTriggeredByKeyPress) {
            this.mExecutor.execute(new Runnable() { // from class: android.hardware.input.InputManager$KeyboardBacklightListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InputManager.KeyboardBacklightListenerDelegate.this.lambda$notifyKeyboardBacklightChange$0(deviceId, state, isTriggeredByKeyPress);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyKeyboardBacklightChange$0(int deviceId, IKeyboardBacklightState state, boolean isTriggeredByKeyPress) {
            this.mListener.onKeyboardBacklightChanged(deviceId, new LocalKeyboardBacklightState(state.brightnessLevel, state.maxBrightnessLevel), isTriggeredByKeyPress);
        }
    }

    /* loaded from: classes2.dex */
    private class LocalKeyboardBacklightListener extends IKeyboardBacklightListener.Stub {
        private LocalKeyboardBacklightListener() {
        }

        @Override // android.hardware.input.IKeyboardBacklightListener
        public void onBrightnessChanged(int deviceId, IKeyboardBacklightState state, boolean isTriggeredByKeyPress) {
            synchronized (InputManager.this.mKeyboardBacklightListenerLock) {
                if (InputManager.this.mKeyboardBacklightListeners == null) {
                    return;
                }
                int numListeners = InputManager.this.mKeyboardBacklightListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    ((KeyboardBacklightListenerDelegate) InputManager.this.mKeyboardBacklightListeners.get(i)).notifyKeyboardBacklightChange(deviceId, state, isTriggeredByKeyPress);
                }
            }
        }
    }
}
