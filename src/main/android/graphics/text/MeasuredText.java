package android.graphics.text;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.CriticalNative;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class MeasuredText {
    private static final String TAG = "MeasuredText";
    private final int mBottom;
    private final char[] mChars;
    private final boolean mComputeHyphenation;
    private final boolean mComputeLayout;
    private final long mNativePtr;
    private final int mTop;

    private static native void nGetBounds(long j, char[] cArr, int i, int i2, Rect rect);

    @CriticalNative
    private static native float nGetCharWidthAt(long j, int i);

    private static native long nGetExtent(long j, char[] cArr, int i, int i2);

    @CriticalNative
    private static native int nGetMemoryUsage(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static native long nGetReleaseFunc();

    @CriticalNative
    private static native float nGetWidth(long j, int i, int i2);

    private MeasuredText(long ptr, char[] chars, boolean computeHyphenation, boolean computeLayout, int top, int bottom) {
        this.mNativePtr = ptr;
        this.mChars = chars;
        this.mComputeHyphenation = computeHyphenation;
        this.mComputeLayout = computeLayout;
        this.mTop = top;
        this.mBottom = bottom;
    }

    public char[] getChars() {
        return this.mChars;
    }

    public float getWidth(int start, int end) {
        Preconditions.checkArgument(start >= 0 && start <= this.mChars.length, "start(%d) must be 0 <= start <= %d", Integer.valueOf(start), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(end >= 0 && end <= this.mChars.length, "end(%d) must be 0 <= end <= %d", Integer.valueOf(end), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(start <= end, "start(%d) is larger than end(%d)", Integer.valueOf(start), Integer.valueOf(end));
        return nGetWidth(this.mNativePtr, start, end);
    }

    public int getMemoryUsage() {
        return nGetMemoryUsage(this.mNativePtr);
    }

    public void getBounds(int start, int end, Rect rect) {
        Preconditions.checkArgument(start >= 0 && start <= this.mChars.length, "start(%d) must be 0 <= start <= %d", Integer.valueOf(start), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(end >= 0 && end <= this.mChars.length, "end(%d) must be 0 <= end <= %d", Integer.valueOf(end), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(start <= end, "start(%d) is larger than end(%d)", Integer.valueOf(start), Integer.valueOf(end));
        Preconditions.checkNotNull(rect);
        nGetBounds(this.mNativePtr, this.mChars, start, end, rect);
    }

    public void getFontMetricsInt(int start, int end, Paint.FontMetricsInt outMetrics) {
        Preconditions.checkArgument(start >= 0 && start <= this.mChars.length, "start(%d) must be 0 <= start <= %d", Integer.valueOf(start), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(end >= 0 && end <= this.mChars.length, "end(%d) must be 0 <= end <= %d", Integer.valueOf(end), Integer.valueOf(this.mChars.length));
        Preconditions.checkArgument(start <= end, "start(%d) is larger than end(%d)", Integer.valueOf(start), Integer.valueOf(end));
        Objects.requireNonNull(outMetrics);
        long packed = nGetExtent(this.mNativePtr, this.mChars, start, end);
        outMetrics.ascent = (int) (packed >> 32);
        outMetrics.descent = (int) ((-1) & packed);
        outMetrics.top = Math.min(outMetrics.ascent, this.mTop);
        outMetrics.bottom = Math.max(outMetrics.descent, this.mBottom);
    }

    public float getCharWidthAt(int offset) {
        Preconditions.checkArgument(offset >= 0 && offset < this.mChars.length, "offset(%d) is larger than text length %d" + offset, Integer.valueOf(this.mChars.length));
        return nGetCharWidthAt(this.mNativePtr, offset);
    }

    public long getNativePtr() {
        return this.mNativePtr;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        public static final int HYPHENATION_MODE_FAST = 2;
        public static final int HYPHENATION_MODE_NONE = 0;
        public static final int HYPHENATION_MODE_NORMAL = 1;
        private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(MeasuredText.class.getClassLoader(), MeasuredText.nGetReleaseFunc());
        private int mBottom;
        private Paint.FontMetricsInt mCachedMetrics;
        private boolean mComputeHyphenation;
        private boolean mComputeLayout;
        private int mCurrentOffset;
        private boolean mFastHyphenation;
        private MeasuredText mHintMt;
        private long mNativePtr;
        private final char[] mText;
        private int mTop;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface HyphenationMode {
        }

        private static native void nAddReplacementRun(long j, long j2, int i, int i2, float f);

        private static native void nAddStyleRun(long j, long j2, int i, int i2, int i3, int i4, boolean z);

        private static native long nBuildMeasuredText(long j, long j2, char[] cArr, boolean z, boolean z2, boolean z3);

        private static native void nFreeBuilder(long j);

        private static native long nInitBuilder();

        public Builder(char[] text) {
            this.mComputeHyphenation = false;
            this.mComputeLayout = true;
            this.mFastHyphenation = false;
            this.mCurrentOffset = 0;
            this.mHintMt = null;
            this.mTop = 0;
            this.mBottom = 0;
            this.mCachedMetrics = new Paint.FontMetricsInt();
            Preconditions.checkNotNull(text);
            this.mText = text;
            this.mNativePtr = nInitBuilder();
        }

        public Builder(MeasuredText text) {
            this.mComputeHyphenation = false;
            this.mComputeLayout = true;
            this.mFastHyphenation = false;
            this.mCurrentOffset = 0;
            this.mHintMt = null;
            this.mTop = 0;
            this.mBottom = 0;
            this.mCachedMetrics = new Paint.FontMetricsInt();
            Preconditions.checkNotNull(text);
            this.mText = text.mChars;
            this.mNativePtr = nInitBuilder();
            if (!text.mComputeLayout) {
                throw new IllegalArgumentException("The input MeasuredText must not be created with setComputeLayout(false).");
            }
            this.mComputeHyphenation = text.mComputeHyphenation;
            this.mComputeLayout = text.mComputeLayout;
            this.mHintMt = text;
        }

        public Builder appendStyleRun(Paint paint, int length, boolean isRtl) {
            return appendStyleRun(paint, null, length, isRtl);
        }

        public Builder appendStyleRun(Paint paint, LineBreakConfig lineBreakConfig, int length, boolean isRtl) {
            Preconditions.checkNotNull(paint);
            Preconditions.checkArgument(length > 0, "length can not be negative");
            int end = this.mCurrentOffset + length;
            Preconditions.checkArgument(end <= this.mText.length, "Style exceeds the text length");
            int lbStyle = lineBreakConfig != null ? lineBreakConfig.getLineBreakStyle() : 0;
            int lbWordStyle = lineBreakConfig != null ? lineBreakConfig.getLineBreakWordStyle() : 0;
            nAddStyleRun(this.mNativePtr, paint.getNativeInstance(), lbStyle, lbWordStyle, this.mCurrentOffset, end, isRtl);
            this.mCurrentOffset = end;
            paint.getFontMetricsInt(this.mCachedMetrics);
            this.mTop = Math.min(this.mTop, this.mCachedMetrics.top);
            this.mBottom = Math.max(this.mBottom, this.mCachedMetrics.bottom);
            return this;
        }

        public Builder appendReplacementRun(Paint paint, int length, float width) {
            Preconditions.checkArgument(length > 0, "length can not be negative");
            int end = this.mCurrentOffset + length;
            Preconditions.checkArgument(end <= this.mText.length, "Replacement exceeds the text length");
            nAddReplacementRun(this.mNativePtr, paint.getNativeInstance(), this.mCurrentOffset, end, width);
            this.mCurrentOffset = end;
            return this;
        }

        @Deprecated
        public Builder setComputeHyphenation(boolean computeHyphenation) {
            setComputeHyphenation(computeHyphenation ? 1 : 0);
            return this;
        }

        public Builder setComputeHyphenation(int mode) {
            switch (mode) {
                case 0:
                    this.mComputeHyphenation = false;
                    this.mFastHyphenation = false;
                    break;
                case 1:
                    this.mComputeHyphenation = true;
                    this.mFastHyphenation = false;
                    break;
                case 2:
                    this.mComputeHyphenation = true;
                    this.mFastHyphenation = true;
                    break;
                default:
                    Log.m110e(MeasuredText.TAG, "Unknown hyphenation mode: " + mode);
                    this.mComputeHyphenation = false;
                    this.mFastHyphenation = false;
                    break;
            }
            return this;
        }

        public Builder setComputeLayout(boolean computeLayout) {
            this.mComputeLayout = computeLayout;
            return this;
        }

        public MeasuredText build() {
            ensureNativePtrNoReuse();
            if (this.mCurrentOffset != this.mText.length) {
                throw new IllegalStateException("Style info has not been provided for all text.");
            }
            MeasuredText measuredText = this.mHintMt;
            if (measuredText != null && measuredText.mComputeHyphenation != this.mComputeHyphenation) {
                throw new IllegalArgumentException("The hyphenation configuration is different from given hint MeasuredText");
            }
            try {
                MeasuredText measuredText2 = this.mHintMt;
                long hintPtr = measuredText2 == null ? 0L : measuredText2.getNativePtr();
                long ptr = nBuildMeasuredText(this.mNativePtr, hintPtr, this.mText, this.mComputeHyphenation, this.mComputeLayout, this.mFastHyphenation);
                MeasuredText res = new MeasuredText(ptr, this.mText, this.mComputeHyphenation, this.mComputeLayout, this.mTop, this.mBottom);
                sRegistry.registerNativeAllocation(res, ptr);
                return res;
            } finally {
                nFreeBuilder(this.mNativePtr);
                this.mNativePtr = 0L;
            }
        }

        private void ensureNativePtrNoReuse() {
            if (this.mNativePtr == 0) {
                throw new IllegalStateException("Builder can not be reused.");
            }
        }
    }
}
