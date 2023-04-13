package com.android.server.power;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
@VisibleForTesting
/* loaded from: classes2.dex */
public class WirelessChargerDetector {
    public static final double MOVEMENT_ANGLE_COS_THRESHOLD = Math.cos(0.08726646259971647d);
    public boolean mAtRest;
    public boolean mDetectionInProgress;
    public long mDetectionStartTime;
    public float mFirstSampleX;
    public float mFirstSampleY;
    public float mFirstSampleZ;
    public Sensor mGravitySensor;
    public final Handler mHandler;
    public float mLastSampleX;
    public float mLastSampleY;
    public float mLastSampleZ;
    public int mMovingSamples;
    public boolean mMustUpdateRestPosition;
    public boolean mPoweredWirelessly;
    public float mRestX;
    public float mRestY;
    public float mRestZ;
    public final SensorManager mSensorManager;
    public final SuspendBlocker mSuspendBlocker;
    public int mTotalSamples;
    public final Object mLock = new Object();
    public final SensorEventListener mListener = new SensorEventListener() { // from class: com.android.server.power.WirelessChargerDetector.1
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized (WirelessChargerDetector.this.mLock) {
                WirelessChargerDetector wirelessChargerDetector = WirelessChargerDetector.this;
                float[] fArr = sensorEvent.values;
                wirelessChargerDetector.processSampleLocked(fArr[0], fArr[1], fArr[2]);
            }
        }
    };
    public final Runnable mSensorTimeout = new Runnable() { // from class: com.android.server.power.WirelessChargerDetector.2
        @Override // java.lang.Runnable
        public void run() {
            synchronized (WirelessChargerDetector.this.mLock) {
                WirelessChargerDetector.this.finishDetectionLocked();
            }
        }
    };

    public WirelessChargerDetector(SensorManager sensorManager, SuspendBlocker suspendBlocker, Handler handler) {
        this.mSensorManager = sensorManager;
        this.mSuspendBlocker = suspendBlocker;
        this.mHandler = handler;
        this.mGravitySensor = sensorManager.getDefaultSensor(9);
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println();
            printWriter.println("Wireless Charger Detector State:");
            printWriter.println("  mGravitySensor=" + this.mGravitySensor);
            printWriter.println("  mPoweredWirelessly=" + this.mPoweredWirelessly);
            printWriter.println("  mAtRest=" + this.mAtRest);
            printWriter.println("  mRestX=" + this.mRestX + ", mRestY=" + this.mRestY + ", mRestZ=" + this.mRestZ);
            StringBuilder sb = new StringBuilder();
            sb.append("  mDetectionInProgress=");
            sb.append(this.mDetectionInProgress);
            printWriter.println(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  mDetectionStartTime=");
            long j = this.mDetectionStartTime;
            sb2.append(j == 0 ? "0 (never)" : TimeUtils.formatUptime(j));
            printWriter.println(sb2.toString());
            printWriter.println("  mMustUpdateRestPosition=" + this.mMustUpdateRestPosition);
            printWriter.println("  mTotalSamples=" + this.mTotalSamples);
            printWriter.println("  mMovingSamples=" + this.mMovingSamples);
            printWriter.println("  mFirstSampleX=" + this.mFirstSampleX + ", mFirstSampleY=" + this.mFirstSampleY + ", mFirstSampleZ=" + this.mFirstSampleZ);
            printWriter.println("  mLastSampleX=" + this.mLastSampleX + ", mLastSampleY=" + this.mLastSampleY + ", mLastSampleZ=" + this.mLastSampleZ);
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        synchronized (this.mLock) {
            protoOutputStream.write(1133871366145L, this.mPoweredWirelessly);
            protoOutputStream.write(1133871366146L, this.mAtRest);
            long start2 = protoOutputStream.start(1146756268035L);
            protoOutputStream.write(1108101562369L, this.mRestX);
            protoOutputStream.write(1108101562370L, this.mRestY);
            protoOutputStream.write(1108101562371L, this.mRestZ);
            protoOutputStream.end(start2);
            protoOutputStream.write(1133871366148L, this.mDetectionInProgress);
            protoOutputStream.write(1112396529669L, this.mDetectionStartTime);
            protoOutputStream.write(1133871366150L, this.mMustUpdateRestPosition);
            protoOutputStream.write(1120986464263L, this.mTotalSamples);
            protoOutputStream.write(1120986464264L, this.mMovingSamples);
            long start3 = protoOutputStream.start(1146756268041L);
            protoOutputStream.write(1108101562369L, this.mFirstSampleX);
            protoOutputStream.write(1108101562370L, this.mFirstSampleY);
            protoOutputStream.write(1108101562371L, this.mFirstSampleZ);
            protoOutputStream.end(start3);
            long start4 = protoOutputStream.start(1146756268042L);
            protoOutputStream.write(1108101562369L, this.mLastSampleX);
            protoOutputStream.write(1108101562370L, this.mLastSampleY);
            protoOutputStream.write(1108101562371L, this.mLastSampleZ);
            protoOutputStream.end(start4);
        }
        protoOutputStream.end(start);
    }

    public boolean update(boolean z, int i) {
        boolean z2;
        synchronized (this.mLock) {
            boolean z3 = this.mPoweredWirelessly;
            z2 = true;
            if (z && i == 4) {
                this.mPoweredWirelessly = true;
                this.mMustUpdateRestPosition = true;
                startDetectionLocked();
            } else {
                this.mPoweredWirelessly = false;
                if (this.mAtRest) {
                    if (i != 0 && i != 4) {
                        this.mMustUpdateRestPosition = false;
                        clearAtRestLocked();
                    } else {
                        startDetectionLocked();
                    }
                }
            }
            if (!this.mPoweredWirelessly || z3 || this.mAtRest) {
                z2 = false;
            }
        }
        return z2;
    }

    public final void startDetectionLocked() {
        Sensor sensor;
        if (this.mDetectionInProgress || (sensor = this.mGravitySensor) == null || !this.mSensorManager.registerListener(this.mListener, sensor, 50000)) {
            return;
        }
        this.mSuspendBlocker.acquire();
        this.mDetectionInProgress = true;
        this.mDetectionStartTime = SystemClock.uptimeMillis();
        this.mTotalSamples = 0;
        this.mMovingSamples = 0;
        Message obtain = Message.obtain(this.mHandler, this.mSensorTimeout);
        obtain.setAsynchronous(true);
        this.mHandler.sendMessageDelayed(obtain, 800L);
    }

    public final void finishDetectionLocked() {
        if (this.mDetectionInProgress) {
            this.mSensorManager.unregisterListener(this.mListener);
            this.mHandler.removeCallbacks(this.mSensorTimeout);
            if (this.mMustUpdateRestPosition) {
                clearAtRestLocked();
                if (this.mTotalSamples < 3) {
                    Slog.w("WirelessChargerDetector", "Wireless charger detector is broken.  Only received " + this.mTotalSamples + " samples from the gravity sensor but we need at least 3 and we expect to see about 16 on average.");
                } else if (this.mMovingSamples == 0) {
                    this.mAtRest = true;
                    this.mRestX = this.mLastSampleX;
                    this.mRestY = this.mLastSampleY;
                    this.mRestZ = this.mLastSampleZ;
                }
                this.mMustUpdateRestPosition = false;
            }
            this.mDetectionInProgress = false;
            this.mSuspendBlocker.release();
        }
    }

    public final void processSampleLocked(float f, float f2, float f3) {
        if (this.mDetectionInProgress) {
            this.mLastSampleX = f;
            this.mLastSampleY = f2;
            this.mLastSampleZ = f3;
            int i = this.mTotalSamples + 1;
            this.mTotalSamples = i;
            if (i == 1) {
                this.mFirstSampleX = f;
                this.mFirstSampleY = f2;
                this.mFirstSampleZ = f3;
            } else if (hasMoved(this.mFirstSampleX, this.mFirstSampleY, this.mFirstSampleZ, f, f2, f3)) {
                this.mMovingSamples++;
            }
            if (this.mAtRest && hasMoved(this.mRestX, this.mRestY, this.mRestZ, f, f2, f3)) {
                clearAtRestLocked();
            }
        }
    }

    public final void clearAtRestLocked() {
        this.mAtRest = false;
        this.mRestX = 0.0f;
        this.mRestY = 0.0f;
        this.mRestZ = 0.0f;
    }

    public static boolean hasMoved(float f, float f2, float f3, float f4, float f5, float f6) {
        double d = (f * f4) + (f2 * f5) + (f3 * f6);
        double sqrt = Math.sqrt((f * f) + (f2 * f2) + (f3 * f3));
        double sqrt2 = Math.sqrt((f4 * f4) + (f5 * f5) + (f6 * f6));
        return sqrt < 8.806650161743164d || sqrt > 10.806650161743164d || sqrt2 < 8.806650161743164d || sqrt2 > 10.806650161743164d || d < (sqrt * sqrt2) * MOVEMENT_ANGLE_COS_THRESHOLD;
    }
}
