package com.android.internal.org.bouncycastle.crypto.modes;

import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.SkippingStreamCipher;
import com.android.internal.org.bouncycastle.crypto.StreamBlockCipher;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public class SICBlockCipher extends StreamBlockCipher implements SkippingStreamCipher {

    /* renamed from: IV */
    private byte[] f762IV;
    private final int blockSize;
    private int byteCount;
    private final BlockCipher cipher;
    private byte[] counter;
    private byte[] counterOut;

    public SICBlockCipher(BlockCipher c) {
        super(c);
        this.cipher = c;
        int blockSize = c.getBlockSize();
        this.blockSize = blockSize;
        this.f762IV = new byte[blockSize];
        this.counter = new byte[blockSize];
        this.counterOut = new byte[blockSize];
        this.byteCount = 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] clone = Arrays.clone(ivParam.getIV());
            this.f762IV = clone;
            int i = this.blockSize;
            if (i < clone.length) {
                throw new IllegalArgumentException("CTR/SIC mode requires IV no greater than: " + this.blockSize + " bytes.");
            }
            int maxCounterSize = 8 > i / 2 ? i / 2 : 8;
            if (i - clone.length > maxCounterSize) {
                throw new IllegalArgumentException("CTR/SIC mode requires IV of at least: " + (this.blockSize - maxCounterSize) + " bytes.");
            }
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
            }
            reset();
            return;
        }
        throw new IllegalArgumentException("CTR/SIC mode requires ParametersWithIV");
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/SIC";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.StreamBlockCipher
    protected byte calculateByte(byte in) throws DataLengthException, IllegalStateException {
        int i = this.byteCount;
        if (i == 0) {
            this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
            byte[] bArr = this.counterOut;
            int i2 = this.byteCount;
            this.byteCount = i2 + 1;
            return (byte) (bArr[i2] ^ in);
        }
        byte[] bArr2 = this.counterOut;
        int i3 = i + 1;
        this.byteCount = i3;
        byte rv = (byte) (bArr2[i] ^ in);
        if (i3 == this.counter.length) {
            this.byteCount = 0;
            incrementCounterAt(0);
            checkCounter();
        }
        return rv;
    }

    private void checkCounter() {
        if (this.f762IV.length < this.blockSize) {
            int i = 0;
            while (true) {
                byte[] bArr = this.f762IV;
                if (i != bArr.length) {
                    if (this.counter[i] == bArr[i]) {
                        i++;
                    } else {
                        throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void incrementCounterAt(int pos) {
        byte b;
        int i = this.counter.length - pos;
        do {
            i--;
            if (i >= 0) {
                byte[] bArr = this.counter;
                b = (byte) (bArr[i] + 1);
                bArr[i] = b;
            } else {
                return;
            }
        } while (b == 0);
    }

    private void incrementCounter(int offSet) {
        byte[] bArr = this.counter;
        byte old = bArr[bArr.length - 1];
        int length = bArr.length - 1;
        bArr[length] = (byte) (bArr[length] + offSet);
        if (old != 0 && bArr[bArr.length - 1] < old) {
            incrementCounterAt(1);
        }
    }

    private void decrementCounterAt(int pos) {
        byte[] bArr;
        byte b;
        int i = this.counter.length - pos;
        do {
            i--;
            if (i >= 0) {
                b = (byte) (bArr[i] - 1);
                this.counter[i] = b;
            } else {
                return;
            }
        } while (b == -1);
    }

    private void adjustCounter(long n) {
        if (n >= 0) {
            long numBlocks = (this.byteCount + n) / this.blockSize;
            long rem = numBlocks;
            if (rem > 255) {
                for (int i = 5; i >= 1; i--) {
                    long diff = 1 << (i * 8);
                    while (rem >= diff) {
                        incrementCounterAt(i);
                        rem -= diff;
                    }
                }
            }
            int i2 = (int) rem;
            incrementCounter(i2);
            this.byteCount = (int) ((this.byteCount + n) - (this.blockSize * numBlocks));
            return;
        }
        long numBlocks2 = ((-n) - this.byteCount) / this.blockSize;
        long rem2 = numBlocks2;
        if (rem2 > 255) {
            for (int i3 = 5; i3 >= 1; i3--) {
                long diff2 = 1 << (i3 * 8);
                while (rem2 > diff2) {
                    decrementCounterAt(i3);
                    rem2 -= diff2;
                }
            }
        }
        for (long i4 = 0; i4 != rem2; i4++) {
            decrementCounterAt(0);
        }
        int gap = (int) (this.byteCount + n + (this.blockSize * numBlocks2));
        if (gap >= 0) {
            this.byteCount = 0;
            return;
        }
        decrementCounterAt(0);
        this.byteCount = this.blockSize + gap;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void reset() {
        Arrays.fill(this.counter, (byte) 0);
        byte[] bArr = this.f762IV;
        System.arraycopy(bArr, 0, this.counter, 0, bArr.length);
        this.cipher.reset();
        this.byteCount = 0;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.SkippingCipher
    public long skip(long numberOfBytes) {
        adjustCounter(numberOfBytes);
        checkCounter();
        this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
        return numberOfBytes;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.SkippingCipher
    public long seekTo(long position) {
        reset();
        return skip(position);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.SkippingCipher
    public long getPosition() {
        int v;
        byte[] bArr = this.counter;
        byte[] res = new byte[bArr.length];
        System.arraycopy(bArr, 0, res, 0, res.length);
        for (int i = res.length - 1; i >= 1; i--) {
            byte[] bArr2 = this.f762IV;
            if (i < bArr2.length) {
                v = (res[i] & 255) - (bArr2[i] & 255);
            } else {
                v = res[i] & 255;
            }
            if (v < 0) {
                int i2 = i - 1;
                res[i2] = (byte) (res[i2] - 1);
                v += 256;
            }
            res[i] = (byte) v;
        }
        int i3 = res.length;
        return (Pack.bigEndianToLong(res, i3 - 8) * this.blockSize) + this.byteCount;
    }
}
