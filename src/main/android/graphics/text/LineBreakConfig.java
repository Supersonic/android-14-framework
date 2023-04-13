package android.graphics.text;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class LineBreakConfig {
    public static final int LINE_BREAK_STYLE_LOOSE = 1;
    public static final int LINE_BREAK_STYLE_NONE = 0;
    public static final int LINE_BREAK_STYLE_NORMAL = 2;
    public static final int LINE_BREAK_STYLE_STRICT = 3;
    public static final int LINE_BREAK_WORD_STYLE_NONE = 0;
    public static final int LINE_BREAK_WORD_STYLE_PHRASE = 1;
    public static final LineBreakConfig NONE = new Builder().setLineBreakStyle(0).setLineBreakWordStyle(0).build();
    private final boolean mAutoPhraseBreaking;
    private final int mLineBreakStyle;
    private final int mLineBreakWordStyle;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LineBreakStyle {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LineBreakWordStyle {
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private int mLineBreakStyle = 0;
        private int mLineBreakWordStyle = 0;
        private boolean mAutoPhraseBreaking = false;

        public Builder setLineBreakStyle(int lineBreakStyle) {
            this.mLineBreakStyle = lineBreakStyle;
            return this;
        }

        public Builder setLineBreakWordStyle(int lineBreakWordStyle) {
            this.mLineBreakWordStyle = lineBreakWordStyle;
            return this;
        }

        public Builder setAutoPhraseBreaking(boolean autoPhraseBreaking) {
            this.mAutoPhraseBreaking = autoPhraseBreaking;
            return this;
        }

        public LineBreakConfig build() {
            return new LineBreakConfig(this.mLineBreakStyle, this.mLineBreakWordStyle, this.mAutoPhraseBreaking);
        }
    }

    public static LineBreakConfig getLineBreakConfig(int lineBreakStyle, int lineBreakWordStyle) {
        Builder builder = new Builder();
        return builder.setLineBreakStyle(lineBreakStyle).setLineBreakWordStyle(lineBreakWordStyle).build();
    }

    public static LineBreakConfig getLineBreakConfig(int lineBreakStyle, int lineBreakWordStyle, boolean autoPhraseBreaking) {
        Builder builder = new Builder();
        return builder.setLineBreakStyle(lineBreakStyle).setLineBreakWordStyle(lineBreakWordStyle).setAutoPhraseBreaking(autoPhraseBreaking).build();
    }

    private LineBreakConfig(int lineBreakStyle, int lineBreakWordStyle, boolean autoPhraseBreaking) {
        this.mLineBreakStyle = lineBreakStyle;
        this.mLineBreakWordStyle = lineBreakWordStyle;
        this.mAutoPhraseBreaking = autoPhraseBreaking;
    }

    public int getLineBreakStyle() {
        return this.mLineBreakStyle;
    }

    public int getLineBreakWordStyle() {
        return this.mLineBreakWordStyle;
    }

    public boolean getAutoPhraseBreaking() {
        return this.mAutoPhraseBreaking;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineBreakConfig)) {
            return false;
        }
        LineBreakConfig that = (LineBreakConfig) o;
        if (this.mLineBreakStyle != that.mLineBreakStyle || this.mLineBreakWordStyle != that.mLineBreakWordStyle || this.mAutoPhraseBreaking != that.mAutoPhraseBreaking) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLineBreakStyle), Integer.valueOf(this.mLineBreakWordStyle));
    }
}
