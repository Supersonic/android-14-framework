package android.graphics.text;
/* loaded from: classes.dex */
public class GraphemeBreak {
    private static native void nIsGraphemeBreak(float[] fArr, char[] cArr, int i, int i2, boolean[] zArr);

    private GraphemeBreak() {
    }

    public static void isGraphemeBreak(float[] advances, char[] text, int start, int end, boolean[] isGraphemeBreak) {
        if (start > end) {
            throw new IllegalArgumentException("start is greater than end: start = " + start + " end = " + end);
        }
        if (advances.length < end) {
            throw new IllegalArgumentException("the length of advances is less than endadvances.length = " + advances.length + " end = " + end);
        }
        if (isGraphemeBreak.length < end - start) {
            throw new IndexOutOfBoundsException("isGraphemeBreak doesn't have enough space to receive the result, isGraphemeBreak.length: " + isGraphemeBreak.length + " needed space: " + (end - start));
        }
        nIsGraphemeBreak(advances, text, start, end, isGraphemeBreak);
    }
}
