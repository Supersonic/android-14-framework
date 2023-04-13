package com.android.internal.org.bouncycastle.cert.jcajce;

import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.util.CollectionStore;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* loaded from: classes4.dex */
public class JcaCertStore extends CollectionStore {
    public JcaCertStore(Collection collection) throws CertificateEncodingException {
        super(convertCerts(collection));
    }

    private static Collection convertCerts(Collection collection) throws CertificateEncodingException {
        List list = new ArrayList(collection.size());
        for (Object o : collection) {
            if (o instanceof X509Certificate) {
                X509Certificate cert = (X509Certificate) o;
                try {
                    list.add(new X509CertificateHolder(cert.getEncoded()));
                } catch (IOException e) {
                    throw new CertificateEncodingException("unable to read encoding: " + e.getMessage());
                }
            } else {
                list.add((X509CertificateHolder) o);
            }
        }
        return list;
    }
}
