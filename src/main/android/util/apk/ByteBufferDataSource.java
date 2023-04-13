package android.util.apk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.DigestException;
/* loaded from: classes3.dex */
class ByteBufferDataSource implements DataSource {
    private final ByteBuffer mBuf;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBufferDataSource(ByteBuffer buf) {
        this.mBuf = buf.slice();
    }

    @Override // android.util.apk.DataSource
    public long size() {
        return this.mBuf.capacity();
    }

    @Override // android.util.apk.DataSource
    public void feedIntoDataDigester(DataDigester md, long offset, int size) throws IOException, DigestException {
        ByteBuffer region;
        synchronized (this.mBuf) {
            this.mBuf.position(0);
            this.mBuf.limit(((int) offset) + size);
            this.mBuf.position((int) offset);
            region = this.mBuf.slice();
        }
        md.consume(region);
    }
}
