package com.android.internal.org.bouncycastle.asn1.x500.style;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1ParsingException;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERUTF8String;
import com.android.internal.org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import com.android.internal.org.bouncycastle.asn1.x500.RDN;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public abstract class AbstractX500NameStyle implements X500NameStyle {
    public static Hashtable copyHashTable(Hashtable paramsMap) {
        Hashtable newTable = new Hashtable();
        Enumeration keys = paramsMap.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }

    private int calcHashCode(ASN1Encodable enc) {
        String value = IETFUtils.canonicalString(enc);
        return value.hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public int calculateHashCode(X500Name name) {
        int hashCodeValue = 0;
        RDN[] rdns = name.getRDNs();
        for (int i = 0; i != rdns.length; i++) {
            if (rdns[i].isMultiValued()) {
                AttributeTypeAndValue[] atv = rdns[i].getTypesAndValues();
                for (int j = 0; j != atv.length; j++) {
                    hashCodeValue = (hashCodeValue ^ atv[j].getType().hashCode()) ^ calcHashCode(atv[j].getValue());
                }
            } else {
                hashCodeValue = (hashCodeValue ^ rdns[i].getFirst().getType().hashCode()) ^ calcHashCode(rdns[i].getFirst().getValue());
            }
        }
        return hashCodeValue;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public ASN1Encodable stringToValue(ASN1ObjectIdentifier oid, String value) {
        if (value.length() != 0 && value.charAt(0) == '#') {
            try {
                return IETFUtils.valueFromHexString(value, 1);
            } catch (IOException e) {
                throw new ASN1ParsingException("can't recode value for oid " + oid.getId());
            }
        }
        if (value.length() != 0 && value.charAt(0) == '\\') {
            value = value.substring(1);
        }
        return encodeStringValue(oid, value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Encodable encodeStringValue(ASN1ObjectIdentifier oid, String value) {
        return new DERUTF8String(value);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle
    public boolean areEqual(X500Name name1, X500Name name2) {
        RDN[] rdns1 = name1.getRDNs();
        RDN[] rdns2 = name2.getRDNs();
        if (rdns1.length != rdns2.length) {
            return false;
        }
        boolean reverse = false;
        if (rdns1[0].getFirst() != null && rdns2[0].getFirst() != null) {
            reverse = !rdns1[0].getFirst().getType().equals((ASN1Primitive) rdns2[0].getFirst().getType());
        }
        for (int i = 0; i != rdns1.length; i++) {
            if (!foundMatch(reverse, rdns1[i], rdns2)) {
                return false;
            }
        }
        return true;
    }

    private boolean foundMatch(boolean reverse, RDN rdn, RDN[] possRDNs) {
        if (reverse) {
            for (int i = possRDNs.length - 1; i >= 0; i--) {
                if (possRDNs[i] != null && rdnAreEqual(rdn, possRDNs[i])) {
                    possRDNs[i] = null;
                    return true;
                }
            }
            return false;
        }
        for (int i2 = 0; i2 != possRDNs.length; i2++) {
            if (possRDNs[i2] != null && rdnAreEqual(rdn, possRDNs[i2])) {
                possRDNs[i2] = null;
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rdnAreEqual(RDN rdn1, RDN rdn2) {
        return IETFUtils.rDNAreEqual(rdn1, rdn2);
    }
}
