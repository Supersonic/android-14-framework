package android.app.timedetector;

import android.app.time.UnixEpochTime;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ManualTimeSuggestion implements Parcelable {
    public static final Parcelable.Creator<ManualTimeSuggestion> CREATOR = new Parcelable.Creator<ManualTimeSuggestion>() { // from class: android.app.timedetector.ManualTimeSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManualTimeSuggestion createFromParcel(Parcel in) {
            TimeSuggestionHelper helper = TimeSuggestionHelper.handleCreateFromParcel(ManualTimeSuggestion.class, in);
            return new ManualTimeSuggestion(helper);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ManualTimeSuggestion[] newArray(int size) {
            return new ManualTimeSuggestion[size];
        }
    };
    private final TimeSuggestionHelper mTimeSuggestionHelper;

    public ManualTimeSuggestion(UnixEpochTime unixEpochTime) {
        this.mTimeSuggestionHelper = new TimeSuggestionHelper(ManualTimeSuggestion.class, unixEpochTime);
    }

    private ManualTimeSuggestion(TimeSuggestionHelper helper) {
        this.mTimeSuggestionHelper = (TimeSuggestionHelper) Objects.requireNonNull(helper);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mTimeSuggestionHelper.handleWriteToParcel(dest, flags);
    }

    public UnixEpochTime getUnixEpochTime() {
        return this.mTimeSuggestionHelper.getUnixEpochTime();
    }

    public List<String> getDebugInfo() {
        return this.mTimeSuggestionHelper.getDebugInfo();
    }

    public void addDebugInfo(String... debugInfos) {
        this.mTimeSuggestionHelper.addDebugInfo(debugInfos);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ManualTimeSuggestion that = (ManualTimeSuggestion) o;
        return this.mTimeSuggestionHelper.handleEquals(that.mTimeSuggestionHelper);
    }

    public int hashCode() {
        return this.mTimeSuggestionHelper.hashCode();
    }

    public String toString() {
        return this.mTimeSuggestionHelper.handleToString();
    }

    public static ManualTimeSuggestion parseCommandLineArg(ShellCommand cmd) throws IllegalArgumentException {
        return new ManualTimeSuggestion(TimeSuggestionHelper.handleParseCommandLineArg(ManualTimeSuggestion.class, cmd));
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        TimeSuggestionHelper.handlePrintCommandLineOpts(pw, "Manual", ManualTimeSuggestion.class);
    }
}
