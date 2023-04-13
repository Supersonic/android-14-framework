package android.view;

import android.util.EventLog;
/* loaded from: classes4.dex */
public class EventLogTags {
    public static final int IMF_IME_ANIM_CANCEL = 32008;
    public static final int IMF_IME_ANIM_FINISH = 32007;
    public static final int IMF_IME_ANIM_START = 32006;
    public static final int IMF_IME_REMOTE_ANIM_CANCEL = 32011;
    public static final int IMF_IME_REMOTE_ANIM_END = 32010;
    public static final int IMF_IME_REMOTE_ANIM_START = 32009;
    public static final int VIEW_ENQUEUE_INPUT_EVENT = 62002;

    private EventLogTags() {
    }

    public static void writeImfImeAnimStart(String token, int animationType, float alpha, String currentInsets, String shownInsets, String hiddenInsets) {
        EventLog.writeEvent((int) IMF_IME_ANIM_START, token, Integer.valueOf(animationType), Float.valueOf(alpha), currentInsets, shownInsets, hiddenInsets);
    }

    public static void writeImfImeAnimFinish(String token, int animationType, float alpha, int shown, String insets) {
        EventLog.writeEvent((int) IMF_IME_ANIM_FINISH, token, Integer.valueOf(animationType), Float.valueOf(alpha), Integer.valueOf(shown), insets);
    }

    public static void writeImfImeAnimCancel(String token, int animationType, String pendingInsets) {
        EventLog.writeEvent((int) IMF_IME_ANIM_CANCEL, token, Integer.valueOf(animationType), pendingInsets);
    }

    public static void writeImfImeRemoteAnimStart(String token, int displayid, int direction, float alpha, float starty, float endy, String leash, String insets, String surfacePosition, String imeFrame) {
        EventLog.writeEvent((int) IMF_IME_REMOTE_ANIM_START, token, Integer.valueOf(displayid), Integer.valueOf(direction), Float.valueOf(alpha), Float.valueOf(starty), Float.valueOf(endy), leash, insets, surfacePosition, imeFrame);
    }

    public static void writeImfImeRemoteAnimEnd(String token, int displayid, int direction, float endy, String leash, String insets, String surfacePosition, String imeFrame) {
        EventLog.writeEvent((int) IMF_IME_REMOTE_ANIM_END, token, Integer.valueOf(displayid), Integer.valueOf(direction), Float.valueOf(endy), leash, insets, surfacePosition, imeFrame);
    }

    public static void writeImfImeRemoteAnimCancel(String token, int displayid, String insets) {
        EventLog.writeEvent((int) IMF_IME_REMOTE_ANIM_CANCEL, token, Integer.valueOf(displayid), insets);
    }

    public static void writeViewEnqueueInputEvent(String eventtype, String action) {
        EventLog.writeEvent((int) VIEW_ENQUEUE_INPUT_EVENT, eventtype, action);
    }
}
