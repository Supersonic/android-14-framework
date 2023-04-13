package com.android.internal.telephony.nitz;

import android.app.timedetector.TelephonyTimeSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import android.os.TimestampedValue;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.NitzData;
import com.android.internal.telephony.NitzSignal;
import com.android.internal.telephony.NitzStateMachine;
import com.android.internal.telephony.Phone;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public final class NitzStateMachineImpl implements NitzStateMachine {
    private String mCountryIsoCode;
    private final NitzStateMachine.DeviceState mDeviceState;
    private TimestampedValue<NitzSignal> mLastNitzSignalCleared;
    private NitzSignal mLatestNitzSignal;
    private final NitzSignalInputFilterPredicate mNitzSignalInputFilter;
    private final int mSlotIndex;
    private final TimeServiceHelper mTimeServiceHelper;
    private final TimeZoneSuggester mTimeZoneSuggester;

    @FunctionalInterface
    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface NitzSignalInputFilterPredicate {
        boolean mustProcessNitzSignal(NitzSignal nitzSignal, NitzSignal nitzSignal2);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface TimeZoneSuggester {
        TelephonyTimeZoneSuggestion getTimeZoneSuggestion(int i, String str, NitzSignal nitzSignal);
    }

    public static NitzStateMachineImpl createInstance(Phone phone) {
        Objects.requireNonNull(phone);
        int phoneId = phone.getPhoneId();
        NitzStateMachine.DeviceStateImpl deviceStateImpl = new NitzStateMachine.DeviceStateImpl(phone);
        return new NitzStateMachineImpl(phoneId, deviceStateImpl, NitzSignalInputFilterPredicateFactory.create(phone.getContext(), deviceStateImpl), new TimeZoneSuggesterImpl(deviceStateImpl, new TimeZoneLookupHelper()), new TimeServiceHelperImpl(phone));
    }

    @VisibleForTesting
    public NitzStateMachineImpl(int i, NitzStateMachine.DeviceState deviceState, NitzSignalInputFilterPredicate nitzSignalInputFilterPredicate, TimeZoneSuggester timeZoneSuggester, TimeServiceHelper timeServiceHelper) {
        this.mSlotIndex = i;
        Objects.requireNonNull(deviceState);
        this.mDeviceState = deviceState;
        Objects.requireNonNull(timeZoneSuggester);
        this.mTimeZoneSuggester = timeZoneSuggester;
        Objects.requireNonNull(timeServiceHelper);
        this.mTimeServiceHelper = timeServiceHelper;
        Objects.requireNonNull(nitzSignalInputFilterPredicate);
        this.mNitzSignalInputFilter = nitzSignalInputFilterPredicate;
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleNetworkAvailable() {
        restoreNetworkStateAndRerunDetection("handleNetworkAvailable");
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleNetworkUnavailable() {
        if (clearNetworkState(false)) {
            runDetection("handleNetworkUnavailable");
        }
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleCountryDetected(String str) {
        Rlog.d("NitzStateMachineImpl", "handleCountryDetected: countryIsoCode=" + str + ", mLatestNitzSignal=" + this.mLatestNitzSignal);
        String str2 = this.mCountryIsoCode;
        Objects.requireNonNull(str);
        this.mCountryIsoCode = str;
        if (Objects.equals(str2, str)) {
            return;
        }
        NitzSignal nitzSignal = this.mLatestNitzSignal;
        doTimeZoneDetection(str, nitzSignal, "handleCountryDetected(\"" + str + "\")");
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleCountryUnavailable() {
        Rlog.d("NitzStateMachineImpl", "handleCountryUnavailable: mLatestNitzSignal=" + this.mLatestNitzSignal);
        this.mCountryIsoCode = null;
        doTimeZoneDetection(null, this.mLatestNitzSignal, "handleCountryUnavailable");
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleNitzReceived(NitzSignal nitzSignal) {
        Objects.requireNonNull(nitzSignal);
        NitzSignal nitzSignal2 = this.mLatestNitzSignal;
        if (!this.mNitzSignalInputFilter.mustProcessNitzSignal(nitzSignal2, nitzSignal)) {
            Rlog.d("NitzStateMachineImpl", "handleNitzReceived: previousNitzSignal=" + nitzSignal2 + ", nitzSignal=" + nitzSignal + ": NITZ filtered");
            return;
        }
        this.mLatestNitzSignal = nitzSignal;
        this.mLastNitzSignalCleared = null;
        runDetection("handleNitzReceived(" + nitzSignal + ")");
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void handleAirplaneModeChanged(boolean z) {
        boolean z2 = this.mCountryIsoCode != null;
        this.mCountryIsoCode = null;
        boolean clearNetworkState = clearNetworkState(true);
        if (z2 || clearNetworkState) {
            runDetection("handleAirplaneModeChanged(" + z + ")");
        }
    }

    private void restoreNetworkStateAndRerunDetection(String str) {
        if (this.mLastNitzSignalCleared == null) {
            Rlog.d("NitzStateMachineImpl", str + ": mLastNitzSignalCleared is null.");
            return;
        }
        if (this.mDeviceState.elapsedRealtimeMillis() - this.mLastNitzSignalCleared.getReferenceTimeMillis() < ((long) this.mDeviceState.getNitzNetworkDisconnectRetentionMillis())) {
            this.mLatestNitzSignal = (NitzSignal) this.mLastNitzSignalCleared.getValue();
            this.mLastNitzSignalCleared = null;
            runDetection(str + ", mLatestNitzSignal restored from mLastNitzSignalCleared=" + this.mLastNitzSignalCleared.getValue());
            return;
        }
        Rlog.d("NitzStateMachineImpl", str + ": mLastNitzSignalCleared is too old.");
    }

    private boolean clearNetworkState(boolean z) {
        if (z) {
            this.mLastNitzSignalCleared = null;
        } else {
            this.mLastNitzSignalCleared = new TimestampedValue<>(this.mDeviceState.elapsedRealtimeMillis(), this.mLatestNitzSignal);
        }
        boolean z2 = this.mLatestNitzSignal != null;
        this.mLatestNitzSignal = null;
        return z2;
    }

    private void runDetection(String str) {
        String str2 = this.mCountryIsoCode;
        NitzSignal nitzSignal = this.mLatestNitzSignal;
        Rlog.d("NitzStateMachineImpl", "runDetection: reason=" + str + ", countryIsoCode=" + str2 + ", nitzSignal=" + nitzSignal);
        doTimeZoneDetection(str2, nitzSignal, str);
        doTimeDetection(nitzSignal, str);
    }

    private void doTimeZoneDetection(String str, NitzSignal nitzSignal, String str2) {
        try {
            Objects.requireNonNull(str2);
            TelephonyTimeZoneSuggestion timeZoneSuggestion = this.mTimeZoneSuggester.getTimeZoneSuggestion(this.mSlotIndex, str, nitzSignal);
            timeZoneSuggestion.addDebugInfo("Detection reason=" + str2);
            Rlog.d("NitzStateMachineImpl", "doTimeZoneDetection: countryIsoCode=" + str + ", nitzSignal=" + nitzSignal + ", suggestion=" + timeZoneSuggestion + ", reason=" + str2);
            this.mTimeServiceHelper.maybeSuggestDeviceTimeZone(timeZoneSuggestion);
        } catch (RuntimeException e) {
            Rlog.e("NitzStateMachineImpl", "doTimeZoneDetection: Exception thrown mSlotIndex=" + this.mSlotIndex + ", countryIsoCode=" + str + ", nitzSignal=" + nitzSignal + ", reason=" + str2 + ", ex=" + e, e);
        }
    }

    private void doTimeDetection(NitzSignal nitzSignal, String str) {
        try {
            Objects.requireNonNull(str);
            TelephonyTimeSuggestion.Builder builder = new TelephonyTimeSuggestion.Builder(this.mSlotIndex);
            if (nitzSignal == null) {
                builder.addDebugInfo("Clearing time suggestion reason=" + str);
            } else {
                builder.setUnixEpochTime(nitzSignal.createTimeSignal());
                builder.addDebugInfo("Sending new time suggestion nitzSignal=" + nitzSignal + ", reason=" + str);
            }
            this.mTimeServiceHelper.suggestDeviceTime(builder.build());
        } catch (RuntimeException e) {
            Rlog.e("NitzStateMachineImpl", "doTimeDetection: Exception thrown mSlotIndex=" + this.mSlotIndex + ", nitzSignal=" + nitzSignal + ", reason=" + str + ", ex=" + e, e);
        }
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void dumpState(PrintWriter printWriter) {
        printWriter.println(" NitzStateMachineImpl.mLatestNitzSignal=" + this.mLatestNitzSignal);
        printWriter.println(" NitzStateMachineImpl.mCountryIsoCode=" + this.mCountryIsoCode);
        this.mTimeServiceHelper.dumpState(printWriter);
        printWriter.flush();
    }

    @Override // com.android.internal.telephony.NitzStateMachine
    public void dumpLogs(FileDescriptor fileDescriptor, IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        this.mTimeServiceHelper.dumpLogs(indentingPrintWriter);
    }

    @VisibleForTesting
    public NitzData getLatestNitzData() {
        NitzSignal nitzSignal = this.mLatestNitzSignal;
        if (nitzSignal != null) {
            return nitzSignal.getNitzData();
        }
        return null;
    }

    @VisibleForTesting
    public NitzData getLastNitzDataCleared() {
        TimestampedValue<NitzSignal> timestampedValue = this.mLastNitzSignalCleared;
        if (timestampedValue != null) {
            return ((NitzSignal) timestampedValue.getValue()).getNitzData();
        }
        return null;
    }
}
