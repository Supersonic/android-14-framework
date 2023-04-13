package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.TabStopSpan;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class Layout {
    public static final int BREAK_STRATEGY_BALANCED = 2;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_SIMPLE = 0;
    public static final float DEFAULT_LINESPACING_ADDITION = 0.0f;
    public static final float DEFAULT_LINESPACING_MULTIPLIER = 1.0f;
    public static final int DIR_LEFT_TO_RIGHT = 1;
    static final int DIR_REQUEST_DEFAULT_LTR = 2;
    static final int DIR_REQUEST_DEFAULT_RTL = -2;
    static final int DIR_REQUEST_LTR = 1;
    static final int DIR_REQUEST_RTL = -1;
    public static final int DIR_RIGHT_TO_LEFT = -1;
    public static final int HYPHENATION_FREQUENCY_FULL = 2;
    public static final int HYPHENATION_FREQUENCY_FULL_FAST = 4;
    public static final int HYPHENATION_FREQUENCY_NONE = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int HYPHENATION_FREQUENCY_NORMAL_FAST = 3;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;
    public static final int JUSTIFICATION_MODE_NONE = 0;
    static final int RUN_LEVEL_MASK = 63;
    static final int RUN_LEVEL_SHIFT = 26;
    static final int RUN_RTL_FLAG = 67108864;
    private static final float TAB_INCREMENT = 20.0f;
    public static final int TEXT_SELECTION_LAYOUT_LEFT_TO_RIGHT = 1;
    public static final int TEXT_SELECTION_LAYOUT_RIGHT_TO_LEFT = 0;
    private Alignment mAlignment;
    private int mJustificationMode;
    private SpanSet<LineBackgroundSpan> mLineBackgroundSpans;
    private TextPaint mPaint;
    private float mSpacingAdd;
    private float mSpacingMult;
    private boolean mSpannedText;
    private CharSequence mText;
    private TextDirectionHeuristic mTextDir;
    private int mWidth;
    private TextPaint mWorkPaint;
    private static final ParagraphStyle[] NO_PARA_SPANS = (ParagraphStyle[]) ArrayUtils.emptyArray(ParagraphStyle.class);
    public static final TextInclusionStrategy INCLUSION_STRATEGY_ANY_OVERLAP = new TextInclusionStrategy() { // from class: android.text.Layout$$ExternalSyntheticLambda1
        @Override // android.text.Layout.TextInclusionStrategy
        public final boolean isSegmentInside(RectF rectF, RectF rectF2) {
            return RectF.intersects(rectF, rectF2);
        }
    };
    public static final TextInclusionStrategy INCLUSION_STRATEGY_CONTAINS_CENTER = new TextInclusionStrategy() { // from class: android.text.Layout$$ExternalSyntheticLambda2
        @Override // android.text.Layout.TextInclusionStrategy
        public final boolean isSegmentInside(RectF rectF, RectF rectF2) {
            boolean contains;
            contains = rectF2.contains(rectF.centerX(), rectF.centerY());
            return contains;
        }
    };
    public static final TextInclusionStrategy INCLUSION_STRATEGY_CONTAINS_ALL = new TextInclusionStrategy() { // from class: android.text.Layout$$ExternalSyntheticLambda3
        @Override // android.text.Layout.TextInclusionStrategy
        public final boolean isSegmentInside(RectF rectF, RectF rectF2) {
            boolean contains;
            contains = rectF2.contains(rectF);
            return contains;
        }
    };
    private static final Rect sTempRect = new Rect();
    static final int RUN_LENGTH_MASK = 67108863;
    public static final Directions DIRS_ALL_LEFT_TO_RIGHT = new Directions(new int[]{0, RUN_LENGTH_MASK});
    public static final Directions DIRS_ALL_RIGHT_TO_LEFT = new Directions(new int[]{0, 134217727});

    /* loaded from: classes3.dex */
    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_OPPOSITE,
        ALIGN_CENTER,
        ALIGN_LEFT,
        ALIGN_RIGHT
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface BreakStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Direction {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface HyphenationFrequency {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface JustificationMode {
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface SelectionRectangleConsumer {
        void accept(float f, float f2, float f3, float f4, int i);
    }

    @FunctionalInterface
    /* loaded from: classes3.dex */
    public interface TextInclusionStrategy {
        boolean isSegmentInside(RectF rectF, RectF rectF2);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TextSelectionLayout {
    }

    public abstract int getBottomPadding();

    public abstract int getEllipsisCount(int i);

    public abstract int getEllipsisStart(int i);

    public abstract boolean getLineContainsTab(int i);

    public abstract int getLineCount();

    public abstract int getLineDescent(int i);

    public abstract Directions getLineDirections(int i);

    public abstract int getLineStart(int i);

    public abstract int getLineTop(int i);

    public abstract int getParagraphDirection(int i);

    public abstract int getTopPadding();

    public static float getDesiredWidth(CharSequence source, TextPaint paint) {
        return getDesiredWidth(source, 0, source.length(), paint);
    }

    public static float getDesiredWidth(CharSequence source, int start, int end, TextPaint paint) {
        return getDesiredWidth(source, start, end, paint, TextDirectionHeuristics.FIRSTSTRONG_LTR);
    }

    public static float getDesiredWidth(CharSequence source, int start, int end, TextPaint paint, TextDirectionHeuristic textDir) {
        return getDesiredWidthWithLimit(source, start, end, paint, textDir, Float.MAX_VALUE);
    }

    public static float getDesiredWidthWithLimit(CharSequence source, int start, int end, TextPaint paint, TextDirectionHeuristic textDir, float upperLimit) {
        float need = 0.0f;
        int i = start;
        while (i <= end) {
            int next = TextUtils.indexOf(source, '\n', i, end);
            if (next < 0) {
                next = end;
            }
            float w = measurePara(paint, source, i, next, textDir);
            if (w > upperLimit) {
                return upperLimit;
            }
            if (w > need) {
                need = w;
            }
            i = next + 1;
        }
        return need;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Layout(CharSequence text, TextPaint paint, int width, Alignment align, float spacingMult, float spacingAdd) {
        this(text, paint, width, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingMult, spacingAdd);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Layout(CharSequence text, TextPaint paint, int width, Alignment align, TextDirectionHeuristic textDir, float spacingMult, float spacingAdd) {
        this.mWorkPaint = new TextPaint();
        this.mAlignment = Alignment.ALIGN_NORMAL;
        if (width < 0) {
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        }
        if (paint != null) {
            paint.bgColor = 0;
            paint.baselineShift = 0;
        }
        this.mText = text;
        this.mPaint = paint;
        this.mWidth = width;
        this.mAlignment = align;
        this.mSpacingMult = spacingMult;
        this.mSpacingAdd = spacingAdd;
        this.mSpannedText = text instanceof Spanned;
        this.mTextDir = textDir;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setJustificationMode(int justificationMode) {
        this.mJustificationMode = justificationMode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void replaceWith(CharSequence text, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd) {
        if (width < 0) {
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        }
        this.mText = text;
        this.mPaint = paint;
        this.mWidth = width;
        this.mAlignment = align;
        this.mSpacingMult = spacingmult;
        this.mSpacingAdd = spacingadd;
        this.mSpannedText = text instanceof Spanned;
    }

    public void draw(Canvas c) {
        draw(c, null, null, 0);
    }

    public void draw(Canvas canvas, Path selectionHighlight, Paint selectionHighlightPaint, int cursorOffsetVertical) {
        draw(canvas, null, null, selectionHighlight, selectionHighlightPaint, cursorOffsetVertical);
    }

    public void draw(Canvas canvas, List<Path> highlightPaths, List<Paint> highlightPaints, Path selectionPath, Paint selectionPaint, int cursorOffsetVertical) {
        long lineRange = getLineRangeForDraw(canvas);
        int firstLine = TextUtils.unpackRangeStartFromLong(lineRange);
        int lastLine = TextUtils.unpackRangeEndFromLong(lineRange);
        if (lastLine < 0) {
            return;
        }
        drawWithoutText(canvas, highlightPaths, highlightPaints, selectionPath, selectionPaint, cursorOffsetVertical, firstLine, lastLine);
        drawText(canvas, firstLine, lastLine);
    }

    public void drawText(Canvas canvas) {
        long lineRange = getLineRangeForDraw(canvas);
        int firstLine = TextUtils.unpackRangeStartFromLong(lineRange);
        int lastLine = TextUtils.unpackRangeEndFromLong(lineRange);
        if (lastLine < 0) {
            return;
        }
        drawText(canvas, firstLine, lastLine);
    }

    public void drawBackground(Canvas canvas) {
        long lineRange = getLineRangeForDraw(canvas);
        int firstLine = TextUtils.unpackRangeStartFromLong(lineRange);
        int lastLine = TextUtils.unpackRangeEndFromLong(lineRange);
        if (lastLine < 0) {
            return;
        }
        drawBackground(canvas, firstLine, lastLine);
    }

    public void drawWithoutText(Canvas canvas, List<Path> highlightPaths, List<Paint> highlightPaints, Path selectionPath, Paint selectionPaint, int cursorOffsetVertical, int firstLine, int lastLine) {
        drawBackground(canvas, firstLine, lastLine);
        if (highlightPaths == null && highlightPaints == null) {
            return;
        }
        if (cursorOffsetVertical != 0) {
            canvas.translate(0.0f, cursorOffsetVertical);
        }
        try {
            if (highlightPaths != null) {
                if (highlightPaints == null) {
                    throw new IllegalArgumentException("if highlight is specified, highlightPaint must be specified.");
                }
                if (highlightPaints.size() != highlightPaths.size()) {
                    throw new IllegalArgumentException("The highlight path size is different from the size of highlight paints");
                }
                for (int i = 0; i < highlightPaths.size(); i++) {
                    Path highlight = highlightPaths.get(i);
                    Paint highlightPaint = highlightPaints.get(i);
                    if (highlight != null) {
                        canvas.drawPath(highlight, highlightPaint);
                    }
                }
            }
            if (selectionPath != null) {
                canvas.drawPath(selectionPath, selectionPaint);
            }
        } finally {
            if (cursorOffsetVertical != 0) {
                canvas.translate(0.0f, -cursorOffsetVertical);
            }
        }
    }

    private boolean isJustificationRequired(int lineNum) {
        int lineEnd;
        return (this.mJustificationMode == 0 || (lineEnd = getLineEnd(lineNum)) >= this.mText.length() || this.mText.charAt(lineEnd + (-1)) == '\n') ? false : true;
    }

    private float getJustifyWidth(int lineNum) {
        Alignment align;
        int indentWidth;
        Alignment paraAlign = this.mAlignment;
        int left = 0;
        int right = this.mWidth;
        int dir = getParagraphDirection(lineNum);
        ParagraphStyle[] spans = NO_PARA_SPANS;
        if (this.mSpannedText) {
            Spanned sp = (Spanned) this.mText;
            int start = getLineStart(lineNum);
            boolean isFirstParaLine = start == 0 || this.mText.charAt(start + (-1)) == '\n';
            if (isFirstParaLine) {
                spans = (ParagraphStyle[]) getParagraphSpans(sp, start, sp.nextSpanTransition(start, this.mText.length(), ParagraphStyle.class), ParagraphStyle.class);
                int n = spans.length - 1;
                while (true) {
                    if (n >= 0) {
                        if (spans[n] instanceof AlignmentSpan) {
                            paraAlign = ((AlignmentSpan) spans[n]).getAlignment();
                            break;
                        } else {
                            n--;
                        }
                    } else {
                        break;
                    }
                }
            }
            int spanEnd = spans.length;
            boolean useFirstLineMargin = isFirstParaLine;
            int n2 = 0;
            while (true) {
                if (n2 >= spanEnd) {
                    break;
                }
                if (spans[n2] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                    int count = ((LeadingMarginSpan.LeadingMarginSpan2) spans[n2]).getLeadingMarginLineCount();
                    int startLine = getLineForOffset(sp.getSpanStart(spans[n2]));
                    if (lineNum < startLine + count) {
                        useFirstLineMargin = true;
                        break;
                    }
                }
                n2++;
            }
            for (int n3 = 0; n3 < spanEnd; n3++) {
                if (spans[n3] instanceof LeadingMarginSpan) {
                    LeadingMarginSpan margin = (LeadingMarginSpan) spans[n3];
                    if (dir == -1) {
                        right -= margin.getLeadingMargin(useFirstLineMargin);
                    } else {
                        left += margin.getLeadingMargin(useFirstLineMargin);
                    }
                }
            }
        }
        if (paraAlign == Alignment.ALIGN_LEFT) {
            align = dir == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else {
            Alignment align2 = Alignment.ALIGN_RIGHT;
            if (paraAlign == align2) {
                align = dir == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
            } else {
                align = paraAlign;
            }
        }
        if (align == Alignment.ALIGN_NORMAL) {
            indentWidth = dir == 1 ? getIndentAdjust(lineNum, Alignment.ALIGN_LEFT) : -getIndentAdjust(lineNum, Alignment.ALIGN_RIGHT);
        } else if (align != Alignment.ALIGN_OPPOSITE) {
            indentWidth = getIndentAdjust(lineNum, Alignment.ALIGN_CENTER);
        } else {
            indentWidth = dir == 1 ? -getIndentAdjust(lineNum, Alignment.ALIGN_RIGHT) : getIndentAdjust(lineNum, Alignment.ALIGN_LEFT);
        }
        return (right - left) - indentWidth;
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x010f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:114:0x00c4 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00ad  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x011d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void drawText(Canvas canvas, int firstLine, int lastLine) {
        int previousLineEnd;
        int dir;
        int start;
        int lineNum;
        TabStops tabStops;
        CharSequence buf;
        TextPaint paint;
        Layout layout;
        int lbaseline;
        TextLine tl;
        int dir2;
        int left;
        int right;
        ParagraphStyle[] spans;
        boolean tabStopsIsInitialized;
        TabStops tabStops2;
        int dir3;
        int i;
        Alignment align;
        int x;
        int indentWidth;
        ParagraphStyle[] spans2;
        TabStops tabStops3;
        int lineNum2;
        TextLine tl2;
        TabStops tabStops4;
        ParagraphStyle[] spans3;
        boolean z;
        boolean tabStopsIsInitialized2;
        Alignment paraAlign;
        int spanEnd;
        ParagraphStyle[] spans4;
        int length;
        boolean useFirstLineMargin;
        int n;
        boolean useFirstLineMargin2;
        int n2;
        int n3;
        boolean useFirstLineMargin3;
        int length2;
        int dir4;
        ParagraphStyle[] spans5;
        int start2;
        int lineNum3;
        TabStops tabStops5;
        CharSequence buf2;
        TextPaint paint2;
        Layout layout2;
        int lbaseline2;
        TextLine tl3;
        int dir5;
        int textLength;
        Spanned sp;
        boolean useFirstLineMargin4;
        Alignment paraAlign2;
        int n4;
        Alignment paraAlign3;
        Alignment paraAlign4;
        Layout layout3 = this;
        int i2 = firstLine;
        int previousLineBottom = layout3.getLineTop(i2);
        int previousLineEnd2 = layout3.getLineStart(i2);
        ParagraphStyle[] spans6 = NO_PARA_SPANS;
        int spanEnd2 = 0;
        TextPaint paint3 = layout3.mWorkPaint;
        paint3.set(layout3.mPaint);
        CharSequence buf3 = layout3.mText;
        Alignment paraAlign5 = layout3.mAlignment;
        boolean tabStopsIsInitialized3 = false;
        TextLine tl4 = TextLine.obtain();
        TabStops tabStops6 = null;
        int lineNum4 = firstLine;
        while (lineNum4 <= lastLine) {
            int start3 = previousLineEnd2;
            int previousLineEnd3 = layout3.getLineStart(lineNum4 + 1);
            boolean justify = layout3.isJustificationRequired(lineNum4);
            int end = layout3.getLineVisibleEnd(lineNum4, start3, previousLineEnd3);
            paint3.setStartHyphenEdit(layout3.getStartHyphenEdit(lineNum4));
            paint3.setEndHyphenEdit(layout3.getEndHyphenEdit(lineNum4));
            int ltop = previousLineBottom;
            int previousLineBottom2 = lineNum4 + 1;
            int lbottom = layout3.getLineTop(previousLineBottom2);
            int ltop2 = ltop;
            int ltop3 = layout3.getParagraphDirection(lineNum4);
            int lbaseline3 = lbottom - layout3.getLineDescent(lineNum4);
            int lbaseline4 = layout3.mWidth;
            TextLine tl5 = tl4;
            if (!layout3.mSpannedText) {
                previousLineEnd = previousLineEnd3;
                ParagraphStyle[] spans7 = spans6;
                dir = ltop3;
                start = start3;
                lineNum = lineNum4;
                tabStops = tabStops6;
                buf = buf3;
                paint = paint3;
                layout = layout3;
                lbaseline = lbaseline3;
                tl = tl5;
                dir2 = ltop2;
                left = 0;
                right = lbaseline4;
                spans = spans7;
            } else {
                Spanned sp2 = (Spanned) buf3;
                int textLength2 = buf3.length();
                if (start3 != 0) {
                    previousLineEnd = previousLineEnd3;
                    int previousLineEnd4 = start3 - 1;
                    spans3 = spans6;
                    if (buf3.charAt(previousLineEnd4) != '\n') {
                        z = false;
                        boolean isFirstParaLine = z;
                        if (start3 >= spanEnd2 && (lineNum4 == i2 || isFirstParaLine)) {
                            int spanEnd3 = sp2.nextSpanTransition(start3, textLength2, ParagraphStyle.class);
                            ParagraphStyle[] spans8 = (ParagraphStyle[]) getParagraphSpans(sp2, start3, spanEnd3, ParagraphStyle.class);
                            paraAlign2 = layout3.mAlignment;
                            n4 = spans8.length - 1;
                            while (true) {
                                if (n4 >= 0) {
                                    paraAlign3 = paraAlign2;
                                    break;
                                }
                                paraAlign4 = paraAlign2;
                                if (!(spans8[n4] instanceof AlignmentSpan)) {
                                    n4--;
                                    paraAlign2 = paraAlign4;
                                } else {
                                    Alignment paraAlign6 = ((AlignmentSpan) spans8[n4]).getAlignment();
                                    paraAlign3 = paraAlign6;
                                    break;
                                }
                            }
                            tabStopsIsInitialized2 = false;
                            spans4 = spans8;
                            spanEnd = spanEnd3;
                            paraAlign = paraAlign3;
                            length = spans4.length;
                            useFirstLineMargin = isFirstParaLine;
                            n = 0;
                            while (true) {
                                if (n < length) {
                                    useFirstLineMargin2 = useFirstLineMargin;
                                    break;
                                }
                                if (!(spans4[n] instanceof LeadingMarginSpan.LeadingMarginSpan2)) {
                                    sp = sp2;
                                    useFirstLineMargin4 = useFirstLineMargin;
                                } else {
                                    int count = ((LeadingMarginSpan.LeadingMarginSpan2) spans4[n]).getLeadingMarginLineCount();
                                    useFirstLineMargin4 = useFirstLineMargin;
                                    int startLine = layout3.getLineForOffset(sp2.getSpanStart(spans4[n]));
                                    sp = sp2;
                                    if (lineNum4 < startLine + count) {
                                        useFirstLineMargin2 = true;
                                        break;
                                    }
                                }
                                n++;
                                useFirstLineMargin = useFirstLineMargin4;
                                sp2 = sp;
                            }
                            n2 = 0;
                            left = 0;
                            right = lbaseline4;
                            while (n2 < length) {
                                if (!(spans4[n2] instanceof LeadingMarginSpan)) {
                                    n3 = n2;
                                    useFirstLineMargin3 = useFirstLineMargin2;
                                    length2 = length;
                                    dir4 = ltop3;
                                    spans5 = spans4;
                                    start2 = start3;
                                    lineNum3 = lineNum4;
                                    tabStops5 = tabStops6;
                                    buf2 = buf3;
                                    paint2 = paint3;
                                    layout2 = layout3;
                                    lbaseline2 = lbaseline3;
                                    tl3 = tl5;
                                    dir5 = ltop2;
                                    textLength = textLength2;
                                } else {
                                    LeadingMarginSpan margin = (LeadingMarginSpan) spans4[n2];
                                    if (ltop3 == -1) {
                                        lbaseline2 = lbaseline3;
                                        n3 = n2;
                                        boolean useFirstLineMargin5 = useFirstLineMargin2;
                                        length2 = length;
                                        spans5 = spans4;
                                        start2 = start3;
                                        lineNum3 = lineNum4;
                                        tabStops5 = tabStops6;
                                        tl3 = tl5;
                                        textLength = textLength2;
                                        buf2 = buf3;
                                        dir4 = ltop3;
                                        dir5 = ltop2;
                                        margin.drawLeadingMargin(canvas, paint3, right, ltop3, dir5, lbaseline2, lbottom, buf3, start2, end, isFirstParaLine, this);
                                        right -= margin.getLeadingMargin(useFirstLineMargin5);
                                        layout2 = this;
                                        paint2 = paint3;
                                        useFirstLineMargin3 = useFirstLineMargin5;
                                    } else {
                                        n3 = n2;
                                        length2 = length;
                                        dir4 = ltop3;
                                        spans5 = spans4;
                                        start2 = start3;
                                        lineNum3 = lineNum4;
                                        tabStops5 = tabStops6;
                                        buf2 = buf3;
                                        lbaseline2 = lbaseline3;
                                        tl3 = tl5;
                                        dir5 = ltop2;
                                        textLength = textLength2;
                                        paint2 = paint3;
                                        useFirstLineMargin3 = useFirstLineMargin2;
                                        layout2 = this;
                                        margin.drawLeadingMargin(canvas, paint3, left, dir4, dir5, lbaseline2, lbottom, buf2, start2, end, isFirstParaLine, this);
                                        left += margin.getLeadingMargin(useFirstLineMargin3);
                                    }
                                }
                                paint3 = paint2;
                                layout3 = layout2;
                                n2 = n3 + 1;
                                ltop2 = dir5;
                                textLength2 = textLength;
                                buf3 = buf2;
                                ltop3 = dir4;
                                lbaseline3 = lbaseline2;
                                length = length2;
                                spans4 = spans5;
                                start3 = start2;
                                lineNum4 = lineNum3;
                                tabStops6 = tabStops5;
                                tl5 = tl3;
                                useFirstLineMargin2 = useFirstLineMargin3;
                            }
                            dir = ltop3;
                            ParagraphStyle[] spans9 = spans4;
                            start = start3;
                            lineNum = lineNum4;
                            tabStops = tabStops6;
                            buf = buf3;
                            paint = paint3;
                            layout = layout3;
                            lbaseline = lbaseline3;
                            tl = tl5;
                            dir2 = ltop2;
                            spanEnd2 = spanEnd;
                            paraAlign5 = paraAlign;
                            tabStopsIsInitialized3 = tabStopsIsInitialized2;
                            spans = spans9;
                        }
                        paraAlign = paraAlign5;
                        tabStopsIsInitialized2 = tabStopsIsInitialized3;
                        spans4 = spans3;
                        spanEnd = spanEnd2;
                        length = spans4.length;
                        useFirstLineMargin = isFirstParaLine;
                        n = 0;
                        while (true) {
                            if (n < length) {
                            }
                            n++;
                            useFirstLineMargin = useFirstLineMargin4;
                            sp2 = sp;
                        }
                        n2 = 0;
                        left = 0;
                        right = lbaseline4;
                        while (n2 < length) {
                        }
                        dir = ltop3;
                        ParagraphStyle[] spans92 = spans4;
                        start = start3;
                        lineNum = lineNum4;
                        tabStops = tabStops6;
                        buf = buf3;
                        paint = paint3;
                        layout = layout3;
                        lbaseline = lbaseline3;
                        tl = tl5;
                        dir2 = ltop2;
                        spanEnd2 = spanEnd;
                        paraAlign5 = paraAlign;
                        tabStopsIsInitialized3 = tabStopsIsInitialized2;
                        spans = spans92;
                    }
                } else {
                    previousLineEnd = previousLineEnd3;
                    spans3 = spans6;
                }
                z = true;
                boolean isFirstParaLine2 = z;
                if (start3 >= spanEnd2) {
                    int spanEnd32 = sp2.nextSpanTransition(start3, textLength2, ParagraphStyle.class);
                    ParagraphStyle[] spans82 = (ParagraphStyle[]) getParagraphSpans(sp2, start3, spanEnd32, ParagraphStyle.class);
                    paraAlign2 = layout3.mAlignment;
                    n4 = spans82.length - 1;
                    while (true) {
                        if (n4 >= 0) {
                        }
                        n4--;
                        paraAlign2 = paraAlign4;
                    }
                    tabStopsIsInitialized2 = false;
                    spans4 = spans82;
                    spanEnd = spanEnd32;
                    paraAlign = paraAlign3;
                    length = spans4.length;
                    useFirstLineMargin = isFirstParaLine2;
                    n = 0;
                    while (true) {
                        if (n < length) {
                        }
                        n++;
                        useFirstLineMargin = useFirstLineMargin4;
                        sp2 = sp;
                    }
                    n2 = 0;
                    left = 0;
                    right = lbaseline4;
                    while (n2 < length) {
                    }
                    dir = ltop3;
                    ParagraphStyle[] spans922 = spans4;
                    start = start3;
                    lineNum = lineNum4;
                    tabStops = tabStops6;
                    buf = buf3;
                    paint = paint3;
                    layout = layout3;
                    lbaseline = lbaseline3;
                    tl = tl5;
                    dir2 = ltop2;
                    spanEnd2 = spanEnd;
                    paraAlign5 = paraAlign;
                    tabStopsIsInitialized3 = tabStopsIsInitialized2;
                    spans = spans922;
                }
                paraAlign = paraAlign5;
                tabStopsIsInitialized2 = tabStopsIsInitialized3;
                spans4 = spans3;
                spanEnd = spanEnd2;
                length = spans4.length;
                useFirstLineMargin = isFirstParaLine2;
                n = 0;
                while (true) {
                    if (n < length) {
                    }
                    n++;
                    useFirstLineMargin = useFirstLineMargin4;
                    sp2 = sp;
                }
                n2 = 0;
                left = 0;
                right = lbaseline4;
                while (n2 < length) {
                }
                dir = ltop3;
                ParagraphStyle[] spans9222 = spans4;
                start = start3;
                lineNum = lineNum4;
                tabStops = tabStops6;
                buf = buf3;
                paint = paint3;
                layout = layout3;
                lbaseline = lbaseline3;
                tl = tl5;
                dir2 = ltop2;
                spanEnd2 = spanEnd;
                paraAlign5 = paraAlign;
                tabStopsIsInitialized3 = tabStopsIsInitialized2;
                spans = spans9222;
            }
            int lineNum5 = lineNum;
            boolean hasTab = layout.getLineContainsTab(lineNum5);
            if (hasTab && !tabStopsIsInitialized3) {
                TabStops tabStops7 = tabStops;
                if (tabStops7 == null) {
                    tabStops4 = new TabStops(TAB_INCREMENT, spans);
                } else {
                    tabStops7.reset(TAB_INCREMENT, spans);
                    tabStops4 = tabStops7;
                }
                tabStopsIsInitialized = true;
                tabStops2 = tabStops4;
            } else {
                tabStopsIsInitialized = tabStopsIsInitialized3;
                tabStops2 = tabStops;
            }
            Alignment align2 = paraAlign5;
            if (align2 == Alignment.ALIGN_LEFT) {
                dir3 = dir;
                i = 1;
                align = dir3 == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
            } else {
                dir3 = dir;
                i = 1;
                if (align2 != Alignment.ALIGN_RIGHT) {
                    align = align2;
                } else {
                    align = dir3 == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
                }
            }
            if (align == Alignment.ALIGN_NORMAL) {
                if (dir3 == i) {
                    int indentWidth2 = layout.getIndentAdjust(lineNum5, Alignment.ALIGN_LEFT);
                    indentWidth = indentWidth2;
                    x = left + indentWidth2;
                } else {
                    int indentWidth3 = -layout.getIndentAdjust(lineNum5, Alignment.ALIGN_RIGHT);
                    indentWidth = indentWidth3;
                    x = right - indentWidth3;
                }
            } else {
                int max = (int) layout.getLineExtent(lineNum5, tabStops2, false);
                if (align == Alignment.ALIGN_OPPOSITE) {
                    if (dir3 == i) {
                        int indentWidth4 = -layout.getIndentAdjust(lineNum5, Alignment.ALIGN_RIGHT);
                        indentWidth = indentWidth4;
                        x = (right - max) - indentWidth4;
                    } else {
                        int indentWidth5 = layout.getIndentAdjust(lineNum5, Alignment.ALIGN_LEFT);
                        indentWidth = indentWidth5;
                        x = (left - max) + indentWidth5;
                    }
                } else {
                    int indentWidth6 = layout.getIndentAdjust(lineNum5, Alignment.ALIGN_CENTER);
                    x = (((right + left) - (max & (-2))) >> 1) + indentWidth6;
                    indentWidth = indentWidth6;
                }
            }
            Directions directions = layout.getLineDirections(lineNum5);
            if (directions != DIRS_ALL_LEFT_TO_RIGHT || layout.mSpannedText || hasTab || justify) {
                spans2 = spans;
                int x2 = x;
                int lbaseline5 = lbaseline;
                tabStops3 = tabStops2;
                lineNum2 = lineNum5;
                tl.set(paint, buf, start, end, dir3, directions, hasTab, tabStops2, layout.getEllipsisStart(lineNum5), layout.getEllipsisStart(lineNum5) + layout.getEllipsisCount(lineNum5), isFallbackLineSpacingEnabled());
                if (!justify) {
                    tl2 = tl;
                } else {
                    tl2 = tl;
                    tl2.justify((right - left) - indentWidth);
                }
                tl2.draw(canvas, x2, dir2, lbaseline5, lbottom);
            } else {
                spans2 = spans;
                canvas.drawText(buf, start, end, x, lbaseline, paint);
                tabStops3 = tabStops2;
                lineNum2 = lineNum5;
                tl2 = tl;
            }
            lineNum4 = lineNum2 + 1;
            i2 = firstLine;
            paint3 = paint;
            layout3 = layout;
            tl4 = tl2;
            previousLineBottom = lbottom;
            buf3 = buf;
            previousLineEnd2 = previousLineEnd;
            tabStopsIsInitialized3 = tabStopsIsInitialized;
            spans6 = spans2;
            tabStops6 = tabStops3;
        }
        TextLine.recycle(tl4);
    }

    public void drawBackground(Canvas canvas, int firstLine, int lastLine) {
        ParagraphStyle[] spans;
        int spanEnd;
        int spanEnd2;
        if (this.mSpannedText) {
            if (this.mLineBackgroundSpans == null) {
                this.mLineBackgroundSpans = new SpanSet<>(LineBackgroundSpan.class);
            }
            Spanned buffer = (Spanned) this.mText;
            int textLength = buffer.length();
            this.mLineBackgroundSpans.init(buffer, 0, textLength);
            if (this.mLineBackgroundSpans.numberOfSpans > 0) {
                int previousLineBottom = getLineTop(firstLine);
                int previousLineEnd = getLineStart(firstLine);
                ParagraphStyle[] spans2 = NO_PARA_SPANS;
                int spansLength = 0;
                TextPaint paint = this.mPaint;
                int spanEnd3 = 0;
                int width = this.mWidth;
                int i = firstLine;
                while (i <= lastLine) {
                    int start = previousLineEnd;
                    int end = getLineStart(i + 1);
                    int ltop = previousLineBottom;
                    int lbottom = getLineTop(i + 1);
                    int previousLineBottom2 = getLineDescent(i);
                    int lbaseline = lbottom - previousLineBottom2;
                    if (end < spanEnd3) {
                        spans = spans2;
                        spanEnd = spanEnd3;
                        spanEnd2 = spansLength;
                    } else {
                        int spanEnd4 = this.mLineBackgroundSpans.getNextTransition(start, textLength);
                        int spansLength2 = 0;
                        if (start == end && start != 0) {
                            spanEnd = spanEnd4;
                            spanEnd2 = 0;
                            spans = spans2;
                        } else {
                            for (int j = 0; j < this.mLineBackgroundSpans.numberOfSpans; j++) {
                                if (this.mLineBackgroundSpans.spanStarts[j] < end && this.mLineBackgroundSpans.spanEnds[j] > start) {
                                    spans2 = (ParagraphStyle[]) GrowingArrayUtils.append((LineBackgroundSpan[]) spans2, spansLength2, this.mLineBackgroundSpans.spans[j]);
                                    spansLength2++;
                                }
                            }
                            spanEnd = spanEnd4;
                            spanEnd2 = spansLength2;
                            spans = spans2;
                        }
                    }
                    int n = 0;
                    while (n < spanEnd2) {
                        LineBackgroundSpan lineBackgroundSpan = (LineBackgroundSpan) spans[n];
                        int spansLength3 = spanEnd2;
                        int spansLength4 = width;
                        int end2 = end;
                        int start2 = start;
                        int i2 = i;
                        lineBackgroundSpan.drawBackground(canvas, paint, 0, spansLength4, ltop, lbaseline, lbottom, buffer, start2, end2, i2);
                        n++;
                        spanEnd2 = spansLength3;
                        end = end2;
                        start = start2;
                        i = i2;
                        width = width;
                        paint = paint;
                    }
                    int spansLength5 = spanEnd2;
                    i++;
                    previousLineEnd = end;
                    previousLineBottom = lbottom;
                    spans2 = spans;
                    spanEnd3 = spanEnd;
                    spansLength = spansLength5;
                }
            }
            this.mLineBackgroundSpans.recycle();
        }
    }

    public long getLineRangeForDraw(Canvas canvas) {
        Rect rect = sTempRect;
        synchronized (rect) {
            if (!canvas.getClipBounds(rect)) {
                return TextUtils.packRangeInLong(0, -1);
            }
            int dtop = rect.top;
            int dbottom = rect.bottom;
            int top = Math.max(dtop, 0);
            int bottom = Math.min(getLineTop(getLineCount()), dbottom);
            return top >= bottom ? TextUtils.packRangeInLong(0, -1) : TextUtils.packRangeInLong(getLineForVertical(top), getLineForVertical(bottom));
        }
    }

    private int getLineStartPos(int line, int left, int right) {
        Alignment align = getParagraphAlignment(line);
        int dir = getParagraphDirection(line);
        if (align == Alignment.ALIGN_LEFT) {
            align = dir == 1 ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else if (align == Alignment.ALIGN_RIGHT) {
            align = dir == 1 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
        }
        if (align == Alignment.ALIGN_NORMAL) {
            if (dir == 1) {
                int x = getIndentAdjust(line, Alignment.ALIGN_LEFT) + left;
                return x;
            }
            int x2 = getIndentAdjust(line, Alignment.ALIGN_RIGHT) + right;
            return x2;
        }
        TabStops tabStops = null;
        if (this.mSpannedText && getLineContainsTab(line)) {
            Spanned spanned = (Spanned) this.mText;
            int start = getLineStart(line);
            int spanEnd = spanned.nextSpanTransition(start, spanned.length(), TabStopSpan.class);
            TabStopSpan[] tabSpans = (TabStopSpan[]) getParagraphSpans(spanned, start, spanEnd, TabStopSpan.class);
            if (tabSpans.length > 0) {
                tabStops = new TabStops(TAB_INCREMENT, tabSpans);
            }
        }
        int max = (int) getLineExtent(line, tabStops, false);
        if (align == Alignment.ALIGN_OPPOSITE) {
            if (dir == 1) {
                int x3 = (right - max) + getIndentAdjust(line, Alignment.ALIGN_RIGHT);
                return x3;
            }
            int x4 = left - max;
            return x4 + getIndentAdjust(line, Alignment.ALIGN_LEFT);
        }
        int x5 = ((left + right) - (max & (-2))) >> (getIndentAdjust(line, Alignment.ALIGN_CENTER) + 1);
        return x5;
    }

    public final CharSequence getText() {
        return this.mText;
    }

    public final TextPaint getPaint() {
        return this.mPaint;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public int getEllipsizedWidth() {
        return this.mWidth;
    }

    public final void increaseWidthTo(int wid) {
        if (wid < this.mWidth) {
            throw new RuntimeException("attempted to reduce Layout width");
        }
        this.mWidth = wid;
    }

    public int getHeight() {
        return getLineTop(getLineCount());
    }

    public int getHeight(boolean cap) {
        return getHeight();
    }

    public final Alignment getAlignment() {
        return this.mAlignment;
    }

    public final float getSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public final float getSpacingAdd() {
        return this.mSpacingAdd;
    }

    public final TextDirectionHeuristic getTextDirectionHeuristic() {
        return this.mTextDir;
    }

    public int getLineBounds(int line, Rect bounds) {
        if (bounds != null) {
            bounds.left = 0;
            bounds.top = getLineTop(line);
            bounds.right = this.mWidth;
            bounds.bottom = getLineTop(line + 1);
        }
        return getLineBaseline(line);
    }

    public int getStartHyphenEdit(int line) {
        return 0;
    }

    public int getEndHyphenEdit(int line) {
        return 0;
    }

    public int getIndentAdjust(int line, Alignment alignment) {
        return 0;
    }

    public boolean isFallbackLineSpacingEnabled() {
        return false;
    }

    public boolean isLevelBoundary(int offset) {
        int line = getLineForOffset(offset);
        Directions dirs = getLineDirections(line);
        if (dirs == DIRS_ALL_LEFT_TO_RIGHT || dirs == DIRS_ALL_RIGHT_TO_LEFT) {
            return false;
        }
        int[] runs = dirs.mDirections;
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        if (offset == lineStart || offset == lineEnd) {
            int paraLevel = getParagraphDirection(line) == 1 ? 0 : 1;
            int runIndex = offset == lineStart ? 0 : runs.length - 2;
            return ((runs[runIndex + 1] >>> 26) & 63) != paraLevel;
        }
        int offset2 = offset - lineStart;
        for (int i = 0; i < runs.length; i += 2) {
            if (offset2 == runs[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean isRtlCharAt(int offset) {
        int line = getLineForOffset(offset);
        Directions dirs = getLineDirections(line);
        if (dirs == DIRS_ALL_LEFT_TO_RIGHT) {
            return false;
        }
        if (dirs == DIRS_ALL_RIGHT_TO_LEFT) {
            return true;
        }
        int[] runs = dirs.mDirections;
        int lineStart = getLineStart(line);
        for (int i = 0; i < runs.length; i += 2) {
            int start = runs[i] + lineStart;
            int limit = (runs[i + 1] & RUN_LENGTH_MASK) + start;
            if (offset >= start && offset < limit) {
                int level = (runs[i + 1] >>> 26) & 63;
                return (level & 1) != 0;
            }
        }
        return false;
    }

    public long getRunRange(int offset) {
        int line = getLineForOffset(offset);
        Directions dirs = getLineDirections(line);
        if (dirs == DIRS_ALL_LEFT_TO_RIGHT || dirs == DIRS_ALL_RIGHT_TO_LEFT) {
            return TextUtils.packRangeInLong(0, getLineEnd(line));
        }
        int[] runs = dirs.mDirections;
        int lineStart = getLineStart(line);
        for (int i = 0; i < runs.length; i += 2) {
            int start = runs[i] + lineStart;
            int limit = (runs[i + 1] & RUN_LENGTH_MASK) + start;
            if (offset >= start && offset < limit) {
                return TextUtils.packRangeInLong(start, limit);
            }
        }
        int i2 = getLineEnd(line);
        return TextUtils.packRangeInLong(0, i2);
    }

    public boolean primaryIsTrailingPrevious(int offset) {
        int line = getLineForOffset(offset);
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int[] runs = getLineDirections(line).mDirections;
        int levelAt = -1;
        int i = 0;
        while (true) {
            if (i >= runs.length) {
                break;
            }
            int start = runs[i] + lineStart;
            int limit = (runs[i + 1] & RUN_LENGTH_MASK) + start;
            if (limit > lineEnd) {
                limit = lineEnd;
            }
            if (offset < start || offset >= limit) {
                i += 2;
            } else if (offset > start) {
                return false;
            } else {
                levelAt = (runs[i + 1] >>> 26) & 63;
            }
        }
        if (levelAt == -1) {
            levelAt = getParagraphDirection(line) == 1 ? 0 : 1;
        }
        int levelBefore = -1;
        if (offset == lineStart) {
            levelBefore = getParagraphDirection(line) == 1 ? 0 : 1;
        } else {
            int offset2 = offset - 1;
            int i2 = 0;
            while (true) {
                if (i2 < runs.length) {
                    int start2 = runs[i2] + lineStart;
                    int limit2 = (runs[i2 + 1] & RUN_LENGTH_MASK) + start2;
                    if (limit2 > lineEnd) {
                        limit2 = lineEnd;
                    }
                    if (offset2 < start2 || offset2 >= limit2) {
                        i2 += 2;
                    } else {
                        levelBefore = (runs[i2 + 1] >>> 26) & 63;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return levelBefore < levelAt;
    }

    public boolean[] primaryIsTrailingPreviousAllLineOffsets(int line) {
        byte b;
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int[] runs = getLineDirections(line).mDirections;
        boolean[] trailing = new boolean[(lineEnd - lineStart) + 1];
        byte[] level = new byte[(lineEnd - lineStart) + 1];
        for (int i = 0; i < runs.length; i += 2) {
            int start = runs[i] + lineStart;
            int limit = (runs[i + 1] & RUN_LENGTH_MASK) + start;
            if (limit > lineEnd) {
                limit = lineEnd;
            }
            if (limit != start) {
                level[(limit - lineStart) - 1] = (byte) ((runs[i + 1] >>> 26) & 63);
            }
        }
        for (int i2 = 0; i2 < runs.length; i2 += 2) {
            int start2 = runs[i2] + lineStart;
            byte currentLevel = (byte) ((runs[i2 + 1] >>> 26) & 63);
            int i3 = start2 - lineStart;
            boolean z = false;
            if (start2 == lineStart) {
                b = getParagraphDirection(line) == 1 ? (byte) 0 : (byte) 1;
            } else {
                b = level[(start2 - lineStart) - 1];
            }
            if (currentLevel > b) {
                z = true;
            }
            trailing[i3] = z;
        }
        return trailing;
    }

    public float getPrimaryHorizontal(int offset) {
        return getPrimaryHorizontal(offset, false);
    }

    public float getPrimaryHorizontal(int offset, boolean clamped) {
        boolean trailing = primaryIsTrailingPrevious(offset);
        return getHorizontal(offset, trailing, clamped);
    }

    public float getSecondaryHorizontal(int offset) {
        return getSecondaryHorizontal(offset, false);
    }

    public float getSecondaryHorizontal(int offset, boolean clamped) {
        boolean trailing = primaryIsTrailingPrevious(offset);
        return getHorizontal(offset, !trailing, clamped);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getHorizontal(int offset, boolean primary) {
        return primary ? getPrimaryHorizontal(offset) : getSecondaryHorizontal(offset);
    }

    private float getHorizontal(int offset, boolean trailing, boolean clamped) {
        int line = getLineForOffset(offset);
        return getHorizontal(offset, trailing, line, clamped);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0076  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private float getHorizontal(int offset, boolean trailing, int line, boolean clamped) {
        TabStops tabStops;
        int start = getLineStart(line);
        int end = getLineEnd(line);
        int dir = getParagraphDirection(line);
        boolean hasTab = getLineContainsTab(line);
        Directions directions = getLineDirections(line);
        if (hasTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, start, end, TabStopSpan.class);
                if (tabs.length > 0) {
                    TabStops tabStops2 = new TabStops(TAB_INCREMENT, tabs);
                    tabStops = tabStops2;
                    TextLine tl = TextLine.obtain();
                    tl.set(this.mPaint, this.mText, start, end, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
                    float wid = tl.measure(offset - start, trailing, null);
                    TextLine.recycle(tl);
                    if (clamped) {
                        int i = this.mWidth;
                        if (wid > i) {
                            wid = i;
                        }
                    }
                    int left = getParagraphLeft(line);
                    int right = getParagraphRight(line);
                    return getLineStartPos(line, left, right) + wid;
                }
            }
        }
        tabStops = null;
        TextLine tl2 = TextLine.obtain();
        tl2.set(this.mPaint, this.mText, start, end, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        float wid2 = tl2.measure(offset - start, trailing, null);
        TextLine.recycle(tl2);
        if (clamped) {
        }
        int left2 = getParagraphLeft(line);
        int right2 = getParagraphRight(line);
        return getLineStartPos(line, left2, right2) + wid2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:12:0x006b  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00ad A[LOOP:2: B:26:0x00aa->B:28:0x00ad, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public float[] getLineHorizontals(int line, boolean clamped, boolean primary) {
        TabStops tabStops;
        float[] horizontal;
        int offset;
        int start = getLineStart(line);
        int end = getLineEnd(line);
        int dir = getParagraphDirection(line);
        boolean hasTab = getLineContainsTab(line);
        Directions directions = getLineDirections(line);
        if (hasTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, start, end, TabStopSpan.class);
                if (tabs.length > 0) {
                    TabStops tabStops2 = new TabStops(TAB_INCREMENT, tabs);
                    tabStops = tabStops2;
                    TextLine tl = TextLine.obtain();
                    tl.set(this.mPaint, this.mText, start, end, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
                    boolean[] trailings = primaryIsTrailingPreviousAllLineOffsets(line);
                    if (!primary) {
                        for (int offset2 = 0; offset2 < trailings.length; offset2++) {
                            trailings[offset2] = !trailings[offset2];
                        }
                    }
                    float[] wid = tl.measureAllOffsets(trailings, null);
                    TextLine.recycle(tl);
                    if (clamped) {
                        for (int offset3 = 0; offset3 < wid.length; offset3++) {
                            float f = wid[offset3];
                            int i = this.mWidth;
                            if (f > i) {
                                wid[offset3] = i;
                            }
                        }
                    }
                    int left = getParagraphLeft(line);
                    int right = getParagraphRight(line);
                    int lineStartPos = getLineStartPos(line, left, right);
                    horizontal = new float[(end - start) + 1];
                    for (offset = 0; offset < horizontal.length; offset++) {
                        horizontal[offset] = lineStartPos + wid[offset];
                    }
                    return horizontal;
                }
            }
        }
        tabStops = null;
        TextLine tl2 = TextLine.obtain();
        tl2.set(this.mPaint, this.mText, start, end, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        boolean[] trailings2 = primaryIsTrailingPreviousAllLineOffsets(line);
        if (!primary) {
        }
        float[] wid2 = tl2.measureAllOffsets(trailings2, null);
        TextLine.recycle(tl2);
        if (clamped) {
        }
        int left2 = getParagraphLeft(line);
        int right2 = getParagraphRight(line);
        int lineStartPos2 = getLineStartPos(line, left2, right2);
        horizontal = new float[(end - start) + 1];
        while (offset < horizontal.length) {
        }
        return horizontal;
    }

    private void fillHorizontalBoundsForLine(int line, float[] horizontalBounds) {
        TabStops tabStops;
        float[] horizontalBounds2 = horizontalBounds;
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int lineLength = lineEnd - lineStart;
        int dir = getParagraphDirection(line);
        Directions directions = getLineDirections(line);
        boolean hasTab = getLineContainsTab(line);
        if (hasTab) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, lineStart, lineEnd, TabStopSpan.class);
                if (tabs.length > 0) {
                    TabStops tabStops2 = new TabStops(TAB_INCREMENT, tabs);
                    tabStops = tabStops2;
                    TextLine tl = TextLine.obtain();
                    tl.set(this.mPaint, this.mText, lineStart, lineEnd, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
                    if (horizontalBounds2 != null || horizontalBounds2.length < lineLength * 2) {
                        horizontalBounds2 = new float[lineLength * 2];
                    }
                    tl.measureAllBounds(horizontalBounds2, null);
                    TextLine.recycle(tl);
                }
            }
        }
        tabStops = null;
        TextLine tl2 = TextLine.obtain();
        tl2.set(this.mPaint, this.mText, lineStart, lineEnd, dir, directions, hasTab, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        if (horizontalBounds2 != null) {
        }
        horizontalBounds2 = new float[lineLength * 2];
        tl2.measureAllBounds(horizontalBounds2, null);
        TextLine.recycle(tl2);
    }

    public void fillCharacterBounds(int start, int end, float[] bounds, int boundsStart) {
        if (start < 0 || end < start || end > this.mText.length()) {
            throw new IndexOutOfBoundsException("given range: " + start + ", " + end + " is out of the text range: 0, " + this.mText.length());
        }
        if (bounds == null) {
            throw new IllegalArgumentException("bounds can't be null.");
        }
        int neededLength = (end - start) * 4;
        if (neededLength > bounds.length - boundsStart) {
            throw new IndexOutOfBoundsException("bounds doesn't have enough space to store the result, needed: " + neededLength + " had: " + (bounds.length - boundsStart));
        }
        if (start == end) {
            return;
        }
        int startLine = getLineForOffset(start);
        int endLine = getLineForOffset(end - 1);
        float[] horizontalBounds = null;
        int line = startLine;
        while (line <= endLine) {
            int lineStart = getLineStart(line);
            int lineEnd = getLineEnd(line);
            int lineLength = lineEnd - lineStart;
            if (horizontalBounds == null || horizontalBounds.length < lineLength * 2) {
                horizontalBounds = new float[lineLength * 2];
            }
            fillHorizontalBoundsForLine(line, horizontalBounds);
            int lineLeft = getParagraphLeft(line);
            int lineRight = getParagraphRight(line);
            int lineStartPos = getLineStartPos(line, lineLeft, lineRight);
            int lineTop = getLineTop(line);
            int startLine2 = startLine;
            int lineBottom = getLineBottom(line);
            int startIndex = Math.max(start, lineStart);
            int endLine2 = endLine;
            int endIndex = Math.min(end, lineEnd);
            int lineEnd2 = startIndex;
            while (lineEnd2 < endIndex) {
                int offset = lineEnd2 - lineStart;
                int endIndex2 = endIndex;
                float left = horizontalBounds[offset * 2] + lineStartPos;
                float[] horizontalBounds2 = horizontalBounds;
                float right = horizontalBounds[(offset * 2) + 1] + lineStartPos;
                int boundsIndex = boundsStart + ((lineEnd2 - start) * 4);
                bounds[boundsIndex] = left;
                bounds[boundsIndex + 1] = lineTop;
                bounds[boundsIndex + 2] = right;
                float right2 = lineBottom;
                bounds[boundsIndex + 3] = right2;
                lineEnd2++;
                endIndex = endIndex2;
                horizontalBounds = horizontalBounds2;
                lineStart = lineStart;
            }
            line++;
            startLine = startLine2;
            endLine = endLine2;
        }
    }

    public float getLineLeft(int line) {
        Alignment resultAlign;
        int dir = getParagraphDirection(line);
        Alignment align = getParagraphAlignment(line);
        if (align == null) {
            align = Alignment.ALIGN_CENTER;
        }
        switch (C33821.$SwitchMap$android$text$Layout$Alignment[align.ordinal()]) {
            case 1:
                if (dir != -1) {
                    resultAlign = Alignment.ALIGN_LEFT;
                    break;
                } else {
                    resultAlign = Alignment.ALIGN_RIGHT;
                    break;
                }
            case 2:
                if (dir != -1) {
                    resultAlign = Alignment.ALIGN_RIGHT;
                    break;
                } else {
                    resultAlign = Alignment.ALIGN_LEFT;
                    break;
                }
            case 3:
                resultAlign = Alignment.ALIGN_CENTER;
                break;
            case 4:
                resultAlign = Alignment.ALIGN_RIGHT;
                break;
            default:
                resultAlign = Alignment.ALIGN_LEFT;
                break;
        }
        switch (resultAlign) {
            case ALIGN_CENTER:
                int left = getParagraphLeft(line);
                float max = getLineMax(line);
                return (float) Math.floor(left + ((this.mWidth - max) / 2.0f));
            case ALIGN_RIGHT:
                return this.mWidth - getLineMax(line);
            default:
                return 0.0f;
        }
    }

    public float getLineRight(int line) {
        Alignment resultAlign;
        int dir = getParagraphDirection(line);
        Alignment align = getParagraphAlignment(line);
        if (align == null) {
            align = Alignment.ALIGN_CENTER;
        }
        switch (C33821.$SwitchMap$android$text$Layout$Alignment[align.ordinal()]) {
            case 1:
                if (dir != -1) {
                    resultAlign = Alignment.ALIGN_LEFT;
                    break;
                } else {
                    resultAlign = Alignment.ALIGN_RIGHT;
                    break;
                }
            case 2:
                if (dir != -1) {
                    resultAlign = Alignment.ALIGN_RIGHT;
                    break;
                } else {
                    resultAlign = Alignment.ALIGN_LEFT;
                    break;
                }
            case 3:
                resultAlign = Alignment.ALIGN_CENTER;
                break;
            case 4:
                resultAlign = Alignment.ALIGN_RIGHT;
                break;
            default:
                resultAlign = Alignment.ALIGN_LEFT;
                break;
        }
        switch (resultAlign) {
            case ALIGN_CENTER:
                int right = getParagraphRight(line);
                float max = getLineMax(line);
                return (float) Math.ceil(right - ((this.mWidth - max) / 2.0f));
            case ALIGN_RIGHT:
                return this.mWidth;
            default:
                return getLineMax(line);
        }
    }

    public float getLineMax(int line) {
        float margin = getParagraphLeadingMargin(line);
        float signedExtent = getLineExtent(line, false);
        return (signedExtent >= 0.0f ? signedExtent : -signedExtent) + margin;
    }

    public float getLineWidth(int line) {
        float margin = getParagraphLeadingMargin(line);
        float signedExtent = getLineExtent(line, true);
        return (signedExtent >= 0.0f ? signedExtent : -signedExtent) + margin;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x003f A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0041  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private float getLineExtent(int line, boolean full) {
        TabStops tabStops;
        Directions directions;
        int start = getLineStart(line);
        int end = full ? getLineEnd(line) : getLineVisibleEnd(line);
        boolean hasTabs = getLineContainsTab(line);
        if (hasTabs) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spanned) {
                TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) charSequence, start, end, TabStopSpan.class);
                if (tabs.length > 0) {
                    TabStops tabStops2 = new TabStops(TAB_INCREMENT, tabs);
                    tabStops = tabStops2;
                    directions = getLineDirections(line);
                    if (directions != null) {
                        return 0.0f;
                    }
                    int dir = getParagraphDirection(line);
                    TextLine tl = TextLine.obtain();
                    TextPaint paint = this.mWorkPaint;
                    paint.set(this.mPaint);
                    paint.setStartHyphenEdit(getStartHyphenEdit(line));
                    paint.setEndHyphenEdit(getEndHyphenEdit(line));
                    tl.set(paint, this.mText, start, end, dir, directions, hasTabs, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
                    if (isJustificationRequired(line)) {
                        tl.justify(getJustifyWidth(line));
                    }
                    float width = tl.metrics(null);
                    TextLine.recycle(tl);
                    return width;
                }
            }
        }
        tabStops = null;
        directions = getLineDirections(line);
        if (directions != null) {
        }
    }

    private float getLineExtent(int line, TabStops tabStops, boolean full) {
        int start = getLineStart(line);
        int end = full ? getLineEnd(line) : getLineVisibleEnd(line);
        boolean hasTabs = getLineContainsTab(line);
        Directions directions = getLineDirections(line);
        int dir = getParagraphDirection(line);
        TextLine tl = TextLine.obtain();
        TextPaint paint = this.mWorkPaint;
        paint.set(this.mPaint);
        paint.setStartHyphenEdit(getStartHyphenEdit(line));
        paint.setEndHyphenEdit(getEndHyphenEdit(line));
        tl.set(paint, this.mText, start, end, dir, directions, hasTabs, tabStops, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        if (isJustificationRequired(line)) {
            tl.justify(getJustifyWidth(line));
        }
        float width = tl.metrics(null);
        TextLine.recycle(tl);
        return width;
    }

    public int getLineForVertical(int vertical) {
        int high = getLineCount();
        int low = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineTop(guess) > vertical) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return 0;
        }
        return low;
    }

    public int getLineForOffset(int offset) {
        int high = getLineCount();
        int low = -1;
        while (high - low > 1) {
            int guess = (high + low) / 2;
            if (getLineStart(guess) > offset) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return 0;
        }
        return low;
    }

    public int getOffsetForHorizontal(int line, float horiz) {
        return getOffsetForHorizontal(line, horiz, true);
    }

    public int getOffsetForHorizontal(int line, float horiz, boolean primary) {
        TextLine tl;
        int max;
        Layout layout = this;
        int lineEndOffset = getLineEnd(line);
        int lineStartOffset = getLineStart(line);
        Directions dirs = getLineDirections(line);
        TextLine tl2 = TextLine.obtain();
        Directions dirs2 = dirs;
        tl2.set(layout.mPaint, layout.mText, lineStartOffset, lineEndOffset, getParagraphDirection(line), dirs, false, null, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        HorizontalMeasurementProvider horizontal = new HorizontalMeasurementProvider(line, primary);
        int i = true;
        if (line == getLineCount() - 1) {
            max = lineEndOffset;
            tl = tl2;
        } else {
            int max2 = lineEndOffset - lineStartOffset;
            tl = tl2;
            max = tl.getOffsetToLeftRightOf(max2, !layout.isRtlCharAt(lineEndOffset - 1)) + lineStartOffset;
        }
        int best = lineStartOffset;
        float bestdist = Math.abs(horizontal.get(lineStartOffset) - horiz);
        int i2 = 0;
        while (true) {
            Directions dirs3 = dirs2;
            if (i2 >= dirs3.mDirections.length) {
                break;
            }
            int here = dirs3.mDirections[i2] + lineStartOffset;
            int there = (dirs3.mDirections[i2 + 1] & RUN_LENGTH_MASK) + here;
            boolean isRtl = (dirs3.mDirections[i2 + 1] & 67108864) != 0 ? i : false;
            int swap = isRtl ? -1 : i;
            if (there > max) {
                there = max;
            }
            int high = (there - 1) + 1;
            int low = (here + 1) - 1;
            while (high - low > 1) {
                int guess = (high + low) / 2;
                int adguess = layout.getOffsetAtStartOf(guess);
                int swap2 = swap;
                if (horizontal.get(adguess) * swap2 >= swap2 * horiz) {
                    high = guess;
                } else {
                    low = guess;
                }
                swap = swap2;
                layout = this;
            }
            if (low < here + 1) {
                low = here + 1;
            }
            if (low < there) {
                int aft = tl.getOffsetToLeftRightOf(low - lineStartOffset, isRtl) + lineStartOffset;
                int swap3 = tl.getOffsetToLeftRightOf(aft - lineStartOffset, !isRtl);
                int low2 = swap3 + lineStartOffset;
                if (low2 >= here && low2 < there) {
                    float dist = Math.abs(horizontal.get(low2) - horiz);
                    if (aft < there) {
                        float other = Math.abs(horizontal.get(aft) - horiz);
                        if (other < dist) {
                            dist = other;
                            low2 = aft;
                        }
                    }
                    if (dist < bestdist) {
                        bestdist = dist;
                        best = low2;
                    }
                }
            }
            float dist2 = Math.abs(horizontal.get(here) - horiz);
            if (dist2 < bestdist) {
                bestdist = dist2;
                best = here;
            }
            i2 += 2;
            layout = this;
            dirs2 = dirs3;
            i = true;
        }
        if (Math.abs(horizontal.get(max) - horiz) <= bestdist) {
            best = max;
        }
        TextLine.recycle(tl);
        return best;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class HorizontalMeasurementProvider {
        private float[] mHorizontals;
        private final int mLine;
        private int mLineStartOffset;
        private final boolean mPrimary;

        HorizontalMeasurementProvider(int line, boolean primary) {
            this.mLine = line;
            this.mPrimary = primary;
            init();
        }

        private void init() {
            Directions dirs = Layout.this.getLineDirections(this.mLine);
            if (dirs == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return;
            }
            this.mHorizontals = Layout.this.getLineHorizontals(this.mLine, false, this.mPrimary);
            this.mLineStartOffset = Layout.this.getLineStart(this.mLine);
        }

        float get(int offset) {
            int index = offset - this.mLineStartOffset;
            float[] fArr = this.mHorizontals;
            if (fArr == null || index < 0 || index >= fArr.length) {
                return Layout.this.getHorizontal(offset, this.mPrimary);
            }
            return fArr[index];
        }
    }

    public int[] getRangeForRect(RectF area, SegmentFinder segmentFinder, TextInclusionStrategy inclusionStrategy) {
        int startLine;
        int startLine2 = getLineForVertical((int) area.top);
        if (area.top <= getLineBottom(startLine2, false)) {
            startLine = startLine2;
        } else {
            int startLine3 = startLine2 + 1;
            if (startLine3 >= getLineCount()) {
                return null;
            }
            startLine = startLine3;
        }
        int endLine = getLineForVertical((int) area.bottom);
        if ((endLine != 0 || area.bottom >= getLineTop(0)) && endLine >= startLine) {
            int startLine4 = startLine;
            int start = getStartOrEndOffsetForAreaWithinLine(startLine, area, segmentFinder, inclusionStrategy, true);
            while (start == -1 && startLine4 < endLine) {
                startLine4++;
                start = getStartOrEndOffsetForAreaWithinLine(startLine4, area, segmentFinder, inclusionStrategy, true);
            }
            if (start == -1) {
                return null;
            }
            int end = getStartOrEndOffsetForAreaWithinLine(endLine, area, segmentFinder, inclusionStrategy, false);
            int endLine2 = endLine;
            while (end == -1 && startLine4 < endLine2) {
                int endLine3 = endLine2 - 1;
                end = getStartOrEndOffsetForAreaWithinLine(endLine3, area, segmentFinder, inclusionStrategy, false);
                endLine2 = endLine3;
            }
            if (end == -1) {
                return null;
            }
            return new int[]{segmentFinder.previousStartBoundary(start + 1), segmentFinder.nextEndBoundary(end - 1)};
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x00fb, code lost:
        return -1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private int getStartOrEndOffsetForAreaWithinLine(int line, RectF area, SegmentFinder segmentFinder, TextInclusionStrategy inclusionStrategy, boolean getStart) {
        float f;
        float f2;
        int runIndex;
        Directions directions;
        int lineStartPos;
        float[] horizontalBounds;
        int lineEndOffset;
        int lineStartOffset;
        int result;
        int i;
        int lineTop = getLineTop(line);
        int lineBottom = getLineBottom(line, false);
        int lineStartOffset2 = getLineStart(line);
        int lineEndOffset2 = getLineEnd(line);
        if (lineStartOffset2 == lineEndOffset2) {
            return -1;
        }
        float[] horizontalBounds2 = new float[(lineEndOffset2 - lineStartOffset2) * 2];
        fillHorizontalBoundsForLine(line, horizontalBounds2);
        int lineStartPos2 = getLineStartPos(line, getParagraphLeft(line), getParagraphRight(line));
        Directions directions2 = getLineDirections(line);
        int runIndex2 = getStart ? 0 : directions2.getRunCount() - 1;
        while (true) {
            if ((!getStart || runIndex2 >= directions2.getRunCount()) && (getStart || runIndex2 < 0)) {
                break;
            }
            int runStartOffset = directions2.getRunStart(runIndex2);
            int runEndOffset = Math.min(runStartOffset + directions2.getRunLength(runIndex2), lineEndOffset2 - lineStartOffset2);
            boolean isRtl = directions2.isRunRtl(runIndex2);
            float f3 = lineStartPos2;
            if (isRtl) {
                f = horizontalBounds2[(runEndOffset - 1) * 2];
            } else {
                f = horizontalBounds2[runStartOffset * 2];
            }
            float runLeft = f3 + f;
            float f4 = lineStartPos2;
            if (isRtl) {
                f2 = horizontalBounds2[(runStartOffset * 2) + 1];
            } else {
                f2 = horizontalBounds2[((runEndOffset - 1) * 2) + 1];
            }
            float runRight = f4 + f2;
            if (getStart) {
                runIndex = runIndex2;
                directions = directions2;
                lineStartPos = lineStartPos2;
                horizontalBounds = horizontalBounds2;
                lineEndOffset = lineEndOffset2;
                lineStartOffset = lineStartOffset2;
                result = getStartOffsetForAreaWithinRun(area, lineTop, lineBottom, lineStartOffset2, lineStartPos2, horizontalBounds2, runStartOffset, runEndOffset, runLeft, runRight, isRtl, segmentFinder, inclusionStrategy);
            } else {
                runIndex = runIndex2;
                directions = directions2;
                lineStartPos = lineStartPos2;
                horizontalBounds = horizontalBounds2;
                lineEndOffset = lineEndOffset2;
                lineStartOffset = lineStartOffset2;
                result = getEndOffsetForAreaWithinRun(area, lineTop, lineBottom, lineStartOffset, lineStartPos, horizontalBounds, runStartOffset, runEndOffset, runLeft, runRight, isRtl, segmentFinder, inclusionStrategy);
            }
            if (result >= 0) {
                return result;
            }
            if (!getStart) {
                i = -1;
            } else {
                i = 1;
            }
            runIndex2 = runIndex + i;
            directions2 = directions;
            lineStartPos2 = lineStartPos;
            horizontalBounds2 = horizontalBounds;
            lineEndOffset2 = lineEndOffset;
            lineStartOffset2 = lineStartOffset;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:46:0x009e, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00d8, code lost:
        return -1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int getStartOffsetForAreaWithinRun(RectF area, int lineTop, int lineBottom, int lineStartOffset, int lineStartPos, float[] horizontalBounds, int runStartOffset, int runEndOffset, float runLeft, float runRight, boolean isRtl, SegmentFinder segmentFinder, TextInclusionStrategy inclusionStrategy) {
        int low;
        int segmentStartOffset;
        if (runRight >= area.left && runLeft <= area.right) {
            if ((!isRtl && area.left <= runLeft) || (isRtl && area.right >= runRight)) {
                low = runStartOffset;
            } else {
                int low2 = runStartOffset;
                int high = runEndOffset;
                while (high - low2 > 1) {
                    int guess = (high + low2) / 2;
                    float pos = lineStartPos + horizontalBounds[guess * 2];
                    if ((!isRtl && pos > area.left) || (isRtl && pos < area.right)) {
                        high = guess;
                    } else {
                        low2 = guess;
                    }
                }
                low = isRtl ? high : low2;
            }
            int high2 = lineStartOffset + low;
            int segmentEndOffset = segmentFinder.nextEndBoundary(high2);
            if (segmentEndOffset == -1 || (segmentStartOffset = segmentFinder.previousStartBoundary(segmentEndOffset)) >= lineStartOffset + runEndOffset) {
                return -1;
            }
            int segmentStartOffset2 = Math.max(segmentStartOffset, lineStartOffset + runStartOffset);
            int segmentEndOffset2 = Math.min(segmentEndOffset, lineStartOffset + runEndOffset);
            RectF segmentBounds = new RectF(0.0f, lineTop, 0.0f, lineBottom);
            while (true) {
                float segmentStart = lineStartPos + horizontalBounds[((segmentStartOffset2 - lineStartOffset) * 2) + (isRtl ? 1 : 0)];
                if ((isRtl || segmentStart <= area.right) && (!isRtl || segmentStart >= area.left)) {
                    float segmentEnd = lineStartPos + horizontalBounds[(((segmentEndOffset2 - lineStartOffset) - 1) * 2) + (!isRtl ? 1 : 0)];
                    segmentBounds.left = isRtl ? segmentEnd : segmentStart;
                    segmentBounds.right = isRtl ? segmentStart : segmentEnd;
                    if (inclusionStrategy.isSegmentInside(segmentBounds, area)) {
                        return segmentStartOffset2;
                    }
                    segmentStartOffset2 = segmentFinder.nextStartBoundary(segmentStartOffset2);
                    if (segmentStartOffset2 == -1 || segmentStartOffset2 >= lineStartOffset + runEndOffset) {
                        break;
                    }
                    segmentEndOffset2 = Math.min(segmentFinder.nextEndBoundary(segmentStartOffset2), lineStartOffset + runEndOffset);
                }
            }
        }
        return -1;
    }

    /* JADX WARN: Code restructure failed: missing block: B:64:0x00d9, code lost:
        return -1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int getEndOffsetForAreaWithinRun(RectF area, int lineTop, int lineBottom, int lineStartOffset, int lineStartPos, float[] horizontalBounds, int runStartOffset, int runEndOffset, float runLeft, float runRight, boolean isRtl, SegmentFinder segmentFinder, TextInclusionStrategy inclusionStrategy) {
        int low;
        int segmentEndOffset;
        if (runRight >= area.left && runLeft <= area.right) {
            if ((!isRtl && area.right >= runRight) || (isRtl && area.left <= runLeft)) {
                low = runEndOffset - 1;
            } else {
                int low2 = runStartOffset;
                int high = runEndOffset;
                while (high - low2 > 1) {
                    int guess = (high + low2) / 2;
                    float pos = lineStartPos + horizontalBounds[guess * 2];
                    if ((!isRtl && pos > area.right) || (isRtl && pos < area.left)) {
                        high = guess;
                    } else {
                        low2 = guess;
                    }
                }
                low = isRtl ? high : low2;
            }
            int high2 = lineStartOffset + low;
            int segmentStartOffset = segmentFinder.previousStartBoundary(high2 + 1);
            if (segmentStartOffset != -1 && (segmentEndOffset = segmentFinder.nextEndBoundary(segmentStartOffset)) > lineStartOffset + runStartOffset) {
                int segmentStartOffset2 = Math.max(segmentStartOffset, lineStartOffset + runStartOffset);
                int segmentEndOffset2 = Math.min(segmentEndOffset, lineStartOffset + runEndOffset);
                RectF segmentBounds = new RectF(0.0f, lineTop, 0.0f, lineBottom);
                while (true) {
                    float segmentEnd = lineStartPos + horizontalBounds[(((segmentEndOffset2 - lineStartOffset) - 1) * 2) + (!isRtl ? 1 : 0)];
                    if ((isRtl || segmentEnd >= area.left) && (!isRtl || segmentEnd <= area.right)) {
                        float segmentStart = lineStartPos + horizontalBounds[((segmentStartOffset2 - lineStartOffset) * 2) + (isRtl ? 1 : 0)];
                        segmentBounds.left = isRtl ? segmentEnd : segmentStart;
                        segmentBounds.right = isRtl ? segmentStart : segmentEnd;
                        if (inclusionStrategy.isSegmentInside(segmentBounds, area)) {
                            return segmentEndOffset2;
                        }
                        segmentEndOffset2 = segmentFinder.previousEndBoundary(segmentEndOffset2);
                        if (segmentEndOffset2 == -1 || segmentEndOffset2 <= lineStartOffset + runStartOffset) {
                            break;
                        }
                        segmentStartOffset2 = Math.max(segmentFinder.previousStartBoundary(segmentEndOffset2), lineStartOffset + runStartOffset);
                    }
                }
                return -1;
            }
            return -1;
        }
        return -1;
    }

    public final int getLineEnd(int line) {
        return getLineStart(line + 1);
    }

    public int getLineVisibleEnd(int line) {
        return getLineVisibleEnd(line, getLineStart(line), getLineStart(line + 1));
    }

    private int getLineVisibleEnd(int line, int start, int end) {
        CharSequence text = this.mText;
        if (line == getLineCount() - 1) {
            return end;
        }
        while (end > start) {
            char ch = text.charAt(end - 1);
            if (ch == '\n') {
                return end - 1;
            }
            if (!TextLine.isLineEndSpace(ch)) {
                break;
            }
            end--;
        }
        return end;
    }

    public final int getLineBottom(int line) {
        return getLineBottom(line, true);
    }

    public int getLineBottom(int line, boolean includeLineSpacing) {
        if (includeLineSpacing) {
            return getLineTop(line + 1);
        }
        return getLineTop(line + 1) - getLineExtra(line);
    }

    public final int getLineBaseline(int line) {
        return getLineTop(line + 1) - getLineDescent(line);
    }

    public final int getLineAscent(int line) {
        return getLineTop(line) - (getLineTop(line + 1) - getLineDescent(line));
    }

    public int getLineExtra(int line) {
        return 0;
    }

    public int getOffsetToLeftOf(int offset) {
        return getOffsetToLeftRightOf(offset, true);
    }

    public int getOffsetToRightOf(int offset) {
        return getOffsetToLeftRightOf(offset, false);
    }

    private int getOffsetToLeftRightOf(int caret, boolean toLeft) {
        boolean toLeft2 = toLeft;
        int line = getLineForOffset(caret);
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int lineDir = getParagraphDirection(line);
        boolean lineChanged = false;
        boolean advance = toLeft2 == (lineDir == -1);
        if (advance) {
            if (caret == lineEnd) {
                if (line >= getLineCount() - 1) {
                    return caret;
                }
                lineChanged = true;
                line++;
            }
        } else if (caret == lineStart) {
            if (line <= 0) {
                return caret;
            }
            lineChanged = true;
            line--;
        }
        if (lineChanged) {
            lineStart = getLineStart(line);
            lineEnd = getLineEnd(line);
            int newDir = getParagraphDirection(line);
            if (newDir != lineDir) {
                toLeft2 = !toLeft2;
                lineDir = newDir;
            }
        }
        Directions directions = getLineDirections(line);
        TextLine tl = TextLine.obtain();
        tl.set(this.mPaint, this.mText, lineStart, lineEnd, lineDir, directions, false, null, getEllipsisStart(line), getEllipsisStart(line) + getEllipsisCount(line), isFallbackLineSpacingEnabled());
        int caret2 = tl.getOffsetToLeftRightOf(caret - lineStart, toLeft2) + lineStart;
        TextLine.recycle(tl);
        return caret2;
    }

    private int getOffsetAtStartOf(int offset) {
        char c1;
        if (offset == 0) {
            return 0;
        }
        CharSequence text = this.mText;
        char c = text.charAt(offset);
        if (c >= 56320 && c <= 57343 && (c1 = text.charAt(offset - 1)) >= 55296 && c1 <= 56319) {
            offset--;
        }
        if (this.mSpannedText) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i++) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = start;
                }
            }
        }
        return offset;
    }

    public boolean shouldClampCursor(int line) {
        switch (C33821.$SwitchMap$android$text$Layout$Alignment[getParagraphAlignment(line).ordinal()]) {
            case 1:
                return getParagraphDirection(line) > 0;
            case 5:
                return true;
            default:
                return false;
        }
    }

    public void getCursorPath(int point, Path dest, CharSequence editingBuffer) {
        dest.reset();
        int line = getLineForOffset(point);
        int top = getLineTop(line);
        int bottom = getLineBottom(line, false);
        boolean clamped = shouldClampCursor(line);
        float h1 = getPrimaryHorizontal(point, clamped) - 0.5f;
        int caps = TextKeyListener.getMetaState(editingBuffer, 1) | TextKeyListener.getMetaState(editingBuffer, 2048);
        int fn = TextKeyListener.getMetaState(editingBuffer, 2);
        int dist = 0;
        if (caps != 0 || fn != 0) {
            dist = (bottom - top) >> 2;
            if (fn != 0) {
                top += dist;
            }
            if (caps != 0) {
                bottom -= dist;
            }
        }
        if (h1 < 0.5f) {
            h1 = 0.5f;
        }
        dest.moveTo(h1, top);
        dest.lineTo(h1, bottom);
        if (caps == 2) {
            dest.moveTo(h1, bottom);
            dest.lineTo(h1 - dist, bottom + dist);
            dest.lineTo(h1, bottom);
            dest.lineTo(dist + h1, bottom + dist);
        } else if (caps == 1) {
            dest.moveTo(h1, bottom);
            dest.lineTo(h1 - dist, bottom + dist);
            dest.moveTo(h1 - dist, (bottom + dist) - 0.5f);
            dest.lineTo(dist + h1, (bottom + dist) - 0.5f);
            dest.moveTo(dist + h1, bottom + dist);
            dest.lineTo(h1, bottom);
        }
        if (fn == 2) {
            dest.moveTo(h1, top);
            dest.lineTo(h1 - dist, top - dist);
            dest.lineTo(h1, top);
            dest.lineTo(dist + h1, top - dist);
        } else if (fn == 1) {
            dest.moveTo(h1, top);
            dest.lineTo(h1 - dist, top - dist);
            dest.moveTo(h1 - dist, (top - dist) + 0.5f);
            dest.lineTo(dist + h1, (top - dist) + 0.5f);
            dest.moveTo(dist + h1, top - dist);
            dest.lineTo(h1, top);
        }
    }

    private void addSelection(int line, int start, int end, int top, int bottom, SelectionRectangleConsumer consumer) {
        int layout;
        Layout layout2 = this;
        int i = line;
        int linestart = getLineStart(line);
        int lineend = getLineEnd(line);
        Directions dirs = getLineDirections(line);
        if (lineend > linestart && layout2.mText.charAt(lineend - 1) == '\n') {
            lineend--;
        }
        int i2 = 0;
        while (i2 < dirs.mDirections.length) {
            int here = dirs.mDirections[i2] + linestart;
            int there = (dirs.mDirections[i2 + 1] & RUN_LENGTH_MASK) + here;
            if (there > lineend) {
                there = lineend;
            }
            if (start <= there && end >= here) {
                int st = Math.max(start, here);
                int en = Math.min(end, there);
                if (st != en) {
                    float h1 = layout2.getHorizontal(st, false, i, false);
                    float h2 = layout2.getHorizontal(en, true, i, false);
                    float left = Math.min(h1, h2);
                    float right = Math.max(h1, h2);
                    if ((dirs.mDirections[i2 + 1] & 67108864) != 0) {
                        layout = 0;
                    } else {
                        layout = 1;
                    }
                    consumer.accept(left, top, right, bottom, layout);
                }
            }
            i2 += 2;
            layout2 = this;
            i = line;
        }
    }

    public void getSelectionPath(int start, int end, final Path dest) {
        dest.reset();
        getSelection(start, end, new SelectionRectangleConsumer() { // from class: android.text.Layout$$ExternalSyntheticLambda0
            @Override // android.text.Layout.SelectionRectangleConsumer
            public final void accept(float f, float f2, float f3, float f4, int i) {
                Path.this.addRect(f, f2, f3, f4, Path.Direction.CW);
            }
        });
    }

    public final void getSelection(int start, int end, SelectionRectangleConsumer consumer) {
        int start2;
        int end2;
        int i;
        if (start == end) {
            return;
        }
        if (end >= start) {
            start2 = start;
            end2 = end;
        } else {
            start2 = end;
            end2 = start;
        }
        int startline = getLineForOffset(start2);
        int endline = getLineForOffset(end2);
        int top = getLineTop(startline);
        int bottom = getLineBottom(endline, false);
        if (startline == endline) {
            addSelection(startline, start2, end2, top, bottom, consumer);
            return;
        }
        float width = this.mWidth;
        addSelection(startline, start2, getLineEnd(startline), top, getLineBottom(startline), consumer);
        if (getParagraphDirection(startline) == -1) {
            consumer.accept(getLineLeft(startline), top, 0.0f, getLineBottom(startline), 0);
            i = -1;
        } else {
            i = -1;
            consumer.accept(getLineRight(startline), top, width, getLineBottom(startline), 1);
        }
        for (int i2 = startline + 1; i2 < endline; i2++) {
            int top2 = getLineTop(i2);
            int bottom2 = getLineBottom(i2);
            if (getParagraphDirection(i2) == i) {
                consumer.accept(0.0f, top2, width, bottom2, 0);
            } else {
                consumer.accept(0.0f, top2, width, bottom2, 1);
            }
        }
        int top3 = getLineTop(endline);
        int bottom3 = getLineBottom(endline, false);
        addSelection(endline, getLineStart(endline), end2, top3, bottom3, consumer);
        if (getParagraphDirection(endline) == i) {
            consumer.accept(width, top3, getLineRight(endline), bottom3, 0);
        } else {
            consumer.accept(0.0f, top3, getLineLeft(endline), bottom3, 1);
        }
    }

    public final Alignment getParagraphAlignment(int line) {
        Alignment align = this.mAlignment;
        if (this.mSpannedText) {
            Spanned sp = (Spanned) this.mText;
            AlignmentSpan[] spans = (AlignmentSpan[]) getParagraphSpans(sp, getLineStart(line), getLineEnd(line), AlignmentSpan.class);
            int spanLength = spans.length;
            if (spanLength > 0) {
                return spans[spanLength - 1].getAlignment();
            }
            return align;
        }
        return align;
    }

    public final int getParagraphLeft(int line) {
        int dir = getParagraphDirection(line);
        if (dir == -1 || !this.mSpannedText) {
            return 0;
        }
        return getParagraphLeadingMargin(line);
    }

    public final int getParagraphRight(int line) {
        int right = this.mWidth;
        int dir = getParagraphDirection(line);
        if (dir == 1 || !this.mSpannedText) {
            return right;
        }
        return right - getParagraphLeadingMargin(line);
    }

    private int getParagraphLeadingMargin(int line) {
        if (this.mSpannedText) {
            Spanned spanned = (Spanned) this.mText;
            int lineStart = getLineStart(line);
            int lineEnd = getLineEnd(line);
            int spanEnd = spanned.nextSpanTransition(lineStart, lineEnd, LeadingMarginSpan.class);
            LeadingMarginSpan[] spans = (LeadingMarginSpan[]) getParagraphSpans(spanned, lineStart, spanEnd, LeadingMarginSpan.class);
            if (spans.length == 0) {
                return 0;
            }
            int margin = 0;
            boolean useFirstLineMargin = lineStart == 0 || spanned.charAt(lineStart + (-1)) == '\n';
            for (int i = 0; i < spans.length; i++) {
                if (spans[i] instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                    int spStart = spanned.getSpanStart(spans[i]);
                    int spanLine = getLineForOffset(spStart);
                    int count = ((LeadingMarginSpan.LeadingMarginSpan2) spans[i]).getLeadingMarginLineCount();
                    useFirstLineMargin |= line < spanLine + count;
                }
            }
            for (LeadingMarginSpan span : spans) {
                margin += span.getLeadingMargin(useFirstLineMargin);
            }
            return margin;
        }
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x006a, code lost:
        if ((r25 instanceof android.text.Spanned) == false) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x006c, code lost:
        r4 = (android.text.Spanned) r25;
        r5 = r4.nextSpanTransition(r26, r27, android.text.style.TabStopSpan.class);
        r0 = (android.text.style.TabStopSpan[]) getParagraphSpans(r4, r26, r5, android.text.style.TabStopSpan.class);
        r17 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0082, code lost:
        if (r0.length <= 0) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x008d, code lost:
        r3 = new android.text.Layout.TabStops(android.text.Layout.TAB_INCREMENT, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0091, code lost:
        r18 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0094, code lost:
        r17 = true;
        r18 = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00f1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static float measurePara(TextPaint paint, CharSequence text, int start, int end, TextDirectionHeuristic textDir) {
        TextLine tl;
        MeasuredParagraph mt;
        Directions directions;
        int dir;
        boolean hasTabs;
        int margin;
        TabStops tabStops;
        boolean hasTabs2;
        int margin2;
        MeasuredParagraph mt2 = null;
        TextLine tl2 = TextLine.obtain();
        try {
            mt = MeasuredParagraph.buildForBidi(text, start, end, textDir, null);
            try {
                char[] chars = mt.getChars();
                int len = chars.length;
                directions = mt.getDirections(0, len);
                dir = mt.getParagraphDir();
                boolean hasTabs3 = false;
                TabStops tabStops2 = null;
                if (!(text instanceof Spanned)) {
                    hasTabs = false;
                    margin = 0;
                } else {
                    try {
                        LeadingMarginSpan[] spans = (LeadingMarginSpan[]) getParagraphSpans((Spanned) text, start, end, LeadingMarginSpan.class);
                        int length = spans.length;
                        int margin3 = 0;
                        int margin4 = 0;
                        while (margin4 < length) {
                            LeadingMarginSpan lms = spans[margin4];
                            margin3 += lms.getLeadingMargin(true);
                            margin4++;
                            length = length;
                            hasTabs3 = hasTabs3;
                        }
                        hasTabs = hasTabs3;
                        margin = margin3;
                    } catch (Throwable th) {
                        th = th;
                        mt2 = mt;
                        tl = tl2;
                        TextLine.recycle(tl);
                        if (mt2 != null) {
                            mt2.recycle();
                        }
                        throw th;
                    }
                }
                int i = 0;
                while (true) {
                    if (i >= len) {
                        tabStops = null;
                        hasTabs2 = hasTabs;
                        break;
                    } else if (chars[i] == '\t') {
                        break;
                    } else {
                        i++;
                    }
                }
                margin2 = margin;
                tl = tl2;
            } catch (Throwable th2) {
                th = th2;
                tl = tl2;
                mt2 = mt;
            }
        } catch (Throwable th3) {
            th = th3;
            tl = tl2;
        }
        try {
            tl2.set(paint, text, start, end, dir, directions, hasTabs2, tabStops, 0, 0, false);
            float abs = margin2 + Math.abs(tl.metrics(null));
            TextLine.recycle(tl);
            if (mt != null) {
                mt.recycle();
            }
            return abs;
        } catch (Throwable th4) {
            th = th4;
            mt2 = mt;
            TextLine.recycle(tl);
            if (mt2 != null) {
            }
            throw th;
        }
    }

    /* loaded from: classes3.dex */
    public static class TabStops {
        private float mIncrement;
        private int mNumStops;
        private float[] mStops;

        public TabStops(float increment, Object[] spans) {
            reset(increment, spans);
        }

        void reset(float increment, Object[] spans) {
            this.mIncrement = increment;
            int ns = 0;
            if (spans != null) {
                float[] stops = this.mStops;
                for (Object o : spans) {
                    if (o instanceof TabStopSpan) {
                        if (stops == null) {
                            stops = new float[10];
                        } else if (ns == stops.length) {
                            float[] nstops = new float[ns * 2];
                            for (int i = 0; i < ns; i++) {
                                nstops[i] = stops[i];
                            }
                            stops = nstops;
                        }
                        stops[ns] = ((TabStopSpan) o).getTabStop();
                        ns++;
                    }
                }
                if (ns > 1) {
                    Arrays.sort(stops, 0, ns);
                }
                if (stops != this.mStops) {
                    this.mStops = stops;
                }
            }
            this.mNumStops = ns;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public float nextTab(float h) {
            int ns = this.mNumStops;
            if (ns > 0) {
                float[] stops = this.mStops;
                for (int i = 0; i < ns; i++) {
                    float stop = stops[i];
                    if (stop > h) {
                        return stop;
                    }
                }
            }
            return nextDefaultStop(h, this.mIncrement);
        }

        public static float nextDefaultStop(float h, float inc) {
            return ((int) ((h + inc) / inc)) * inc;
        }
    }

    static float nextTab(CharSequence text, int start, int end, float h, Object[] tabs) {
        float nh = Float.MAX_VALUE;
        boolean alltabs = false;
        if (text instanceof Spanned) {
            if (tabs == null) {
                tabs = getParagraphSpans((Spanned) text, start, end, TabStopSpan.class);
                alltabs = true;
            }
            for (int i = 0; i < tabs.length; i++) {
                if (alltabs || (tabs[i] instanceof TabStopSpan)) {
                    int where = ((TabStopSpan) tabs[i]).getTabStop();
                    if (where < nh && where > h) {
                        nh = where;
                    }
                }
            }
            if (nh != Float.MAX_VALUE) {
                return nh;
            }
        }
        return ((int) ((h + TAB_INCREMENT) / TAB_INCREMENT)) * TAB_INCREMENT;
    }

    protected final boolean isSpanned() {
        return this.mSpannedText;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> T[] getParagraphSpans(Spanned text, int start, int end, Class<T> type) {
        if (start == end && start > 0) {
            return (T[]) ArrayUtils.emptyArray(type);
        }
        if (text instanceof SpannableStringBuilder) {
            return (T[]) ((SpannableStringBuilder) text).getSpans(start, end, type, false);
        }
        return (T[]) text.getSpans(start, end, type);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ellipsize(int start, int end, int line, char[] dest, int destoff, TextUtils.TruncateAt method) {
        char c;
        int ellipsisCount = getEllipsisCount(line);
        if (ellipsisCount == 0) {
            return;
        }
        int ellipsisStart = getEllipsisStart(line);
        int lineStart = getLineStart(line);
        String ellipsisString = TextUtils.getEllipsisString(method);
        int ellipsisStringLen = ellipsisString.length();
        boolean useEllipsisString = ellipsisCount >= ellipsisStringLen;
        int min = Math.max(0, (start - ellipsisStart) - lineStart);
        int max = Math.min(ellipsisCount, (end - ellipsisStart) - lineStart);
        for (int i = min; i < max; i++) {
            if (useEllipsisString && i < ellipsisStringLen) {
                c = ellipsisString.charAt(i);
            } else {
                c = 65279;
            }
            int a = i + ellipsisStart + lineStart;
            dest[(destoff + a) - start] = c;
        }
    }

    /* loaded from: classes3.dex */
    public static class Directions {
        public int[] mDirections;

        public Directions(int[] dirs) {
            this.mDirections = dirs;
        }

        public int getRunCount() {
            return this.mDirections.length / 2;
        }

        public int getRunStart(int runIndex) {
            return this.mDirections[runIndex * 2];
        }

        public int getRunLength(int runIndex) {
            return this.mDirections[(runIndex * 2) + 1] & Layout.RUN_LENGTH_MASK;
        }

        public int getRunLevel(int runIndex) {
            return (this.mDirections[(runIndex * 2) + 1] >>> 26) & 63;
        }

        public boolean isRunRtl(int runIndex) {
            return (this.mDirections[(runIndex * 2) + 1] & 67108864) != 0;
        }
    }

    /* loaded from: classes3.dex */
    static class Ellipsizer implements CharSequence, GetChars {
        Layout mLayout;
        TextUtils.TruncateAt mMethod;
        CharSequence mText;
        int mWidth;

        public Ellipsizer(CharSequence s) {
            this.mText = s;
        }

        @Override // java.lang.CharSequence
        public char charAt(int off) {
            char[] buf = TextUtils.obtain(1);
            getChars(off, off + 1, buf, 0);
            char ret = buf[0];
            TextUtils.recycle(buf);
            return ret;
        }

        @Override // android.text.GetChars
        public void getChars(int start, int end, char[] dest, int destoff) {
            int line1 = this.mLayout.getLineForOffset(start);
            int line2 = this.mLayout.getLineForOffset(end);
            TextUtils.getChars(this.mText, start, end, dest, destoff);
            for (int i = line1; i <= line2; i++) {
                this.mLayout.ellipsize(start, end, i, dest, destoff, this.mMethod);
            }
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mText.length();
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            char[] s = new char[end - start];
            getChars(start, end, s, 0);
            return new String(s);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            char[] s = new char[length()];
            getChars(0, length(), s, 0);
            return new String(s);
        }
    }

    /* loaded from: classes3.dex */
    static class SpannedEllipsizer extends Ellipsizer implements Spanned {
        private Spanned mSpanned;

        public SpannedEllipsizer(CharSequence display) {
            super(display);
            this.mSpanned = (Spanned) display;
        }

        @Override // android.text.Spanned
        public <T> T[] getSpans(int start, int end, Class<T> type) {
            return (T[]) this.mSpanned.getSpans(start, end, type);
        }

        @Override // android.text.Spanned
        public int getSpanStart(Object tag) {
            return this.mSpanned.getSpanStart(tag);
        }

        @Override // android.text.Spanned
        public int getSpanEnd(Object tag) {
            return this.mSpanned.getSpanEnd(tag);
        }

        @Override // android.text.Spanned
        public int getSpanFlags(Object tag) {
            return this.mSpanned.getSpanFlags(tag);
        }

        @Override // android.text.Spanned
        public int nextSpanTransition(int start, int limit, Class type) {
            return this.mSpanned.nextSpanTransition(start, limit, type);
        }

        @Override // android.text.Layout.Ellipsizer, java.lang.CharSequence
        public CharSequence subSequence(int start, int end) {
            char[] s = new char[end - start];
            getChars(start, end, s, 0);
            SpannableString ss = new SpannableString(new String(s));
            TextUtils.copySpansFrom(this.mSpanned, start, end, Object.class, ss, 0);
            return ss;
        }
    }
}
