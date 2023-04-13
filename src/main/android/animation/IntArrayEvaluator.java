package android.animation;
/* loaded from: classes.dex */
public class IntArrayEvaluator implements TypeEvaluator<int[]> {
    private int[] mArray;

    public IntArrayEvaluator() {
    }

    public IntArrayEvaluator(int[] reuseArray) {
        this.mArray = reuseArray;
    }

    @Override // android.animation.TypeEvaluator
    public int[] evaluate(float fraction, int[] startValue, int[] endValue) {
        int[] array = this.mArray;
        if (array == null) {
            array = new int[startValue.length];
        }
        for (int i = 0; i < array.length; i++) {
            int start = startValue[i];
            int end = endValue[i];
            array[i] = (int) (start + ((end - start) * fraction));
        }
        return array;
    }
}
