package com.android.internal.org.bouncycastle.operator.p026bc;

import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.crypto.digests.SHA1Digest;
import com.android.internal.org.bouncycastle.crypto.digests.SHA224Digest;
import com.android.internal.org.bouncycastle.crypto.digests.SHA256Digest;
import com.android.internal.org.bouncycastle.crypto.digests.SHA384Digest;
import com.android.internal.org.bouncycastle.crypto.digests.SHA512Digest;
import com.android.internal.org.bouncycastle.operator.OperatorCreationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* renamed from: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider */
/* loaded from: classes4.dex */
public class BcDefaultDigestProvider implements BcDigestProvider {
    private static final Map lookup = createTable();
    public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

    private static Map createTable() {
        Map table = new HashMap();
        table.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider.1
            @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider.2
            @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider.3
            @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA256Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider.4
            @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider() { // from class: com.android.internal.org.bouncycastle.operator.bc.BcDefaultDigestProvider.5
            @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        return Collections.unmodifiableMap(table);
    }

    private BcDefaultDigestProvider() {
    }

    @Override // com.android.internal.org.bouncycastle.operator.p026bc.BcDigestProvider
    public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {
        BcDigestProvider extProv = (BcDigestProvider) lookup.get(digestAlgorithmIdentifier.getAlgorithm());
        if (extProv == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return extProv.get(digestAlgorithmIdentifier);
    }
}
