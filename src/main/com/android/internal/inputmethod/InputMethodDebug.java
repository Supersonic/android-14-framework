package com.android.internal.inputmethod;

import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.NtpTrustedTime;
import android.view.View;
import java.util.StringJoiner;
/* loaded from: classes4.dex */
public final class InputMethodDebug {
    private InputMethodDebug() {
    }

    public static String startInputReasonToString(int reason) {
        switch (reason) {
            case 0:
                return "UNSPECIFIED";
            case 1:
                return "WINDOW_FOCUS_GAIN";
            case 2:
                return "WINDOW_FOCUS_GAIN_REPORT_ONLY";
            case 3:
                return "SCHEDULED_CHECK_FOCUS";
            case 4:
                return "APP_CALLED_RESTART_INPUT_API";
            case 5:
                return "CHECK_FOCUS";
            case 6:
                return "BOUND_TO_IMMS";
            case 7:
                return "UNBOUND_FROM_IMMS";
            case 8:
                return "ACTIVATED_BY_IMMS";
            case 9:
                return "DEACTIVATED_BY_IMMS";
            case 10:
                return "SESSION_CREATED_BY_IME";
            case 11:
            default:
                return "Unknown=" + reason;
            case 12:
                return "BOUND_ACCESSIBILITY_SESSION_TO_IMMS";
        }
    }

    public static String unbindReasonToString(int reason) {
        switch (reason) {
            case 0:
                return "UNSPECIFIED";
            case 1:
                return "SWITCH_CLIENT";
            case 2:
                return "SWITCH_IME";
            case 3:
                return "DISCONNECT_IME";
            case 4:
                return "NO_IME";
            case 5:
                return "SWITCH_IME_FAILED";
            case 6:
                return "SWITCH_USER";
            default:
                return "Unknown=" + reason;
        }
    }

    public static String softInputModeToString(int softInputMode) {
        StringJoiner joiner = new StringJoiner(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
        int state = softInputMode & 15;
        int adjust = softInputMode & 240;
        boolean isForwardNav = (softInputMode & 256) != 0;
        switch (state) {
            case 0:
                joiner.add("STATE_UNSPECIFIED");
                break;
            case 1:
                joiner.add("STATE_UNCHANGED");
                break;
            case 2:
                joiner.add("STATE_HIDDEN");
                break;
            case 3:
                joiner.add("STATE_ALWAYS_HIDDEN");
                break;
            case 4:
                joiner.add("STATE_VISIBLE");
                break;
            case 5:
                joiner.add("STATE_ALWAYS_VISIBLE");
                break;
            default:
                joiner.add("STATE_UNKNOWN(" + state + NavigationBarInflaterView.KEY_CODE_END);
                break;
        }
        switch (adjust) {
            case 0:
                joiner.add("ADJUST_UNSPECIFIED");
                break;
            case 16:
                joiner.add("ADJUST_RESIZE");
                break;
            case 32:
                joiner.add("ADJUST_PAN");
                break;
            case 48:
                joiner.add("ADJUST_NOTHING");
                break;
            default:
                joiner.add("ADJUST_UNKNOWN(" + adjust + NavigationBarInflaterView.KEY_CODE_END);
                break;
        }
        if (isForwardNav) {
            joiner.add("IS_FORWARD_NAVIGATION");
        }
        return joiner.setEmptyValue("(none)").toString();
    }

    public static String startInputFlagsToString(int startInputFlags) {
        StringJoiner joiner = new StringJoiner(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
        if ((startInputFlags & 1) != 0) {
            joiner.add("VIEW_HAS_FOCUS");
        }
        if ((startInputFlags & 2) != 0) {
            joiner.add("IS_TEXT_EDITOR");
        }
        if ((startInputFlags & 4) != 0) {
            joiner.add("INITIAL_CONNECTION");
        }
        return joiner.setEmptyValue("(none)").toString();
    }

    public static String softInputDisplayReasonToString(int reason) {
        switch (reason) {
            case 1:
                return "SHOW_SOFT_INPUT";
            case 2:
                return "ATTACH_NEW_INPUT";
            case 3:
                return "SHOW_SOFT_INPUT_FROM_IME";
            case 4:
                return "HIDE_SOFT_INPUT";
            case 5:
                return "HIDE_SOFT_INPUT_FROM_IME";
            case 6:
                return "SHOW_AUTO_EDITOR_FORWARD_NAV";
            case 7:
                return "SHOW_STATE_VISIBLE_FORWARD_NAV";
            case 8:
                return "SHOW_STATE_ALWAYS_VISIBLE";
            case 9:
                return "SHOW_SETTINGS_ON_CHANGE";
            case 10:
                return "HIDE_SWITCH_USER";
            case 11:
                return "HIDE_INVALID_USER";
            case 12:
                return "HIDE_UNSPECIFIED_WINDOW";
            case 13:
                return "HIDE_STATE_HIDDEN_FORWARD_NAV";
            case 14:
                return "HIDE_ALWAYS_HIDDEN_STATE";
            case 15:
                return "HIDE_RESET_SHELL_COMMAND";
            case 16:
                return "HIDE_SETTINGS_ON_CHANGE";
            case 17:
                return "HIDE_POWER_BUTTON_GO_HOME";
            case 18:
                return "HIDE_DOCKED_STACK_ATTACHED";
            case 19:
                return "HIDE_RECENTS_ANIMATION";
            case 20:
                return "HIDE_BUBBLES";
            case 21:
                return "HIDE_SAME_WINDOW_FOCUSED_WITHOUT_EDITOR";
            case 22:
                return "HIDE_REMOVE_CLIENT";
            case 23:
                return "SHOW_RESTORE_IME_VISIBILITY";
            case 24:
                return "SHOW_TOGGLE_SOFT_INPUT";
            case 25:
                return "HIDE_TOGGLE_SOFT_INPUT";
            case 26:
                return "SHOW_SOFT_INPUT_BY_INSETS_API";
            case 27:
                return "HIDE_DISPLAY_IME_POLICY_HIDE";
            case 28:
                return "HIDE_SOFT_INPUT_BY_INSETS_API";
            case 29:
                return "HIDE_SOFT_INPUT_BY_BACK_KEY";
            case 30:
                return "HIDE_SOFT_INPUT_IME_TOGGLE_SOFT_INPUT";
            case 31:
                return "HIDE_SOFT_INPUT_EXTRACT_INPUT_CHANGED";
            case 32:
                return "HIDE_SOFT_INPUT_IMM_DEPRECATION";
            case 33:
                return "HIDE_WINDOW_GAINED_FOCUS_WITHOUT_EDITOR";
            default:
                return "Unknown=" + reason;
        }
    }

    public static String handwritingGestureTypeFlagsToString(int gestureTypeFlags) {
        StringJoiner joiner = new StringJoiner(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
        if ((gestureTypeFlags & 1) != 0) {
            joiner.add("SELECT");
        }
        if ((gestureTypeFlags & 32) != 0) {
            joiner.add("SELECT_RANGE");
        }
        if ((gestureTypeFlags & 2) != 0) {
            joiner.add("INSERT");
        }
        if ((gestureTypeFlags & 4) != 0) {
            joiner.add(SQLiteDatabase.JOURNAL_MODE_DELETE);
        }
        if ((gestureTypeFlags & 64) != 0) {
            joiner.add("DELETE_RANGE");
        }
        if ((gestureTypeFlags & 8) != 0) {
            joiner.add("REMOVE_SPACE");
        }
        if ((gestureTypeFlags & 16) != 0) {
            joiner.add("JOIN_OR_SPLIT");
        }
        return joiner.setEmptyValue("(none)").toString();
    }

    public static String dumpViewInfo(View view) {
        if (view == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(view);
        sb.append(",focus=" + view.hasFocus());
        sb.append(",windowFocus=" + view.hasWindowFocus());
        sb.append(",window=" + view.getWindowToken());
        sb.append(",displayId=" + view.getContext().getDisplayId());
        sb.append(",temporaryDetach=" + view.isTemporarilyDetached());
        sb.append(",hasImeFocus=" + view.hasImeFocus());
        return sb.toString();
    }
}
