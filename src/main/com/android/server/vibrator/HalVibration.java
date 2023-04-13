package com.android.server.vibrator;

import android.os.CombinedVibration;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.util.SparseArray;
import com.android.server.vibrator.Vibration;
import com.android.server.vibrator.VibrationStats;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
/* loaded from: classes2.dex */
public final class HalVibration extends Vibration {
    public final CountDownLatch mCompletionLatch;
    public CombinedVibration mEffect;
    public final SparseArray<VibrationEffect> mFallbacks;
    public CombinedVibration mOriginalEffect;
    public Vibration.Status mStatus;

    public HalVibration(IBinder iBinder, CombinedVibration combinedVibration, Vibration.CallerInfo callerInfo) {
        super(iBinder, callerInfo);
        this.mFallbacks = new SparseArray<>();
        this.mCompletionLatch = new CountDownLatch(1);
        this.mEffect = combinedVibration;
        this.mStatus = Vibration.Status.RUNNING;
    }

    public void end(Vibration.EndInfo endInfo) {
        if (hasEnded()) {
            return;
        }
        this.mStatus = endInfo.status;
        this.stats.reportEnded(endInfo.endedBy);
        this.mCompletionLatch.countDown();
    }

    public void waitForEnd() throws InterruptedException {
        this.mCompletionLatch.await();
    }

    public VibrationEffect getFallback(int i) {
        return this.mFallbacks.get(i);
    }

    public void addFallback(int i, VibrationEffect vibrationEffect) {
        this.mFallbacks.put(i, vibrationEffect);
    }

    public void updateEffects(Function<VibrationEffect, VibrationEffect> function) {
        CombinedVibration transformCombinedEffect = transformCombinedEffect(this.mEffect, function);
        if (!transformCombinedEffect.equals(this.mEffect)) {
            if (this.mOriginalEffect == null) {
                this.mOriginalEffect = this.mEffect;
            }
            this.mEffect = transformCombinedEffect;
        }
        for (int i = 0; i < this.mFallbacks.size(); i++) {
            SparseArray<VibrationEffect> sparseArray = this.mFallbacks;
            sparseArray.setValueAt(i, function.apply(sparseArray.valueAt(i)));
        }
    }

    public static CombinedVibration transformCombinedEffect(CombinedVibration combinedVibration, Function<VibrationEffect, VibrationEffect> function) {
        if (combinedVibration instanceof CombinedVibration.Mono) {
            return CombinedVibration.createParallel(function.apply(((CombinedVibration.Mono) combinedVibration).getEffect()));
        }
        if (combinedVibration instanceof CombinedVibration.Stereo) {
            SparseArray effects = ((CombinedVibration.Stereo) combinedVibration).getEffects();
            CombinedVibration.ParallelCombination startParallel = CombinedVibration.startParallel();
            for (int i = 0; i < effects.size(); i++) {
                startParallel.addVibrator(effects.keyAt(i), function.apply((VibrationEffect) effects.valueAt(i)));
            }
            return startParallel.combine();
        } else if (combinedVibration instanceof CombinedVibration.Sequential) {
            List<CombinedVibration> effects2 = ((CombinedVibration.Sequential) combinedVibration).getEffects();
            CombinedVibration.SequentialCombination startSequential = CombinedVibration.startSequential();
            for (CombinedVibration combinedVibration2 : effects2) {
                startSequential.addNext(transformCombinedEffect(combinedVibration2, function));
            }
            return startSequential.combine();
        } else {
            return combinedVibration;
        }
    }

    public boolean hasEnded() {
        return this.mStatus != Vibration.Status.RUNNING;
    }

    @Override // com.android.server.vibrator.Vibration
    public boolean isRepeating() {
        return this.mEffect.getDuration() == Long.MAX_VALUE;
    }

    public CombinedVibration getEffect() {
        return this.mEffect;
    }

    public Vibration.DebugInfo getDebugInfo() {
        return new Vibration.DebugInfo(this.mStatus, this.stats, this.mEffect, this.mOriginalEffect, 0.0f, this.callerInfo);
    }

    public VibrationStats.StatsInfo getStatsInfo(long j) {
        int i = isRepeating() ? 2 : 1;
        Vibration.CallerInfo callerInfo = this.callerInfo;
        return new VibrationStats.StatsInfo(callerInfo.uid, i, callerInfo.attrs.getUsage(), this.mStatus, this.stats, j);
    }

    public boolean canPipelineWith(HalVibration halVibration) {
        Vibration.CallerInfo callerInfo = this.callerInfo;
        return callerInfo.uid == halVibration.callerInfo.uid && callerInfo.attrs.isFlagSet(8) && halVibration.callerInfo.attrs.isFlagSet(8) && !isRepeating();
    }
}
