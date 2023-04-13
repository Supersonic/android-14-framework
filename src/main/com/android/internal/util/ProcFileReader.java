package com.android.internal.util;

import com.android.internal.midi.MidiConstants;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
/* loaded from: classes3.dex */
public class ProcFileReader implements Closeable {
    private final byte[] mBuffer;
    private boolean mLineFinished;
    private final InputStream mStream;
    private int mTail;

    public ProcFileReader(InputStream stream) throws IOException {
        this(stream, 4096);
    }

    public ProcFileReader(InputStream stream, int bufferSize) throws IOException {
        this.mStream = stream;
        this.mBuffer = new byte[bufferSize];
        if (stream.markSupported()) {
            stream.mark(0);
        }
        fillBuf();
    }

    private int fillBuf() throws IOException {
        byte[] bArr = this.mBuffer;
        int length = bArr.length;
        int i = this.mTail;
        int length2 = length - i;
        if (length2 == 0) {
            throw new IOException("attempting to fill already-full buffer");
        }
        int read = this.mStream.read(bArr, i, length2);
        if (read != -1) {
            this.mTail += read;
        }
        return read;
    }

    private void consumeBuf(int count) throws IOException {
        int i;
        while (true) {
            i = this.mTail;
            if (count >= i || this.mBuffer[count] != 32) {
                break;
            }
            count++;
        }
        byte[] bArr = this.mBuffer;
        System.arraycopy(bArr, count, bArr, 0, i - count);
        int i2 = this.mTail - count;
        this.mTail = i2;
        if (i2 == 0) {
            fillBuf();
        }
    }

    private int nextTokenIndex() throws IOException {
        if (this.mLineFinished) {
            return -1;
        }
        int i = 0;
        while (true) {
            if (i < this.mTail) {
                byte b = this.mBuffer[i];
                if (b == 10) {
                    this.mLineFinished = true;
                    return i;
                } else if (b != 32) {
                    i++;
                } else {
                    return i;
                }
            } else if (fillBuf() <= 0) {
                throw new ProtocolException("End of stream while looking for token boundary");
            }
        }
    }

    public boolean hasMoreData() {
        return this.mTail > 0;
    }

    public void finishLine() throws IOException {
        if (this.mLineFinished) {
            this.mLineFinished = false;
            return;
        }
        int i = 0;
        while (true) {
            if (i < this.mTail) {
                if (this.mBuffer[i] != 10) {
                    i++;
                } else {
                    consumeBuf(i + 1);
                    return;
                }
            } else if (fillBuf() <= 0) {
                throw new ProtocolException("End of stream while looking for line boundary");
            }
        }
    }

    public String nextString() throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex == -1) {
            throw new ProtocolException("Missing required string");
        }
        return parseAndConsumeString(tokenIndex);
    }

    public long nextLong() throws IOException {
        return nextLong(false);
    }

    public long nextLong(boolean stopAtInvalid) throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex == -1) {
            throw new ProtocolException("Missing required long");
        }
        return parseAndConsumeLong(tokenIndex, stopAtInvalid);
    }

    public long nextOptionalLong(long def) throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex == -1) {
            return def;
        }
        return parseAndConsumeLong(tokenIndex, false);
    }

    private String parseAndConsumeString(int tokenIndex) throws IOException {
        String s = new String(this.mBuffer, 0, tokenIndex, StandardCharsets.US_ASCII);
        consumeBuf(tokenIndex + 1);
        return s;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0042  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private long parseAndConsumeLong(int tokenIndex, boolean stopAtInvalid) throws IOException {
        boolean negative = this.mBuffer[0] == 45;
        long result = 0;
        for (int i = negative ? 1 : 0; i < tokenIndex; i++) {
            int digit = this.mBuffer[i] + MidiConstants.STATUS_CHANNEL_PRESSURE;
            if (digit < 0 || digit > 9) {
                if (!stopAtInvalid) {
                    throw invalidLong(tokenIndex);
                }
                int i2 = tokenIndex + 1;
                consumeBuf(i2);
                return !negative ? result : -result;
            }
            long next = (10 * result) - digit;
            if (next > result) {
                throw invalidLong(tokenIndex);
            }
            result = next;
        }
        int i22 = tokenIndex + 1;
        consumeBuf(i22);
        if (!negative) {
        }
    }

    private NumberFormatException invalidLong(int tokenIndex) {
        return new NumberFormatException("invalid long: " + new String(this.mBuffer, 0, tokenIndex, StandardCharsets.US_ASCII));
    }

    public int nextInt() throws IOException {
        long value = nextLong();
        if (value > 2147483647L || value < -2147483648L) {
            throw new NumberFormatException("parsed value larger than integer");
        }
        return (int) value;
    }

    public void nextIgnored() throws IOException {
        int tokenIndex = nextTokenIndex();
        if (tokenIndex == -1) {
            throw new ProtocolException("Missing required token");
        }
        consumeBuf(tokenIndex + 1);
    }

    public void rewind() throws IOException {
        InputStream inputStream = this.mStream;
        if (inputStream instanceof FileInputStream) {
            ((FileInputStream) inputStream).getChannel().position(0L);
        } else if (inputStream.markSupported()) {
            this.mStream.reset();
        } else {
            throw new IOException("The InputStream is NOT markable");
        }
        this.mTail = 0;
        this.mLineFinished = false;
        fillBuf();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.mStream.close();
    }
}
