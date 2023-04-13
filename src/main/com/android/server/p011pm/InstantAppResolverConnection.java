package com.android.server.p011pm;

import android.app.IInstantAppResolver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.InstantAppRequestInfo;
import android.content.pm.InstantAppResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.server.p011pm.InstantAppResolverConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
/* renamed from: com.android.server.pm.InstantAppResolverConnection */
/* loaded from: classes2.dex */
public final class InstantAppResolverConnection implements IBinder.DeathRecipient {
    public static final long BIND_SERVICE_TIMEOUT_MS;
    public static final long CALL_SERVICE_TIMEOUT_MS;
    public static final boolean DEBUG_INSTANT;
    public final Context mContext;
    public final Intent mIntent;
    @GuardedBy({"mLock"})
    public IInstantAppResolver mRemoteInstance;
    public final Object mLock = new Object();
    public final GetInstantAppResolveInfoCaller mGetInstantAppResolveInfoCaller = new GetInstantAppResolveInfoCaller();
    public final ServiceConnection mServiceConnection = new MyServiceConnection();
    @GuardedBy({"mLock"})
    public int mBindState = 0;
    public final Handler mBgHandler = BackgroundThread.getHandler();

    /* renamed from: com.android.server.pm.InstantAppResolverConnection$PhaseTwoCallback */
    /* loaded from: classes2.dex */
    public static abstract class PhaseTwoCallback {
        public abstract void onPhaseTwoResolved(List<InstantAppResolveInfo> list, long j);
    }

    static {
        boolean z = Build.IS_ENG;
        BIND_SERVICE_TIMEOUT_MS = z ? 500L : 300L;
        CALL_SERVICE_TIMEOUT_MS = z ? 200L : 100L;
        DEBUG_INSTANT = Build.IS_DEBUGGABLE;
    }

    public InstantAppResolverConnection(Context context, ComponentName componentName, String str) {
        this.mContext = context;
        this.mIntent = new Intent(str).setComponent(componentName);
    }

    public List<InstantAppResolveInfo> getInstantAppResolveInfoList(InstantAppRequestInfo instantAppRequestInfo) throws ConnectionException {
        throwIfCalledOnMainThread();
        try {
            try {
                try {
                    List<InstantAppResolveInfo> instantAppResolveInfoList = this.mGetInstantAppResolveInfoCaller.getInstantAppResolveInfoList(getRemoteInstanceLazy(instantAppRequestInfo.getToken()), instantAppRequestInfo);
                    synchronized (this.mLock) {
                        this.mLock.notifyAll();
                    }
                    return instantAppResolveInfoList;
                } catch (RemoteException unused) {
                    synchronized (this.mLock) {
                        this.mLock.notifyAll();
                        return null;
                    }
                } catch (TimeoutException unused2) {
                    throw new ConnectionException(2);
                }
            } catch (InterruptedException unused3) {
                throw new ConnectionException(3);
            } catch (TimeoutException unused4) {
                throw new ConnectionException(1);
            }
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mLock.notifyAll();
                throw th;
            }
        }
    }

    /* renamed from: com.android.server.pm.InstantAppResolverConnection$1 */
    /* loaded from: classes2.dex */
    public class IRemoteCallback$StubC12811 extends IRemoteCallback.Stub {
        public final /* synthetic */ PhaseTwoCallback val$callback;
        public final /* synthetic */ Handler val$callbackHandler;
        public final /* synthetic */ long val$startTime;

        public IRemoteCallback$StubC12811(Handler handler, PhaseTwoCallback phaseTwoCallback, long j) {
            this.val$callbackHandler = handler;
            this.val$callback = phaseTwoCallback;
            this.val$startTime = j;
        }

        public void sendResult(Bundle bundle) throws RemoteException {
            final ArrayList parcelableArrayList = bundle.getParcelableArrayList("android.app.extra.RESOLVE_INFO", InstantAppResolveInfo.class);
            Handler handler = this.val$callbackHandler;
            final PhaseTwoCallback phaseTwoCallback = this.val$callback;
            final long j = this.val$startTime;
            handler.post(new Runnable() { // from class: com.android.server.pm.InstantAppResolverConnection$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InstantAppResolverConnection.PhaseTwoCallback.this.onPhaseTwoResolved(parcelableArrayList, j);
                }
            });
        }
    }

    public void getInstantAppIntentFilterList(InstantAppRequestInfo instantAppRequestInfo, PhaseTwoCallback phaseTwoCallback, Handler handler, long j) throws ConnectionException {
        try {
            getRemoteInstanceLazy(instantAppRequestInfo.getToken()).getInstantAppIntentFilterList(instantAppRequestInfo, new IRemoteCallback$StubC12811(handler, phaseTwoCallback, j));
        } catch (RemoteException unused) {
        } catch (InterruptedException unused2) {
            throw new ConnectionException(3);
        } catch (TimeoutException unused3) {
            throw new ConnectionException(1);
        }
    }

    public final IInstantAppResolver getRemoteInstanceLazy(String str) throws ConnectionException, TimeoutException, InterruptedException {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return bind(str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mLock"})
    public final void waitForBindLocked(String str) throws TimeoutException, InterruptedException {
        long uptimeMillis = SystemClock.uptimeMillis();
        while (this.mBindState != 0 && this.mRemoteInstance == null) {
            long uptimeMillis2 = BIND_SERVICE_TIMEOUT_MS - (SystemClock.uptimeMillis() - uptimeMillis);
            if (uptimeMillis2 <= 0) {
                throw new TimeoutException("[" + str + "] Didn't bind to resolver in time!");
            }
            this.mLock.wait(uptimeMillis2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:89:0x0118 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final IInstantAppResolver bind(String str) throws ConnectionException, TimeoutException, InterruptedException {
        boolean z;
        Throwable th;
        boolean z2;
        IInstantAppResolver iInstantAppResolver;
        synchronized (this.mLock) {
            IInstantAppResolver iInstantAppResolver2 = this.mRemoteInstance;
            if (iInstantAppResolver2 != null) {
                return iInstantAppResolver2;
            }
            if (this.mBindState == 2) {
                if (DEBUG_INSTANT) {
                    Slog.i("PackageManager", "[" + str + "] Previous bind timed out; waiting for connection");
                }
                try {
                    waitForBindLocked(str);
                    IInstantAppResolver iInstantAppResolver3 = this.mRemoteInstance;
                    if (iInstantAppResolver3 != null) {
                        return iInstantAppResolver3;
                    }
                } catch (TimeoutException unused) {
                    z = true;
                }
            }
            z = false;
            if (this.mBindState == 1) {
                if (DEBUG_INSTANT) {
                    Slog.i("PackageManager", "[" + str + "] Another thread is binding; waiting for connection");
                }
                waitForBindLocked(str);
                IInstantAppResolver iInstantAppResolver4 = this.mRemoteInstance;
                if (iInstantAppResolver4 != null) {
                    return iInstantAppResolver4;
                }
                throw new ConnectionException(1);
            }
            this.mBindState = 1;
            if (z) {
                try {
                    if (DEBUG_INSTANT) {
                        Slog.i("PackageManager", "[" + str + "] Previous connection never established; rebinding");
                    }
                    this.mContext.unbindService(this.mServiceConnection);
                } catch (Throwable th2) {
                    th = th2;
                    z2 = false;
                    synchronized (this.mLock) {
                        if (z2 && 0 == 0) {
                            this.mBindState = 2;
                        } else {
                            this.mBindState = 0;
                        }
                        this.mLock.notifyAll();
                    }
                    throw th;
                }
            }
            if (DEBUG_INSTANT) {
                Slog.v("PackageManager", "[" + str + "] Binding to instant app resolver");
            }
            z2 = this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 67108865, UserHandle.SYSTEM);
            try {
                if (z2) {
                    synchronized (this.mLock) {
                        waitForBindLocked(str);
                        iInstantAppResolver = this.mRemoteInstance;
                    }
                    synchronized (this.mLock) {
                        if (z2 && iInstantAppResolver == null) {
                            this.mBindState = 2;
                        } else {
                            this.mBindState = 0;
                        }
                        this.mLock.notifyAll();
                    }
                    return iInstantAppResolver;
                }
                Slog.w("PackageManager", "[" + str + "] Failed to bind to: " + this.mIntent);
                throw new ConnectionException(1);
            } catch (Throwable th3) {
                th = th3;
                synchronized (this.mLock) {
                }
            }
        }
    }

    public final void throwIfCalledOnMainThread() {
        if (Thread.currentThread() == this.mContext.getMainLooper().getThread()) {
            throw new RuntimeException("Cannot invoke on the main thread");
        }
    }

    public void optimisticBind() {
        this.mBgHandler.post(new Runnable() { // from class: com.android.server.pm.InstantAppResolverConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InstantAppResolverConnection.this.lambda$optimisticBind$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$optimisticBind$0() {
        try {
            if (bind("Optimistic Bind") == null || !DEBUG_INSTANT) {
                return;
            }
            Slog.i("PackageManager", "Optimistic bind succeeded.");
        } catch (ConnectionException | InterruptedException | TimeoutException e) {
            Slog.e("PackageManager", "Optimistic bind failed.", e);
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        if (DEBUG_INSTANT) {
            Slog.d("PackageManager", "Binder to instant app resolver died");
        }
        synchronized (this.mLock) {
            handleBinderDiedLocked();
        }
        optimisticBind();
    }

    @GuardedBy({"mLock"})
    public final void handleBinderDiedLocked() {
        IInstantAppResolver iInstantAppResolver = this.mRemoteInstance;
        if (iInstantAppResolver != null) {
            try {
                iInstantAppResolver.asBinder().unlinkToDeath(this, 0);
            } catch (NoSuchElementException unused) {
            }
        }
        this.mRemoteInstance = null;
    }

    /* renamed from: com.android.server.pm.InstantAppResolverConnection$ConnectionException */
    /* loaded from: classes2.dex */
    public static class ConnectionException extends Exception {
        public final int failure;

        public ConnectionException(int i) {
            this.failure = i;
        }
    }

    /* renamed from: com.android.server.pm.InstantAppResolverConnection$MyServiceConnection */
    /* loaded from: classes2.dex */
    public final class MyServiceConnection implements ServiceConnection {
        public MyServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (InstantAppResolverConnection.DEBUG_INSTANT) {
                Slog.d("PackageManager", "Connected to instant app resolver");
            }
            synchronized (InstantAppResolverConnection.this.mLock) {
                InstantAppResolverConnection.this.mRemoteInstance = IInstantAppResolver.Stub.asInterface(iBinder);
                if (InstantAppResolverConnection.this.mBindState == 2) {
                    InstantAppResolverConnection.this.mBindState = 0;
                }
                try {
                    iBinder.linkToDeath(InstantAppResolverConnection.this, 0);
                } catch (RemoteException unused) {
                    InstantAppResolverConnection.this.handleBinderDiedLocked();
                }
                InstantAppResolverConnection.this.mLock.notifyAll();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            if (InstantAppResolverConnection.DEBUG_INSTANT) {
                Slog.d("PackageManager", "Disconnected from instant app resolver");
            }
            synchronized (InstantAppResolverConnection.this.mLock) {
                InstantAppResolverConnection.this.handleBinderDiedLocked();
            }
        }
    }

    /* renamed from: com.android.server.pm.InstantAppResolverConnection$GetInstantAppResolveInfoCaller */
    /* loaded from: classes2.dex */
    public static final class GetInstantAppResolveInfoCaller extends TimedRemoteCaller<List<InstantAppResolveInfo>> {
        public final IRemoteCallback mCallback;

        public GetInstantAppResolveInfoCaller() {
            super(InstantAppResolverConnection.CALL_SERVICE_TIMEOUT_MS);
            this.mCallback = new IRemoteCallback.Stub() { // from class: com.android.server.pm.InstantAppResolverConnection.GetInstantAppResolveInfoCaller.1
                public void sendResult(Bundle bundle) throws RemoteException {
                    GetInstantAppResolveInfoCaller.this.onRemoteMethodResult(bundle.getParcelableArrayList("android.app.extra.RESOLVE_INFO", InstantAppResolveInfo.class), bundle.getInt("android.app.extra.SEQUENCE", -1));
                }
            };
        }

        public List<InstantAppResolveInfo> getInstantAppResolveInfoList(IInstantAppResolver iInstantAppResolver, InstantAppRequestInfo instantAppRequestInfo) throws RemoteException, TimeoutException {
            int onBeforeRemoteCall = onBeforeRemoteCall();
            iInstantAppResolver.getInstantAppResolveInfoList(instantAppRequestInfo, onBeforeRemoteCall, this.mCallback);
            return (List) getResultTimed(onBeforeRemoteCall);
        }
    }
}
