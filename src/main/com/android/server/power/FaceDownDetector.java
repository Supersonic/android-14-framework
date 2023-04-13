package com.android.server.power;

import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.clipboard.ClipboardService;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class FaceDownDetector implements SensorEventListener {
    public float mAccelerationThreshold;
    public Sensor mAccelerometer;
    public Context mContext;
    public final Handler mHandler;
    public boolean mIsEnabled;
    public final Consumer<Boolean> mOnFlip;
    @VisibleForTesting
    final BroadcastReceiver mScreenReceiver;
    public SensorManager mSensorManager;
    public int mSensorMaxLatencyMicros;
    public Duration mTimeThreshold;
    public final Runnable mUserActivityRunnable;
    public long mUserInteractionBackoffMillis;
    public float mZAccelerationThreshold;
    public float mZAccelerationThresholdLenient;
    public long mLastFlipTime = 0;
    public int mPreviousResultType = 1;
    public long mPreviousResultTime = 0;
    public long mMillisSaved = 0;
    public final ExponentialMovingAverage mCurrentXYAcceleration = new ExponentialMovingAverage(this, 0.5f);
    public final ExponentialMovingAverage mCurrentZAcceleration = new ExponentialMovingAverage(this, 0.5f);
    public boolean mFaceDown = false;
    public boolean mInteractive = false;
    public boolean mActive = false;
    public float mPrevAcceleration = 0.0f;
    public long mPrevAccelerationTime = 0;
    public boolean mZAccelerationIsFaceDown = false;
    public long mZAccelerationFaceDownTime = 0;

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public FaceDownDetector(Consumer<Boolean> consumer) {
        Objects.requireNonNull(consumer);
        this.mOnFlip = consumer;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mScreenReceiver = new ScreenStateReceiver();
        this.mUserActivityRunnable = new Runnable() { // from class: com.android.server.power.FaceDownDetector$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FaceDownDetector.this.lambda$new$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mFaceDown) {
            exitFaceDown(3, SystemClock.uptimeMillis() - this.mLastFlipTime);
            updateActiveState();
        }
    }

    public void systemReady(Context context) {
        this.mContext = context;
        SensorManager sensorManager = (SensorManager) context.getSystemService(SensorManager.class);
        this.mSensorManager = sensorManager;
        this.mAccelerometer = sensorManager.getDefaultSensor(1);
        readValuesFromDeviceConfig();
        DeviceConfig.addOnPropertiesChangedListener("attention_manager_service", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.power.FaceDownDetector$$ExternalSyntheticLambda1
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                FaceDownDetector.this.lambda$systemReady$1(properties);
            }
        });
        updateActiveState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemReady$1(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    public final void registerScreenReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.setPriority(1000);
        context.registerReceiver(this.mScreenReceiver, intentFilter);
    }

    public final void updateActiveState() {
        long uptimeMillis = SystemClock.uptimeMillis();
        boolean z = true;
        boolean z2 = this.mPreviousResultType == 3 && uptimeMillis - this.mPreviousResultTime < this.mUserInteractionBackoffMillis;
        boolean z3 = this.mInteractive;
        if (!z3 || !this.mIsEnabled || z2) {
            z = false;
        }
        if (this.mActive != z) {
            if (z) {
                this.mSensorManager.registerListener(this, this.mAccelerometer, 3, this.mSensorMaxLatencyMicros);
                if (this.mPreviousResultType == 4) {
                    logScreenOff();
                }
            } else {
                if (this.mFaceDown && !z3) {
                    this.mPreviousResultType = 4;
                    this.mPreviousResultTime = uptimeMillis;
                }
                this.mSensorManager.unregisterListener(this);
                this.mFaceDown = false;
                this.mOnFlip.accept(Boolean.FALSE);
            }
            this.mActive = z;
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("FaceDownDetector:");
        printWriter.println("  mFaceDown=" + this.mFaceDown);
        printWriter.println("  mActive=" + this.mActive);
        printWriter.println("  mLastFlipTime=" + this.mLastFlipTime);
        printWriter.println("  mSensorMaxLatencyMicros=" + this.mSensorMaxLatencyMicros);
        printWriter.println("  mUserInteractionBackoffMillis=" + this.mUserInteractionBackoffMillis);
        printWriter.println("  mPreviousResultTime=" + this.mPreviousResultTime);
        printWriter.println("  mPreviousResultType=" + this.mPreviousResultType);
        printWriter.println("  mMillisSaved=" + this.mMillisSaved);
        printWriter.println("  mZAccelerationThreshold=" + this.mZAccelerationThreshold);
        printWriter.println("  mAccelerationThreshold=" + this.mAccelerationThreshold);
        printWriter.println("  mTimeThreshold=" + this.mTimeThreshold);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1 && this.mActive && this.mIsEnabled) {
            float[] fArr = sensorEvent.values;
            float f = fArr[0];
            float f2 = fArr[1];
            this.mCurrentXYAcceleration.updateMovingAverage((f * f) + (f2 * f2));
            this.mCurrentZAcceleration.updateMovingAverage(sensorEvent.values[2]);
            long j = sensorEvent.timestamp;
            if (Math.abs(this.mCurrentXYAcceleration.mMovingAverage - this.mPrevAcceleration) > this.mAccelerationThreshold) {
                this.mPrevAcceleration = this.mCurrentXYAcceleration.mMovingAverage;
                this.mPrevAccelerationTime = j;
            }
            boolean z = j - this.mPrevAccelerationTime <= this.mTimeThreshold.toNanos();
            boolean z2 = this.mCurrentZAcceleration.mMovingAverage < (this.mFaceDown ? this.mZAccelerationThresholdLenient : this.mZAccelerationThreshold);
            boolean z3 = z2 && this.mZAccelerationIsFaceDown && j - this.mZAccelerationFaceDownTime > this.mTimeThreshold.toNanos();
            if (z2 && !this.mZAccelerationIsFaceDown) {
                this.mZAccelerationFaceDownTime = j;
                this.mZAccelerationIsFaceDown = true;
            } else if (!z2) {
                this.mZAccelerationIsFaceDown = false;
            }
            if (!z && z3 && !this.mFaceDown) {
                faceDownDetected();
            } else if (z3 || !this.mFaceDown) {
            } else {
                unFlipDetected();
            }
        }
    }

    public final void faceDownDetected() {
        this.mLastFlipTime = SystemClock.uptimeMillis();
        this.mFaceDown = true;
        this.mOnFlip.accept(Boolean.TRUE);
    }

    public final void unFlipDetected() {
        exitFaceDown(2, SystemClock.uptimeMillis() - this.mLastFlipTime);
    }

    public void userActivity(int i) {
        if (i != 5) {
            this.mHandler.post(this.mUserActivityRunnable);
        }
    }

    public final void exitFaceDown(int i, long j) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.FACE_DOWN_REPORTED, i, j, 0L, 0L);
        this.mFaceDown = false;
        this.mLastFlipTime = 0L;
        this.mPreviousResultType = i;
        this.mPreviousResultTime = SystemClock.uptimeMillis();
        this.mOnFlip.accept(Boolean.FALSE);
    }

    public final void logScreenOff() {
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mPreviousResultTime;
        FrameworkStatsLog.write((int) FrameworkStatsLog.FACE_DOWN_REPORTED, 4, j - this.mLastFlipTime, this.mMillisSaved, uptimeMillis - j);
        this.mPreviousResultType = 1;
    }

    public final boolean isEnabled() {
        return DeviceConfig.getBoolean("attention_manager_service", "enable_flip_to_screen_off", true) && this.mContext.getResources().getBoolean(17891686);
    }

    public final float getAccelerationThreshold() {
        return getFloatFlagValue("acceleration_threshold", 0.2f, -2.0f, 2.0f);
    }

    public final float getZAccelerationThreshold() {
        return getFloatFlagValue("z_acceleration_threshold", -9.5f, -15.0f, 0.0f);
    }

    public final long getUserInteractionBackoffMillis() {
        return getLongFlagValue("face_down_interaction_backoff_millis", 60000L, 0L, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
    }

    public final int getSensorMaxLatencyMicros() {
        return this.mContext.getResources().getInteger(17694847);
    }

    public final float getFloatFlagValue(String str, float f, float f2, float f3) {
        float f4 = DeviceConfig.getFloat("attention_manager_service", str, f);
        if (f4 < f2 || f4 > f3) {
            Slog.w("FaceDownDetector", "Bad flag value supplied for: " + str);
            return f;
        }
        return f4;
    }

    public final long getLongFlagValue(String str, long j, long j2, long j3) {
        long j4 = DeviceConfig.getLong("attention_manager_service", str, j);
        if (j4 < j2 || j4 > j3) {
            Slog.w("FaceDownDetector", "Bad flag value supplied for: " + str);
            return j;
        }
        return j4;
    }

    public final Duration getTimeThreshold() {
        long j = DeviceConfig.getLong("attention_manager_service", "time_threshold_millis", 1000L);
        if (j < 0 || j > 15000) {
            Slog.w("FaceDownDetector", "Bad flag value supplied for: time_threshold_millis");
            return Duration.ofMillis(1000L);
        }
        return Duration.ofMillis(j);
    }

    public final void onDeviceConfigChange(Set<String> set) {
        for (String str : set) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -1974380596:
                    if (str.equals("time_threshold_millis")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1762356372:
                    if (str.equals("acceleration_threshold")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1566292150:
                    if (str.equals("enable_flip_to_screen_off")) {
                        c = 2;
                        break;
                    }
                    break;
                case 941263057:
                    if (str.equals("z_acceleration_threshold")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                case 3:
                    readValuesFromDeviceConfig();
                    updateActiveState();
                    return;
                default:
                    Slog.i("FaceDownDetector", "Ignoring change on " + str);
            }
        }
    }

    public final void readValuesFromDeviceConfig() {
        this.mAccelerationThreshold = getAccelerationThreshold();
        float zAccelerationThreshold = getZAccelerationThreshold();
        this.mZAccelerationThreshold = zAccelerationThreshold;
        this.mZAccelerationThresholdLenient = zAccelerationThreshold + 1.0f;
        this.mTimeThreshold = getTimeThreshold();
        this.mSensorMaxLatencyMicros = getSensorMaxLatencyMicros();
        this.mUserInteractionBackoffMillis = getUserInteractionBackoffMillis();
        boolean z = this.mIsEnabled;
        boolean isEnabled = isEnabled();
        this.mIsEnabled = isEnabled;
        if (z != isEnabled) {
            if (!isEnabled) {
                this.mContext.unregisterReceiver(this.mScreenReceiver);
                this.mInteractive = false;
            } else {
                registerScreenReceiver(this.mContext);
                this.mInteractive = ((PowerManager) this.mContext.getSystemService(PowerManager.class)).isInteractive();
            }
        }
        Slog.i("FaceDownDetector", "readValuesFromDeviceConfig():\nmAccelerationThreshold=" + this.mAccelerationThreshold + "\nmZAccelerationThreshold=" + this.mZAccelerationThreshold + "\nmTimeThreshold=" + this.mTimeThreshold + "\nmIsEnabled=" + this.mIsEnabled);
    }

    public void setMillisSaved(long j) {
        this.mMillisSaved = j;
    }

    /* loaded from: classes2.dex */
    public final class ScreenStateReceiver extends BroadcastReceiver {
        public ScreenStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                FaceDownDetector.this.mInteractive = false;
                FaceDownDetector.this.updateActiveState();
            } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                FaceDownDetector.this.mInteractive = true;
                FaceDownDetector.this.updateActiveState();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class ExponentialMovingAverage {
        public final float mAlpha;
        public final float mInitialAverage;
        public float mMovingAverage;

        public ExponentialMovingAverage(FaceDownDetector faceDownDetector, float f) {
            this(f, 0.0f);
        }

        public ExponentialMovingAverage(float f, float f2) {
            this.mAlpha = f;
            this.mInitialAverage = f2;
            this.mMovingAverage = f2;
        }

        public void updateMovingAverage(float f) {
            this.mMovingAverage = f + (this.mAlpha * (this.mMovingAverage - f));
        }
    }
}
