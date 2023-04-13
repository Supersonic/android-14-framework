package android.inputmethodservice;

import android.content.Context;
import android.p008os.IBinder;
import android.util.Slog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.android.internal.policy.PhoneWindow;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class InkWindow extends PhoneWindow {
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private View mInkView;
    private InkVisibilityListener mInkViewVisibilityListener;
    private boolean mIsViewAdded;
    private final WindowManager mWindowManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface InkVisibilityListener {
        void onInkViewVisible();
    }

    public InkWindow(Context context) {
        super(context);
        setType(2011);
        WindowManager.LayoutParams attrs = getAttributes();
        attrs.layoutInDisplayCutoutMode = 3;
        attrs.setFitInsetsTypes(0);
        attrs.windowAnimations = -1;
        setAttributes(attrs);
        addFlags(792);
        setBackgroundDrawableResource(17170445);
        setLayout(-1, -1);
        this.mWindowManager = (WindowManager) context.getSystemService(WindowManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initOnly() {
        show(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void show() {
        show(false);
    }

    private void show(boolean keepInvisible) {
        if (getDecorView() == null) {
            Slog.m94i("InputMethodService", "DecorView is not set for InkWindow. show() failed.");
            return;
        }
        getDecorView().setVisibility(keepInvisible ? 4 : 0);
        if (!this.mIsViewAdded) {
            this.mWindowManager.addView(getDecorView(), getAttributes());
            this.mIsViewAdded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hide(boolean remove) {
        if (getDecorView() != null) {
            getDecorView().setVisibility(remove ? 8 : 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setToken(IBinder token) {
        WindowManager.LayoutParams lp = getAttributes();
        lp.token = token;
        setAttributes(lp);
    }

    @Override // com.android.internal.policy.PhoneWindow, android.view.Window
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        View view2 = this.mInkView;
        if (view2 == null) {
            this.mInkView = view;
        } else if (view2 != view) {
            throw new IllegalStateException("Only one Child Inking view is permitted.");
        }
        super.addContentView(view, params);
        initInkViewVisibilityListener();
    }

    @Override // com.android.internal.policy.PhoneWindow, android.view.Window
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        this.mInkView = view;
        super.setContentView(view, params);
        initInkViewVisibilityListener();
    }

    @Override // com.android.internal.policy.PhoneWindow, android.view.Window
    public void setContentView(View view) {
        this.mInkView = view;
        super.setContentView(view);
        initInkViewVisibilityListener();
    }

    @Override // com.android.internal.policy.PhoneWindow, android.view.Window
    public void clearContentView() {
        View view;
        if (this.mGlobalLayoutListener != null && (view = this.mInkView) != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
        }
        this.mGlobalLayoutListener = null;
        this.mInkView = null;
        super.clearContentView();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInkViewVisibilityListener(InkVisibilityListener listener) {
        this.mInkViewVisibilityListener = listener;
        initInkViewVisibilityListener();
    }

    void initInkViewVisibilityListener() {
        if (this.mInkView == null || this.mInkViewVisibilityListener == null || this.mGlobalLayoutListener != null) {
            return;
        }
        this.mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.inputmethodservice.InkWindow.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (InkWindow.this.mInkView != null && InkWindow.this.mInkView.isVisibleToUser()) {
                    if (InkWindow.this.mInkViewVisibilityListener != null) {
                        InkWindow.this.mInkViewVisibilityListener.onInkViewVisible();
                    }
                    InkWindow.this.mInkView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    InkWindow.this.mGlobalLayoutListener = null;
                }
            }
        };
        this.mInkView.getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInkViewVisible() {
        View view;
        return getDecorView().getVisibility() == 0 && (view = this.mInkView) != null && view.isVisibleToUser();
    }
}
