package com.android.server.print;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.print.IPrintSpooler;
import android.print.IPrintSpoolerCallbacks;
import android.print.IPrintSpoolerClient;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.dump.DualDumpOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeoutException;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public final class RemotePrintSpooler {
    public static final long BIND_SPOOLER_SERVICE_TIMEOUT;
    public final PrintSpoolerCallbacks mCallbacks;
    public boolean mCanUnbind;
    public final Context mContext;
    public boolean mDestroyed;
    public final Intent mIntent;
    @GuardedBy({"mLock"})
    public boolean mIsBinding;
    public boolean mIsLowPriority;
    public IPrintSpooler mRemoteInstance;
    public final UserHandle mUserHandle;
    public final Object mLock = new Object();
    public final GetPrintJobInfosCaller mGetPrintJobInfosCaller = new GetPrintJobInfosCaller();
    public final GetPrintJobInfoCaller mGetPrintJobInfoCaller = new GetPrintJobInfoCaller();
    public final SetPrintJobStateCaller mSetPrintJobStatusCaller = new SetPrintJobStateCaller();
    public final SetPrintJobTagCaller mSetPrintJobTagCaller = new SetPrintJobTagCaller();
    public final OnCustomPrinterIconLoadedCaller mCustomPrinterIconLoadedCaller = new OnCustomPrinterIconLoadedCaller();
    public final ClearCustomPrinterIconCacheCaller mClearCustomPrinterIconCache = new ClearCustomPrinterIconCacheCaller();
    public final GetCustomPrinterIconCaller mGetCustomPrinterIconCaller = new GetCustomPrinterIconCaller();
    public final ServiceConnection mServiceConnection = new MyServiceConnection();
    public final PrintSpoolerClient mClient = new PrintSpoolerClient(this);

    /* loaded from: classes2.dex */
    public interface PrintSpoolerCallbacks {
        void onAllPrintJobsForServiceHandled(ComponentName componentName);

        void onPrintJobQueued(PrintJobInfo printJobInfo);

        void onPrintJobStateChanged(PrintJobInfo printJobInfo);
    }

    static {
        BIND_SPOOLER_SERVICE_TIMEOUT = Build.IS_ENG ? 120000L : 10000L;
    }

    public RemotePrintSpooler(Context context, int i, boolean z, PrintSpoolerCallbacks printSpoolerCallbacks) {
        this.mContext = context;
        this.mUserHandle = new UserHandle(i);
        this.mCallbacks = printSpoolerCallbacks;
        this.mIsLowPriority = z;
        Intent intent = new Intent();
        this.mIntent = intent;
        intent.setComponent(new ComponentName("com.android.printspooler", "com.android.printspooler.model.PrintSpoolerService"));
    }

    public void increasePriority() {
        if (this.mIsLowPriority) {
            this.mIsLowPriority = false;
            synchronized (this.mLock) {
                throwIfDestroyedLocked();
                while (!this.mCanUnbind) {
                    try {
                        this.mLock.wait();
                    } catch (InterruptedException unused) {
                        Slog.e("RemotePrintSpooler", "Interrupted while waiting for operation to complete");
                    }
                }
                unbindLocked();
            }
        }
    }

    public final List<PrintJobInfo> getPrintJobInfos(ComponentName componentName, int i, int i2) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                List<PrintJobInfo> printJobInfos = this.mGetPrintJobInfosCaller.getPrintJobInfos(getRemoteInstanceLazy(), componentName, i, i2);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
                return printJobInfos;
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error getting print jobs.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                    return null;
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final void writePrintJobData(ParcelFileDescriptor parcelFileDescriptor, PrintJobId printJobId) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().writePrintJobData(parcelFileDescriptor, printJobId);
                IoUtils.closeQuietly(parcelFileDescriptor);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error writing print job data.", e);
                IoUtils.closeQuietly(parcelFileDescriptor);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly(parcelFileDescriptor);
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final PrintJobInfo getPrintJobInfo(PrintJobId printJobId, int i) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                PrintJobInfo printJobInfo = this.mGetPrintJobInfoCaller.getPrintJobInfo(getRemoteInstanceLazy(), printJobId, i);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
                return printJobInfo;
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error getting print job info.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                    return null;
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final boolean setPrintJobState(PrintJobId printJobId, int i, String str) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                boolean printJobState = this.mSetPrintJobStatusCaller.setPrintJobState(getRemoteInstanceLazy(), printJobId, i, str);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
                return printJobState;
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting print job state.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                    return false;
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object] */
    public final void setProgress(PrintJobId printJobId, float f) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().setProgress(printJobId, f);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting progress.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object] */
    public final void setStatus(PrintJobId printJobId, CharSequence charSequence) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().setStatus(printJobId, charSequence);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting status.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object] */
    public final void setStatus(PrintJobId printJobId, int i, CharSequence charSequence) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().setStatusRes(printJobId, i, charSequence);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting status.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final void onCustomPrinterIconLoaded(PrinterId printerId, Icon icon) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                this.mCustomPrinterIconLoadedCaller.onCustomPrinterIconLoaded(getRemoteInstanceLazy(), printerId, icon);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error loading new custom printer icon.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final Icon getCustomPrinterIcon(PrinterId printerId) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                Icon customPrinterIcon = this.mGetCustomPrinterIconCaller.getCustomPrinterIcon(getRemoteInstanceLazy(), printerId);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
                return customPrinterIcon;
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error getting custom printer icon.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                    return null;
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public void clearCustomPrinterIconCache() {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                this.mClearCustomPrinterIconCache.clearCustomPrinterIconCache(getRemoteInstanceLazy());
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error clearing custom printer icon cache.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final boolean setPrintJobTag(PrintJobId printJobId, String str) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                boolean printJobTag = this.mSetPrintJobTagCaller.setPrintJobTag(getRemoteInstanceLazy(), printJobId, str);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
                return printJobTag;
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting print job tag.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                    return false;
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object] */
    public final void setPrintJobCancelling(PrintJobId printJobId, boolean z) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().setPrintJobCancelling(printJobId, z);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error setting print job cancelling.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.lang.Object] */
    public final void pruneApprovedPrintServices(List<ComponentName> list) {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().pruneApprovedPrintServices(list);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error pruning approved print services.", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.Object] */
    public final void removeObsoletePrintJobs() {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            this.mCanUnbind = false;
        }
        try {
            try {
                getRemoteInstanceLazy().removeObsoletePrintJobs();
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this.mLock.notifyAll();
                }
            } catch (RemoteException | InterruptedException | TimeoutException e) {
                Slog.e("RemotePrintSpooler", "Error removing obsolete print jobs .", e);
                synchronized (this.mLock) {
                    this.mCanUnbind = true;
                    this = this.mLock;
                    this.notifyAll();
                }
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mCanUnbind = true;
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    public final void destroy() {
        throwIfCalledOnMainThread();
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            unbindLocked();
            this.mDestroyed = true;
            this.mCanUnbind = false;
        }
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream) {
        synchronized (this.mLock) {
            dualDumpOutputStream.write("is_destroyed", 1133871366145L, this.mDestroyed);
            dualDumpOutputStream.write("is_bound", 1133871366146L, this.mRemoteInstance != null);
        }
        try {
            if (dualDumpOutputStream.isProto()) {
                dualDumpOutputStream.write((String) null, 1146756268035L, TransferPipe.dumpAsync(getRemoteInstanceLazy().asBinder(), new String[]{"--proto"}));
            } else {
                dualDumpOutputStream.writeNested("internal_state", TransferPipe.dumpAsync(getRemoteInstanceLazy().asBinder(), new String[0]));
            }
        } catch (RemoteException | IOException | InterruptedException | TimeoutException e) {
            Slog.e("RemotePrintSpooler", "Failed to dump remote instance", e);
        }
    }

    public final void onAllPrintJobsHandled() {
        synchronized (this.mLock) {
            throwIfDestroyedLocked();
            unbindLocked();
        }
    }

    public final void onPrintJobStateChanged(PrintJobInfo printJobInfo) {
        this.mCallbacks.onPrintJobStateChanged(printJobInfo);
    }

    public final IPrintSpooler getRemoteInstanceLazy() throws TimeoutException, InterruptedException {
        synchronized (this.mLock) {
            IPrintSpooler iPrintSpooler = this.mRemoteInstance;
            if (iPrintSpooler != null) {
                return iPrintSpooler;
            }
            bindLocked();
            return this.mRemoteInstance;
        }
    }

    @GuardedBy({"mLock"})
    public final void bindLocked() throws TimeoutException, InterruptedException {
        while (this.mIsBinding) {
            this.mLock.wait();
        }
        if (this.mRemoteInstance != null) {
            return;
        }
        this.mIsBinding = true;
        try {
            this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, this.mIsLowPriority ? 1 : 67108865, this.mUserHandle);
            long uptimeMillis = SystemClock.uptimeMillis();
            while (this.mRemoteInstance == null) {
                long uptimeMillis2 = BIND_SPOOLER_SERVICE_TIMEOUT - (SystemClock.uptimeMillis() - uptimeMillis);
                if (uptimeMillis2 <= 0) {
                    throw new TimeoutException("Cannot get spooler!");
                }
                this.mLock.wait(uptimeMillis2);
            }
            this.mCanUnbind = true;
        } finally {
            this.mIsBinding = false;
            this.mLock.notifyAll();
        }
    }

    public final void unbindLocked() {
        if (this.mRemoteInstance == null) {
            return;
        }
        while (!this.mCanUnbind) {
            try {
                this.mLock.wait();
            } catch (InterruptedException unused) {
            }
        }
        clearClientLocked();
        this.mRemoteInstance = null;
        this.mContext.unbindService(this.mServiceConnection);
    }

    public final void setClientLocked() {
        try {
            this.mRemoteInstance.setClient(this.mClient);
        } catch (RemoteException e) {
            Slog.d("RemotePrintSpooler", "Error setting print spooler client", e);
        }
    }

    public final void clearClientLocked() {
        try {
            this.mRemoteInstance.setClient((IPrintSpoolerClient) null);
        } catch (RemoteException e) {
            Slog.d("RemotePrintSpooler", "Error clearing print spooler client", e);
        }
    }

    public final void throwIfDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("Cannot interact with a destroyed instance.");
        }
    }

    public final void throwIfCalledOnMainThread() {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            throw new RuntimeException("Cannot invoke on the main thread");
        }
    }

    /* loaded from: classes2.dex */
    public final class MyServiceConnection implements ServiceConnection {
        public MyServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (RemotePrintSpooler.this.mLock) {
                RemotePrintSpooler.this.mRemoteInstance = IPrintSpooler.Stub.asInterface(iBinder);
                RemotePrintSpooler.this.setClientLocked();
                RemotePrintSpooler.this.mLock.notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (RemotePrintSpooler.this.mLock) {
                if (RemotePrintSpooler.this.mRemoteInstance != null) {
                    RemotePrintSpooler.this.clearClientLocked();
                    RemotePrintSpooler.this.mRemoteInstance = null;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class GetPrintJobInfosCaller extends TimedRemoteCaller<List<PrintJobInfo>> {
        public final IPrintSpoolerCallbacks mCallback;

        public GetPrintJobInfosCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.GetPrintJobInfosCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onGetPrintJobInfosResult(List<PrintJobInfo> list, int i) {
                    GetPrintJobInfosCaller.this.onRemoteMethodResult(list, i);
                }
            };
        }

        public List<PrintJobInfo> getPrintJobInfos(IPrintSpooler iPrintSpooler, ComponentName componentName, int i, int i2) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.getPrintJobInfos(this.mCallback, componentName, i, i2, onBeforeRemoteCall);
            return (List) getResultTimed(onBeforeRemoteCall);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GetPrintJobInfoCaller extends TimedRemoteCaller<PrintJobInfo> {
        public final IPrintSpoolerCallbacks mCallback;

        public GetPrintJobInfoCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.GetPrintJobInfoCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onGetPrintJobInfoResult(PrintJobInfo printJobInfo, int i) {
                    GetPrintJobInfoCaller.this.onRemoteMethodResult(printJobInfo, i);
                }
            };
        }

        public PrintJobInfo getPrintJobInfo(IPrintSpooler iPrintSpooler, PrintJobId printJobId, int i) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.getPrintJobInfo(printJobId, this.mCallback, i, onBeforeRemoteCall);
            return (PrintJobInfo) getResultTimed(onBeforeRemoteCall);
        }
    }

    /* loaded from: classes2.dex */
    public static final class SetPrintJobStateCaller extends TimedRemoteCaller<Boolean> {
        public final IPrintSpoolerCallbacks mCallback;

        public SetPrintJobStateCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.SetPrintJobStateCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onSetPrintJobStateResult(boolean z, int i) {
                    SetPrintJobStateCaller.this.onRemoteMethodResult(Boolean.valueOf(z), i);
                }
            };
        }

        public boolean setPrintJobState(IPrintSpooler iPrintSpooler, PrintJobId printJobId, int i, String str) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.setPrintJobState(printJobId, i, str, this.mCallback, onBeforeRemoteCall);
            return ((Boolean) getResultTimed(onBeforeRemoteCall)).booleanValue();
        }
    }

    /* loaded from: classes2.dex */
    public static final class SetPrintJobTagCaller extends TimedRemoteCaller<Boolean> {
        public final IPrintSpoolerCallbacks mCallback;

        public SetPrintJobTagCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.SetPrintJobTagCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onSetPrintJobTagResult(boolean z, int i) {
                    SetPrintJobTagCaller.this.onRemoteMethodResult(Boolean.valueOf(z), i);
                }
            };
        }

        public boolean setPrintJobTag(IPrintSpooler iPrintSpooler, PrintJobId printJobId, String str) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.setPrintJobTag(printJobId, str, this.mCallback, onBeforeRemoteCall);
            return ((Boolean) getResultTimed(onBeforeRemoteCall)).booleanValue();
        }
    }

    /* loaded from: classes2.dex */
    public static final class OnCustomPrinterIconLoadedCaller extends TimedRemoteCaller<Void> {
        public final IPrintSpoolerCallbacks mCallback;

        public OnCustomPrinterIconLoadedCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.OnCustomPrinterIconLoadedCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onCustomPrinterIconCached(int i) {
                    OnCustomPrinterIconLoadedCaller.this.onRemoteMethodResult(null, i);
                }
            };
        }

        public Void onCustomPrinterIconLoaded(IPrintSpooler iPrintSpooler, PrinterId printerId, Icon icon) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.onCustomPrinterIconLoaded(printerId, icon, this.mCallback, onBeforeRemoteCall);
            return (Void) getResultTimed(onBeforeRemoteCall);
        }
    }

    /* loaded from: classes2.dex */
    public static final class ClearCustomPrinterIconCacheCaller extends TimedRemoteCaller<Void> {
        public final IPrintSpoolerCallbacks mCallback;

        public ClearCustomPrinterIconCacheCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.ClearCustomPrinterIconCacheCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void customPrinterIconCacheCleared(int i) {
                    ClearCustomPrinterIconCacheCaller.this.onRemoteMethodResult(null, i);
                }
            };
        }

        public Void clearCustomPrinterIconCache(IPrintSpooler iPrintSpooler) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.clearCustomPrinterIconCache(this.mCallback, onBeforeRemoteCall);
            return (Void) getResultTimed(onBeforeRemoteCall);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GetCustomPrinterIconCaller extends TimedRemoteCaller<Icon> {
        public final IPrintSpoolerCallbacks mCallback;

        public GetCustomPrinterIconCaller() {
            super(5000L);
            this.mCallback = new BasePrintSpoolerServiceCallbacks() { // from class: com.android.server.print.RemotePrintSpooler.GetCustomPrinterIconCaller.1
                @Override // com.android.server.print.RemotePrintSpooler.BasePrintSpoolerServiceCallbacks
                public void onGetCustomPrinterIconResult(Icon icon, int i) {
                    GetCustomPrinterIconCaller.this.onRemoteMethodResult(icon, i);
                }
            };
        }

        public Icon getCustomPrinterIcon(IPrintSpooler iPrintSpooler, PrinterId printerId) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iPrintSpooler.getCustomPrinterIcon(printerId, this.mCallback, onBeforeRemoteCall);
            return (Icon) getResultTimed(onBeforeRemoteCall);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class BasePrintSpoolerServiceCallbacks extends IPrintSpoolerCallbacks.Stub {
        public void customPrinterIconCacheCleared(int i) {
        }

        public void onCancelPrintJobResult(boolean z, int i) {
        }

        public void onCustomPrinterIconCached(int i) {
        }

        public void onGetCustomPrinterIconResult(Icon icon, int i) {
        }

        public void onGetPrintJobInfoResult(PrintJobInfo printJobInfo, int i) {
        }

        public void onGetPrintJobInfosResult(List<PrintJobInfo> list, int i) {
        }

        public void onSetPrintJobStateResult(boolean z, int i) {
        }

        public void onSetPrintJobTagResult(boolean z, int i) {
        }

        public BasePrintSpoolerServiceCallbacks() {
        }
    }

    /* loaded from: classes2.dex */
    public static final class PrintSpoolerClient extends IPrintSpoolerClient.Stub {
        public final WeakReference<RemotePrintSpooler> mWeakSpooler;

        public PrintSpoolerClient(RemotePrintSpooler remotePrintSpooler) {
            this.mWeakSpooler = new WeakReference<>(remotePrintSpooler);
        }

        public void onPrintJobQueued(PrintJobInfo printJobInfo) {
            RemotePrintSpooler remotePrintSpooler = this.mWeakSpooler.get();
            if (remotePrintSpooler != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    remotePrintSpooler.mCallbacks.onPrintJobQueued(printJobInfo);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void onAllPrintJobsForServiceHandled(ComponentName componentName) {
            RemotePrintSpooler remotePrintSpooler = this.mWeakSpooler.get();
            if (remotePrintSpooler != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    remotePrintSpooler.mCallbacks.onAllPrintJobsForServiceHandled(componentName);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void onAllPrintJobsHandled() {
            RemotePrintSpooler remotePrintSpooler = this.mWeakSpooler.get();
            if (remotePrintSpooler != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    remotePrintSpooler.onAllPrintJobsHandled();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void onPrintJobStateChanged(PrintJobInfo printJobInfo) {
            RemotePrintSpooler remotePrintSpooler = this.mWeakSpooler.get();
            if (remotePrintSpooler != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    remotePrintSpooler.onPrintJobStateChanged(printJobInfo);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }
}
