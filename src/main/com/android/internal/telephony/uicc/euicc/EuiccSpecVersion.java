package com.android.internal.telephony.uicc.euicc;

import com.android.internal.telephony.uicc.asn1.Asn1Decoder;
import com.android.internal.telephony.uicc.asn1.Asn1Node;
import com.android.internal.telephony.uicc.asn1.InvalidAsn1DataException;
import com.android.internal.telephony.uicc.asn1.TagNotFoundException;
import com.android.telephony.Rlog;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class EuiccSpecVersion implements Comparable<EuiccSpecVersion> {
    private final int[] mVersionValues;

    public static EuiccSpecVersion fromOpenChannelResponse(byte[] bArr) {
        byte[] asBytes;
        try {
            Asn1Decoder asn1Decoder = new Asn1Decoder(bArr);
            if (asn1Decoder.hasNextNode()) {
                Asn1Node nextNode = asn1Decoder.nextNode();
                try {
                    if (nextNode.getTag() == 224) {
                        asBytes = nextNode.getChild(130, new int[0]).asBytes();
                    } else {
                        asBytes = nextNode.getChild(224, new int[]{130}).asBytes();
                    }
                } catch (InvalidAsn1DataException | TagNotFoundException unused) {
                    Rlog.e("EuiccSpecVer", "Cannot parse select response of ISD-R: " + nextNode.toHex());
                }
                if (asBytes.length == 3) {
                    return new EuiccSpecVersion(asBytes);
                }
                Rlog.e("EuiccSpecVer", "Cannot parse select response of ISD-R: " + nextNode.toHex());
                return null;
            }
            return null;
        } catch (InvalidAsn1DataException e) {
            Rlog.e("EuiccSpecVer", "Cannot parse the select response of ISD-R.", e);
            return null;
        }
    }

    public EuiccSpecVersion(int i, int i2, int i3) {
        this.mVersionValues = r0;
        int[] iArr = {i, i2, i3};
    }

    public EuiccSpecVersion(byte[] bArr) {
        this.mVersionValues = r0;
        int[] iArr = {bArr[0] & 255, bArr[1] & 255, bArr[2] & 255};
    }

    public int getMajor() {
        return this.mVersionValues[0];
    }

    public int getMinor() {
        return this.mVersionValues[1];
    }

    public int getRevision() {
        return this.mVersionValues[2];
    }

    @Override // java.lang.Comparable
    public int compareTo(EuiccSpecVersion euiccSpecVersion) {
        if (getMajor() > euiccSpecVersion.getMajor()) {
            return 1;
        }
        if (getMajor() < euiccSpecVersion.getMajor()) {
            return -1;
        }
        if (getMinor() > euiccSpecVersion.getMinor()) {
            return 1;
        }
        if (getMinor() < euiccSpecVersion.getMinor()) {
            return -1;
        }
        if (getRevision() > euiccSpecVersion.getRevision()) {
            return 1;
        }
        return getRevision() < euiccSpecVersion.getRevision() ? -1 : 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || EuiccSpecVersion.class != obj.getClass()) {
            return false;
        }
        return Arrays.equals(this.mVersionValues, ((EuiccSpecVersion) obj).mVersionValues);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mVersionValues);
    }

    public String toString() {
        return this.mVersionValues[0] + "." + this.mVersionValues[1] + "." + this.mVersionValues[2];
    }
}
