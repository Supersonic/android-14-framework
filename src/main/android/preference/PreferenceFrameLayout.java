package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes3.dex */
public class PreferenceFrameLayout extends FrameLayout {
    private static final int DEFAULT_BORDER_BOTTOM = 0;
    private static final int DEFAULT_BORDER_LEFT = 0;
    private static final int DEFAULT_BORDER_RIGHT = 0;
    private static final int DEFAULT_BORDER_TOP = 0;
    private final int mBorderBottom;
    private final int mBorderLeft;
    private final int mBorderRight;
    private final int mBorderTop;
    private boolean mPaddingApplied;

    public PreferenceFrameLayout(Context context) {
        this(context, null);
    }

    public PreferenceFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, C4057R.attr.preferenceFrameLayoutStyle);
    }

    public PreferenceFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.PreferenceFrameLayout, defStyleAttr, defStyleRes);
        float density = context.getResources().getDisplayMetrics().density;
        int defaultBorderTop = (int) ((density * 0.0f) + 0.5f);
        int defaultBottomPadding = (int) ((density * 0.0f) + 0.5f);
        int defaultLeftPadding = (int) ((density * 0.0f) + 0.5f);
        int defaultRightPadding = (int) ((0.0f * density) + 0.5f);
        this.mBorderTop = a.getDimensionPixelSize(3, defaultBorderTop);
        this.mBorderBottom = a.getDimensionPixelSize(0, defaultBottomPadding);
        this.mBorderLeft = a.getDimensionPixelSize(1, defaultLeftPadding);
        this.mBorderRight = a.getDimensionPixelSize(2, defaultRightPadding);
        a.recycle();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    public void addView(View child) {
        int borderTop = getPaddingTop();
        int borderBottom = getPaddingBottom();
        int borderLeft = getPaddingLeft();
        int borderRight = getPaddingRight();
        ViewGroup.LayoutParams params = child.getLayoutParams();
        LayoutParams layoutParams = params instanceof LayoutParams ? (LayoutParams) child.getLayoutParams() : null;
        if (layoutParams != null && layoutParams.removeBorders) {
            if (this.mPaddingApplied) {
                borderTop -= this.mBorderTop;
                borderBottom -= this.mBorderBottom;
                borderLeft -= this.mBorderLeft;
                borderRight -= this.mBorderRight;
                this.mPaddingApplied = false;
            }
        } else if (!this.mPaddingApplied) {
            borderTop += this.mBorderTop;
            borderBottom += this.mBorderBottom;
            borderLeft += this.mBorderLeft;
            borderRight += this.mBorderRight;
            this.mPaddingApplied = true;
        }
        int previousTop = getPaddingTop();
        int previousBottom = getPaddingBottom();
        int previousLeft = getPaddingLeft();
        int previousRight = getPaddingRight();
        if (previousTop != borderTop || previousBottom != borderBottom || previousLeft != borderLeft || previousRight != borderRight) {
            setPadding(borderLeft, borderTop, borderRight, borderBottom);
        }
        super.addView(child);
    }

    /* loaded from: classes3.dex */
    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean removeBorders;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.removeBorders = false;
            TypedArray a = c.obtainStyledAttributes(attrs, C4057R.styleable.PreferenceFrameLayout_Layout);
            this.removeBorders = a.getBoolean(0, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.removeBorders = false;
        }
    }
}
