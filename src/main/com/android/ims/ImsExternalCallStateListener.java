package com.android.ims;

import android.telephony.ims.ImsExternalCallState;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public abstract class ImsExternalCallStateListener {
    protected Executor mListenerExecutor;

    public abstract void onImsExternalCallStateUpdate(List<ImsExternalCallState> list, Executor executor);

    public ImsExternalCallStateListener(Executor executor) {
        this.mListenerExecutor = new ImsEcbmStateListener$$ExternalSyntheticLambda0();
        if (executor != null) {
            this.mListenerExecutor = executor;
        }
    }

    public final void onImsExternalCallStateUpdate(List<ImsExternalCallState> externalCallState) {
        onImsExternalCallStateUpdate(externalCallState, this.mListenerExecutor);
    }
}
