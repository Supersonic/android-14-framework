package com.android.server.autofill;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.service.autofill.Dataset;
import android.service.autofill.FillResponse;
import android.service.autofill.IFillCallback;
import android.service.autofill.SaveInfo;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.autofill.IAutoFillManagerClient;
import android.view.inputmethod.InlineSuggestionsRequest;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.AndroidFuture;
import com.android.server.autofill.RemoteFillService;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public final class ClientSuggestionsSession {
    public final RemoteFillService.FillServiceCallbacks mCallbacks;
    public final IAutoFillManagerClient mClient;
    public final ComponentName mComponentName;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public AndroidFuture<FillResponse> mPendingFillRequest;
    public final int mSessionId;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public int mPendingFillRequestId = Integer.MIN_VALUE;

    public ClientSuggestionsSession(int i, IAutoFillManagerClient iAutoFillManagerClient, Handler handler, ComponentName componentName, RemoteFillService.FillServiceCallbacks fillServiceCallbacks) {
        this.mSessionId = i;
        this.mClient = iAutoFillManagerClient;
        this.mHandler = handler;
        this.mComponentName = componentName;
        this.mCallbacks = fillServiceCallbacks;
    }

    public void onFillRequest(final int i, final InlineSuggestionsRequest inlineSuggestionsRequest, final int i2) {
        final AtomicReference atomicReference = new AtomicReference();
        final AtomicReference atomicReference2 = new AtomicReference();
        final AndroidFuture<FillResponse> androidFuture = new AndroidFuture<>();
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ClientSuggestionsSession$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ClientSuggestionsSession.this.lambda$onFillRequest$0(i, inlineSuggestionsRequest, androidFuture, atomicReference2, atomicReference);
            }
        });
        androidFuture.orTimeout(15000L, TimeUnit.MILLISECONDS);
        atomicReference2.set(androidFuture);
        synchronized (this.mLock) {
            this.mPendingFillRequest = androidFuture;
            this.mPendingFillRequestId = i;
        }
        androidFuture.whenComplete(new BiConsumer() { // from class: com.android.server.autofill.ClientSuggestionsSession$$ExternalSyntheticLambda1
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ClientSuggestionsSession.this.lambda$onFillRequest$2(i, i2, atomicReference, (FillResponse) obj, (Throwable) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFillRequest$0(int i, InlineSuggestionsRequest inlineSuggestionsRequest, AndroidFuture androidFuture, AtomicReference atomicReference, AtomicReference atomicReference2) {
        if (Helper.sVerbose) {
            Slog.v("ClientSuggestionsSession", "calling onFillRequest() for id=" + i);
        }
        try {
            this.mClient.requestFillFromClient(i, inlineSuggestionsRequest, new FillCallbackImpl(androidFuture, atomicReference, atomicReference2));
        } catch (RemoteException e) {
            androidFuture.completeExceptionally(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFillRequest$2(final int i, final int i2, final AtomicReference atomicReference, final FillResponse fillResponse, final Throwable th) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.autofill.ClientSuggestionsSession$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ClientSuggestionsSession.this.lambda$onFillRequest$1(th, fillResponse, i, i2, atomicReference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFillRequest$1(Throwable th, FillResponse fillResponse, int i, int i2, AtomicReference atomicReference) {
        synchronized (this.mLock) {
            this.mPendingFillRequest = null;
            this.mPendingFillRequestId = Integer.MIN_VALUE;
        }
        if (th == null) {
            processAutofillId(fillResponse);
            this.mCallbacks.onFillRequestSuccess(i, fillResponse, this.mComponentName.getPackageName(), i2);
            return;
        }
        Slog.e("ClientSuggestionsSession", "Error calling on  client fill request", th);
        if (th instanceof TimeoutException) {
            dispatchCancellationSignal((ICancellationSignal) atomicReference.get());
            this.mCallbacks.onFillRequestTimeout(i);
        } else if (th instanceof CancellationException) {
            dispatchCancellationSignal((ICancellationSignal) atomicReference.get());
        } else {
            this.mCallbacks.onFillRequestFailure(i, th.getMessage());
        }
    }

    public static ApplicationInfo getAppInfo(ComponentName componentName, int i) {
        try {
            ApplicationInfo applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(componentName.getPackageName(), 128L, i);
            if (applicationInfo != null) {
                return applicationInfo;
            }
            return null;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @GuardedBy({"mLock"})
    public static CharSequence getAppLabelLocked(Context context, ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return null;
        }
        return applicationInfo.loadSafeLabel(context.getPackageManager(), 0.0f, 5);
    }

    @GuardedBy({"mLock"})
    public static Drawable getAppIconLocked(Context context, ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return null;
        }
        return applicationInfo.loadIcon(context.getPackageManager());
    }

    public int cancelCurrentRequest() {
        int i;
        synchronized (this.mLock) {
            AndroidFuture<FillResponse> androidFuture = this.mPendingFillRequest;
            i = (androidFuture == null || !androidFuture.cancel(false)) ? Integer.MIN_VALUE : this.mPendingFillRequestId;
        }
        return i;
    }

    public final void processAutofillId(FillResponse fillResponse) {
        if (fillResponse == null) {
            return;
        }
        List datasets = fillResponse.getDatasets();
        if (datasets != null && !datasets.isEmpty()) {
            for (int i = 0; i < datasets.size(); i++) {
                Dataset dataset = (Dataset) datasets.get(i);
                if (dataset != null) {
                    applySessionId(dataset.getFieldIds());
                }
            }
        }
        SaveInfo saveInfo = fillResponse.getSaveInfo();
        if (saveInfo != null) {
            applySessionId(saveInfo.getOptionalIds());
            applySessionId(saveInfo.getRequiredIds());
            applySessionId(saveInfo.getSanitizerValues());
            applySessionId(saveInfo.getTriggerId());
        }
    }

    public final void applySessionId(List<AutofillId> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            applySessionId(list.get(i));
        }
    }

    public final void applySessionId(AutofillId[][] autofillIdArr) {
        if (autofillIdArr == null) {
            return;
        }
        for (AutofillId[] autofillIdArr2 : autofillIdArr) {
            applySessionId(autofillIdArr2);
        }
    }

    public final void applySessionId(AutofillId[] autofillIdArr) {
        if (autofillIdArr == null) {
            return;
        }
        for (AutofillId autofillId : autofillIdArr) {
            applySessionId(autofillId);
        }
    }

    public final void applySessionId(AutofillId autofillId) {
        if (autofillId == null) {
            return;
        }
        autofillId.setSessionId(this.mSessionId);
    }

    public final void dispatchCancellationSignal(ICancellationSignal iCancellationSignal) {
        if (iCancellationSignal == null) {
            return;
        }
        try {
            iCancellationSignal.cancel();
        } catch (RemoteException e) {
            Slog.e("ClientSuggestionsSession", "Error requesting a cancellation", e);
        }
    }

    /* loaded from: classes.dex */
    public class FillCallbackImpl extends IFillCallback.Stub {
        public final AtomicReference<ICancellationSignal> mCancellationSink;
        public final AndroidFuture<FillResponse> mFillRequest;
        public final AtomicReference<AndroidFuture<FillResponse>> mFutureRef;

        public FillCallbackImpl(AndroidFuture<FillResponse> androidFuture, AtomicReference<AndroidFuture<FillResponse>> atomicReference, AtomicReference<ICancellationSignal> atomicReference2) {
            this.mFillRequest = androidFuture;
            this.mFutureRef = atomicReference;
            this.mCancellationSink = atomicReference2;
        }

        public void onCancellable(ICancellationSignal iCancellationSignal) {
            AndroidFuture<FillResponse> androidFuture = this.mFutureRef.get();
            if (androidFuture != null && androidFuture.isCancelled()) {
                ClientSuggestionsSession.this.dispatchCancellationSignal(iCancellationSignal);
            } else {
                this.mCancellationSink.set(iCancellationSignal);
            }
        }

        public void onSuccess(FillResponse fillResponse) {
            this.mFillRequest.complete(fillResponse);
        }

        public void onFailure(int i, CharSequence charSequence) {
            this.mFillRequest.completeExceptionally(new RuntimeException(charSequence == null ? "" : String.valueOf(charSequence)));
        }
    }
}
