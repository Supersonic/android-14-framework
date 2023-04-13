package android.app.time;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class TimeZoneState implements Parcelable {
    public static final Parcelable.Creator<TimeZoneState> CREATOR = new Parcelable.Creator<TimeZoneState>() { // from class: android.app.time.TimeZoneState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneState createFromParcel(Parcel in) {
            return TimeZoneState.createFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TimeZoneState[] newArray(int size) {
            return new TimeZoneState[size];
        }
    };
    private final String mId;
    private final boolean mUserShouldConfirmId;

    public TimeZoneState(String id, boolean userShouldConfirmId) {
        this.mId = (String) Objects.requireNonNull(id);
        this.mUserShouldConfirmId = userShouldConfirmId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static TimeZoneState createFromParcel(Parcel in) {
        String zoneId = in.readString8();
        boolean userShouldConfirmId = in.readBoolean();
        return new TimeZoneState(zoneId, userShouldConfirmId);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mId);
        dest.writeBoolean(this.mUserShouldConfirmId);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static TimeZoneState parseCommandLineArgs(ShellCommand cmd) {
        char c;
        String zoneIdString = null;
        Boolean userShouldConfirmId = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case -1988134094:
                        if (opt.equals("--user_should_confirm_id")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1274807534:
                        if (opt.equals("--zone_id")) {
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
                        zoneIdString = cmd.getNextArgRequired();
                        break;
                    case 1:
                        userShouldConfirmId = Boolean.valueOf(Boolean.parseBoolean(cmd.getNextArgRequired()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else if (zoneIdString == null) {
                throw new IllegalArgumentException("No zoneId specified.");
            } else {
                if (userShouldConfirmId == null) {
                    throw new IllegalArgumentException("No userShouldConfirmId specified.");
                }
                return new TimeZoneState(zoneIdString, userShouldConfirmId.booleanValue());
            }
        }
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        pw.println("TimeZoneState options:");
        pw.println("  --zone_id {<Olson ID>}");
        pw.println("  --user_should_confirm_id {true|false}");
        pw.println();
        pw.println("See " + TimeZoneState.class.getName() + " for more information");
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return this.mId;
    }

    public boolean getUserShouldConfirmId() {
        return this.mUserShouldConfirmId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeZoneState that = (TimeZoneState) o;
        if (Objects.equals(this.mId, that.mId) && this.mUserShouldConfirmId == that.mUserShouldConfirmId) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId, Boolean.valueOf(this.mUserShouldConfirmId));
    }

    public String toString() {
        return "TimeZoneState{mZoneId=" + this.mId + ", mUserShouldConfirmId=" + this.mUserShouldConfirmId + '}';
    }
}
