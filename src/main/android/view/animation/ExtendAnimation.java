package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.util.AttributeSet;
import android.view.animation.Animation;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class ExtendAnimation extends Animation {
    private int mFromBottomType;
    private float mFromBottomValue;
    protected Insets mFromInsets;
    private int mFromLeftType;
    private float mFromLeftValue;
    private int mFromRightType;
    private float mFromRightValue;
    private int mFromTopType;
    private float mFromTopValue;
    private int mToBottomType;
    private float mToBottomValue;
    protected Insets mToInsets;
    private int mToLeftType;
    private float mToLeftValue;
    private int mToRightType;
    private float mToRightValue;
    private int mToTopType;
    private float mToTopValue;

    public ExtendAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFromInsets = Insets.NONE;
        this.mToInsets = Insets.NONE;
        this.mFromLeftType = 0;
        this.mFromTopType = 0;
        this.mFromRightType = 0;
        this.mFromBottomType = 0;
        this.mToLeftType = 0;
        this.mToTopType = 0;
        this.mToRightType = 0;
        this.mToBottomType = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ExtendAnimation);
        Animation.Description d = Animation.Description.parseValue(a.peekValue(0), context);
        this.mFromLeftType = d.type;
        this.mFromLeftValue = d.value;
        Animation.Description d2 = Animation.Description.parseValue(a.peekValue(1), context);
        this.mFromTopType = d2.type;
        this.mFromTopValue = d2.value;
        Animation.Description d3 = Animation.Description.parseValue(a.peekValue(2), context);
        this.mFromRightType = d3.type;
        this.mFromRightValue = d3.value;
        Animation.Description d4 = Animation.Description.parseValue(a.peekValue(3), context);
        this.mFromBottomType = d4.type;
        this.mFromBottomValue = d4.value;
        Animation.Description d5 = Animation.Description.parseValue(a.peekValue(4), context);
        this.mToLeftType = d5.type;
        this.mToLeftValue = d5.value;
        Animation.Description d6 = Animation.Description.parseValue(a.peekValue(5), context);
        this.mToTopType = d6.type;
        this.mToTopValue = d6.value;
        Animation.Description d7 = Animation.Description.parseValue(a.peekValue(6), context);
        this.mToRightType = d7.type;
        this.mToRightValue = d7.value;
        Animation.Description d8 = Animation.Description.parseValue(a.peekValue(7), context);
        this.mToBottomType = d8.type;
        this.mToBottomValue = d8.value;
        a.recycle();
    }

    public ExtendAnimation(Insets fromInsets, Insets toInsets) {
        this.mFromInsets = Insets.NONE;
        this.mToInsets = Insets.NONE;
        this.mFromLeftType = 0;
        this.mFromTopType = 0;
        this.mFromRightType = 0;
        this.mFromBottomType = 0;
        this.mToLeftType = 0;
        this.mToTopType = 0;
        this.mToRightType = 0;
        this.mToBottomType = 0;
        if (fromInsets == null || toInsets == null) {
            throw new RuntimeException("Expected non-null animation outsets");
        }
        this.mFromLeftValue = -fromInsets.left;
        this.mFromTopValue = -fromInsets.top;
        this.mFromRightValue = -fromInsets.right;
        this.mFromBottomValue = -fromInsets.bottom;
        this.mToLeftValue = -toInsets.left;
        this.mToTopValue = -toInsets.top;
        this.mToRightValue = -toInsets.right;
        this.mToBottomValue = -toInsets.bottom;
    }

    public ExtendAnimation(int fromL, int fromT, int fromR, int fromB, int toL, int toT, int toR, int toB) {
        this(Insets.m186of(-fromL, -fromT, -fromR, -fromB), Insets.m186of(-toL, -toT, -toR, -toB));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.Animation
    public void applyTransformation(float it, Transformation tr) {
        int l = this.mFromInsets.left + ((int) ((this.mToInsets.left - this.mFromInsets.left) * it));
        int t = this.mFromInsets.top + ((int) ((this.mToInsets.top - this.mFromInsets.top) * it));
        int r = this.mFromInsets.right + ((int) ((this.mToInsets.right - this.mFromInsets.right) * it));
        int b = this.mFromInsets.bottom + ((int) ((this.mToInsets.bottom - this.mFromInsets.bottom) * it));
        tr.setInsets(l, t, r, b);
    }

    @Override // android.view.animation.Animation
    public boolean willChangeTransformationMatrix() {
        return false;
    }

    @Override // android.view.animation.Animation
    public boolean hasExtension() {
        return this.mFromInsets.left < 0 || this.mFromInsets.top < 0 || this.mFromInsets.right < 0 || this.mFromInsets.bottom < 0;
    }

    @Override // android.view.animation.Animation
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.mFromInsets = Insets.min(Insets.m186of(-((int) resolveSize(this.mFromLeftType, this.mFromLeftValue, width, parentWidth)), -((int) resolveSize(this.mFromTopType, this.mFromTopValue, height, parentHeight)), -((int) resolveSize(this.mFromRightType, this.mFromRightValue, width, parentWidth)), -((int) resolveSize(this.mFromBottomType, this.mFromBottomValue, height, parentHeight))), Insets.NONE);
        this.mToInsets = Insets.min(Insets.m186of(-((int) resolveSize(this.mToLeftType, this.mToLeftValue, width, parentWidth)), -((int) resolveSize(this.mToTopType, this.mToTopValue, height, parentHeight)), -((int) resolveSize(this.mToRightType, this.mToRightValue, width, parentWidth)), -((int) resolveSize(this.mToBottomType, this.mToBottomValue, height, parentHeight))), Insets.NONE);
    }
}
