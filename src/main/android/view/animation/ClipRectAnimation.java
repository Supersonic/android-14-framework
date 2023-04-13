package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Animation;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class ClipRectAnimation extends Animation {
    private int mFromBottomType;
    private float mFromBottomValue;
    private int mFromLeftType;
    private float mFromLeftValue;
    protected final Rect mFromRect;
    private int mFromRightType;
    private float mFromRightValue;
    private int mFromTopType;
    private float mFromTopValue;
    private int mToBottomType;
    private float mToBottomValue;
    private int mToLeftType;
    private float mToLeftValue;
    protected final Rect mToRect;
    private int mToRightType;
    private float mToRightValue;
    private int mToTopType;
    private float mToTopValue;

    public ClipRectAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFromRect = new Rect();
        this.mToRect = new Rect();
        this.mFromLeftType = 0;
        this.mFromTopType = 0;
        this.mFromRightType = 0;
        this.mFromBottomType = 0;
        this.mToLeftType = 0;
        this.mToTopType = 0;
        this.mToRightType = 0;
        this.mToBottomType = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ClipRectAnimation);
        Animation.Description d = Animation.Description.parseValue(a.peekValue(1), context);
        this.mFromLeftType = d.type;
        this.mFromLeftValue = d.value;
        Animation.Description d2 = Animation.Description.parseValue(a.peekValue(3), context);
        this.mFromTopType = d2.type;
        this.mFromTopValue = d2.value;
        Animation.Description d3 = Animation.Description.parseValue(a.peekValue(2), context);
        this.mFromRightType = d3.type;
        this.mFromRightValue = d3.value;
        Animation.Description d4 = Animation.Description.parseValue(a.peekValue(0), context);
        this.mFromBottomType = d4.type;
        this.mFromBottomValue = d4.value;
        Animation.Description d5 = Animation.Description.parseValue(a.peekValue(5), context);
        this.mToLeftType = d5.type;
        this.mToLeftValue = d5.value;
        Animation.Description d6 = Animation.Description.parseValue(a.peekValue(7), context);
        this.mToTopType = d6.type;
        this.mToTopValue = d6.value;
        Animation.Description d7 = Animation.Description.parseValue(a.peekValue(6), context);
        this.mToRightType = d7.type;
        this.mToRightValue = d7.value;
        Animation.Description d8 = Animation.Description.parseValue(a.peekValue(4), context);
        this.mToBottomType = d8.type;
        this.mToBottomValue = d8.value;
        a.recycle();
    }

    public ClipRectAnimation(Rect fromClip, Rect toClip) {
        this.mFromRect = new Rect();
        this.mToRect = new Rect();
        this.mFromLeftType = 0;
        this.mFromTopType = 0;
        this.mFromRightType = 0;
        this.mFromBottomType = 0;
        this.mToLeftType = 0;
        this.mToTopType = 0;
        this.mToRightType = 0;
        this.mToBottomType = 0;
        if (fromClip == null || toClip == null) {
            throw new RuntimeException("Expected non-null animation clip rects");
        }
        this.mFromLeftValue = fromClip.left;
        this.mFromTopValue = fromClip.top;
        this.mFromRightValue = fromClip.right;
        this.mFromBottomValue = fromClip.bottom;
        this.mToLeftValue = toClip.left;
        this.mToTopValue = toClip.top;
        this.mToRightValue = toClip.right;
        this.mToBottomValue = toClip.bottom;
    }

    public ClipRectAnimation(int fromL, int fromT, int fromR, int fromB, int toL, int toT, int toR, int toB) {
        this(new Rect(fromL, fromT, fromR, fromB), new Rect(toL, toT, toR, toB));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.Animation
    public void applyTransformation(float it, Transformation tr) {
        int l = this.mFromRect.left + ((int) ((this.mToRect.left - this.mFromRect.left) * it));
        int t = this.mFromRect.top + ((int) ((this.mToRect.top - this.mFromRect.top) * it));
        int r = this.mFromRect.right + ((int) ((this.mToRect.right - this.mFromRect.right) * it));
        int b = this.mFromRect.bottom + ((int) ((this.mToRect.bottom - this.mFromRect.bottom) * it));
        tr.setClipRect(l, t, r, b);
    }

    @Override // android.view.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override // android.view.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mFromRect.set((int) resolveSize(this.mFromLeftType, this.mFromLeftValue, width, parentWidth), (int) resolveSize(this.mFromTopType, this.mFromTopValue, height, parentHeight), (int) resolveSize(this.mFromRightType, this.mFromRightValue, width, parentWidth), (int) resolveSize(this.mFromBottomType, this.mFromBottomValue, height, parentHeight));
        this.mToRect.set((int) resolveSize(this.mToLeftType, this.mToLeftValue, width, parentWidth), (int) resolveSize(this.mToTopType, this.mToTopValue, height, parentHeight), (int) resolveSize(this.mToRightType, this.mToRightValue, width, parentWidth), (int) resolveSize(this.mToBottomType, this.mToBottomValue, height, parentHeight));
    }
}
