package com.android.server.vibrator;

import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.WorkSource;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.vibrator.Vibration;
import java.util.NoSuchElementException;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class VibrationThread extends Thread {
    public VibrationStepConductor mExecutingConductor;
    @GuardedBy({"mLock"})
    public VibrationStepConductor mRequestedActiveConductor;
    public final VibratorManagerHooks mVibratorManagerHooks;
    public final PowerManager.WakeLock mWakeLock;
    public final Object mLock = new Object();
    public boolean mCalledVibrationCompleteCallback = false;

    /* loaded from: classes2.dex */
    public interface VibratorManagerHooks {
        void cancelSyncedVibration();

        void noteVibratorOff(int i);

        void noteVibratorOn(int i, long j);

        void onVibrationCompleted(long j, Vibration.EndInfo endInfo);

        void onVibrationThreadReleased(long j);

        boolean prepareSyncedVibration(long j, int[] iArr);

        boolean triggerSyncedVibration(long j);
    }

    public VibrationThread(PowerManager.WakeLock wakeLock, VibratorManagerHooks vibratorManagerHooks) {
        this.mWakeLock = wakeLock;
        this.mVibratorManagerHooks = vibratorManagerHooks;
    }

    public boolean runVibrationOnVibrationThread(VibrationStepConductor vibrationStepConductor) {
        synchronized (this.mLock) {
            if (this.mRequestedActiveConductor != null) {
                Slog.wtf("VibrationThread", "Attempt to start vibration when one already running");
                return false;
            }
            this.mRequestedActiveConductor = vibrationStepConductor;
            this.mLock.notifyAll();
            return true;
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setThreadPriority(-8);
        while (true) {
            VibrationStepConductor waitForVibrationRequest = waitForVibrationRequest();
            Objects.requireNonNull(waitForVibrationRequest);
            this.mExecutingConductor = waitForVibrationRequest;
            this.mCalledVibrationCompleteCallback = false;
            runCurrentVibrationWithWakeLock();
            if (!this.mExecutingConductor.isFinished()) {
                Slog.wtf("VibrationThread", "VibrationThread terminated with unfinished vibration");
            }
            synchronized (this.mLock) {
                this.mRequestedActiveConductor = null;
            }
            this.mVibratorManagerHooks.onVibrationThreadReleased(this.mExecutingConductor.getVibration().f1160id);
            synchronized (this.mLock) {
                this.mLock.notifyAll();
            }
            this.mExecutingConductor = null;
        }
    }

    public boolean waitForThreadIdle(long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = j + elapsedRealtime;
        synchronized (this.mLock) {
            while (this.mRequestedActiveConductor != null) {
                if (elapsedRealtime >= j2) {
                    return false;
                }
                try {
                    this.mLock.wait(j2 - elapsedRealtime);
                } catch (InterruptedException unused) {
                    Slog.w("VibrationThread", "VibrationThread interrupted waiting to stop, continuing");
                }
                elapsedRealtime = SystemClock.elapsedRealtime();
            }
            return true;
        }
    }

    public final VibrationStepConductor waitForVibrationRequest() {
        while (true) {
            synchronized (this.mLock) {
                VibrationStepConductor vibrationStepConductor = this.mRequestedActiveConductor;
                if (vibrationStepConductor != null) {
                    return vibrationStepConductor;
                }
                try {
                    this.mLock.wait();
                } catch (InterruptedException unused) {
                    Slog.w("VibrationThread", "VibrationThread interrupted waiting to start, continuing");
                }
            }
        }
    }

    @VisibleForTesting
    public boolean isRunningVibrationId(long j) {
        boolean z;
        synchronized (this.mLock) {
            VibrationStepConductor vibrationStepConductor = this.mRequestedActiveConductor;
            z = vibrationStepConductor != null && vibrationStepConductor.getVibration().f1160id == j;
        }
        return z;
    }

    public final void runCurrentVibrationWithWakeLock() {
        this.mWakeLock.setWorkSource(new WorkSource(this.mExecutingConductor.getVibration().callerInfo.uid));
        this.mWakeLock.acquire();
        try {
            runCurrentVibrationWithWakeLockAndDeathLink();
            clientVibrationCompleteIfNotAlready(new Vibration.EndInfo(Vibration.Status.FINISHED_UNEXPECTED));
        } finally {
            this.mWakeLock.release();
            this.mWakeLock.setWorkSource(null);
        }
    }

    public final void runCurrentVibrationWithWakeLockAndDeathLink() {
        IBinder iBinder = this.mExecutingConductor.getVibration().callerToken;
        try {
            iBinder.linkToDeath(this.mExecutingConductor, 0);
            try {
                playVibration();
                try {
                    iBinder.unlinkToDeath(this.mExecutingConductor, 0);
                } catch (NoSuchElementException e) {
                    Slog.wtf("VibrationThread", "Failed to unlink token", e);
                }
            } catch (Throwable th) {
                try {
                    iBinder.unlinkToDeath(this.mExecutingConductor, 0);
                } catch (NoSuchElementException e2) {
                    Slog.wtf("VibrationThread", "Failed to unlink token", e2);
                }
                throw th;
            }
        } catch (RemoteException e3) {
            Slog.e("VibrationThread", "Error linking vibration to token death", e3);
            clientVibrationCompleteIfNotAlready(new Vibration.EndInfo(Vibration.Status.IGNORED_ERROR_TOKEN));
        }
    }

    public final void clientVibrationCompleteIfNotAlready(Vibration.EndInfo endInfo) {
        if (this.mCalledVibrationCompleteCallback) {
            return;
        }
        this.mCalledVibrationCompleteCallback = true;
        this.mVibratorManagerHooks.onVibrationCompleted(this.mExecutingConductor.getVibration().f1160id, endInfo);
    }

    public final void playVibration() {
        Vibration.EndInfo calculateVibrationEndInfo;
        Trace.traceBegin(8388608L, "playVibration");
        try {
            this.mExecutingConductor.prepareToStart();
            while (!this.mExecutingConductor.isFinished()) {
                if (this.mExecutingConductor.waitUntilNextStepIsDue()) {
                    this.mExecutingConductor.runNextStep();
                }
                if (!this.mCalledVibrationCompleteCallback && (calculateVibrationEndInfo = this.mExecutingConductor.calculateVibrationEndInfo()) != null) {
                    clientVibrationCompleteIfNotAlready(calculateVibrationEndInfo);
                }
            }
        } finally {
            Trace.traceEnd(8388608L);
        }
    }
}
