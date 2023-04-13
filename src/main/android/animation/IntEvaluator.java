package android.animation;
/* loaded from: classes.dex */
public class IntEvaluator implements TypeEvaluator<Integer> {
    @Override // android.animation.TypeEvaluator
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue.intValue();
        return Integer.valueOf((int) (startInt + ((endValue.intValue() - startInt) * fraction)));
    }
}
