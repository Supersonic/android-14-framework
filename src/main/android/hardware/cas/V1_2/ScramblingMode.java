package android.hardware.cas.V1_2;

import java.util.ArrayList;
/* loaded from: classes.dex */
public final class ScramblingMode {
    public static final int AES128 = 9;
    public static final int AES_ECB = 10;
    public static final int AES_SCTE52 = 11;
    public static final int DVB_CISSA_V1 = 6;
    public static final int DVB_CSA1 = 1;
    public static final int DVB_CSA2 = 2;
    public static final int DVB_CSA3_ENHANCE = 5;
    public static final int DVB_CSA3_MINIMAL = 4;
    public static final int DVB_CSA3_STANDARD = 3;
    public static final int DVB_IDSA = 7;
    public static final int MULTI2 = 8;
    public static final int RESERVED = 0;
    public static final int TDES_ECB = 12;
    public static final int TDES_SCTE52 = 13;

    public static final String toString(int o) {
        if (o == 0) {
            return "RESERVED";
        }
        if (o == 1) {
            return "DVB_CSA1";
        }
        if (o == 2) {
            return "DVB_CSA2";
        }
        if (o == 3) {
            return "DVB_CSA3_STANDARD";
        }
        if (o == 4) {
            return "DVB_CSA3_MINIMAL";
        }
        if (o == 5) {
            return "DVB_CSA3_ENHANCE";
        }
        if (o == 6) {
            return "DVB_CISSA_V1";
        }
        if (o == 7) {
            return "DVB_IDSA";
        }
        if (o == 8) {
            return "MULTI2";
        }
        if (o == 9) {
            return "AES128";
        }
        if (o == 10) {
            return "AES_ECB";
        }
        if (o == 11) {
            return "AES_SCTE52";
        }
        if (o == 12) {
            return "TDES_ECB";
        }
        if (o == 13) {
            return "TDES_SCTE52";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add("RESERVED");
        if ((o & 1) == 1) {
            list.add("DVB_CSA1");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("DVB_CSA2");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("DVB_CSA3_STANDARD");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("DVB_CSA3_MINIMAL");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("DVB_CSA3_ENHANCE");
            flipped |= 5;
        }
        if ((o & 6) == 6) {
            list.add("DVB_CISSA_V1");
            flipped |= 6;
        }
        if ((o & 7) == 7) {
            list.add("DVB_IDSA");
            flipped |= 7;
        }
        if ((o & 8) == 8) {
            list.add("MULTI2");
            flipped |= 8;
        }
        if ((o & 9) == 9) {
            list.add("AES128");
            flipped |= 9;
        }
        if ((o & 10) == 10) {
            list.add("AES_ECB");
            flipped |= 10;
        }
        if ((o & 11) == 11) {
            list.add("AES_SCTE52");
            flipped |= 11;
        }
        if ((o & 12) == 12) {
            list.add("TDES_ECB");
            flipped |= 12;
        }
        if ((o & 13) == 13) {
            list.add("TDES_SCTE52");
            flipped |= 13;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
