package android.text;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.text.LineBreakConfig;
import android.graphics.text.MeasuredText;
import android.text.AutoGrowArray;
import android.text.Layout;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import android.util.Pools;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class MeasuredParagraph {
    private static final char OBJECT_REPLACEMENT_CHARACTER = 65532;
    private static final Pools.SynchronizedPool<MeasuredParagraph> sPool = new Pools.SynchronizedPool<>(1);
    private Paint.FontMetricsInt mCachedFm;
    private char[] mCopiedBuffer;
    private boolean mLtrWithoutBidi;
    private MeasuredText mMeasuredText;
    private int mParaDir;
    private Spanned mSpanned;
    private int mTextLength;
    private int mTextStart;
    private float mWholeWidth;
    private AutoGrowArray.ByteArray mLevels = new AutoGrowArray.ByteArray();
    private AutoGrowArray.FloatArray mWidths = new AutoGrowArray.FloatArray();
    private AutoGrowArray.IntArray mSpanEndCache = new AutoGrowArray.IntArray(4);
    private AutoGrowArray.IntArray mFontMetrics = new AutoGrowArray.IntArray(16);
    private TextPaint mCachedPaint = new TextPaint();

    private MeasuredParagraph() {
    }

    private static MeasuredParagraph obtain() {
        MeasuredParagraph mt = sPool.acquire();
        return mt != null ? mt : new MeasuredParagraph();
    }

    public void recycle() {
        release();
        sPool.release(this);
    }

    public void release() {
        reset();
        this.mLevels.clearWithReleasingLargeArray();
        this.mWidths.clearWithReleasingLargeArray();
        this.mFontMetrics.clearWithReleasingLargeArray();
        this.mSpanEndCache.clearWithReleasingLargeArray();
    }

    private void reset() {
        this.mSpanned = null;
        this.mCopiedBuffer = null;
        this.mWholeWidth = 0.0f;
        this.mLevels.clear();
        this.mWidths.clear();
        this.mFontMetrics.clear();
        this.mSpanEndCache.clear();
        this.mMeasuredText = null;
    }

    public int getTextLength() {
        return this.mTextLength;
    }

    public char[] getChars() {
        return this.mCopiedBuffer;
    }

    public int getParagraphDir() {
        return this.mParaDir;
    }

    public Layout.Directions getDirections(int start, int end) {
        if (this.mLtrWithoutBidi) {
            return Layout.DIRS_ALL_LEFT_TO_RIGHT;
        }
        int length = end - start;
        return AndroidBidi.directions(this.mParaDir, this.mLevels.getRawArray(), start, this.mCopiedBuffer, start, length);
    }

    public float getWholeWidth() {
        return this.mWholeWidth;
    }

    public AutoGrowArray.FloatArray getWidths() {
        return this.mWidths;
    }

    public AutoGrowArray.IntArray getSpanEndCache() {
        return this.mSpanEndCache;
    }

    public AutoGrowArray.IntArray getFontMetrics() {
        return this.mFontMetrics;
    }

    public MeasuredText getMeasuredText() {
        return this.mMeasuredText;
    }

    public float getWidth(int start, int end) {
        MeasuredText measuredText = this.mMeasuredText;
        if (measuredText == null) {
            float[] widths = this.mWidths.getRawArray();
            float r = 0.0f;
            for (int i = start; i < end; i++) {
                r += widths[i];
            }
            return r;
        }
        return measuredText.getWidth(start, end);
    }

    public void getBounds(int start, int end, Rect bounds) {
        this.mMeasuredText.getBounds(start, end, bounds);
    }

    public void getFontMetricsInt(int start, int end, Paint.FontMetricsInt fmi) {
        this.mMeasuredText.getFontMetricsInt(start, end, fmi);
    }

    public float getCharWidthAt(int offset) {
        return this.mMeasuredText.getCharWidthAt(offset);
    }

    public static MeasuredParagraph buildForBidi(CharSequence text, int start, int end, TextDirectionHeuristic textDir, MeasuredParagraph recycle) {
        MeasuredParagraph mt = recycle == null ? obtain() : recycle;
        mt.resetAndAnalyzeBidi(text, start, end, textDir);
        return mt;
    }

    public static MeasuredParagraph buildForMeasurement(TextPaint paint, CharSequence text, int start, int end, TextDirectionHeuristic textDir, MeasuredParagraph recycle) {
        MeasuredParagraph mt = recycle == null ? obtain() : recycle;
        mt.resetAndAnalyzeBidi(text, start, end, textDir);
        mt.mWidths.resize(mt.mTextLength);
        if (mt.mTextLength == 0) {
            return mt;
        }
        if (mt.mSpanned == null) {
            mt.applyMetricsAffectingSpan(paint, null, null, start, end, null);
        } else {
            int spanStart = start;
            while (spanStart < end) {
                int spanEnd = mt.mSpanned.nextSpanTransition(spanStart, end, MetricAffectingSpan.class);
                MetricAffectingSpan[] spans = (MetricAffectingSpan[]) mt.mSpanned.getSpans(spanStart, spanEnd, MetricAffectingSpan.class);
                mt.applyMetricsAffectingSpan(paint, null, (MetricAffectingSpan[]) TextUtils.removeEmptySpans(spans, mt.mSpanned, MetricAffectingSpan.class), spanStart, spanEnd, null);
                spanStart = spanEnd;
            }
        }
        return mt;
    }

    public static MeasuredParagraph buildForStaticLayout(TextPaint paint, LineBreakConfig lineBreakConfig, CharSequence text, int start, int end, TextDirectionHeuristic textDir, int hyphenationMode, boolean computeLayout, MeasuredParagraph hint, MeasuredParagraph recycle) {
        MeasuredParagraph mt;
        MeasuredParagraph mt2 = recycle == null ? obtain() : recycle;
        mt2.resetAndAnalyzeBidi(text, start, end, textDir);
        MeasuredText.Builder builder = hint == null ? new MeasuredText.Builder(mt2.mCopiedBuffer).setComputeHyphenation(hyphenationMode).setComputeLayout(computeLayout) : new MeasuredText.Builder(hint.mMeasuredText);
        if (mt2.mTextLength == 0) {
            mt2.mMeasuredText = builder.build();
            return mt2;
        }
        if (mt2.mSpanned == null) {
            mt2.applyMetricsAffectingSpan(paint, lineBreakConfig, null, start, end, builder);
            mt2.mSpanEndCache.append(end);
            mt = mt2;
        } else {
            int spanStart = start;
            while (spanStart < end) {
                int spanEnd = mt2.mSpanned.nextSpanTransition(spanStart, end, MetricAffectingSpan.class);
                MetricAffectingSpan[] spans = (MetricAffectingSpan[]) mt2.mSpanned.getSpans(spanStart, spanEnd, MetricAffectingSpan.class);
                MeasuredParagraph mt3 = mt2;
                mt2.applyMetricsAffectingSpan(paint, lineBreakConfig, (MetricAffectingSpan[]) TextUtils.removeEmptySpans(spans, mt2.mSpanned, MetricAffectingSpan.class), spanStart, spanEnd, builder);
                mt3.mSpanEndCache.append(spanEnd);
                spanStart = spanEnd;
                mt2 = mt3;
            }
            mt = mt2;
        }
        mt.mMeasuredText = builder.build();
        return mt;
    }

    private void resetAndAnalyzeBidi(CharSequence text, int start, int end, TextDirectionHeuristic textDir) {
        int bidiRequest;
        reset();
        this.mSpanned = text instanceof Spanned ? (Spanned) text : null;
        this.mTextStart = start;
        int i = end - start;
        this.mTextLength = i;
        char[] cArr = this.mCopiedBuffer;
        if (cArr == null || cArr.length != i) {
            this.mCopiedBuffer = new char[i];
        }
        TextUtils.getChars(text, start, end, this.mCopiedBuffer, 0);
        Spanned spanned = this.mSpanned;
        if (spanned != null) {
            ReplacementSpan[] spans = (ReplacementSpan[]) spanned.getSpans(start, end, ReplacementSpan.class);
            for (int i2 = 0; i2 < spans.length; i2++) {
                int startInPara = this.mSpanned.getSpanStart(spans[i2]) - start;
                int endInPara = this.mSpanned.getSpanEnd(spans[i2]) - start;
                if (startInPara < 0) {
                    startInPara = 0;
                }
                if (endInPara > this.mTextLength) {
                    endInPara = this.mTextLength;
                }
                Arrays.fill(this.mCopiedBuffer, startInPara, endInPara, (char) OBJECT_REPLACEMENT_CHARACTER);
            }
        }
        if ((textDir == TextDirectionHeuristics.LTR || textDir == TextDirectionHeuristics.FIRSTSTRONG_LTR || textDir == TextDirectionHeuristics.ANYRTL_LTR) && TextUtils.doesNotNeedBidi(this.mCopiedBuffer, 0, this.mTextLength)) {
            this.mLevels.clear();
            this.mParaDir = 1;
            this.mLtrWithoutBidi = true;
            return;
        }
        if (textDir == TextDirectionHeuristics.LTR) {
            bidiRequest = 1;
        } else if (textDir == TextDirectionHeuristics.RTL) {
            bidiRequest = -1;
        } else if (textDir == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            bidiRequest = 2;
        } else if (textDir == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
            bidiRequest = -2;
        } else {
            boolean isRtl = textDir.isRtl(this.mCopiedBuffer, 0, this.mTextLength);
            bidiRequest = isRtl ? -1 : 1;
        }
        this.mLevels.resize(this.mTextLength);
        this.mParaDir = AndroidBidi.bidi(bidiRequest, this.mCopiedBuffer, this.mLevels.getRawArray());
        this.mLtrWithoutBidi = false;
    }

    private void applyReplacementRun(ReplacementSpan replacement, int start, int end, TextPaint paint, MeasuredText.Builder builder) {
        Spanned spanned = this.mSpanned;
        int i = this.mTextStart;
        float width = replacement.getSize(paint, spanned, start + i, end + i, this.mCachedFm);
        if (builder == null) {
            this.mWidths.set(start, width);
            if (end > start + 1) {
                Arrays.fill(this.mWidths.getRawArray(), start + 1, end, 0.0f);
            }
            this.mWholeWidth += width;
            return;
        }
        builder.appendReplacementRun(paint, end - start, width);
    }

    private void applyStyleRun(int start, int end, TextPaint paint, LineBreakConfig config, MeasuredText.Builder builder) {
        int levelEnd;
        boolean z = false;
        if (this.mLtrWithoutBidi) {
            if (builder == null) {
                this.mWholeWidth += paint.getTextRunAdvances(this.mCopiedBuffer, start, end - start, start, end - start, false, this.mWidths.getRawArray(), start);
                return;
            } else {
                builder.appendStyleRun(paint, config, end - start, false);
                return;
            }
        }
        byte level = this.mLevels.get(start);
        byte level2 = level;
        int levelStart = start;
        int levelEnd2 = start + 1;
        while (true) {
            if (levelEnd2 == end || this.mLevels.get(levelEnd2) != level2) {
                boolean isRtl = (level2 & 1) != 0 ? true : z;
                if (builder == null) {
                    int levelLength = levelEnd2 - levelStart;
                    levelEnd = levelEnd2;
                    this.mWholeWidth += paint.getTextRunAdvances(this.mCopiedBuffer, levelStart, levelLength, levelStart, levelLength, isRtl, this.mWidths.getRawArray(), levelStart);
                } else {
                    levelEnd = levelEnd2;
                    builder.appendStyleRun(paint, config, levelEnd - levelStart, isRtl);
                }
                if (levelEnd != end) {
                    int levelStart2 = levelEnd;
                    levelStart = levelStart2;
                    level2 = this.mLevels.get(levelEnd);
                } else {
                    return;
                }
            } else {
                levelEnd = levelEnd2;
            }
            levelEnd2 = levelEnd + 1;
            z = false;
        }
    }

    private void applyMetricsAffectingSpan(TextPaint paint, LineBreakConfig lineBreakConfig, MetricAffectingSpan[] spans, int start, int end, MeasuredText.Builder builder) {
        ReplacementSpan replacement;
        this.mCachedPaint.set(paint);
        this.mCachedPaint.baselineShift = 0;
        boolean needFontMetrics = builder != null;
        if (needFontMetrics && this.mCachedFm == null) {
            this.mCachedFm = new Paint.FontMetricsInt();
        }
        ReplacementSpan replacement2 = null;
        if (spans == null) {
            replacement = null;
        } else {
            for (MetricAffectingSpan span : spans) {
                if (span instanceof ReplacementSpan) {
                    replacement2 = (ReplacementSpan) span;
                } else {
                    span.updateMeasureState(this.mCachedPaint);
                }
            }
            replacement = replacement2;
        }
        int i = this.mTextStart;
        int startInCopiedBuffer = start - i;
        int endInCopiedBuffer = end - i;
        if (builder != null) {
            this.mCachedPaint.getFontMetricsInt(this.mCachedFm);
        }
        if (replacement != null) {
            applyReplacementRun(replacement, startInCopiedBuffer, endInCopiedBuffer, this.mCachedPaint, builder);
        } else {
            applyStyleRun(startInCopiedBuffer, endInCopiedBuffer, this.mCachedPaint, lineBreakConfig, builder);
        }
        if (needFontMetrics) {
            if (this.mCachedPaint.baselineShift < 0) {
                this.mCachedFm.ascent += this.mCachedPaint.baselineShift;
                this.mCachedFm.top += this.mCachedPaint.baselineShift;
            } else {
                this.mCachedFm.descent += this.mCachedPaint.baselineShift;
                this.mCachedFm.bottom += this.mCachedPaint.baselineShift;
            }
            this.mFontMetrics.append(this.mCachedFm.top);
            this.mFontMetrics.append(this.mCachedFm.bottom);
            this.mFontMetrics.append(this.mCachedFm.ascent);
            this.mFontMetrics.append(this.mCachedFm.descent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int breakText(int limit, boolean forwards, float width) {
        float[] w = this.mWidths.getRawArray();
        if (forwards) {
            int i = 0;
            while (i < limit) {
                width -= w[i];
                if (width < 0.0f) {
                    break;
                }
                i++;
            }
            while (i > 0 && this.mCopiedBuffer[i - 1] == ' ') {
                i--;
            }
            return i;
        }
        int i2 = limit - 1;
        while (i2 >= 0) {
            width -= w[i2];
            if (width < 0.0f) {
                break;
            }
            i2--;
        }
        while (i2 < limit - 1 && (this.mCopiedBuffer[i2 + 1] == ' ' || w[i2 + 1] == 0.0f)) {
            i2++;
        }
        return (limit - i2) - 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float measure(int start, int limit) {
        float width = 0.0f;
        float[] w = this.mWidths.getRawArray();
        for (int i = start; i < limit; i++) {
            width += w[i];
        }
        return width;
    }

    public int getMemoryUsage() {
        return this.mMeasuredText.getMemoryUsage();
    }
}
