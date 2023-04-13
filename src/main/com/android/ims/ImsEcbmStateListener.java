package com.android.ims;

import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public abstract class ImsEcbmStateListener {
    protected Executor mListenerExecutor;

    public abstract void onECBMEntered(Executor executor);

    public abstract void onECBMExited(Executor executor);

    public ImsEcbmStateListener(Executor executor) {
        this.mListenerExecutor = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
        if (executor != null) {
            this.mListenerExecutor = executor;
        }
    }

    public final void onECBMEntered() {
        onECBMEntered(this.mListenerExecutor);
    }

    public final void onECBMExited() {
        onECBMExited(this.mListenerExecutor);
    }
}
