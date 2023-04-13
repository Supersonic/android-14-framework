package android.p008os;

import android.media.MediaMetrics;
import android.util.Log;
import android.util.Printer;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
/* renamed from: android.os.Looper */
/* loaded from: classes3.dex */
public final class Looper {
    private static final String TAG = "Looper";
    private static Looper sMainLooper;
    private static Observer sObserver;
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private boolean mInLoop;
    private Printer mLogging;
    final MessageQueue mQueue;
    private boolean mSlowDeliveryDetected;
    private long mSlowDeliveryThresholdMs;
    private long mSlowDispatchThresholdMs;
    final Thread mThread = Thread.currentThread();
    private long mTraceTag;

    /* renamed from: android.os.Looper$Observer */
    /* loaded from: classes3.dex */
    public interface Observer {
        void dispatchingThrewException(Object obj, Message message, Exception exc);

        Object messageDispatchStarting();

        void messageDispatched(Object obj, Message message);
    }

    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean quitAllowed) {
        ThreadLocal<Looper> threadLocal = sThreadLocal;
        if (threadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        threadLocal.set(new Looper(quitAllowed));
    }

    @Deprecated
    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }

    public static Looper getMainLooper() {
        Looper looper;
        synchronized (Looper.class) {
            looper = sMainLooper;
        }
        return looper;
    }

    public static void setObserver(Observer observer) {
        sObserver = observer;
    }

    /* JADX WARN: Removed duplicated region for block: B:97:0x01f5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static boolean loopOnce(Looper me, long ident, int thresholdOverride) {
        long slowDispatchThresholdMs;
        long slowDeliveryThresholdMs;
        Object token;
        long traceTag;
        Object token2;
        Observer observer;
        Message msg;
        String str;
        boolean z;
        String str2;
        Message msg2;
        Printer logging;
        Message msg3 = me.mQueue.next();
        if (msg3 == null) {
            return false;
        }
        Printer logging2 = me.mLogging;
        if (logging2 != null) {
            logging2.println(">>>>> Dispatching to " + msg3.target + " " + msg3.callback + ": " + msg3.what);
        }
        Observer observer2 = sObserver;
        long traceTag2 = me.mTraceTag;
        long slowDispatchThresholdMs2 = me.mSlowDispatchThresholdMs;
        long slowDeliveryThresholdMs2 = me.mSlowDeliveryThresholdMs;
        boolean hasOverride = thresholdOverride >= 0;
        if (hasOverride) {
            long slowDispatchThresholdMs3 = thresholdOverride;
            long slowDeliveryThresholdMs3 = thresholdOverride;
            slowDispatchThresholdMs = slowDispatchThresholdMs3;
            slowDeliveryThresholdMs = slowDeliveryThresholdMs3;
        } else {
            slowDispatchThresholdMs = slowDispatchThresholdMs2;
            slowDeliveryThresholdMs = slowDeliveryThresholdMs2;
        }
        boolean logSlowDelivery = (slowDeliveryThresholdMs > 0 || hasOverride) && msg3.when > 0;
        boolean logSlowDispatch = slowDispatchThresholdMs > 0 || hasOverride;
        boolean needStartTime = logSlowDelivery || logSlowDispatch;
        if (traceTag2 != 0 && Trace.isTagEnabled(traceTag2)) {
            Trace.traceBegin(traceTag2, msg3.target.getTraceName(msg3));
        }
        long dispatchStart = needStartTime ? SystemClock.uptimeMillis() : 0L;
        if (observer2 != null) {
            Object token3 = observer2.messageDispatchStarting();
            token = token3;
        } else {
            token = null;
        }
        long origWorkSource = ThreadLocalWorkSource.setUid(msg3.workSourceUid);
        try {
            msg3.target.dispatchMessage(msg3);
            if (observer2 != null) {
                try {
                    observer2.messageDispatched(token, msg3);
                } catch (Exception e) {
                    exception = e;
                    token2 = token;
                    traceTag = traceTag2;
                    observer = observer2;
                    msg = msg3;
                    if (observer != null) {
                        try {
                            observer.dispatchingThrewException(token2, msg, exception);
                        } catch (Throwable th) {
                            exception = th;
                            ThreadLocalWorkSource.restore(origWorkSource);
                            if (traceTag != 0) {
                                Trace.traceEnd(traceTag);
                            }
                            throw exception;
                        }
                    }
                    throw exception;
                } catch (Throwable th2) {
                    exception = th2;
                    traceTag = traceTag2;
                    ThreadLocalWorkSource.restore(origWorkSource);
                    if (traceTag != 0) {
                    }
                    throw exception;
                }
            }
            long dispatchEnd = logSlowDispatch ? SystemClock.uptimeMillis() : 0L;
            ThreadLocalWorkSource.restore(origWorkSource);
            if (traceTag2 != 0) {
                Trace.traceEnd(traceTag2);
            }
            if (!logSlowDelivery) {
                str = TAG;
                z = true;
            } else if (!me.mSlowDeliveryDetected) {
                long j = msg3.when;
                str = TAG;
                z = true;
                if (showSlowLog(slowDeliveryThresholdMs, j, dispatchStart, "delivery", msg3)) {
                    me.mSlowDeliveryDetected = true;
                }
            } else if (dispatchStart - msg3.when <= 10) {
                Slog.m90w(TAG, "Drained");
                me.mSlowDeliveryDetected = false;
                str = TAG;
                z = true;
            } else {
                str = TAG;
                z = true;
            }
            if (logSlowDispatch) {
                str2 = " ";
                msg2 = msg3;
                logging = logging2;
                showSlowLog(slowDispatchThresholdMs, dispatchStart, dispatchEnd, "dispatch", msg2);
            } else {
                str2 = " ";
                msg2 = msg3;
                logging = logging2;
            }
            if (logging != null) {
                logging.println("<<<<< Finished to " + msg2.target + str2 + msg2.callback);
            }
            long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(str, "Thread identity changed from 0x" + Long.toHexString(ident) + " to 0x" + Long.toHexString(newIdent) + " while dispatching to " + msg2.target.getClass().getName() + str2 + msg2.callback + " what=" + msg2.what);
            }
            msg2.recycleUnchecked();
            return z;
        } catch (Exception e2) {
            exception = e2;
            token2 = token;
            traceTag = traceTag2;
            observer = observer2;
            msg = msg3;
        } catch (Throwable th3) {
            exception = th3;
            traceTag = traceTag2;
        }
    }

    public static void loop() {
        Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        if (me.mInLoop) {
            Slog.m90w(TAG, "Loop again would have the queued messages be executed before this one completed.");
        }
        me.mInLoop = true;
        Binder.clearCallingIdentity();
        long ident = Binder.clearCallingIdentity();
        int thresholdOverride = SystemProperties.getInt("log.looper." + Process.myUid() + MediaMetrics.SEPARATOR + Thread.currentThread().getName() + ".slow", -1);
        me.mSlowDeliveryDetected = false;
        do {
        } while (loopOnce(me, ident, thresholdOverride));
    }

    private static boolean showSlowLog(long threshold, long measureStart, long measureEnd, String what, Message msg) {
        long actualTime = measureEnd - measureStart;
        if (actualTime < threshold) {
            return false;
        }
        Slog.m90w(TAG, "Slow " + what + " took " + actualTime + "ms " + Thread.currentThread().getName() + " h=" + msg.target.getClass().getName() + " c=" + msg.callback + " m=" + msg.what);
        return true;
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        return myLooper().mQueue;
    }

    private Looper(boolean quitAllowed) {
        this.mQueue = new MessageQueue(quitAllowed);
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == this.mThread;
    }

    public void setMessageLogging(Printer printer) {
        this.mLogging = printer;
    }

    public void setTraceTag(long traceTag) {
        this.mTraceTag = traceTag;
    }

    public void setSlowLogThresholdMs(long slowDispatchThresholdMs, long slowDeliveryThresholdMs) {
        this.mSlowDispatchThresholdMs = slowDispatchThresholdMs;
        this.mSlowDeliveryThresholdMs = slowDeliveryThresholdMs;
    }

    public void quit() {
        this.mQueue.quit(false);
    }

    public void quitSafely() {
        this.mQueue.quit(true);
    }

    public Thread getThread() {
        return this.mThread;
    }

    public MessageQueue getQueue() {
        return this.mQueue;
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + toString());
        this.mQueue.dump(pw, prefix + "  ", null);
    }

    public void dump(Printer pw, String prefix, Handler handler) {
        pw.println(prefix + toString());
        this.mQueue.dump(pw, prefix + "  ", handler);
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long looperToken = proto.start(fieldId);
        proto.write(1138166333441L, this.mThread.getName());
        proto.write(1112396529666L, this.mThread.getId());
        MessageQueue messageQueue = this.mQueue;
        if (messageQueue != null) {
            messageQueue.dumpDebug(proto, 1146756268035L);
        }
        proto.end(looperToken);
    }

    public String toString() {
        return "Looper (" + this.mThread.getName() + ", tid " + this.mThread.getId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
