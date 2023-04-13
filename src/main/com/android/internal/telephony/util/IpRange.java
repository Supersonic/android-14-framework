package com.android.internal.telephony.util;

import android.net.IpPrefix;
import com.android.net.module.annotation.VisibleForTesting;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class IpRange {
    private final byte[] mEndAddr;
    private final byte[] mStartAddr;

    public IpRange(InetAddress inetAddress, InetAddress inetAddress2) {
        Objects.requireNonNull(inetAddress, "startAddr must not be null");
        Objects.requireNonNull(inetAddress2, "endAddr must not be null");
        if (!inetAddress.getClass().equals(inetAddress2.getClass())) {
            throw new IllegalArgumentException("Invalid range: Address family mismatch");
        }
        if (addrToBigInteger(inetAddress.getAddress()).compareTo(addrToBigInteger(inetAddress2.getAddress())) >= 0) {
            throw new IllegalArgumentException("Invalid range; start address must be before end address");
        }
        this.mStartAddr = inetAddress.getAddress();
        this.mEndAddr = inetAddress2.getAddress();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public IpRange(IpPrefix ipPrefix) {
        Objects.requireNonNull(ipPrefix, "prefix must not be null");
        this.mStartAddr = ipPrefix.getRawAddress();
        this.mEndAddr = ipPrefix.getRawAddress();
        int prefixLength = ipPrefix.getPrefixLength();
        while (true) {
            byte[] bArr = this.mEndAddr;
            if (prefixLength >= bArr.length * 8) {
                return;
            }
            int i = prefixLength / 8;
            bArr[i] = (byte) (bArr[i] | ((byte) (128 >> (prefixLength % 8))));
            prefixLength++;
        }
    }

    private static InetAddress getAsInetAddress(byte[] bArr) {
        try {
            return InetAddress.getByAddress(bArr);
        } catch (UnknownHostException unused) {
            throw new IllegalArgumentException("Address is invalid");
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public InetAddress getStartAddr() {
        return getAsInetAddress(this.mStartAddr);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public InetAddress getEndAddr() {
        return getAsInetAddress(this.mEndAddr);
    }

    public List<IpPrefix> asIpPrefixes() {
        boolean z = this.mStartAddr.length == 16;
        ArrayList arrayList = new ArrayList();
        LinkedList linkedList = new LinkedList();
        linkedList.add(new IpPrefix(z ? getAsInetAddress(new byte[16]) : getAsInetAddress(new byte[4]), 0));
        while (!linkedList.isEmpty()) {
            IpPrefix ipPrefix = (IpPrefix) linkedList.poll();
            IpRange ipRange = new IpRange(ipPrefix);
            if (containsRange(ipRange)) {
                arrayList.add(ipPrefix);
            } else if (overlapsRange(ipRange)) {
                linkedList.addAll(getSubsetPrefixes(ipPrefix));
            }
        }
        return arrayList;
    }

    private static List<IpPrefix> getSubsetPrefixes(IpPrefix ipPrefix) {
        ArrayList arrayList = new ArrayList();
        int prefixLength = ipPrefix.getPrefixLength();
        int i = prefixLength + 1;
        arrayList.add(new IpPrefix(ipPrefix.getAddress(), i));
        byte[] rawAddress = ipPrefix.getRawAddress();
        int i2 = prefixLength / 8;
        rawAddress[i2] = (byte) ((128 >> (prefixLength % 8)) ^ rawAddress[i2]);
        arrayList.add(new IpPrefix(getAsInetAddress(rawAddress), i));
        return arrayList;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean containsRange(IpRange ipRange) {
        return addrToBigInteger(this.mStartAddr).compareTo(addrToBigInteger(ipRange.mStartAddr)) <= 0 && addrToBigInteger(this.mEndAddr).compareTo(addrToBigInteger(ipRange.mEndAddr)) >= 0;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public boolean overlapsRange(IpRange ipRange) {
        return addrToBigInteger(this.mStartAddr).compareTo(addrToBigInteger(ipRange.mEndAddr)) <= 0 && addrToBigInteger(ipRange.mStartAddr).compareTo(addrToBigInteger(this.mEndAddr)) <= 0;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(Arrays.hashCode(this.mStartAddr)), Integer.valueOf(Arrays.hashCode(this.mEndAddr)));
    }

    public boolean equals(Object obj) {
        if (obj instanceof IpRange) {
            IpRange ipRange = (IpRange) obj;
            return Arrays.equals(this.mStartAddr, ipRange.mStartAddr) && Arrays.equals(this.mEndAddr, ipRange.mEndAddr);
        }
        return false;
    }

    private static BigInteger addrToBigInteger(byte[] bArr) {
        return new BigInteger(1, bArr);
    }
}
