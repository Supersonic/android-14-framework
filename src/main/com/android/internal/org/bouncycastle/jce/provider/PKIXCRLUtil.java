package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes4.dex */
abstract class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set findCRLs(PKIXCRLStoreSelector crlselect, Date validityDate, List certStores, List pkixCrlStores) throws AnnotatedException {
        X509Certificate cert;
        HashSet initialSet = new HashSet();
        try {
            findCRLs(initialSet, crlselect, pkixCrlStores);
            findCRLs(initialSet, crlselect, certStores);
            Set finalSet = new HashSet();
            Iterator it = initialSet.iterator();
            while (it.hasNext()) {
                X509CRL crl = (X509CRL) it.next();
                if (crl.getNextUpdate().after(validityDate) && ((cert = crlselect.getCertificateChecking()) == null || crl.getThisUpdate().before(cert.getNotAfter()))) {
                    finalSet.add(crl);
                }
            }
            return finalSet;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }

    private static void findCRLs(HashSet crls, PKIXCRLStoreSelector crlSelect, List crlStores) throws AnnotatedException {
        AnnotatedException lastException = null;
        boolean foundValidStore = false;
        for (Object obj : crlStores) {
            CertStore store = (CertStore) obj;
            try {
                crls.addAll(PKIXCRLStoreSelector.getCRLs(crlSelect, store));
                foundValidStore = true;
            } catch (CertStoreException e) {
                lastException = new AnnotatedException("Exception searching in X.509 CRL store.", e);
            }
        }
        if (!foundValidStore && lastException != null) {
            throw lastException;
        }
    }
}
