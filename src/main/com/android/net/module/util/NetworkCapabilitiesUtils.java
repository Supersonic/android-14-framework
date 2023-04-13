package com.android.net.module.util;

import android.net.NetworkCapabilities;
/* loaded from: classes5.dex */
public final class NetworkCapabilitiesUtils {
    private static final int[] DISPLAY_TRANSPORT_PRIORITIES = {4, 0, 5, 2, 1, 3, 8};
    public static final long RESTRICTED_CAPABILITIES = BitUtils.packBitList(31, 5, 2, 10, 29, 3, 7, 4, 23, 8, 27, 30, 9, 33);
    private static final long FORCE_RESTRICTED_CAPABILITIES = BitUtils.packBitList(29, 22, 26);
    public static final long UNRESTRICTED_CAPABILITIES = BitUtils.packBitList(12, 0, 1, 6);

    public static int getDisplayTransport(int[] transports) {
        int[] iArr;
        for (int transport : DISPLAY_TRANSPORT_PRIORITIES) {
            if (CollectionUtils.contains(transports, transport)) {
                return transport;
            }
        }
        if (transports.length < 1) {
            throw new IllegalArgumentException("No transport in the provided array");
        }
        return transports[0];
    }

    public static boolean inferRestrictedCapability(NetworkCapabilities nc) {
        int[] unpackBits;
        int[] unpackBits2;
        int[] unpackBits3;
        for (int capability : BitUtils.unpackBits(FORCE_RESTRICTED_CAPABILITIES)) {
            if (nc.hasCapability(capability)) {
                return true;
            }
        }
        for (int capability2 : BitUtils.unpackBits(UNRESTRICTED_CAPABILITIES)) {
            if (nc.hasCapability(capability2)) {
                return false;
            }
        }
        for (int capability3 : BitUtils.unpackBits(RESTRICTED_CAPABILITIES)) {
            if (nc.hasCapability(capability3)) {
                return true;
            }
        }
        return false;
    }
}
