package com.android.net.module.util;

import android.net.IpPrefix;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
/* loaded from: classes5.dex */
public final class IpRange {
    private static final int SIGNUM_POSITIVE = 1;
    private final byte[] mEndAddr;
    private final byte[] mStartAddr;

    public IpRange(InetAddress startAddr, InetAddress endAddr) {
        Objects.requireNonNull(startAddr, "startAddr must not be null");
        Objects.requireNonNull(endAddr, "endAddr must not be null");
        if (!startAddr.getClass().equals(endAddr.getClass())) {
            throw new IllegalArgumentException("Invalid range: Address family mismatch");
        }
        if (addrToBigInteger(startAddr.getAddress()).compareTo(addrToBigInteger(endAddr.getAddress())) >= 0) {
            throw new IllegalArgumentException("Invalid range; start address must be before end address");
        }
        this.mStartAddr = startAddr.getAddress();
        this.mEndAddr = endAddr.getAddress();
    }

    public IpRange(IpPrefix prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        this.mStartAddr = prefix.getRawAddress();
        this.mEndAddr = prefix.getRawAddress();
        int bitIndex = prefix.getPrefixLength();
        while (true) {
            byte[] bArr = this.mEndAddr;
            if (bitIndex < bArr.length * 8) {
                int i = bitIndex / 8;
                bArr[i] = (byte) (bArr[i] | ((byte) (128 >> (bitIndex % 8))));
                bitIndex++;
            } else {
                return;
            }
        }
    }

    private static InetAddress getAsInetAddress(byte[] address) {
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Address is invalid");
        }
    }

    public InetAddress getStartAddr() {
        return getAsInetAddress(this.mStartAddr);
    }

    public InetAddress getEndAddr() {
        return getAsInetAddress(this.mEndAddr);
    }

    public List<IpPrefix> asIpPrefixes() {
        boolean isIpv6 = this.mStartAddr.length == 16;
        List<IpPrefix> result = new ArrayList<>();
        Queue<IpPrefix> workingSet = new LinkedList<>();
        workingSet.add(new IpPrefix(isIpv6 ? getAsInetAddress(new byte[16]) : getAsInetAddress(new byte[4]), 0));
        while (!workingSet.isEmpty()) {
            IpPrefix workingPrefix = workingSet.poll();
            IpRange workingRange = new IpRange(workingPrefix);
            if (containsRange(workingRange)) {
                result.add(workingPrefix);
            } else if (overlapsRange(workingRange)) {
                workingSet.addAll(getSubsetPrefixes(workingPrefix));
            }
        }
        return result;
    }

    private static List<IpPrefix> getSubsetPrefixes(IpPrefix prefix) {
        List<IpPrefix> result = new ArrayList<>();
        int currentPrefixLen = prefix.getPrefixLength();
        result.add(new IpPrefix(prefix.getAddress(), currentPrefixLen + 1));
        byte[] other = prefix.getRawAddress();
        other[currentPrefixLen / 8] = (byte) (other[currentPrefixLen / 8] ^ (128 >> (currentPrefixLen % 8)));
        result.add(new IpPrefix(getAsInetAddress(other), currentPrefixLen + 1));
        return result;
    }

    public boolean containsRange(IpRange other) {
        return addrToBigInteger(this.mStartAddr).compareTo(addrToBigInteger(other.mStartAddr)) <= 0 && addrToBigInteger(this.mEndAddr).compareTo(addrToBigInteger(other.mEndAddr)) >= 0;
    }

    public boolean overlapsRange(IpRange other) {
        return addrToBigInteger(this.mStartAddr).compareTo(addrToBigInteger(other.mEndAddr)) <= 0 && addrToBigInteger(other.mStartAddr).compareTo(addrToBigInteger(this.mEndAddr)) <= 0;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(Arrays.hashCode(this.mStartAddr)), Integer.valueOf(Arrays.hashCode(this.mEndAddr)));
    }

    public boolean equals(Object obj) {
        if (obj instanceof IpRange) {
            IpRange other = (IpRange) obj;
            return Arrays.equals(this.mStartAddr, other.mStartAddr) && Arrays.equals(this.mEndAddr, other.mEndAddr);
        }
        return false;
    }

    private static BigInteger addrToBigInteger(byte[] addr) {
        return new BigInteger(1, addr);
    }
}
