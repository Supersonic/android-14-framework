package com.android.internal.infra;

import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p001pm.ParceledListSlice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.p008os.UserHandle;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
@Deprecated
/* loaded from: classes4.dex */
public abstract class AbstractRemoteService<S extends AbstractRemoteService<S, I>, I extends IInterface> implements IBinder.DeathRecipient {
    protected static final int LAST_PRIVATE_MSG = 2;
    private static final int MSG_BIND = 1;
    private static final int MSG_UNBIND = 2;
    public static final long PERMANENT_BOUND_TIMEOUT_MS = 0;
    private static final int SERVICE_NOT_EXIST = -1;
    private final int mBindingFlags;
    private boolean mBound;
    private boolean mCompleted;
    protected final ComponentName mComponentName;
    private boolean mConnecting;
    private final Context mContext;
    private boolean mDestroyed;
    protected final Handler mHandler;
    private final Intent mIntent;
    private long mNextUnbind;
    protected I mService;
    private boolean mServiceDied;
    private final int mUserId;
    public final boolean mVerbose;
    private final VultureCallback<S> mVultureCallback;
    protected final String mTag = getClass().getSimpleName();
    private final ServiceConnection mServiceConnection = new RemoteServiceConnection();
    protected final ArrayList<BasePendingRequest<S, I>> mUnfinishedRequests = new ArrayList<>();
    private int mServiceExitReason = -1;
    private int mServiceExitSubReason = -1;

    /* loaded from: classes4.dex */
    public interface AsyncRequest<I extends IInterface> {
        void run(I i) throws RemoteException;
    }

    /* loaded from: classes4.dex */
    public interface VultureCallback<T> {
        void onServiceDied(T t);
    }

    protected abstract I getServiceInterface(IBinder iBinder);

    protected abstract long getTimeoutIdleBindMillis();

    abstract void handleBindFailure();

    protected abstract void handleOnDestroy();

    abstract void handlePendingRequestWhileUnBound(BasePendingRequest<S, I> basePendingRequest);

    abstract void handlePendingRequests();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractRemoteService(Context context, String serviceInterface, ComponentName componentName, int userId, VultureCallback<S> callback, Handler handler, int bindingFlags, boolean verbose) {
        this.mContext = context;
        this.mVultureCallback = callback;
        this.mVerbose = verbose;
        this.mComponentName = componentName;
        this.mIntent = new Intent(serviceInterface).setComponent(componentName);
        this.mUserId = userId;
        this.mHandler = new Handler(handler.getLooper());
        this.mBindingFlags = bindingFlags;
    }

    public final void destroy() {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractRemoteService) obj).handleDestroy();
            }
        }, this));
    }

    public final boolean isDestroyed() {
        return this.mDestroyed;
    }

    public final ComponentName getComponentName() {
        return this.mComponentName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnConnectedStateChangedInternal(boolean connected) {
        handleOnConnectedStateChanged(connected);
        if (connected) {
            handlePendingRequests();
        }
    }

    protected void handleOnConnectedStateChanged(boolean state) {
    }

    protected long getRemoteRequestMillis() {
        throw new UnsupportedOperationException("not implemented by " + getClass());
    }

    public final I getServiceInterface() {
        return this.mService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroy() {
        if (checkIfDestroyed()) {
            return;
        }
        handleOnDestroy();
        handleEnsureUnbound();
        this.mDestroyed = true;
    }

    @Override // android.p008os.IBinder.DeathRecipient
    public void binderDied() {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractRemoteService) obj).handleBinderDied();
            }
        }, this));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBinderDied() {
        if (checkIfDestroyed()) {
            return;
        }
        I i = this.mService;
        if (i != null) {
            i.asBinder().unlinkToDeath(this, 0);
        }
        updateServicelicationExitInfo(this.mComponentName, this.mUserId);
        this.mConnecting = true;
        this.mService = null;
        this.mServiceDied = true;
        cancelScheduledUnbind();
        this.mVultureCallback.onServiceDied(this);
        handleBindFailure();
    }

    private void updateServicelicationExitInfo(ComponentName componentName, int userId) {
        IActivityManager am = ActivityManager.getService();
        String packageName = componentName.getPackageName();
        ParceledListSlice<ApplicationExitInfo> plistSlice = null;
        try {
            plistSlice = am.getHistoricalProcessExitReasons(packageName, 0, 1, userId);
        } catch (RemoteException e) {
        }
        if (plistSlice == null) {
            return;
        }
        List<ApplicationExitInfo> list = plistSlice.getList();
        if (list.isEmpty()) {
            return;
        }
        ApplicationExitInfo info = list.get(0);
        this.mServiceExitReason = info.getReason();
        this.mServiceExitSubReason = info.getSubReason();
        if (this.mVerbose) {
            Slog.m92v(this.mTag, "updateServicelicationExitInfo: exitReason=" + ApplicationExitInfo.reasonCodeToString(this.mServiceExitReason) + " exitSubReason= " + ApplicationExitInfo.subreasonToString(this.mServiceExitSubReason));
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.append((CharSequence) prefix).append("service:").println();
        pw.append((CharSequence) prefix).append("  ").append("userId=").append((CharSequence) String.valueOf(this.mUserId)).println();
        pw.append((CharSequence) prefix).append("  ").append("componentName=").append((CharSequence) this.mComponentName.flattenToString()).println();
        pw.append((CharSequence) prefix).append("  ").append("destroyed=").append((CharSequence) String.valueOf(this.mDestroyed)).println();
        pw.append((CharSequence) prefix).append("  ").append("numUnfinishedRequests=").append((CharSequence) String.valueOf(this.mUnfinishedRequests.size())).println();
        pw.append((CharSequence) prefix).append("  ").append("bound=").append((CharSequence) String.valueOf(this.mBound));
        boolean bound = handleIsBound();
        pw.append((CharSequence) prefix).append("  ").append("connected=").append((CharSequence) String.valueOf(bound));
        long idleTimeout = getTimeoutIdleBindMillis();
        if (bound) {
            if (idleTimeout > 0) {
                pw.append(" (unbind in : ");
                TimeUtils.formatDuration(this.mNextUnbind - SystemClock.elapsedRealtime(), pw);
                pw.append(NavigationBarInflaterView.KEY_CODE_END);
            } else {
                pw.append(" (permanently bound)");
            }
        }
        pw.println();
        if (this.mServiceExitReason != -1) {
            pw.append((CharSequence) prefix).append("  ").append("serviceExistReason=").append((CharSequence) ApplicationExitInfo.reasonCodeToString(this.mServiceExitReason));
            pw.println();
        }
        if (this.mServiceExitSubReason != -1) {
            pw.append((CharSequence) prefix).append("  ").append("serviceExistSubReason=").append((CharSequence) ApplicationExitInfo.subreasonToString(this.mServiceExitSubReason));
            pw.println();
        }
        pw.append((CharSequence) prefix).append("mBindingFlags=").println(this.mBindingFlags);
        pw.append((CharSequence) prefix).append("idleTimeout=").append((CharSequence) Long.toString(idleTimeout / 1000)).append("s\n");
        pw.append((CharSequence) prefix).append("requestTimeout=");
        try {
            pw.append((CharSequence) Long.toString(getRemoteRequestMillis() / 1000)).append("s\n");
        } catch (UnsupportedOperationException e) {
            pw.append("not supported\n");
        }
        pw.println();
    }

    protected void scheduleRequest(BasePendingRequest<S, I> pendingRequest) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AbstractRemoteService) obj).handlePendingRequest((AbstractRemoteService.BasePendingRequest) obj2);
            }
        }, this, pendingRequest));
    }

    void finishRequest(BasePendingRequest<S, I> finshedRequest) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda5
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AbstractRemoteService) obj).handleFinishRequest((AbstractRemoteService.BasePendingRequest) obj2);
            }
        }, this, finshedRequest));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFinishRequest(BasePendingRequest<S, I> finshedRequest) {
        this.mUnfinishedRequests.remove(finshedRequest);
        if (this.mUnfinishedRequests.isEmpty()) {
            scheduleUnbind();
        }
    }

    protected void scheduleAsyncRequest(AsyncRequest<I> request) {
        MyAsyncPendingRequest<S, I> asyncRequest = new MyAsyncPendingRequest<>(this, request);
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AbstractRemoteService) obj).handlePendingRequest((AbstractRemoteService.MyAsyncPendingRequest) obj2);
            }
        }, this, asyncRequest));
    }

    protected void executeAsyncRequest(AsyncRequest<I> request) {
        MyAsyncPendingRequest<S, I> asyncRequest = new MyAsyncPendingRequest<>(this, request);
        handlePendingRequest(asyncRequest);
    }

    private void cancelScheduledUnbind() {
        this.mHandler.removeMessages(2);
    }

    protected void scheduleBind() {
        if (this.mHandler.hasMessages(1)) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "scheduleBind(): already scheduled");
                return;
            }
            return;
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractRemoteService) obj).handleEnsureBound();
            }
        }, this).setWhat(1));
    }

    protected void scheduleUnbind() {
        scheduleUnbind(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleUnbind(boolean delay) {
        long unbindDelay = getTimeoutIdleBindMillis();
        if (unbindDelay <= 0) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "not scheduling unbind when value is " + unbindDelay);
                return;
            }
            return;
        }
        if (!delay) {
            unbindDelay = 0;
        }
        cancelScheduledUnbind();
        this.mNextUnbind = SystemClock.elapsedRealtime() + unbindDelay;
        if (this.mVerbose) {
            Slog.m92v(this.mTag, "unbinding in " + unbindDelay + "ms: " + this.mNextUnbind);
        }
        this.mHandler.sendMessageDelayed(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.infra.AbstractRemoteService$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractRemoteService) obj).handleUnbind();
            }
        }, this).setWhat(2), unbindDelay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUnbind() {
        if (checkIfDestroyed()) {
            return;
        }
        handleEnsureUnbound();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void handlePendingRequest(BasePendingRequest<S, I> pendingRequest) {
        if (checkIfDestroyed() || this.mCompleted) {
            return;
        }
        if (!handleIsBound()) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "handlePendingRequest(): queuing " + pendingRequest);
            }
            handlePendingRequestWhileUnBound(pendingRequest);
            handleEnsureBound();
            return;
        }
        if (this.mVerbose) {
            Slog.m92v(this.mTag, "handlePendingRequest(): " + pendingRequest);
        }
        this.mUnfinishedRequests.add(pendingRequest);
        cancelScheduledUnbind();
        pendingRequest.run();
        if (pendingRequest.isFinal()) {
            this.mCompleted = true;
        }
    }

    private boolean handleIsBound() {
        return this.mService != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEnsureBound() {
        if (handleIsBound() || this.mConnecting) {
            return;
        }
        if (this.mVerbose) {
            Slog.m92v(this.mTag, "ensureBound()");
        }
        this.mConnecting = true;
        int flags = 67112961 | this.mBindingFlags;
        boolean willBind = this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, flags, this.mHandler, new UserHandle(this.mUserId));
        this.mBound = true;
        if (!willBind) {
            Slog.m90w(this.mTag, "could not bind to " + this.mIntent + " using flags " + flags);
            this.mConnecting = false;
            if (!this.mServiceDied) {
                handleBinderDied();
            }
        }
    }

    private void handleEnsureUnbound() {
        if (handleIsBound() || this.mConnecting) {
            if (this.mVerbose) {
                Slog.m92v(this.mTag, "ensureUnbound()");
            }
            this.mConnecting = false;
            if (handleIsBound()) {
                handleOnConnectedStateChangedInternal(false);
                I i = this.mService;
                if (i != null) {
                    i.asBinder().unlinkToDeath(this, 0);
                    this.mService = null;
                }
            }
            this.mNextUnbind = 0L;
            if (this.mBound) {
                this.mContext.unbindService(this.mServiceConnection);
                this.mBound = false;
            }
        }
    }

    /* loaded from: classes4.dex */
    private class RemoteServiceConnection implements ServiceConnection {
        private RemoteServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (AbstractRemoteService.this.mVerbose) {
                Slog.m92v(AbstractRemoteService.this.mTag, "onServiceConnected()");
            }
            if (AbstractRemoteService.this.mDestroyed || !AbstractRemoteService.this.mConnecting) {
                Slog.wtf(AbstractRemoteService.this.mTag, "onServiceConnected() was dispatched after unbindService.");
                return;
            }
            AbstractRemoteService.this.mConnecting = false;
            try {
                service.linkToDeath(AbstractRemoteService.this, 0);
                AbstractRemoteService abstractRemoteService = AbstractRemoteService.this;
                abstractRemoteService.mService = (I) abstractRemoteService.getServiceInterface(service);
                AbstractRemoteService.this.mServiceExitReason = -1;
                AbstractRemoteService.this.mServiceExitSubReason = -1;
                AbstractRemoteService.this.handleOnConnectedStateChangedInternal(true);
                AbstractRemoteService.this.mServiceDied = false;
            } catch (RemoteException e) {
                AbstractRemoteService.this.handleBinderDied();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            if (AbstractRemoteService.this.mVerbose) {
                Slog.m92v(AbstractRemoteService.this.mTag, "onServiceDisconnected()");
            }
            AbstractRemoteService.this.mConnecting = true;
            AbstractRemoteService.this.mService = null;
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName name) {
            if (AbstractRemoteService.this.mVerbose) {
                Slog.m92v(AbstractRemoteService.this.mTag, "onBindingDied()");
            }
            AbstractRemoteService.this.scheduleUnbind(false);
        }
    }

    private boolean checkIfDestroyed() {
        if (this.mDestroyed && this.mVerbose) {
            Slog.m92v(this.mTag, "Not handling operation as service for " + this.mComponentName + " is already destroyed");
        }
        return this.mDestroyed;
    }

    public String toString() {
        return getClass().getSimpleName() + NavigationBarInflaterView.SIZE_MOD_START + this.mComponentName + " " + System.identityHashCode(this) + (this.mService != null ? " (bound)" : " (unbound)") + (this.mDestroyed ? " (destroyed)" : "") + NavigationBarInflaterView.SIZE_MOD_END;
    }

    /* loaded from: classes4.dex */
    public static abstract class BasePendingRequest<S extends AbstractRemoteService<S, I>, I extends IInterface> implements Runnable {
        boolean mCancelled;
        boolean mCompleted;
        final WeakReference<S> mWeakService;
        protected final String mTag = getClass().getSimpleName();
        protected final Object mLock = new Object();

        BasePendingRequest(S service) {
            this.mWeakService = new WeakReference<>(service);
        }

        protected final S getService() {
            return this.mWeakService.get();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final boolean finish() {
            synchronized (this.mLock) {
                if (!this.mCompleted && !this.mCancelled) {
                    this.mCompleted = true;
                    S service = this.mWeakService.get();
                    if (service != null) {
                        service.finishRequest(this);
                    }
                    onFinished();
                    return true;
                }
                return false;
            }
        }

        void onFinished() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onFailed() {
        }

        protected final boolean isCancelledLocked() {
            return this.mCancelled;
        }

        public boolean cancel() {
            synchronized (this.mLock) {
                if (!this.mCancelled && !this.mCompleted) {
                    this.mCancelled = true;
                    S service = this.mWeakService.get();
                    if (service != null) {
                        service.finishRequest(this);
                    }
                    onCancel();
                    return true;
                }
                return false;
            }
        }

        void onCancel() {
        }

        protected boolean isFinal() {
            return false;
        }

        protected boolean isRequestCompleted() {
            boolean z;
            synchronized (this.mLock) {
                z = this.mCompleted;
            }
            return z;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class PendingRequest<S extends AbstractRemoteService<S, I>, I extends IInterface> extends BasePendingRequest<S, I> {
        private final Handler mServiceHandler;
        private final Runnable mTimeoutTrigger;

        protected abstract void onTimeout(S s);

        protected PendingRequest(final S service) {
            super(service);
            Handler handler = service.mHandler;
            this.mServiceHandler = handler;
            Runnable runnable = new Runnable() { // from class: com.android.internal.infra.AbstractRemoteService$PendingRequest$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AbstractRemoteService.PendingRequest.this.lambda$new$0(service);
                }
            };
            this.mTimeoutTrigger = runnable;
            handler.postAtTime(runnable, SystemClock.uptimeMillis() + service.getRemoteRequestMillis());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(AbstractRemoteService service) {
            synchronized (this.mLock) {
                if (this.mCancelled) {
                    return;
                }
                this.mCompleted = true;
                S remoteService = this.mWeakService.get();
                if (remoteService != null) {
                    Slog.m90w(this.mTag, "timed out after " + service.getRemoteRequestMillis() + " ms");
                    remoteService.finishRequest(this);
                    onTimeout(remoteService);
                    return;
                }
                Slog.m90w(this.mTag, "timed out (no service)");
            }
        }

        @Override // com.android.internal.infra.AbstractRemoteService.BasePendingRequest
        final void onFinished() {
            this.mServiceHandler.removeCallbacks(this.mTimeoutTrigger);
        }

        @Override // com.android.internal.infra.AbstractRemoteService.BasePendingRequest
        final void onCancel() {
            this.mServiceHandler.removeCallbacks(this.mTimeoutTrigger);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class MyAsyncPendingRequest<S extends AbstractRemoteService<S, I>, I extends IInterface> extends BasePendingRequest<S, I> {
        private static final String TAG = MyAsyncPendingRequest.class.getSimpleName();
        private final AsyncRequest<I> mRequest;

        protected MyAsyncPendingRequest(S service, AsyncRequest<I> request) {
            super(service);
            this.mRequest = request;
        }

        @Override // java.lang.Runnable
        public void run() {
            S remoteService = getService();
            try {
                if (remoteService == null) {
                    return;
                }
                try {
                    this.mRequest.run(remoteService.mService);
                } catch (RemoteException e) {
                    Slog.m90w(TAG, "exception handling async request (" + this + "): " + e);
                }
            } finally {
                finish();
            }
        }
    }
}
