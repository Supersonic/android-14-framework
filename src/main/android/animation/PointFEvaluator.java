package android.animation;

import android.graphics.PointF;
/* loaded from: classes.dex */
public class PointFEvaluator implements TypeEvaluator<PointF> {
    private PointF mPoint;

    public PointFEvaluator() {
    }

    public PointFEvaluator(PointF reuse) {
        this.mPoint = reuse;
    }

    @Override // android.animation.TypeEvaluator
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float x = startValue.f78x + ((endValue.f78x - startValue.f78x) * fraction);
        float y = startValue.f79y + ((endValue.f79y - startValue.f79y) * fraction);
        PointF pointF = this.mPoint;
        if (pointF != null) {
            pointF.set(x, y);
            return this.mPoint;
        }
        return new PointF(x, y);
    }
}
