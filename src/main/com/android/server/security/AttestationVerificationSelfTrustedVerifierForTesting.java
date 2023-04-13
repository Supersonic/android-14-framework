package com.android.server.security;

import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import android.util.Slog;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class AttestationVerificationSelfTrustedVerifierForTesting {
    public static final boolean DEBUG;
    public static final String GOLDEN_ALIAS;
    public static volatile AttestationVerificationSelfTrustedVerifierForTesting sAttestationVerificationSelfTrustedVerifier;
    public final KeyStore mAndroidKeyStore;
    public X509Certificate mGoldenRootCert;
    public final CertificateFactory mCertificateFactory = CertificateFactory.getInstance("X.509");
    public final CertPathValidator mCertPathValidator = CertPathValidator.getInstance("PKIX");

    static {
        DEBUG = Build.IS_DEBUGGABLE && Log.isLoggable("AVF", 2);
        GOLDEN_ALIAS = AttestationVerificationSelfTrustedVerifierForTesting.class.getCanonicalName() + ".Golden";
        sAttestationVerificationSelfTrustedVerifier = null;
    }

    public static AttestationVerificationSelfTrustedVerifierForTesting getInstance() throws Exception {
        if (sAttestationVerificationSelfTrustedVerifier == null) {
            synchronized (AttestationVerificationSelfTrustedVerifierForTesting.class) {
                if (sAttestationVerificationSelfTrustedVerifier == null) {
                    sAttestationVerificationSelfTrustedVerifier = new AttestationVerificationSelfTrustedVerifierForTesting();
                }
            }
        }
        return sAttestationVerificationSelfTrustedVerifier;
    }

    public static void debugVerboseLog(String str, Throwable th) {
        if (DEBUG) {
            Slog.v("AVF", str, th);
        }
    }

    public static void debugVerboseLog(String str) {
        if (DEBUG) {
            Slog.v("AVF", str);
        }
    }

    public AttestationVerificationSelfTrustedVerifierForTesting() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        this.mAndroidKeyStore = keyStore;
        keyStore.load(null);
        String str = GOLDEN_ALIAS;
        if (!keyStore.containsAlias(str)) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "AndroidKeyStore");
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(str, 12).setAttestationChallenge(str.getBytes()).setDigests("SHA-256", "SHA-512").build());
            keyPairGenerator.generateKeyPair();
        }
        X509Certificate[] x509CertificateArr = (X509Certificate[]) ((KeyStore.PrivateKeyEntry) keyStore.getEntry(str, null)).getCertificateChain();
        this.mGoldenRootCert = x509CertificateArr[x509CertificateArr.length - 1];
    }

    public int verifyAttestation(int i, Bundle bundle, byte[] bArr) {
        ArrayList arrayList = new ArrayList();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        while (byteArrayInputStream.available() > 0) {
            try {
                arrayList.add((X509Certificate) this.mCertificateFactory.generateCertificate(byteArrayInputStream));
            } catch (CertificateException e) {
                debugVerboseLog("Unable to parse certificates from attestation", e);
                return 2;
            }
        }
        return (i == 3 && validateRequirements(bundle) && checkLeafChallenge(bundle, arrayList) && verifyCertificateChain(arrayList)) ? 1 : 2;
    }

    public final boolean verifyCertificateChain(List<X509Certificate> list) {
        if (list.size() < 2) {
            debugVerboseLog("Certificate chain less than 2 in size.");
            return false;
        }
        try {
            CertPath generateCertPath = this.mCertificateFactory.generateCertPath(list);
            PKIXParameters pKIXParameters = new PKIXParameters(getTrustAnchors());
            pKIXParameters.setRevocationEnabled(false);
            this.mCertPathValidator.validate(generateCertPath, pKIXParameters);
            return true;
        } catch (Throwable th) {
            debugVerboseLog("Invalid certificate chain", th);
            return false;
        }
    }

    public final Set<TrustAnchor> getTrustAnchors() {
        return Collections.singleton(new TrustAnchor(this.mGoldenRootCert, null));
    }

    public final boolean validateRequirements(Bundle bundle) {
        if (bundle.size() != 1) {
            debugVerboseLog("Requirements does not contain exactly 1 key.");
            return false;
        } else if (bundle.containsKey("localbinding.challenge")) {
            return true;
        } else {
            debugVerboseLog("Requirements does not contain key: localbinding.challenge");
            return false;
        }
    }

    public final boolean checkLeafChallenge(Bundle bundle, List<X509Certificate> list) {
        try {
            if (Arrays.equals(bundle.getByteArray("localbinding.challenge"), getChallengeFromCert(list.get(0)))) {
                return true;
            }
            debugVerboseLog("Self-Trusted validation failed; challenge mismatch.");
            return false;
        } catch (Throwable th) {
            debugVerboseLog("Unable to parse challenge from certificate.", th);
            return false;
        }
    }

    public final byte[] getChallengeFromCert(X509Certificate x509Certificate) throws CertificateEncodingException, IOException {
        return Certificate.getInstance(new ASN1InputStream(x509Certificate.getEncoded()).readObject()).getTBSCertificate().getExtensions().getExtensionParsedValue(new ASN1ObjectIdentifier("1.3.6.1.4.1.11129.2.1.17")).getObjectAt(4).getOctets();
    }
}
