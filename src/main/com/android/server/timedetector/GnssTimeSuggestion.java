package com.android.server.timedetector;

import android.app.time.UnixEpochTime;
import android.app.timedetector.TimeSuggestionHelper;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssTimeSuggestion {
    public final TimeSuggestionHelper mTimeSuggestionHelper;

    public GnssTimeSuggestion(UnixEpochTime unixEpochTime) {
        this.mTimeSuggestionHelper = new TimeSuggestionHelper(GnssTimeSuggestion.class, unixEpochTime);
    }

    public GnssTimeSuggestion(TimeSuggestionHelper timeSuggestionHelper) {
        Objects.requireNonNull(timeSuggestionHelper);
        this.mTimeSuggestionHelper = timeSuggestionHelper;
    }

    public UnixEpochTime getUnixEpochTime() {
        return this.mTimeSuggestionHelper.getUnixEpochTime();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || GnssTimeSuggestion.class != obj.getClass()) {
            return false;
        }
        return this.mTimeSuggestionHelper.handleEquals(((GnssTimeSuggestion) obj).mTimeSuggestionHelper);
    }

    public int hashCode() {
        return this.mTimeSuggestionHelper.hashCode();
    }

    public String toString() {
        return this.mTimeSuggestionHelper.handleToString();
    }

    public static GnssTimeSuggestion parseCommandLineArg(ShellCommand shellCommand) throws IllegalArgumentException {
        return new GnssTimeSuggestion(TimeSuggestionHelper.handleParseCommandLineArg(GnssTimeSuggestion.class, shellCommand));
    }

    public static void printCommandLineOpts(PrintWriter printWriter) {
        TimeSuggestionHelper.handlePrintCommandLineOpts(printWriter, "GNSS", GnssTimeSuggestion.class);
    }
}
