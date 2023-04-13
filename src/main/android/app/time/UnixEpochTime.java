package android.app.time;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.ShellCommand;
import java.io.PrintWriter;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class UnixEpochTime implements Parcelable {
    public static final Parcelable.Creator<UnixEpochTime> CREATOR = new Parcelable.Creator<UnixEpochTime>() { // from class: android.app.time.UnixEpochTime.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UnixEpochTime createFromParcel(Parcel source) {
            long elapsedRealtimeMillis = source.readLong();
            long unixEpochTimeMillis = source.readLong();
            return new UnixEpochTime(elapsedRealtimeMillis, unixEpochTimeMillis);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UnixEpochTime[] newArray(int size) {
            return new UnixEpochTime[size];
        }
    };
    private final long mElapsedRealtimeMillis;
    private final long mUnixEpochTimeMillis;

    public UnixEpochTime(long elapsedRealtimeMillis, long unixEpochTimeMillis) {
        this.mElapsedRealtimeMillis = elapsedRealtimeMillis;
        this.mUnixEpochTimeMillis = unixEpochTimeMillis;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static UnixEpochTime parseCommandLineArgs(ShellCommand cmd) {
        char c;
        Long elapsedRealtimeMillis = null;
        Long unixEpochTimeMillis = null;
        while (true) {
            String opt = cmd.getNextArg();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case 48316014:
                        if (opt.equals("--elapsed_realtime")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 410278458:
                        if (opt.equals("--unix_epoch_time")) {
                            c = 1;
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
                        elapsedRealtimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    case 1:
                        unixEpochTimeMillis = Long.valueOf(Long.parseLong(cmd.getNextArgRequired()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown option: " + opt);
                }
            } else if (elapsedRealtimeMillis == null) {
                throw new IllegalArgumentException("No elapsedRealtimeMillis specified.");
            } else {
                if (unixEpochTimeMillis == null) {
                    throw new IllegalArgumentException("No unixEpochTimeMillis specified.");
                }
                return new UnixEpochTime(elapsedRealtimeMillis.longValue(), unixEpochTimeMillis.longValue());
            }
        }
    }

    public static void printCommandLineOpts(PrintWriter pw) {
        pw.println("UnixEpochTime options:\n");
        pw.println("  --elapsed_realtime <elapsed realtime millis>");
        pw.println("  --unix_epoch_time <Unix epoch time millis>");
        pw.println();
        pw.println("See " + UnixEpochTime.class.getName() + " for more information");
    }

    public long getElapsedRealtimeMillis() {
        return this.mElapsedRealtimeMillis;
    }

    public long getUnixEpochTimeMillis() {
        return this.mUnixEpochTimeMillis;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnixEpochTime that = (UnixEpochTime) o;
        if (this.mElapsedRealtimeMillis == that.mElapsedRealtimeMillis && this.mUnixEpochTimeMillis == that.mUnixEpochTimeMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mElapsedRealtimeMillis), Long.valueOf(this.mUnixEpochTimeMillis));
    }

    public String toString() {
        return "UnixEpochTime{mElapsedRealtimeMillis=" + this.mElapsedRealtimeMillis + ", mUnixEpochTimeMillis=" + this.mUnixEpochTimeMillis + '}';
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mElapsedRealtimeMillis);
        dest.writeLong(this.mUnixEpochTimeMillis);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: at */
    public UnixEpochTime m193at(long elapsedRealtimeTimeMillis) {
        long adjustedUnixEpochTimeMillis = (elapsedRealtimeTimeMillis - this.mElapsedRealtimeMillis) + this.mUnixEpochTimeMillis;
        return new UnixEpochTime(elapsedRealtimeTimeMillis, adjustedUnixEpochTimeMillis);
    }

    public static long elapsedRealtimeDifference(UnixEpochTime one, UnixEpochTime two) {
        return one.mElapsedRealtimeMillis - two.mElapsedRealtimeMillis;
    }
}
