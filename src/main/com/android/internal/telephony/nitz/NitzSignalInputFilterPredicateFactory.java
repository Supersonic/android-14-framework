package com.android.internal.telephony.nitz;

import android.content.Context;
import android.os.PowerManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.NitzData;
import com.android.internal.telephony.NitzSignal;
import com.android.internal.telephony.NitzStateMachine;
import com.android.internal.telephony.nitz.NitzStateMachineImpl;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.Objects;
@VisibleForTesting
/* loaded from: classes.dex */
public final class NitzSignalInputFilterPredicateFactory {

    @FunctionalInterface
    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface TrivalentPredicate {
        Boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2);
    }

    private NitzSignalInputFilterPredicateFactory() {
    }

    public static NitzStateMachineImpl.NitzSignalInputFilterPredicate create(Context context, NitzStateMachine.DeviceState deviceState) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(deviceState);
        return new NitzSignalInputFilterPredicateImpl(new TrivalentPredicate[]{createIgnoreNitzPropertyCheck(deviceState), createBogusElapsedRealtimeCheck(context, deviceState), createNoOldSignalCheck(), createRateLimitCheck(deviceState)});
    }

    @VisibleForTesting
    public static TrivalentPredicate createIgnoreNitzPropertyCheck(final NitzStateMachine.DeviceState deviceState) {
        return new TrivalentPredicate() { // from class: com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory.TrivalentPredicate
            public final Boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
                Boolean lambda$createIgnoreNitzPropertyCheck$0;
                lambda$createIgnoreNitzPropertyCheck$0 = NitzSignalInputFilterPredicateFactory.lambda$createIgnoreNitzPropertyCheck$0(NitzStateMachine.DeviceState.this, nitzSignal, nitzSignal2);
                return lambda$createIgnoreNitzPropertyCheck$0;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$createIgnoreNitzPropertyCheck$0(NitzStateMachine.DeviceState deviceState, NitzSignal nitzSignal, NitzSignal nitzSignal2) {
        if (deviceState.getIgnoreNitz()) {
            Rlog.d("NitzStateMachineImpl", "mustProcessNitzSignal: Not processing NITZ signal because gsm.ignore-nitz is set");
            return Boolean.FALSE;
        }
        return null;
    }

    @VisibleForTesting
    public static TrivalentPredicate createBogusElapsedRealtimeCheck(Context context, final NitzStateMachine.DeviceState deviceState) {
        final PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "NitzSignalInputFilterPredicateFactory");
        return new TrivalentPredicate() { // from class: com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory.TrivalentPredicate
            public final Boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
                Boolean lambda$createBogusElapsedRealtimeCheck$1;
                lambda$createBogusElapsedRealtimeCheck$1 = NitzSignalInputFilterPredicateFactory.lambda$createBogusElapsedRealtimeCheck$1(newWakeLock, deviceState, nitzSignal, nitzSignal2);
                return lambda$createBogusElapsedRealtimeCheck$1;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$createBogusElapsedRealtimeCheck$1(PowerManager.WakeLock wakeLock, NitzStateMachine.DeviceState deviceState, NitzSignal nitzSignal, NitzSignal nitzSignal2) {
        Objects.requireNonNull(nitzSignal2);
        try {
            wakeLock.acquire();
            long elapsedRealtimeMillis = deviceState.elapsedRealtimeMillis();
            long receiptElapsedRealtimeMillis = elapsedRealtimeMillis - nitzSignal2.getReceiptElapsedRealtimeMillis();
            if (receiptElapsedRealtimeMillis < 0 || receiptElapsedRealtimeMillis > 2147483647L) {
                Rlog.d("NitzStateMachineImpl", "mustProcessNitzSignal: Not processing NITZ signal because unexpected elapsedRealtime=" + elapsedRealtimeMillis + " nitzSignal=" + nitzSignal2);
                return Boolean.FALSE;
            }
            wakeLock.release();
            return null;
        } finally {
            wakeLock.release();
        }
    }

    @VisibleForTesting
    public static TrivalentPredicate createNoOldSignalCheck() {
        return new TrivalentPredicate() { // from class: com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory.TrivalentPredicate
            public final Boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
                Boolean lambda$createNoOldSignalCheck$2;
                lambda$createNoOldSignalCheck$2 = NitzSignalInputFilterPredicateFactory.lambda$createNoOldSignalCheck$2(nitzSignal, nitzSignal2);
                return lambda$createNoOldSignalCheck$2;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$createNoOldSignalCheck$2(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
        if (nitzSignal == null) {
            return Boolean.TRUE;
        }
        return null;
    }

    @VisibleForTesting
    public static TrivalentPredicate createRateLimitCheck(final NitzStateMachine.DeviceState deviceState) {
        return new TrivalentPredicate() { // from class: com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory.1
            @Override // com.android.internal.telephony.nitz.NitzSignalInputFilterPredicateFactory.TrivalentPredicate
            public Boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
                Objects.requireNonNull(nitzSignal2);
                Objects.requireNonNull(nitzSignal2.getNitzData());
                Objects.requireNonNull(nitzSignal);
                Objects.requireNonNull(nitzSignal.getNitzData());
                NitzData nitzData = nitzSignal2.getNitzData();
                NitzData nitzData2 = nitzSignal.getNitzData();
                if (!offsetInfoIsTheSame(nitzData2, nitzData)) {
                    return Boolean.TRUE;
                }
                int nitzUpdateSpacingMillis = NitzStateMachine.DeviceState.this.getNitzUpdateSpacingMillis();
                if (nitzSignal2.getReceiptElapsedRealtimeMillis() - nitzSignal.getReceiptElapsedRealtimeMillis() > nitzUpdateSpacingMillis) {
                    return Boolean.TRUE;
                }
                int nitzUpdateDiffMillis = NitzStateMachine.DeviceState.this.getNitzUpdateDiffMillis();
                if (Math.abs((nitzData.getCurrentTimeInMillis() - nitzData2.getCurrentTimeInMillis()) - (nitzSignal2.getAgeAdjustedElapsedRealtimeMillis() - nitzSignal.getAgeAdjustedElapsedRealtimeMillis())) > nitzUpdateDiffMillis) {
                    return Boolean.TRUE;
                }
                Rlog.d("NitzStateMachineImpl", "mustProcessNitzSignal: NITZ signal filtered previousSignal=" + nitzSignal + ", newSignal=" + nitzSignal2 + ", nitzUpdateSpacing=" + nitzUpdateSpacingMillis + ", nitzUpdateDiff=" + nitzUpdateDiffMillis);
                return Boolean.FALSE;
            }

            private boolean offsetInfoIsTheSame(NitzData nitzData, NitzData nitzData2) {
                return Objects.equals(nitzData2.getDstAdjustmentMillis(), nitzData.getDstAdjustmentMillis()) && Objects.equals(nitzData2.getEmulatorHostTimeZone(), nitzData.getEmulatorHostTimeZone()) && nitzData2.getLocalOffsetMillis() == nitzData.getLocalOffsetMillis();
            }
        };
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class NitzSignalInputFilterPredicateImpl implements NitzStateMachineImpl.NitzSignalInputFilterPredicate {
        private final TrivalentPredicate[] mComponents;

        @VisibleForTesting
        public NitzSignalInputFilterPredicateImpl(TrivalentPredicate[] trivalentPredicateArr) {
            this.mComponents = (TrivalentPredicate[]) Arrays.copyOf(trivalentPredicateArr, trivalentPredicateArr.length);
        }

        @Override // com.android.internal.telephony.nitz.NitzStateMachineImpl.NitzSignalInputFilterPredicate
        public boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2) {
            Objects.requireNonNull(nitzSignal2);
            for (TrivalentPredicate trivalentPredicate : this.mComponents) {
                Boolean mustProcessNitzSignal = trivalentPredicate.mustProcessNitzSignal(nitzSignal, nitzSignal2);
                if (mustProcessNitzSignal != null) {
                    return mustProcessNitzSignal.booleanValue();
                }
            }
            return true;
        }
    }
}
