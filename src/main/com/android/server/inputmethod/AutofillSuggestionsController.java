package com.android.server.inputmethod;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.inputmethod.IInlineSuggestionsRequestCallback;
import com.android.internal.inputmethod.IInlineSuggestionsResponseCallback;
import com.android.internal.inputmethod.InlineSuggestionsRequestInfo;
import com.android.server.inputmethod.InputMethodUtils;
/* loaded from: classes.dex */
public final class AutofillSuggestionsController {
    public static final String TAG = "AutofillSuggestionsController";
    @GuardedBy({"ImfLock.class"})
    public IInlineSuggestionsRequestCallback mInlineSuggestionsRequestCallback;
    public final ArrayMap<String, InputMethodInfo> mMethodMap;
    @GuardedBy({"ImfLock.class"})
    public CreateInlineSuggestionsRequest mPendingInlineSuggestionsRequest;
    public final InputMethodManagerService mService;
    public final InputMethodUtils.InputMethodSettings mSettings;

    /* loaded from: classes.dex */
    public static final class CreateInlineSuggestionsRequest {
        public final IInlineSuggestionsRequestCallback mCallback;
        public final String mPackageName;
        public final InlineSuggestionsRequestInfo mRequestInfo;

        public CreateInlineSuggestionsRequest(InlineSuggestionsRequestInfo inlineSuggestionsRequestInfo, IInlineSuggestionsRequestCallback iInlineSuggestionsRequestCallback, String str) {
            this.mRequestInfo = inlineSuggestionsRequestInfo;
            this.mCallback = iInlineSuggestionsRequestCallback;
            this.mPackageName = str;
        }
    }

    public AutofillSuggestionsController(InputMethodManagerService inputMethodManagerService) {
        this.mService = inputMethodManagerService;
        this.mMethodMap = inputMethodManagerService.mMethodMap;
        this.mSettings = inputMethodManagerService.mSettings;
    }

    @GuardedBy({"ImfLock.class"})
    public void onCreateInlineSuggestionsRequest(int i, InlineSuggestionsRequestInfo inlineSuggestionsRequestInfo, IInlineSuggestionsRequestCallback iInlineSuggestionsRequestCallback, boolean z) {
        clearPendingInlineSuggestionsRequest();
        this.mInlineSuggestionsRequestCallback = iInlineSuggestionsRequestCallback;
        InputMethodInfo inputMethodInfo = this.mMethodMap.get(this.mService.getSelectedMethodIdLocked());
        try {
            if (i == this.mSettings.getCurrentUserId() && inputMethodInfo != null && isInlineSuggestionsEnabled(inputMethodInfo, z)) {
                this.mPendingInlineSuggestionsRequest = new CreateInlineSuggestionsRequest(inlineSuggestionsRequestInfo, iInlineSuggestionsRequestCallback, inputMethodInfo.getPackageName());
                if (this.mService.getCurMethodLocked() != null) {
                    performOnCreateInlineSuggestionsRequest();
                }
            } else {
                iInlineSuggestionsRequestCallback.onInlineSuggestionsUnsupported();
            }
        } catch (RemoteException e) {
            String str = TAG;
            Slog.w(str, "RemoteException calling onCreateInlineSuggestionsRequest(): " + e);
        }
    }

    @GuardedBy({"ImfLock.class"})
    public void performOnCreateInlineSuggestionsRequest() {
        if (this.mPendingInlineSuggestionsRequest == null) {
            return;
        }
        IInputMethodInvoker curMethodLocked = this.mService.getCurMethodLocked();
        if (curMethodLocked != null) {
            CreateInlineSuggestionsRequest createInlineSuggestionsRequest = this.mPendingInlineSuggestionsRequest;
            curMethodLocked.onCreateInlineSuggestionsRequest(this.mPendingInlineSuggestionsRequest.mRequestInfo, new InlineSuggestionsRequestCallbackDecorator(createInlineSuggestionsRequest.mCallback, createInlineSuggestionsRequest.mPackageName, this.mService.getCurTokenDisplayIdLocked(), this.mService.getCurTokenLocked(), this.mService));
        } else {
            Slog.w(TAG, "No IME connected! Abandoning inline suggestions creation request.");
        }
        clearPendingInlineSuggestionsRequest();
    }

    @GuardedBy({"ImfLock.class"})
    public final void clearPendingInlineSuggestionsRequest() {
        this.mPendingInlineSuggestionsRequest = null;
    }

    public static boolean isInlineSuggestionsEnabled(InputMethodInfo inputMethodInfo, boolean z) {
        return inputMethodInfo.isInlineSuggestionsEnabled() && (!z || inputMethodInfo.supportsInlineSuggestionsWithTouchExploration());
    }

    @GuardedBy({"ImfLock.class"})
    public void invalidateAutofillSession() {
        IInlineSuggestionsRequestCallback iInlineSuggestionsRequestCallback = this.mInlineSuggestionsRequestCallback;
        if (iInlineSuggestionsRequestCallback != null) {
            try {
                iInlineSuggestionsRequestCallback.onInlineSuggestionsSessionInvalidated();
            } catch (RemoteException e) {
                Slog.e(TAG, "Cannot invalidate autofill session.", e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class InlineSuggestionsRequestCallbackDecorator extends IInlineSuggestionsRequestCallback.Stub {
        public final IInlineSuggestionsRequestCallback mCallback;
        public final int mImeDisplayId;
        public final String mImePackageName;
        public final IBinder mImeToken;
        public final InputMethodManagerService mImms;

        public InlineSuggestionsRequestCallbackDecorator(IInlineSuggestionsRequestCallback iInlineSuggestionsRequestCallback, String str, int i, IBinder iBinder, InputMethodManagerService inputMethodManagerService) {
            this.mCallback = iInlineSuggestionsRequestCallback;
            this.mImePackageName = str;
            this.mImeDisplayId = i;
            this.mImeToken = iBinder;
            this.mImms = inputMethodManagerService;
        }

        public void onInlineSuggestionsUnsupported() throws RemoteException {
            this.mCallback.onInlineSuggestionsUnsupported();
        }

        public void onInlineSuggestionsRequest(InlineSuggestionsRequest inlineSuggestionsRequest, IInlineSuggestionsResponseCallback iInlineSuggestionsResponseCallback) throws RemoteException {
            if (!this.mImePackageName.equals(inlineSuggestionsRequest.getHostPackageName())) {
                throw new SecurityException("Host package name in the provide request=[" + inlineSuggestionsRequest.getHostPackageName() + "] doesn't match the IME package name=[" + this.mImePackageName + "].");
            }
            inlineSuggestionsRequest.setHostDisplayId(this.mImeDisplayId);
            this.mImms.setCurHostInputToken(this.mImeToken, inlineSuggestionsRequest.getHostInputToken());
            this.mCallback.onInlineSuggestionsRequest(inlineSuggestionsRequest, iInlineSuggestionsResponseCallback);
        }

        public void onInputMethodStartInput(AutofillId autofillId) throws RemoteException {
            this.mCallback.onInputMethodStartInput(autofillId);
        }

        public void onInputMethodShowInputRequested(boolean z) throws RemoteException {
            this.mCallback.onInputMethodShowInputRequested(z);
        }

        public void onInputMethodStartInputView() throws RemoteException {
            this.mCallback.onInputMethodStartInputView();
        }

        public void onInputMethodFinishInputView() throws RemoteException {
            this.mCallback.onInputMethodFinishInputView();
        }

        public void onInputMethodFinishInput() throws RemoteException {
            this.mCallback.onInputMethodFinishInput();
        }

        public void onInlineSuggestionsSessionInvalidated() throws RemoteException {
            this.mCallback.onInlineSuggestionsSessionInvalidated();
        }
    }
}
