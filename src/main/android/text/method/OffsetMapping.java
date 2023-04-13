package android.text.method;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public interface OffsetMapping {
    public static final int MAP_STRATEGY_CHARACTER = 0;
    public static final int MAP_STRATEGY_CURSOR = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MapStrategy {
    }

    int originalToTransformed(int i, int i2);

    void originalToTransformed(TextUpdate textUpdate);

    int transformedToOriginal(int i, int i2);

    /* loaded from: classes3.dex */
    public static class TextUpdate {
        public int after;
        public int before;
        public int where;

        public TextUpdate(int where, int before, int after) {
            this.where = where;
            this.before = before;
            this.after = after;
        }
    }
}
