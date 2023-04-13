package android.media;

import java.io.Closeable;
import java.io.IOException;
/* loaded from: classes2.dex */
public abstract class MediaDataSource implements Closeable {
    public abstract long getSize() throws IOException;

    public abstract int readAt(long j, byte[] bArr, int i, int i2) throws IOException;
}
