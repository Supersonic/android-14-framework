package com.android.internal.telephony;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class AsyncService extends Service {
    public static final int CMD_ASYNC_SERVICE_DESTROY = 16777216;
    public static final int CMD_ASYNC_SERVICE_ON_START_INTENT = 16777215;
    protected static final boolean DBG = true;
    AsyncServiceInfo mAsyncServiceInfo;
    Handler mHandler;
    protected Messenger mMessenger;

    /* loaded from: classes.dex */
    public static final class AsyncServiceInfo {
        public Handler mHandler;
        public int mRestartFlags;
    }

    public abstract AsyncServiceInfo createHandler();

    public Handler getHandler() {
        return this.mHandler;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        AsyncServiceInfo createHandler = createHandler();
        this.mAsyncServiceInfo = createHandler;
        this.mHandler = createHandler.mHandler;
        this.mMessenger = new Messenger(this.mHandler);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d("AsyncService", "onStartCommand");
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = CMD_ASYNC_SERVICE_ON_START_INTENT;
        obtainMessage.arg1 = i;
        obtainMessage.arg2 = i2;
        obtainMessage.obj = intent;
        this.mHandler.sendMessage(obtainMessage);
        return this.mAsyncServiceInfo.mRestartFlags;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d("AsyncService", "onDestroy");
        Message obtainMessage = this.mHandler.obtainMessage();
        obtainMessage.what = CMD_ASYNC_SERVICE_DESTROY;
        this.mHandler.sendMessage(obtainMessage);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }
}
