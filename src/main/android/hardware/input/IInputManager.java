package android.hardware.input;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.hardware.input.IInputDeviceBatteryListener;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputSensorEventListener;
import android.hardware.input.IKeyboardBacklightListener;
import android.hardware.input.ITabletModeChangedListener;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.p008os.Binder;
import android.p008os.CombinedVibration;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.IVibratorStateListener;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import android.p008os.VibrationEffect;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.InputMonitor;
import android.view.PointerIcon;
import android.view.VerifiedInputEvent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public interface IInputManager extends IInterface {
    void addKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) throws RemoteException;

    void addPortAssociation(String str, int i) throws RemoteException;

    void addUniqueIdAssociation(String str, String str2) throws RemoteException;

    void cancelCurrentTouch() throws RemoteException;

    void cancelVibrate(int i, IBinder iBinder) throws RemoteException;

    void clearAllModifierKeyRemappings() throws RemoteException;

    void closeLightSession(int i, IBinder iBinder) throws RemoteException;

    void disableInputDevice(int i) throws RemoteException;

    void disableSensor(int i, int i2) throws RemoteException;

    void enableInputDevice(int i) throws RemoteException;

    boolean enableSensor(int i, int i2, int i3, int i4) throws RemoteException;

    boolean flushSensor(int i, int i2) throws RemoteException;

    IInputDeviceBatteryState getBatteryState(int i) throws RemoteException;

    String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) throws RemoteException;

    String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) throws RemoteException;

    HostUsiVersion getHostUsiVersionFromDisplayConfig(int i) throws RemoteException;

    InputDevice getInputDevice(int i) throws RemoteException;

    String getInputDeviceBluetoothAddress(int i) throws RemoteException;

    int[] getInputDeviceIds() throws RemoteException;

    int getKeyCodeForKeyLocation(int i, int i2) throws RemoteException;

    KeyboardLayout getKeyboardLayout(String str) throws RemoteException;

    String getKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) throws RemoteException;

    KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) throws RemoteException;

    KeyboardLayout[] getKeyboardLayouts() throws RemoteException;

    KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier inputDeviceIdentifier) throws RemoteException;

    LightState getLightState(int i, int i2) throws RemoteException;

    List<Light> getLights(int i) throws RemoteException;

    Map getModifierKeyRemapping() throws RemoteException;

    InputSensorInfo[] getSensorList(int i) throws RemoteException;

    TouchCalibration getTouchCalibrationForInputDevice(String str, int i) throws RemoteException;

    String getVelocityTrackerStrategy() throws RemoteException;

    int[] getVibratorIds(int i) throws RemoteException;

    boolean hasKeys(int i, int i2, int[] iArr, boolean[] zArr) throws RemoteException;

    boolean injectInputEvent(InputEvent inputEvent, int i) throws RemoteException;

    boolean injectInputEventToTarget(InputEvent inputEvent, int i, int i2) throws RemoteException;

    int isInTabletMode() throws RemoteException;

    boolean isInputDeviceEnabled(int i) throws RemoteException;

    int isMicMuted() throws RemoteException;

    boolean isVibrating(int i) throws RemoteException;

    InputMonitor monitorGestureInput(IBinder iBinder, String str, int i) throws RemoteException;

    void openLightSession(int i, String str, IBinder iBinder) throws RemoteException;

    void pilferPointers(IBinder iBinder) throws RemoteException;

    void registerBatteryListener(int i, IInputDeviceBatteryListener iInputDeviceBatteryListener) throws RemoteException;

    void registerInputDevicesChangedListener(IInputDevicesChangedListener iInputDevicesChangedListener) throws RemoteException;

    void registerKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener) throws RemoteException;

    boolean registerSensorListener(IInputSensorEventListener iInputSensorEventListener) throws RemoteException;

    void registerTabletModeChangedListener(ITabletModeChangedListener iTabletModeChangedListener) throws RemoteException;

    boolean registerVibratorStateListener(int i, IVibratorStateListener iVibratorStateListener) throws RemoteException;

    void remapModifierKey(int i, int i2) throws RemoteException;

    void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) throws RemoteException;

    void removePortAssociation(String str) throws RemoteException;

    void removeUniqueIdAssociation(String str) throws RemoteException;

    void requestPointerCapture(IBinder iBinder, boolean z) throws RemoteException;

    void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, String str) throws RemoteException;

    void setCustomPointerIcon(PointerIcon pointerIcon) throws RemoteException;

    void setKeyboardLayoutForInputDevice(InputDeviceIdentifier inputDeviceIdentifier, int i, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, String str) throws RemoteException;

    void setLightStates(int i, int[] iArr, LightState[] lightStateArr, IBinder iBinder) throws RemoteException;

    void setPointerIconType(int i) throws RemoteException;

    void setTouchCalibrationForInputDevice(String str, int i, TouchCalibration touchCalibration) throws RemoteException;

    void tryPointerSpeed(int i) throws RemoteException;

    void unregisterBatteryListener(int i, IInputDeviceBatteryListener iInputDeviceBatteryListener) throws RemoteException;

    void unregisterKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener) throws RemoteException;

    void unregisterSensorListener(IInputSensorEventListener iInputSensorEventListener) throws RemoteException;

    boolean unregisterVibratorStateListener(int i, IVibratorStateListener iVibratorStateListener) throws RemoteException;

    VerifiedInputEvent verifyInputEvent(InputEvent inputEvent) throws RemoteException;

    void vibrate(int i, VibrationEffect vibrationEffect, IBinder iBinder) throws RemoteException;

    void vibrateCombined(int i, CombinedVibration combinedVibration, IBinder iBinder) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInputManager {
        @Override // android.hardware.input.IInputManager
        public String getVelocityTrackerStrategy() throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public InputDevice getInputDevice(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public int[] getInputDeviceIds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public boolean isInputDeviceEnabled(int deviceId) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public void enableInputDevice(int deviceId) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void disableInputDevice(int deviceId) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public int getKeyCodeForKeyLocation(int deviceId, int locationKeyCode) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.input.IInputManager
        public void tryPointerSpeed(int speed) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public boolean injectInputEvent(InputEvent ev, int mode) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public boolean injectInputEventToTarget(InputEvent ev, int mode, int targetUid) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public VerifiedInputEvent verifyInputEvent(InputEvent ev) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public TouchCalibration getTouchCalibrationForInputDevice(String inputDeviceDescriptor, int rotation) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void setTouchCalibrationForInputDevice(String inputDeviceDescriptor, int rotation, TouchCalibration calibration) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public KeyboardLayout[] getKeyboardLayouts() throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype, String keyboardLayoutDescriptor) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void remapModifierKey(int fromKey, int toKey) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void clearAllModifierKeyRemappings() throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public Map getModifierKeyRemapping() throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public int isInTabletMode() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.input.IInputManager
        public void registerTabletModeChangedListener(ITabletModeChangedListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public int isMicMuted() throws RemoteException {
            return 0;
        }

        @Override // android.hardware.input.IInputManager
        public void vibrate(int deviceId, VibrationEffect effect, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void vibrateCombined(int deviceId, CombinedVibration vibration, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void cancelVibrate(int deviceId, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public int[] getVibratorIds(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public boolean isVibrating(int deviceId) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public boolean registerVibratorStateListener(int deviceId, IVibratorStateListener listener) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public boolean unregisterVibratorStateListener(int deviceId, IVibratorStateListener listener) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public IInputDeviceBatteryState getBatteryState(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void setPointerIconType(int typeId) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void setCustomPointerIcon(PointerIcon icon) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void requestPointerCapture(IBinder inputChannelToken, boolean enabled) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public InputMonitor monitorGestureInput(IBinder token, String name, int displayId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void addPortAssociation(String inputPort, int displayPort) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void removePortAssociation(String inputPort) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void addUniqueIdAssociation(String inputPort, String displayUniqueId) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void removeUniqueIdAssociation(String inputPort) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public InputSensorInfo[] getSensorList(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public boolean registerSensorListener(IInputSensorEventListener listener) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public void unregisterSensorListener(IInputSensorEventListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public boolean enableSensor(int deviceId, int sensorType, int samplingPeriodUs, int maxBatchReportLatencyUs) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public void disableSensor(int deviceId, int sensorType) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public boolean flushSensor(int deviceId, int sensorType) throws RemoteException {
            return false;
        }

        @Override // android.hardware.input.IInputManager
        public List<Light> getLights(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public LightState getLightState(int deviceId, int lightId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void setLightStates(int deviceId, int[] lightIds, LightState[] states, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void openLightSession(int deviceId, String opPkg, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void closeLightSession(int deviceId, IBinder token) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void cancelCurrentTouch() throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void registerBatteryListener(int deviceId, IInputDeviceBatteryListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void unregisterBatteryListener(int deviceId, IInputDeviceBatteryListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public String getInputDeviceBluetoothAddress(int deviceId) throws RemoteException {
            return null;
        }

        @Override // android.hardware.input.IInputManager
        public void pilferPointers(IBinder inputChannelToken) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void registerKeyboardBacklightListener(IKeyboardBacklightListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public void unregisterKeyboardBacklightListener(IKeyboardBacklightListener listener) throws RemoteException {
        }

        @Override // android.hardware.input.IInputManager
        public HostUsiVersion getHostUsiVersionFromDisplayConfig(int displayId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInputManager {
        public static final String DESCRIPTOR = "android.hardware.input.IInputManager";
        static final int TRANSACTION_addKeyboardLayoutForInputDevice = 21;
        static final int TRANSACTION_addPortAssociation = 45;
        static final int TRANSACTION_addUniqueIdAssociation = 47;
        static final int TRANSACTION_cancelCurrentTouch = 60;
        static final int TRANSACTION_cancelVibrate = 35;
        static final int TRANSACTION_clearAllModifierKeyRemappings = 27;
        static final int TRANSACTION_closeLightSession = 59;
        static final int TRANSACTION_disableInputDevice = 6;
        static final int TRANSACTION_disableSensor = 53;
        static final int TRANSACTION_enableInputDevice = 5;
        static final int TRANSACTION_enableSensor = 52;
        static final int TRANSACTION_flushSensor = 54;
        static final int TRANSACTION_getBatteryState = 40;
        static final int TRANSACTION_getCurrentKeyboardLayoutForInputDevice = 18;
        static final int TRANSACTION_getEnabledKeyboardLayoutsForInputDevice = 20;
        static final int TRANSACTION_getHostUsiVersionFromDisplayConfig = 67;
        static final int TRANSACTION_getInputDevice = 2;
        static final int TRANSACTION_getInputDeviceBluetoothAddress = 63;
        static final int TRANSACTION_getInputDeviceIds = 3;
        static final int TRANSACTION_getKeyCodeForKeyLocation = 8;
        static final int TRANSACTION_getKeyboardLayout = 17;
        static final int TRANSACTION_getKeyboardLayoutForInputDevice = 23;
        static final int TRANSACTION_getKeyboardLayoutListForInputDevice = 25;
        static final int TRANSACTION_getKeyboardLayouts = 15;
        static final int TRANSACTION_getKeyboardLayoutsForInputDevice = 16;
        static final int TRANSACTION_getLightState = 56;
        static final int TRANSACTION_getLights = 55;
        static final int TRANSACTION_getModifierKeyRemapping = 28;
        static final int TRANSACTION_getSensorList = 49;
        static final int TRANSACTION_getTouchCalibrationForInputDevice = 13;
        static final int TRANSACTION_getVelocityTrackerStrategy = 1;
        static final int TRANSACTION_getVibratorIds = 36;
        static final int TRANSACTION_hasKeys = 7;
        static final int TRANSACTION_injectInputEvent = 10;
        static final int TRANSACTION_injectInputEventToTarget = 11;
        static final int TRANSACTION_isInTabletMode = 30;
        static final int TRANSACTION_isInputDeviceEnabled = 4;
        static final int TRANSACTION_isMicMuted = 32;
        static final int TRANSACTION_isVibrating = 37;
        static final int TRANSACTION_monitorGestureInput = 44;
        static final int TRANSACTION_openLightSession = 58;
        static final int TRANSACTION_pilferPointers = 64;
        static final int TRANSACTION_registerBatteryListener = 61;
        static final int TRANSACTION_registerInputDevicesChangedListener = 29;
        static final int TRANSACTION_registerKeyboardBacklightListener = 65;
        static final int TRANSACTION_registerSensorListener = 50;
        static final int TRANSACTION_registerTabletModeChangedListener = 31;
        static final int TRANSACTION_registerVibratorStateListener = 38;
        static final int TRANSACTION_remapModifierKey = 26;
        static final int TRANSACTION_removeKeyboardLayoutForInputDevice = 22;
        static final int TRANSACTION_removePortAssociation = 46;
        static final int TRANSACTION_removeUniqueIdAssociation = 48;
        static final int TRANSACTION_requestPointerCapture = 43;
        static final int TRANSACTION_setCurrentKeyboardLayoutForInputDevice = 19;
        static final int TRANSACTION_setCustomPointerIcon = 42;
        static final int TRANSACTION_setKeyboardLayoutForInputDevice = 24;
        static final int TRANSACTION_setLightStates = 57;
        static final int TRANSACTION_setPointerIconType = 41;
        static final int TRANSACTION_setTouchCalibrationForInputDevice = 14;
        static final int TRANSACTION_tryPointerSpeed = 9;
        static final int TRANSACTION_unregisterBatteryListener = 62;
        static final int TRANSACTION_unregisterKeyboardBacklightListener = 66;
        static final int TRANSACTION_unregisterSensorListener = 51;
        static final int TRANSACTION_unregisterVibratorStateListener = 39;
        static final int TRANSACTION_verifyInputEvent = 12;
        static final int TRANSACTION_vibrate = 33;
        static final int TRANSACTION_vibrateCombined = 34;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IInputManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputManager)) {
                return (IInputManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getVelocityTrackerStrategy";
                case 2:
                    return "getInputDevice";
                case 3:
                    return "getInputDeviceIds";
                case 4:
                    return "isInputDeviceEnabled";
                case 5:
                    return "enableInputDevice";
                case 6:
                    return "disableInputDevice";
                case 7:
                    return "hasKeys";
                case 8:
                    return "getKeyCodeForKeyLocation";
                case 9:
                    return "tryPointerSpeed";
                case 10:
                    return "injectInputEvent";
                case 11:
                    return "injectInputEventToTarget";
                case 12:
                    return "verifyInputEvent";
                case 13:
                    return "getTouchCalibrationForInputDevice";
                case 14:
                    return "setTouchCalibrationForInputDevice";
                case 15:
                    return "getKeyboardLayouts";
                case 16:
                    return "getKeyboardLayoutsForInputDevice";
                case 17:
                    return "getKeyboardLayout";
                case 18:
                    return "getCurrentKeyboardLayoutForInputDevice";
                case 19:
                    return "setCurrentKeyboardLayoutForInputDevice";
                case 20:
                    return "getEnabledKeyboardLayoutsForInputDevice";
                case 21:
                    return "addKeyboardLayoutForInputDevice";
                case 22:
                    return "removeKeyboardLayoutForInputDevice";
                case 23:
                    return "getKeyboardLayoutForInputDevice";
                case 24:
                    return "setKeyboardLayoutForInputDevice";
                case 25:
                    return "getKeyboardLayoutListForInputDevice";
                case 26:
                    return "remapModifierKey";
                case 27:
                    return "clearAllModifierKeyRemappings";
                case 28:
                    return "getModifierKeyRemapping";
                case 29:
                    return "registerInputDevicesChangedListener";
                case 30:
                    return "isInTabletMode";
                case 31:
                    return "registerTabletModeChangedListener";
                case 32:
                    return "isMicMuted";
                case 33:
                    return "vibrate";
                case 34:
                    return "vibrateCombined";
                case 35:
                    return "cancelVibrate";
                case 36:
                    return "getVibratorIds";
                case 37:
                    return "isVibrating";
                case 38:
                    return "registerVibratorStateListener";
                case 39:
                    return "unregisterVibratorStateListener";
                case 40:
                    return "getBatteryState";
                case 41:
                    return "setPointerIconType";
                case 42:
                    return "setCustomPointerIcon";
                case 43:
                    return "requestPointerCapture";
                case 44:
                    return "monitorGestureInput";
                case 45:
                    return "addPortAssociation";
                case 46:
                    return "removePortAssociation";
                case 47:
                    return "addUniqueIdAssociation";
                case 48:
                    return "removeUniqueIdAssociation";
                case 49:
                    return "getSensorList";
                case 50:
                    return "registerSensorListener";
                case 51:
                    return "unregisterSensorListener";
                case 52:
                    return "enableSensor";
                case 53:
                    return "disableSensor";
                case 54:
                    return "flushSensor";
                case 55:
                    return "getLights";
                case 56:
                    return "getLightState";
                case 57:
                    return "setLightStates";
                case 58:
                    return "openLightSession";
                case 59:
                    return "closeLightSession";
                case 60:
                    return "cancelCurrentTouch";
                case 61:
                    return "registerBatteryListener";
                case 62:
                    return "unregisterBatteryListener";
                case 63:
                    return "getInputDeviceBluetoothAddress";
                case 64:
                    return "pilferPointers";
                case 65:
                    return "registerKeyboardBacklightListener";
                case 66:
                    return "unregisterKeyboardBacklightListener";
                case 67:
                    return "getHostUsiVersionFromDisplayConfig";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean[] _arg3;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _result = getVelocityTrackerStrategy();
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            InputDevice _result2 = getInputDevice(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int[] _result3 = getInputDeviceIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result3);
                            break;
                        case 4:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isInputDeviceEnabled(_arg02);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            enableInputDevice(_arg03);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            disableInputDevice(_arg04);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg05 = data.readInt();
                            int _arg1 = data.readInt();
                            int[] _arg2 = data.createIntArray();
                            int _arg3_length = data.readInt();
                            if (_arg3_length < 0) {
                                _arg3 = null;
                            } else {
                                _arg3 = new boolean[_arg3_length];
                            }
                            data.enforceNoDataAvail();
                            boolean _result5 = hasKeys(_arg05, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            reply.writeBooleanArray(_arg3);
                            break;
                        case 8:
                            int _arg06 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getKeyCodeForKeyLocation(_arg06, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 9:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            tryPointerSpeed(_arg07);
                            reply.writeNoException();
                            break;
                        case 10:
                            InputEvent _arg08 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = injectInputEvent(_arg08, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 11:
                            InputEvent _arg09 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = injectInputEventToTarget(_arg09, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 12:
                            InputEvent _arg010 = (InputEvent) data.readTypedObject(InputEvent.CREATOR);
                            data.enforceNoDataAvail();
                            VerifiedInputEvent _result9 = verifyInputEvent(_arg010);
                            reply.writeNoException();
                            reply.writeTypedObject(_result9, 1);
                            break;
                        case 13:
                            String _arg011 = data.readString();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            TouchCalibration _result10 = getTouchCalibrationForInputDevice(_arg011, _arg15);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 14:
                            String _arg012 = data.readString();
                            int _arg16 = data.readInt();
                            TouchCalibration _arg23 = (TouchCalibration) data.readTypedObject(TouchCalibration.CREATOR);
                            data.enforceNoDataAvail();
                            setTouchCalibrationForInputDevice(_arg012, _arg16, _arg23);
                            reply.writeNoException();
                            break;
                        case 15:
                            KeyboardLayout[] _result11 = getKeyboardLayouts();
                            reply.writeNoException();
                            reply.writeTypedArray(_result11, 1);
                            break;
                        case 16:
                            InputDeviceIdentifier _arg013 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            data.enforceNoDataAvail();
                            KeyboardLayout[] _result12 = getKeyboardLayoutsForInputDevice(_arg013);
                            reply.writeNoException();
                            reply.writeTypedArray(_result12, 1);
                            break;
                        case 17:
                            String _arg014 = data.readString();
                            data.enforceNoDataAvail();
                            KeyboardLayout _result13 = getKeyboardLayout(_arg014);
                            reply.writeNoException();
                            reply.writeTypedObject(_result13, 1);
                            break;
                        case 18:
                            InputDeviceIdentifier _arg015 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            data.enforceNoDataAvail();
                            String _result14 = getCurrentKeyboardLayoutForInputDevice(_arg015);
                            reply.writeNoException();
                            reply.writeString(_result14);
                            break;
                        case 19:
                            InputDeviceIdentifier _arg016 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            setCurrentKeyboardLayoutForInputDevice(_arg016, _arg17);
                            reply.writeNoException();
                            break;
                        case 20:
                            InputDeviceIdentifier _arg017 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            data.enforceNoDataAvail();
                            String[] _result15 = getEnabledKeyboardLayoutsForInputDevice(_arg017);
                            reply.writeNoException();
                            reply.writeStringArray(_result15);
                            break;
                        case 21:
                            InputDeviceIdentifier _arg018 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            addKeyboardLayoutForInputDevice(_arg018, _arg18);
                            reply.writeNoException();
                            break;
                        case 22:
                            InputDeviceIdentifier _arg019 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            String _arg19 = data.readString();
                            data.enforceNoDataAvail();
                            removeKeyboardLayoutForInputDevice(_arg019, _arg19);
                            reply.writeNoException();
                            break;
                        case 23:
                            InputDeviceIdentifier _arg020 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            int _arg110 = data.readInt();
                            InputMethodInfo _arg24 = (InputMethodInfo) data.readTypedObject(InputMethodInfo.CREATOR);
                            InputMethodSubtype _arg32 = (InputMethodSubtype) data.readTypedObject(InputMethodSubtype.CREATOR);
                            data.enforceNoDataAvail();
                            String _result16 = getKeyboardLayoutForInputDevice(_arg020, _arg110, _arg24, _arg32);
                            reply.writeNoException();
                            reply.writeString(_result16);
                            break;
                        case 24:
                            InputDeviceIdentifier _arg021 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            int _arg111 = data.readInt();
                            InputMethodInfo _arg25 = (InputMethodInfo) data.readTypedObject(InputMethodInfo.CREATOR);
                            InputMethodSubtype _arg33 = (InputMethodSubtype) data.readTypedObject(InputMethodSubtype.CREATOR);
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            setKeyboardLayoutForInputDevice(_arg021, _arg111, _arg25, _arg33, _arg4);
                            reply.writeNoException();
                            break;
                        case 25:
                            InputDeviceIdentifier _arg022 = (InputDeviceIdentifier) data.readTypedObject(InputDeviceIdentifier.CREATOR);
                            int _arg112 = data.readInt();
                            InputMethodInfo _arg26 = (InputMethodInfo) data.readTypedObject(InputMethodInfo.CREATOR);
                            InputMethodSubtype _arg34 = (InputMethodSubtype) data.readTypedObject(InputMethodSubtype.CREATOR);
                            data.enforceNoDataAvail();
                            KeyboardLayout[] _result17 = getKeyboardLayoutListForInputDevice(_arg022, _arg112, _arg26, _arg34);
                            reply.writeNoException();
                            reply.writeTypedArray(_result17, 1);
                            break;
                        case 26:
                            int _arg023 = data.readInt();
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            remapModifierKey(_arg023, _arg113);
                            reply.writeNoException();
                            break;
                        case 27:
                            clearAllModifierKeyRemappings();
                            reply.writeNoException();
                            break;
                        case 28:
                            Map _result18 = getModifierKeyRemapping();
                            reply.writeNoException();
                            reply.writeMap(_result18);
                            break;
                        case 29:
                            IInputDevicesChangedListener _arg024 = IInputDevicesChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerInputDevicesChangedListener(_arg024);
                            reply.writeNoException();
                            break;
                        case 30:
                            int _result19 = isInTabletMode();
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            break;
                        case 31:
                            ITabletModeChangedListener _arg025 = ITabletModeChangedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTabletModeChangedListener(_arg025);
                            reply.writeNoException();
                            break;
                        case 32:
                            int _result20 = isMicMuted();
                            reply.writeNoException();
                            reply.writeInt(_result20);
                            break;
                        case 33:
                            int _arg026 = data.readInt();
                            VibrationEffect _arg114 = (VibrationEffect) data.readTypedObject(VibrationEffect.CREATOR);
                            IBinder _arg27 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            vibrate(_arg026, _arg114, _arg27);
                            reply.writeNoException();
                            break;
                        case 34:
                            int _arg027 = data.readInt();
                            CombinedVibration _arg115 = (CombinedVibration) data.readTypedObject(CombinedVibration.CREATOR);
                            IBinder _arg28 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            vibrateCombined(_arg027, _arg115, _arg28);
                            reply.writeNoException();
                            break;
                        case 35:
                            int _arg028 = data.readInt();
                            IBinder _arg116 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            cancelVibrate(_arg028, _arg116);
                            reply.writeNoException();
                            break;
                        case 36:
                            int _arg029 = data.readInt();
                            data.enforceNoDataAvail();
                            int[] _result21 = getVibratorIds(_arg029);
                            reply.writeNoException();
                            reply.writeIntArray(_result21);
                            break;
                        case 37:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result22 = isVibrating(_arg030);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            break;
                        case 38:
                            int _arg031 = data.readInt();
                            IVibratorStateListener _arg117 = IVibratorStateListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result23 = registerVibratorStateListener(_arg031, _arg117);
                            reply.writeNoException();
                            reply.writeBoolean(_result23);
                            break;
                        case 39:
                            int _arg032 = data.readInt();
                            IVibratorStateListener _arg118 = IVibratorStateListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result24 = unregisterVibratorStateListener(_arg032, _arg118);
                            reply.writeNoException();
                            reply.writeBoolean(_result24);
                            break;
                        case 40:
                            int _arg033 = data.readInt();
                            data.enforceNoDataAvail();
                            IInputDeviceBatteryState _result25 = getBatteryState(_arg033);
                            reply.writeNoException();
                            reply.writeTypedObject(_result25, 1);
                            break;
                        case 41:
                            int _arg034 = data.readInt();
                            data.enforceNoDataAvail();
                            setPointerIconType(_arg034);
                            reply.writeNoException();
                            break;
                        case 42:
                            PointerIcon _arg035 = (PointerIcon) data.readTypedObject(PointerIcon.CREATOR);
                            data.enforceNoDataAvail();
                            setCustomPointerIcon(_arg035);
                            reply.writeNoException();
                            break;
                        case 43:
                            IBinder _arg036 = data.readStrongBinder();
                            boolean _arg119 = data.readBoolean();
                            data.enforceNoDataAvail();
                            requestPointerCapture(_arg036, _arg119);
                            break;
                        case 44:
                            IBinder _arg037 = data.readStrongBinder();
                            String _arg120 = data.readString();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            InputMonitor _result26 = monitorGestureInput(_arg037, _arg120, _arg29);
                            reply.writeNoException();
                            reply.writeTypedObject(_result26, 1);
                            break;
                        case 45:
                            String _arg038 = data.readString();
                            int _arg121 = data.readInt();
                            data.enforceNoDataAvail();
                            addPortAssociation(_arg038, _arg121);
                            reply.writeNoException();
                            break;
                        case 46:
                            String _arg039 = data.readString();
                            data.enforceNoDataAvail();
                            removePortAssociation(_arg039);
                            reply.writeNoException();
                            break;
                        case 47:
                            String _arg040 = data.readString();
                            String _arg122 = data.readString();
                            data.enforceNoDataAvail();
                            addUniqueIdAssociation(_arg040, _arg122);
                            reply.writeNoException();
                            break;
                        case 48:
                            String _arg041 = data.readString();
                            data.enforceNoDataAvail();
                            removeUniqueIdAssociation(_arg041);
                            reply.writeNoException();
                            break;
                        case 49:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            InputSensorInfo[] _result27 = getSensorList(_arg042);
                            reply.writeNoException();
                            reply.writeTypedArray(_result27, 1);
                            break;
                        case 50:
                            IInputSensorEventListener _arg043 = IInputSensorEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result28 = registerSensorListener(_arg043);
                            reply.writeNoException();
                            reply.writeBoolean(_result28);
                            break;
                        case 51:
                            IInputSensorEventListener _arg044 = IInputSensorEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSensorListener(_arg044);
                            reply.writeNoException();
                            break;
                        case 52:
                            int _arg045 = data.readInt();
                            int _arg123 = data.readInt();
                            int _arg210 = data.readInt();
                            int _arg35 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result29 = enableSensor(_arg045, _arg123, _arg210, _arg35);
                            reply.writeNoException();
                            reply.writeBoolean(_result29);
                            break;
                        case 53:
                            int _arg046 = data.readInt();
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            disableSensor(_arg046, _arg124);
                            reply.writeNoException();
                            break;
                        case 54:
                            int _arg047 = data.readInt();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result30 = flushSensor(_arg047, _arg125);
                            reply.writeNoException();
                            reply.writeBoolean(_result30);
                            break;
                        case 55:
                            int _arg048 = data.readInt();
                            data.enforceNoDataAvail();
                            List<Light> _result31 = getLights(_arg048);
                            reply.writeNoException();
                            reply.writeTypedList(_result31, 1);
                            break;
                        case 56:
                            int _arg049 = data.readInt();
                            int _arg126 = data.readInt();
                            data.enforceNoDataAvail();
                            LightState _result32 = getLightState(_arg049, _arg126);
                            reply.writeNoException();
                            reply.writeTypedObject(_result32, 1);
                            break;
                        case 57:
                            int _arg050 = data.readInt();
                            int[] _arg127 = data.createIntArray();
                            LightState[] _arg211 = (LightState[]) data.createTypedArray(LightState.CREATOR);
                            IBinder _arg36 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            setLightStates(_arg050, _arg127, _arg211, _arg36);
                            reply.writeNoException();
                            break;
                        case 58:
                            int _arg051 = data.readInt();
                            String _arg128 = data.readString();
                            IBinder _arg212 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            openLightSession(_arg051, _arg128, _arg212);
                            reply.writeNoException();
                            break;
                        case 59:
                            int _arg052 = data.readInt();
                            IBinder _arg129 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            closeLightSession(_arg052, _arg129);
                            reply.writeNoException();
                            break;
                        case 60:
                            cancelCurrentTouch();
                            reply.writeNoException();
                            break;
                        case 61:
                            int _arg053 = data.readInt();
                            IInputDeviceBatteryListener _arg130 = IInputDeviceBatteryListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerBatteryListener(_arg053, _arg130);
                            reply.writeNoException();
                            break;
                        case 62:
                            int _arg054 = data.readInt();
                            IInputDeviceBatteryListener _arg131 = IInputDeviceBatteryListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterBatteryListener(_arg054, _arg131);
                            reply.writeNoException();
                            break;
                        case 63:
                            int _arg055 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result33 = getInputDeviceBluetoothAddress(_arg055);
                            reply.writeNoException();
                            reply.writeString(_result33);
                            break;
                        case 64:
                            IBinder _arg056 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            pilferPointers(_arg056);
                            reply.writeNoException();
                            break;
                        case 65:
                            IKeyboardBacklightListener _arg057 = IKeyboardBacklightListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerKeyboardBacklightListener(_arg057);
                            reply.writeNoException();
                            break;
                        case 66:
                            IKeyboardBacklightListener _arg058 = IKeyboardBacklightListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterKeyboardBacklightListener(_arg058);
                            reply.writeNoException();
                            break;
                        case 67:
                            int _arg059 = data.readInt();
                            data.enforceNoDataAvail();
                            HostUsiVersion _result34 = getHostUsiVersionFromDisplayConfig(_arg059);
                            reply.writeNoException();
                            reply.writeTypedObject(_result34, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInputManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.hardware.input.IInputManager
            public String getVelocityTrackerStrategy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public InputDevice getInputDevice(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    InputDevice _result = (InputDevice) _reply.readTypedObject(InputDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int[] getInputDeviceIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean isInputDeviceEnabled(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void enableInputDevice(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void disableInputDevice(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sourceMask);
                    _data.writeIntArray(keyCodes);
                    _data.writeInt(keyExists.length);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readBooleanArray(keyExists);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int getKeyCodeForKeyLocation(int deviceId, int locationKeyCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(locationKeyCode);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void tryPointerSpeed(int speed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(speed);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean injectInputEvent(InputEvent ev, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ev, 0);
                    _data.writeInt(mode);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean injectInputEventToTarget(InputEvent ev, int mode, int targetUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ev, 0);
                    _data.writeInt(mode);
                    _data.writeInt(targetUid);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public VerifiedInputEvent verifyInputEvent(InputEvent ev) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ev, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    VerifiedInputEvent _result = (VerifiedInputEvent) _reply.readTypedObject(VerifiedInputEvent.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public TouchCalibration getTouchCalibrationForInputDevice(String inputDeviceDescriptor, int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    _data.writeInt(rotation);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    TouchCalibration _result = (TouchCalibration) _reply.readTypedObject(TouchCalibration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setTouchCalibrationForInputDevice(String inputDeviceDescriptor, int rotation, TouchCalibration calibration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    _data.writeInt(rotation);
                    _data.writeTypedObject(calibration, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout[] getKeyboardLayouts() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    KeyboardLayout[] _result = (KeyboardLayout[]) _reply.createTypedArray(KeyboardLayout.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout[] getKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    KeyboardLayout[] _result = (KeyboardLayout[]) _reply.createTypedArray(KeyboardLayout.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    KeyboardLayout _result = (KeyboardLayout) _reply.readTypedObject(KeyboardLayout.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public String getCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setCurrentKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public String[] getEnabledKeyboardLayoutsForInputDevice(InputDeviceIdentifier identifier) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void addKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void removeKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public String getKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeInt(userId);
                    _data.writeTypedObject(imeInfo, 0);
                    _data.writeTypedObject(imeSubtype, 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setKeyboardLayoutForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeInt(userId);
                    _data.writeTypedObject(imeInfo, 0);
                    _data.writeTypedObject(imeSubtype, 0);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout[] getKeyboardLayoutListForInputDevice(InputDeviceIdentifier identifier, int userId, InputMethodInfo imeInfo, InputMethodSubtype imeSubtype) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(identifier, 0);
                    _data.writeInt(userId);
                    _data.writeTypedObject(imeInfo, 0);
                    _data.writeTypedObject(imeSubtype, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    KeyboardLayout[] _result = (KeyboardLayout[]) _reply.createTypedArray(KeyboardLayout.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void remapModifierKey(int fromKey, int toKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(fromKey);
                    _data.writeInt(toKey);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void clearAllModifierKeyRemappings() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public Map getModifierKeyRemapping() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int isInTabletMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void registerTabletModeChangedListener(ITabletModeChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int isMicMuted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void vibrate(int deviceId, VibrationEffect effect, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeTypedObject(effect, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void vibrateCombined(int deviceId, CombinedVibration vibration, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeTypedObject(vibration, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void cancelVibrate(int deviceId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int[] getVibratorIds(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean isVibrating(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean registerVibratorStateListener(int deviceId, IVibratorStateListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean unregisterVibratorStateListener(int deviceId, IVibratorStateListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public IInputDeviceBatteryState getBatteryState(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    IInputDeviceBatteryState _result = (IInputDeviceBatteryState) _reply.readTypedObject(IInputDeviceBatteryState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setPointerIconType(int typeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(typeId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setCustomPointerIcon(PointerIcon icon) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(icon, 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void requestPointerCapture(IBinder inputChannelToken, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(inputChannelToken);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(43, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public InputMonitor monitorGestureInput(IBinder token, String name, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(name);
                    _data.writeInt(displayId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    InputMonitor _result = (InputMonitor) _reply.readTypedObject(InputMonitor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void addPortAssociation(String inputPort, int displayPort) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputPort);
                    _data.writeInt(displayPort);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void removePortAssociation(String inputPort) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputPort);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void addUniqueIdAssociation(String inputPort, String displayUniqueId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputPort);
                    _data.writeString(displayUniqueId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void removeUniqueIdAssociation(String inputPort) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputPort);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public InputSensorInfo[] getSensorList(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    InputSensorInfo[] _result = (InputSensorInfo[]) _reply.createTypedArray(InputSensorInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean registerSensorListener(IInputSensorEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void unregisterSensorListener(IInputSensorEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean enableSensor(int deviceId, int sensorType, int samplingPeriodUs, int maxBatchReportLatencyUs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sensorType);
                    _data.writeInt(samplingPeriodUs);
                    _data.writeInt(maxBatchReportLatencyUs);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void disableSensor(int deviceId, int sensorType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sensorType);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean flushSensor(int deviceId, int sensorType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sensorType);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public List<Light> getLights(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    List<Light> _result = _reply.createTypedArrayList(Light.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public LightState getLightState(int deviceId, int lightId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(lightId);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    LightState _result = (LightState) _reply.readTypedObject(LightState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setLightStates(int deviceId, int[] lightIds, LightState[] states, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeIntArray(lightIds);
                    _data.writeTypedArray(states, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void openLightSession(int deviceId, String opPkg, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeString(opPkg);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void closeLightSession(int deviceId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void cancelCurrentTouch() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void registerBatteryListener(int deviceId, IInputDeviceBatteryListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void unregisterBatteryListener(int deviceId, IInputDeviceBatteryListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public String getInputDeviceBluetoothAddress(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void pilferPointers(IBinder inputChannelToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(inputChannelToken);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void registerKeyboardBacklightListener(IKeyboardBacklightListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public void unregisterKeyboardBacklightListener(IKeyboardBacklightListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public HostUsiVersion getHostUsiVersionFromDisplayConfig(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    HostUsiVersion _result = (HostUsiVersion) _reply.readTypedObject(HostUsiVersion.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void setCurrentKeyboardLayoutForInputDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SET_KEYBOARD_LAYOUT, source);
        }

        protected void addKeyboardLayoutForInputDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SET_KEYBOARD_LAYOUT, source);
        }

        protected void removeKeyboardLayoutForInputDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SET_KEYBOARD_LAYOUT, source);
        }

        protected void setKeyboardLayoutForInputDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.SET_KEYBOARD_LAYOUT, source);
        }

        protected void remapModifierKey_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.REMAP_MODIFIER_KEYS, source);
        }

        protected void clearAllModifierKeyRemappings_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.REMAP_MODIFIER_KEYS, source);
        }

        protected void getModifierKeyRemapping_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.REMAP_MODIFIER_KEYS, source);
        }

        protected void getInputDeviceBluetoothAddress_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.BLUETOOTH, source);
        }

        protected void pilferPointers_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MONITOR_INPUT, source);
        }

        protected void registerKeyboardBacklightListener_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MONITOR_KEYBOARD_BACKLIGHT, source);
        }

        protected void unregisterKeyboardBacklightListener_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MONITOR_KEYBOARD_BACKLIGHT, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 66;
        }
    }
}
