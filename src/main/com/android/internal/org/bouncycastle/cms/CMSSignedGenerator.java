package com.android.internal.org.bouncycastle.cms;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DERTaggedObject;
import com.android.internal.org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.cert.X509AttributeCertificateHolder;
import com.android.internal.org.bouncycastle.cert.X509CRLHolder;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Store;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public class CMSSignedGenerator {
    public static final String DATA = CMSObjectIdentifiers.data.getId();
    public static final String DIGEST_MD5;
    public static final String DIGEST_SHA1;
    public static final String DIGEST_SHA224;
    public static final String DIGEST_SHA256;
    public static final String DIGEST_SHA384;
    public static final String DIGEST_SHA512;
    private static final Map EC_ALGORITHMS;
    public static final String ENCRYPTION_DSA;
    public static final String ENCRYPTION_ECDSA;
    private static final String ENCRYPTION_ECDSA_WITH_SHA1;
    private static final String ENCRYPTION_ECDSA_WITH_SHA224;
    private static final String ENCRYPTION_ECDSA_WITH_SHA256;
    private static final String ENCRYPTION_ECDSA_WITH_SHA384;
    private static final String ENCRYPTION_ECDSA_WITH_SHA512;
    public static final String ENCRYPTION_RSA;
    public static final String ENCRYPTION_RSA_PSS;
    private static final Set NO_PARAMS;
    protected List certs = new ArrayList();
    protected List crls = new ArrayList();
    protected List _signers = new ArrayList();
    protected List signerGens = new ArrayList();
    protected Map digests = new HashMap();

    static {
        String id = OIWObjectIdentifiers.idSHA1.getId();
        DIGEST_SHA1 = id;
        String id2 = NISTObjectIdentifiers.id_sha224.getId();
        DIGEST_SHA224 = id2;
        String id3 = NISTObjectIdentifiers.id_sha256.getId();
        DIGEST_SHA256 = id3;
        String id4 = NISTObjectIdentifiers.id_sha384.getId();
        DIGEST_SHA384 = id4;
        String id5 = NISTObjectIdentifiers.id_sha512.getId();
        DIGEST_SHA512 = id5;
        DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
        ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
        String id6 = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
        ENCRYPTION_DSA = id6;
        String id7 = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
        ENCRYPTION_ECDSA = id7;
        ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
        String id8 = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
        ENCRYPTION_ECDSA_WITH_SHA1 = id8;
        String id9 = X9ObjectIdentifiers.ecdsa_with_SHA224.getId();
        ENCRYPTION_ECDSA_WITH_SHA224 = id9;
        String id10 = X9ObjectIdentifiers.ecdsa_with_SHA256.getId();
        ENCRYPTION_ECDSA_WITH_SHA256 = id10;
        String id11 = X9ObjectIdentifiers.ecdsa_with_SHA384.getId();
        ENCRYPTION_ECDSA_WITH_SHA384 = id11;
        String id12 = X9ObjectIdentifiers.ecdsa_with_SHA512.getId();
        ENCRYPTION_ECDSA_WITH_SHA512 = id12;
        HashSet hashSet = new HashSet();
        NO_PARAMS = hashSet;
        HashMap hashMap = new HashMap();
        EC_ALGORITHMS = hashMap;
        hashSet.add(id6);
        hashSet.add(id7);
        hashSet.add(id8);
        hashSet.add(id9);
        hashSet.add(id10);
        hashSet.add(id11);
        hashSet.add(id12);
        hashMap.put(id, id8);
        hashMap.put(id2, id9);
        hashMap.put(id3, id10);
        hashMap.put(id4, id11);
        hashMap.put(id5, id12);
    }

    protected Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, byte[] hash) {
        Map param = new HashMap();
        param.put(CMSAttributeTableGenerator.CONTENT_TYPE, contentType);
        param.put(CMSAttributeTableGenerator.DIGEST_ALGORITHM_IDENTIFIER, digAlgId);
        param.put(CMSAttributeTableGenerator.DIGEST, Arrays.clone(hash));
        return param;
    }

    public void addCertificate(X509CertificateHolder certificate) throws CMSException {
        this.certs.add(certificate.toASN1Structure());
    }

    public void addCertificates(Store certStore) throws CMSException {
        this.certs.addAll(CMSUtils.getCertificatesFromStore(certStore));
    }

    public void addCRL(X509CRLHolder crl) {
        this.crls.add(crl.toASN1Structure());
    }

    public void addCRLs(Store crlStore) throws CMSException {
        this.crls.addAll(CMSUtils.getCRLsFromStore(crlStore));
    }

    public void addAttributeCertificate(X509AttributeCertificateHolder attrCert) throws CMSException {
        this.certs.add(new DERTaggedObject(false, 2, attrCert.toASN1Structure()));
    }

    public void addAttributeCertificates(Store attrStore) throws CMSException {
        this.certs.addAll(CMSUtils.getAttributeCertificatesFromStore(attrStore));
    }

    public void addSigners(SignerInformationStore signerStore) {
        for (SignerInformation signerInformation : signerStore.getSigners()) {
            this._signers.add(signerInformation);
        }
    }

    public void addSignerInfoGenerator(SignerInfoGenerator infoGen) {
        this.signerGens.add(infoGen);
    }

    public Map getGeneratedDigests() {
        return new HashMap(this.digests);
    }
}
