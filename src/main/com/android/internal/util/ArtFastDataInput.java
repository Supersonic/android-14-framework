package com.android.internal.util;

import android.util.CharsetUtils;
import com.android.modules.utils.FastDataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class ArtFastDataInput extends FastDataInput {
    private static AtomicReference<ArtFastDataInput> sInCache = new AtomicReference<>();
    private final long mBufferPtr;

    public ArtFastDataInput(InputStream in, int bufferSize) {
        super(in, bufferSize);
        this.mBufferPtr = this.mRuntime.addressOf(this.mBuffer);
    }

    public static ArtFastDataInput obtain(InputStream in) {
        ArtFastDataInput instance = sInCache.getAndSet(null);
        if (instance != null) {
            instance.setInput(in);
            return instance;
        }
        return new ArtFastDataInput(in, 32768);
    }

    @Override // com.android.modules.utils.FastDataInput
    public void release() {
        super.release();
        if (this.mBufferCap == 32768) {
            sInCache.compareAndSet(null, this);
        }
    }

    @Override // com.android.modules.utils.FastDataInput, java.io.DataInput
    public String readUTF() throws IOException {
        int len = readUnsignedShort();
        if (this.mBufferCap > len) {
            if (this.mBufferLim - this.mBufferPos < len) {
                fill(len);
            }
            String res = CharsetUtils.fromModifiedUtf8Bytes(this.mBufferPtr, this.mBufferPos, len);
            this.mBufferPos += len;
            return res;
        }
        byte[] tmp = (byte[]) this.mRuntime.newNonMovableArray(Byte.TYPE, len + 1);
        readFully(tmp, 0, len);
        return CharsetUtils.fromModifiedUtf8Bytes(this.mRuntime.addressOf(tmp), 0, len);
    }
}
