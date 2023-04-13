package android.hardware.audio.common.V2_0;
/* loaded from: classes.dex */
public final class AudioChannelMask {
    public static final String toString(int i) {
        if (i == 0) {
            return "REPRESENTATION_POSITION";
        }
        if (i == 2) {
            return "REPRESENTATION_INDEX";
        }
        if (i == 0) {
            return "NONE";
        }
        if (i == -1073741824) {
            return "INVALID";
        }
        if (i == 1) {
            return "OUT_FRONT_LEFT";
        }
        if (i == 2) {
            return "OUT_FRONT_RIGHT";
        }
        if (i == 4) {
            return "OUT_FRONT_CENTER";
        }
        if (i == 8) {
            return "OUT_LOW_FREQUENCY";
        }
        if (i == 16) {
            return "OUT_BACK_LEFT";
        }
        if (i == 32) {
            return "OUT_BACK_RIGHT";
        }
        if (i == 64) {
            return "OUT_FRONT_LEFT_OF_CENTER";
        }
        if (i == 128) {
            return "OUT_FRONT_RIGHT_OF_CENTER";
        }
        if (i == 256) {
            return "OUT_BACK_CENTER";
        }
        if (i == 512) {
            return "OUT_SIDE_LEFT";
        }
        if (i == 1024) {
            return "OUT_SIDE_RIGHT";
        }
        if (i == 2048) {
            return "OUT_TOP_CENTER";
        }
        if (i == 4096) {
            return "OUT_TOP_FRONT_LEFT";
        }
        if (i == 8192) {
            return "OUT_TOP_FRONT_CENTER";
        }
        if (i == 16384) {
            return "OUT_TOP_FRONT_RIGHT";
        }
        if (i == 32768) {
            return "OUT_TOP_BACK_LEFT";
        }
        if (i == 65536) {
            return "OUT_TOP_BACK_CENTER";
        }
        if (i == 131072) {
            return "OUT_TOP_BACK_RIGHT";
        }
        if (i == 1) {
            return "OUT_MONO";
        }
        if (i == 3) {
            return "OUT_STEREO";
        }
        if (i == 11) {
            return "OUT_2POINT1";
        }
        if (i == 51) {
            return "OUT_QUAD";
        }
        if (i == 51) {
            return "OUT_QUAD_BACK";
        }
        if (i == 1539) {
            return "OUT_QUAD_SIDE";
        }
        if (i == 263) {
            return "OUT_SURROUND";
        }
        if (i == 55) {
            return "OUT_PENTA";
        }
        if (i == 63) {
            return "OUT_5POINT1";
        }
        if (i == 63) {
            return "OUT_5POINT1_BACK";
        }
        if (i == 1551) {
            return "OUT_5POINT1_SIDE";
        }
        if (i == 319) {
            return "OUT_6POINT1";
        }
        if (i == 1599) {
            return "OUT_7POINT1";
        }
        if (i == 262143) {
            return "OUT_ALL";
        }
        if (i == 4) {
            return "IN_LEFT";
        }
        if (i == 8) {
            return "IN_RIGHT";
        }
        if (i == 16) {
            return "IN_FRONT";
        }
        if (i == 32) {
            return "IN_BACK";
        }
        if (i == 64) {
            return "IN_LEFT_PROCESSED";
        }
        if (i == 128) {
            return "IN_RIGHT_PROCESSED";
        }
        if (i == 256) {
            return "IN_FRONT_PROCESSED";
        }
        if (i == 512) {
            return "IN_BACK_PROCESSED";
        }
        if (i == 1024) {
            return "IN_PRESSURE";
        }
        if (i == 2048) {
            return "IN_X_AXIS";
        }
        if (i == 4096) {
            return "IN_Y_AXIS";
        }
        if (i == 8192) {
            return "IN_Z_AXIS";
        }
        if (i == 16384) {
            return "IN_VOICE_UPLINK";
        }
        if (i == 32768) {
            return "IN_VOICE_DNLINK";
        }
        if (i == 16) {
            return "IN_MONO";
        }
        if (i == 12) {
            return "IN_STEREO";
        }
        if (i == 48) {
            return "IN_FRONT_BACK";
        }
        if (i == 252) {
            return "IN_6";
        }
        if (i == 16400) {
            return "IN_VOICE_UPLINK_MONO";
        }
        if (i == 32784) {
            return "IN_VOICE_DNLINK_MONO";
        }
        if (i == 49168) {
            return "IN_VOICE_CALL_MONO";
        }
        if (i == 65532) {
            return "IN_ALL";
        }
        if (i == 30) {
            return "COUNT_MAX";
        }
        if (i == Integer.MIN_VALUE) {
            return "INDEX_HDR";
        }
        if (i == -2147483647) {
            return "INDEX_MASK_1";
        }
        if (i == -2147483645) {
            return "INDEX_MASK_2";
        }
        if (i == -2147483641) {
            return "INDEX_MASK_3";
        }
        if (i == -2147483633) {
            return "INDEX_MASK_4";
        }
        if (i == -2147483617) {
            return "INDEX_MASK_5";
        }
        if (i == -2147483585) {
            return "INDEX_MASK_6";
        }
        if (i == -2147483521) {
            return "INDEX_MASK_7";
        }
        if (i == -2147483393) {
            return "INDEX_MASK_8";
        }
        return "0x" + Integer.toHexString(i);
    }
}
