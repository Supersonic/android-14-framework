package com.android.server.biometrics.log;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.biometrics.Utils;
/* loaded from: classes.dex */
public class BiometricLogger {
    public final ALSProbe mALSProbe;
    public long mFirstAcquireTimeMs;
    public boolean mShouldLogMetrics;
    public final BiometricFrameworkStatsLogger mSink;
    public final int mStatsAction;
    public final int mStatsClient;
    public final int mStatsModality;

    public static BiometricLogger ofUnknown(Context context) {
        return new BiometricLogger(context, 0, 0, 0);
    }

    public BiometricLogger(Context context, int i, int i2, int i3) {
        this(i, i2, i3, BiometricFrameworkStatsLogger.getInstance(), (SensorManager) context.getSystemService(SensorManager.class));
    }

    @VisibleForTesting
    public BiometricLogger(int i, int i2, int i3, BiometricFrameworkStatsLogger biometricFrameworkStatsLogger, SensorManager sensorManager) {
        this.mShouldLogMetrics = true;
        this.mStatsModality = i;
        this.mStatsAction = i2;
        this.mStatsClient = i3;
        this.mSink = biometricFrameworkStatsLogger;
        this.mALSProbe = new ALSProbe(sensorManager);
    }

    public BiometricLogger swapAction(Context context, int i) {
        return new BiometricLogger(context, this.mStatsModality, i, this.mStatsClient);
    }

    public void disableMetrics() {
        this.mShouldLogMetrics = false;
        this.mALSProbe.destroy();
    }

    public int getStatsClient() {
        return this.mStatsClient;
    }

    public final boolean shouldSkipLogging() {
        int i = this.mStatsModality;
        boolean z = i == 0 || this.mStatsAction == 0;
        if (i == 0) {
            Slog.w("BiometricLogger", "Unknown field detected: MODALITY_UNKNOWN, will not report metric");
        }
        if (this.mStatsAction == 0) {
            Slog.w("BiometricLogger", "Unknown field detected: ACTION_UNKNOWN, will not report metric");
        }
        if (this.mStatsClient == 0) {
            Slog.w("BiometricLogger", "Unknown field detected: CLIENT_UNKNOWN");
        }
        return z;
    }

    public void logOnAcquired(Context context, OperationContextExt operationContextExt, int i, int i2, int i3) {
        if (this.mShouldLogMetrics) {
            int i4 = this.mStatsModality;
            boolean z = i4 == 4;
            boolean z2 = i4 == 1;
            if (z || z2) {
                if ((z2 && i == 7) || (z && i == 20)) {
                    this.mFirstAcquireTimeMs = System.currentTimeMillis();
                }
            } else if (i == 0 && this.mFirstAcquireTimeMs == 0) {
                this.mFirstAcquireTimeMs = System.currentTimeMillis();
            }
            if (shouldSkipLogging()) {
                return;
            }
            this.mSink.acquired(operationContextExt, this.mStatsModality, this.mStatsAction, this.mStatsClient, Utils.isDebugEnabled(context, i3), i, i2, i3);
        }
    }

    public void logOnError(Context context, OperationContextExt operationContextExt, int i, int i2, int i3) {
        if (this.mShouldLogMetrics) {
            long currentTimeMillis = this.mFirstAcquireTimeMs != 0 ? System.currentTimeMillis() - this.mFirstAcquireTimeMs : -1L;
            Slog.v("BiometricLogger", "Error latency: " + currentTimeMillis);
            if (shouldSkipLogging()) {
                return;
            }
            this.mSink.error(operationContextExt, this.mStatsModality, this.mStatsAction, this.mStatsClient, Utils.isDebugEnabled(context, i3), currentTimeMillis, i, i2, i3);
        }
    }

    public void logOnAuthenticated(Context context, OperationContextExt operationContextExt, boolean z, boolean z2, int i, boolean z3) {
        if (this.mShouldLogMetrics) {
            int i2 = !z ? 1 : (z3 && z2) ? 2 : 3;
            long currentTimeMillis = this.mFirstAcquireTimeMs != 0 ? System.currentTimeMillis() - this.mFirstAcquireTimeMs : -1L;
            Slog.v("BiometricLogger", "Authentication latency: " + currentTimeMillis);
            if (shouldSkipLogging()) {
                return;
            }
            this.mSink.authenticate(operationContextExt, this.mStatsModality, this.mStatsAction, this.mStatsClient, Utils.isDebugEnabled(context, i), currentTimeMillis, i2, z2, i, this.mALSProbe);
        }
    }

    public void logOnEnrolled(int i, long j, boolean z) {
        if (this.mShouldLogMetrics) {
            Slog.v("BiometricLogger", "Enroll latency: " + j);
            if (shouldSkipLogging()) {
                return;
            }
            this.mSink.enroll(this.mStatsModality, this.mStatsAction, this.mStatsClient, i, j, z, this.mALSProbe.getMostRecentLux());
        }
    }

    public void logUnknownEnrollmentInHal() {
        if (shouldSkipLogging()) {
            return;
        }
        this.mSink.reportUnknownTemplateEnrolledHal(this.mStatsModality);
    }

    public void logUnknownEnrollmentInFramework() {
        if (shouldSkipLogging()) {
            return;
        }
        this.mSink.reportUnknownTemplateEnrolledFramework(this.mStatsModality);
    }

    public CallbackWithProbe<Probe> getAmbientLightProbe(boolean z) {
        return new CallbackWithProbe<>(this.mALSProbe, z);
    }
}
