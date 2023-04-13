package android.text;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.OffsetMapping;
import android.text.style.ReplacementSpan;
import android.text.style.UpdateLayout;
import android.text.style.WrapTogetherSpan;
import android.util.ArraySet;
import android.util.Pools;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.lang.ref.WeakReference;
/* loaded from: classes3.dex */
public class DynamicLayout extends Layout {
    private static final int BLOCK_MINIMUM_CHARACTER_LENGTH = 400;
    private static final int COLUMNS_ELLIPSIZE = 7;
    private static final int COLUMNS_NORMAL = 5;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 6;
    private static final int ELLIPSIS_START = 5;
    private static final int ELLIPSIS_UNDEFINED = Integer.MIN_VALUE;
    private static final int EXTRA = 3;
    private static final int HYPHEN = 4;
    private static final int HYPHEN_MASK = 255;
    public static final int INVALID_BLOCK_INDEX = -1;
    private static final int MAY_PROTRUDE_FROM_TOP_OR_BOTTOM = 4;
    private static final int MAY_PROTRUDE_FROM_TOP_OR_BOTTOM_MASK = 256;
    private static final int PRIORITY = 128;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_MASK = 536870912;
    private static final int TOP = 1;
    private CharSequence mBase;
    private int[] mBlockEndLines;
    private int[] mBlockIndices;
    private ArraySet<Integer> mBlocksAlwaysNeedToBeRedrawn;
    private int mBottomPadding;
    private int mBreakStrategy;
    private CharSequence mDisplay;
    private boolean mEllipsize;
    private TextUtils.TruncateAt mEllipsizeAt;
    private int mEllipsizedWidth;
    private boolean mFallbackLineSpacing;
    private int mHyphenationFrequency;
    private boolean mIncludePad;
    private int mIndexFirstChangedBlock;
    private PackedIntVector mInts;
    private int mJustificationMode;
    private int mNumberOfBlocks;
    private PackedObjectVector<Layout.Directions> mObjects;
    private Rect mTempRect;
    private int mTopPadding;
    private ChangeWatcher mWatcher;
    private static StaticLayout sStaticLayout = null;
    private static StaticLayout.Builder sBuilder = null;
    private static final Object[] sLock = new Object[0];

    /* loaded from: classes3.dex */
    public static final class Builder {
        private static final Pools.SynchronizedPool<Builder> sPool = new Pools.SynchronizedPool<>(3);
        private Layout.Alignment mAlignment;
        private CharSequence mBase;
        private int mBreakStrategy;
        private CharSequence mDisplay;
        private TextUtils.TruncateAt mEllipsize;
        private int mEllipsizedWidth;
        private boolean mFallbackLineSpacing;
        private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
        private int mHyphenationFrequency;
        private boolean mIncludePad;
        private int mJustificationMode;
        private TextPaint mPaint;
        private float mSpacingAdd;
        private float mSpacingMult;
        private TextDirectionHeuristic mTextDir;
        private int mWidth;

        private Builder() {
        }

        public static Builder obtain(CharSequence base, TextPaint paint, int width) {
            Builder b = sPool.acquire();
            if (b == null) {
                b = new Builder();
            }
            b.mBase = base;
            b.mDisplay = base;
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
            b.mBreakStrategy = 0;
            b.mHyphenationFrequency = 0;
            b.mJustificationMode = 0;
            return b;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void recycle(Builder b) {
            b.mBase = null;
            b.mDisplay = null;
            b.mPaint = null;
            sPool.release(b);
        }

        public Builder setDisplayText(CharSequence display) {
            this.mDisplay = display;
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

        public Builder setBreakStrategy(int breakStrategy) {
            this.mBreakStrategy = breakStrategy;
            return this;
        }

        public Builder setHyphenationFrequency(int hyphenationFrequency) {
            this.mHyphenationFrequency = hyphenationFrequency;
            return this;
        }

        public Builder setJustificationMode(int justificationMode) {
            this.mJustificationMode = justificationMode;
            return this;
        }

        public DynamicLayout build() {
            DynamicLayout result = new DynamicLayout(this);
            recycle(this);
            return result;
        }
    }

    @Deprecated
    public DynamicLayout(CharSequence base, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, base, paint, width, align, spacingmult, spacingadd, includepad);
    }

    @Deprecated
    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, display, paint, width, align, spacingmult, spacingadd, includepad, null, 0);
    }

    @Deprecated
    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        this(base, display, paint, width, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, 0, 0, 0, ellipsize, ellipsizedWidth);
    }

    @Deprecated
    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Layout.Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, int breakStrategy, int hyphenationFrequency, int justificationMode, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        super(createEllipsizer(ellipsize, display), paint, width, align, textDir, spacingmult, spacingadd);
        this.mTempRect = new Rect();
        Builder b = Builder.obtain(base, paint, width).setAlignment(align).setTextDirection(textDir).setLineSpacing(spacingadd, spacingmult).setEllipsizedWidth(ellipsizedWidth).setEllipsize(ellipsize);
        this.mDisplay = display;
        this.mIncludePad = includepad;
        this.mBreakStrategy = breakStrategy;
        this.mJustificationMode = justificationMode;
        this.mHyphenationFrequency = hyphenationFrequency;
        generate(b);
        Builder.recycle(b);
    }

    private DynamicLayout(Builder b) {
        super(createEllipsizer(b.mEllipsize, b.mDisplay), b.mPaint, b.mWidth, b.mAlignment, b.mTextDir, b.mSpacingMult, b.mSpacingAdd);
        this.mTempRect = new Rect();
        this.mDisplay = b.mDisplay;
        this.mIncludePad = b.mIncludePad;
        this.mBreakStrategy = b.mBreakStrategy;
        this.mJustificationMode = b.mJustificationMode;
        this.mHyphenationFrequency = b.mHyphenationFrequency;
        generate(b);
    }

    private static CharSequence createEllipsizer(TextUtils.TruncateAt ellipsize, CharSequence display) {
        if (ellipsize == null) {
            return display;
        }
        if (display instanceof Spanned) {
            return new Layout.SpannedEllipsizer(display);
        }
        return new Layout.Ellipsizer(display);
    }

    private void generate(Builder b) {
        int[] start;
        this.mBase = b.mBase;
        this.mFallbackLineSpacing = b.mFallbackLineSpacing;
        if (b.mEllipsize != null) {
            this.mInts = new PackedIntVector(7);
            this.mEllipsizedWidth = b.mEllipsizedWidth;
            this.mEllipsizeAt = b.mEllipsize;
            Layout.Ellipsizer e = (Layout.Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = b.mEllipsizedWidth;
            e.mMethod = b.mEllipsize;
            this.mEllipsize = true;
        } else {
            this.mInts = new PackedIntVector(5);
            this.mEllipsizedWidth = b.mWidth;
            this.mEllipsizeAt = null;
        }
        this.mObjects = new PackedObjectVector<>(1);
        if (b.mEllipsize != null) {
            start = new int[7];
            start[5] = Integer.MIN_VALUE;
        } else {
            start = new int[5];
        }
        Layout.Directions[] dirs = {DIRS_ALL_LEFT_TO_RIGHT};
        Paint.FontMetricsInt fm = b.mFontMetricsInt;
        b.mPaint.getFontMetricsInt(fm);
        int asc = fm.ascent;
        int desc = fm.descent;
        start[0] = 1073741824;
        start[1] = 0;
        start[2] = desc;
        this.mInts.insertAt(0, start);
        start[1] = desc - asc;
        this.mInts.insertAt(1, start);
        this.mObjects.insertAt(0, dirs);
        reflow(this.mBase, 0, 0, this.mDisplay.length());
        if (this.mBase instanceof Spannable) {
            if (this.mWatcher == null) {
                this.mWatcher = new ChangeWatcher(this);
            }
            CharSequence charSequence = this.mBase;
            Spannable sp = (Spannable) charSequence;
            int baseLength = charSequence.length();
            ChangeWatcher[] spans = (ChangeWatcher[]) sp.getSpans(0, baseLength, ChangeWatcher.class);
            for (ChangeWatcher changeWatcher : spans) {
                sp.removeSpan(changeWatcher);
            }
            sp.setSpan(this.mWatcher, 0, baseLength, 8388626);
        }
    }

    public void reflow(CharSequence s, int where, int before, int after) {
        int find;
        int look;
        int where2;
        int endline;
        StaticLayout reflowed;
        StaticLayout.Builder b;
        StaticLayout reflowed2;
        StaticLayout.Builder b2;
        int botpad;
        int botpad2;
        int[] ints;
        if (s != this.mBase) {
            return;
        }
        CharSequence text = this.mDisplay;
        int len = text.length();
        int find2 = TextUtils.lastIndexOf(text, '\n', where - 1);
        if (find2 < 0) {
            find = 0;
        } else {
            find = find2 + 1;
        }
        int find3 = where - find;
        int before2 = before + find3;
        int after2 = after + find3;
        int where3 = where - find3;
        int look2 = TextUtils.indexOf(text, '\n', where3 + after2);
        if (look2 < 0) {
            look = len;
        } else {
            look = look2 + 1;
        }
        int change = look - (where3 + after2);
        int before3 = before2 + change;
        int after3 = after2 + change;
        if (!(text instanceof Spanned)) {
            where2 = where3;
        } else {
            Spanned sp = (Spanned) text;
            while (true) {
                boolean again = false;
                Object[] force = sp.getSpans(where3, where3 + after3, WrapTogetherSpan.class);
                for (int i = 0; i < force.length; i++) {
                    int st = sp.getSpanStart(force[i]);
                    int en = sp.getSpanEnd(force[i]);
                    if (st < where3) {
                        again = true;
                        int diff = where3 - st;
                        before3 += diff;
                        after3 += diff;
                        where3 -= diff;
                    }
                    if (en > where3 + after3) {
                        int diff2 = en - (where3 + after3);
                        before3 += diff2;
                        after3 += diff2;
                        again = true;
                    }
                }
                if (!again) {
                    break;
                }
            }
            where2 = where3;
        }
        int startline = getLineForOffset(where2);
        int startv = getLineTop(startline);
        int endline2 = getLineForOffset(where2 + before3);
        if (where2 + after3 != len) {
            endline = endline2;
        } else {
            int endline3 = getLineCount();
            endline = endline3;
        }
        int endv = getLineTop(endline);
        boolean islast = endline == getLineCount();
        synchronized (sLock) {
            try {
                reflowed = sStaticLayout;
                b = sBuilder;
                sStaticLayout = null;
                sBuilder = null;
            } catch (Throwable th) {
                th = th;
                while (true) {
                    try {
                        break;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
                throw th;
            }
        }
        if (reflowed == null) {
            StaticLayout reflowed3 = new StaticLayout((CharSequence) null);
            StaticLayout.Builder b3 = StaticLayout.Builder.obtain(text, where2, where2 + after3, getPaint(), getWidth());
            reflowed2 = reflowed3;
            b2 = b3;
        } else {
            reflowed2 = reflowed;
            b2 = b;
        }
        b2.setText(text, where2, where2 + after3).setPaint(getPaint()).setWidth(getWidth()).setTextDirection(getTextDirectionHeuristic()).setLineSpacing(getSpacingAdd(), getSpacingMultiplier()).setUseLineSpacingFromFallbacks(this.mFallbackLineSpacing).setEllipsizedWidth(this.mEllipsizedWidth).setEllipsize(this.mEllipsizeAt).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setAddLastLineLineSpacing(!islast);
        reflowed2.generate(b2, false, true);
        int n = reflowed2.getLineCount();
        int n2 = (where2 + after3 != len && reflowed2.getLineStart(n - 1) == where2 + after3) ? n - 1 : n;
        this.mInts.deleteAt(startline, endline - startline);
        this.mObjects.deleteAt(startline, endline - startline);
        int ht = reflowed2.getLineTop(n2);
        int toppad = 0;
        if (this.mIncludePad && startline == 0) {
            toppad = reflowed2.getTopPadding();
            this.mTopPadding = toppad;
            ht -= toppad;
        }
        if (this.mIncludePad && islast) {
            int botpad3 = reflowed2.getBottomPadding();
            this.mBottomPadding = botpad3;
            botpad2 = botpad3;
            botpad = ht + botpad3;
        } else {
            botpad = ht;
            botpad2 = 0;
        }
        this.mInts.adjustValuesBelow(startline, 0, after3 - before3);
        this.mInts.adjustValuesBelow(startline, 1, (startv - endv) + botpad);
        if (this.mEllipsize) {
            int[] ints2 = new int[7];
            ints2[5] = Integer.MIN_VALUE;
            ints = ints2;
        } else {
            int[] ints3 = new int[5];
            ints = ints3;
        }
        Layout.Directions[] objects = new Layout.Directions[1];
        int i2 = 0;
        while (i2 < n2) {
            int ht2 = botpad;
            int start = reflowed2.getLineStart(i2);
            ints[0] = start;
            ints[0] = ints[0] | (reflowed2.getParagraphDirection(i2) << 30);
            ints[0] = ints[0] | (reflowed2.getLineContainsTab(i2) ? 536870912 : 0);
            int top = reflowed2.getLineTop(i2) + startv;
            if (i2 > 0) {
                top -= toppad;
            }
            ints[1] = top;
            int desc = reflowed2.getLineDescent(i2);
            int toppad2 = toppad;
            if (i2 == n2 - 1) {
                desc += botpad2;
            }
            ints[2] = desc;
            ints[3] = reflowed2.getLineExtra(i2);
            objects[0] = reflowed2.getLineDirections(i2);
            int end = i2 == n2 + (-1) ? where2 + after3 : reflowed2.getLineStart(i2 + 1);
            int where4 = where2;
            int where5 = reflowed2.getStartHyphenEdit(i2);
            int after4 = after3;
            int after5 = reflowed2.getEndHyphenEdit(i2);
            ints[4] = StaticLayout.packHyphenEdit(where5, after5);
            ints[4] = ints[4] | (contentMayProtrudeFromLineTopOrBottom(text, start, end) ? 256 : 0);
            if (this.mEllipsize) {
                ints[5] = reflowed2.getEllipsisStart(i2);
                ints[6] = reflowed2.getEllipsisCount(i2);
            }
            this.mInts.insertAt(startline + i2, ints);
            this.mObjects.insertAt(startline + i2, objects);
            i2++;
            where2 = where4;
            botpad = ht2;
            toppad = toppad2;
            after3 = after4;
        }
        int i3 = endline - 1;
        updateBlocks(startline, i3, n2);
        b2.finish();
        synchronized (sLock) {
            sStaticLayout = reflowed2;
            sBuilder = b2;
        }
    }

    private boolean contentMayProtrudeFromLineTopOrBottom(CharSequence text, int start, int end) {
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            if (((ReplacementSpan[]) spanned.getSpans(start, end, ReplacementSpan.class)).length > 0) {
                return true;
            }
        }
        Paint paint = getPaint();
        if (text instanceof PrecomputedText) {
            PrecomputedText precomputed = (PrecomputedText) text;
            precomputed.getBounds(start, end, this.mTempRect);
        } else {
            paint.getTextBounds(text, start, end, this.mTempRect);
        }
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        return this.mTempRect.top < fm.top || this.mTempRect.bottom > fm.bottom;
    }

    private void createBlocks() {
        int offset = 400;
        this.mNumberOfBlocks = 0;
        CharSequence text = this.mDisplay;
        while (true) {
            int offset2 = TextUtils.indexOf(text, '\n', offset);
            if (offset2 < 0) {
                break;
            }
            addBlockAtOffset(offset2);
            offset = offset2 + 400;
        }
        addBlockAtOffset(text.length());
        this.mBlockIndices = new int[this.mBlockEndLines.length];
        for (int i = 0; i < this.mBlockEndLines.length; i++) {
            this.mBlockIndices[i] = -1;
        }
    }

    public ArraySet<Integer> getBlocksAlwaysNeedToBeRedrawn() {
        return this.mBlocksAlwaysNeedToBeRedrawn;
    }

    private void updateAlwaysNeedsToBeRedrawn(int blockIndex) {
        int startLine = blockIndex == 0 ? 0 : this.mBlockEndLines[blockIndex - 1] + 1;
        int endLine = this.mBlockEndLines[blockIndex];
        for (int i = startLine; i <= endLine; i++) {
            if (getContentMayProtrudeFromTopOrBottom(i)) {
                if (this.mBlocksAlwaysNeedToBeRedrawn == null) {
                    this.mBlocksAlwaysNeedToBeRedrawn = new ArraySet<>();
                }
                this.mBlocksAlwaysNeedToBeRedrawn.add(Integer.valueOf(blockIndex));
                return;
            }
        }
        ArraySet<Integer> arraySet = this.mBlocksAlwaysNeedToBeRedrawn;
        if (arraySet != null) {
            arraySet.remove(Integer.valueOf(blockIndex));
        }
    }

    private void addBlockAtOffset(int offset) {
        int line = getLineForOffset(offset);
        int[] iArr = this.mBlockEndLines;
        if (iArr == null) {
            int[] newUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(1);
            this.mBlockEndLines = newUnpaddedIntArray;
            int i = this.mNumberOfBlocks;
            newUnpaddedIntArray[i] = line;
            updateAlwaysNeedsToBeRedrawn(i);
            this.mNumberOfBlocks++;
            return;
        }
        int i2 = this.mNumberOfBlocks;
        int previousBlockEndLine = iArr[i2 - 1];
        if (line > previousBlockEndLine) {
            this.mBlockEndLines = GrowingArrayUtils.append(iArr, i2, line);
            updateAlwaysNeedsToBeRedrawn(this.mNumberOfBlocks);
            this.mNumberOfBlocks++;
        }
    }

    public void updateBlocks(int startLine, int endLine, int newLineCount) {
        int i;
        int i2;
        int lastBlockEndLine;
        boolean createBlockAfter;
        int newFirstChangedBlock;
        if (this.mBlockEndLines == null) {
            createBlocks();
            return;
        }
        int firstBlock = -1;
        int lastBlock = -1;
        int i3 = 0;
        while (true) {
            if (i3 < this.mNumberOfBlocks) {
                if (this.mBlockEndLines[i3] < startLine) {
                    i3++;
                } else {
                    firstBlock = i3;
                    break;
                }
            } else {
                break;
            }
        }
        int i4 = firstBlock;
        while (true) {
            i = this.mNumberOfBlocks;
            if (i4 < i) {
                if (this.mBlockEndLines[i4] < endLine) {
                    i4++;
                } else {
                    lastBlock = i4;
                    break;
                }
            } else {
                break;
            }
        }
        int[] iArr = this.mBlockEndLines;
        int lastBlockEndLine2 = iArr[lastBlock];
        if (firstBlock == 0) {
            i2 = 0;
        } else {
            i2 = iArr[firstBlock - 1] + 1;
        }
        boolean createBlockBefore = startLine > i2;
        boolean createBlock = newLineCount > 0;
        boolean createBlockAfter2 = endLine < iArr[lastBlock];
        int numAddedBlocks = createBlockBefore ? 0 + 1 : 0;
        if (createBlock) {
            numAddedBlocks++;
        }
        if (createBlockAfter2) {
            numAddedBlocks++;
        }
        int numRemovedBlocks = (lastBlock - firstBlock) + 1;
        int newNumberOfBlocks = (i + numAddedBlocks) - numRemovedBlocks;
        if (newNumberOfBlocks == 0) {
            iArr[0] = 0;
            this.mBlockIndices[0] = -1;
            this.mNumberOfBlocks = 1;
            return;
        }
        if (newNumberOfBlocks > iArr.length) {
            int[] blockEndLines = ArrayUtils.newUnpaddedIntArray(Math.max(iArr.length * 2, newNumberOfBlocks));
            int[] blockIndices = new int[blockEndLines.length];
            System.arraycopy(this.mBlockEndLines, 0, blockEndLines, 0, firstBlock);
            System.arraycopy(this.mBlockIndices, 0, blockIndices, 0, firstBlock);
            lastBlockEndLine = lastBlockEndLine2;
            createBlockAfter = createBlockAfter2;
            System.arraycopy(this.mBlockEndLines, lastBlock + 1, blockEndLines, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            System.arraycopy(this.mBlockIndices, lastBlock + 1, blockIndices, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            this.mBlockEndLines = blockEndLines;
            this.mBlockIndices = blockIndices;
        } else {
            lastBlockEndLine = lastBlockEndLine2;
            createBlockAfter = createBlockAfter2;
            if (numAddedBlocks + numRemovedBlocks != 0) {
                System.arraycopy(iArr, lastBlock + 1, iArr, firstBlock + numAddedBlocks, (i - lastBlock) - 1);
                int[] iArr2 = this.mBlockIndices;
                System.arraycopy(iArr2, lastBlock + 1, iArr2, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) - 1);
            }
        }
        if (numAddedBlocks + numRemovedBlocks != 0 && this.mBlocksAlwaysNeedToBeRedrawn != null) {
            ArraySet<Integer> set = new ArraySet<>();
            int changedBlockCount = numAddedBlocks - numRemovedBlocks;
            for (int i5 = 0; i5 < this.mBlocksAlwaysNeedToBeRedrawn.size(); i5++) {
                Integer block = this.mBlocksAlwaysNeedToBeRedrawn.valueAt(i5);
                if (block.intValue() < firstBlock) {
                    set.add(block);
                }
                if (block.intValue() > lastBlock) {
                    set.add(Integer.valueOf(block.intValue() + changedBlockCount));
                }
            }
            this.mBlocksAlwaysNeedToBeRedrawn = set;
        }
        this.mNumberOfBlocks = newNumberOfBlocks;
        int deltaLines = newLineCount - ((endLine - startLine) + 1);
        if (deltaLines != 0) {
            newFirstChangedBlock = firstBlock + numAddedBlocks;
            for (int i6 = newFirstChangedBlock; i6 < this.mNumberOfBlocks; i6++) {
                int[] iArr3 = this.mBlockEndLines;
                iArr3[i6] = iArr3[i6] + deltaLines;
            }
        } else {
            newFirstChangedBlock = this.mNumberOfBlocks;
        }
        this.mIndexFirstChangedBlock = Math.min(this.mIndexFirstChangedBlock, newFirstChangedBlock);
        int blockIndex = firstBlock;
        if (createBlockBefore) {
            this.mBlockEndLines[blockIndex] = startLine - 1;
            updateAlwaysNeedsToBeRedrawn(blockIndex);
            this.mBlockIndices[blockIndex] = -1;
            blockIndex++;
        }
        if (createBlock) {
            this.mBlockEndLines[blockIndex] = (startLine + newLineCount) - 1;
            updateAlwaysNeedsToBeRedrawn(blockIndex);
            this.mBlockIndices[blockIndex] = -1;
            blockIndex++;
        }
        if (createBlockAfter) {
            this.mBlockEndLines[blockIndex] = lastBlockEndLine + deltaLines;
            updateAlwaysNeedsToBeRedrawn(blockIndex);
            this.mBlockIndices[blockIndex] = -1;
        }
    }

    public void setBlocksDataForTest(int[] blockEndLines, int[] blockIndices, int numberOfBlocks, int totalLines) {
        int[] iArr = new int[blockEndLines.length];
        this.mBlockEndLines = iArr;
        this.mBlockIndices = new int[blockIndices.length];
        System.arraycopy(blockEndLines, 0, iArr, 0, blockEndLines.length);
        System.arraycopy(blockIndices, 0, this.mBlockIndices, 0, blockIndices.length);
        this.mNumberOfBlocks = numberOfBlocks;
        while (this.mInts.size() < totalLines) {
            PackedIntVector packedIntVector = this.mInts;
            packedIntVector.insertAt(packedIntVector.size(), new int[5]);
        }
    }

    public int[] getBlockEndLines() {
        return this.mBlockEndLines;
    }

    public int[] getBlockIndices() {
        return this.mBlockIndices;
    }

    public int getBlockIndex(int index) {
        return this.mBlockIndices[index];
    }

    public void setBlockIndex(int index, int blockIndex) {
        this.mBlockIndices[index] = blockIndex;
    }

    public int getNumberOfBlocks() {
        return this.mNumberOfBlocks;
    }

    public int getIndexFirstChangedBlock() {
        return this.mIndexFirstChangedBlock;
    }

    public void setIndexFirstChangedBlock(int i) {
        this.mIndexFirstChangedBlock = i;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mInts.size() - 1;
    }

    @Override // android.text.Layout
    public int getLineTop(int line) {
        return this.mInts.getValue(line, 1);
    }

    @Override // android.text.Layout
    public int getLineDescent(int line) {
        return this.mInts.getValue(line, 2);
    }

    @Override // android.text.Layout
    public int getLineExtra(int line) {
        return this.mInts.getValue(line, 3);
    }

    @Override // android.text.Layout
    public int getLineStart(int line) {
        return this.mInts.getValue(line, 0) & 536870911;
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int line) {
        return (this.mInts.getValue(line, 0) & 536870912) != 0;
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int line) {
        return this.mInts.getValue(line, 0) >> 30;
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int line) {
        return this.mObjects.getValue(line, 0);
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
    public int getStartHyphenEdit(int line) {
        return StaticLayout.unpackStartHyphenEdit(this.mInts.getValue(line, 4) & 255);
    }

    @Override // android.text.Layout
    public int getEndHyphenEdit(int line) {
        return StaticLayout.unpackEndHyphenEdit(this.mInts.getValue(line, 4) & 255);
    }

    private boolean getContentMayProtrudeFromTopOrBottom(int line) {
        return (this.mInts.getValue(line, 4) & 256) != 0;
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ChangeWatcher implements TextWatcher, SpanWatcher {
        private WeakReference<DynamicLayout> mLayout;
        private OffsetMapping.TextUpdate mTransformedTextUpdate;

        public ChangeWatcher(DynamicLayout layout) {
            this.mLayout = new WeakReference<>(layout);
        }

        private void reflow(CharSequence s, int where, int before, int after) {
            DynamicLayout ml = this.mLayout.get();
            if (ml != null) {
                ml.reflow(s, where, before, after);
            } else if (s instanceof Spannable) {
                ((Spannable) s).removeSpan(this);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int where, int before, int after) {
            DynamicLayout dynamicLayout = this.mLayout.get();
            if (dynamicLayout != null && (dynamicLayout.mDisplay instanceof OffsetMapping)) {
                OffsetMapping transformedText = (OffsetMapping) dynamicLayout.mDisplay;
                OffsetMapping.TextUpdate textUpdate = this.mTransformedTextUpdate;
                if (textUpdate == null) {
                    this.mTransformedTextUpdate = new OffsetMapping.TextUpdate(where, before, after);
                } else {
                    textUpdate.where = where;
                    this.mTransformedTextUpdate.before = before;
                    this.mTransformedTextUpdate.after = after;
                }
                transformedText.originalToTransformed(this.mTransformedTextUpdate);
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int where, int before, int after) {
            DynamicLayout dynamicLayout = this.mLayout.get();
            if (dynamicLayout != null && (dynamicLayout.mDisplay instanceof OffsetMapping)) {
                where = this.mTransformedTextUpdate.where;
                before = this.mTransformedTextUpdate.before;
                after = this.mTransformedTextUpdate.after;
            }
            reflow(s, where, before, after);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }

        private void transformAndReflow(Spannable s, int start, int end) {
            DynamicLayout dynamicLayout = this.mLayout.get();
            if (dynamicLayout != null && (dynamicLayout.mDisplay instanceof OffsetMapping)) {
                OffsetMapping transformedText = (OffsetMapping) dynamicLayout.mDisplay;
                start = transformedText.originalToTransformed(start, 0);
                end = transformedText.originalToTransformed(end, 0);
            }
            reflow(s, start, end - start, end - start);
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                transformAndReflow(s, start, end);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                transformAndReflow(s, start, end);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable s, Object o, int start, int end, int nstart, int nend) {
            if (o instanceof UpdateLayout) {
                if (start > end) {
                    start = 0;
                }
                transformAndReflow(s, start, end);
                transformAndReflow(s, nstart, nend);
            }
        }
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int line) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(line, 5);
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int line) {
        if (this.mEllipsizeAt == null) {
            return 0;
        }
        return this.mInts.getValue(line, 6);
    }
}
