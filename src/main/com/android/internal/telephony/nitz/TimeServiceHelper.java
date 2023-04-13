package com.android.internal.telephony.nitz;

import android.app.timedetector.TelephonyTimeSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IndentingPrintWriter;
import java.io.PrintWriter;
@VisibleForTesting
/* loaded from: classes.dex */
public interface TimeServiceHelper {
    void dumpLogs(IndentingPrintWriter indentingPrintWriter);

    void dumpState(PrintWriter printWriter);

    void maybeSuggestDeviceTimeZone(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion);

    void suggestDeviceTime(TelephonyTimeSuggestion telephonyTimeSuggestion);
}
