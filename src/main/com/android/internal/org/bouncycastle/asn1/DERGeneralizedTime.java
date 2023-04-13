package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.util.Date;
/* loaded from: classes4.dex */
public class DERGeneralizedTime extends ASN1GeneralizedTime {
    public DERGeneralizedTime(byte[] time) {
        super(time);
    }

    public DERGeneralizedTime(Date time) {
        super(time);
    }

    public DERGeneralizedTime(String time) {
        super(time);
    }

    private byte[] getDERTime() {
        if (this.time[this.time.length - 1] == 90) {
            if (!hasMinutes()) {
                byte[] derTime = new byte[this.time.length + 4];
                System.arraycopy(this.time, 0, derTime, 0, this.time.length - 1);
                System.arraycopy(Strings.toByteArray("0000Z"), 0, derTime, this.time.length - 1, 5);
                return derTime;
            } else if (!hasSeconds()) {
                byte[] derTime2 = new byte[this.time.length + 2];
                System.arraycopy(this.time, 0, derTime2, 0, this.time.length - 1);
                System.arraycopy(Strings.toByteArray("00Z"), 0, derTime2, this.time.length - 1, 3);
                return derTime2;
            } else if (hasFractionalSeconds()) {
                int ind = this.time.length - 2;
                while (ind > 0 && this.time[ind] == 48) {
                    ind--;
                }
                if (this.time[ind] == 46) {
                    byte[] derTime3 = new byte[ind + 1];
                    System.arraycopy(this.time, 0, derTime3, 0, ind);
                    derTime3[ind] = 90;
                    return derTime3;
                }
                byte[] derTime4 = new byte[ind + 2];
                System.arraycopy(this.time, 0, derTime4, 0, ind + 1);
                derTime4[ind + 1] = 90;
                return derTime4;
            } else {
                return this.time;
            }
        }
        return this.time;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    int encodedLength() {
        int length = getDERTime().length;
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncoded(withTag, 24, getDERTime());
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime, com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    ASN1Primitive toDLObject() {
        return this;
    }
}
