package android.util.apk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public abstract class VerityBuilder {
    private static final int CHUNK_SIZE_BYTES = 4096;
    private static final byte[] DEFAULT_SALT = new byte[8];
    private static final int DIGEST_SIZE_BYTES = 32;
    private static final int FSVERITY_HEADER_SIZE_BYTES = 64;
    private static final String JCA_DIGEST_ALGORITHM = "SHA-256";
    private static final int MMAP_REGION_SIZE_BYTES = 1048576;
    private static final int ZIP_EOCD_CENTRAL_DIR_OFFSET_FIELD_OFFSET = 16;
    private static final int ZIP_EOCD_CENTRAL_DIR_OFFSET_FIELD_SIZE = 4;

    private VerityBuilder() {
    }

    /* loaded from: classes3.dex */
    public static class VerityResult {
        public final int merkleTreeSize;
        public final byte[] rootHash;
        public final ByteBuffer verityData;

        private VerityResult(ByteBuffer verityData, int merkleTreeSize, byte[] rootHash) {
            this.verityData = verityData;
            this.merkleTreeSize = merkleTreeSize;
            this.rootHash = rootHash;
        }
    }

    public static VerityResult generateApkVerityTree(RandomAccessFile apk, SignatureInfo signatureInfo, ByteBufferFactory bufferFactory) throws IOException, SecurityException, NoSuchAlgorithmException, DigestException {
        return generateVerityTreeInternal(apk, bufferFactory, signatureInfo);
    }

    private static VerityResult generateVerityTreeInternal(RandomAccessFile apk, ByteBufferFactory bufferFactory, SignatureInfo signatureInfo) throws IOException, SecurityException, NoSuchAlgorithmException, DigestException {
        long signingBlockSize = signatureInfo.centralDirOffset - signatureInfo.apkSigningBlockOffset;
        long dataSize = apk.getChannel().size() - signingBlockSize;
        int[] levelOffset = calculateVerityLevelOffset(dataSize);
        int merkleTreeSize = levelOffset[levelOffset.length - 1];
        ByteBuffer output = bufferFactory.create(merkleTreeSize + 4096);
        output.order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer tree = slice(output, 0, merkleTreeSize);
        byte[] apkRootHash = generateVerityTreeInternal(apk, signatureInfo, DEFAULT_SALT, levelOffset, tree);
        return new VerityResult(output, merkleTreeSize, apkRootHash);
    }

    static void generateApkVerityFooter(RandomAccessFile apk, SignatureInfo signatureInfo, ByteBuffer footerOutput) throws IOException {
        footerOutput.order(ByteOrder.LITTLE_ENDIAN);
        generateApkVerityHeader(footerOutput, apk.getChannel().size(), DEFAULT_SALT);
        long signingBlockSize = signatureInfo.centralDirOffset - signatureInfo.apkSigningBlockOffset;
        generateApkVerityExtensions(footerOutput, signatureInfo.apkSigningBlockOffset, signingBlockSize, signatureInfo.eocdOffset);
    }

    public static byte[] generateFsVerityRootHash(String apkPath, byte[] salt, ByteBufferFactory bufferFactory) throws IOException, NoSuchAlgorithmException, DigestException {
        RandomAccessFile apk = new RandomAccessFile(apkPath, "r");
        try {
            int[] levelOffset = calculateVerityLevelOffset(apk.length());
            int merkleTreeSize = levelOffset[levelOffset.length - 1];
            ByteBuffer output = bufferFactory.create(merkleTreeSize + 4096);
            output.order(ByteOrder.LITTLE_ENDIAN);
            ByteBuffer tree = slice(output, 0, merkleTreeSize);
            byte[] generateFsVerityTreeInternal = generateFsVerityTreeInternal(apk, salt, levelOffset, tree);
            apk.close();
            return generateFsVerityTreeInternal;
        } catch (Throwable th) {
            try {
                apk.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] generateApkVerity(String apkPath, ByteBufferFactory bufferFactory, SignatureInfo signatureInfo) throws IOException, SignatureNotFoundException, SecurityException, DigestException, NoSuchAlgorithmException {
        RandomAccessFile apk = new RandomAccessFile(apkPath, "r");
        try {
            VerityResult result = generateVerityTreeInternal(apk, bufferFactory, signatureInfo);
            ByteBuffer footer = slice(result.verityData, result.merkleTreeSize, result.verityData.limit());
            generateApkVerityFooter(apk, signatureInfo, footer);
            footer.putInt(footer.position() + 4);
            result.verityData.limit(result.merkleTreeSize + footer.position());
            byte[] bArr = result.rootHash;
            apk.close();
            return bArr;
        } catch (Throwable th) {
            try {
                apk.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class BufferedDigester implements DataDigester {
        private static final int BUFFER_SIZE = 4096;
        private int mBytesDigestedSinceReset;
        private final byte[] mDigestBuffer;
        private final MessageDigest mMd;
        private final ByteBuffer mOutput;
        private final byte[] mSalt;

        private BufferedDigester(byte[] salt, ByteBuffer output) throws NoSuchAlgorithmException {
            this.mDigestBuffer = new byte[32];
            this.mSalt = salt;
            this.mOutput = output.slice();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            this.mMd = messageDigest;
            if (salt != null) {
                messageDigest.update(salt);
            }
            this.mBytesDigestedSinceReset = 0;
        }

        @Override // android.util.apk.DataDigester
        public void consume(ByteBuffer buffer) throws DigestException {
            int offset = buffer.position();
            int remaining = buffer.remaining();
            while (remaining > 0) {
                int allowance = Math.min(remaining, 4096 - this.mBytesDigestedSinceReset);
                buffer.limit(buffer.position() + allowance);
                this.mMd.update(buffer);
                offset += allowance;
                remaining -= allowance;
                int i = this.mBytesDigestedSinceReset + allowance;
                this.mBytesDigestedSinceReset = i;
                if (i == 4096) {
                    MessageDigest messageDigest = this.mMd;
                    byte[] bArr = this.mDigestBuffer;
                    messageDigest.digest(bArr, 0, bArr.length);
                    this.mOutput.put(this.mDigestBuffer);
                    byte[] bArr2 = this.mSalt;
                    if (bArr2 != null) {
                        this.mMd.update(bArr2);
                    }
                    this.mBytesDigestedSinceReset = 0;
                }
            }
        }

        public void assertEmptyBuffer() throws DigestException {
            if (this.mBytesDigestedSinceReset != 0) {
                throw new IllegalStateException("Buffer is not empty: " + this.mBytesDigestedSinceReset);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillUpLastOutputChunk() {
            int lastBlockSize = this.mOutput.position() % 4096;
            if (lastBlockSize == 0) {
                return;
            }
            this.mOutput.put(ByteBuffer.allocate(4096 - lastBlockSize));
        }
    }

    private static void consumeByChunk(DataDigester digester, DataSource source, int chunkSize) throws IOException, DigestException {
        long inputRemaining = source.size();
        long inputOffset = 0;
        while (inputRemaining > 0) {
            int size = (int) Math.min(inputRemaining, chunkSize);
            source.feedIntoDataDigester(digester, inputOffset, size);
            inputOffset += size;
            inputRemaining -= size;
        }
    }

    private static void generateFsVerityDigestAtLeafLevel(RandomAccessFile file, byte[] salt, ByteBuffer output) throws IOException, NoSuchAlgorithmException, DigestException {
        BufferedDigester digester = new BufferedDigester(salt, output);
        consumeByChunk(digester, DataSource.create(file.getFD(), 0L, file.length()), 1048576);
        int lastIncompleteChunkSize = (int) (file.length() % 4096);
        if (lastIncompleteChunkSize != 0) {
            digester.consume(ByteBuffer.allocate(4096 - lastIncompleteChunkSize));
        }
        digester.assertEmptyBuffer();
        digester.fillUpLastOutputChunk();
    }

    private static void generateApkVerityDigestAtLeafLevel(RandomAccessFile apk, SignatureInfo signatureInfo, byte[] salt, ByteBuffer output) throws IOException, NoSuchAlgorithmException, DigestException {
        BufferedDigester digester = new BufferedDigester(salt, output);
        consumeByChunk(digester, DataSource.create(apk.getFD(), 0L, signatureInfo.apkSigningBlockOffset), 1048576);
        long eocdCdOffsetFieldPosition = signatureInfo.eocdOffset + 16;
        consumeByChunk(digester, DataSource.create(apk.getFD(), signatureInfo.centralDirOffset, eocdCdOffsetFieldPosition - signatureInfo.centralDirOffset), 1048576);
        ByteBuffer alternativeCentralDirOffset = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        alternativeCentralDirOffset.putInt(Math.toIntExact(signatureInfo.apkSigningBlockOffset));
        alternativeCentralDirOffset.flip();
        digester.consume(alternativeCentralDirOffset);
        long offsetAfterEocdCdOffsetField = 4 + eocdCdOffsetFieldPosition;
        consumeByChunk(digester, DataSource.create(apk.getFD(), offsetAfterEocdCdOffsetField, apk.getChannel().size() - offsetAfterEocdCdOffsetField), 1048576);
        int lastIncompleteChunkSize = (int) (apk.getChannel().size() % 4096);
        if (lastIncompleteChunkSize != 0) {
            digester.consume(ByteBuffer.allocate(4096 - lastIncompleteChunkSize));
        }
        digester.assertEmptyBuffer();
        digester.fillUpLastOutputChunk();
    }

    private static byte[] generateFsVerityTreeInternal(RandomAccessFile apk, byte[] salt, int[] levelOffset, ByteBuffer output) throws IOException, NoSuchAlgorithmException, DigestException {
        generateFsVerityDigestAtLeafLevel(apk, salt, slice(output, levelOffset[levelOffset.length - 2], levelOffset[levelOffset.length - 1]));
        for (int level = levelOffset.length - 3; level >= 0; level--) {
            ByteBuffer inputBuffer = slice(output, levelOffset[level + 1], levelOffset[level + 2]);
            ByteBuffer outputBuffer = slice(output, levelOffset[level], levelOffset[level + 1]);
            DataSource source = new ByteBufferDataSource(inputBuffer);
            BufferedDigester digester = new BufferedDigester(salt, outputBuffer);
            consumeByChunk(digester, source, 4096);
            digester.assertEmptyBuffer();
            digester.fillUpLastOutputChunk();
        }
        byte[] rootHash = new byte[32];
        BufferedDigester digester2 = new BufferedDigester(salt, ByteBuffer.wrap(rootHash));
        digester2.consume(slice(output, 0, 4096));
        digester2.assertEmptyBuffer();
        return rootHash;
    }

    private static byte[] generateVerityTreeInternal(RandomAccessFile apk, SignatureInfo signatureInfo, byte[] salt, int[] levelOffset, ByteBuffer output) throws IOException, NoSuchAlgorithmException, DigestException {
        assertSigningBlockAlignedAndHasFullPages(signatureInfo);
        generateApkVerityDigestAtLeafLevel(apk, signatureInfo, salt, slice(output, levelOffset[levelOffset.length - 2], levelOffset[levelOffset.length - 1]));
        for (int level = levelOffset.length - 3; level >= 0; level--) {
            ByteBuffer inputBuffer = slice(output, levelOffset[level + 1], levelOffset[level + 2]);
            ByteBuffer outputBuffer = slice(output, levelOffset[level], levelOffset[level + 1]);
            DataSource source = new ByteBufferDataSource(inputBuffer);
            BufferedDigester digester = new BufferedDigester(salt, outputBuffer);
            consumeByChunk(digester, source, 4096);
            digester.assertEmptyBuffer();
            digester.fillUpLastOutputChunk();
        }
        byte[] rootHash = new byte[32];
        BufferedDigester digester2 = new BufferedDigester(salt, ByteBuffer.wrap(rootHash));
        digester2.consume(slice(output, 0, 4096));
        digester2.assertEmptyBuffer();
        return rootHash;
    }

    private static ByteBuffer generateApkVerityHeader(ByteBuffer buffer, long fileSize, byte[] salt) {
        if (salt.length != 8) {
            throw new IllegalArgumentException("salt is not 8 bytes long");
        }
        buffer.put("TrueBrew".getBytes());
        buffer.put((byte) 1);
        buffer.put((byte) 0);
        buffer.put((byte) 12);
        buffer.put((byte) 7);
        buffer.putShort((short) 1);
        buffer.putShort((short) 1);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putLong(fileSize);
        buffer.put((byte) 2);
        buffer.put((byte) 0);
        buffer.put(salt);
        skip(buffer, 22);
        return buffer;
    }

    private static ByteBuffer generateApkVerityExtensions(ByteBuffer buffer, long signingBlockOffset, long signingBlockSize, long eocdOffset) {
        buffer.putInt(24);
        buffer.putShort((short) 1);
        skip(buffer, 2);
        buffer.putLong(signingBlockOffset);
        buffer.putLong(signingBlockSize);
        buffer.putInt(20);
        buffer.putShort((short) 2);
        skip(buffer, 2);
        buffer.putLong(16 + eocdOffset);
        buffer.putInt(Math.toIntExact(signingBlockOffset));
        int kPadding = 4;
        if (4 == 8) {
            kPadding = 0;
        }
        skip(buffer, kPadding);
        return buffer;
    }

    private static int[] calculateVerityLevelOffset(long fileSize) {
        ArrayList<Long> levelSize = new ArrayList<>();
        while (true) {
            long levelDigestSize = divideRoundup(fileSize, 4096L) * 32;
            long chunksSize = divideRoundup(levelDigestSize, 4096L) * 4096;
            levelSize.add(Long.valueOf(chunksSize));
            if (levelDigestSize <= 4096) {
                break;
            }
            fileSize = levelDigestSize;
        }
        int[] levelOffset = new int[levelSize.size() + 1];
        levelOffset[0] = 0;
        for (int i = 0; i < levelSize.size(); i++) {
            levelOffset[i + 1] = levelOffset[i] + Math.toIntExact(levelSize.get((levelSize.size() - i) - 1).longValue());
        }
        return levelOffset;
    }

    private static void assertSigningBlockAlignedAndHasFullPages(SignatureInfo signatureInfo) {
        if (signatureInfo.apkSigningBlockOffset % 4096 != 0) {
            throw new IllegalArgumentException("APK Signing Block does not start at the page boundary: " + signatureInfo.apkSigningBlockOffset);
        }
        if ((signatureInfo.centralDirOffset - signatureInfo.apkSigningBlockOffset) % 4096 != 0) {
            throw new IllegalArgumentException("Size of APK Signing Block is not a multiple of 4096: " + (signatureInfo.centralDirOffset - signatureInfo.apkSigningBlockOffset));
        }
    }

    private static ByteBuffer slice(ByteBuffer buffer, int begin, int end) {
        ByteBuffer b = buffer.duplicate();
        b.position(0);
        b.limit(end);
        b.position(begin);
        return b.slice();
    }

    private static void skip(ByteBuffer buffer, int bytes) {
        buffer.position(buffer.position() + bytes);
    }

    private static long divideRoundup(long dividend, long divisor) {
        return ((dividend + divisor) - 1) / divisor;
    }
}
