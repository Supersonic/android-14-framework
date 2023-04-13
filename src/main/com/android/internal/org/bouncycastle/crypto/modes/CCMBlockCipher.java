package com.android.internal.org.bouncycastle.crypto.modes;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import com.android.internal.org.bouncycastle.crypto.Mac;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import com.android.internal.org.bouncycastle.crypto.params.AEADParameters;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayOutputStream;
/* loaded from: classes4.dex */
public class CCMBlockCipher implements AEADBlockCipher {
    private int blockSize;
    private BlockCipher cipher;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private CipherParameters keyParam;
    private byte[] macBlock;
    private int macSize;
    private byte[] nonce;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();

    public CCMBlockCipher(BlockCipher c) {
        this.cipher = c;
        int blockSize = c.getBlockSize();
        this.blockSize = blockSize;
        this.macBlock = new byte[blockSize];
        if (blockSize != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters cipherParameters;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            this.nonce = param.getNonce();
            this.initialAssociatedText = param.getAssociatedText();
            this.macSize = getMacSize(forEncryption, param.getMacSize());
            cipherParameters = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            this.nonce = param2.getIV();
            this.initialAssociatedText = null;
            this.macSize = getMacSize(forEncryption, 64);
            cipherParameters = param2.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to CCM: " + params.getClass().getName());
        }
        if (cipherParameters != null) {
            this.keyParam = cipherParameters;
        }
        byte[] bArr = this.nonce;
        if (bArr == null || bArr.length < 7 || bArr.length > 13) {
            throw new IllegalArgumentException("nonce must have length from 7 to 13 octets");
        }
        reset();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CCM";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.associatedText.write(in, inOff, len);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in);
        return 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length < inOff + inLen) {
            throw new DataLengthException("Input buffer too short");
        }
        this.data.write(in, inOff, inLen);
        return 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int len = processPacket(this.data.getBuffer(), 0, this.data.size(), out, outOff);
        reset();
        return len;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public void reset() {
        this.cipher.reset();
        this.associatedText.reset();
        this.data.reset();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, mac.length);
        return mac;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public int getUpdateOutputSize(int len) {
        return 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.AEADCipher
    public int getOutputSize(int len) {
        int totalData = this.data.size() + len;
        if (this.forEncryption) {
            return this.macSize + totalData;
        }
        int i = this.macSize;
        if (totalData < i) {
            return 0;
        }
        return totalData - i;
    }

    public byte[] processPacket(byte[] in, int inOff, int inLen) throws IllegalStateException, InvalidCipherTextException {
        byte[] output;
        if (this.forEncryption) {
            output = new byte[this.macSize + inLen];
        } else {
            int i = this.macSize;
            if (inLen < i) {
                throw new InvalidCipherTextException("data too short");
            }
            output = new byte[inLen - i];
        }
        processPacket(in, inOff, inLen, output, 0);
        return output;
    }

    public int processPacket(byte[] in, int inOff, int inLen, byte[] output, int outOff) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
        int outputLen;
        int i;
        int i2;
        if (this.keyParam == null) {
            throw new IllegalStateException("CCM cipher unitialized.");
        }
        byte[] bArr = this.nonce;
        int q = 15 - bArr.length;
        if (q < 4) {
            int limitLen = 1 << (q * 8);
            if (inLen >= limitLen) {
                throw new IllegalStateException("CCM packet too large for choice of q.");
            }
        }
        int limitLen2 = this.blockSize;
        byte[] iv = new byte[limitLen2];
        iv[0] = (byte) ((q - 1) & 7);
        System.arraycopy(bArr, 0, iv, 1, bArr.length);
        BlockCipher ctrCipher = new SICBlockCipher(this.cipher);
        ctrCipher.init(this.forEncryption, new ParametersWithIV(this.keyParam, iv));
        int inIndex = inOff;
        int outIndex = outOff;
        if (!this.forEncryption) {
            int n = this.macSize;
            if (inLen < n) {
                throw new InvalidCipherTextException("data too short");
            }
            outputLen = inLen - n;
            if (output.length >= outputLen + outOff) {
                byte b = 0;
                System.arraycopy(in, inOff + outputLen, this.macBlock, 0, n);
                byte[] bArr2 = this.macBlock;
                ctrCipher.processBlock(bArr2, 0, bArr2, 0);
                int i3 = this.macSize;
                while (true) {
                    byte[] bArr3 = this.macBlock;
                    if (i3 == bArr3.length) {
                        break;
                    }
                    bArr3[i3] = b;
                    i3++;
                    b = 0;
                }
                while (true) {
                    int i4 = inOff + outputLen;
                    i = this.blockSize;
                    if (inIndex >= i4 - i) {
                        break;
                    }
                    ctrCipher.processBlock(in, inIndex, output, outIndex);
                    int i5 = this.blockSize;
                    outIndex += i5;
                    inIndex += i5;
                }
                byte[] block = new byte[i];
                System.arraycopy(in, inIndex, block, 0, outputLen - (inIndex - inOff));
                ctrCipher.processBlock(block, 0, block, 0);
                System.arraycopy(block, 0, output, outIndex, outputLen - (inIndex - inOff));
                byte[] calculatedMacBlock = new byte[this.blockSize];
                calculateMac(output, outOff, outputLen, calculatedMacBlock);
                if (!Arrays.constantTimeAreEqual(this.macBlock, calculatedMacBlock)) {
                    throw new InvalidCipherTextException("mac check in CCM failed");
                }
            } else {
                throw new OutputLengthException("Output buffer too short.");
            }
        } else {
            outputLen = this.macSize + inLen;
            if (output.length < outputLen + outOff) {
                throw new OutputLengthException("Output buffer too short.");
            }
            calculateMac(in, inOff, inLen, this.macBlock);
            byte[] encMac = new byte[this.blockSize];
            ctrCipher.processBlock(this.macBlock, 0, encMac, 0);
            while (true) {
                i2 = this.blockSize;
                if (inIndex >= (inOff + inLen) - i2) {
                    break;
                }
                ctrCipher.processBlock(in, inIndex, output, outIndex);
                int i6 = this.blockSize;
                outIndex += i6;
                inIndex += i6;
            }
            byte[] block2 = new byte[i2];
            System.arraycopy(in, inIndex, block2, 0, (inLen + inOff) - inIndex);
            ctrCipher.processBlock(block2, 0, block2, 0);
            System.arraycopy(block2, 0, output, outIndex, (inLen + inOff) - inIndex);
            System.arraycopy(encMac, 0, output, outOff + inLen, this.macSize);
        }
        return outputLen;
    }

    private int calculateMac(byte[] data, int dataOff, int dataLen, byte[] macBlock) {
        int extra;
        Mac cMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
        cMac.init(this.keyParam);
        byte[] b0 = new byte[16];
        if (hasAssociatedText()) {
            b0[0] = (byte) (b0[0] | 64);
        }
        b0[0] = (byte) (b0[0] | ((((cMac.getMacSize() - 2) / 2) & 7) << 3));
        byte b = b0[0];
        byte[] bArr = this.nonce;
        b0[0] = (byte) (b | (((15 - bArr.length) - 1) & 7));
        System.arraycopy(bArr, 0, b0, 1, bArr.length);
        int q = dataLen;
        int count = 1;
        while (q > 0) {
            b0[b0.length - count] = (byte) (q & 255);
            q >>>= 8;
            count++;
        }
        cMac.update(b0, 0, b0.length);
        if (hasAssociatedText()) {
            int textLength = getAssociatedTextLength();
            if (textLength < 65280) {
                cMac.update((byte) (textLength >> 8));
                cMac.update((byte) textLength);
                extra = 2;
            } else {
                cMac.update((byte) -1);
                cMac.update((byte) -2);
                cMac.update((byte) (textLength >> 24));
                cMac.update((byte) (textLength >> 16));
                cMac.update((byte) (textLength >> 8));
                cMac.update((byte) textLength);
                extra = 6;
            }
            byte[] bArr2 = this.initialAssociatedText;
            if (bArr2 != null) {
                cMac.update(bArr2, 0, bArr2.length);
            }
            if (this.associatedText.size() > 0) {
                cMac.update(this.associatedText.getBuffer(), 0, this.associatedText.size());
            }
            int extra2 = (extra + textLength) % 16;
            if (extra2 != 0) {
                for (int i = extra2; i != 16; i++) {
                    cMac.update((byte) 0);
                }
            }
        }
        cMac.update(data, dataOff, dataLen);
        return cMac.doFinal(macBlock, 0);
    }

    private int getMacSize(boolean forEncryption, int requestedMacBits) {
        if (forEncryption && (requestedMacBits < 32 || requestedMacBits > 128 || (requestedMacBits & 15) != 0)) {
            throw new IllegalArgumentException("tag length in octets must be one of {4,6,8,10,12,14,16}");
        }
        return requestedMacBits >>> 3;
    }

    private int getAssociatedTextLength() {
        int size = this.associatedText.size();
        byte[] bArr = this.initialAssociatedText;
        return size + (bArr == null ? 0 : bArr.length);
    }

    private boolean hasAssociatedText() {
        return getAssociatedTextLength() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public ExposedByteArrayOutputStream() {
        }

        public byte[] getBuffer() {
            return this.buf;
        }
    }
}
