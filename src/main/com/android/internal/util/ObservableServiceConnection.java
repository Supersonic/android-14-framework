package com.android.internal.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.p008os.IBinder;
import com.android.internal.util.CallbackRegistry;
import com.android.internal.util.ObservableServiceConnection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class ObservableServiceConnection<T> implements ServiceConnection {
    public static final int DISCONNECT_REASON_BINDING_DIED = 3;
    public static final int DISCONNECT_REASON_DISCONNECTED = 2;
    public static final int DISCONNECT_REASON_NONE = 0;
    public static final int DISCONNECT_REASON_NULL_BINDING = 1;
    public static final int DISCONNECT_REASON_UNBIND = 4;
    private final Context mContext;
    private final Executor mExecutor;
    private final int mFlags;
    private T mService;
    private final Intent mServiceIntent;
    private final ServiceTransformer<T> mTransformer;
    private final Object mLock = new Object();
    private boolean mBoundCalled = false;
    private int mLastDisconnectReason = 0;
    private final CallbackRegistry<Callback<T>, ObservableServiceConnection<T>, T> mCallbackRegistry = new CallbackRegistry<>(new C43811());

    /* loaded from: classes3.dex */
    public interface Callback<T> {
        void onConnected(ObservableServiceConnection<T> observableServiceConnection, T t);

        void onDisconnected(ObservableServiceConnection<T> observableServiceConnection, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DisconnectReason {
    }

    /* loaded from: classes3.dex */
    public interface ServiceTransformer<T> {
        T convert(IBinder iBinder);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.util.ObservableServiceConnection$1 */
    /* loaded from: classes3.dex */
    public class C43811 extends CallbackRegistry.NotifierCallback<Callback<T>, ObservableServiceConnection<T>, T> {
        C43811() {
        }

        @Override // com.android.internal.util.CallbackRegistry.NotifierCallback
        public /* bridge */ /* synthetic */ void onNotifyCallback(Object obj, Object obj2, int i, Object obj3) {
            onNotifyCallback((Callback<int>) obj, (ObservableServiceConnection<int>) obj2, i, (int) obj3);
        }

        public void onNotifyCallback(final Callback<T> callback, final ObservableServiceConnection<T> sender, final int disconnectReason, final T service) {
            ObservableServiceConnection.this.mExecutor.execute(new Runnable() { // from class: com.android.internal.util.ObservableServiceConnection$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ObservableServiceConnection.C43811.this.lambda$onNotifyCallback$0(service, callback, sender, disconnectReason);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNotifyCallback$0(Object service, Callback callback, ObservableServiceConnection sender, int disconnectReason) {
            synchronized (ObservableServiceConnection.this.mLock) {
                if (service != null) {
                    callback.onConnected(sender, service);
                } else if (ObservableServiceConnection.this.mLastDisconnectReason != 0) {
                    callback.onDisconnected(sender, disconnectReason);
                }
            }
        }
    }

    public ObservableServiceConnection(Context context, Executor executor, ServiceTransformer<T> transformer, Intent serviceIntent, int flags) {
        this.mContext = context;
        this.mExecutor = executor;
        this.mTransformer = transformer;
        this.mServiceIntent = serviceIntent;
        this.mFlags = flags;
    }

    public void execute(Runnable runnable) {
        this.mExecutor.execute(runnable);
    }

    public boolean bind() {
        synchronized (this.mLock) {
            if (this.mBoundCalled) {
                return false;
            }
            boolean bindResult = this.mContext.bindService(this.mServiceIntent, this.mFlags, this.mExecutor, this);
            this.mBoundCalled = true;
            return bindResult;
        }
    }

    public void unbind() {
        onDisconnected(4);
    }

    public void addCallback(final Callback<T> callback) {
        this.mCallbackRegistry.add(callback);
        this.mExecutor.execute(new Runnable() { // from class: com.android.internal.util.ObservableServiceConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ObservableServiceConnection.this.lambda$addCallback$0(callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addCallback$0(Callback callback) {
        synchronized (this.mLock) {
            T t = this.mService;
            if (t != null) {
                callback.onConnected(this, t);
            } else {
                int i = this.mLastDisconnectReason;
                if (i != 0) {
                    callback.onDisconnected(this, i);
                }
            }
        }
    }

    public void removeCallback(Callback<T> callback) {
        synchronized (this.mLock) {
            this.mCallbackRegistry.remove(callback);
        }
    }

    private void onDisconnected(int reason) {
        synchronized (this.mLock) {
            if (this.mBoundCalled) {
                this.mBoundCalled = false;
                this.mLastDisconnectReason = reason;
                this.mContext.unbindService(this);
                this.mService = null;
                this.mCallbackRegistry.notifyCallbacks(this, reason, null);
            }
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName name, IBinder service) {
        synchronized (this.mLock) {
            T convert = this.mTransformer.convert(service);
            this.mService = convert;
            this.mLastDisconnectReason = 0;
            this.mCallbackRegistry.notifyCallbacks(this, 0, convert);
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName name) {
        onDisconnected(2);
    }

    @Override // android.content.ServiceConnection
    public final void onBindingDied(ComponentName name) {
        onDisconnected(3);
    }

    @Override // android.content.ServiceConnection
    public final void onNullBinding(ComponentName name) {
        onDisconnected(1);
    }
}
