package android.app.timedetector;

import android.p008os.Build;
import android.p008os.Environment;
import java.time.Instant;
/* loaded from: classes.dex */
public class TimeDetectorHelper {
    private static final int MANUAL_SUGGESTION_YEAR_MAX_WITHOUT_Y2038_ISSUE = 2100;
    private static final int MANUAL_SUGGESTION_YEAR_MAX_WITH_Y2038_ISSUE = 2037;
    private static final int MANUAL_SUGGESTION_YEAR_MIN = 2015;
    private static final Instant SUGGESTION_UPPER_BOUND_WITH_Y2038_ISSUE = Instant.ofEpochMilli(2147483647000L);
    private static final Instant SUGGESTION_UPPER_BOUND_WIITHOUT_Y2038_ISSUE = Instant.ofEpochMilli(Long.MAX_VALUE);
    private static final Instant MANUAL_SUGGESTION_LOWER_BOUND = Instant.ofEpochMilli(1415491200000L);
    private static final Instant AUTO_SUGGESTION_LOWER_BOUND_DEFAULT = Instant.ofEpochMilli(Long.max(Environment.getRootDirectory().lastModified(), Build.TIME));
    public static final TimeDetectorHelper INSTANCE = new TimeDetectorHelper();

    protected TimeDetectorHelper() {
    }

    public int getManualDateSelectionYearMin() {
        return 2015;
    }

    public int getManualDateSelectionYearMax() {
        if (getDeviceHasY2038Issue()) {
            return 2037;
        }
        return 2100;
    }

    public Instant getManualSuggestionLowerBound() {
        return MANUAL_SUGGESTION_LOWER_BOUND;
    }

    public Instant getAutoSuggestionLowerBoundDefault() {
        return AUTO_SUGGESTION_LOWER_BOUND_DEFAULT;
    }

    public Instant getSuggestionUpperBound() {
        if (getDeviceHasY2038Issue()) {
            return SUGGESTION_UPPER_BOUND_WITH_Y2038_ISSUE;
        }
        return SUGGESTION_UPPER_BOUND_WIITHOUT_Y2038_ISSUE;
    }

    private boolean getDeviceHasY2038Issue() {
        return Build.SUPPORTED_32_BIT_ABIS.length > 0;
    }
}
