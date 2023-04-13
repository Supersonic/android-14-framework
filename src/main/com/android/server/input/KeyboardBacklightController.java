package com.android.server.input;

import android.content.Context;
import android.graphics.Color;
import android.hardware.input.IKeyboardBacklightListener;
import android.hardware.input.IKeyboardBacklightState;
import android.hardware.input.InputManager;
import android.hardware.lights.Light;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputDevice;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.input.InputManagerService;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;
/* loaded from: classes.dex */
public final class KeyboardBacklightController implements InputManagerService.KeyboardBacklightControllerInterface, InputManager.InputDeviceListener {
    public final Context mContext;
    @GuardedBy({"mDataStore"})
    public final PersistentDataStore mDataStore;
    public final Handler mHandler;
    public final NativeInputManagerService mNative;
    public static final boolean DEBUG = Log.isLoggable("KbdBacklightController", 3);
    @VisibleForTesting
    static final long USER_INACTIVITY_THRESHOLD_MILLIS = Duration.ofSeconds(30).toMillis();
    @VisibleForTesting
    static final int[] BRIGHTNESS_VALUE_FOR_LEVEL = new int[11];
    public final SparseArray<KeyboardBacklightState> mKeyboardBacklights = new SparseArray<>(1);
    public boolean mIsBacklightOn = false;
    public boolean mIsInteractive = true;
    @GuardedBy({"mKeyboardBacklightListenerRecords"})
    public final SparseArray<KeyboardBacklightListenerRecord> mKeyboardBacklightListenerRecords = new SparseArray<>();

    /* loaded from: classes.dex */
    public enum Direction {
        DIRECTION_UP,
        DIRECTION_DOWN
    }

    static {
        for (int i = 0; i <= 10; i++) {
            BRIGHTNESS_VALUE_FOR_LEVEL[i] = (int) Math.floor((i * 255.0f) / 10.0f);
        }
    }

    public KeyboardBacklightController(Context context, NativeInputManagerService nativeInputManagerService, PersistentDataStore persistentDataStore, Looper looper) {
        this.mContext = context;
        this.mNative = nativeInputManagerService;
        this.mDataStore = persistentDataStore;
        this.mHandler = new Handler(looper, new Handler.Callback() { // from class: com.android.server.input.KeyboardBacklightController$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean handleMessage;
                handleMessage = KeyboardBacklightController.this.handleMessage(message);
                return handleMessage;
            }
        });
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void systemRunning() {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        Objects.requireNonNull(inputManager);
        inputManager.registerInputDeviceListener(this, this.mHandler);
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, inputManager.getInputDeviceIds()));
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void incrementKeyboardBacklight(int i) {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 2, Integer.valueOf(i)));
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void decrementKeyboardBacklight(int i) {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 3, Integer.valueOf(i)));
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void notifyUserActivity() {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 4));
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void onInteractiveChanged(boolean z) {
        this.mHandler.sendMessage(Message.obtain(this.mHandler, 6, Boolean.valueOf(z)));
    }

    public final void updateKeyboardBacklight(int i, Direction direction) {
        int max;
        InputDevice inputDevice = getInputDevice(i);
        KeyboardBacklightState keyboardBacklightState = this.mKeyboardBacklights.get(i);
        if (inputDevice == null || keyboardBacklightState == null) {
            return;
        }
        Light light = keyboardBacklightState.mLight;
        int i2 = keyboardBacklightState.mBrightnessLevel;
        if (direction == Direction.DIRECTION_UP) {
            max = Math.min(i2 + 1, 10);
        } else {
            max = Math.max(i2 - 1, 0);
        }
        updateBacklightState(i, light, max, true);
        synchronized (this.mDataStore) {
            this.mDataStore.setKeyboardBacklightBrightness(inputDevice.getDescriptor(), light.getId(), BRIGHTNESS_VALUE_FOR_LEVEL[max]);
            this.mDataStore.saveIfNeeded();
        }
    }

    public final void restoreBacklightBrightness(InputDevice inputDevice, Light light) {
        OptionalInt keyboardBacklightBrightness;
        synchronized (this.mDataStore) {
            keyboardBacklightBrightness = this.mDataStore.getKeyboardBacklightBrightness(inputDevice.getDescriptor(), light.getId());
        }
        if (keyboardBacklightBrightness.isPresent()) {
            updateBacklightState(inputDevice.getId(), light, Arrays.binarySearch(BRIGHTNESS_VALUE_FOR_LEVEL, Math.max(0, Math.min(255, keyboardBacklightBrightness.getAsInt()))), false);
            if (DEBUG) {
                Slog.d("KbdBacklightController", "Restoring brightness level " + keyboardBacklightBrightness.getAsInt());
            }
        }
    }

    public final void handleUserActivity() {
        if (this.mIsInteractive) {
            if (!this.mIsBacklightOn) {
                this.mIsBacklightOn = true;
                for (int i = 0; i < this.mKeyboardBacklights.size(); i++) {
                    int keyAt = this.mKeyboardBacklights.keyAt(i);
                    KeyboardBacklightState valueAt = this.mKeyboardBacklights.valueAt(i);
                    updateBacklightState(keyAt, valueAt.mLight, valueAt.mBrightnessLevel, false);
                }
            }
            this.mHandler.removeMessages(5);
            this.mHandler.sendEmptyMessageAtTime(5, SystemClock.uptimeMillis() + USER_INACTIVITY_THRESHOLD_MILLIS);
        }
    }

    public final void handleUserInactivity() {
        if (this.mIsBacklightOn) {
            this.mIsBacklightOn = false;
            for (int i = 0; i < this.mKeyboardBacklights.size(); i++) {
                int keyAt = this.mKeyboardBacklights.keyAt(i);
                KeyboardBacklightState valueAt = this.mKeyboardBacklights.valueAt(i);
                updateBacklightState(keyAt, valueAt.mLight, valueAt.mBrightnessLevel, false);
            }
        }
    }

    @VisibleForTesting
    public void handleInteractiveStateChange(boolean z) {
        this.mIsInteractive = z;
        if (z) {
            handleUserActivity();
        } else {
            handleUserInactivity();
        }
    }

    public final boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                for (int i : (int[]) message.obj) {
                    onInputDeviceAdded(i);
                }
                return true;
            case 2:
                updateKeyboardBacklight(((Integer) message.obj).intValue(), Direction.DIRECTION_UP);
                return true;
            case 3:
                updateKeyboardBacklight(((Integer) message.obj).intValue(), Direction.DIRECTION_DOWN);
                return true;
            case 4:
                handleUserActivity();
                return true;
            case 5:
                handleUserInactivity();
                return true;
            case 6:
                handleInteractiveStateChange(((Boolean) message.obj).booleanValue());
                return true;
            default:
                return false;
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    @VisibleForTesting
    public void onInputDeviceAdded(int i) {
        onInputDeviceChanged(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    @VisibleForTesting
    public void onInputDeviceRemoved(int i) {
        this.mKeyboardBacklights.remove(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    @VisibleForTesting
    public void onInputDeviceChanged(int i) {
        InputDevice inputDevice = getInputDevice(i);
        if (inputDevice == null) {
            return;
        }
        Light keyboardBacklight = getKeyboardBacklight(inputDevice);
        if (keyboardBacklight == null) {
            this.mKeyboardBacklights.remove(i);
            return;
        }
        KeyboardBacklightState keyboardBacklightState = this.mKeyboardBacklights.get(i);
        if (keyboardBacklightState == null || keyboardBacklightState.mLight.getId() != keyboardBacklight.getId()) {
            this.mKeyboardBacklights.put(i, new KeyboardBacklightState(keyboardBacklight));
            restoreBacklightBrightness(inputDevice, keyboardBacklight);
        }
    }

    public final InputDevice getInputDevice(int i) {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        if (inputManager != null) {
            return inputManager.getInputDevice(i);
        }
        return null;
    }

    public final Light getKeyboardBacklight(InputDevice inputDevice) {
        for (Light light : inputDevice.getLightsManager().getLights()) {
            if (light.getType() == 10003 && light.hasBrightnessControl()) {
                return light;
            }
        }
        return null;
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void registerKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener, int i) {
        synchronized (this.mKeyboardBacklightListenerRecords) {
            if (this.mKeyboardBacklightListenerRecords.get(i) != null) {
                throw new IllegalStateException("The calling process has already registered a KeyboardBacklightListener.");
            }
            KeyboardBacklightListenerRecord keyboardBacklightListenerRecord = new KeyboardBacklightListenerRecord(i, iKeyboardBacklightListener);
            try {
                iKeyboardBacklightListener.asBinder().linkToDeath(keyboardBacklightListenerRecord, 0);
                this.mKeyboardBacklightListenerRecords.put(i, keyboardBacklightListenerRecord);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void unregisterKeyboardBacklightListener(IKeyboardBacklightListener iKeyboardBacklightListener, int i) {
        synchronized (this.mKeyboardBacklightListenerRecords) {
            KeyboardBacklightListenerRecord keyboardBacklightListenerRecord = this.mKeyboardBacklightListenerRecords.get(i);
            if (keyboardBacklightListenerRecord == null) {
                throw new IllegalStateException("The calling process has no registered KeyboardBacklightListener.");
            }
            IKeyboardBacklightListener iKeyboardBacklightListener2 = keyboardBacklightListenerRecord.mListener;
            if (iKeyboardBacklightListener2 != iKeyboardBacklightListener) {
                throw new IllegalStateException("The calling process has a different registered KeyboardBacklightListener.");
            }
            iKeyboardBacklightListener2.asBinder().unlinkToDeath(keyboardBacklightListenerRecord, 0);
            this.mKeyboardBacklightListenerRecords.remove(i);
        }
    }

    public final void updateBacklightState(int i, Light light, int i2, boolean z) {
        KeyboardBacklightState keyboardBacklightState = this.mKeyboardBacklights.get(i);
        if (keyboardBacklightState == null) {
            return;
        }
        this.mNative.setLightColor(i, light.getId(), this.mIsBacklightOn ? Color.argb(BRIGHTNESS_VALUE_FOR_LEVEL[i2], 0, 0, 0) : 0);
        if (DEBUG) {
            Slog.d("KbdBacklightController", "Changing state from " + keyboardBacklightState.mBrightnessLevel + " to " + i2 + "(isBacklightOn = " + this.mIsBacklightOn + ")");
        }
        keyboardBacklightState.mBrightnessLevel = i2;
        synchronized (this.mKeyboardBacklightListenerRecords) {
            for (int i3 = 0; i3 < this.mKeyboardBacklightListenerRecords.size(); i3++) {
                IKeyboardBacklightState iKeyboardBacklightState = new IKeyboardBacklightState();
                iKeyboardBacklightState.brightnessLevel = i2;
                iKeyboardBacklightState.maxBrightnessLevel = 10;
                this.mKeyboardBacklightListenerRecords.valueAt(i3).notifyKeyboardBacklightChanged(i, iKeyboardBacklightState, z);
            }
        }
    }

    public final void onKeyboardBacklightListenerDied(int i) {
        synchronized (this.mKeyboardBacklightListenerRecords) {
            this.mKeyboardBacklightListenerRecords.remove(i);
        }
    }

    @Override // com.android.server.input.InputManagerService.KeyboardBacklightControllerInterface
    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.println("KbdBacklightController: " + this.mKeyboardBacklights.size() + " keyboard backlights, isBacklightOn = " + this.mIsBacklightOn);
        indentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mKeyboardBacklights.size(); i++) {
            indentingPrintWriter.println(i + ": " + this.mKeyboardBacklights.valueAt(i).toString());
        }
        indentingPrintWriter.decreaseIndent();
    }

    /* loaded from: classes.dex */
    public class KeyboardBacklightListenerRecord implements IBinder.DeathRecipient {
        public final IKeyboardBacklightListener mListener;
        public final int mPid;

        public KeyboardBacklightListenerRecord(int i, IKeyboardBacklightListener iKeyboardBacklightListener) {
            this.mPid = i;
            this.mListener = iKeyboardBacklightListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (KeyboardBacklightController.DEBUG) {
                Slog.d("KbdBacklightController", "Keyboard backlight listener for pid " + this.mPid + " died.");
            }
            KeyboardBacklightController.this.onKeyboardBacklightListenerDied(this.mPid);
        }

        public void notifyKeyboardBacklightChanged(int i, IKeyboardBacklightState iKeyboardBacklightState, boolean z) {
            try {
                this.mListener.onBrightnessChanged(i, iKeyboardBacklightState, z);
            } catch (RemoteException e) {
                Slog.w("KbdBacklightController", "Failed to notify process " + this.mPid + " that keyboard backlight changed, assuming it died.", e);
                binderDied();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class KeyboardBacklightState {
        public int mBrightnessLevel;
        public final Light mLight;

        public KeyboardBacklightState(Light light) {
            this.mLight = light;
        }

        public String toString() {
            return "KeyboardBacklightState{Light=" + this.mLight.getId() + ", BrightnessLevel=" + this.mBrightnessLevel + "}";
        }
    }
}
