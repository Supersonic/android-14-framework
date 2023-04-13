package android.graphics.drawable;

import android.graphics.Rect;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class RippleComponent {
    protected final Rect mBounds;
    protected float mDensityScale;
    private boolean mHasMaxRadius;
    protected final RippleDrawable mOwner;
    protected float mTargetRadius;

    public RippleComponent(RippleDrawable owner, Rect bounds) {
        this.mOwner = owner;
        this.mBounds = bounds;
    }

    public void onBoundsChange() {
        if (!this.mHasMaxRadius) {
            float targetRadius = getTargetRadius(this.mBounds);
            this.mTargetRadius = targetRadius;
            onTargetRadiusChanged(targetRadius);
        }
    }

    public final void setup(float maxRadius, int densityDpi) {
        if (maxRadius >= 0.0f) {
            this.mHasMaxRadius = true;
            this.mTargetRadius = maxRadius;
        } else {
            this.mTargetRadius = getTargetRadius(this.mBounds);
        }
        this.mDensityScale = densityDpi * 0.00625f;
        onTargetRadiusChanged(this.mTargetRadius);
    }

    private static float getTargetRadius(Rect bounds) {
        float halfWidth = bounds.width() / 2.0f;
        float halfHeight = bounds.height() / 2.0f;
        return (float) Math.sqrt((halfWidth * halfWidth) + (halfHeight * halfHeight));
    }

    public void getBounds(Rect bounds) {
        int r = (int) Math.ceil(this.mTargetRadius);
        bounds.set(-r, -r, r, r);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void invalidateSelf() {
        this.mOwner.invalidateSelf(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onHotspotBoundsChanged() {
        if (!this.mHasMaxRadius) {
            float targetRadius = getTargetRadius(this.mBounds);
            this.mTargetRadius = targetRadius;
            onTargetRadiusChanged(targetRadius);
        }
    }

    protected void onTargetRadiusChanged(float targetRadius) {
    }
}
