package com.android.internal.telephony.util;

import android.net.MacAddress;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
/* loaded from: classes.dex */
public final class MacAddressUtils {
    private static final long LOCALLY_ASSIGNED_MASK = longAddrFromByteAddr(MacAddress.fromString("2:0:0:0:0:0").toByteArray());
    private static final long MULTICAST_MASK = longAddrFromByteAddr(MacAddress.fromString("1:0:0:0:0:0").toByteArray());
    private static final long OUI_MASK = longAddrFromByteAddr(MacAddress.fromString("ff:ff:ff:0:0:0").toByteArray());
    private static final long NIC_MASK = longAddrFromByteAddr(MacAddress.fromString("0:0:0:ff:ff:ff").toByteArray());
    private static final MacAddress DEFAULT_MAC_ADDRESS = MacAddress.fromString("02:00:00:00:00:00");

    public static boolean isMulticastAddress(MacAddress macAddress) {
        return (longAddrFromByteAddr(macAddress.toByteArray()) & MULTICAST_MASK) != 0;
    }

    public static MacAddress createRandomUnicastAddress() {
        return createRandomUnicastAddress(null, new SecureRandom());
    }

    public static MacAddress createRandomUnicastAddress(MacAddress macAddress, Random random) {
        long longAddrFromByteAddr;
        if (macAddress == null) {
            longAddrFromByteAddr = random.nextLong() & 281474976710655L;
        } else {
            longAddrFromByteAddr = (longAddrFromByteAddr(macAddress.toByteArray()) & OUI_MASK) | (NIC_MASK & random.nextLong());
        }
        MacAddress fromBytes = MacAddress.fromBytes(byteAddrFromLongAddr((longAddrFromByteAddr | LOCALLY_ASSIGNED_MASK) & (~MULTICAST_MASK)));
        return fromBytes.equals(DEFAULT_MAC_ADDRESS) ? createRandomUnicastAddress(macAddress, random) : fromBytes;
    }

    public static long longAddrFromByteAddr(byte[] bArr) {
        Objects.requireNonNull(bArr);
        if (!isMacAddress(bArr)) {
            throw new IllegalArgumentException(Arrays.toString(bArr) + " was not a valid MAC address");
        }
        long j = 0;
        for (byte b : bArr) {
            j = (j << 8) + (b & 255);
        }
        return j;
    }

    public static byte[] byteAddrFromLongAddr(long j) {
        int i = 6;
        byte[] bArr = new byte[6];
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                return bArr;
            }
            bArr[i2] = (byte) j;
            j >>= 8;
            i = i2;
        }
    }

    public static boolean isMacAddress(byte[] bArr) {
        return bArr != null && bArr.length == 6;
    }
}
