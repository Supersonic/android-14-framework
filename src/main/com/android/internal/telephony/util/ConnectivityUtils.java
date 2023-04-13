package com.android.internal.telephony.util;

import com.android.internal.telephony.data.KeepaliveStatus;
import java.net.Inet6Address;
import java.net.InetAddress;
/* loaded from: classes.dex */
public final class ConnectivityUtils {
    public static int saturatedCast(long j) {
        return j > 2147483647L ? KeepaliveStatus.INVALID_HANDLE : j < -2147483648L ? NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_ROUTER : (int) j;
    }

    private ConnectivityUtils() {
    }

    public static String addressAndPortToString(InetAddress inetAddress, int i) {
        return String.format(inetAddress instanceof Inet6Address ? "[%s]:%d" : "%s:%d", inetAddress.getHostAddress(), Integer.valueOf(i));
    }

    public static boolean isIPv6ULA(InetAddress inetAddress) {
        return (inetAddress instanceof Inet6Address) && (inetAddress.getAddress()[0] & 254) == 252;
    }
}
