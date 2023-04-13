package android.app.time;

import android.annotation.SystemApi;
import android.app.timedetector.TimeSuggestionHelper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class ExternalTimeSuggestion implements Parcelable {
    public static final Parcelable.Creator<ExternalTimeSuggestion> CREATOR = new Parcelable.Creator<ExternalTimeSuggestion>() { // from class: android.app.time.ExternalTimeSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExternalTimeSuggestion createFromParcel(Parcel in) {
            TimeSuggestionHelper helper = TimeSuggestionHelper.handleCreateFromParcel(ExternalTimeSuggestion.class, in);
            return new ExternalTimeSuggestion(helper);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExternalTimeSuggestion[] newArray(int size) {
            return new ExternalTimeSuggestion[size];
        }
    };
    private final TimeSuggestionHelper mTimeSuggestionHelper;

    public ExternalTimeSuggestion(long elapsedRealtimeMillis, long suggestionMillis) {
        this.mTimeSuggestionHelper = new TimeSuggestionHelper(ExternalTimeSuggestion.class, new UnixEpochTime(elapsedRealtimeMillis, suggestionMillis));
    }

    private ExternalTimeSuggestion(TimeSuggestionHelper helper) {
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
        ExternalTimeSuggestion that = (ExternalTimeSuggestion) o;
        return this.mTimeSuggestionHelper.handleEquals(that.mTimeSuggestionHelper);
    }

    public int hashCode() {
        return this.mTimeSuggestionHelper.hashCode();
    }

    public String toString() {
        return this.mTimeSuggestionHelper.handleToString();
    }

    public static ExternalTimeSuggestion parseCommandLineArg(ShellCommand cmd) throws IllegalArgumentException {
        return new ExternalTimeSuggestion(TimeSuggestionHelper.handleParseCommandLineArg(ExternalTimeSuggestion.class, cmd));
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        TimeSuggestionHelper.handlePrintCommandLineOpts(pw, "External", ExternalTimeSuggestion.class);
    }
}
