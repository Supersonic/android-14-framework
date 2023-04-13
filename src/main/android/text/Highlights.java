package android.text;

import android.graphics.Paint;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public class Highlights {
    private final List<Pair<Paint, int[]>> mHighlights;

    private Highlights(List<Pair<Paint, int[]>> highlights) {
        this.mHighlights = highlights;
    }

    public int getSize() {
        return this.mHighlights.size();
    }

    public Paint getPaint(int index) {
        return this.mHighlights.get(index).first;
    }

    public int[] getRanges(int index) {
        return this.mHighlights.get(index).second;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final List<Pair<Paint, int[]>> mHighlights = new ArrayList();

        public Builder addRange(Paint paint, int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("start must not be larger than end: " + start + ", " + end);
            }
            Objects.requireNonNull(paint);
            int[] range = {start, end};
            this.mHighlights.add(new Pair<>(paint, range));
            return this;
        }

        public Builder addRanges(Paint paint, int... ranges) {
            if (ranges.length % 2 == 1) {
                throw new IllegalArgumentException("Flatten ranges must have even numbered elements");
            }
            for (int j = 0; j < ranges.length / 2; j++) {
                int start = ranges[j * 2];
                int end = ranges[(j * 2) + 1];
                if (start > end) {
                    throw new IllegalArgumentException("Reverse range found in the flatten range: " + Arrays.toString(ranges));
                }
            }
            Objects.requireNonNull(paint);
            this.mHighlights.add(new Pair<>(paint, ranges));
            return this;
        }

        public Highlights build() {
            return new Highlights(this.mHighlights);
        }
    }
}
