package android.p008os;

import android.annotation.SystemApi;
import android.app.AppOpsManager;
import android.media.MediaMetrics;
import android.p008os.IBinder;
import android.util.ExceptionUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.p028os.BinderCallHeavyHitterWatcher;
import com.android.internal.p028os.BinderInternal;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FunctionalUtils;
import dalvik.annotation.optimization.CriticalNative;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;
import libcore.io.IoUtils;
import libcore.util.NativeAllocationRegistry;
/* renamed from: android.os.Binder */
/* loaded from: classes3.dex */
public class Binder implements IBinder {
    public static final boolean CHECK_PARCEL_SIZE = false;
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final int NATIVE_ALLOCATION_SIZE = 500;
    static final String TAG = "Binder";
    private static final int TRANSACTION_TRACE_NAME_ID_LIMIT = 1024;
    public static final int UNSET_WORKSOURCE = -1;
    private String mDescriptor;
    private final long mObject;
    private IInterface mOwner;
    private volatile String mSimpleDescriptor;
    private volatile AtomicReferenceArray<String> mTransactionTraceNames;
    public static boolean LOG_RUNTIME_EXCEPTION = false;
    private static volatile String sDumpDisabled = null;
    private static volatile TransactionTracker sTransactionTracker = null;
    private static BinderInternal.Observer sObserver = null;
    private static volatile BinderCallHeavyHitterWatcher sHeavyHitterWatcher = null;
    private static volatile boolean sStackTrackingEnabled = false;
    static volatile boolean sWarnOnBlocking = false;
    static ThreadLocal<Boolean> sWarnOnBlockingOnCurrentThread = ThreadLocal.withInitial(new Supplier() { // from class: android.os.Binder$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            Boolean valueOf;
            valueOf = Boolean.valueOf(Binder.sWarnOnBlocking);
            return valueOf;
        }
    });
    private static volatile BinderInternal.WorkSourceProvider sWorkSourceProvider = new BinderInternal.WorkSourceProvider() { // from class: android.os.Binder$$ExternalSyntheticLambda1
        @Override // com.android.internal.p028os.BinderInternal.WorkSourceProvider
        public final int resolveWorkSourceUid(int i) {
            int callingUid;
            callingUid = Binder.getCallingUid();
            return callingUid;
        }
    };

    public static final native void blockUntilThreadAvailable();

    @CriticalNative
    public static final native long clearCallingIdentity();

    @CriticalNative
    public static final native long clearCallingWorkSource();

    public static final native void flushPendingCommands();

    @CriticalNative
    public static final native int getCallingPid();

    @CriticalNative
    public static final native int getCallingUid();

    @CriticalNative
    public static final native int getCallingWorkSourceUid();

    private static native long getNativeBBinderHolder();

    /* JADX INFO: Access modifiers changed from: private */
    public static native long getNativeFinalizer();

    @CriticalNative
    public static final native int getThreadStrictModePolicy();

    @CriticalNative
    private static native boolean hasExplicitIdentity();

    @CriticalNative
    public static final native boolean isDirectlyHandlingTransaction();

    @CriticalNative
    public static final native void restoreCallingIdentity(long j);

    @CriticalNative
    public static final native void restoreCallingWorkSource(long j);

    @CriticalNative
    public static final native long setCallingWorkSourceUid(int i);

    @CriticalNative
    public static final native void setThreadStrictModePolicy(int i);

    public final native void forceDowngradeToSystemStability();

    @Override // android.p008os.IBinder
    public final native IBinder getExtension();

    @SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
    public final native void markVintfStability();

    public final native void setExtension(IBinder iBinder);

    /* renamed from: android.os.Binder$NoImagePreloadHolder */
    /* loaded from: classes3.dex */
    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(Binder.class.getClassLoader(), Binder.getNativeFinalizer(), 500);

        private NoImagePreloadHolder() {
        }
    }

    public static void enableStackTracking() {
        sStackTrackingEnabled = true;
    }

    public static void disableStackTracking() {
        sStackTrackingEnabled = false;
    }

    public static boolean isStackTrackingEnabled() {
        return sStackTrackingEnabled;
    }

    public static synchronized TransactionTracker getTransactionTracker() {
        TransactionTracker transactionTracker;
        synchronized (Binder.class) {
            if (sTransactionTracker == null) {
                sTransactionTracker = new TransactionTracker();
            }
            transactionTracker = sTransactionTracker;
        }
        return transactionTracker;
    }

    public static void setObserver(BinderInternal.Observer observer) {
        sObserver = observer;
    }

    public static void setWarnOnBlocking(boolean warnOnBlocking) {
        sWarnOnBlocking = warnOnBlocking;
    }

    public static IBinder allowBlocking(IBinder binder) {
        try {
            if (binder instanceof BinderProxy) {
                ((BinderProxy) binder).mWarnOnBlocking = false;
            } else if (binder != null && binder.getInterfaceDescriptor() != null && binder.queryLocalInterface(binder.getInterfaceDescriptor()) == null) {
                Log.m104w(TAG, "Unable to allow blocking on interface " + binder);
            }
        } catch (RemoteException e) {
        }
        return binder;
    }

    public static IBinder defaultBlocking(IBinder binder) {
        if (binder instanceof BinderProxy) {
            ((BinderProxy) binder).mWarnOnBlocking = sWarnOnBlocking;
        }
        return binder;
    }

    public static void copyAllowBlocking(IBinder fromBinder, IBinder toBinder) {
        if ((fromBinder instanceof BinderProxy) && (toBinder instanceof BinderProxy)) {
            ((BinderProxy) toBinder).mWarnOnBlocking = ((BinderProxy) fromBinder).mWarnOnBlocking;
        }
    }

    public static void allowBlockingForCurrentThread() {
        sWarnOnBlockingOnCurrentThread.set(false);
    }

    public static void defaultBlockingForCurrentThread() {
        sWarnOnBlockingOnCurrentThread.set(Boolean.valueOf(sWarnOnBlocking));
    }

    public static final int getCallingUidOrThrow() {
        if (!isDirectlyHandlingTransaction() && !hasExplicitIdentity()) {
            throw new IllegalStateException("Thread is not in a binder transaction, and the calling identity has not been explicitly set with clearCallingIdentity");
        }
        return getCallingUid();
    }

    public static final int getCallingUidOrWtf(String message) {
        if (!isDirectlyHandlingTransaction() && !hasExplicitIdentity()) {
            Slog.wtf(TAG, message + ": Thread is not in a binder transaction, and the calling identity has not been explicitly set with clearCallingIdentity");
        }
        return getCallingUid();
    }

    public static final UserHandle getCallingUserHandle() {
        return UserHandle.m145of(UserHandle.getUserId(getCallingUid()));
    }

    public static final void withCleanCallingIdentity(FunctionalUtils.ThrowingRunnable action) {
        long callingIdentity = clearCallingIdentity();
        try {
            action.runOrThrow();
            restoreCallingIdentity(callingIdentity);
            if (0 != 0) {
                throw ExceptionUtils.propagate(null);
            }
        } catch (Throwable throwable) {
            restoreCallingIdentity(callingIdentity);
            throw ExceptionUtils.propagate(throwable);
        }
    }

    public static final <T> T withCleanCallingIdentity(FunctionalUtils.ThrowingSupplier<T> action) {
        long callingIdentity = clearCallingIdentity();
        try {
            T orThrow = action.getOrThrow();
            restoreCallingIdentity(callingIdentity);
            if (0 == 0) {
                return orThrow;
            }
            throw ExceptionUtils.propagate(null);
        } catch (Throwable throwable) {
            restoreCallingIdentity(callingIdentity);
            throw ExceptionUtils.propagate(throwable);
        }
    }

    public static final void joinThreadPool() {
        BinderInternal.joinThreadPool();
    }

    public static final boolean isProxy(IInterface iface) {
        return iface.asBinder() != iface;
    }

    public Binder() {
        this(null);
    }

    public Binder(String descriptor) {
        this.mTransactionTraceNames = null;
        this.mSimpleDescriptor = null;
        long nativeBBinderHolder = getNativeBBinderHolder();
        this.mObject = nativeBBinderHolder;
        NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, nativeBBinderHolder);
        this.mDescriptor = descriptor;
    }

    public void attachInterface(IInterface owner, String descriptor) {
        this.mOwner = owner;
        this.mDescriptor = descriptor;
    }

    @Override // android.p008os.IBinder
    public String getInterfaceDescriptor() {
        return this.mDescriptor;
    }

    @Override // android.p008os.IBinder
    public boolean pingBinder() {
        return true;
    }

    @Override // android.p008os.IBinder
    public boolean isBinderAlive() {
        return true;
    }

    @Override // android.p008os.IBinder
    public IInterface queryLocalInterface(String descriptor) {
        String str = this.mDescriptor;
        if (str != null && str.equals(descriptor)) {
            return this.mOwner;
        }
        return null;
    }

    public static void setDumpDisabled(String msg) {
        sDumpDisabled = msg;
    }

    @SystemApi
    /* renamed from: android.os.Binder$ProxyTransactListener */
    /* loaded from: classes3.dex */
    public interface ProxyTransactListener {
        void onTransactEnded(Object obj);

        Object onTransactStarted(IBinder iBinder, int i);

        default Object onTransactStarted(IBinder binder, int transactionCode, int flags) {
            return onTransactStarted(binder, transactionCode);
        }
    }

    /* renamed from: android.os.Binder$PropagateWorkSourceTransactListener */
    /* loaded from: classes3.dex */
    public static class PropagateWorkSourceTransactListener implements ProxyTransactListener {
        @Override // android.p008os.Binder.ProxyTransactListener
        public Object onTransactStarted(IBinder binder, int transactionCode) {
            int uid = ThreadLocalWorkSource.getUid();
            if (uid != -1) {
                return Long.valueOf(Binder.setCallingWorkSourceUid(uid));
            }
            return null;
        }

        @Override // android.p008os.Binder.ProxyTransactListener
        public void onTransactEnded(Object session) {
            if (session != null) {
                long token = ((Long) session).longValue();
                Binder.restoreCallingWorkSource(token);
            }
        }
    }

    @SystemApi
    public static void setProxyTransactListener(ProxyTransactListener listener) {
        BinderProxy.setTransactListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        FileDescriptor fileDescriptor;
        if (code != 1598968902) {
            if (code != 1598311760) {
                if (code == 1598246212) {
                    ParcelFileDescriptor in = data.readFileDescriptor();
                    ParcelFileDescriptor out = data.readFileDescriptor();
                    ParcelFileDescriptor err = data.readFileDescriptor();
                    String[] args = data.readStringArray();
                    ShellCallback shellCallback = ShellCallback.CREATOR.createFromParcel(data);
                    ResultReceiver resultReceiver = ResultReceiver.CREATOR.createFromParcel(data);
                    if (out != null) {
                        if (in == null) {
                            fileDescriptor = null;
                        } else {
                            try {
                                fileDescriptor = in.getFileDescriptor();
                            } catch (Throwable th) {
                                IoUtils.closeQuietly(in);
                                IoUtils.closeQuietly(out);
                                IoUtils.closeQuietly(err);
                                if (reply != null) {
                                    reply.writeNoException();
                                } else {
                                    StrictMode.clearGatheredViolations();
                                }
                                throw th;
                            }
                        }
                        shellCommand(fileDescriptor, out.getFileDescriptor(), err != null ? err.getFileDescriptor() : out.getFileDescriptor(), args, shellCallback, resultReceiver);
                    }
                    IoUtils.closeQuietly(in);
                    IoUtils.closeQuietly(out);
                    IoUtils.closeQuietly(err);
                    if (reply != null) {
                        reply.writeNoException();
                    } else {
                        StrictMode.clearGatheredViolations();
                    }
                    return true;
                }
                return false;
            }
            ParcelFileDescriptor fd = data.readFileDescriptor();
            String[] args2 = data.readStringArray();
            if (fd != null) {
                try {
                    try {
                        dump(fd.getFileDescriptor(), args2);
                        IoUtils.closeQuietly(fd);
                    } catch (Throwable th2) {
                        th = th2;
                        IoUtils.closeQuietly(fd);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            if (reply != null) {
                reply.writeNoException();
            } else {
                StrictMode.clearGatheredViolations();
            }
            return true;
        }
        reply.writeString(getInterfaceDescriptor());
        return true;
    }

    public String getTransactionName(int transactionCode) {
        return null;
    }

    public final String getTransactionTraceName(int transactionCode) {
        if (this.mTransactionTraceNames == null) {
            int highestId = Math.min(getMaxTransactionId(), 1024);
            this.mSimpleDescriptor = getSimpleDescriptor();
            this.mTransactionTraceNames = new AtomicReferenceArray<>(highestId + 1);
        }
        int highestId2 = transactionCode - 1;
        if (highestId2 < 0 || highestId2 >= this.mTransactionTraceNames.length()) {
            return this.mSimpleDescriptor + "#" + transactionCode;
        }
        String transactionTraceName = (String) this.mTransactionTraceNames.getAcquire(highestId2);
        if (transactionTraceName == null) {
            String transactionName = getTransactionName(transactionCode);
            StringBuffer buf = new StringBuffer();
            buf.append("AIDL::java::");
            if (transactionName != null) {
                buf.append(this.mSimpleDescriptor).append("::").append(transactionName);
            } else {
                buf.append(this.mSimpleDescriptor).append("::#").append(transactionCode);
            }
            buf.append("::server");
            String transactionTraceName2 = buf.toString();
            this.mTransactionTraceNames.setRelease(highestId2, transactionTraceName2);
            return transactionTraceName2;
        }
        return transactionTraceName;
    }

    private String getSimpleDescriptor() {
        String descriptor = this.mDescriptor;
        if (descriptor == null) {
            return TAG;
        }
        int dot = descriptor.lastIndexOf(MediaMetrics.SEPARATOR);
        if (dot > 0) {
            return descriptor.substring(dot + 1);
        }
        return descriptor;
    }

    public int getMaxTransactionId() {
        return 0;
    }

    @Override // android.p008os.IBinder
    public void dump(FileDescriptor fd, String[] args) {
        FileOutputStream fout = new FileOutputStream(fd);
        PrintWriter pw = new FastPrintWriter(fout);
        try {
            doDump(fd, pw, args);
        } finally {
            pw.flush();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doDump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String disabled = sDumpDisabled;
        if (disabled == null) {
            try {
                dump(fd, pw, args);
                return;
            } catch (SecurityException e) {
                pw.println("Security exception: " + e.getMessage());
                throw e;
            } catch (Throwable e2) {
                pw.println();
                pw.println("Exception occurred while dumping:");
                e2.printStackTrace(pw);
                return;
            }
        }
        pw.println(sDumpDisabled);
    }

    @Override // android.p008os.IBinder
    public void dumpAsync(final FileDescriptor fd, final String[] args) {
        FileOutputStream fout = new FileOutputStream(fd);
        final PrintWriter pw = new FastPrintWriter(fout);
        Thread thr = new Thread("Binder.dumpAsync") { // from class: android.os.Binder.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Binder.this.dump(fd, pw, args);
                } finally {
                    pw.flush();
                }
            }
        };
        thr.start();
    }

    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
    }

    @Override // android.p008os.IBinder
    public void shellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) throws RemoteException {
        onShellCommand(in, out, err, args, callback, resultReceiver);
    }

    public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) throws RemoteException {
        ParcelFileDescriptor inPfd;
        int callingUid = getCallingUid();
        if (callingUid != 0 && callingUid != 2000) {
            resultReceiver.send(-1, null);
            throw new SecurityException("Shell commands are only callable by ADB");
        }
        if (in == null) {
            try {
                in = new FileInputStream("/dev/null").getFD();
            } catch (IOException e) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(err != null ? err : out));
                pw.println("Failed to open /dev/null: " + e.getMessage());
                pw.flush();
                resultReceiver.send(-1, null);
                return;
            }
        }
        if (out == null) {
            out = new FileOutputStream("/dev/null").getFD();
        }
        if (err == null) {
            err = out;
        }
        if (args == null) {
            args = new String[0];
        }
        int result = -1;
        try {
            try {
                inPfd = ParcelFileDescriptor.dup(in);
            } catch (IOException e2) {
                PrintWriter pw2 = new FastPrintWriter(new FileOutputStream(err));
                pw2.println("dup() failed: " + e2.getMessage());
                pw2.flush();
            }
            try {
                ParcelFileDescriptor outPfd = ParcelFileDescriptor.dup(out);
                ParcelFileDescriptor errPfd = ParcelFileDescriptor.dup(err);
                result = handleShellCommand(inPfd, outPfd, errPfd, args);
                if (errPfd != null) {
                    errPfd.close();
                }
                if (outPfd != null) {
                    outPfd.close();
                }
                if (inPfd != null) {
                    inPfd.close();
                }
            } catch (Throwable th) {
                if (inPfd != null) {
                    try {
                        inPfd.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } finally {
            resultReceiver.send(-1, null);
        }
    }

    @SystemApi
    public int handleShellCommand(ParcelFileDescriptor in, ParcelFileDescriptor out, ParcelFileDescriptor err, String[] args) {
        FileOutputStream ferr = new FileOutputStream(err.getFileDescriptor());
        PrintWriter pw = new FastPrintWriter(ferr);
        pw.println("No shell command implementation.");
        pw.flush();
        return 0;
    }

    @Override // android.p008os.IBinder
    public final boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (data != null) {
            data.setDataPosition(0);
        }
        boolean r = onTransact(code, data, reply, flags);
        if (reply != null) {
            reply.setDataPosition(0);
        }
        return r;
    }

    @Override // android.p008os.IBinder
    public void linkToDeath(IBinder.DeathRecipient recipient, int flags) {
    }

    @Override // android.p008os.IBinder
    public boolean unlinkToDeath(IBinder.DeathRecipient recipient, int flags) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkParcel(IBinder obj, int code, Parcel parcel, String msg) {
    }

    public static void setWorkSourceProvider(BinderInternal.WorkSourceProvider workSourceProvider) {
        if (workSourceProvider == null) {
            throw new IllegalArgumentException("workSourceProvider cannot be null");
        }
        sWorkSourceProvider = workSourceProvider;
    }

    private boolean execTransact(int code, long dataObj, long replyObj, int flags) {
        Parcel data = Parcel.obtain(dataObj);
        Parcel reply = Parcel.obtain(replyObj);
        int callingUid = data.isForRpc() ? -1 : getCallingUid();
        long origWorkSource = callingUid == -1 ? -1L : ThreadLocalWorkSource.setUid(callingUid);
        try {
            return execTransactInternal(code, data, reply, flags, callingUid);
        } finally {
            reply.recycle();
            data.recycle();
            if (callingUid != -1) {
                ThreadLocalWorkSource.restore(origWorkSource);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x0076, code lost:
        if (r6 != null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0078, code lost:
        r8 = android.p008os.Binder.sWorkSourceProvider.resolveWorkSourceUid(r18.readCallingWorkSourceUid());
        r6.callEnded(r7, r18.dataSize(), r19.dataSize(), r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x008d, code lost:
        checkParcel(r16, r17, r19, "Unreasonably large binder reply buffer");
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00c9, code lost:
        if (r6 != null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00cc, code lost:
        android.p008os.StrictMode.clearGatheredViolations();
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00cf, code lost:
        return r0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean execTransactInternal(int code, Parcel data, Parcel reply, int flags, int callingUid) {
        boolean res;
        BinderInternal.Observer observer = sObserver;
        BinderInternal.CallSession callSession = observer != null ? observer.callStarted(this, code, -1) : null;
        boolean tagEnabled = Trace.isTagEnabled(16777216L);
        boolean tracingEnabled = true;
        boolean hasFullyQualifiedName = getMaxTransactionId() > 0;
        String transactionTraceName = (tagEnabled && hasFullyQualifiedName) ? getTransactionTraceName(code) : (tagEnabled && isStackTrackingEnabled()) ? getTransactionTraceName(code) : null;
        tracingEnabled = (!tagEnabled || transactionTraceName == null) ? false : false;
        try {
            try {
                BinderCallHeavyHitterWatcher heavyHitterWatcher = sHeavyHitterWatcher;
                if (heavyHitterWatcher != null && callingUid != -1) {
                    heavyHitterWatcher.onTransaction(callingUid, getClass(), code);
                }
                if (tracingEnabled) {
                    Trace.traceBegin(16777216L, transactionTraceName);
                }
                if ((flags & 2) == 0 || callingUid == -1) {
                    res = onTransact(code, data, reply, flags);
                } else {
                    AppOpsManager.startNotedAppOpsCollection(callingUid);
                    try {
                        res = onTransact(code, data, reply, flags);
                        AppOpsManager.finishNotedAppOpsCollection();
                    } catch (Throwable th) {
                        AppOpsManager.finishNotedAppOpsCollection();
                        throw th;
                    }
                }
                if (tracingEnabled) {
                    Trace.traceEnd(16777216L);
                }
            } catch (Throwable th2) {
                if (tracingEnabled) {
                    Trace.traceEnd(16777216L);
                }
                if (observer != null) {
                    int workSourceUid = sWorkSourceProvider.resolveWorkSourceUid(data.readCallingWorkSourceUid());
                    observer.callEnded(callSession, data.dataSize(), reply.dataSize(), workSourceUid);
                }
                checkParcel(this, code, reply, "Unreasonably large binder reply buffer");
                throw th2;
            }
        } catch (RemoteException | RuntimeException e) {
            if (observer != null) {
                observer.callThrewException(callSession, e);
            }
            if (LOG_RUNTIME_EXCEPTION) {
                Log.m103w(TAG, "Caught a RuntimeException from the binder stub implementation.", e);
            }
            if ((flags & 1) == 0) {
                reply.setDataSize(0);
                reply.setDataPosition(0);
                reply.writeException(e);
            } else if (e instanceof RemoteException) {
                Log.m103w(TAG, "Binder call failed.", e);
            } else {
                Log.m103w(TAG, "Caught a RuntimeException from the binder stub implementation.", e);
            }
            res = true;
            if (tracingEnabled) {
                Trace.traceEnd(16777216L);
            }
        }
    }

    public static synchronized void setHeavyHitterWatcherConfig(boolean enabled, int batchSize, float threshold, BinderCallHeavyHitterWatcher.BinderCallHeavyHitterListener listener) {
        synchronized (Binder.class) {
            Slog.m94i(TAG, "Setting heavy hitter watcher config: " + enabled + ", " + batchSize + ", " + threshold);
            BinderCallHeavyHitterWatcher watcher = sHeavyHitterWatcher;
            if (enabled) {
                if (listener == null) {
                    throw new IllegalArgumentException();
                }
                boolean newWatcher = false;
                if (watcher == null) {
                    watcher = BinderCallHeavyHitterWatcher.getInstance();
                    newWatcher = true;
                }
                watcher.setConfig(true, batchSize, threshold, listener);
                if (newWatcher) {
                    sHeavyHitterWatcher = watcher;
                }
            } else if (watcher != null) {
                watcher.setConfig(false, 0, 0.0f, null);
            }
        }
    }
}
