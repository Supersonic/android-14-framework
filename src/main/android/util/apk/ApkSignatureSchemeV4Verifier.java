package android.util.apk;

import android.p008os.incremental.IncrementalManager;
import android.p008os.incremental.V4Signature;
import android.util.ArrayMap;
import android.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
/* loaded from: classes3.dex */
public class ApkSignatureSchemeV4Verifier {
    static final int APK_SIGNATURE_SCHEME_DEFAULT = -1;

    public static VerifiedSigner extractCertificates(String apkFile) throws SignatureNotFoundException, SecurityException {
        Pair<V4Signature.HashingInfo, V4Signature.SigningInfos> pair = extractSignature(apkFile);
        return verify(apkFile, pair.first, pair.second, -1);
    }

    public static Pair<V4Signature.HashingInfo, V4Signature.SigningInfos> extractSignature(String apkFile) throws SignatureNotFoundException {
        File apk = new File(apkFile);
        byte[] signatureBytes = IncrementalManager.unsafeGetFileSignature(apk.getAbsolutePath());
        if (signatureBytes == null || signatureBytes.length == 0) {
            throw new SignatureNotFoundException("Failed to obtain signature bytes from IncFS.");
        }
        try {
            V4Signature signature = V4Signature.readFrom(signatureBytes);
            if (!signature.isVersionSupported()) {
                throw new SecurityException("v4 signature version " + signature.version + " is not supported");
            }
            V4Signature.HashingInfo hashingInfo = V4Signature.HashingInfo.fromByteArray(signature.hashingInfo);
            V4Signature.SigningInfos signingInfos = V4Signature.SigningInfos.fromByteArray(signature.signingInfos);
            return Pair.create(hashingInfo, signingInfos);
        } catch (IOException e) {
            throw new SignatureNotFoundException("Failed to read V4 signature.", e);
        }
    }

    public static VerifiedSigner verify(String apkFile, V4Signature.HashingInfo hashingInfo, V4Signature.SigningInfos signingInfos, int v3BlockId) throws SignatureNotFoundException, SecurityException {
        V4Signature.SigningInfo signingInfo = findSigningInfoForBlockId(signingInfos, v3BlockId);
        byte[] signedData = V4Signature.getSignedData(new File(apkFile).length(), hashingInfo, signingInfo);
        Pair<Certificate, byte[]> result = verifySigner(signingInfo, signedData);
        Map<Integer, byte[]> contentDigests = new ArrayMap<>();
        contentDigests.put(Integer.valueOf(convertToContentDigestType(hashingInfo.hashAlgorithm)), hashingInfo.rawRootHash);
        return new VerifiedSigner(new Certificate[]{result.first}, result.second, contentDigests);
    }

    private static V4Signature.SigningInfo findSigningInfoForBlockId(V4Signature.SigningInfos signingInfos, int v3BlockId) throws SignatureNotFoundException {
        V4Signature.SigningInfoBlock[] signingInfoBlockArr;
        if (v3BlockId == -1 || v3BlockId == -262969152) {
            return signingInfos.signingInfo;
        }
        for (V4Signature.SigningInfoBlock signingInfoBlock : signingInfos.signingInfoBlocks) {
            if (v3BlockId == signingInfoBlock.blockId) {
                try {
                    return V4Signature.SigningInfo.fromByteArray(signingInfoBlock.signingInfo);
                } catch (IOException e) {
                    throw new SecurityException("Failed to read V4 signature block: " + signingInfoBlock.blockId, e);
                }
            }
        }
        throw new SecurityException("Failed to find V4 signature block corresponding to V3 blockId: " + v3BlockId);
    }

    private static Pair<Certificate, byte[]> verifySigner(V4Signature.SigningInfo signingInfo, byte[] signedData) throws SecurityException {
        if (!ApkSigningBlockUtils.isSupportedSignatureAlgorithm(signingInfo.signatureAlgorithmId)) {
            throw new SecurityException("No supported signatures found");
        }
        int signatureAlgorithmId = signingInfo.signatureAlgorithmId;
        byte[] signatureBytes = signingInfo.signature;
        byte[] publicKeyBytes = signingInfo.publicKey;
        byte[] encodedCert = signingInfo.certificate;
        String keyAlgorithm = ApkSigningBlockUtils.getSignatureAlgorithmJcaKeyAlgorithm(signatureAlgorithmId);
        Pair<String, ? extends AlgorithmParameterSpec> signatureAlgorithmParams = ApkSigningBlockUtils.getSignatureAlgorithmJcaSignatureAlgorithm(signatureAlgorithmId);
        String jcaSignatureAlgorithm = signatureAlgorithmParams.first;
        AlgorithmParameterSpec jcaSignatureAlgorithmParams = (AlgorithmParameterSpec) signatureAlgorithmParams.second;
        try {
            PublicKey publicKey = KeyFactory.getInstance(keyAlgorithm).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Signature sig = Signature.getInstance(jcaSignatureAlgorithm);
            sig.initVerify(publicKey);
            if (jcaSignatureAlgorithmParams != null) {
                sig.setParameter(jcaSignatureAlgorithmParams);
            }
            sig.update(signedData);
            boolean sigVerified = sig.verify(signatureBytes);
            if (!sigVerified) {
                throw new SecurityException(jcaSignatureAlgorithm + " signature did not verify");
            }
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                try {
                    X509Certificate certificate = new VerbatimX509Certificate((X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert)), encodedCert);
                    byte[] certificatePublicKeyBytes = certificate.getPublicKey().getEncoded();
                    if (!Arrays.equals(publicKeyBytes, certificatePublicKeyBytes)) {
                        throw new SecurityException("Public key mismatch between certificate and signature record");
                    }
                    return Pair.create(certificate, signingInfo.apkDigest);
                } catch (CertificateException e) {
                    throw new SecurityException("Failed to decode certificate", e);
                }
            } catch (CertificateException e2) {
                throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e2);
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException e3) {
            throw new SecurityException("Failed to verify " + jcaSignatureAlgorithm + " signature", e3);
        }
    }

    private static int convertToContentDigestType(int hashAlgorithm) throws SecurityException {
        if (hashAlgorithm == 1) {
            return 3;
        }
        throw new SecurityException("Unsupported hashAlgorithm: " + hashAlgorithm);
    }

    /* loaded from: classes3.dex */
    public static class VerifiedSigner {
        public final byte[] apkDigest;
        public final Certificate[] certs;
        public final Map<Integer, byte[]> contentDigests;

        public VerifiedSigner(Certificate[] certs, byte[] apkDigest, Map<Integer, byte[]> contentDigests) {
            this.certs = certs;
            this.apkDigest = apkDigest;
            this.contentDigests = contentDigests;
        }
    }
}
