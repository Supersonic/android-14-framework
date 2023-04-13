package com.android.internal.org.bouncycastle.jcajce.provider.keystore.pkcs12;

import android.media.MediaMetrics;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.BEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERBMPString;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.DERSet;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import com.android.internal.org.bouncycastle.asn1.pkcs.CertBag;
import com.android.internal.org.bouncycastle.asn1.pkcs.ContentInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.EncryptedData;
import com.android.internal.org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.MacData;
import com.android.internal.org.bouncycastle.asn1.pkcs.PBES2Parameters;
import com.android.internal.org.bouncycastle.asn1.pkcs.PBKDF2Params;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.Pfx;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.SafeBag;
import com.android.internal.org.bouncycastle.asn1.util.ASN1Dump;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DigestInfo;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.jcajce.PKCS12Key;
import com.android.internal.org.bouncycastle.jcajce.PKCS12StoreParameter;
import com.android.internal.org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import com.android.internal.org.bouncycastle.jcajce.util.BCJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.interfaces.BCKeyStore;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Properties;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore {
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    private static final int MIN_ITERATIONS = 51200;
    static final int NULL = 0;
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "com.android.internal.org.bouncycastle.pkcs12.max_it_count";
    private static final int SALT_SIZE = 20;
    static final int SEALED = 4;
    static final int SECRET = 3;
    private static final DefaultSecretKeyProvider keySizeProvider = new DefaultSecretKeyProvider();
    private ASN1ObjectIdentifier certAlgorithm;
    private CertificateFactory certFact;
    private ASN1ObjectIdentifier keyAlgorithm;
    private final JcaJceHelper helper = new DefaultJcaJceHelper();
    private final JcaJceHelper selfHelper = new BCJcaJceHelper();
    private IgnoresCaseHashtable keys = new IgnoresCaseHashtable();
    private Hashtable localIds = new Hashtable();
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private Hashtable keyCerts = new Hashtable();
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    private int itCount = 102400;
    private int saltLength = 20;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class CertId {

        /* renamed from: id */
        byte[] f811id;

        CertId(PublicKey key) {
            this.f811id = PKCS12KeyStoreSpi.this.createSubjectKeyId(key).getKeyIdentifier();
        }

        CertId(byte[] id) {
            this.f811id = id;
        }

        public int hashCode() {
            return Arrays.hashCode(this.f811id);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CertId)) {
                return false;
            }
            CertId cId = (CertId) o;
            return Arrays.areEqual(this.f811id, cId.f811id);
        }
    }

    public PKCS12KeyStoreSpi(JcaJceHelper helper, ASN1ObjectIdentifier keyAlgorithm, ASN1ObjectIdentifier certAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
        this.certAlgorithm = certAlgorithm;
        try {
            this.certFact = helper.createCertificateFactory("X.509");
        } catch (Exception e) {
            throw new IllegalArgumentException("can't create cert factory - " + e.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubjectKeyIdentifier createSubjectKeyId(PublicKey pubKey) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());
            return new SubjectKeyIdentifier(getDigest(info));
        } catch (Exception e) {
            throw new RuntimeException("error creating key");
        }
    }

    private static byte[] getDigest(SubjectPublicKeyInfo spki) {
        Digest digest = AndroidDigestFactory.getSHA1();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = spki.getPublicKeyData().getBytes();
        digest.update(bytes, 0, bytes.length);
        digest.doFinal(resBuf, 0);
        return resBuf;
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.BCKeyStore
    public void setRandom(SecureRandom rand) {
        this.random = rand;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration engineAliases() {
        Hashtable tab = new Hashtable();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        Enumeration e2 = this.keys.keys();
        while (e2.hasMoreElements()) {
            String a = (String) e2.nextElement();
            if (tab.get(a) == null) {
                tab.put(a, "key");
            }
        }
        return tab.keys();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        return (this.certs.get(alias) == null && this.keys.get(alias) == null) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        Key k = (Key) this.keys.remove(alias);
        Certificate c = (Certificate) this.certs.remove(alias);
        if (c != null) {
            this.chainCerts.remove(new CertId(c.getPublicKey()));
        }
        if (k != null) {
            String id = (String) this.localIds.remove(alias);
            if (id != null) {
                c = (Certificate) this.keyCerts.remove(id);
            }
            if (c != null) {
                this.chainCerts.remove(new CertId(c.getPublicKey()));
            }
        }
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate c = (Certificate) this.certs.get(alias);
        if (c == null) {
            String id = (String) this.localIds.get(alias);
            if (id != null) {
                return (Certificate) this.keyCerts.get(id);
            }
            return (Certificate) this.keyCerts.get(alias);
        }
        return c;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate cert) {
        Enumeration c = this.certs.elements();
        Enumeration k = this.certs.keys();
        while (c.hasMoreElements()) {
            Certificate tc = (Certificate) c.nextElement();
            String ta = (String) k.nextElement();
            if (tc.equals(cert)) {
                return ta;
            }
        }
        Enumeration c2 = this.keyCerts.elements();
        Enumeration k2 = this.keyCerts.keys();
        while (c2.hasMoreElements()) {
            Certificate tc2 = (Certificate) c2.nextElement();
            String ta2 = (String) k2.nextElement();
            if (tc2.equals(cert)) {
                return ta2;
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        Certificate c;
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (engineIsKeyEntry(alias) && (c = engineGetCertificate(alias)) != null) {
            Vector cs = new Vector();
            while (c != null) {
                X509Certificate x509c = (X509Certificate) c;
                Certificate nextC = null;
                byte[] akiBytes = x509c.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (akiBytes != null) {
                    ASN1OctetString akiValue = ASN1OctetString.getInstance(akiBytes);
                    AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(akiValue.getOctets());
                    byte[] keyID = aki.getKeyIdentifier();
                    if (keyID != null) {
                        nextC = (Certificate) this.chainCerts.get(new CertId(keyID));
                    }
                }
                if (nextC == null) {
                    Principal i = x509c.getIssuerDN();
                    Principal s = x509c.getSubjectDN();
                    if (!i.equals(s)) {
                        Enumeration e = this.chainCerts.keys();
                        while (true) {
                            if (!e.hasMoreElements()) {
                                break;
                            }
                            X509Certificate crt = (X509Certificate) this.chainCerts.get(e.nextElement());
                            Principal sub = crt.getSubjectDN();
                            if (sub.equals(i)) {
                                try {
                                    x509c.verify(crt.getPublicKey());
                                    nextC = crt;
                                    break;
                                } catch (Exception e2) {
                                }
                            }
                        }
                    }
                }
                if (cs.contains(c)) {
                    c = null;
                } else {
                    cs.addElement(c);
                    if (nextC != c) {
                        c = nextC;
                    } else {
                        c = null;
                    }
                }
            }
            Certificate[] certChain = new Certificate[cs.size()];
            for (int i2 = 0; i2 != certChain.length; i2++) {
                certChain[i2] = (Certificate) cs.elementAt(i2);
            }
            return certChain;
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(alias) == null && this.certs.get(alias) == null) {
            return null;
        }
        return new Date();
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (alias == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key) this.keys.get(alias);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        return this.certs.get(alias) != null && this.keys.get(alias) == null;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        return this.keys.get(alias) != null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        if (this.keys.get(alias) != null) {
            throw new KeyStoreException("There is a key entry with the name " + alias + MediaMetrics.SEPARATOR);
        }
        this.certs.put(alias, cert);
        this.chainCerts.put(new CertId(cert.getPublicKey()), cert);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if ((key instanceof PrivateKey) && chain == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(alias) != null) {
            engineDeleteEntry(alias);
        }
        this.keys.put(alias, key);
        if (chain != null) {
            this.certs.put(alias, chain[0]);
            for (int i = 0; i != chain.length; i++) {
                this.chainCerts.put(new CertId(chain[i].getPublicKey()), chain[i]);
            }
        }
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        Hashtable tab = new Hashtable();
        Enumeration e = this.certs.keys();
        while (e.hasMoreElements()) {
            tab.put(e.nextElement(), "cert");
        }
        Enumeration e2 = this.keys.keys();
        while (e2.hasMoreElements()) {
            String a = (String) e2.nextElement();
            if (tab.get(a) == null) {
                tab.put(a, "key");
            }
        }
        return tab.size();
    }

    protected PrivateKey unwrapKey(AlgorithmIdentifier algId, byte[] data, char[] password, boolean wrongPKCS12Zero) throws IOException {
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        try {
            if (algorithm.m69on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), validateIterationCount(pbeParams.getIterations()));
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                cipher.init(4, key, defParams);
                return (PrivateKey) cipher.unwrap(data, "", 2);
            } else if (algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
                return (PrivateKey) createCipher(4, password, algId).unwrap(data, "", 2);
            } else {
                throw new IOException("exception unwrapping private key - cannot recognise: " + algorithm);
            }
        } catch (Exception e) {
            throw new IOException("exception unwrapping private key - " + e.toString());
        }
    }

    protected byte[] wrapKey(String algorithm, Key key, PKCS12PBEParams pbeParams, char[] password) throws IOException {
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        try {
            SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(algorithm);
            PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
            Cipher cipher = this.helper.createCipher(algorithm);
            cipher.init(3, keyFact.generateSecret(pbeSpec), defParams);
            byte[] out = cipher.wrap(key);
            return out;
        } catch (Exception e) {
            throw new IOException("exception encrypting data - " + e.toString());
        }
    }

    protected byte[] cryptData(boolean forEncryption, AlgorithmIdentifier algId, char[] password, boolean wrongPKCS12Zero, byte[] data) throws IOException {
        ASN1ObjectIdentifier algorithm = algId.getAlgorithm();
        int mode = forEncryption ? 1 : 2;
        if (algorithm.m69on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
            try {
                PBEParameterSpec defParams = new PBEParameterSpec(pbeParams.getIV(), pbeParams.getIterations().intValue());
                PKCS12Key key = new PKCS12Key(password, wrongPKCS12Zero);
                Cipher cipher = this.helper.createCipher(algorithm.getId());
                cipher.init(mode, key, defParams);
                return cipher.doFinal(data);
            } catch (Exception e) {
                throw new IOException("exception decrypting data - " + e.toString());
            }
        } else if (algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_PBES2)) {
            try {
                return createCipher(mode, password, algId).doFinal(data);
            } catch (Exception e2) {
                throw new IOException("exception decrypting data - " + e2.toString());
            }
        } else {
            throw new IOException("unknown PBE algorithm: " + algorithm);
        }
    }

    private Cipher createCipher(int mode, char[] password, AlgorithmIdentifier algId) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        SecretKey key;
        PBES2Parameters alg = PBES2Parameters.getInstance(algId.getParameters());
        PBKDF2Params func = PBKDF2Params.getInstance(alg.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance(alg.getEncryptionScheme());
        SecretKeyFactory keyFact = this.selfHelper.createSecretKeyFactory(alg.getKeyDerivationFunc().getAlgorithm().getId());
        if (func.isDefaultPrf()) {
            key = keyFact.generateSecret(new PBEKeySpec(password, func.getSalt(), validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme)));
        } else {
            key = keyFact.generateSecret(new PBKDF2KeySpec(password, func.getSalt(), validateIterationCount(func.getIterationCount()), keySizeProvider.getKeySize(encScheme), func.getPrf()));
        }
        Cipher cipher = this.selfHelper.createCipher(alg.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable encParams = alg.getEncryptionScheme().getParameters();
        if (encParams instanceof ASN1OctetString) {
            cipher.init(mode, key, new IvParameterSpec(ASN1OctetString.getInstance(encParams).getOctets()));
        }
        return cipher;
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        Vector chain;
        boolean wrongPKCS12Zero;
        String str;
        String str2;
        String str3;
        SafeBag b;
        ASN1OctetString content;
        int head;
        ASN1InputStream bIn;
        Pfx bag;
        int head2;
        ContentInfo[] c;
        String str4;
        String str5;
        boolean wrongPKCS12Zero2;
        EncryptedData d;
        byte[] octets;
        ASN1Sequence seq;
        String str6;
        boolean wrongPKCS12Zero3;
        ASN1Sequence seq2;
        String str7;
        int head3;
        ASN1InputStream bIn2;
        Pfx bag2;
        Pfx bag3;
        char[] cArr = password;
        if (stream == null) {
            return;
        }
        BufferedInputStream bufIn = new BufferedInputStream(stream);
        bufIn.mark(10);
        int head4 = bufIn.read();
        if (head4 != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufIn.reset();
        ASN1InputStream bIn3 = new ASN1InputStream(bufIn);
        try {
            Pfx bag4 = Pfx.getInstance(bIn3.readObject());
            ContentInfo info = bag4.getAuthSafe();
            Vector chain2 = new Vector();
            boolean unmarkedKey = false;
            boolean wrongPKCS12Zero4 = false;
            if (bag4.getMacData() == null) {
                chain = chain2;
                wrongPKCS12Zero = false;
            } else if (cArr == null) {
                throw new NullPointerException("no password supplied when one expected");
            } else {
                MacData mData = bag4.getMacData();
                DigestInfo dInfo = mData.getMac();
                this.macAlgorithm = dInfo.getAlgorithmId();
                byte[] salt = mData.getSalt();
                this.itCount = validateIterationCount(mData.getIterationCount());
                this.saltLength = salt.length;
                byte[] data = ((ASN1OctetString) info.getContent()).getOctets();
                try {
                    chain = chain2;
                    try {
                        byte[] res = calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, false, data);
                        byte[] dig = dInfo.getDigest();
                        if (!Arrays.constantTimeAreEqual(res, dig)) {
                            try {
                                if (cArr.length > 0) {
                                    throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                                }
                                byte[] res2 = calculatePbeMac(this.macAlgorithm.getAlgorithm(), salt, this.itCount, password, true, data);
                                if (!Arrays.constantTimeAreEqual(res2, dig)) {
                                    throw new IOException("PKCS12 key store mac invalid - wrong password or corrupted file.");
                                }
                                wrongPKCS12Zero4 = true;
                            } catch (IOException e) {
                                throw e;
                            } catch (Exception e2) {
                                e = e2;
                                throw new IOException("error constructing MAC: " + e.toString());
                            }
                        }
                        wrongPKCS12Zero = wrongPKCS12Zero4;
                    } catch (IOException e3) {
                        throw e3;
                    } catch (Exception e4) {
                        e = e4;
                    }
                } catch (IOException e5) {
                    throw e5;
                } catch (Exception e6) {
                    e = e6;
                }
            }
            this.keys = new IgnoresCaseHashtable();
            this.localIds = new Hashtable();
            String str8 = "unmarked";
            String str9 = "attempt to add existing attribute with different value";
            if (!info.getContentType().equals((ASN1Primitive) data)) {
                str = "attempt to add existing attribute with different value";
                str2 = "unmarked";
            } else {
                ASN1OctetString content2 = ASN1OctetString.getInstance(info.getContent());
                AuthenticatedSafe authSafe = AuthenticatedSafe.getInstance(content2.getOctets());
                ContentInfo[] c2 = authSafe.getContentInfo();
                int i = 0;
                while (i != c2.length) {
                    if (c2[i].getContentType().equals((ASN1Primitive) data)) {
                        ASN1OctetString authSafeContent = ASN1OctetString.getInstance(c2[i].getContent());
                        ASN1Sequence seq3 = ASN1Sequence.getInstance(authSafeContent.getOctets());
                        int j = 0;
                        while (true) {
                            content = content2;
                            if (j != seq3.size()) {
                                SafeBag b2 = SafeBag.getInstance(seq3.getObjectAt(j));
                                ASN1OctetString authSafeContent2 = authSafeContent;
                                ASN1Sequence seq4 = seq3;
                                if (b2.getBagId().equals((ASN1Primitive) pkcs8ShroudedKeyBag)) {
                                    EncryptedPrivateKeyInfo eIn = EncryptedPrivateKeyInfo.getInstance(b2.getBagValue());
                                    head3 = head4;
                                    PrivateKey privKey = unwrapKey(eIn.getEncryptionAlgorithm(), eIn.getEncryptedData(), cArr, wrongPKCS12Zero);
                                    String alias = null;
                                    ASN1OctetString localId = null;
                                    if (b2.getBagAttributes() != null) {
                                        Enumeration e7 = b2.getBagAttributes().getObjects();
                                        while (e7.hasMoreElements()) {
                                            EncryptedPrivateKeyInfo eIn2 = eIn;
                                            ASN1Sequence sq = (ASN1Sequence) e7.nextElement();
                                            String alias2 = alias;
                                            ASN1ObjectIdentifier aOid = (ASN1ObjectIdentifier) sq.getObjectAt(0);
                                            ASN1InputStream bIn4 = bIn3;
                                            ASN1Set attrSet = (ASN1Set) sq.getObjectAt(1);
                                            ASN1OctetString localId2 = null;
                                            if (attrSet.size() > 0) {
                                                bag3 = bag4;
                                                ASN1Primitive attr = (ASN1Primitive) attrSet.getObjectAt(0);
                                                if (privKey instanceof PKCS12BagAttributeCarrier) {
                                                    PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) privKey;
                                                    ASN1Encodable existing = bagAttr.getBagAttribute(aOid);
                                                    if (existing == null) {
                                                        bagAttr.setBagAttribute(aOid, attr);
                                                    } else if (!existing.toASN1Primitive().equals(attr)) {
                                                        throw new IOException(str9);
                                                    }
                                                }
                                                localId2 = attr;
                                            } else {
                                                bag3 = bag4;
                                            }
                                            if (aOid.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                                                String alias3 = localId2.getString();
                                                this.keys.put(alias3, privKey);
                                                alias = alias3;
                                            } else if (!aOid.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                                alias = alias2;
                                            } else {
                                                localId = localId2;
                                                alias = alias2;
                                            }
                                            bIn3 = bIn4;
                                            eIn = eIn2;
                                            bag4 = bag3;
                                        }
                                        bIn2 = bIn3;
                                        bag2 = bag4;
                                    } else {
                                        bIn2 = bIn3;
                                        bag2 = bag4;
                                    }
                                    if (localId != null) {
                                        String name = new String(Hex.encode(localId.getOctets()));
                                        if (alias == null) {
                                            this.keys.put(name, privKey);
                                        } else {
                                            this.localIds.put(alias, name);
                                        }
                                    } else {
                                        unmarkedKey = true;
                                        this.keys.put(str8, privKey);
                                    }
                                } else {
                                    head3 = head4;
                                    bIn2 = bIn3;
                                    bag2 = bag4;
                                    if (b2.getBagId().equals((ASN1Primitive) certBag)) {
                                        chain.addElement(b2);
                                    } else {
                                        System.out.println("extra in data " + b2.getBagId());
                                        System.out.println(ASN1Dump.dumpAsString(b2));
                                    }
                                }
                                j++;
                                bIn3 = bIn2;
                                content2 = content;
                                authSafeContent = authSafeContent2;
                                seq3 = seq4;
                                head4 = head3;
                                bag4 = bag2;
                            } else {
                                head = head4;
                                bIn = bIn3;
                                bag = bag4;
                                head2 = i;
                                c = c2;
                                str4 = str9;
                                str5 = str8;
                                wrongPKCS12Zero2 = wrongPKCS12Zero;
                                break;
                            }
                        }
                    } else {
                        content = content2;
                        head = head4;
                        bIn = bIn3;
                        bag = bag4;
                        if (c2[i].getContentType().equals((ASN1Primitive) encryptedData)) {
                            EncryptedData d2 = EncryptedData.getInstance(c2[i].getContent());
                            head2 = i;
                            c = c2;
                            int i2 = 0;
                            String str10 = str9;
                            str5 = str8;
                            byte[] octets2 = cryptData(false, d2.getEncryptionAlgorithm(), password, wrongPKCS12Zero, d2.getContent().getOctets());
                            ASN1Sequence seq5 = ASN1Sequence.getInstance(octets2);
                            int j2 = 0;
                            while (j2 != seq5.size()) {
                                SafeBag b3 = SafeBag.getInstance(seq5.getObjectAt(j2));
                                if (b3.getBagId().equals((ASN1Primitive) certBag)) {
                                    chain.addElement(b3);
                                    d = d2;
                                    octets = octets2;
                                    seq = seq5;
                                    wrongPKCS12Zero3 = wrongPKCS12Zero;
                                    str6 = str10;
                                } else if (b3.getBagId().equals((ASN1Primitive) pkcs8ShroudedKeyBag)) {
                                    EncryptedPrivateKeyInfo eIn3 = EncryptedPrivateKeyInfo.getInstance(b3.getBagValue());
                                    PrivateKey privKey2 = unwrapKey(eIn3.getEncryptionAlgorithm(), eIn3.getEncryptedData(), cArr, wrongPKCS12Zero);
                                    PKCS12BagAttributeCarrier bagAttr2 = (PKCS12BagAttributeCarrier) privKey2;
                                    ASN1OctetString localId3 = null;
                                    Enumeration e8 = b3.getBagAttributes().getObjects();
                                    String alias4 = null;
                                    while (e8.hasMoreElements()) {
                                        EncryptedData d3 = d2;
                                        ASN1Sequence sq2 = (ASN1Sequence) e8.nextElement();
                                        ASN1ObjectIdentifier aOid2 = (ASN1ObjectIdentifier) sq2.getObjectAt(i2);
                                        byte[] octets3 = octets2;
                                        ASN1Set attrSet2 = (ASN1Set) sq2.getObjectAt(1);
                                        ASN1OctetString aSN1OctetString = null;
                                        if (attrSet2.size() > 0) {
                                            ASN1Primitive attr2 = (ASN1Primitive) attrSet2.getObjectAt(0);
                                            ASN1Encodable existing2 = bagAttr2.getBagAttribute(aOid2);
                                            if (existing2 == null) {
                                                seq2 = seq5;
                                                str7 = str10;
                                                bagAttr2.setBagAttribute(aOid2, attr2);
                                            } else if (!existing2.toASN1Primitive().equals(attr2)) {
                                                throw new IOException(str10);
                                            } else {
                                                seq2 = seq5;
                                                str7 = str10;
                                            }
                                            aSN1OctetString = attr2;
                                        } else {
                                            seq2 = seq5;
                                            str7 = str10;
                                        }
                                        if (aOid2.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                                            String alias5 = aSN1OctetString.getString();
                                            this.keys.put(alias5, privKey2);
                                            alias4 = alias5;
                                        } else if (aOid2.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                            localId3 = aSN1OctetString;
                                        }
                                        str10 = str7;
                                        octets2 = octets3;
                                        d2 = d3;
                                        seq5 = seq2;
                                        i2 = 0;
                                    }
                                    d = d2;
                                    octets = octets2;
                                    seq = seq5;
                                    str6 = str10;
                                    String name2 = new String(Hex.encode(localId3.getOctets()));
                                    String alias6 = alias4;
                                    if (alias6 == null) {
                                        this.keys.put(name2, privKey2);
                                    } else {
                                        this.localIds.put(alias6, name2);
                                    }
                                    wrongPKCS12Zero3 = wrongPKCS12Zero;
                                } else {
                                    d = d2;
                                    octets = octets2;
                                    seq = seq5;
                                    str6 = str10;
                                    if (b3.getBagId().equals((ASN1Primitive) keyBag)) {
                                        PrivateKeyInfo kInfo = PrivateKeyInfo.getInstance(b3.getBagValue());
                                        PrivateKey privKey3 = BouncyCastleProvider.getPrivateKey(kInfo);
                                        PKCS12BagAttributeCarrier bagAttr3 = (PKCS12BagAttributeCarrier) privKey3;
                                        String alias7 = null;
                                        ASN1OctetString localId4 = null;
                                        Enumeration e9 = b3.getBagAttributes().getObjects();
                                        while (e9.hasMoreElements()) {
                                            PrivateKeyInfo kInfo2 = kInfo;
                                            ASN1Sequence sq3 = ASN1Sequence.getInstance(e9.nextElement());
                                            boolean wrongPKCS12Zero5 = wrongPKCS12Zero;
                                            ASN1ObjectIdentifier aOid3 = ASN1ObjectIdentifier.getInstance(sq3.getObjectAt(0));
                                            ASN1Set attrSet3 = ASN1Set.getInstance(sq3.getObjectAt(1));
                                            if (attrSet3.size() > 0) {
                                                ASN1Primitive attr3 = (ASN1Primitive) attrSet3.getObjectAt(0);
                                                ASN1Encodable existing3 = bagAttr3.getBagAttribute(aOid3);
                                                if (existing3 == null) {
                                                    bagAttr3.setBagAttribute(aOid3, attr3);
                                                } else if (!existing3.toASN1Primitive().equals(attr3)) {
                                                    throw new IOException(str6);
                                                }
                                                if (aOid3.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                                                    alias7 = ((DERBMPString) attr3).getString();
                                                    this.keys.put(alias7, privKey3);
                                                } else if (aOid3.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                                    localId4 = (ASN1OctetString) attr3;
                                                }
                                            }
                                            wrongPKCS12Zero = wrongPKCS12Zero5;
                                            kInfo = kInfo2;
                                        }
                                        wrongPKCS12Zero3 = wrongPKCS12Zero;
                                        String name3 = new String(Hex.encode(localId4.getOctets()));
                                        if (alias7 == null) {
                                            this.keys.put(name3, privKey3);
                                        } else {
                                            this.localIds.put(alias7, name3);
                                        }
                                    } else {
                                        wrongPKCS12Zero3 = wrongPKCS12Zero;
                                        System.out.println("extra in encryptedData " + b3.getBagId());
                                        System.out.println(ASN1Dump.dumpAsString(b3));
                                    }
                                }
                                j2++;
                                cArr = password;
                                str10 = str6;
                                octets2 = octets;
                                wrongPKCS12Zero = wrongPKCS12Zero3;
                                d2 = d;
                                seq5 = seq;
                                i2 = 0;
                            }
                            wrongPKCS12Zero2 = wrongPKCS12Zero;
                            str4 = str10;
                        } else {
                            head2 = i;
                            c = c2;
                            str4 = str9;
                            str5 = str8;
                            wrongPKCS12Zero2 = wrongPKCS12Zero;
                            System.out.println("extra " + c[head2].getContentType().getId());
                            System.out.println("extra " + ASN1Dump.dumpAsString(c[head2].getContent()));
                        }
                    }
                    i = head2 + 1;
                    cArr = password;
                    str9 = str4;
                    c2 = c;
                    bIn3 = bIn;
                    content2 = content;
                    wrongPKCS12Zero = wrongPKCS12Zero2;
                    head4 = head;
                    bag4 = bag;
                    str8 = str5;
                }
                str2 = str8;
                str = str9;
            }
            this.certs = new IgnoresCaseHashtable();
            this.chainCerts = new Hashtable();
            this.keyCerts = new Hashtable();
            int i3 = 0;
            while (i3 != chain.size()) {
                SafeBag b4 = (SafeBag) chain.elementAt(i3);
                CertBag cb = CertBag.getInstance(b4.getBagValue());
                if (!cb.getCertId().equals((ASN1Primitive) x509Certificate)) {
                    throw new RuntimeException("Unsupported certificate type: " + cb.getCertId());
                }
                try {
                    ByteArrayInputStream cIn = new ByteArrayInputStream(((ASN1OctetString) cb.getCertValue()).getOctets());
                    Certificate cert = this.certFact.generateCertificate(cIn);
                    ASN1OctetString localId5 = null;
                    String alias8 = null;
                    if (b4.getBagAttributes() != null) {
                        Enumeration e10 = b4.getBagAttributes().getObjects();
                        while (e10.hasMoreElements()) {
                            ASN1Sequence sq4 = ASN1Sequence.getInstance(e10.nextElement());
                            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(sq4.getObjectAt(0));
                            ASN1Set attrSet4 = ASN1Set.getInstance(sq4.getObjectAt(1));
                            if (attrSet4.size() <= 0) {
                                b = b4;
                            } else {
                                ASN1Primitive attr4 = (ASN1Primitive) attrSet4.getObjectAt(0);
                                if (!(cert instanceof PKCS12BagAttributeCarrier)) {
                                    b = b4;
                                } else {
                                    PKCS12BagAttributeCarrier bagAttr4 = (PKCS12BagAttributeCarrier) cert;
                                    ASN1Encodable existing4 = bagAttr4.getBagAttribute(oid);
                                    if (existing4 != null) {
                                        b = b4;
                                        if (!existing4.toASN1Primitive().equals(attr4)) {
                                            throw new IOException(str);
                                        }
                                    } else {
                                        b = b4;
                                        bagAttr4.setBagAttribute(oid, attr4);
                                    }
                                }
                                if (oid.equals((ASN1Primitive) pkcs_9_at_friendlyName)) {
                                    alias8 = ((DERBMPString) attr4).getString();
                                } else if (oid.equals((ASN1Primitive) pkcs_9_at_localKeyId)) {
                                    localId5 = (ASN1OctetString) attr4;
                                }
                            }
                            b4 = b;
                        }
                    }
                    this.chainCerts.put(new CertId(cert.getPublicKey()), cert);
                    if (unmarkedKey) {
                        if (!this.keyCerts.isEmpty()) {
                            str3 = str2;
                        } else {
                            String name4 = new String(Hex.encode(createSubjectKeyId(cert.getPublicKey()).getKeyIdentifier()));
                            this.keyCerts.put(name4, cert);
                            IgnoresCaseHashtable ignoresCaseHashtable = this.keys;
                            str3 = str2;
                            ignoresCaseHashtable.put(name4, ignoresCaseHashtable.remove(str3));
                        }
                    } else {
                        str3 = str2;
                        if (localId5 != null) {
                            this.keyCerts.put(new String(Hex.encode(localId5.getOctets())), cert);
                        }
                        if (alias8 != null) {
                            this.certs.put(alias8, cert);
                        }
                    }
                    i3++;
                    str2 = str3;
                } catch (Exception e11) {
                    throw new RuntimeException(e11.toString());
                }
            }
        } catch (Exception e12) {
            throw new IOException(e12.getMessage());
        }
    }

    private int validateIterationCount(BigInteger i) {
        int count = i.intValue();
        if (count < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        BigInteger maxValue = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
        if (maxValue != null && maxValue.intValue() < count) {
            throw new IllegalStateException("iteration count " + count + " greater than " + maxValue.intValue());
        }
        return count;
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(KeyStore.LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        PKCS12StoreParameter bcParam;
        char[] password;
        if (param == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(param instanceof PKCS12StoreParameter) && !(param instanceof JDKPKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + param.getClass().getName());
        }
        if (param instanceof PKCS12StoreParameter) {
            bcParam = (PKCS12StoreParameter) param;
        } else {
            bcParam = new PKCS12StoreParameter(((JDKPKCS12StoreParameter) param).getOutputStream(), param.getProtectionParameter(), ((JDKPKCS12StoreParameter) param).isUseDEREncoding());
        }
        KeyStore.ProtectionParameter protParam = param.getProtectionParameter();
        if (protParam == null) {
            password = null;
        } else if (protParam instanceof KeyStore.PasswordProtection) {
            password = ((KeyStore.PasswordProtection) protParam).getPassword();
        } else {
            throw new IllegalArgumentException("No support for protection parameter of type " + protParam.getClass().getName());
        }
        doStore(bcParam.getOutputStream(), password, bcParam.isForDEREncoding());
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream stream, char[] password) throws IOException {
        doStore(stream, password, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x01e8 A[Catch: CertificateEncodingException -> 0x01cb, TRY_ENTER, TRY_LEAVE, TryCatch #13 {CertificateEncodingException -> 0x01cb, blocks: (B:41:0x01bd, B:51:0x01e8), top: B:177:0x01bd }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x01ff A[Catch: CertificateEncodingException -> 0x0240, TRY_LEAVE, TryCatch #4 {CertificateEncodingException -> 0x0240, blocks: (B:49:0x01e0, B:52:0x01f5, B:53:0x01f9, B:55:0x01ff, B:48:0x01d4), top: B:159:0x01f5 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void doStore(OutputStream stream, char[] password, boolean useDEREncoding) throws IOException {
        DigestInfo dInfo;
        Hashtable doneCerts;
        Enumeration ks;
        byte[] keySEncoded;
        byte[] cSalt;
        Enumeration e;
        if (password == null) {
            throw new NullPointerException("No password supplied for PKCS#12 KeyStore.");
        }
        ASN1EncodableVector keyS = new ASN1EncodableVector();
        Enumeration ks2 = this.keys.keys();
        while (ks2.hasMoreElements()) {
            byte[] kSalt = new byte[20];
            this.random.nextBytes(kSalt);
            String name = (String) ks2.nextElement();
            PrivateKey privKey = (PrivateKey) this.keys.get(name);
            PKCS12PBEParams kParams = new PKCS12PBEParams(kSalt, MIN_ITERATIONS);
            byte[] kBytes = wrapKey(this.keyAlgorithm.getId(), privKey, kParams, password);
            AlgorithmIdentifier kAlgId = new AlgorithmIdentifier(this.keyAlgorithm, kParams.toASN1Primitive());
            EncryptedPrivateKeyInfo kInfo = new EncryptedPrivateKeyInfo(kAlgId, kBytes);
            boolean attrSet = false;
            ASN1EncodableVector kName = new ASN1EncodableVector();
            if (privKey instanceof PKCS12BagAttributeCarrier) {
                PKCS12BagAttributeCarrier bagAttrs = (PKCS12BagAttributeCarrier) privKey;
                DERBMPString nm = (DERBMPString) bagAttrs.getBagAttribute(pkcs_9_at_friendlyName);
                if (nm == null || !nm.getString().equals(name)) {
                    bagAttrs.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name));
                }
                if (bagAttrs.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                    Certificate ct = engineGetCertificate(name);
                    bagAttrs.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(ct.getPublicKey()));
                }
                Enumeration e2 = bagAttrs.getBagAttributeKeys();
                while (e2.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e2.nextElement();
                    ASN1EncodableVector kSeq = new ASN1EncodableVector();
                    kSeq.add(oid);
                    kSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                    attrSet = true;
                    kName.add(new DERSequence(kSeq));
                    e2 = e2;
                    privKey = privKey;
                }
            }
            if (!attrSet) {
                ASN1EncodableVector kSeq2 = new ASN1EncodableVector();
                Certificate ct2 = engineGetCertificate(name);
                kSeq2.add(pkcs_9_at_localKeyId);
                kSeq2.add(new DERSet(createSubjectKeyId(ct2.getPublicKey())));
                kName.add(new DERSequence(kSeq2));
                ASN1EncodableVector kSeq3 = new ASN1EncodableVector();
                kSeq3.add(pkcs_9_at_friendlyName);
                kSeq3.add(new DERSet(new DERBMPString(name)));
                kName.add(new DERSequence(kSeq3));
            }
            SafeBag kBag = new SafeBag(pkcs8ShroudedKeyBag, kInfo.toASN1Primitive(), new DERSet(kName));
            keyS.add(kBag);
        }
        DERSequence dERSequence = new DERSequence(keyS);
        String str = ASN1Encoding.DER;
        byte[] keySEncoded2 = dERSequence.getEncoded(ASN1Encoding.DER);
        BEROctetString keyString = new BEROctetString(keySEncoded2);
        byte[] cSalt2 = new byte[20];
        this.random.nextBytes(cSalt2);
        ASN1EncodableVector certSeq = new ASN1EncodableVector();
        PKCS12PBEParams cParams = new PKCS12PBEParams(cSalt2, MIN_ITERATIONS);
        AlgorithmIdentifier cAlgId = new AlgorithmIdentifier(this.certAlgorithm, cParams.toASN1Primitive());
        Hashtable doneCerts2 = new Hashtable();
        Enumeration cs = this.keys.keys();
        while (cs.hasMoreElements()) {
            try {
                String name2 = (String) cs.nextElement();
                Certificate cert = engineGetCertificate(name2);
                Enumeration cs2 = cs;
                try {
                    boolean cAttrSet = false;
                    ASN1EncodableVector keyS2 = keyS;
                    try {
                        CertBag cBag = new CertBag(x509Certificate, new DEROctetString(cert.getEncoded()));
                        ASN1EncodableVector fName = new ASN1EncodableVector();
                        if (cert instanceof PKCS12BagAttributeCarrier) {
                            PKCS12BagAttributeCarrier bagAttrs2 = (PKCS12BagAttributeCarrier) cert;
                            DERBMPString nm2 = (DERBMPString) bagAttrs2.getBagAttribute(pkcs_9_at_friendlyName);
                            try {
                                if (nm2 != null) {
                                    ks = ks2;
                                    try {
                                        if (nm2.getString().equals(name2)) {
                                            if (bagAttrs2.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                                                bagAttrs2.setBagAttribute(pkcs_9_at_localKeyId, createSubjectKeyId(cert.getPublicKey()));
                                            }
                                            e = bagAttrs2.getBagAttributeKeys();
                                            while (e.hasMoreElements()) {
                                                ASN1ObjectIdentifier oid2 = (ASN1ObjectIdentifier) e.nextElement();
                                                ASN1EncodableVector fSeq = new ASN1EncodableVector();
                                                Enumeration e3 = e;
                                                fSeq.add(oid2);
                                                byte[] keySEncoded3 = keySEncoded2;
                                                try {
                                                    byte[] cSalt3 = cSalt2;
                                                    try {
                                                        fSeq.add(new DERSet(bagAttrs2.getBagAttribute(oid2)));
                                                        fName.add(new DERSequence(fSeq));
                                                        cAttrSet = true;
                                                        e = e3;
                                                        keySEncoded2 = keySEncoded3;
                                                        cSalt2 = cSalt3;
                                                    } catch (CertificateEncodingException e4) {
                                                        e = e4;
                                                        throw new IOException("Error encoding certificate: " + e.toString());
                                                    }
                                                } catch (CertificateEncodingException e5) {
                                                    e = e5;
                                                }
                                            }
                                            keySEncoded = keySEncoded2;
                                            cSalt = cSalt2;
                                        }
                                    } catch (CertificateEncodingException e6) {
                                        e = e6;
                                        throw new IOException("Error encoding certificate: " + e.toString());
                                    }
                                } else {
                                    ks = ks2;
                                }
                                e = bagAttrs2.getBagAttributeKeys();
                                while (e.hasMoreElements()) {
                                }
                                keySEncoded = keySEncoded2;
                                cSalt = cSalt2;
                            } catch (CertificateEncodingException e7) {
                                e = e7;
                            }
                            bagAttrs2.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(name2));
                            if (bagAttrs2.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                            }
                        } else {
                            ks = ks2;
                            keySEncoded = keySEncoded2;
                            cSalt = cSalt2;
                        }
                        if (!cAttrSet) {
                            ASN1EncodableVector fSeq2 = new ASN1EncodableVector();
                            fSeq2.add(pkcs_9_at_localKeyId);
                            fSeq2.add(new DERSet(createSubjectKeyId(cert.getPublicKey())));
                            fName.add(new DERSequence(fSeq2));
                            ASN1EncodableVector fSeq3 = new ASN1EncodableVector();
                            fSeq3.add(pkcs_9_at_friendlyName);
                            fSeq3.add(new DERSet(new DERBMPString(name2)));
                            fName.add(new DERSequence(fSeq3));
                        }
                        SafeBag sBag = new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
                        certSeq.add(sBag);
                        doneCerts2.put(cert, cert);
                        cs = cs2;
                        keyS = keyS2;
                        ks2 = ks;
                        keySEncoded2 = keySEncoded;
                        cSalt2 = cSalt;
                    } catch (CertificateEncodingException e8) {
                        e = e8;
                    }
                } catch (CertificateEncodingException e9) {
                    e = e9;
                }
            } catch (CertificateEncodingException e10) {
                e = e10;
            }
        }
        Enumeration cs3 = this.certs.keys();
        while (cs3.hasMoreElements()) {
            try {
                String certId = (String) cs3.nextElement();
                Certificate cert2 = (Certificate) this.certs.get(certId);
                if (this.keys.get(certId) == null) {
                    SafeBag sBag2 = createSafeBag(certId, cert2);
                    certSeq.add(sBag2);
                    doneCerts2.put(cert2, cert2);
                }
            } catch (CertificateEncodingException e11) {
                throw new IOException("Error encoding certificate: " + e11.toString());
            }
        }
        Set usedSet = getUsedCertificateSet();
        Enumeration cs4 = this.chainCerts.keys();
        while (cs4.hasMoreElements()) {
            try {
                CertId certId2 = (CertId) cs4.nextElement();
                Certificate cert3 = (Certificate) this.chainCerts.get(certId2);
                if (usedSet.contains(cert3) && doneCerts2.get(cert3) == null) {
                    CertBag cBag2 = new CertBag(x509Certificate, new DEROctetString(cert3.getEncoded()));
                    ASN1EncodableVector fName2 = new ASN1EncodableVector();
                    if (cert3 instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier bagAttrs3 = (PKCS12BagAttributeCarrier) cert3;
                        Enumeration e12 = bagAttrs3.getBagAttributeKeys();
                        while (e12.hasMoreElements()) {
                            ASN1ObjectIdentifier oid3 = (ASN1ObjectIdentifier) e12.nextElement();
                            CertId certId3 = certId2;
                            if (oid3.equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                                certId2 = certId3;
                            } else {
                                ASN1EncodableVector fSeq4 = new ASN1EncodableVector();
                                fSeq4.add(oid3);
                                Certificate cert4 = cert3;
                                Hashtable doneCerts3 = doneCerts2;
                                try {
                                    fSeq4.add(new DERSet(bagAttrs3.getBagAttribute(oid3)));
                                    fName2.add(new DERSequence(fSeq4));
                                    certId2 = certId3;
                                    cert3 = cert4;
                                    doneCerts2 = doneCerts3;
                                } catch (CertificateEncodingException e13) {
                                    e = e13;
                                    throw new IOException("Error encoding certificate: " + e.toString());
                                }
                            }
                        }
                        doneCerts = doneCerts2;
                    } else {
                        doneCerts = doneCerts2;
                    }
                    SafeBag sBag3 = new SafeBag(certBag, cBag2.toASN1Primitive(), new DERSet(fName2));
                    certSeq.add(sBag3);
                    doneCerts2 = doneCerts;
                }
            } catch (CertificateEncodingException e14) {
                e = e14;
            }
        }
        byte[] certSeqEncoded = new DERSequence(certSeq).getEncoded(ASN1Encoding.DER);
        byte[] certBytes = cryptData(true, cAlgId, password, false, certSeqEncoded);
        EncryptedData cInfo = new EncryptedData(data, cAlgId, new BEROctetString(certBytes));
        ContentInfo[] info = {new ContentInfo(data, keyString), new ContentInfo(encryptedData, cInfo.toASN1Primitive())};
        AuthenticatedSafe auth = new AuthenticatedSafe(info);
        byte[] pkg = auth.getEncoded(useDEREncoding ? ASN1Encoding.DER : ASN1Encoding.BER);
        ContentInfo mainInfo = new ContentInfo(data, new BEROctetString(pkg));
        byte[] mSalt = new byte[this.saltLength];
        this.random.nextBytes(mSalt);
        byte[] data = ((ASN1OctetString) mainInfo.getContent()).getOctets();
        try {
            try {
                try {
                    byte[] res = calculatePbeMac(this.macAlgorithm.getAlgorithm(), mSalt, this.itCount, password, false, data);
                    dInfo = new DigestInfo(this.macAlgorithm, res);
                } catch (Exception e15) {
                    e = e15;
                }
                try {
                    MacData mData = new MacData(dInfo, mSalt, this.itCount);
                    Pfx pfx = new Pfx(mainInfo, mData);
                    if (!useDEREncoding) {
                        str = ASN1Encoding.BER;
                    }
                    pfx.encodeTo(stream, str);
                } catch (Exception e16) {
                    e = e16;
                    throw new IOException("error constructing MAC: " + e.toString());
                }
            } catch (Exception e17) {
                e = e17;
            }
        } catch (Exception e18) {
            e = e18;
        }
    }

    private SafeBag createSafeBag(String certId, Certificate cert) throws CertificateEncodingException {
        CertBag cBag = new CertBag(x509Certificate, new DEROctetString(cert.getEncoded()));
        ASN1EncodableVector fName = new ASN1EncodableVector();
        boolean cAttrSet = false;
        if (cert instanceof PKCS12BagAttributeCarrier) {
            PKCS12BagAttributeCarrier bagAttrs = (PKCS12BagAttributeCarrier) cert;
            DERBMPString nm = (DERBMPString) bagAttrs.getBagAttribute(pkcs_9_at_friendlyName);
            if ((nm == null || !nm.getString().equals(certId)) && certId != null) {
                bagAttrs.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(certId));
            }
            Enumeration e = bagAttrs.getBagAttributeKeys();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                if (!oid.equals((ASN1Primitive) PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                    ASN1EncodableVector fSeq = new ASN1EncodableVector();
                    fSeq.add(oid);
                    fSeq.add(new DERSet(bagAttrs.getBagAttribute(oid)));
                    fName.add(new DERSequence(fSeq));
                    cAttrSet = true;
                }
            }
        }
        if (!cAttrSet) {
            ASN1EncodableVector fSeq2 = new ASN1EncodableVector();
            fSeq2.add(pkcs_9_at_friendlyName);
            fSeq2.add(new DERSet(new DERBMPString(certId)));
            fName.add(new DERSequence(fSeq2));
        }
        return new SafeBag(certBag, cBag.toASN1Primitive(), new DERSet(fName));
    }

    private Set getUsedCertificateSet() {
        Set usedSet = new HashSet();
        Enumeration en = this.keys.keys();
        while (en.hasMoreElements()) {
            String alias = (String) en.nextElement();
            Certificate[] certs = engineGetCertificateChain(alias);
            for (int i = 0; i != certs.length; i++) {
                usedSet.add(certs[i]);
            }
        }
        Enumeration en2 = this.certs.keys();
        while (en2.hasMoreElements()) {
            String alias2 = (String) en2.nextElement();
            Certificate cert = engineGetCertificate(alias2);
            usedSet.add(cert);
        }
        return usedSet;
    }

    private byte[] calculatePbeMac(ASN1ObjectIdentifier oid, byte[] salt, int itCount, char[] password, boolean wrongPkcs12Zero, byte[] data) throws Exception {
        PBEParameterSpec defParams = new PBEParameterSpec(salt, itCount);
        Mac mac = this.selfHelper.createMac(oid.getId());
        mac.init(new PKCS12Key(password, wrongPkcs12Zero), defParams);
        mac.update(data);
        return mac.doFinal();
    }

    /* loaded from: classes4.dex */
    public static class BCPKCS12KeyStore extends PKCS12KeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new DefaultJcaJceHelper(), pbeWithSHAAnd3_KeyTripleDES_CBC, pbeWithSHAAnd40BitRC2_CBC);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class IgnoresCaseHashtable {
        private Hashtable keys;
        private Hashtable orig;

        private IgnoresCaseHashtable() {
            this.orig = new Hashtable();
            this.keys = new Hashtable();
        }

        public void put(String key, Object value) {
            String lower = key == null ? null : Strings.toLowerCase(key);
            String k = (String) this.keys.get(lower);
            if (k != null) {
                this.orig.remove(k);
            }
            this.keys.put(lower, key);
            this.orig.put(key, value);
        }

        public Enumeration keys() {
            return this.orig.keys();
        }

        public Object remove(String alias) {
            String k = (String) this.keys.remove(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.remove(k);
        }

        public Object get(String alias) {
            String k = (String) this.keys.get(alias == null ? null : Strings.toLowerCase(alias));
            if (k == null) {
                return null;
            }
            return this.orig.get(k);
        }

        public Enumeration elements() {
            return this.orig.elements();
        }

        public int size() {
            return this.orig.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            Map keySizes = new HashMap();
            keySizes.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
            keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(keySizes);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer keySize = (Integer) this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (keySize != null) {
                return keySize.intValue();
            }
            return -1;
        }
    }
}
