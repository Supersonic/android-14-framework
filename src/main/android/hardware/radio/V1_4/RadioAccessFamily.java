package android.hardware.radio.V1_4;

import com.android.internal.telephony.DctConstants;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class RadioAccessFamily {
    public static final int EDGE = 4;
    public static final int EHRPD = 8192;
    public static final int EVDO_0 = 128;
    public static final int EVDO_A = 256;
    public static final int EVDO_B = 4096;
    public static final int GPRS = 2;
    public static final int GSM = 65536;
    public static final int HSDPA = 512;
    public static final int HSPA = 2048;
    public static final int HSPAP = 32768;
    public static final int HSUPA = 1024;
    public static final int IS95A = 16;
    public static final int IS95B = 32;
    public static final int LTE = 16384;
    public static final int LTE_CA = 524288;

    /* renamed from: NR */
    public static final int f178NR = 1048576;
    public static final int ONE_X_RTT = 64;
    public static final int TD_SCDMA = 131072;
    public static final int UMTS = 8;
    public static final int UNKNOWN = 1;

    public static final String toString(int o) {
        if (o == 1) {
            return "UNKNOWN";
        }
        if (o == 2) {
            return "GPRS";
        }
        if (o == 4) {
            return "EDGE";
        }
        if (o == 8) {
            return "UMTS";
        }
        if (o == 16) {
            return "IS95A";
        }
        if (o == 32) {
            return "IS95B";
        }
        if (o == 64) {
            return "ONE_X_RTT";
        }
        if (o == 128) {
            return "EVDO_0";
        }
        if (o == 256) {
            return "EVDO_A";
        }
        if (o == 512) {
            return "HSDPA";
        }
        if (o == 1024) {
            return "HSUPA";
        }
        if (o == 2048) {
            return "HSPA";
        }
        if (o == 4096) {
            return "EVDO_B";
        }
        if (o == 8192) {
            return "EHRPD";
        }
        if (o == 16384) {
            return DctConstants.RAT_NAME_LTE;
        }
        if (o == 32768) {
            return "HSPAP";
        }
        if (o == 65536) {
            return "GSM";
        }
        if (o == 131072) {
            return "TD_SCDMA";
        }
        if (o == 524288) {
            return "LTE_CA";
        }
        if (o == 1048576) {
            return "NR";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add("UNKNOWN");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("GPRS");
            flipped |= 2;
        }
        if ((o & 4) == 4) {
            list.add("EDGE");
            flipped |= 4;
        }
        if ((o & 8) == 8) {
            list.add("UMTS");
            flipped |= 8;
        }
        if ((o & 16) == 16) {
            list.add("IS95A");
            flipped |= 16;
        }
        if ((o & 32) == 32) {
            list.add("IS95B");
            flipped |= 32;
        }
        if ((o & 64) == 64) {
            list.add("ONE_X_RTT");
            flipped |= 64;
        }
        if ((o & 128) == 128) {
            list.add("EVDO_0");
            flipped |= 128;
        }
        if ((o & 256) == 256) {
            list.add("EVDO_A");
            flipped |= 256;
        }
        if ((o & 512) == 512) {
            list.add("HSDPA");
            flipped |= 512;
        }
        if ((o & 1024) == 1024) {
            list.add("HSUPA");
            flipped |= 1024;
        }
        if ((o & 2048) == 2048) {
            list.add("HSPA");
            flipped |= 2048;
        }
        if ((o & 4096) == 4096) {
            list.add("EVDO_B");
            flipped |= 4096;
        }
        if ((o & 8192) == 8192) {
            list.add("EHRPD");
            flipped |= 8192;
        }
        if ((o & 16384) == 16384) {
            list.add(DctConstants.RAT_NAME_LTE);
            flipped |= 16384;
        }
        if ((o & 32768) == 32768) {
            list.add("HSPAP");
            flipped |= 32768;
        }
        if ((o & 65536) == 65536) {
            list.add("GSM");
            flipped |= 65536;
        }
        if ((o & 131072) == 131072) {
            list.add("TD_SCDMA");
            flipped |= 131072;
        }
        if ((o & 524288) == 524288) {
            list.add("LTE_CA");
            flipped |= 524288;
        }
        if ((o & 1048576) == 1048576) {
            list.add("NR");
            flipped |= 1048576;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
