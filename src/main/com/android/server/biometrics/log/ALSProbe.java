package com.android.server.biometrics.log;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.log.ALSProbe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class ALSProbe implements Probe {
    public boolean mDestroyRequested;
    public boolean mDestroyed;
    public boolean mDisableRequested;
    public boolean mEnabled;
    public volatile float mLastAmbientLux;
    public final Sensor mLightSensor;
    public final SensorEventListener mLightSensorListener;
    public long mMaxSubscriptionTime;
    public NextConsumer mNextConsumer;
    public final SensorManager mSensorManager;
    public final Handler mTimer;

    public ALSProbe(SensorManager sensorManager) {
        this(sensorManager, new Handler(Looper.getMainLooper()), TimeUnit.MINUTES.toMillis(1L));
    }

    @VisibleForTesting
    public ALSProbe(SensorManager sensorManager, Handler handler, long j) {
        this.mMaxSubscriptionTime = -1L;
        this.mEnabled = false;
        this.mDestroyed = false;
        this.mDestroyRequested = false;
        this.mDisableRequested = false;
        this.mNextConsumer = null;
        this.mLastAmbientLux = -1.0f;
        this.mLightSensorListener = new SensorEventListener() { // from class: com.android.server.biometrics.log.ALSProbe.1
            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent sensorEvent) {
                ALSProbe.this.onNext(sensorEvent.values[0]);
            }
        };
        this.mSensorManager = sensorManager;
        Sensor defaultSensor = sensorManager != null ? sensorManager.getDefaultSensor(5) : null;
        this.mLightSensor = defaultSensor;
        this.mTimer = handler;
        this.mMaxSubscriptionTime = j;
        if (sensorManager == null || defaultSensor == null) {
            Slog.w("ALSProbe", "No sensor - probe disabled");
            this.mDestroyed = true;
        }
    }

    @Override // com.android.server.biometrics.log.Probe
    public synchronized void enable() {
        if (!this.mDestroyed && !this.mDestroyRequested) {
            this.mDisableRequested = false;
            enableLightSensorLoggingLocked();
        }
    }

    @Override // com.android.server.biometrics.log.Probe
    public synchronized void disable() {
        this.mDisableRequested = true;
        if (!this.mDestroyed && this.mNextConsumer == null) {
            disableLightSensorLoggingLocked(false);
        }
    }

    @Override // com.android.server.biometrics.log.Probe
    public synchronized void destroy() {
        this.mDestroyRequested = true;
        if (!this.mDestroyed && this.mNextConsumer == null) {
            disableLightSensorLoggingLocked(true);
            this.mDestroyed = true;
        }
    }

    public final synchronized void onNext(float f) {
        this.mLastAmbientLux = f;
        NextConsumer nextConsumer = this.mNextConsumer;
        this.mNextConsumer = null;
        if (nextConsumer != null) {
            Slog.v("ALSProbe", "Finishing next consumer");
            if (this.mDestroyRequested) {
                destroy();
            } else if (this.mDisableRequested) {
                disable();
            }
            nextConsumer.consume(f);
        }
    }

    public float getMostRecentLux() {
        return this.mLastAmbientLux;
    }

    public synchronized void awaitNextLux(Consumer<Float> consumer, Handler handler) {
        NextConsumer nextConsumer = new NextConsumer(consumer, handler);
        float f = this.mLastAmbientLux;
        if (f > -1.0f) {
            nextConsumer.consume(f);
        } else {
            NextConsumer nextConsumer2 = this.mNextConsumer;
            if (nextConsumer2 != null) {
                nextConsumer2.add(nextConsumer);
            } else {
                this.mDestroyed = false;
                this.mNextConsumer = nextConsumer;
                enableLightSensorLoggingLocked();
            }
        }
    }

    public final void enableLightSensorLoggingLocked() {
        if (!this.mEnabled) {
            this.mEnabled = true;
            this.mLastAmbientLux = -1.0f;
            this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, 3);
            Slog.v("ALSProbe", "Enable ALS: " + this.mLightSensorListener.hashCode());
        }
        resetTimerLocked(true);
    }

    public final void disableLightSensorLoggingLocked(boolean z) {
        resetTimerLocked(false);
        if (this.mEnabled) {
            this.mEnabled = false;
            if (!z) {
                this.mLastAmbientLux = -1.0f;
            }
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            Slog.v("ALSProbe", "Disable ALS: " + this.mLightSensorListener.hashCode());
        }
    }

    public final void resetTimerLocked(boolean z) {
        this.mTimer.removeCallbacksAndMessages(this);
        if (!z || this.mMaxSubscriptionTime <= 0) {
            return;
        }
        this.mTimer.postDelayed(new Runnable() { // from class: com.android.server.biometrics.log.ALSProbe$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ALSProbe.this.onTimeout();
            }
        }, this, this.mMaxSubscriptionTime);
    }

    public final synchronized void onTimeout() {
        Slog.e("ALSProbe", "Max time exceeded for ALS logger - disabling: " + this.mLightSensorListener.hashCode());
        onNext(this.mLastAmbientLux);
        disable();
    }

    /* loaded from: classes.dex */
    public static class NextConsumer {
        public final Consumer<Float> mConsumer;
        public final Handler mHandler;
        public final List<NextConsumer> mOthers;

        public NextConsumer(Consumer<Float> consumer, Handler handler) {
            this.mOthers = new ArrayList();
            this.mConsumer = consumer;
            this.mHandler = handler;
        }

        public void consume(final float f) {
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.post(new Runnable() { // from class: com.android.server.biometrics.log.ALSProbe$NextConsumer$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ALSProbe.NextConsumer.this.lambda$consume$0(f);
                    }
                });
            } else {
                this.mConsumer.accept(Float.valueOf(f));
            }
            for (NextConsumer nextConsumer : this.mOthers) {
                nextConsumer.consume(f);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$consume$0(float f) {
            this.mConsumer.accept(Float.valueOf(f));
        }

        public void add(NextConsumer nextConsumer) {
            this.mOthers.add(nextConsumer);
        }
    }
}
