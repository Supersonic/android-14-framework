package android.view.inputmethod;

import android.graphics.FontListParser;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.Layout;
import android.text.SegmentFinder;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextBoundsInfo implements Parcelable {
    private static final int BIDI_LEVEL_MASK = 66584576;
    private static final int BIDI_LEVEL_SHIFT = 19;
    public static final Parcelable.Creator<TextBoundsInfo> CREATOR = new Parcelable.Creator<TextBoundsInfo>() { // from class: android.view.inputmethod.TextBoundsInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextBoundsInfo createFromParcel(Parcel source) {
            return new TextBoundsInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextBoundsInfo[] newArray(int size) {
            return new TextBoundsInfo[size];
        }
    };
    public static final int FLAG_CHARACTER_LINEFEED = 2;
    public static final int FLAG_CHARACTER_PUNCTUATION = 4;
    public static final int FLAG_CHARACTER_WHITESPACE = 1;
    private static final int FLAG_GRAPHEME_SEGMENT_END = 67108864;
    private static final int FLAG_GRAPHEME_SEGMENT_START = 134217728;
    public static final int FLAG_LINE_IS_RTL = 8;
    private static final int FLAG_LINE_SEGMENT_END = 1073741824;
    private static final int FLAG_LINE_SEGMENT_START = Integer.MIN_VALUE;
    private static final int FLAG_WORD_SEGMENT_END = 268435456;
    private static final int FLAG_WORD_SEGMENT_START = 536870912;
    private static final int KNOWN_CHARACTER_FLAGS = 15;
    private static final String TEXT_BOUNDS_INFO_KEY = "android.view.inputmethod.TextBoundsInfo";
    private final float[] mCharacterBounds;
    private final int mEnd;
    private final SegmentFinder mGraphemeSegmentFinder;
    private final int[] mInternalCharacterFlags;
    private final SegmentFinder mLineSegmentFinder;
    private final float[] mMatrixValues;
    private final int mStart;
    private final SegmentFinder mWordSegmentFinder;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface CharacterFlags {
    }

    public void getMatrix(Matrix matrix) {
        Objects.requireNonNull(matrix);
        matrix.setValues(this.mMatrixValues);
    }

    public int getStartIndex() {
        return this.mStart;
    }

    public int getEndIndex() {
        return this.mEnd;
    }

    public void getCharacterBounds(int index, RectF bounds) {
        int i = this.mStart;
        if (index < i || index >= this.mEnd) {
            throw new IndexOutOfBoundsException("Index is out of the bounds of [" + this.mStart + ", " + this.mEnd + ").");
        }
        int offset = (index - i) * 4;
        float[] fArr = this.mCharacterBounds;
        bounds.set(fArr[offset], fArr[offset + 1], fArr[offset + 2], fArr[offset + 3]);
    }

    public int getCharacterFlags(int index) {
        int i = this.mStart;
        if (index < i || index >= this.mEnd) {
            throw new IndexOutOfBoundsException("Index is out of the bounds of [" + this.mStart + ", " + this.mEnd + ").");
        }
        int offset = index - i;
        return this.mInternalCharacterFlags[offset] & 15;
    }

    public int getCharacterBidiLevel(int index) {
        int i = this.mStart;
        if (index < i || index >= this.mEnd) {
            throw new IndexOutOfBoundsException("Index is out of the bounds of [" + this.mStart + ", " + this.mEnd + ").");
        }
        int offset = index - i;
        return (this.mInternalCharacterFlags[offset] & BIDI_LEVEL_MASK) >> 19;
    }

    public SegmentFinder getWordSegmentFinder() {
        return this.mWordSegmentFinder;
    }

    public SegmentFinder getGraphemeSegmentFinder() {
        return this.mGraphemeSegmentFinder;
    }

    public SegmentFinder getLineSegmentFinder() {
        return this.mLineSegmentFinder;
    }

    public int getOffsetForPosition(float x, float y) {
        int lineLimit;
        int[] lineRange = new int[2];
        RectF lineBounds = new RectF();
        getLineInfo(y, lineRange, lineBounds);
        if (lineRange[0] == -1 || lineRange[1] == -1) {
            return -1;
        }
        int lineStart = lineRange[0];
        int lineEnd = lineRange[1];
        boolean lineEndsWithLinefeed = (2 & getCharacterFlags(lineEnd + (-1))) != 0;
        if (lineEndsWithLinefeed) {
            lineLimit = lineEnd;
        } else {
            int lineLimit2 = lineEnd + 1;
            lineLimit = lineLimit2;
        }
        int graphemeStart = this.mGraphemeSegmentFinder.nextEndBoundary(lineStart);
        if (graphemeStart == -1) {
            return -1;
        }
        int graphemeStart2 = this.mGraphemeSegmentFinder.previousStartBoundary(graphemeStart);
        int target = -1;
        float minDistance = Float.MAX_VALUE;
        while (graphemeStart2 != -1 && graphemeStart2 < lineLimit) {
            if (graphemeStart2 >= lineStart) {
                float cursorPosition = getCursorHorizontalPosition(graphemeStart2, lineStart, lineEnd, lineBounds.left, lineBounds.right);
                float distance = Math.abs(cursorPosition - x);
                if (distance < minDistance) {
                    minDistance = distance;
                    target = graphemeStart2;
                }
            }
            graphemeStart2 = this.mGraphemeSegmentFinder.nextStartBoundary(graphemeStart2);
        }
        return target;
    }

    private boolean primaryIsTrailingPrevious(int offset, int lineStart, int lineEnd) {
        int bidiLevel;
        int bidiLevelBefore;
        if (offset < lineEnd) {
            bidiLevel = getCharacterBidiLevel(offset);
        } else {
            int bidiLevel2 = offset - 1;
            boolean lineIsRtl = (getCharacterFlags(bidiLevel2) & 8) == 8;
            bidiLevel = lineIsRtl ? 1 : 0;
        }
        if (offset <= lineStart) {
            boolean lineIsRtl2 = (getCharacterFlags(offset) & 8) == 8;
            bidiLevelBefore = lineIsRtl2 ? 1 : 0;
        } else {
            bidiLevelBefore = getCharacterBidiLevel(offset - 1);
        }
        return bidiLevelBefore < bidiLevel;
    }

    private float getCursorHorizontalPosition(int index, int lineStart, int lineEnd, float lineLeft, float lineRight) {
        int targetIndex;
        boolean isStart;
        Preconditions.checkArgumentInRange(index, lineStart, lineEnd, FontListParser.ATTR_INDEX);
        boolean lineIsRtl = (getCharacterFlags(lineStart) & 8) != 0;
        boolean isPrimaryIsTrailingPrevious = primaryIsTrailingPrevious(index, lineStart, lineEnd);
        if (isPrimaryIsTrailingPrevious) {
            if (index <= lineStart) {
                return lineIsRtl ? lineRight : lineLeft;
            }
            targetIndex = index - 1;
            isStart = false;
        } else if (index >= lineEnd) {
            return lineIsRtl ? lineLeft : lineRight;
        } else {
            targetIndex = index;
            isStart = true;
        }
        boolean isRtl = (getCharacterBidiLevel(targetIndex) & 1) != 0;
        int offset = targetIndex - this.mStart;
        float[] fArr = this.mCharacterBounds;
        int i = offset * 4;
        return isRtl != isStart ? fArr[i] : fArr[i + 2];
    }

    private void getBoundsForRange(int start, int end, RectF rectF) {
        Preconditions.checkArgumentInRange(start, this.mStart, this.mEnd - 1, "start");
        Preconditions.checkArgumentInRange(end, start, this.mEnd, "end");
        if (end <= start) {
            rectF.setEmpty();
            return;
        }
        rectF.left = Float.MAX_VALUE;
        rectF.top = Float.MAX_VALUE;
        rectF.right = Float.MIN_VALUE;
        rectF.bottom = Float.MIN_VALUE;
        for (int index = start; index < end; index++) {
            int offset = index - this.mStart;
            rectF.left = Math.min(rectF.left, this.mCharacterBounds[offset * 4]);
            rectF.top = Math.min(rectF.top, this.mCharacterBounds[(offset * 4) + 1]);
            rectF.right = Math.max(rectF.right, this.mCharacterBounds[(offset * 4) + 2]);
            rectF.bottom = Math.max(rectF.bottom, this.mCharacterBounds[(offset * 4) + 3]);
        }
    }

    private void getLineInfo(float y, int[] characterRange, RectF bounds) {
        characterRange[0] = -1;
        characterRange[1] = -1;
        int currentLineEnd = this.mLineSegmentFinder.nextEndBoundary(this.mStart);
        if (currentLineEnd == -1) {
            return;
        }
        int currentLineStart = this.mLineSegmentFinder.previousStartBoundary(currentLineEnd);
        float top = Float.MAX_VALUE;
        float bottom = Float.MIN_VALUE;
        float minDistance = Float.MAX_VALUE;
        RectF currentLineBounds = new RectF();
        while (currentLineStart != -1 && currentLineStart < this.mEnd) {
            int lineStartInRange = Math.max(this.mStart, currentLineStart);
            int lineEndInRange = Math.min(this.mEnd, currentLineEnd);
            getBoundsForRange(lineStartInRange, lineEndInRange, currentLineBounds);
            top = Math.min(currentLineBounds.top, top);
            bottom = Math.max(currentLineBounds.bottom, bottom);
            float distance = verticalDistance(currentLineBounds, y);
            if (distance == 0.0f) {
                characterRange[0] = currentLineStart;
                characterRange[1] = currentLineEnd;
                if (bounds != null) {
                    bounds.set(currentLineBounds);
                    return;
                }
                return;
            }
            if (distance < minDistance) {
                minDistance = distance;
                characterRange[0] = currentLineStart;
                characterRange[1] = currentLineEnd;
                if (bounds != null) {
                    bounds.set(currentLineBounds);
                }
            }
            if (y < bounds.top) {
                break;
            }
            currentLineStart = this.mLineSegmentFinder.nextStartBoundary(currentLineStart);
            currentLineEnd = this.mLineSegmentFinder.nextEndBoundary(currentLineEnd);
        }
        if (y < top || y > bottom) {
            characterRange[0] = -1;
            characterRange[1] = -1;
            if (bounds != null) {
                bounds.setEmpty();
            }
        }
    }

    public int[] getRangeForRect(RectF area, SegmentFinder segmentFinder, Layout.TextInclusionStrategy inclusionStrategy) {
        int lineStart;
        int lineEnd = this.mLineSegmentFinder.nextEndBoundary(this.mStart);
        if (lineEnd == -1) {
            return null;
        }
        int lineStart2 = this.mLineSegmentFinder.previousStartBoundary(lineEnd);
        int start = -1;
        while (lineStart2 != -1 && start == -1) {
            start = getStartForRectWithinLine(lineStart2, lineEnd, area, segmentFinder, inclusionStrategy);
            lineStart2 = this.mLineSegmentFinder.nextStartBoundary(lineStart2);
            lineEnd = this.mLineSegmentFinder.nextEndBoundary(lineEnd);
        }
        if (start == -1 || (lineStart = this.mLineSegmentFinder.previousStartBoundary(this.mEnd)) == -1) {
            return null;
        }
        int lineEnd2 = this.mLineSegmentFinder.nextEndBoundary(lineStart);
        int end = -1;
        while (lineEnd2 > start && end == -1) {
            end = getEndForRectWithinLine(lineStart, lineEnd2, area, segmentFinder, inclusionStrategy);
            lineStart = this.mLineSegmentFinder.previousStartBoundary(lineStart);
            lineEnd2 = this.mLineSegmentFinder.previousEndBoundary(lineEnd2);
        }
        int start2 = segmentFinder.previousStartBoundary(start + 1);
        int start3 = end - 1;
        int end2 = segmentFinder.nextEndBoundary(start3);
        return new int[]{start2, end2};
    }

    private int getStartForRectWithinLine(int lineStart, int lineEnd, RectF area, SegmentFinder segmentFinder, Layout.TextInclusionStrategy inclusionStrategy) {
        if (lineStart >= lineEnd) {
            return -1;
        }
        int runStart = lineStart;
        int runLevel = -1;
        for (int index = lineStart; index < lineEnd; index++) {
            int level = getCharacterBidiLevel(index);
            if (level != runLevel) {
                int start = getStartForRectWithinRun(runStart, index, area, segmentFinder, inclusionStrategy);
                if (start != -1) {
                    return start;
                }
                int runStart2 = index;
                runStart = runStart2;
                runLevel = level;
            }
        }
        return getStartForRectWithinRun(runStart, lineEnd, area, segmentFinder, inclusionStrategy);
    }

    private int getStartForRectWithinRun(int runStart, int runEnd, RectF area, SegmentFinder segmentFinder, Layout.TextInclusionStrategy inclusionStrategy) {
        int segmentEndOffset;
        if (runStart >= runEnd || (segmentEndOffset = segmentFinder.nextEndBoundary(runStart)) == -1) {
            return -1;
        }
        int segmentStartOffset = segmentFinder.previousStartBoundary(segmentEndOffset);
        RectF segmentBounds = new RectF();
        while (segmentStartOffset != -1 && segmentStartOffset < runEnd) {
            int start = Math.max(runStart, segmentStartOffset);
            int end = Math.min(runEnd, segmentEndOffset);
            getBoundsForRange(start, end, segmentBounds);
            if (inclusionStrategy.isSegmentInside(segmentBounds, area)) {
                return start;
            }
            segmentStartOffset = segmentFinder.nextStartBoundary(segmentStartOffset);
            segmentEndOffset = segmentFinder.nextEndBoundary(segmentEndOffset);
        }
        return -1;
    }

    private int getEndForRectWithinLine(int lineStart, int lineEnd, RectF area, SegmentFinder segmentFinder, Layout.TextInclusionStrategy inclusionStrategy) {
        if (lineStart >= lineEnd) {
            return -1;
        }
        int lineStart2 = Math.max(lineStart, this.mStart);
        int lineEnd2 = Math.min(lineEnd, this.mEnd);
        int runEnd = lineEnd2;
        int runLevel = -1;
        for (int index = lineEnd2 - 1; index >= lineStart2; index--) {
            int level = getCharacterBidiLevel(index);
            if (level != runLevel) {
                int end = getEndForRectWithinRun(index + 1, runEnd, area, segmentFinder, inclusionStrategy);
                if (end != -1) {
                    return end;
                }
                int runEnd2 = index + 1;
                runEnd = runEnd2;
                runLevel = level;
            }
        }
        return getEndForRectWithinRun(lineStart2, runEnd, area, segmentFinder, inclusionStrategy);
    }

    private int getEndForRectWithinRun(int runStart, int runEnd, RectF area, SegmentFinder segmentFinder, Layout.TextInclusionStrategy inclusionStrategy) {
        int segmentStart;
        if (runStart >= runEnd || (segmentStart = segmentFinder.previousStartBoundary(runEnd)) == -1) {
            return -1;
        }
        int segmentEnd = segmentFinder.nextEndBoundary(segmentStart);
        RectF segmentBounds = new RectF();
        while (segmentEnd != -1 && segmentEnd > runStart) {
            int start = Math.max(runStart, segmentStart);
            int end = Math.min(runEnd, segmentEnd);
            getBoundsForRange(start, end, segmentBounds);
            if (inclusionStrategy.isSegmentInside(segmentBounds, area)) {
                return end;
            }
            segmentStart = segmentFinder.previousStartBoundary(segmentStart);
            segmentEnd = segmentFinder.previousEndBoundary(segmentEnd);
        }
        return -1;
    }

    private static float verticalDistance(RectF rectF, float y) {
        if (rectF.top <= y && y < rectF.bottom) {
            return 0.0f;
        }
        if (y < rectF.top) {
            return rectF.top - y;
        }
        return y - rectF.bottom;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStart);
        dest.writeInt(this.mEnd);
        dest.writeFloatArray(this.mMatrixValues);
        dest.writeFloatArray(this.mCharacterBounds);
        int[] encodedFlags = Arrays.copyOf(this.mInternalCharacterFlags, (this.mEnd - this.mStart) + 1);
        encodeSegmentFinder(encodedFlags, 134217728, 67108864, this.mStart, this.mEnd, this.mGraphemeSegmentFinder);
        encodeSegmentFinder(encodedFlags, 536870912, 268435456, this.mStart, this.mEnd, this.mWordSegmentFinder);
        encodeSegmentFinder(encodedFlags, Integer.MIN_VALUE, 1073741824, this.mStart, this.mEnd, this.mLineSegmentFinder);
        dest.writeIntArray(encodedFlags);
    }

    private TextBoundsInfo(Parcel source) {
        int readInt = source.readInt();
        this.mStart = readInt;
        int readInt2 = source.readInt();
        this.mEnd = readInt2;
        this.mMatrixValues = (float[]) Objects.requireNonNull(source.createFloatArray());
        this.mCharacterBounds = (float[]) Objects.requireNonNull(source.createFloatArray());
        int[] encodedFlags = (int[]) Objects.requireNonNull(source.createIntArray());
        this.mGraphemeSegmentFinder = decodeSegmentFinder(encodedFlags, 134217728, 67108864, readInt, readInt2);
        this.mWordSegmentFinder = decodeSegmentFinder(encodedFlags, 536870912, 268435456, readInt, readInt2);
        this.mLineSegmentFinder = decodeSegmentFinder(encodedFlags, Integer.MIN_VALUE, 1073741824, readInt, readInt2);
        int length = readInt2 - readInt;
        this.mInternalCharacterFlags = new int[length];
        for (int i = 0; i < length; i++) {
            this.mInternalCharacterFlags[i] = encodedFlags[i] & 66584591;
        }
    }

    private TextBoundsInfo(Builder builder) {
        int i = builder.mStart;
        this.mStart = i;
        int i2 = builder.mEnd;
        this.mEnd = i2;
        this.mMatrixValues = Arrays.copyOf(builder.mMatrixValues, 9);
        int length = i2 - i;
        this.mCharacterBounds = Arrays.copyOf(builder.mCharacterBounds, length * 4);
        this.mInternalCharacterFlags = new int[length];
        for (int index = 0; index < length; index++) {
            this.mInternalCharacterFlags[index] = builder.mCharacterFlags[index] | (builder.mCharacterBidiLevels[index] << 19);
        }
        this.mGraphemeSegmentFinder = builder.mGraphemeSegmentFinder;
        this.mWordSegmentFinder = builder.mWordSegmentFinder;
        this.mLineSegmentFinder = builder.mLineSegmentFinder;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TEXT_BOUNDS_INFO_KEY, this);
        return bundle;
    }

    public static TextBoundsInfo createFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return (TextBoundsInfo) bundle.getParcelable(TEXT_BOUNDS_INFO_KEY, TextBoundsInfo.class);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private int[] mCharacterBidiLevels;
        private float[] mCharacterBounds;
        private int[] mCharacterFlags;
        private SegmentFinder mGraphemeSegmentFinder;
        private SegmentFinder mLineSegmentFinder;
        private boolean mMatrixInitialized;
        private SegmentFinder mWordSegmentFinder;
        private final float[] mMatrixValues = new float[9];
        private int mStart = -1;
        private int mEnd = -1;

        public Builder(int start, int end) {
            setStartAndEnd(start, end);
        }

        public Builder clear() {
            this.mMatrixInitialized = false;
            this.mStart = -1;
            this.mEnd = -1;
            this.mCharacterBounds = null;
            this.mCharacterFlags = null;
            this.mLineSegmentFinder = null;
            this.mWordSegmentFinder = null;
            this.mGraphemeSegmentFinder = null;
            return this;
        }

        public Builder setMatrix(Matrix matrix) {
            ((Matrix) Objects.requireNonNull(matrix)).getValues(this.mMatrixValues);
            this.mMatrixInitialized = true;
            return this;
        }

        public Builder setStartAndEnd(int start, int end) {
            Preconditions.checkArgument(start >= 0);
            Preconditions.checkArgumentInRange(start, 0, end, "start");
            this.mStart = start;
            this.mEnd = end;
            return this;
        }

        public Builder setCharacterBounds(float[] characterBounds) {
            this.mCharacterBounds = (float[]) Objects.requireNonNull(characterBounds);
            return this;
        }

        public Builder setCharacterFlags(int[] characterFlags) {
            Objects.requireNonNull(characterFlags);
            for (int characterFlag : characterFlags) {
                if ((characterFlag & (-16)) != 0) {
                    throw new IllegalArgumentException("characterFlags contains invalid flags.");
                }
            }
            this.mCharacterFlags = characterFlags;
            return this;
        }

        public Builder setCharacterBidiLevel(int[] characterBidiLevels) {
            Objects.requireNonNull(characterBidiLevels);
            for (int index = 0; index < characterBidiLevels.length; index++) {
                Preconditions.checkArgumentInRange(characterBidiLevels[index], 0, 125, "bidiLevels[" + index + NavigationBarInflaterView.SIZE_MOD_END);
            }
            this.mCharacterBidiLevels = characterBidiLevels;
            return this;
        }

        public Builder setGraphemeSegmentFinder(SegmentFinder graphemeSegmentFinder) {
            this.mGraphemeSegmentFinder = (SegmentFinder) Objects.requireNonNull(graphemeSegmentFinder);
            return this;
        }

        public Builder setWordSegmentFinder(SegmentFinder wordSegmentFinder) {
            this.mWordSegmentFinder = (SegmentFinder) Objects.requireNonNull(wordSegmentFinder);
            return this;
        }

        public Builder setLineSegmentFinder(SegmentFinder lineSegmentFinder) {
            this.mLineSegmentFinder = (SegmentFinder) Objects.requireNonNull(lineSegmentFinder);
            return this;
        }

        public TextBoundsInfo build() {
            int i;
            int i2 = this.mStart;
            if (i2 < 0 || (i = this.mEnd) < 0) {
                throw new IllegalStateException("Start and end must be set.");
            }
            if (!this.mMatrixInitialized) {
                throw new IllegalStateException("Matrix must be set.");
            }
            float[] fArr = this.mCharacterBounds;
            if (fArr == null) {
                throw new IllegalStateException("CharacterBounds must be set.");
            }
            int[] iArr = this.mCharacterFlags;
            if (iArr == null) {
                throw new IllegalStateException("CharacterFlags must be set.");
            }
            int[] iArr2 = this.mCharacterBidiLevels;
            if (iArr2 == null) {
                throw new IllegalStateException("CharacterBidiLevel must be set.");
            }
            if (fArr.length != (i - i2) * 4) {
                throw new IllegalStateException("The length of characterBounds doesn't match the length of the given start and end. Expected length: " + ((this.mEnd - this.mStart) * 4) + " characterBounds length: " + this.mCharacterBounds.length);
            }
            if (iArr.length != i - i2) {
                throw new IllegalStateException("The length of characterFlags doesn't match the length of the given start and end. Expected length: " + (this.mEnd - this.mStart) + " characterFlags length: " + this.mCharacterFlags.length);
            }
            if (iArr2.length != i - i2) {
                throw new IllegalStateException("The length of characterBidiLevels doesn't match the length of the given start and end. Expected length: " + (this.mEnd - this.mStart) + " characterFlags length: " + this.mCharacterBidiLevels.length);
            }
            if (this.mGraphemeSegmentFinder == null) {
                throw new IllegalStateException("GraphemeSegmentFinder must be set.");
            }
            if (this.mWordSegmentFinder == null) {
                throw new IllegalStateException("WordSegmentFinder must be set.");
            }
            SegmentFinder segmentFinder = this.mLineSegmentFinder;
            if (segmentFinder == null) {
                throw new IllegalStateException("LineSegmentFinder must be set.");
            }
            if (!TextBoundsInfo.isLineDirectionFlagConsistent(iArr, segmentFinder, i2, i)) {
                throw new IllegalStateException("characters in the same line must have the same FLAG_LINE_IS_RTL flag value.");
            }
            return new TextBoundsInfo(this);
        }
    }

    private static void encodeSegmentFinder(int[] flags, int segmentStartFlag, int segmentEndFlag, int start, int end, SegmentFinder segmentFinder) {
        if ((end - start) + 1 != flags.length) {
            throw new IllegalStateException("The given flags array must have the same length as the given range. flags length: " + flags.length + " range: [" + start + ", " + end + NavigationBarInflaterView.SIZE_MOD_END);
        }
        int segmentEnd = segmentFinder.nextEndBoundary(start);
        if (segmentEnd == -1) {
            return;
        }
        int segmentStart = segmentFinder.previousStartBoundary(segmentEnd);
        while (segmentEnd != -1 && segmentEnd <= end) {
            if (segmentStart >= start) {
                int i = segmentStart - start;
                flags[i] = flags[i] | segmentStartFlag;
                int i2 = segmentEnd - start;
                flags[i2] = flags[i2] | segmentEndFlag;
            }
            segmentStart = segmentFinder.nextStartBoundary(segmentStart);
            segmentEnd = segmentFinder.nextEndBoundary(segmentEnd);
        }
    }

    private static SegmentFinder decodeSegmentFinder(int[] flags, int segmentStartFlag, int segmentEndFlag, int start, int end) {
        if ((end - start) + 1 != flags.length) {
            throw new IllegalStateException("The given flags array must have the same length as the given range. flags length: " + flags.length + " range: [" + start + ", " + end + NavigationBarInflaterView.SIZE_MOD_END);
        }
        int[] breaks = ArrayUtils.newUnpaddedIntArray(10);
        int count = 0;
        for (int offset = 0; offset < flags.length; offset++) {
            if ((flags[offset] & segmentStartFlag) == segmentStartFlag) {
                breaks = GrowingArrayUtils.append(breaks, count, start + offset);
                count++;
            }
            int count2 = flags[offset];
            if ((count2 & segmentEndFlag) == segmentEndFlag) {
                breaks = GrowingArrayUtils.append(breaks, count, start + offset);
                count++;
            }
        }
        return new SegmentFinder.PrescribedSegmentFinder(Arrays.copyOf(breaks, count));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isLineDirectionFlagConsistent(int[] characterFlags, SegmentFinder lineSegmentFinder, int start, int end) {
        int segmentEnd = lineSegmentFinder.nextEndBoundary(start);
        if (segmentEnd == -1) {
            return true;
        }
        int segmentStart = lineSegmentFinder.previousStartBoundary(segmentEnd);
        while (segmentStart != -1 && segmentStart < end) {
            int lineStart = Math.max(segmentStart, start);
            int lineEnd = Math.min(segmentEnd, end);
            boolean lineIsRtl = (characterFlags[lineStart - start] & 8) != 0;
            for (int index = lineStart + 1; index < lineEnd; index++) {
                int flags = characterFlags[index - start];
                boolean characterLineIsRtl = (flags & 8) != 0;
                if (characterLineIsRtl != lineIsRtl) {
                    return false;
                }
            }
            segmentStart = lineSegmentFinder.nextStartBoundary(segmentStart);
            segmentEnd = lineSegmentFinder.nextEndBoundary(segmentEnd);
        }
        return true;
    }
}
