package android.app.timedetector;

import android.app.time.UnixEpochTime;
import android.p008os.SystemClock;
/* loaded from: classes.dex */
public interface TimeDetector {
    public static final String SHELL_COMMAND_CLEAR_NETWORK_TIME = "clear_network_time";
    public static final String SHELL_COMMAND_CLEAR_SYSTEM_CLOCK_NETWORK_TIME = "clear_system_clock_network_time";
    public static final String SHELL_COMMAND_CONFIRM_TIME = "confirm_time";
    public static final String SHELL_COMMAND_GET_NETWORK_TIME = "get_network_time";
    public static final String SHELL_COMMAND_GET_TIME_STATE = "get_time_state";
    public static final String SHELL_COMMAND_IS_AUTO_DETECTION_ENABLED = "is_auto_detection_enabled";
    public static final String SHELL_COMMAND_SERVICE_NAME = "time_detector";
    public static final String SHELL_COMMAND_SET_AUTO_DETECTION_ENABLED = "set_auto_detection_enabled";
    public static final String SHELL_COMMAND_SET_SYSTEM_CLOCK_NETWORK_TIME = "set_system_clock_network_time";
    public static final String SHELL_COMMAND_SET_TIME_STATE = "set_time_state_for_tests";
    public static final String SHELL_COMMAND_SUGGEST_EXTERNAL_TIME = "suggest_external_time";
    public static final String SHELL_COMMAND_SUGGEST_GNSS_TIME = "suggest_gnss_time";
    public static final String SHELL_COMMAND_SUGGEST_MANUAL_TIME = "suggest_manual_time";
    public static final String SHELL_COMMAND_SUGGEST_NETWORK_TIME = "suggest_network_time";
    public static final String SHELL_COMMAND_SUGGEST_TELEPHONY_TIME = "suggest_telephony_time";

    boolean suggestManualTime(ManualTimeSuggestion manualTimeSuggestion);

    void suggestTelephonyTime(TelephonyTimeSuggestion telephonyTimeSuggestion);

    static ManualTimeSuggestion createManualTimeSuggestion(long when, String why) {
        UnixEpochTime unixEpochTime = new UnixEpochTime(SystemClock.elapsedRealtime(), when);
        ManualTimeSuggestion manualTimeSuggestion = new ManualTimeSuggestion(unixEpochTime);
        manualTimeSuggestion.addDebugInfo(why);
        return manualTimeSuggestion;
    }
}
