package com.android.server.display;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManagerInternal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.DisplayDeviceConfig;
import com.android.server.display.utils.SensorUtils;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayPowerProximityStateController {
    @VisibleForTesting
    static final int MSG_PROXIMITY_SENSOR_DEBOUNCED = 1;
    @VisibleForTesting
    static final int PROXIMITY_POSITIVE = 1;
    @VisibleForTesting
    static final int PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY = 0;
    @VisibleForTesting
    static final int PROXIMITY_UNKNOWN = -1;
    public Clock mClock;
    public DisplayDeviceConfig mDisplayDeviceConfig;
    public int mDisplayId;
    public final DisplayPowerProximityStateHandler mHandler;
    public boolean mIgnoreProximityUntilChanged;
    public final Runnable mNudgeUpdatePowerState;
    @GuardedBy({"mLock"})
    public boolean mPendingWaitForNegativeProximityLocked;
    public Sensor mProximitySensor;
    public boolean mProximitySensorEnabled;
    public float mProximityThreshold;
    public boolean mScreenOffBecauseOfProximity;
    public final SensorManager mSensorManager;
    public final String mTag;
    public boolean mWaitingForNegativeProximity;
    public final WakelockController mWakelockController;
    public final Object mLock = new Object();
    public final SensorEventListener mProximitySensorListener = new SensorEventListener() { // from class: com.android.server.display.DisplayPowerProximityStateController.1
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (DisplayPowerProximityStateController.this.mProximitySensorEnabled) {
                long uptimeMillis = DisplayPowerProximityStateController.this.mClock.uptimeMillis();
                boolean z = false;
                float f = sensorEvent.values[0];
                if (f >= 0.0f && f < DisplayPowerProximityStateController.this.mProximityThreshold) {
                    z = true;
                }
                DisplayPowerProximityStateController.this.handleProximitySensorEvent(uptimeMillis, z);
            }
        }
    };
    public int mPendingProximity = -1;
    public long mPendingProximityDebounceTime = -1;
    public int mProximity = -1;
    public boolean mSkipRampBecauseOfProximityChangeToNegative = false;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Clock {
        long uptimeMillis();
    }

    public DisplayPowerProximityStateController(WakelockController wakelockController, DisplayDeviceConfig displayDeviceConfig, Looper looper, Runnable runnable, int i, SensorManager sensorManager, Injector injector) {
        this.mClock = (injector == null ? new Injector() : injector).createClock();
        this.mWakelockController = wakelockController;
        this.mHandler = new DisplayPowerProximityStateHandler(looper);
        this.mNudgeUpdatePowerState = runnable;
        this.mDisplayDeviceConfig = displayDeviceConfig;
        this.mDisplayId = i;
        this.mTag = "DisplayPowerProximityStateController[" + this.mDisplayId + "]";
        this.mSensorManager = sensorManager;
        loadProximitySensor();
    }

    public void updatePendingProximityRequestsLocked() {
        synchronized (this.mLock) {
            this.mWaitingForNegativeProximity |= this.mPendingWaitForNegativeProximityLocked;
            this.mPendingWaitForNegativeProximityLocked = false;
            if (this.mIgnoreProximityUntilChanged) {
                this.mWaitingForNegativeProximity = false;
            }
        }
    }

    public void cleanup() {
        setProximitySensorEnabled(false);
    }

    public boolean isProximitySensorAvailable() {
        return this.mProximitySensor != null;
    }

    public boolean setPendingWaitForNegativeProximityLocked(boolean z) {
        synchronized (this.mLock) {
            if (z) {
                if (!this.mPendingWaitForNegativeProximityLocked) {
                    this.mPendingWaitForNegativeProximityLocked = true;
                    return true;
                }
            }
            return false;
        }
    }

    public void updateProximityState(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, int i) {
        this.mSkipRampBecauseOfProximityChangeToNegative = false;
        if (this.mProximitySensor != null) {
            if (displayPowerRequest.useProximitySensor && i != 1) {
                setProximitySensorEnabled(true);
                if (!this.mScreenOffBecauseOfProximity && this.mProximity == 1 && !this.mIgnoreProximityUntilChanged) {
                    this.mScreenOffBecauseOfProximity = true;
                    sendOnProximityPositiveWithWakelock();
                }
            } else if (this.mWaitingForNegativeProximity && this.mScreenOffBecauseOfProximity && this.mProximity == 1 && i != 1) {
                setProximitySensorEnabled(true);
            } else {
                setProximitySensorEnabled(false);
                this.mWaitingForNegativeProximity = false;
            }
            if (this.mScreenOffBecauseOfProximity) {
                if (this.mProximity != 1 || this.mIgnoreProximityUntilChanged) {
                    this.mScreenOffBecauseOfProximity = false;
                    this.mSkipRampBecauseOfProximityChangeToNegative = true;
                    sendOnProximityNegativeWithWakelock();
                    return;
                }
                return;
            }
            return;
        }
        this.mWaitingForNegativeProximity = false;
        this.mIgnoreProximityUntilChanged = false;
    }

    public boolean shouldSkipRampBecauseOfProximityChangeToNegative() {
        return this.mSkipRampBecauseOfProximityChangeToNegative;
    }

    public boolean isScreenOffBecauseOfProximity() {
        return this.mScreenOffBecauseOfProximity;
    }

    public void ignoreProximitySensorUntilChanged() {
        this.mHandler.sendEmptyMessage(2);
    }

    public void notifyDisplayDeviceChanged(DisplayDeviceConfig displayDeviceConfig) {
        this.mDisplayDeviceConfig = displayDeviceConfig;
        loadProximitySensor();
    }

    public void dumpLocal(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("DisplayPowerProximityStateController:");
        synchronized (this.mLock) {
            printWriter.println("  mPendingWaitForNegativeProximityLocked=" + this.mPendingWaitForNegativeProximityLocked);
        }
        printWriter.println("  mDisplayId=" + this.mDisplayId);
        printWriter.println("  mWaitingForNegativeProximity=" + this.mWaitingForNegativeProximity);
        printWriter.println("  mIgnoreProximityUntilChanged=" + this.mIgnoreProximityUntilChanged);
        printWriter.println("  mProximitySensor=" + this.mProximitySensor);
        printWriter.println("  mProximitySensorEnabled=" + this.mProximitySensorEnabled);
        printWriter.println("  mProximityThreshold=" + this.mProximityThreshold);
        printWriter.println("  mProximity=" + proximityToString(this.mProximity));
        printWriter.println("  mPendingProximity=" + proximityToString(this.mPendingProximity));
        printWriter.println("  mPendingProximityDebounceTime=" + TimeUtils.formatUptime(this.mPendingProximityDebounceTime));
        printWriter.println("  mScreenOffBecauseOfProximity=" + this.mScreenOffBecauseOfProximity);
        printWriter.println("  mSkipRampBecauseOfProximityChangeToNegative=" + this.mSkipRampBecauseOfProximityChangeToNegative);
    }

    public void ignoreProximitySensorUntilChangedInternal() {
        if (this.mIgnoreProximityUntilChanged || this.mProximity != 1) {
            return;
        }
        this.mIgnoreProximityUntilChanged = true;
        Slog.i(this.mTag, "Ignoring proximity");
        this.mNudgeUpdatePowerState.run();
    }

    public final void sendOnProximityPositiveWithWakelock() {
        this.mWakelockController.acquireWakelock(1);
        this.mHandler.post(this.mWakelockController.getOnProximityPositiveRunnable());
    }

    public final void sendOnProximityNegativeWithWakelock() {
        this.mWakelockController.acquireWakelock(2);
        this.mHandler.post(this.mWakelockController.getOnProximityNegativeRunnable());
    }

    public final void loadProximitySensor() {
        if (this.mDisplayId != 0) {
            return;
        }
        DisplayDeviceConfig.SensorData proximitySensor = this.mDisplayDeviceConfig.getProximitySensor();
        Sensor findSensor = SensorUtils.findSensor(this.mSensorManager, proximitySensor.type, proximitySensor.name, 8);
        this.mProximitySensor = findSensor;
        if (findSensor != null) {
            this.mProximityThreshold = Math.min(findSensor.getMaximumRange(), 5.0f);
        }
    }

    public final void setProximitySensorEnabled(boolean z) {
        if (z) {
            if (this.mProximitySensorEnabled) {
                return;
            }
            this.mProximitySensorEnabled = true;
            this.mIgnoreProximityUntilChanged = false;
            this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, 3, this.mHandler);
        } else if (this.mProximitySensorEnabled) {
            this.mProximitySensorEnabled = false;
            this.mProximity = -1;
            this.mIgnoreProximityUntilChanged = false;
            this.mPendingProximity = -1;
            this.mHandler.removeMessages(1);
            this.mSensorManager.unregisterListener(this.mProximitySensorListener);
            if (this.mWakelockController.releaseWakelock(3)) {
                this.mPendingProximityDebounceTime = -1L;
            }
        }
    }

    public final void handleProximitySensorEvent(long j, boolean z) {
        if (this.mProximitySensorEnabled) {
            int i = this.mPendingProximity;
            if (i != 0 || z) {
                if (i == 1 && z) {
                    return;
                }
                this.mHandler.removeMessages(1);
                if (z) {
                    this.mPendingProximity = 1;
                    this.mPendingProximityDebounceTime = j + 0;
                    this.mWakelockController.acquireWakelock(3);
                } else {
                    this.mPendingProximity = 0;
                    this.mPendingProximityDebounceTime = j + 250;
                    this.mWakelockController.acquireWakelock(3);
                }
                debounceProximitySensor();
            }
        }
    }

    public final void debounceProximitySensor() {
        if (!this.mProximitySensorEnabled || this.mPendingProximity == -1 || this.mPendingProximityDebounceTime < 0) {
            return;
        }
        if (this.mPendingProximityDebounceTime <= this.mClock.uptimeMillis()) {
            if (this.mProximity != this.mPendingProximity) {
                this.mIgnoreProximityUntilChanged = false;
                String str = this.mTag;
                Slog.i(str, "No longer ignoring proximity [" + this.mPendingProximity + "]");
            }
            this.mProximity = this.mPendingProximity;
            this.mNudgeUpdatePowerState.run();
            if (this.mWakelockController.releaseWakelock(3)) {
                this.mPendingProximityDebounceTime = -1L;
                return;
            }
            return;
        }
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(1), this.mPendingProximityDebounceTime);
    }

    /* loaded from: classes.dex */
    public class DisplayPowerProximityStateHandler extends Handler {
        public DisplayPowerProximityStateHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                DisplayPowerProximityStateController.this.debounceProximitySensor();
            } else if (i != 2) {
            } else {
                DisplayPowerProximityStateController.this.ignoreProximitySensorUntilChangedInternal();
            }
        }
    }

    public final String proximityToString(int i) {
        return i != -1 ? i != 0 ? i != 1 ? Integer.toString(i) : "Positive" : "Negative" : "Unknown";
    }

    @VisibleForTesting
    public boolean getPendingWaitForNegativeProximityLocked() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPendingWaitForNegativeProximityLocked;
        }
        return z;
    }

    @VisibleForTesting
    public boolean getWaitingForNegativeProximity() {
        return this.mWaitingForNegativeProximity;
    }

    @VisibleForTesting
    public boolean shouldIgnoreProximityUntilChanged() {
        return this.mIgnoreProximityUntilChanged;
    }

    @VisibleForTesting
    public Handler getHandler() {
        return this.mHandler;
    }

    @VisibleForTesting
    public int getPendingProximity() {
        return this.mPendingProximity;
    }

    @VisibleForTesting
    public int getProximity() {
        return this.mProximity;
    }

    @VisibleForTesting
    public long getPendingProximityDebounceTime() {
        return this.mPendingProximityDebounceTime;
    }

    @VisibleForTesting
    public SensorEventListener getProximitySensorListener() {
        return this.mProximitySensorListener;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public Clock createClock() {
            return new Clock() { // from class: com.android.server.display.DisplayPowerProximityStateController$Injector$$ExternalSyntheticLambda0
                @Override // com.android.server.display.DisplayPowerProximityStateController.Clock
                public final long uptimeMillis() {
                    long uptimeMillis;
                    uptimeMillis = SystemClock.uptimeMillis();
                    return uptimeMillis;
                }
            };
        }
    }
}
