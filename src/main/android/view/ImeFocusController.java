package android.view;

import android.util.proto.ProtoOutputStream;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
/* loaded from: classes4.dex */
public final class ImeFocusController {
    private static final boolean DEBUG = false;
    private static final String TAG = "ImeFocusController";
    private InputMethodManagerDelegate mDelegate;
    private boolean mHasImeFocus = false;
    private final ViewRootImpl mViewRootImpl;

    /* loaded from: classes4.dex */
    public interface InputMethodManagerDelegate {
        void onPostWindowGainedFocus(View view, WindowManager.LayoutParams layoutParams);

        void onPreWindowGainedFocus(ViewRootImpl viewRootImpl);

        void onScheduledCheckFocus(ViewRootImpl viewRootImpl);

        void onViewDetachedFromWindow(View view, ViewRootImpl viewRootImpl);

        void onViewFocusChanged(View view, boolean z);

        void onWindowDismissed(ViewRootImpl viewRootImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImeFocusController(ViewRootImpl viewRootImpl) {
        this.mViewRootImpl = viewRootImpl;
    }

    private InputMethodManagerDelegate getImmDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = ((InputMethodManager) this.mViewRootImpl.mContext.getSystemService(InputMethodManager.class)).getDelegate();
        }
        return this.mDelegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMovedToDisplay() {
        this.mDelegate = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTraversal(boolean hasWindowFocus, WindowManager.LayoutParams windowAttribute) {
        boolean hasImeFocus = WindowManager.LayoutParams.mayUseInputMethod(windowAttribute.flags);
        if (!hasWindowFocus || isInLocalFocusMode(windowAttribute) || hasImeFocus == this.mHasImeFocus) {
            return;
        }
        this.mHasImeFocus = hasImeFocus;
        if (hasImeFocus) {
            getImmDelegate().onPreWindowGainedFocus(this.mViewRootImpl);
            View focusedView = this.mViewRootImpl.mView.findFocus();
            View viewForWindowFocus = focusedView != null ? focusedView : this.mViewRootImpl.mView;
            getImmDelegate().onPostWindowGainedFocus(viewForWindowFocus, windowAttribute);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPreWindowFocus(boolean hasWindowFocus, WindowManager.LayoutParams windowAttribute) {
        boolean mayUseInputMethod = WindowManager.LayoutParams.mayUseInputMethod(windowAttribute.flags);
        this.mHasImeFocus = mayUseInputMethod;
        if (!hasWindowFocus || !mayUseInputMethod || isInLocalFocusMode(windowAttribute)) {
            return;
        }
        getImmDelegate().onPreWindowGainedFocus(this.mViewRootImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onPostWindowFocus(View focusedView, boolean hasWindowFocus, WindowManager.LayoutParams windowAttribute) {
        if (!hasWindowFocus || !this.mHasImeFocus || isInLocalFocusMode(windowAttribute)) {
            return;
        }
        View viewForWindowFocus = focusedView != null ? focusedView : this.mViewRootImpl.mView;
        getImmDelegate().onPostWindowGainedFocus(viewForWindowFocus, windowAttribute);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onScheduledCheckFocus() {
        getImmDelegate().onScheduledCheckFocus(this.mViewRootImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onViewFocusChanged(View view, boolean hasFocus) {
        getImmDelegate().onViewFocusChanged(view, hasFocus);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onViewDetachedFromWindow(View view) {
        getImmDelegate().onViewDetachedFromWindow(view, this.mViewRootImpl);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onWindowDismissed() {
        getImmDelegate().onWindowDismissed(this.mViewRootImpl);
        this.mHasImeFocus = false;
    }

    private static boolean isInLocalFocusMode(WindowManager.LayoutParams windowAttribute) {
        return (windowAttribute.flags & 268435456) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int onProcessImeInputStage(Object token, InputEvent event, WindowManager.LayoutParams windowAttribute, InputMethodManager.FinishedInputEventCallback callback) {
        InputMethodManager imm;
        if (!this.mHasImeFocus || isInLocalFocusMode(windowAttribute) || (imm = (InputMethodManager) this.mViewRootImpl.mContext.getSystemService(InputMethodManager.class)) == null) {
            return 0;
        }
        return imm.dispatchInputEvent(event, token, callback, this.mViewRootImpl.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasImeFocus() {
        return this.mHasImeFocus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1133871366145L, this.mHasImeFocus);
        proto.end(token);
    }
}
