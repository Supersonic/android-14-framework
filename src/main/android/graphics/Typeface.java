package android.graphics;

import android.content.res.AssetManager;
import android.content.res.FontResourcesParser;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.graphics.fonts.FontVariationAxis;
import android.graphics.fonts.SystemFonts;
import android.icu.util.ULocale;
import android.p008os.ParcelFileDescriptor;
import android.p008os.SharedMemory;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.provider.FontRequest;
import android.provider.FontsContract;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.text.FontConfig;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LruCache;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class Typeface {
    public static final int BOLD = 1;
    public static final int BOLD_ITALIC = 3;
    public static final String DEFAULT_FAMILY = "sans-serif";
    public static final boolean ENABLE_LAZY_TYPEFACE_INITIALIZATION = true;
    public static final int ITALIC = 2;
    public static final int NORMAL = 0;
    public static final int RESOLVE_BY_FONT_TABLE = -1;
    private static final int STYLE_ITALIC = 1;
    public static final int STYLE_MASK = 3;
    private static final int STYLE_NORMAL = 0;
    static Typeface sDefaultTypeface;
    static Typeface[] sDefaults;
    private final Runnable mCleaner;
    private final int mStyle;
    private int[] mSupportedAxes;
    private final String mSystemFontFamilyName;
    private final int mWeight;
    public final long native_instance;
    private static String TAG = "Typeface";
    private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(Typeface.class.getClassLoader(), nativeGetReleaseFunc());
    public static final Typeface DEFAULT = null;
    public static final Typeface DEFAULT_BOLD = null;
    public static final Typeface SANS_SERIF = null;
    public static final Typeface SERIF = null;
    public static final Typeface MONOSPACE = null;
    private static final LongSparseArray<SparseArray<Typeface>> sStyledTypefaceCache = new LongSparseArray<>(3);
    private static final Object sStyledCacheLock = new Object();
    private static final LongSparseArray<SparseArray<Typeface>> sWeightTypefaceCache = new LongSparseArray<>(3);
    private static final Object sWeightCacheLock = new Object();
    private static final LruCache<String, Typeface> sDynamicTypefaceCache = new LruCache<>(16);
    private static final Object sDynamicCacheLock = new Object();
    static final Map<String, Typeface> sSystemFontMap = new ArrayMap();
    static ByteBuffer sSystemFontMapBuffer = null;
    static SharedMemory sSystemFontMapSharedMemory = null;
    private static final Object SYSTEM_FONT_MAP_LOCK = new Object();
    @Deprecated
    static final Map<String, FontFamily[]> sSystemFallbackMap = Collections.emptyMap();
    private static final int[] EMPTY_AXES = new int[0];

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Style {
    }

    @CriticalNative
    private static native void nativeAddFontCollections(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeCreateFromArray(long[] jArr, long j, int i, int i2);

    private static native long nativeCreateFromTypeface(long j, int i);

    private static native long nativeCreateFromTypefaceWithExactStyle(long j, int i, boolean z);

    private static native long nativeCreateFromTypefaceWithVariation(long j, List<FontVariationAxis> list);

    private static native long nativeCreateWeightAlias(long j, int i);

    private static native void nativeForceSetStaticFinalField(String str, Typeface typeface);

    @CriticalNative
    private static native long nativeGetReleaseFunc();

    @CriticalNative
    private static native int nativeGetStyle(long j);

    private static native int[] nativeGetSupportedAxes(long j);

    @CriticalNative
    private static native int nativeGetWeight(long j);

    private static native long[] nativeReadTypefaces(ByteBuffer byteBuffer, int i);

    private static native void nativeRegisterGenericFamily(String str, long j);

    @FastNative
    private static native void nativeRegisterLocaleList(String str);

    @CriticalNative
    private static native void nativeSetDefault(long j);

    private static native void nativeWarmUpCache(String str);

    private static native int nativeWriteTypefaces(ByteBuffer byteBuffer, int i, long[] jArr);

    static {
        preloadFontFile("/system/fonts/Roboto-Regular.ttf");
        preloadFontFile("/system/fonts/RobotoStatic-Regular.ttf");
        String locale = SystemProperties.get("persist.sys.locale", "en-US");
        String script = ULocale.addLikelySubtags(ULocale.forLanguageTag(locale)).getScript();
        FontConfig config = SystemFonts.getSystemPreinstalledFontConfig();
        for (int i = 0; i < config.getFontFamilies().size(); i++) {
            FontConfig.FontFamily family = config.getFontFamilies().get(i);
            if (!family.getLocaleList().isEmpty()) {
                nativeRegisterLocaleList(family.getLocaleList().toLanguageTags());
            }
            boolean loadFamily = false;
            for (int j = 0; j < family.getLocaleList().size(); j++) {
                String fontScript = ULocale.addLikelySubtags(ULocale.forLocale(family.getLocaleList().get(j))).getScript();
                loadFamily = fontScript.equals(script);
                if (loadFamily) {
                    break;
                }
            }
            if (loadFamily) {
                for (int j2 = 0; j2 < family.getFontList().size(); j2++) {
                    preloadFontFile(family.getFontList().get(j2).getFile().getAbsolutePath());
                }
            }
        }
    }

    public static SharedMemory getSystemFontMapSharedMemory() {
        Objects.requireNonNull(sSystemFontMapSharedMemory);
        return sSystemFontMapSharedMemory;
    }

    private static void setDefault(Typeface t) {
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            sDefaultTypeface = t;
            nativeSetDefault(t.native_instance);
        }
    }

    private static Typeface getDefault() {
        Typeface typeface;
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            typeface = sDefaultTypeface;
        }
        return typeface;
    }

    public int getWeight() {
        return this.mWeight;
    }

    public int getStyle() {
        return this.mStyle;
    }

    public final boolean isBold() {
        return (this.mStyle & 1) != 0;
    }

    public final boolean isItalic() {
        return (this.mStyle & 2) != 0;
    }

    public final String getSystemFontFamilyName() {
        return this.mSystemFontFamilyName;
    }

    private static boolean hasFontFamily(String familyName) {
        boolean containsKey;
        Objects.requireNonNull(familyName, "familyName cannot be null");
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            containsKey = sSystemFontMap.containsKey(familyName);
        }
        return containsKey;
    }

    public static Typeface createFromResources(FontResourcesParser.FamilyResourceEntry entry, AssetManager mgr, String path) {
        Typeface typeface;
        FontResourcesParser.FontFileResourceEntry[] entries;
        if (entry instanceof FontResourcesParser.ProviderResourceEntry) {
            FontResourcesParser.ProviderResourceEntry providerEntry = (FontResourcesParser.ProviderResourceEntry) entry;
            String systemFontFamilyName = providerEntry.getSystemFontFamilyName();
            if (systemFontFamilyName != null && hasFontFamily(systemFontFamilyName)) {
                return create(systemFontFamilyName, 0);
            }
            List<List<String>> givenCerts = providerEntry.getCerts();
            List<List<byte[]>> certs = new ArrayList<>();
            if (givenCerts != null) {
                for (int i = 0; i < givenCerts.size(); i++) {
                    List<String> certSet = givenCerts.get(i);
                    List<byte[]> byteArraySet = new ArrayList<>();
                    for (int j = 0; j < certSet.size(); j++) {
                        byteArraySet.add(Base64.decode(certSet.get(j), 0));
                    }
                    certs.add(byteArraySet);
                }
            }
            FontRequest request = new FontRequest(providerEntry.getAuthority(), providerEntry.getPackage(), providerEntry.getQuery(), certs);
            Typeface typeface2 = FontsContract.getFontSync(request);
            return typeface2 == null ? DEFAULT : typeface2;
        }
        Typeface typeface3 = findFromCache(mgr, path);
        if (typeface3 != null) {
            return typeface3;
        }
        FontResourcesParser.FontFamilyFilesResourceEntry filesEntry = (FontResourcesParser.FontFamilyFilesResourceEntry) entry;
        FontFamily.Builder familyBuilder = null;
        try {
            for (FontResourcesParser.FontFileResourceEntry fontFile : filesEntry.getEntries()) {
                Font.Builder fontBuilder = new Font.Builder(mgr, fontFile.getFileName(), false, -1).setTtcIndex(fontFile.getTtcIndex()).setFontVariationSettings(fontFile.getVariationSettings());
                if (fontFile.getWeight() != -1) {
                    fontBuilder.setWeight(fontFile.getWeight());
                }
                if (fontFile.getItalic() != -1) {
                    int i2 = 1;
                    if (fontFile.getItalic() != 1) {
                        i2 = 0;
                    }
                    fontBuilder.setSlant(i2);
                }
                if (familyBuilder == null) {
                    familyBuilder = new FontFamily.Builder(fontBuilder.build());
                } else {
                    familyBuilder.addFont(fontBuilder.build());
                }
            }
        } catch (IOException e) {
            typeface = DEFAULT;
        } catch (IllegalArgumentException e2) {
            return null;
        }
        if (familyBuilder == null) {
            return DEFAULT;
        }
        android.graphics.fonts.FontFamily family = familyBuilder.build();
        FontStyle normal = new FontStyle(400, 0);
        Font bestFont = family.getFont(0);
        int bestScore = normal.getMatchScore(bestFont.getStyle());
        for (int i3 = 1; i3 < family.getSize(); i3++) {
            Font candidate = family.getFont(i3);
            int score = normal.getMatchScore(candidate.getStyle());
            if (score < bestScore) {
                bestFont = candidate;
                bestScore = score;
            }
        }
        typeface = new CustomFallbackBuilder(family).setStyle(bestFont.getStyle()).build();
        synchronized (sDynamicCacheLock) {
            String key = Builder.createAssetUid(mgr, path, 0, null, -1, -1, DEFAULT_FAMILY);
            sDynamicTypefaceCache.put(key, typeface);
        }
        return typeface;
    }

    public static Typeface findFromCache(AssetManager mgr, String path) {
        synchronized (sDynamicCacheLock) {
            String key = Builder.createAssetUid(mgr, path, 0, null, -1, -1, DEFAULT_FAMILY);
            Typeface typeface = sDynamicTypefaceCache.get(key);
            if (typeface != null) {
                return typeface;
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        public static final int BOLD_WEIGHT = 700;
        public static final int NORMAL_WEIGHT = 400;
        private final AssetManager mAssetManager;
        private String mFallbackFamilyName;
        private final Font.Builder mFontBuilder;
        private int mItalic;
        private final String mPath;
        private int mWeight;

        public Builder(File path) {
            this.mWeight = -1;
            this.mItalic = -1;
            this.mFontBuilder = new Font.Builder(path);
            this.mAssetManager = null;
            this.mPath = null;
        }

        public Builder(FileDescriptor fd) {
            Font.Builder builder;
            this.mWeight = -1;
            this.mItalic = -1;
            try {
                builder = new Font.Builder(ParcelFileDescriptor.dup(fd));
            } catch (IOException e) {
                builder = null;
            }
            this.mFontBuilder = builder;
            this.mAssetManager = null;
            this.mPath = null;
        }

        public Builder(String path) {
            this.mWeight = -1;
            this.mItalic = -1;
            this.mFontBuilder = new Font.Builder(new File(path));
            this.mAssetManager = null;
            this.mPath = null;
        }

        public Builder(AssetManager assetManager, String path) {
            this(assetManager, path, true, 0);
        }

        public Builder(AssetManager assetManager, String path, boolean isAsset, int cookie) {
            this.mWeight = -1;
            this.mItalic = -1;
            this.mFontBuilder = new Font.Builder(assetManager, path, isAsset, cookie);
            this.mAssetManager = assetManager;
            this.mPath = path;
        }

        public Builder setWeight(int weight) {
            this.mWeight = weight;
            this.mFontBuilder.setWeight(weight);
            return this;
        }

        public Builder setItalic(boolean italic) {
            this.mItalic = italic ? 1 : 0;
            this.mFontBuilder.setSlant(italic ? 1 : 0);
            return this;
        }

        public Builder setTtcIndex(int ttcIndex) {
            this.mFontBuilder.setTtcIndex(ttcIndex);
            return this;
        }

        public Builder setFontVariationSettings(String variationSettings) {
            this.mFontBuilder.setFontVariationSettings(variationSettings);
            return this;
        }

        public Builder setFontVariationSettings(FontVariationAxis[] axes) {
            this.mFontBuilder.setFontVariationSettings(axes);
            return this;
        }

        public Builder setFallback(String familyName) {
            this.mFallbackFamilyName = familyName;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static String createAssetUid(AssetManager mgr, String path, int ttcIndex, FontVariationAxis[] axes, int weight, int italic, String fallback) {
            SparseArray<String> pkgs = mgr.getAssignedPackageIdentifiers();
            StringBuilder builder = new StringBuilder();
            int size = pkgs.size();
            for (int i = 0; i < size; i++) {
                builder.append(pkgs.valueAt(i));
                builder.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            }
            builder.append(path);
            builder.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            builder.append(Integer.toString(ttcIndex));
            builder.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            builder.append(Integer.toString(weight));
            builder.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            builder.append(Integer.toString(italic));
            builder.append("--");
            builder.append(fallback);
            builder.append("--");
            if (axes != null) {
                for (FontVariationAxis axis : axes) {
                    builder.append(axis.getTag());
                    builder.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    builder.append(Float.toString(axis.getStyleValue()));
                }
            }
            return builder.toString();
        }

        private Typeface resolveFallbackTypeface() {
            String str = this.mFallbackFamilyName;
            if (str == null) {
                return null;
            }
            Typeface base = Typeface.getSystemDefaultTypeface(str);
            int weight = this.mWeight;
            if (weight == -1 && this.mItalic == -1) {
                return base;
            }
            if (weight == -1) {
                weight = base.mWeight;
            }
            int i = this.mItalic;
            boolean z = false;
            if (i != -1 ? i == 1 : (base.mStyle & 2) != 0) {
                z = true;
            }
            boolean italic = z;
            return Typeface.createWeightStyle(base, weight, italic);
        }

        public Typeface build() {
            String key;
            Font.Builder builder = this.mFontBuilder;
            if (builder == null) {
                return resolveFallbackTypeface();
            }
            try {
                Font font = builder.build();
                AssetManager assetManager = this.mAssetManager;
                if (assetManager == null) {
                    key = null;
                } else {
                    String str = this.mPath;
                    int ttcIndex = font.getTtcIndex();
                    FontVariationAxis[] axes = font.getAxes();
                    int i = this.mWeight;
                    int i2 = this.mItalic;
                    String str2 = this.mFallbackFamilyName;
                    if (str2 == null) {
                        str2 = Typeface.DEFAULT_FAMILY;
                    }
                    key = createAssetUid(assetManager, str, ttcIndex, axes, i, i2, str2);
                }
                if (key != null) {
                    synchronized (Typeface.sDynamicCacheLock) {
                        Typeface typeface = (Typeface) Typeface.sDynamicTypefaceCache.get(key);
                        if (typeface != null) {
                            return typeface;
                        }
                    }
                }
                android.graphics.fonts.FontFamily family = new FontFamily.Builder(font).build();
                int weight = this.mWeight;
                if (weight == -1) {
                    weight = font.getStyle().getWeight();
                }
                int i3 = this.mItalic;
                if (i3 == -1) {
                    i3 = font.getStyle().getSlant();
                }
                int slant = i3;
                CustomFallbackBuilder builder2 = new CustomFallbackBuilder(family).setStyle(new FontStyle(weight, slant));
                String str3 = this.mFallbackFamilyName;
                if (str3 != null) {
                    builder2.setSystemFallback(str3);
                }
                Typeface typeface2 = builder2.build();
                if (key != null) {
                    synchronized (Typeface.sDynamicCacheLock) {
                        Typeface.sDynamicTypefaceCache.put(key, typeface2);
                    }
                }
                return typeface2;
            } catch (IOException | IllegalArgumentException e) {
                return resolveFallbackTypeface();
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class CustomFallbackBuilder {
        private static final int MAX_CUSTOM_FALLBACK = 64;
        private String mFallbackName;
        private final ArrayList<android.graphics.fonts.FontFamily> mFamilies;
        private FontStyle mStyle;

        public static int getMaxCustomFallbackCount() {
            return 64;
        }

        public CustomFallbackBuilder(android.graphics.fonts.FontFamily family) {
            ArrayList<android.graphics.fonts.FontFamily> arrayList = new ArrayList<>();
            this.mFamilies = arrayList;
            this.mFallbackName = null;
            Preconditions.checkNotNull(family);
            arrayList.add(family);
        }

        public CustomFallbackBuilder setSystemFallback(String familyName) {
            Preconditions.checkNotNull(familyName);
            this.mFallbackName = familyName;
            return this;
        }

        public CustomFallbackBuilder setStyle(FontStyle style) {
            this.mStyle = style;
            return this;
        }

        public CustomFallbackBuilder addCustomFallback(android.graphics.fonts.FontFamily family) {
            Preconditions.checkNotNull(family);
            Preconditions.checkArgument(this.mFamilies.size() < getMaxCustomFallbackCount(), "Custom fallback limit exceeded(%d)", Integer.valueOf(getMaxCustomFallbackCount()));
            this.mFamilies.add(family);
            return this;
        }

        public Typeface build() {
            int userFallbackSize = this.mFamilies.size();
            Typeface fallbackTypeface = Typeface.getSystemDefaultTypeface(this.mFallbackName);
            long[] ptrArray = new long[userFallbackSize];
            for (int i = 0; i < userFallbackSize; i++) {
                ptrArray[i] = this.mFamilies.get(i).getNativePtr();
            }
            FontStyle fontStyle = this.mStyle;
            int weight = fontStyle == null ? 400 : fontStyle.getWeight();
            FontStyle fontStyle2 = this.mStyle;
            int italic = (fontStyle2 == null || fontStyle2.getSlant() == 0) ? 0 : 1;
            return new Typeface(Typeface.nativeCreateFromArray(ptrArray, fallbackTypeface.native_instance, weight, italic), null);
        }
    }

    public static Typeface create(String familyName, int style) {
        return create(getSystemDefaultTypeface(familyName), style);
    }

    public static Typeface create(Typeface family, int style) {
        if ((style & (-4)) != 0) {
            style = 0;
        }
        if (family == null) {
            family = getDefault();
        }
        if (family.mStyle == style) {
            return family;
        }
        long ni = family.native_instance;
        synchronized (sStyledCacheLock) {
            LongSparseArray<SparseArray<Typeface>> longSparseArray = sStyledTypefaceCache;
            SparseArray<Typeface> styles = longSparseArray.get(ni);
            if (styles == null) {
                styles = new SparseArray<>(4);
                longSparseArray.put(ni, styles);
            } else {
                Typeface typeface = styles.get(style);
                if (typeface != null) {
                    return typeface;
                }
            }
            Typeface typeface2 = new Typeface(nativeCreateFromTypeface(ni, style), family.getSystemFontFamilyName());
            styles.put(style, typeface2);
            return typeface2;
        }
    }

    public static Typeface create(Typeface family, int weight, boolean italic) {
        Preconditions.checkArgumentInRange(weight, 0, 1000, "weight");
        if (family == null) {
            family = getDefault();
        }
        return createWeightStyle(family, weight, italic);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Typeface createWeightStyle(Typeface base, int weight, boolean italic) {
        int key = (weight << 1) | (italic ? 1 : 0);
        synchronized (sWeightCacheLock) {
            LongSparseArray<SparseArray<Typeface>> longSparseArray = sWeightTypefaceCache;
            SparseArray<Typeface> innerCache = longSparseArray.get(base.native_instance);
            if (innerCache == null) {
                innerCache = new SparseArray<>(4);
                longSparseArray.put(base.native_instance, innerCache);
            } else {
                Typeface typeface = innerCache.get(key);
                if (typeface != null) {
                    return typeface;
                }
            }
            Typeface typeface2 = new Typeface(nativeCreateFromTypefaceWithExactStyle(base.native_instance, weight, italic), base.getSystemFontFamilyName());
            innerCache.put(key, typeface2);
            return typeface2;
        }
    }

    public static Typeface createFromTypefaceWithVariation(Typeface family, List<FontVariationAxis> axes) {
        Typeface base = family == null ? DEFAULT : family;
        Typeface typeface = new Typeface(nativeCreateFromTypefaceWithVariation(base.native_instance, axes), base.getSystemFontFamilyName());
        return typeface;
    }

    public static Typeface defaultFromStyle(int style) {
        Typeface typeface;
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            typeface = sDefaults[style];
        }
        return typeface;
    }

    public static Typeface createFromAsset(AssetManager mgr, String path) {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(mgr);
        Typeface typeface = new Builder(mgr, path).build();
        if (typeface != null) {
            return typeface;
        }
        try {
            InputStream inputStream = mgr.open(path);
            if (inputStream != null) {
                inputStream.close();
            }
            return DEFAULT;
        } catch (IOException e) {
            throw new RuntimeException("Font asset not found " + path);
        }
    }

    private static String createProviderUid(String authority, String query) {
        return "provider:" + authority + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + query;
    }

    public static Typeface createFromFile(File file) {
        Typeface typeface = new Builder(file).build();
        if (typeface != null) {
            return typeface;
        }
        if (!file.exists()) {
            throw new RuntimeException("Font asset not found " + file.getAbsolutePath());
        }
        return DEFAULT;
    }

    public static Typeface createFromFile(String path) {
        Preconditions.checkNotNull(path);
        return createFromFile(new File(path));
    }

    @Deprecated
    private static Typeface createFromFamilies(FontFamily[] families) {
        long[] ptrArray = new long[families.length];
        for (int i = 0; i < families.length; i++) {
            ptrArray[i] = families[i].mNativePtr;
        }
        return new Typeface(nativeCreateFromArray(ptrArray, 0L, -1, -1), null);
    }

    private static Typeface createFromFamilies(String familyName, android.graphics.fonts.FontFamily[] families) {
        long[] ptrArray = new long[families.length];
        for (int i = 0; i < families.length; i++) {
            ptrArray[i] = families[i].getNativePtr();
        }
        return new Typeface(nativeCreateFromArray(ptrArray, 0L, -1, -1), familyName);
    }

    @Deprecated
    private static Typeface createFromFamiliesWithDefault(FontFamily[] families, int weight, int italic) {
        return createFromFamiliesWithDefault(families, DEFAULT_FAMILY, weight, italic);
    }

    @Deprecated
    private static Typeface createFromFamiliesWithDefault(FontFamily[] families, String fallbackName, int weight, int italic) {
        Typeface fallbackTypeface = getSystemDefaultTypeface(fallbackName);
        long[] ptrArray = new long[families.length];
        for (int i = 0; i < families.length; i++) {
            ptrArray[i] = families[i].mNativePtr;
        }
        return new Typeface(nativeCreateFromArray(ptrArray, fallbackTypeface.native_instance, weight, italic), null);
    }

    private Typeface(long ni) {
        this(ni, null);
    }

    private Typeface(long ni, String systemFontFamilyName) {
        if (ni == 0) {
            throw new RuntimeException("native typeface cannot be made");
        }
        this.native_instance = ni;
        this.mCleaner = sRegistry.registerNativeAllocation(this, ni);
        this.mStyle = nativeGetStyle(ni);
        this.mWeight = nativeGetWeight(ni);
        this.mSystemFontFamilyName = systemFontFamilyName;
    }

    public void releaseNativeObjectForTest() {
        this.mCleaner.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Typeface getSystemDefaultTypeface(String familyName) {
        Typeface tf = sSystemFontMap.get(familyName);
        return tf == null ? DEFAULT : tf;
    }

    public static void initSystemDefaultTypefaces(Map<String, android.graphics.fonts.FontFamily[]> fallbacks, List<FontConfig.Alias> aliases, Map<String, Typeface> outSystemFontMap) {
        Typeface base;
        for (Map.Entry<String, android.graphics.fonts.FontFamily[]> entry : fallbacks.entrySet()) {
            outSystemFontMap.put(entry.getKey(), createFromFamilies(entry.getKey(), entry.getValue()));
        }
        for (int i = 0; i < aliases.size(); i++) {
            FontConfig.Alias alias = aliases.get(i);
            if (!outSystemFontMap.containsKey(alias.getName()) && (base = outSystemFontMap.get(alias.getOriginal())) != null) {
                int weight = alias.getWeight();
                Typeface newFace = weight == 400 ? base : new Typeface(nativeCreateWeightAlias(base.native_instance, weight), alias.getName());
                outSystemFontMap.put(alias.getName(), newFace);
            }
        }
    }

    private static void registerGenericFamilyNative(String familyName, Typeface typeface) {
        if (typeface != null) {
            nativeRegisterGenericFamily(familyName, typeface.native_instance);
        }
    }

    public static SharedMemory serializeFontMap(Map<String, Typeface> fontMap) throws IOException, ErrnoException {
        long[] nativePtrs = new long[fontMap.size()];
        ByteArrayOutputStream namesBytes = new ByteArrayOutputStream();
        int i = 0;
        for (Map.Entry<String, Typeface> entry : fontMap.entrySet()) {
            nativePtrs[i] = entry.getValue().native_instance;
            writeString(namesBytes, entry.getKey());
            i++;
        }
        int typefacesBytesCount = nativeWriteTypefaces(null, 4, nativePtrs);
        SharedMemory sharedMemory = SharedMemory.create("fontMap", typefacesBytesCount + 4 + namesBytes.size());
        ByteBuffer writableBuffer = sharedMemory.mapReadWrite().order(ByteOrder.BIG_ENDIAN);
        try {
            writableBuffer.putInt(typefacesBytesCount);
            int writtenBytesCount = nativeWriteTypefaces(writableBuffer, writableBuffer.position(), nativePtrs);
            if (writtenBytesCount != typefacesBytesCount) {
                throw new IOException(String.format("Unexpected bytes written: %d, expected: %d", Integer.valueOf(writtenBytesCount), Integer.valueOf(typefacesBytesCount)));
            }
            writableBuffer.position(writableBuffer.position() + writtenBytesCount);
            writableBuffer.put(namesBytes.toByteArray());
            SharedMemory.unmap(writableBuffer);
            sharedMemory.setProtect(OsConstants.PROT_READ);
            return sharedMemory;
        } catch (Throwable th) {
            SharedMemory.unmap(writableBuffer);
            throw th;
        }
    }

    public static long[] deserializeFontMap(ByteBuffer buffer, Map<String, Typeface> out) throws IOException {
        int typefacesBytesCount = buffer.getInt();
        long[] nativePtrs = nativeReadTypefaces(buffer, buffer.position());
        if (nativePtrs == null) {
            throw new IOException("Could not read typefaces");
        }
        out.clear();
        buffer.position(buffer.position() + typefacesBytesCount);
        for (long nativePtr : nativePtrs) {
            String name = readString(buffer);
            out.put(name, new Typeface(nativePtr, name));
        }
        return nativePtrs;
    }

    private static String readString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }

    private static void writeString(ByteArrayOutputStream bos, String value) throws IOException {
        byte[] bytes = value.getBytes();
        writeInt(bos, bytes.length);
        bos.write(bytes);
    }

    private static void writeInt(ByteArrayOutputStream bos, int value) {
        bos.write((value >> 24) & 255);
        bos.write((value >> 16) & 255);
        bos.write((value >> 8) & 255);
        bos.write(value & 255);
    }

    public static Map<String, Typeface> getSystemFontMap() {
        Map<String, Typeface> map;
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            map = sSystemFontMap;
        }
        return map;
    }

    public static void setSystemFontMap(SharedMemory sharedMemory) throws IOException, ErrnoException {
        if (sSystemFontMapBuffer != null) {
            if (sharedMemory == null || sharedMemory == sSystemFontMapSharedMemory) {
                return;
            }
            throw new UnsupportedOperationException("Once set, buffer-based system font map cannot be updated");
        }
        sSystemFontMapSharedMemory = sharedMemory;
        Trace.traceBegin(2L, "setSystemFontMap");
        try {
            if (sharedMemory == null) {
                loadPreinstalledSystemFontMap();
                return;
            }
            sSystemFontMapBuffer = sharedMemory.mapReadOnly().order(ByteOrder.BIG_ENDIAN);
            Map<String, Typeface> systemFontMap = new ArrayMap<>();
            long[] nativePtrs = deserializeFontMap(sSystemFontMapBuffer, systemFontMap);
            for (long ptr : nativePtrs) {
                nativeAddFontCollections(ptr);
            }
            setSystemFontMap(systemFontMap);
        } finally {
            Trace.traceEnd(2L);
        }
    }

    public static void setSystemFontMap(Map<String, Typeface> systemFontMap) {
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            Map<String, Typeface> map = sSystemFontMap;
            map.clear();
            map.putAll(systemFontMap);
            if (map.containsKey(DEFAULT_FAMILY)) {
                setDefault(map.get(DEFAULT_FAMILY));
            }
            nativeForceSetStaticFinalField("DEFAULT", create(sDefaultTypeface, 0));
            nativeForceSetStaticFinalField("DEFAULT_BOLD", create(sDefaultTypeface, 1));
            nativeForceSetStaticFinalField("SANS_SERIF", create(DEFAULT_FAMILY, 0));
            nativeForceSetStaticFinalField("SERIF", create("serif", 0));
            nativeForceSetStaticFinalField("MONOSPACE", create("monospace", 0));
            String str = null;
            String str2 = null;
            sDefaults = new Typeface[]{DEFAULT, DEFAULT_BOLD, create((String) null, 2), create((String) null, 3)};
            String[] genericFamilies = {"serif", DEFAULT_FAMILY, "cursive", "fantasy", "monospace", "system-ui"};
            for (String genericFamily : genericFamilies) {
                registerGenericFamilyNative(genericFamily, systemFontMap.get(genericFamily));
            }
        }
    }

    public static Pair<List<Typeface>, List<Typeface>> changeDefaultFontForTest(List<Typeface> defaults, List<Typeface> genericFamilies) {
        Pair<List<Typeface>, List<Typeface>> pair;
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            List<Typeface> oldDefaults = Arrays.asList(sDefaults);
            sDefaults = (Typeface[]) defaults.toArray(new Typeface[4]);
            setDefault(defaults.get(0));
            ArrayList<Typeface> oldGenerics = new ArrayList<>();
            Map<String, Typeface> map = sSystemFontMap;
            oldGenerics.add(map.get(DEFAULT_FAMILY));
            map.put(DEFAULT_FAMILY, genericFamilies.get(0));
            oldGenerics.add(map.get("serif"));
            map.put("serif", genericFamilies.get(1));
            oldGenerics.add(map.get("monospace"));
            map.put("monospace", genericFamilies.get(2));
            pair = new Pair<>(oldDefaults, oldGenerics);
        }
        return pair;
    }

    private static void preloadFontFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Log.m108i(TAG, "Preloading " + file.getAbsolutePath());
            nativeWarmUpCache(filePath);
        }
    }

    public static void destroySystemFontMap() {
        synchronized (SYSTEM_FONT_MAP_LOCK) {
            for (Typeface typeface : sSystemFontMap.values()) {
                typeface.releaseNativeObjectForTest();
            }
            sSystemFontMap.clear();
            ByteBuffer byteBuffer = sSystemFontMapBuffer;
            if (byteBuffer != null) {
                SharedMemory.unmap(byteBuffer);
            }
            sSystemFontMapBuffer = null;
            sSystemFontMapSharedMemory = null;
            synchronized (sStyledCacheLock) {
                destroyTypefaceCacheLocked(sStyledTypefaceCache);
            }
            synchronized (sWeightCacheLock) {
                destroyTypefaceCacheLocked(sWeightTypefaceCache);
            }
        }
    }

    private static void destroyTypefaceCacheLocked(LongSparseArray<SparseArray<Typeface>> cache) {
        for (int i = 0; i < cache.size(); i++) {
            SparseArray<Typeface> array = cache.valueAt(i);
            for (int j = 0; j < array.size(); j++) {
                array.valueAt(j).releaseNativeObjectForTest();
            }
        }
        cache.clear();
    }

    public static void loadPreinstalledSystemFontMap() {
        FontConfig fontConfig = SystemFonts.getSystemPreinstalledFontConfig();
        Map<String, android.graphics.fonts.FontFamily[]> fallback = SystemFonts.buildSystemFallback(fontConfig);
        Map<String, Typeface> typefaceMap = SystemFonts.buildSystemTypefaces(fontConfig, fallback);
        setSystemFontMap(typefaceMap);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Typeface typeface = (Typeface) o;
        if (this.mStyle == typeface.mStyle && this.native_instance == typeface.native_instance) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.native_instance;
        int result = (17 * 31) + ((int) (j ^ (j >>> 32)));
        return (result * 31) + this.mStyle;
    }

    public boolean isSupportedAxes(int axis) {
        synchronized (this) {
            if (this.mSupportedAxes == null) {
                int[] nativeGetSupportedAxes = nativeGetSupportedAxes(this.native_instance);
                this.mSupportedAxes = nativeGetSupportedAxes;
                if (nativeGetSupportedAxes == null) {
                    this.mSupportedAxes = EMPTY_AXES;
                }
            }
        }
        return Arrays.binarySearch(this.mSupportedAxes, axis) >= 0;
    }
}
