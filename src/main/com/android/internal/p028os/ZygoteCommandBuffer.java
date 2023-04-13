package com.android.internal.p028os;

import android.net.LocalSocket;
import java.io.FileDescriptor;
import java.lang.ref.Reference;
/* renamed from: com.android.internal.os.ZygoteCommandBuffer */
/* loaded from: classes4.dex */
class ZygoteCommandBuffer implements AutoCloseable {
    private long mNativeBuffer;
    private final int mNativeSocket;
    private final LocalSocket mSocket;

    private static native void freeNativeBuffer(long j);

    private static native long getNativeBuffer(int i);

    private static native void insert(long j, String str);

    private static native boolean nativeForkRepeatedly(long j, int i, int i2, int i3, String str);

    private static native int nativeGetCount(long j);

    private static native String nativeNextArg(long j);

    private static native void nativeReadFullyAndReset(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZygoteCommandBuffer(LocalSocket socket) {
        this.mSocket = socket;
        if (socket == null) {
            this.mNativeSocket = -1;
        } else {
            this.mNativeSocket = socket.getFileDescriptor().getInt$();
        }
        this.mNativeBuffer = getNativeBuffer(this.mNativeSocket);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZygoteCommandBuffer(String[] args) {
        this((LocalSocket) null);
        setCommand(args);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        freeNativeBuffer(this.mNativeBuffer);
        this.mNativeBuffer = 0L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCount() {
        try {
            return nativeGetCount(this.mNativeBuffer);
        } finally {
            Reference.reachabilityFence(this.mSocket);
        }
    }

    private void setCommand(String[] command) {
        int nArgs = command.length;
        insert(this.mNativeBuffer, Integer.toString(nArgs));
        for (String s : command) {
            insert(this.mNativeBuffer, s);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String nextArg() {
        try {
            return nativeNextArg(this.mNativeBuffer);
        } finally {
            Reference.reachabilityFence(this.mSocket);
        }
    }

    void readFullyAndReset() {
        try {
            nativeReadFullyAndReset(this.mNativeBuffer);
        } finally {
            Reference.reachabilityFence(this.mSocket);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean forkRepeatedly(FileDescriptor zygoteSocket, int expectedUid, int minUid, String firstNiceName) {
        try {
            return nativeForkRepeatedly(this.mNativeBuffer, zygoteSocket.getInt$(), expectedUid, minUid, firstNiceName);
        } finally {
            Reference.reachabilityFence(this.mSocket);
            Reference.reachabilityFence(zygoteSocket);
        }
    }
}
