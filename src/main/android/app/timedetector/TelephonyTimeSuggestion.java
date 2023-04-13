package android.app.timedetector;

import android.app.time.UnixEpochTime;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import android.text.format.DateFormat;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TelephonyTimeSuggestion implements Parcelable {
    public static final Parcelable.Creator<TelephonyTimeSuggestion> CREATOR = new Parcelable.Creator<TelephonyTimeSuggestion>() { // from class: android.app.timedetector.TelephonyTimeSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyTimeSuggestion createFromParcel(Parcel in) {
            return TelephonyTimeSuggestion.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyTimeSuggestion[] newArray(int size) {
            return new TelephonyTimeSuggestion[size];
        }
    };
    private ArrayList<String> mDebugInfo;
    private final int mSlotIndex;
    private final UnixEpochTime mUnixEpochTime;

    private TelephonyTimeSuggestion(Builder builder) {
        this.mSlotIndex = builder.mSlotIndex;
        this.mUnixEpochTime = builder.mUnixEpochTime;
        this.mDebugInfo = builder.mDebugInfo != null ? new ArrayList<>(builder.mDebugInfo) : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TelephonyTimeSuggestion createFromParcel(Parcel in) {
        int slotIndex = in.readInt();
        UnixEpochTime unixEpochTime = (UnixEpochTime) in.readParcelable(null, UnixEpochTime.class);
        TelephonyTimeSuggestion suggestion = new Builder(slotIndex).setUnixEpochTime(unixEpochTime).build();
        ArrayList<String> debugInfo = in.readArrayList(null, String.class);
        if (debugInfo != null) {
            suggestion.addDebugInfo(debugInfo);
        }
        return suggestion;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TelephonyTimeSuggestion parseCommandLineArg(ShellCommand cmd) throws IllegalArgumentException {
        char c;
        Integer slotIndex = null;
        Long elapsedRealtimeMillis = null;
        Long unixEpochTimeMillis = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case 16142561:
                        if (opt.equals("--reference_time")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 48316014:
                        if (opt.equals("--elapsed_realtime")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 410278458:
                        if (opt.equals("--unix_epoch_time")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 2069298417:
                        if (opt.equals("--slot_index")) {
                            c = 0;
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
                        slotIndex = Integer.valueOf(Integer.parseInt(cmd.getNextArgRequired()));
                        break;
                    case 1:
                    case 2:
                        elapsedRealtimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    case 3:
                        unixEpochTimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else if (slotIndex == null) {
                throw new IllegalArgumentException("No slotIndex specified.");
            } else {
                if (elapsedRealtimeMillis == null) {
                    throw new IllegalArgumentException("No elapsedRealtimeMillis specified.");
                }
                if (unixEpochTimeMillis == null) {
                    throw new IllegalArgumentException("No unixEpochTimeMillis specified.");
                }
                UnixEpochTime timeSignal = new UnixEpochTime(elapsedRealtimeMillis.longValue(), unixEpochTimeMillis.longValue());
                Builder builder = new Builder(slotIndex.intValue()).setUnixEpochTime(timeSignal).addDebugInfo("Command line injection");
                return builder.build();
            }
        }
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        pw.println("Telephony suggestion options:");
        pw.println("  --slot_index <number>");
        pw.println("  --elapsed_realtime <elapsed realtime millis>");
        pw.println("  --unix_epoch_time <Unix epoch time millis>");
        pw.println();
        pw.println("See " + TelephonyTimeSuggestion.class.getName() + " for more information");
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSlotIndex);
        dest.writeParcelable(this.mUnixEpochTime, 0);
        dest.writeList(this.mDebugInfo);
    }

    public int getSlotIndex() {
        return this.mSlotIndex;
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
        TelephonyTimeSuggestion that = (TelephonyTimeSuggestion) o;
        if (this.mSlotIndex == that.mSlotIndex && Objects.equals(this.mUnixEpochTime, that.mUnixEpochTime)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSlotIndex), this.mUnixEpochTime);
    }

    public String toString() {
        return "TelephonyTimeSuggestion{mSlotIndex='" + this.mSlotIndex + DateFormat.QUOTE + ", mUnixEpochTime=" + this.mUnixEpochTime + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private List<String> mDebugInfo;
        private final int mSlotIndex;
        private UnixEpochTime mUnixEpochTime;

        public Builder(int slotIndex) {
            this.mSlotIndex = slotIndex;
        }

        public Builder setUnixEpochTime(UnixEpochTime unixEpochTime) {
            this.mUnixEpochTime = unixEpochTime;
            return this;
        }

        public Builder addDebugInfo(String debugInfo) {
            if (this.mDebugInfo == null) {
                this.mDebugInfo = new ArrayList();
            }
            this.mDebugInfo.add(debugInfo);
            return this;
        }

        public TelephonyTimeSuggestion build() {
            return new TelephonyTimeSuggestion(this);
        }
    }
}
