package android.p008os;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import java.io.FileDescriptor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
/* renamed from: android.os.MessageQueue */
/* loaded from: classes3.dex */
public final class MessageQueue {
    private static final boolean DEBUG = false;
    private static final String TAG = "MessageQueue";
    private boolean mBlocked;
    private SparseArray<FileDescriptorRecord> mFileDescriptorRecords;
    Message mMessages;
    private int mNextBarrierToken;
    private IdleHandler[] mPendingIdleHandlers;
    private final boolean mQuitAllowed;
    private boolean mQuitting;
    private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList<>();
    private long mPtr = nativeInit();

    /* renamed from: android.os.MessageQueue$IdleHandler */
    /* loaded from: classes3.dex */
    public interface IdleHandler {
        boolean queueIdle();
    }

    /* renamed from: android.os.MessageQueue$OnFileDescriptorEventListener */
    /* loaded from: classes3.dex */
    public interface OnFileDescriptorEventListener {
        public static final int EVENT_ERROR = 4;
        public static final int EVENT_INPUT = 1;
        public static final int EVENT_OUTPUT = 2;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.MessageQueue$OnFileDescriptorEventListener$Events */
        /* loaded from: classes3.dex */
        public @interface Events {
        }

        int onFileDescriptorEvents(FileDescriptor fileDescriptor, int i);
    }

    private static native void nativeDestroy(long j);

    private static native long nativeInit();

    private static native boolean nativeIsPolling(long j);

    private native void nativePollOnce(long j, int i);

    private static native void nativeSetFileDescriptorEvents(long j, int i, int i2);

    private static native void nativeWake(long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public MessageQueue(boolean quitAllowed) {
        this.mQuitAllowed = quitAllowed;
    }

    protected void finalize() throws Throwable {
        try {
            dispose();
        } finally {
            super.finalize();
        }
    }

    private void dispose() {
        long j = this.mPtr;
        if (j != 0) {
            nativeDestroy(j);
            this.mPtr = 0L;
        }
    }

    public boolean isIdle() {
        boolean z;
        synchronized (this) {
            long now = SystemClock.uptimeMillis();
            Message message = this.mMessages;
            z = message == null || now < message.when;
        }
        return z;
    }

    public void addIdleHandler(IdleHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Can't add a null IdleHandler");
        }
        synchronized (this) {
            this.mIdleHandlers.add(handler);
        }
    }

    public void removeIdleHandler(IdleHandler handler) {
        synchronized (this) {
            this.mIdleHandlers.remove(handler);
        }
    }

    public boolean isPolling() {
        boolean isPollingLocked;
        synchronized (this) {
            isPollingLocked = isPollingLocked();
        }
        return isPollingLocked;
    }

    private boolean isPollingLocked() {
        return !this.mQuitting && nativeIsPolling(this.mPtr);
    }

    public void addOnFileDescriptorEventListener(FileDescriptor fd, int events, OnFileDescriptorEventListener listener) {
        if (fd == null) {
            throw new IllegalArgumentException("fd must not be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this) {
            updateOnFileDescriptorEventListenerLocked(fd, events, listener);
        }
    }

    public void removeOnFileDescriptorEventListener(FileDescriptor fd) {
        if (fd == null) {
            throw new IllegalArgumentException("fd must not be null");
        }
        synchronized (this) {
            updateOnFileDescriptorEventListenerLocked(fd, 0, null);
        }
    }

    private void updateOnFileDescriptorEventListenerLocked(FileDescriptor fd, int events, OnFileDescriptorEventListener listener) {
        int fdNum = fd.getInt$();
        int index = -1;
        FileDescriptorRecord record = null;
        SparseArray<FileDescriptorRecord> sparseArray = this.mFileDescriptorRecords;
        if (sparseArray != null && (index = sparseArray.indexOfKey(fdNum)) >= 0 && (record = this.mFileDescriptorRecords.valueAt(index)) != null && record.mEvents == events) {
            return;
        }
        if (events != 0) {
            int events2 = events | 4;
            if (record == null) {
                if (this.mFileDescriptorRecords == null) {
                    this.mFileDescriptorRecords = new SparseArray<>();
                }
                this.mFileDescriptorRecords.put(fdNum, new FileDescriptorRecord(fd, events2, listener));
            } else {
                record.mListener = listener;
                record.mEvents = events2;
                record.mSeq++;
            }
            nativeSetFileDescriptorEvents(this.mPtr, fdNum, events2);
        } else if (record != null) {
            record.mEvents = 0;
            this.mFileDescriptorRecords.removeAt(index);
            nativeSetFileDescriptorEvents(this.mPtr, fdNum, 0);
        }
    }

    private int dispatchEvents(int fd, int events) {
        synchronized (this) {
            FileDescriptorRecord record = this.mFileDescriptorRecords.get(fd);
            if (record == null) {
                return 0;
            }
            int oldWatchedEvents = record.mEvents;
            int events2 = events & oldWatchedEvents;
            if (events2 == 0) {
                return oldWatchedEvents;
            }
            OnFileDescriptorEventListener listener = record.mListener;
            int seq = record.mSeq;
            int newWatchedEvents = listener.onFileDescriptorEvents(record.mDescriptor, events2);
            if (newWatchedEvents != 0) {
                newWatchedEvents |= 4;
            }
            if (newWatchedEvents != oldWatchedEvents) {
                synchronized (this) {
                    int index = this.mFileDescriptorRecords.indexOfKey(fd);
                    if (index >= 0 && this.mFileDescriptorRecords.valueAt(index) == record && record.mSeq == seq) {
                        record.mEvents = newWatchedEvents;
                        if (newWatchedEvents == 0) {
                            this.mFileDescriptorRecords.removeAt(index);
                        }
                    }
                }
            }
            return newWatchedEvents;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0095, code lost:
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0096, code lost:
        if (r5 >= r2) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0098, code lost:
        r6 = r13.mPendingIdleHandlers;
        r7 = r6[r5];
        r6[r5] = null;
        r6 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00a3, code lost:
        r6 = r7.queueIdle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00a5, code lost:
        r8 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a6, code lost:
        android.util.Log.wtf(android.p008os.MessageQueue.TAG, "IdleHandler threw exception", r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x00bd, code lost:
        r2 = 0;
        r4 = 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Message next() {
        IdleHandler idler;
        boolean keep;
        Message message;
        long ptr = this.mPtr;
        if (ptr == 0) {
            return null;
        }
        int pendingIdleHandlerCount = -1;
        int nextPollTimeoutMillis = 0;
        while (true) {
            if (nextPollTimeoutMillis != 0) {
                Binder.flushPendingCommands();
            }
            nativePollOnce(ptr, nextPollTimeoutMillis);
            synchronized (this) {
                long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = this.mMessages;
                if (msg != null && msg.target == null) {
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                        if (msg == null) {
                            break;
                        }
                    } while (!msg.isAsynchronous());
                }
                if (msg != null) {
                    if (now < msg.when) {
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, 2147483647L);
                    } else {
                        this.mBlocked = false;
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            this.mMessages = msg.next;
                        }
                        msg.next = null;
                        msg.markInUse();
                        return msg;
                    }
                } else {
                    nextPollTimeoutMillis = -1;
                }
                if (this.mQuitting) {
                    dispose();
                    return null;
                }
                if (pendingIdleHandlerCount < 0 && ((message = this.mMessages) == null || now < message.when)) {
                    pendingIdleHandlerCount = this.mIdleHandlers.size();
                }
                if (pendingIdleHandlerCount <= 0) {
                    this.mBlocked = true;
                } else {
                    if (this.mPendingIdleHandlers == null) {
                        this.mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
                    }
                    this.mPendingIdleHandlers = (IdleHandler[]) this.mIdleHandlers.toArray(this.mPendingIdleHandlers);
                }
            }
        }
        if (!keep) {
            synchronized (this) {
                this.mIdleHandlers.remove(idler);
            }
        }
        int i = i + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void quit(boolean safe) {
        if (!this.mQuitAllowed) {
            throw new IllegalStateException("Main thread not allowed to quit.");
        }
        synchronized (this) {
            if (this.mQuitting) {
                return;
            }
            this.mQuitting = true;
            if (safe) {
                removeAllFutureMessagesLocked();
            } else {
                removeAllMessagesLocked();
            }
            nativeWake(this.mPtr);
        }
    }

    public int postSyncBarrier() {
        return postSyncBarrier(SystemClock.uptimeMillis());
    }

    private int postSyncBarrier(long when) {
        int token;
        synchronized (this) {
            token = this.mNextBarrierToken;
            this.mNextBarrierToken = token + 1;
            Message msg = Message.obtain();
            msg.markInUse();
            msg.when = when;
            msg.arg1 = token;
            Message prev = null;
            Message p = this.mMessages;
            if (when != 0) {
                while (p != null && p.when <= when) {
                    prev = p;
                    p = p.next;
                }
            }
            if (prev != null) {
                msg.next = p;
                prev.next = msg;
            } else {
                msg.next = p;
                this.mMessages = msg;
            }
        }
        return token;
    }

    public void removeSyncBarrier(int token) {
        boolean needWake;
        synchronized (this) {
            Message prev = null;
            Message p = this.mMessages;
            while (p != null && (p.target != null || p.arg1 != token)) {
                prev = p;
                p = p.next;
            }
            if (p == null) {
                throw new IllegalStateException("The specified message queue synchronization  barrier token has not been posted or has already been removed.");
            }
            if (prev != null) {
                prev.next = p.next;
                needWake = false;
            } else {
                Message message = p.next;
                this.mMessages = message;
                if (message != null && message.target == null) {
                    needWake = false;
                }
                needWake = true;
            }
            p.recycleUnchecked();
            if (needWake && !this.mQuitting) {
                nativeWake(this.mPtr);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0082 A[Catch: all -> 0x00a2, TryCatch #0 {, blocks: (B:5:0x0005, B:7:0x000b, B:9:0x0010, B:10:0x0036, B:12:0x0038, B:16:0x0048, B:19:0x004f, B:21:0x0053, B:23:0x0057, B:26:0x005e, B:28:0x0064, B:32:0x006d, B:35:0x0075, B:38:0x0082, B:39:0x0087, B:36:0x007a, B:41:0x0089, B:42:0x00a1), top: B:48:0x0005 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean enqueueMessage(Message msg, long when) {
        Message prev;
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        synchronized (this) {
            if (msg.isInUse()) {
                throw new IllegalStateException(msg + " This message is already in use.");
            }
            boolean needWake = false;
            if (this.mQuitting) {
                IllegalStateException e = new IllegalStateException(msg.target + " sending message to a Handler on a dead thread");
                Log.m103w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }
            msg.markInUse();
            msg.when = when;
            Message p = this.mMessages;
            if (p != null && when != 0 && when >= p.when) {
                if (this.mBlocked && p.target == null && msg.isAsynchronous()) {
                    needWake = true;
                }
                while (true) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    } else if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p;
                prev.next = msg;
                if (needWake) {
                    nativeWake(this.mPtr);
                }
                return true;
            }
            msg.next = p;
            this.mMessages = msg;
            needWake = this.mBlocked;
            if (needWake) {
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMessages(Handler h, int what, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h && p.what == what && (object == null || p.obj == object)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasEqualMessages(Handler h, int what, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h && p.what == what && (object == null || object.equals(p.obj))) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMessages(Handler h, Runnable r, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h && p.callback == r && (object == null || p.obj == object)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasMessages(Handler h) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            for (Message p = this.mMessages; p != null; p = p.next) {
                if (p.target == h) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMessages(Handler h, int what, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.what == what && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.what == what && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeEqualMessages(Handler h, int what, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.what == what && (object == null || object.equals(p.obj))) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.what == what && (object == null || object.equals(n2.obj))) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMessages(Handler h, Runnable r, Object object) {
        if (h == null || r == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.callback == r && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.callback == r && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    void removeEqualMessages(Handler h, Runnable r, Object object) {
        if (h == null || r == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && p.callback == r && (object == null || object.equals(p.obj))) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && n2.callback == r && (object == null || object.equals(n2.obj))) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCallbacksAndMessages(Handler h, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && (object == null || p.obj == object)) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && (object == null || n2.obj == object)) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeCallbacksAndEqualMessages(Handler h, Object object) {
        if (h == null) {
            return;
        }
        synchronized (this) {
            Message p = this.mMessages;
            while (p != null && p.target == h && (object == null || object.equals(p.obj))) {
                Message n = p.next;
                this.mMessages = n;
                p.recycleUnchecked();
                p = n;
            }
            while (p != null) {
                Message n2 = p.next;
                if (n2 != null && n2.target == h && (object == null || object.equals(n2.obj))) {
                    Message nn = n2.next;
                    n2.recycleUnchecked();
                    p.next = nn;
                } else {
                    p = n2;
                }
            }
        }
    }

    private void removeAllMessagesLocked() {
        Message p = this.mMessages;
        while (p != null) {
            Message n = p.next;
            p.recycleUnchecked();
            p = n;
        }
        this.mMessages = null;
    }

    private void removeAllFutureMessagesLocked() {
        long now = SystemClock.uptimeMillis();
        Message p = this.mMessages;
        if (p != null) {
            if (p.when > now) {
                removeAllMessagesLocked();
                return;
            }
            while (true) {
                Message n = p.next;
                if (n == null) {
                    return;
                }
                if (n.when <= now) {
                    p = n;
                } else {
                    p.next = null;
                    do {
                        Message p2 = n;
                        n = p2.next;
                        p2.recycleUnchecked();
                    } while (n != null);
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(Printer pw, String prefix, Handler h) {
        synchronized (this) {
            long now = SystemClock.uptimeMillis();
            int n = 0;
            for (Message msg = this.mMessages; msg != null; msg = msg.next) {
                if (h == null || h == msg.target) {
                    pw.println(prefix + "Message " + n + ": " + msg.toString(now));
                }
                n++;
            }
            pw.println(prefix + "(Total messages: " + n + ", polling=" + isPollingLocked() + ", quitting=" + this.mQuitting + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long messageQueueToken = proto.start(fieldId);
        synchronized (this) {
            for (Message msg = this.mMessages; msg != null; msg = msg.next) {
                msg.dumpDebug(proto, 2246267895809L);
            }
            proto.write(1133871366146L, isPollingLocked());
            proto.write(1133871366147L, this.mQuitting);
        }
        proto.end(messageQueueToken);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.MessageQueue$FileDescriptorRecord */
    /* loaded from: classes3.dex */
    public static final class FileDescriptorRecord {
        public final FileDescriptor mDescriptor;
        public int mEvents;
        public OnFileDescriptorEventListener mListener;
        public int mSeq;

        public FileDescriptorRecord(FileDescriptor descriptor, int events, OnFileDescriptorEventListener listener) {
            this.mDescriptor = descriptor;
            this.mEvents = events;
            this.mListener = listener;
        }
    }
}
