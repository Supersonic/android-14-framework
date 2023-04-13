package android.graphics.fonts;

import android.annotation.SystemApi;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class FontFamilyUpdateRequest {
    private final List<FontFamily> mFontFamilies;
    private final List<FontFileUpdateRequest> mFontFiles;

    /* loaded from: classes.dex */
    public static final class FontFamily {
        private final List<Font> mFonts;
        private final String mName;

        /* loaded from: classes.dex */
        public static final class Builder {
            private final List<Font> mFonts;
            private final String mName;

            public Builder(String name, List<Font> fonts) {
                Objects.requireNonNull(name);
                Preconditions.checkStringNotEmpty(name);
                Objects.requireNonNull(fonts);
                Preconditions.checkCollectionElementsNotNull(fonts, "fonts");
                Preconditions.checkCollectionNotEmpty(fonts, "fonts");
                this.mName = name;
                this.mFonts = new ArrayList(fonts);
            }

            public Builder addFont(Font font) {
                this.mFonts.add(font);
                return this;
            }

            public FontFamily build() {
                return new FontFamily(this.mName, this.mFonts);
            }
        }

        private FontFamily(String name, List<Font> fonts) {
            this.mName = name;
            this.mFonts = fonts;
        }

        public String getName() {
            return this.mName;
        }

        public List<Font> getFonts() {
            return this.mFonts;
        }
    }

    /* loaded from: classes.dex */
    public static final class Font {
        private final List<FontVariationAxis> mAxes;
        private final int mIndex;
        private final String mPostScriptName;
        private final FontStyle mStyle;

        /* loaded from: classes.dex */
        public static final class Builder {
            private List<FontVariationAxis> mAxes = Collections.emptyList();
            private int mIndex = 0;
            private final String mPostScriptName;
            private final FontStyle mStyle;

            public Builder(String postScriptName, FontStyle style) {
                Objects.requireNonNull(postScriptName);
                Preconditions.checkStringNotEmpty(postScriptName);
                Objects.requireNonNull(style);
                this.mPostScriptName = postScriptName;
                this.mStyle = style;
            }

            public Builder setAxes(List<FontVariationAxis> axes) {
                Objects.requireNonNull(axes);
                Preconditions.checkCollectionElementsNotNull(axes, "axes");
                this.mAxes = axes;
                return this;
            }

            public Builder setIndex(int index) {
                Preconditions.checkArgumentNonnegative(index);
                this.mIndex = index;
                return this;
            }

            public Font build() {
                return new Font(this.mPostScriptName, this.mStyle, this.mIndex, this.mAxes);
            }
        }

        private Font(String postScriptName, FontStyle style, int index, List<FontVariationAxis> axes) {
            this.mPostScriptName = postScriptName;
            this.mStyle = style;
            this.mIndex = index;
            this.mAxes = axes;
        }

        public String getPostScriptName() {
            return this.mPostScriptName;
        }

        public FontStyle getStyle() {
            return this.mStyle;
        }

        public List<FontVariationAxis> getAxes() {
            return this.mAxes;
        }

        public int getIndex() {
            return this.mIndex;
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final List<FontFileUpdateRequest> mFontFileUpdateRequests = new ArrayList();
        private final List<FontFamily> mFontFamilies = new ArrayList();

        public Builder addFontFileUpdateRequest(FontFileUpdateRequest request) {
            Objects.requireNonNull(request);
            this.mFontFileUpdateRequests.add(request);
            return this;
        }

        public Builder addFontFamily(FontFamily fontFamily) {
            Objects.requireNonNull(fontFamily);
            this.mFontFamilies.add(fontFamily);
            return this;
        }

        public FontFamilyUpdateRequest build() {
            return new FontFamilyUpdateRequest(this.mFontFileUpdateRequests, this.mFontFamilies);
        }
    }

    private FontFamilyUpdateRequest(List<FontFileUpdateRequest> fontFiles, List<FontFamily> fontFamilies) {
        this.mFontFiles = fontFiles;
        this.mFontFamilies = fontFamilies;
    }

    public List<FontFileUpdateRequest> getFontFileUpdateRequests() {
        return this.mFontFiles;
    }

    public List<FontFamily> getFontFamilies() {
        return this.mFontFamilies;
    }
}
