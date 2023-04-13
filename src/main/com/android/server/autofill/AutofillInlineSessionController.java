package com.android.server.autofill;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.view.autofill.AutofillId;
import android.view.inputmethod.InlineSuggestionsRequest;
import com.android.internal.annotations.GuardedBy;
import com.android.server.autofill.p007ui.InlineFillUi;
import com.android.server.inputmethod.InputMethodManagerInternal;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class AutofillInlineSessionController {
    public final ComponentName mComponentName;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public InlineFillUi mInlineFillUi;
    public final InputMethodManagerInternal mInputMethodManagerInternal;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public AutofillInlineSuggestionsRequestSession mSession;
    public final InlineFillUi.InlineUiEventCallback mUiCallback;
    public final int mUserId;

    public AutofillInlineSessionController(InputMethodManagerInternal inputMethodManagerInternal, int i, ComponentName componentName, Handler handler, Object obj, InlineFillUi.InlineUiEventCallback inlineUiEventCallback) {
        this.mInputMethodManagerInternal = inputMethodManagerInternal;
        this.mUserId = i;
        this.mComponentName = componentName;
        this.mHandler = handler;
        this.mLock = obj;
        this.mUiCallback = inlineUiEventCallback;
    }

    @GuardedBy({"mLock"})
    public void onCreateInlineSuggestionsRequestLocked(AutofillId autofillId, Consumer<InlineSuggestionsRequest> consumer, Bundle bundle) {
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession != null) {
            autofillInlineSuggestionsRequestSession.destroySessionLocked();
        }
        this.mInlineFillUi = null;
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession2 = new AutofillInlineSuggestionsRequestSession(this.mInputMethodManagerInternal, this.mUserId, this.mComponentName, this.mHandler, this.mLock, autofillId, consumer, bundle, this.mUiCallback);
        this.mSession = autofillInlineSuggestionsRequestSession2;
        autofillInlineSuggestionsRequestSession2.onCreateInlineSuggestionsRequestLocked();
    }

    @GuardedBy({"mLock"})
    public void destroyLocked(AutofillId autofillId) {
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession != null) {
            autofillInlineSuggestionsRequestSession.onInlineSuggestionsResponseLocked(InlineFillUi.emptyUi(autofillId));
            this.mSession.destroySessionLocked();
            this.mSession = null;
        }
        this.mInlineFillUi = null;
    }

    @GuardedBy({"mLock"})
    public Optional<InlineSuggestionsRequest> getInlineSuggestionsRequestLocked() {
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession != null) {
            return autofillInlineSuggestionsRequestSession.getInlineSuggestionsRequestLocked();
        }
        return Optional.empty();
    }

    @GuardedBy({"mLock"})
    public boolean hideInlineSuggestionsUiLocked(AutofillId autofillId) {
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession != null) {
            return autofillInlineSuggestionsRequestSession.onInlineSuggestionsResponseLocked(InlineFillUi.emptyUi(autofillId));
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public void disableFilterMatching(AutofillId autofillId) {
        InlineFillUi inlineFillUi = this.mInlineFillUi;
        if (inlineFillUi == null || !inlineFillUi.getAutofillId().equals(autofillId)) {
            return;
        }
        this.mInlineFillUi.disableFilterMatching();
    }

    @GuardedBy({"mLock"})
    public void resetInlineFillUiLocked() {
        this.mInlineFillUi = null;
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession != null) {
            autofillInlineSuggestionsRequestSession.resetInlineFillUiLocked();
        }
    }

    @GuardedBy({"mLock"})
    public boolean filterInlineFillUiLocked(AutofillId autofillId, String str) {
        InlineFillUi inlineFillUi = this.mInlineFillUi;
        if (inlineFillUi == null || !inlineFillUi.getAutofillId().equals(autofillId)) {
            return false;
        }
        this.mInlineFillUi.setFilterText(str);
        return requestImeToShowInlineSuggestionsLocked();
    }

    @GuardedBy({"mLock"})
    public boolean setInlineFillUiLocked(InlineFillUi inlineFillUi) {
        this.mInlineFillUi = inlineFillUi;
        return requestImeToShowInlineSuggestionsLocked();
    }

    @GuardedBy({"mLock"})
    public final boolean requestImeToShowInlineSuggestionsLocked() {
        InlineFillUi inlineFillUi;
        AutofillInlineSuggestionsRequestSession autofillInlineSuggestionsRequestSession = this.mSession;
        if (autofillInlineSuggestionsRequestSession == null || (inlineFillUi = this.mInlineFillUi) == null) {
            return false;
        }
        return autofillInlineSuggestionsRequestSession.onInlineSuggestionsResponseLocked(inlineFillUi);
    }
}
