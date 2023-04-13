package android.security.keystore;

import android.annotation.SystemApi;
import android.content.Context;
import android.security.keymaster.KeymasterCertificateChain;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore2.AndroidKeyStoreSpi;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AttestationUtils {
    public static final int ID_TYPE_IMEI = 2;
    public static final int ID_TYPE_MEID = 3;
    public static final int ID_TYPE_SERIAL = 1;
    public static final int USE_INDIVIDUAL_ATTESTATION = 4;

    private AttestationUtils() {
    }

    public static X509Certificate[] parseCertificateChain(KeymasterCertificateChain kmChain) throws KeyAttestationException {
        Collection<byte[]> rawChain = kmChain.getCertificates();
        if (rawChain.size() < 2) {
            throw new KeyAttestationException("Attestation certificate chain contained " + rawChain.size() + " entries. At least two are required.");
        }
        ByteArrayOutputStream concatenatedRawChain = new ByteArrayOutputStream();
        try {
            for (byte[] cert : rawChain) {
                concatenatedRawChain.write(cert);
            }
            return (X509Certificate[]) CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(concatenatedRawChain.toByteArray())).toArray(new X509Certificate[0]);
        } catch (Exception e) {
            throw new KeyAttestationException("Unable to construct certificate chain", e);
        }
    }

    public static X509Certificate[] attestDeviceIds(Context context, int[] idTypes, byte[] attestationChallenge) throws DeviceIdAttestationException {
        if (attestationChallenge == null) {
            throw new NullPointerException("Missing attestation challenge");
        }
        if (idTypes == null) {
            throw new NullPointerException("Missing id types");
        }
        String keystoreAlias = generateRandomAlias();
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keystoreAlias, 4).setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1")).setDigests("SHA-256").setAttestationChallenge(attestationChallenge);
        if (idTypes != null) {
            builder.setAttestationIds(idTypes);
            builder.setDevicePropertiesAttestationIncluded(true);
        }
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, AndroidKeyStoreSpi.NAME);
            keyPairGenerator.initialize(builder.build());
            keyPairGenerator.generateKeyPair();
            KeyStore keyStore = KeyStore.getInstance(AndroidKeyStoreSpi.NAME);
            keyStore.load(null);
            Certificate[] certs = keyStore.getCertificateChain(keystoreAlias);
            X509Certificate[] certificateChain = (X509Certificate[]) Arrays.copyOf(certs, certs.length, X509Certificate[].class);
            keyStore.deleteEntry(keystoreAlias);
            return certificateChain;
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e2) {
            if (e2.getCause() instanceof DeviceIdAttestationException) {
                throw ((DeviceIdAttestationException) e2.getCause());
            }
            if ((e2 instanceof ProviderException) && (e2.getCause() instanceof IllegalArgumentException)) {
                throw ((IllegalArgumentException) e2.getCause());
            }
            throw new DeviceIdAttestationException("Unable to perform attestation", e2);
        }
    }

    private static String generateRandomAlias() {
        Random random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(random.nextInt(26) + 65);
        }
        return builder.toString();
    }

    public static boolean isChainValid(KeymasterCertificateChain chain) {
        return chain != null && chain.getCertificates().size() >= 2;
    }
}
