package com.android.internal.org.bouncycastle.jcajce.provider.keystore.p024bc;

import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.digests.SHA1Digest;
import com.android.internal.org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.macs.HMac;
import com.android.internal.org.bouncycastle.crypto.p020io.DigestInputStream;
import com.android.internal.org.bouncycastle.crypto.p020io.DigestOutputStream;
import com.android.internal.org.bouncycastle.crypto.p020io.MacInputStream;
import com.android.internal.org.bouncycastle.crypto.p020io.MacOutputStream;
import com.android.internal.org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.interfaces.BCKeyStore;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.p027io.Streams;
import com.android.internal.org.bouncycastle.util.p027io.TeeOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi */
/* loaded from: classes4.dex */
public class BcKeyStoreSpi extends KeyStoreSpi implements BCKeyStore {
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    private static final String KEY_CIPHER = "PBEWithSHAAnd3-KeyTripleDES-CBC";
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    private static final int KEY_SALT_SIZE = 20;
    static final int KEY_SECRET = 2;
    private static final int MIN_ITERATIONS = 1024;
    static final int NULL = 0;
    static final int SEALED = 4;
    static final int SECRET = 3;
    private static final String STORE_CIPHER = "PBEWithSHAAndTwofish-CBC";
    private static final int STORE_SALT_SIZE = 20;
    private static final int STORE_VERSION = 2;
    protected int version;
    protected Hashtable table = new Hashtable();
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private final JcaJceHelper helper = new DefaultJcaJceHelper();

    public BcKeyStoreSpi(int version) {
        this.version = version;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$StoreEntry */
    /* loaded from: classes4.dex */
    public class StoreEntry {
        String alias;
        Certificate[] certChain;
        Date date;
        Object obj;
        int type;

        StoreEntry(String alias, Certificate obj) {
            this.date = new Date();
            this.type = 1;
            this.alias = alias;
            this.obj = obj;
            this.certChain = null;
        }

        StoreEntry(String alias, byte[] obj, Certificate[] certChain) {
            this.date = new Date();
            this.type = 3;
            this.alias = alias;
            this.obj = obj;
            this.certChain = certChain;
        }

        StoreEntry(String alias, Key key, char[] password, Certificate[] certChain) throws Exception {
            this.date = new Date();
            this.type = 4;
            this.alias = alias;
            this.certChain = certChain;
            byte[] salt = new byte[20];
            BcKeyStoreSpi.this.random.nextBytes(salt);
            int iterationCount = (BcKeyStoreSpi.this.random.nextInt() & 1023) + 1024;
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(bOut);
            dOut.writeInt(salt.length);
            dOut.write(salt);
            dOut.writeInt(iterationCount);
            Cipher cipher = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 1, password, salt, iterationCount);
            CipherOutputStream cOut = new CipherOutputStream(dOut, cipher);
            DataOutputStream dOut2 = new DataOutputStream(cOut);
            BcKeyStoreSpi.this.encodeKey(key, dOut2);
            dOut2.close();
            this.obj = bOut.toByteArray();
        }

        StoreEntry(String alias, Date date, int type, Object obj) {
            this.date = new Date();
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
        }

        StoreEntry(String alias, Date date, int type, Object obj, Certificate[] certChain) {
            this.date = new Date();
            this.alias = alias;
            this.date = date;
            this.type = type;
            this.obj = obj;
            this.certChain = certChain;
        }

        int getType() {
            return this.type;
        }

        String getAlias() {
            return this.alias;
        }

        Object getObject() {
            return this.obj;
        }

        Object getObject(char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
            Key k;
            if (password == null || password.length == 0) {
                Object obj = this.obj;
                if (obj instanceof Key) {
                    return obj;
                }
            }
            if (this.type == 4) {
                ByteArrayInputStream bIn = new ByteArrayInputStream((byte[]) this.obj);
                DataInputStream dIn = new DataInputStream(bIn);
                try {
                    byte[] salt = new byte[dIn.readInt()];
                    dIn.readFully(salt);
                    Cipher cipher = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 2, password, salt, dIn.readInt());
                    CipherInputStream cIn = new CipherInputStream(dIn, cipher);
                    try {
                        return BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn));
                    } catch (Exception e) {
                        ByteArrayInputStream bIn2 = new ByteArrayInputStream((byte[]) this.obj);
                        DataInputStream dIn2 = new DataInputStream(bIn2);
                        byte[] salt2 = new byte[dIn2.readInt()];
                        dIn2.readFully(salt2);
                        int iterationCount = dIn2.readInt();
                        Cipher cipher2 = BcKeyStoreSpi.this.makePBECipher("BrokenPBEWithSHAAnd3-KeyTripleDES-CBC", 2, password, salt2, iterationCount);
                        CipherInputStream cIn2 = new CipherInputStream(dIn2, cipher2);
                        try {
                            k = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn2));
                        } catch (Exception e2) {
                            ByteArrayInputStream bIn3 = new ByteArrayInputStream((byte[]) this.obj);
                            DataInputStream dIn3 = new DataInputStream(bIn3);
                            salt2 = new byte[dIn3.readInt()];
                            dIn3.readFully(salt2);
                            iterationCount = dIn3.readInt();
                            Cipher cipher3 = BcKeyStoreSpi.this.makePBECipher("OldPBEWithSHAAnd3-KeyTripleDES-CBC", 2, password, salt2, iterationCount);
                            CipherInputStream cIn3 = new CipherInputStream(dIn3, cipher3);
                            k = BcKeyStoreSpi.this.decodeKey(new DataInputStream(cIn3));
                        }
                        if (k != null) {
                            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                            DataOutputStream dOut = new DataOutputStream(bOut);
                            dOut.writeInt(salt2.length);
                            dOut.write(salt2);
                            dOut.writeInt(iterationCount);
                            Cipher out = BcKeyStoreSpi.this.makePBECipher(BcKeyStoreSpi.KEY_CIPHER, 1, password, salt2, iterationCount);
                            CipherOutputStream cOut = new CipherOutputStream(dOut, out);
                            DataOutputStream dOut2 = new DataOutputStream(cOut);
                            BcKeyStoreSpi.this.encodeKey(k, dOut2);
                            dOut2.close();
                            this.obj = bOut.toByteArray();
                            return k;
                        }
                        throw new UnrecoverableKeyException("no match");
                    }
                } catch (Exception e3) {
                    throw new UnrecoverableKeyException("no match");
                }
            }
            throw new RuntimeException("forget something!");
        }

        Certificate[] getCertificateChain() {
            return this.certChain;
        }

        Date getDate() {
            return this.date;
        }
    }

    private void encodeCertificate(Certificate cert, DataOutputStream dOut) throws IOException {
        try {
            byte[] cEnc = cert.getEncoded();
            dOut.writeUTF(cert.getType());
            dOut.writeInt(cEnc.length);
            dOut.write(cEnc);
        } catch (CertificateEncodingException ex) {
            throw new IOException(ex.toString());
        }
    }

    private Certificate decodeCertificate(DataInputStream dIn) throws IOException {
        String type = dIn.readUTF();
        byte[] cEnc = new byte[dIn.readInt()];
        dIn.readFully(cEnc);
        try {
            CertificateFactory cFact = this.helper.createCertificateFactory(type);
            ByteArrayInputStream bIn = new ByteArrayInputStream(cEnc);
            return cFact.generateCertificate(bIn);
        } catch (NoSuchProviderException ex) {
            throw new IOException(ex.toString());
        } catch (CertificateException ex2) {
            throw new IOException(ex2.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void encodeKey(Key key, DataOutputStream dOut) throws IOException {
        byte[] enc = key.getEncoded();
        if (key instanceof PrivateKey) {
            dOut.write(0);
        } else if (key instanceof PublicKey) {
            dOut.write(1);
        } else {
            dOut.write(2);
        }
        dOut.writeUTF(key.getFormat());
        dOut.writeUTF(key.getAlgorithm());
        dOut.writeInt(enc.length);
        dOut.write(enc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Key decodeKey(DataInputStream dIn) throws IOException {
        KeySpec spec;
        int keyType = dIn.read();
        String format = dIn.readUTF();
        String algorithm = dIn.readUTF();
        byte[] enc = new byte[dIn.readInt()];
        dIn.readFully(enc);
        if (format.equals("PKCS#8") || format.equals("PKCS8")) {
            spec = new PKCS8EncodedKeySpec(enc);
        } else if (format.equals("X.509") || format.equals("X509")) {
            spec = new X509EncodedKeySpec(enc);
        } else if (format.equals("RAW")) {
            return new SecretKeySpec(enc, algorithm);
        } else {
            throw new IOException("Key format " + format + " not recognised!");
        }
        try {
            switch (keyType) {
                case 0:
                    return BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(enc));
                case 1:
                    return BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(enc));
                case 2:
                    return this.helper.createSecretKeyFactory(algorithm).generateSecret(spec);
                default:
                    throw new IOException("Key type " + keyType + " not recognised!");
            }
        } catch (Exception e) {
            throw new IOException("Exception creating key: " + e.toString());
        }
    }

    protected Cipher makePBECipher(String algorithm, int mode, char[] password, byte[] salt, int iterationCount) throws IOException {
        try {
            PBEKeySpec pbeSpec = new PBEKeySpec(password);
            SecretKeyFactory keyFact = this.helper.createSecretKeyFactory(algorithm);
            PBEParameterSpec defParams = new PBEParameterSpec(salt, iterationCount);
            Cipher cipher = this.helper.createCipher(algorithm);
            cipher.init(mode, keyFact.generateSecret(pbeSpec), defParams);
            return cipher;
        } catch (Exception e) {
            throw new IOException("Error initialising store of key store: " + e);
        }
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.BCKeyStore
    public void setRandom(SecureRandom rand) {
        this.random = rand;
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration engineAliases() {
        return this.table.keys();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        return this.table.get(alias) != null;
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        Object entry = this.table.get(alias);
        if (entry == null) {
            return;
        }
        this.table.remove(alias);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null) {
            if (entry.getType() == 1) {
                return (Certificate) entry.getObject();
            }
            Certificate[] chain = entry.getCertificateChain();
            if (chain != null) {
                return chain[0];
            }
            return null;
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate cert) {
        Enumeration e = this.table.elements();
        while (e.hasMoreElements()) {
            StoreEntry entry = (StoreEntry) e.nextElement();
            if (entry.getObject() instanceof Certificate) {
                Certificate c = (Certificate) entry.getObject();
                if (c.equals(cert)) {
                    return entry.getAlias();
                }
            } else {
                Certificate[] chain = entry.getCertificateChain();
                if (chain != null && chain[0].equals(cert)) {
                    return entry.getAlias();
                }
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null) {
            return entry.getCertificateChain();
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null) {
            return entry.getDate();
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null || entry.getType() == 1) {
            return null;
        }
        return (Key) entry.getObject(password);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        return entry != null && entry.getType() == 1;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        return (entry == null || entry.getType() == 1) ? false : true;
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null && entry.getType() != 1) {
            throw new KeyStoreException("key store already has a key entry with alias " + alias);
        }
        this.table.put(alias, new StoreEntry(alias, cert));
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
        this.table.put(alias, new StoreEntry(alias, key, chain));
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if ((key instanceof PrivateKey) && chain == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        try {
            this.table.put(alias, new StoreEntry(alias, key, password, chain));
        } catch (Exception e) {
            throw new BCKeyStoreException(e.toString(), e);
        }
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.table.size();
    }

    protected void loadStore(InputStream in) throws IOException {
        Certificate[] chain;
        DataInputStream dIn = new DataInputStream(in);
        for (int type = dIn.read(); type > 0; type = dIn.read()) {
            String alias = dIn.readUTF();
            Date date = new Date(dIn.readLong());
            int chainLength = dIn.readInt();
            if (chainLength == 0) {
                chain = null;
            } else {
                Certificate[] chain2 = new Certificate[chainLength];
                for (int i = 0; i != chainLength; i++) {
                    chain2[i] = decodeCertificate(dIn);
                }
                chain = chain2;
            }
            switch (type) {
                case 1:
                    Certificate cert = decodeCertificate(dIn);
                    this.table.put(alias, new StoreEntry(alias, date, 1, cert));
                    break;
                case 2:
                    Key key = decodeKey(dIn);
                    this.table.put(alias, new StoreEntry(alias, date, 2, key, chain));
                    break;
                case 3:
                case 4:
                    byte[] b = new byte[dIn.readInt()];
                    dIn.readFully(b);
                    this.table.put(alias, new StoreEntry(alias, date, type, b, chain));
                    break;
                default:
                    throw new IOException("Unknown object type in store.");
            }
        }
    }

    protected void saveStore(OutputStream out) throws IOException {
        Enumeration e = this.table.elements();
        DataOutputStream dOut = new DataOutputStream(out);
        while (e.hasMoreElements()) {
            StoreEntry entry = (StoreEntry) e.nextElement();
            dOut.write(entry.getType());
            dOut.writeUTF(entry.getAlias());
            dOut.writeLong(entry.getDate().getTime());
            Certificate[] chain = entry.getCertificateChain();
            if (chain == null) {
                dOut.writeInt(0);
            } else {
                dOut.writeInt(chain.length);
                for (int i = 0; i != chain.length; i++) {
                    encodeCertificate(chain[i], dOut);
                }
            }
            int i2 = entry.getType();
            switch (i2) {
                case 1:
                    encodeCertificate((Certificate) entry.getObject(), dOut);
                    break;
                case 2:
                    encodeKey((Key) entry.getObject(), dOut);
                    break;
                case 3:
                case 4:
                    byte[] b = (byte[]) entry.getObject();
                    dOut.writeInt(b.length);
                    dOut.write(b);
                    break;
                default:
                    throw new IOException("Unknown object type in store.");
            }
        }
        dOut.write(0);
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        CipherParameters macParams;
        this.table.clear();
        if (stream == null) {
            return;
        }
        DataInputStream dIn = new DataInputStream(stream);
        int version = dIn.readInt();
        if (version != 2 && version != 0 && version != 1) {
            throw new IOException("Wrong version of key store.");
        }
        int saltLength = dIn.readInt();
        if (saltLength <= 0) {
            throw new IOException("Invalid salt detected");
        }
        byte[] salt = new byte[saltLength];
        dIn.readFully(salt);
        int iterationCount = dIn.readInt();
        HMac hMac = new HMac(new SHA1Digest());
        if (password != null && password.length != 0) {
            byte[] passKey = PBEParametersGenerator.PKCS12PasswordToBytes(password);
            PBEParametersGenerator pbeGen = new PKCS12ParametersGenerator(new SHA1Digest());
            pbeGen.init(passKey, salt, iterationCount);
            if (version != 2) {
                macParams = pbeGen.generateDerivedMacParameters(hMac.getMacSize());
            } else {
                macParams = pbeGen.generateDerivedMacParameters(hMac.getMacSize() * 8);
            }
            Arrays.fill(passKey, (byte) 0);
            hMac.init(macParams);
            MacInputStream mIn = new MacInputStream(dIn, hMac);
            loadStore(mIn);
            byte[] mac = new byte[hMac.getMacSize()];
            hMac.doFinal(mac, 0);
            byte[] oldMac = new byte[hMac.getMacSize()];
            dIn.readFully(oldMac);
            if (!Arrays.constantTimeAreEqual(mac, oldMac)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
            return;
        }
        loadStore(dIn);
        dIn.readFully(new byte[hMac.getMacSize()]);
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream stream, char[] password) throws IOException {
        DataOutputStream dOut = new DataOutputStream(stream);
        byte[] salt = new byte[20];
        int iterationCount = (this.random.nextInt() & 1023) + 1024;
        this.random.nextBytes(salt);
        dOut.writeInt(this.version);
        dOut.writeInt(salt.length);
        dOut.write(salt);
        dOut.writeInt(iterationCount);
        HMac hMac = new HMac(new SHA1Digest());
        MacOutputStream mOut = new MacOutputStream(hMac);
        PBEParametersGenerator pbeGen = new PKCS12ParametersGenerator(new SHA1Digest());
        byte[] passKey = PBEParametersGenerator.PKCS12PasswordToBytes(password);
        pbeGen.init(passKey, salt, iterationCount);
        if (this.version < 2) {
            hMac.init(pbeGen.generateDerivedMacParameters(hMac.getMacSize()));
        } else {
            hMac.init(pbeGen.generateDerivedMacParameters(hMac.getMacSize() * 8));
        }
        for (int i = 0; i != passKey.length; i++) {
            passKey[i] = 0;
        }
        saveStore(new TeeOutputStream(dOut, mOut));
        byte[] mac = new byte[hMac.getMacSize()];
        hMac.doFinal(mac, 0);
        dOut.write(mac);
        dOut.close();
    }

    public boolean engineProbe(InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("input stream must not be null");
        }
        DataInputStream dIn = new DataInputStream(stream);
        int version = dIn.readInt();
        if (version == 2 || version == 0 || version == 1) {
            byte[] salt = new byte[dIn.readInt()];
            return salt.length == 20;
        }
        return false;
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$BouncyCastleStore */
    /* loaded from: classes4.dex */
    public static class BouncyCastleStore extends BcKeyStoreSpi {
        public BouncyCastleStore() {
            super(1);
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.keystore.p024bc.BcKeyStoreSpi, java.security.KeyStoreSpi
        public void engineLoad(InputStream stream, char[] password) throws IOException {
            String cipherAlg;
            this.table.clear();
            if (stream == null) {
                return;
            }
            DataInputStream dIn = new DataInputStream(stream);
            int version = dIn.readInt();
            if (version != 2 && version != 0 && version != 1) {
                throw new IOException("Wrong version of key store.");
            }
            byte[] salt = new byte[dIn.readInt()];
            if (salt.length != 20) {
                throw new IOException("Key store corrupted.");
            }
            dIn.readFully(salt);
            int iterationCount = dIn.readInt();
            if (iterationCount < 0 || iterationCount > 65536) {
                throw new IOException("Key store corrupted.");
            }
            if (version == 0) {
                cipherAlg = "OldPBEWithSHAAndTwofish-CBC";
            } else {
                cipherAlg = BcKeyStoreSpi.STORE_CIPHER;
            }
            Cipher cipher = makePBECipher(cipherAlg, 2, password, salt, iterationCount);
            CipherInputStream cIn = new CipherInputStream(dIn, cipher);
            Digest dig = new SHA1Digest();
            DigestInputStream dgIn = new DigestInputStream(cIn, dig);
            loadStore(dgIn);
            byte[] hash = new byte[dig.getDigestSize()];
            dig.doFinal(hash, 0);
            byte[] oldHash = new byte[dig.getDigestSize()];
            Streams.readFully(cIn, oldHash);
            if (!Arrays.constantTimeAreEqual(hash, oldHash)) {
                this.table.clear();
                throw new IOException("KeyStore integrity check failed.");
            }
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.keystore.p024bc.BcKeyStoreSpi, java.security.KeyStoreSpi
        public void engineStore(OutputStream stream, char[] password) throws IOException {
            DataOutputStream dOut = new DataOutputStream(stream);
            byte[] salt = new byte[20];
            int iterationCount = (this.random.nextInt() & 1023) + 1024;
            this.random.nextBytes(salt);
            dOut.writeInt(this.version);
            dOut.writeInt(salt.length);
            dOut.write(salt);
            dOut.writeInt(iterationCount);
            Cipher cipher = makePBECipher(BcKeyStoreSpi.STORE_CIPHER, 1, password, salt, iterationCount);
            CipherOutputStream cOut = new CipherOutputStream(dOut, cipher);
            DigestOutputStream dgOut = new DigestOutputStream(new SHA1Digest());
            saveStore(new TeeOutputStream(cOut, dgOut));
            byte[] dig = dgOut.getDigest();
            cOut.write(dig);
            cOut.close();
        }

        @Override // com.android.internal.org.bouncycastle.jcajce.provider.keystore.p024bc.BcKeyStoreSpi
        public boolean engineProbe(InputStream stream) throws IOException {
            if (stream == null) {
                throw new NullPointerException("input stream must not be null");
            }
            DataInputStream dIn = new DataInputStream(stream);
            int version = dIn.readInt();
            if (version == 2 || version == 0 || version == 1) {
                byte[] salt = new byte[dIn.readInt()];
                return salt.length == 20;
            }
            return false;
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Std */
    /* loaded from: classes4.dex */
    public static class Std extends BcKeyStoreSpi {
        public Std() {
            super(2);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Version1 */
    /* loaded from: classes4.dex */
    public static class Version1 extends BcKeyStoreSpi {
        public Version1() {
            super(1);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$BCKeyStoreException */
    /* loaded from: classes4.dex */
    private static class BCKeyStoreException extends KeyStoreException {
        private final Exception cause;

        public BCKeyStoreException(String msg, Exception cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
