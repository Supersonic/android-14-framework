package com.android.internal.org.bouncycastle.x509;

import com.android.internal.org.bouncycastle.util.Selector;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CertSelector;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes4.dex */
public class ExtendedPKIXBuilderParameters extends ExtendedPKIXParameters {
    private Set excludedCerts;
    private int maxPathLength;

    public Set getExcludedCerts() {
        return Collections.unmodifiableSet(this.excludedCerts);
    }

    public void setExcludedCerts(Set excludedCerts) {
        if (excludedCerts == null) {
            Set excludedCerts2 = Collections.EMPTY_SET;
        } else {
            this.excludedCerts = new HashSet(excludedCerts);
        }
    }

    public ExtendedPKIXBuilderParameters(Set trustAnchors, Selector targetConstraints) throws InvalidAlgorithmParameterException {
        super(trustAnchors);
        this.maxPathLength = 5;
        this.excludedCerts = Collections.EMPTY_SET;
        setTargetConstraints(targetConstraints);
    }

    public void setMaxPathLength(int maxPathLength) {
        if (maxPathLength < -1) {
            throw new InvalidParameterException("The maximum path length parameter can not be less than -1.");
        }
        this.maxPathLength = maxPathLength;
    }

    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.org.bouncycastle.x509.ExtendedPKIXParameters
    public void setParams(PKIXParameters params) {
        super.setParams(params);
        if (params instanceof ExtendedPKIXBuilderParameters) {
            ExtendedPKIXBuilderParameters _params = (ExtendedPKIXBuilderParameters) params;
            this.maxPathLength = _params.maxPathLength;
            this.excludedCerts = new HashSet(_params.excludedCerts);
        }
        if (params instanceof PKIXBuilderParameters) {
            this.maxPathLength = ((PKIXBuilderParameters) params).getMaxPathLength();
        }
    }

    @Override // com.android.internal.org.bouncycastle.x509.ExtendedPKIXParameters, java.security.cert.PKIXParameters, java.security.cert.CertPathParameters
    public Object clone() {
        try {
            ExtendedPKIXBuilderParameters params = new ExtendedPKIXBuilderParameters(getTrustAnchors(), getTargetConstraints());
            params.setParams(this);
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static ExtendedPKIXParameters getInstance(PKIXParameters pkixParams) {
        try {
            ExtendedPKIXBuilderParameters params = new ExtendedPKIXBuilderParameters(pkixParams.getTrustAnchors(), X509CertStoreSelector.getInstance((X509CertSelector) pkixParams.getTargetCertConstraints()));
            params.setParams(pkixParams);
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
