package android.hardware.p002tv.cec.V1_0;
/* renamed from: android.hardware.tv.cec.V1_0.CecLogicalAddress */
/* loaded from: classes.dex */
public final class CecLogicalAddress {
    public static final String toString(int i) {
        if (i == 0) {
            return "TV";
        }
        if (i == 1) {
            return "RECORDER_1";
        }
        if (i == 2) {
            return "RECORDER_2";
        }
        if (i == 3) {
            return "TUNER_1";
        }
        if (i == 4) {
            return "PLAYBACK_1";
        }
        if (i == 5) {
            return "AUDIO_SYSTEM";
        }
        if (i == 6) {
            return "TUNER_2";
        }
        if (i == 7) {
            return "TUNER_3";
        }
        if (i == 8) {
            return "PLAYBACK_2";
        }
        if (i == 9) {
            return "RECORDER_3";
        }
        if (i == 10) {
            return "TUNER_4";
        }
        if (i == 11) {
            return "PLAYBACK_3";
        }
        if (i == 14) {
            return "FREE_USE";
        }
        if (i == 15) {
            return "UNREGISTERED";
        }
        if (i == 15) {
            return "BROADCAST";
        }
        return "0x" + Integer.toHexString(i);
    }
}
