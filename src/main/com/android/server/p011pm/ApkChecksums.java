package com.android.server.p011pm;

import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApkChecksum;
import android.content.pm.Checksum;
import android.content.pm.IOnChecksumsReadyListener;
import android.content.pm.Signature;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.incremental.IncrementalManager;
import android.os.incremental.IncrementalStorage;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.apk.ApkSignatureSchemeV2Verifier;
import android.util.apk.ApkSignatureSchemeV3Verifier;
import android.util.apk.ApkSignatureSchemeV4Verifier;
import android.util.apk.ApkSignatureVerifier;
import android.util.apk.ApkSigningBlockUtils;
import android.util.apk.ByteBufferFactory;
import android.util.apk.SignatureInfo;
import android.util.apk.SignatureNotFoundException;
import android.util.apk.VerityBuilder;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.security.VerityUtils;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.DigestException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
/* renamed from: com.android.server.pm.ApkChecksums */
/* loaded from: classes2.dex */
public class ApkChecksums {
    public static final Certificate[] EMPTY_CERTIFICATE_ARRAY = new Certificate[0];

    public static int getChecksumKindForContentDigestAlgo(int i) {
        if (i != 1) {
            return i != 2 ? -1 : 64;
        }
        return 32;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* renamed from: com.android.server.pm.ApkChecksums$Injector */
    /* loaded from: classes2.dex */
    public static class Injector {
        public final Producer<Context> mContext;
        public final Producer<Handler> mHandlerProducer;
        public final Producer<IncrementalManager> mIncrementalManagerProducer;
        public final Producer<PackageManagerInternal> mPackageManagerInternalProducer;

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
        /* renamed from: com.android.server.pm.ApkChecksums$Injector$Producer */
        /* loaded from: classes2.dex */
        public interface Producer<T> {
            T produce();
        }

        public Injector(Producer<Context> producer, Producer<Handler> producer2, Producer<IncrementalManager> producer3, Producer<PackageManagerInternal> producer4) {
            this.mContext = producer;
            this.mHandlerProducer = producer2;
            this.mIncrementalManagerProducer = producer3;
            this.mPackageManagerInternalProducer = producer4;
        }

        public Handler getHandler() {
            return this.mHandlerProducer.produce();
        }

        public IncrementalManager getIncrementalManager() {
            return this.mIncrementalManagerProducer.produce();
        }

        public PackageManagerInternal getPackageManagerInternal() {
            return this.mPackageManagerInternalProducer.produce();
        }
    }

    public static String buildDigestsPathForApk(String str) {
        if (!ApkLiteParseUtils.isApkPath(str)) {
            throw new IllegalStateException("Code path is not an apk " + str);
        }
        return str.substring(0, str.length() - 4) + ".digests";
    }

    public static String buildSignaturePathForDigests(String str) {
        return str + ".signature";
    }

    public static boolean isDigestOrDigestSignatureFile(File file) {
        String name = file.getName();
        return name.endsWith(".digests") || name.endsWith(".signature");
    }

    public static File findDigestsForFile(File file) {
        File file2 = new File(buildDigestsPathForApk(file.getAbsolutePath()));
        if (file2.exists()) {
            return file2;
        }
        return null;
    }

    public static File findSignatureForDigests(File file) {
        File file2 = new File(buildSignaturePathForDigests(file.getAbsolutePath()));
        if (file2.exists()) {
            return file2;
        }
        return null;
    }

    public static void writeChecksums(OutputStream outputStream, Checksum[] checksumArr) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            for (Checksum checksum : checksumArr) {
                Checksum.writeToStream(dataOutputStream, checksum);
            }
            dataOutputStream.close();
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static Checksum[] readChecksums(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            Checksum[] readChecksums = readChecksums(fileInputStream);
            fileInputStream.close();
            return readChecksums;
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static Checksum[] readChecksums(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < 100; i++) {
                try {
                    arrayList.add(Checksum.readFromStream(dataInputStream));
                } catch (EOFException unused) {
                }
            }
            Checksum[] checksumArr = (Checksum[]) arrayList.toArray(new Checksum[arrayList.size()]);
            dataInputStream.close();
            return checksumArr;
        } catch (Throwable th) {
            try {
                dataInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static Certificate[] verifySignature(Checksum[] checksumArr, byte[] bArr) throws NoSuchAlgorithmException, IOException, SignatureException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            writeChecksums(byteArrayOutputStream, checksumArr);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            PKCS7 pkcs7 = new PKCS7(bArr);
            X509Certificate[] certificates = pkcs7.getCertificates();
            if (certificates == null || certificates.length == 0) {
                throw new SignatureException("Signature missing certificates");
            }
            SignerInfo[] verify = pkcs7.verify(byteArray);
            if (verify == null || verify.length == 0) {
                throw new SignatureException("Verification failed");
            }
            ArrayList arrayList = new ArrayList(verify.length);
            for (SignerInfo signerInfo : verify) {
                ArrayList certificateChain = signerInfo.getCertificateChain(pkcs7);
                if (certificateChain == null) {
                    throw new SignatureException("Verification passed, but certification chain is empty.");
                }
                arrayList.addAll(certificateChain);
            }
            return (Certificate[]) arrayList.toArray(new Certificate[arrayList.size()]);
        } catch (Throwable th) {
            try {
                byteArrayOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static void getChecksums(List<Pair<String, File>> list, int i, int i2, String str, Certificate[] certificateArr, IOnChecksumsReadyListener iOnChecksumsReadyListener, Injector injector) {
        ArrayList arrayList = new ArrayList(list.size());
        int size = list.size();
        for (int i3 = 0; i3 < size; i3++) {
            String str2 = (String) list.get(i3).first;
            File file = (File) list.get(i3).second;
            ArrayMap arrayMap = new ArrayMap();
            arrayList.add(arrayMap);
            try {
                getAvailableApkChecksums(str2, file, i | i2, str, certificateArr, arrayMap, injector);
            } catch (Throwable th) {
                Slog.e("ApkChecksums", "Preferred checksum calculation error", th);
            }
        }
        processRequiredChecksums(list, arrayList, i2, iOnChecksumsReadyListener, injector, SystemClock.uptimeMillis());
    }

    public static void processRequiredChecksums(final List<Pair<String, File>> list, final List<Map<Integer, ApkChecksum>> list2, final int i, final IOnChecksumsReadyListener iOnChecksumsReadyListener, final Injector injector, final long j) {
        List<Pair<String, File>> list3 = list;
        boolean z = SystemClock.uptimeMillis() - j >= BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
        ArrayList arrayList = new ArrayList();
        int size = list.size();
        int i2 = 0;
        while (i2 < size) {
            String str = (String) list3.get(i2).first;
            File file = (File) list3.get(i2).second;
            Map<Integer, ApkChecksum> map = list2.get(i2);
            if (!z || i != 0) {
                try {
                    if (needToWait(file, i, map, injector)) {
                        injector.getHandler().postDelayed(new Runnable() { // from class: com.android.server.pm.ApkChecksums$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ApkChecksums.processRequiredChecksums(list, list2, i, iOnChecksumsReadyListener, injector, j);
                            }
                        }, 1000L);
                        return;
                    }
                    getRequiredApkChecksums(str, file, i, map);
                } catch (Throwable th) {
                    Slog.e("ApkChecksums", "Required checksum calculation error", th);
                }
            }
            arrayList.addAll(map.values());
            i2++;
            list3 = list;
        }
        try {
            iOnChecksumsReadyListener.onChecksumsReady(arrayList);
        } catch (RemoteException e) {
            Slog.w("ApkChecksums", e);
        }
    }

    public static void getAvailableApkChecksums(String str, File file, int i, String str2, Certificate[] certificateArr, Map<Integer, ApkChecksum> map, Injector injector) {
        Map<Integer, ApkChecksum> extractHashFromV2V3Signature;
        ApkChecksum extractHashFromFS;
        String absolutePath = file.getAbsolutePath();
        if (isRequired(1, i, map) && (extractHashFromFS = extractHashFromFS(str, absolutePath)) != null) {
            map.put(Integer.valueOf(extractHashFromFS.getType()), extractHashFromFS);
        }
        if ((isRequired(32, i, map) || isRequired(64, i, map)) && (extractHashFromV2V3Signature = extractHashFromV2V3Signature(str, absolutePath, i)) != null) {
            map.putAll(extractHashFromV2V3Signature);
        }
        getInstallerChecksums(str, file, i, str2, certificateArr, map, injector);
    }

    public static void getInstallerChecksums(String str, File file, int i, String str2, Certificate[] certificateArr, Map<Integer, ApkChecksum> map, Injector injector) {
        File findDigestsForFile;
        Signature[] signatures;
        Signature[] pastSigningCertificates;
        Signature signature;
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        if ((certificateArr == null || certificateArr.length != 0) && (findDigestsForFile = findDigestsForFile(file)) != null) {
            File findSignatureForDigests = findSignatureForDigests(findDigestsForFile);
            try {
                Checksum[] readChecksums = readChecksums(findDigestsForFile);
                if (findSignatureForDigests != null) {
                    Certificate[] verifySignature = verifySignature(readChecksums, Files.readAllBytes(findSignatureForDigests.toPath()));
                    if (verifySignature != null && verifySignature.length != 0) {
                        signatures = new Signature[verifySignature.length];
                        int length = verifySignature.length;
                        for (int i2 = 0; i2 < length; i2++) {
                            signatures[i2] = new Signature(verifySignature[i2].getEncoded());
                        }
                        pastSigningCertificates = null;
                    }
                    Slog.e("ApkChecksums", "Error validating signature");
                    return;
                }
                AndroidPackage androidPackage = injector.getPackageManagerInternal().getPackage(str2);
                if (androidPackage == null) {
                    Slog.e("ApkChecksums", "Installer package not found.");
                    return;
                } else {
                    signatures = androidPackage.getSigningDetails().getSignatures();
                    pastSigningCertificates = androidPackage.getSigningDetails().getPastSigningCertificates();
                }
                if (signatures != null && signatures.length != 0 && (signature = signatures[0]) != null) {
                    byte[] byteArray = signature.toByteArray();
                    Set<Signature> convertToSet = convertToSet(certificateArr);
                    if (convertToSet != null && !convertToSet.isEmpty()) {
                        Signature isTrusted = isTrusted(signatures, convertToSet);
                        if (isTrusted == null) {
                            isTrusted = isTrusted(pastSigningCertificates, convertToSet);
                        }
                        if (isTrusted == null) {
                            return;
                        }
                        byteArray = isTrusted.toByteArray();
                    }
                    for (Checksum checksum : readChecksums) {
                        ApkChecksum apkChecksum = map.get(Integer.valueOf(checksum.getType()));
                        if (apkChecksum != null && !Arrays.equals(apkChecksum.getValue(), checksum.getValue())) {
                            throw new InvalidParameterException("System digest " + checksum.getType() + " mismatch, can't bind installer-provided digests to the APK.");
                        }
                    }
                    for (Checksum checksum2 : readChecksums) {
                        if (isRequired(checksum2.getType(), i, map)) {
                            map.put(Integer.valueOf(checksum2.getType()), new ApkChecksum(str, checksum2, str2, byteArray));
                        }
                    }
                    return;
                }
                Slog.e("ApkChecksums", "Can't obtain certificates.");
            } catch (IOException e) {
                Slog.e("ApkChecksums", "Error reading .digests or .signature", e);
            } catch (InvalidParameterException | NoSuchAlgorithmException | SignatureException e2) {
                Slog.e("ApkChecksums", "Error validating digests. Invalid digests will be removed", e2);
                try {
                    Files.deleteIfExists(findDigestsForFile.toPath());
                    if (findSignatureForDigests != null) {
                        Files.deleteIfExists(findSignatureForDigests.toPath());
                    }
                } catch (IOException unused) {
                }
            } catch (CertificateEncodingException e3) {
                Slog.e("ApkChecksums", "Error encoding trustedInstallers", e3);
            }
        }
    }

    public static boolean needToWait(File file, int i, Map<Integer, ApkChecksum> map, Injector injector) throws IOException {
        if (isRequired(1, i, map) || isRequired(2, i, map) || isRequired(4, i, map) || isRequired(8, i, map) || isRequired(16, i, map) || isRequired(32, i, map) || isRequired(64, i, map)) {
            String absolutePath = file.getAbsolutePath();
            if (IncrementalManager.isIncrementalPath(absolutePath)) {
                IncrementalManager incrementalManager = injector.getIncrementalManager();
                if (incrementalManager == null) {
                    Slog.e("ApkChecksums", "IncrementalManager is missing.");
                    return false;
                }
                IncrementalStorage openStorage = incrementalManager.openStorage(absolutePath);
                if (openStorage == null) {
                    Slog.e("ApkChecksums", "IncrementalStorage is missing for a path on IncFs: " + absolutePath);
                    return false;
                }
                return !openStorage.isFileFullyLoaded(absolutePath);
            }
            return false;
        }
        return false;
    }

    public static void getRequiredApkChecksums(String str, File file, int i, Map<Integer, ApkChecksum> map) {
        String absolutePath = file.getAbsolutePath();
        if (isRequired(1, i, map)) {
            try {
                map.put(1, new ApkChecksum(str, 1, verityHashForFile(file, VerityBuilder.generateFsVerityRootHash(absolutePath, (byte[]) null, new ByteBufferFactory() { // from class: com.android.server.pm.ApkChecksums.1
                    public ByteBuffer create(int i2) {
                        return ByteBuffer.allocate(i2);
                    }
                }))));
            } catch (IOException | DigestException | NoSuchAlgorithmException e) {
                Slog.e("ApkChecksums", "Error calculating WHOLE_MERKLE_ROOT_4K_SHA256", e);
            }
        }
        calculateChecksumIfRequested(map, str, file, i, 2);
        calculateChecksumIfRequested(map, str, file, i, 4);
        calculateChecksumIfRequested(map, str, file, i, 8);
        calculateChecksumIfRequested(map, str, file, i, 16);
        calculatePartialChecksumsIfRequested(map, str, file, i);
    }

    public static boolean isRequired(int i, int i2, Map<Integer, ApkChecksum> map) {
        return ((i2 & i) == 0 || map.containsKey(Integer.valueOf(i))) ? false : true;
    }

    public static Set<Signature> convertToSet(Certificate[] certificateArr) throws CertificateEncodingException {
        if (certificateArr == null) {
            return null;
        }
        ArraySet arraySet = new ArraySet(certificateArr.length);
        for (Certificate certificate : certificateArr) {
            arraySet.add(new Signature(certificate.getEncoded()));
        }
        return arraySet;
    }

    public static Signature isTrusted(Signature[] signatureArr, Set<Signature> set) {
        if (signatureArr == null) {
            return null;
        }
        for (Signature signature : signatureArr) {
            if (set.contains(signature)) {
                return signature;
            }
        }
        return null;
    }

    public static boolean containsFile(File file, String str) {
        if (file == null) {
            return false;
        }
        return FileUtils.contains(file.getAbsolutePath(), str);
    }

    public static ApkChecksum extractHashFromFS(String str, String str2) {
        byte[] fsverityDigest;
        if (!containsFile(Environment.getProductDirectory(), str2) && (fsverityDigest = VerityUtils.getFsverityDigest(str2)) != null) {
            return new ApkChecksum(str, 1, fsverityDigest);
        }
        try {
            byte[] bArr = (byte[]) ApkSignatureSchemeV4Verifier.extractCertificates(str2).contentDigests.getOrDefault(3, null);
            if (bArr != null) {
                return new ApkChecksum(str, 1, verityHashForFile(new File(str2), bArr));
            }
        } catch (SecurityException e) {
            Slog.e("ApkChecksums", "V4 signature error", e);
        } catch (SignatureNotFoundException unused) {
        }
        return null;
    }

    public static byte[] verityHashForFile(File file, byte[] bArr) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(256);
            allocate.order(ByteOrder.LITTLE_ENDIAN);
            allocate.put((byte) 1);
            allocate.put((byte) 1);
            allocate.put((byte) 12);
            allocate.put((byte) 0);
            allocate.putInt(0);
            allocate.putLong(file.length());
            allocate.put(bArr);
            for (int i = 0; i < 208; i++) {
                allocate.put((byte) 0);
            }
            allocate.flip();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(allocate);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            Slog.e("ApkChecksums", "Device does not support MessageDigest algorithm", e);
            return null;
        }
    }

    public static Map<Integer, ApkChecksum> extractHashFromV2V3Signature(String str, String str2, int i) {
        Map map;
        byte[] bArr;
        byte[] bArr2;
        ParseResult verifySignaturesInternal = ApkSignatureVerifier.verifySignaturesInternal(ParseTypeImpl.forDefaultParsing(), str2, 2, false);
        if (verifySignaturesInternal.isError()) {
            if (!(verifySignaturesInternal.getException() instanceof SignatureNotFoundException)) {
                Slog.e("ApkChecksums", "Signature verification error", verifySignaturesInternal.getException());
            }
            map = null;
        } else {
            map = ((ApkSignatureVerifier.SigningDetailsWithDigests) verifySignaturesInternal.getResult()).contentDigests;
        }
        if (map == null) {
            return null;
        }
        ArrayMap arrayMap = new ArrayMap();
        if ((i & 32) != 0 && (bArr2 = (byte[]) map.getOrDefault(1, null)) != null) {
            arrayMap.put(32, new ApkChecksum(str, 32, bArr2));
        }
        if ((i & 64) != 0 && (bArr = (byte[]) map.getOrDefault(2, null)) != null) {
            arrayMap.put(64, new ApkChecksum(str, 64, bArr));
        }
        return arrayMap;
    }

    public static String getMessageDigestAlgoForChecksumKind(int i) throws NoSuchAlgorithmException {
        if (i != 2) {
            if (i != 4) {
                if (i != 8) {
                    if (i == 16) {
                        return "SHA512";
                    }
                    throw new NoSuchAlgorithmException("Invalid checksum type: " + i);
                }
                return "SHA256";
            }
            return "SHA1";
        }
        return "MD5";
    }

    public static void calculateChecksumIfRequested(Map<Integer, ApkChecksum> map, String str, File file, int i, int i2) {
        byte[] apkChecksum;
        if ((i & i2) == 0 || map.containsKey(Integer.valueOf(i2)) || (apkChecksum = getApkChecksum(file, i2)) == null) {
            return;
        }
        map.put(Integer.valueOf(i2), new ApkChecksum(str, i2, apkChecksum));
    }

    public static byte[] getApkChecksum(File file, int i) {
        int max = (int) Math.max(4096L, Math.min(131072L, file.length()));
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                byte[] bArr = new byte[max];
                MessageDigest messageDigest = MessageDigest.getInstance(getMessageDigestAlgoForChecksumKind(i));
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read != -1) {
                        messageDigest.update(bArr, 0, read);
                    } else {
                        byte[] digest = messageDigest.digest();
                        fileInputStream.close();
                        return digest;
                    }
                }
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            Slog.e("ApkChecksums", "Error reading " + file.getAbsolutePath() + " to compute hash.", e);
            return null;
        } catch (NoSuchAlgorithmException e2) {
            Slog.e("ApkChecksums", "Device does not support MessageDigest algorithm", e2);
            return null;
        }
    }

    public static int[] getContentDigestAlgos(boolean z, boolean z2) {
        if (z && z2) {
            return new int[]{1, 2};
        }
        if (z) {
            return new int[]{1};
        }
        return new int[]{2};
    }

    public static void calculatePartialChecksumsIfRequested(Map<Integer, ApkChecksum> map, String str, File file, int i) {
        SignatureInfo signatureInfo;
        boolean z = true;
        boolean z2 = ((i & 32) == 0 || map.containsKey(32)) ? false : true;
        z = ((i & 64) == 0 || map.containsKey(64)) ? false : false;
        if (z2 || z) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                try {
                    try {
                        signatureInfo = ApkSignatureSchemeV3Verifier.findSignature(randomAccessFile);
                    } catch (SignatureNotFoundException unused) {
                        signatureInfo = ApkSignatureSchemeV2Verifier.findSignature(randomAccessFile);
                    }
                } catch (SignatureNotFoundException unused2) {
                    signatureInfo = null;
                }
                if (signatureInfo == null) {
                    Slog.e("ApkChecksums", "V2/V3 signatures not found in " + file.getAbsolutePath());
                    randomAccessFile.close();
                    return;
                }
                int[] contentDigestAlgos = getContentDigestAlgos(z2, z);
                byte[][] computeContentDigestsPer1MbChunk = ApkSigningBlockUtils.computeContentDigestsPer1MbChunk(contentDigestAlgos, randomAccessFile.getFD(), signatureInfo);
                int length = contentDigestAlgos.length;
                for (int i2 = 0; i2 < length; i2++) {
                    int checksumKindForContentDigestAlgo = getChecksumKindForContentDigestAlgo(contentDigestAlgos[i2]);
                    if (checksumKindForContentDigestAlgo != -1) {
                        map.put(Integer.valueOf(checksumKindForContentDigestAlgo), new ApkChecksum(str, checksumKindForContentDigestAlgo, computeContentDigestsPer1MbChunk[i2]));
                    }
                }
                randomAccessFile.close();
            } catch (IOException | DigestException e) {
                Slog.e("ApkChecksums", "Error computing hash.", e);
            }
        }
    }
}
