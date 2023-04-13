package com.android.server.timedetector;

import android.app.time.UnixEpochTime;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NetworkTimeSuggestion {
    public ArrayList<String> mDebugInfo;
    public final int mUncertaintyMillis;
    public final UnixEpochTime mUnixEpochTime;

    public NetworkTimeSuggestion(UnixEpochTime unixEpochTime, int i) {
        Objects.requireNonNull(unixEpochTime);
        this.mUnixEpochTime = unixEpochTime;
        if (i < 0) {
            throw new IllegalArgumentException("uncertaintyMillis < 0");
        }
        this.mUncertaintyMillis = i;
    }

    public UnixEpochTime getUnixEpochTime() {
        return this.mUnixEpochTime;
    }

    public int getUncertaintyMillis() {
        return this.mUncertaintyMillis;
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
        if (obj instanceof NetworkTimeSuggestion) {
            NetworkTimeSuggestion networkTimeSuggestion = (NetworkTimeSuggestion) obj;
            return this.mUnixEpochTime.equals(networkTimeSuggestion.mUnixEpochTime) && this.mUncertaintyMillis == networkTimeSuggestion.mUncertaintyMillis;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mUnixEpochTime, Integer.valueOf(this.mUncertaintyMillis));
    }

    public String toString() {
        return "NetworkTimeSuggestion{mUnixEpochTime=" + this.mUnixEpochTime + ", mUncertaintyMillis=" + this.mUncertaintyMillis + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    public static NetworkTimeSuggestion parseCommandLineArg(ShellCommand shellCommand) throws IllegalArgumentException {
        Long l = null;
        Long l2 = null;
        Integer num = null;
        while (true) {
            String nextArg = shellCommand.getNextArg();
            if (nextArg == null) {
                if (l != null) {
                    if (l2 != null) {
                        if (num == null) {
                            throw new IllegalArgumentException("No uncertaintyMillis specified.");
                        }
                        NetworkTimeSuggestion networkTimeSuggestion = new NetworkTimeSuggestion(new UnixEpochTime(l.longValue(), l2.longValue()), num.intValue());
                        networkTimeSuggestion.addDebugInfo("Command line injection");
                        return networkTimeSuggestion;
                    }
                    throw new IllegalArgumentException("No unixEpochTimeMillis specified.");
                }
                throw new IllegalArgumentException("No elapsedRealtimeMillis specified.");
            }
            char c = 65535;
            switch (nextArg.hashCode()) {
                case 16142561:
                    if (nextArg.equals("--reference_time")) {
                        c = 0;
                        break;
                    }
                    break;
                case 48316014:
                    if (nextArg.equals("--elapsed_realtime")) {
                        c = 1;
                        break;
                    }
                    break;
                case 410278458:
                    if (nextArg.equals("--unix_epoch_time")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1387445527:
                    if (nextArg.equals("--uncertainty_millis")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                    l = Long.valueOf(Long.parseLong(shellCommand.getNextArgRequired()));
                    break;
                case 2:
                    l2 = Long.valueOf(Long.parseLong(shellCommand.getNextArgRequired()));
                    break;
                case 3:
                    num = Integer.valueOf(Integer.parseInt(shellCommand.getNextArgRequired()));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + nextArg);
            }
        }
    }

    public static void printCommandLineOpts(PrintWriter printWriter) {
        printWriter.printf("%s suggestion options:\n", "Network");
        printWriter.println("  --elapsed_realtime <elapsed realtime millis> - the elapsed realtime millis when unix epoch time was read");
        printWriter.println("  --unix_epoch_time <Unix epoch time millis>");
        printWriter.println("  --uncertainty_millis <Uncertainty millis> - a positive error bound (+/-) estimate for unix epoch time");
        printWriter.println();
        printWriter.println("See " + NetworkTimeSuggestion.class.getName() + " for more information");
    }
}
