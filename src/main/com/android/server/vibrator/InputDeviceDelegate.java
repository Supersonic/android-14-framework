package com.android.server.vibrator;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.CombinedVibration;
import android.os.Handler;
import android.os.VibratorManager;
import android.util.SparseArray;
import android.view.InputDevice;
import com.android.internal.annotations.GuardedBy;
import com.android.server.vibrator.Vibration;
/* loaded from: classes2.dex */
public final class InputDeviceDelegate implements InputManager.InputDeviceListener {
    public final Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public InputManager mInputManager;
    @GuardedBy({"mLock"})
    public boolean mShouldVibrateInputDevices;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<VibratorManager> mInputDeviceVibrators = new SparseArray<>();

    public InputDeviceDelegate(Context context, Handler handler) {
        this.mHandler = handler;
        this.mContext = context;
    }

    public void onSystemReady() {
        synchronized (this.mLock) {
            this.mInputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        updateInputDevice(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        updateInputDevice(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        synchronized (this.mLock) {
            this.mInputDeviceVibrators.remove(i);
        }
    }

    public boolean vibrateIfAvailable(Vibration.CallerInfo callerInfo, CombinedVibration combinedVibration) {
        boolean z;
        synchronized (this.mLock) {
            for (int i = 0; i < this.mInputDeviceVibrators.size(); i++) {
                this.mInputDeviceVibrators.valueAt(i).vibrate(callerInfo.uid, callerInfo.opPkg, combinedVibration, callerInfo.reason, callerInfo.attrs);
            }
            z = this.mInputDeviceVibrators.size() > 0;
        }
        return z;
    }

    public boolean updateInputDeviceVibrators(boolean z) {
        synchronized (this.mLock) {
            if (this.mInputManager == null) {
                return false;
            }
            if (z == this.mShouldVibrateInputDevices) {
                return false;
            }
            this.mShouldVibrateInputDevices = z;
            this.mInputDeviceVibrators.clear();
            if (z) {
                this.mInputManager.registerInputDeviceListener(this, this.mHandler);
                for (int i : this.mInputManager.getInputDeviceIds()) {
                    InputDevice inputDevice = this.mInputManager.getInputDevice(i);
                    if (inputDevice != null) {
                        VibratorManager vibratorManager = inputDevice.getVibratorManager();
                        if (vibratorManager.getVibratorIds().length > 0) {
                            this.mInputDeviceVibrators.put(inputDevice.getId(), vibratorManager);
                        }
                    }
                }
            } else {
                this.mInputManager.unregisterInputDeviceListener(this);
            }
            return true;
        }
    }

    public final void updateInputDevice(int i) {
        synchronized (this.mLock) {
            InputManager inputManager = this.mInputManager;
            if (inputManager == null) {
                return;
            }
            if (this.mShouldVibrateInputDevices) {
                InputDevice inputDevice = inputManager.getInputDevice(i);
                if (inputDevice == null) {
                    this.mInputDeviceVibrators.remove(i);
                    return;
                }
                VibratorManager vibratorManager = inputDevice.getVibratorManager();
                if (vibratorManager.getVibratorIds().length > 0) {
                    this.mInputDeviceVibrators.put(inputDevice.getId(), vibratorManager);
                } else {
                    this.mInputDeviceVibrators.remove(i);
                }
            }
        }
    }
}
