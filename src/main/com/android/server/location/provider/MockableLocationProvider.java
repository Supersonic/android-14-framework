package com.android.server.location.provider;

import android.location.Location;
import android.location.LocationResult;
import android.location.provider.ProviderRequest;
import android.os.Bundle;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.Preconditions;
import com.android.server.location.provider.AbstractLocationProvider;
import com.android.server.location.provider.MockableLocationProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.function.UnaryOperator;
/* loaded from: classes.dex */
public class MockableLocationProvider extends AbstractLocationProvider {
    @GuardedBy({"mOwnerLock"})
    public MockLocationProvider mMockProvider;
    public final Object mOwnerLock;
    @GuardedBy({"mOwnerLock"})
    public AbstractLocationProvider mProvider;
    @GuardedBy({"mOwnerLock"})
    public AbstractLocationProvider mRealProvider;
    @GuardedBy({"mOwnerLock"})
    public ProviderRequest mRequest;
    @GuardedBy({"mOwnerLock"})
    public boolean mStarted;

    public static /* synthetic */ AbstractLocationProvider.State lambda$setProviderLocked$0(AbstractLocationProvider.State state, AbstractLocationProvider.State state2) {
        return state;
    }

    public MockableLocationProvider(Object obj) {
        super(ConcurrentUtils.DIRECT_EXECUTOR, null, null, Collections.emptySet());
        this.mOwnerLock = obj;
        this.mRequest = ProviderRequest.EMPTY_REQUEST;
    }

    public AbstractLocationProvider getProvider() {
        AbstractLocationProvider abstractLocationProvider;
        synchronized (this.mOwnerLock) {
            abstractLocationProvider = this.mProvider;
        }
        return abstractLocationProvider;
    }

    public void setRealProvider(AbstractLocationProvider abstractLocationProvider) {
        synchronized (this.mOwnerLock) {
            if (this.mRealProvider == abstractLocationProvider) {
                return;
            }
            this.mRealProvider = abstractLocationProvider;
            if (!isMock()) {
                setProviderLocked(this.mRealProvider);
            }
        }
    }

    public void setMockProvider(MockLocationProvider mockLocationProvider) {
        synchronized (this.mOwnerLock) {
            if (this.mMockProvider == mockLocationProvider) {
                return;
            }
            this.mMockProvider = mockLocationProvider;
            if (mockLocationProvider != null) {
                setProviderLocked(mockLocationProvider);
            } else {
                setProviderLocked(this.mRealProvider);
            }
        }
    }

    @GuardedBy({"mOwnerLock"})
    public final void setProviderLocked(AbstractLocationProvider abstractLocationProvider) {
        final AbstractLocationProvider.State state;
        AbstractLocationProvider abstractLocationProvider2 = this.mProvider;
        if (abstractLocationProvider2 == abstractLocationProvider) {
            return;
        }
        this.mProvider = abstractLocationProvider;
        if (abstractLocationProvider2 != null) {
            abstractLocationProvider2.getController().setListener(null);
            if (abstractLocationProvider2.getController().isStarted()) {
                abstractLocationProvider2.getController().setRequest(ProviderRequest.EMPTY_REQUEST);
                abstractLocationProvider2.getController().stop();
            }
        }
        AbstractLocationProvider abstractLocationProvider3 = this.mProvider;
        if (abstractLocationProvider3 != null) {
            state = abstractLocationProvider3.getController().setListener(new ListenerWrapper(this.mProvider));
            if (this.mStarted) {
                if (!this.mProvider.getController().isStarted()) {
                    this.mProvider.getController().start();
                }
                this.mProvider.getController().setRequest(this.mRequest);
            } else if (this.mProvider.getController().isStarted()) {
                this.mProvider.getController().setRequest(ProviderRequest.EMPTY_REQUEST);
                this.mProvider.getController().stop();
            }
        } else {
            state = AbstractLocationProvider.State.EMPTY_STATE;
        }
        setState(new UnaryOperator() { // from class: com.android.server.location.provider.MockableLocationProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                AbstractLocationProvider.State lambda$setProviderLocked$0;
                lambda$setProviderLocked$0 = MockableLocationProvider.lambda$setProviderLocked$0(AbstractLocationProvider.State.this, (AbstractLocationProvider.State) obj);
                return lambda$setProviderLocked$0;
            }
        });
    }

    public boolean isMock() {
        boolean z;
        synchronized (this.mOwnerLock) {
            MockLocationProvider mockLocationProvider = this.mMockProvider;
            z = mockLocationProvider != null && this.mProvider == mockLocationProvider;
        }
        return z;
    }

    public void setMockProviderAllowed(boolean z) {
        synchronized (this.mOwnerLock) {
            Preconditions.checkState(isMock());
            this.mMockProvider.setProviderAllowed(z);
        }
    }

    public void setMockProviderLocation(Location location) {
        synchronized (this.mOwnerLock) {
            Preconditions.checkState(isMock());
            this.mMockProvider.setProviderLocation(location);
        }
    }

    public ProviderRequest getCurrentRequest() {
        ProviderRequest providerRequest;
        synchronized (this.mOwnerLock) {
            providerRequest = this.mRequest;
        }
        return providerRequest;
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onStart() {
        synchronized (this.mOwnerLock) {
            Preconditions.checkState(!this.mStarted);
            this.mStarted = true;
            AbstractLocationProvider abstractLocationProvider = this.mProvider;
            if (abstractLocationProvider != null) {
                abstractLocationProvider.getController().start();
                if (!this.mRequest.equals(ProviderRequest.EMPTY_REQUEST)) {
                    this.mProvider.getController().setRequest(this.mRequest);
                }
            }
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onStop() {
        synchronized (this.mOwnerLock) {
            Preconditions.checkState(this.mStarted);
            this.mStarted = false;
            if (this.mProvider != null) {
                if (!this.mRequest.equals(ProviderRequest.EMPTY_REQUEST)) {
                    this.mProvider.getController().setRequest(ProviderRequest.EMPTY_REQUEST);
                }
                this.mProvider.getController().stop();
            }
            this.mRequest = ProviderRequest.EMPTY_REQUEST;
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onSetRequest(ProviderRequest providerRequest) {
        synchronized (this.mOwnerLock) {
            if (providerRequest == this.mRequest) {
                return;
            }
            this.mRequest = providerRequest;
            AbstractLocationProvider abstractLocationProvider = this.mProvider;
            if (abstractLocationProvider != null) {
                abstractLocationProvider.getController().setRequest(providerRequest);
            }
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onFlush(Runnable runnable) {
        synchronized (this.mOwnerLock) {
            AbstractLocationProvider abstractLocationProvider = this.mProvider;
            if (abstractLocationProvider != null) {
                abstractLocationProvider.getController().flush(runnable);
            } else {
                runnable.run();
            }
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onExtraCommand(int i, int i2, String str, Bundle bundle) {
        synchronized (this.mOwnerLock) {
            AbstractLocationProvider abstractLocationProvider = this.mProvider;
            if (abstractLocationProvider != null) {
                abstractLocationProvider.getController().sendExtraCommand(i, i2, str, bundle);
            }
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AbstractLocationProvider abstractLocationProvider;
        AbstractLocationProvider.State state;
        Preconditions.checkState(!Thread.holdsLock(this.mOwnerLock));
        synchronized (this.mOwnerLock) {
            abstractLocationProvider = this.mProvider;
            state = getState();
        }
        printWriter.println("allowed=" + state.allowed);
        if (state.identity != null) {
            printWriter.println("identity=" + state.identity);
        }
        if (!state.extraAttributionTags.isEmpty()) {
            printWriter.println("extra attribution tags=" + state.extraAttributionTags);
        }
        if (state.properties != null) {
            printWriter.println("properties=" + state.properties);
        }
        if (abstractLocationProvider != null) {
            abstractLocationProvider.dump(fileDescriptor, printWriter, strArr);
        }
    }

    /* loaded from: classes.dex */
    public class ListenerWrapper implements AbstractLocationProvider.Listener {
        public final AbstractLocationProvider mListenerProvider;

        public static /* synthetic */ AbstractLocationProvider.State lambda$onStateChanged$0(AbstractLocationProvider.State state, AbstractLocationProvider.State state2) {
            return state;
        }

        public ListenerWrapper(AbstractLocationProvider abstractLocationProvider) {
            this.mListenerProvider = abstractLocationProvider;
        }

        @Override // com.android.server.location.provider.AbstractLocationProvider.Listener
        public final void onStateChanged(AbstractLocationProvider.State state, final AbstractLocationProvider.State state2) {
            synchronized (MockableLocationProvider.this.mOwnerLock) {
                if (this.mListenerProvider != MockableLocationProvider.this.mProvider) {
                    return;
                }
                MockableLocationProvider.this.setState(new UnaryOperator() { // from class: com.android.server.location.provider.MockableLocationProvider$ListenerWrapper$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        AbstractLocationProvider.State lambda$onStateChanged$0;
                        lambda$onStateChanged$0 = MockableLocationProvider.ListenerWrapper.lambda$onStateChanged$0(AbstractLocationProvider.State.this, (AbstractLocationProvider.State) obj);
                        return lambda$onStateChanged$0;
                    }
                });
            }
        }

        @Override // com.android.server.location.provider.AbstractLocationProvider.Listener
        public final void onReportLocation(LocationResult locationResult) {
            synchronized (MockableLocationProvider.this.mOwnerLock) {
                if (this.mListenerProvider != MockableLocationProvider.this.mProvider) {
                    return;
                }
                MockableLocationProvider.this.reportLocation(locationResult);
            }
        }
    }
}
