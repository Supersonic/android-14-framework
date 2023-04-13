package android.app.timezonedetector;

import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import android.speech.tts.TextToSpeech;
import android.telecom.Logging.Session;
import android.text.TextUtils;
import android.text.format.DateFormat;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class TelephonyTimeZoneSuggestion implements Parcelable {
    public static final Parcelable.Creator<TelephonyTimeZoneSuggestion> CREATOR = new Parcelable.Creator<TelephonyTimeZoneSuggestion>() { // from class: android.app.timezonedetector.TelephonyTimeZoneSuggestion.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyTimeZoneSuggestion createFromParcel(Parcel in) {
            return TelephonyTimeZoneSuggestion.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TelephonyTimeZoneSuggestion[] newArray(int size) {
            return new TelephonyTimeZoneSuggestion[size];
        }
    };
    public static final int MATCH_TYPE_EMULATOR_ZONE_ID = 4;
    public static final int MATCH_TYPE_NA = 0;
    public static final int MATCH_TYPE_NETWORK_COUNTRY_AND_OFFSET = 3;
    public static final int MATCH_TYPE_NETWORK_COUNTRY_ONLY = 2;
    public static final int MATCH_TYPE_TEST_NETWORK_OFFSET_ONLY = 5;
    public static final int QUALITY_MULTIPLE_ZONES_WITH_DIFFERENT_OFFSETS = 3;
    public static final int QUALITY_MULTIPLE_ZONES_WITH_SAME_OFFSET = 2;
    public static final int QUALITY_NA = 0;
    public static final int QUALITY_SINGLE_ZONE = 1;
    private List<String> mDebugInfo;
    private final int mMatchType;
    private final int mQuality;
    private final int mSlotIndex;
    private final String mZoneId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MatchType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Quality {
    }

    public static TelephonyTimeZoneSuggestion createEmptySuggestion(int slotIndex, String debugInfo) {
        return new Builder(slotIndex).addDebugInfo(debugInfo).build();
    }

    private TelephonyTimeZoneSuggestion(Builder builder) {
        this.mSlotIndex = builder.mSlotIndex;
        this.mZoneId = builder.mZoneId;
        this.mMatchType = builder.mMatchType;
        this.mQuality = builder.mQuality;
        this.mDebugInfo = builder.mDebugInfo != null ? new ArrayList(builder.mDebugInfo) : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TelephonyTimeZoneSuggestion createFromParcel(Parcel in) {
        int slotIndex = in.readInt();
        TelephonyTimeZoneSuggestion suggestion = new Builder(slotIndex).setZoneId(in.readString()).setMatchType(in.readInt()).setQuality(in.readInt()).build();
        List<String> debugInfo = in.readArrayList(TelephonyTimeZoneSuggestion.class.getClassLoader(), String.class);
        if (debugInfo != null) {
            suggestion.addDebugInfo(debugInfo);
        }
        return suggestion;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSlotIndex);
        dest.writeString(this.mZoneId);
        dest.writeInt(this.mMatchType);
        dest.writeInt(this.mQuality);
        dest.writeList(this.mDebugInfo);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getSlotIndex() {
        return this.mSlotIndex;
    }

    public String getZoneId() {
        return this.mZoneId;
    }

    public int getMatchType() {
        return this.mMatchType;
    }

    public int getQuality() {
        return this.mQuality;
    }

    public List<String> getDebugInfo() {
        List<String> list = this.mDebugInfo;
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public void addDebugInfo(String debugInfo) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList();
        }
        this.mDebugInfo.add(debugInfo);
    }

    public void addDebugInfo(List<String> debugInfo) {
        if (this.mDebugInfo == null) {
            this.mDebugInfo = new ArrayList(debugInfo.size());
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
        TelephonyTimeZoneSuggestion that = (TelephonyTimeZoneSuggestion) o;
        if (this.mSlotIndex == that.mSlotIndex && this.mMatchType == that.mMatchType && this.mQuality == that.mQuality && Objects.equals(this.mZoneId, that.mZoneId)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSlotIndex), this.mZoneId, Integer.valueOf(this.mMatchType), Integer.valueOf(this.mQuality));
    }

    public String toString() {
        return "TelephonyTimeZoneSuggestion{mSlotIndex=" + this.mSlotIndex + ", mZoneId='" + this.mZoneId + DateFormat.QUOTE + ", mMatchType=" + this.mMatchType + ", mQuality=" + this.mQuality + ", mDebugInfo=" + this.mDebugInfo + '}';
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private List<String> mDebugInfo;
        private int mMatchType;
        private int mQuality;
        private final int mSlotIndex;
        private String mZoneId;

        public Builder(int slotIndex) {
            this.mSlotIndex = slotIndex;
        }

        public Builder setZoneId(String zoneId) {
            this.mZoneId = zoneId;
            return this;
        }

        public Builder setMatchType(int matchType) {
            this.mMatchType = matchType;
            return this;
        }

        public Builder setQuality(int quality) {
            this.mQuality = quality;
            return this;
        }

        public Builder addDebugInfo(String debugInfo) {
            if (this.mDebugInfo == null) {
                this.mDebugInfo = new ArrayList();
            }
            this.mDebugInfo.add(debugInfo);
            return this;
        }

        void validate() {
            int quality = this.mQuality;
            int matchType = this.mMatchType;
            if (this.mZoneId == null) {
                if (quality != 0 || matchType != 0) {
                    throw new RuntimeException("Invalid quality or match type for null zone ID. quality=" + quality + ", matchType=" + matchType);
                }
                return;
            }
            boolean matchTypeValid = false;
            boolean qualityValid = quality == 1 || quality == 2 || quality == 3;
            if (matchType == 2 || matchType == 3 || matchType == 4 || matchType == 5) {
                matchTypeValid = true;
            }
            if (!qualityValid || !matchTypeValid) {
                throw new RuntimeException("Invalid quality or match type with zone ID. quality=" + quality + ", matchType=" + matchType);
            }
        }

        public TelephonyTimeZoneSuggestion build() {
            validate();
            return new TelephonyTimeZoneSuggestion(this);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TelephonyTimeZoneSuggestion parseCommandLineArg(ShellCommand cmd) throws IllegalArgumentException {
        char c;
        Integer slotIndex = null;
        String zoneId = null;
        Integer quality = null;
        Integer matchType = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case -174375148:
                        if (opt.equals("--match_type")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1274807534:
                        if (opt.equals("--zone_id")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 2037196639:
                        if (opt.equals("--quality")) {
                            c = 2;
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
                        zoneId = cmd.getNextArgRequired();
                        break;
                    case 2:
                        quality = Integer.valueOf(parseQualityCommandLineArg(cmd.getNextArgRequired()));
                        break;
                    case 3:
                        matchType = Integer.valueOf(parseMatchTypeCommandLineArg(cmd.getNextArgRequired()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else if (slotIndex == null) {
                throw new IllegalArgumentException("No slotIndex specified.");
            } else {
                Builder builder = new Builder(slotIndex.intValue());
                if (!TextUtils.isEmpty(zoneId) && !Session.SESSION_SEPARATION_CHAR_CHILD.equals(zoneId)) {
                    builder.setZoneId(zoneId);
                }
                if (quality != null) {
                    builder.setQuality(quality.intValue());
                }
                if (matchType != null) {
                    builder.setMatchType(matchType.intValue());
                }
                builder.addDebugInfo("Command line injection");
                return builder.build();
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int parseQualityCommandLineArg(String arg) {
        char c;
        switch (arg.hashCode()) {
            case -902265784:
                if (arg.equals("single")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -650306251:
                if (arg.equals("multiple_same")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1611832522:
                if (arg.equals("multiple_different")) {
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
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            default:
                throw new IllegalArgumentException("Unrecognized quality: " + arg);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int parseMatchTypeCommandLineArg(String arg) {
        char c;
        switch (arg.hashCode()) {
            case -1592694013:
                if (arg.equals("country_with_offset")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 556438401:
                if (arg.equals(Context.TEST_NETWORK_SERVICE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 957831062:
                if (arg.equals(TextToSpeech.Engine.KEY_PARAM_COUNTRY)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1336193813:
                if (arg.equals("emulator")) {
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
                return 4;
            case 1:
                return 3;
            case 2:
                return 2;
            case 3:
                return 5;
            default:
                throw new IllegalArgumentException("Unrecognized match_type: " + arg);
        }
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        pw.println("Telephony suggestion options:");
        pw.println("  --slot_index <number>");
        pw.println("  To withdraw a previous suggestion:");
        pw.println("    [--zone_id \"_\"]");
        pw.println("  To make a new suggestion:");
        pw.println("    --zone_id <Olson ID>");
        pw.println("    --quality <single|multiple_same|multiple_different>");
        pw.println("    --match_type <emulator|country_with_offset|country|test_network>");
        pw.println();
        pw.println("See " + TelephonyTimeZoneSuggestion.class.getName() + " for more information");
    }
}
