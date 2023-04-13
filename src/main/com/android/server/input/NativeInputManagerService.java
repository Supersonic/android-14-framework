package com.android.server.input;

import android.hardware.display.DisplayViewport;
import android.hardware.input.InputSensorInfo;
import android.hardware.lights.Light;
import android.os.IBinder;
import android.os.MessageQueue;
import android.util.SparseArray;
import android.view.InputApplicationHandle;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.PointerIcon;
import android.view.VerifiedInputEvent;
import java.util.List;
/* loaded from: classes.dex */
public interface NativeInputManagerService {
    void addKeyRemapping(int i, int i2, int i3);

    boolean canDispatchToDisplay(int i, int i2);

    void cancelCurrentTouch();

    void cancelVibrate(int i, int i2);

    void changeKeyboardLayoutAssociation();

    void changeTypeAssociation();

    void changeUniqueIdAssociation();

    InputChannel createInputChannel(String str);

    InputChannel createInputMonitor(int i, String str, int i2);

    void disableInputDevice(int i);

    void disableSensor(int i, int i2);

    void displayRemoved(int i);

    String dump();

    void enableInputDevice(int i);

    boolean enableSensor(int i, int i2, int i3, int i4);

    boolean flushSensor(int i, int i2);

    int getBatteryCapacity(int i);

    String getBatteryDevicePath(int i);

    int getBatteryStatus(int i);

    String getBluetoothAddress(int i);

    int getKeyCodeForKeyLocation(int i, int i2);

    int getKeyCodeState(int i, int i2, int i3);

    int getLightColor(int i, int i2);

    int getLightPlayerId(int i, int i2);

    List<Light> getLights(int i);

    int getScanCodeState(int i, int i2, int i3);

    InputSensorInfo[] getSensorList(int i);

    int getSwitchState(int i, int i2, int i3);

    int[] getVibratorIds(int i);

    boolean hasKeys(int i, int i2, int[] iArr, boolean[] zArr);

    int injectInputEvent(InputEvent inputEvent, boolean z, int i, int i2, int i3, int i4);

    boolean isInputDeviceEnabled(int i);

    boolean isVibrating(int i);

    void monitor();

    void notifyPortAssociationsChanged();

    void pilferPointers(IBinder iBinder);

    void reloadCalibration();

    void reloadDeviceAliases();

    void reloadKeyboardLayouts();

    void reloadPointerIcons();

    void removeInputChannel(IBinder iBinder);

    void requestPointerCapture(IBinder iBinder, boolean z);

    void setCustomPointerIcon(PointerIcon pointerIcon);

    void setDisplayEligibilityForPointerCapture(int i, boolean z);

    void setDisplayViewports(DisplayViewport[] displayViewportArr);

    void setFocusedApplication(int i, InputApplicationHandle inputApplicationHandle);

    void setFocusedDisplay(int i);

    boolean setInTouchMode(boolean z, int i, int i2, boolean z2, int i3);

    void setInputDispatchMode(boolean z, boolean z2);

    void setInputFilterEnabled(boolean z);

    void setInteractive(boolean z);

    void setLightColor(int i, int i2, int i3);

    void setLightPlayerId(int i, int i2, int i3);

    void setMaximumObscuringOpacityForTouch(float f);

    void setMotionClassifierEnabled(boolean z);

    void setPointerAcceleration(float f);

    void setPointerDisplayId(int i);

    void setPointerIconType(int i);

    void setPointerSpeed(int i);

    void setShowTouches(boolean z);

    void setStylusButtonMotionEventsEnabled(boolean z);

    void setSystemUiLightsOut(boolean z);

    void setTouchpadNaturalScrollingEnabled(boolean z);

    void setTouchpadPointerSpeed(int i);

    void setTouchpadRightClickZoneEnabled(boolean z);

    void setTouchpadTapToClickEnabled(boolean z);

    void start();

    void toggleCapsLock(int i);

    boolean transferTouch(IBinder iBinder, int i);

    boolean transferTouchFocus(IBinder iBinder, IBinder iBinder2, boolean z);

    VerifiedInputEvent verifyInputEvent(InputEvent inputEvent);

    void vibrate(int i, long[] jArr, int[] iArr, int i2, int i3);

    void vibrateCombined(int i, long[] jArr, SparseArray<int[]> sparseArray, int i2, int i3);

    /* loaded from: classes.dex */
    public static class NativeImpl implements NativeInputManagerService {
        public final long mPtr;

        private native long init(InputManagerService inputManagerService, MessageQueue messageQueue);

        @Override // com.android.server.input.NativeInputManagerService
        public native void addKeyRemapping(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean canDispatchToDisplay(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native void cancelCurrentTouch();

        @Override // com.android.server.input.NativeInputManagerService
        public native void cancelVibrate(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native void changeKeyboardLayoutAssociation();

        @Override // com.android.server.input.NativeInputManagerService
        public native void changeTypeAssociation();

        @Override // com.android.server.input.NativeInputManagerService
        public native void changeUniqueIdAssociation();

        @Override // com.android.server.input.NativeInputManagerService
        public native InputChannel createInputChannel(String str);

        @Override // com.android.server.input.NativeInputManagerService
        public native InputChannel createInputMonitor(int i, String str, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native void disableInputDevice(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void disableSensor(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native void displayRemoved(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native String dump();

        @Override // com.android.server.input.NativeInputManagerService
        public native void enableInputDevice(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean enableSensor(int i, int i2, int i3, int i4);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean flushSensor(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getBatteryCapacity(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native String getBatteryDevicePath(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getBatteryStatus(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native String getBluetoothAddress(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getKeyCodeForKeyLocation(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getKeyCodeState(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getLightColor(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getLightPlayerId(int i, int i2);

        @Override // com.android.server.input.NativeInputManagerService
        public native List<Light> getLights(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getScanCodeState(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native InputSensorInfo[] getSensorList(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native int getSwitchState(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native int[] getVibratorIds(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean hasKeys(int i, int i2, int[] iArr, boolean[] zArr);

        @Override // com.android.server.input.NativeInputManagerService
        public native int injectInputEvent(InputEvent inputEvent, boolean z, int i, int i2, int i3, int i4);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean isInputDeviceEnabled(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean isVibrating(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void monitor();

        public native void notifyPointerDisplayIdChanged();

        @Override // com.android.server.input.NativeInputManagerService
        public native void notifyPortAssociationsChanged();

        @Override // com.android.server.input.NativeInputManagerService
        public native void pilferPointers(IBinder iBinder);

        @Override // com.android.server.input.NativeInputManagerService
        public native void reloadCalibration();

        @Override // com.android.server.input.NativeInputManagerService
        public native void reloadDeviceAliases();

        @Override // com.android.server.input.NativeInputManagerService
        public native void reloadKeyboardLayouts();

        @Override // com.android.server.input.NativeInputManagerService
        public native void reloadPointerIcons();

        @Override // com.android.server.input.NativeInputManagerService
        public native void removeInputChannel(IBinder iBinder);

        @Override // com.android.server.input.NativeInputManagerService
        public native void requestPointerCapture(IBinder iBinder, boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setCustomPointerIcon(PointerIcon pointerIcon);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setDisplayEligibilityForPointerCapture(int i, boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setDisplayViewports(DisplayViewport[] displayViewportArr);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setFocusedApplication(int i, InputApplicationHandle inputApplicationHandle);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setFocusedDisplay(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean setInTouchMode(boolean z, int i, int i2, boolean z2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setInputDispatchMode(boolean z, boolean z2);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setInputFilterEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setInteractive(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setLightColor(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setLightPlayerId(int i, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setMaximumObscuringOpacityForTouch(float f);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setMotionClassifierEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setPointerAcceleration(float f);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setPointerDisplayId(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setPointerIconType(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setPointerSpeed(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setShowTouches(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setStylusButtonMotionEventsEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setSystemUiLightsOut(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setTouchpadNaturalScrollingEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setTouchpadPointerSpeed(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setTouchpadRightClickZoneEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void setTouchpadTapToClickEnabled(boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native void start();

        @Override // com.android.server.input.NativeInputManagerService
        public native void toggleCapsLock(int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean transferTouch(IBinder iBinder, int i);

        @Override // com.android.server.input.NativeInputManagerService
        public native boolean transferTouchFocus(IBinder iBinder, IBinder iBinder2, boolean z);

        @Override // com.android.server.input.NativeInputManagerService
        public native VerifiedInputEvent verifyInputEvent(InputEvent inputEvent);

        @Override // com.android.server.input.NativeInputManagerService
        public native void vibrate(int i, long[] jArr, int[] iArr, int i2, int i3);

        @Override // com.android.server.input.NativeInputManagerService
        public native void vibrateCombined(int i, long[] jArr, SparseArray<int[]> sparseArray, int i2, int i3);

        public NativeImpl(InputManagerService inputManagerService, MessageQueue messageQueue) {
            this.mPtr = init(inputManagerService, messageQueue);
        }
    }
}
