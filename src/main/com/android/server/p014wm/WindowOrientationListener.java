package com.android.server.p014wm;

import android.app.ActivityThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.DeviceConfig;
import android.rotationresolver.RotationResolverInternal;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.p014wm.WindowOrientationListener;
import java.io.PrintWriter;
import java.util.Set;
/* renamed from: com.android.server.wm.WindowOrientationListener */
/* loaded from: classes2.dex */
public abstract class WindowOrientationListener {
    public static final boolean LOG = SystemProperties.getBoolean("debug.orientation.log", false);
    public final Context mContext;
    public int mCurrentRotation;
    public final int mDefaultRotation;
    public boolean mEnabled;
    public Handler mHandler;
    public final Object mLock;
    @VisibleForTesting
    OrientationJudge mOrientationJudge;
    public int mRate;
    @VisibleForTesting
    RotationResolverInternal mRotationResolverService;
    public Sensor mSensor;
    public SensorManager mSensorManager;
    public String mSensorType;

    public abstract boolean isKeyguardShowingAndNotOccluded();

    @VisibleForTesting
    public abstract boolean isRotationResolverEnabled();

    public abstract void onProposedRotationChanged(int i);

    public WindowOrientationListener(Context context, Handler handler, int i) {
        this(context, handler, i, 2);
    }

    public WindowOrientationListener(Context context, Handler handler, int i, int i2) {
        this.mCurrentRotation = -1;
        this.mLock = new Object();
        this.mContext = context;
        this.mHandler = handler;
        this.mDefaultRotation = i;
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        this.mSensorManager = sensorManager;
        this.mRate = i2;
        Sensor sensor = null;
        Sensor sensor2 = null;
        for (Sensor sensor3 : sensorManager.getSensorList(27)) {
            if (sensor3.isWakeUpSensor()) {
                sensor = sensor3;
            } else {
                sensor2 = sensor3;
            }
        }
        if (sensor != null) {
            this.mSensor = sensor;
        } else {
            this.mSensor = sensor2;
        }
        if (this.mSensor != null) {
            this.mOrientationJudge = new OrientationSensorJudge();
        }
        if (this.mOrientationJudge == null) {
            Sensor defaultSensor = this.mSensorManager.getDefaultSensor(1);
            this.mSensor = defaultSensor;
            if (defaultSensor != null) {
                this.mOrientationJudge = new AccelSensorJudge(context);
            }
        }
    }

    public void enable() {
        enable(true);
    }

    public void enable(boolean z) {
        synchronized (this.mLock) {
            if (this.mSensor == null) {
                Slog.w("WindowOrientationListener", "Cannot detect sensors. Not enabled");
            } else if (this.mEnabled) {
            } else {
                if (LOG) {
                    Slog.d("WindowOrientationListener", "WindowOrientationListener enabled clearCurrentRotation=" + z);
                }
                this.mOrientationJudge.resetLocked(z);
                if (this.mSensor.getType() == 1) {
                    this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, 100000, this.mHandler);
                } else {
                    this.mSensorManager.registerListener(this.mOrientationJudge, this.mSensor, this.mRate, this.mHandler);
                }
                this.mEnabled = true;
            }
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            if (this.mSensor == null) {
                Slog.w("WindowOrientationListener", "Cannot detect sensors. Invalid disable");
                return;
            }
            if (this.mEnabled) {
                if (LOG) {
                    Slog.d("WindowOrientationListener", "WindowOrientationListener disabled");
                }
                this.mSensorManager.unregisterListener(this.mOrientationJudge);
                this.mEnabled = false;
            }
        }
    }

    public void onTouchStart() {
        synchronized (this.mLock) {
            OrientationJudge orientationJudge = this.mOrientationJudge;
            if (orientationJudge != null) {
                orientationJudge.onTouchStartLocked();
            }
        }
    }

    public void onTouchEnd() {
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        synchronized (this.mLock) {
            OrientationJudge orientationJudge = this.mOrientationJudge;
            if (orientationJudge != null) {
                orientationJudge.onTouchEndLocked(elapsedRealtimeNanos);
            }
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void setCurrentRotation(int i) {
        synchronized (this.mLock) {
            this.mCurrentRotation = i;
        }
    }

    public int getProposedRotation() {
        synchronized (this.mLock) {
            if (this.mEnabled) {
                return this.mOrientationJudge.getProposedRotationLocked();
            }
            return -1;
        }
    }

    public boolean canDetectOrientation() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSensor != null;
        }
        return z;
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this.mLock) {
            printWriter.println(str + "WindowOrientationListener");
            String str2 = str + "  ";
            printWriter.println(str2 + "mEnabled=" + this.mEnabled);
            printWriter.println(str2 + "mCurrentRotation=" + Surface.rotationToString(this.mCurrentRotation));
            printWriter.println(str2 + "mSensorType=" + this.mSensorType);
            printWriter.println(str2 + "mSensor=" + this.mSensor);
            printWriter.println(str2 + "mRate=" + this.mRate);
            OrientationJudge orientationJudge = this.mOrientationJudge;
            if (orientationJudge != null) {
                orientationJudge.dumpLocked(printWriter, str2);
            }
        }
    }

    public boolean shouldStayEnabledWhileDreaming() {
        if (this.mContext.getResources().getBoolean(17891688)) {
            return true;
        }
        return this.mSensor.getType() == 27 && this.mSensor.isWakeUpSensor();
    }

    /* renamed from: com.android.server.wm.WindowOrientationListener$OrientationJudge */
    /* loaded from: classes2.dex */
    public abstract class OrientationJudge implements SensorEventListener {
        public abstract void dumpLocked(PrintWriter printWriter, String str);

        public abstract int getProposedRotationLocked();

        public abstract void onTouchEndLocked(long j);

        public abstract void onTouchStartLocked();

        public abstract void resetLocked(boolean z);

        public OrientationJudge() {
        }
    }

    /* renamed from: com.android.server.wm.WindowOrientationListener$AccelSensorJudge */
    /* loaded from: classes2.dex */
    public final class AccelSensorJudge extends OrientationJudge {
        public boolean mAccelerating;
        public long mAccelerationTimestampNanos;
        public boolean mFlat;
        public long mFlatTimestampNanos;
        public long mLastFilteredTimestampNanos;
        public float mLastFilteredX;
        public float mLastFilteredY;
        public float mLastFilteredZ;
        public boolean mOverhead;
        public int mPredictedRotation;
        public long mPredictedRotationTimestampNanos;
        public int mProposedRotation;
        public long mSwingTimestampNanos;
        public boolean mSwinging;
        public float[] mTiltHistory;
        public int mTiltHistoryIndex;
        public long[] mTiltHistoryTimestampNanos;
        public final int[][] mTiltToleranceConfig;
        public long mTouchEndedTimestampNanos;
        public boolean mTouched;

        public final boolean isAcceleratingLocked(float f) {
            return f < 5.80665f || f > 13.80665f;
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public final float remainingMS(long j, long j2) {
            if (j >= j2) {
                return 0.0f;
            }
            return ((float) (j2 - j)) * 1.0E-6f;
        }

        public AccelSensorJudge(Context context) {
            super();
            this.mTiltToleranceConfig = new int[][]{new int[]{-25, 70}, new int[]{-25, 65}, new int[]{-25, 60}, new int[]{-25, 65}};
            this.mTouchEndedTimestampNanos = Long.MIN_VALUE;
            this.mTiltHistory = new float[200];
            this.mTiltHistoryTimestampNanos = new long[200];
            int[] intArray = context.getResources().getIntArray(17235994);
            if (intArray.length != 8) {
                Slog.wtf("WindowOrientationListener", "config_autoRotationTiltTolerance should have exactly 8 elements");
                return;
            }
            for (int i = 0; i < 4; i++) {
                int i2 = i * 2;
                int i3 = intArray[i2];
                int i4 = intArray[i2 + 1];
                if (i3 >= -90 && i3 <= i4 && i4 <= 90) {
                    int[] iArr = this.mTiltToleranceConfig[i];
                    iArr[0] = i3;
                    iArr[1] = i4;
                } else {
                    Slog.wtf("WindowOrientationListener", "config_autoRotationTiltTolerance contains invalid range: min=" + i3 + ", max=" + i4);
                }
            }
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public int getProposedRotationLocked() {
            return this.mProposedRotation;
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void dumpLocked(PrintWriter printWriter, String str) {
            printWriter.println(str + "AccelSensorJudge");
            String str2 = str + "  ";
            printWriter.println(str2 + "mProposedRotation=" + this.mProposedRotation);
            printWriter.println(str2 + "mPredictedRotation=" + this.mPredictedRotation);
            printWriter.println(str2 + "mLastFilteredX=" + this.mLastFilteredX);
            printWriter.println(str2 + "mLastFilteredY=" + this.mLastFilteredY);
            printWriter.println(str2 + "mLastFilteredZ=" + this.mLastFilteredZ);
            long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos() - this.mLastFilteredTimestampNanos;
            printWriter.println(str2 + "mLastFilteredTimestampNanos=" + this.mLastFilteredTimestampNanos + " (" + (((float) elapsedRealtimeNanos) * 1.0E-6f) + "ms ago)");
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append("mTiltHistory={last: ");
            sb.append(getLastTiltLocked());
            sb.append("}");
            printWriter.println(sb.toString());
            printWriter.println(str2 + "mFlat=" + this.mFlat);
            printWriter.println(str2 + "mSwinging=" + this.mSwinging);
            printWriter.println(str2 + "mAccelerating=" + this.mAccelerating);
            printWriter.println(str2 + "mOverhead=" + this.mOverhead);
            printWriter.println(str2 + "mTouched=" + this.mTouched);
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append("mTiltToleranceConfig=[");
            printWriter.print(sb2.toString());
            for (int i = 0; i < 4; i++) {
                if (i != 0) {
                    printWriter.print(", ");
                }
                printWriter.print("[");
                printWriter.print(this.mTiltToleranceConfig[i][0]);
                printWriter.print(", ");
                printWriter.print(this.mTiltToleranceConfig[i][1]);
                printWriter.print("]");
            }
            printWriter.println("]");
        }

        /* JADX WARN: Removed duplicated region for block: B:28:0x00ef A[Catch: all -> 0x0342, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x001c, B:7:0x0057, B:9:0x0066, B:18:0x007c, B:20:0x0097, B:26:0x00e5, B:28:0x00ef, B:30:0x0103, B:32:0x0109, B:33:0x0110, B:34:0x0115, B:36:0x011b, B:38:0x0120, B:40:0x013b, B:42:0x0140, B:44:0x0146, B:48:0x014f, B:52:0x0158, B:54:0x015c, B:56:0x0162, B:57:0x0178, B:85:0x023f, B:87:0x024b, B:90:0x0255, B:92:0x025d, B:93:0x0313, B:89:0x0251, B:59:0x017f, B:61:0x0187, B:63:0x018d, B:64:0x01a3, B:65:0x01a7, B:67:0x01b9, B:68:0x01bb, B:72:0x01c4, B:74:0x01ca, B:76:0x01d0, B:78:0x01d9, B:79:0x0213, B:81:0x0219, B:82:0x0237, B:51:0x0156, B:22:0x00d4, B:24:0x00da, B:25:0x00e1), top: B:104:0x000b }] */
        /* JADX WARN: Removed duplicated region for block: B:87:0x024b A[Catch: all -> 0x0342, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x001c, B:7:0x0057, B:9:0x0066, B:18:0x007c, B:20:0x0097, B:26:0x00e5, B:28:0x00ef, B:30:0x0103, B:32:0x0109, B:33:0x0110, B:34:0x0115, B:36:0x011b, B:38:0x0120, B:40:0x013b, B:42:0x0140, B:44:0x0146, B:48:0x014f, B:52:0x0158, B:54:0x015c, B:56:0x0162, B:57:0x0178, B:85:0x023f, B:87:0x024b, B:90:0x0255, B:92:0x025d, B:93:0x0313, B:89:0x0251, B:59:0x017f, B:61:0x0187, B:63:0x018d, B:64:0x01a3, B:65:0x01a7, B:67:0x01b9, B:68:0x01bb, B:72:0x01c4, B:74:0x01ca, B:76:0x01d0, B:78:0x01d9, B:79:0x0213, B:81:0x0219, B:82:0x0237, B:51:0x0156, B:22:0x00d4, B:24:0x00da, B:25:0x00e1), top: B:104:0x000b }] */
        /* JADX WARN: Removed duplicated region for block: B:92:0x025d A[Catch: all -> 0x0342, TryCatch #0 {, blocks: (B:4:0x000b, B:6:0x001c, B:7:0x0057, B:9:0x0066, B:18:0x007c, B:20:0x0097, B:26:0x00e5, B:28:0x00ef, B:30:0x0103, B:32:0x0109, B:33:0x0110, B:34:0x0115, B:36:0x011b, B:38:0x0120, B:40:0x013b, B:42:0x0140, B:44:0x0146, B:48:0x014f, B:52:0x0158, B:54:0x015c, B:56:0x0162, B:57:0x0178, B:85:0x023f, B:87:0x024b, B:90:0x0255, B:92:0x025d, B:93:0x0313, B:89:0x0251, B:59:0x017f, B:61:0x0187, B:63:0x018d, B:64:0x01a3, B:65:0x01a7, B:67:0x01b9, B:68:0x01bb, B:72:0x01c4, B:74:0x01ca, B:76:0x01d0, B:78:0x01d9, B:79:0x0213, B:81:0x0219, B:82:0x0237, B:51:0x0156, B:22:0x00d4, B:24:0x00da, B:25:0x00e1), top: B:104:0x000b }] */
        @Override // android.hardware.SensorEventListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onSensorChanged(SensorEvent sensorEvent) {
            Object[] objArr;
            boolean z;
            boolean z2;
            int i;
            int i2;
            boolean z3;
            boolean z4;
            synchronized (WindowOrientationListener.this.mLock) {
                float[] fArr = sensorEvent.values;
                boolean z5 = false;
                int i3 = 0;
                float f = fArr[0];
                float f2 = fArr[1];
                float f3 = fArr[2];
                if (WindowOrientationListener.LOG) {
                    Slog.v("WindowOrientationListener", "Raw acceleration vector: x=" + f + ", y=" + f2 + ", z=" + f3 + ", magnitude=" + Math.sqrt((f * f) + (f2 * f2) + (f3 * f3)));
                }
                long j = sensorEvent.timestamp;
                long j2 = this.mLastFilteredTimestampNanos;
                float f4 = ((float) (j - j2)) * 1.0E-6f;
                if (j >= j2 && j <= j2 + 1000000000 && (f != 0.0f || f2 != 0.0f || f3 != 0.0f)) {
                    float f5 = f4 / (200.0f + f4);
                    float f6 = this.mLastFilteredX;
                    f = ((f - f6) * f5) + f6;
                    float f7 = this.mLastFilteredY;
                    f2 = ((f2 - f7) * f5) + f7;
                    float f8 = this.mLastFilteredZ;
                    f3 = (f5 * (f3 - f8)) + f8;
                    if (WindowOrientationListener.LOG) {
                        Slog.v("WindowOrientationListener", "Filtered acceleration vector: x=" + f + ", y=" + f2 + ", z=" + f3 + ", magnitude=" + Math.sqrt((f * f) + (f2 * f2) + (f3 * f3)));
                    }
                    objArr = null;
                    this.mLastFilteredTimestampNanos = j;
                    this.mLastFilteredX = f;
                    this.mLastFilteredY = f2;
                    this.mLastFilteredZ = f3;
                    if (objArr == null) {
                        float sqrt = (float) Math.sqrt((f * f) + (f2 * f2) + (f3 * f3));
                        if (sqrt < 1.0f) {
                            if (WindowOrientationListener.LOG) {
                                Slog.v("WindowOrientationListener", "Ignoring sensor data, magnitude too close to zero.");
                            }
                            clearPredictedRotationLocked();
                        } else {
                            if (isAcceleratingLocked(sqrt)) {
                                this.mAccelerationTimestampNanos = j;
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            int round = (int) Math.round(Math.asin(f3 / sqrt) * 57.295780181884766d);
                            float f9 = round;
                            addTiltHistoryEntryLocked(j, f9);
                            if (isFlatLocked(j)) {
                                this.mFlatTimestampNanos = j;
                                z3 = true;
                            } else {
                                z3 = false;
                            }
                            if (isSwingingLocked(j, f9)) {
                                this.mSwingTimestampNanos = j;
                                z = true;
                            } else {
                                z = false;
                            }
                            if (round <= -40) {
                                this.mOverhead = true;
                            } else if (round >= -15) {
                                this.mOverhead = false;
                            }
                            if (this.mOverhead) {
                                if (WindowOrientationListener.LOG) {
                                    Slog.v("WindowOrientationListener", "Ignoring sensor data, device is overhead: tiltAngle=" + round);
                                }
                                clearPredictedRotationLocked();
                            } else if (Math.abs(round) > 80) {
                                if (WindowOrientationListener.LOG) {
                                    Slog.v("WindowOrientationListener", "Ignoring sensor data, tilt angle too high: tiltAngle=" + round);
                                }
                                clearPredictedRotationLocked();
                            } else {
                                z4 = z3;
                                int round2 = (int) Math.round((-Math.atan2(-f, f2)) * 57.295780181884766d);
                                if (round2 < 0) {
                                    round2 += 360;
                                }
                                int i4 = (round2 + 45) / 90;
                                if (i4 != 4) {
                                    i3 = i4;
                                }
                                if (isTiltAngleAcceptableLocked(i3, round) && isOrientationAngleAcceptableLocked(i3, round2)) {
                                    updatePredictedRotationLocked(j, i3);
                                    if (WindowOrientationListener.LOG) {
                                        Slog.v("WindowOrientationListener", "Predicted: tiltAngle=" + round + ", orientationAngle=" + round2 + ", predictedRotation=" + this.mPredictedRotation + ", predictedRotationAgeMS=" + (((float) (j - this.mPredictedRotationTimestampNanos)) * 1.0E-6f));
                                    }
                                } else {
                                    if (WindowOrientationListener.LOG) {
                                        Slog.v("WindowOrientationListener", "Ignoring sensor data, no predicted rotation: tiltAngle=" + round + ", orientationAngle=" + round2);
                                    }
                                    clearPredictedRotationLocked();
                                }
                                z5 = z4;
                                this.mFlat = z5;
                                this.mSwinging = z;
                                this.mAccelerating = z2;
                                i = this.mProposedRotation;
                                if (this.mPredictedRotation >= 0 || isPredictedRotationAcceptableLocked(j)) {
                                    this.mProposedRotation = this.mPredictedRotation;
                                }
                                i2 = this.mProposedRotation;
                                if (WindowOrientationListener.LOG) {
                                    Slog.v("WindowOrientationListener", "Result: currentRotation=" + WindowOrientationListener.this.mCurrentRotation + ", proposedRotation=" + i2 + ", predictedRotation=" + this.mPredictedRotation + ", timeDeltaMS=" + f4 + ", isAccelerating=" + z2 + ", isFlat=" + z5 + ", isSwinging=" + z + ", isOverhead=" + this.mOverhead + ", isTouched=" + this.mTouched + ", timeUntilSettledMS=" + remainingMS(j, this.mPredictedRotationTimestampNanos + 40000000) + ", timeUntilAccelerationDelayExpiredMS=" + remainingMS(j, this.mAccelerationTimestampNanos + 500000000) + ", timeUntilFlatDelayExpiredMS=" + remainingMS(j, this.mFlatTimestampNanos + 500000000) + ", timeUntilSwingDelayExpiredMS=" + remainingMS(j, this.mSwingTimestampNanos + 300000000) + ", timeUntilTouchDelayExpiredMS=" + remainingMS(j, this.mTouchEndedTimestampNanos + 500000000));
                                }
                            }
                            z4 = z3;
                            z5 = z4;
                            this.mFlat = z5;
                            this.mSwinging = z;
                            this.mAccelerating = z2;
                            i = this.mProposedRotation;
                            if (this.mPredictedRotation >= 0) {
                            }
                            this.mProposedRotation = this.mPredictedRotation;
                            i2 = this.mProposedRotation;
                            if (WindowOrientationListener.LOG) {
                            }
                        }
                    }
                    z = false;
                    z2 = false;
                    this.mFlat = z5;
                    this.mSwinging = z;
                    this.mAccelerating = z2;
                    i = this.mProposedRotation;
                    if (this.mPredictedRotation >= 0) {
                    }
                    this.mProposedRotation = this.mPredictedRotation;
                    i2 = this.mProposedRotation;
                    if (WindowOrientationListener.LOG) {
                    }
                }
                if (WindowOrientationListener.LOG) {
                    Slog.v("WindowOrientationListener", "Resetting orientation listener.");
                }
                resetLocked(true);
                objArr = 1;
                this.mLastFilteredTimestampNanos = j;
                this.mLastFilteredX = f;
                this.mLastFilteredY = f2;
                this.mLastFilteredZ = f3;
                if (objArr == null) {
                }
                z = false;
                z2 = false;
                this.mFlat = z5;
                this.mSwinging = z;
                this.mAccelerating = z2;
                i = this.mProposedRotation;
                if (this.mPredictedRotation >= 0) {
                }
                this.mProposedRotation = this.mPredictedRotation;
                i2 = this.mProposedRotation;
                if (WindowOrientationListener.LOG) {
                }
            }
            if (i2 == i || i2 < 0) {
                return;
            }
            if (WindowOrientationListener.LOG) {
                Slog.v("WindowOrientationListener", "Proposed rotation changed!  proposedRotation=" + i2 + ", oldProposedRotation=" + i);
            }
            WindowOrientationListener.this.onProposedRotationChanged(i2);
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void onTouchStartLocked() {
            this.mTouched = true;
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void onTouchEndLocked(long j) {
            this.mTouched = false;
            this.mTouchEndedTimestampNanos = j;
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void resetLocked(boolean z) {
            this.mLastFilteredTimestampNanos = Long.MIN_VALUE;
            if (z) {
                this.mProposedRotation = -1;
            }
            this.mFlatTimestampNanos = Long.MIN_VALUE;
            this.mFlat = false;
            this.mSwingTimestampNanos = Long.MIN_VALUE;
            this.mSwinging = false;
            this.mAccelerationTimestampNanos = Long.MIN_VALUE;
            this.mAccelerating = false;
            this.mOverhead = false;
            clearPredictedRotationLocked();
            clearTiltHistoryLocked();
        }

        public final boolean isTiltAngleAcceptableLocked(int i, int i2) {
            int[] iArr = this.mTiltToleranceConfig[i];
            return i2 >= iArr[0] && i2 <= iArr[1];
        }

        public final boolean isOrientationAngleAcceptableLocked(int i, int i2) {
            int i3 = WindowOrientationListener.this.mCurrentRotation;
            if (i3 >= 0) {
                if (i == i3 || i == (i3 + 1) % 4) {
                    int i4 = ((i * 90) - 45) + 22;
                    if (i == 0) {
                        if (i2 >= 315 && i2 < i4 + 360) {
                            return false;
                        }
                    } else if (i2 < i4) {
                        return false;
                    }
                }
                if (i == i3 || i == (i3 + 3) % 4) {
                    int i5 = ((i * 90) + 45) - 22;
                    return i == 0 ? i2 > 45 || i2 <= i5 : i2 <= i5;
                }
                return true;
            }
            return true;
        }

        public final boolean isPredictedRotationAcceptableLocked(long j) {
            return j >= this.mPredictedRotationTimestampNanos + 40000000 && j >= this.mFlatTimestampNanos + 500000000 && j >= this.mSwingTimestampNanos + 300000000 && j >= this.mAccelerationTimestampNanos + 500000000 && !this.mTouched && j >= this.mTouchEndedTimestampNanos + 500000000;
        }

        public final void clearPredictedRotationLocked() {
            this.mPredictedRotation = -1;
            this.mPredictedRotationTimestampNanos = Long.MIN_VALUE;
        }

        public final void updatePredictedRotationLocked(long j, int i) {
            if (this.mPredictedRotation != i) {
                this.mPredictedRotation = i;
                this.mPredictedRotationTimestampNanos = j;
            }
        }

        public final void clearTiltHistoryLocked() {
            this.mTiltHistoryTimestampNanos[0] = Long.MIN_VALUE;
            this.mTiltHistoryIndex = 1;
        }

        public final void addTiltHistoryEntryLocked(long j, float f) {
            float[] fArr = this.mTiltHistory;
            int i = this.mTiltHistoryIndex;
            fArr[i] = f;
            long[] jArr = this.mTiltHistoryTimestampNanos;
            jArr[i] = j;
            int i2 = (i + 1) % 200;
            this.mTiltHistoryIndex = i2;
            jArr[i2] = Long.MIN_VALUE;
        }

        public final boolean isFlatLocked(long j) {
            int i = this.mTiltHistoryIndex;
            do {
                i = nextTiltHistoryIndexLocked(i);
                if (i < 0 || this.mTiltHistory[i] < 80.0f) {
                    return false;
                }
            } while (this.mTiltHistoryTimestampNanos[i] + 1000000000 > j);
            return true;
        }

        public final boolean isSwingingLocked(long j, float f) {
            int i = this.mTiltHistoryIndex;
            do {
                i = nextTiltHistoryIndexLocked(i);
                if (i < 0 || this.mTiltHistoryTimestampNanos[i] + 300000000 < j) {
                    return false;
                }
            } while (this.mTiltHistory[i] + 20.0f > f);
            return true;
        }

        public final int nextTiltHistoryIndexLocked(int i) {
            if (i == 0) {
                i = 200;
            }
            int i2 = i - 1;
            if (this.mTiltHistoryTimestampNanos[i2] != Long.MIN_VALUE) {
                return i2;
            }
            return -1;
        }

        public final float getLastTiltLocked() {
            int nextTiltHistoryIndexLocked = nextTiltHistoryIndexLocked(this.mTiltHistoryIndex);
            if (nextTiltHistoryIndexLocked >= 0) {
                return this.mTiltHistory[nextTiltHistoryIndexLocked];
            }
            return Float.NaN;
        }
    }

    /* renamed from: com.android.server.wm.WindowOrientationListener$OrientationSensorJudge */
    /* loaded from: classes2.dex */
    public final class OrientationSensorJudge extends OrientationJudge {
        public final ActivityTaskManagerInternal mActivityTaskManagerInternal;
        public Runnable mCancelRotationResolverRequest;
        public int mCurrentCallbackId;
        public int mDesiredRotation;
        public int mLastRotationResolution;
        public long mLastRotationResolutionTimeStamp;
        public int mProposedRotation;
        public boolean mRotationEvaluationScheduled;
        public Runnable mRotationEvaluator;
        public long mRotationMemorizationTimeoutMillis;
        public long mRotationResolverTimeoutMillis;
        public long mTouchEndedTimestampNanos;
        public boolean mTouching;

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public final int rotationToLogEnum(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        return i != 3 ? 0 : 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }

        public OrientationSensorJudge() {
            super();
            this.mTouchEndedTimestampNanos = Long.MIN_VALUE;
            this.mProposedRotation = -1;
            this.mDesiredRotation = -1;
            this.mLastRotationResolution = -1;
            this.mCurrentCallbackId = 0;
            this.mRotationEvaluator = new Runnable() { // from class: com.android.server.wm.WindowOrientationListener.OrientationSensorJudge.2
                @Override // java.lang.Runnable
                public void run() {
                    int evaluateRotationChangeLocked;
                    synchronized (WindowOrientationListener.this.mLock) {
                        OrientationSensorJudge.this.mRotationEvaluationScheduled = false;
                        evaluateRotationChangeLocked = OrientationSensorJudge.this.evaluateRotationChangeLocked();
                    }
                    if (evaluateRotationChangeLocked >= 0) {
                        WindowOrientationListener.this.onProposedRotationChanged(evaluateRotationChangeLocked);
                    }
                }
            };
            setupRotationResolverParameters();
            this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        }

        public final void setupRotationResolverParameters() {
            DeviceConfig.addOnPropertiesChangedListener("window_manager", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.wm.WindowOrientationListener$OrientationSensorJudge$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    WindowOrientationListener.OrientationSensorJudge.this.lambda$setupRotationResolverParameters$0(properties);
                }
            });
            readRotationResolverParameters();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setupRotationResolverParameters$0(DeviceConfig.Properties properties) {
            Set keyset = properties.getKeyset();
            if (keyset.contains("rotation_resolver_timeout_millis") || keyset.contains("rotation_memorization_timeout_millis")) {
                readRotationResolverParameters();
            }
        }

        public final void readRotationResolverParameters() {
            this.mRotationResolverTimeoutMillis = DeviceConfig.getLong("window_manager", "rotation_resolver_timeout_millis", 700L);
            this.mRotationMemorizationTimeoutMillis = DeviceConfig.getLong("window_manager", "rotation_memorization_timeout_millis", (long) BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS);
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public int getProposedRotationLocked() {
            return this.mProposedRotation;
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void onTouchStartLocked() {
            this.mTouching = true;
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void onTouchEndLocked(long j) {
            this.mTouching = false;
            this.mTouchEndedTimestampNanos = j;
            if (this.mDesiredRotation != this.mProposedRotation) {
                scheduleRotationEvaluationIfNecessaryLocked(SystemClock.elapsedRealtimeNanos());
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            WindowProcessController topApp;
            ApplicationInfo applicationInfo;
            final int i = (int) sensorEvent.values[0];
            if (i < 0 || i > 3) {
                return;
            }
            FrameworkStatsLog.write((int) FrameworkStatsLog.DEVICE_ROTATED, sensorEvent.timestamp, rotationToLogEnum(i), 2);
            if (WindowOrientationListener.this.isRotationResolverEnabled()) {
                if (WindowOrientationListener.this.isKeyguardShowingAndNotOccluded()) {
                    if (this.mLastRotationResolution != -1 && SystemClock.uptimeMillis() - this.mLastRotationResolutionTimeStamp < this.mRotationMemorizationTimeoutMillis) {
                        Slog.d("WindowOrientationListener", "Reusing the last rotation resolution: " + this.mLastRotationResolution);
                        finalizeRotation(this.mLastRotationResolution);
                        return;
                    }
                    finalizeRotation(WindowOrientationListener.this.mDefaultRotation);
                    return;
                }
                WindowOrientationListener windowOrientationListener = WindowOrientationListener.this;
                if (windowOrientationListener.mRotationResolverService == null) {
                    windowOrientationListener.mRotationResolverService = (RotationResolverInternal) LocalServices.getService(RotationResolverInternal.class);
                    if (WindowOrientationListener.this.mRotationResolverService == null) {
                        finalizeRotation(i);
                        return;
                    }
                }
                ActivityTaskManagerInternal activityTaskManagerInternal = this.mActivityTaskManagerInternal;
                String str = (activityTaskManagerInternal == null || (topApp = activityTaskManagerInternal.getTopApp()) == null || (applicationInfo = topApp.mInfo) == null || (r10 = applicationInfo.packageName) == null) ? null : null;
                this.mCurrentCallbackId++;
                if (this.mCancelRotationResolverRequest != null) {
                    WindowOrientationListener.this.getHandler().removeCallbacks(this.mCancelRotationResolverRequest);
                }
                final CancellationSignal cancellationSignal = new CancellationSignal();
                this.mCancelRotationResolverRequest = new Runnable() { // from class: com.android.server.wm.WindowOrientationListener$OrientationSensorJudge$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        cancellationSignal.cancel();
                    }
                };
                WindowOrientationListener.this.getHandler().postDelayed(this.mCancelRotationResolverRequest, this.mRotationResolverTimeoutMillis);
                WindowOrientationListener.this.mRotationResolverService.resolveRotation(new RotationResolverInternal.RotationResolverCallbackInternal() { // from class: com.android.server.wm.WindowOrientationListener.OrientationSensorJudge.1
                    public final int mCallbackId;

                    {
                        this.mCallbackId = OrientationSensorJudge.this.mCurrentCallbackId;
                    }

                    public void onSuccess(int i2) {
                        finalizeRotationIfFresh(i2);
                    }

                    public void onFailure(int i2) {
                        finalizeRotationIfFresh(i);
                    }

                    public final void finalizeRotationIfFresh(int i2) {
                        if (this.mCallbackId == OrientationSensorJudge.this.mCurrentCallbackId) {
                            WindowOrientationListener.this.getHandler().removeCallbacks(OrientationSensorJudge.this.mCancelRotationResolverRequest);
                            OrientationSensorJudge.this.finalizeRotation(i2);
                            return;
                        }
                        Slog.d("WindowOrientationListener", String.format("An outdated callback received [%s vs. %s]. Ignoring it.", Integer.valueOf(this.mCallbackId), Integer.valueOf(OrientationSensorJudge.this.mCurrentCallbackId)));
                    }
                }, str, i, WindowOrientationListener.this.mCurrentRotation, this.mRotationResolverTimeoutMillis, cancellationSignal);
                return;
            }
            finalizeRotation(i);
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void dumpLocked(PrintWriter printWriter, String str) {
            printWriter.println(str + "OrientationSensorJudge");
            String str2 = str + "  ";
            printWriter.println(str2 + "mDesiredRotation=" + Surface.rotationToString(this.mDesiredRotation));
            printWriter.println(str2 + "mProposedRotation=" + Surface.rotationToString(this.mProposedRotation));
            printWriter.println(str2 + "mTouching=" + this.mTouching);
            printWriter.println(str2 + "mTouchEndedTimestampNanos=" + this.mTouchEndedTimestampNanos);
            printWriter.println(str2 + "mLastRotationResolution=" + this.mLastRotationResolution);
        }

        @Override // com.android.server.p014wm.WindowOrientationListener.OrientationJudge
        public void resetLocked(boolean z) {
            if (z) {
                this.mProposedRotation = -1;
                this.mDesiredRotation = -1;
            }
            this.mTouching = false;
            this.mTouchEndedTimestampNanos = Long.MIN_VALUE;
            unscheduleRotationEvaluationLocked();
        }

        public int evaluateRotationChangeLocked() {
            unscheduleRotationEvaluationLocked();
            if (this.mDesiredRotation == this.mProposedRotation) {
                return -1;
            }
            long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
            if (isDesiredRotationAcceptableLocked(elapsedRealtimeNanos)) {
                int i = this.mDesiredRotation;
                this.mProposedRotation = i;
                return i;
            }
            scheduleRotationEvaluationIfNecessaryLocked(elapsedRealtimeNanos);
            return -1;
        }

        public final void finalizeRotation(int i) {
            int evaluateRotationChangeLocked;
            synchronized (WindowOrientationListener.this.mLock) {
                this.mDesiredRotation = i;
                evaluateRotationChangeLocked = evaluateRotationChangeLocked();
            }
            if (evaluateRotationChangeLocked >= 0) {
                this.mLastRotationResolution = evaluateRotationChangeLocked;
                this.mLastRotationResolutionTimeStamp = SystemClock.uptimeMillis();
                WindowOrientationListener.this.onProposedRotationChanged(evaluateRotationChangeLocked);
            }
        }

        public final boolean isDesiredRotationAcceptableLocked(long j) {
            return !this.mTouching && j >= this.mTouchEndedTimestampNanos + 500000000;
        }

        public final void scheduleRotationEvaluationIfNecessaryLocked(long j) {
            if (this.mRotationEvaluationScheduled || this.mDesiredRotation == this.mProposedRotation) {
                if (WindowOrientationListener.LOG) {
                    Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, an evaluation is already scheduled or is unnecessary.");
                }
            } else if (this.mTouching) {
                if (WindowOrientationListener.LOG) {
                    Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, user is still touching the screen.");
                }
            } else {
                long j2 = this.mTouchEndedTimestampNanos + 500000000;
                if (j >= j2) {
                    if (WindowOrientationListener.LOG) {
                        Slog.d("WindowOrientationListener", "scheduleRotationEvaluationLocked: ignoring, already past the next possible time of rotation.");
                        return;
                    }
                    return;
                }
                WindowOrientationListener.this.mHandler.postDelayed(this.mRotationEvaluator, (long) Math.ceil(((float) (j2 - j)) * 1.0E-6f));
                this.mRotationEvaluationScheduled = true;
            }
        }

        public final void unscheduleRotationEvaluationLocked() {
            if (this.mRotationEvaluationScheduled) {
                WindowOrientationListener.this.mHandler.removeCallbacks(this.mRotationEvaluator);
                this.mRotationEvaluationScheduled = false;
            }
        }
    }
}
