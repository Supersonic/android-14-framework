package com.android.internal.org.bouncycastle.jcajce;

import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes4.dex */
public class CompositePrivateKey implements PrivateKey {
    private final List<PrivateKey> keys;

    public CompositePrivateKey(PrivateKey... keys) {
        if (keys == null || keys.length == 0) {
            throw new IllegalArgumentException("at least one public key must be provided");
        }
        List<PrivateKey> keyList = new ArrayList<>(keys.length);
        for (int i = 0; i != keys.length; i++) {
            keyList.add(keys[i]);
        }
        this.keys = Collections.unmodifiableList(keyList);
    }

    public List<PrivateKey> getPrivateKeys() {
        return this.keys;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "Composite";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.keys.size(); i++) {
            v.add(PrivateKeyInfo.getInstance(this.keys.get(i).getEncoded()));
        }
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite), new DERSequence(v)).getEncoded(ASN1Encoding.DER);
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
        if (o instanceof CompositePrivateKey) {
            return this.keys.equals(((CompositePrivateKey) o).keys);
        }
        return false;
    }
}
