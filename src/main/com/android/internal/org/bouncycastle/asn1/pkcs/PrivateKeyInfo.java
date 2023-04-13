package com.android.internal.org.bouncycastle.asn1.pkcs;

import com.android.internal.org.bouncycastle.asn1.ASN1BitString;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.io.IOException;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class PrivateKeyInfo extends ASN1Object {
    private ASN1Set attributes;
    private ASN1OctetString privateKey;
    private AlgorithmIdentifier privateKeyAlgorithm;
    private ASN1BitString publicKey;
    private ASN1Integer version;

    public static PrivateKeyInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static PrivateKeyInfo getInstance(Object obj) {
        if (obj instanceof PrivateKeyInfo) {
            return (PrivateKeyInfo) obj;
        }
        if (obj != null) {
            return new PrivateKeyInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private static int getVersionValue(ASN1Integer version) {
        int versionValue = version.intValueExact();
        if (versionValue < 0 || versionValue > 1) {
            throw new IllegalArgumentException("invalid version for private key info");
        }
        return versionValue;
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey) throws IOException {
        this(privateKeyAlgorithm, privateKey, null, null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey, ASN1Set attributes) throws IOException {
        this(privateKeyAlgorithm, privateKey, attributes, null);
    }

    public PrivateKeyInfo(AlgorithmIdentifier privateKeyAlgorithm, ASN1Encodable privateKey, ASN1Set attributes, byte[] publicKey) throws IOException {
        this.version = new ASN1Integer(publicKey != null ? BigIntegers.ONE : BigIntegers.ZERO);
        this.privateKeyAlgorithm = privateKeyAlgorithm;
        this.privateKey = new DEROctetString(privateKey);
        this.attributes = attributes;
        this.publicKey = publicKey == null ? null : new DERBitString(publicKey);
    }

    private PrivateKeyInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(e.nextElement());
        this.version = aSN1Integer;
        int versionValue = getVersionValue(aSN1Integer);
        this.privateKeyAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        this.privateKey = ASN1OctetString.getInstance(e.nextElement());
        int lastTag = -1;
        while (e.hasMoreElements()) {
            ASN1TaggedObject tagged = (ASN1TaggedObject) e.nextElement();
            int tag = tagged.getTagNo();
            if (tag <= lastTag) {
                throw new IllegalArgumentException("invalid optional field in private key info");
            }
            lastTag = tag;
            switch (tag) {
                case 0:
                    this.attributes = ASN1Set.getInstance(tagged, false);
                    break;
                case 1:
                    if (versionValue < 1) {
                        throw new IllegalArgumentException("'publicKey' requires version v2(1) or later");
                    }
                    this.publicKey = DERBitString.getInstance(tagged, false);
                    break;
                default:
                    throw new IllegalArgumentException("unknown optional field in private key info");
            }
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Set getAttributes() {
        return this.attributes;
    }

    public AlgorithmIdentifier getPrivateKeyAlgorithm() {
        return this.privateKeyAlgorithm;
    }

    public ASN1OctetString getPrivateKey() {
        return new DEROctetString(this.privateKey.getOctets());
    }

    public ASN1Encodable parsePrivateKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.privateKey.getOctets());
    }

    public boolean hasPublicKey() {
        return this.publicKey != null;
    }

    public ASN1Encodable parsePublicKey() throws IOException {
        ASN1BitString aSN1BitString = this.publicKey;
        if (aSN1BitString == null) {
            return null;
        }
        return ASN1Primitive.fromByteArray(aSN1BitString.getOctets());
    }

    public ASN1BitString getPublicKeyData() {
        return this.publicKey;
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Object, com.android.internal.org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.version);
        v.add(this.privateKeyAlgorithm);
        v.add(this.privateKey);
        ASN1Set aSN1Set = this.attributes;
        if (aSN1Set != null) {
            v.add(new DERTaggedObject(false, 0, aSN1Set));
        }
        ASN1BitString aSN1BitString = this.publicKey;
        if (aSN1BitString != null) {
            v.add(new DERTaggedObject(false, 1, aSN1BitString));
        }
        return new DERSequence(v);
    }
}
