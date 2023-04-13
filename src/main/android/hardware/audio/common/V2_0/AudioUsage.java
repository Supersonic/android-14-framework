package android.hardware.audio.common.V2_0;
/* loaded from: classes.dex */
public final class AudioUsage {
    public static final String toString(int i) {
        if (i == 0) {
            return "UNKNOWN";
        }
        if (i == 1) {
            return "MEDIA";
        }
        if (i == 2) {
            return "VOICE_COMMUNICATION";
        }
        if (i == 3) {
            return "VOICE_COMMUNICATION_SIGNALLING";
        }
        if (i == 4) {
            return "ALARM";
        }
        if (i == 5) {
            return "NOTIFICATION";
        }
        if (i == 6) {
            return "NOTIFICATION_TELEPHONY_RINGTONE";
        }
        if (i == 7) {
            return "NOTIFICATION_COMMUNICATION_REQUEST";
        }
        if (i == 8) {
            return "NOTIFICATION_COMMUNICATION_INSTANT";
        }
        if (i == 9) {
            return "NOTIFICATION_COMMUNICATION_DELAYED";
        }
        if (i == 10) {
            return "NOTIFICATION_EVENT";
        }
        if (i == 11) {
            return "ASSISTANCE_ACCESSIBILITY";
        }
        if (i == 12) {
            return "ASSISTANCE_NAVIGATION_GUIDANCE";
        }
        if (i == 13) {
            return "ASSISTANCE_SONIFICATION";
        }
        if (i == 14) {
            return "GAME";
        }
        if (i == 15) {
            return "VIRTUAL_SOURCE";
        }
        if (i == 16) {
            return "ASSISTANT";
        }
        if (i == 17) {
            return "CNT";
        }
        if (i == 16) {
            return "MAX";
        }
        return "0x" + Integer.toHexString(i);
    }
}
