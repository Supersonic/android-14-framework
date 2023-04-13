package android.content.res;

import android.app.blob.XmlTags;
import android.content.p001pm.ActivityInfo;
import android.content.res.XmlBlock;
import android.content.res.loader.ResourcesLoader;
import android.p008os.ParcelFileDescriptor;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.TypedValue;
import com.android.internal.content.p014om.OverlayConfig;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AssetManager implements AutoCloseable {
    public static final int ACCESS_BUFFER = 3;
    public static final int ACCESS_RANDOM = 1;
    public static final int ACCESS_STREAMING = 2;
    public static final int ACCESS_UNKNOWN = 0;
    public static final int COOKIE_UNKNOWN = -1;
    private static final boolean DEBUG_REFS = false;
    private static final String FRAMEWORK_APK_PATH = "/system/framework/framework-res.apk";
    private static final String TAG = "AssetManager";
    private static ArraySet<ApkAssets> sSystemApkAssetsSet;
    private ApkAssets[] mApkAssets;
    private ResourcesLoader[] mLoaders;
    private int mNumRefs;
    private long mObject;
    private final long[] mOffsets;
    private boolean mOpen;
    private HashMap<Long, RuntimeException> mRefStacks;
    private final TypedValue mValue;
    private static final Object sSync = new Object();
    private static final ApkAssets[] sEmptyApkAssets = new ApkAssets[0];
    static AssetManager sSystem = null;
    private static ApkAssets[] sSystemApkAssets = new ApkAssets[0];

    public static native String getAssetAllocations();

    public static native int getGlobalAssetCount();

    public static native int getGlobalAssetManagerCount();

    private static native void nativeApplyStyle(long j, long j2, int i, int i2, long j3, int[] iArr, long j4, long j5);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeAssetDestroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetGetRemainingLength(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetRead(long j, byte[] bArr, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeAssetReadChar(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeAssetSeek(long j, long j2, int i);

    private static native int[] nativeAttributeResolutionStack(long j, long j2, int i, int i2, int i3);

    private static native boolean nativeContainsAllocatedTable(long j);

    private static native long nativeCreate();

    private static native void nativeDestroy(long j);

    private static native SparseArray<String> nativeGetAssignedPackageIdentifiers(long j, boolean z, boolean z2);

    private static native String nativeGetLastResourceResolution(long j);

    private static native String[] nativeGetLocales(long j, boolean z);

    private static native Map nativeGetOverlayableMap(long j, String str);

    private static native String nativeGetOverlayablesToString(long j, String str);

    private static native int nativeGetParentThemeIdentifier(long j, int i);

    private static native int nativeGetResourceArray(long j, int i, int[] iArr);

    private static native int nativeGetResourceArraySize(long j, int i);

    private static native int nativeGetResourceBagValue(long j, int i, int i2, TypedValue typedValue);

    private static native String nativeGetResourceEntryName(long j, int i);

    private static native int nativeGetResourceIdentifier(long j, String str, String str2, String str3);

    private static native int[] nativeGetResourceIntArray(long j, int i);

    private static native String nativeGetResourceName(long j, int i);

    private static native String nativeGetResourcePackageName(long j, int i);

    private static native String[] nativeGetResourceStringArray(long j, int i);

    private static native int[] nativeGetResourceStringArrayInfo(long j, int i);

    private static native String nativeGetResourceTypeName(long j, int i);

    private static native int nativeGetResourceValue(long j, int i, short s, TypedValue typedValue, boolean z);

    private static native Configuration[] nativeGetSizeConfigurations(long j);

    private static native int[] nativeGetStyleAttributes(long j, int i);

    private static native long nativeGetThemeFreeFunction();

    private static native String[] nativeList(long j, String str) throws IOException;

    private static native long nativeOpenAsset(long j, String str, int i);

    private static native ParcelFileDescriptor nativeOpenAssetFd(long j, String str, long[] jArr) throws IOException;

    private static native long nativeOpenNonAsset(long j, int i, String str, int i2);

    private static native ParcelFileDescriptor nativeOpenNonAssetFd(long j, int i, String str, long[] jArr) throws IOException;

    private static native long nativeOpenXmlAsset(long j, int i, String str);

    private static native long nativeOpenXmlAssetFd(long j, int i, FileDescriptor fileDescriptor);

    private static native boolean nativeResolveAttrs(long j, long j2, int i, int i2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4);

    private static native boolean nativeRetrieveAttributes(long j, long j2, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetApkAssets(long j, ApkAssets[] apkAssetsArr, boolean z);

    private static native void nativeSetConfiguration(long j, int i, int i2, String str, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18);

    private static native void nativeSetResourceResolutionLoggingEnabled(long j, boolean z);

    private static native void nativeThemeApplyStyle(long j, long j2, int i, boolean z);

    private static native void nativeThemeCopy(long j, long j2, long j3, long j4);

    private static native long nativeThemeCreate(long j);

    private static native void nativeThemeDump(long j, long j2, int i, String str, String str2);

    private static native int nativeThemeGetAttributeValue(long j, long j2, int i, TypedValue typedValue, boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native int nativeThemeGetChangingConfigurations(long j);

    private static native void nativeThemeRebase(long j, long j2, int[] iArr, boolean[] zArr, int i);

    /* loaded from: classes.dex */
    public static class Builder {
        private ArrayList<ApkAssets> mUserApkAssets = new ArrayList<>();
        private ArrayList<ResourcesLoader> mLoaders = new ArrayList<>();

        public Builder addApkAssets(ApkAssets apkAssets) {
            this.mUserApkAssets.add(apkAssets);
            return this;
        }

        public Builder addLoader(ResourcesLoader loader) {
            this.mLoaders.add(loader);
            return this;
        }

        public AssetManager build() {
            ApkAssets[] systemApkAssets = AssetManager.getSystem().getApkAssets();
            ArrayList<ApkAssets> loaderApkAssets = new ArrayList<>();
            ArraySet<ApkAssets> uniqueLoaderApkAssets = new ArraySet<>();
            int i = this.mLoaders.size();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                List<ApkAssets> currentLoaderApkAssets = this.mLoaders.get(i).getApkAssets();
                for (int j = currentLoaderApkAssets.size() - 1; j >= 0; j--) {
                    ApkAssets apkAssets = currentLoaderApkAssets.get(j);
                    if (uniqueLoaderApkAssets.add(apkAssets)) {
                        loaderApkAssets.add(0, apkAssets);
                    }
                }
            }
            int i2 = systemApkAssets.length;
            int totalApkAssetCount = i2 + this.mUserApkAssets.size() + loaderApkAssets.size();
            ApkAssets[] apkAssets2 = new ApkAssets[totalApkAssetCount];
            System.arraycopy(systemApkAssets, 0, apkAssets2, 0, systemApkAssets.length);
            int n = this.mUserApkAssets.size();
            for (int i3 = 0; i3 < n; i3++) {
                apkAssets2[systemApkAssets.length + i3] = this.mUserApkAssets.get(i3);
            }
            int n2 = loaderApkAssets.size();
            for (int i4 = 0; i4 < n2; i4++) {
                apkAssets2[systemApkAssets.length + i4 + this.mUserApkAssets.size()] = loaderApkAssets.get(i4);
            }
            AssetManager assetManager = new AssetManager(false);
            assetManager.mApkAssets = apkAssets2;
            AssetManager.nativeSetApkAssets(assetManager.mObject, apkAssets2, false);
            assetManager.mLoaders = this.mLoaders.isEmpty() ? null : (ResourcesLoader[]) this.mLoaders.toArray(new ResourcesLoader[0]);
            return assetManager;
        }
    }

    public AssetManager() {
        ApkAssets[] assets;
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked(false, FRAMEWORK_APK_PATH);
            assets = sSystemApkAssets;
        }
        this.mObject = nativeCreate();
        setApkAssets(assets, false);
    }

    private AssetManager(boolean sentinel) {
        this.mValue = new TypedValue();
        this.mOffsets = new long[2];
        this.mOpen = true;
        this.mNumRefs = 1;
        this.mObject = nativeCreate();
    }

    public static void createSystemAssetsInZygoteLocked(boolean reinitialize, String frameworkPath) {
        if (sSystem != null && !reinitialize) {
            return;
        }
        try {
            ArrayList<ApkAssets> apkAssets = new ArrayList<>();
            apkAssets.add(ApkAssets.loadFromPath(frameworkPath, 1));
            String[] systemIdmapPaths = OverlayConfig.getZygoteInstance().createImmutableFrameworkIdmapsInZygote();
            for (String idmapPath : systemIdmapPaths) {
                apkAssets.add(ApkAssets.loadOverlayFromPath(idmapPath, 1));
            }
            sSystemApkAssetsSet = new ArraySet<>(apkAssets);
            sSystemApkAssets = (ApkAssets[]) apkAssets.toArray(new ApkAssets[apkAssets.size()]);
            if (sSystem == null) {
                sSystem = new AssetManager(true);
            }
            sSystem.setApkAssets(sSystemApkAssets, false);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create system AssetManager", e);
        }
    }

    public static AssetManager getSystem() {
        AssetManager assetManager;
        synchronized (sSync) {
            createSystemAssetsInZygoteLocked(false, FRAMEWORK_APK_PATH);
            assetManager = sSystem;
        }
        return assetManager;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decRefsLocked(hashCode());
            }
        }
    }

    public void setApkAssets(ApkAssets[] apkAssets, boolean invalidateCaches) {
        Objects.requireNonNull(apkAssets, "apkAssets");
        ApkAssets[] apkAssetsArr = sSystemApkAssets;
        ApkAssets[] newApkAssets = new ApkAssets[apkAssetsArr.length + apkAssets.length];
        System.arraycopy(apkAssetsArr, 0, newApkAssets, 0, apkAssetsArr.length);
        int newLength = sSystemApkAssets.length;
        for (ApkAssets apkAsset : apkAssets) {
            if (!sSystemApkAssetsSet.contains(apkAsset)) {
                newApkAssets[newLength] = apkAsset;
                newLength++;
            }
        }
        if (newLength != newApkAssets.length) {
            newApkAssets = (ApkAssets[]) Arrays.copyOf(newApkAssets, newLength);
        }
        synchronized (this) {
            ensureOpenLocked();
            this.mApkAssets = newApkAssets;
            nativeSetApkAssets(this.mObject, newApkAssets, invalidateCaches);
            if (invalidateCaches) {
                invalidateCachesLocked(-1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLoaders(List<ResourcesLoader> newLoaders) {
        Objects.requireNonNull(newLoaders, "newLoaders");
        ArrayList<ApkAssets> apkAssets = new ArrayList<>();
        int i = 0;
        while (true) {
            ApkAssets[] apkAssetsArr = this.mApkAssets;
            if (i >= apkAssetsArr.length) {
                break;
            }
            if (!apkAssetsArr[i].isForLoader()) {
                apkAssets.add(this.mApkAssets[i]);
            }
            i++;
        }
        if (!newLoaders.isEmpty()) {
            int loaderStartIndex = apkAssets.size();
            ArraySet<ApkAssets> uniqueLoaderApkAssets = new ArraySet<>();
            for (int i2 = newLoaders.size() - 1; i2 >= 0; i2--) {
                List<ApkAssets> currentLoaderApkAssets = newLoaders.get(i2).getApkAssets();
                for (int j = currentLoaderApkAssets.size() - 1; j >= 0; j--) {
                    ApkAssets loaderApkAssets = currentLoaderApkAssets.get(j);
                    if (uniqueLoaderApkAssets.add(loaderApkAssets)) {
                        apkAssets.add(loaderStartIndex, loaderApkAssets);
                    }
                }
            }
        }
        this.mLoaders = (ResourcesLoader[]) newLoaders.toArray(new ResourcesLoader[0]);
        setApkAssets((ApkAssets[]) apkAssets.toArray(new ApkAssets[0]), true);
    }

    private void invalidateCachesLocked(int diff) {
    }

    public ApkAssets[] getApkAssets() {
        synchronized (this) {
            if (this.mOpen) {
                return this.mApkAssets;
            }
            return sEmptyApkAssets;
        }
    }

    public String[] getApkPaths() {
        synchronized (this) {
            if (this.mOpen) {
                ApkAssets[] apkAssetsArr = this.mApkAssets;
                String[] paths = new String[apkAssetsArr.length];
                int count = apkAssetsArr.length;
                for (int i = 0; i < count; i++) {
                    paths[i] = this.mApkAssets[i].getAssetPath();
                }
                return paths;
            }
            return new String[0];
        }
    }

    public int findCookieForPath(String path) {
        Objects.requireNonNull(path, "path");
        synchronized (this) {
            ensureValidLocked();
            int count = this.mApkAssets.length;
            for (int i = 0; i < count; i++) {
                if (path.equals(this.mApkAssets[i].getAssetPath())) {
                    return i + 1;
                }
            }
            return 0;
        }
    }

    @Deprecated
    public int addAssetPath(String path) {
        return addAssetPathInternal(path, false, false);
    }

    @Deprecated
    public int addAssetPathAsSharedLibrary(String path) {
        return addAssetPathInternal(path, false, true);
    }

    @Deprecated
    public int addOverlayPath(String path) {
        return addAssetPathInternal(path, true, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9 */
    private int addAssetPathInternal(String path, boolean overlay, boolean appAsLib) {
        Objects.requireNonNull(path, "path");
        synchronized (this) {
            ensureOpenLocked();
            int count = this.mApkAssets.length;
            for (int i = 0; i < count; i++) {
                if (this.mApkAssets[i].getAssetPath().equals(path)) {
                    return i + 1;
                }
            }
            int i2 = 0;
            try {
                if (overlay) {
                    String idmapPath = "/data/resource-cache/" + path.substring(1).replace('/', '@') + "@idmap";
                    i2 = ApkAssets.loadOverlayFromPath(idmapPath, 0);
                } else {
                    i2 = ApkAssets.loadFromPath(path, appAsLib ? 2 : 0);
                }
                ApkAssets[] apkAssetsArr = (ApkAssets[]) Arrays.copyOf(this.mApkAssets, count + 1);
                this.mApkAssets = apkAssetsArr;
                apkAssetsArr[count] = i2;
                nativeSetApkAssets(this.mObject, apkAssetsArr, true);
                invalidateCachesLocked(-1);
                return count + 1;
            } catch (IOException e) {
                return i2;
            }
        }
    }

    public List<ResourcesLoader> getLoaders() {
        ResourcesLoader[] resourcesLoaderArr = this.mLoaders;
        return resourcesLoaderArr == null ? Collections.emptyList() : Arrays.asList(resourcesLoaderArr);
    }

    private void ensureValidLocked() {
        if (this.mObject == 0) {
            throw new RuntimeException("AssetManager has been destroyed");
        }
    }

    private void ensureOpenLocked() {
        if (!this.mOpen) {
            throw new RuntimeException("AssetManager has been closed");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getResourceValue(int resId, int densityDpi, TypedValue outValue, boolean resolveRefs) {
        Objects.requireNonNull(outValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int cookie = nativeGetResourceValue(this.mObject, resId, (short) densityDpi, outValue, resolveRefs);
            if (cookie <= 0) {
                return false;
            }
            outValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(outValue.changingConfigurations);
            if (outValue.type == 3) {
                CharSequence pooledStringForCookie = getPooledStringForCookie(cookie, outValue.data);
                outValue.string = pooledStringForCookie;
                if (pooledStringForCookie == null) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getResourceText(int resId) {
        synchronized (this) {
            TypedValue outValue = this.mValue;
            if (getResourceValue(resId, 0, outValue, true)) {
                return outValue.coerceToString();
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getResourceBagText(int resId, int bagEntryId) {
        synchronized (this) {
            ensureValidLocked();
            TypedValue outValue = this.mValue;
            int cookie = nativeGetResourceBagValue(this.mObject, resId, bagEntryId, outValue);
            if (cookie <= 0) {
                return null;
            }
            outValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(outValue.changingConfigurations);
            if (outValue.type == 3) {
                return getPooledStringForCookie(cookie, outValue.data);
            }
            return outValue.coerceToString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getResourceArraySize(int resId) {
        int nativeGetResourceArraySize;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceArraySize = nativeGetResourceArraySize(this.mObject, resId);
        }
        return nativeGetResourceArraySize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getResourceArray(int resId, int[] outData) {
        int nativeGetResourceArray;
        Objects.requireNonNull(outData, "outData");
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceArray = nativeGetResourceArray(this.mObject, resId, outData);
        }
        return nativeGetResourceArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String[] getResourceStringArray(int resId) {
        String[] nativeGetResourceStringArray;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceStringArray = nativeGetResourceStringArray(this.mObject, resId);
        }
        return nativeGetResourceStringArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence[] getResourceTextArray(int resId) {
        CharSequence charSequence;
        synchronized (this) {
            ensureValidLocked();
            int[] rawInfoArray = nativeGetResourceStringArrayInfo(this.mObject, resId);
            if (rawInfoArray == null) {
                return null;
            }
            int rawInfoArrayLen = rawInfoArray.length;
            int infoArrayLen = rawInfoArrayLen / 2;
            CharSequence[] retArray = new CharSequence[infoArrayLen];
            int i = 0;
            int j = 0;
            while (i < rawInfoArrayLen) {
                int cookie = rawInfoArray[i];
                int index = rawInfoArray[i + 1];
                if (index < 0 || cookie <= 0) {
                    charSequence = null;
                } else {
                    charSequence = getPooledStringForCookie(cookie, index);
                }
                retArray[j] = charSequence;
                i += 2;
                j++;
            }
            return retArray;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getResourceIntArray(int resId) {
        int[] nativeGetResourceIntArray;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceIntArray = nativeGetResourceIntArray(this.mObject, resId);
        }
        return nativeGetResourceIntArray;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getStyleAttributes(int resId) {
        int[] nativeGetStyleAttributes;
        synchronized (this) {
            ensureValidLocked();
            nativeGetStyleAttributes = nativeGetStyleAttributes(this.mObject, resId);
        }
        return nativeGetStyleAttributes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getThemeValue(long theme, int resId, TypedValue outValue, boolean resolveRefs) {
        Objects.requireNonNull(outValue, "outValue");
        synchronized (this) {
            ensureValidLocked();
            int cookie = nativeThemeGetAttributeValue(this.mObject, theme, resId, outValue, resolveRefs);
            if (cookie <= 0) {
                return false;
            }
            outValue.changingConfigurations = ActivityInfo.activityInfoConfigNativeToJava(outValue.changingConfigurations);
            if (outValue.type == 3) {
                CharSequence pooledStringForCookie = getPooledStringForCookie(cookie, outValue.data);
                outValue.string = pooledStringForCookie;
                if (pooledStringForCookie == null) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpTheme(long theme, int priority, String tag, String prefix) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeDump(this.mObject, theme, priority, tag, prefix);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getResourceName(int resId) {
        String nativeGetResourceName;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceName = nativeGetResourceName(this.mObject, resId);
        }
        return nativeGetResourceName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getResourcePackageName(int resId) {
        String nativeGetResourcePackageName;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourcePackageName = nativeGetResourcePackageName(this.mObject, resId);
        }
        return nativeGetResourcePackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getResourceTypeName(int resId) {
        String nativeGetResourceTypeName;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceTypeName = nativeGetResourceTypeName(this.mObject, resId);
        }
        return nativeGetResourceTypeName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getResourceEntryName(int resId) {
        String nativeGetResourceEntryName;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceEntryName = nativeGetResourceEntryName(this.mObject, resId);
        }
        return nativeGetResourceEntryName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getResourceIdentifier(String name, String defType, String defPackage) {
        int nativeGetResourceIdentifier;
        synchronized (this) {
            ensureValidLocked();
            nativeGetResourceIdentifier = nativeGetResourceIdentifier(this.mObject, name, defType, defPackage);
        }
        return nativeGetResourceIdentifier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getParentThemeIdentifier(int resId) {
        int nativeGetParentThemeIdentifier;
        synchronized (this) {
            ensureValidLocked();
            nativeGetParentThemeIdentifier = nativeGetParentThemeIdentifier(this.mObject, resId);
        }
        return nativeGetParentThemeIdentifier;
    }

    public void setResourceResolutionLoggingEnabled(boolean enabled) {
        synchronized (this) {
            ensureValidLocked();
            nativeSetResourceResolutionLoggingEnabled(this.mObject, enabled);
        }
    }

    public String getLastResourceResolution() {
        String nativeGetLastResourceResolution;
        synchronized (this) {
            ensureValidLocked();
            nativeGetLastResourceResolution = nativeGetLastResourceResolution(this.mObject);
        }
        return nativeGetLastResourceResolution;
    }

    public boolean containsAllocatedTable() {
        boolean nativeContainsAllocatedTable;
        synchronized (this) {
            ensureValidLocked();
            nativeContainsAllocatedTable = nativeContainsAllocatedTable(this.mObject);
        }
        return nativeContainsAllocatedTable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getPooledStringForCookie(int cookie, int id) {
        return getApkAssets()[cookie - 1].getStringFromPool(id);
    }

    public InputStream open(String fileName) throws IOException {
        return open(fileName, 2);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        AssetInputStream assetInputStream;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long asset = nativeOpenAsset(this.mObject, fileName, accessMode);
            if (asset == 0) {
                throw new FileNotFoundException("Asset file: " + fileName);
            }
            assetInputStream = new AssetInputStream(asset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public AssetFileDescriptor openFd(String fileName) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor pfd = nativeOpenAssetFd(this.mObject, fileName, this.mOffsets);
            if (pfd == null) {
                throw new FileNotFoundException("Asset file: " + fileName);
            }
            long[] jArr = this.mOffsets;
            assetFileDescriptor = new AssetFileDescriptor(pfd, jArr[0], jArr[1]);
        }
        return assetFileDescriptor;
    }

    public String[] list(String path) throws IOException {
        String[] nativeList;
        Objects.requireNonNull(path, "path");
        synchronized (this) {
            ensureValidLocked();
            nativeList = nativeList(this.mObject, path);
        }
        return nativeList;
    }

    public InputStream openNonAsset(String fileName) throws IOException {
        return openNonAsset(0, fileName, 2);
    }

    public InputStream openNonAsset(String fileName, int accessMode) throws IOException {
        return openNonAsset(0, fileName, accessMode);
    }

    public InputStream openNonAsset(int cookie, String fileName) throws IOException {
        return openNonAsset(cookie, fileName, 2);
    }

    public InputStream openNonAsset(int cookie, String fileName, int accessMode) throws IOException {
        AssetInputStream assetInputStream;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long asset = nativeOpenNonAsset(this.mObject, cookie, fileName, accessMode);
            if (asset == 0) {
                throw new FileNotFoundException("Asset absolute file: " + fileName);
            }
            assetInputStream = new AssetInputStream(asset);
            incRefsLocked(assetInputStream.hashCode());
        }
        return assetInputStream;
    }

    public AssetFileDescriptor openNonAssetFd(String fileName) throws IOException {
        return openNonAssetFd(0, fileName);
    }

    public AssetFileDescriptor openNonAssetFd(int cookie, String fileName) throws IOException {
        AssetFileDescriptor assetFileDescriptor;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            ParcelFileDescriptor pfd = nativeOpenNonAssetFd(this.mObject, cookie, fileName, this.mOffsets);
            if (pfd == null) {
                throw new FileNotFoundException("Asset absolute file: " + fileName);
            }
            long[] jArr = this.mOffsets;
            assetFileDescriptor = new AssetFileDescriptor(pfd, jArr[0], jArr[1]);
        }
        return assetFileDescriptor;
    }

    public XmlResourceParser openXmlResourceParser(String fileName) throws IOException {
        return openXmlResourceParser(0, fileName);
    }

    public XmlResourceParser openXmlResourceParser(int cookie, String fileName) throws IOException {
        XmlBlock block = openXmlBlockAsset(cookie, fileName);
        try {
            XmlResourceParser parser = block.newParser();
            if (parser == null) {
                throw new AssertionError("block.newParser() returned a null parser");
            }
            if (block != null) {
                block.close();
            }
            return parser;
        } catch (Throwable th) {
            if (block != null) {
                try {
                    block.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    XmlBlock openXmlBlockAsset(String fileName) throws IOException {
        return openXmlBlockAsset(0, fileName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XmlBlock openXmlBlockAsset(int cookie, String fileName) throws IOException {
        XmlBlock block;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            ensureOpenLocked();
            long xmlBlock = nativeOpenXmlAsset(this.mObject, cookie, fileName);
            if (xmlBlock == 0) {
                throw new FileNotFoundException("Asset XML file: " + fileName);
            }
            block = new XmlBlock(this, xmlBlock);
            incRefsLocked(block.hashCode());
        }
        return block;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void xmlBlockGone(int id) {
        synchronized (this) {
            decRefsLocked(id);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyStyle(long themePtr, int defStyleAttr, int defStyleRes, XmlBlock.Parser parser, int[] inAttrs, long outValuesAddress, long outIndicesAddress) {
        Objects.requireNonNull(inAttrs, "inAttrs");
        synchronized (this) {
            ensureValidLocked();
            nativeApplyStyle(this.mObject, themePtr, defStyleAttr, defStyleRes, parser != null ? parser.mParseState : 0L, inAttrs, outValuesAddress, outIndicesAddress);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getAttributeResolutionStack(long themePtr, int defStyleAttr, int defStyleRes, int xmlStyle) {
        int[] nativeAttributeResolutionStack;
        synchronized (this) {
            nativeAttributeResolutionStack = nativeAttributeResolutionStack(this.mObject, themePtr, xmlStyle, defStyleAttr, defStyleRes);
        }
        return nativeAttributeResolutionStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resolveAttrs(long themePtr, int defStyleAttr, int defStyleRes, int[] inValues, int[] inAttrs, int[] outValues, int[] outIndices) {
        boolean nativeResolveAttrs;
        Objects.requireNonNull(inAttrs, "inAttrs");
        Objects.requireNonNull(outValues, "outValues");
        Objects.requireNonNull(outIndices, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            nativeResolveAttrs = nativeResolveAttrs(this.mObject, themePtr, defStyleAttr, defStyleRes, inValues, inAttrs, outValues, outIndices);
        }
        return nativeResolveAttrs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean retrieveAttributes(XmlBlock.Parser parser, int[] inAttrs, int[] outValues, int[] outIndices) {
        boolean nativeRetrieveAttributes;
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(inAttrs, "inAttrs");
        Objects.requireNonNull(outValues, "outValues");
        Objects.requireNonNull(outIndices, "outIndices");
        synchronized (this) {
            ensureValidLocked();
            nativeRetrieveAttributes = nativeRetrieveAttributes(this.mObject, parser.mParseState, inAttrs, outValues, outIndices);
        }
        return nativeRetrieveAttributes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long createTheme() {
        long themePtr;
        synchronized (this) {
            ensureValidLocked();
            themePtr = nativeThemeCreate(this.mObject);
            incRefsLocked(themePtr);
        }
        return themePtr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseTheme(long themePtr) {
        synchronized (this) {
            decRefsLocked(themePtr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getThemeFreeFunction() {
        return nativeGetThemeFreeFunction();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyStyleToTheme(long themePtr, int resId, boolean force) {
        synchronized (this) {
            ensureValidLocked();
            nativeThemeApplyStyle(this.mObject, themePtr, resId, force);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AssetManager rebaseTheme(long themePtr, AssetManager newAssetManager, int[] styleIds, boolean[] force, int count) {
        if (this != newAssetManager) {
            synchronized (this) {
                ensureValidLocked();
                decRefsLocked(themePtr);
            }
            synchronized (newAssetManager) {
                newAssetManager.ensureValidLocked();
                newAssetManager.incRefsLocked(themePtr);
            }
        }
        try {
            synchronized (newAssetManager) {
                newAssetManager.ensureValidLocked();
                nativeThemeRebase(newAssetManager.mObject, themePtr, styleIds, force, count);
            }
            return newAssetManager;
        } finally {
            Reference.reachabilityFence(newAssetManager);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setThemeTo(long dstThemePtr, AssetManager srcAssetManager, long srcThemePtr) {
        synchronized (this) {
            ensureValidLocked();
            synchronized (srcAssetManager) {
                srcAssetManager.ensureValidLocked();
                nativeThemeCopy(this.mObject, dstThemePtr, srcAssetManager.mObject, srcThemePtr);
            }
        }
    }

    protected void finalize() throws Throwable {
        synchronized (this) {
            long j = this.mObject;
            if (j != 0) {
                nativeDestroy(j);
                this.mObject = 0L;
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AssetInputStream extends InputStream {
        private long mAssetNativePtr;
        private long mLength;
        private long mMarkPos;

        public final int getAssetInt() {
            throw new UnsupportedOperationException();
        }

        public final long getNativeAsset() {
            return this.mAssetNativePtr;
        }

        private AssetInputStream(long assetNativePtr) {
            this.mAssetNativePtr = assetNativePtr;
            this.mLength = AssetManager.nativeAssetGetLength(assetNativePtr);
        }

        @Override // java.io.InputStream
        public final int read() throws IOException {
            ensureOpen();
            return AssetManager.nativeAssetReadChar(this.mAssetNativePtr);
        }

        @Override // java.io.InputStream
        public final int read(byte[] b) throws IOException {
            ensureOpen();
            Objects.requireNonNull(b, XmlTags.TAG_BLOB);
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, b, 0, b.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] b, int off, int len) throws IOException {
            ensureOpen();
            Objects.requireNonNull(b, XmlTags.TAG_BLOB);
            return AssetManager.nativeAssetRead(this.mAssetNativePtr, b, off, len);
        }

        @Override // java.io.InputStream
        public final long skip(long n) throws IOException {
            ensureOpen();
            long pos = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
            long j = this.mLength;
            if (pos + n > j) {
                n = j - pos;
            }
            if (n > 0) {
                AssetManager.nativeAssetSeek(this.mAssetNativePtr, n, 0);
            }
            return n;
        }

        @Override // java.io.InputStream
        public final int available() throws IOException {
            ensureOpen();
            long len = AssetManager.nativeAssetGetRemainingLength(this.mAssetNativePtr);
            if (len > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) len;
        }

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public final void mark(int readlimit) {
            ensureOpen();
            this.mMarkPos = AssetManager.nativeAssetSeek(this.mAssetNativePtr, 0L, 0);
        }

        @Override // java.io.InputStream
        public final void reset() throws IOException {
            ensureOpen();
            AssetManager.nativeAssetSeek(this.mAssetNativePtr, this.mMarkPos, -1);
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public final void close() throws IOException {
            long j = this.mAssetNativePtr;
            if (j != 0) {
                AssetManager.nativeAssetDestroy(j);
                this.mAssetNativePtr = 0L;
                synchronized (AssetManager.this) {
                    AssetManager.this.decRefsLocked(hashCode());
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        private void ensureOpen() {
            if (this.mAssetNativePtr == 0) {
                throw new IllegalStateException("AssetInputStream is closed");
            }
        }
    }

    public boolean isUpToDate() {
        ApkAssets[] apkAssetsArr;
        synchronized (this) {
            if (this.mOpen) {
                for (ApkAssets apkAssets : this.mApkAssets) {
                    if (!apkAssets.isUpToDate()) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    public String[] getLocales() {
        String[] nativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            nativeGetLocales = nativeGetLocales(this.mObject, false);
        }
        return nativeGetLocales;
    }

    public String[] getNonSystemLocales() {
        String[] nativeGetLocales;
        synchronized (this) {
            ensureValidLocked();
            nativeGetLocales = nativeGetLocales(this.mObject, true);
        }
        return nativeGetLocales;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration[] getSizeConfigurations() {
        Configuration[] nativeGetSizeConfigurations;
        synchronized (this) {
            ensureValidLocked();
            nativeGetSizeConfigurations = nativeGetSizeConfigurations(this.mObject);
        }
        return nativeGetSizeConfigurations;
    }

    public void setConfiguration(int mcc, int mnc, String locale, int orientation, int touchscreen, int density, int keyboard, int keyboardHidden, int navigation, int screenWidth, int screenHeight, int smallestScreenWidthDp, int screenWidthDp, int screenHeightDp, int screenLayout, int uiMode, int colorMode, int grammaticalGender, int majorVersion) {
        synchronized (this) {
            ensureValidLocked();
            nativeSetConfiguration(this.mObject, mcc, mnc, locale, orientation, touchscreen, density, keyboard, keyboardHidden, navigation, screenWidth, screenHeight, smallestScreenWidthDp, screenWidthDp, screenHeightDp, screenLayout, uiMode, colorMode, grammaticalGender, majorVersion);
        }
    }

    public SparseArray<String> getAssignedPackageIdentifiers() {
        return getAssignedPackageIdentifiers(true, true);
    }

    public SparseArray<String> getAssignedPackageIdentifiers(boolean includeOverlays, boolean includeLoaders) {
        SparseArray<String> nativeGetAssignedPackageIdentifiers;
        synchronized (this) {
            ensureValidLocked();
            nativeGetAssignedPackageIdentifiers = nativeGetAssignedPackageIdentifiers(this.mObject, includeOverlays, includeLoaders);
        }
        return nativeGetAssignedPackageIdentifiers;
    }

    public Map<String, String> getOverlayableMap(String packageName) {
        Map<String, String> nativeGetOverlayableMap;
        synchronized (this) {
            ensureValidLocked();
            nativeGetOverlayableMap = nativeGetOverlayableMap(this.mObject, packageName);
        }
        return nativeGetOverlayableMap;
    }

    public String getOverlayablesToString(String packageName) {
        String nativeGetOverlayablesToString;
        synchronized (this) {
            ensureValidLocked();
            nativeGetOverlayablesToString = nativeGetOverlayablesToString(this.mObject, packageName);
        }
        return nativeGetOverlayablesToString;
    }

    private void incRefsLocked(long id) {
        this.mNumRefs++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decRefsLocked(long id) {
        int i = this.mNumRefs - 1;
        this.mNumRefs = i;
        if (i == 0) {
            long j = this.mObject;
            if (j != 0) {
                nativeDestroy(j);
                this.mObject = 0L;
                this.mApkAssets = sEmptyApkAssets;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "class=" + getClass());
        pw.println(prefix + "apkAssets=");
        for (int i = 0; i < this.mApkAssets.length; i++) {
            pw.println(prefix + i);
            this.mApkAssets[i].dump(pw, prefix + "  ");
        }
    }
}
