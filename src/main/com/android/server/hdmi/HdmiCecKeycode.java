package com.android.server.hdmi;

import com.android.internal.util.FrameworkStatsLog;
import java.util.Arrays;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public final class HdmiCecKeycode {
    public static final KeycodeEntry[] KEYCODE_ENTRIES = {new KeycodeEntry(23, 0), new KeycodeEntry(19, 1), new KeycodeEntry(20, 2), new KeycodeEntry(21, 3), new KeycodeEntry(22, 4), new KeycodeEntry(-1, 5), new KeycodeEntry(-1, 6), new KeycodeEntry(-1, 7), new KeycodeEntry(-1, 8), new KeycodeEntry(3, 9), new KeycodeEntry(82, 9), new KeycodeEntry((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_PICK_RESULT, 10), new KeycodeEntry(256, 11, false), new KeycodeEntry(-1, 12), new KeycodeEntry(4, 13), new KeycodeEntry(111, 13), new KeycodeEntry(226, 16), new KeycodeEntry((int) FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP, 17), new KeycodeEntry(234, 29), new KeycodeEntry((int) FrameworkStatsLog.CAMERA_ACTION_EVENT, 30), new KeycodeEntry((int) FrameworkStatsLog.APP_COMPATIBILITY_CHANGE_REPORTED, 31), new KeycodeEntry(7, 32), new KeycodeEntry(8, 33), new KeycodeEntry(9, 34), new KeycodeEntry(10, 35), new KeycodeEntry(11, 36), new KeycodeEntry(12, 37), new KeycodeEntry(13, 38), new KeycodeEntry(14, 39), new KeycodeEntry(15, 40), new KeycodeEntry(16, 41), new KeycodeEntry(56, 42), new KeycodeEntry((int) FrameworkStatsLog.f418x97ec91aa, 43), new KeycodeEntry(28, 44), new KeycodeEntry(-1, 47), new KeycodeEntry((int) FrameworkStatsLog.f382x8c80549a, 48), new KeycodeEntry((int) FrameworkStatsLog.f381xaad26533, 49), new KeycodeEntry(229, 50), new KeycodeEntry(222, 51), new KeycodeEntry(178, 52), new KeycodeEntry((int) FrameworkStatsLog.f383xde3a78eb, 53), new KeycodeEntry(-1, 54), new KeycodeEntry(92, 55), new KeycodeEntry(93, 56), new KeycodeEntry(26, 64, false), new KeycodeEntry(24, 65), new KeycodeEntry(25, 66), new KeycodeEntry((int) FrameworkStatsLog.f376xd07885aa, 67, false), new KeycodeEntry(126, 68), new KeycodeEntry(86, 69), new KeycodeEntry(127, 70), new KeycodeEntry(85, 70), new KeycodeEntry(130, 71), new KeycodeEntry(89, 72), new KeycodeEntry(90, 73), new KeycodeEntry(129, 74), new KeycodeEntry(87, 75), new KeycodeEntry(88, 76), new KeycodeEntry(-1, 77), new KeycodeEntry(-1, 78), new KeycodeEntry(-1, 79), new KeycodeEntry(-1, 80), new KeycodeEntry((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__DOCSUI_LAUNCH_OTHER_APP, 81), new KeycodeEntry(-1, 82), new KeycodeEntry((int) FrameworkStatsLog.f384xd8079e8d, 83), new KeycodeEntry(258, 84), new KeycodeEntry(-1, 85), new KeycodeEntry(-1, 86), new KeycodeEntry(235, 86, true, intToSingleByteArray(16)), new KeycodeEntry(236, 86, true, intToSingleByteArray(96)), new KeycodeEntry(FrameworkStatsLog.REBOOT_ESCROW_RECOVERY_REPORTED, 86, true, intToSingleByteArray(128)), new KeycodeEntry(FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED, 86, true, intToSingleByteArray(144)), new KeycodeEntry(241, 86, true, intToSingleByteArray(1)), new KeycodeEntry(-1, 87), new KeycodeEntry(-1, 96, false), new KeycodeEntry(-1, 97, false), new KeycodeEntry(-1, 98, false), new KeycodeEntry(-1, 99, false), new KeycodeEntry(-1, 100, false), new KeycodeEntry(-1, 101, false), new KeycodeEntry(-1, 102, false), new KeycodeEntry(-1, 103, false), new KeycodeEntry(-1, 104, false), new KeycodeEntry(-1, 105, false), new KeycodeEntry(-1, 106, false), new KeycodeEntry(-1, 107, false), new KeycodeEntry(-1, 108, false), new KeycodeEntry(-1, 109, false), new KeycodeEntry(186, 113), new KeycodeEntry(183, 114), new KeycodeEntry(184, 115), new KeycodeEntry(185, 116), new KeycodeEntry((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__SET_PERSONAL_APPS_SUSPENDED, 117), new KeycodeEntry((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_PERCEPTIBLE, 118)};

    public static String getKeycodeType(byte b) {
        if (b == 16 || b == 17) {
            return "Menu";
        }
        switch (b) {
            case 0:
                return "Select";
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 13:
                return "Navigation";
            case 9:
            case 10:
            case 11:
            case 12:
                return "Menu";
            default:
                switch (b) {
                    case 29:
                    case 42:
                    case 43:
                    case 44:
                        return "General";
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                        return "Number";
                    default:
                        switch (b) {
                            case 47:
                            case 48:
                            case 49:
                            case 50:
                                return "Channel";
                            case 51:
                            case 52:
                                return "Select";
                            case 53:
                            case 54:
                                return "General";
                            case 55:
                            case 56:
                                return "Navigation";
                            default:
                                switch (b) {
                                    case 64:
                                        return "Power";
                                    case 65:
                                        return "Volume up";
                                    case 66:
                                        return "Volume down";
                                    case 67:
                                        return "Volume mute";
                                    case 68:
                                    case 69:
                                    case 70:
                                    case 71:
                                    case 72:
                                    case 73:
                                    case 74:
                                    case 75:
                                    case 76:
                                    case 77:
                                    case 78:
                                        return "Media";
                                    default:
                                        switch (b) {
                                            case 80:
                                            case 81:
                                            case 82:
                                                return "Media";
                                            case 83:
                                            case 84:
                                                return "Timer";
                                            case 85:
                                                return "General";
                                            case 86:
                                            case 87:
                                                return "Select";
                                            default:
                                                switch (b) {
                                                    case 96:
                                                    case 97:
                                                    case 98:
                                                    case 99:
                                                    case 100:
                                                    case 101:
                                                    case 102:
                                                    case 103:
                                                    case 104:
                                                    case 105:
                                                    case 106:
                                                        return "Functional";
                                                    case 107:
                                                        return "Power toggle";
                                                    case 108:
                                                        return "Power off";
                                                    case 109:
                                                        return "Power on";
                                                    default:
                                                        switch (b) {
                                                            case 113:
                                                            case 114:
                                                            case 115:
                                                            case 116:
                                                            case 117:
                                                                return "Function key";
                                                            case 118:
                                                                return "General";
                                                            default:
                                                                return "Unknown";
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
    }

    public static int getMuteKey(boolean z) {
        return 67;
    }

    public static byte[] intToSingleByteArray(int i) {
        return new byte[]{(byte) (i & 255)};
    }

    /* loaded from: classes.dex */
    public static class KeycodeEntry {
        public final int mAndroidKeycode;
        public final byte[] mCecKeycodeAndParams;
        public final boolean mIsRepeatable;

        public KeycodeEntry(int i, int i2, boolean z, byte[] bArr) {
            this.mAndroidKeycode = i;
            this.mIsRepeatable = z;
            byte[] bArr2 = new byte[bArr.length + 1];
            this.mCecKeycodeAndParams = bArr2;
            System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
            bArr2[0] = (byte) (i2 & 255);
        }

        public KeycodeEntry(int i, int i2, boolean z) {
            this(i, i2, z, EmptyArray.BYTE);
        }

        public KeycodeEntry(int i, int i2) {
            this(i, i2, true, EmptyArray.BYTE);
        }

        public final byte[] toCecKeycodeAndParamIfMatched(int i) {
            if (this.mAndroidKeycode == i) {
                return this.mCecKeycodeAndParams;
            }
            return null;
        }

        public final int toAndroidKeycodeIfMatched(byte[] bArr) {
            if (Arrays.equals(this.mCecKeycodeAndParams, bArr)) {
                return this.mAndroidKeycode;
            }
            return -1;
        }

        public final Boolean isRepeatableIfMatched(int i) {
            if (this.mAndroidKeycode == i) {
                return Boolean.valueOf(this.mIsRepeatable);
            }
            return null;
        }
    }

    public static byte[] androidKeyToCecKey(int i) {
        int i2 = 0;
        while (true) {
            KeycodeEntry[] keycodeEntryArr = KEYCODE_ENTRIES;
            if (i2 >= keycodeEntryArr.length) {
                return null;
            }
            byte[] cecKeycodeAndParamIfMatched = keycodeEntryArr[i2].toCecKeycodeAndParamIfMatched(i);
            if (cecKeycodeAndParamIfMatched != null) {
                return cecKeycodeAndParamIfMatched;
            }
            i2++;
        }
    }

    public static int cecKeycodeAndParamsToAndroidKey(byte[] bArr) {
        int i = 0;
        while (true) {
            KeycodeEntry[] keycodeEntryArr = KEYCODE_ENTRIES;
            if (i >= keycodeEntryArr.length) {
                return -1;
            }
            int androidKeycodeIfMatched = keycodeEntryArr[i].toAndroidKeycodeIfMatched(bArr);
            if (androidKeycodeIfMatched != -1) {
                return androidKeycodeIfMatched;
            }
            i++;
        }
    }

    public static boolean isRepeatableKey(int i) {
        int i2 = 0;
        while (true) {
            KeycodeEntry[] keycodeEntryArr = KEYCODE_ENTRIES;
            if (i2 >= keycodeEntryArr.length) {
                return false;
            }
            Boolean isRepeatableIfMatched = keycodeEntryArr[i2].isRepeatableIfMatched(i);
            if (isRepeatableIfMatched != null) {
                return isRepeatableIfMatched.booleanValue();
            }
            i2++;
        }
    }

    public static boolean isSupportedKeycode(int i) {
        return androidKeyToCecKey(i) != null;
    }

    public static boolean isVolumeKeycode(int i) {
        byte b = androidKeyToCecKey(i)[0];
        if (isSupportedKeycode(i)) {
            return b == 65 || b == 66 || b == 67 || b == 101 || b == 102;
        }
        return false;
    }
}
