package android.graphics.fonts;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.RectF;
import android.p008os.BatteryStats;
import android.p008os.LocaleList;
import android.p008os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.TypedValue;
import android.util.proto.ProtoStream;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class Font {
    private static final NativeAllocationRegistry BUFFER_REGISTRY = NativeAllocationRegistry.createMalloced(ByteBuffer.class.getClassLoader(), nGetReleaseNativeFont());
    private static final NativeAllocationRegistry FONT_REGISTRY = NativeAllocationRegistry.createMalloced(Font.class.getClassLoader(), nGetReleaseNativeFont());
    private static final int NOT_SPECIFIED = -1;
    private static final int STYLE_ITALIC = 1;
    private static final int STYLE_NORMAL = 0;
    private static final String TAG = "Font";
    private final long mNativePtr;
    private final Object mLock = new Object();
    private ByteBuffer mBuffer = null;
    private boolean mIsFileInitialized = false;
    private File mFile = null;
    private FontStyle mFontStyle = null;
    private FontVariationAxis[] mAxes = null;
    private LocaleList mLocaleList = null;

    @CriticalNative
    private static native long nCloneFont(long j);

    @FastNative
    private static native long[] nGetAvailableFontSet();

    @CriticalNative
    private static native int nGetAxisCount(long j);

    @CriticalNative
    private static native long nGetAxisInfo(long j, int i);

    @CriticalNative
    private static native long nGetBufferAddress(long j);

    @FastNative
    private static native float nGetFontMetrics(long j, long j2, Paint.FontMetrics fontMetrics);

    @FastNative
    private static native String nGetFontPath(long j);

    @FastNative
    private static native float nGetGlyphBounds(long j, int i, long j2, RectF rectF);

    @CriticalNative
    private static native int nGetIndex(long j);

    @FastNative
    private static native String nGetLocaleList(long j);

    @CriticalNative
    private static native long nGetMinikinFontPtr(long j);

    @CriticalNative
    private static native int nGetPackedStyle(long j);

    @CriticalNative
    private static native long nGetReleaseNativeFont();

    @CriticalNative
    private static native int nGetSourceId(long j);

    @FastNative
    private static native ByteBuffer nNewByteBuffer(long j);

    /* loaded from: classes.dex */
    public static final class Builder {
        private FontVariationAxis[] mAxes;
        private ByteBuffer mBuffer;
        private IOException mException;
        private File mFile;
        private Font mFont;
        private int mItalic;
        private String mLocaleList;
        private int mTtcIndex;
        private int mWeight;

        @CriticalNative
        private static native void nAddAxis(long j, int i, float f);

        private static native long nBuild(long j, ByteBuffer byteBuffer, String str, String str2, int i, boolean z, int i2);

        @FastNative
        private static native long nClone(long j, long j2, int i, boolean z, int i2);

        private static native long nInitBuilder();

        public Builder(ByteBuffer buffer) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            Preconditions.checkNotNull(buffer, "buffer can not be null");
            if (!buffer.isDirect()) {
                throw new IllegalArgumentException("Only direct buffer can be used as the source of font data.");
            }
            this.mBuffer = buffer;
        }

        public Builder(ByteBuffer buffer, File path, String localeList) {
            this(buffer);
            this.mFile = path;
            this.mLocaleList = localeList;
        }

        public Builder(File path, String localeList) {
            this(path);
            this.mLocaleList = localeList;
        }

        public Builder(File path) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            Preconditions.checkNotNull(path, "path can not be null");
            try {
                FileInputStream fis = new FileInputStream(path);
                FileChannel fc = fis.getChannel();
                this.mBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0L, fc.size());
                fis.close();
            } catch (IOException e) {
                this.mException = e;
            }
            this.mFile = path;
        }

        public Builder(ParcelFileDescriptor fd) {
            this(fd, 0L, -1L);
        }

        public Builder(ParcelFileDescriptor fd, long offset, long size) {
            IOException e;
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            try {
                FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
                try {
                    FileChannel fc = fis.getChannel();
                    long size2 = size == -1 ? fc.size() - offset : size;
                    try {
                        this.mBuffer = fc.map(FileChannel.MapMode.READ_ONLY, offset, size2);
                        try {
                            fis.close();
                        } catch (IOException e2) {
                            e = e2;
                            this.mException = e;
                        }
                    } catch (Throwable th) {
                        th = th;
                        size = size2;
                        fis.close();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e3) {
                e = e3;
            }
        }

        public Builder(AssetManager am, String path) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            try {
                this.mBuffer = createBuffer(am, path, true, -1);
            } catch (IOException e) {
                this.mException = e;
            }
        }

        public Builder(AssetManager am, String path, boolean isAsset, int cookie) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            try {
                this.mBuffer = createBuffer(am, path, isAsset, cookie);
            } catch (IOException e) {
                this.mException = e;
            }
        }

        public Builder(Resources res, int resId) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            TypedValue value = new TypedValue();
            res.getValue(resId, value, true);
            if (value.string == null) {
                this.mException = new FileNotFoundException(resId + " not found");
                return;
            }
            String str = value.string.toString();
            if (!str.toLowerCase().endsWith(".xml")) {
                try {
                    this.mBuffer = createBuffer(res.getAssets(), str, false, value.assetCookie);
                    return;
                } catch (IOException e) {
                    this.mException = e;
                    return;
                }
            }
            this.mException = new FileNotFoundException(resId + " must be font file.");
        }

        public Builder(Font font) {
            this.mLocaleList = "";
            this.mWeight = -1;
            this.mItalic = -1;
            this.mTtcIndex = 0;
            this.mAxes = null;
            this.mFont = font;
            this.mBuffer = font.getBuffer();
            this.mWeight = font.getStyle().getWeight();
            this.mItalic = font.getStyle().getSlant();
            this.mAxes = font.getAxes();
            this.mFile = font.getFile();
            this.mTtcIndex = font.getTtcIndex();
        }

        public static ByteBuffer createBuffer(AssetManager am, String path, boolean isAsset, int cookie) throws IOException {
            InputStream assetStream;
            AssetFileDescriptor assetFD;
            Preconditions.checkNotNull(am, "assetManager can not be null");
            Preconditions.checkNotNull(path, "path can not be null");
            try {
                if (isAsset) {
                    assetFD = am.openFd(path);
                } else if (cookie > 0) {
                    assetFD = am.openNonAssetFd(cookie, path);
                } else {
                    assetFD = am.openNonAssetFd(path);
                }
                FileInputStream fis = assetFD.createInputStream();
                FileChannel fc = fis.getChannel();
                long startOffset = assetFD.getStartOffset();
                long declaredLength = assetFD.getDeclaredLength();
                MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
                if (fis != null) {
                    fis.close();
                }
                return map;
            } catch (IOException e) {
                if (isAsset) {
                    assetStream = am.open(path, 3);
                } else {
                    assetStream = am.openNonAsset(cookie, path, 3);
                }
                try {
                    int capacity = assetStream.available();
                    ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
                    buffer.order(ByteOrder.nativeOrder());
                    assetStream.read(buffer.array(), buffer.arrayOffset(), assetStream.available());
                    if (assetStream.read() != -1) {
                        throw new IOException("Unable to access full contents of " + path);
                    }
                    if (assetStream != null) {
                        assetStream.close();
                    }
                    return buffer;
                } catch (Throwable th) {
                    if (assetStream != null) {
                        try {
                            assetStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
        }

        public Builder setWeight(int weight) {
            boolean z = true;
            Preconditions.checkArgument((1 > weight || weight > 1000) ? false : false);
            this.mWeight = weight;
            return this;
        }

        public Builder setSlant(int slant) {
            this.mItalic = slant == 0 ? 0 : 1;
            return this;
        }

        public Builder setTtcIndex(int ttcIndex) {
            this.mTtcIndex = ttcIndex;
            return this;
        }

        public Builder setFontVariationSettings(String variationSettings) {
            this.mAxes = FontVariationAxis.fromFontVariationSettings(variationSettings);
            return this;
        }

        public Builder setFontVariationSettings(FontVariationAxis[] axes) {
            this.mAxes = axes == null ? null : (FontVariationAxis[]) axes.clone();
            return this;
        }

        public Font build() throws IOException {
            if (this.mException != null) {
                throw new IOException("Failed to read font contents", this.mException);
            }
            if (this.mWeight == -1 || this.mItalic == -1) {
                int packed = FontFileUtil.analyzeStyle(this.mBuffer, this.mTtcIndex, this.mAxes);
                if (!FontFileUtil.isSuccess(packed)) {
                    this.mWeight = 400;
                    this.mItalic = 0;
                } else {
                    if (this.mWeight == -1) {
                        this.mWeight = FontFileUtil.unpackWeight(packed);
                    }
                    if (this.mItalic == -1) {
                        this.mItalic = FontFileUtil.unpackItalic(packed) ? 1 : 0;
                    }
                }
            }
            this.mWeight = Math.max(1, Math.min(1000, this.mWeight));
            int i = this.mItalic;
            boolean italic = i == 1;
            boolean z = i == 1;
            long builderPtr = nInitBuilder();
            FontVariationAxis[] fontVariationAxisArr = this.mAxes;
            if (fontVariationAxisArr != null) {
                for (FontVariationAxis axis : fontVariationAxisArr) {
                    nAddAxis(builderPtr, axis.getOpenTypeTagValue(), axis.getStyleValue());
                }
            }
            ByteBuffer readonlyBuffer = this.mBuffer.asReadOnlyBuffer();
            File file = this.mFile;
            String filePath = file == null ? "" : file.getAbsolutePath();
            Font font = this.mFont;
            if (font == null) {
                long ptr = nBuild(builderPtr, readonlyBuffer, filePath, this.mLocaleList, this.mWeight, italic, this.mTtcIndex);
                Font font2 = new Font(ptr);
                return font2;
            }
            long ptr2 = nClone(font.getNativePtr(), builderPtr, this.mWeight, italic, this.mTtcIndex);
            Font font3 = new Font(ptr2);
            return font3;
        }
    }

    public Font(long nativePtr) {
        this.mNativePtr = nativePtr;
        FONT_REGISTRY.registerNativeAllocation(this, nativePtr);
    }

    public ByteBuffer getBuffer() {
        ByteBuffer byteBuffer;
        synchronized (this.mLock) {
            if (this.mBuffer == null) {
                long ref = nCloneFont(this.mNativePtr);
                ByteBuffer fromNative = nNewByteBuffer(this.mNativePtr);
                BUFFER_REGISTRY.registerNativeAllocation(fromNative, ref);
                this.mBuffer = fromNative.asReadOnlyBuffer();
            }
            byteBuffer = this.mBuffer;
        }
        return byteBuffer;
    }

    public File getFile() {
        File file;
        synchronized (this.mLock) {
            if (!this.mIsFileInitialized) {
                String path = nGetFontPath(this.mNativePtr);
                if (!TextUtils.isEmpty(path)) {
                    this.mFile = new File(path);
                }
                this.mIsFileInitialized = true;
            }
            file = this.mFile;
        }
        return file;
    }

    public FontStyle getStyle() {
        FontStyle fontStyle;
        synchronized (this.mLock) {
            if (this.mFontStyle == null) {
                int packedStyle = nGetPackedStyle(this.mNativePtr);
                this.mFontStyle = new FontStyle(FontFileUtil.unpackWeight(packedStyle), FontFileUtil.unpackItalic(packedStyle) ? 1 : 0);
            }
            fontStyle = this.mFontStyle;
        }
        return fontStyle;
    }

    public int getTtcIndex() {
        return nGetIndex(this.mNativePtr);
    }

    public FontVariationAxis[] getAxes() {
        synchronized (this.mLock) {
            if (this.mAxes == null) {
                int axisCount = nGetAxisCount(this.mNativePtr);
                this.mAxes = new FontVariationAxis[axisCount];
                char[] charBuffer = new char[4];
                for (int i = 0; i < axisCount; i++) {
                    long packedAxis = nGetAxisInfo(this.mNativePtr, i);
                    float value = Float.intBitsToFloat((int) (4294967295L & packedAxis));
                    charBuffer[0] = (char) ((BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK & packedAxis) >>> 56);
                    charBuffer[1] = (char) ((BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK & packedAxis) >>> 48);
                    charBuffer[2] = (char) ((BatteryStats.STEP_LEVEL_LEVEL_MASK & packedAxis) >>> 40);
                    charBuffer[3] = (char) ((ProtoStream.FIELD_TYPE_MASK & packedAxis) >>> 32);
                    this.mAxes[i] = new FontVariationAxis(new String(charBuffer), value);
                }
            }
        }
        return this.mAxes;
    }

    public LocaleList getLocaleList() {
        LocaleList localeList;
        synchronized (this.mLock) {
            if (this.mLocaleList == null) {
                String langTags = nGetLocaleList(this.mNativePtr);
                if (TextUtils.isEmpty(langTags)) {
                    this.mLocaleList = LocaleList.getEmptyLocaleList();
                } else {
                    this.mLocaleList = LocaleList.forLanguageTags(langTags);
                }
            }
            localeList = this.mLocaleList;
        }
        return localeList;
    }

    public float getGlyphBounds(int glyphId, Paint paint, RectF outBoundingBox) {
        return nGetGlyphBounds(this.mNativePtr, glyphId, paint.getNativeInstance(), outBoundingBox);
    }

    public void getMetrics(Paint paint, Paint.FontMetrics outMetrics) {
        nGetFontMetrics(this.mNativePtr, paint.getNativeInstance(), outMetrics);
    }

    public long getNativePtr() {
        return this.mNativePtr;
    }

    public int getSourceIdentifier() {
        return nGetSourceId(this.mNativePtr);
    }

    private boolean isSameSource(Font other) {
        Objects.requireNonNull(other);
        ByteBuffer myBuffer = getBuffer();
        ByteBuffer otherBuffer = other.getBuffer();
        if (myBuffer == otherBuffer) {
            return true;
        }
        if (myBuffer.capacity() != otherBuffer.capacity()) {
            return false;
        }
        if (getSourceIdentifier() == other.getSourceIdentifier() && myBuffer.position() == otherBuffer.position()) {
            return true;
        }
        return myBuffer.equals(otherBuffer);
    }

    public boolean paramEquals(Font f) {
        return f.getStyle().equals(getStyle()) && f.getTtcIndex() == getTtcIndex() && Arrays.equals(f.getAxes(), getAxes()) && Objects.equals(f.getLocaleList(), getLocaleList()) && Objects.equals(getFile(), f.getFile());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Font) {
            Font f = (Font) o;
            if (nGetMinikinFontPtr(this.mNativePtr) == nGetMinikinFontPtr(f.mNativePtr)) {
                return true;
            }
            if (paramEquals(f)) {
                return isSameSource(f);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(getStyle(), Integer.valueOf(getTtcIndex()), Integer.valueOf(Arrays.hashCode(getAxes())), getLocaleList());
    }

    public String toString() {
        return "Font {path=" + getFile() + ", style=" + getStyle() + ", ttcIndex=" + getTtcIndex() + ", axes=" + FontVariationAxis.toFontVariationSettings(getAxes()) + ", localeList=" + getLocaleList() + ", buffer=" + getBuffer() + "}";
    }

    public static Set<Font> getAvailableFonts() {
        long[] nGetAvailableFontSet;
        IdentityHashMap<Font, Font> map = new IdentityHashMap<>();
        for (long nativePtr : nGetAvailableFontSet()) {
            Font font = new Font(nativePtr);
            map.put(font, font);
        }
        return Collections.unmodifiableSet(map.keySet());
    }
}
