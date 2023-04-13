package com.android.server.integrity.model;

import android.p005os.IInstalld;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
/* loaded from: classes.dex */
public class BitOutputStream {
    public final byte[] mBuffer = new byte[IInstalld.FLAG_USE_QUOTA];
    public int mNextBitIndex = 0;
    public final OutputStream mOutputStream;

    public BitOutputStream(OutputStream outputStream) {
        this.mOutputStream = outputStream;
    }

    public void setNext(int i, int i2) throws IOException {
        if (i <= 0) {
            return;
        }
        int i3 = 1 << (i - 1);
        while (true) {
            int i4 = i - 1;
            if (i <= 0) {
                return;
            }
            setNext((i2 & i3) != 0);
            i3 >>>= 1;
            i = i4;
        }
    }

    public void setNext(boolean z) throws IOException {
        int i = this.mNextBitIndex / 8;
        if (i == 4096) {
            this.mOutputStream.write(this.mBuffer);
            reset();
            i = 0;
        }
        if (z) {
            byte[] bArr = this.mBuffer;
            bArr[i] = (byte) (bArr[i] | (1 << (7 - (this.mNextBitIndex % 8))));
        }
        this.mNextBitIndex++;
    }

    public void setNext() throws IOException {
        setNext(true);
    }

    public void flush() throws IOException {
        int i = this.mNextBitIndex;
        int i2 = i / 8;
        if (i % 8 != 0) {
            i2++;
        }
        this.mOutputStream.write(this.mBuffer, 0, i2);
        reset();
    }

    public final void reset() {
        this.mNextBitIndex = 0;
        Arrays.fill(this.mBuffer, (byte) 0);
    }
}
