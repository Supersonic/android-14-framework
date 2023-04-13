package android.graphics.text;

import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class LineBreaker {
    public static final int BREAK_STRATEGY_BALANCED = 2;
    public static final int BREAK_STRATEGY_HIGH_QUALITY = 1;
    public static final int BREAK_STRATEGY_SIMPLE = 0;
    public static final int HYPHENATION_FREQUENCY_FULL = 2;
    public static final int HYPHENATION_FREQUENCY_NONE = 0;
    public static final int HYPHENATION_FREQUENCY_NORMAL = 1;
    public static final int JUSTIFICATION_MODE_INTER_WORD = 1;
    public static final int JUSTIFICATION_MODE_NONE = 0;
    private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(LineBreaker.class.getClassLoader(), nGetReleaseFunc());
    private final long mNativePtr;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BreakStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface HyphenationFrequency {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface JustificationMode {
    }

    private static native long nComputeLineBreaks(long j, char[] cArr, long j2, int i, float f, int i2, float f2, float[] fArr, float f3, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native float nGetLineAscent(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native int nGetLineBreakOffset(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native int nGetLineCount(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native float nGetLineDescent(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native int nGetLineFlag(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native float nGetLineWidth(long j, int i);

    @CriticalNative
    private static native long nGetReleaseFunc();

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native long nGetReleaseResultFunc();

    @FastNative
    private static native long nInit(int i, int i2, boolean z, int[] iArr);

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mBreakStrategy = 0;
        private int mHyphenationFrequency = 0;
        private int mJustificationMode = 0;
        private int[] mIndents = null;

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

        public Builder setIndents(int[] indents) {
            this.mIndents = indents;
            return this;
        }

        public LineBreaker build() {
            return new LineBreaker(this.mBreakStrategy, this.mHyphenationFrequency, this.mJustificationMode, this.mIndents);
        }
    }

    /* loaded from: classes.dex */
    public static class ParagraphConstraints {
        private float mWidth = 0.0f;
        private float mFirstWidth = 0.0f;
        private int mFirstWidthLineCount = 0;
        private float[] mVariableTabStops = null;
        private float mDefaultTabStop = 0.0f;

        public void setWidth(float width) {
            this.mWidth = width;
        }

        public void setIndent(float firstWidth, int firstWidthLineCount) {
            this.mFirstWidth = firstWidth;
            this.mFirstWidthLineCount = firstWidthLineCount;
        }

        public void setTabStops(float[] tabStops, float defaultTabStop) {
            this.mVariableTabStops = tabStops;
            this.mDefaultTabStop = defaultTabStop;
        }

        public float getWidth() {
            return this.mWidth;
        }

        public float getFirstWidth() {
            return this.mFirstWidth;
        }

        public int getFirstWidthLineCount() {
            return this.mFirstWidthLineCount;
        }

        public float[] getTabStops() {
            return this.mVariableTabStops;
        }

        public float getDefaultTabStop() {
            return this.mDefaultTabStop;
        }
    }

    /* loaded from: classes.dex */
    public static class Result {
        private static final int END_HYPHEN_MASK = 7;
        private static final int HYPHEN_MASK = 255;
        private static final int START_HYPHEN_BITS_SHIFT = 3;
        private static final int START_HYPHEN_MASK = 24;
        private static final int TAB_MASK = 536870912;
        private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(Result.class.getClassLoader(), LineBreaker.nGetReleaseResultFunc());
        private final long mPtr;

        private Result(long ptr) {
            this.mPtr = ptr;
            sRegistry.registerNativeAllocation(this, ptr);
        }

        public int getLineCount() {
            return LineBreaker.nGetLineCount(this.mPtr);
        }

        public int getLineBreakOffset(int lineIndex) {
            return LineBreaker.nGetLineBreakOffset(this.mPtr, lineIndex);
        }

        public float getLineWidth(int lineIndex) {
            return LineBreaker.nGetLineWidth(this.mPtr, lineIndex);
        }

        public float getLineAscent(int lineIndex) {
            return LineBreaker.nGetLineAscent(this.mPtr, lineIndex);
        }

        public float getLineDescent(int lineIndex) {
            return LineBreaker.nGetLineDescent(this.mPtr, lineIndex);
        }

        public boolean hasLineTab(int lineIndex) {
            return (LineBreaker.nGetLineFlag(this.mPtr, lineIndex) & 536870912) != 0;
        }

        public int getStartLineHyphenEdit(int lineIndex) {
            return (LineBreaker.nGetLineFlag(this.mPtr, lineIndex) & 24) >> 3;
        }

        public int getEndLineHyphenEdit(int lineIndex) {
            return LineBreaker.nGetLineFlag(this.mPtr, lineIndex) & 7;
        }
    }

    private LineBreaker(int breakStrategy, int hyphenationFrequency, int justify, int[] indents) {
        long nInit = nInit(breakStrategy, hyphenationFrequency, justify == 1, indents);
        this.mNativePtr = nInit;
        sRegistry.registerNativeAllocation(this, nInit);
    }

    public Result computeLineBreaks(MeasuredText measuredPara, ParagraphConstraints constraints, int lineNumber) {
        return new Result(nComputeLineBreaks(this.mNativePtr, measuredPara.getChars(), measuredPara.getNativePtr(), measuredPara.getChars().length, constraints.mFirstWidth, constraints.mFirstWidthLineCount, constraints.mWidth, constraints.mVariableTabStops, constraints.mDefaultTabStop, lineNumber));
    }
}
