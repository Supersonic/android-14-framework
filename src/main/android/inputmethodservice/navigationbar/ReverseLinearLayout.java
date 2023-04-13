package android.inputmethodservice.navigationbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
/* loaded from: classes2.dex */
class ReverseLinearLayout extends LinearLayout {
    private boolean mIsAlternativeOrder;
    private boolean mIsLayoutReverse;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface Reversible {
        void reverse(boolean z);
    }

    ReverseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        updateOrder();
    }

    @Override // android.view.ViewGroup
    public void addView(View child) {
        reverseParams(child.getLayoutParams(), child, this.mIsLayoutReverse);
        if (this.mIsLayoutReverse) {
            super.addView(child, 0);
        } else {
            super.addView(child);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void addView(View child, ViewGroup.LayoutParams params) {
        reverseParams(params, child, this.mIsLayoutReverse);
        if (this.mIsLayoutReverse) {
            super.addView(child, 0, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        updateOrder();
    }

    public void setAlternativeOrder(boolean alternative) {
        this.mIsAlternativeOrder = alternative;
        updateOrder();
    }

    private void updateOrder() {
        boolean isLayoutRtl = getLayoutDirection() == 1;
        boolean isLayoutReverse = this.mIsAlternativeOrder ^ isLayoutRtl;
        if (this.mIsLayoutReverse != isLayoutReverse) {
            int childCount = getChildCount();
            ArrayList<View> childList = new ArrayList<>(childCount);
            for (int i = 0; i < childCount; i++) {
                childList.add(getChildAt(i));
            }
            removeAllViews();
            for (int i2 = childCount - 1; i2 >= 0; i2--) {
                View child = childList.get(i2);
                super.addView(child);
            }
            this.mIsLayoutReverse = isLayoutReverse;
        }
    }

    private static void reverseParams(ViewGroup.LayoutParams params, View child, boolean isLayoutReverse) {
        if (child instanceof Reversible) {
            ((Reversible) child).reverse(isLayoutReverse);
        }
        if (child.getPaddingLeft() == child.getPaddingRight() && child.getPaddingTop() == child.getPaddingBottom()) {
            child.setPadding(child.getPaddingTop(), child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingLeft());
        }
        if (params == null) {
            return;
        }
        int width = params.width;
        params.width = params.height;
        params.height = width;
    }

    /* loaded from: classes2.dex */
    public static class ReverseRelativeLayout extends RelativeLayout implements Reversible {
        private int mDefaultGravity;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ReverseRelativeLayout(Context context) {
            super(context);
            this.mDefaultGravity = 0;
        }

        @Override // android.inputmethodservice.navigationbar.ReverseLinearLayout.Reversible
        public void reverse(boolean isLayoutReverse) {
            updateGravity(isLayoutReverse);
            ReverseLinearLayout.reverseGroup(this, isLayoutReverse);
        }

        public void setDefaultGravity(int gravity) {
            this.mDefaultGravity = gravity;
        }

        public void updateGravity(boolean isLayoutReverse) {
            int i = this.mDefaultGravity;
            if (i == 48 || i == 80) {
                int gravityToApply = this.mDefaultGravity;
                if (isLayoutReverse) {
                    gravityToApply = i != 48 ? 48 : 80;
                }
                if (getGravity() != gravityToApply) {
                    setGravity(gravityToApply);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void reverseGroup(ViewGroup group, boolean isLayoutReverse) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            reverseParams(child.getLayoutParams(), child, isLayoutReverse);
            if (child instanceof ViewGroup) {
                reverseGroup((ViewGroup) child, isLayoutReverse);
            }
        }
    }
}
