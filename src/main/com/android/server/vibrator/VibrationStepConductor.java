package com.android.server.vibrator;

import android.os.Build;
import android.os.CombinedVibration;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.vibrator.PrebakedSegment;
import android.os.vibrator.PrimitiveSegment;
import android.os.vibrator.RampSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.IntArray;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.server.vibrator.Vibration;
import com.android.server.vibrator.VibrationThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
/* loaded from: classes2.dex */
public final class VibrationStepConductor implements IBinder.DeathRecipient {
    public static final List<Step> EMPTY_STEP_LIST = new ArrayList();
    public final DeviceVibrationEffectAdapter deviceEffectAdapter;
    public int mPendingVibrateSteps;
    public int mRemainingStartSequentialEffectSteps;
    @GuardedBy({"mLock"})
    public final IntArray mSignalVibratorsComplete;
    public int mSuccessfulVibratorOnSteps;
    public final HalVibration mVibration;
    public final VibrationSettings vibrationSettings;
    public final VibrationThread.VibratorManagerHooks vibratorManagerHooks;
    public final SparseArray<VibratorController> mVibrators = new SparseArray<>();
    public final PriorityQueue<Step> mNextSteps = new PriorityQueue<>();
    public final Queue<Step> mPendingOnVibratorCompleteSteps = new LinkedList();
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public Vibration.EndInfo mSignalCancel = null;
    @GuardedBy({"mLock"})
    public boolean mSignalCancelImmediate = false;
    public Vibration.EndInfo mCancelledVibrationEndInfo = null;
    public boolean mCancelledImmediately = false;

    public VibrationStepConductor(HalVibration halVibration, VibrationSettings vibrationSettings, DeviceVibrationEffectAdapter deviceVibrationEffectAdapter, SparseArray<VibratorController> sparseArray, VibrationThread.VibratorManagerHooks vibratorManagerHooks) {
        this.mVibration = halVibration;
        this.vibrationSettings = vibrationSettings;
        this.deviceEffectAdapter = deviceVibrationEffectAdapter;
        this.vibratorManagerHooks = vibratorManagerHooks;
        CombinedVibration effect = halVibration.getEffect();
        for (int i = 0; i < sparseArray.size(); i++) {
            if (effect.hasVibrator(sparseArray.keyAt(i))) {
                this.mVibrators.put(sparseArray.keyAt(i), sparseArray.valueAt(i));
            }
        }
        this.mSignalVibratorsComplete = new IntArray(this.mVibrators.size());
    }

    public AbstractVibratorStep nextVibrateStep(long j, VibratorController vibratorController, VibrationEffect.Composed composed, int i, long j2) {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        int repeatIndex = i >= composed.getSegments().size() ? composed.getRepeatIndex() : i;
        if (repeatIndex < 0) {
            return new CompleteEffectVibratorStep(this, j, false, vibratorController, j2);
        }
        VibrationEffectSegment vibrationEffectSegment = (VibrationEffectSegment) composed.getSegments().get(repeatIndex);
        if (vibrationEffectSegment instanceof PrebakedSegment) {
            return new PerformPrebakedVibratorStep(this, j, vibratorController, composed, repeatIndex, j2);
        }
        if (vibrationEffectSegment instanceof PrimitiveSegment) {
            return new ComposePrimitivesVibratorStep(this, j, vibratorController, composed, repeatIndex, j2);
        }
        if (vibrationEffectSegment instanceof RampSegment) {
            return new ComposePwleVibratorStep(this, j, vibratorController, composed, repeatIndex, j2);
        }
        return new SetAmplitudeVibratorStep(this, j, vibratorController, composed, repeatIndex, j2);
    }

    public void prepareToStart() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        CombinedVibration.Sequential sequential = toSequential(this.mVibration.getEffect());
        this.mPendingVibrateSteps++;
        this.mRemainingStartSequentialEffectSteps = sequential.getEffects().size();
        this.mNextSteps.offer(new StartSequentialEffectStep(this, sequential));
        this.mVibration.stats.reportStarted();
    }

    public HalVibration getVibration() {
        return this.mVibration;
    }

    public SparseArray<VibratorController> getVibrators() {
        return this.mVibrators;
    }

    public boolean isFinished() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        if (this.mCancelledImmediately) {
            return true;
        }
        return this.mPendingOnVibratorCompleteSteps.isEmpty() && this.mNextSteps.isEmpty();
    }

    public Vibration.EndInfo calculateVibrationEndInfo() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        Vibration.EndInfo endInfo = this.mCancelledVibrationEndInfo;
        if (endInfo != null) {
            return endInfo;
        }
        if (this.mPendingVibrateSteps > 0 || this.mRemainingStartSequentialEffectSteps > 0) {
            return null;
        }
        if (this.mSuccessfulVibratorOnSteps > 0) {
            return new Vibration.EndInfo(Vibration.Status.FINISHED);
        }
        return new Vibration.EndInfo(Vibration.Status.IGNORED_UNSUPPORTED);
    }

    public boolean waitUntilNextStepIsDue() {
        Step peek;
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        processAllNotifySignals();
        if (this.mCancelledImmediately) {
            return false;
        }
        if (this.mPendingOnVibratorCompleteSteps.isEmpty() && (peek = this.mNextSteps.peek()) != null) {
            long calculateWaitTime = peek.calculateWaitTime();
            if (calculateWaitTime <= 0) {
                return true;
            }
            synchronized (this.mLock) {
                if (hasPendingNotifySignalLocked()) {
                    return false;
                }
                try {
                    this.mLock.wait(calculateWaitTime);
                } catch (InterruptedException unused) {
                }
                return false;
            }
        }
        return true;
    }

    public final Step pollNext() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        if (!this.mPendingOnVibratorCompleteSteps.isEmpty()) {
            return this.mPendingOnVibratorCompleteSteps.poll();
        }
        return this.mNextSteps.poll();
    }

    public void runNextStep() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        Step pollNext = pollNext();
        if (pollNext != null) {
            List<Step> play = pollNext.play();
            if (pollNext.getVibratorOnDuration() > 0) {
                this.mSuccessfulVibratorOnSteps++;
            }
            if (pollNext instanceof StartSequentialEffectStep) {
                this.mRemainingStartSequentialEffectSteps--;
            }
            if (!pollNext.isCleanUp()) {
                this.mPendingVibrateSteps--;
            }
            for (int i = 0; i < play.size(); i++) {
                this.mPendingVibrateSteps += !play.get(i).isCleanUp();
            }
            this.mNextSteps.addAll(play);
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        notifyCancelled(new Vibration.EndInfo(Vibration.Status.CANCELLED_BINDER_DIED), false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0041, code lost:
        if (r3.mSignalCancelImmediate == false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void notifyCancelled(Vibration.EndInfo endInfo, boolean z) {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(false);
        }
        if (endInfo == null || !endInfo.status.name().startsWith("CANCEL")) {
            Slog.w("VibrationThread", "Vibration cancel requested with bad signal=" + endInfo + ", using CANCELLED_UNKNOWN_REASON to ensure cancellation.");
            endInfo = new Vibration.EndInfo(Vibration.Status.CANCELLED_BY_UNKNOWN_REASON);
        }
        synchronized (this.mLock) {
            if (z) {
            }
            Vibration.EndInfo endInfo2 = this.mSignalCancel;
            if (endInfo2 == null) {
                this.mSignalCancelImmediate = z | this.mSignalCancelImmediate;
                if (endInfo2 == null) {
                    this.mSignalCancel = endInfo;
                }
                this.mLock.notify();
            }
        }
    }

    public void notifyVibratorComplete(int i) {
        synchronized (this.mLock) {
            this.mSignalVibratorsComplete.add(i);
            this.mLock.notify();
        }
    }

    public void notifySyncedVibrationComplete() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mVibrators.size(); i++) {
                this.mSignalVibratorsComplete.add(this.mVibrators.keyAt(i));
            }
            this.mLock.notify();
        }
    }

    public boolean wasNotifiedToCancel() {
        boolean z;
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(false);
        }
        synchronized (this.mLock) {
            z = this.mSignalCancel != null;
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final boolean hasPendingNotifySignalLocked() {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        if (this.mSignalCancel == null || this.mCancelledVibrationEndInfo != null) {
            return (this.mSignalCancelImmediate && !this.mCancelledImmediately) || this.mSignalVibratorsComplete.size() > 0;
        }
        return true;
    }

    public final void processAllNotifySignals() {
        int[] iArr;
        Vibration.EndInfo endInfo;
        boolean z = true;
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        synchronized (this.mLock) {
            iArr = null;
            if (this.mSignalCancelImmediate) {
                if (this.mCancelledImmediately) {
                    Slog.wtf("VibrationThread", "Immediate cancellation signal processed twice");
                }
                endInfo = this.mSignalCancel;
            } else {
                z = false;
                endInfo = null;
            }
            Vibration.EndInfo endInfo2 = this.mSignalCancel;
            if (endInfo2 != null && this.mCancelledVibrationEndInfo == null) {
                endInfo = endInfo2;
            }
            if (!z && this.mSignalVibratorsComplete.size() > 0) {
                iArr = this.mSignalVibratorsComplete.toArray();
                this.mSignalVibratorsComplete.clear();
            }
        }
        if (z) {
            processCancelImmediately(endInfo);
            return;
        }
        if (endInfo != null) {
            processCancel(endInfo);
        }
        if (iArr != null) {
            processVibratorsComplete(iArr);
        }
    }

    public void processCancel(Vibration.EndInfo endInfo) {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        this.mCancelledVibrationEndInfo = endInfo;
        ArrayList arrayList = new ArrayList();
        while (true) {
            Step pollNext = pollNext();
            if (pollNext != null) {
                arrayList.addAll(pollNext.cancel());
            } else {
                this.mPendingVibrateSteps = 0;
                this.mNextSteps.addAll(arrayList);
                return;
            }
        }
    }

    public void processCancelImmediately(Vibration.EndInfo endInfo) {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        this.mCancelledImmediately = true;
        this.mCancelledVibrationEndInfo = endInfo;
        while (true) {
            Step pollNext = pollNext();
            if (pollNext != null) {
                pollNext.cancelImmediately();
            } else {
                this.mPendingVibrateSteps = 0;
                return;
            }
        }
    }

    public final void processVibratorsComplete(int[] iArr) {
        if (Build.IS_DEBUGGABLE) {
            expectIsVibrationThread(true);
        }
        for (int i : iArr) {
            Iterator<Step> it = this.mNextSteps.iterator();
            while (true) {
                if (it.hasNext()) {
                    Step next = it.next();
                    if (next.acceptVibratorCompleteCallback(i)) {
                        it.remove();
                        this.mPendingOnVibratorCompleteSteps.offer(next);
                        break;
                    }
                }
            }
        }
    }

    public static CombinedVibration.Sequential toSequential(CombinedVibration combinedVibration) {
        if (combinedVibration instanceof CombinedVibration.Sequential) {
            return (CombinedVibration.Sequential) combinedVibration;
        }
        return CombinedVibration.startSequential().addNext(combinedVibration).combine();
    }

    public static void expectIsVibrationThread(boolean z) {
        if ((Thread.currentThread() instanceof VibrationThread) != z) {
            Slog.wtfStack("VibrationStepConductor", "Thread caller assertion failed, expected isVibrationThread=" + z);
        }
    }
}
