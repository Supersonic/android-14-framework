package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class ViewAnimator extends FrameLayout {
    boolean mAnimateFirstTime;
    boolean mFirstTime;
    Animation mInAnimation;
    Animation mOutAnimation;
    int mWhichChild;

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<ViewAnimator> {
        private int mAnimateFirstViewId;
        private int mInAnimationId;
        private int mOutAnimationId;
        private boolean mPropertiesMapped = false;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mAnimateFirstViewId = propertyMapper.mapBoolean("animateFirstView", 16843477);
            this.mInAnimationId = propertyMapper.mapObject("inAnimation", 16843127);
            this.mOutAnimationId = propertyMapper.mapObject("outAnimation", 16843128);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(ViewAnimator node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mAnimateFirstViewId, node.getAnimateFirstView());
            propertyReader.readObject(this.mInAnimationId, node.getInAnimation());
            propertyReader.readObject(this.mOutAnimationId, node.getOutAnimation());
        }
    }

    public ViewAnimator(Context context) {
        super(context);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        initViewAnimator(context, null);
    }

    public ViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ViewAnimator);
        saveAttributeDataForStyleable(context, C4057R.styleable.ViewAnimator, attrs, a, 0, 0);
        int resource = a.getResourceId(0, 0);
        if (resource > 0) {
            setInAnimation(context, resource);
        }
        int resource2 = a.getResourceId(1, 0);
        if (resource2 > 0) {
            setOutAnimation(context, resource2);
        }
        boolean flag = a.getBoolean(2, true);
        setAnimateFirstView(flag);
        a.recycle();
        initViewAnimator(context, attrs);
    }

    private void initViewAnimator(Context context, AttributeSet attrs) {
        if (attrs == null) {
            this.mMeasureAllChildren = true;
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.FrameLayout);
        saveAttributeDataForStyleable(context, C4057R.styleable.FrameLayout, attrs, a, 0, 0);
        boolean measureAllChildren = a.getBoolean(0, true);
        setMeasureAllChildren(measureAllChildren);
        a.recycle();
    }

    @RemotableViewMethod
    public void setDisplayedChild(int whichChild) {
        this.mWhichChild = whichChild;
        if (whichChild >= getChildCount()) {
            this.mWhichChild = 0;
        } else if (whichChild < 0) {
            this.mWhichChild = getChildCount() - 1;
        }
        boolean hasFocus = getFocusedChild() != null;
        showOnly(this.mWhichChild);
        if (hasFocus) {
            requestFocus(2);
        }
    }

    public int getDisplayedChild() {
        return this.mWhichChild;
    }

    @RemotableViewMethod
    public void showNext() {
        setDisplayedChild(this.mWhichChild + 1);
    }

    @RemotableViewMethod
    public void showPrevious() {
        setDisplayedChild(this.mWhichChild - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showOnly(int childIndex, boolean animate) {
        Animation animation;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (i == childIndex) {
                if (animate && (animation = this.mInAnimation) != null) {
                    child.startAnimation(animation);
                }
                child.setVisibility(0);
                this.mFirstTime = false;
            } else {
                if (animate && this.mOutAnimation != null && child.getVisibility() == 0) {
                    child.startAnimation(this.mOutAnimation);
                } else if (child.getAnimation() == this.mInAnimation) {
                    child.clearAnimation();
                }
                child.setVisibility(8);
            }
        }
    }

    void showOnly(int childIndex) {
        boolean animate = !this.mFirstTime || this.mAnimateFirstTime;
        showOnly(childIndex, animate);
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int i;
        super.addView(child, index, params);
        if (getChildCount() == 1) {
            child.setVisibility(0);
        } else {
            child.setVisibility(8);
        }
        if (index >= 0 && (i = this.mWhichChild) >= index) {
            setDisplayedChild(i + 1);
        }
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        super.removeAllViews();
        this.mWhichChild = 0;
        this.mFirstTime = true;
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        int index = indexOfChild(view);
        if (index >= 0) {
            removeViewAt(index);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        int childCount = getChildCount();
        if (childCount == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
            return;
        }
        int i = this.mWhichChild;
        if (i >= childCount) {
            setDisplayedChild(childCount - 1);
        } else if (i == index) {
            setDisplayedChild(i);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewInLayout(View view) {
        removeView(view);
    }

    @Override // android.view.ViewGroup
    public void removeViews(int start, int count) {
        super.removeViews(start, count);
        if (getChildCount() == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
            return;
        }
        int i = this.mWhichChild;
        if (i >= start && i < start + count) {
            setDisplayedChild(i);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewsInLayout(int start, int count) {
        removeViews(start, count);
    }

    public View getCurrentView() {
        return getChildAt(this.mWhichChild);
    }

    public Animation getInAnimation() {
        return this.mInAnimation;
    }

    public void setInAnimation(Animation inAnimation) {
        this.mInAnimation = inAnimation;
    }

    public Animation getOutAnimation() {
        return this.mOutAnimation;
    }

    public void setOutAnimation(Animation outAnimation) {
        this.mOutAnimation = outAnimation;
    }

    public void setInAnimation(Context context, int resourceID) {
        setInAnimation(AnimationUtils.loadAnimation(context, resourceID));
    }

    public void setOutAnimation(Context context, int resourceID) {
        setOutAnimation(AnimationUtils.loadAnimation(context, resourceID));
    }

    public boolean getAnimateFirstView() {
        return this.mAnimateFirstTime;
    }

    public void setAnimateFirstView(boolean animate) {
        this.mAnimateFirstTime = animate;
    }

    @Override // android.view.View
    public int getBaseline() {
        return getCurrentView() != null ? getCurrentView().getBaseline() : super.getBaseline();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ViewAnimator.class.getName();
    }
}
