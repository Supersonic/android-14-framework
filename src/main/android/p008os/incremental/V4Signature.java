package android.p008os.incremental;

import android.p008os.ParcelFileDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
/* renamed from: android.os.incremental.V4Signature */
/* loaded from: classes3.dex */
public class V4Signature {
    public static final String EXT = ".idsig";
    public static final int HASHING_ALGORITHM_SHA256 = 1;
    public static final int INCFS_MAX_SIGNATURE_SIZE = 8096;
    public static final byte LOG2_BLOCK_SIZE_4096_BYTES = 12;
    public static final int SUPPORTED_VERSION = 2;
    public final byte[] hashingInfo;
    public final byte[] signingInfos;
    public final int version;

    /* renamed from: android.os.incremental.V4Signature$HashingInfo */
    /* loaded from: classes3.dex */
    public static class HashingInfo {
        public final int hashAlgorithm;
        public final byte log2BlockSize;
        public final byte[] rawRootHash;
        public final byte[] salt;

        HashingInfo(int hashAlgorithm, byte log2BlockSize, byte[] salt, byte[] rawRootHash) {
            this.hashAlgorithm = hashAlgorithm;
            this.log2BlockSize = log2BlockSize;
            this.salt = salt;
            this.rawRootHash = rawRootHash;
        }

        public static HashingInfo fromByteArray(byte[] bytes) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            int hashAlgorithm = buffer.getInt();
            byte log2BlockSize = buffer.get();
            byte[] salt = V4Signature.readBytes(buffer);
            byte[] rawRootHash = V4Signature.readBytes(buffer);
            return new HashingInfo(hashAlgorithm, log2BlockSize, salt, rawRootHash);
        }
    }

    /* renamed from: android.os.incremental.V4Signature$SigningInfo */
    /* loaded from: classes3.dex */
    public static class SigningInfo {
        public final byte[] additionalData;
        public final byte[] apkDigest;
        public final byte[] certificate;
        public final byte[] publicKey;
        public final byte[] signature;
        public final int signatureAlgorithmId;

        SigningInfo(byte[] apkDigest, byte[] certificate, byte[] additionalData, byte[] publicKey, int signatureAlgorithmId, byte[] signature) {
            this.apkDigest = apkDigest;
            this.certificate = certificate;
            this.additionalData = additionalData;
            this.publicKey = publicKey;
            this.signatureAlgorithmId = signatureAlgorithmId;
            this.signature = signature;
        }

        public static SigningInfo fromByteArray(byte[] bytes) throws IOException {
            return fromByteBuffer(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
        }

        public static SigningInfo fromByteBuffer(ByteBuffer buffer) throws IOException {
            byte[] apkDigest = V4Signature.readBytes(buffer);
            byte[] certificate = V4Signature.readBytes(buffer);
            byte[] additionalData = V4Signature.readBytes(buffer);
            byte[] publicKey = V4Signature.readBytes(buffer);
            int signatureAlgorithmId = buffer.getInt();
            byte[] signature = V4Signature.readBytes(buffer);
            return new SigningInfo(apkDigest, certificate, additionalData, publicKey, signatureAlgorithmId, signature);
        }
    }

    /* renamed from: android.os.incremental.V4Signature$SigningInfoBlock */
    /* loaded from: classes3.dex */
    public static class SigningInfoBlock {
        public final int blockId;
        public final byte[] signingInfo;

        public SigningInfoBlock(int blockId, byte[] signingInfo) {
            this.blockId = blockId;
            this.signingInfo = signingInfo;
        }

        static SigningInfoBlock fromByteBuffer(ByteBuffer buffer) throws IOException {
            int blockId = buffer.getInt();
            byte[] signingInfo = V4Signature.readBytes(buffer);
            return new SigningInfoBlock(blockId, signingInfo);
        }
    }

    /* renamed from: android.os.incremental.V4Signature$SigningInfos */
    /* loaded from: classes3.dex */
    public static class SigningInfos {
        public final SigningInfo signingInfo;
        public final SigningInfoBlock[] signingInfoBlocks;

        public SigningInfos(SigningInfo signingInfo) {
            this.signingInfo = signingInfo;
            this.signingInfoBlocks = new SigningInfoBlock[0];
        }

        public SigningInfos(SigningInfo signingInfo, SigningInfoBlock... signingInfoBlocks) {
            this.signingInfo = signingInfo;
            this.signingInfoBlocks = signingInfoBlocks;
        }

        public static SigningInfos fromByteArray(byte[] bytes) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            SigningInfo signingInfo = SigningInfo.fromByteBuffer(buffer);
            if (!buffer.hasRemaining()) {
                return new SigningInfos(signingInfo);
            }
            ArrayList<SigningInfoBlock> signingInfoBlocks = new ArrayList<>(1);
            while (buffer.hasRemaining()) {
                signingInfoBlocks.add(SigningInfoBlock.fromByteBuffer(buffer));
            }
            return new SigningInfos(signingInfo, (SigningInfoBlock[]) signingInfoBlocks.toArray(new SigningInfoBlock[signingInfoBlocks.size()]));
        }
    }

    public static V4Signature readFrom(ParcelFileDescriptor pfd) throws IOException {
        InputStream stream = new ParcelFileDescriptor.AutoCloseInputStream(pfd.dup());
        try {
            V4Signature readFrom = readFrom(stream);
            stream.close();
            return readFrom;
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static V4Signature readFrom(byte[] bytes) throws IOException {
        InputStream stream = new ByteArrayInputStream(bytes);
        try {
            V4Signature readFrom = readFrom(stream);
            stream.close();
            return readFrom;
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            writeTo(stream);
            byte[] byteArray = stream.toByteArray();
            stream.close();
            return byteArray;
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] getSignedData(long fileSize, HashingInfo hashingInfo, SigningInfo signingInfo) {
        int size = bytesSize(hashingInfo.salt) + 17 + bytesSize(hashingInfo.rawRootHash) + bytesSize(signingInfo.apkDigest) + bytesSize(signingInfo.certificate) + bytesSize(signingInfo.additionalData);
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(size);
        buffer.putLong(fileSize);
        buffer.putInt(hashingInfo.hashAlgorithm);
        buffer.put(hashingInfo.log2BlockSize);
        writeBytes(buffer, hashingInfo.salt);
        writeBytes(buffer, hashingInfo.rawRootHash);
        writeBytes(buffer, signingInfo.apkDigest);
        writeBytes(buffer, signingInfo.certificate);
        writeBytes(buffer, signingInfo.additionalData);
        return buffer.array();
    }

    public boolean isVersionSupported() {
        return this.version == 2;
    }

    private V4Signature(int version, byte[] hashingInfo, byte[] signingInfos) {
        this.version = version;
        this.hashingInfo = hashingInfo;
        this.signingInfos = signingInfos;
    }

    private static V4Signature readFrom(InputStream stream) throws IOException {
        int version = readIntLE(stream);
        int maxSize = INCFS_MAX_SIGNATURE_SIZE;
        byte[] hashingInfo = readBytes(stream, INCFS_MAX_SIGNATURE_SIZE);
        if (hashingInfo != null) {
            maxSize = INCFS_MAX_SIGNATURE_SIZE - hashingInfo.length;
        }
        byte[] signingInfo = readBytes(stream, maxSize);
        return new V4Signature(version, hashingInfo, signingInfo);
    }

    private void writeTo(OutputStream stream) throws IOException {
        writeIntLE(stream, this.version);
        writeBytes(stream, this.hashingInfo);
        writeBytes(stream, this.signingInfos);
    }

    private static int bytesSize(byte[] bytes) {
        return (bytes == null ? 0 : bytes.length) + 4;
    }

    private static void readFully(InputStream stream, byte[] buffer) throws IOException {
        int len = buffer.length;
        int n = 0;
        while (n < len) {
            int count = stream.read(buffer, n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    private static int readIntLE(InputStream stream) throws IOException {
        byte[] buffer = new byte[4];
        readFully(stream, buffer);
        return ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static void writeIntLE(OutputStream stream, int v) throws IOException {
        byte[] buffer = ByteBuffer.wrap(new byte[4]).order(ByteOrder.LITTLE_ENDIAN).putInt(v).array();
        stream.write(buffer);
    }

    private static byte[] readBytes(InputStream stream, int maxSize) throws IOException {
        try {
            int size = readIntLE(stream);
            if (size > maxSize) {
                throw new IOException("Signature is too long. Max allowed is 8096");
            }
            byte[] bytes = new byte[size];
            readFully(stream, bytes);
            return bytes;
        } catch (EOFException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] readBytes(ByteBuffer buffer) throws IOException {
        if (buffer.remaining() < 4) {
            throw new EOFException();
        }
        int size = buffer.getInt();
        if (buffer.remaining() < size) {
            throw new EOFException();
        }
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return bytes;
    }

    private static void writeBytes(OutputStream stream, byte[] bytes) throws IOException {
        if (bytes == null) {
            writeIntLE(stream, 0);
            return;
        }
        writeIntLE(stream, bytes.length);
        stream.write(bytes);
    }

    private static void writeBytes(ByteBuffer buffer, byte[] bytes) {
        if (bytes == null) {
            buffer.putInt(0);
            return;
        }
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }
}
