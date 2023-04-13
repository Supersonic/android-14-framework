package android.util.apk;

import android.content.p001pm.PackageManager;
import android.content.p001pm.Signature;
import android.content.p001pm.SigningDetails;
import android.content.p001pm.parsing.result.ParseInput;
import android.content.p001pm.parsing.result.ParseResult;
import android.p008os.Trace;
import android.p008os.incremental.V4Signature;
import android.util.Pair;
import android.util.apk.ApkSignatureSchemeV2Verifier;
import android.util.apk.ApkSignatureSchemeV3Verifier;
import android.util.apk.ApkSignatureSchemeV4Verifier;
import android.util.jar.StrictJarFile;
import com.android.internal.util.ArrayUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import libcore.io.IoUtils;
/* loaded from: classes3.dex */
public class ApkSignatureVerifier {
    private static final AtomicReference<byte[]> sBuffer = new AtomicReference<>();

    public static ParseResult<SigningDetails> verify(ParseInput input, String apkPath, int minSignatureSchemeVersion) {
        return verifySignatures(input, apkPath, minSignatureSchemeVersion, true);
    }

    public static ParseResult<SigningDetails> unsafeGetCertsWithoutVerification(ParseInput input, String apkPath, int minSignatureSchemeVersion) {
        return verifySignatures(input, apkPath, minSignatureSchemeVersion, false);
    }

    private static ParseResult<SigningDetails> verifySignatures(ParseInput input, String apkPath, int minSignatureSchemeVersion, boolean verifyFull) {
        ParseResult<SigningDetailsWithDigests> result = verifySignaturesInternal(input, apkPath, minSignatureSchemeVersion, verifyFull);
        if (result.isError()) {
            return input.error(result);
        }
        return input.success(result.getResult().signingDetails);
    }

    public static ParseResult<SigningDetailsWithDigests> verifySignaturesInternal(ParseInput input, String apkPath, int minSignatureSchemeVersion, boolean verifyFull) {
        if (minSignatureSchemeVersion > 4) {
            return input.error(-103, "No signature found in package of version " + minSignatureSchemeVersion + " or newer for package " + apkPath);
        }
        try {
            return verifyV4Signature(input, apkPath, minSignatureSchemeVersion, verifyFull);
        } catch (SignatureNotFoundException e) {
            if (minSignatureSchemeVersion >= 4) {
                return input.error(-103, "No APK Signature Scheme v4 signature in package " + apkPath, e);
            }
            if (minSignatureSchemeVersion > 3) {
                return input.error(-103, "No signature found in package of version " + minSignatureSchemeVersion + " or newer for package " + apkPath);
            }
            return verifyV3AndBelowSignatures(input, apkPath, minSignatureSchemeVersion, verifyFull);
        }
    }

    private static ParseResult<SigningDetailsWithDigests> verifyV3AndBelowSignatures(ParseInput input, String apkPath, int minSignatureSchemeVersion, boolean verifyFull) {
        try {
            return verifyV3Signature(input, apkPath, verifyFull);
        } catch (SignatureNotFoundException e) {
            if (minSignatureSchemeVersion >= 3) {
                return input.error(-103, "No APK Signature Scheme v3 signature in package " + apkPath, e);
            }
            if (minSignatureSchemeVersion > 2) {
                return input.error(-103, "No signature found in package of version " + minSignatureSchemeVersion + " or newer for package " + apkPath);
            }
            try {
                return verifyV2Signature(input, apkPath, verifyFull);
            } catch (SignatureNotFoundException e2) {
                if (minSignatureSchemeVersion >= 2) {
                    return input.error(-103, "No APK Signature Scheme v2 signature in package " + apkPath, e2);
                }
                if (minSignatureSchemeVersion > 1) {
                    return input.error(-103, "No signature found in package of version " + minSignatureSchemeVersion + " or newer for package " + apkPath);
                }
                return verifyV1Signature(input, apkPath, verifyFull);
            }
        }
    }

    private static ParseResult<SigningDetailsWithDigests> verifyV4Signature(ParseInput input, String apkPath, int minSignatureSchemeVersion, boolean verifyFull) throws SignatureNotFoundException {
        Trace.traceBegin(262144L, verifyFull ? "verifyV4" : "certsOnlyV4");
        try {
            try {
                try {
                    Pair<V4Signature.HashingInfo, V4Signature.SigningInfos> v4Pair = ApkSignatureSchemeV4Verifier.extractSignature(apkPath);
                    V4Signature.HashingInfo hashingInfo = v4Pair.first;
                    V4Signature.SigningInfos signingInfos = v4Pair.second;
                    Signature[] pastSignerSigs = null;
                    Map<Integer, byte[]> nonstreamingDigests = null;
                    Certificate[][] nonstreamingCerts = null;
                    int v3BlockId = -1;
                    if (verifyFull || signingInfos.signingInfoBlocks.length > 0) {
                        try {
                            ApkSignatureSchemeV3Verifier.VerifiedSigner v3Signer = ApkSignatureSchemeV3Verifier.unsafeGetCertsWithoutVerification(apkPath);
                            nonstreamingDigests = v3Signer.contentDigests;
                            nonstreamingCerts = new Certificate[][]{v3Signer.certs};
                            if (v3Signer.por != null) {
                                pastSignerSigs = new Signature[v3Signer.por.certs.size()];
                                for (int i = 0; i < pastSignerSigs.length; i++) {
                                    pastSignerSigs[i] = new Signature(v3Signer.por.certs.get(i).getEncoded());
                                    pastSignerSigs[i].setFlags(v3Signer.por.flagsList.get(i).intValue());
                                }
                            }
                            v3BlockId = v3Signer.blockId;
                        } catch (SignatureNotFoundException e) {
                            try {
                                ApkSignatureSchemeV2Verifier.VerifiedSigner v2Signer = ApkSignatureSchemeV2Verifier.verify(apkPath, false);
                                nonstreamingDigests = v2Signer.contentDigests;
                                nonstreamingCerts = v2Signer.certs;
                            } catch (SignatureNotFoundException ee) {
                                throw new SecurityException("V4 verification failed to collect V2/V3 certificates from : " + apkPath, ee);
                            }
                        }
                    }
                    ApkSignatureSchemeV4Verifier.VerifiedSigner vSigner = ApkSignatureSchemeV4Verifier.verify(apkPath, hashingInfo, signingInfos, v3BlockId);
                    Certificate[][] signerCerts = {vSigner.certs};
                    Signature[] signerSigs = convertToSignatures(signerCerts);
                    if (verifyFull) {
                        Signature[] nonstreamingSigs = convertToSignatures(nonstreamingCerts);
                        if (nonstreamingSigs.length != signerSigs.length) {
                            throw new SecurityException("Invalid number of certificates: " + nonstreamingSigs.length);
                        }
                        int i2 = 0;
                        int size = signerSigs.length;
                        while (i2 < size) {
                            Certificate[][] signerCerts2 = signerCerts;
                            if (!nonstreamingSigs[i2].equals(signerSigs[i2])) {
                                throw new SecurityException("V4 signature certificate does not match V2/V3");
                            }
                            i2++;
                            signerCerts = signerCerts2;
                        }
                        boolean found = false;
                        Iterator<byte[]> it = nonstreamingDigests.values().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            byte[] nonstreamingDigest = it.next();
                            boolean found2 = found;
                            if (ArrayUtils.equals(vSigner.apkDigest, nonstreamingDigest, vSigner.apkDigest.length)) {
                                found = true;
                                break;
                            }
                            found = found2;
                        }
                        if (!found) {
                            throw new SecurityException("APK digest in V4 signature does not match V2/V3");
                        }
                    }
                    ParseResult<SigningDetailsWithDigests> success = input.success(new SigningDetailsWithDigests(new SigningDetails(signerSigs, 4, pastSignerSigs), vSigner.contentDigests));
                    Trace.traceEnd(262144L);
                    return success;
                } catch (SignatureNotFoundException e2) {
                    throw e2;
                }
            } catch (Exception e3) {
                ParseResult<SigningDetailsWithDigests> error = input.error(-103, "Failed to collect certificates from " + apkPath + " using APK Signature Scheme v4", e3);
                Trace.traceEnd(262144L);
                return error;
            }
        } catch (Throwable ee2) {
            Trace.traceEnd(262144L);
            throw ee2;
        }
    }

    private static ParseResult<SigningDetailsWithDigests> verifyV3Signature(ParseInput input, String apkPath, boolean verifyFull) throws SignatureNotFoundException {
        Trace.traceBegin(262144L, verifyFull ? "verifyV3" : "certsOnlyV3");
        try {
            try {
                ApkSignatureSchemeV3Verifier.VerifiedSigner vSigner = verifyFull ? ApkSignatureSchemeV3Verifier.verify(apkPath) : ApkSignatureSchemeV3Verifier.unsafeGetCertsWithoutVerification(apkPath);
                Certificate[][] signerCerts = {vSigner.certs};
                Signature[] signerSigs = convertToSignatures(signerCerts);
                Signature[] pastSignerSigs = null;
                if (vSigner.por != null) {
                    pastSignerSigs = new Signature[vSigner.por.certs.size()];
                    for (int i = 0; i < pastSignerSigs.length; i++) {
                        pastSignerSigs[i] = new Signature(vSigner.por.certs.get(i).getEncoded());
                        pastSignerSigs[i].setFlags(vSigner.por.flagsList.get(i).intValue());
                    }
                }
                ParseResult<SigningDetailsWithDigests> success = input.success(new SigningDetailsWithDigests(new SigningDetails(signerSigs, 3, pastSignerSigs), vSigner.contentDigests));
                Trace.traceEnd(262144L);
                return success;
            } catch (SignatureNotFoundException e) {
                throw e;
            } catch (Exception e2) {
                ParseResult<SigningDetailsWithDigests> error = input.error(-103, "Failed to collect certificates from " + apkPath + " using APK Signature Scheme v3", e2);
                Trace.traceEnd(262144L);
                return error;
            }
        } catch (Throwable th) {
            Trace.traceEnd(262144L);
            throw th;
        }
    }

    private static ParseResult<SigningDetailsWithDigests> verifyV2Signature(ParseInput input, String apkPath, boolean verifyFull) throws SignatureNotFoundException {
        Trace.traceBegin(262144L, verifyFull ? "verifyV2" : "certsOnlyV2");
        try {
            try {
                ApkSignatureSchemeV2Verifier.VerifiedSigner vSigner = ApkSignatureSchemeV2Verifier.verify(apkPath, verifyFull);
                Certificate[][] signerCerts = vSigner.certs;
                Signature[] signerSigs = convertToSignatures(signerCerts);
                ParseResult<SigningDetailsWithDigests> success = input.success(new SigningDetailsWithDigests(new SigningDetails(signerSigs, 2), vSigner.contentDigests));
                Trace.traceEnd(262144L);
                return success;
            } catch (SignatureNotFoundException e) {
                throw e;
            } catch (Exception e2) {
                ParseResult<SigningDetailsWithDigests> error = input.error(-103, "Failed to collect certificates from " + apkPath + " using APK Signature Scheme v2", e2);
                Trace.traceEnd(262144L);
                return error;
            }
        } catch (Throwable th) {
            Trace.traceEnd(262144L);
            throw th;
        }
    }

    private static ParseResult<SigningDetailsWithDigests> verifyV1Signature(ParseInput input, String apkPath, boolean verifyFull) {
        StrictJarFile jarFile = null;
        try {
            Trace.traceBegin(262144L, "strictJarFileCtor");
            jarFile = new StrictJarFile(apkPath, true, verifyFull);
            List<ZipEntry> toVerify = new ArrayList<>();
            ZipEntry manifestEntry = jarFile.findEntry("AndroidManifest.xml");
            if (manifestEntry == null) {
                ParseResult<SigningDetailsWithDigests> error = input.error(-101, "Package " + apkPath + " has no manifest");
                Trace.traceEnd(262144L);
                closeQuietly(jarFile);
                return error;
            }
            ParseResult<Certificate[][]> result = loadCertificates(input, jarFile, manifestEntry);
            if (result.isError()) {
                ParseResult<SigningDetailsWithDigests> error2 = input.error((ParseResult<?>) result);
                Trace.traceEnd(262144L);
                closeQuietly(jarFile);
                return error2;
            }
            Certificate[][] lastCerts = result.getResult();
            if (ArrayUtils.isEmpty(lastCerts)) {
                ParseResult<SigningDetailsWithDigests> error3 = input.error(-103, "Package " + apkPath + " has no certificates at entry AndroidManifest.xml");
                Trace.traceEnd(262144L);
                closeQuietly(jarFile);
                return error3;
            }
            Signature[] lastSigs = convertToSignatures(lastCerts);
            if (verifyFull) {
                Iterator<ZipEntry> i = jarFile.iterator();
                while (i.hasNext()) {
                    ZipEntry entry = i.next();
                    if (!entry.isDirectory()) {
                        String entryName = entry.getName();
                        if (!entryName.startsWith("META-INF/") && !entryName.equals("AndroidManifest.xml")) {
                            toVerify.add(entry);
                        }
                    }
                }
                Iterator<ZipEntry> it = toVerify.iterator();
                while (it.hasNext()) {
                    ZipEntry entry2 = it.next();
                    ParseResult<Certificate[][]> ret = loadCertificates(input, jarFile, entry2);
                    if (ret.isError()) {
                        return input.error((ParseResult<?>) ret);
                    }
                    Certificate[][] entryCerts = ret.getResult();
                    if (ArrayUtils.isEmpty(entryCerts)) {
                        return input.error(-103, "Package " + apkPath + " has no certificates at entry " + entry2.getName());
                    }
                    Signature[] entrySigs = convertToSignatures(entryCerts);
                    Iterator<ZipEntry> it2 = it;
                    if (!Signature.areExactMatch(lastSigs, entrySigs)) {
                        return input.error(PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES, "Package " + apkPath + " has mismatched certificates at entry " + entry2.getName());
                    }
                    it = it2;
                }
            }
            return input.success(new SigningDetailsWithDigests(new SigningDetails(lastSigs, 1), null));
        } catch (GeneralSecurityException e) {
            return input.error(PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING, "Failed to collect certificates from " + apkPath, e);
        } catch (IOException | RuntimeException e2) {
            return input.error(-103, "Failed to collect certificates from " + apkPath, e2);
        } finally {
            Trace.traceEnd(262144L);
            closeQuietly(jarFile);
        }
    }

    private static ParseResult<Certificate[][]> loadCertificates(ParseInput input, StrictJarFile jarFile, ZipEntry entry) {
        InputStream is = null;
        try {
            is = jarFile.getInputStream(entry);
            readFullyIgnoringContents(is);
            return input.success(jarFile.getCertificateChains(entry));
        } catch (IOException | RuntimeException e) {
            return input.error(-102, "Failed reading " + entry.getName() + " in " + jarFile, e);
        } finally {
            IoUtils.closeQuietly(is);
        }
    }

    private static void readFullyIgnoringContents(InputStream in) throws IOException {
        byte[] buffer = sBuffer.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[4096];
        }
        int count = 0;
        while (true) {
            int n = in.read(buffer, 0, buffer.length);
            if (n != -1) {
                count += n;
            } else {
                sBuffer.set(buffer);
                return;
            }
        }
    }

    private static Signature[] convertToSignatures(Certificate[][] certs) throws CertificateEncodingException {
        Signature[] res = new Signature[certs.length];
        for (int i = 0; i < certs.length; i++) {
            res[i] = new Signature(certs[i]);
        }
        return res;
    }

    private static void closeQuietly(StrictJarFile jarFile) {
        if (jarFile != null) {
            try {
                jarFile.close();
            } catch (Exception e) {
            }
        }
    }

    public static int getMinimumSignatureSchemeVersionForTargetSdk(int targetSdk) {
        if (targetSdk >= 30) {
            return 2;
        }
        return 1;
    }

    /* loaded from: classes3.dex */
    public static class Result {
        public final Certificate[][] certs;
        public final int signatureSchemeVersion;
        public final Signature[] sigs;

        public Result(Certificate[][] certs, Signature[] sigs, int signingVersion) {
            this.certs = certs;
            this.sigs = sigs;
            this.signatureSchemeVersion = signingVersion;
        }
    }

    public static byte[] getVerityRootHash(String apkPath) throws IOException, SecurityException {
        try {
            return ApkSignatureSchemeV3Verifier.getVerityRootHash(apkPath);
        } catch (SignatureNotFoundException e) {
            try {
                return ApkSignatureSchemeV2Verifier.getVerityRootHash(apkPath);
            } catch (SignatureNotFoundException e2) {
                return null;
            }
        }
    }

    public static byte[] generateApkVerity(String apkPath, ByteBufferFactory bufferFactory) throws IOException, SignatureNotFoundException, SecurityException, DigestException, NoSuchAlgorithmException {
        try {
            return ApkSignatureSchemeV3Verifier.generateApkVerity(apkPath, bufferFactory);
        } catch (SignatureNotFoundException e) {
            return ApkSignatureSchemeV2Verifier.generateApkVerity(apkPath, bufferFactory);
        }
    }

    /* loaded from: classes3.dex */
    public static class SigningDetailsWithDigests {
        public final Map<Integer, byte[]> contentDigests;
        public final SigningDetails signingDetails;

        SigningDetailsWithDigests(SigningDetails signingDetails, Map<Integer, byte[]> contentDigests) {
            this.signingDetails = signingDetails;
            this.contentDigests = contentDigests;
        }
    }
}
