package android.service.voice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
final class VoiceInteractionWindow extends Dialog {
    private static final boolean DEBUG = false;
    private static final String TAG = "VoiceInteractionWindow";
    private final Rect mBounds;
    private final Callback mCallback;
    private final KeyEvent.DispatcherState mDispatcherState;
    private final int mGravity;
    private final KeyEvent.Callback mKeyEventCallback;
    private final String mName;
    private final boolean mTakesFocus;
    private int mWindowState;
    private final int mWindowType;

    /* loaded from: classes3.dex */
    interface Callback {
        void onBackPressed();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
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
    public VoiceInteractionWindow(Context context, String name, int theme, Callback callback, KeyEvent.Callback keyEventCallback, KeyEvent.DispatcherState dispatcherState, int windowType, int gravity, boolean takesFocus) {
        super(context, theme);
        this.mBounds = new Rect();
        this.mWindowState = 0;
        this.mName = name;
        this.mCallback = callback;
        this.mKeyEventCallback = keyEventCallback;
        this.mDispatcherState = dispatcherState;
        this.mWindowType = windowType;
        this.mGravity = gravity;
        this.mTakesFocus = takesFocus;
        initDockWindow();
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

    private void updateWidthHeight(WindowManager.LayoutParams lp) {
        if (lp.gravity == 48 || lp.gravity == 80) {
            lp.width = -1;
            lp.height = -2;
            return;
        }
        lp.width = -2;
        lp.height = -1;
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback != null && callback.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback != null && callback.onKeyLongPress(keyCode, event)) {
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback != null && callback.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        KeyEvent.Callback callback = this.mKeyEventCallback;
        if (callback != null && callback.onKeyMultiple(keyCode, count, event)) {
            return true;
        }
        return super.onKeyMultiple(keyCode, count, event);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void initDockWindow() {
        int windowSetFlags;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.type = this.mWindowType;
        lp.setTitle(this.mName);
        lp.gravity = this.mGravity;
        updateWidthHeight(lp);
        getWindow().setAttributes(lp);
        int windowModFlags = 266;
        if (!this.mTakesFocus) {
            windowSetFlags = 256 | 8;
        } else {
            windowSetFlags = 256 | 32;
            windowModFlags = 266 | 32;
        }
        getWindow().setFlags(windowSetFlags, windowModFlags);
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
}
