package android.inputmethodservice;

import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.view.inputmethod.InlineSuggestionsResponse;
import com.android.internal.inputmethod.IInlineSuggestionsRequestCallback;
import com.android.internal.inputmethod.IInlineSuggestionsResponseCallback;
import com.android.internal.inputmethod.InlineSuggestionsRequestInfo;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class InlineSuggestionSession {
    static final InlineSuggestionsResponse EMPTY_RESPONSE = new InlineSuggestionsResponse(Collections.emptyList());
    private static final String TAG = "ImsInlineSuggestionSession";
    private final IInlineSuggestionsRequestCallback mCallback;
    private boolean mCallbackInvoked = false;
    private final Supplier<IBinder> mHostInputTokenSupplier;
    private final InlineSuggestionSessionController mInlineSuggestionSessionController;
    private final Handler mMainThreadHandler;
    private Boolean mPreviousResponseIsEmpty;
    private final InlineSuggestionsRequestInfo mRequestInfo;
    private final Function<Bundle, InlineSuggestionsRequest> mRequestSupplier;
    private InlineSuggestionsResponseCallbackImpl mResponseCallback;
    private final Consumer<InlineSuggestionsResponse> mResponseConsumer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InlineSuggestionSession(InlineSuggestionsRequestInfo requestInfo, IInlineSuggestionsRequestCallback callback, Function<Bundle, InlineSuggestionsRequest> requestSupplier, Supplier<IBinder> hostInputTokenSupplier, Consumer<InlineSuggestionsResponse> responseConsumer, InlineSuggestionSessionController inlineSuggestionSessionController, Handler mainThreadHandler) {
        this.mRequestInfo = requestInfo;
        this.mCallback = callback;
        this.mRequestSupplier = requestSupplier;
        this.mHostInputTokenSupplier = hostInputTokenSupplier;
        this.mResponseConsumer = responseConsumer;
        this.mInlineSuggestionSessionController = inlineSuggestionSessionController;
        this.mMainThreadHandler = mainThreadHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InlineSuggestionsRequestInfo getRequestInfo() {
        return this.mRequestInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IInlineSuggestionsRequestCallback getRequestCallback() {
        return this.mCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldSendImeStatus() {
        return this.mResponseCallback != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCallbackInvoked() {
        return this.mCallbackInvoked;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        try {
            this.mCallback.onInlineSuggestionsSessionInvalidated();
        } catch (RemoteException e) {
            Log.m103w(TAG, "onInlineSuggestionsSessionInvalidated() remote exception", e);
        }
        if (this.mResponseCallback != null) {
            consumeInlineSuggestionsResponse(EMPTY_RESPONSE);
            this.mResponseCallback.invalidate();
            this.mResponseCallback = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeInlineSuggestionRequestUncheck() {
        if (this.mCallbackInvoked) {
            return;
        }
        try {
            InlineSuggestionsRequest request = this.mRequestSupplier.apply(this.mRequestInfo.getUiExtras());
            if (request == null) {
                this.mCallback.onInlineSuggestionsUnsupported();
            } else {
                request.setHostInputToken(this.mHostInputTokenSupplier.get());
                request.filterContentTypes();
                InlineSuggestionsResponseCallbackImpl inlineSuggestionsResponseCallbackImpl = new InlineSuggestionsResponseCallbackImpl();
                this.mResponseCallback = inlineSuggestionsResponseCallbackImpl;
                this.mCallback.onInlineSuggestionsRequest(request, inlineSuggestionsResponseCallbackImpl);
            }
        } catch (RemoteException e) {
            Log.m104w(TAG, "makeInlinedSuggestionsRequest() remote exception:" + e);
        }
        this.mCallbackInvoked = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleOnInlineSuggestionsResponse(AutofillId fieldId, InlineSuggestionsResponse response) {
        if (!this.mInlineSuggestionSessionController.match(fieldId)) {
            return;
        }
        consumeInlineSuggestionsResponse(response);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void consumeInlineSuggestionsResponse(InlineSuggestionsResponse response) {
        boolean isResponseEmpty = response.getInlineSuggestions().isEmpty();
        if (isResponseEmpty && Boolean.TRUE.equals(this.mPreviousResponseIsEmpty)) {
            return;
        }
        this.mPreviousResponseIsEmpty = Boolean.valueOf(isResponseEmpty);
        this.mResponseConsumer.accept(response);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class InlineSuggestionsResponseCallbackImpl extends IInlineSuggestionsResponseCallback.Stub {
        private volatile boolean mInvalid;
        private final WeakReference<InlineSuggestionSession> mSession;

        private InlineSuggestionsResponseCallbackImpl(InlineSuggestionSession session) {
            this.mInvalid = false;
            this.mSession = new WeakReference<>(session);
        }

        void invalidate() {
            this.mInvalid = true;
        }

        @Override // com.android.internal.inputmethod.IInlineSuggestionsResponseCallback
        public void onInlineSuggestionsResponse(AutofillId fieldId, InlineSuggestionsResponse response) {
            InlineSuggestionSession session;
            if (!this.mInvalid && (session = this.mSession.get()) != null) {
                session.mMainThreadHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.inputmethodservice.InlineSuggestionSession$InlineSuggestionsResponseCallbackImpl$$ExternalSyntheticLambda0
                    @Override // com.android.internal.util.function.TriConsumer
                    public final void accept(Object obj, Object obj2, Object obj3) {
                        ((InlineSuggestionSession) obj).handleOnInlineSuggestionsResponse((AutofillId) obj2, (InlineSuggestionsResponse) obj3);
                    }
                }, session, fieldId, response));
            }
        }
    }
}
