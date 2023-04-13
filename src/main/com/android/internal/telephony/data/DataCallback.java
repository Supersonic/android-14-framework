package com.android.internal.telephony.data;

import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class DataCallback {
    private final Executor mExecutor;

    public DataCallback(Executor executor) {
        this.mExecutor = executor;
    }

    @VisibleForTesting
    public Executor getExecutor() {
        return this.mExecutor;
    }

    public void invokeFromExecutor(Runnable runnable) {
        this.mExecutor.execute(runnable);
    }
}
