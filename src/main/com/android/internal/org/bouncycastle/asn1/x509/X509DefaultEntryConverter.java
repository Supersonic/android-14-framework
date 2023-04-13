package com.android.internal.org.bouncycastle.asn1.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERGeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.DERIA5String;
import com.android.internal.org.bouncycastle.asn1.DERPrintableString;
import com.android.internal.org.bouncycastle.asn1.DERUTF8String;
import java.io.IOException;
/* loaded from: classes4.dex */
public class X509DefaultEntryConverter extends X509NameEntryConverter {
    @Override // com.android.internal.org.bouncycastle.asn1.x509.X509NameEntryConverter
    public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier oid, String value) {
        if (value.length() != 0 && value.charAt(0) == '#') {
            try {
                return convertHexEncoded(value, 1);
            } catch (IOException e) {
                throw new RuntimeException("can't recode value for oid " + oid.getId());
            }
        }
        if (value.length() != 0 && value.charAt(0) == '\\') {
            value = value.substring(1);
        }
        if (oid.equals((ASN1Primitive) X509Name.EmailAddress) || oid.equals((ASN1Primitive) X509Name.f627DC)) {
            return new DERIA5String(value);
        }
        if (oid.equals((ASN1Primitive) X509Name.DATE_OF_BIRTH)) {
            return new DERGeneralizedTime(value);
        }
        if (oid.equals((ASN1Primitive) X509Name.f625C) || oid.equals((ASN1Primitive) X509Name.f632SN) || oid.equals((ASN1Primitive) X509Name.DN_QUALIFIER) || oid.equals((ASN1Primitive) X509Name.TELEPHONE_NUMBER)) {
            return new DERPrintableString(value);
        }
        return new DERUTF8String(value);
    }
}
