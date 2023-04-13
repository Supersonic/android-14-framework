package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
/* loaded from: classes4.dex */
public class ViewSwitcher extends ViewAnimator {
    ViewFactory mFactory;

    /* loaded from: classes4.dex */
    public interface ViewFactory {
        View makeView();
    }

    public ViewSwitcher(Context context) {
        super(context);
    }

    public ViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.ViewAnimator, android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() >= 2) {
            throw new IllegalStateException("Can't add more than 2 views to a ViewSwitcher");
        }
        super.addView(child, index, params);
    }

    @Override // android.widget.ViewAnimator, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ViewSwitcher.class.getName();
    }

    public View getNextView() {
        int which = this.mWhichChild == 0 ? 1 : 0;
        return getChildAt(which);
    }

    private View obtainView() {
        View child = this.mFactory.makeView();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(-1, -2);
        }
        addView(child, lp);
        return child;
    }

    public void setFactory(ViewFactory factory) {
        this.mFactory = factory;
        obtainView();
        obtainView();
    }

    public void reset() {
        this.mFirstTime = true;
        View v = getChildAt(0);
        if (v != null) {
            v.setVisibility(8);
        }
        View v2 = getChildAt(1);
        if (v2 != null) {
            v2.setVisibility(8);
        }
    }
}
