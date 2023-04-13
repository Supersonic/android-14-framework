package com.android.server.timezonedetector;

import android.app.time.LocationTimeZoneAlgorithmStatus;
import android.os.ShellCommand;
import android.os.SystemClock;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
/* loaded from: classes2.dex */
public final class LocationAlgorithmEvent {
    public final LocationTimeZoneAlgorithmStatus mAlgorithmStatus;
    public ArrayList<String> mDebugInfo;
    public final GeolocationTimeZoneSuggestion mSuggestion;

    public LocationAlgorithmEvent(LocationTimeZoneAlgorithmStatus locationTimeZoneAlgorithmStatus, GeolocationTimeZoneSuggestion geolocationTimeZoneSuggestion) {
        Objects.requireNonNull(locationTimeZoneAlgorithmStatus);
        this.mAlgorithmStatus = locationTimeZoneAlgorithmStatus;
        this.mSuggestion = geolocationTimeZoneSuggestion;
    }

    public LocationTimeZoneAlgorithmStatus getAlgorithmStatus() {
        return this.mAlgorithmStatus;
    }

    public GeolocationTimeZoneSuggestion getSuggestion() {
        return this.mSuggestion;
    }

    public List<String> getDebugInfo() {
        ArrayList<String> arrayList = this.mDebugInfo;
        return arrayList == null ? Collections.emptyList() : Collections.unmodifiableList(arrayList);
    }

    public void addDebugInfo(String... strArr) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList<>();
        }
        this.mDebugInfo.addAll(Arrays.asList(strArr));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || LocationAlgorithmEvent.class != obj.getClass()) {
            return false;
        }
        LocationAlgorithmEvent locationAlgorithmEvent = (LocationAlgorithmEvent) obj;
        return this.mAlgorithmStatus.equals(locationAlgorithmEvent.mAlgorithmStatus) && Objects.equals(this.mSuggestion, locationAlgorithmEvent.mSuggestion);
    }

    public int hashCode() {
        return Objects.hash(this.mAlgorithmStatus, this.mSuggestion);
    }

    public String toString() {
        return "LocationAlgorithmEvent{mAlgorithmStatus=" + this.mAlgorithmStatus + ", mSuggestion=" + this.mSuggestion + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    public static LocationAlgorithmEvent parseCommandLineArg(ShellCommand shellCommand) {
        GeolocationTimeZoneSuggestion geolocationTimeZoneSuggestion = null;
        LocationTimeZoneAlgorithmStatus locationTimeZoneAlgorithmStatus = null;
        String str = null;
        while (true) {
            String nextArg = shellCommand.getNextArg();
            if (nextArg == null) {
                if (locationTimeZoneAlgorithmStatus == null) {
                    throw new IllegalArgumentException("Missing --status");
                }
                if (str != null) {
                    List<String> parseZoneIds = parseZoneIds(str);
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    if (parseZoneIds == null) {
                        geolocationTimeZoneSuggestion = GeolocationTimeZoneSuggestion.createUncertainSuggestion(elapsedRealtime);
                    } else {
                        geolocationTimeZoneSuggestion = GeolocationTimeZoneSuggestion.createCertainSuggestion(elapsedRealtime, parseZoneIds);
                    }
                }
                LocationAlgorithmEvent locationAlgorithmEvent = new LocationAlgorithmEvent(locationTimeZoneAlgorithmStatus, geolocationTimeZoneSuggestion);
                locationAlgorithmEvent.addDebugInfo("Command line injection");
                return locationAlgorithmEvent;
            } else if (nextArg.equals("--suggestion")) {
                str = shellCommand.getNextArgRequired();
            } else if (nextArg.equals("--status")) {
                locationTimeZoneAlgorithmStatus = LocationTimeZoneAlgorithmStatus.parseCommandlineArg(shellCommand.getNextArgRequired());
            } else {
                throw new IllegalArgumentException("Unknown option: " + nextArg);
            }
        }
    }

    public static List<String> parseZoneIds(String str) {
        if ("UNCERTAIN".equals(str)) {
            return null;
        }
        if ("EMPTY".equals(str)) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken());
        }
        return arrayList;
    }

    public static void printCommandLineOpts(PrintWriter printWriter) {
        printWriter.println("Location algorithm event options:");
        printWriter.println("  --status {LocationTimeZoneAlgorithmStatus toString() format}");
        printWriter.println("  [--suggestion {UNCERTAIN|EMPTY|<Olson ID>+}]");
        printWriter.println();
        printWriter.println("See " + LocationAlgorithmEvent.class.getName() + " for more information");
    }
}
