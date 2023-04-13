package com.android.internal.org.bouncycastle.jce.provider;

import android.text.format.Time;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1String;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x500.RDN;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.style.BCStyle;
import com.android.internal.org.bouncycastle.asn1.x509.BasicConstraints;
import com.android.internal.org.bouncycastle.asn1.x509.CRLDistPoint;
import com.android.internal.org.bouncycastle.asn1.x509.DistributionPoint;
import com.android.internal.org.bouncycastle.asn1.x509.DistributionPointName;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralSubtree;
import com.android.internal.org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import com.android.internal.org.bouncycastle.asn1.x509.NameConstraints;
import com.android.internal.org.bouncycastle.asn1.x509.PolicyInformation;
import com.android.internal.org.bouncycastle.jcajce.PKIXCRLStore;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertStoreSelector;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class RFC3280CertPathUtilities {
    public static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int CRL_SIGN = 6;
    protected static final int KEY_CERT_SIGN = 5;
    private static final Class revChkClass = ClassUtil.loadClass(RFC3280CertPathUtilities.class, "java.security.cert.PKIXRevocationChecker");
    public static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    public static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    public static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    public static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    public static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    public static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    public static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    public static final String KEY_USAGE = Extension.keyUsage.getId();
    public static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    RFC3280CertPathUtilities() {
    }

    protected static void processCRLB2(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        try {
            IssuingDistributionPoint idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
            if (idp != null) {
                if (idp.getDistributionPoint() != null) {
                    DistributionPointName dpName = IssuingDistributionPoint.getInstance(idp).getDistributionPoint();
                    List names = new ArrayList();
                    if (dpName.getType() == 0) {
                        for (GeneralName generalName : GeneralNames.getInstance(dpName.getName()).getNames()) {
                            names.add(generalName);
                        }
                    }
                    if (dpName.getType() == 1) {
                        ASN1EncodableVector vec = new ASN1EncodableVector();
                        try {
                            Enumeration e = ASN1Sequence.getInstance(PrincipalUtils.getIssuerPrincipal(crl)).getObjects();
                            while (e.hasMoreElements()) {
                                vec.add((ASN1Encodable) e.nextElement());
                            }
                            vec.add(dpName.getName());
                            names.add(new GeneralName(X500Name.getInstance(new DERSequence(vec))));
                        } catch (Exception e2) {
                            throw new AnnotatedException("Could not read CRL issuer.", e2);
                        }
                    }
                    boolean matches = false;
                    if (dp.getDistributionPoint() != null) {
                        DistributionPointName dpName2 = dp.getDistributionPoint();
                        GeneralName[] genNames = null;
                        if (dpName2.getType() == 0) {
                            genNames = GeneralNames.getInstance(dpName2.getName()).getNames();
                        }
                        if (dpName2.getType() == 1) {
                            if (dp.getCRLIssuer() != null) {
                                genNames = dp.getCRLIssuer().getNames();
                            } else {
                                GeneralName[] genNames2 = new GeneralName[1];
                                try {
                                    genNames2[0] = new GeneralName(PrincipalUtils.getEncodedIssuerPrincipal(cert));
                                    genNames = genNames2;
                                } catch (Exception e3) {
                                    throw new AnnotatedException("Could not read certificate issuer.", e3);
                                }
                            }
                            for (int j = 0; j < genNames.length; j++) {
                                Enumeration e4 = ASN1Sequence.getInstance(genNames[j].getName().toASN1Primitive()).getObjects();
                                ASN1EncodableVector vec2 = new ASN1EncodableVector();
                                while (e4.hasMoreElements()) {
                                    vec2.add((ASN1Encodable) e4.nextElement());
                                }
                                vec2.add(dpName2.getName());
                                genNames[j] = new GeneralName(X500Name.getInstance(new DERSequence(vec2)));
                            }
                        }
                        if (genNames != null) {
                            int j2 = 0;
                            while (true) {
                                if (j2 < genNames.length) {
                                    if (!names.contains(genNames[j2])) {
                                        j2++;
                                    } else {
                                        matches = true;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                        if (!matches) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    } else if (dp.getCRLIssuer() == null) {
                        throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
                    } else {
                        GeneralName[] genNames3 = dp.getCRLIssuer().getNames();
                        int j3 = 0;
                        while (true) {
                            if (j3 < genNames3.length) {
                                if (!names.contains(genNames3[j3])) {
                                    j3++;
                                } else {
                                    matches = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!matches) {
                            throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
                        }
                    }
                }
                try {
                    BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue((X509Extension) cert, BASIC_CONSTRAINTS));
                    if (cert instanceof X509Certificate) {
                        if (idp.onlyContainsUserCerts() && bc != null && bc.isCA()) {
                            throw new AnnotatedException("CA Cert CRL only contains user certificates.");
                        }
                        if (idp.onlyContainsCACerts() && (bc == null || !bc.isCA())) {
                            throw new AnnotatedException("End CRL only contains CA certificates.");
                        }
                    }
                    if (idp.onlyContainsAttributeCerts()) {
                        throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
                    }
                } catch (Exception e5) {
                    throw new AnnotatedException("Basic constraints extension could not be decoded.", e5);
                }
            }
        } catch (Exception e6) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e6);
        }
    }

    protected static void processCRLB1(DistributionPoint dp, Object cert, X509CRL crl) throws AnnotatedException {
        ASN1Primitive idp = CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT);
        boolean isIndirect = false;
        if (idp != null && IssuingDistributionPoint.getInstance(idp).isIndirectCRL()) {
            isIndirect = true;
        }
        try {
            byte[] issuerBytes = PrincipalUtils.getIssuerPrincipal(crl).getEncoded();
            boolean matchIssuer = false;
            if (dp.getCRLIssuer() != null) {
                GeneralName[] genNames = dp.getCRLIssuer().getNames();
                for (int j = 0; j < genNames.length; j++) {
                    if (genNames[j].getTagNo() == 4) {
                        try {
                            if (Arrays.areEqual(genNames[j].getName().toASN1Primitive().getEncoded(), issuerBytes)) {
                                matchIssuer = true;
                            }
                        } catch (IOException e) {
                            throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                        }
                    }
                }
                if (matchIssuer && !isIndirect) {
                    throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
                }
                if (!matchIssuer) {
                    throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
                }
            } else if (PrincipalUtils.getIssuerPrincipal(crl).equals(PrincipalUtils.getEncodedIssuerPrincipal(cert))) {
                matchIssuer = true;
            }
            if (!matchIssuer) {
                throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
            }
        } catch (IOException e2) {
            throw new AnnotatedException("Exception encoding CRL issuer: " + e2.getMessage(), e2);
        }
    }

    protected static ReasonsMask processCRLD(X509CRL crl, DistributionPoint dp) throws AnnotatedException {
        ReasonsMask reasonsMask;
        ReasonsMask reasonsMask2;
        try {
            IssuingDistributionPoint idp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT));
            if (idp != null && idp.getOnlySomeReasons() != null && dp.getReasons() != null) {
                return new ReasonsMask(dp.getReasons()).intersect(new ReasonsMask(idp.getOnlySomeReasons()));
            }
            if ((idp == null || idp.getOnlySomeReasons() == null) && dp.getReasons() == null) {
                return ReasonsMask.allReasons;
            }
            if (dp.getReasons() == null) {
                reasonsMask = ReasonsMask.allReasons;
            } else {
                reasonsMask = new ReasonsMask(dp.getReasons());
            }
            if (idp == null) {
                reasonsMask2 = ReasonsMask.allReasons;
            } else {
                reasonsMask2 = new ReasonsMask(idp.getOnlySomeReasons());
            }
            return reasonsMask.intersect(reasonsMask2);
        } catch (Exception e) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e);
        }
    }

    protected static Set processCRLF(X509CRL crl, Object cert, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, PKIXExtendedParameters paramsPKIX, List certPathCerts, JcaJceHelper helper) throws AnnotatedException {
        CertPathBuilderSpi builder;
        X509CertSelector tmpCertSelector;
        PKIXExtendedParameters.Builder paramsBuilder;
        X509Certificate x509Certificate = defaultCRLSignCert;
        X509CertSelector certSelector = new X509CertSelector();
        try {
            byte[] issuerPrincipal = PrincipalUtils.getIssuerPrincipal(crl).getEncoded();
            certSelector.setSubject(issuerPrincipal);
            PKIXCertStoreSelector selector = new PKIXCertStoreSelector.Builder(certSelector).build();
            LinkedHashSet coll = new LinkedHashSet();
            try {
                CertPathValidatorUtilities.findCertificates(coll, selector, paramsPKIX.getCertificateStores());
                CertPathValidatorUtilities.findCertificates(coll, selector, paramsPKIX.getCertStores());
                coll.add(x509Certificate);
                Iterator cert_it = coll.iterator();
                List validCerts = new ArrayList();
                List validKeys = new ArrayList();
                while (cert_it.hasNext()) {
                    X509Certificate signingCert = (X509Certificate) cert_it.next();
                    if (signingCert.equals(x509Certificate)) {
                        validCerts.add(signingCert);
                        validKeys.add(defaultCRLSignKey);
                    } else {
                        try {
                            builder = new PKIXCertPathBuilderSpi(true);
                            tmpCertSelector = new X509CertSelector();
                            tmpCertSelector.setCertificate(signingCert);
                        } catch (CertPathBuilderException e) {
                            e = e;
                        } catch (CertPathValidatorException e2) {
                            e = e2;
                        } catch (Exception e3) {
                            e = e3;
                        }
                        try {
                            paramsBuilder = new PKIXExtendedParameters.Builder(paramsPKIX).setTargetConstraints(new PKIXCertStoreSelector.Builder(tmpCertSelector).build());
                        } catch (CertPathBuilderException e4) {
                            e = e4;
                            throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
                        } catch (CertPathValidatorException e5) {
                            e = e5;
                            throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e);
                        } catch (Exception e6) {
                            e = e6;
                            throw new AnnotatedException(e.getMessage());
                        }
                        try {
                            if (certPathCerts.contains(signingCert)) {
                                paramsBuilder.setRevocationEnabled(false);
                            } else {
                                paramsBuilder.setRevocationEnabled(true);
                            }
                            PKIXExtendedBuilderParameters extParams = new PKIXExtendedBuilderParameters.Builder(paramsBuilder.build()).build();
                            List certs = builder.engineBuild(extParams).getCertPath().getCertificates();
                            validCerts.add(signingCert);
                            try {
                                validKeys.add(CertPathValidatorUtilities.getNextWorkingKey(certs, 0, helper));
                                x509Certificate = defaultCRLSignCert;
                            } catch (CertPathBuilderException e7) {
                                e = e7;
                                throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
                            } catch (CertPathValidatorException e8) {
                                e = e8;
                                throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e);
                            } catch (Exception e9) {
                                e = e9;
                                throw new AnnotatedException(e.getMessage());
                            }
                        } catch (CertPathBuilderException e10) {
                            e = e10;
                            throw new AnnotatedException("CertPath for CRL signer failed to validate.", e);
                        } catch (CertPathValidatorException e11) {
                            e = e11;
                            throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", e);
                        } catch (Exception e12) {
                            e = e12;
                            throw new AnnotatedException(e.getMessage());
                        }
                    }
                }
                Set checkKeys = new HashSet();
                AnnotatedException lastException = null;
                for (int i = 0; i < validCerts.size(); i++) {
                    X509Certificate signCert = (X509Certificate) validCerts.get(i);
                    boolean[] keyUsage = signCert.getKeyUsage();
                    if (keyUsage == null || (keyUsage.length > 6 && keyUsage[6])) {
                        checkKeys.add(validKeys.get(i));
                    } else {
                        lastException = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
                    }
                }
                if (checkKeys.isEmpty() && lastException == null) {
                    throw new AnnotatedException("Cannot find a valid issuer certificate.");
                }
                if (!checkKeys.isEmpty() || lastException == null) {
                    return checkKeys;
                }
                throw lastException;
            } catch (AnnotatedException e13) {
                throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", e13);
            }
        } catch (IOException e14) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate for CRL could not be set.", e14);
        }
    }

    protected static PublicKey processCRLG(X509CRL crl, Set keys) throws AnnotatedException {
        Exception lastException = null;
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            PublicKey key = (PublicKey) it.next();
            try {
                crl.verify(key);
                return key;
            } catch (Exception e) {
                lastException = e;
            }
        }
        throw new AnnotatedException("Cannot verify CRL.", lastException);
    }

    protected static X509CRL processCRLH(Set deltacrls, PublicKey key) throws AnnotatedException {
        Exception lastException = null;
        Iterator it = deltacrls.iterator();
        while (it.hasNext()) {
            X509CRL crl = (X509CRL) it.next();
            try {
                crl.verify(key);
                return crl;
            } catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw new AnnotatedException("Cannot verify delta CRL.", lastException);
        }
        return null;
    }

    protected static void processCRLC(X509CRL deltaCRL, X509CRL completeCRL, PKIXExtendedParameters pkixParams) throws AnnotatedException {
        if (deltaCRL == null) {
            return;
        }
        if (deltaCRL.hasUnsupportedCriticalExtension()) {
            throw new AnnotatedException("delta CRL has unsupported critical extensions");
        }
        try {
            String str = ISSUING_DISTRIBUTION_POINT;
            IssuingDistributionPoint completeidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(completeCRL, str));
            if (pkixParams.isUseDeltasEnabled()) {
                if (!PrincipalUtils.getIssuerPrincipal(deltaCRL).equals(PrincipalUtils.getIssuerPrincipal(completeCRL))) {
                    throw new AnnotatedException("Complete CRL issuer does not match delta CRL issuer.");
                }
                try {
                    IssuingDistributionPoint deltaidp = IssuingDistributionPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(deltaCRL, str));
                    boolean match = false;
                    if (completeidp == null) {
                        if (deltaidp == null) {
                            match = true;
                        }
                    } else if (completeidp.equals(deltaidp)) {
                        match = true;
                    }
                    if (!match) {
                        throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
                    }
                    try {
                        String str2 = AUTHORITY_KEY_IDENTIFIER;
                        ASN1Primitive completeKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(completeCRL, str2);
                        try {
                            ASN1Primitive deltaKeyIdentifier = CertPathValidatorUtilities.getExtensionValue(deltaCRL, str2);
                            if (completeKeyIdentifier == null) {
                                throw new AnnotatedException("CRL authority key identifier is null.");
                            }
                            if (deltaKeyIdentifier == null) {
                                throw new AnnotatedException("Delta CRL authority key identifier is null.");
                            }
                            if (!completeKeyIdentifier.equals(deltaKeyIdentifier)) {
                                throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
                            }
                        } catch (AnnotatedException e) {
                            throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", e);
                        }
                    } catch (AnnotatedException e2) {
                        throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", e2);
                    }
                } catch (Exception e3) {
                    throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", e3);
                }
            }
        } catch (Exception e4) {
            throw new AnnotatedException("Issuing distribution point extension could not be decoded.", e4);
        }
    }

    protected static void processCRLI(Date validDate, X509CRL deltacrl, Object cert, CertStatus certStatus, PKIXExtendedParameters pkixParams) throws AnnotatedException {
        if (pkixParams.isUseDeltasEnabled() && deltacrl != null) {
            CertPathValidatorUtilities.getCertStatus(validDate, deltacrl, cert, certStatus);
        }
    }

    protected static void processCRLJ(Date validDate, X509CRL completecrl, Object cert, CertStatus certStatus) throws AnnotatedException {
        if (certStatus.getCertStatus() == 11) {
            CertPathValidatorUtilities.getCertStatus(validDate, completecrl, cert, certStatus);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00e0, code lost:
        r0 = (com.android.internal.org.bouncycastle.asn1.ASN1Sequence) com.android.internal.org.bouncycastle.jce.provider.CertPathValidatorUtilities.getExtensionValue(r5, com.android.internal.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00e8, code lost:
        r23 = r0.getObjects();
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00f3, code lost:
        if (r23.hasMoreElements() == false) goto L71;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00f6, code lost:
        r0 = com.android.internal.org.bouncycastle.asn1.x509.PolicyInformation.getInstance(r23.nextElement());
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x010c, code lost:
        if (com.android.internal.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.ANY_POLICY.equals(r0.getPolicyIdentifier().getId()) == false) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x010f, code lost:
        r0 = com.android.internal.org.bouncycastle.jce.provider.CertPathValidatorUtilities.getQualifierSet(r0.getPolicyQualifiers());
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0119, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0121, code lost:
        throw new com.android.internal.org.bouncycastle.jce.exception.ExtCertPathValidatorException("Policy qualifier info set could not be decoded.", r0, r31, r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0123, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x012b, code lost:
        throw new java.security.cert.CertPathValidatorException("Policy information could not be decoded.", r0, r31, r32);
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x012c, code lost:
        r0 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0132, code lost:
        if (r5.getCriticalExtensionOIDs() == null) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0134, code lost:
        r8 = r5.getCriticalExtensionOIDs().contains(com.android.internal.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
        r24 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x0141, code lost:
        r24 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0143, code lost:
        r10 = (com.android.internal.org.bouncycastle.jce.provider.PKIXPolicyNode) r21.getParent();
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x0152, code lost:
        if (com.android.internal.org.bouncycastle.jce.provider.RFC3280CertPathUtilities.ANY_POLICY.equals(r10.getValidPolicy()) == false) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0154, code lost:
        r28 = r12;
        r29 = r13;
        r30 = r14;
        r25 = new com.android.internal.org.bouncycastle.jce.provider.PKIXPolicyNode(new java.util.ArrayList(), r15, (java.util.Set) r13.get(r11), r10, r0, r11, r24);
        r10.addChild(r25);
        r33[r15].add(r25);
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x0187, code lost:
        r28 = r12;
        r29 = r13;
        r30 = r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0191, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x01a1, code lost:
        throw new com.android.internal.org.bouncycastle.jce.exception.ExtCertPathValidatorException("Certificate policies extension could not be decoded.", r0, r31, r32);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static PKIXPolicyNode prepareCertB(CertPath certPath, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, int policyMapping) throws CertPathValidatorException {
        Set s_idp;
        Map m_idp;
        ASN1Sequence mappings;
        int l;
        boolean idp_found;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int n = certs.size();
        int i = n - index;
        try {
            ASN1Sequence pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_MAPPINGS));
            if (pm == null) {
                return validPolicyTree;
            }
            ASN1Sequence mappings2 = pm;
            Map hashMap = new HashMap();
            Set<String> hashSet = new HashSet();
            for (int j = 0; j < mappings2.size(); j++) {
                ASN1Sequence mapping = (ASN1Sequence) mappings2.getObjectAt(j);
                String id_p = ((ASN1ObjectIdentifier) mapping.getObjectAt(0)).getId();
                String sd_p = ((ASN1ObjectIdentifier) mapping.getObjectAt(1)).getId();
                if (!hashMap.containsKey(id_p)) {
                    HashSet hashSet2 = new HashSet();
                    hashSet2.add(sd_p);
                    hashMap.put(id_p, hashSet2);
                    hashSet.add(id_p);
                } else {
                    Set tmp = (Set) hashMap.get(id_p);
                    tmp.add(sd_p);
                }
            }
            PKIXPolicyNode _validPolicyTree = validPolicyTree;
            for (String id_p2 : hashSet) {
                if (policyMapping > 0) {
                    Iterator nodes_i = policyNodes[i].iterator();
                    while (true) {
                        if (!nodes_i.hasNext()) {
                            idp_found = false;
                            break;
                        }
                        PKIXPolicyNode node = (PKIXPolicyNode) nodes_i.next();
                        if (node.getValidPolicy().equals(id_p2)) {
                            node.expectedPolicies = (Set) hashMap.get(id_p2);
                            idp_found = true;
                            break;
                        }
                    }
                    if (!idp_found) {
                        Iterator nodes_i2 = policyNodes[i].iterator();
                        while (true) {
                            if (!nodes_i2.hasNext()) {
                                s_idp = hashSet;
                                m_idp = hashMap;
                                mappings = mappings2;
                                break;
                            }
                            PKIXPolicyNode node2 = (PKIXPolicyNode) nodes_i2.next();
                            if (ANY_POLICY.equals(node2.getValidPolicy())) {
                                break;
                            }
                        }
                    } else {
                        s_idp = hashSet;
                        m_idp = hashMap;
                        mappings = mappings2;
                    }
                } else {
                    String id_p3 = id_p2;
                    s_idp = hashSet;
                    m_idp = hashMap;
                    mappings = mappings2;
                    if (policyMapping <= 0) {
                        Iterator nodes_i3 = policyNodes[i].iterator();
                        while (nodes_i3.hasNext()) {
                            PKIXPolicyNode node3 = (PKIXPolicyNode) nodes_i3.next();
                            String id_p4 = id_p3;
                            if (node3.getValidPolicy().equals(id_p4)) {
                                ((PKIXPolicyNode) node3.getParent()).removeChild(node3);
                                nodes_i3.remove();
                                for (int k = i - 1; k >= 0; k--) {
                                    List nodes = policyNodes[k];
                                    PKIXPolicyNode _validPolicyTree2 = _validPolicyTree;
                                    while (l < nodes.size()) {
                                        PKIXPolicyNode node22 = (PKIXPolicyNode) nodes.get(l);
                                        l = (node22.hasChildren() || (_validPolicyTree2 = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree2, policyNodes, node22)) != null) ? l + 1 : 0;
                                    }
                                    _validPolicyTree = _validPolicyTree2;
                                }
                            }
                            id_p3 = id_p4;
                        }
                    }
                }
                hashSet = s_idp;
                hashMap = m_idp;
                mappings2 = mappings;
            }
            return _validPolicyTree;
        } catch (AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void prepareNextCertA(CertPath certPath, int index) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Sequence pm = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_MAPPINGS));
            if (pm != null) {
                for (int j = 0; j < pm.size(); j++) {
                    try {
                        ASN1Sequence mapping = ASN1Sequence.getInstance(pm.getObjectAt(j));
                        ASN1ObjectIdentifier issuerDomainPolicy = ASN1ObjectIdentifier.getInstance(mapping.getObjectAt(0));
                        ASN1ObjectIdentifier subjectDomainPolicy = ASN1ObjectIdentifier.getInstance(mapping.getObjectAt(1));
                        if (ANY_POLICY.equals(issuerDomainPolicy.getId())) {
                            throw new CertPathValidatorException("IssuerDomainPolicy is anyPolicy", null, certPath, index);
                        }
                        if (ANY_POLICY.equals(subjectDomainPolicy.getId())) {
                            throw new CertPathValidatorException("SubjectDomainPolicy is anyPolicy", null, certPath, index);
                        }
                    } catch (Exception e) {
                        throw new ExtCertPathValidatorException("Policy mappings extension contents could not be decoded.", e, certPath, index);
                    }
                }
            }
        } catch (AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Policy mappings extension could not be decoded.", ex, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void processCertF(CertPath certPath, int index, PKIXPolicyNode validPolicyTree, int explicitPolicy) throws CertPathValidatorException {
        if (explicitPolicy <= 0 && validPolicyTree == null) {
            throw new ExtCertPathValidatorException("No valid policy tree found when one expected.", null, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static PKIXPolicyNode processCertE(CertPath certPath, int index, PKIXPolicyNode validPolicyTree) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Sequence certPolicies = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
            if (certPolicies == null) {
                return null;
            }
            return validPolicyTree;
        } catch (AnnotatedException e) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void processCertBC(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator, boolean isForCRLCheck) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int n = certs.size();
        int i = n - index;
        if (!CertPathValidatorUtilities.isSelfIssued(cert) || (i >= n && !isForCRLCheck)) {
            X500Name principal = PrincipalUtils.getSubjectPrincipal(cert);
            try {
                ASN1Sequence dns = ASN1Sequence.getInstance(principal);
                try {
                    nameConstraintValidator.checkPermittedDN(dns);
                    nameConstraintValidator.checkExcludedDN(dns);
                    try {
                        GeneralNames altName = GeneralNames.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, SUBJECT_ALTERNATIVE_NAME));
                        RDN[] emails = X500Name.getInstance(dns).getRDNs(BCStyle.EmailAddress);
                        for (int eI = 0; eI != emails.length; eI++) {
                            String email = ((ASN1String) emails[eI].getFirst().getValue()).getString();
                            GeneralName emailAsGeneralName = new GeneralName(1, email);
                            try {
                                nameConstraintValidator.checkPermitted(emailAsGeneralName);
                                nameConstraintValidator.checkExcluded(emailAsGeneralName);
                            } catch (PKIXNameConstraintValidatorException ex) {
                                throw new CertPathValidatorException("Subtree check for certificate subject alternative email failed.", ex, certPath, index);
                            }
                        }
                        if (altName != null) {
                            try {
                                GeneralName[] genNames = altName.getNames();
                                for (int j = 0; j < genNames.length; j++) {
                                    try {
                                        nameConstraintValidator.checkPermitted(genNames[j]);
                                        nameConstraintValidator.checkExcluded(genNames[j]);
                                    } catch (PKIXNameConstraintValidatorException e) {
                                        throw new CertPathValidatorException("Subtree check for certificate subject alternative name failed.", e, certPath, index);
                                    }
                                }
                            } catch (Exception e2) {
                                throw new CertPathValidatorException("Subject alternative name contents could not be decoded.", e2, certPath, index);
                            }
                        }
                    } catch (Exception e3) {
                        throw new CertPathValidatorException("Subject alternative name extension could not be decoded.", e3, certPath, index);
                    }
                } catch (PKIXNameConstraintValidatorException e4) {
                    throw new CertPathValidatorException("Subtree check for certificate subject failed.", e4, certPath, index);
                }
            } catch (Exception e5) {
                throw new CertPathValidatorException("Exception extracting subject name when checking subtrees.", e5, certPath, index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Incorrect condition in loop: B:61:0x012f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static PKIXPolicyNode processCertD(CertPath certPath, int index, Set acceptablePolicies, PKIXPolicyNode validPolicyTree, List[] policyNodes, int inhibitAnyPolicy, boolean isForCRLCheck) throws CertPathValidatorException {
        Enumeration e;
        int i;
        String _policy;
        PKIXPolicyNode _node;
        int k;
        List _nodes;
        HashSet hashSet;
        int i2;
        int k2;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        int n = certs.size();
        int i3 = n - index;
        try {
            ASN1Sequence certPolicies = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CERTIFICATE_POLICIES));
            if (certPolicies != null && validPolicyTree != null) {
                Enumeration e2 = certPolicies.getObjects();
                HashSet hashSet2 = new HashSet();
                while (e2.hasMoreElements()) {
                    PolicyInformation pInfo = PolicyInformation.getInstance(e2.nextElement());
                    ASN1ObjectIdentifier pOid = pInfo.getPolicyIdentifier();
                    hashSet2.add(pOid.getId());
                    if (!ANY_POLICY.equals(pOid.getId())) {
                        try {
                            Set pq = CertPathValidatorUtilities.getQualifierSet(pInfo.getPolicyQualifiers());
                            boolean match = CertPathValidatorUtilities.processCertD1i(i3, policyNodes, pOid, pq);
                            if (!match) {
                                CertPathValidatorUtilities.processCertD1ii(i3, policyNodes, pOid, pq);
                            }
                        } catch (CertPathValidatorException ex) {
                            throw new ExtCertPathValidatorException("Policy qualifier info set could not be build.", ex, certPath, index);
                        }
                    }
                }
                if (acceptablePolicies.isEmpty() || acceptablePolicies.contains(ANY_POLICY)) {
                    acceptablePolicies.clear();
                    acceptablePolicies.addAll(hashSet2);
                } else {
                    HashSet hashSet3 = new HashSet();
                    for (Object o : acceptablePolicies) {
                        if (hashSet2.contains(o)) {
                            hashSet3.add(o);
                        }
                    }
                    acceptablePolicies.clear();
                    acceptablePolicies.addAll(hashSet3);
                }
                if (inhibitAnyPolicy > 0 || ((i3 < n || isForCRLCheck) && CertPathValidatorUtilities.isSelfIssued(cert))) {
                    Enumeration e3 = certPolicies.getObjects();
                    while (true) {
                        if (!e3.hasMoreElements()) {
                            e = e3;
                            i = i3;
                            break;
                        }
                        PolicyInformation pInfo2 = PolicyInformation.getInstance(e3.nextElement());
                        if (ANY_POLICY.equals(pInfo2.getPolicyIdentifier().getId())) {
                            Set _apq = CertPathValidatorUtilities.getQualifierSet(pInfo2.getPolicyQualifiers());
                            List _nodes2 = policyNodes[i3 - 1];
                            int k3 = 0;
                            while (k3 < _nodes2.size()) {
                                PKIXPolicyNode _node2 = (PKIXPolicyNode) _nodes2.get(k3);
                                for (Object _tmp : _node2.getExpectedPolicies()) {
                                    if (_tmp instanceof String) {
                                        _policy = (String) _tmp;
                                    } else if (_tmp instanceof ASN1ObjectIdentifier) {
                                        _policy = ((ASN1ObjectIdentifier) _tmp).getId();
                                    }
                                    Iterator _childrenIter = _node2.getChildren();
                                    boolean _found = false;
                                    while (_found) {
                                        PKIXPolicyNode _child = (PKIXPolicyNode) _childrenIter.next();
                                        Enumeration e4 = e3;
                                        if (_policy.equals(_child.getValidPolicy())) {
                                            _found = true;
                                        }
                                        e3 = e4;
                                    }
                                    Enumeration e5 = e3;
                                    if (_found) {
                                        _node = _node2;
                                        k = k3;
                                        _nodes = _nodes2;
                                        hashSet = hashSet2;
                                        i2 = i3;
                                    } else {
                                        Set _newChildExpectedPolicies = new HashSet();
                                        _newChildExpectedPolicies.add(_policy);
                                        PKIXPolicyNode _node3 = _node2;
                                        k = k3;
                                        _nodes = _nodes2;
                                        hashSet = hashSet2;
                                        i2 = i3;
                                        PKIXPolicyNode _newChild = new PKIXPolicyNode(new ArrayList(), i3, _newChildExpectedPolicies, _node3, _apq, _policy, false);
                                        _node = _node3;
                                        _node.addChild(_newChild);
                                        policyNodes[i2].add(_newChild);
                                    }
                                    _node2 = _node;
                                    e3 = e5;
                                    k3 = k;
                                    _nodes2 = _nodes;
                                    hashSet2 = hashSet;
                                    i3 = i2;
                                }
                                k3++;
                            }
                            e = e3;
                            i = i3;
                        }
                    }
                } else {
                    i = i3;
                }
                PKIXPolicyNode _validPolicyTree = validPolicyTree;
                for (int j = i - 1; j >= 0; j--) {
                    List nodes = policyNodes[j];
                    while (k2 < nodes.size()) {
                        PKIXPolicyNode node = (PKIXPolicyNode) nodes.get(k2);
                        k2 = (node.hasChildren() || (_validPolicyTree = CertPathValidatorUtilities.removePolicyNode(_validPolicyTree, policyNodes, node)) != null) ? k2 + 1 : 0;
                    }
                }
                Set criticalExtensionOids = cert.getCriticalExtensionOIDs();
                if (criticalExtensionOids != null) {
                    boolean critical = criticalExtensionOids.contains(CERTIFICATE_POLICIES);
                    List nodes2 = policyNodes[i];
                    for (int j2 = 0; j2 < nodes2.size(); j2++) {
                        ((PKIXPolicyNode) nodes2.get(j2)).setCritical(critical);
                    }
                }
                return _validPolicyTree;
            }
            return null;
        } catch (AnnotatedException e6) {
            throw new ExtCertPathValidatorException("Could not read certificate policies extension from certificate.", e6, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void processCertA(CertPath certPath, PKIXExtendedParameters paramsPKIX, Date validityDate, PKIXCertRevocationChecker revocationChecker, int index, PublicKey workingPublicKey, boolean verificationAlreadyPerformed, X500Name workingIssuerName, X509Certificate sign) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!verificationAlreadyPerformed) {
            try {
            } catch (GeneralSecurityException e) {
                e = e;
            }
            try {
                CertPathValidatorUtilities.verifyX509Certificate(cert, workingPublicKey, paramsPKIX.getSigProvider());
            } catch (GeneralSecurityException e2) {
                e = e2;
                throw new ExtCertPathValidatorException("Could not validate certificate signature.", e, certPath, index);
            }
        }
        try {
        } catch (AnnotatedException e3) {
            e = e3;
        }
        try {
            Date validCertDate = CertPathValidatorUtilities.getValidCertDateFromValidityModel(validityDate, paramsPKIX.getValidityModel(), certPath, index);
            try {
                cert.checkValidity(validCertDate);
                if (revocationChecker != null) {
                    revocationChecker.initialize(new PKIXCertRevocationCheckerParameters(paramsPKIX, validCertDate, certPath, index, sign, workingPublicKey));
                    revocationChecker.check(cert);
                }
                X500Name issuer = PrincipalUtils.getIssuerPrincipal(cert);
                if (!issuer.equals(workingIssuerName)) {
                    throw new ExtCertPathValidatorException("IssuerName(" + issuer + ") does not match SubjectName(" + workingIssuerName + ") of signing certificate.", null, certPath, index);
                }
            } catch (CertificateExpiredException e4) {
                throw new ExtCertPathValidatorException("Could not validate certificate: " + e4.getMessage(), e4, certPath, index);
            } catch (CertificateNotYetValidException e5) {
                throw new ExtCertPathValidatorException("Could not validate certificate: " + e5.getMessage(), e5, certPath, index);
            }
        } catch (AnnotatedException e6) {
            e = e6;
            throw new ExtCertPathValidatorException("Could not validate time of certificate.", e, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0031, code lost:
        r5 = com.android.internal.org.bouncycastle.asn1.ASN1Integer.getInstance(r4, false).intValueExact();
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003a, code lost:
        if (r5 >= r9) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003c, code lost:
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int prepareNextCertI1(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
            if (pc != null) {
                Enumeration policyConstraints = pc.getObjects();
                while (true) {
                    if (!policyConstraints.hasMoreElements()) {
                        break;
                    }
                    try {
                        ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                        if (constraint.getTagNo() == 0) {
                            break;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, index);
                    }
                }
            }
            return explicitPolicy;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0032, code lost:
        r5 = com.android.internal.org.bouncycastle.asn1.ASN1Integer.getInstance(r4, false).intValueExact();
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x003b, code lost:
        if (r5 >= r9) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x003d, code lost:
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int prepareNextCertI2(CertPath certPath, int index, int policyMapping) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
            if (pc != null) {
                Enumeration policyConstraints = pc.getObjects();
                while (true) {
                    if (!policyConstraints.hasMoreElements()) {
                        break;
                    }
                    try {
                        ASN1TaggedObject constraint = ASN1TaggedObject.getInstance(policyConstraints.nextElement());
                        if (constraint.getTagNo() == 1) {
                            break;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new ExtCertPathValidatorException("Policy constraints extension contents cannot be decoded.", e, certPath, index);
                    }
                }
            }
            return policyMapping;
        } catch (Exception e2) {
            throw new ExtCertPathValidatorException("Policy constraints extension cannot be decoded.", e2, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void prepareNextCertG(CertPath certPath, int index, PKIXNameConstraintValidator nameConstraintValidator) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        NameConstraints nc = null;
        try {
            ASN1Sequence ncSeq = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, NAME_CONSTRAINTS));
            if (ncSeq != null) {
                nc = NameConstraints.getInstance(ncSeq);
            }
            if (nc != null) {
                GeneralSubtree[] permitted = nc.getPermittedSubtrees();
                if (permitted != null) {
                    try {
                        nameConstraintValidator.intersectPermittedSubtree(permitted);
                    } catch (Exception ex) {
                        throw new ExtCertPathValidatorException("Permitted subtrees cannot be build from name constraints extension.", ex, certPath, index);
                    }
                }
                GeneralSubtree[] excluded = nc.getExcludedSubtrees();
                if (excluded != null) {
                    for (int i = 0; i != excluded.length; i++) {
                        try {
                            nameConstraintValidator.addExcludedSubtree(excluded[i]);
                        } catch (Exception ex2) {
                            throw new ExtCertPathValidatorException("Excluded subtrees cannot be build from name constraints extension.", ex2, certPath, index);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Name constraints extension could not be decoded.", e, certPath, index);
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:6:0x002d */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static void checkCRL(PKIXCertRevocationCheckerParameters params, DistributionPoint dp, PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate defaultCRLSignCert, PublicKey defaultCRLSignKey, CertStatus certStatus, ReasonsMask reasonMask, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, RecoverableCertPathValidatorException {
        Set crls;
        X509CRL crl;
        ReasonsMask interimReasonsMask;
        Set criticalExtensions;
        if (validityDate.getTime() > currentDate.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
        }
        Set crls2 = CertPathValidatorUtilities.getCompleteCRLs(params, dp, cert, paramsPKIX, validityDate);
        Iterator crl_iter = crls2.iterator();
        boolean validCrlFound = false;
        AnnotatedException lastException = null;
        while (validCrlFound && certStatus.getCertStatus() == 11 && !reasonMask.isAllReasons()) {
            try {
                crl = (X509CRL) crl_iter.next();
                interimReasonsMask = processCRLD(crl, dp);
            } catch (AnnotatedException e) {
                e = e;
                crls = crls2;
            }
            if (interimReasonsMask.hasNewReasons(reasonMask)) {
                crls = crls2;
                try {
                    Set keys = processCRLF(crl, cert, defaultCRLSignCert, defaultCRLSignKey, paramsPKIX, certPathCerts, helper);
                    PublicKey key = processCRLG(crl, keys);
                    X509CRL deltaCRL = null;
                    if (paramsPKIX.isUseDeltasEnabled()) {
                        try {
                            Set deltaCRLs = CertPathValidatorUtilities.getDeltaCRLs(validityDate, crl, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores(), helper);
                            deltaCRL = processCRLH(deltaCRLs, key);
                        } catch (AnnotatedException e2) {
                            e = e2;
                            lastException = e;
                            crls2 = crls;
                        }
                    }
                    if (paramsPKIX.getValidityModel() != 1 && cert.getNotAfter().getTime() < crl.getThisUpdate().getTime()) {
                        throw new AnnotatedException("No valid CRL for current time found.");
                    }
                    processCRLB1(dp, cert, crl);
                    processCRLB2(dp, cert, crl);
                    processCRLC(deltaCRL, crl, paramsPKIX);
                    processCRLI(validityDate, deltaCRL, cert, certStatus, paramsPKIX);
                    processCRLJ(validityDate, crl, cert, certStatus);
                    if (certStatus.getCertStatus() == 8) {
                        certStatus.setCertStatus(11);
                    }
                    reasonMask.addReasons(interimReasonsMask);
                    Set criticalExtensions2 = crl.getCriticalExtensionOIDs();
                    if (criticalExtensions2 != null) {
                        Set criticalExtensions3 = new HashSet(criticalExtensions2);
                        criticalExtensions3.remove(Extension.issuingDistributionPoint.getId());
                        criticalExtensions3.remove(Extension.deltaCRLIndicator.getId());
                        if (!criticalExtensions3.isEmpty()) {
                            throw new AnnotatedException("CRL contains unsupported critical extensions.");
                        }
                    }
                    if (deltaCRL != null && (criticalExtensions = deltaCRL.getCriticalExtensionOIDs()) != null) {
                        Set criticalExtensions4 = new HashSet(criticalExtensions);
                        criticalExtensions4.remove(Extension.issuingDistributionPoint.getId());
                        criticalExtensions4.remove(Extension.deltaCRLIndicator.getId());
                        if (!criticalExtensions4.isEmpty()) {
                            throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                        }
                    }
                    validCrlFound = true;
                } catch (AnnotatedException e3) {
                    e = e3;
                }
                crls2 = crls;
            } else {
                continue;
            }
        }
        if (!validCrlFound) {
            throw lastException;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void checkCRLs(PKIXCertRevocationCheckerParameters params, PKIXExtendedParameters paramsPKIX, Date currentDate, Date validityDate, X509Certificate cert, X509Certificate sign, PublicKey workingPublicKey, List certPathCerts, JcaJceHelper helper) throws AnnotatedException, RecoverableCertPathValidatorException {
        int i;
        CertStatus certStatus;
        CertStatus certStatus2;
        CRLDistPoint crldp;
        DistributionPoint[] dps;
        int i2;
        CertStatus certStatus3;
        PKIXExtendedParameters.Builder paramsBldr;
        AnnotatedException lastException = null;
        try {
            CRLDistPoint crldp2 = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, CRL_DISTRIBUTION_POINTS));
            PKIXExtendedParameters.Builder paramsBldr2 = new PKIXExtendedParameters.Builder(paramsPKIX);
            try {
                List<PKIXCRLStore> extras = CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(crldp2, paramsPKIX.getNamedCRLStoreMap(), validityDate, helper);
                for (PKIXCRLStore pKIXCRLStore : extras) {
                    try {
                        paramsBldr2.addCRLStore(pKIXCRLStore);
                    } catch (AnnotatedException e) {
                        e = e;
                        throw new AnnotatedException("No additional CRL locations could be decoded from CRL distribution point extension.", e);
                    }
                }
                CertStatus certStatus4 = new CertStatus();
                ReasonsMask reasonsMask = new ReasonsMask();
                PKIXExtendedParameters finalParams = paramsBldr2.build();
                boolean validCrlFound = false;
                int i3 = 11;
                if (crldp2 == null) {
                    i = 11;
                    certStatus = certStatus4;
                } else {
                    try {
                        DistributionPoint[] dps2 = crldp2.getDistributionPoints();
                        if (dps2 != null) {
                            AnnotatedException lastException2 = null;
                            boolean validCrlFound2 = false;
                            int i4 = 0;
                            while (i4 < dps2.length && certStatus4.getCertStatus() == i3 && !reasonsMask.isAllReasons()) {
                                try {
                                    dps = dps2;
                                    crldp = crldp2;
                                    i2 = i3;
                                    certStatus3 = certStatus4;
                                    paramsBldr = paramsBldr2;
                                } catch (AnnotatedException e2) {
                                    e = e2;
                                    crldp = crldp2;
                                    dps = dps2;
                                    i2 = i3;
                                    certStatus3 = certStatus4;
                                    paramsBldr = paramsBldr2;
                                }
                                try {
                                    checkCRL(params, dps2[i4], finalParams, currentDate, validityDate, cert, sign, workingPublicKey, certStatus3, reasonsMask, certPathCerts, helper);
                                    validCrlFound2 = true;
                                } catch (AnnotatedException e3) {
                                    e = e3;
                                    lastException2 = e;
                                    i4++;
                                    i3 = i2;
                                    dps2 = dps;
                                    crldp2 = crldp;
                                    certStatus4 = certStatus3;
                                    paramsBldr2 = paramsBldr;
                                }
                                i4++;
                                i3 = i2;
                                dps2 = dps;
                                crldp2 = crldp;
                                certStatus4 = certStatus3;
                                paramsBldr2 = paramsBldr;
                            }
                            i = i3;
                            certStatus = certStatus4;
                            lastException = lastException2;
                            validCrlFound = validCrlFound2;
                        } else {
                            i = 11;
                            certStatus = certStatus4;
                        }
                    } catch (Exception e4) {
                        throw new AnnotatedException("Distribution points could not be read.", e4);
                    }
                }
                if (certStatus.getCertStatus() == i && !reasonsMask.isAllReasons()) {
                    try {
                        try {
                            X500Name issuer = PrincipalUtils.getIssuerPrincipal(cert);
                            DistributionPoint dp = new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, issuer))), null, null);
                            PKIXExtendedParameters paramsPKIXClone = (PKIXExtendedParameters) paramsPKIX.clone();
                            checkCRL(params, dp, paramsPKIXClone, currentDate, validityDate, cert, sign, workingPublicKey, certStatus, reasonsMask, certPathCerts, helper);
                            validCrlFound = true;
                        } catch (RuntimeException e5) {
                            throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", e5);
                        }
                    } catch (AnnotatedException e6) {
                        lastException = e6;
                    }
                }
                if (!validCrlFound) {
                    if (lastException instanceof AnnotatedException) {
                        throw lastException;
                    }
                    throw new AnnotatedException("No valid CRL found.", lastException);
                } else if (certStatus.getCertStatus() != i) {
                    CertStatus certStatus5 = certStatus;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                    df.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
                    String message = "Certificate revocation after " + df.format(certStatus5.getRevocationDate());
                    throw new AnnotatedException(message + ", reason: " + crlReasons[certStatus5.getCertStatus()]);
                } else {
                    if (reasonsMask.isAllReasons() || certStatus.getCertStatus() != i) {
                        certStatus2 = certStatus;
                    } else {
                        certStatus2 = certStatus;
                        certStatus2.setCertStatus(12);
                    }
                    if (certStatus2.getCertStatus() == 12) {
                        throw new AnnotatedException("Certificate status could not be determined.");
                    }
                }
            } catch (AnnotatedException e7) {
                e = e7;
            }
        } catch (Exception e8) {
            throw new AnnotatedException("CRL distribution point extension could not be read.", e8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertJ(CertPath certPath, int index, int inhibitAnyPolicy) throws CertPathValidatorException {
        int _inhibitAnyPolicy;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Integer iap = ASN1Integer.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, INHIBIT_ANY_POLICY));
            if (iap != null && (_inhibitAnyPolicy = iap.intValueExact()) < inhibitAnyPolicy) {
                return _inhibitAnyPolicy;
            }
            return inhibitAnyPolicy;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Inhibit any-policy extension cannot be decoded.", e, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void prepareNextCertK(CertPath certPath, int index) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, BASIC_CONSTRAINTS));
            if (bc != null) {
                if (!bc.isCA()) {
                    throw new CertPathValidatorException("Not a CA certificate", null, certPath, index);
                }
                return;
            }
            throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints", null, certPath, index);
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertL(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert)) {
            if (maxPathLength <= 0) {
                throw new ExtCertPathValidatorException("Max path length not greater than zero", null, certPath, index);
            }
            return maxPathLength - 1;
        }
        return maxPathLength;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertM(CertPath certPath, int index, int maxPathLength) throws CertPathValidatorException {
        BigInteger _pathLengthConstraint;
        int _plc;
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            BasicConstraints bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, BASIC_CONSTRAINTS));
            if (bc != null && (_pathLengthConstraint = bc.getPathLenConstraint()) != null && (_plc = _pathLengthConstraint.intValue()) < maxPathLength) {
                return _plc;
            }
            return maxPathLength;
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void prepareNextCertN(CertPath certPath, int index) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        boolean[] keyUsage = cert.getKeyUsage();
        if (keyUsage != null) {
            if (keyUsage.length <= 5 || !keyUsage[5]) {
                throw new ExtCertPathValidatorException("Issuer certificate keyusage extension is critical and does not permit key signing.", null, certPath, index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void prepareNextCertO(CertPath certPath, int index, Set criticalExtensions, List pathCheckers) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        Iterator tmpIter = pathCheckers.iterator();
        while (tmpIter.hasNext()) {
            try {
                ((PKIXCertPathChecker) tmpIter.next()).check(cert, criticalExtensions);
            } catch (CertPathValidatorException e) {
                throw new CertPathValidatorException(e.getMessage(), e.getCause(), certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertH1(CertPath certPath, int index, int explicitPolicy) {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && explicitPolicy != 0) {
            return explicitPolicy - 1;
        }
        return explicitPolicy;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertH2(CertPath certPath, int index, int policyMapping) {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && policyMapping != 0) {
            return policyMapping - 1;
        }
        return policyMapping;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int prepareNextCertH3(CertPath certPath, int index, int inhibitAnyPolicy) {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && inhibitAnyPolicy != 0) {
            return inhibitAnyPolicy - 1;
        }
        return inhibitAnyPolicy;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int wrapupCertA(int explicitPolicy, X509Certificate cert) {
        if (!CertPathValidatorUtilities.isSelfIssued(cert) && explicitPolicy != 0) {
            return explicitPolicy - 1;
        }
        return explicitPolicy;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int wrapupCertB(CertPath certPath, int index, int explicitPolicy) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        try {
            ASN1Sequence pc = ASN1Sequence.getInstance(CertPathValidatorUtilities.getExtensionValue(cert, POLICY_CONSTRAINTS));
            if (pc != null) {
                Enumeration policyConstraints = pc.getObjects();
                while (policyConstraints.hasMoreElements()) {
                    ASN1TaggedObject constraint = (ASN1TaggedObject) policyConstraints.nextElement();
                    switch (constraint.getTagNo()) {
                        case 0:
                            try {
                                int tmpInt = ASN1Integer.getInstance(constraint, false).intValueExact();
                                if (tmpInt != 0) {
                                    break;
                                } else {
                                    return 0;
                                }
                            } catch (Exception e) {
                                throw new ExtCertPathValidatorException("Policy constraints requireExplicitPolicy field could not be decoded.", e, certPath, index);
                            }
                    }
                }
            }
            return explicitPolicy;
        } catch (AnnotatedException e2) {
            throw new ExtCertPathValidatorException("Policy constraints could not be decoded.", e2, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void wrapupCertF(CertPath certPath, int index, List pathCheckers, Set criticalExtensions) throws CertPathValidatorException {
        List certs = certPath.getCertificates();
        X509Certificate cert = (X509Certificate) certs.get(index);
        Iterator tmpIter = pathCheckers.iterator();
        while (tmpIter.hasNext()) {
            try {
                ((PKIXCertPathChecker) tmpIter.next()).check(cert, criticalExtensions);
            } catch (CertPathValidatorException e) {
                throw new ExtCertPathValidatorException(e.getMessage(), e, certPath, index);
            } catch (Exception e2) {
                throw new CertPathValidatorException("Additional certificate path checker failed.", e2, certPath, index);
            }
        }
        if (!criticalExtensions.isEmpty()) {
            throw new ExtCertPathValidatorException("Certificate has unsupported critical extension: " + criticalExtensions, null, certPath, index);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static PKIXPolicyNode wrapupCertG(CertPath certPath, PKIXExtendedParameters paramsPKIX, Set userInitialPolicySet, int index, List[] policyNodes, PKIXPolicyNode validPolicyTree, Set acceptablePolicies) throws CertPathValidatorException {
        PKIXPolicyNode validPolicyTree2;
        int n = certPath.getCertificates().size();
        if (validPolicyTree == null) {
            if (paramsPKIX.isExplicitPolicyRequired()) {
                throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
            }
            return null;
        } else if (CertPathValidatorUtilities.isAnyPolicy(userInitialPolicySet)) {
            if (paramsPKIX.isExplicitPolicyRequired()) {
                if (acceptablePolicies.isEmpty()) {
                    throw new ExtCertPathValidatorException("Explicit policy requested but none available.", null, certPath, index);
                }
                Set<PKIXPolicyNode> _validPolicyNodeSet = new HashSet();
                for (List _nodeDepth : policyNodes) {
                    for (int k = 0; k < _nodeDepth.size(); k++) {
                        PKIXPolicyNode _node = (PKIXPolicyNode) _nodeDepth.get(k);
                        if (ANY_POLICY.equals(_node.getValidPolicy())) {
                            Iterator _iter = _node.getChildren();
                            while (_iter.hasNext()) {
                                _validPolicyNodeSet.add(_iter.next());
                            }
                        }
                    }
                }
                for (PKIXPolicyNode _node2 : _validPolicyNodeSet) {
                    String _validPolicy = _node2.getValidPolicy();
                    acceptablePolicies.contains(_validPolicy);
                }
                if (validPolicyTree != null) {
                    validPolicyTree2 = validPolicyTree;
                    for (int j = n - 1; j >= 0; j--) {
                        List nodes = policyNodes[j];
                        for (int k2 = 0; k2 < nodes.size(); k2++) {
                            PKIXPolicyNode node = (PKIXPolicyNode) nodes.get(k2);
                            if (!node.hasChildren()) {
                                validPolicyTree2 = CertPathValidatorUtilities.removePolicyNode(validPolicyTree2, policyNodes, node);
                            }
                        }
                    }
                    PKIXPolicyNode intersection = validPolicyTree2;
                    return intersection;
                }
            }
            validPolicyTree2 = validPolicyTree;
            PKIXPolicyNode intersection2 = validPolicyTree2;
            return intersection2;
        } else {
            Set<PKIXPolicyNode> _validPolicyNodeSet2 = new HashSet();
            for (List _nodeDepth2 : policyNodes) {
                for (int k3 = 0; k3 < _nodeDepth2.size(); k3++) {
                    PKIXPolicyNode _node3 = (PKIXPolicyNode) _nodeDepth2.get(k3);
                    if (ANY_POLICY.equals(_node3.getValidPolicy())) {
                        Iterator _iter2 = _node3.getChildren();
                        while (_iter2.hasNext()) {
                            PKIXPolicyNode _c_node = (PKIXPolicyNode) _iter2.next();
                            if (!ANY_POLICY.equals(_c_node.getValidPolicy())) {
                                _validPolicyNodeSet2.add(_c_node);
                            }
                        }
                    }
                }
            }
            PKIXPolicyNode validPolicyTree3 = validPolicyTree;
            for (PKIXPolicyNode _node4 : _validPolicyNodeSet2) {
                String _validPolicy2 = _node4.getValidPolicy();
                if (!userInitialPolicySet.contains(_validPolicy2)) {
                    validPolicyTree3 = CertPathValidatorUtilities.removePolicyNode(validPolicyTree3, policyNodes, _node4);
                }
            }
            if (validPolicyTree3 != null) {
                for (int j2 = n - 1; j2 >= 0; j2--) {
                    List nodes2 = policyNodes[j2];
                    for (int k4 = 0; k4 < nodes2.size(); k4++) {
                        PKIXPolicyNode node2 = (PKIXPolicyNode) nodes2.get(k4);
                        if (!node2.hasChildren()) {
                            validPolicyTree3 = CertPathValidatorUtilities.removePolicyNode(validPolicyTree3, policyNodes, node2);
                        }
                    }
                }
            }
            PKIXPolicyNode intersection3 = validPolicyTree3;
            return intersection3;
        }
    }
}
