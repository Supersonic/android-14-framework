package com.android.internal.org.bouncycastle.jcajce.provider.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.util.Strings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public class DigestFactory {
    private static Set md5 = new HashSet();
    private static Set sha1 = new HashSet();
    private static Set sha224 = new HashSet();
    private static Set sha256 = new HashSet();
    private static Set sha384 = new HashSet();
    private static Set sha512 = new HashSet();
    private static Map oids = new HashMap();

    static {
        md5.add(KeyProperties.DIGEST_MD5);
        md5.add(PKCSObjectIdentifiers.md5.getId());
        sha1.add("SHA1");
        sha1.add("SHA-1");
        sha1.add(OIWObjectIdentifiers.idSHA1.getId());
        sha224.add("SHA224");
        sha224.add(KeyProperties.DIGEST_SHA224);
        sha224.add(NISTObjectIdentifiers.id_sha224.getId());
        sha256.add("SHA256");
        sha256.add("SHA-256");
        sha256.add(NISTObjectIdentifiers.id_sha256.getId());
        sha384.add("SHA384");
        sha384.add(KeyProperties.DIGEST_SHA384);
        sha384.add(NISTObjectIdentifiers.id_sha384.getId());
        sha512.add("SHA512");
        sha512.add(KeyProperties.DIGEST_SHA512);
        sha512.add(NISTObjectIdentifiers.id_sha512.getId());
        oids.put(KeyProperties.DIGEST_MD5, PKCSObjectIdentifiers.md5);
        oids.put(PKCSObjectIdentifiers.md5.getId(), PKCSObjectIdentifiers.md5);
        oids.put("SHA1", OIWObjectIdentifiers.idSHA1);
        oids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        oids.put(OIWObjectIdentifiers.idSHA1.getId(), OIWObjectIdentifiers.idSHA1);
        oids.put("SHA224", NISTObjectIdentifiers.id_sha224);
        oids.put(KeyProperties.DIGEST_SHA224, NISTObjectIdentifiers.id_sha224);
        oids.put(NISTObjectIdentifiers.id_sha224.getId(), NISTObjectIdentifiers.id_sha224);
        oids.put("SHA256", NISTObjectIdentifiers.id_sha256);
        oids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        oids.put(NISTObjectIdentifiers.id_sha256.getId(), NISTObjectIdentifiers.id_sha256);
        oids.put("SHA384", NISTObjectIdentifiers.id_sha384);
        oids.put(KeyProperties.DIGEST_SHA384, NISTObjectIdentifiers.id_sha384);
        oids.put(NISTObjectIdentifiers.id_sha384.getId(), NISTObjectIdentifiers.id_sha384);
        oids.put("SHA512", NISTObjectIdentifiers.id_sha512);
        oids.put(KeyProperties.DIGEST_SHA512, NISTObjectIdentifiers.id_sha512);
        oids.put(NISTObjectIdentifiers.id_sha512.getId(), NISTObjectIdentifiers.id_sha512);
        oids.put("SHA512(224)", NISTObjectIdentifiers.id_sha512_224);
        oids.put("SHA-512(224)", NISTObjectIdentifiers.id_sha512_224);
        oids.put(NISTObjectIdentifiers.id_sha512_224.getId(), NISTObjectIdentifiers.id_sha512_224);
        oids.put("SHA512(256)", NISTObjectIdentifiers.id_sha512_256);
        oids.put("SHA-512(256)", NISTObjectIdentifiers.id_sha512_256);
        oids.put(NISTObjectIdentifiers.id_sha512_256.getId(), NISTObjectIdentifiers.id_sha512_256);
        oids.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        oids.put(NISTObjectIdentifiers.id_sha3_224.getId(), NISTObjectIdentifiers.id_sha3_224);
        oids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        oids.put(NISTObjectIdentifiers.id_sha3_256.getId(), NISTObjectIdentifiers.id_sha3_256);
        oids.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        oids.put(NISTObjectIdentifiers.id_sha3_384.getId(), NISTObjectIdentifiers.id_sha3_384);
        oids.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        oids.put(NISTObjectIdentifiers.id_sha3_512.getId(), NISTObjectIdentifiers.id_sha3_512);
    }

    public static Digest getDigest(String digestName) {
        String digestName2 = Strings.toUpperCase(digestName);
        if (sha1.contains(digestName2)) {
            return AndroidDigestFactory.getSHA1();
        }
        if (md5.contains(digestName2)) {
            return AndroidDigestFactory.getMD5();
        }
        if (sha224.contains(digestName2)) {
            return AndroidDigestFactory.getSHA224();
        }
        if (sha256.contains(digestName2)) {
            return AndroidDigestFactory.getSHA256();
        }
        if (sha384.contains(digestName2)) {
            return AndroidDigestFactory.getSHA384();
        }
        if (sha512.contains(digestName2)) {
            return AndroidDigestFactory.getSHA512();
        }
        return null;
    }

    public static boolean isSameDigest(String digest1, String digest2) {
        return (sha1.contains(digest1) && sha1.contains(digest2)) || (sha224.contains(digest1) && sha224.contains(digest2)) || ((sha256.contains(digest1) && sha256.contains(digest2)) || ((sha384.contains(digest1) && sha384.contains(digest2)) || ((sha512.contains(digest1) && sha512.contains(digest2)) || (md5.contains(digest1) && md5.contains(digest2)))));
    }

    public static ASN1ObjectIdentifier getOID(String digestName) {
        return (ASN1ObjectIdentifier) oids.get(digestName);
    }
}
