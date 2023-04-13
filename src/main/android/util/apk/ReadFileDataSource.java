package android.util.apk;

import android.system.ErrnoException;
import android.system.Os;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.DigestException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class ReadFileDataSource implements DataSource {
    private static final int CHUNK_SIZE = 1048576;
    private final FileDescriptor mFd;
    private final long mFilePosition;
    private final long mSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReadFileDataSource(FileDescriptor fd, long position, long size) {
        this.mFd = fd;
        this.mFilePosition = position;
        this.mSize = size;
    }

    @Override // android.util.apk.DataSource
    public long size() {
        return this.mSize;
    }

    @Override // android.util.apk.DataSource
    public void feedIntoDataDigester(DataDigester md, long offset, int size) throws IOException, DigestException {
        try {
            byte[] buffer = new byte[Math.min(size, 1048576)];
            long start = this.mFilePosition + offset;
            long end = start + size;
            long min = Math.min(size, 1048576);
            long pos = start;
            while (true) {
                long curSize = min;
                if (pos < end) {
                    int i = (int) curSize;
                    long curSize2 = pos;
                    int readSize = Os.pread(this.mFd, buffer, 0, i, curSize2);
                    try {
                        md.consume(ByteBuffer.wrap(buffer, 0, readSize));
                        pos += readSize;
                        min = Math.min(end - pos, 1048576L);
                    } catch (ErrnoException e) {
                        e = e;
                        throw new IOException(e);
                    }
                } else {
                    return;
                }
            }
        } catch (ErrnoException e2) {
            e = e2;
        }
    }
}
