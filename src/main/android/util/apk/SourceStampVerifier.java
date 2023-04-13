package android.util.apk;

import android.util.Pair;
import android.util.Slog;
import android.util.apk.ApkSigningBlockUtils;
import android.util.jar.StrictJarFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import libcore.io.Streams;
/* loaded from: classes3.dex */
public abstract class SourceStampVerifier {
    private static final int APK_SIGNATURE_SCHEME_V2_BLOCK_ID = 1896449818;
    private static final int APK_SIGNATURE_SCHEME_V3_BLOCK_ID = -262969152;
    private static final int PROOF_OF_ROTATION_ATTR_ID = -1654455305;
    private static final int SOURCE_STAMP_BLOCK_ID = 1845461005;
    private static final String SOURCE_STAMP_CERTIFICATE_HASH_ZIP_ENTRY_NAME = "stamp-cert-sha256";
    private static final String TAG = "SourceStampVerifier";
    private static final int VERSION_APK_SIGNATURE_SCHEME_V2 = 2;
    private static final int VERSION_APK_SIGNATURE_SCHEME_V3 = 3;
    private static final int VERSION_JAR_SIGNATURE_SCHEME = 1;

    private SourceStampVerifier() {
    }

    public static SourceStampVerificationResult verify(List<String> apkFiles) {
        Certificate stampCertificate = null;
        List<? extends Certificate> stampCertificateLineage = Collections.emptyList();
        for (String apkFile : apkFiles) {
            SourceStampVerificationResult sourceStampVerificationResult = verify(apkFile);
            if (!sourceStampVerificationResult.isPresent() || !sourceStampVerificationResult.isVerified()) {
                return sourceStampVerificationResult;
            }
            if (stampCertificate != null && (!stampCertificate.equals(sourceStampVerificationResult.getCertificate()) || !stampCertificateLineage.equals(sourceStampVerificationResult.getCertificateLineage()))) {
                return SourceStampVerificationResult.notVerified();
            }
            stampCertificate = sourceStampVerificationResult.getCertificate();
            stampCertificateLineage = sourceStampVerificationResult.getCertificateLineage();
        }
        return SourceStampVerificationResult.verified(stampCertificate, stampCertificateLineage);
    }

    public static SourceStampVerificationResult verify(String apkFile) {
        StrictJarFile apkJar = null;
        try {
            RandomAccessFile apk = new RandomAccessFile(apkFile, "r");
            try {
                apkJar = new StrictJarFile(apkFile, false, false);
                byte[] sourceStampCertificateDigest = getSourceStampCertificateDigest(apkJar);
                if (sourceStampCertificateDigest == null) {
                    SourceStampVerificationResult notPresent = SourceStampVerificationResult.notPresent();
                    apk.close();
                    return notPresent;
                }
                byte[] manifestBytes = getManifestBytes(apkJar);
                SourceStampVerificationResult verify = verify(apk, sourceStampCertificateDigest, manifestBytes);
                apk.close();
                return verify;
            } catch (Throwable th) {
                try {
                    apk.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            return SourceStampVerificationResult.notPresent();
        } finally {
            closeApkJar(apkJar);
        }
    }

    private static SourceStampVerificationResult verify(RandomAccessFile apk, byte[] sourceStampCertificateDigest, byte[] manifestBytes) {
        try {
            SignatureInfo signatureInfo = ApkSigningBlockUtils.findSignature(apk, SOURCE_STAMP_BLOCK_ID);
            Map<Integer, Map<Integer, byte[]>> signatureSchemeApkContentDigests = getSignatureSchemeApkContentDigests(apk, manifestBytes);
            return verify(signatureInfo, getSignatureSchemeDigests(signatureSchemeApkContentDigests), sourceStampCertificateDigest);
        } catch (SignatureNotFoundException | IOException | RuntimeException e) {
            return SourceStampVerificationResult.notVerified();
        }
    }

    private static SourceStampVerificationResult verify(SignatureInfo signatureInfo, Map<Integer, byte[]> signatureSchemeDigests, byte[] sourceStampCertificateDigest) throws SecurityException, IOException {
        ByteBuffer sourceStampBlock = signatureInfo.signatureBlock;
        ByteBuffer sourceStampBlockData = ApkSigningBlockUtils.getLengthPrefixedSlice(sourceStampBlock);
        X509Certificate sourceStampCertificate = verifySourceStampCertificate(sourceStampBlockData, sourceStampCertificateDigest);
        ByteBuffer signedSignatureSchemes = ApkSigningBlockUtils.getLengthPrefixedSlice(sourceStampBlockData);
        Map<Integer, ByteBuffer> signedSignatureSchemeData = new HashMap<>();
        while (signedSignatureSchemes.hasRemaining()) {
            ByteBuffer signedSignatureScheme = ApkSigningBlockUtils.getLengthPrefixedSlice(signedSignatureSchemes);
            int signatureSchemeId = signedSignatureScheme.getInt();
            signedSignatureSchemeData.put(Integer.valueOf(signatureSchemeId), signedSignatureScheme);
        }
        for (Map.Entry<Integer, byte[]> signatureSchemeDigest : signatureSchemeDigests.entrySet()) {
            if (!signedSignatureSchemeData.containsKey(signatureSchemeDigest.getKey())) {
                throw new SecurityException(String.format("No signatures found for signature scheme %d", signatureSchemeDigest.getKey()));
            }
            ByteBuffer signatures = ApkSigningBlockUtils.getLengthPrefixedSlice(signedSignatureSchemeData.get(signatureSchemeDigest.getKey()));
            verifySourceStampSignature(signatureSchemeDigest.getValue(), sourceStampCertificate, signatures);
        }
        List<? extends Certificate> sourceStampCertificateLineage = Collections.emptyList();
        if (sourceStampBlockData.hasRemaining()) {
            ByteBuffer stampAttributeData = ApkSigningBlockUtils.getLengthPrefixedSlice(sourceStampBlockData);
            ByteBuffer stampAttributeDataSignatures = ApkSigningBlockUtils.getLengthPrefixedSlice(sourceStampBlockData);
            byte[] stampAttributeBytes = new byte[stampAttributeData.remaining()];
            stampAttributeData.get(stampAttributeBytes);
            stampAttributeData.flip();
            verifySourceStampSignature(stampAttributeBytes, sourceStampCertificate, stampAttributeDataSignatures);
            ApkSigningBlockUtils.VerifiedProofOfRotation verifiedProofOfRotation = verifySourceStampAttributes(stampAttributeData, sourceStampCertificate);
            if (verifiedProofOfRotation != null) {
                sourceStampCertificateLineage = verifiedProofOfRotation.certs;
            }
        }
        return SourceStampVerificationResult.verified(sourceStampCertificate, sourceStampCertificateLineage);
    }

    private static X509Certificate verifySourceStampCertificate(ByteBuffer sourceStampBlockData, byte[] sourceStampCertificateDigest) throws IOException {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            byte[] sourceStampEncodedCertificate = ApkSigningBlockUtils.readLengthPrefixedByteArray(sourceStampBlockData);
            try {
                X509Certificate sourceStampCertificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(sourceStampEncodedCertificate));
                byte[] sourceStampBlockCertificateDigest = computeSha256Digest(sourceStampEncodedCertificate);
                if (!Arrays.equals(sourceStampCertificateDigest, sourceStampBlockCertificateDigest)) {
                    throw new SecurityException("Certificate mismatch between APK and signature block");
                }
                return new VerbatimX509Certificate(sourceStampCertificate, sourceStampEncodedCertificate);
            } catch (CertificateException e) {
                throw new SecurityException("Failed to decode certificate", e);
            }
        } catch (CertificateException e2) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e2);
        }
    }

    private static void verifySourceStampSignature(byte[] data, X509Certificate sourceStampCertificate, ByteBuffer signatures) throws IOException {
        int signatureCount = 0;
        int bestSigAlgorithm = -1;
        byte[] bestSigAlgorithmSignatureBytes = null;
        while (signatures.hasRemaining()) {
            signatureCount++;
            try {
                ByteBuffer signature = ApkSigningBlockUtils.getLengthPrefixedSlice(signatures);
                if (signature.remaining() < 8) {
                    throw new SecurityException("Signature record too short");
                }
                int sigAlgorithm = signature.getInt();
                if (ApkSigningBlockUtils.isSupportedSignatureAlgorithm(sigAlgorithm)) {
                    if (bestSigAlgorithm == -1 || ApkSigningBlockUtils.compareSignatureAlgorithm(sigAlgorithm, bestSigAlgorithm) > 0) {
                        bestSigAlgorithm = sigAlgorithm;
                        bestSigAlgorithmSignatureBytes = ApkSigningBlockUtils.readLengthPrefixedByteArray(signature);
                    }
                }
            } catch (IOException | BufferUnderflowException e) {
                throw new SecurityException("Failed to parse signature record #" + signatureCount, e);
            }
        }
        if (bestSigAlgorithm == -1) {
            if (signatureCount == 0) {
                throw new SecurityException("No signatures found");
            }
            throw new SecurityException("No supported signatures found");
        }
        Pair<String, ? extends AlgorithmParameterSpec> signatureAlgorithmParams = ApkSigningBlockUtils.getSignatureAlgorithmJcaSignatureAlgorithm(bestSigAlgorithm);
        String jcaSignatureAlgorithm = signatureAlgorithmParams.first;
        AlgorithmParameterSpec jcaSignatureAlgorithmParams = (AlgorithmParameterSpec) signatureAlgorithmParams.second;
        PublicKey publicKey = sourceStampCertificate.getPublicKey();
        try {
            Signature sig = Signature.getInstance(jcaSignatureAlgorithm);
            sig.initVerify(publicKey);
            if (jcaSignatureAlgorithmParams != null) {
                sig.setParameter(jcaSignatureAlgorithmParams);
            }
            sig.update(data);
            boolean sigVerified = sig.verify(bestSigAlgorithmSignatureBytes);
            if (!sigVerified) {
                throw new SecurityException(jcaSignatureAlgorithm + " signature did not verify");
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e2) {
            throw new SecurityException("Failed to verify " + jcaSignatureAlgorithm + " signature", e2);
        }
    }

    private static Map<Integer, Map<Integer, byte[]>> getSignatureSchemeApkContentDigests(RandomAccessFile apk, byte[] manifestBytes) throws IOException {
        Map<Integer, Map<Integer, byte[]>> signatureSchemeApkContentDigests = new HashMap<>();
        try {
            SignatureInfo v3SignatureInfo = ApkSigningBlockUtils.findSignature(apk, APK_SIGNATURE_SCHEME_V3_BLOCK_ID);
            signatureSchemeApkContentDigests.put(3, getApkContentDigestsFromSignatureBlock(v3SignatureInfo.signatureBlock));
        } catch (SignatureNotFoundException e) {
        }
        try {
            SignatureInfo v2SignatureInfo = ApkSigningBlockUtils.findSignature(apk, APK_SIGNATURE_SCHEME_V2_BLOCK_ID);
            signatureSchemeApkContentDigests.put(2, getApkContentDigestsFromSignatureBlock(v2SignatureInfo.signatureBlock));
        } catch (SignatureNotFoundException e2) {
        }
        if (manifestBytes != null) {
            Map<Integer, byte[]> jarSignatureSchemeApkContentDigests = new HashMap<>();
            jarSignatureSchemeApkContentDigests.put(4, computeSha256Digest(manifestBytes));
            signatureSchemeApkContentDigests.put(1, jarSignatureSchemeApkContentDigests);
        }
        return signatureSchemeApkContentDigests;
    }

    private static Map<Integer, byte[]> getApkContentDigestsFromSignatureBlock(ByteBuffer signatureBlock) throws IOException {
        Map<Integer, byte[]> apkContentDigests = new HashMap<>();
        ByteBuffer signers = ApkSigningBlockUtils.getLengthPrefixedSlice(signatureBlock);
        while (signers.hasRemaining()) {
            ByteBuffer signer = ApkSigningBlockUtils.getLengthPrefixedSlice(signers);
            ByteBuffer signedData = ApkSigningBlockUtils.getLengthPrefixedSlice(signer);
            ByteBuffer digests = ApkSigningBlockUtils.getLengthPrefixedSlice(signedData);
            while (digests.hasRemaining()) {
                ByteBuffer digest = ApkSigningBlockUtils.getLengthPrefixedSlice(digests);
                int sigAlgorithm = digest.getInt();
                byte[] contentDigest = ApkSigningBlockUtils.readLengthPrefixedByteArray(digest);
                int digestAlgorithm = ApkSigningBlockUtils.getSignatureAlgorithmContentDigestAlgorithm(sigAlgorithm);
                apkContentDigests.put(Integer.valueOf(digestAlgorithm), contentDigest);
            }
        }
        return apkContentDigests;
    }

    private static Map<Integer, byte[]> getSignatureSchemeDigests(Map<Integer, Map<Integer, byte[]>> signatureSchemeApkContentDigests) {
        Map<Integer, byte[]> digests = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, byte[]>> signatureSchemeApkContentDigest : signatureSchemeApkContentDigests.entrySet()) {
            List<Pair<Integer, byte[]>> apkDigests = getApkDigests(signatureSchemeApkContentDigest.getValue());
            digests.put(signatureSchemeApkContentDigest.getKey(), encodeApkContentDigests(apkDigests));
        }
        return digests;
    }

    private static List<Pair<Integer, byte[]>> getApkDigests(Map<Integer, byte[]> apkContentDigests) {
        List<Pair<Integer, byte[]>> digests = new ArrayList<>();
        for (Map.Entry<Integer, byte[]> apkContentDigest : apkContentDigests.entrySet()) {
            digests.add(Pair.create(apkContentDigest.getKey(), apkContentDigest.getValue()));
        }
        digests.sort(Comparator.comparing(new Function() { // from class: android.util.apk.SourceStampVerifier$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return SourceStampVerifier.lambda$getApkDigests$0((Pair) obj);
            }
        }));
        return digests;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Integer lambda$getApkDigests$0(Pair pair) {
        return (Integer) pair.first;
    }

    private static byte[] getSourceStampCertificateDigest(StrictJarFile apkJar) throws IOException {
        ZipEntry zipEntry = apkJar.findEntry(SOURCE_STAMP_CERTIFICATE_HASH_ZIP_ENTRY_NAME);
        if (zipEntry == null) {
            return null;
        }
        return Streams.readFully(apkJar.getInputStream(zipEntry));
    }

    private static byte[] getManifestBytes(StrictJarFile apkJar) throws IOException {
        ZipEntry zipEntry = apkJar.findEntry("META-INF/MANIFEST.MF");
        if (zipEntry == null) {
            return null;
        }
        return Streams.readFully(apkJar.getInputStream(zipEntry));
    }

    private static byte[] encodeApkContentDigests(List<Pair<Integer, byte[]>> apkContentDigests) {
        int resultSize = 0;
        for (Pair<Integer, byte[]> element : apkContentDigests) {
            resultSize += element.second.length + 12;
        }
        ByteBuffer result = ByteBuffer.allocate(resultSize);
        result.order(ByteOrder.LITTLE_ENDIAN);
        for (Pair<Integer, byte[]> element2 : apkContentDigests) {
            byte[] second = element2.second;
            result.putInt(second.length + 8);
            result.putInt(element2.first.intValue());
            result.putInt(second.length);
            result.put(second);
        }
        return result.array();
    }

    private static ApkSigningBlockUtils.VerifiedProofOfRotation verifySourceStampAttributes(ByteBuffer stampAttributeData, X509Certificate sourceStampCertificate) throws IOException {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            ByteBuffer stampAttributes = ApkSigningBlockUtils.getLengthPrefixedSlice(stampAttributeData);
            ApkSigningBlockUtils.VerifiedProofOfRotation verifiedProofOfRotation = null;
            while (stampAttributes.hasRemaining()) {
                ByteBuffer attribute = ApkSigningBlockUtils.getLengthPrefixedSlice(stampAttributes);
                int id = attribute.getInt();
                if (id == PROOF_OF_ROTATION_ATTR_ID) {
                    if (verifiedProofOfRotation != null) {
                        throw new SecurityException("Encountered multiple Proof-of-rotation records when verifying source stamp signature");
                    }
                    verifiedProofOfRotation = ApkSigningBlockUtils.verifyProofOfRotationStruct(attribute, certFactory);
                    try {
                        if (verifiedProofOfRotation.certs.size() > 0 && !Arrays.equals(verifiedProofOfRotation.certs.get(verifiedProofOfRotation.certs.size() - 1).getEncoded(), sourceStampCertificate.getEncoded())) {
                            throw new SecurityException("Terminal certificate in Proof-of-rotation record does not match source stamp certificate");
                        }
                    } catch (CertificateEncodingException e) {
                        throw new SecurityException("Failed to encode certificate when comparing Proof-of-rotation record and source stamp certificate", e);
                    }
                }
            }
            return verifiedProofOfRotation;
        } catch (CertificateException e2) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e2);
        }
    }

    private static byte[] computeSha256Digest(byte[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to find SHA-256", e);
        }
    }

    private static void closeApkJar(StrictJarFile apkJar) {
        if (apkJar == null) {
            return;
        }
        try {
            apkJar.close();
        } catch (IOException e) {
            Slog.m95e(TAG, "Could not close APK jar", e);
        }
    }
}
