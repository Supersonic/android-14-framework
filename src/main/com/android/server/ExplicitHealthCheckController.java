package com.android.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.watchdog.ExplicitHealthCheckService;
import android.service.watchdog.IExplicitHealthCheckService;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class ExplicitHealthCheckController {
    @GuardedBy({"mLock"})
    public ServiceConnection mConnection;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public boolean mEnabled;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public Runnable mNotifySyncRunnable;
    @GuardedBy({"mLock"})
    public Consumer<String> mPassedConsumer;
    @GuardedBy({"mLock"})
    public IExplicitHealthCheckService mRemoteService;
    @GuardedBy({"mLock"})
    public Consumer<List<ExplicitHealthCheckService.PackageConfig>> mSupportedConsumer;

    public ExplicitHealthCheckController(Context context) {
        this.mContext = context;
    }

    public void setEnabled(boolean z) {
        synchronized (this.mLock) {
            StringBuilder sb = new StringBuilder();
            sb.append("Explicit health checks ");
            sb.append(z ? "enabled." : "disabled.");
            Slog.i("ExplicitHealthCheckController", sb.toString());
            this.mEnabled = z;
        }
    }

    public void setCallbacks(Consumer<String> consumer, Consumer<List<ExplicitHealthCheckService.PackageConfig>> consumer2, Runnable runnable) {
        synchronized (this.mLock) {
            if (this.mPassedConsumer != null || this.mSupportedConsumer != null || this.mNotifySyncRunnable != null) {
                Slog.wtf("ExplicitHealthCheckController", "Resetting health check controller callbacks");
            }
            Objects.requireNonNull(consumer);
            this.mPassedConsumer = consumer;
            Objects.requireNonNull(consumer2);
            this.mSupportedConsumer = consumer2;
            Objects.requireNonNull(runnable);
            this.mNotifySyncRunnable = runnable;
        }
    }

    public void syncRequests(final Set<String> set) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEnabled;
        }
        if (!z) {
            Slog.i("ExplicitHealthCheckController", "Health checks disabled, no supported packages");
            this.mSupportedConsumer.accept(Collections.emptyList());
            return;
        }
        getSupportedPackages(new Consumer() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ExplicitHealthCheckController.this.lambda$syncRequests$3(set, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$syncRequests$3(final Set set, final List list) {
        this.mSupportedConsumer.accept(list);
        getRequestedPackages(new Consumer() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ExplicitHealthCheckController.this.lambda$syncRequests$2(list, set, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$syncRequests$2(List list, Set set, List list2) {
        synchronized (this.mLock) {
            ArraySet arraySet = new ArraySet();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                arraySet.add(((ExplicitHealthCheckService.PackageConfig) it.next()).getPackageName());
            }
            set.retainAll(arraySet);
            actOnDifference(list2, set, new Consumer() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ExplicitHealthCheckController.this.lambda$syncRequests$0((String) obj);
                }
            });
            actOnDifference(set, list2, new Consumer() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ExplicitHealthCheckController.this.lambda$syncRequests$1((String) obj);
                }
            });
            if (set.isEmpty()) {
                Slog.i("ExplicitHealthCheckController", "No more health check requests, unbinding...");
                unbindService();
            }
        }
    }

    public final void actOnDifference(Collection<String> collection, Collection<String> collection2, Consumer<String> consumer) {
        for (String str : collection) {
            if (!collection2.contains(str)) {
                consumer.accept(str);
            }
        }
    }

    /* renamed from: request */
    public final void lambda$syncRequests$1(String str) {
        synchronized (this.mLock) {
            if (prepareServiceLocked("request health check for " + str)) {
                Slog.i("ExplicitHealthCheckController", "Requesting health check for package " + str);
                try {
                    this.mRemoteService.request(str);
                } catch (RemoteException e) {
                    Slog.w("ExplicitHealthCheckController", "Failed to request health check for package " + str, e);
                }
            }
        }
    }

    /* renamed from: cancel */
    public final void lambda$syncRequests$0(String str) {
        synchronized (this.mLock) {
            if (prepareServiceLocked("cancel health check for " + str)) {
                Slog.i("ExplicitHealthCheckController", "Cancelling health check for package " + str);
                try {
                    this.mRemoteService.cancel(str);
                } catch (RemoteException e) {
                    Slog.w("ExplicitHealthCheckController", "Failed to cancel health check for package " + str, e);
                }
            }
        }
    }

    public final void getSupportedPackages(final Consumer<List<ExplicitHealthCheckService.PackageConfig>> consumer) {
        synchronized (this.mLock) {
            if (prepareServiceLocked("get health check supported packages")) {
                Slog.d("ExplicitHealthCheckController", "Getting health check supported packages");
                try {
                    this.mRemoteService.getSupportedPackages(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda1
                        public final void onResult(Bundle bundle) {
                            ExplicitHealthCheckController.lambda$getSupportedPackages$4(consumer, bundle);
                        }
                    }));
                } catch (RemoteException e) {
                    Slog.w("ExplicitHealthCheckController", "Failed to get health check supported packages", e);
                }
            }
        }
    }

    public static /* synthetic */ void lambda$getSupportedPackages$4(Consumer consumer, Bundle bundle) {
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("android.service.watchdog.extra.supported_packages", ExplicitHealthCheckService.PackageConfig.class);
        Slog.i("ExplicitHealthCheckController", "Explicit health check supported packages " + parcelableArrayList);
        consumer.accept(parcelableArrayList);
    }

    public final void getRequestedPackages(final Consumer<List<String>> consumer) {
        synchronized (this.mLock) {
            if (prepareServiceLocked("get health check requested packages")) {
                Slog.d("ExplicitHealthCheckController", "Getting health check requested packages");
                try {
                    this.mRemoteService.getRequestedPackages(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda3
                        public final void onResult(Bundle bundle) {
                            ExplicitHealthCheckController.lambda$getRequestedPackages$5(consumer, bundle);
                        }
                    }));
                } catch (RemoteException e) {
                    Slog.w("ExplicitHealthCheckController", "Failed to get health check requested packages", e);
                }
            }
        }
    }

    public static /* synthetic */ void lambda$getRequestedPackages$5(Consumer consumer, Bundle bundle) {
        ArrayList<String> stringArrayList = bundle.getStringArrayList("android.service.watchdog.extra.requested_packages");
        Slog.i("ExplicitHealthCheckController", "Explicit health check requested packages " + stringArrayList);
        consumer.accept(stringArrayList);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0044, code lost:
        android.util.Slog.i("ExplicitHealthCheckController", "Not binding to service, service disabled");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void bindService() {
        synchronized (this.mLock) {
            boolean z = this.mEnabled;
            if (z && this.mConnection == null && this.mRemoteService == null) {
                ComponentName serviceComponentNameLocked = getServiceComponentNameLocked();
                if (serviceComponentNameLocked == null) {
                    Slog.wtf("ExplicitHealthCheckController", "Explicit health check service not found");
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(serviceComponentNameLocked);
                ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.android.server.ExplicitHealthCheckController.1
                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        Slog.i("ExplicitHealthCheckController", "Explicit health check service is connected " + componentName);
                        ExplicitHealthCheckController.this.initState(iBinder);
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName componentName) {
                        Slog.i("ExplicitHealthCheckController", "Explicit health check service is disconnected " + componentName);
                        synchronized (ExplicitHealthCheckController.this.mLock) {
                            ExplicitHealthCheckController.this.mRemoteService = null;
                        }
                    }

                    @Override // android.content.ServiceConnection
                    public void onBindingDied(ComponentName componentName) {
                        Slog.i("ExplicitHealthCheckController", "Explicit health check service binding is dead. Rebind: " + componentName);
                        ExplicitHealthCheckController.this.unbindService();
                        ExplicitHealthCheckController.this.bindService();
                    }

                    @Override // android.content.ServiceConnection
                    public void onNullBinding(ComponentName componentName) {
                        Slog.wtf("ExplicitHealthCheckController", "Explicit health check service binding is null?? " + componentName);
                    }
                };
                this.mConnection = serviceConnection;
                this.mContext.bindServiceAsUser(intent, serviceConnection, 1, UserHandle.of(0));
                Slog.i("ExplicitHealthCheckController", "Explicit health check service is bound");
                return;
            }
            if (this.mRemoteService != null) {
                Slog.i("ExplicitHealthCheckController", "Not binding to service, service already connected");
            } else {
                Slog.i("ExplicitHealthCheckController", "Not binding to service, service already connecting");
            }
        }
    }

    public final void unbindService() {
        synchronized (this.mLock) {
            if (this.mRemoteService != null) {
                this.mContext.unbindService(this.mConnection);
                this.mRemoteService = null;
                this.mConnection = null;
            }
            Slog.i("ExplicitHealthCheckController", "Explicit health check service is unbound");
        }
    }

    @GuardedBy({"mLock"})
    public final ServiceInfo getServiceInfoLocked() {
        ServiceInfo serviceInfo;
        String servicesSystemSharedLibraryPackageName = this.mContext.getPackageManager().getServicesSystemSharedLibraryPackageName();
        if (servicesSystemSharedLibraryPackageName == null) {
            Slog.w("ExplicitHealthCheckController", "no external services package!");
            return null;
        }
        Intent intent = new Intent("android.service.watchdog.ExplicitHealthCheckService");
        intent.setPackage(servicesSystemSharedLibraryPackageName);
        ResolveInfo resolveService = this.mContext.getPackageManager().resolveService(intent, 132);
        if (resolveService == null || (serviceInfo = resolveService.serviceInfo) == null) {
            Slog.w("ExplicitHealthCheckController", "No valid components found.");
            return null;
        }
        return serviceInfo;
    }

    @GuardedBy({"mLock"})
    public final ComponentName getServiceComponentNameLocked() {
        ServiceInfo serviceInfoLocked = getServiceInfoLocked();
        if (serviceInfoLocked == null) {
            return null;
        }
        ComponentName componentName = new ComponentName(serviceInfoLocked.packageName, serviceInfoLocked.name);
        if ("android.permission.BIND_EXPLICIT_HEALTH_CHECK_SERVICE".equals(serviceInfoLocked.permission)) {
            return componentName;
        }
        Slog.w("ExplicitHealthCheckController", componentName.flattenToShortString() + " does not require permission android.permission.BIND_EXPLICIT_HEALTH_CHECK_SERVICE");
        return null;
    }

    public final void initState(IBinder iBinder) {
        synchronized (this.mLock) {
            if (!this.mEnabled) {
                Slog.w("ExplicitHealthCheckController", "Attempting to connect disabled service?? Unbinding...");
                unbindService();
                return;
            }
            IExplicitHealthCheckService asInterface = IExplicitHealthCheckService.Stub.asInterface(iBinder);
            this.mRemoteService = asInterface;
            try {
                asInterface.setCallback(new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ExplicitHealthCheckController$$ExternalSyntheticLambda6
                    public final void onResult(Bundle bundle) {
                        ExplicitHealthCheckController.this.lambda$initState$6(bundle);
                    }
                }));
                Slog.i("ExplicitHealthCheckController", "Service initialized, syncing requests");
            } catch (RemoteException unused) {
                Slog.wtf("ExplicitHealthCheckController", "Could not setCallback on explicit health check service");
            }
            this.mNotifySyncRunnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initState$6(Bundle bundle) {
        String string = bundle.getString("android.service.watchdog.extra.health_check_passed_package");
        if (!TextUtils.isEmpty(string)) {
            Consumer<String> consumer = this.mPassedConsumer;
            if (consumer == null) {
                Slog.wtf("ExplicitHealthCheckController", "Health check passed for package " + string + "but no consumer registered.");
                return;
            }
            consumer.accept(string);
            return;
        }
        Slog.wtf("ExplicitHealthCheckController", "Empty package passed explicit health check?");
    }

    @GuardedBy({"mLock"})
    public final boolean prepareServiceLocked(String str) {
        if (this.mRemoteService == null || !this.mEnabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("Service not ready to ");
            sb.append(str);
            sb.append(this.mEnabled ? ". Binding..." : ". Disabled");
            Slog.i("ExplicitHealthCheckController", sb.toString());
            if (this.mEnabled) {
                bindService();
                return false;
            }
            return false;
        }
        return true;
    }
}
