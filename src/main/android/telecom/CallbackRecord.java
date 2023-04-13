package android.telecom;

import android.p008os.Handler;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class CallbackRecord<T> {
    private final T mCallback;
    private final Handler mHandler;

    public CallbackRecord(T callback, Handler handler) {
        this.mCallback = callback;
        this.mHandler = handler;
    }

    public T getCallback() {
        return this.mCallback;
    }

    public Handler getHandler() {
        return this.mHandler;
    }
}
