package android.text;

import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public abstract class SegmentFinder {
    public static final int DONE = -1;

    public abstract int nextEndBoundary(int i);

    public abstract int nextStartBoundary(int i);

    public abstract int previousEndBoundary(int i);

    public abstract int previousStartBoundary(int i);

    /* loaded from: classes3.dex */
    public static class PrescribedSegmentFinder extends SegmentFinder {
        private final int[] mSegments;

        public PrescribedSegmentFinder(int[] segments) {
            checkSegmentsValid(segments);
            this.mSegments = segments;
        }

        @Override // android.text.SegmentFinder
        public int previousStartBoundary(int offset) {
            return findPrevious(offset, true);
        }

        @Override // android.text.SegmentFinder
        public int previousEndBoundary(int offset) {
            return findPrevious(offset, false);
        }

        @Override // android.text.SegmentFinder
        public int nextStartBoundary(int offset) {
            return findNext(offset, true);
        }

        @Override // android.text.SegmentFinder
        public int nextEndBoundary(int offset) {
            return findNext(offset, false);
        }

        private int findNext(int offset, boolean isStart) {
            int index;
            if (offset < 0) {
                return -1;
            }
            int[] iArr = this.mSegments;
            if (iArr.length < 1 || offset > iArr[iArr.length - 1]) {
                return -1;
            }
            int i = iArr[0];
            if (offset < i) {
                return isStart ? i : iArr[1];
            }
            int index2 = Arrays.binarySearch(iArr, offset);
            if (index2 >= 0) {
                int i2 = index2 + 1;
                int[] iArr2 = this.mSegments;
                if (i2 < iArr2.length && iArr2[index2 + 1] == offset) {
                    index2++;
                }
                index = index2 + 1;
            } else {
                index = -(index2 + 1);
            }
            int[] iArr3 = this.mSegments;
            if (index >= iArr3.length) {
                return -1;
            }
            boolean indexIsStart = index % 2 == 0;
            if (isStart != indexIsStart) {
                if (index + 1 < iArr3.length) {
                    return iArr3[index + 1];
                }
                return -1;
            }
            return iArr3[index];
        }

        private int findPrevious(int offset, boolean isStart) {
            int index;
            int[] iArr = this.mSegments;
            if (iArr.length < 1 || offset < iArr[0]) {
                return -1;
            }
            if (offset > iArr[iArr.length - 1]) {
                int length = iArr.length;
                return isStart ? iArr[length - 2] : iArr[length - 1];
            }
            int index2 = Arrays.binarySearch(iArr, offset);
            if (index2 >= 0) {
                if (index2 > 0 && this.mSegments[index2 - 1] == offset) {
                    index2--;
                }
                index = index2 - 1;
            } else {
                index = (-(index2 + 1)) - 1;
            }
            if (index < 0) {
                return -1;
            }
            boolean indexIsStart = index % 2 == 0;
            if (isStart != indexIsStart) {
                if (index > 0) {
                    return this.mSegments[index - 1];
                }
                return -1;
            }
            return this.mSegments[index];
        }

        private static void checkSegmentsValid(int[] segments) {
            Objects.requireNonNull(segments);
            Preconditions.checkArgument(segments.length % 2 == 0, "the length of segments must be even");
            if (segments.length == 0) {
                return;
            }
            int lastSegmentEnd = Integer.MIN_VALUE;
            for (int index = 0; index < segments.length; index += 2) {
                if (segments[index] < lastSegmentEnd) {
                    throw new IllegalArgumentException("segments can't overlap");
                }
                if (segments[index] >= segments[index + 1]) {
                    throw new IllegalArgumentException("the segment range can't be empty");
                }
                lastSegmentEnd = segments[index + 1];
            }
        }
    }
}
