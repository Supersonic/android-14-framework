package android.util.apk;

import android.p008os.incremental.IncrementalManager;
import java.io.FileDescriptor;
import java.io.IOException;
import java.security.DigestException;
/* loaded from: classes3.dex */
interface DataSource {
    void feedIntoDataDigester(DataDigester dataDigester, long j, int i) throws IOException, DigestException;

    long size();

    static DataSource create(FileDescriptor fd, long pos, long size) {
        if (IncrementalManager.isIncrementalFileFd(fd)) {
            return new ReadFileDataSource(fd, pos, size);
        }
        return new MemoryMappedFileDataSource(fd, pos, size);
    }
}
