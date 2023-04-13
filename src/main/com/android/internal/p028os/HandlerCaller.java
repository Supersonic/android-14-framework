package com.android.internal.p028os;

import android.content.Context;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
@Deprecated
/* renamed from: com.android.internal.os.HandlerCaller */
/* loaded from: classes4.dex */
public class HandlerCaller {
    final Callback mCallback;

    /* renamed from: mH */
    final Handler f903mH;
    final Looper mMainLooper;

    /* renamed from: com.android.internal.os.HandlerCaller$Callback */
    /* loaded from: classes4.dex */
    public interface Callback {
        void executeMessage(Message message);
    }

    /* renamed from: com.android.internal.os.HandlerCaller$MyHandler */
    /* loaded from: classes4.dex */
    class MyHandler extends Handler {
        MyHandler(Looper looper, boolean async) {
            super(looper, null, async);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            HandlerCaller.this.mCallback.executeMessage(msg);
        }
    }

    public HandlerCaller(Context context, Looper looper, Callback callback, boolean asyncHandler) {
        Looper mainLooper = looper != null ? looper : context.getMainLooper();
        this.mMainLooper = mainLooper;
        this.f903mH = new MyHandler(mainLooper, asyncHandler);
        this.mCallback = callback;
    }

    public Handler getHandler() {
        return this.f903mH;
    }

    public void executeOrSendMessage(Message msg) {
        if (Looper.myLooper() == this.mMainLooper) {
            this.mCallback.executeMessage(msg);
            msg.recycle();
            return;
        }
        this.f903mH.sendMessage(msg);
    }

    public void sendMessageDelayed(Message msg, long delayMillis) {
        this.f903mH.sendMessageDelayed(msg, delayMillis);
    }

    public boolean hasMessages(int what) {
        return this.f903mH.hasMessages(what);
    }

    public void removeMessages(int what) {
        this.f903mH.removeMessages(what);
    }

    public void removeMessages(int what, Object obj) {
        this.f903mH.removeMessages(what, obj);
    }

    public void sendMessage(Message msg) {
        this.f903mH.sendMessage(msg);
    }

    public SomeArgs sendMessageAndWait(Message msg) {
        if (Looper.myLooper() == this.f903mH.getLooper()) {
            throw new IllegalStateException("Can't wait on same thread as looper");
        }
        SomeArgs args = (SomeArgs) msg.obj;
        args.mWaitState = 1;
        this.f903mH.sendMessage(msg);
        synchronized (args) {
            while (args.mWaitState == 1) {
                try {
                    args.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }
        args.mWaitState = 0;
        return args;
    }

    public Message obtainMessage(int what) {
        return this.f903mH.obtainMessage(what);
    }

    public Message obtainMessageBO(int what, boolean arg1, Object arg2) {
        return this.f903mH.obtainMessage(what, arg1 ? 1 : 0, 0, arg2);
    }

    public Message obtainMessageBOO(int what, boolean arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        return this.f903mH.obtainMessage(what, arg1 ? 1 : 0, 0, args);
    }

    public Message obtainMessageO(int what, Object arg1) {
        return this.f903mH.obtainMessage(what, 0, 0, arg1);
    }

    public Message obtainMessageI(int what, int arg1) {
        return this.f903mH.obtainMessage(what, arg1, 0);
    }

    public Message obtainMessageII(int what, int arg1, int arg2) {
        return this.f903mH.obtainMessage(what, arg1, arg2);
    }

    public Message obtainMessageIO(int what, int arg1, Object arg2) {
        return this.f903mH.obtainMessage(what, arg1, 0, arg2);
    }

    public Message obtainMessageIIO(int what, int arg1, int arg2, Object arg3) {
        return this.f903mH.obtainMessage(what, arg1, arg2, arg3);
    }

    public Message obtainMessageIIOO(int what, int arg1, int arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg3;
        args.arg2 = arg4;
        return this.f903mH.obtainMessage(what, arg1, arg2, args);
    }

    public Message obtainMessageIOO(int what, int arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        return this.f903mH.obtainMessage(what, arg1, 0, args);
    }

    public Message obtainMessageIOOO(int what, int arg1, Object arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        args.arg3 = arg4;
        return this.f903mH.obtainMessage(what, arg1, 0, args);
    }

    public Message obtainMessageIIOOO(int what, int arg1, int arg2, Object arg3, Object arg4, Object arg5) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg3;
        args.arg2 = arg4;
        args.arg3 = arg5;
        return this.f903mH.obtainMessage(what, arg1, arg2, args);
    }

    public Message obtainMessageIIOOOO(int what, int arg1, int arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg3;
        args.arg2 = arg4;
        args.arg3 = arg5;
        args.arg4 = arg6;
        return this.f903mH.obtainMessage(what, arg1, arg2, args);
    }

    public Message obtainMessageOO(int what, Object arg1, Object arg2) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOO(int what, Object arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOOO(int what, Object arg1, Object arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        args.arg4 = arg4;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOOOO(int what, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        args.arg4 = arg4;
        args.arg5 = arg5;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOOOII(int what, Object arg1, Object arg2, Object arg3, Object arg4, int arg5, int arg6) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        args.arg4 = arg4;
        args.argi5 = arg5;
        args.argi6 = arg6;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIII(int what, int arg1, int arg2, int arg3, int arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIIIII(int what, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        args.argi5 = arg5;
        args.argi6 = arg6;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIIIO(int what, int arg1, int arg2, int arg3, int arg4, Object arg5) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg5;
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        return this.f903mH.obtainMessage(what, 0, 0, args);
    }
}
