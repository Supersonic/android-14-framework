package android.drm;

import android.p008os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import libcore.io.IoBridge;
import libcore.io.Streams;
@Deprecated
/* loaded from: classes.dex */
public class DrmOutputStream extends OutputStream {
    private static final String TAG = "DrmOutputStream";
    private final DrmManagerClient mClient;
    private final FileDescriptor mFd;
    private final ParcelFileDescriptor mPfd;
    private int mSessionId;

    public DrmOutputStream(DrmManagerClient client, ParcelFileDescriptor pfd, String mimeType) throws IOException {
        this.mSessionId = -1;
        this.mClient = client;
        this.mPfd = pfd;
        this.mFd = pfd.getFileDescriptor();
        int openConvertSession = client.openConvertSession(mimeType);
        this.mSessionId = openConvertSession;
        if (openConvertSession == -1) {
            throw new UnknownServiceException("Failed to open DRM session for " + mimeType);
        }
    }

    public void finish() throws IOException {
        DrmConvertedStatus status = this.mClient.closeConvertSession(this.mSessionId);
        if (status.statusCode == 1) {
            try {
                Os.lseek(this.mFd, status.offset, OsConstants.SEEK_SET);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            IoBridge.write(this.mFd, status.convertedData, 0, status.convertedData.length);
            this.mSessionId = -1;
            return;
        }
        throw new IOException("Unexpected DRM status: " + status.statusCode);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.mSessionId == -1) {
            Log.m104w(TAG, "Closing stream without finishing");
        }
        this.mPfd.close();
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        byte[] exactBuffer;
        ArrayUtils.throwsIfOutOfBounds(buffer.length, offset, count);
        if (count == buffer.length) {
            exactBuffer = buffer;
        } else {
            exactBuffer = new byte[count];
            System.arraycopy(buffer, offset, exactBuffer, 0, count);
        }
        DrmConvertedStatus status = this.mClient.convertData(this.mSessionId, exactBuffer);
        if (status.statusCode == 1) {
            IoBridge.write(this.mFd, status.convertedData, 0, status.convertedData.length);
            return;
        }
        throw new IOException("Unexpected DRM status: " + status.statusCode);
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        Streams.writeSingleByte(this, oneByte);
    }
}
