package com.android.internal.p028os;

import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ProxyFileDescriptorCallback;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
/* renamed from: com.android.internal.os.FuseAppLoop */
/* loaded from: classes4.dex */
public class FuseAppLoop implements Handler.Callback {
    private static final int ARGS_POOL_SIZE = 50;
    private static final int FUSE_FSYNC = 20;
    private static final int FUSE_GETATTR = 3;
    private static final int FUSE_LOOKUP = 1;
    private static final int FUSE_MAX_WRITE = 131072;
    private static final int FUSE_OK = 0;
    private static final int FUSE_OPEN = 14;
    private static final int FUSE_READ = 15;
    private static final int FUSE_RELEASE = 18;
    private static final int FUSE_WRITE = 16;
    private static final int MIN_INODE = 2;
    public static final int ROOT_INODE = 1;
    private long mInstance;
    private final int mMountPointId;
    private final Thread mThread;
    private static final String TAG = "FuseAppLoop";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final ThreadFactory sDefaultThreadFactory = new ThreadFactory() { // from class: com.android.internal.os.FuseAppLoop.1
        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            return new Thread(r, FuseAppLoop.TAG);
        }
    };
    private final Object mLock = new Object();
    private final SparseArray<CallbackEntry> mCallbackMap = new SparseArray<>();
    private final BytesMap mBytesMap = new BytesMap();
    private final LinkedList<Args> mArgsPool = new LinkedList<>();
    private int mNextInode = 2;

    /* renamed from: com.android.internal.os.FuseAppLoop$UnmountedException */
    /* loaded from: classes4.dex */
    public static class UnmountedException extends Exception {
    }

    native void native_delete(long j);

    native long native_new(int i);

    native void native_replyGetAttr(long j, long j2, long j3, long j4);

    native void native_replyLookup(long j, long j2, long j3, long j4);

    native void native_replyOpen(long j, long j2, long j3);

    native void native_replyRead(long j, long j2, int i, byte[] bArr);

    native void native_replySimple(long j, long j2, int i);

    native void native_replyWrite(long j, long j2, int i);

    native void native_start(long j);

    public FuseAppLoop(int mountPointId, ParcelFileDescriptor fd, ThreadFactory factory) {
        this.mMountPointId = mountPointId;
        factory = factory == null ? sDefaultThreadFactory : factory;
        this.mInstance = native_new(fd.detachFd());
        Thread newThread = factory.newThread(new Runnable() { // from class: com.android.internal.os.FuseAppLoop$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FuseAppLoop.this.lambda$new$0();
            }
        });
        this.mThread = newThread;
        newThread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        native_start(this.mInstance);
        synchronized (this.mLock) {
            native_delete(this.mInstance);
            this.mInstance = 0L;
            this.mBytesMap.clear();
        }
    }

    public int registerCallback(ProxyFileDescriptorCallback callback, Handler handler) throws FuseUnavailableMountException {
        int id;
        synchronized (this.mLock) {
            Objects.requireNonNull(callback);
            Objects.requireNonNull(handler);
            Preconditions.checkState(this.mCallbackMap.size() < 2147483645, "Too many opened files.");
            Preconditions.checkArgument(Thread.currentThread().getId() != handler.getLooper().getThread().getId(), "Handler must be different from the current thread");
            if (this.mInstance == 0) {
                throw new FuseUnavailableMountException(this.mMountPointId);
            }
            do {
                id = this.mNextInode;
                int i = id + 1;
                this.mNextInode = i;
                if (i < 0) {
                    this.mNextInode = 2;
                }
            } while (this.mCallbackMap.get(id) != null);
            this.mCallbackMap.put(id, new CallbackEntry(callback, new Handler(handler.getLooper(), this)));
        }
        return id;
    }

    public void unregisterCallback(int id) {
        synchronized (this.mLock) {
            this.mCallbackMap.remove(id);
        }
    }

    public int getMountPointId() {
        return this.mMountPointId;
    }

    /* JADX WARN: Not initialized variable reg: 19, insn: 0x00ba: MOVE  (r14 I:??[long, double] A[D('inode' long)]) = (r19 I:??[long, double] A[D('offset' long)]), block:B:45:0x00b1 */
    /* JADX WARN: Removed duplicated region for block: B:185:0x0225 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // android.p008os.Handler.Callback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean handleMessage(Message msg) {
        long unique;
        Object obj;
        long offset;
        byte[] data;
        long unique2;
        Object obj2;
        long fileSize;
        Object obj3;
        Object obj4;
        Object obj5;
        Object obj6;
        Args args = (Args) msg.obj;
        CallbackEntry entry = args.entry;
        long inode = args.inode;
        long unique3 = args.unique;
        int size = args.size;
        long offset2 = args.offset;
        byte[] data2 = args.data;
        try {
            try {
                try {
                    switch (msg.what) {
                        case 1:
                            data = data2;
                            unique2 = unique3;
                            long fileSize2 = entry.callback.onGetSize();
                            Object obj7 = this.mLock;
                            try {
                                synchronized (obj7) {
                                    try {
                                        long j = this.mInstance;
                                        if (j != 0) {
                                            obj2 = obj7;
                                            native_replyLookup(j, unique2, inode, fileSize2);
                                        } else {
                                            obj2 = obj7;
                                        }
                                        recycleLocked(args);
                                        return true;
                                    } catch (Throwable th) {
                                        th = th;
                                        throw th;
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                            throw th;
                        case 3:
                            data = data2;
                            try {
                                fileSize = entry.callback.onGetSize();
                            } catch (Exception e) {
                                e = e;
                                unique = unique3;
                            }
                            try {
                                Object obj8 = this.mLock;
                                try {
                                    synchronized (obj8) {
                                        try {
                                            long j2 = this.mInstance;
                                            if (j2 != 0) {
                                                obj3 = obj8;
                                                unique2 = unique3;
                                                native_replyGetAttr(j2, unique3, inode, fileSize);
                                            } else {
                                                obj3 = obj8;
                                                unique2 = unique3;
                                            }
                                            recycleLocked(args);
                                            return true;
                                        } catch (Throwable th3) {
                                            th = th3;
                                            throw th;
                                        }
                                    }
                                } catch (Throwable th4) {
                                    th = th4;
                                }
                                throw th;
                            } catch (Exception e2) {
                                e = e2;
                                unique = unique3;
                                Exception error = e;
                                synchronized (this.mLock) {
                                }
                            }
                            break;
                        case 15:
                            data = data2;
                            try {
                                int readSize = entry.callback.onRead(offset2, size, data);
                                Object obj9 = this.mLock;
                                try {
                                    synchronized (obj9) {
                                        try {
                                            long j3 = this.mInstance;
                                            if (j3 != 0) {
                                                obj4 = obj9;
                                                native_replyRead(j3, unique3, readSize, data);
                                            } else {
                                                obj4 = obj9;
                                            }
                                            recycleLocked(args);
                                            unique2 = unique3;
                                            return true;
                                        } catch (Throwable th5) {
                                            th = th5;
                                            throw th;
                                        }
                                    }
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                                try {
                                    throw th;
                                } catch (Exception e3) {
                                    e = e3;
                                    unique = unique3;
                                    Exception error2 = e;
                                    synchronized (this.mLock) {
                                        try {
                                            try {
                                                Log.m109e(TAG, "", error2);
                                                replySimpleLocked(unique, getError(error2));
                                                recycleLocked(args);
                                                return true;
                                            } catch (Throwable th7) {
                                                th = th7;
                                                throw th;
                                            }
                                        } catch (Throwable th8) {
                                            th = th8;
                                        }
                                    }
                                }
                            } catch (Exception e4) {
                                e = e4;
                                unique = unique3;
                            }
                        case 16:
                            data = data2;
                            try {
                                try {
                                    int writeSize = entry.callback.onWrite(offset2, size, data);
                                    Object obj10 = this.mLock;
                                    try {
                                        synchronized (obj10) {
                                            try {
                                                long j4 = this.mInstance;
                                                if (j4 != 0) {
                                                    obj5 = obj10;
                                                    native_replyWrite(j4, unique3, writeSize);
                                                } else {
                                                    obj5 = obj10;
                                                }
                                                recycleLocked(args);
                                                unique2 = unique3;
                                                return true;
                                            } catch (Throwable th9) {
                                                th = th9;
                                                throw th;
                                            }
                                        }
                                    } catch (Throwable th10) {
                                        th = th10;
                                    }
                                } catch (Exception e5) {
                                    e = e5;
                                    unique = unique3;
                                }
                            } catch (Exception e6) {
                                e = e6;
                                unique = unique3;
                            }
                            try {
                                throw th;
                            } catch (Exception e7) {
                                e = e7;
                                unique = unique3;
                                Exception error22 = e;
                                synchronized (this.mLock) {
                                }
                            }
                            break;
                        case 18:
                            data = data2;
                            entry.callback.onRelease();
                            Object obj11 = this.mLock;
                            try {
                                synchronized (obj11) {
                                    try {
                                        long j5 = this.mInstance;
                                        if (j5 != 0) {
                                            obj6 = obj11;
                                            native_replySimple(j5, unique3, 0);
                                        } else {
                                            obj6 = obj11;
                                        }
                                        this.mBytesMap.stopUsing(inode);
                                        recycleLocked(args);
                                        unique2 = unique3;
                                        return true;
                                    } catch (Throwable th11) {
                                        th = th11;
                                        throw th;
                                    }
                                }
                            } catch (Throwable th12) {
                                th = th12;
                            }
                            throw th;
                        case 20:
                            try {
                                entry.callback.onFsync();
                                Object obj12 = this.mLock;
                                try {
                                    synchronized (obj12) {
                                        try {
                                            long j6 = this.mInstance;
                                            if (j6 != 0) {
                                                obj = obj12;
                                                offset = offset2;
                                                data = data2;
                                                native_replySimple(j6, unique3, 0);
                                            } else {
                                                obj = obj12;
                                                offset = offset2;
                                                data = data2;
                                            }
                                            recycleLocked(args);
                                            unique2 = unique3;
                                            return true;
                                        } catch (Throwable th13) {
                                            th = th13;
                                            throw th;
                                        }
                                    }
                                } catch (Throwable th14) {
                                    th = th14;
                                }
                                throw th;
                            } catch (Exception e8) {
                                e = e8;
                                unique = unique3;
                                Exception error222 = e;
                                synchronized (this.mLock) {
                                }
                            }
                            break;
                        default:
                            unique = unique3;
                            try {
                                try {
                                    throw new IllegalArgumentException("Unknown FUSE command: " + msg.what);
                                } catch (Exception e9) {
                                    e = e9;
                                    Exception error2222 = e;
                                    synchronized (this.mLock) {
                                    }
                                }
                            } catch (Exception e10) {
                                e = e10;
                            }
                            break;
                    }
                } catch (Exception e11) {
                    e = e11;
                    unique = unique3;
                }
            } catch (Exception e12) {
                e = e12;
            }
        } catch (Exception e13) {
            e = e13;
            unique = unique3;
        }
    }

    private void onCommand(int command, long unique, long inode, long offset, int size, byte[] data) {
        Args args;
        synchronized (this.mLock) {
            try {
                if (this.mArgsPool.size() == 0) {
                    args = new Args();
                } else {
                    args = this.mArgsPool.pop();
                }
                args.unique = unique;
                args.inode = inode;
                args.offset = offset;
                args.size = size;
                args.data = data;
                args.entry = getCallbackEntryOrThrowLocked(inode);
            } catch (Exception error) {
                replySimpleLocked(unique, getError(error));
            }
            if (!args.entry.handler.sendMessage(Message.obtain(args.entry.handler, command, 0, 0, args))) {
                throw new ErrnoException("onCommand", OsConstants.EBADF);
            }
        }
    }

    private byte[] onOpen(long unique, long inode) {
        CallbackEntry entry;
        synchronized (this.mLock) {
            try {
                entry = getCallbackEntryOrThrowLocked(inode);
            } catch (ErrnoException error) {
                replySimpleLocked(unique, getError(error));
            }
            if (entry.opened) {
                throw new ErrnoException("onOpen", OsConstants.EMFILE);
            }
            long j = this.mInstance;
            if (j != 0) {
                native_replyOpen(j, unique, inode);
                entry.opened = true;
                return this.mBytesMap.startUsing(inode);
            }
            return null;
        }
    }

    private static int getError(Exception error) {
        int errno;
        if ((error instanceof ErrnoException) && (errno = ((ErrnoException) error).errno) != OsConstants.ENOSYS) {
            return -errno;
        }
        return -OsConstants.EBADF;
    }

    private CallbackEntry getCallbackEntryOrThrowLocked(long inode) throws ErrnoException {
        CallbackEntry entry = this.mCallbackMap.get(checkInode(inode));
        if (entry == null) {
            throw new ErrnoException("getCallbackEntryOrThrowLocked", OsConstants.ENOENT);
        }
        return entry;
    }

    private void recycleLocked(Args args) {
        if (this.mArgsPool.size() < 50) {
            this.mArgsPool.add(args);
        }
    }

    private void replySimpleLocked(long unique, int result) {
        long j = this.mInstance;
        if (j != 0) {
            native_replySimple(j, unique, result);
        }
    }

    private static int checkInode(long inode) {
        Preconditions.checkArgumentInRange(inode, 2L, 2147483647L, "checkInode");
        return (int) inode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.FuseAppLoop$CallbackEntry */
    /* loaded from: classes4.dex */
    public static class CallbackEntry {
        final ProxyFileDescriptorCallback callback;
        final Handler handler;
        boolean opened;

        CallbackEntry(ProxyFileDescriptorCallback callback, Handler handler) {
            this.callback = (ProxyFileDescriptorCallback) Objects.requireNonNull(callback);
            this.handler = (Handler) Objects.requireNonNull(handler);
        }

        long getThreadId() {
            return this.handler.getLooper().getThread().getId();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.FuseAppLoop$BytesMapEntry */
    /* loaded from: classes4.dex */
    public static class BytesMapEntry {
        byte[] bytes;
        int counter;

        private BytesMapEntry() {
            this.counter = 0;
            this.bytes = new byte[131072];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.FuseAppLoop$BytesMap */
    /* loaded from: classes4.dex */
    public static class BytesMap {
        final Map<Long, BytesMapEntry> mEntries;

        private BytesMap() {
            this.mEntries = new HashMap();
        }

        byte[] startUsing(long inode) {
            BytesMapEntry entry = this.mEntries.get(Long.valueOf(inode));
            if (entry == null) {
                entry = new BytesMapEntry();
                this.mEntries.put(Long.valueOf(inode), entry);
            }
            entry.counter++;
            return entry.bytes;
        }

        void stopUsing(long inode) {
            BytesMapEntry entry = this.mEntries.get(Long.valueOf(inode));
            Objects.requireNonNull(entry);
            entry.counter--;
            if (entry.counter <= 0) {
                this.mEntries.remove(Long.valueOf(inode));
            }
        }

        void clear() {
            this.mEntries.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.os.FuseAppLoop$Args */
    /* loaded from: classes4.dex */
    public static class Args {
        byte[] data;
        CallbackEntry entry;
        long inode;
        long offset;
        int size;
        long unique;

        private Args() {
        }
    }
}
