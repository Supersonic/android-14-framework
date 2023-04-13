package android.inputmethodservice;

import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManagerGlobal;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodSession;
import android.window.WindowProviderService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public abstract class AbstractInputMethodService extends WindowProviderService implements KeyEvent.Callback {
    final KeyEvent.DispatcherState mDispatcherState = new KeyEvent.DispatcherState();
    private InputMethod mInputMethod;
    private InputMethodServiceInternal mInputMethodServiceInternal;

    public abstract AbstractInputMethodImpl onCreateInputMethodInterface();

    public abstract AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface();

    /* JADX INFO: Access modifiers changed from: protected */
    public final InputMethod getInputMethodInternal() {
        return this.mInputMethod;
    }

    /* loaded from: classes2.dex */
    public abstract class AbstractInputMethodImpl implements InputMethod {
        public AbstractInputMethodImpl() {
        }

        @Override // android.view.inputmethod.InputMethod
        public void createSession(InputMethod.SessionCallback callback) {
            callback.sessionCreated(AbstractInputMethodService.this.onCreateInputMethodSessionInterface());
        }

        @Override // android.view.inputmethod.InputMethod
        public void setSessionEnabled(InputMethodSession session, boolean enabled) {
            ((AbstractInputMethodSessionImpl) session).setEnabled(enabled);
        }

        @Override // android.view.inputmethod.InputMethod
        public void revokeSession(InputMethodSession session) {
            ((AbstractInputMethodSessionImpl) session).revokeSelf();
        }
    }

    /* loaded from: classes2.dex */
    public abstract class AbstractInputMethodSessionImpl implements InputMethodSession {
        boolean mEnabled = true;
        boolean mRevoked;

        public AbstractInputMethodSessionImpl() {
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public boolean isRevoked() {
            return this.mRevoked;
        }

        public void setEnabled(boolean enabled) {
            if (!this.mRevoked) {
                this.mEnabled = enabled;
            }
        }

        public void revokeSelf() {
            this.mRevoked = true;
            this.mEnabled = false;
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchKeyEvent(int seq, KeyEvent event, InputMethodSession.EventCallback callback) {
            AbstractInputMethodService abstractInputMethodService = AbstractInputMethodService.this;
            boolean handled = event.dispatch(abstractInputMethodService, abstractInputMethodService.mDispatcherState, this);
            if (callback != null) {
                callback.finishedEvent(seq, handled);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchTrackballEvent(int seq, MotionEvent event, InputMethodSession.EventCallback callback) {
            boolean handled = AbstractInputMethodService.this.onTrackballEvent(event);
            if (callback != null) {
                callback.finishedEvent(seq, handled);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void dispatchGenericMotionEvent(int seq, MotionEvent event, InputMethodSession.EventCallback callback) {
            boolean handled = AbstractInputMethodService.this.onGenericMotionEvent(event);
            if (callback != null) {
                callback.finishedEvent(seq, handled);
            }
        }
    }

    public KeyEvent.DispatcherState getKeyDispatcherState() {
        return this.mDispatcherState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mInputMethod == null) {
            this.mInputMethod = onCreateInputMethodInterface();
        }
        if (this.mInputMethodServiceInternal == null) {
            this.mInputMethodServiceInternal = createInputMethodServiceInternal();
        }
        return new IInputMethodWrapper(this.mInputMethodServiceInternal, this.mInputMethod);
    }

    InputMethodServiceInternal createInputMethodServiceInternal() {
        return new InputMethodServiceInternal() { // from class: android.inputmethodservice.AbstractInputMethodService.1
            @Override // android.inputmethodservice.InputMethodServiceInternal
            public Context getContext() {
                return AbstractInputMethodService.this;
            }

            @Override // android.inputmethodservice.InputMethodServiceInternal
            public void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
                AbstractInputMethodService.this.dump(fd, fout, args);
            }
        };
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    @Override // android.window.WindowProviderService, android.window.WindowProvider
    public final int getWindowType() {
        return 2011;
    }

    @Override // android.window.WindowProviderService, android.window.WindowProvider
    public final Bundle getWindowContextOptions() {
        return super.getWindowContextOptions();
    }

    @Override // android.window.WindowProviderService
    public final int getInitialDisplayId() {
        try {
            return WindowManagerGlobal.getWindowManagerService().getImeDisplayId();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
