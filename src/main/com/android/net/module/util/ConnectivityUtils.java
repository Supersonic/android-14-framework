package com.android.net.module.util;

import com.android.internal.midi.MidiConstants;
import java.net.Inet6Address;
import java.net.InetAddress;
/* loaded from: classes5.dex */
public final class ConnectivityUtils {
    private ConnectivityUtils() {
    }

    public static String addressAndPortToString(InetAddress address, int port) {
        return String.format(address instanceof Inet6Address ? "[%s]:%d" : "%s:%d", address.getHostAddress(), Integer.valueOf(port));
    }

    public static boolean isIPv6ULA(InetAddress addr) {
        return (addr instanceof Inet6Address) && (addr.getAddress()[0] & MidiConstants.STATUS_ACTIVE_SENSING) == 252;
    }

    public static int saturatedCast(long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (value < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }
}
