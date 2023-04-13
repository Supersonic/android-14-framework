package com.android.server.autofill;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.inputmethod.InlineSuggestion;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.view.inputmethod.InlineSuggestionsResponse;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.IInlineSuggestionsRequestCallback;
import com.android.internal.inputmethod.IInlineSuggestionsResponseCallback;
import com.android.internal.inputmethod.InlineSuggestionsRequestInfo;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.autofill.p007ui.InlineFillUi;
import com.android.server.inputmethod.InputMethodManagerInternal;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class AutofillInlineSuggestionsRequestSession {
    public static final String TAG = "AutofillInlineSuggestionsRequestSession";
    @GuardedBy({"mLock"})
    public AutofillId mAutofillId;
    public final ComponentName mComponentName;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public AutofillId mImeCurrentFieldId;
    @GuardedBy({"mLock"})
    public boolean mImeInputStarted;
    @GuardedBy({"mLock"})
    public boolean mImeInputViewStarted;
    @GuardedBy({"mLock"})
    public InlineSuggestionsRequest mImeRequest;
    @GuardedBy({"mLock"})
    public Consumer<InlineSuggestionsRequest> mImeRequestConsumer;
    @GuardedBy({"mLock"})
    public boolean mImeRequestReceived;
    @GuardedBy({"mLock"})
    public InlineFillUi mInlineFillUi;
    public final InputMethodManagerInternal mInputMethodManagerInternal;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public boolean mPreviousHasNonPinSuggestionShow;
    @GuardedBy({"mLock"})
    public IInlineSuggestionsResponseCallback mResponseCallback;
    public final InlineFillUi.InlineUiEventCallback mUiCallback;
    public final Bundle mUiExtras;
    public final int mUserId;
    @GuardedBy({"mLock"})
    public Boolean mPreviousResponseIsNotEmpty = null;
    @GuardedBy({"mLock"})
    public boolean mDestroyed = false;
    @GuardedBy({"mLock"})
    public boolean mImeSessionInvalidated = false;

    public AutofillInlineSuggestionsRequestSession(InputMethodManagerInternal inputMethodManagerInternal, int i, ComponentName componentName, Handler handler, Object obj, AutofillId autofillId, Consumer<InlineSuggestionsRequest> consumer, Bundle bundle, InlineFillUi.InlineUiEventCallback inlineUiEventCallback) {
        this.mInputMethodManagerInternal = inputMethodManagerInternal;
        this.mUserId = i;
        this.mComponentName = componentName;
        this.mHandler = handler;
        this.mLock = obj;
        this.mUiExtras = bundle;
        this.mUiCallback = inlineUiEventCallback;
        this.mAutofillId = autofillId;
        this.mImeRequestConsumer = consumer;
    }

    @GuardedBy({"mLock"})
    public Optional<InlineSuggestionsRequest> getInlineSuggestionsRequestLocked() {
        if (this.mDestroyed) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.mImeRequest);
    }

    @GuardedBy({"mLock"})
    public boolean onInlineSuggestionsResponseLocked(InlineFillUi inlineFillUi) {
        if (this.mDestroyed) {
            return false;
        }
        if (Helper.sDebug) {
            String str = TAG;
            Slog.d(str, "onInlineSuggestionsResponseLocked called for:" + inlineFillUi.getAutofillId());
        }
        if (this.mImeRequest == null || this.mResponseCallback == null || this.mImeSessionInvalidated) {
            return false;
        }
        this.mAutofillId = inlineFillUi.getAutofillId();
        this.mInlineFillUi = inlineFillUi;
        maybeUpdateResponseToImeLocked();
        return true;
    }

    @GuardedBy({"mLock"})
    public void destroySessionLocked() {
        this.mDestroyed = true;
        if (this.mImeRequestReceived) {
            return;
        }
        String str = TAG;
        Slog.w(str, "Never received an InlineSuggestionsRequest from the IME for " + this.mAutofillId);
    }

    @GuardedBy({"mLock"})
    public void onCreateInlineSuggestionsRequestLocked() {
        if (this.mDestroyed) {
            return;
        }
        this.mImeSessionInvalidated = false;
        if (Helper.sDebug) {
            String str = TAG;
            Slog.d(str, "onCreateInlineSuggestionsRequestLocked called: " + this.mAutofillId);
        }
        this.mInputMethodManagerInternal.onCreateInlineSuggestionsRequest(this.mUserId, new InlineSuggestionsRequestInfo(this.mComponentName, this.mAutofillId, this.mUiExtras), new InlineSuggestionsRequestCallbackImpl());
    }

    @GuardedBy({"mLock"})
    public void resetInlineFillUiLocked() {
        this.mInlineFillUi = null;
    }

    @GuardedBy({"mLock"})
    public final void maybeUpdateResponseToImeLocked() {
        if (Helper.sVerbose) {
            Slog.v(TAG, "maybeUpdateResponseToImeLocked called");
        }
        if (this.mDestroyed || this.mResponseCallback == null || !this.mImeInputViewStarted || this.mInlineFillUi == null || !match(this.mAutofillId, this.mImeCurrentFieldId)) {
            return;
        }
        InlineSuggestionsResponse inlineSuggestionsResponse = this.mInlineFillUi.getInlineSuggestionsResponse();
        boolean isEmpty = inlineSuggestionsResponse.getInlineSuggestions().isEmpty();
        if (isEmpty && Boolean.FALSE.equals(this.mPreviousResponseIsNotEmpty)) {
            return;
        }
        maybeNotifyFillUiEventLocked(inlineSuggestionsResponse.getInlineSuggestions());
        updateResponseToImeUncheckLocked(inlineSuggestionsResponse);
        this.mPreviousResponseIsNotEmpty = Boolean.valueOf(!isEmpty);
    }

    @GuardedBy({"mLock"})
    public final void updateResponseToImeUncheckLocked(InlineSuggestionsResponse inlineSuggestionsResponse) {
        if (this.mDestroyed) {
            return;
        }
        if (Helper.sDebug) {
            String str = TAG;
            Slog.d(str, "Send inline response: " + inlineSuggestionsResponse.getInlineSuggestions().size());
        }
        try {
            this.mResponseCallback.onInlineSuggestionsResponse(this.mAutofillId, inlineSuggestionsResponse);
        } catch (RemoteException unused) {
            Slog.e(TAG, "RemoteException sending InlineSuggestionsResponse to IME");
        }
    }

    @GuardedBy({"mLock"})
    public final void maybeNotifyFillUiEventLocked(List<InlineSuggestion> list) {
        if (this.mDestroyed) {
            return;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                break;
            } else if (!list.get(i).getInfo().isPinned()) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (Helper.sDebug) {
            Slog.d(TAG, "maybeNotifyFillUiEventLoked(): hasSuggestionToShow=" + z + ", mPreviousHasNonPinSuggestionShow=" + this.mPreviousHasNonPinSuggestionShow);
        }
        if (z && !this.mPreviousHasNonPinSuggestionShow) {
            this.mUiCallback.notifyInlineUiShown(this.mAutofillId);
        } else if (!z && this.mPreviousHasNonPinSuggestionShow) {
            this.mUiCallback.notifyInlineUiHidden(this.mAutofillId);
        }
        this.mPreviousHasNonPinSuggestionShow = z;
    }

    public final void handleOnReceiveImeRequest(InlineSuggestionsRequest inlineSuggestionsRequest, IInlineSuggestionsResponseCallback iInlineSuggestionsResponseCallback) {
        synchronized (this.mLock) {
            if (!this.mDestroyed && !this.mImeRequestReceived) {
                this.mImeRequestReceived = true;
                this.mImeSessionInvalidated = false;
                if (inlineSuggestionsRequest != null && iInlineSuggestionsResponseCallback != null) {
                    this.mImeRequest = inlineSuggestionsRequest;
                    this.mResponseCallback = iInlineSuggestionsResponseCallback;
                    handleOnReceiveImeStatusUpdated(this.mAutofillId, true, false);
                }
                Consumer<InlineSuggestionsRequest> consumer = this.mImeRequestConsumer;
                if (consumer != null) {
                    consumer.accept(this.mImeRequest);
                    this.mImeRequestConsumer = null;
                }
            }
        }
    }

    public final void handleOnReceiveImeStatusUpdated(boolean z, boolean z2) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            if (this.mImeCurrentFieldId != null) {
                boolean z3 = true;
                boolean z4 = this.mImeInputStarted != z;
                if (this.mImeInputViewStarted == z2) {
                    z3 = false;
                }
                this.mImeInputStarted = z;
                this.mImeInputViewStarted = z2;
                if (z4 || z3) {
                    maybeUpdateResponseToImeLocked();
                }
            }
        }
    }

    public final void handleOnReceiveImeStatusUpdated(AutofillId autofillId, boolean z, boolean z2) {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            if (autofillId != null) {
                this.mImeCurrentFieldId = autofillId;
            }
            handleOnReceiveImeStatusUpdated(z, z2);
        }
    }

    public final void handleOnReceiveImeSessionInvalidated() {
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            this.mImeSessionInvalidated = true;
        }
    }

    /* loaded from: classes.dex */
    public static final class InlineSuggestionsRequestCallbackImpl extends IInlineSuggestionsRequestCallback.Stub {
        public final WeakReference<AutofillInlineSuggestionsRequestSession> mSession;

        public InlineSuggestionsRequestCallbackImpl(AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession) {
            this.mSession = new WeakReference<>(autofillInlineSuggestionsRequestSession);
        }

        public void onInlineSuggestionsUnsupported() throws RemoteException {
            if (Helper.sDebug) {
                Slog.d(AutofillInlineSuggestionsRequestSession.TAG, "onInlineSuggestionsUnsupported() called.");
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                autofillInlineSuggestionsRequestSession.mHandler.sendMessage(PooledLambda.obtainMessage(new C0522xe2687de6(), autofillInlineSuggestionsRequestSession, (Object) null, (Object) null));
            }
        }

        public void onInlineSuggestionsRequest(InlineSuggestionsRequest inlineSuggestionsRequest, IInlineSuggestionsResponseCallback iInlineSuggestionsResponseCallback) {
            if (Helper.sDebug) {
                String str = AutofillInlineSuggestionsRequestSession.TAG;
                Slog.d(str, "onInlineSuggestionsRequest() received: " + inlineSuggestionsRequest);
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                autofillInlineSuggestionsRequestSession.mHandler.sendMessage(PooledLambda.obtainMessage(new C0522xe2687de6(), autofillInlineSuggestionsRequestSession, inlineSuggestionsRequest, iInlineSuggestionsResponseCallback));
            }
        }

        public void onInputMethodStartInput(AutofillId autofillId) throws RemoteException {
            if (Helper.sVerbose) {
                String str = AutofillInlineSuggestionsRequestSession.TAG;
                Slog.v(str, "onInputMethodStartInput() received on " + autofillId);
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                autofillInlineSuggestionsRequestSession.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.autofill.AutofillInlineSuggestionsRequestSession$InlineSuggestionsRequestCallbackImpl$$ExternalSyntheticLambda1
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                        ((AutofillInlineSuggestionsRequestSession) obj).handleOnReceiveImeStatusUpdated((AutofillId) obj2, ((Boolean) obj3).booleanValue(), ((Boolean) obj4).booleanValue());
                    }
                }, autofillInlineSuggestionsRequestSession, autofillId, Boolean.TRUE, Boolean.FALSE));
            }
        }

        public void onInputMethodShowInputRequested(boolean z) throws RemoteException {
            if (Helper.sVerbose) {
                String str = AutofillInlineSuggestionsRequestSession.TAG;
                Slog.v(str, "onInputMethodShowInputRequested() received: " + z);
            }
        }

        public void onInputMethodStartInputView() {
            if (Helper.sVerbose) {
                Slog.v(AutofillInlineSuggestionsRequestSession.TAG, "onInputMethodStartInputView() received");
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                Handler handler = autofillInlineSuggestionsRequestSession.mHandler;
                C0524xe2687de8 c0524xe2687de8 = new C0524xe2687de8();
                Boolean bool = Boolean.TRUE;
                handler.sendMessage(PooledLambda.obtainMessage(c0524xe2687de8, autofillInlineSuggestionsRequestSession, bool, bool));
            }
        }

        public void onInputMethodFinishInputView() {
            if (Helper.sVerbose) {
                Slog.v(AutofillInlineSuggestionsRequestSession.TAG, "onInputMethodFinishInputView() received");
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                autofillInlineSuggestionsRequestSession.mHandler.sendMessage(PooledLambda.obtainMessage(new C0524xe2687de8(), autofillInlineSuggestionsRequestSession, Boolean.TRUE, Boolean.FALSE));
            }
        }

        public void onInputMethodFinishInput() throws RemoteException {
            if (Helper.sVerbose) {
                Slog.v(AutofillInlineSuggestionsRequestSession.TAG, "onInputMethodFinishInput() received");
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                Handler handler = autofillInlineSuggestionsRequestSession.mHandler;
                C0524xe2687de8 c0524xe2687de8 = new C0524xe2687de8();
                Boolean bool = Boolean.FALSE;
                handler.sendMessage(PooledLambda.obtainMessage(c0524xe2687de8, autofillInlineSuggestionsRequestSession, bool, bool));
            }
        }

        public void onInlineSuggestionsSessionInvalidated() throws RemoteException {
            if (Helper.sDebug) {
                Slog.d(AutofillInlineSuggestionsRequestSession.TAG, "onInlineSuggestionsSessionInvalidated() called.");
            }
            AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession.get();
            if (autofillInlineSuggestionsRequestSession != null) {
                autofillInlineSuggestionsRequestSession.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.autofill.AutofillInlineSuggestionsRequestSession$InlineSuggestionsRequestCallbackImpl$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((AutofillInlineSuggestionsRequestSession) obj).handleOnReceiveImeSessionInvalidated();
                    }
                }, autofillInlineSuggestionsRequestSession));
            }
        }
    }

    public static boolean match(AutofillId autofillId, AutofillId autofillId2) {
        return (autofillId == null || autofillId2 == null || autofillId.getViewId() != autofillId2.getViewId()) ? false : true;
    }
}
