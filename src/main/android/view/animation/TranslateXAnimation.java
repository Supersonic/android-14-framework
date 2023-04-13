package android.view.animation;

import android.graphics.Matrix;
/* loaded from: classes4.dex */
public class TranslateXAnimation extends TranslateAnimation {
    float[] mTmpValues;

    public TranslateXAnimation(float fromXDelta, float toXDelta) {
        super(fromXDelta, toXDelta, 0.0f, 0.0f);
        this.mTmpValues = new float[9];
    }

    public TranslateXAnimation(int fromXType, float fromXValue, int toXType, float toXValue) {
        super(fromXType, fromXValue, toXType, toXValue, 0, 0.0f, 0, 0.0f);
        this.mTmpValues = new float[9];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.animation.TranslateAnimation, android.view.animation.Animation
    public void applyTransformation(float interpolatedTime, Transformation t) {
        Matrix m = t.getMatrix();
        m.getValues(this.mTmpValues);
        float dx = this.mFromXDelta + ((this.mToXDelta - this.mFromXDelta) * interpolatedTime);
        t.getMatrix().setTranslate(dx, this.mTmpValues[5]);
    }
}
