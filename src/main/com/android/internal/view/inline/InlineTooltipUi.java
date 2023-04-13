package com.android.internal.view.inline;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.transition.Transition;
import android.util.Slog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.autofill.AutofillFeatureFlags;
import android.view.autofill.Helper;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.inline.InlineContentView;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
/* loaded from: classes2.dex */
public final class InlineTooltipUi extends PopupWindow implements AutoCloseable {
    private static final int FIRST_TIME_SHOW_DEFAULT_DELAY_MS = 250;
    private static final String TAG = "InlineTooltipUi";
    private final ViewGroup mContentContainer;
    private DelayShowRunnable mDelayShowTooltip;
    private boolean mHasEverDetached;
    private boolean mShowing;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private final WindowManager mWm;
    private boolean mDelayShowAtStart = true;
    private boolean mDelaying = false;
    private final Rect mTmpRect = new Rect();
    private final View.OnAttachStateChangeListener mAnchorOnAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: com.android.internal.view.inline.InlineTooltipUi.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            InlineTooltipUi.this.mHasEverDetached = true;
            InlineTooltipUi.this.dismiss();
        }
    };
    private final View.OnLayoutChangeListener mAnchoredOnLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: com.android.internal.view.inline.InlineTooltipUi.2
        int mHeight;

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (!InlineTooltipUi.this.mHasEverDetached && this.mHeight != bottom - top) {
                this.mHeight = bottom - top;
                InlineTooltipUi.this.adjustPosition();
            }
        }
    };
    private int mShowDelayConfigMs = DeviceConfig.getInt(Context.AUTOFILL_MANAGER_SERVICE, AutofillFeatureFlags.DEVICE_CONFIG_AUTOFILL_TOOLTIP_SHOW_UP_DELAY, 250);

    public InlineTooltipUi(Context context) {
        this.mContentContainer = new LinearLayout(new ContextWrapper(context));
        this.mWm = (WindowManager) context.getSystemService(WindowManager.class);
        setTouchModal(false);
        setOutsideTouchable(true);
        setInputMethodMode(2);
        setFocusable(false);
    }

    public void setTooltipView(InlineContentView v) {
        this.mContentContainer.removeAllViews();
        this.mContentContainer.addView(v);
        this.mContentContainer.setVisibility(0);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        dismiss();
    }

    @Override // android.widget.PopupWindow
    protected boolean hasContentView() {
        return true;
    }

    @Override // android.widget.PopupWindow
    protected boolean hasDecorView() {
        return true;
    }

    @Override // android.widget.PopupWindow
    protected WindowManager.LayoutParams getDecorViewLayoutParams() {
        return this.mWindowLayoutParams;
    }

    public void update(View anchor) {
        if (anchor == null) {
            View oldAnchor = getAnchor();
            if (oldAnchor != null) {
                removeDelayShowTooltip(oldAnchor);
            }
        } else if (this.mDelayShowAtStart) {
            this.mDelayShowAtStart = false;
            this.mDelaying = true;
            if (this.mDelayShowTooltip == null) {
                this.mDelayShowTooltip = new DelayShowRunnable(anchor);
            }
            int delayTimeMs = this.mShowDelayConfigMs;
            try {
                float scale = WindowManager.fixScale(Settings.Global.getFloat(anchor.getContext().getContentResolver(), "animator_duration_scale"));
                delayTimeMs = (int) (delayTimeMs * scale);
            } catch (Settings.SettingNotFoundException e) {
            }
            anchor.postDelayed(this.mDelayShowTooltip, delayTimeMs);
        } else if (!this.mDelaying) {
            updateInner(anchor);
        }
    }

    private void removeDelayShowTooltip(View anchor) {
        DelayShowRunnable delayShowRunnable = this.mDelayShowTooltip;
        if (delayShowRunnable != null) {
            anchor.removeCallbacks(delayShowRunnable);
            this.mDelayShowTooltip = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInner(View anchor) {
        if (this.mHasEverDetached) {
            return;
        }
        setWindowLayoutType(1005);
        int offsetY = (-anchor.getHeight()) - getPreferHeight(anchor);
        if (!isShowing()) {
            setWidth(-2);
            setHeight(-2);
            showAsDropDown(anchor, 0, offsetY, 49);
            return;
        }
        update(anchor, 0, offsetY, -2, -2);
    }

    private int getPreferHeight(View anchor) {
        int achoredHeight = this.mContentContainer.getHeight();
        return achoredHeight == 0 ? anchor.getHeight() : achoredHeight;
    }

    @Override // android.widget.PopupWindow
    protected boolean findDropDownPosition(View anchor, WindowManager.LayoutParams outParams, int xOffset, int yOffset, int width, int height, int gravity, boolean allowScroll) {
        boolean isAbove = super.findDropDownPosition(anchor, outParams, xOffset, yOffset, width, height, gravity, allowScroll);
        ViewParent parent = anchor.getParent();
        if (parent instanceof View) {
            Rect r = this.mTmpRect;
            ((View) parent).getGlobalVisibleRect(r);
            if (isAbove) {
                outParams.f505y = r.top - getPreferHeight(anchor);
            } else {
                outParams.f505y = r.bottom + 1;
            }
        }
        return isAbove;
    }

    @Override // android.widget.PopupWindow
    protected void update(View anchor, WindowManager.LayoutParams params) {
        if (anchor.isVisibleToUser()) {
            show(params);
        } else {
            hide();
        }
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (isShowing()) {
            return;
        }
        setShowing(true);
        setDropDown(true);
        attachToAnchor(anchor, xoff, yoff, gravity);
        WindowManager.LayoutParams p = createPopupLayoutParams(anchor.getWindowToken());
        this.mWindowLayoutParams = p;
        boolean aboveAnchor = findDropDownPosition(anchor, p, xoff, yoff, p.width, p.height, gravity, getAllowScrollingAnchorParent());
        updateAboveAnchor(aboveAnchor);
        p.accessibilityIdOfAnchor = anchor.getAccessibilityViewId();
        p.packageName = anchor.getContext().getPackageName();
        show(p);
    }

    @Override // android.widget.PopupWindow
    protected void attachToAnchor(View anchor, int xoff, int yoff, int gravity) {
        super.attachToAnchor(anchor, xoff, yoff, gravity);
        anchor.addOnAttachStateChangeListener(this.mAnchorOnAttachStateChangeListener);
    }

    @Override // android.widget.PopupWindow
    protected void detachFromAnchor() {
        View anchor = getAnchor();
        if (anchor != null) {
            anchor.removeOnAttachStateChangeListener(this.mAnchorOnAttachStateChangeListener);
            removeDelayShowTooltip(anchor);
        }
        this.mHasEverDetached = true;
        super.detachFromAnchor();
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        if (!isShowing() || isTransitioningToDismiss()) {
            return;
        }
        setTransitioningToDismiss(true);
        hide();
        detachFromAnchor();
        if (getOnDismissListener() != null) {
            getOnDismissListener().onDismiss();
        }
        super.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustPosition() {
        View anchor = getAnchor();
        if (anchor == null) {
            return;
        }
        update(anchor);
    }

    private void show(WindowManager.LayoutParams params) {
        this.mWindowLayoutParams = params;
        try {
            params.packageName = "android";
            params.setTitle("Autofill Inline Tooltip");
            if (!this.mShowing) {
                if (Helper.sVerbose) {
                    Slog.m92v(TAG, "show()");
                }
                params.flags = 40;
                params.privateFlags |= 4194304;
                this.mContentContainer.addOnLayoutChangeListener(this.mAnchoredOnLayoutChangeListener);
                this.mWm.addView(this.mContentContainer, params);
                this.mShowing = true;
                return;
            }
            this.mWm.updateViewLayout(this.mContentContainer, params);
        } catch (WindowManager.BadTokenException e) {
            Slog.m98d(TAG, "Failed with token " + params.token + " gone.");
        } catch (IllegalStateException e2) {
            Slog.wtf(TAG, "Exception showing window " + params, e2);
        }
    }

    private void hide() {
        try {
            if (this.mShowing) {
                if (Helper.sVerbose) {
                    Slog.m92v(TAG, "hide()");
                }
                this.mContentContainer.removeOnLayoutChangeListener(this.mAnchoredOnLayoutChangeListener);
                this.mWm.removeView(this.mContentContainer);
                this.mShowing = false;
            }
        } catch (IllegalStateException e) {
            Slog.m95e(TAG, "Exception hiding window ", e);
        }
    }

    @Override // android.widget.PopupWindow
    public int getAnimationStyle() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public Drawable getBackground() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public View getContentView() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public float getElevation() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public Transition getEnterTransition() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public Transition getExitTransition() {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public void setBackgroundDrawable(Drawable background) {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public void setContentView(View contentView) {
        if (contentView != null) {
            throw new IllegalStateException("You can't call this!");
        }
    }

    @Override // android.widget.PopupWindow
    public void setElevation(float elevation) {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public void setEnterTransition(Transition enterTransition) {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public void setExitTransition(Transition exitTransition) {
        throw new IllegalStateException("You can't call this!");
    }

    @Override // android.widget.PopupWindow
    public void setTouchInterceptor(View.OnTouchListener l) {
        throw new IllegalStateException("You can't call this!");
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        if (this.mContentContainer != null) {
            pw.print(prefix);
            pw.print("Window: ");
            String prefix2 = prefix + "  ";
            pw.println();
            pw.print(prefix2);
            pw.print("showing: ");
            pw.println(this.mShowing);
            pw.print(prefix2);
            pw.print("view: ");
            pw.println(this.mContentContainer);
            if (this.mWindowLayoutParams != null) {
                pw.print(prefix2);
                pw.print("params: ");
                pw.println(this.mWindowLayoutParams);
            }
            pw.print(prefix2);
            pw.print("screen coordinates: ");
            ViewGroup viewGroup = this.mContentContainer;
            if (viewGroup == null) {
                pw.println("N/A");
                return;
            }
            int[] coordinates = viewGroup.getLocationOnScreen();
            pw.print(coordinates[0]);
            pw.print("x");
            pw.println(coordinates[1]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DelayShowRunnable implements Runnable {
        WeakReference<View> mAnchor;

        DelayShowRunnable(View anchor) {
            this.mAnchor = new WeakReference<>(anchor);
        }

        @Override // java.lang.Runnable
        public void run() {
            InlineTooltipUi.this.mDelaying = false;
            View anchor = this.mAnchor.get();
            if (anchor != null) {
                InlineTooltipUi.this.updateInner(anchor);
            }
        }

        public void setAnchor(View anchor) {
            this.mAnchor.clear();
            this.mAnchor = new WeakReference<>(anchor);
        }
    }
}
