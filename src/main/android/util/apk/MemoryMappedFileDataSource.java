package android.util.apk;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.DirectByteBuffer;
import java.security.DigestException;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class MemoryMappedFileDataSource implements DataSource {
    private static final long MEMORY_PAGE_SIZE_BYTES = Os.sysconf(OsConstants._SC_PAGESIZE);
    private final FileDescriptor mFd;
    private final long mFilePosition;
    private final long mSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MemoryMappedFileDataSource(FileDescriptor fd, long position, long size) {
        this.mFd = fd;
        this.mFilePosition = position;
        this.mSize = size;
    }

    @Override // android.util.apk.DataSource
    public long size() {
        return this.mSize;
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x00c5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // android.util.apk.DataSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void feedIntoDataDigester(DataDigester md, long offset, int size) throws IOException, DigestException {
        long mmapRegionSize;
        ErrnoException errnoException;
        long mmapPtr;
        long filePosition = this.mFilePosition + offset;
        long j = MEMORY_PAGE_SIZE_BYTES;
        long mmapFilePosition = (filePosition / j) * j;
        int dataStartOffsetInMmapRegion = (int) (filePosition - mmapFilePosition);
        long mmapRegionSize2 = size + dataStartOffsetInMmapRegion;
        long mmapPtr2 = 0;
        try {
            try {
                long mmapPtr3 = Os.mmap(0L, mmapRegionSize2, OsConstants.PROT_READ, OsConstants.MAP_SHARED | OsConstants.MAP_POPULATE, this.mFd, mmapFilePosition);
                try {
                    mmapPtr = mmapPtr3;
                    try {
                        try {
                            md.consume(new DirectByteBuffer(size, mmapPtr3 + dataStartOffsetInMmapRegion, this.mFd, (Runnable) null, true));
                            if (mmapPtr != 0) {
                                try {
                                    Os.munmap(mmapPtr, mmapRegionSize2);
                                } catch (ErrnoException e) {
                                }
                            }
                        } catch (ErrnoException e2) {
                            e = e2;
                            mmapRegionSize = mmapRegionSize2;
                            mmapPtr2 = mmapPtr;
                            try {
                                throw new IOException("Failed to mmap " + mmapRegionSize + " bytes", e);
                            } catch (Throwable e3) {
                                errnoException = e3;
                                mmapPtr = mmapPtr2;
                                if (mmapPtr != 0) {
                                    try {
                                        Os.munmap(mmapPtr, mmapRegionSize);
                                    } catch (ErrnoException e4) {
                                    }
                                }
                                throw errnoException;
                            }
                        } catch (Throwable th) {
                            th = th;
                            mmapRegionSize = mmapRegionSize2;
                            errnoException = th;
                            if (mmapPtr != 0) {
                            }
                            throw errnoException;
                        }
                    } catch (ErrnoException e5) {
                        e = e5;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (ErrnoException e6) {
                    e = e6;
                    mmapRegionSize = mmapRegionSize2;
                    mmapPtr2 = mmapPtr3;
                } catch (Throwable th3) {
                    mmapPtr = mmapPtr3;
                    mmapRegionSize = mmapRegionSize2;
                    errnoException = th3;
                }
            } catch (ErrnoException e7) {
                e = e7;
                mmapRegionSize = mmapRegionSize2;
            } catch (Throwable th4) {
                mmapRegionSize = mmapRegionSize2;
                errnoException = th4;
                mmapPtr = 0;
            }
        } catch (ErrnoException e8) {
            e = e8;
            mmapRegionSize = mmapRegionSize2;
        } catch (Throwable th5) {
            mmapRegionSize = mmapRegionSize2;
            errnoException = th5;
            mmapPtr = 0;
        }
    }
}
