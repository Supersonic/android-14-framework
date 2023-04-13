package android.graphics.text;

import android.graphics.Paint;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class TextRunShaper {
    private static native long nativeShapeTextRun(String str, int i, int i2, int i3, int i4, boolean z, long j);

    private static native long nativeShapeTextRun(char[] cArr, int i, int i2, int i3, int i4, boolean z, long j);

    private TextRunShaper() {
    }

    public static PositionedGlyphs shapeTextRun(char[] text, int start, int count, int contextStart, int contextCount, float xOffset, float yOffset, boolean isRtl, Paint paint) {
        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(paint);
        return new PositionedGlyphs(nativeShapeTextRun(text, start, count, contextStart, contextCount, isRtl, paint.getNativeInstance()), xOffset, yOffset);
    }

    public static PositionedGlyphs shapeTextRun(CharSequence text, int start, int count, int contextStart, int contextCount, float xOffset, float yOffset, boolean isRtl, Paint paint) {
        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(paint);
        if (text instanceof String) {
            return new PositionedGlyphs(nativeShapeTextRun((String) text, start, count, contextStart, contextCount, isRtl, paint.getNativeInstance()), xOffset, yOffset);
        }
        char[] buf = new char[contextCount];
        TextUtils.getChars(text, contextStart, contextStart + contextCount, buf, 0);
        return new PositionedGlyphs(nativeShapeTextRun(buf, start - contextStart, count, 0, contextCount, isRtl, paint.getNativeInstance()), xOffset, yOffset);
    }
}
