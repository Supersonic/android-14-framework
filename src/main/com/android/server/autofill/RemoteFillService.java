package com.android.server.autofill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.IAutoFillService;
import android.service.autofill.IFillCallback;
import android.service.autofill.ISaveCallback;
import android.service.autofill.SaveRequest;
import android.util.Slog;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.os.IResultReceiver;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RemoteFillService extends ServiceConnector.Impl<IAutoFillService> {
    private final FillServiceCallbacks mCallbacks;
    private final ComponentName mComponentName;
    private final Object mLock;
    private CompletableFuture<FillResponse> mPendingFillRequest;
    private int mPendingFillRequestId;

    /* loaded from: classes.dex */
    public interface FillServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteFillService> {
        void onFillRequestFailure(int i, CharSequence charSequence);

        void onFillRequestSuccess(int i, FillResponse fillResponse, String str, int i2);

        void onFillRequestTimeout(int i);

        void onSaveRequestFailure(CharSequence charSequence, String str);

        void onSaveRequestSuccess(String str, IntentSender intentSender);
    }

    public long getAutoDisconnectTimeoutMs() {
        return 5000L;
    }

    public RemoteFillService(Context context, ComponentName componentName, int i, FillServiceCallbacks fillServiceCallbacks, boolean z) {
        super(context, new Intent("android.service.autofill.AutofillService").setComponent(componentName), (z ? 4194304 : 0) | 1048576, i, new Function() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IAutoFillService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mLock = new Object();
        this.mPendingFillRequestId = Integer.MIN_VALUE;
        this.mCallbacks = fillServiceCallbacks;
        this.mComponentName = componentName;
    }

    public void onServiceConnectionStatusChanged(IAutoFillService iAutoFillService, boolean z) {
        try {
            iAutoFillService.onConnectedStateChanged(z);
        } catch (Exception e) {
            Slog.w("RemoteFillService", "Exception calling onConnectedStateChanged(" + z + "): " + e);
        }
    }

    public final void dispatchCancellationSignal(ICancellationSignal iCancellationSignal) {
        if (iCancellationSignal == null) {
            return;
        }
        try {
            iCancellationSignal.cancel();
        } catch (RemoteException e) {
            Slog.e("RemoteFillService", "Error requesting a cancellation", e);
        }
    }

    public void addLast(ServiceConnector.Job<IAutoFillService, ?> job) {
        cancelPendingJobs();
        super.addLast(job);
    }

    public int cancelCurrentRequest() {
        int i;
        synchronized (this.mLock) {
            CompletableFuture<FillResponse> completableFuture = this.mPendingFillRequest;
            i = (completableFuture == null || !completableFuture.cancel(false)) ? Integer.MIN_VALUE : this.mPendingFillRequestId;
        }
        return i;
    }

    public void onFillRequest(final FillRequest fillRequest) {
        if (Helper.sVerbose) {
            Slog.v("RemoteFillService", "onFillRequest:" + fillRequest);
        }
        final AtomicReference atomicReference = new AtomicReference();
        final AtomicReference atomicReference2 = new AtomicReference();
        AndroidFuture orTimeout = postAsync(new ServiceConnector.Job() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda2
            public final Object run(Object obj) {
                CompletableFuture lambda$onFillRequest$0;
                lambda$onFillRequest$0 = RemoteFillService.this.lambda$onFillRequest$0(fillRequest, atomicReference2, atomicReference, (IAutoFillService) obj);
                return lambda$onFillRequest$0;
            }
        }).orTimeout(5000L, TimeUnit.MILLISECONDS);
        atomicReference2.set(orTimeout);
        synchronized (this.mLock) {
            this.mPendingFillRequest = orTimeout;
            this.mPendingFillRequestId = fillRequest.getId();
        }
        orTimeout.whenComplete(new BiConsumer() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda3
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteFillService.this.lambda$onFillRequest$2(fillRequest, atomicReference, (FillResponse) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onFillRequest$0(FillRequest fillRequest, final AtomicReference atomicReference, final AtomicReference atomicReference2, IAutoFillService iAutoFillService) throws Exception {
        if (Helper.sVerbose) {
            Slog.v("RemoteFillService", "calling onFillRequest() for id=" + fillRequest.getId());
        }
        final CompletableFuture completableFuture = new CompletableFuture();
        iAutoFillService.onFillRequest(fillRequest, new IFillCallback.Stub() { // from class: com.android.server.autofill.RemoteFillService.1
            public void onCancellable(ICancellationSignal iCancellationSignal) {
                CompletableFuture completableFuture2 = (CompletableFuture) atomicReference.get();
                if (completableFuture2 != null && completableFuture2.isCancelled()) {
                    RemoteFillService.this.dispatchCancellationSignal(iCancellationSignal);
                } else {
                    atomicReference2.set(iCancellationSignal);
                }
            }

            public void onSuccess(FillResponse fillResponse) {
                completableFuture.complete(fillResponse);
            }

            public void onFailure(int i, CharSequence charSequence) {
                completableFuture.completeExceptionally(new RuntimeException(charSequence == null ? "" : String.valueOf(charSequence)));
            }
        });
        return completableFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFillRequest$2(final FillRequest fillRequest, final AtomicReference atomicReference, final FillResponse fillResponse, final Throwable th) {
        Handler.getMain().post(new Runnable() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFillService.this.lambda$onFillRequest$1(th, fillRequest, fillResponse, atomicReference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFillRequest$1(Throwable th, FillRequest fillRequest, FillResponse fillResponse, AtomicReference atomicReference) {
        synchronized (this.mLock) {
            this.mPendingFillRequest = null;
            this.mPendingFillRequestId = Integer.MIN_VALUE;
        }
        if (th == null) {
            this.mCallbacks.onFillRequestSuccess(fillRequest.getId(), fillResponse, this.mComponentName.getPackageName(), fillRequest.getFlags());
            return;
        }
        Slog.e("RemoteFillService", "Error calling on fill request", th);
        if (th instanceof TimeoutException) {
            dispatchCancellationSignal((ICancellationSignal) atomicReference.get());
            this.mCallbacks.onFillRequestTimeout(fillRequest.getId());
        } else if (th instanceof CancellationException) {
            dispatchCancellationSignal((ICancellationSignal) atomicReference.get());
        } else {
            this.mCallbacks.onFillRequestFailure(fillRequest.getId(), th.getMessage());
        }
    }

    public void onSaveRequest(final SaveRequest saveRequest) {
        postAsync(new ServiceConnector.Job() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda5
            public final Object run(Object obj) {
                CompletableFuture lambda$onSaveRequest$3;
                lambda$onSaveRequest$3 = RemoteFillService.this.lambda$onSaveRequest$3(saveRequest, (IAutoFillService) obj);
                return lambda$onSaveRequest$3;
            }
        }).orTimeout(5000L, TimeUnit.MILLISECONDS).whenComplete(new BiConsumer() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteFillService.this.lambda$onSaveRequest$5((IntentSender) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onSaveRequest$3(SaveRequest saveRequest, IAutoFillService iAutoFillService) throws Exception {
        if (Helper.sVerbose) {
            Slog.v("RemoteFillService", "calling onSaveRequest()");
        }
        final CompletableFuture completableFuture = new CompletableFuture();
        iAutoFillService.onSaveRequest(saveRequest, new ISaveCallback.Stub() { // from class: com.android.server.autofill.RemoteFillService.2
            public void onSuccess(IntentSender intentSender) {
                completableFuture.complete(intentSender);
            }

            public void onFailure(CharSequence charSequence) {
                completableFuture.completeExceptionally(new RuntimeException(String.valueOf(charSequence)));
            }
        });
        return completableFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSaveRequest$5(final IntentSender intentSender, final Throwable th) {
        Handler.getMain().post(new Runnable() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                RemoteFillService.this.lambda$onSaveRequest$4(th, intentSender);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSaveRequest$4(Throwable th, IntentSender intentSender) {
        if (th == null) {
            this.mCallbacks.onSaveRequestSuccess(this.mComponentName.getPackageName(), intentSender);
        } else {
            this.mCallbacks.onSaveRequestFailure(this.mComponentName.getPackageName(), th.getMessage());
        }
    }

    public void onSavedPasswordCountRequest(final IResultReceiver iResultReceiver) {
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.autofill.RemoteFillService$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                ((IAutoFillService) obj).onSavedPasswordCountRequest(iResultReceiver);
            }
        });
    }

    public void destroy() {
        unbind();
    }
}
