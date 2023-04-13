package com.android.internal.telephony.uicc.euicc.async;

import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public abstract class AsyncResultCallback<Result> {
    public abstract void onResult(Result result);

    public void onException(Throwable th) {
        Rlog.e("AsyncResultCallback", "Error in onException", th);
    }
}
