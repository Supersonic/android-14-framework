package com.android.internal.org.bouncycastle.jcajce.provider.util;

import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
/* loaded from: classes4.dex */
public interface AsymmetricKeyInfoConverter {
    PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException;

    PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException;
}
