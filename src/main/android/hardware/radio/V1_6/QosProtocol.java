package android.hardware.radio.V1_6;

import android.telephony.ims.SipDelegateImsConfiguration;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class QosProtocol {

    /* renamed from: AH */
    public static final byte f188AH = 51;
    public static final byte ESP = 50;
    public static final byte TCP = 6;
    public static final byte UDP = 17;
    public static final byte UNSPECIFIED = -1;

    public static final String toString(byte o) {
        if (o == -1) {
            return "UNSPECIFIED";
        }
        if (o == 6) {
            return SipDelegateImsConfiguration.SIP_TRANSPORT_TCP;
        }
        if (o == 17) {
            return SipDelegateImsConfiguration.SIP_TRANSPORT_UDP;
        }
        if (o == 50) {
            return "ESP";
        }
        if (o == 51) {
            return "AH";
        }
        return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
    }

    public static final String dumpBitfield(byte o) {
        ArrayList<String> list = new ArrayList<>();
        byte flipped = 0;
        if ((o & (-1)) == -1) {
            list.add("UNSPECIFIED");
            flipped = (byte) (0 | (-1));
        }
        if ((o & 6) == 6) {
            list.add(SipDelegateImsConfiguration.SIP_TRANSPORT_TCP);
            flipped = (byte) (flipped | 6);
        }
        if ((o & 17) == 17) {
            list.add(SipDelegateImsConfiguration.SIP_TRANSPORT_UDP);
            flipped = (byte) (flipped | 17);
        }
        if ((o & 50) == 50) {
            list.add("ESP");
            flipped = (byte) (flipped | 50);
        }
        if ((o & 51) == 51) {
            list.add("AH");
            flipped = (byte) (flipped | 51);
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
        }
        return String.join(" | ", list);
    }
}
