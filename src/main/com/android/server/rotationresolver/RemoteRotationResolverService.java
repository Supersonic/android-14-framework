package com.android.server.rotationresolver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.rotationresolver.RotationResolverInternal;
import android.service.rotationresolver.IRotationResolverCallback;
import android.service.rotationresolver.IRotationResolverService;
import android.service.rotationresolver.RotationResolutionRequest;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.infra.ServiceConnector;
import com.android.server.rotationresolver.RemoteRotationResolverService;
import java.lang.ref.WeakReference;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class RemoteRotationResolverService extends ServiceConnector.Impl<IRotationResolverService> {
    public static final String TAG = RemoteRotationResolverService.class.getSimpleName();
    private final long mIdleUnbindTimeoutMs;

    public long getAutoDisconnectTimeoutMs() {
        return -1L;
    }

    public RemoteRotationResolverService(Context context, ComponentName componentName, int i, long j) {
        super(context, new Intent("android.service.rotationresolver.RotationResolverService").setComponent(componentName), 67112960, i, new Function() { // from class: com.android.server.rotationresolver.RemoteRotationResolverService$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IRotationResolverService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mIdleUnbindTimeoutMs = j;
        connect();
    }

    public void resolveRotation(final RotationRequest rotationRequest) {
        final RotationResolutionRequest rotationResolutionRequest = rotationRequest.mRemoteRequest;
        post(new ServiceConnector.VoidJob() { // from class: com.android.server.rotationresolver.RemoteRotationResolverService$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                RemoteRotationResolverService.lambda$resolveRotation$0(RemoteRotationResolverService.RotationRequest.this, rotationResolutionRequest, (IRotationResolverService) obj);
            }
        });
        getJobHandler().postDelayed(new Runnable() { // from class: com.android.server.rotationresolver.RemoteRotationResolverService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RemoteRotationResolverService.lambda$resolveRotation$1(RemoteRotationResolverService.RotationRequest.this);
            }
        }, rotationRequest.mRemoteRequest.getTimeoutMillis());
    }

    public static /* synthetic */ void lambda$resolveRotation$0(RotationRequest rotationRequest, RotationResolutionRequest rotationResolutionRequest, IRotationResolverService iRotationResolverService) throws Exception {
        iRotationResolverService.resolveRotation(rotationRequest.mIRotationResolverCallback, rotationResolutionRequest);
    }

    public static /* synthetic */ void lambda$resolveRotation$1(RotationRequest rotationRequest) {
        synchronized (rotationRequest.mLock) {
            if (!rotationRequest.mIsFulfilled) {
                rotationRequest.mCallbackInternal.onFailure(1);
                Slog.d(TAG, "Trying to cancel the remote request. Reason: Timed out.");
                rotationRequest.cancelInternal();
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static final class RotationRequest {
        public final RotationResolverInternal.RotationResolverCallbackInternal mCallbackInternal;
        @GuardedBy({"mLock"})
        public ICancellationSignal mCancellation;
        public final CancellationSignal mCancellationSignalInternal;
        public boolean mIsDispatched;
        @GuardedBy({"mLock"})
        public boolean mIsFulfilled;
        public final Object mLock;
        @VisibleForTesting
        final RotationResolutionRequest mRemoteRequest;
        public final IRotationResolverCallback mIRotationResolverCallback = new RotationResolverCallback(this);
        public final long mRequestStartTimeMillis = SystemClock.elapsedRealtime();

        public RotationRequest(RotationResolverInternal.RotationResolverCallbackInternal rotationResolverCallbackInternal, RotationResolutionRequest rotationResolutionRequest, CancellationSignal cancellationSignal, Object obj) {
            this.mCallbackInternal = rotationResolverCallbackInternal;
            this.mRemoteRequest = rotationResolutionRequest;
            this.mCancellationSignalInternal = cancellationSignal;
            this.mLock = obj;
        }

        public void cancelInternal() {
            Handler.getMain().post(new Runnable() { // from class: com.android.server.rotationresolver.RemoteRotationResolverService$RotationRequest$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteRotationResolverService.RotationRequest.this.lambda$cancelInternal$0();
                }
            });
            this.mCallbackInternal.onFailure(0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$cancelInternal$0() {
            synchronized (this.mLock) {
                if (this.mIsFulfilled) {
                    return;
                }
                this.mIsFulfilled = true;
                try {
                    ICancellationSignal iCancellationSignal = this.mCancellation;
                    if (iCancellationSignal != null) {
                        iCancellationSignal.cancel();
                        this.mCancellation = null;
                    }
                } catch (RemoteException unused) {
                    Slog.w(RemoteRotationResolverService.TAG, "Failed to cancel request in remote service.");
                }
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("is dispatched=" + this.mIsDispatched);
            indentingPrintWriter.println("is fulfilled:=" + this.mIsFulfilled);
            indentingPrintWriter.decreaseIndent();
        }

        /* loaded from: classes2.dex */
        public static class RotationResolverCallback extends IRotationResolverCallback.Stub {
            public final WeakReference<RotationRequest> mRequestWeakReference;

            public RotationResolverCallback(RotationRequest rotationRequest) {
                this.mRequestWeakReference = new WeakReference<>(rotationRequest);
            }

            public void onSuccess(int i) {
                RotationRequest rotationRequest = this.mRequestWeakReference.get();
                synchronized (rotationRequest.mLock) {
                    if (rotationRequest.mIsFulfilled) {
                        Slog.w(RemoteRotationResolverService.TAG, "Callback received after the rotation request is fulfilled.");
                        return;
                    }
                    rotationRequest.mIsFulfilled = true;
                    rotationRequest.mCallbackInternal.onSuccess(i);
                    long elapsedRealtime = SystemClock.elapsedRealtime() - rotationRequest.mRequestStartTimeMillis;
                    RotationResolverManagerService.logRotationStatsWithTimeToCalculate(rotationRequest.mRemoteRequest.getProposedRotation(), rotationRequest.mRemoteRequest.getCurrentRotation(), RotationResolverManagerService.surfaceRotationToProto(i), elapsedRealtime);
                    String str = RemoteRotationResolverService.TAG;
                    Slog.d(str, "onSuccess:" + i);
                    String str2 = RemoteRotationResolverService.TAG;
                    Slog.d(str2, "timeToCalculate:" + elapsedRealtime);
                }
            }

            public void onFailure(int i) {
                RotationRequest rotationRequest = this.mRequestWeakReference.get();
                synchronized (rotationRequest.mLock) {
                    if (rotationRequest.mIsFulfilled) {
                        Slog.w(RemoteRotationResolverService.TAG, "Callback received after the rotation request is fulfilled.");
                        return;
                    }
                    rotationRequest.mIsFulfilled = true;
                    rotationRequest.mCallbackInternal.onFailure(i);
                    long elapsedRealtime = SystemClock.elapsedRealtime() - rotationRequest.mRequestStartTimeMillis;
                    RotationResolverManagerService.logRotationStatsWithTimeToCalculate(rotationRequest.mRemoteRequest.getProposedRotation(), rotationRequest.mRemoteRequest.getCurrentRotation(), RotationResolverManagerService.errorCodeToProto(i), elapsedRealtime);
                    String str = RemoteRotationResolverService.TAG;
                    Slog.d(str, "onFailure:" + i);
                    String str2 = RemoteRotationResolverService.TAG;
                    Slog.d(str2, "timeToCalculate:" + elapsedRealtime);
                }
            }

            public void onCancellable(ICancellationSignal iCancellationSignal) {
                RotationRequest rotationRequest = this.mRequestWeakReference.get();
                synchronized (rotationRequest.mLock) {
                    rotationRequest.mCancellation = iCancellationSignal;
                    if (rotationRequest.mCancellationSignalInternal.isCanceled()) {
                        try {
                            iCancellationSignal.cancel();
                        } catch (RemoteException unused) {
                            Slog.w(RemoteRotationResolverService.TAG, "Failed to cancel the remote request.");
                        }
                    }
                }
            }
        }
    }
}
