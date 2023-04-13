package android.text.format;
/* loaded from: classes3.dex */
public class TimeMigrationUtils {
    private TimeMigrationUtils() {
    }

    public static String formatMillisWithFixedFormat(long timeMillis) {
        return new TimeFormatter().formatMillisWithFixedFormat(timeMillis);
    }
}
