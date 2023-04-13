package com.android.server.rotationresolver;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.rotationresolver.RotationResolverInternal;
import android.service.rotationresolver.RotationResolutionRequest;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.LatencyTracker;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.rotationresolver.RemoteRotationResolverService;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public final class RotationResolverManagerPerUserService extends AbstractPerUserSystemService<RotationResolverManagerPerUserService, RotationResolverManagerService> {
    public static final String TAG = "RotationResolverManagerPerUserService";
    public ComponentName mComponentName;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    RemoteRotationResolverService.RotationRequest mCurrentRequest;
    @GuardedBy({"mLock"})
    public LatencyTracker mLatencyTracker;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    RemoteRotationResolverService mRemoteService;

    public RotationResolverManagerPerUserService(RotationResolverManagerService rotationResolverManagerService, Object obj, int i) {
        super(rotationResolverManagerService, obj, i);
        this.mLatencyTracker = LatencyTracker.getInstance(getContext());
    }

    @GuardedBy({"mLock"})
    public void destroyLocked() {
        if (isVerbose()) {
            Slog.v(TAG, "destroyLocked()");
        }
        if (this.mCurrentRequest == null) {
            return;
        }
        Slog.d(TAG, "Trying to cancel the remote request. Reason: Service destroyed.");
        cancelLocked();
        RemoteRotationResolverService remoteRotationResolverService = this.mRemoteService;
        if (remoteRotationResolverService != null) {
            remoteRotationResolverService.unbind();
            this.mRemoteService = null;
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void resolveRotationLocked(final RotationResolverInternal.RotationResolverCallbackInternal rotationResolverCallbackInternal, RotationResolutionRequest rotationResolutionRequest, CancellationSignal cancellationSignal) {
        if (!isServiceAvailableLocked()) {
            Slog.w(TAG, "Service is not available at this moment.");
            rotationResolverCallbackInternal.onFailure(0);
            RotationResolverManagerService.logRotationStats(rotationResolutionRequest.getProposedRotation(), rotationResolutionRequest.getCurrentRotation(), 7);
            return;
        }
        ensureRemoteServiceInitiated();
        RemoteRotationResolverService.RotationRequest rotationRequest = this.mCurrentRequest;
        if (rotationRequest != null && !rotationRequest.mIsFulfilled) {
            cancelLocked();
        }
        synchronized (this.mLock) {
            this.mLatencyTracker.onActionStart(9);
        }
        this.mCurrentRequest = new RemoteRotationResolverService.RotationRequest(new RotationResolverInternal.RotationResolverCallbackInternal() { // from class: com.android.server.rotationresolver.RotationResolverManagerPerUserService.1
            public void onSuccess(int i) {
                synchronized (RotationResolverManagerPerUserService.this.mLock) {
                    RotationResolverManagerPerUserService.this.mLatencyTracker.onActionEnd(9);
                }
                rotationResolverCallbackInternal.onSuccess(i);
            }

            public void onFailure(int i) {
                synchronized (RotationResolverManagerPerUserService.this.mLock) {
                    RotationResolverManagerPerUserService.this.mLatencyTracker.onActionEnd(9);
                }
                rotationResolverCallbackInternal.onFailure(i);
            }
        }, rotationResolutionRequest, cancellationSignal, this.mLock);
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: com.android.server.rotationresolver.RotationResolverManagerPerUserService$$ExternalSyntheticLambda0
            @Override // android.os.CancellationSignal.OnCancelListener
            public final void onCancel() {
                RotationResolverManagerPerUserService.this.lambda$resolveRotationLocked$0();
            }
        });
        this.mRemoteService.resolveRotation(this.mCurrentRequest);
        this.mCurrentRequest.mIsDispatched = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resolveRotationLocked$0() {
        synchronized (this.mLock) {
            RemoteRotationResolverService.RotationRequest rotationRequest = this.mCurrentRequest;
            if (rotationRequest != null && !rotationRequest.mIsFulfilled) {
                Slog.d(TAG, "Trying to cancel the remote request. Reason: Client cancelled.");
                this.mCurrentRequest.cancelInternal();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void ensureRemoteServiceInitiated() {
        if (this.mRemoteService == null) {
            this.mRemoteService = new RemoteRotationResolverService(getContext(), this.mComponentName, getUserId(), 60000L);
        }
    }

    @VisibleForTesting
    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public boolean isServiceAvailableLocked() {
        if (this.mComponentName == null) {
            this.mComponentName = updateServiceInfoLocked();
        }
        return this.mComponentName != null;
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
            if (serviceInfo != null && !"android.permission.BIND_ROTATION_RESOLVER_SERVICE".equals(serviceInfo.permission)) {
                throw new SecurityException(String.format("Service %s requires %s permission. Found %s permission", serviceInfo.getComponentName(), "android.permission.BIND_ROTATION_RESOLVER_SERVICE", serviceInfo.permission));
            }
            return serviceInfo;
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @GuardedBy({"mLock"})
    public final void cancelLocked() {
        RemoteRotationResolverService.RotationRequest rotationRequest = this.mCurrentRequest;
        if (rotationRequest == null) {
            return;
        }
        rotationRequest.cancelInternal();
        this.mCurrentRequest = null;
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
        dumpInternal(new IndentingPrintWriter(printWriter, "  "));
    }

    public void dumpInternal(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            RemoteRotationResolverService remoteRotationResolverService = this.mRemoteService;
            if (remoteRotationResolverService != null) {
                remoteRotationResolverService.dump("", indentingPrintWriter);
            }
            RemoteRotationResolverService.RotationRequest rotationRequest = this.mCurrentRequest;
            if (rotationRequest != null) {
                rotationRequest.dump(indentingPrintWriter);
            }
        }
    }
}
