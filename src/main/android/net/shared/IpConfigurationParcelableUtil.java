package android.net.shared;

import android.net.InetAddresses;
import java.net.InetAddress;
/* loaded from: classes.dex */
public final class IpConfigurationParcelableUtil {
    public static String parcelAddress(InetAddress inetAddress) {
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getHostAddress();
    }

    public static InetAddress unparcelAddress(String str) {
        if (str == null) {
            return null;
        }
        return InetAddresses.parseNumericAddress(str);
    }
}
