package com.android.server.p006am;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.MessageQueue;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import libcore.io.IoUtils;
/* renamed from: com.android.server.am.LmkdConnection */
/* loaded from: classes.dex */
public class LmkdConnection {
    public final ByteBuffer mInputBuf;
    public final DataInputStream mInputData;
    public final LmkdConnectionListener mListener;
    public final MessageQueue mMsgQueue;
    @GuardedBy({"mReplyBufLock"})
    public ByteBuffer mReplyBuf;
    public final Object mReplyBufLock;
    public final Object mLmkdSocketLock = new Object();
    @GuardedBy({"mLmkdSocketLock"})
    public LocalSocket mLmkdSocket = null;
    @GuardedBy({"mLmkdSocketLock"})
    public OutputStream mLmkdOutputStream = null;
    @GuardedBy({"mLmkdSocketLock"})
    public InputStream mLmkdInputStream = null;

    /* renamed from: com.android.server.am.LmkdConnection$LmkdConnectionListener */
    /* loaded from: classes.dex */
    public interface LmkdConnectionListener {
        boolean handleUnsolicitedMessage(DataInputStream dataInputStream, int i);

        boolean isReplyExpected(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i);

        boolean onConnect(OutputStream outputStream);

        void onDisconnect();
    }

    public LmkdConnection(MessageQueue messageQueue, LmkdConnectionListener lmkdConnectionListener) {
        ByteBuffer allocate = ByteBuffer.allocate(222);
        this.mInputBuf = allocate;
        this.mInputData = new DataInputStream(new ByteArrayInputStream(allocate.array()));
        this.mReplyBufLock = new Object();
        this.mReplyBuf = null;
        this.mMsgQueue = messageQueue;
        this.mListener = lmkdConnectionListener;
    }

    public boolean connect() {
        synchronized (this.mLmkdSocketLock) {
            if (this.mLmkdSocket != null) {
                return true;
            }
            LocalSocket openSocket = openSocket();
            if (openSocket == null) {
                Slog.w("ActivityManager", "Failed to connect to lowmemorykiller, retry later");
                return false;
            }
            try {
                OutputStream outputStream = openSocket.getOutputStream();
                InputStream inputStream = openSocket.getInputStream();
                LmkdConnectionListener lmkdConnectionListener = this.mListener;
                if (lmkdConnectionListener != null && !lmkdConnectionListener.onConnect(outputStream)) {
                    Slog.w("ActivityManager", "Failed to communicate with lowmemorykiller, retry later");
                    IoUtils.closeQuietly(openSocket);
                    return false;
                }
                this.mLmkdSocket = openSocket;
                this.mLmkdOutputStream = outputStream;
                this.mLmkdInputStream = inputStream;
                this.mMsgQueue.addOnFileDescriptorEventListener(openSocket.getFileDescriptor(), 5, new MessageQueue.OnFileDescriptorEventListener() { // from class: com.android.server.am.LmkdConnection.1
                    @Override // android.os.MessageQueue.OnFileDescriptorEventListener
                    public int onFileDescriptorEvents(FileDescriptor fileDescriptor, int i) {
                        return LmkdConnection.this.fileDescriptorEventHandler(fileDescriptor, i);
                    }
                });
                this.mLmkdSocketLock.notifyAll();
                return true;
            } catch (IOException unused) {
                IoUtils.closeQuietly(openSocket);
                return false;
            }
        }
    }

    public final int fileDescriptorEventHandler(FileDescriptor fileDescriptor, int i) {
        if (this.mListener == null) {
            return 0;
        }
        if ((i & 1) != 0) {
            processIncomingData();
        }
        if ((i & 4) != 0) {
            synchronized (this.mLmkdSocketLock) {
                this.mMsgQueue.removeOnFileDescriptorEventListener(this.mLmkdSocket.getFileDescriptor());
                IoUtils.closeQuietly(this.mLmkdSocket);
                this.mLmkdSocket = null;
            }
            synchronized (this.mReplyBufLock) {
                if (this.mReplyBuf != null) {
                    this.mReplyBuf = null;
                    this.mReplyBufLock.notifyAll();
                }
            }
            this.mListener.onDisconnect();
            return 0;
        }
        return 5;
    }

    public final void processIncomingData() {
        int read = read(this.mInputBuf);
        if (read > 0) {
            try {
                this.mInputData.reset();
                synchronized (this.mReplyBufLock) {
                    ByteBuffer byteBuffer = this.mReplyBuf;
                    if (byteBuffer != null) {
                        if (this.mListener.isReplyExpected(byteBuffer, this.mInputBuf, read)) {
                            this.mReplyBuf.put(this.mInputBuf.array(), 0, read);
                            this.mReplyBuf.rewind();
                            this.mReplyBufLock.notifyAll();
                        } else if (!this.mListener.handleUnsolicitedMessage(this.mInputData, read)) {
                            this.mReplyBuf = null;
                            this.mReplyBufLock.notifyAll();
                            Slog.e("ActivityManager", "Received an unexpected packet from lmkd");
                        }
                    } else if (!this.mListener.handleUnsolicitedMessage(this.mInputData, read)) {
                        Slog.w("ActivityManager", "Received an unexpected packet from lmkd");
                    }
                }
            } catch (IOException unused) {
                Slog.e("ActivityManager", "Failed to parse lmkd data buffer. Size = " + read);
            }
        }
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.mLmkdSocketLock) {
            z = this.mLmkdSocket != null;
        }
        return z;
    }

    public boolean waitForConnection(long j) {
        synchronized (this.mLmkdSocketLock) {
            if (this.mLmkdSocket != null) {
                return true;
            }
            try {
                this.mLmkdSocketLock.wait(j);
                return this.mLmkdSocket != null;
            } catch (InterruptedException unused) {
                return false;
            }
        }
    }

    public final LocalSocket openSocket() {
        try {
            LocalSocket localSocket = new LocalSocket(3);
            localSocket.connect(new LocalSocketAddress("lmkd", LocalSocketAddress.Namespace.RESERVED));
            return localSocket;
        } catch (IOException e) {
            Slog.e("ActivityManager", "Connection failed: " + e.toString());
            return null;
        }
    }

    public final boolean write(ByteBuffer byteBuffer) {
        synchronized (this.mLmkdSocketLock) {
            try {
                try {
                    this.mLmkdOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
                } catch (IOException unused) {
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return true;
    }

    public final int read(ByteBuffer byteBuffer) {
        int read;
        synchronized (this.mLmkdSocketLock) {
            try {
                try {
                    read = this.mLmkdInputStream.read(byteBuffer.array(), 0, byteBuffer.array().length);
                } catch (IOException unused) {
                    return -1;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return read;
    }

    public boolean exchange(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        boolean z;
        if (byteBuffer2 == null) {
            return write(byteBuffer);
        }
        synchronized (this.mReplyBufLock) {
            this.mReplyBuf = byteBuffer2;
            z = false;
            if (write(byteBuffer)) {
                try {
                    this.mReplyBufLock.wait();
                    if (this.mReplyBuf != null) {
                        z = true;
                    }
                } catch (InterruptedException unused) {
                }
            }
            this.mReplyBuf = null;
        }
        return z;
    }
}
