package com.android.server.systemcaptions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class RemoteSystemCaptionsManagerService {
    public static final String TAG = "RemoteSystemCaptionsManagerService";
    public final ComponentName mComponentName;
    public final Context mContext;
    public final Intent mIntent;
    @GuardedBy({"mLock"})
    public IBinder mService;
    public final int mUserId;
    public final boolean mVerbose;
    public final Object mLock = new Object();
    public final RemoteServiceConnection mServiceConnection = new RemoteServiceConnection();
    @GuardedBy({"mLock"})
    public boolean mBinding = false;
    @GuardedBy({"mLock"})
    public boolean mDestroyed = false;
    public final Handler mHandler = new Handler(Looper.getMainLooper());

    public RemoteSystemCaptionsManagerService(Context context, ComponentName componentName, int i, boolean z) {
        this.mContext = context;
        this.mComponentName = componentName;
        this.mUserId = i;
        this.mVerbose = z;
        this.mIntent = new Intent("android.service.systemcaptions.SystemCaptionsManagerService").setComponent(componentName);
    }

    public void initialize() {
        if (this.mVerbose) {
            Slog.v(TAG, "initialize()");
        }
        scheduleBind();
    }

    public void destroy() {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.systemcaptions.RemoteSystemCaptionsManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((RemoteSystemCaptionsManagerService) obj).handleDestroy();
            }
        }, this));
    }

    public void handleDestroy() {
        if (this.mVerbose) {
            Slog.v(TAG, "handleDestroy()");
        }
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                if (this.mVerbose) {
                    Slog.v(TAG, "handleDestroy(): Already destroyed");
                }
                return;
            }
            this.mDestroyed = true;
            ensureUnboundLocked();
        }
    }

    public final void scheduleBind() {
        if (this.mHandler.hasMessages(1)) {
            if (this.mVerbose) {
                Slog.v(TAG, "scheduleBind(): already scheduled");
                return;
            }
            return;
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.systemcaptions.RemoteSystemCaptionsManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((RemoteSystemCaptionsManagerService) obj).handleEnsureBound();
            }
        }, this).setWhat(1));
    }

    public final void handleEnsureBound() {
        synchronized (this.mLock) {
            if (this.mService == null && !this.mBinding) {
                if (this.mVerbose) {
                    Slog.v(TAG, "handleEnsureBound(): binding");
                }
                this.mBinding = true;
                if (!this.mContext.bindServiceAsUser(this.mIntent, this.mServiceConnection, 67112961, this.mHandler, new UserHandle(this.mUserId))) {
                    String str = TAG;
                    Slog.w(str, "Could not bind to " + this.mIntent + " with flags 67112961");
                    this.mBinding = false;
                    this.mService = null;
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void ensureUnboundLocked() {
        if (this.mService != null || this.mBinding) {
            this.mBinding = false;
            this.mService = null;
            if (this.mVerbose) {
                Slog.v(TAG, "ensureUnbound(): unbinding");
            }
            this.mContext.unbindService(this.mServiceConnection);
        }
    }

    /* loaded from: classes2.dex */
    public class RemoteServiceConnection implements ServiceConnection {
        public RemoteServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (RemoteSystemCaptionsManagerService.this.mLock) {
                if (RemoteSystemCaptionsManagerService.this.mVerbose) {
                    Slog.v(RemoteSystemCaptionsManagerService.TAG, "onServiceConnected()");
                }
                if (!RemoteSystemCaptionsManagerService.this.mDestroyed && RemoteSystemCaptionsManagerService.this.mBinding) {
                    RemoteSystemCaptionsManagerService.this.mBinding = false;
                    RemoteSystemCaptionsManagerService.this.mService = iBinder;
                    return;
                }
                Slog.wtf(RemoteSystemCaptionsManagerService.TAG, "onServiceConnected() dispatched after unbindService");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (RemoteSystemCaptionsManagerService.this.mLock) {
                if (RemoteSystemCaptionsManagerService.this.mVerbose) {
                    Slog.v(RemoteSystemCaptionsManagerService.TAG, "onServiceDisconnected()");
                }
                RemoteSystemCaptionsManagerService.this.mBinding = true;
                RemoteSystemCaptionsManagerService.this.mService = null;
            }
        }
    }
}
