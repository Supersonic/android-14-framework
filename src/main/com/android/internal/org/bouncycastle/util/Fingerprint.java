package com.android.internal.org.bouncycastle.util;

import android.text.format.DateFormat;
import com.android.internal.midi.MidiConstants;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
/* loaded from: classes4.dex */
public class Fingerprint {
    private static char[] encodingTable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
    private final byte[] fingerprint;

    public Fingerprint(byte[] source) {
        this(source, 160);
    }

    public Fingerprint(byte[] source, int bitLength) {
        this.fingerprint = calculateFingerprint(source, bitLength);
    }

    public byte[] getFingerprint() {
        return Arrays.clone(this.fingerprint);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i != this.fingerprint.length; i++) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(encodingTable[(this.fingerprint[i] >>> 4) & 15]);
            sb.append(encodingTable[this.fingerprint[i] & MidiConstants.STATUS_CHANNEL_MASK]);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Fingerprint) {
            return Arrays.areEqual(((Fingerprint) o).fingerprint, this.fingerprint);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.fingerprint);
    }

    public static byte[] calculateFingerprint(byte[] input) {
        return calculateFingerprint(input, 160);
    }

    public static byte[] calculateFingerprint(byte[] input, int bitLength) {
        if (bitLength % 8 != 0) {
            throw new IllegalArgumentException("bitLength must be a multiple of 8");
        }
        Digest digest = AndroidDigestFactory.getSHA256();
        digest.update(input, 0, input.length);
        byte[] rv = new byte[bitLength / 8];
        byte[] untruncated = new byte[32];
        digest.doFinal(untruncated, 0);
        if (bitLength / 8 >= 32) {
            return untruncated;
        }
        System.arraycopy(untruncated, 0, rv, 0, rv.length);
        return rv;
    }
}
