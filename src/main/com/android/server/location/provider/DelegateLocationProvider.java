package com.android.server.location.provider;

import android.location.LocationResult;
import android.os.Bundle;
import com.android.internal.util.Preconditions;
import com.android.server.location.provider.AbstractLocationProvider;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.function.UnaryOperator;
/* loaded from: classes.dex */
public class DelegateLocationProvider extends AbstractLocationProvider implements AbstractLocationProvider.Listener {
    public final AbstractLocationProvider mDelegate;
    public final Object mInitializationLock;
    public boolean mInitialized;

    public static /* synthetic */ AbstractLocationProvider.State lambda$onStateChanged$1(AbstractLocationProvider.State state, AbstractLocationProvider.State state2) {
        return state;
    }

    public DelegateLocationProvider(Executor executor, AbstractLocationProvider abstractLocationProvider) {
        super(executor, null, null, Collections.emptySet());
        this.mInitializationLock = new Object();
        this.mInitialized = false;
        this.mDelegate = abstractLocationProvider;
    }

    public void initializeDelegate() {
        synchronized (this.mInitializationLock) {
            Preconditions.checkState(!this.mInitialized);
            setState(new UnaryOperator() { // from class: com.android.server.location.provider.DelegateLocationProvider$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    AbstractLocationProvider.State lambda$initializeDelegate$0;
                    lambda$initializeDelegate$0 = DelegateLocationProvider.this.lambda$initializeDelegate$0((AbstractLocationProvider.State) obj);
                    return lambda$initializeDelegate$0;
                }
            });
            this.mInitialized = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AbstractLocationProvider.State lambda$initializeDelegate$0(AbstractLocationProvider.State state) {
        return this.mDelegate.getController().setListener(this);
    }

    public final void waitForInitialization() {
        synchronized (this.mInitializationLock) {
            Preconditions.checkState(this.mInitialized);
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider.Listener
    public void onStateChanged(AbstractLocationProvider.State state, final AbstractLocationProvider.State state2) {
        waitForInitialization();
        setState(new UnaryOperator() { // from class: com.android.server.location.provider.DelegateLocationProvider$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                AbstractLocationProvider.State lambda$onStateChanged$1;
                lambda$onStateChanged$1 = DelegateLocationProvider.lambda$onStateChanged$1(AbstractLocationProvider.State.this, (AbstractLocationProvider.State) obj);
                return lambda$onStateChanged$1;
            }
        });
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider.Listener
    public void onReportLocation(LocationResult locationResult) {
        waitForInitialization();
        reportLocation(locationResult);
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onFlush(Runnable runnable) {
        Preconditions.checkState(this.mInitialized);
        this.mDelegate.getController().flush(runnable);
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onExtraCommand(int i, int i2, String str, Bundle bundle) {
        Preconditions.checkState(this.mInitialized);
        this.mDelegate.getController().sendExtraCommand(i, i2, str, bundle);
    }
}
