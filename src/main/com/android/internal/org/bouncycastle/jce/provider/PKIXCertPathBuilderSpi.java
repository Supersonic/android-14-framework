package com.android.internal.org.bouncycastle.jce.provider;

import android.media.MediaMetrics;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertStore;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import com.android.internal.org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import com.android.internal.org.bouncycastle.x509.ExtendedPKIXParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes4.dex */
public class PKIXCertPathBuilderSpi extends CertPathBuilderSpi {
    private Exception certPathException;
    private final boolean isForCRLCheck;

    public PKIXCertPathBuilderSpi() {
        this(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PKIXCertPathBuilderSpi(boolean isForCRLCheck) {
        this.isForCRLCheck = isForCRLCheck;
    }

    @Override // java.security.cert.CertPathBuilderSpi
    public CertPathBuilderResult engineBuild(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters paramsPKIX;
        Exception exc;
        PKIXExtendedBuilderParameters.Builder paramsBldrPKIXBldr;
        if (params instanceof PKIXBuilderParameters) {
            PKIXExtendedParameters.Builder paramsPKIXBldr = new PKIXExtendedParameters.Builder((PKIXBuilderParameters) params);
            if (params instanceof ExtendedPKIXParameters) {
                ExtendedPKIXBuilderParameters extPKIX = (ExtendedPKIXBuilderParameters) params;
                for (PKIXCertStore pKIXCertStore : extPKIX.getAdditionalStores()) {
                    paramsPKIXBldr.addCertificateStore(pKIXCertStore);
                }
                paramsBldrPKIXBldr = new PKIXExtendedBuilderParameters.Builder(paramsPKIXBldr.build());
                paramsBldrPKIXBldr.addExcludedCerts(extPKIX.getExcludedCerts());
                paramsBldrPKIXBldr.setMaxPathLength(extPKIX.getMaxPathLength());
            } else {
                paramsBldrPKIXBldr = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters) params);
            }
            paramsPKIX = paramsBldrPKIXBldr.build();
        } else if (params instanceof PKIXExtendedBuilderParameters) {
            paramsPKIX = (PKIXExtendedBuilderParameters) params;
        } else {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + MediaMetrics.SEPARATOR);
        }
        List certPathList = new ArrayList();
        Collection targets = CertPathValidatorUtilities.findTargets(paramsPKIX);
        CertPathBuilderResult result = null;
        Iterator targetIter = targets.iterator();
        while (targetIter.hasNext() && result == null) {
            X509Certificate cert = (X509Certificate) targetIter.next();
            result = build(cert, paramsPKIX, certPathList);
        }
        if (result == null && (exc = this.certPathException) != null) {
            if (exc instanceof AnnotatedException) {
                throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause());
            }
            throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
        } else if (result == null && this.certPathException == null) {
            throw new CertPathBuilderException("Unable to find certificate chain.");
        } else {
            return result;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x00ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected CertPathBuilderResult build(X509Certificate tbvCert, PKIXExtendedBuilderParameters pkixParams, List tbvPath) {
        if (tbvPath.contains(tbvCert) || pkixParams.getExcludedCerts().contains(tbvCert)) {
            return null;
        }
        if (pkixParams.getMaxPathLength() == -1 || tbvPath.size() - 1 <= pkixParams.getMaxPathLength()) {
            tbvPath.add(tbvCert);
            CertPathBuilderResult builderResult = null;
            try {
                CertificateFactory cFact = new CertificateFactory();
                PKIXCertPathValidatorSpi validator = new PKIXCertPathValidatorSpi(this.isForCRLCheck);
                try {
                } catch (AnnotatedException e) {
                    this.certPathException = e;
                }
                if (CertPathValidatorUtilities.isIssuerTrustAnchor(tbvCert, pkixParams.getBaseParameters().getTrustAnchors(), pkixParams.getBaseParameters().getSigProvider())) {
                    try {
                        CertPath certPath = cFact.engineGenerateCertPath(tbvPath);
                        try {
                            PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) validator.engineValidate(certPath, pkixParams);
                            return new PKIXCertPathBuilderResult(certPath, result.getTrustAnchor(), result.getPolicyTree(), result.getPublicKey());
                        } catch (Exception e2) {
                            throw new AnnotatedException("Certification path could not be validated.", e2);
                        }
                    } catch (Exception e3) {
                        throw new AnnotatedException("Certification path could not be constructed from certificate list.", e3);
                    }
                }
                List stores = new ArrayList();
                stores.addAll(pkixParams.getBaseParameters().getCertificateStores());
                try {
                    stores.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(tbvCert.getExtensionValue(Extension.issuerAlternativeName.getId()), pkixParams.getBaseParameters().getNamedCertificateStoreMap()));
                    Collection issuers = new HashSet();
                    try {
                        issuers.addAll(CertPathValidatorUtilities.findIssuerCerts(tbvCert, pkixParams.getBaseParameters().getCertStores(), stores));
                        if (issuers.isEmpty()) {
                            throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
                        }
                        Iterator it = issuers.iterator();
                        while (it.hasNext() && builderResult == null) {
                            X509Certificate issuer = (X509Certificate) it.next();
                            builderResult = build(issuer, pkixParams, tbvPath);
                        }
                        if (builderResult == null) {
                            tbvPath.remove(tbvCert);
                        }
                        return builderResult;
                    } catch (AnnotatedException e4) {
                        throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", e4);
                    }
                } catch (CertificateParsingException e5) {
                    throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", e5);
                }
                this.certPathException = e;
                if (builderResult == null) {
                }
                return builderResult;
            } catch (Exception e6) {
                throw new RuntimeException("Exception creating support classes.");
            }
        }
        return null;
    }
}
