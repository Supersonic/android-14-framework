package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
/* loaded from: classes3.dex */
public class BoringLayout extends Layout implements TextUtils.EllipsizeCallback {
    int mBottom;
    private int mBottomPadding;
    int mDesc;
    private String mDirect;
    private int mEllipsizedCount;
    private int mEllipsizedStart;
    private int mEllipsizedWidth;
    private float mMax;
    private Paint mPaint;
    private int mTopPadding;
    private boolean mUseFallbackLineSpacing;

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad) {
        return new BoringLayout(source, paint, outerWidth, align, spacingMult, spacingAdd, metrics, includePad);
    }

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingmult, float spacingadd, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        return new BoringLayout(source, paint, outerWidth, align, spacingmult, spacingadd, metrics, includePad, ellipsize, ellipsizedWidth);
    }

    public static BoringLayout make(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth, boolean useFallbackLineSpacing) {
        return new BoringLayout(source, paint, outerWidth, align, 1.0f, 0.0f, metrics, includePad, ellipsize, ellipsizedWidth, useFallbackLineSpacing);
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad) {
        replaceWith(source, paint, outerwidth, align, spacingMult, spacingAdd);
        this.mEllipsizedWidth = outerwidth;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        this.mUseFallbackLineSpacing = false;
        init(source, paint, align, metrics, includePad, true, false);
        return this;
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth, boolean useFallbackLineSpacing) {
        boolean trust;
        if (ellipsize == null || ellipsize == TextUtils.TruncateAt.MARQUEE) {
            replaceWith(source, paint, outerWidth, align, 1.0f, 0.0f);
            this.mEllipsizedWidth = outerWidth;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            trust = true;
        } else {
            replaceWith(TextUtils.ellipsize(source, paint, ellipsizedWidth, ellipsize, true, this), paint, outerWidth, align, 1.0f, 0.0f);
            this.mEllipsizedWidth = ellipsizedWidth;
            trust = false;
        }
        this.mUseFallbackLineSpacing = useFallbackLineSpacing;
        init(getText(), paint, align, metrics, includePad, trust, useFallbackLineSpacing);
        return this;
    }

    public BoringLayout replaceOrMake(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        return replaceOrMake(source, paint, outerWidth, align, metrics, includePad, ellipsize, ellipsizedWidth, false);
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad) {
        super(source, paint, outerwidth, align, spacingMult, spacingAdd);
        this.mEllipsizedWidth = outerwidth;
        this.mEllipsizedStart = 0;
        this.mEllipsizedCount = 0;
        this.mUseFallbackLineSpacing = false;
        init(source, paint, align, metrics, includePad, true, false);
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        this(source, paint, outerWidth, align, spacingMult, spacingAdd, metrics, includePad, ellipsize, ellipsizedWidth, false);
    }

    public BoringLayout(CharSequence source, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, Metrics metrics, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth, boolean useFallbackLineSpacing) {
        super(source, paint, outerWidth, align, spacingMult, spacingAdd);
        boolean trust;
        if (ellipsize == null || ellipsize == TextUtils.TruncateAt.MARQUEE) {
            this.mEllipsizedWidth = outerWidth;
            this.mEllipsizedStart = 0;
            this.mEllipsizedCount = 0;
            trust = true;
        } else {
            replaceWith(TextUtils.ellipsize(source, paint, ellipsizedWidth, ellipsize, true, this), paint, outerWidth, align, spacingMult, spacingAdd);
            this.mEllipsizedWidth = ellipsizedWidth;
            trust = false;
        }
        this.mUseFallbackLineSpacing = useFallbackLineSpacing;
        init(getText(), paint, align, metrics, includePad, trust, useFallbackLineSpacing);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0022  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x002d  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x003b  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0043  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    void init(CharSequence source, TextPaint paint, Layout.Alignment align, Metrics metrics, boolean includePad, boolean trustWidth, boolean useFallbackLineSpacing) {
        int spacing;
        if ((source instanceof String) && align == Layout.Alignment.ALIGN_NORMAL) {
            this.mDirect = source.toString();
            this.mPaint = paint;
            if (!includePad) {
                int spacing2 = metrics.bottom - metrics.top;
                this.mDesc = metrics.bottom;
                spacing = spacing2;
            } else {
                int spacing3 = metrics.descent;
                int spacing4 = spacing3 - metrics.ascent;
                this.mDesc = metrics.descent;
                spacing = spacing4;
            }
            this.mBottom = spacing;
            if (!trustWidth) {
                this.mMax = metrics.width;
            } else {
                TextLine line = TextLine.obtain();
                int length = source.length();
                Layout.Directions directions = Layout.DIRS_ALL_LEFT_TO_RIGHT;
                int i = this.mEllipsizedStart;
                line.set(paint, source, 0, length, 1, directions, false, null, i, i + this.mEllipsizedCount, useFallbackLineSpacing);
                this.mMax = (int) Math.ceil(line.metrics(null));
                TextLine.recycle(line);
            }
            if (!includePad) {
                this.mTopPadding = metrics.top - metrics.ascent;
                this.mBottomPadding = metrics.bottom - metrics.descent;
                return;
            }
            return;
        }
        this.mDirect = null;
        this.mPaint = paint;
        if (!includePad) {
        }
        this.mBottom = spacing;
        if (!trustWidth) {
        }
        if (!includePad) {
        }
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint) {
        return isBoring(text, paint, TextDirectionHeuristics.FIRSTSTRONG_LTR, null);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, Metrics metrics) {
        return isBoring(text, paint, TextDirectionHeuristics.FIRSTSTRONG_LTR, metrics);
    }

    private static boolean hasAnyInterestingChars(CharSequence text, int textLength) {
        char[] buffer = TextUtils.obtain(500);
        for (int start = 0; start < textLength; start += 500) {
            try {
                int end = Math.min(start + 500, textLength);
                TextUtils.getChars(text, start, end, buffer, 0);
                int len = end - start;
                for (int i = 0; i < len; i++) {
                    char c = buffer[i];
                    if (c == '\n' || c == '\t' || TextUtils.couldAffectRtl(c)) {
                        TextUtils.recycle(buffer);
                        return true;
                    }
                }
            } finally {
                TextUtils.recycle(buffer);
            }
        }
        return false;
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir, Metrics metrics) {
        return isBoring(text, paint, textDir, false, metrics);
    }

    public static Metrics isBoring(CharSequence text, TextPaint paint, TextDirectionHeuristic textDir, boolean useFallbackLineSpacing, Metrics metrics) {
        Metrics fm;
        int textLength = text.length();
        if (hasAnyInterestingChars(text, textLength)) {
            return null;
        }
        if (textDir == null || !textDir.isRtl(text, 0, textLength)) {
            if (text instanceof Spanned) {
                Spanned sp = (Spanned) text;
                Object[] styles = sp.getSpans(0, textLength, ParagraphStyle.class);
                if (styles.length > 0) {
                    return null;
                }
            }
            if (metrics == null) {
                Metrics fm2 = new Metrics();
                fm = fm2;
            } else {
                metrics.reset();
                fm = metrics;
            }
            TextLine line = TextLine.obtain();
            line.set(paint, text, 0, textLength, 1, Layout.DIRS_ALL_LEFT_TO_RIGHT, false, null, 0, 0, useFallbackLineSpacing);
            fm.width = (int) Math.ceil(line.metrics(fm));
            TextLine.recycle(line);
            return fm;
        }
        return null;
    }

    @Override // android.text.Layout
    public int getHeight() {
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return 1;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        if (line == 0) {
            return 0;
        }
        return this.mBottom;
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        return this.mDesc;
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        if (line == 0) {
            return 0;
        }
        return getText().length();
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return 1;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return false;
    }

    @Override // android.text.Layout
    public float getLineMax(int line) {
        return this.mMax;
    }

    @Override // android.text.Layout
    public float getLineWidth(int line) {
        if (line == 0) {
            return this.mMax;
        }
        return 0.0f;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        return Layout.DIRS_ALL_LEFT_TO_RIGHT;
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int line) {
        return this.mEllipsizedCount;
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        return this.mEllipsizedStart;
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public boolean isFallbackLineSpacingEnabled() {
        return this.mUseFallbackLineSpacing;
    }

    @Override // android.text.Layout
    public void draw(Canvas c, Path highlight, Paint highlightpaint, int cursorOffset) {
        String str = this.mDirect;
        if (str != null && highlight == null) {
            c.drawText(str, 0.0f, this.mBottom - this.mDesc, this.mPaint);
        } else {
            super.draw(c, highlight, highlightpaint, cursorOffset);
        }
    }

    @Override // android.text.TextUtils.EllipsizeCallback
    public void ellipsized(int start, int end) {
        this.mEllipsizedStart = start;
        this.mEllipsizedCount = end - start;
    }

    /* loaded from: classes3.dex */
    public static class Metrics extends Paint.FontMetricsInt {
        public int width;

        @Override // android.graphics.Paint.FontMetricsInt
        public String toString() {
            return super.toString() + " width=" + this.width;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.top = 0;
            this.bottom = 0;
            this.ascent = 0;
            this.descent = 0;
            this.width = 0;
            this.leading = 0;
        }
    }
}
