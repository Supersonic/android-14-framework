package com.android.net.module.util;

import android.net.RouteInfo;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
/* loaded from: classes5.dex */
public final class NetUtils {
    public static boolean addressTypeMatches(InetAddress left, InetAddress right) {
        return ((left instanceof Inet4Address) && (right instanceof Inet4Address)) || ((left instanceof Inet6Address) && (right instanceof Inet6Address));
    }

    public static RouteInfo selectBestRoute(Collection<RouteInfo> routes, InetAddress dest) {
        if (routes == null || dest == null) {
            return null;
        }
        RouteInfo bestRoute = null;
        for (RouteInfo route : routes) {
            if (addressTypeMatches(route.getDestination().getAddress(), dest) && (bestRoute == null || bestRoute.getDestination().getPrefixLength() < route.getDestination().getPrefixLength())) {
                if (route.matches(dest)) {
                    bestRoute = route;
                }
            }
        }
        if (bestRoute == null || bestRoute.getType() != 1) {
            return null;
        }
        return bestRoute;
    }

    public static InetAddress getNetworkPart(InetAddress address, int prefixLength) {
        byte[] array = address.getAddress();
        maskRawAddress(array, prefixLength);
        try {
            InetAddress netPart = InetAddress.getByAddress(array);
            return netPart;
        } catch (UnknownHostException e) {
            throw new RuntimeException("getNetworkPart error - " + e.toString());
        }
    }

    public static void maskRawAddress(byte[] array, int prefixLength) {
        if (prefixLength < 0 || prefixLength > array.length * 8) {
            throw new RuntimeException("IP address with " + array.length + " bytes has invalid prefix length " + prefixLength);
        }
        int offset = prefixLength / 8;
        int remainder = prefixLength % 8;
        byte mask = (byte) (255 << (8 - remainder));
        if (offset < array.length) {
            array[offset] = (byte) (array[offset] & mask);
        }
        while (true) {
            offset++;
            if (offset < array.length) {
                array[offset] = 0;
            } else {
                return;
            }
        }
    }
}
