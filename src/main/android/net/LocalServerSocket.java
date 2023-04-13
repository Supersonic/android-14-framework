package android.net;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
/* loaded from: classes2.dex */
public class LocalServerSocket implements Closeable {
    private static final int LISTEN_BACKLOG = 50;
    private final LocalSocketImpl impl;
    private final LocalSocketAddress localAddress;

    public LocalServerSocket(String name) throws IOException {
        LocalSocketImpl localSocketImpl = new LocalSocketImpl();
        this.impl = localSocketImpl;
        localSocketImpl.create(2);
        LocalSocketAddress localSocketAddress = new LocalSocketAddress(name);
        this.localAddress = localSocketAddress;
        localSocketImpl.bind(localSocketAddress);
        localSocketImpl.listen(50);
    }

    public LocalServerSocket(FileDescriptor fd) throws IOException {
        LocalSocketImpl localSocketImpl = new LocalSocketImpl(fd);
        this.impl = localSocketImpl;
        localSocketImpl.listen(50);
        this.localAddress = localSocketImpl.getSockAddress();
    }

    public LocalSocketAddress getLocalSocketAddress() {
        return this.localAddress;
    }

    public LocalSocket accept() throws IOException {
        LocalSocketImpl acceptedImpl = new LocalSocketImpl();
        this.impl.accept(acceptedImpl);
        return LocalSocket.createLocalSocketForAccept(acceptedImpl);
    }

    public FileDescriptor getFileDescriptor() {
        return this.impl.getFileDescriptor();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.impl.close();
    }
}
