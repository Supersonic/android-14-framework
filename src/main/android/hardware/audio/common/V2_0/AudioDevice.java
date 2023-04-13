package android.hardware.audio.common.V2_0;
/* loaded from: classes.dex */
public final class AudioDevice {
    public static final String toString(int i) {
        if (i == 0) {
            return "NONE";
        }
        if (i == Integer.MIN_VALUE) {
            return "BIT_IN";
        }
        if (i == 1073741824) {
            return "BIT_DEFAULT";
        }
        if (i == 1) {
            return "OUT_EARPIECE";
        }
        if (i == 2) {
            return "OUT_SPEAKER";
        }
        if (i == 4) {
            return "OUT_WIRED_HEADSET";
        }
        if (i == 8) {
            return "OUT_WIRED_HEADPHONE";
        }
        if (i == 16) {
            return "OUT_BLUETOOTH_SCO";
        }
        if (i == 32) {
            return "OUT_BLUETOOTH_SCO_HEADSET";
        }
        if (i == 64) {
            return "OUT_BLUETOOTH_SCO_CARKIT";
        }
        if (i == 128) {
            return "OUT_BLUETOOTH_A2DP";
        }
        if (i == 256) {
            return "OUT_BLUETOOTH_A2DP_HEADPHONES";
        }
        if (i == 512) {
            return "OUT_BLUETOOTH_A2DP_SPEAKER";
        }
        if (i == 1024) {
            return "OUT_AUX_DIGITAL";
        }
        if (i == 1024) {
            return "OUT_HDMI";
        }
        if (i == 2048) {
            return "OUT_ANLG_DOCK_HEADSET";
        }
        if (i == 4096) {
            return "OUT_DGTL_DOCK_HEADSET";
        }
        if (i == 8192) {
            return "OUT_USB_ACCESSORY";
        }
        if (i == 16384) {
            return "OUT_USB_DEVICE";
        }
        if (i == 32768) {
            return "OUT_REMOTE_SUBMIX";
        }
        if (i == 65536) {
            return "OUT_TELEPHONY_TX";
        }
        if (i == 131072) {
            return "OUT_LINE";
        }
        if (i == 262144) {
            return "OUT_HDMI_ARC";
        }
        if (i == 524288) {
            return "OUT_SPDIF";
        }
        if (i == 1048576) {
            return "OUT_FM";
        }
        if (i == 2097152) {
            return "OUT_AUX_LINE";
        }
        if (i == 4194304) {
            return "OUT_SPEAKER_SAFE";
        }
        if (i == 8388608) {
            return "OUT_IP";
        }
        if (i == 16777216) {
            return "OUT_BUS";
        }
        if (i == 33554432) {
            return "OUT_PROXY";
        }
        if (i == 67108864) {
            return "OUT_USB_HEADSET";
        }
        if (i == 1073741824) {
            return "OUT_DEFAULT";
        }
        if (i == 1207959551) {
            return "OUT_ALL";
        }
        if (i == 896) {
            return "OUT_ALL_A2DP";
        }
        if (i == 112) {
            return "OUT_ALL_SCO";
        }
        if (i == 67133440) {
            return "OUT_ALL_USB";
        }
        if (i == -2147483647) {
            return "IN_COMMUNICATION";
        }
        if (i == -2147483646) {
            return "IN_AMBIENT";
        }
        if (i == -2147483644) {
            return "IN_BUILTIN_MIC";
        }
        if (i == -2147483640) {
            return "IN_BLUETOOTH_SCO_HEADSET";
        }
        if (i == -2147483632) {
            return "IN_WIRED_HEADSET";
        }
        if (i == -2147483616) {
            return "IN_AUX_DIGITAL";
        }
        if (i == -2147483616) {
            return "IN_HDMI";
        }
        if (i == -2147483584) {
            return "IN_VOICE_CALL";
        }
        if (i == -2147483584) {
            return "IN_TELEPHONY_RX";
        }
        if (i == -2147483520) {
            return "IN_BACK_MIC";
        }
        if (i == -2147483392) {
            return "IN_REMOTE_SUBMIX";
        }
        if (i == -2147483136) {
            return "IN_ANLG_DOCK_HEADSET";
        }
        if (i == -2147482624) {
            return "IN_DGTL_DOCK_HEADSET";
        }
        if (i == -2147481600) {
            return "IN_USB_ACCESSORY";
        }
        if (i == -2147479552) {
            return "IN_USB_DEVICE";
        }
        if (i == -2147475456) {
            return "IN_FM_TUNER";
        }
        if (i == -2147467264) {
            return "IN_TV_TUNER";
        }
        if (i == -2147450880) {
            return "IN_LINE";
        }
        if (i == -2147418112) {
            return "IN_SPDIF";
        }
        if (i == -2147352576) {
            return "IN_BLUETOOTH_A2DP";
        }
        if (i == -2147221504) {
            return "IN_LOOPBACK";
        }
        if (i == -2146959360) {
            return "IN_IP";
        }
        if (i == -2146435072) {
            return "IN_BUS";
        }
        if (i == -2130706432) {
            return "IN_PROXY";
        }
        if (i == -2113929216) {
            return "IN_USB_HEADSET";
        }
        if (i == -1073741824) {
            return "IN_DEFAULT";
        }
        if (i == -1021313025) {
            return "IN_ALL";
        }
        if (i == -2147483640) {
            return "IN_ALL_SCO";
        }
        if (i == -2113923072) {
            return "IN_ALL_USB";
        }
        return "0x" + Integer.toHexString(i);
    }
}
