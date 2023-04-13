package com.android.server.timezonedetector.location;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.timezone.ITimeZoneProvider;
import android.service.timezone.ITimeZoneProviderManager;
import android.service.timezone.TimeZoneProviderEvent;
import android.util.IndentingPrintWriter;
import com.android.internal.annotations.GuardedBy;
import com.android.server.servicewatcher.CurrentUserServiceSupplier;
import com.android.server.servicewatcher.ServiceWatcher;
import java.util.Objects;
/* loaded from: classes2.dex */
public class RealLocationTimeZoneProviderProxy extends LocationTimeZoneProviderProxy implements ServiceWatcher.ServiceListener<CurrentUserServiceSupplier.BoundServiceInfo> {
    @GuardedBy({"mSharedLock"})
    public ManagerProxy mManagerProxy;
    @GuardedBy({"mSharedLock"})
    public TimeZoneProviderRequest mRequest;
    public final ServiceWatcher mServiceWatcher;

    public RealLocationTimeZoneProviderProxy(Context context, Handler handler, ThreadingDomain threadingDomain, String str, String str2, boolean z) {
        super(context, threadingDomain);
        CurrentUserServiceSupplier create;
        this.mManagerProxy = null;
        this.mRequest = TimeZoneProviderRequest.createStopUpdatesRequest();
        Objects.requireNonNull(str2);
        if (z) {
            create = CurrentUserServiceSupplier.createUnsafeForTestsOnly(context, str, str2, "android.permission.BIND_TIME_ZONE_PROVIDER_SERVICE", null);
        } else {
            create = CurrentUserServiceSupplier.create(context, str, str2, "android.permission.BIND_TIME_ZONE_PROVIDER_SERVICE", "android.permission.INSTALL_LOCATION_TIME_ZONE_PROVIDER_SERVICE");
        }
        this.mServiceWatcher = ServiceWatcher.create(context, handler, "RealLocationTimeZoneProviderProxy", create, this);
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy
    public void onInitialize() {
        if (!register()) {
            throw new IllegalStateException("Unable to register binder proxy");
        }
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy
    public void onDestroy() {
        this.mServiceWatcher.unregister();
    }

    public final boolean register() {
        boolean checkServiceResolves = this.mServiceWatcher.checkServiceResolves();
        if (checkServiceResolves) {
            this.mServiceWatcher.register();
        }
        return checkServiceResolves;
    }

    @Override // com.android.server.servicewatcher.ServiceWatcher.ServiceListener
    public void onBind(IBinder iBinder, CurrentUserServiceSupplier.BoundServiceInfo boundServiceInfo) {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            this.mManagerProxy = new ManagerProxy();
            this.mListener.onProviderBound();
            trySendCurrentRequest();
        }
    }

    @Override // com.android.server.servicewatcher.ServiceWatcher.ServiceListener
    public void onUnbind() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            this.mManagerProxy = null;
            this.mListener.onProviderUnbound();
        }
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy
    public final void setRequest(TimeZoneProviderRequest timeZoneProviderRequest) {
        this.mThreadingDomain.assertCurrentThread();
        Objects.requireNonNull(timeZoneProviderRequest);
        synchronized (this.mSharedLock) {
            this.mRequest = timeZoneProviderRequest;
            trySendCurrentRequest();
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void trySendCurrentRequest() {
        final ManagerProxy managerProxy = this.mManagerProxy;
        final TimeZoneProviderRequest timeZoneProviderRequest = this.mRequest;
        this.mServiceWatcher.runOnBinder(new ServiceWatcher.BinderOperation() { // from class: com.android.server.timezonedetector.location.RealLocationTimeZoneProviderProxy$$ExternalSyntheticLambda0
            @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
            public final void run(IBinder iBinder) {
                RealLocationTimeZoneProviderProxy.lambda$trySendCurrentRequest$0(TimeZoneProviderRequest.this, managerProxy, iBinder);
            }
        });
    }

    public static /* synthetic */ void lambda$trySendCurrentRequest$0(TimeZoneProviderRequest timeZoneProviderRequest, ManagerProxy managerProxy, IBinder iBinder) throws RemoteException {
        ITimeZoneProvider asInterface = ITimeZoneProvider.Stub.asInterface(iBinder);
        if (timeZoneProviderRequest.sendUpdates()) {
            asInterface.startUpdates(managerProxy, timeZoneProviderRequest.getInitializationTimeout().toMillis(), timeZoneProviderRequest.getEventFilteringAgeThreshold().toMillis());
        } else {
            asInterface.stopUpdates();
        }
    }

    @Override // com.android.server.timezonedetector.Dumpable
    public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        synchronized (this.mSharedLock) {
            indentingPrintWriter.println("{RealLocationTimeZoneProviderProxy}");
            indentingPrintWriter.println("mRequest=" + this.mRequest);
            this.mServiceWatcher.dump(indentingPrintWriter);
        }
    }

    /* loaded from: classes2.dex */
    public class ManagerProxy extends ITimeZoneProviderManager.Stub {
        public ManagerProxy() {
        }

        public void onTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent) {
            synchronized (RealLocationTimeZoneProviderProxy.this.mSharedLock) {
                if (RealLocationTimeZoneProviderProxy.this.mManagerProxy != this) {
                    return;
                }
                RealLocationTimeZoneProviderProxy.this.handleTimeZoneProviderEvent(timeZoneProviderEvent);
            }
        }
    }
}
