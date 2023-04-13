package android.text;

import android.graphics.text.PositionedGlyphs;
/* loaded from: classes3.dex */
public class TextShaper {

    /* loaded from: classes3.dex */
    public interface GlyphsConsumer {
        void accept(int i, int i2, PositionedGlyphs positionedGlyphs, TextPaint textPaint);
    }

    private TextShaper() {
    }

    public static void shapeText(CharSequence text, int start, int count, TextDirectionHeuristic dir, TextPaint paint, GlyphsConsumer consumer) {
        TextLine tl;
        MeasuredParagraph mp = MeasuredParagraph.buildForBidi(text, start, start + count, dir, null);
        TextLine tl2 = TextLine.obtain();
        try {
            try {
                tl2.set(paint, text, start, start + count, mp.getParagraphDir(), mp.getDirections(0, count), false, null, -1, -1, false);
                tl = tl2;
                try {
                    tl.shape(consumer);
                    TextLine.recycle(tl);
                } catch (Throwable th) {
                    th = th;
                    TextLine.recycle(tl);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                tl = tl2;
            }
        } catch (Throwable th3) {
            th = th3;
            tl = tl2;
        }
    }
}
