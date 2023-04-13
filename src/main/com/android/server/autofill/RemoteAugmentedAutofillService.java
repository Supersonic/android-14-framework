package com.android.server.autofill;

import android.app.AppGlobals;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.autofill.Dataset;
import android.service.autofill.augmented.IAugmentedAutofillService;
import android.service.autofill.augmented.IFillCallback;
import android.util.Pair;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutoFillManagerClient;
import android.view.inputmethod.InlineSuggestionsRequest;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.os.IResultReceiver;
import com.android.server.autofill.p007ui.InlineFillUi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class RemoteAugmentedAutofillService extends ServiceConnector.Impl<IAugmentedAutofillService> {
    public static final String TAG = RemoteAugmentedAutofillService.class.getSimpleName();
    private final RemoteAugmentedAutofillServiceCallbacks mCallbacks;
    private final ComponentName mComponentName;
    private final int mIdleUnbindTimeoutMs;
    private final int mRequestTimeoutMs;
    private final AutofillUriGrantsManager mUriGrantsManager;

    /* loaded from: classes.dex */
    public interface RemoteAugmentedAutofillServiceCallbacks extends AbstractRemoteService.VultureCallback<RemoteAugmentedAutofillService> {
        void logAugmentedAutofillAuthenticationSelected(int i, String str, Bundle bundle);

        void logAugmentedAutofillSelected(int i, String str, Bundle bundle);

        void logAugmentedAutofillShown(int i, Bundle bundle);

        void resetLastResponse();

        void setLastResponse(int i);
    }

    public RemoteAugmentedAutofillService(Context context, int i, ComponentName componentName, int i2, RemoteAugmentedAutofillServiceCallbacks remoteAugmentedAutofillServiceCallbacks, boolean z, boolean z2, int i3, int i4) {
        super(context, new Intent("android.service.autofill.augmented.AugmentedAutofillService").setComponent(componentName), z ? 4194304 : 0, i2, new Function() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IAugmentedAutofillService.Stub.asInterface((IBinder) obj);
            }
        });
        this.mIdleUnbindTimeoutMs = i3;
        this.mRequestTimeoutMs = i4;
        this.mComponentName = componentName;
        this.mCallbacks = remoteAugmentedAutofillServiceCallbacks;
        this.mUriGrantsManager = new AutofillUriGrantsManager(i);
        connect();
    }

    public static Pair<ServiceInfo, ComponentName> getComponentName(String str, int i, boolean z) {
        int i2 = !z ? 1048704 : 128;
        try {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(unflattenFromString, i2, i);
            if (serviceInfo == null) {
                String str2 = TAG;
                Slog.e(str2, "Bad service name for flags " + i2 + ": " + str);
                return null;
            }
            return new Pair<>(serviceInfo, unflattenFromString);
        } catch (Exception e) {
            String str3 = TAG;
            Slog.e(str3, "Error getting service info for '" + str + "': " + e);
            return null;
        }
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public AutofillUriGrantsManager getAutofillUriGrantsManager() {
        return this.mUriGrantsManager;
    }

    public void onServiceConnectionStatusChanged(IAugmentedAutofillService iAugmentedAutofillService, boolean z) {
        try {
            if (z) {
                iAugmentedAutofillService.onConnected(Helper.sDebug, Helper.sVerbose);
            } else {
                iAugmentedAutofillService.onDisconnected();
            }
        } catch (Exception e) {
            String str = TAG;
            Slog.w(str, "Exception calling onServiceConnectionStatusChanged(" + z + "): ", e);
        }
    }

    public long getAutoDisconnectTimeoutMs() {
        return this.mIdleUnbindTimeoutMs;
    }

    public void onRequestAutofillLocked(final int i, final IAutoFillManagerClient iAutoFillManagerClient, final int i2, final ComponentName componentName, final IBinder iBinder, final AutofillId autofillId, final AutofillValue autofillValue, final InlineSuggestionsRequest inlineSuggestionsRequest, final Function<InlineFillUi, Boolean> function, final Runnable runnable, final RemoteInlineSuggestionRenderService remoteInlineSuggestionRenderService, final int i3) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        final AtomicReference atomicReference = new AtomicReference();
        postAsync(new ServiceConnector.Job() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService$$ExternalSyntheticLambda3
            public final Object run(Object obj) {
                CompletableFuture lambda$onRequestAutofillLocked$0;
                lambda$onRequestAutofillLocked$0 = RemoteAugmentedAutofillService.this.lambda$onRequestAutofillLocked$0(iAutoFillManagerClient, i, i2, componentName, autofillId, autofillValue, elapsedRealtime, inlineSuggestionsRequest, function, runnable, remoteInlineSuggestionRenderService, i3, iBinder, atomicReference, (IAugmentedAutofillService) obj);
                return lambda$onRequestAutofillLocked$0;
            }
        }).orTimeout(this.mRequestTimeoutMs, TimeUnit.MILLISECONDS).whenComplete(new BiConsumer() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                RemoteAugmentedAutofillService.this.lambda$onRequestAutofillLocked$1(atomicReference, componentName, i, (Void) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$onRequestAutofillLocked$0(final IAutoFillManagerClient iAutoFillManagerClient, final int i, final int i2, final ComponentName componentName, final AutofillId autofillId, final AutofillValue autofillValue, final long j, final InlineSuggestionsRequest inlineSuggestionsRequest, final Function function, final Runnable runnable, final RemoteInlineSuggestionRenderService remoteInlineSuggestionRenderService, final int i3, final IBinder iBinder, final AtomicReference atomicReference, final IAugmentedAutofillService iAugmentedAutofillService) throws Exception {
        final AndroidFuture androidFuture = new AndroidFuture();
        iAutoFillManagerClient.getAugmentedAutofillClient(new IResultReceiver.Stub() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService.1
            public void send(int i4, Bundle bundle) throws RemoteException {
                iAugmentedAutofillService.onFillRequest(i, bundle.getBinder("android.view.autofill.extra.AUGMENTED_AUTOFILL_CLIENT"), i2, componentName, autofillId, autofillValue, j, inlineSuggestionsRequest, new IFillCallback.Stub() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService.1.1
                    public void onSuccess(List<Dataset> list, Bundle bundle2, boolean z) {
                        RemoteAugmentedAutofillService.this.mCallbacks.resetLastResponse();
                        C05321 c05321 = C05321.this;
                        RemoteAugmentedAutofillService.this.maybeRequestShowInlineSuggestions(i, inlineSuggestionsRequest, list, bundle2, autofillId, autofillValue, function, iAutoFillManagerClient, runnable, remoteInlineSuggestionRenderService, i3, componentName, iBinder);
                        if (z) {
                            return;
                        }
                        androidFuture.complete((Object) null);
                    }

                    public boolean isCompleted() {
                        return androidFuture.isDone() && !androidFuture.isCancelled();
                    }

                    public void onCancellable(ICancellationSignal iCancellationSignal) {
                        if (androidFuture.isCancelled()) {
                            RemoteAugmentedAutofillService.this.dispatchCancellation(iCancellationSignal);
                        } else {
                            atomicReference.set(iCancellationSignal);
                        }
                    }

                    public void cancel() {
                        androidFuture.cancel(true);
                    }
                });
            }
        });
        return androidFuture;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestAutofillLocked$1(AtomicReference atomicReference, ComponentName componentName, int i, Void r10, Throwable th) {
        if (th instanceof CancellationException) {
            dispatchCancellation((ICancellationSignal) atomicReference.get());
        } else if (!(th instanceof TimeoutException)) {
            if (th != null) {
                String str = TAG;
                Slog.e(str, "exception handling getAugmentedAutofillClient() for " + i + ": ", th);
            }
        } else {
            String str2 = TAG;
            Slog.w(str2, "PendingAutofillRequest timed out (" + this.mRequestTimeoutMs + "ms) for " + this);
            dispatchCancellation((ICancellationSignal) atomicReference.get());
            ComponentName componentName2 = this.mComponentName;
            if (componentName2 != null) {
                android.service.autofill.augmented.Helper.logResponse(15, componentName2.getPackageName(), componentName, i, this.mRequestTimeoutMs);
            }
        }
    }

    public void dispatchCancellation(final ICancellationSignal iCancellationSignal) {
        if (iCancellationSignal == null) {
            return;
        }
        Handler.getMain().post(new Runnable() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RemoteAugmentedAutofillService.lambda$dispatchCancellation$2(iCancellationSignal);
            }
        });
    }

    public static /* synthetic */ void lambda$dispatchCancellation$2(ICancellationSignal iCancellationSignal) {
        try {
            iCancellationSignal.cancel();
        } catch (RemoteException e) {
            Slog.e(TAG, "Error requesting a cancellation", e);
        }
    }

    public final void maybeRequestShowInlineSuggestions(final int i, InlineSuggestionsRequest inlineSuggestionsRequest, List<Dataset> list, final Bundle bundle, final AutofillId autofillId, AutofillValue autofillValue, final Function<InlineFillUi, Boolean> function, final IAutoFillManagerClient iAutoFillManagerClient, final Runnable runnable, RemoteInlineSuggestionRenderService remoteInlineSuggestionRenderService, final int i2, final ComponentName componentName, final IBinder iBinder) {
        if (list == null || list.isEmpty() || function == null || inlineSuggestionsRequest == null || remoteInlineSuggestionRenderService == null) {
            if (function == null || inlineSuggestionsRequest == null) {
                return;
            }
            function.apply(InlineFillUi.emptyUi(autofillId));
            return;
        }
        this.mCallbacks.setLastResponse(i);
        if (function.apply(InlineFillUi.forAugmentedAutofill(new InlineFillUi.InlineFillUiInfo(inlineSuggestionsRequest, autofillId, (autofillValue == null || !autofillValue.isText()) ? null : autofillValue.getTextValue().toString(), remoteInlineSuggestionRenderService, i2, i), list, new InlineFillUi.InlineSuggestionUiCallback() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService.2
            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void onInflate() {
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void autofill(Dataset dataset, int i3) {
                boolean z = true;
                if (dataset.getAuthentication() != null) {
                    RemoteAugmentedAutofillService.this.mCallbacks.logAugmentedAutofillAuthenticationSelected(i, dataset.getId(), bundle);
                    IntentSender authentication = dataset.getAuthentication();
                    int makeAuthenticationId = AutofillManager.makeAuthenticationId(1, i3);
                    Intent intent = new Intent();
                    intent.putExtra("android.view.autofill.extra.CLIENT_STATE", bundle);
                    try {
                        iAutoFillManagerClient.authenticate(i, makeAuthenticationId, authentication, intent, false);
                        return;
                    } catch (RemoteException unused) {
                        Slog.w(RemoteAugmentedAutofillService.TAG, "Error starting auth flow");
                        function.apply(InlineFillUi.emptyUi(autofillId));
                        return;
                    }
                }
                RemoteAugmentedAutofillService.this.mCallbacks.logAugmentedAutofillSelected(i, dataset.getId(), bundle);
                try {
                    ArrayList fieldIds = dataset.getFieldIds();
                    ClipData fieldContent = dataset.getFieldContent();
                    if (fieldContent != null) {
                        RemoteAugmentedAutofillService.this.mUriGrantsManager.grantUriPermissions(componentName, iBinder, i2, fieldContent);
                        AutofillId autofillId2 = (AutofillId) fieldIds.get(0);
                        if (Helper.sDebug) {
                            Slog.d(RemoteAugmentedAutofillService.TAG, "Calling client autofillContent(): id=" + autofillId2 + ", content=" + fieldContent);
                        }
                        iAutoFillManagerClient.autofillContent(i, autofillId2, fieldContent);
                    } else {
                        if (fieldIds.size() != 1 || !((AutofillId) fieldIds.get(0)).equals(autofillId)) {
                            z = false;
                        }
                        if (Helper.sDebug) {
                            Slog.d(RemoteAugmentedAutofillService.TAG, "Calling client autofill(): ids=" + fieldIds + ", values=" + dataset.getFieldValues());
                        }
                        iAutoFillManagerClient.autofill(i, fieldIds, dataset.getFieldValues(), z);
                    }
                    function.apply(InlineFillUi.emptyUi(autofillId));
                } catch (RemoteException unused2) {
                    Slog.w(RemoteAugmentedAutofillService.TAG, "Encounter exception autofilling the values");
                }
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void authenticate(int i3, int i4) {
                Slog.e(RemoteAugmentedAutofillService.TAG, "authenticate not implemented for augmented autofill");
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void startIntentSender(IntentSender intentSender) {
                try {
                    iAutoFillManagerClient.startIntentSender(intentSender, new Intent());
                } catch (RemoteException unused) {
                    Slog.w(RemoteAugmentedAutofillService.TAG, "RemoteException starting intent sender");
                }
            }

            @Override // com.android.server.autofill.p007ui.InlineFillUi.InlineSuggestionUiCallback
            public void onError() {
                runnable.run();
            }
        })).booleanValue()) {
            this.mCallbacks.logAugmentedAutofillShown(i, bundle);
        }
    }

    public String toString() {
        return "RemoteAugmentedAutofillService[" + ComponentName.flattenToShortString(this.mComponentName) + "]";
    }

    public void onDestroyAutofillWindowsRequest() {
        run(new ServiceConnector.VoidJob() { // from class: com.android.server.autofill.RemoteAugmentedAutofillService$$ExternalSyntheticLambda2
            public final void runNoResult(Object obj) {
                ((IAugmentedAutofillService) obj).onDestroyAllFillWindowsRequest();
            }
        });
    }
}
