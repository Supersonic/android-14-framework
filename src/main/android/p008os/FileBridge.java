package android.p008os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Memory;
import libcore.io.Streams;
@Deprecated
/* renamed from: android.os.FileBridge */
/* loaded from: classes3.dex */
public class FileBridge extends Thread {
    private static final int CMD_CLOSE = 3;
    private static final int CMD_FSYNC = 2;
    private static final int CMD_WRITE = 1;
    private static final int MSG_LENGTH = 8;
    private static final String TAG = "FileBridge";
    private ParcelFileDescriptor mClient;
    private volatile boolean mClosed;
    private ParcelFileDescriptor mServer;
    private ParcelFileDescriptor mTarget;

    public FileBridge() {
        try {
            ParcelFileDescriptor[] fds = ParcelFileDescriptor.createSocketPair(OsConstants.SOCK_STREAM);
            this.mServer = fds[0];
            this.mClient = fds[1];
        } catch (IOException e) {
            throw new RuntimeException("Failed to create bridge");
        }
    }

    public boolean isClosed() {
        return this.mClosed;
    }

    public void forceClose() {
        IoUtils.closeQuietly(this.mTarget);
        IoUtils.closeQuietly(this.mServer);
        this.mClosed = true;
    }

    public void setTargetFile(ParcelFileDescriptor target) {
        this.mTarget = target;
    }

    public ParcelFileDescriptor getClientSocket() {
        return this.mClient;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(8192);
        byte[] temp = tempBuffer.hasArray() ? tempBuffer.array() : new byte[8192];
        while (true) {
            try {
                try {
                    if (IoBridge.read(this.mServer.getFileDescriptor(), temp, 0, 8) != 8) {
                        break;
                    }
                    int cmd = Memory.peekInt(temp, 0, ByteOrder.BIG_ENDIAN);
                    if (cmd == 1) {
                        int len = Memory.peekInt(temp, 4, ByteOrder.BIG_ENDIAN);
                        while (len > 0) {
                            int n = IoBridge.read(this.mServer.getFileDescriptor(), temp, 0, Math.min(temp.length, len));
                            if (n == -1) {
                                throw new IOException("Unexpected EOF; still expected " + len + " bytes");
                            }
                            IoBridge.write(this.mTarget.getFileDescriptor(), temp, 0, n);
                            len -= n;
                        }
                    } else if (cmd == 2) {
                        Os.fsync(this.mTarget.getFileDescriptor());
                        IoBridge.write(this.mServer.getFileDescriptor(), temp, 0, 8);
                    } else if (cmd == 3) {
                        Os.fsync(this.mTarget.getFileDescriptor());
                        this.mTarget.close();
                        this.mClosed = true;
                        IoBridge.write(this.mServer.getFileDescriptor(), temp, 0, 8);
                        break;
                    }
                } catch (ErrnoException | IOException e) {
                    Log.wtf(TAG, "Failed during bridge", e);
                }
            } finally {
                forceClose();
            }
        }
    }

    /* renamed from: android.os.FileBridge$FileBridgeOutputStream */
    /* loaded from: classes3.dex */
    public static class FileBridgeOutputStream extends OutputStream {
        private final FileDescriptor mClient;
        private final ParcelFileDescriptor mClientPfd;
        private final byte[] mTemp;
        private final ByteBuffer mTempBuffer;

        public FileBridgeOutputStream(ParcelFileDescriptor clientPfd) {
            byte[] bArr;
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8);
            this.mTempBuffer = allocateDirect;
            if (allocateDirect.hasArray()) {
                bArr = allocateDirect.array();
            } else {
                bArr = new byte[8];
            }
            this.mTemp = bArr;
            this.mClientPfd = clientPfd;
            this.mClient = clientPfd.getFileDescriptor();
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            try {
                writeCommandAndBlock(3, "close()");
            } finally {
                IoUtils.closeQuietly(this.mClientPfd);
            }
        }

        public void fsync() throws IOException {
            writeCommandAndBlock(2, "fsync()");
        }

        private void writeCommandAndBlock(int cmd, String cmdString) throws IOException {
            Memory.pokeInt(this.mTemp, 0, cmd, ByteOrder.BIG_ENDIAN);
            IoBridge.write(this.mClient, this.mTemp, 0, 8);
            if (IoBridge.read(this.mClient, this.mTemp, 0, 8) == 8 && Memory.peekInt(this.mTemp, 0, ByteOrder.BIG_ENDIAN) == cmd) {
                return;
            }
            throw new IOException("Failed to execute " + cmdString + " across bridge");
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            ArrayUtils.throwsIfOutOfBounds(buffer.length, byteOffset, byteCount);
            Memory.pokeInt(this.mTemp, 0, 1, ByteOrder.BIG_ENDIAN);
            Memory.pokeInt(this.mTemp, 4, byteCount, ByteOrder.BIG_ENDIAN);
            IoBridge.write(this.mClient, this.mTemp, 0, 8);
            IoBridge.write(this.mClient, buffer, byteOffset, byteCount);
        }

        @Override // java.io.OutputStream
        public void write(int oneByte) throws IOException {
            Streams.writeSingleByte(this, oneByte);
        }
    }
}
