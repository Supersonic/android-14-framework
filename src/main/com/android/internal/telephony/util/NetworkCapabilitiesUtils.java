package com.android.internal.telephony.util;

import android.net.NetworkCapabilities;
import com.android.net.module.annotation.VisibleForTesting;
/* loaded from: classes.dex */
public final class NetworkCapabilitiesUtils {
    private static final int[] DISPLAY_TRANSPORT_PRIORITIES = {4, 0, 5, 2, 1, 3, 8};
    @VisibleForTesting
    public static final long RESTRICTED_CAPABILITIES = BitUtils.packBitList(31, 5, 2, 10, 29, 3, 7, 4, 23, 8, 27, 30, 9, 33);
    private static final long FORCE_RESTRICTED_CAPABILITIES = BitUtils.packBitList(29, 22, 26);
    @VisibleForTesting
    public static final long UNRESTRICTED_CAPABILITIES = BitUtils.packBitList(12, 0, 1, 6);

    public static int getDisplayTransport(int[] iArr) {
        int[] iArr2;
        for (int i : DISPLAY_TRANSPORT_PRIORITIES) {
            if (CollectionUtils.contains(iArr, i)) {
                return i;
            }
        }
        if (iArr.length < 1) {
            throw new IllegalArgumentException("No transport in the provided array");
        }
        return iArr[0];
    }

    public static boolean inferRestrictedCapability(NetworkCapabilities networkCapabilities) {
        for (int i : BitUtils.unpackBits(FORCE_RESTRICTED_CAPABILITIES)) {
            if (networkCapabilities.hasCapability(i)) {
                return true;
            }
        }
        for (int i2 : BitUtils.unpackBits(UNRESTRICTED_CAPABILITIES)) {
            if (networkCapabilities.hasCapability(i2)) {
                return false;
            }
        }
        for (int i3 : BitUtils.unpackBits(RESTRICTED_CAPABILITIES)) {
            if (networkCapabilities.hasCapability(i3)) {
                return true;
            }
        }
        return false;
    }
}
