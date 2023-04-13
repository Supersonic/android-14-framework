package android.app.timedetector;

import android.app.time.UnixEpochTime;
import android.p008os.Parcel;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TimeSuggestionHelper {
    private ArrayList<String> mDebugInfo;
    private final Class<?> mHelpedClass;
    private final UnixEpochTime mUnixEpochTime;

    public TimeSuggestionHelper(Class<?> helpedClass, UnixEpochTime unixEpochTime) {
        this.mHelpedClass = (Class) Objects.requireNonNull(helpedClass);
        this.mUnixEpochTime = (UnixEpochTime) Objects.requireNonNull(unixEpochTime);
    }

    public UnixEpochTime getUnixEpochTime() {
        return this.mUnixEpochTime;
    }

    public List<String> getDebugInfo() {
        ArrayList<String> arrayList = this.mDebugInfo;
        return arrayList == null ? Collections.emptyList() : Collections.unmodifiableList(arrayList);
    }

    public void addDebugInfo(String debugInfo) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList<>();
        }
        this.mDebugInfo.add(debugInfo);
    }

    public void addDebugInfo(String... debugInfos) {
        addDebugInfo(Arrays.asList(debugInfos));
    }

    public void addDebugInfo(List<String> debugInfo) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList<>(debugInfo.size());
        }
        this.mDebugInfo.addAll(debugInfo);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeSuggestionHelper that = (TimeSuggestionHelper) o;
        return handleEquals(that);
    }

    public boolean handleEquals(TimeSuggestionHelper o) {
        return Objects.equals(this.mHelpedClass, o.mHelpedClass) && Objects.equals(this.mUnixEpochTime, o.mUnixEpochTime);
    }

    public int hashCode() {
        return Objects.hash(this.mUnixEpochTime);
    }

    public String handleToString() {
        return this.mHelpedClass.getSimpleName() + "{mUnixEpochTime=" + this.mUnixEpochTime + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    public static TimeSuggestionHelper handleCreateFromParcel(Class<?> helpedClass, Parcel in) {
        UnixEpochTime unixEpochTime = (UnixEpochTime) in.readParcelable(null, UnixEpochTime.class);
        TimeSuggestionHelper suggestionHelper = new TimeSuggestionHelper(helpedClass, unixEpochTime);
        suggestionHelper.mDebugInfo = in.readArrayList(null, String.class);
        return suggestionHelper;
    }

    public void handleWriteToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mUnixEpochTime, 0);
        dest.writeList(this.mDebugInfo);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TimeSuggestionHelper handleParseCommandLineArg(Class<?> helpedClass, ShellCommand cmd) throws IllegalArgumentException {
        char c;
        Long elapsedRealtimeMillis = null;
        Long unixEpochTimeMillis = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case 16142561:
                        if (opt.equals("--reference_time")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 48316014:
                        if (opt.equals("--elapsed_realtime")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 410278458:
                        if (opt.equals("--unix_epoch_time")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 1:
                        elapsedRealtimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    case 2:
                        unixEpochTimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else if (elapsedRealtimeMillis == null) {
                throw new IllegalArgumentException("No referenceTimeMillis specified.");
            } else {
                if (unixEpochTimeMillis == null) {
                    throw new IllegalArgumentException("No unixEpochTimeMillis specified.");
                }
                UnixEpochTime timeSignal = new UnixEpochTime(elapsedRealtimeMillis.longValue(), unixEpochTimeMillis.longValue());
                TimeSuggestionHelper suggestionHelper = new TimeSuggestionHelper(helpedClass, timeSignal);
                suggestionHelper.addDebugInfo("Command line injection");
                return suggestionHelper;
            }
        }
    }

    public static void handlePrintCommandLineOpts(PrintWriter pw, String typeName, Class<?> clazz) {
        pw.printf("%s suggestion options:\n", typeName);
        pw.println("  --elapsed_realtime <elapsed realtime millis> - the elapsed realtime millis when unix epoch time was read");
        pw.println("  --unix_epoch_time <Unix epoch time millis>");
        pw.println();
        pw.println("See " + clazz.getName() + " for more information");
    }
}
