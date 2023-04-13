package com.android.internal.org.bouncycastle.asn1;

import java.util.Date;
/* loaded from: classes4.dex */
public class DERUTCTime extends ASN1UTCTime {
    DERUTCTime(byte[] bytes) {
        super(bytes);
    }

    public DERUTCTime(Date time) {
        super(time);
    }

    public DERUTCTime(String time) {
        super(time);
    }
}
