package com.android.net.module.util;

import android.net.MacAddress;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
/* loaded from: classes5.dex */
public final class MacAddressUtils {
    private static final int ETHER_ADDR_LEN = 6;
    private static final long VALID_LONG_MASK = 281474976710655L;
    private static final long LOCALLY_ASSIGNED_MASK = longAddrFromByteAddr(MacAddress.fromString("2:0:0:0:0:0").toByteArray());
    private static final long MULTICAST_MASK = longAddrFromByteAddr(MacAddress.fromString("1:0:0:0:0:0").toByteArray());
    private static final long OUI_MASK = longAddrFromByteAddr(MacAddress.fromString("ff:ff:ff:0:0:0").toByteArray());
    private static final long NIC_MASK = longAddrFromByteAddr(MacAddress.fromString("0:0:0:ff:ff:ff").toByteArray());
    private static final MacAddress DEFAULT_MAC_ADDRESS = MacAddress.fromString("02:00:00:00:00:00");

    public static boolean isMulticastAddress(MacAddress address) {
        return (longAddrFromByteAddr(address.toByteArray()) & MULTICAST_MASK) != 0;
    }

    public static MacAddress createRandomUnicastAddress() {
        return createRandomUnicastAddress(null, new SecureRandom());
    }

    public static MacAddress createRandomUnicastAddress(MacAddress base, Random r) {
        long addr;
        if (base == null) {
            addr = r.nextLong() & VALID_LONG_MASK;
        } else {
            addr = (longAddrFromByteAddr(base.toByteArray()) & OUI_MASK) | (NIC_MASK & r.nextLong());
        }
        MacAddress mac = MacAddress.fromBytes(byteAddrFromLongAddr((addr | LOCALLY_ASSIGNED_MASK) & (~MULTICAST_MASK)));
        if (mac.equals(DEFAULT_MAC_ADDRESS)) {
            return createRandomUnicastAddress(base, r);
        }
        return mac;
    }

    public static long longAddrFromByteAddr(byte[] addr) {
        Objects.requireNonNull(addr);
        if (!isMacAddress(addr)) {
            throw new IllegalArgumentException(Arrays.toString(addr) + " was not a valid MAC address");
        }
        long longAddr = 0;
        for (byte b : addr) {
            int uint8Byte = b & 255;
            longAddr = (longAddr << 8) + uint8Byte;
        }
        return longAddr;
    }

    public static byte[] byteAddrFromLongAddr(long addr) {
        byte[] bytes = new byte[6];
        int index = 6;
        while (true) {
            int index2 = index - 1;
            if (index > 0) {
                bytes[index2] = (byte) addr;
                addr >>= 8;
                index = index2;
            } else {
                return bytes;
            }
        }
    }

    public static boolean isMacAddress(byte[] addr) {
        return addr != null && addr.length == 6;
    }
}
