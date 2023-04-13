package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class Registrant {
    WeakReference refH;
    Object userObj;
    int what;

    public Registrant(Handler handler, int i, Object obj) {
        this.refH = new WeakReference(handler);
        this.what = i;
        this.userObj = obj;
    }

    public void clear() {
        this.refH = null;
        this.userObj = null;
    }

    public void notifyRegistrant() {
        internalNotifyRegistrant(null, null);
    }

    public void notifyResult(Object obj) {
        internalNotifyRegistrant(obj, null);
    }

    public void notifyException(Throwable th) {
        internalNotifyRegistrant(null, th);
    }

    public void notifyRegistrant(AsyncResult asyncResult) {
        internalNotifyRegistrant(asyncResult.result, asyncResult.exception);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void internalNotifyRegistrant(Object obj, Throwable th) {
        Handler handler = getHandler();
        if (handler == null) {
            clear();
            return;
        }
        Message obtain = Message.obtain();
        obtain.what = this.what;
        obtain.obj = new AsyncResult(this.userObj, obj, th);
        handler.sendMessage(obtain);
    }

    public Message messageForRegistrant() {
        Handler handler = getHandler();
        if (handler == null) {
            clear();
            return null;
        }
        Message obtainMessage = handler.obtainMessage();
        obtainMessage.what = this.what;
        obtainMessage.obj = this.userObj;
        return obtainMessage;
    }

    public Handler getHandler() {
        WeakReference weakReference = this.refH;
        if (weakReference == null) {
            return null;
        }
        return (Handler) weakReference.get();
    }
}
