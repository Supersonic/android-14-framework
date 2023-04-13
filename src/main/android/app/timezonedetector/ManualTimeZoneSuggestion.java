package android.app.timezonedetector;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ManualTimeZoneSuggestion implements Parcelable {
    public static final Parcelable.Creator<ManualTimeZoneSuggestion> CREATOR = new Parcelable.Creator<ManualTimeZoneSuggestion>() { // from class: android.app.timezonedetector.ManualTimeZoneSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManualTimeZoneSuggestion createFromParcel(Parcel in) {
            return ManualTimeZoneSuggestion.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManualTimeZoneSuggestion[] newArray(int size) {
            return new ManualTimeZoneSuggestion[size];
        }
    };
    private ArrayList<String> mDebugInfo;
    private final String mZoneId;

    public ManualTimeZoneSuggestion(String zoneId) {
        this.mZoneId = (String) Objects.requireNonNull(zoneId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ManualTimeZoneSuggestion createFromParcel(Parcel in) {
        String zoneId = in.readString();
        ManualTimeZoneSuggestion suggestion = new ManualTimeZoneSuggestion(zoneId);
        ArrayList<String> debugInfo = in.readArrayList(null, String.class);
        suggestion.mDebugInfo = debugInfo;
        return suggestion;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mZoneId);
        dest.writeList(this.mDebugInfo);
    }

    public String getZoneId() {
        return this.mZoneId;
    }

    public List<String> getDebugInfo() {
        ArrayList<String> arrayList = this.mDebugInfo;
        return arrayList == null ? Collections.emptyList() : Collections.unmodifiableList(arrayList);
    }

    public void addDebugInfo(String... debugInfos) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList<>();
        }
        this.mDebugInfo.addAll(Arrays.asList(debugInfos));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ManualTimeZoneSuggestion that = (ManualTimeZoneSuggestion) o;
        return Objects.equals(this.mZoneId, that.mZoneId);
    }

    public int hashCode() {
        return Objects.hash(this.mZoneId);
    }

    public String toString() {
        return "ManualTimeZoneSuggestion{mZoneId=" + this.mZoneId + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    public static ManualTimeZoneSuggestion parseCommandLineArg(ShellCommand cmd) {
        char c;
        String zoneId = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case 1274807534:
                        if (opt.equals("--zone_id")) {
                            c = 0;
                            break;
                        }
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        zoneId = cmd.getNextArgRequired();
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else {
                ManualTimeZoneSuggestion suggestion = new ManualTimeZoneSuggestion(zoneId);
                suggestion.addDebugInfo("Command line injection");
                return suggestion;
            }
        }
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        pw.println("Manual suggestion options:");
        pw.println("  --zone_id <Olson ID>");
        pw.println();
        pw.println("See " + ManualTimeZoneSuggestion.class.getName() + " for more information");
    }
}
