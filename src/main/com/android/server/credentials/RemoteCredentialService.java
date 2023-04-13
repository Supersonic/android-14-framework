package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.credentials.ClearCredentialStateException;
import android.credentials.CreateCredentialException;
import android.credentials.GetCredentialException;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.service.credentials.BeginCreateCredentialRequest;
import android.service.credentials.BeginCreateCredentialResponse;
import android.service.credentials.BeginGetCredentialRequest;
import android.service.credentials.BeginGetCredentialResponse;
import android.service.credentials.ClearCredentialStateRequest;
import android.service.credentials.IBeginCreateCredentialCallback;
import android.service.credentials.IBeginGetCredentialCallback;
import android.service.credentials.IClearCredentialStateCallback;
import android.service.credentials.ICredentialProviderService;
import android.util.Log;
import android.util.Slog;
import com.android.internal.infra.ServiceConnector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* loaded from: classes.dex */
public class RemoteCredentialService extends ServiceConnector.Impl<ICredentialProviderService> {
    private final ComponentName mComponentName;

    /* loaded from: classes.dex */
    public interface ProviderCallbacks<T> {
        void onProviderResponseFailure(int i, Exception exc);

        void onProviderResponseSuccess(T t);
    }

    public long getAutoDisconnectTimeoutMs() {
        return 5000L;
    }

    public RemoteCredentialService(Context context, ComponentName componentName, int i) {
        super(context, new Intent("android.service.credentials.CredentialProviderService").setComponent(componentName), 0, i, new Function() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ICredentialProviderService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mComponentName = componentName;
    }

    public ICancellationSignal onBeginGetCredential(final BeginGetCredentialRequest beginGetCredentialRequest, final ProviderCallbacks<BeginGetCredentialResponse> providerCallbacks) {
        Log.i("RemoteCredentialService", "In onGetCredentials in RemoteCredentialService");
        final AtomicReference atomicReference = new AtomicReference();
        postAsync(new ServiceConnector.Job() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda1
            public final Object run(Object obj) {
                CompletableFuture lambda$onBeginGetCredential$0;
                lambda$onBeginGetCredential$0 = RemoteCredentialService.this.lambda$onBeginGetCredential$0(beginGetCredentialRequest, atomicReference, (ICredentialProviderService) obj);
                return lambda$onBeginGetCredential$0;
            }
        }).orTimeout(5000L, TimeUnit.MILLISECONDS).whenComplete(new BiConsumer() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda2
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteCredentialService.this.lambda$onBeginGetCredential$2(atomicReference, providerCallbacks, (BeginGetCredentialResponse) obj, (Throwable) obj2);
            }
        });
        return (ICancellationSignal) atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onBeginGetCredential$0(BeginGetCredentialRequest beginGetCredentialRequest, AtomicReference atomicReference, ICredentialProviderService iCredentialProviderService) throws Exception {
        final CompletableFuture completableFuture = new CompletableFuture();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            atomicReference.set(iCredentialProviderService.onBeginGetCredential(beginGetCredentialRequest, new IBeginGetCredentialCallback.Stub() { // from class: com.android.server.credentials.RemoteCredentialService.1
                public void onSuccess(BeginGetCredentialResponse beginGetCredentialResponse) {
                    completableFuture.complete(beginGetCredentialResponse);
                }

                public void onFailure(String str, CharSequence charSequence) {
                    Log.i("RemoteCredentialService", "In onFailure in RemoteCredentialService");
                    completableFuture.completeExceptionally(new GetCredentialException(str, charSequence == null ? "" : String.valueOf(charSequence)));
                }
            }));
            return completableFuture;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBeginGetCredential$2(final AtomicReference atomicReference, final ProviderCallbacks providerCallbacks, final BeginGetCredentialResponse beginGetCredentialResponse, final Throwable th) {
        Handler.getMain().post(new Runnable() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                RemoteCredentialService.this.lambda$onBeginGetCredential$1(beginGetCredentialResponse, th, atomicReference, providerCallbacks);
            }
        });
    }

    public ICancellationSignal onCreateCredential(final BeginCreateCredentialRequest beginCreateCredentialRequest, final ProviderCallbacks<BeginCreateCredentialResponse> providerCallbacks) {
        Log.i("RemoteCredentialService", "In onCreateCredential in RemoteCredentialService");
        final AtomicReference atomicReference = new AtomicReference();
        postAsync(new ServiceConnector.Job() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda3
            public final Object run(Object obj) {
                CompletableFuture lambda$onCreateCredential$3;
                lambda$onCreateCredential$3 = RemoteCredentialService.this.lambda$onCreateCredential$3(beginCreateCredentialRequest, atomicReference, (ICredentialProviderService) obj);
                return lambda$onCreateCredential$3;
            }
        }).orTimeout(5000L, TimeUnit.MILLISECONDS).whenComplete(new BiConsumer() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteCredentialService.this.lambda$onCreateCredential$5(atomicReference, providerCallbacks, (BeginCreateCredentialResponse) obj, (Throwable) obj2);
            }
        });
        return (ICancellationSignal) atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onCreateCredential$3(BeginCreateCredentialRequest beginCreateCredentialRequest, AtomicReference atomicReference, ICredentialProviderService iCredentialProviderService) throws Exception {
        final CompletableFuture completableFuture = new CompletableFuture();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            atomicReference.set(iCredentialProviderService.onBeginCreateCredential(beginCreateCredentialRequest, new IBeginCreateCredentialCallback.Stub() { // from class: com.android.server.credentials.RemoteCredentialService.2
                public void onSuccess(BeginCreateCredentialResponse beginCreateCredentialResponse) {
                    Log.i("RemoteCredentialService", "In onSuccess onBeginCreateCredential in RemoteCredentialService");
                    completableFuture.complete(beginCreateCredentialResponse);
                }

                public void onFailure(String str, CharSequence charSequence) {
                    Log.i("RemoteCredentialService", "In onFailure in RemoteCredentialService");
                    completableFuture.completeExceptionally(new CreateCredentialException(str, charSequence == null ? "" : String.valueOf(charSequence)));
                }
            }));
            return completableFuture;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateCredential$5(final AtomicReference atomicReference, final ProviderCallbacks providerCallbacks, final BeginCreateCredentialResponse beginCreateCredentialResponse, final Throwable th) {
        Handler.getMain().post(new Runnable() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                RemoteCredentialService.this.lambda$onCreateCredential$4(beginCreateCredentialResponse, th, atomicReference, providerCallbacks);
            }
        });
    }

    public ICancellationSignal onClearCredentialState(final ClearCredentialStateRequest clearCredentialStateRequest, final ProviderCallbacks<Void> providerCallbacks) {
        Log.i("RemoteCredentialService", "In onClearCredentialState in RemoteCredentialService");
        final AtomicReference atomicReference = new AtomicReference();
        postAsync(new ServiceConnector.Job() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda5
            public final Object run(Object obj) {
                CompletableFuture lambda$onClearCredentialState$6;
                lambda$onClearCredentialState$6 = RemoteCredentialService.this.lambda$onClearCredentialState$6(clearCredentialStateRequest, atomicReference, (ICredentialProviderService) obj);
                return lambda$onClearCredentialState$6;
            }
        }).orTimeout(5000L, TimeUnit.MILLISECONDS).whenComplete(new BiConsumer() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda6
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteCredentialService.this.lambda$onClearCredentialState$8(atomicReference, providerCallbacks, (Void) obj, (Throwable) obj2);
            }
        });
        return (ICancellationSignal) atomicReference.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onClearCredentialState$6(ClearCredentialStateRequest clearCredentialStateRequest, AtomicReference atomicReference, ICredentialProviderService iCredentialProviderService) throws Exception {
        final CompletableFuture completableFuture = new CompletableFuture();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            atomicReference.set(iCredentialProviderService.onClearCredentialState(clearCredentialStateRequest, new IClearCredentialStateCallback.Stub() { // from class: com.android.server.credentials.RemoteCredentialService.3
                public void onSuccess() {
                    Log.i("RemoteCredentialService", "In onSuccess onClearCredentialState in RemoteCredentialService");
                    completableFuture.complete(null);
                }

                public void onFailure(String str, CharSequence charSequence) {
                    Log.i("RemoteCredentialService", "In onFailure in RemoteCredentialService");
                    completableFuture.completeExceptionally(new ClearCredentialStateException(str, charSequence == null ? "" : String.valueOf(charSequence)));
                }
            }));
            return completableFuture;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClearCredentialState$8(final AtomicReference atomicReference, final ProviderCallbacks providerCallbacks, final Void r11, final Throwable th) {
        Handler.getMain().post(new Runnable() { // from class: com.android.server.credentials.RemoteCredentialService$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                RemoteCredentialService.this.lambda$onClearCredentialState$7(r11, th, atomicReference, providerCallbacks);
            }
        });
    }

    /* renamed from: handleExecutionResponse */
    public final <T> void lambda$onCreateCredential$4(T t, Throwable th, AtomicReference<ICancellationSignal> atomicReference, ProviderCallbacks<T> providerCallbacks) {
        if (th == null) {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is null");
            providerCallbacks.onProviderResponseSuccess(t);
        } else if (th instanceof TimeoutException) {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is timeout");
            dispatchCancellationSignal(atomicReference.get());
            providerCallbacks.onProviderResponseFailure(1, null);
        } else if (th instanceof CancellationException) {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is cancellation");
            dispatchCancellationSignal(atomicReference.get());
            providerCallbacks.onProviderResponseFailure(2, null);
        } else if (th instanceof GetCredentialException) {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is provider geterror");
            providerCallbacks.onProviderResponseFailure(3, (GetCredentialException) th);
        } else if (th instanceof CreateCredentialException) {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is provider create error");
            providerCallbacks.onProviderResponseFailure(3, (CreateCredentialException) th);
        } else {
            Log.i("RemoteCredentialService", "In RemoteCredentialService execute error is unknown");
            providerCallbacks.onProviderResponseFailure(0, (Exception) th);
        }
    }

    public final void dispatchCancellationSignal(ICancellationSignal iCancellationSignal) {
        if (iCancellationSignal == null) {
            Slog.e("RemoteCredentialService", "Error dispatching a cancellation - Signal is null");
            return;
        }
        try {
            iCancellationSignal.cancel();
        } catch (RemoteException e) {
            Slog.e("RemoteCredentialService", "Error dispatching a cancellation", e);
        }
    }
}
