package com.android.server.p012tv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.tv.ITvRemoteProvider;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
/* renamed from: com.android.server.tv.TvRemoteProviderProxy */
/* loaded from: classes2.dex */
public final class TvRemoteProviderProxy implements ServiceConnection {
    public static final boolean DEBUG = Log.isLoggable("TvRemoteProviderProxy", 2);
    public boolean mBound;
    public final ComponentName mComponentName;
    public boolean mConnected;
    public final Context mContext;
    public final Object mLock;
    public boolean mRunning;
    public final int mUid;
    public final int mUserId;

    public TvRemoteProviderProxy(Context context, Object obj, ComponentName componentName, int i, int i2) {
        this.mContext = context;
        this.mLock = obj;
        this.mComponentName = componentName;
        this.mUserId = i;
        this.mUid = i2;
    }

    public boolean hasComponentName(String str, String str2) {
        return this.mComponentName.getPackageName().equals(str) && this.mComponentName.getClassName().equals(str2);
    }

    public void start() {
        if (this.mRunning) {
            return;
        }
        if (DEBUG) {
            Slog.d("TvRemoteProviderProxy", this + ": Starting");
        }
        this.mRunning = true;
        bind();
    }

    public void stop() {
        if (this.mRunning) {
            if (DEBUG) {
                Slog.d("TvRemoteProviderProxy", this + ": Stopping");
            }
            this.mRunning = false;
            unbind();
        }
    }

    public void rebindIfDisconnected() {
        if (!this.mRunning || this.mConnected) {
            return;
        }
        unbind();
        bind();
    }

    public final void bind() {
        if (this.mBound) {
            return;
        }
        boolean z = DEBUG;
        if (z) {
            Slog.d("TvRemoteProviderProxy", this + ": Binding");
        }
        Intent intent = new Intent("com.android.media.tv.remoteprovider.TvRemoteProvider");
        intent.setComponent(this.mComponentName);
        try {
            boolean bindServiceAsUser = this.mContext.bindServiceAsUser(intent, this, 67108865, new UserHandle(this.mUserId));
            this.mBound = bindServiceAsUser;
            if (!z || bindServiceAsUser) {
                return;
            }
            Slog.d("TvRemoteProviderProxy", this + ": Bind failed");
        } catch (SecurityException e) {
            if (DEBUG) {
                Slog.d("TvRemoteProviderProxy", this + ": Bind failed", e);
            }
        }
    }

    public final void unbind() {
        if (this.mBound) {
            if (DEBUG) {
                Slog.d("TvRemoteProviderProxy", this + ": Unbinding");
            }
            this.mBound = false;
            this.mContext.unbindService(this);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (DEBUG) {
            Slog.d("TvRemoteProviderProxy", this + ": onServiceConnected()");
        }
        this.mConnected = true;
        ITvRemoteProvider asInterface = ITvRemoteProvider.Stub.asInterface(iBinder);
        if (asInterface == null) {
            Slog.e("TvRemoteProviderProxy", this + ": Invalid binder");
            return;
        }
        try {
            asInterface.setRemoteServiceInputSink(new TvRemoteServiceInput(this.mLock, asInterface));
        } catch (RemoteException unused) {
            Slog.e("TvRemoteProviderProxy", this + ": Failed remote call to setRemoteServiceInputSink");
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.mConnected = false;
        if (DEBUG) {
            Slog.d("TvRemoteProviderProxy", this + ": onServiceDisconnected()");
        }
    }
}
