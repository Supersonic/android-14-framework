package android.text;

import java.lang.reflect.Array;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class SpanSet<E> {
    private final Class<? extends E> classType;
    int numberOfSpans = 0;
    int[] spanEnds;
    int[] spanFlags;
    int[] spanStarts;
    E[] spans;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpanSet(Class<? extends E> type) {
        this.classType = type;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void init(Spanned spanned, int start, int limit) {
        E[] eArr;
        Object[] spans = spanned.getSpans(start, limit, this.classType);
        int length = spans.length;
        if (length > 0 && ((eArr = this.spans) == null || eArr.length < length)) {
            this.spans = (E[]) ((Object[]) Array.newInstance(this.classType, length));
            this.spanStarts = new int[length];
            this.spanEnds = new int[length];
            this.spanFlags = new int[length];
        }
        int prevNumberOfSpans = this.numberOfSpans;
        this.numberOfSpans = 0;
        for (Object obj : spans) {
            int spanStart = spanned.getSpanStart(obj);
            int spanEnd = spanned.getSpanEnd(obj);
            if (spanStart != spanEnd) {
                int spanFlag = spanned.getSpanFlags(obj);
                int i = this.numberOfSpans;
                this.spans[i] = obj;
                this.spanStarts[i] = spanStart;
                this.spanEnds[i] = spanEnd;
                this.spanFlags[i] = spanFlag;
                this.numberOfSpans = i + 1;
            }
        }
        int i2 = this.numberOfSpans;
        if (i2 < prevNumberOfSpans) {
            Arrays.fill(this.spans, i2, prevNumberOfSpans, (Object) null);
        }
    }

    public boolean hasSpansIntersecting(int start, int end) {
        for (int i = 0; i < this.numberOfSpans; i++) {
            if (this.spanStarts[i] < end && this.spanEnds[i] > start) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNextTransition(int start, int limit) {
        for (int i = 0; i < this.numberOfSpans; i++) {
            int spanStart = this.spanStarts[i];
            int spanEnd = this.spanEnds[i];
            if (spanStart > start && spanStart < limit) {
                limit = spanStart;
            }
            if (spanEnd > start && spanEnd < limit) {
                limit = spanEnd;
            }
        }
        return limit;
    }

    public void recycle() {
        E[] eArr = this.spans;
        if (eArr != null) {
            Arrays.fill(eArr, 0, this.numberOfSpans, (Object) null);
        }
    }
}
