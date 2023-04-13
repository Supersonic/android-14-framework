package com.android.internal.org.bouncycastle.jcajce;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public class CompositePublicKey implements PublicKey {
    private final List<PublicKey> keys;

    public CompositePublicKey(PublicKey... keys) {
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException("at least one public key must be provided");
        }
        List<PublicKey> keyList = new ArrayList<>(keys.length);
        for (int i = 0; i != keys.length; i++) {
            keyList.add(keys[i]);
        }
        this.keys = Collections.unmodifiableList(keyList);
    }

    public List<PublicKey> getPublicKeys() {
        return this.keys;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "Composite";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "X.509";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.keys.size(); i++) {
            v.add(SubjectPublicKeyInfo.getInstance(this.keys.get(i).getEncoded()));
        }
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite), new DERSequence(v)).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode composite key: " + e.getMessage());
        }
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CompositePublicKey) {
            return this.keys.equals(((CompositePublicKey) o).keys);
        }
        return false;
    }
}
