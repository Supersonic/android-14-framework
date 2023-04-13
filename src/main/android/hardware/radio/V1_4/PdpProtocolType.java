package android.hardware.radio.V1_4;

import android.telephony.CarrierConfigManager;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class PdpProtocolType {

    /* renamed from: IP */
    public static final int f177IP = 0;
    public static final int IPV4V6 = 2;
    public static final int IPV6 = 1;
    public static final int NON_IP = 4;
    public static final int PPP = 3;
    public static final int UNKNOWN = -1;
    public static final int UNSTRUCTURED = 5;

    public static final String toString(int o) {
        if (o == -1) {
            return "UNKNOWN";
        }
        if (o == 0) {
            return CarrierConfigManager.Apn.PROTOCOL_IPV4;
        }
        if (o == 1) {
            return "IPV6";
        }
        if (o == 2) {
            return CarrierConfigManager.Apn.PROTOCOL_IPV4V6;
        }
        if (o == 3) {
            return "PPP";
        }
        if (o == 4) {
            return "NON_IP";
        }
        if (o == 5) {
            return "UNSTRUCTURED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & (-1)) == -1) {
            list.add("UNKNOWN");
            flipped = 0 | (-1);
        }
        list.add(CarrierConfigManager.Apn.PROTOCOL_IPV4);
        if ((o & 1) == 1) {
            list.add("IPV6");
            flipped |= 1;
        }
        if ((o & 2) == 2) {
            list.add(CarrierConfigManager.Apn.PROTOCOL_IPV4V6);
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("PPP");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("NON_IP");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("UNSTRUCTURED");
            flipped |= 5;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
