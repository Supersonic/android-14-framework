package android.media;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes2.dex */
public final class ResampleInputStream extends InputStream {
    private static final String TAG = "ResampleInputStream";
    private static final int mFirLength = 29;
    private byte[] mBuf;
    private int mBufCount;
    private InputStream mInputStream;
    private final byte[] mOneByte = new byte[1];
    private final int mRateIn;
    private final int mRateOut;

    private static native void fir21(byte[] bArr, int i, byte[] bArr2, int i2, int i3);

    static {
        System.loadLibrary("media_jni");
    }

    public ResampleInputStream(InputStream inputStream, int rateIn, int rateOut) {
        if (rateIn != rateOut * 2) {
            throw new IllegalArgumentException("only support 2:1 at the moment");
        }
        this.mInputStream = inputStream;
        this.mRateIn = 2;
        this.mRateOut = 1;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int rtn = read(this.mOneByte, 0, 1);
        if (rtn == 1) {
            return this.mOneByte[0] & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int offset, int length) throws IOException {
        if (this.mInputStream == null) {
            throw new IllegalStateException("not open");
        }
        int nIn = ((((length / 2) * this.mRateIn) / this.mRateOut) + 29) * 2;
        byte[] bArr = this.mBuf;
        if (bArr == null) {
            this.mBuf = new byte[nIn];
        } else if (nIn > bArr.length) {
            byte[] bf = new byte[nIn];
            System.arraycopy(bArr, 0, bf, 0, this.mBufCount);
            this.mBuf = bf;
        }
        while (true) {
            int i = this.mBufCount;
            int len = ((((i / 2) - 29) * this.mRateOut) / this.mRateIn) * 2;
            if (len > 0) {
                int length2 = len < length ? len : (length / 2) * 2;
                fir21(this.mBuf, 0, b, offset, length2 / 2);
                int nFwd = (this.mRateIn * length2) / this.mRateOut;
                int i2 = this.mBufCount - nFwd;
                this.mBufCount = i2;
                if (i2 > 0) {
                    byte[] bArr2 = this.mBuf;
                    System.arraycopy(bArr2, nFwd, bArr2, 0, i2);
                }
                return length2;
            }
            InputStream inputStream = this.mInputStream;
            byte[] bArr3 = this.mBuf;
            int n = inputStream.read(bArr3, i, bArr3.length - i);
            if (n == -1) {
                return -1;
            }
            this.mBufCount += n;
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            InputStream inputStream = this.mInputStream;
            if (inputStream != null) {
                inputStream.close();
            }
        } finally {
            this.mInputStream = null;
        }
    }

    protected void finalize() throws Throwable {
        if (this.mInputStream != null) {
            close();
            throw new IllegalStateException("someone forgot to close ResampleInputStream");
        }
    }
}
