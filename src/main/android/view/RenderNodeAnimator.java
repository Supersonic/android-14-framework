package android.view;

import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.animation.RenderNodeAnimator;
/* loaded from: classes4.dex */
public class RenderNodeAnimator extends android.graphics.animation.RenderNodeAnimator implements RenderNodeAnimator.ViewListener {
    private View mViewTarget;

    public RenderNodeAnimator(int property, float finalValue) {
        super(property, finalValue);
    }

    public RenderNodeAnimator(CanvasProperty<Float> property, float finalValue) {
        super(property, finalValue);
    }

    public RenderNodeAnimator(CanvasProperty<Paint> property, int paintField, float finalValue) {
        super(property, paintField, finalValue);
    }

    public RenderNodeAnimator(int x, int y, float startRadius, float endRadius) {
        super(x, y, startRadius, endRadius);
    }

    @Override // android.graphics.animation.RenderNodeAnimator.ViewListener
    public void onAlphaAnimationStart(float finalAlpha) {
        this.mViewTarget.ensureTransformationInfo();
        this.mViewTarget.setAlphaInternal(finalAlpha);
    }

    @Override // android.graphics.animation.RenderNodeAnimator.ViewListener
    public void invalidateParent(boolean forceRedraw) {
        this.mViewTarget.invalidateViewProperty(true, false);
    }

    public void setTarget(View view) {
        this.mViewTarget = view;
        setViewListener(this);
        setTarget(this.mViewTarget.mRenderNode);
    }
}
