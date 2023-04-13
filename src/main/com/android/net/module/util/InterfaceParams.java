package com.android.net.module.util;

import android.net.MacAddress;
import android.text.TextUtils;
import java.net.NetworkInterface;
import java.net.SocketException;
/* loaded from: classes5.dex */
public class InterfaceParams {
    private static final int ETHER_MTU = 1500;
    private static final int IPV6_MIN_MTU = 1280;
    public final int defaultMtu;
    public final boolean hasMacAddress;
    public final int index;
    public final MacAddress macAddr;
    public final String name;

    public static InterfaceParams getByName(String name) {
        NetworkInterface netif = getNetworkInterfaceByName(name);
        if (netif == null) {
            return null;
        }
        MacAddress macAddr = getMacAddress(netif);
        try {
            return new InterfaceParams(name, netif.getIndex(), macAddr, netif.getMTU());
        } catch (IllegalArgumentException | SocketException e) {
            return null;
        }
    }

    public InterfaceParams(String name, int index, MacAddress macAddr) {
        this(name, index, macAddr, 1500);
    }

    public InterfaceParams(String name, int index, MacAddress macAddr, int defaultMtu) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("impossible interface name");
        }
        if (index <= 0) {
            throw new IllegalArgumentException("invalid interface index");
        }
        this.name = name;
        this.index = index;
        boolean z = macAddr != null;
        this.hasMacAddress = z;
        this.macAddr = z ? macAddr : MacAddress.fromBytes(new byte[]{2, 0, 0, 0, 0, 0});
        this.defaultMtu = defaultMtu > 1280 ? defaultMtu : 1280;
    }

    public String toString() {
        return String.format("%s/%d/%s/%d", this.name, Integer.valueOf(this.index), this.macAddr, Integer.valueOf(this.defaultMtu));
    }

    private static NetworkInterface getNetworkInterfaceByName(String name) {
        try {
            return NetworkInterface.getByName(name);
        } catch (NullPointerException | SocketException e) {
            return null;
        }
    }

    private static MacAddress getMacAddress(NetworkInterface netif) {
        try {
            return MacAddress.fromBytes(netif.getHardwareAddress());
        } catch (IllegalArgumentException | NullPointerException | SocketException e) {
            return null;
        }
    }
}
