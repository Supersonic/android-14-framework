package android.net;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructLinger;
import android.system.StructTimeval;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class LocalSocketImpl {

    /* renamed from: fd */
    private FileDescriptor f299fd;
    private SocketInputStream fis;
    private SocketOutputStream fos;
    FileDescriptor[] inboundFileDescriptors;
    private boolean mFdCreatedInternally;
    FileDescriptor[] outboundFileDescriptors;
    private Object readMonitor = new Object();
    private Object writeMonitor = new Object();

    private native void bindLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native void connectLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native Credentials getPeerCredentials_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int read_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int readba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void write_native(int i, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void writeba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class SocketInputStream extends InputStream {
        SocketInputStream() {
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            FileDescriptor myFd = LocalSocketImpl.this.f299fd;
            if (myFd == null) {
                throw new IOException("socket closed");
            }
            try {
                return Os.ioctlInt(myFd, OsConstants.FIONREAD);
            } catch (ErrnoException e) {
                throw e.rethrowAsIOException();
            }
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ret;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.f299fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                ret = LocalSocketImpl.this.read_native(myFd);
            }
            return ret;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int ret;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.f299fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                if (off < 0 || len < 0 || off + len > b.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                ret = LocalSocketImpl.this.readba_native(b, off, len, myFd);
            }
            return ret;
        }
    }

    /* loaded from: classes2.dex */
    class SocketOutputStream extends OutputStream {
        SocketOutputStream() {
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.f299fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                if (off < 0 || len < 0 || off + len > b.length) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                LocalSocketImpl.this.writeba_native(b, off, len, myFd);
            }
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.f299fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                LocalSocketImpl.this.write_native(b, myFd);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalSocketImpl() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalSocketImpl(FileDescriptor fd) {
        this.f299fd = fd;
    }

    public String toString() {
        return super.toString() + " fd:" + this.f299fd;
    }

    public void create(int sockType) throws IOException {
        int osType;
        if (this.f299fd != null) {
            throw new IOException("LocalSocketImpl already has an fd");
        }
        switch (sockType) {
            case 1:
                osType = OsConstants.SOCK_DGRAM;
                break;
            case 2:
                osType = OsConstants.SOCK_STREAM;
                break;
            case 3:
                osType = OsConstants.SOCK_SEQPACKET;
                break;
            default:
                throw new IllegalStateException("unknown sockType");
        }
        try {
            this.f299fd = Os.socket(OsConstants.AF_UNIX, osType, 0);
            this.mFdCreatedInternally = true;
        } catch (ErrnoException e) {
            e.rethrowAsIOException();
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            FileDescriptor fileDescriptor = this.f299fd;
            if (fileDescriptor == null || !this.mFdCreatedInternally) {
                this.f299fd = null;
                return;
            }
            try {
                Os.close(fileDescriptor);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            this.f299fd = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void connect(LocalSocketAddress address, int timeout) throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        connectLocal(fileDescriptor, address.getName(), address.getNamespace().getId());
    }

    public void bind(LocalSocketAddress endpoint) throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        bindLocal(fileDescriptor, endpoint.getName(), endpoint.getNamespace().getId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void listen(int backlog) throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.listen(fileDescriptor, backlog);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void accept(LocalSocketImpl s) throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        try {
            s.f299fd = Os.accept(fileDescriptor, null);
            s.mFdCreatedInternally = true;
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InputStream getInputStream() throws IOException {
        SocketInputStream socketInputStream;
        if (this.f299fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fis == null) {
                this.fis = new SocketInputStream();
            }
            socketInputStream = this.fis;
        }
        return socketInputStream;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public OutputStream getOutputStream() throws IOException {
        SocketOutputStream socketOutputStream;
        if (this.f299fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fos == null) {
                this.fos = new SocketOutputStream();
            }
            socketOutputStream = this.fos;
        }
        return socketOutputStream;
    }

    protected int available() throws IOException {
        return getInputStream().available();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shutdownInput() throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.shutdown(fileDescriptor, OsConstants.SHUT_RD);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shutdownOutput() throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        try {
            Os.shutdown(fileDescriptor, OsConstants.SHUT_WR);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileDescriptor getFileDescriptor() {
        return this.f299fd;
    }

    protected boolean supportsUrgentData() {
        return false;
    }

    protected void sendUrgentData(int data) throws IOException {
        throw new RuntimeException("not impled");
    }

    public Object getOption(int optID) throws IOException {
        FileDescriptor fileDescriptor = this.f299fd;
        if (fileDescriptor == null) {
            throw new IOException("socket not created");
        }
        try {
            switch (optID) {
                case 1:
                    Object toReturn = Integer.valueOf(Os.getsockoptInt(fileDescriptor, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY));
                    return toReturn;
                case 4:
                case 4097:
                case 4098:
                    int osOpt = javaSoToOsOpt(optID);
                    Object toReturn2 = Integer.valueOf(Os.getsockoptInt(this.f299fd, OsConstants.SOL_SOCKET, osOpt));
                    return toReturn2;
                case 128:
                    StructLinger linger = Os.getsockoptLinger(fileDescriptor, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER);
                    if (!linger.isOn()) {
                        return -1;
                    }
                    Object toReturn3 = Integer.valueOf(linger.l_linger);
                    return toReturn3;
                case 4102:
                    StructTimeval timeval = Os.getsockoptTimeval(fileDescriptor, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO);
                    Object toReturn4 = Integer.valueOf((int) timeval.toMillis());
                    return toReturn4;
                default:
                    throw new IOException("Unknown option: " + optID);
            }
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public void setOption(int optID, Object value) throws IOException {
        if (this.f299fd == null) {
            throw new IOException("socket not created");
        }
        int boolValue = -1;
        int intValue = 0;
        if (value instanceof Integer) {
            intValue = ((Integer) value).intValue();
        } else if (value instanceof Boolean) {
            boolValue = ((Boolean) value).booleanValue();
        } else {
            throw new IOException("bad value: " + value);
        }
        try {
            switch (optID) {
                case 1:
                    Os.setsockoptInt(this.f299fd, OsConstants.IPPROTO_TCP, OsConstants.TCP_NODELAY, intValue);
                    return;
                case 4:
                case 4097:
                case 4098:
                    int osOpt = javaSoToOsOpt(optID);
                    Os.setsockoptInt(this.f299fd, OsConstants.SOL_SOCKET, osOpt, intValue);
                    return;
                case 128:
                    StructLinger linger = new StructLinger(boolValue, intValue);
                    Os.setsockoptLinger(this.f299fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER, linger);
                    return;
                case 4102:
                    StructTimeval timeval = StructTimeval.fromMillis(intValue);
                    Os.setsockoptTimeval(this.f299fd, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, timeval);
                    Os.setsockoptTimeval(this.f299fd, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, timeval);
                    return;
                default:
                    throw new IOException("Unknown option: " + optID);
            }
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public void setFileDescriptorsForSend(FileDescriptor[] fds) {
        synchronized (this.writeMonitor) {
            this.outboundFileDescriptors = fds;
        }
    }

    public FileDescriptor[] getAncillaryFileDescriptors() throws IOException {
        FileDescriptor[] result;
        synchronized (this.readMonitor) {
            result = this.inboundFileDescriptors;
            this.inboundFileDescriptors = null;
        }
        return result;
    }

    public Credentials getPeerCredentials() throws IOException {
        return getPeerCredentials_native(this.f299fd);
    }

    public LocalSocketAddress getSockAddress() throws IOException {
        return null;
    }

    protected void finalize() throws IOException {
        close();
    }

    private static int javaSoToOsOpt(int optID) {
        switch (optID) {
            case 4:
                return OsConstants.SO_REUSEADDR;
            case 4097:
                return OsConstants.SO_SNDBUF;
            case 4098:
                return OsConstants.SO_RCVBUF;
            default:
                throw new UnsupportedOperationException("Unknown option: " + optID);
        }
    }
}
