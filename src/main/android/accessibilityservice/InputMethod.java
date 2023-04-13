package android.accessibilityservice;

import android.p008os.RemoteException;
import android.p008os.Trace;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.SurroundingText;
import android.view.inputmethod.TextAttribute;
import com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
import com.android.internal.inputmethod.RemoteAccessibilityInputConnection;
/* loaded from: classes.dex */
public class InputMethod {
    private static final String LOG_TAG = "A11yInputMethod";
    private EditorInfo mInputEditorInfo;
    private boolean mInputStarted;
    private final AccessibilityService mService;
    private RemoteAccessibilityInputConnection mStartedInputConnection;

    public InputMethod(AccessibilityService service) {
        this.mService = service;
    }

    public final AccessibilityInputConnection getCurrentInputConnection() {
        RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mStartedInputConnection;
        if (remoteAccessibilityInputConnection != null) {
            return new AccessibilityInputConnection(remoteAccessibilityInputConnection);
        }
        return null;
    }

    public final boolean getCurrentInputStarted() {
        return this.mInputStarted;
    }

    public final EditorInfo getCurrentInputEditorInfo() {
        return this.mInputEditorInfo;
    }

    public void onStartInput(EditorInfo attribute, boolean restarting) {
    }

    public void onFinishInput() {
    }

    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void createImeSession(IAccessibilityInputMethodSessionCallback callback) {
        AccessibilityInputMethodSessionWrapper wrapper = new AccessibilityInputMethodSessionWrapper(this.mService.getMainLooper(), new SessionImpl());
        try {
            callback.sessionCreated(wrapper, this.mService.getConnectionId());
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void startInput(RemoteAccessibilityInputConnection ic, EditorInfo attribute) {
        Log.m106v(LOG_TAG, "startInput(): editor=" + attribute);
        Trace.traceBegin(32L, "AccessibilityService.startInput");
        doStartInput(ic, attribute, false);
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void restartInput(RemoteAccessibilityInputConnection ic, EditorInfo attribute) {
        Log.m106v(LOG_TAG, "restartInput(): editor=" + attribute);
        Trace.traceBegin(32L, "AccessibilityService.restartInput");
        doStartInput(ic, attribute, true);
        Trace.traceEnd(32L);
    }

    final void doStartInput(RemoteAccessibilityInputConnection ic, EditorInfo attribute, boolean restarting) {
        if ((ic == null || !restarting) && this.mInputStarted) {
            doFinishInput();
            if (ic == null) {
                return;
            }
        }
        this.mInputStarted = true;
        this.mStartedInputConnection = ic;
        this.mInputEditorInfo = attribute;
        Log.m106v(LOG_TAG, "CALL: onStartInput");
        onStartInput(attribute, restarting);
    }

    final void doFinishInput() {
        Log.m106v(LOG_TAG, "CALL: doFinishInput");
        if (this.mInputStarted) {
            Log.m106v(LOG_TAG, "CALL: onFinishInput");
            onFinishInput();
        }
        this.mInputStarted = false;
        this.mStartedInputConnection = null;
        this.mInputEditorInfo = null;
    }

    /* loaded from: classes.dex */
    public final class AccessibilityInputConnection {
        private final RemoteAccessibilityInputConnection mIc;

        AccessibilityInputConnection(RemoteAccessibilityInputConnection ic) {
            this.mIc = ic;
        }

        public void commitText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.commitText(text, newCursorPosition, textAttribute);
            }
        }

        public void setSelection(int start, int end) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.setSelection(start, end);
            }
        }

        public SurroundingText getSurroundingText(int beforeLength, int afterLength, int flags) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                return remoteAccessibilityInputConnection.getSurroundingText(beforeLength, afterLength, flags);
            }
            return null;
        }

        public void deleteSurroundingText(int beforeLength, int afterLength) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.deleteSurroundingText(beforeLength, afterLength);
            }
        }

        public void sendKeyEvent(KeyEvent event) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.sendKeyEvent(event);
            }
        }

        public void performEditorAction(int editorAction) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.performEditorAction(editorAction);
            }
        }

        public void performContextMenuAction(int id) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.performContextMenuAction(id);
            }
        }

        public int getCursorCapsMode(int reqModes) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                return remoteAccessibilityInputConnection.getCursorCapsMode(reqModes);
            }
            return 0;
        }

        public void clearMetaKeyStates(int states) {
            RemoteAccessibilityInputConnection remoteAccessibilityInputConnection = this.mIc;
            if (remoteAccessibilityInputConnection != null) {
                remoteAccessibilityInputConnection.clearMetaKeyStates(states);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class SessionImpl implements AccessibilityInputMethodSession {
        boolean mEnabled;

        private SessionImpl() {
            this.mEnabled = true;
        }

        @Override // android.accessibilityservice.AccessibilityInputMethodSession
        public void setEnabled(boolean enabled) {
            this.mEnabled = enabled;
        }

        @Override // android.accessibilityservice.AccessibilityInputMethodSession
        public void finishInput() {
            if (this.mEnabled) {
                InputMethod.this.doFinishInput();
            }
        }

        @Override // android.accessibilityservice.AccessibilityInputMethodSession
        public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
            if (this.mEnabled) {
                InputMethod.this.onUpdateSelection(oldSelEnd, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
            }
        }

        @Override // android.accessibilityservice.AccessibilityInputMethodSession
        public void invalidateInput(EditorInfo editorInfo, IRemoteAccessibilityInputConnection connection, int sessionId) {
            if (!this.mEnabled || InputMethod.this.mStartedInputConnection == null || !InputMethod.this.mStartedInputConnection.isSameConnection(connection)) {
                return;
            }
            editorInfo.makeCompatible(InputMethod.this.mService.getApplicationInfo().targetSdkVersion);
            InputMethod inputMethod = InputMethod.this;
            inputMethod.restartInput(new RemoteAccessibilityInputConnection(inputMethod.mStartedInputConnection, sessionId), editorInfo);
        }
    }
}
