package android.accessibilityservice;

import android.p008os.Handler;
import android.p008os.Looper;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import java.util.concurrent.atomic.AtomicReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AccessibilityInputMethodSessionWrapper extends IAccessibilityInputMethodSession.Stub {
    private final Handler mHandler;
    private final AtomicReference<AccessibilityInputMethodSession> mSessionRef;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityInputMethodSessionWrapper(Looper looper, AccessibilityInputMethodSession session) {
        this.mSessionRef = new AtomicReference<>(session);
        this.mHandler = Handler.createAsync(looper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessibilityInputMethodSession getSession() {
        return this.mSessionRef.get();
    }

    @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
    public void updateSelection(final int oldSelStart, final int oldSelEnd, final int newSelStart, final int newSelEnd, final int candidatesStart, final int candidatesEnd) {
        if (this.mHandler.getLooper().isCurrentThread()) {
            lambda$updateSelection$0(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        } else {
            this.mHandler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityInputMethodSessionWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityInputMethodSessionWrapper.this.lambda$updateSelection$0(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: doUpdateSelection */
    public void lambda$updateSelection$0(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        AccessibilityInputMethodSession session = this.mSessionRef.get();
        if (session != null) {
            session.updateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        }
    }

    @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
    public void finishInput() {
        if (this.mHandler.getLooper().isCurrentThread()) {
            doFinishInput();
        } else {
            this.mHandler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityInputMethodSessionWrapper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityInputMethodSessionWrapper.this.doFinishInput();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFinishInput() {
        AccessibilityInputMethodSession session = this.mSessionRef.get();
        if (session != null) {
            session.finishInput();
        }
    }

    @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
    public void finishSession() {
        if (this.mHandler.getLooper().isCurrentThread()) {
            doFinishSession();
        } else {
            this.mHandler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityInputMethodSessionWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityInputMethodSessionWrapper.this.doFinishSession();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFinishSession() {
        this.mSessionRef.set(null);
    }

    @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
    public void invalidateInput(final EditorInfo editorInfo, final IRemoteAccessibilityInputConnection connection, final int sessionId) {
        if (this.mHandler.getLooper().isCurrentThread()) {
            lambda$invalidateInput$1(editorInfo, connection, sessionId);
        } else {
            this.mHandler.post(new Runnable() { // from class: android.accessibilityservice.AccessibilityInputMethodSessionWrapper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityInputMethodSessionWrapper.this.lambda$invalidateInput$1(editorInfo, connection, sessionId);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: doInvalidateInput */
    public void lambda$invalidateInput$1(EditorInfo editorInfo, IRemoteAccessibilityInputConnection connection, int sessionId) {
        AccessibilityInputMethodSession session = this.mSessionRef.get();
        if (session != null) {
            session.invalidateInput(editorInfo, connection, sessionId);
        }
    }
}
