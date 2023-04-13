package com.android.internal.inputmethod;

import android.view.KeyEvent;
import android.view.inputmethod.SurroundingText;
import android.view.inputmethod.TextAttribute;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes4.dex */
public final class RemoteAccessibilityInputConnection {
    private static final int MAX_WAIT_TIME_MILLIS = 2000;
    private static final String TAG = "RemoteA11yInputConnection";
    private final CancellationGroup mCancellationGroup;
    IRemoteAccessibilityInputConnectionInvoker mInvoker;

    public RemoteAccessibilityInputConnection(IRemoteAccessibilityInputConnection connection, CancellationGroup cancellationGroup) {
        this.mInvoker = IRemoteAccessibilityInputConnectionInvoker.create(connection);
        this.mCancellationGroup = cancellationGroup;
    }

    public RemoteAccessibilityInputConnection(RemoteAccessibilityInputConnection original, int sessionId) {
        this.mInvoker = original.mInvoker.cloneWithSessionId(sessionId);
        this.mCancellationGroup = original.mCancellationGroup;
    }

    public boolean isSameConnection(IRemoteAccessibilityInputConnection connection) {
        return this.mInvoker.isSameConnection(connection);
    }

    public void commitText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        this.mInvoker.commitText(text, newCursorPosition, textAttribute);
    }

    public void setSelection(int start, int end) {
        this.mInvoker.setSelection(start, end);
    }

    public SurroundingText getSurroundingText(int beforeLength, int afterLength, int flags) {
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<SurroundingText> value = this.mInvoker.getSurroundingText(beforeLength, afterLength, flags);
        return (SurroundingText) CompletableFutureUtil.getResultOrNull(value, TAG, "getSurroundingText()", this.mCancellationGroup, 2000L);
    }

    public void deleteSurroundingText(int beforeLength, int afterLength) {
        this.mInvoker.deleteSurroundingText(beforeLength, afterLength);
    }

    public void sendKeyEvent(KeyEvent event) {
        this.mInvoker.sendKeyEvent(event);
    }

    public void performEditorAction(int actionCode) {
        this.mInvoker.performEditorAction(actionCode);
    }

    public void performContextMenuAction(int id) {
        this.mInvoker.performContextMenuAction(id);
    }

    public int getCursorCapsMode(int reqModes) {
        if (this.mCancellationGroup.isCanceled()) {
            return 0;
        }
        CompletableFuture<Integer> value = this.mInvoker.getCursorCapsMode(reqModes);
        return CompletableFutureUtil.getResultOrZero(value, TAG, "getCursorCapsMode()", this.mCancellationGroup, 2000L);
    }

    public void clearMetaKeyStates(int states) {
        this.mInvoker.clearMetaKeyStates(states);
    }
}
