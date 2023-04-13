package com.android.internal.util;

import android.util.CharsetUtils;
import com.android.modules.utils.FastDataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class ArtFastDataOutput extends FastDataOutput {
    private static AtomicReference<ArtFastDataOutput> sOutCache = new AtomicReference<>();
    private final long mBufferPtr;

    public ArtFastDataOutput(OutputStream out, int bufferSize) {
        super(out, bufferSize);
        this.mBufferPtr = this.mRuntime.addressOf(this.mBuffer);
    }

    public static ArtFastDataOutput obtain(OutputStream out) {
        ArtFastDataOutput instance = sOutCache.getAndSet(null);
        if (instance != null) {
            instance.setOutput(out);
            return instance;
        }
        return new ArtFastDataOutput(out, 32768);
    }

    @Override // com.android.modules.utils.FastDataOutput
    public void release() {
        super.release();
        if (this.mBufferCap == 32768) {
            sOutCache.compareAndSet(null, this);
        }
    }

    @Override // com.android.modules.utils.FastDataOutput, java.io.DataOutput
    public void writeUTF(String s) throws IOException {
        if (this.mBufferCap - this.mBufferPos < s.length() + 2) {
            drain();
        }
        int len = CharsetUtils.toModifiedUtf8Bytes(s, this.mBufferPtr, this.mBufferPos + 2, this.mBufferCap);
        if (Math.abs(len) > 65535) {
            throw new IOException("Modified UTF-8 length too large: " + len);
        }
        if (len >= 0) {
            writeShort(len);
            this.mBufferPos += len;
            return;
        }
        int len2 = -len;
        byte[] tmp = (byte[]) this.mRuntime.newNonMovableArray(Byte.TYPE, len2 + 1);
        CharsetUtils.toModifiedUtf8Bytes(s, this.mRuntime.addressOf(tmp), 0, tmp.length);
        writeShort(len2);
        write(tmp, 0, len2);
    }
}
