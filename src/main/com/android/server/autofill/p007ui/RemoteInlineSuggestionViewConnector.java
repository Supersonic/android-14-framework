package com.android.server.autofill.p007ui;

import android.content.IntentSender;
import android.os.IBinder;
import android.service.autofill.IInlineSuggestionUiCallback;
import android.service.autofill.InlinePresentation;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.autofill.Helper;
import com.android.server.autofill.RemoteInlineSuggestionRenderService;
import com.android.server.autofill.p007ui.InlineFillUi;
import com.android.server.inputmethod.InputMethodManagerInternal;
import java.util.Objects;
import java.util.function.Consumer;
/* renamed from: com.android.server.autofill.ui.RemoteInlineSuggestionViewConnector */
/* loaded from: classes.dex */
public final class RemoteInlineSuggestionViewConnector {
    public static final String TAG = "RemoteInlineSuggestionViewConnector";
    public final int mDisplayId;
    public final IBinder mHostInputToken;
    public final InlinePresentation mInlinePresentation;
    public final Runnable mOnAutofillCallback;
    public final Runnable mOnErrorCallback;
    public final Runnable mOnInflateCallback;
    public final RemoteInlineSuggestionRenderService mRemoteRenderService;
    public final int mSessionId;
    public final Consumer<IntentSender> mStartIntentSenderFromClientApp;
    public final int mUserId;

    public RemoteInlineSuggestionViewConnector(InlineFillUi.InlineFillUiInfo inlineFillUiInfo, InlinePresentation inlinePresentation, Runnable runnable, final InlineFillUi.InlineSuggestionUiCallback inlineSuggestionUiCallback) {
        this.mRemoteRenderService = inlineFillUiInfo.mRemoteRenderService;
        this.mInlinePresentation = inlinePresentation;
        this.mHostInputToken = inlineFillUiInfo.mInlineRequest.getHostInputToken();
        this.mDisplayId = inlineFillUiInfo.mInlineRequest.getHostDisplayId();
        this.mUserId = inlineFillUiInfo.mUserId;
        this.mSessionId = inlineFillUiInfo.mSessionId;
        this.mOnAutofillCallback = runnable;
        Objects.requireNonNull(inlineSuggestionUiCallback);
        this.mOnErrorCallback = new Runnable() { // from class: com.android.server.autofill.ui.RemoteInlineSuggestionViewConnector$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InlineFillUi.InlineSuggestionUiCallback.this.onError();
            }
        };
        this.mOnInflateCallback = new Runnable() { // from class: com.android.server.autofill.ui.RemoteInlineSuggestionViewConnector$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                InlineFillUi.InlineSuggestionUiCallback.this.onInflate();
            }
        };
        this.mStartIntentSenderFromClientApp = new Consumer() { // from class: com.android.server.autofill.ui.RemoteInlineSuggestionViewConnector$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InlineFillUi.InlineSuggestionUiCallback.this.startIntentSender((IntentSender) obj);
            }
        };
    }

    public boolean renderSuggestion(int i, int i2, IInlineSuggestionUiCallback iInlineSuggestionUiCallback) {
        if (this.mRemoteRenderService != null) {
            if (Helper.sDebug) {
                Slog.d(TAG, "Request to recreate the UI");
            }
            this.mRemoteRenderService.renderSuggestion(iInlineSuggestionUiCallback, this.mInlinePresentation, i, i2, this.mHostInputToken, this.mDisplayId, this.mUserId, this.mSessionId);
            return true;
        }
        return false;
    }

    public void onClick() {
        this.mOnAutofillCallback.run();
    }

    public void onError() {
        this.mOnErrorCallback.run();
    }

    public void onRender() {
        this.mOnInflateCallback.run();
    }

    public void onTransferTouchFocusToImeWindow(IBinder iBinder, int i) {
        if (((InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class)).transferTouchFocusToImeWindow(iBinder, i)) {
            return;
        }
        Slog.e(TAG, "Cannot transfer touch focus from suggestion to IME");
        this.mOnErrorCallback.run();
    }

    public void onStartIntentSender(IntentSender intentSender) {
        this.mStartIntentSenderFromClientApp.accept(intentSender);
    }
}
