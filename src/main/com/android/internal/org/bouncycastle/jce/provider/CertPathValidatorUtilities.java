package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Enumerated;
import com.android.internal.org.bouncycastle.asn1.ASN1GeneralizedTime;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1OutputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.style.RFC4519Style;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.CRLDistPoint;
import com.android.internal.org.bouncycastle.asn1.x509.DistributionPoint;
import com.android.internal.org.bouncycastle.asn1.x509.DistributionPointName;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.PolicyInformation;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.jcajce.PKIXCRLStore;
import com.android.internal.org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertStore;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertStoreSelector;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import com.android.internal.org.bouncycastle.jcajce.PKIXExtendedParameters;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import com.android.internal.org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import com.android.internal.org.bouncycastle.util.Properties;
import com.android.internal.org.bouncycastle.x509.X509AttributeCertificate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
class CertPathValidatorUtilities {
    protected static final String ANY_POLICY = "2.5.29.32.0";
    protected static final int CRL_SIGN = 6;
    protected static final int KEY_CERT_SIGN = 5;
    protected static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
    protected static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
    protected static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
    protected static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
    protected static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
    protected static final String KEY_USAGE = Extension.keyUsage.getId();
    protected static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
    protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
    protected static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
    protected static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
    protected static final String FRESHEST_CRL = Extension.freshestCRL.getId();
    protected static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
    protected static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
    protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
    protected static final String[] crlReasons = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise"};

    CertPathValidatorUtilities() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Collection findTargets(PKIXExtendedBuilderParameters paramsPKIX) throws CertPathBuilderException {
        PKIXExtendedParameters baseParams = paramsPKIX.getBaseParameters();
        PKIXCertStoreSelector certSelect = baseParams.getTargetConstraints();
        LinkedHashSet targets = new LinkedHashSet();
        try {
            findCertificates(targets, certSelect, baseParams.getCertificateStores());
            findCertificates(targets, certSelect, baseParams.getCertStores());
            if (!targets.isEmpty()) {
                return targets;
            }
            Certificate target = certSelect.getCertificate();
            if (target == null) {
                throw new CertPathBuilderException("No certificate found matching targetConstraints.");
            }
            return Collections.singleton(target);
        } catch (AnnotatedException e) {
            throw new ExtCertPathBuilderException("Error finding target certificate.", e);
        }
    }

    protected static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors) throws AnnotatedException {
        return findTrustAnchor(cert, trustAnchors, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static TrustAnchor findTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        TrustAnchor trust = null;
        PublicKey trustPublicKey = null;
        Exception invalidKeyEx = null;
        X509CertSelector certSelectX509 = new X509CertSelector();
        X500Principal certIssuerPrincipal = cert.getIssuerX500Principal();
        certSelectX509.setSubject(certIssuerPrincipal);
        X500Name certIssuerName = null;
        Iterator iter = trustAnchors.iterator();
        while (iter.hasNext() && trust == null) {
            trust = (TrustAnchor) iter.next();
            if (trust.getTrustedCert() != null) {
                if (certSelectX509.match(trust.getTrustedCert())) {
                    trustPublicKey = trust.getTrustedCert().getPublicKey();
                } else {
                    trust = null;
                }
            } else if (trust.getCA() != null && trust.getCAName() != null && trust.getCAPublicKey() != null) {
                if (certIssuerName == null) {
                    certIssuerName = X500Name.getInstance(certIssuerPrincipal.getEncoded());
                }
                try {
                    X500Name caName = X500Name.getInstance(trust.getCA().getEncoded());
                    if (certIssuerName.equals(caName)) {
                        trustPublicKey = trust.getCAPublicKey();
                    } else {
                        trust = null;
                    }
                } catch (IllegalArgumentException e) {
                    trust = null;
                }
            } else {
                trust = null;
            }
            if (trustPublicKey != null) {
                try {
                    verifyX509Certificate(cert, trustPublicKey, sigProvider);
                } catch (Exception ex) {
                    invalidKeyEx = ex;
                    trust = null;
                    trustPublicKey = null;
                }
            }
        }
        if (trust == null && invalidKeyEx != null) {
            throw new AnnotatedException("TrustAnchor found but certificate validation failed.", invalidKeyEx);
        }
        return trust;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isIssuerTrustAnchor(X509Certificate cert, Set trustAnchors, String sigProvider) throws AnnotatedException {
        try {
            return findTrustAnchor(cert, trustAnchors, sigProvider) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<PKIXCertStore> getAdditionalStoresFromAltNames(byte[] issuerAlternativeName, Map<GeneralName, PKIXCertStore> altNameCertStoreMap) throws CertificateParsingException {
        if (issuerAlternativeName == null) {
            return Collections.EMPTY_LIST;
        }
        GeneralNames issuerAltName = GeneralNames.getInstance(ASN1OctetString.getInstance(issuerAlternativeName).getOctets());
        GeneralName[] names = issuerAltName.getNames();
        List<PKIXCertStore> stores = new ArrayList<>();
        for (int i = 0; i != names.length; i++) {
            GeneralName altName = names[i];
            PKIXCertStore altStore = altNameCertStoreMap.get(altName);
            if (altStore != null) {
                stores.add(altStore);
            }
        }
        return stores;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Date getValidityDate(PKIXExtendedParameters paramsPKIX, Date currentDate) {
        Date validityDate = paramsPKIX.getValidityDate();
        return validityDate == null ? currentDate : validityDate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isSelfIssued(X509Certificate cert) {
        return cert.getSubjectDN().equals(cert.getIssuerDN());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ASN1Primitive getExtensionValue(X509Extension ext, String oid) throws AnnotatedException {
        byte[] bytes = ext.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        return getObject(oid, bytes);
    }

    private static ASN1Primitive getObject(String oid, byte[] ext) throws AnnotatedException {
        try {
            ASN1OctetString octs = ASN1OctetString.getInstance(ext);
            return ASN1Primitive.fromByteArray(octs.getOctets());
        } catch (Exception e) {
            throw new AnnotatedException("exception processing extension " + oid, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey key) throws CertPathValidatorException {
        try {
            return SubjectPublicKeyInfo.getInstance(key.getEncoded()).getAlgorithm();
        } catch (Exception e) {
            throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final Set getQualifierSet(ASN1Sequence qualifiers) throws CertPathValidatorException {
        Set pq = new HashSet();
        if (qualifiers == null) {
            return pq;
        }
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = ASN1OutputStream.create(bOut);
        Enumeration e = qualifiers.getObjects();
        while (e.hasMoreElements()) {
            try {
                aOut.writeObject((ASN1Encodable) e.nextElement());
                pq.add(new PolicyQualifierInfo(bOut.toByteArray()));
                bOut.reset();
            } catch (IOException ex) {
                throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", ex);
            }
        }
        return pq;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static PKIXPolicyNode removePolicyNode(PKIXPolicyNode validPolicyTree, List[] policyNodes, PKIXPolicyNode _node) {
        PKIXPolicyNode _parent = (PKIXPolicyNode) _node.getParent();
        if (validPolicyTree == null) {
            return null;
        }
        if (_parent == null) {
            for (int j = 0; j < policyNodes.length; j++) {
                policyNodes[j] = new ArrayList();
            }
            return null;
        }
        _parent.removeChild(_node);
        removePolicyNodeRecurse(policyNodes, _node);
        return validPolicyTree;
    }

    private static void removePolicyNodeRecurse(List[] policyNodes, PKIXPolicyNode _node) {
        policyNodes[_node.getDepth()].remove(_node);
        if (_node.hasChildren()) {
            Iterator _iter = _node.getChildren();
            while (_iter.hasNext()) {
                PKIXPolicyNode _child = (PKIXPolicyNode) _iter.next();
                removePolicyNodeRecurse(policyNodes, _child);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean processCertD1i(int index, List[] policyNodes, ASN1ObjectIdentifier pOid, Set pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); j++) {
            PKIXPolicyNode node = (PKIXPolicyNode) policyNodeVec.get(j);
            Set expectedPolicies = node.getExpectedPolicies();
            if (expectedPolicies.contains(pOid.getId())) {
                Set childExpectedPolicies = new HashSet();
                childExpectedPolicies.add(pOid.getId());
                PKIXPolicyNode child = new PKIXPolicyNode(new ArrayList(), index, childExpectedPolicies, node, pq, pOid.getId(), false);
                node.addChild(child);
                policyNodes[index].add(child);
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void processCertD1ii(int index, List[] policyNodes, ASN1ObjectIdentifier _poid, Set _pq) {
        List policyNodeVec = policyNodes[index - 1];
        for (int j = 0; j < policyNodeVec.size(); j++) {
            PKIXPolicyNode _node = (PKIXPolicyNode) policyNodeVec.get(j);
            if ("2.5.29.32.0".equals(_node.getValidPolicy())) {
                Set _childExpectedPolicies = new HashSet();
                _childExpectedPolicies.add(_poid.getId());
                PKIXPolicyNode _child = new PKIXPolicyNode(new ArrayList(), index, _childExpectedPolicies, _node, _pq, _poid.getId(), false);
                _node.addChild(_child);
                policyNodes[index].add(_child);
                return;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:69:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected static void prepareNextCertB1(int i, List[] policyNodes, String id_p, Map m_idp, X509Certificate cert) throws AnnotatedException, CertPathValidatorException {
        boolean idp_found;
        Set pq;
        boolean ci;
        Iterator nodes_i = policyNodes[i].iterator();
        while (true) {
            if (!nodes_i.hasNext()) {
                idp_found = false;
                break;
            }
            PKIXPolicyNode node = (PKIXPolicyNode) nodes_i.next();
            if (node.getValidPolicy().equals(id_p)) {
                node.expectedPolicies = (Set) m_idp.get(id_p);
                idp_found = true;
                break;
            }
        }
        if (!idp_found) {
            Iterator nodes_i2 = policyNodes[i].iterator();
            while (true) {
                if (!nodes_i2.hasNext()) {
                    break;
                }
                PKIXPolicyNode node2 = (PKIXPolicyNode) nodes_i2.next();
                if ("2.5.29.32.0".equals(node2.getValidPolicy())) {
                    try {
                    } catch (Exception e) {
                        e = e;
                    }
                    try {
                        ASN1Sequence policies = DERSequence.getInstance(getExtensionValue(cert, CERTIFICATE_POLICIES));
                        Enumeration e2 = policies.getObjects();
                        while (true) {
                            if (!e2.hasMoreElements()) {
                                pq = null;
                                break;
                            }
                            try {
                                PolicyInformation pinfo = PolicyInformation.getInstance(e2.nextElement());
                                if ("2.5.29.32.0".equals(pinfo.getPolicyIdentifier().getId())) {
                                    try {
                                        pq = getQualifierSet(pinfo.getPolicyQualifiers());
                                        break;
                                    } catch (CertPathValidatorException ex) {
                                        throw new ExtCertPathValidatorException("Policy qualifier info set could not be built.", ex);
                                    }
                                }
                            } catch (Exception ex2) {
                                throw new AnnotatedException("Policy information cannot be decoded.", ex2);
                            }
                        }
                        if (cert.getCriticalExtensionOIDs() == null) {
                            ci = false;
                        } else {
                            boolean ci2 = cert.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
                            ci = ci2;
                        }
                        PKIXPolicyNode p_node = (PKIXPolicyNode) node2.getParent();
                        if ("2.5.29.32.0".equals(p_node.getValidPolicy())) {
                            PKIXPolicyNode c_node = new PKIXPolicyNode(new ArrayList(), i, (Set) m_idp.get(id_p), p_node, pq, id_p, ci);
                            p_node.addChild(c_node);
                            policyNodes[i].add(c_node);
                        }
                    } catch (Exception e3) {
                        e = e3;
                        throw new AnnotatedException("Certificate policies cannot be decoded.", e);
                    }
                }
            }
        }
    }

    protected static PKIXPolicyNode prepareNextCertB2(int i, List[] policyNodes, String id_p, PKIXPolicyNode validPolicyTree) {
        int l;
        Iterator nodes_i = policyNodes[i].iterator();
        while (nodes_i.hasNext()) {
            PKIXPolicyNode node = (PKIXPolicyNode) nodes_i.next();
            if (node.getValidPolicy().equals(id_p)) {
                PKIXPolicyNode p_node = (PKIXPolicyNode) node.getParent();
                p_node.removeChild(node);
                nodes_i.remove();
                for (int k = i - 1; k >= 0; k--) {
                    List nodes = policyNodes[k];
                    while (l < nodes.size()) {
                        PKIXPolicyNode node2 = (PKIXPolicyNode) nodes.get(l);
                        l = (node2.hasChildren() || (validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, node2)) != null) ? l + 1 : 0;
                    }
                }
            }
        }
        return validPolicyTree;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isAnyPolicy(Set policySet) {
        return policySet == null || policySet.contains("2.5.29.32.0") || policySet.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void findCertificates(LinkedHashSet certs, PKIXCertStoreSelector certSelect, List certStores) throws AnnotatedException {
        for (Object obj : certStores) {
            CertStore certStore = (CertStore) obj;
            try {
                certs.addAll(PKIXCertStoreSelector.getCertificates(certSelect, certStore));
            } catch (CertStoreException e) {
                throw new AnnotatedException("Problem while picking certificates from certificate store.", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint crldp, Map<GeneralName, PKIXCRLStore> namedCRLStoreMap, Date validDate, JcaJceHelper helper) throws AnnotatedException {
        if (crldp == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            DistributionPoint[] dps = crldp.getDistributionPoints();
            List<PKIXCRLStore> stores = new ArrayList<>();
            for (DistributionPoint distributionPoint : dps) {
                DistributionPointName dpn = distributionPoint.getDistributionPoint();
                if (dpn != null && dpn.getType() == 0) {
                    for (GeneralName generalName : GeneralNames.getInstance(dpn.getName()).getNames()) {
                        PKIXCRLStore store = namedCRLStoreMap.get(generalName);
                        if (store != null) {
                            stores.add(store);
                        }
                    }
                }
            }
            if (stores.isEmpty() && Properties.isOverrideSet("com.android.internal.org.bouncycastle.x509.enableCRLDP")) {
                try {
                    helper.createCertificateFactory("X.509");
                    for (int i = 0; i < dps.length; i++) {
                        DistributionPointName dpn2 = dps[i].getDistributionPoint();
                        if (dpn2 != null && dpn2.getType() == 0) {
                            GeneralName[] genNames = GeneralNames.getInstance(dpn2.getName()).getNames();
                            for (int j = 0; j < genNames.length; j++) {
                                GeneralName name = genNames[i];
                                if (name.getTagNo() == 6) {
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new AnnotatedException("cannot create certificate factory: " + e.getMessage(), e);
                }
            }
            return stores;
        } catch (Exception e2) {
            throw new AnnotatedException("Distribution points could not be read.", e2);
        }
    }

    protected static void getCRLIssuersFromDistributionPoint(DistributionPoint dp, Collection issuerPrincipals, X509CRLSelector selector) throws AnnotatedException {
        List<X500Name> issuers = new ArrayList();
        if (dp.getCRLIssuer() != null) {
            GeneralName[] genNames = dp.getCRLIssuer().getNames();
            for (int j = 0; j < genNames.length; j++) {
                if (genNames[j].getTagNo() == 4) {
                    try {
                        issuers.add(X500Name.getInstance(genNames[j].getName().toASN1Primitive().getEncoded()));
                    } catch (IOException e) {
                        throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", e);
                    }
                }
            }
        } else if (dp.getDistributionPoint() == null) {
            throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
        } else {
            for (Object obj : issuerPrincipals) {
                issuers.add(obj);
            }
        }
        for (X500Name x500Name : issuers) {
            try {
                selector.addIssuerName(x500Name.getEncoded());
            } catch (IOException ex) {
                throw new AnnotatedException("Cannot decode CRL issuer information.", ex);
            }
        }
    }

    private static BigInteger getSerialNumber(Object cert) {
        return ((X509Certificate) cert).getSerialNumber();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void getCertStatus(Date validDate, X509CRL crl, Object cert, CertStatus certStatus) throws AnnotatedException {
        X509CRLEntry crl_entry;
        int reasonCodeValue;
        X500Name certIssuer;
        try {
            boolean isIndirect = X509CRLObject.isIndirectCRL(crl);
            if (isIndirect) {
                crl_entry = crl.getRevokedCertificate(getSerialNumber(cert));
                if (crl_entry == null) {
                    return;
                }
                X500Principal certificateIssuer = crl_entry.getCertificateIssuer();
                if (certificateIssuer == null) {
                    certIssuer = PrincipalUtils.getIssuerPrincipal(crl);
                } else {
                    certIssuer = PrincipalUtils.getX500Name(certificateIssuer);
                }
                if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(certIssuer)) {
                    return;
                }
            } else if (!PrincipalUtils.getEncodedIssuerPrincipal(cert).equals(PrincipalUtils.getIssuerPrincipal(crl)) || (crl_entry = crl.getRevokedCertificate(getSerialNumber(cert))) == null) {
                return;
            }
            ASN1Enumerated reasonCode = null;
            if (crl_entry.hasExtensions()) {
                if (crl_entry.hasUnsupportedCriticalExtension()) {
                    throw new AnnotatedException("CRL entry has unsupported critical extensions.");
                }
                try {
                    reasonCode = ASN1Enumerated.getInstance(getExtensionValue(crl_entry, Extension.reasonCode.getId()));
                } catch (Exception e) {
                    throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", e);
                }
            }
            if (reasonCode == null) {
                reasonCodeValue = 0;
            } else {
                reasonCodeValue = reasonCode.intValueExact();
            }
            if (validDate.getTime() >= crl_entry.getRevocationDate().getTime() || reasonCodeValue == 0 || reasonCodeValue == 1 || reasonCodeValue == 2 || reasonCodeValue == 10) {
                certStatus.setCertStatus(reasonCodeValue);
                certStatus.setRevocationDate(crl_entry.getRevocationDate());
            }
        } catch (CRLException exception) {
            throw new AnnotatedException("Failed check for indirect CRL.", exception);
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:44:0x00e0
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    protected static java.util.Set getDeltaCRLs(java.util.Date r20, java.security.cert.X509CRL r21, java.util.List<java.security.cert.CertStore> r22, java.util.List<com.android.internal.org.bouncycastle.jcajce.PKIXCRLStore> r23, com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper r24) throws com.android.internal.org.bouncycastle.jce.provider.AnnotatedException {
        /*
            Method dump skipped, instructions count: 351
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.org.bouncycastle.jce.provider.CertPathValidatorUtilities.getDeltaCRLs(java.util.Date, java.security.cert.X509CRL, java.util.List, java.util.List, com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper):java.util.Set");
    }

    private static boolean isDeltaCRL(X509CRL crl) {
        Set critical = crl.getCriticalExtensionOIDs();
        if (critical == null) {
            return false;
        }
        return critical.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Set getCompleteCRLs(PKIXCertRevocationCheckerParameters params, DistributionPoint dp, Object cert, PKIXExtendedParameters paramsPKIX, Date validityDate) throws AnnotatedException, RecoverableCertPathValidatorException {
        X509CRLSelector baseCrlSelect = new X509CRLSelector();
        try {
            Set issuers = new HashSet();
            issuers.add(PrincipalUtils.getEncodedIssuerPrincipal(cert));
            getCRLIssuersFromDistributionPoint(dp, issuers, baseCrlSelect);
            if (cert instanceof X509Certificate) {
                baseCrlSelect.setCertificateChecking((X509Certificate) cert);
            }
            PKIXCRLStoreSelector crlSelect = new PKIXCRLStoreSelector.Builder(baseCrlSelect).setCompleteCRLEnabled(true).build();
            Set crls = PKIXCRLUtil.findCRLs(crlSelect, validityDate, paramsPKIX.getCertStores(), paramsPKIX.getCRLStores());
            checkCRLsNotEmpty(params, crls, cert);
            return crls;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Could not get issuer information from distribution point.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Date getValidCertDateFromValidityModel(Date validityDate, int validityModel, CertPath certPath, int index) throws AnnotatedException {
        if (1 != validityModel || index <= 0) {
            return validityDate;
        }
        X509Certificate issuedCert = (X509Certificate) certPath.getCertificates().get(index - 1);
        if (index - 1 == 0) {
            ASN1GeneralizedTime dateOfCertgen = null;
            try {
                byte[] extBytes = ((X509Certificate) certPath.getCertificates().get(index - 1)).getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
                if (extBytes != null) {
                    dateOfCertgen = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(extBytes));
                }
                if (dateOfCertgen != null) {
                    try {
                        return dateOfCertgen.getDate();
                    } catch (ParseException e) {
                        throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", e);
                    }
                }
            } catch (IOException e2) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            } catch (IllegalArgumentException e3) {
                throw new AnnotatedException("Date of cert gen extension could not be read.");
            }
        }
        return issuedCert.getNotBefore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static PublicKey getNextWorkingKey(List certs, int index, JcaJceHelper helper) throws CertPathValidatorException {
        Certificate cert = (Certificate) certs.get(index);
        PublicKey pubKey = cert.getPublicKey();
        if (!(pubKey instanceof DSAPublicKey)) {
            return pubKey;
        }
        DSAPublicKey dsaPubKey = (DSAPublicKey) pubKey;
        if (dsaPubKey.getParams() != null) {
            return dsaPubKey;
        }
        for (int i = index + 1; i < certs.size(); i++) {
            X509Certificate parentCert = (X509Certificate) certs.get(i);
            PublicKey pubKey2 = parentCert.getPublicKey();
            if (!(pubKey2 instanceof DSAPublicKey)) {
                throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
            }
            DSAPublicKey prevDSAPubKey = (DSAPublicKey) pubKey2;
            if (prevDSAPubKey.getParams() != null) {
                DSAParams dsaParams = prevDSAPubKey.getParams();
                DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(dsaPubKey.getY(), dsaParams.getP(), dsaParams.getQ(), dsaParams.getG());
                try {
                    KeyFactory keyFactory = helper.createKeyFactory("DSA");
                    return keyFactory.generatePublic(dsaPubKeySpec);
                } catch (Exception exception) {
                    throw new RuntimeException(exception.getMessage());
                }
            }
        }
        throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Collection findIssuerCerts(X509Certificate cert, List<CertStore> certStores, List<PKIXCertStore> pkixCertStores) throws AnnotatedException {
        X509CertSelector selector = new X509CertSelector();
        try {
            selector.setSubject(PrincipalUtils.getIssuerPrincipal(cert).getEncoded());
            try {
                byte[] akiExtensionValue = cert.getExtensionValue(AUTHORITY_KEY_IDENTIFIER);
                if (akiExtensionValue != null) {
                    ASN1OctetString aki = ASN1OctetString.getInstance(akiExtensionValue);
                    byte[] authorityKeyIdentifier = AuthorityKeyIdentifier.getInstance(aki.getOctets()).getKeyIdentifier();
                    if (authorityKeyIdentifier != null) {
                        selector.setSubjectKeyIdentifier(new DEROctetString(authorityKeyIdentifier).getEncoded());
                    }
                }
            } catch (Exception e) {
            }
            PKIXCertStoreSelector certSelect = new PKIXCertStoreSelector.Builder(selector).build();
            LinkedHashSet certs = new LinkedHashSet();
            try {
                findCertificates(certs, certSelect, certStores);
                findCertificates(certs, certSelect, pkixCertStores);
                return certs;
            } catch (AnnotatedException e2) {
                throw new AnnotatedException("Issuer certificate cannot be searched.", e2);
            }
        } catch (Exception e3) {
            throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void verifyX509Certificate(X509Certificate cert, PublicKey publicKey, String sigProvider) throws GeneralSecurityException {
        if (sigProvider == null) {
            cert.verify(publicKey);
        } else {
            cert.verify(publicKey, sigProvider);
        }
    }

    static void checkCRLsNotEmpty(PKIXCertRevocationCheckerParameters params, Set crls, Object cert) throws RecoverableCertPathValidatorException {
        if (crls.isEmpty()) {
            if (cert instanceof X509AttributeCertificate) {
                X509AttributeCertificate aCert = (X509AttributeCertificate) cert;
                throw new RecoverableCertPathValidatorException("No CRLs found for issuer \"" + aCert.getIssuer().getPrincipals()[0] + "\"", null, params.getCertPath(), params.getIndex());
            } else {
                X509Certificate xCert = (X509Certificate) cert;
                throw new RecoverableCertPathValidatorException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal(xCert)) + "\"", null, params.getCertPath(), params.getIndex());
            }
        }
    }
}
