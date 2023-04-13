package com.android.server.tracing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IMessenger;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.tracing.ITracingServiceProxy;
import android.tracing.TraceReportParams;
import android.util.Log;
import android.util.LruCache;
import android.util.Slog;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.SystemService;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class TracingServiceProxy extends SystemService {
    public final LruCache<ComponentName, ServiceConnector<IMessenger>> mCachedReporterServices;
    public final Context mContext;
    public final PackageManager mPackageManager;
    public final ITracingServiceProxy.Stub mTracingServiceProxy;

    public TracingServiceProxy(Context context) {
        super(context);
        this.mTracingServiceProxy = new ITracingServiceProxy.Stub() { // from class: com.android.server.tracing.TracingServiceProxy.1
            public void notifyTraceSessionEnded(boolean z) {
                TracingServiceProxy.this.notifyTraceur(z);
            }

            public void reportTrace(TraceReportParams traceReportParams) {
                TracingServiceProxy.this.reportTrace(traceReportParams);
            }
        };
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mCachedReporterServices = new LruCache<>(8);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("tracing.proxy", this.mTracingServiceProxy);
    }

    public final void notifyTraceur(boolean z) {
        Intent intent = new Intent();
        try {
            intent.setClassName(this.mPackageManager.getPackageInfo("com.android.traceur", 1048576).packageName, "com.android.traceur.StopTraceService");
            if (z) {
                intent.setAction("com.android.traceur.NOTIFY_SESSION_STOLEN");
            } else {
                intent.setAction("com.android.traceur.NOTIFY_SESSION_STOPPED");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mContext.startForegroundServiceAsUser(intent, UserHandle.SYSTEM);
            } catch (RuntimeException e) {
                Log.e("TracingServiceProxy", "Failed to notifyTraceSessionEnded", e);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("TracingServiceProxy", "Failed to locate Traceur", e2);
        }
    }

    public final void reportTrace(TraceReportParams traceReportParams) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.TRACING_SERVICE_REPORT_EVENT, 1, traceReportParams.uuidLsb, traceReportParams.uuidMsb);
        ComponentName componentName = new ComponentName(traceReportParams.reporterPackageName, traceReportParams.reporterClassName);
        if (!hasBindServicePermission(componentName)) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.TRACING_SERVICE_REPORT_EVENT, 3, traceReportParams.uuidLsb, traceReportParams.uuidMsb);
            return;
        }
        boolean hasPermission = hasPermission(componentName, "android.permission.DUMP");
        boolean hasPermission2 = hasPermission(componentName, "android.permission.PACKAGE_USAGE_STATS");
        if (!hasPermission || !hasPermission2) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.TRACING_SERVICE_REPORT_EVENT, 4, traceReportParams.uuidLsb, traceReportParams.uuidMsb);
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            reportTrace(getOrCreateReporterService(componentName), traceReportParams);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void reportTrace(ServiceConnector<IMessenger> serviceConnector, final TraceReportParams traceReportParams) {
        serviceConnector.post(new ServiceConnector.VoidJob() { // from class: com.android.server.tracing.TracingServiceProxy$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                TracingServiceProxy.lambda$reportTrace$0(traceReportParams, (IMessenger) obj);
            }
        }).whenComplete(new BiConsumer() { // from class: com.android.server.tracing.TracingServiceProxy$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                TracingServiceProxy.lambda$reportTrace$1(traceReportParams, (Void) obj, (Throwable) obj2);
            }
        });
    }

    public static /* synthetic */ void lambda$reportTrace$0(TraceReportParams traceReportParams, IMessenger iMessenger) throws Exception {
        if (traceReportParams.usePipeForTesting) {
            ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(traceReportParams.fd);
            try {
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(createPipe[1]);
                byte[] readNBytes = autoCloseInputStream.readNBytes(1024);
                if (readNBytes.length == 1024) {
                    throw new IllegalArgumentException("Trace file too large when |usePipeForTesting| is set.");
                }
                autoCloseOutputStream.write(readNBytes);
                autoCloseOutputStream.close();
                autoCloseInputStream.close();
                traceReportParams.fd = createPipe[0];
            } catch (Throwable th) {
                try {
                    autoCloseInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        Message obtain = Message.obtain();
        obtain.what = 1;
        obtain.obj = traceReportParams;
        iMessenger.send(obtain);
        FrameworkStatsLog.write((int) FrameworkStatsLog.TRACING_SERVICE_REPORT_EVENT, 2, traceReportParams.uuidLsb, traceReportParams.uuidMsb);
    }

    public static /* synthetic */ void lambda$reportTrace$1(TraceReportParams traceReportParams, Void r7, Throwable th) {
        if (th != null) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.TRACING_SERVICE_REPORT_EVENT, 5, traceReportParams.uuidLsb, traceReportParams.uuidMsb);
            Slog.e("TracingServiceProxy", "Failed to report trace", th);
        }
        try {
            traceReportParams.fd.close();
        } catch (IOException unused) {
        }
    }

    public final ServiceConnector<IMessenger> getOrCreateReporterService(ComponentName componentName) {
        ServiceConnector<IMessenger> serviceConnector = this.mCachedReporterServices.get(componentName);
        if (serviceConnector == null) {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            Context context = this.mContext;
            ServiceConnector<IMessenger> serviceConnector2 = new ServiceConnector.Impl<IMessenger>(context, intent, 33, context.getUser().getIdentifier(), new Function() { // from class: com.android.server.tracing.TracingServiceProxy$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return IMessenger.Stub.asInterface((IBinder) obj);
                }
            }) { // from class: com.android.server.tracing.TracingServiceProxy.2
                public long getAutoDisconnectTimeoutMs() {
                    return 15000L;
                }

                public long getRequestTimeoutMs() {
                    return 10000L;
                }
            };
            this.mCachedReporterServices.put(intent.getComponent(), serviceConnector2);
            return serviceConnector2;
        }
        return serviceConnector;
    }

    public final boolean hasPermission(ComponentName componentName, String str) throws SecurityException {
        if (this.mPackageManager.checkPermission(str, componentName.getPackageName()) != 0) {
            Slog.e("TracingServiceProxy", "Trace reporting service " + componentName.toShortString() + " does not have " + str + " permission");
            return false;
        }
        return true;
    }

    public final boolean hasBindServicePermission(ComponentName componentName) {
        try {
            ServiceInfo serviceInfo = this.mPackageManager.getServiceInfo(componentName, 0);
            if ("android.permission.BIND_TRACE_REPORT_SERVICE".equals(serviceInfo.permission)) {
                return true;
            }
            Slog.e("TracingServiceProxy", "Trace reporting service " + componentName.toShortString() + " does not request android.permission.BIND_TRACE_REPORT_SERVICE permission; instead requests " + serviceInfo.permission);
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.e("TracingServiceProxy", "Trace reporting service " + componentName.toShortString() + " does not exist");
            return false;
        }
    }
}
