package android.graphics.fonts;

import android.util.SparseIntArray;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.util.ArrayList;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class FontFamily {
    private static final String TAG = "FontFamily";
    private final long mNativePtr;

    @CriticalNative
    private static native long nGetFont(long j, int i);

    @CriticalNative
    private static native int nGetFontSize(long j);

    @FastNative
    private static native String nGetLangTags(long j);

    @CriticalNative
    private static native int nGetVariant(long j);

    /* loaded from: classes.dex */
    public static final class Builder {
        private static final NativeAllocationRegistry sFamilyRegistory = NativeAllocationRegistry.createMalloced(FontFamily.class.getClassLoader(), nGetReleaseNativeFamily());
        private final ArrayList<Font> mFonts;
        private final SparseIntArray mStyles;

        @CriticalNative
        private static native void nAddFont(long j, long j2);

        private static native long nBuild(long j, String str, int i, boolean z, boolean z2);

        @CriticalNative
        private static native long nGetReleaseNativeFamily();

        private static native long nInitBuilder();

        public Builder(Font font) {
            ArrayList<Font> arrayList = new ArrayList<>();
            this.mFonts = arrayList;
            SparseIntArray sparseIntArray = new SparseIntArray(4);
            this.mStyles = sparseIntArray;
            Preconditions.checkNotNull(font, "font can not be null");
            sparseIntArray.append(makeStyleIdentifier(font), 0);
            arrayList.add(font);
        }

        public Builder addFont(Font font) {
            Preconditions.checkNotNull(font, "font can not be null");
            int key = makeStyleIdentifier(font);
            if (this.mStyles.indexOfKey(key) >= 0) {
                throw new IllegalArgumentException(font + " has already been added");
            }
            this.mStyles.append(key, 0);
            this.mFonts.add(font);
            return this;
        }

        public FontFamily build() {
            return build("", 0, true, false);
        }

        public FontFamily build(String langTags, int variant, boolean isCustomFallback, boolean isDefaultFallback) {
            long builderPtr = nInitBuilder();
            for (int i = 0; i < this.mFonts.size(); i++) {
                nAddFont(builderPtr, this.mFonts.get(i).getNativePtr());
            }
            long ptr = nBuild(builderPtr, langTags, variant, isCustomFallback, isDefaultFallback);
            FontFamily family = new FontFamily(ptr);
            sFamilyRegistory.registerNativeAllocation(family, ptr);
            return family;
        }

        private static int makeStyleIdentifier(Font font) {
            return font.getStyle().getWeight() | (font.getStyle().getSlant() << 16);
        }
    }

    public FontFamily(long ptr) {
        this.mNativePtr = ptr;
    }

    public String getLangTags() {
        return nGetLangTags(this.mNativePtr);
    }

    public int getVariant() {
        return nGetVariant(this.mNativePtr);
    }

    public Font getFont(int index) {
        if (index < 0 || getSize() <= index) {
            throw new IndexOutOfBoundsException();
        }
        return new Font(nGetFont(this.mNativePtr, index));
    }

    public int getSize() {
        return nGetFontSize(this.mNativePtr);
    }

    public long getNativePtr() {
        return this.mNativePtr;
    }
}
