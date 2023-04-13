package android.text;

import android.graphics.TemporaryBuffer;
import android.graphics.text.GraphemeBreak;
import android.text.AutoGrowArray;
/* loaded from: classes3.dex */
public class GraphemeClusterSegmentFinder extends SegmentFinder {
    private static AutoGrowArray.FloatArray sTempAdvances = null;
    private final boolean[] mIsGraphemeBreak;

    public GraphemeClusterSegmentFinder(CharSequence text, TextPaint textPaint) {
        AutoGrowArray.FloatArray floatArray = sTempAdvances;
        if (floatArray == null) {
            sTempAdvances = new AutoGrowArray.FloatArray(text.length());
        } else if (floatArray.size() < text.length()) {
            sTempAdvances.resize(text.length());
        }
        boolean[] zArr = new boolean[text.length()];
        this.mIsGraphemeBreak = zArr;
        float[] advances = sTempAdvances.getRawArray();
        char[] chars = TemporaryBuffer.obtain(text.length());
        TextUtils.getChars(text, 0, text.length(), chars, 0);
        textPaint.getTextWidths(chars, 0, text.length(), advances);
        GraphemeBreak.isGraphemeBreak(advances, chars, 0, text.length(), zArr);
        TemporaryBuffer.recycle(chars);
    }

    private int previousBoundary(int offset) {
        if (offset <= 0) {
            return -1;
        }
        do {
            offset--;
            if (offset <= 0) {
                break;
            }
        } while (!this.mIsGraphemeBreak[offset]);
        return offset;
    }

    private int nextBoundary(int offset) {
        boolean[] zArr;
        if (offset >= this.mIsGraphemeBreak.length) {
            return -1;
        }
        do {
            offset++;
            zArr = this.mIsGraphemeBreak;
            if (offset >= zArr.length) {
                break;
            }
        } while (!zArr[offset]);
        return offset;
    }

    @Override // android.text.SegmentFinder
    public int previousStartBoundary(int offset) {
        return previousBoundary(offset);
    }

    @Override // android.text.SegmentFinder
    public int previousEndBoundary(int offset) {
        int boundary;
        if (offset == 0 || (boundary = previousBoundary(offset)) == -1 || previousBoundary(boundary) == -1) {
            return -1;
        }
        return boundary;
    }

    @Override // android.text.SegmentFinder
    public int nextStartBoundary(int offset) {
        int boundary;
        if (offset == this.mIsGraphemeBreak.length || (boundary = nextBoundary(offset)) == -1 || nextBoundary(boundary) == -1) {
            return -1;
        }
        return boundary;
    }

    @Override // android.text.SegmentFinder
    public int nextEndBoundary(int offset) {
        return nextBoundary(offset);
    }
}
