package android.text;

import android.graphics.Paint;
import android.graphics.text.LineBreakConfig;
import android.graphics.text.LineBreaker;
import android.p008os.SystemProperties;
import android.text.Layout;
import android.text.PrecomputedText;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.util.Pools;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class StaticLayout extends Layout {
    private static final char CHAR_NEW_LINE = '\n';
    private static final int COLUMNS_ELLIPSIZE = 7;
    private static final int COLUMNS_NORMAL = 5;
    private static final int DEFAULT_MAX_LINE_HEIGHT = -1;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 6;
    private static final int ELLIPSIS_START = 5;
    private static final int END_HYPHEN_MASK = 7;
    private static final int EXTRA = 3;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int HYPHEN = 4;
    private static final int HYPHEN_MASK = 255;
    private static final int START = 0;
    private static final int START_HYPHEN_BITS_SHIFT = 3;
    private static final int START_HYPHEN_MASK = 24;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final float TAB_INCREMENT = 20.0f;
    private static final int TAB_MASK = 536870912;
    static final String TAG = "StaticLayout";
    private static final int TOP = 1;
    private int mBottomPadding;
    private int mColumns;
    private boolean mEllipsized;
    private int mEllipsizedWidth;
    private boolean mFallbackLineSpacing;
    private int[] mLeftIndents;
    private int mLineCount;
    private Layout.Directions[] mLineDirections;
    private int[] mLines;
    private int mMaxLineHeight;
    private int mMaximumVisibleLineCount;
    private int[] mRightIndents;
    private int mTopPadding;

    /* loaded from: classes3.dex */
    public static final class Builder {
        private static final int DEFAULT_LINECOUNT_THRESHOLD_FOR_PHRASE = 3;
        private static final String PROPERTY_LINECOUNT_THRESHOLD_FOR_PHRASE = "android.phrase.linecount.threshold";
        private static final Pools.SynchronizedPool<Builder> sPool = new Pools.SynchronizedPool<>(3);
        private boolean mAddLastLineLineSpacing;
        private Layout.Alignment mAlignment;
        private int mBreakStrategy;
        private TextUtils.TruncateAt mEllipsize;
        private int mEllipsizedWidth;
        private int mEnd;
        private boolean mFallbackLineSpacing;
        private int mHyphenationFrequency;
        private boolean mIncludePad;
        private int mJustificationMode;
        private int[] mLeftIndents;
        private int mMaxLines;
        private TextPaint mPaint;
        private int[] mRightIndents;
        private float mSpacingAdd;
        private float mSpacingMult;
        private int mStart;
        private CharSequence mText;
        private TextDirectionHeuristic mTextDir;
        private int mWidth;
        private LineBreakConfig mLineBreakConfig = LineBreakConfig.NONE;
        private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();

        private Builder() {
        }

        public static Builder obtain(CharSequence source, int start, int end, TextPaint paint, int width) {
            Builder b = sPool.acquire();
            if (b == null) {
                b = new Builder();
            }
            b.mText = source;
            b.mStart = start;
            b.mEnd = end;
            b.mPaint = paint;
            b.mWidth = width;
            b.mAlignment = Layout.Alignment.ALIGN_NORMAL;
            b.mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
            b.mSpacingMult = 1.0f;
            b.mSpacingAdd = 0.0f;
            b.mIncludePad = true;
            b.mFallbackLineSpacing = false;
            b.mEllipsizedWidth = width;
            b.mEllipsize = null;
            b.mMaxLines = Integer.MAX_VALUE;
            b.mBreakStrategy = 0;
            b.mHyphenationFrequency = 0;
            b.mJustificationMode = 0;
            b.mLineBreakConfig = LineBreakConfig.NONE;
            return b;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void recycle(Builder b) {
            b.mPaint = null;
            b.mText = null;
            b.mLeftIndents = null;
            b.mRightIndents = null;
            sPool.release(b);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void finish() {
            this.mText = null;
            this.mPaint = null;
            this.mLeftIndents = null;
            this.mRightIndents = null;
        }

        public Builder setText(CharSequence source) {
            return setText(source, 0, source.length());
        }

        public Builder setText(CharSequence source, int start, int end) {
            this.mText = source;
            this.mStart = start;
            this.mEnd = end;
            return this;
        }

        public Builder setPaint(TextPaint paint) {
            this.mPaint = paint;
            return this;
        }

        public Builder setWidth(int width) {
            this.mWidth = width;
            if (this.mEllipsize == null) {
                this.mEllipsizedWidth = width;
            }
            return this;
        }

        public Builder setAlignment(Layout.Alignment alignment) {
            this.mAlignment = alignment;
            return this;
        }

        public Builder setTextDirection(TextDirectionHeuristic textDir) {
            this.mTextDir = textDir;
            return this;
        }

        public Builder setLineSpacing(float spacingAdd, float spacingMult) {
            this.mSpacingAdd = spacingAdd;
            this.mSpacingMult = spacingMult;
            return this;
        }

        public Builder setIncludePad(boolean includePad) {
            this.mIncludePad = includePad;
            return this;
        }

        public Builder setUseLineSpacingFromFallbacks(boolean useLineSpacingFromFallbacks) {
            this.mFallbackLineSpacing = useLineSpacingFromFallbacks;
            return this;
        }

        public Builder setEllipsizedWidth(int ellipsizedWidth) {
            this.mEllipsizedWidth = ellipsizedWidth;
            return this;
        }

        public Builder setEllipsize(TextUtils.TruncateAt ellipsize) {
            this.mEllipsize = ellipsize;
            return this;
        }

        public Builder setMaxLines(int maxLines) {
            this.mMaxLines = maxLines;
            return this;
        }

        public Builder setBreakStrategy(int breakStrategy) {
            this.mBreakStrategy = breakStrategy;
            return this;
        }

        public Builder setHyphenationFrequency(int hyphenationFrequency) {
            this.mHyphenationFrequency = hyphenationFrequency;
            return this;
        }

        public Builder setIndents(int[] leftIndents, int[] rightIndents) {
            this.mLeftIndents = leftIndents;
            this.mRightIndents = rightIndents;
            return this;
        }

        public Builder setJustificationMode(int justificationMode) {
            this.mJustificationMode = justificationMode;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setAddLastLineLineSpacing(boolean value) {
            this.mAddLastLineLineSpacing = value;
            return this;
        }

        public Builder setLineBreakConfig(LineBreakConfig lineBreakConfig) {
            this.mLineBreakConfig = lineBreakConfig;
            return this;
        }

        public StaticLayout build() {
            reviseLineBreakConfig();
            StaticLayout result = new StaticLayout(this);
            recycle(this);
            return result;
        }

        private void reviseLineBreakConfig() {
            boolean autoPhraseBreaking = this.mLineBreakConfig.getAutoPhraseBreaking();
            int wordStyle = this.mLineBreakConfig.getLineBreakWordStyle();
            if (autoPhraseBreaking && wordStyle != 1 && shouldEnablePhraseBreaking()) {
                this.mLineBreakConfig = LineBreakConfig.getLineBreakConfig(this.mLineBreakConfig.getLineBreakStyle(), 1, this.mLineBreakConfig.getAutoPhraseBreaking());
            }
        }

        private boolean shouldEnablePhraseBreaking() {
            if (TextUtils.isEmpty(this.mText) || this.mWidth <= 0) {
                return false;
            }
            int lineLimit = SystemProperties.getInt(PROPERTY_LINECOUNT_THRESHOLD_FOR_PHRASE, 3);
            double desiredWidth = Layout.getDesiredWidth(this.mText, this.mStart, this.mEnd, this.mPaint, this.mTextDir);
            int lineCount = (int) Math.ceil(desiredWidth / this.mWidth);
            return lineCount > 0 && lineCount <= lineLimit;
        }

        public int getLineBreakWordStyle() {
            return this.mLineBreakConfig.getLineBreakWordStyle();
        }
    }

    @Deprecated
    public StaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad);
    }

    @Deprecated
    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, null, 0);
    }

    @Deprecated
    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        this(source, bufstart, bufend, paint, outerwidth, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth, Integer.MAX_VALUE);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @Deprecated
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth, int maxLines) {
        super(r1, paint, outerwidth, align, textDir, spacingmult, spacingadd);
        Layout.SpannedEllipsizer ellipsizer;
        if (ellipsize == null) {
            ellipsizer = source;
        } else if (source instanceof Spanned) {
            ellipsizer = new Layout.SpannedEllipsizer(source);
        } else {
            ellipsizer = new Layout.Ellipsizer(source);
        }
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        Builder b = Builder.obtain(source, bufstart, bufend, paint, outerwidth).setAlignment(align).setTextDirection(textDir).setLineSpacing(spacingadd, spacingmult).setIncludePad(includepad).setEllipsizedWidth(ellipsizedWidth).setEllipsize(ellipsize).setMaxLines(maxLines);
        if (ellipsize == null) {
            this.mColumns = 5;
            this.mEllipsizedWidth = outerwidth;
        } else {
            Layout.Ellipsizer e = (Layout.Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = ellipsizedWidth;
            e.mMethod = ellipsize;
            this.mEllipsizedWidth = ellipsizedWidth;
            this.mColumns = 7;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = maxLines;
        generate(b, b.mIncludePad, b.mIncludePad);
        Builder.recycle(b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaticLayout(CharSequence text) {
        super(text, null, 0, null, 0.0f, 0.0f);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mColumns = 7;
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.CharSequence] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private StaticLayout(Builder b) {
        super(r3, b.mPaint, b.mWidth, b.mAlignment, b.mTextDir, b.mSpacingMult, b.mSpacingAdd);
        Layout.SpannedEllipsizer ellipsizer;
        if (b.mEllipsize == null) {
            ellipsizer = b.mText;
        } else if (b.mText instanceof Spanned) {
            ellipsizer = new Layout.SpannedEllipsizer(b.mText);
        } else {
            ellipsizer = new Layout.Ellipsizer(b.mText);
        }
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        if (b.mEllipsize != null) {
            Layout.Ellipsizer e = (Layout.Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = b.mEllipsizedWidth;
            e.mMethod = b.mEllipsize;
            this.mEllipsizedWidth = b.mEllipsizedWidth;
            this.mColumns = 7;
        } else {
            this.mColumns = 5;
            this.mEllipsizedWidth = b.mWidth;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = b.mMaxLines;
        this.mLeftIndents = b.mLeftIndents;
        this.mRightIndents = b.mRightIndents;
        setJustificationMode(b.mJustificationMode);
        generate(b, b.mIncludePad, b.mIncludePad);
    }

    private static int getBaseHyphenationFrequency(int frequency) {
        switch (frequency) {
            case 1:
            case 3:
                return 1;
            case 2:
            case 4:
                return 2;
            default:
                return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x032e, code lost:
        if (r2 == android.text.TextUtils.TruncateAt.MARQUEE) goto L177;
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x05b2, code lost:
        continue;
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x031f  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x0335  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x033e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x03ad  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0633 A[LOOP:0: B:50:0x0188->B:189:0x0633, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:204:0x0623 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x02a6  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x02e1 A[LOOP:3: B:96:0x02df->B:97:0x02e1, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void generate(Builder b, boolean includepad, boolean trackpad) {
        int[] indents;
        LineBreaker.ParagraphConstraints constraints;
        Paint.FontMetricsInt fm;
        LineBreaker lineBreaker;
        TextUtils.TruncateAt ellipsize;
        float ellipsizedWidth;
        boolean z;
        Spanned spanned;
        PrecomputedText.ParagraphInfo[] paragraphInfo;
        TextDirectionHeuristic textDir;
        TextPaint paint;
        int bufEnd;
        int bufStart;
        CharSequence source;
        StaticLayout staticLayout;
        float ellipsizedWidth2;
        TextUtils.TruncateAt ellipsize2;
        Paint.FontMetricsInt fm2;
        int v;
        CharSequence source2;
        TextDirectionHeuristic textDir2;
        TextPaint paint2;
        int bufStart2;
        CharSequence source3;
        int[] chooseHtv;
        int firstWidthLineCount;
        int firstWidth;
        int restWidth;
        LineHeightSpan[] chooseHt;
        float[] variableTabStops;
        int breakCount;
        LineBreaker.ParagraphConstraints constraints2;
        int lineBreakCapacity;
        int[] breaks;
        float[] lineWidths;
        float[] ascents;
        float[] descents;
        boolean[] hasTabs;
        int[] hyphenEdits;
        int i;
        int fmTop;
        TextUtils.TruncateAt ellipsize3;
        boolean z2;
        TextUtils.TruncateAt ellipsize4;
        PrecomputedText.ParagraphInfo[] paragraphInfo2;
        float[] variableTabStops2;
        int breakCount2;
        int spanStart;
        int paraEnd;
        int breakIndex;
        int fmTop2;
        MeasuredParagraph measuredPara;
        int ascent;
        int descent;
        int fmTop3;
        int fmBottom;
        Paint.FontMetricsInt fm3;
        int fmTop4;
        int fmTop5;
        float[] variableTabStops3;
        StaticLayout staticLayout2 = this;
        CharSequence source4 = b.mText;
        int bufStart3 = b.mStart;
        int bufEnd2 = b.mEnd;
        TextPaint paint3 = b.mPaint;
        int outerWidth = b.mWidth;
        TextDirectionHeuristic textDir3 = b.mTextDir;
        float spacingmult = b.mSpacingMult;
        float spacingadd = b.mSpacingAdd;
        float ellipsizedWidth3 = b.mEllipsizedWidth;
        TextUtils.TruncateAt ellipsize5 = b.mEllipsize;
        boolean addLastLineSpacing = b.mAddLastLineLineSpacing;
        int[] breaks2 = null;
        float[] lineWidths2 = null;
        float[] ascents2 = null;
        float[] descents2 = null;
        boolean[] hasTabs2 = null;
        int[] hyphenEdits2 = null;
        staticLayout2.mLineCount = 0;
        staticLayout2.mEllipsized = false;
        staticLayout2.mMaxLineHeight = staticLayout2.mMaximumVisibleLineCount < 1 ? 0 : -1;
        staticLayout2.mFallbackLineSpacing = b.mFallbackLineSpacing;
        int v2 = 0;
        boolean needMultiply = (spacingmult == 1.0f && spacingadd == 0.0f) ? false : true;
        Paint.FontMetricsInt fm4 = b.mFontMetricsInt;
        int[] indents2 = staticLayout2.mLeftIndents;
        if (indents2 != null || staticLayout2.mRightIndents != null) {
            int leftLen = indents2 == null ? 0 : indents2.length;
            int[] iArr = staticLayout2.mRightIndents;
            int rightLen = iArr == null ? 0 : iArr.length;
            int indentsLen = Math.max(leftLen, rightLen);
            int[] indents3 = new int[indentsLen];
            for (int i2 = 0; i2 < leftLen; i2++) {
                indents3[i2] = staticLayout2.mLeftIndents[i2];
            }
            for (int i3 = 0; i3 < rightLen; i3++) {
                indents3[i3] = indents3[i3] + staticLayout2.mRightIndents[i3];
            }
            indents = indents3;
        } else {
            indents = null;
        }
        LineBreaker lineBreaker2 = new LineBreaker.Builder().setBreakStrategy(b.mBreakStrategy).setHyphenationFrequency(getBaseHyphenationFrequency(b.mHyphenationFrequency)).setJustificationMode(b.mJustificationMode).setIndents(indents).build();
        LineBreaker.ParagraphConstraints constraints3 = new LineBreaker.ParagraphConstraints();
        PrecomputedText.ParagraphInfo[] paragraphInfo3 = null;
        Spanned spanned2 = source4 instanceof Spanned ? (Spanned) source4 : null;
        if (!(source4 instanceof PrecomputedText)) {
            constraints = constraints3;
            fm = fm4;
            lineBreaker = lineBreaker2;
            ellipsize = ellipsize5;
            ellipsizedWidth = ellipsizedWidth3;
            z = false;
            spanned = spanned2;
        } else {
            PrecomputedText precomputed = (PrecomputedText) source4;
            ellipsizedWidth = ellipsizedWidth3;
            spanned = spanned2;
            ellipsize = ellipsize5;
            fm = fm4;
            lineBreaker = lineBreaker2;
            constraints = constraints3;
            z = false;
            int checkResult = precomputed.checkResultUsable(bufStart3, bufEnd2, textDir3, paint3, b.mBreakStrategy, b.mHyphenationFrequency, b.mLineBreakConfig);
            switch (checkResult) {
                case 1:
                    PrecomputedText.Params newParams = new PrecomputedText.Params.Builder(paint3).setBreakStrategy(b.mBreakStrategy).setHyphenationFrequency(b.mHyphenationFrequency).setTextDirection(textDir3).setLineBreakConfig(b.mLineBreakConfig).build();
                    paragraphInfo3 = PrecomputedText.create(precomputed, newParams).getParagraphInfo();
                    break;
                case 2:
                    paragraphInfo3 = precomputed.getParagraphInfo();
                    break;
            }
        }
        if (paragraphInfo3 != null) {
            paragraphInfo = paragraphInfo3;
        } else {
            PrecomputedText.Params param = new PrecomputedText.Params(paint3, b.mLineBreakConfig, textDir3, b.mBreakStrategy, b.mHyphenationFrequency);
            paragraphInfo = PrecomputedText.createMeasuredParagraphs(source4, param, bufStart3, bufEnd2, z);
        }
        int paraIndex = 0;
        int lineBreakCapacity2 = 0;
        int[] chooseHtv2 = null;
        while (true) {
            if (paraIndex < paragraphInfo.length) {
                int paraStart = paraIndex == 0 ? bufStart3 : paragraphInfo[paraIndex - 1].paragraphEnd;
                int paraEnd2 = paragraphInfo[paraIndex].paragraphEnd;
                int firstWidthLineCount2 = 1;
                int firstWidth2 = outerWidth;
                int restWidth2 = outerWidth;
                if (spanned == null) {
                    textDir2 = textDir3;
                    paint2 = paint3;
                    bufStart2 = bufStart3;
                    source3 = source4;
                    chooseHtv = chooseHtv2;
                    firstWidthLineCount = 1;
                    firstWidth = firstWidth2;
                    restWidth = restWidth2;
                    chooseHt = null;
                } else {
                    LeadingMarginSpan[] sp = (LeadingMarginSpan[]) getParagraphSpans(spanned, paraStart, paraEnd2, LeadingMarginSpan.class);
                    textDir2 = textDir3;
                    int i4 = 0;
                    while (true) {
                        paint2 = paint3;
                        if (i4 < sp.length) {
                            LeadingMarginSpan lms = sp[i4];
                            int bufStart4 = bufStart3;
                            CharSequence source5 = source4;
                            firstWidth2 -= sp[i4].getLeadingMargin(true);
                            restWidth2 -= sp[i4].getLeadingMargin(false);
                            if (lms instanceof LeadingMarginSpan.LeadingMarginSpan2) {
                                LeadingMarginSpan.LeadingMarginSpan2 lms2 = (LeadingMarginSpan.LeadingMarginSpan2) lms;
                                firstWidthLineCount2 = Math.max(firstWidthLineCount2, lms2.getLeadingMarginLineCount());
                            }
                            i4++;
                            paint3 = paint2;
                            bufStart3 = bufStart4;
                            source4 = source5;
                        } else {
                            bufStart2 = bufStart3;
                            source3 = source4;
                            LineHeightSpan[] chooseHt2 = (LineHeightSpan[]) getParagraphSpans(spanned, paraStart, paraEnd2, LineHeightSpan.class);
                            if (chooseHt2.length == 0) {
                                chooseHtv = chooseHtv2;
                                firstWidthLineCount = firstWidthLineCount2;
                                firstWidth = firstWidth2;
                                restWidth = restWidth2;
                                chooseHt = null;
                            } else {
                                if (chooseHtv2 == null || chooseHtv2.length < chooseHt2.length) {
                                    chooseHtv2 = ArrayUtils.newUnpaddedIntArray(chooseHt2.length);
                                }
                                for (int i5 = 0; i5 < chooseHt2.length; i5++) {
                                    int o = spanned.getSpanStart(chooseHt2[i5]);
                                    if (o < paraStart) {
                                        chooseHtv2[i5] = staticLayout2.getLineTop(staticLayout2.getLineForOffset(o));
                                    } else {
                                        chooseHtv2[i5] = v2;
                                    }
                                }
                                chooseHtv = chooseHtv2;
                                firstWidthLineCount = firstWidthLineCount2;
                                firstWidth = firstWidth2;
                                chooseHt = chooseHt2;
                                restWidth = restWidth2;
                            }
                        }
                    }
                }
                if (spanned != null) {
                    TabStopSpan[] spans = (TabStopSpan[]) getParagraphSpans(spanned, paraStart, paraEnd2, TabStopSpan.class);
                    if (spans.length > 0) {
                        float[] stops = new float[spans.length];
                        for (int i6 = 0; i6 < spans.length; i6++) {
                            stops[i6] = spans[i6].getTabStop();
                        }
                        int i7 = stops.length;
                        Arrays.sort(stops, 0, i7);
                        variableTabStops = stops;
                        MeasuredParagraph measuredPara2 = paragraphInfo[paraIndex].measured;
                        char[] chs = measuredPara2.getChars();
                        int[] spanEndCache = measuredPara2.getSpanEndCache().getRawArray();
                        int[] fmCache = measuredPara2.getFontMetrics().getRawArray();
                        LineBreaker.ParagraphConstraints constraints4 = constraints;
                        constraints4.setWidth(restWidth);
                        constraints4.setIndent(firstWidth, firstWidthLineCount);
                        constraints4.setTabStops(variableTabStops, TAB_INCREMENT);
                        int firstWidthLineCount3 = firstWidthLineCount;
                        LineBreaker lineBreaker3 = lineBreaker;
                        LineBreaker.Result res = lineBreaker3.computeLineBreaks(measuredPara2.getMeasuredText(), constraints4, staticLayout2.mLineCount);
                        breakCount = res.getLineCount();
                        if (lineBreakCapacity2 < breakCount) {
                            constraints2 = constraints4;
                            lineBreakCapacity = lineBreakCapacity2;
                            breaks = breaks2;
                            lineWidths = lineWidths2;
                            ascents = ascents2;
                            descents = descents2;
                            hasTabs = hasTabs2;
                            hyphenEdits = hyphenEdits2;
                        } else {
                            constraints2 = constraints4;
                            int[] breaks3 = new int[breakCount];
                            float[] lineWidths3 = new float[breakCount];
                            float[] lineWidths4 = new float[breakCount];
                            float[] ascents3 = new float[breakCount];
                            boolean[] hasTabs3 = new boolean[breakCount];
                            lineBreakCapacity = breakCount;
                            hyphenEdits = new int[breakCount];
                            breaks = breaks3;
                            lineWidths = lineWidths3;
                            ascents = lineWidths4;
                            descents = ascents3;
                            hasTabs = hasTabs3;
                        }
                        i = 0;
                        while (i < breakCount) {
                            breaks[i] = res.getLineBreakOffset(i);
                            lineWidths[i] = res.getLineWidth(i);
                            ascents[i] = res.getLineAscent(i);
                            descents[i] = res.getLineDescent(i);
                            hasTabs[i] = res.hasLineTab(i);
                            int startLineHyphenEdit = res.getStartLineHyphenEdit(i);
                            int paraIndex2 = paraIndex;
                            int paraIndex3 = res.getEndLineHyphenEdit(i);
                            hyphenEdits[i] = packHyphenEdit(startLineHyphenEdit, paraIndex3);
                            i++;
                            paraIndex = paraIndex2;
                        }
                        int paraIndex4 = paraIndex;
                        int i8 = staticLayout2.mMaximumVisibleLineCount;
                        fmTop = i8 - staticLayout2.mLineCount;
                        if (ellipsize == null) {
                            ellipsize3 = ellipsize;
                            if (ellipsize3 != TextUtils.TruncateAt.END) {
                                if (staticLayout2.mMaximumVisibleLineCount == 1) {
                                }
                            }
                            z2 = true;
                            boolean ellipsisMayBeApplied = z2;
                            if (fmTop > 0 || fmTop >= breakCount || !ellipsisMayBeApplied) {
                                ellipsize4 = ellipsize3;
                                paragraphInfo2 = paragraphInfo;
                                variableTabStops2 = variableTabStops;
                                breakCount2 = breakCount;
                            } else {
                                float width = 0.0f;
                                boolean hasTab = false;
                                ellipsize4 = ellipsize3;
                                int i9 = fmTop - 1;
                                while (i9 < breakCount) {
                                    PrecomputedText.ParagraphInfo[] paragraphInfo4 = paragraphInfo;
                                    if (i9 == breakCount - 1) {
                                        width += lineWidths[i9];
                                        variableTabStops3 = variableTabStops;
                                    } else {
                                        int j = i9 == 0 ? 0 : breaks[i9 - 1];
                                        while (true) {
                                            variableTabStops3 = variableTabStops;
                                            if (j < breaks[i9]) {
                                                width += measuredPara2.getCharWidthAt(j);
                                                j++;
                                                variableTabStops = variableTabStops3;
                                            }
                                        }
                                    }
                                    hasTab |= hasTabs[i9];
                                    i9++;
                                    paragraphInfo = paragraphInfo4;
                                    variableTabStops = variableTabStops3;
                                }
                                paragraphInfo2 = paragraphInfo;
                                variableTabStops2 = variableTabStops;
                                int i10 = fmTop - 1;
                                breaks[i10] = breaks[breakCount - 1];
                                lineWidths[fmTop - 1] = width;
                                hasTabs[fmTop - 1] = hasTab;
                                breakCount2 = fmTop;
                            }
                            int spanEnd = paraStart;
                            int fmTop6 = 0;
                            int fmBottom2 = 0;
                            int fmAscent = 0;
                            int fmDescent = 0;
                            int fmCacheIndex = 0;
                            int spanEndCacheIndex = 0;
                            int breakIndex2 = 0;
                            spanStart = paraStart;
                            while (spanStart < paraEnd2) {
                                int spanEndCacheIndex2 = spanEndCacheIndex + 1;
                                int spanStart2 = firstWidth;
                                int spanEnd2 = spanEndCache[spanEndCacheIndex];
                                int i11 = 0;
                                int here = spanEnd;
                                int here2 = fmCache[(fmCacheIndex * 4) + 0];
                                int restWidth3 = restWidth;
                                Paint.FontMetricsInt fm5 = fm;
                                fm5.top = here2;
                                boolean z3 = true;
                                fm5.bottom = fmCache[(fmCacheIndex * 4) + 1];
                                fm5.ascent = fmCache[(fmCacheIndex * 4) + 2];
                                fm5.descent = fmCache[(fmCacheIndex * 4) + 3];
                                int fmCacheIndex2 = fmCacheIndex + 1;
                                if (fm5.top < fmTop6) {
                                    fmTop6 = fm5.top;
                                }
                                if (fm5.ascent < fmAscent) {
                                    fmAscent = fm5.ascent;
                                }
                                if (fm5.descent > fmDescent) {
                                    fmDescent = fm5.descent;
                                }
                                if (fm5.bottom <= fmBottom2) {
                                    breakIndex = breakIndex2;
                                } else {
                                    fmBottom2 = fm5.bottom;
                                    breakIndex = breakIndex2;
                                }
                                while (true) {
                                    if (breakIndex < breakCount2) {
                                        fmTop2 = fmTop6;
                                        if (paraStart + breaks[breakIndex] < spanStart) {
                                            breakIndex++;
                                            fmTop6 = fmTop2;
                                        }
                                    } else {
                                        fmTop2 = fmTop6;
                                    }
                                }
                                int here3 = here;
                                int v3 = v2;
                                fmTop6 = fmTop2;
                                int i12 = fmDescent;
                                int fmDescent2 = breakIndex;
                                int spanEnd3 = i12;
                                while (true) {
                                    if (fmDescent2 < breakCount2) {
                                        measuredPara = measuredPara2;
                                        if (paraStart + breaks[fmDescent2] <= spanEnd2) {
                                            int endPos = breaks[fmDescent2] + paraStart;
                                            boolean moreChars = endPos < bufEnd2 ? z3 : i11;
                                            if (staticLayout2.mFallbackLineSpacing) {
                                                ascent = Math.min(fmAscent, Math.round(ascents[fmDescent2]));
                                            } else {
                                                ascent = fmAscent;
                                            }
                                            int paraEnd3 = paraEnd2;
                                            if (staticLayout2.mFallbackLineSpacing) {
                                                descent = Math.max(spanEnd3, Math.round(descents[fmDescent2]));
                                            } else {
                                                descent = spanEnd3;
                                            }
                                            if (!staticLayout2.mFallbackLineSpacing) {
                                                fmTop3 = fmTop6;
                                                fmBottom = fmBottom2;
                                            } else {
                                                if (ascent < fmTop6) {
                                                    fmTop6 = ascent;
                                                }
                                                if (descent <= fmBottom2) {
                                                    fmTop3 = fmTop6;
                                                    fmBottom = fmBottom2;
                                                } else {
                                                    int fmBottom3 = descent;
                                                    fmTop3 = fmTop6;
                                                    fmBottom = fmBottom3;
                                                }
                                            }
                                            int breakIndex3 = fmDescent2;
                                            TextUtils.TruncateAt ellipsize6 = ellipsize4;
                                            boolean z4 = z3;
                                            int fmAscent2 = i11;
                                            LineBreaker.ParagraphConstraints constraints5 = constraints2;
                                            Spanned spanned3 = spanned;
                                            float ellipsizedWidth4 = ellipsizedWidth;
                                            int paraStart2 = paraStart;
                                            int remainingLineCount = fmTop;
                                            int paraIndex5 = paraIndex4;
                                            int remainingLineCount2 = fmTop3;
                                            int fmAscent3 = fmBottom;
                                            TextDirectionHeuristic textDir4 = textDir2;
                                            float[] variableTabStops4 = variableTabStops2;
                                            int breakCount3 = breakCount2;
                                            Paint.FontMetricsInt fm6 = fm5;
                                            TextPaint paint4 = paint2;
                                            int restWidth4 = restWidth3;
                                            int bufEnd3 = bufEnd2;
                                            int spanEnd4 = spanEnd2;
                                            int bufStart5 = bufStart2;
                                            int bufStart6 = spanStart2;
                                            int spanStart3 = spanStart;
                                            CharSequence source6 = source3;
                                            int firstWidthLineCount4 = firstWidthLineCount3;
                                            v3 = out(source3, here3, endPos, ascent, descent, remainingLineCount2, fmAscent3, v3, spacingmult, spacingadd, chooseHt, chooseHtv, fm6, hasTabs[fmDescent2], hyphenEdits[breakIndex3], needMultiply, measuredPara, bufEnd3, includepad, trackpad, addLastLineSpacing, chs, paraStart2, ellipsize6, ellipsizedWidth4, lineWidths[breakIndex3], paint4, moreChars);
                                            if (endPos < spanEnd4) {
                                                fm3 = fm6;
                                                int fmTop7 = fm3.top;
                                                fmBottom2 = fm3.bottom;
                                                int fmAscent4 = fm3.ascent;
                                                fmAscent = fmAscent4;
                                                fmTop5 = fmTop7;
                                                fmTop4 = fm3.descent;
                                            } else {
                                                fm3 = fm6;
                                                fmTop4 = fmAscent2;
                                                fmTop5 = fmAscent2;
                                                fmAscent = fmAscent2;
                                                fmBottom2 = fmAscent2;
                                            }
                                            here3 = endPos;
                                            fmDescent2 = breakIndex3 + 1;
                                            if (this.mLineCount >= this.mMaximumVisibleLineCount && this.mEllipsized) {
                                                return;
                                            }
                                            spanEnd2 = spanEnd4;
                                            spanEnd3 = fmTop4;
                                            fm5 = fm3;
                                            staticLayout2 = this;
                                            fmTop6 = fmTop5;
                                            restWidth3 = restWidth4;
                                            firstWidthLineCount3 = firstWidthLineCount4;
                                            breakCount2 = breakCount3;
                                            spanStart = spanStart3;
                                            measuredPara2 = measuredPara;
                                            ellipsize4 = ellipsize6;
                                            constraints2 = constraints5;
                                            i11 = fmAscent2;
                                            z3 = z4;
                                            ellipsizedWidth = ellipsizedWidth4;
                                            spanned = spanned3;
                                            paraEnd2 = paraEnd3;
                                            paraStart = paraStart2;
                                            paraIndex4 = paraIndex5;
                                            fmTop = remainingLineCount;
                                            paint2 = paint4;
                                            bufEnd2 = bufEnd3;
                                            source3 = source6;
                                            variableTabStops2 = variableTabStops4;
                                            spanStart2 = bufStart6;
                                            textDir2 = textDir4;
                                            bufStart2 = bufStart5;
                                        }
                                    } else {
                                        measuredPara = measuredPara2;
                                    }
                                }
                                int remainingLineCount3 = fmTop;
                                Paint.FontMetricsInt fm7 = fm5;
                                int breakIndex4 = fmDescent2;
                                float ellipsizedWidth5 = ellipsizedWidth;
                                int fmDescent3 = spanEnd3;
                                int fmDescent4 = spanEnd2;
                                spanStart = fmDescent4;
                                staticLayout2 = staticLayout2;
                                fmCacheIndex = fmCacheIndex2;
                                restWidth = restWidth3;
                                firstWidth = spanStart2;
                                fmDescent = fmDescent3;
                                firstWidthLineCount3 = firstWidthLineCount3;
                                spanEnd = here3;
                                breakCount2 = breakCount2;
                                spanEndCacheIndex = spanEndCacheIndex2;
                                v2 = v3;
                                ellipsize4 = ellipsize4;
                                constraints2 = constraints2;
                                breakIndex2 = breakIndex4;
                                ellipsizedWidth = ellipsizedWidth5;
                                spanned = spanned;
                                paraEnd2 = paraEnd2;
                                paraStart = paraStart;
                                paraIndex4 = paraIndex4;
                                fmTop = remainingLineCount3;
                                fmAscent = fmAscent;
                                paint2 = paint2;
                                bufEnd2 = bufEnd2;
                                bufStart2 = bufStart2;
                                source3 = source3;
                                fm = fm7;
                                variableTabStops2 = variableTabStops2;
                                measuredPara2 = measuredPara;
                                textDir2 = textDir2;
                            }
                            paraEnd = paraEnd2;
                            staticLayout = staticLayout2;
                            Spanned spanned4 = spanned;
                            ellipsize2 = ellipsize4;
                            int paraIndex6 = paraIndex4;
                            LineBreaker.ParagraphConstraints constraints6 = constraints2;
                            textDir = textDir2;
                            paint = paint2;
                            bufStart = bufStart2;
                            ellipsizedWidth2 = ellipsizedWidth;
                            source = source3;
                            fm2 = fm;
                            bufEnd = bufEnd2;
                            if (paraEnd != bufEnd) {
                                paraIndex = paraIndex6 + 1;
                                fm = fm2;
                                staticLayout2 = staticLayout;
                                bufEnd2 = bufEnd;
                                chooseHtv2 = chooseHtv;
                                lineBreakCapacity2 = lineBreakCapacity;
                                breaks2 = breaks;
                                lineWidths2 = lineWidths;
                                ascents2 = ascents;
                                descents2 = descents;
                                hasTabs2 = hasTabs;
                                hyphenEdits2 = hyphenEdits;
                                paragraphInfo = paragraphInfo2;
                                lineBreaker = lineBreaker3;
                                ellipsize = ellipsize2;
                                constraints = constraints6;
                                ellipsizedWidth = ellipsizedWidth2;
                                spanned = spanned4;
                                textDir3 = textDir;
                                paint3 = paint;
                                bufStart3 = bufStart;
                                source4 = source;
                            } else {
                                v = v2;
                            }
                        } else {
                            ellipsize3 = ellipsize;
                        }
                        z2 = false;
                        boolean ellipsisMayBeApplied2 = z2;
                        if (fmTop > 0) {
                        }
                        ellipsize4 = ellipsize3;
                        paragraphInfo2 = paragraphInfo;
                        variableTabStops2 = variableTabStops;
                        breakCount2 = breakCount;
                        int spanEnd5 = paraStart;
                        int fmTop62 = 0;
                        int fmBottom22 = 0;
                        int fmAscent5 = 0;
                        int fmDescent5 = 0;
                        int fmCacheIndex3 = 0;
                        int spanEndCacheIndex3 = 0;
                        int breakIndex22 = 0;
                        spanStart = paraStart;
                        while (spanStart < paraEnd2) {
                        }
                        paraEnd = paraEnd2;
                        staticLayout = staticLayout2;
                        Spanned spanned42 = spanned;
                        ellipsize2 = ellipsize4;
                        int paraIndex62 = paraIndex4;
                        LineBreaker.ParagraphConstraints constraints62 = constraints2;
                        textDir = textDir2;
                        paint = paint2;
                        bufStart = bufStart2;
                        ellipsizedWidth2 = ellipsizedWidth;
                        source = source3;
                        fm2 = fm;
                        bufEnd = bufEnd2;
                        if (paraEnd != bufEnd) {
                        }
                    }
                }
                variableTabStops = null;
                MeasuredParagraph measuredPara22 = paragraphInfo[paraIndex].measured;
                char[] chs2 = measuredPara22.getChars();
                int[] spanEndCache2 = measuredPara22.getSpanEndCache().getRawArray();
                int[] fmCache2 = measuredPara22.getFontMetrics().getRawArray();
                LineBreaker.ParagraphConstraints constraints42 = constraints;
                constraints42.setWidth(restWidth);
                constraints42.setIndent(firstWidth, firstWidthLineCount);
                constraints42.setTabStops(variableTabStops, TAB_INCREMENT);
                int firstWidthLineCount32 = firstWidthLineCount;
                LineBreaker lineBreaker32 = lineBreaker;
                LineBreaker.Result res2 = lineBreaker32.computeLineBreaks(measuredPara22.getMeasuredText(), constraints42, staticLayout2.mLineCount);
                breakCount = res2.getLineCount();
                if (lineBreakCapacity2 < breakCount) {
                }
                i = 0;
                while (i < breakCount) {
                }
                int paraIndex42 = paraIndex;
                int i82 = staticLayout2.mMaximumVisibleLineCount;
                fmTop = i82 - staticLayout2.mLineCount;
                if (ellipsize == null) {
                }
                z2 = false;
                boolean ellipsisMayBeApplied22 = z2;
                if (fmTop > 0) {
                }
                ellipsize4 = ellipsize3;
                paragraphInfo2 = paragraphInfo;
                variableTabStops2 = variableTabStops;
                breakCount2 = breakCount;
                int spanEnd52 = paraStart;
                int fmTop622 = 0;
                int fmBottom222 = 0;
                int fmAscent52 = 0;
                int fmDescent52 = 0;
                int fmCacheIndex32 = 0;
                int spanEndCacheIndex32 = 0;
                int breakIndex222 = 0;
                spanStart = paraStart;
                while (spanStart < paraEnd2) {
                }
                paraEnd = paraEnd2;
                staticLayout = staticLayout2;
                Spanned spanned422 = spanned;
                ellipsize2 = ellipsize4;
                int paraIndex622 = paraIndex42;
                LineBreaker.ParagraphConstraints constraints622 = constraints2;
                textDir = textDir2;
                paint = paint2;
                bufStart = bufStart2;
                ellipsizedWidth2 = ellipsizedWidth;
                source = source3;
                fm2 = fm;
                bufEnd = bufEnd2;
                if (paraEnd != bufEnd) {
                }
            } else {
                textDir = textDir3;
                paint = paint3;
                bufEnd = bufEnd2;
                bufStart = bufStart3;
                source = source4;
                staticLayout = staticLayout2;
                ellipsizedWidth2 = ellipsizedWidth;
                ellipsize2 = ellipsize;
                fm2 = fm;
                v = v2;
            }
        }
        int bufStart7 = bufStart;
        if (bufEnd != bufStart7) {
            source2 = source;
            if (source2.charAt(bufEnd - 1) != '\n') {
                return;
            }
        } else {
            source2 = source;
        }
        if (staticLayout.mLineCount < staticLayout.mMaximumVisibleLineCount) {
            MeasuredParagraph measuredPara3 = MeasuredParagraph.buildForBidi(source2, bufEnd, bufEnd, textDir, null);
            TextPaint paint5 = paint;
            paint5.getFontMetricsInt(fm2);
            out(source2, bufEnd, bufEnd, fm2.ascent, fm2.descent, fm2.top, fm2.bottom, v, spacingmult, spacingadd, null, null, fm2, false, 0, needMultiply, measuredPara3, bufEnd, includepad, trackpad, addLastLineSpacing, null, bufStart7, ellipsize2, ellipsizedWidth2, 0.0f, paint5, false);
        }
    }

    private int out(CharSequence text, int start, int end, int above, int below, int top, int bottom, int v, float spacingmult, float spacingadd, LineHeightSpan[] chooseHt, int[] chooseHtv, Paint.FontMetricsInt fm, boolean hasTab, int hyphenEdit, boolean needMultiply, MeasuredParagraph measured, int bufEnd, boolean includePad, boolean trackPad, boolean addLastLineLineSpacing, char[] chs, int widthStart, TextUtils.TruncateAt ellipsize, float ellipsisWidth, float textWidth, TextPaint paint, boolean moreChars) {
        int[] lines;
        int i;
        int j;
        int above2;
        int below2;
        int top2;
        int bottom2;
        int i2;
        int i3;
        TextUtils.TruncateAt truncateAt;
        int i4;
        int i5;
        int i6;
        int i7;
        boolean lastCharIsNewLine;
        int extra;
        int want;
        int i8;
        int j2;
        int j3 = this.mLineCount;
        int i9 = this.mColumns;
        int off = j3 * i9;
        int i10 = 1;
        int want2 = off + i9 + 1;
        int[] lines2 = this.mLines;
        int dir = measured.getParagraphDir();
        if (want2 < lines2.length) {
            lines = lines2;
        } else {
            int[] grow = ArrayUtils.newUnpaddedIntArray(GrowingArrayUtils.growSize(want2));
            System.arraycopy(lines2, 0, grow, 0, lines2.length);
            this.mLines = grow;
            lines = grow;
        }
        if (j3 >= this.mLineDirections.length) {
            Layout.Directions[] grow2 = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, GrowingArrayUtils.growSize(j3));
            Layout.Directions[] directionsArr = this.mLineDirections;
            System.arraycopy(directionsArr, 0, grow2, 0, directionsArr.length);
            this.mLineDirections = grow2;
        }
        if (chooseHt != null) {
            fm.ascent = above;
            fm.descent = below;
            fm.top = top;
            fm.bottom = bottom;
            int i11 = 0;
            while (i11 < chooseHt.length) {
                if (chooseHt[i11] instanceof LineHeightSpan.WithDensity) {
                    want = want2;
                    i8 = i10;
                    j2 = j3;
                    ((LineHeightSpan.WithDensity) chooseHt[i11]).chooseHeight(text, start, end, chooseHtv[i11], v, fm, paint);
                } else {
                    want = want2;
                    i8 = i10;
                    j2 = j3;
                    chooseHt[i11].chooseHeight(text, start, end, chooseHtv[i11], v, fm);
                }
                i11++;
                i10 = i8;
                want2 = want;
                j3 = j2;
            }
            i = i10;
            j = j3;
            above2 = fm.ascent;
            below2 = fm.descent;
            top2 = fm.top;
            bottom2 = fm.bottom;
        } else {
            i = 1;
            j = j3;
            above2 = above;
            below2 = below;
            top2 = top;
            bottom2 = bottom;
        }
        int i12 = j == 0 ? i : 0;
        int i13 = j + 1;
        int i14 = this.mMaximumVisibleLineCount;
        int i15 = i13 == i14 ? i : 0;
        if (ellipsize == null) {
            i2 = widthStart;
            i3 = bufEnd;
            truncateAt = ellipsize;
            i4 = 0;
        } else {
            boolean forceEllipsis = (moreChars && this.mLineCount + i == i14) ? i : 0;
            if (((((!(i14 == i && moreChars) && (i12 == 0 || moreChars)) || ellipsize == TextUtils.TruncateAt.MARQUEE) && (i12 != 0 || ((i15 == 0 && moreChars) || ellipsize != TextUtils.TruncateAt.END))) ? 0 : i) != 0) {
                i2 = widthStart;
                truncateAt = ellipsize;
                i3 = bufEnd;
                calculateEllipsis(start, end, measured, widthStart, ellipsisWidth, ellipsize, j, textWidth, paint, forceEllipsis);
                i4 = 0;
            } else {
                i2 = widthStart;
                truncateAt = ellipsize;
                i3 = bufEnd;
                int[] iArr = this.mLines;
                int i16 = this.mColumns;
                i4 = 0;
                iArr[(i16 * j) + 5] = 0;
                iArr[(i16 * j) + 6] = 0;
            }
        }
        if (this.mEllipsized) {
            lastCharIsNewLine = true;
            i6 = i4;
            i7 = start;
        } else {
            if (i2 != i3 && i3 > 0) {
                if (text.charAt(i3 - 1) == '\n') {
                    i5 = 1;
                    int i17 = i5;
                    if (end != i3 && i17 == 0) {
                        lastCharIsNewLine = true;
                        i6 = i4;
                        i7 = start;
                    } else {
                        i6 = i4;
                        i7 = start;
                        if (i7 != i3 && i17 != 0) {
                            lastCharIsNewLine = true;
                        } else {
                            lastCharIsNewLine = false;
                        }
                    }
                }
            }
            i5 = i4;
            int i172 = i5;
            if (end != i3) {
            }
            i6 = i4;
            i7 = start;
            if (i7 != i3) {
            }
            lastCharIsNewLine = false;
        }
        if (i12 != 0) {
            if (trackPad) {
                this.mTopPadding = top2 - above2;
            }
            if (includePad) {
                above2 = top2;
            }
        }
        if (lastCharIsNewLine) {
            if (trackPad) {
                this.mBottomPadding = bottom2 - below2;
            }
            if (includePad) {
                below2 = bottom2;
            }
        }
        if (needMultiply && (addLastLineLineSpacing || !lastCharIsNewLine)) {
            double ex = ((below2 - above2) * (spacingmult - 1.0f)) + spacingadd;
            if (ex >= 0.0d) {
                extra = (int) (EXTRA_ROUNDING + ex);
            } else {
                extra = -((int) ((-ex) + EXTRA_ROUNDING));
            }
        } else {
            extra = 0;
        }
        lines[off + 0] = i7;
        lines[off + 1] = v;
        lines[off + 2] = below2 + extra;
        lines[off + 3] = extra;
        boolean z = this.mEllipsized;
        if (!z && i15 != 0) {
            int maxLineBelow = includePad ? bottom2 : below2;
            this.mMaxLineHeight = v + (maxLineBelow - above2);
        }
        int maxLineBelow2 = below2 - above2;
        int v2 = v + maxLineBelow2 + extra;
        int i18 = this.mColumns;
        lines[off + i18 + i6] = end;
        lines[off + i18 + 1] = v2;
        int i19 = off + 0;
        lines[i19] = lines[i19] | (hasTab ? 536870912 : i6);
        if (z) {
            if (truncateAt == TextUtils.TruncateAt.START) {
                lines[off + 4] = packHyphenEdit(i6, unpackEndHyphenEdit(hyphenEdit));
            } else if (truncateAt == TextUtils.TruncateAt.END) {
                lines[off + 4] = packHyphenEdit(unpackStartHyphenEdit(hyphenEdit), i6);
            } else {
                lines[off + 4] = packHyphenEdit(i6, i6);
            }
        } else {
            lines[off + 4] = hyphenEdit;
        }
        int i20 = off + 0;
        lines[i20] = lines[i20] | (dir << 30);
        this.mLineDirections[j] = measured.getDirections(i7 - i2, end - i2);
        this.mLineCount++;
        return v2;
    }

    private void calculateEllipsis(int lineStart, int lineEnd, MeasuredParagraph measured, int widthStart, float avail, TextUtils.TruncateAt where, int line, float textWidth, TextPaint paint, boolean forceEllipsis) {
        float avail2 = avail - getTotalInsets(line);
        if (textWidth <= avail2 && !forceEllipsis) {
            int[] iArr = this.mLines;
            int i = this.mColumns;
            iArr[(i * line) + 5] = 0;
            iArr[(i * line) + 6] = 0;
            return;
        }
        float ellipsisWidth = paint.measureText(TextUtils.getEllipsisString(where));
        int ellipsisStart = 0;
        int ellipsisCount = 0;
        int len = lineEnd - lineStart;
        if (where == TextUtils.TruncateAt.START) {
            if (this.mMaximumVisibleLineCount == 1) {
                float sum = 0.0f;
                int i2 = len;
                while (true) {
                    if (i2 <= 0) {
                        break;
                    }
                    float w = measured.getCharWidthAt(((i2 - 1) + lineStart) - widthStart);
                    if (w + sum + ellipsisWidth > avail2) {
                        while (i2 < len && measured.getCharWidthAt((i2 + lineStart) - widthStart) == 0.0f) {
                            i2++;
                        }
                    } else {
                        sum += w;
                        i2--;
                    }
                }
                ellipsisStart = 0;
                ellipsisCount = i2;
            } else if (Log.isLoggable(TAG, 5)) {
                Log.m104w(TAG, "Start Ellipsis only supported with one line");
            }
        } else if (where == TextUtils.TruncateAt.END || where == TextUtils.TruncateAt.MARQUEE || where == TextUtils.TruncateAt.END_SMALL) {
            float sum2 = 0.0f;
            int i3 = 0;
            while (i3 < len) {
                float w2 = measured.getCharWidthAt((i3 + lineStart) - widthStart);
                if (w2 + sum2 + ellipsisWidth > avail2) {
                    break;
                }
                sum2 += w2;
                i3++;
            }
            ellipsisStart = i3;
            ellipsisCount = len - i3;
            if (forceEllipsis && ellipsisCount == 0 && len > 0) {
                ellipsisStart = len - 1;
                ellipsisCount = 1;
            }
        } else if (this.mMaximumVisibleLineCount == 1) {
            float lsum = 0.0f;
            float rsum = 0.0f;
            int right = len;
            float ravail = (avail2 - ellipsisWidth) / 2.0f;
            while (true) {
                if (right <= 0) {
                    break;
                }
                float w3 = measured.getCharWidthAt(((right - 1) + lineStart) - widthStart);
                if (w3 + rsum <= ravail) {
                    rsum += w3;
                    right--;
                } else {
                    while (right < len && measured.getCharWidthAt((right + lineStart) - widthStart) == 0.0f) {
                        right++;
                    }
                }
            }
            float lavail = (avail2 - ellipsisWidth) - rsum;
            int left = 0;
            while (left < right) {
                float w4 = measured.getCharWidthAt((left + lineStart) - widthStart);
                if (w4 + lsum > lavail) {
                    break;
                }
                lsum += w4;
                left++;
            }
            ellipsisStart = left;
            ellipsisCount = right - left;
        } else if (Log.isLoggable(TAG, 5)) {
            Log.m104w(TAG, "Middle Ellipsis only supported with one line");
        }
        this.mEllipsized = true;
        int[] iArr2 = this.mLines;
        int i4 = this.mColumns;
        iArr2[(i4 * line) + 5] = ellipsisStart;
        iArr2[(i4 * line) + 6] = ellipsisCount;
    }

    private float getTotalInsets(int line) {
        int totalIndent = 0;
        int[] iArr = this.mLeftIndents;
        if (iArr != null) {
            totalIndent = iArr[Math.min(line, iArr.length - 1)];
        }
        int[] iArr2 = this.mRightIndents;
        if (iArr2 != null) {
            totalIndent += iArr2[Math.min(line, iArr2.length - 1)];
        }
        return totalIndent;
    }

    @Override // android.text.Layout
    public int getLineForVertical(int vertical) {
        int high = this.mLineCount;
        int low = -1;
        int[] lines = this.mLines;
        while (high - low > 1) {
            int guess = (high + low) >> 1;
            if (lines[(this.mColumns * guess) + 1] > vertical) {
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

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        return this.mLines[(this.mColumns * line) + 1];
    }

    @Override // android.text.Layout
    public int getLineExtra(int line) {
        return this.mLines[(this.mColumns * line) + 3];
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        return this.mLines[(this.mColumns * line) + 2];
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        return this.mLines[(this.mColumns * line) + 0] & 536870911;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return this.mLines[(this.mColumns * line) + 0] >> 30;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return (this.mLines[(this.mColumns * line) + 0] & 536870912) != 0;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        if (line > getLineCount()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.mLineDirections[line];
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int packHyphenEdit(int start, int end) {
        return (start << 3) | end;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int unpackStartHyphenEdit(int packedHyphenEdit) {
        return (packedHyphenEdit & 24) >> 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int unpackEndHyphenEdit(int packedHyphenEdit) {
        return packedHyphenEdit & 7;
    }

    @Override // android.text.Layout
    public int getStartHyphenEdit(int lineNumber) {
        return unpackStartHyphenEdit(this.mLines[(this.mColumns * lineNumber) + 4] & 255);
    }

    @Override // android.text.Layout
    public int getEndHyphenEdit(int lineNumber) {
        return unpackEndHyphenEdit(this.mLines[(this.mColumns * lineNumber) + 4] & 255);
    }

    @Override // android.text.Layout
    public int getIndentAdjust(int line, Layout.Alignment align) {
        if (align == Layout.Alignment.ALIGN_LEFT) {
            int[] iArr = this.mLeftIndents;
            if (iArr == null) {
                return 0;
            }
            return iArr[Math.min(line, iArr.length - 1)];
        } else if (align == Layout.Alignment.ALIGN_RIGHT) {
            int[] iArr2 = this.mRightIndents;
            if (iArr2 == null) {
                return 0;
            }
            return -iArr2[Math.min(line, iArr2.length - 1)];
        } else if (align == Layout.Alignment.ALIGN_CENTER) {
            int left = 0;
            int[] iArr3 = this.mLeftIndents;
            if (iArr3 != null) {
                left = iArr3[Math.min(line, iArr3.length - 1)];
            }
            int right = 0;
            int[] iArr4 = this.mRightIndents;
            if (iArr4 != null) {
                right = iArr4[Math.min(line, iArr4.length - 1)];
            }
            return (left - right) >> 1;
        } else {
            throw new AssertionError("unhandled alignment " + align);
        }
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int line) {
        int i = this.mColumns;
        if (i < 7) {
            return 0;
        }
        return this.mLines[(i * line) + 6];
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        int i = this.mColumns;
        if (i < 7) {
            return 0;
        }
        return this.mLines[(i * line) + 5];
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public boolean isFallbackLineSpacingEnabled() {
        return this.mFallbackLineSpacing;
    }

    @Override // android.text.Layout
    public int getHeight(boolean cap) {
        int i;
        if (cap && this.mLineCount > this.mMaximumVisibleLineCount && this.mMaxLineHeight == -1 && Log.isLoggable(TAG, 5)) {
            Log.m104w(TAG, "maxLineHeight should not be -1.  maxLines:" + this.mMaximumVisibleLineCount + " lineCount:" + this.mLineCount);
        }
        return (!cap || this.mLineCount <= this.mMaximumVisibleLineCount || (i = this.mMaxLineHeight) == -1) ? super.getHeight() : i;
    }

    /* loaded from: classes3.dex */
    static class LineBreaks {
        private static final int INITIAL_SIZE = 16;
        public int[] breaks = new int[16];
        public float[] widths = new float[16];
        public float[] ascents = new float[16];
        public float[] descents = new float[16];
        public int[] flags = new int[16];

        LineBreaks() {
        }
    }
}
