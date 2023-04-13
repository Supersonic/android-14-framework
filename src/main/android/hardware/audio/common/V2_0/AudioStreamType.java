package android.hardware.audio.common.V2_0;
/* loaded from: classes.dex */
public final class AudioStreamType {
    public static final String toString(int i) {
        if (i == -1) {
            return "DEFAULT";
        }
        if (i == 0) {
            return "MIN";
        }
        if (i == 0) {
            return "VOICE_CALL";
        }
        if (i == 1) {
            return "SYSTEM";
        }
        if (i == 2) {
            return "RING";
        }
        if (i == 3) {
            return "MUSIC";
        }
        if (i == 4) {
            return "ALARM";
        }
        if (i == 5) {
            return "NOTIFICATION";
        }
        if (i == 6) {
            return "BLUETOOTH_SCO";
        }
        if (i == 7) {
            return "ENFORCED_AUDIBLE";
        }
        if (i == 8) {
            return "DTMF";
        }
        if (i == 9) {
            return "TTS";
        }
        if (i == 10) {
            return "ACCESSIBILITY";
        }
        if (i == 11) {
            return "REROUTING";
        }
        if (i == 12) {
            return "PATCH";
        }
        if (i == 11) {
            return "PUBLIC_CNT";
        }
        if (i == 12) {
            return "FOR_POLICY_CNT";
        }
        if (i == 13) {
            return "CNT";
        }
        return "0x" + Integer.toHexString(i);
    }
}
