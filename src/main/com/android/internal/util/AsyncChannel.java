package com.android.internal.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Messenger;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.Objects;
import java.util.Stack;
/* loaded from: classes3.dex */
public class AsyncChannel {
    private static final int BASE = 69632;
    public static final int CMD_CHANNEL_DISCONNECT = 69635;
    public static final int CMD_CHANNEL_DISCONNECTED = 69636;
    public static final int CMD_CHANNEL_FULLY_CONNECTED = 69634;
    public static final int CMD_CHANNEL_FULL_CONNECTION = 69633;
    public static final int CMD_CHANNEL_HALF_CONNECTED = 69632;
    private static final int CMD_TO_STRING_COUNT = 5;
    private static final boolean DBG = false;
    public static final int STATUS_BINDING_UNSUCCESSFUL = 1;
    public static final int STATUS_FULL_CONNECTION_REFUSED_ALREADY_CONNECTED = 3;
    public static final int STATUS_REMOTE_DISCONNECTION = 4;
    public static final int STATUS_SEND_UNSUCCESSFUL = 2;
    public static final int STATUS_SUCCESSFUL = 0;
    private static final String TAG = "AsyncChannel";
    private static String[] sCmdToString;
    private AsyncChannelConnection mConnection;
    private DeathMonitor mDeathMonitor;
    private Messenger mDstMessenger;
    private Context mSrcContext;
    private Handler mSrcHandler;
    private Messenger mSrcMessenger;

    static {
        sCmdToString = r0;
        String[] strArr = {"CMD_CHANNEL_HALF_CONNECTED", "CMD_CHANNEL_FULL_CONNECTION", "CMD_CHANNEL_FULLY_CONNECTED", "CMD_CHANNEL_DISCONNECT", "CMD_CHANNEL_DISCONNECTED"};
    }

    protected static String cmdToString(int cmd) {
        int cmd2 = cmd - 69632;
        if (cmd2 >= 0) {
            String[] strArr = sCmdToString;
            if (cmd2 < strArr.length) {
                return strArr[cmd2];
            }
            return null;
        }
        return null;
    }

    public int connectSrcHandlerToPackageSync(Context srcContext, Handler srcHandler, String dstPackageName, String dstClassName) {
        this.mConnection = new AsyncChannelConnection();
        this.mSrcContext = srcContext;
        this.mSrcHandler = srcHandler;
        this.mSrcMessenger = new Messenger(srcHandler);
        this.mDstMessenger = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(dstPackageName, dstClassName);
        boolean result = srcContext.bindService(intent, this.mConnection, 1);
        return !result;
    }

    public int connectSync(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        connected(srcContext, srcHandler, dstMessenger);
        return 0;
    }

    public int connectSync(Context srcContext, Handler srcHandler, Handler dstHandler) {
        return connectSync(srcContext, srcHandler, new Messenger(dstHandler));
    }

    public int fullyConnectSync(Context srcContext, Handler srcHandler, Handler dstHandler) {
        int status = connectSync(srcContext, srcHandler, dstHandler);
        if (status == 0) {
            Message response = sendMessageSynchronously(CMD_CHANNEL_FULL_CONNECTION);
            return response.arg1;
        }
        return status;
    }

    public void connect(Context srcContext, Handler srcHandler, String dstPackageName, String dstClassName) {
        new Thread(new Runnable(srcContext, srcHandler, dstPackageName, dstClassName) { // from class: com.android.internal.util.AsyncChannel.1ConnectAsync
            String mDstClassName;
            String mDstPackageName;
            Context mSrcCtx;
            Handler mSrcHdlr;

            {
                this.mSrcCtx = srcContext;
                this.mSrcHdlr = srcHandler;
                this.mDstPackageName = dstPackageName;
                this.mDstClassName = dstClassName;
            }

            @Override // java.lang.Runnable
            public void run() {
                int result = AsyncChannel.this.connectSrcHandlerToPackageSync(this.mSrcCtx, this.mSrcHdlr, this.mDstPackageName, this.mDstClassName);
                AsyncChannel.this.replyHalfConnected(result);
            }
        }).start();
    }

    public void connect(Context srcContext, Handler srcHandler, Class<?> klass) {
        connect(srcContext, srcHandler, klass.getPackage().getName(), klass.getName());
    }

    public void connect(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        connected(srcContext, srcHandler, dstMessenger);
        replyHalfConnected(0);
    }

    public void connected(Context srcContext, Handler srcHandler, Messenger dstMessenger) {
        this.mSrcContext = srcContext;
        this.mSrcHandler = srcHandler;
        this.mSrcMessenger = new Messenger(this.mSrcHandler);
        this.mDstMessenger = dstMessenger;
    }

    public void connect(Context srcContext, Handler srcHandler, Handler dstHandler) {
        connect(srcContext, srcHandler, new Messenger(dstHandler));
    }

    public void connect(AsyncService srcAsyncService, Messenger dstMessenger) {
        connect(srcAsyncService, srcAsyncService.getHandler(), dstMessenger);
    }

    public void disconnected() {
        this.mSrcContext = null;
        this.mSrcHandler = null;
        this.mSrcMessenger = null;
        this.mDstMessenger = null;
        this.mDeathMonitor = null;
        this.mConnection = null;
    }

    public void disconnect() {
        Messenger messenger;
        Context context;
        AsyncChannelConnection asyncChannelConnection = this.mConnection;
        if (asyncChannelConnection != null && (context = this.mSrcContext) != null) {
            context.unbindService(asyncChannelConnection);
            this.mConnection = null;
        }
        try {
            Message msg = Message.obtain();
            msg.what = CMD_CHANNEL_DISCONNECTED;
            msg.replyTo = this.mSrcMessenger;
            this.mDstMessenger.send(msg);
        } catch (Exception e) {
        }
        replyDisconnected(0);
        this.mSrcHandler = null;
        if (this.mConnection == null && (messenger = this.mDstMessenger) != null && this.mDeathMonitor != null) {
            messenger.getBinder().unlinkToDeath(this.mDeathMonitor, 0);
            this.mDeathMonitor = null;
        }
    }

    public void sendMessage(Message msg) {
        msg.replyTo = this.mSrcMessenger;
        try {
            this.mDstMessenger.send(msg);
        } catch (RemoteException e) {
            replyDisconnected(2);
        }
    }

    public void sendMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        sendMessage(msg);
    }

    public void sendMessage(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        sendMessage(msg);
    }

    public void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        sendMessage(msg);
    }

    public void replyToMessage(Message srcMsg, Message dstMsg) {
        try {
            dstMsg.replyTo = this.mSrcMessenger;
            srcMsg.replyTo.send(dstMsg);
        } catch (RemoteException e) {
            log("TODO: handle replyToMessage RemoteException" + e);
            e.printStackTrace();
        }
    }

    public void replyToMessage(Message srcMsg, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        replyToMessage(srcMsg, msg);
    }

    public void replyToMessage(Message srcMsg, int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        replyToMessage(srcMsg, msg);
    }

    public Message sendMessageSynchronously(Message msg) {
        Message resultMsg = SyncMessenger.sendMessageSynchronously(this.mDstMessenger, msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    public Message sendMessageSynchronously(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        Message resultMsg = sendMessageSynchronously(msg);
        return resultMsg;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SyncMessenger {
        private SyncHandler mHandler;
        private HandlerThread mHandlerThread;
        private Messenger mMessenger;
        private static Stack<SyncMessenger> sStack = new Stack<>();
        private static int sCount = 0;

        private SyncMessenger() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class SyncHandler extends Handler {
            private Object mLockObject;
            private Message mResultMsg;

            private SyncHandler(Looper looper) {
                super(looper);
                this.mLockObject = new Object();
            }

            @Override // android.p008os.Handler
            public void handleMessage(Message msg) {
                Message msgCopy = Message.obtain();
                msgCopy.copyFrom(msg);
                synchronized (this.mLockObject) {
                    this.mResultMsg = msgCopy;
                    this.mLockObject.notify();
                }
            }
        }

        private static SyncMessenger obtain() {
            SyncMessenger sm;
            synchronized (sStack) {
                if (sStack.isEmpty()) {
                    sm = new SyncMessenger();
                    StringBuilder append = new StringBuilder().append("SyncHandler-");
                    int i = sCount;
                    sCount = i + 1;
                    HandlerThread handlerThread = new HandlerThread(append.append(i).toString());
                    sm.mHandlerThread = handlerThread;
                    handlerThread.start();
                    Objects.requireNonNull(sm);
                    sm.mHandler = new SyncHandler(sm.mHandlerThread.getLooper());
                    sm.mMessenger = new Messenger(sm.mHandler);
                } else {
                    sm = sStack.pop();
                }
            }
            return sm;
        }

        private void recycle() {
            synchronized (sStack) {
                sStack.push(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Message sendMessageSynchronously(Messenger dstMessenger, Message msg) {
            SyncMessenger sm = obtain();
            Message resultMsg = null;
            if (dstMessenger != null && msg != null) {
                try {
                    msg.replyTo = sm.mMessenger;
                    synchronized (sm.mHandler.mLockObject) {
                        if (sm.mHandler.mResultMsg != null) {
                            Log.wtf(AsyncChannel.TAG, "mResultMsg should be null here");
                            sm.mHandler.mResultMsg = null;
                        }
                        dstMessenger.send(msg);
                        sm.mHandler.mLockObject.wait();
                        resultMsg = sm.mHandler.mResultMsg;
                        sm.mHandler.mResultMsg = null;
                    }
                } catch (RemoteException e) {
                    Log.m109e(AsyncChannel.TAG, "error in sendMessageSynchronously", e);
                } catch (InterruptedException e2) {
                    Log.m109e(AsyncChannel.TAG, "error in sendMessageSynchronously", e2);
                }
            }
            sm.recycle();
            return resultMsg;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyHalfConnected(int status) {
        Message msg = this.mSrcHandler.obtainMessage(69632);
        msg.arg1 = status;
        msg.obj = this;
        msg.replyTo = this.mDstMessenger;
        if (!linkToDeathMonitor()) {
            msg.arg1 = 1;
        }
        this.mSrcHandler.sendMessage(msg);
    }

    private boolean linkToDeathMonitor() {
        if (this.mConnection == null && this.mDeathMonitor == null) {
            this.mDeathMonitor = new DeathMonitor();
            try {
                this.mDstMessenger.getBinder().linkToDeath(this.mDeathMonitor, 0);
                return true;
            } catch (RemoteException e) {
                this.mDeathMonitor = null;
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replyDisconnected(int status) {
        Handler handler = this.mSrcHandler;
        if (handler == null) {
            return;
        }
        Message msg = handler.obtainMessage(CMD_CHANNEL_DISCONNECTED);
        msg.arg1 = status;
        msg.obj = this;
        msg.replyTo = this.mDstMessenger;
        this.mSrcHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class AsyncChannelConnection implements ServiceConnection {
        AsyncChannelConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            AsyncChannel.this.mDstMessenger = new Messenger(service);
            AsyncChannel.this.replyHalfConnected(0);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            AsyncChannel.this.replyDisconnected(0);
        }
    }

    private static void log(String s) {
        Log.m112d(TAG, s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class DeathMonitor implements IBinder.DeathRecipient {
        DeathMonitor() {
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            AsyncChannel.this.replyDisconnected(4);
        }
    }
}
