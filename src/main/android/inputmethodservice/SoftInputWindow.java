package android.inputmethodservice;

import android.app.Dialog;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SoftInputWindow extends Dialog {
    private static final boolean DEBUG = false;
    private static final String TAG = "SoftInputWindow";
    private final Rect mBounds;
    private final KeyEvent.DispatcherState mDispatcherState;
    private final InputMethodService mService;
    private int mWindowState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    private @interface WindowState {
        public static final int DESTROYED = 4;
        public static final int REJECTED_AT_LEAST_ONCE = 3;
        public static final int SHOWN_AT_LEAST_ONCE = 2;
        public static final int TOKEN_PENDING = 0;
        public static final int TOKEN_SET = 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setToken(IBinder token) {
        switch (this.mWindowState) {
            case 0:
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.token = token;
                getWindow().setAttributes(lp);
                updateWindowState(1);
                getWindow().getDecorView().setVisibility(4);
                show();
                return;
            case 1:
            case 2:
            case 3:
                throw new IllegalStateException("setToken can be called only once");
            case 4:
                Log.m108i(TAG, "Ignoring setToken() because window is already destroyed.");
                return;
            default:
                throw new IllegalStateException("Unexpected state=" + this.mWindowState);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SoftInputWindow(InputMethodService service, int theme, KeyEvent.DispatcherState dispatcherState) {
        super(service, theme);
        this.mBounds = new Rect();
        this.mWindowState = 0;
        this.mService = service;
        this.mDispatcherState = dispatcherState;
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.mDispatcherState.reset();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getWindow().getDecorView().getHitRect(this.mBounds);
        if (ev.isWithinBoundsNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1)) {
            return super.dispatchTouchEvent(ev);
        }
        MotionEvent temp = ev.clampNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1);
        boolean handled = super.dispatchTouchEvent(temp);
        temp.recycle();
        return handled;
    }

    @Override // android.app.Dialog
    public void show() {
        switch (this.mWindowState) {
            case 0:
                throw new IllegalStateException("Window token is not set yet.");
            case 1:
            case 2:
                try {
                    super.show();
                    updateWindowState(2);
                    return;
                } catch (WindowManager.BadTokenException e) {
                    Log.m108i(TAG, "Probably the IME window token is already invalidated. show() does nothing.");
                    updateWindowState(3);
                    return;
                }
            case 3:
                Log.m108i(TAG, "Not trying to call show() because it was already rejected once.");
                return;
            case 4:
                Log.m108i(TAG, "Ignoring show() because the window is already destroyed.");
                return;
            default:
                throw new IllegalStateException("Unexpected state=" + this.mWindowState);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dismissForDestroyIfNecessary() {
        switch (this.mWindowState) {
            case 0:
            case 1:
                updateWindowState(4);
                return;
            case 2:
                try {
                    getWindow().setWindowAnimations(0);
                    dismiss();
                } catch (WindowManager.BadTokenException e) {
                    Log.m108i(TAG, "Probably the IME window token is already invalidated. No need to dismiss it.");
                }
                updateWindowState(4);
                return;
            case 3:
                Log.m108i(TAG, "Not trying to dismiss the window because it is most likely unnecessary.");
                updateWindowState(4);
                return;
            case 4:
                throw new IllegalStateException("dismissForDestroyIfNecessary can be called only once");
            default:
                throw new IllegalStateException("Unexpected state=" + this.mWindowState);
        }
    }

    private void updateWindowState(int newState) {
        this.mWindowState = newState;
    }

    private static String stateToString(int state) {
        switch (state) {
            case 0:
                return "TOKEN_PENDING";
            case 1:
                return "TOKEN_SET";
            case 2:
                return "SHOWN_AT_LEAST_ONCE";
            case 3:
                return "REJECTED_AT_LEAST_ONCE";
            case 4:
                return "DESTROYED";
            default:
                throw new IllegalStateException("Unknown state=" + state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        this.mBounds.dumpDebug(proto, 1146756268037L);
        proto.write(1120986464262L, this.mWindowState);
        proto.end(token);
    }
}
