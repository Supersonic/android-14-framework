package com.android.internal.telephony.nitz;

import android.app.timedetector.TelephonyTimeSuggestion;
import android.app.timedetector.TimeDetector;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import android.app.timezonedetector.TimeZoneDetector;
import android.content.Context;
import android.os.SystemClock;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TimeServiceHelperImpl implements TimeServiceHelper {
    private TelephonyTimeZoneSuggestion mLastSuggestedTimeZone;
    private final int mSlotIndex;
    private final TimeDetector mTimeDetector;
    private final TimeZoneDetector mTimeZoneDetector;
    private final LocalLog mTimeZoneLog = new LocalLog(32, false);
    private final LocalLog mTimeLog = new LocalLog(32, false);

    public TimeServiceHelperImpl(Phone phone) {
        this.mSlotIndex = phone.getPhoneId();
        Context context = phone.getContext();
        Objects.requireNonNull(context);
        TimeDetector timeDetector = (TimeDetector) context.getSystemService(TimeDetector.class);
        Objects.requireNonNull(timeDetector);
        this.mTimeDetector = timeDetector;
        TimeZoneDetector timeZoneDetector = (TimeZoneDetector) context.getSystemService(TimeZoneDetector.class);
        Objects.requireNonNull(timeZoneDetector);
        this.mTimeZoneDetector = timeZoneDetector;
    }

    @Override // com.android.internal.telephony.nitz.TimeServiceHelper
    public void suggestDeviceTime(TelephonyTimeSuggestion telephonyTimeSuggestion) {
        LocalLog localLog = this.mTimeLog;
        localLog.log("Sending time suggestion: " + telephonyTimeSuggestion);
        Objects.requireNonNull(telephonyTimeSuggestion);
        if (telephonyTimeSuggestion.getUnixEpochTime() != null) {
            TelephonyMetrics.getInstance().writeNITZEvent(this.mSlotIndex, telephonyTimeSuggestion.getUnixEpochTime().getUnixEpochTimeMillis());
        }
        this.mTimeDetector.suggestTelephonyTime(telephonyTimeSuggestion);
    }

    @Override // com.android.internal.telephony.nitz.TimeServiceHelper
    public void maybeSuggestDeviceTimeZone(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) {
        Objects.requireNonNull(telephonyTimeZoneSuggestion);
        if (shouldSendNewTimeZoneSuggestion(this.mLastSuggestedTimeZone, telephonyTimeZoneSuggestion)) {
            LocalLog localLog = this.mTimeZoneLog;
            localLog.log("Suggesting time zone update: " + telephonyTimeZoneSuggestion);
            this.mTimeZoneDetector.suggestTelephonyTimeZone(telephonyTimeZoneSuggestion);
            this.mLastSuggestedTimeZone = telephonyTimeZoneSuggestion;
        }
    }

    private static boolean shouldSendNewTimeZoneSuggestion(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion, TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion2) {
        if (telephonyTimeZoneSuggestion == null) {
            return true;
        }
        return !Objects.equals(telephonyTimeZoneSuggestion2, telephonyTimeZoneSuggestion);
    }

    @Override // com.android.internal.telephony.nitz.TimeServiceHelper
    public void dumpLogs(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("TimeServiceHelperImpl:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("SystemClock.elapsedRealtime()=" + SystemClock.elapsedRealtime());
        indentingPrintWriter.println("System.currentTimeMillis()=" + System.currentTimeMillis());
        indentingPrintWriter.println("Time Logs:");
        indentingPrintWriter.increaseIndent();
        this.mTimeLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Time zone Logs:");
        indentingPrintWriter.increaseIndent();
        this.mTimeZoneLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }

    @Override // com.android.internal.telephony.nitz.TimeServiceHelper
    public void dumpState(PrintWriter printWriter) {
        printWriter.println(" TimeServiceHelperImpl.mLastSuggestedTimeZone=" + this.mLastSuggestedTimeZone);
    }
}
