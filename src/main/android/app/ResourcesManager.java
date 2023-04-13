package android.app;

import android.app.ResourcesManager;
import android.content.p001pm.ApplicationInfo;
import android.content.res.ApkAssets;
import android.content.res.AssetManager;
import android.content.res.CompatResources;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ResourcesImpl;
import android.content.res.ResourcesKey;
import android.content.res.loader.ResourcesLoader;
import android.hardware.display.DisplayManagerGlobal;
import android.p008os.IBinder;
import android.p008os.Process;
import android.p008os.Trace;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.DisplayInfo;
import com.android.internal.util.ArrayUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class ResourcesManager {
    private static final boolean DEBUG = false;
    static final String TAG = "ResourcesManager";
    private static ResourcesManager sResourcesManager;
    private ArrayList<Pair<String[], ApplicationInfo>> mPendingAppInfoUpdates;
    private CompatibilityInfo mResCompatibilityInfo;
    private final Object mLock = new Object();
    private final Configuration mResConfiguration = new Configuration();
    private int mResDisplayId = 0;
    private final ArrayMap<ResourcesKey, WeakReference<ResourcesImpl>> mResourceImpls = new ArrayMap<>();
    private final ArrayList<WeakReference<Resources>> mResourceReferences = new ArrayList<>();
    private final ReferenceQueue<Resources> mResourcesReferencesQueue = new ReferenceQueue<>();
    private final ArrayMap<ApkKey, WeakReference<ApkAssets>> mCachedApkAssets = new ArrayMap<>();
    private final WeakHashMap<IBinder, ActivityResources> mActivityResourceReferences = new WeakHashMap<>();
    private final UpdateHandler mUpdateCallbacks = new UpdateHandler();
    private final ArraySet<String> mApplicationOwnedApks = new ArraySet<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ApkKey {
        public final boolean overlay;
        public final String path;
        public final boolean sharedLib;

        ApkKey(String path, boolean sharedLib, boolean overlay) {
            this.path = path;
            this.sharedLib = sharedLib;
            this.overlay = overlay;
        }

        public int hashCode() {
            int result = (1 * 31) + this.path.hashCode();
            return (((result * 31) + Boolean.hashCode(this.sharedLib)) * 31) + Boolean.hashCode(this.overlay);
        }

        public boolean equals(Object obj) {
            if (obj instanceof ApkKey) {
                ApkKey other = (ApkKey) obj;
                return this.path.equals(other.path) && this.sharedLib == other.sharedLib && this.overlay == other.overlay;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ApkAssetsSupplier {
        final ArrayMap<ApkKey, ApkAssets> mLocalCache;

        private ApkAssetsSupplier() {
            this.mLocalCache = new ArrayMap<>();
        }

        ApkAssets load(ApkKey apkKey) throws IOException {
            ApkAssets apkAssets = this.mLocalCache.get(apkKey);
            if (apkAssets == null) {
                ApkAssets apkAssets2 = ResourcesManager.this.loadApkAssets(apkKey);
                this.mLocalCache.put(apkKey, apkAssets2);
                return apkAssets2;
            }
            return apkAssets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActivityResources {
        public final ArrayList<ActivityResource> activityResources;
        public final ReferenceQueue<Resources> activityResourcesQueue;
        public final Configuration overrideConfig;
        public int overrideDisplayId;

        private ActivityResources() {
            this.overrideConfig = new Configuration();
            this.activityResources = new ArrayList<>();
            this.activityResourcesQueue = new ReferenceQueue<>();
        }

        public int countLiveReferences() {
            int count = 0;
            for (int i = 0; i < this.activityResources.size(); i++) {
                WeakReference<Resources> resources = this.activityResources.get(i).resources;
                if (resources != null && resources.get() != null) {
                    count++;
                }
            }
            return count;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActivityResource {
        public final Configuration overrideConfig;
        public Integer overrideDisplayId;
        public WeakReference<Resources> resources;

        private ActivityResource() {
            this.overrideConfig = new Configuration();
        }
    }

    public static ResourcesManager getInstance() {
        ResourcesManager resourcesManager;
        synchronized (ResourcesManager.class) {
            if (sResourcesManager == null) {
                sResourcesManager = new ResourcesManager();
            }
            resourcesManager = sResourcesManager;
        }
        return resourcesManager;
    }

    public void invalidatePath(String path) {
        ResourcesImpl resImpl;
        List<ResourcesImpl> implsToFlush = new ArrayList<>();
        synchronized (this.mLock) {
            for (int i = this.mResourceImpls.size() - 1; i >= 0; i--) {
                ResourcesKey key = this.mResourceImpls.keyAt(i);
                if (key.isPathReferenced(path) && (resImpl = this.mResourceImpls.removeAt(i).get()) != null) {
                    implsToFlush.add(resImpl);
                }
            }
        }
        for (int i2 = 0; i2 < implsToFlush.size(); i2++) {
            implsToFlush.get(i2).flushLayoutCache();
        }
        List<ApkAssets> assetsToClose = new ArrayList<>();
        synchronized (this.mCachedApkAssets) {
            for (int i3 = this.mCachedApkAssets.size() - 1; i3 >= 0; i3--) {
                ApkKey key2 = this.mCachedApkAssets.keyAt(i3);
                if (key2.path.equals(path)) {
                    WeakReference<ApkAssets> apkAssetsRef = this.mCachedApkAssets.removeAt(i3);
                    ApkAssets apkAssets = apkAssetsRef != null ? apkAssetsRef.get() : null;
                    if (apkAssets != null) {
                        assetsToClose.add(apkAssets);
                    }
                }
            }
        }
        for (int i4 = 0; i4 < assetsToClose.size(); i4++) {
            assetsToClose.get(i4).close();
        }
        Log.m108i(TAG, "Invalidated " + implsToFlush.size() + " asset managers that referenced " + path);
    }

    public Configuration getConfiguration() {
        return this.mResConfiguration;
    }

    public DisplayMetrics getDisplayMetrics() {
        return getDisplayMetrics(this.mResDisplayId, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    protected DisplayMetrics getDisplayMetrics(int displayId, DisplayAdjustments da) {
        DisplayManagerGlobal displayManagerGlobal = DisplayManagerGlobal.getInstance();
        DisplayMetrics dm = new DisplayMetrics();
        DisplayInfo displayInfo = displayManagerGlobal != null ? displayManagerGlobal.getDisplayInfo(displayId) : null;
        if (displayInfo != null) {
            displayInfo.getAppMetrics(dm, da);
        } else {
            dm.setToDefaults();
        }
        return dm;
    }

    private DisplayMetrics getDisplayMetrics(Configuration config) {
        DisplayManagerGlobal displayManagerGlobal = DisplayManagerGlobal.getInstance();
        DisplayMetrics dm = new DisplayMetrics();
        DisplayInfo displayInfo = displayManagerGlobal != null ? displayManagerGlobal.getDisplayInfo(this.mResDisplayId) : null;
        if (displayInfo != null) {
            displayInfo.getAppMetrics(dm, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS.getCompatibilityInfo(), config);
        } else {
            dm.setToDefaults();
        }
        return dm;
    }

    private static void applyDisplayMetricsToConfiguration(DisplayMetrics dm, Configuration config) {
        config.touchscreen = 1;
        config.densityDpi = dm.densityDpi;
        config.screenWidthDp = (int) ((dm.widthPixels / dm.density) + 0.5f);
        config.screenHeightDp = (int) ((dm.heightPixels / dm.density) + 0.5f);
        int sl = Configuration.resetScreenLayout(config.screenLayout);
        if (dm.widthPixels > dm.heightPixels) {
            config.orientation = 2;
            config.screenLayout = Configuration.reduceScreenLayout(sl, config.screenWidthDp, config.screenHeightDp);
        } else {
            config.orientation = 1;
            config.screenLayout = Configuration.reduceScreenLayout(sl, config.screenHeightDp, config.screenWidthDp);
        }
        config.smallestScreenWidthDp = Math.min(config.screenWidthDp, config.screenHeightDp);
        config.compatScreenWidthDp = config.screenWidthDp;
        config.compatScreenHeightDp = config.screenHeightDp;
        config.compatSmallestScreenWidthDp = config.smallestScreenWidthDp;
    }

    public boolean applyCompatConfiguration(int displayDensity, Configuration compatConfiguration) {
        synchronized (this.mLock) {
            CompatibilityInfo compatibilityInfo = this.mResCompatibilityInfo;
            if (compatibilityInfo == null || compatibilityInfo.supportsScreen()) {
                return false;
            }
            this.mResCompatibilityInfo.applyToConfiguration(displayDensity, compatConfiguration);
            return true;
        }
    }

    public Display getAdjustedDisplay(int displayId, Resources resources) {
        DisplayManagerGlobal dm = DisplayManagerGlobal.getInstance();
        if (dm == null) {
            return null;
        }
        return dm.getCompatibleDisplay(displayId, resources);
    }

    public void initializeApplicationPaths(String sourceDir, String[] splitDirs) {
        synchronized (this.mLock) {
            if (this.mApplicationOwnedApks.isEmpty()) {
                addApplicationPathsLocked(sourceDir, splitDirs);
            }
        }
    }

    private void addApplicationPathsLocked(String sourceDir, String[] splitDirs) {
        this.mApplicationOwnedApks.add(sourceDir);
        if (splitDirs != null) {
            this.mApplicationOwnedApks.addAll(Arrays.asList(splitDirs));
        }
    }

    private static String overlayPathToIdmapPath(String path) {
        return "/data/resource-cache/" + path.substring(1).replace('/', '@') + "@idmap";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ApkAssets loadApkAssets(ApkKey key) throws IOException {
        WeakReference<ApkAssets> apkAssetsRef;
        int flags;
        ApkAssets apkAssets;
        ApkAssets apkAssets2;
        synchronized (this.mCachedApkAssets) {
            apkAssetsRef = this.mCachedApkAssets.get(key);
        }
        if (apkAssetsRef != null && (apkAssets2 = apkAssetsRef.get()) != null && apkAssets2.isUpToDate()) {
            return apkAssets2;
        }
        int flags2 = 0;
        if (key.sharedLib) {
            flags2 = 0 | 2;
        }
        if (!this.mApplicationOwnedApks.contains(key.path)) {
            flags = flags2;
        } else {
            flags = flags2 | 16;
        }
        if (key.overlay) {
            apkAssets = ApkAssets.loadOverlayFromPath(overlayPathToIdmapPath(key.path), flags);
        } else {
            apkAssets = ApkAssets.loadFromPath(key.path, flags);
        }
        synchronized (this.mCachedApkAssets) {
            this.mCachedApkAssets.put(key, new WeakReference<>(apkAssets));
        }
        return apkAssets;
    }

    private static ArrayList<ApkKey> extractApkKeys(ResourcesKey key) {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        ArrayList<ApkKey> apkKeys = new ArrayList<>();
        if (key.mResDir != null) {
            apkKeys.add(new ApkKey(key.mResDir, false, false));
        }
        if (key.mSplitResDirs != null) {
            for (String splitResDir : key.mSplitResDirs) {
                apkKeys.add(new ApkKey(splitResDir, false, false));
            }
        }
        if (key.mLibDirs != null) {
            for (String libDir : key.mLibDirs) {
                if (libDir.endsWith(".apk")) {
                    apkKeys.add(new ApkKey(libDir, true, false));
                }
            }
        }
        if (key.mOverlayPaths != null) {
            for (String idmapPath : key.mOverlayPaths) {
                apkKeys.add(new ApkKey(idmapPath, false, true));
            }
        }
        return apkKeys;
    }

    protected AssetManager createAssetManager(ResourcesKey key) {
        return createAssetManager(key, null);
    }

    private AssetManager createAssetManager(ResourcesKey key, ApkAssetsSupplier apkSupplier) {
        ResourcesLoader[] resourcesLoaderArr;
        ApkAssets load;
        AssetManager.Builder builder = new AssetManager.Builder();
        ArrayList<ApkKey> apkKeys = extractApkKeys(key);
        int n = apkKeys.size();
        for (int i = 0; i < n; i++) {
            ApkKey apkKey = apkKeys.get(i);
            if (apkSupplier != null) {
                try {
                    load = apkSupplier.load(apkKey);
                } catch (IOException e) {
                    if (apkKey.overlay) {
                        Log.m103w(TAG, String.format("failed to add overlay path '%s'", apkKey.path), e);
                    } else if (apkKey.sharedLib) {
                        Log.m103w(TAG, String.format("asset path '%s' does not exist or contains no resources", apkKey.path), e);
                    } else {
                        Log.m109e(TAG, String.format("failed to add asset path '%s'", apkKey.path), e);
                        return null;
                    }
                }
            } else {
                load = loadApkAssets(apkKey);
            }
            builder.addApkAssets(load);
        }
        if (key.mLoaders != null) {
            for (ResourcesLoader loader : key.mLoaders) {
                builder.addLoader(loader);
            }
        }
        return builder.build();
    }

    private static <T> int countLiveReferences(Collection<WeakReference<T>> collection) {
        int count = 0;
        Iterator<WeakReference<T>> it = collection.iterator();
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            T value = ref != null ? ref.get() : null;
            if (value != null) {
                count++;
            }
        }
        return count;
    }

    public void dump(String prefix, PrintWriter printWriter) {
        int references;
        int resImpls;
        int liveAssets;
        synchronized (this.mLock) {
            int refs = countLiveReferences(this.mResourceReferences);
            for (ActivityResources activityResources : this.mActivityResourceReferences.values()) {
                refs += activityResources.countLiveReferences();
            }
            references = refs;
            resImpls = countLiveReferences(this.mResourceImpls.values());
        }
        synchronized (this.mCachedApkAssets) {
            liveAssets = countLiveReferences(this.mCachedApkAssets.values());
        }
        IndentingPrintWriter pw = new IndentingPrintWriter(printWriter, "  ");
        for (int i = 0; i < prefix.length() / 2; i++) {
            pw.increaseIndent();
        }
        pw.println("ResourcesManager:");
        pw.increaseIndent();
        pw.print("total apks: ");
        pw.println(liveAssets);
        pw.print("resources: ");
        pw.println(references);
        pw.print("resource impls: ");
        pw.println(resImpls);
    }

    private Configuration generateConfig(ResourcesKey key) {
        boolean hasOverrideConfig = key.hasOverrideConfiguration();
        if (hasOverrideConfig) {
            Configuration config = new Configuration(getConfiguration());
            config.updateFrom(key.mOverrideConfiguration);
            return config;
        }
        return getConfiguration();
    }

    private int generateDisplayId(ResourcesKey key) {
        return key.mDisplayId != -1 ? key.mDisplayId : this.mResDisplayId;
    }

    private ResourcesImpl createResourcesImpl(ResourcesKey key, ApkAssetsSupplier apkSupplier) {
        AssetManager assets = createAssetManager(key, apkSupplier);
        if (assets == null) {
            return null;
        }
        DisplayAdjustments daj = new DisplayAdjustments(key.mOverrideConfiguration);
        daj.setCompatibilityInfo(key.mCompatInfo);
        Configuration config = generateConfig(key);
        DisplayMetrics displayMetrics = getDisplayMetrics(generateDisplayId(key), daj);
        ResourcesImpl impl = new ResourcesImpl(assets, displayMetrics, config, daj);
        return impl;
    }

    private ResourcesImpl findResourcesImplForKeyLocked(ResourcesKey key) {
        WeakReference<ResourcesImpl> weakImplRef = this.mResourceImpls.get(key);
        ResourcesImpl impl = weakImplRef != null ? weakImplRef.get() : null;
        if (impl == null || !impl.getAssets().isUpToDate()) {
            return null;
        }
        return impl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ResourcesImpl findOrCreateResourcesImplForKeyLocked(ResourcesKey key) {
        return findOrCreateResourcesImplForKeyLocked(key, null);
    }

    private ResourcesImpl findOrCreateResourcesImplForKeyLocked(ResourcesKey key, ApkAssetsSupplier apkSupplier) {
        ResourcesImpl impl = findResourcesImplForKeyLocked(key);
        if (impl == null && (impl = createResourcesImpl(key, apkSupplier)) != null) {
            this.mResourceImpls.put(key, new WeakReference<>(impl));
        }
        return impl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ResourcesKey findKeyForResourceImplLocked(ResourcesImpl resourceImpl) {
        int refCount = this.mResourceImpls.size();
        for (int i = 0; i < refCount; i++) {
            WeakReference<ResourcesImpl> weakImplRef = this.mResourceImpls.valueAt(i);
            if (weakImplRef != null && weakImplRef.refersTo(resourceImpl)) {
                return this.mResourceImpls.keyAt(i);
            }
        }
        return null;
    }

    public boolean isSameResourcesOverrideConfig(IBinder activityToken, Configuration overrideConfig) {
        ActivityResources activityResources;
        synchronized (this.mLock) {
            if (activityToken == null) {
                activityResources = null;
            } else {
                try {
                    activityResources = this.mActivityResourceReferences.get(activityToken);
                } finally {
                }
            }
            boolean z = true;
            if (activityResources == null) {
                if (overrideConfig != null) {
                    z = false;
                }
                return z;
            }
            if (!Objects.equals(activityResources.overrideConfig, overrideConfig) && (overrideConfig == null || activityResources.overrideConfig == null || overrideConfig.diffPublicOnly(activityResources.overrideConfig) != 0)) {
                z = false;
            }
            return z;
        }
    }

    private ActivityResources getOrCreateActivityResourcesStructLocked(IBinder activityToken) {
        ActivityResources activityResources = this.mActivityResourceReferences.get(activityToken);
        if (activityResources == null) {
            ActivityResources activityResources2 = new ActivityResources();
            this.mActivityResourceReferences.put(activityToken, activityResources2);
            return activityResources2;
        }
        return activityResources;
    }

    private Resources findResourcesForActivityLocked(IBinder targetActivityToken, ResourcesKey targetKey, ClassLoader targetClassLoader) {
        ActivityResources activityResources = getOrCreateActivityResourcesStructLocked(targetActivityToken);
        int size = activityResources.activityResources.size();
        int index = 0;
        while (true) {
            if (index >= size) {
                return null;
            }
            ActivityResource activityResource = activityResources.activityResources.get(index);
            Resources resources = activityResource.resources.get();
            ResourcesKey key = resources != null ? findKeyForResourceImplLocked(resources.getImpl()) : null;
            if (key == null || !Objects.equals(resources.getClassLoader(), targetClassLoader) || !Objects.equals(key, targetKey)) {
                index++;
            } else {
                return resources;
            }
        }
    }

    private Resources createResourcesForActivityLocked(IBinder activityToken, Configuration initialOverrideConfig, Integer overrideDisplayId, ClassLoader classLoader, ResourcesImpl impl, CompatibilityInfo compatInfo) {
        ActivityResources activityResources = getOrCreateActivityResourcesStructLocked(activityToken);
        cleanupReferences(activityResources.activityResources, activityResources.activityResourcesQueue, new Function() { // from class: android.app.ResourcesManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                WeakReference weakReference;
                weakReference = ((ResourcesManager.ActivityResource) obj).resources;
                return weakReference;
            }
        });
        Resources resources = compatInfo.needsCompatResources() ? new CompatResources(classLoader) : new Resources(classLoader);
        resources.setImpl(impl);
        resources.setCallbacks(this.mUpdateCallbacks);
        ActivityResource activityResource = new ActivityResource();
        activityResource.resources = new WeakReference<>(resources, activityResources.activityResourcesQueue);
        activityResource.overrideConfig.setTo(initialOverrideConfig);
        activityResource.overrideDisplayId = overrideDisplayId;
        activityResources.activityResources.add(activityResource);
        return resources;
    }

    private Resources createResourcesLocked(ClassLoader classLoader, ResourcesImpl impl, CompatibilityInfo compatInfo) {
        cleanupReferences(this.mResourceReferences, this.mResourcesReferencesQueue);
        Resources resources = compatInfo.needsCompatResources() ? new CompatResources(classLoader) : new Resources(classLoader);
        resources.setImpl(impl);
        resources.setCallbacks(this.mUpdateCallbacks);
        this.mResourceReferences.add(new WeakReference<>(resources, this.mResourcesReferencesQueue));
        return resources;
    }

    public Resources createBaseTokenResources(IBinder token, String resDir, String[] splitResDirs, String[] legacyOverlayDirs, String[] overlayPaths, String[] libDirs, int displayId, Configuration overrideConfig, CompatibilityInfo compatInfo, ClassLoader classLoader, List<ResourcesLoader> loaders) {
        try {
            Trace.traceBegin(8192L, "ResourcesManager#createBaseActivityResources");
            ResourcesKey key = new ResourcesKey(resDir, splitResDirs, combinedOverlayPaths(legacyOverlayDirs, overlayPaths), libDirs, displayId, overrideConfig, compatInfo, loaders == null ? null : (ResourcesLoader[]) loaders.toArray(new ResourcesLoader[0]));
            ClassLoader classLoader2 = classLoader != null ? classLoader : ClassLoader.getSystemClassLoader();
            try {
                synchronized (this.mLock) {
                    try {
                        getOrCreateActivityResourcesStructLocked(token);
                    } catch (Throwable th) {
                        th = th;
                        Trace.traceEnd(8192L);
                        throw th;
                    }
                }
                updateResourcesForActivity(token, overrideConfig, displayId);
                synchronized (this.mLock) {
                    Resources resources = findResourcesForActivityLocked(token, key, classLoader2);
                    if (resources != null) {
                        Trace.traceEnd(8192L);
                        return resources;
                    }
                    Resources createResourcesForActivity = createResourcesForActivity(token, key, Configuration.EMPTY, null, classLoader2, null);
                    Trace.traceEnd(8192L);
                    return createResourcesForActivity;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private void rebaseKeyForActivity(IBinder activityToken, ResourcesKey key, boolean overridesActivityDisplay) {
        Configuration config;
        synchronized (this.mLock) {
            ActivityResources activityResources = getOrCreateActivityResourcesStructLocked(activityToken);
            if (key.mDisplayId == -1) {
                key.mDisplayId = activityResources.overrideDisplayId;
            }
            if (key.hasOverrideConfiguration()) {
                config = new Configuration(activityResources.overrideConfig);
                config.updateFrom(key.mOverrideConfiguration);
            } else {
                config = activityResources.overrideConfig;
            }
            if (overridesActivityDisplay && key.mOverrideConfiguration.windowConfiguration.getAppBounds() == null) {
                if (!key.hasOverrideConfiguration()) {
                    config = new Configuration(config);
                }
                config.windowConfiguration.setAppBounds(null);
            }
            key.mOverrideConfiguration.setTo(config);
        }
    }

    private void rebaseKeyForDisplay(ResourcesKey key, int overrideDisplay) {
        Configuration temp = new Configuration();
        DisplayAdjustments daj = new DisplayAdjustments(key.mOverrideConfiguration);
        daj.setCompatibilityInfo(key.mCompatInfo);
        DisplayMetrics dm = getDisplayMetrics(overrideDisplay, daj);
        applyDisplayMetricsToConfiguration(dm, temp);
        if (key.hasOverrideConfiguration()) {
            temp.updateFrom(key.mOverrideConfiguration);
        }
        key.mOverrideConfiguration.setTo(temp);
    }

    private static <T> void cleanupReferences(ArrayList<WeakReference<T>> references, ReferenceQueue<T> referenceQueue) {
        cleanupReferences(references, referenceQueue, Function.identity());
    }

    private static <C, T> void cleanupReferences(ArrayList<C> referenceContainers, ReferenceQueue<T> referenceQueue, final Function<C, WeakReference<T>> unwrappingFunction) {
        Reference<? extends T> enqueuedRef = referenceQueue.poll();
        if (enqueuedRef == null) {
            return;
        }
        final HashSet<Reference<? extends T>> deadReferences = new HashSet<>();
        while (enqueuedRef != null) {
            deadReferences.add(enqueuedRef);
            enqueuedRef = referenceQueue.poll();
        }
        ArrayUtils.unstableRemoveIf(referenceContainers, new Predicate() { // from class: android.app.ResourcesManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ResourcesManager.lambda$cleanupReferences$1(unwrappingFunction, deadReferences, obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$cleanupReferences$1(Function unwrappingFunction, HashSet deadReferences, Object refContainer) {
        WeakReference weakReference = (WeakReference) unwrappingFunction.apply(refContainer);
        return weakReference == null || deadReferences.contains(weakReference);
    }

    private ApkAssetsSupplier createApkAssetsSupplierNotLocked(ResourcesKey key) {
        Trace.traceBegin(8192L, "ResourcesManager#createApkAssetsSupplierNotLocked");
        try {
            ApkAssetsSupplier supplier = new ApkAssetsSupplier();
            ArrayList<ApkKey> apkKeys = extractApkKeys(key);
            int n = apkKeys.size();
            for (int i = 0; i < n; i++) {
                ApkKey apkKey = apkKeys.get(i);
                try {
                    supplier.load(apkKey);
                } catch (IOException e) {
                    Log.m103w(TAG, String.format("failed to preload asset path '%s'", apkKey.path), e);
                }
            }
            return supplier;
        } finally {
            Trace.traceEnd(8192L);
        }
    }

    private Resources createResources(ResourcesKey key, ClassLoader classLoader, ApkAssetsSupplier apkSupplier) {
        synchronized (this.mLock) {
            ResourcesImpl resourcesImpl = findOrCreateResourcesImplForKeyLocked(key, apkSupplier);
            if (resourcesImpl == null) {
                return null;
            }
            return createResourcesLocked(classLoader, resourcesImpl, key.mCompatInfo);
        }
    }

    private Resources createResourcesForActivity(IBinder activityToken, ResourcesKey key, Configuration initialOverrideConfig, Integer overrideDisplayId, ClassLoader classLoader, ApkAssetsSupplier apkSupplier) {
        synchronized (this.mLock) {
            ResourcesImpl resourcesImpl = findOrCreateResourcesImplForKeyLocked(key, apkSupplier);
            if (resourcesImpl == null) {
                return null;
            }
            return createResourcesForActivityLocked(activityToken, initialOverrideConfig, overrideDisplayId, classLoader, resourcesImpl, key.mCompatInfo);
        }
    }

    public Resources getResources(IBinder activityToken, String resDir, String[] splitResDirs, String[] legacyOverlayDirs, String[] overlayPaths, String[] libDirs, Integer overrideDisplayId, Configuration overrideConfig, CompatibilityInfo compatInfo, ClassLoader classLoader, List<ResourcesLoader> loaders) {
        Resources resources;
        try {
            Trace.traceBegin(8192L, "ResourcesManager#getResources");
            ResourcesKey key = new ResourcesKey(resDir, splitResDirs, combinedOverlayPaths(legacyOverlayDirs, overlayPaths), libDirs, overrideDisplayId != null ? overrideDisplayId.intValue() : -1, overrideConfig, compatInfo, loaders == null ? null : (ResourcesLoader[]) loaders.toArray(new ResourcesLoader[0]));
            ClassLoader classLoader2 = classLoader != null ? classLoader : ClassLoader.getSystemClassLoader();
            try {
                ApkAssetsSupplier assetsSupplier = createApkAssetsSupplierNotLocked(key);
                if (overrideDisplayId != null) {
                    rebaseKeyForDisplay(key, overrideDisplayId.intValue());
                }
                if (activityToken != null) {
                    Configuration initialOverrideConfig = new Configuration(key.mOverrideConfiguration);
                    rebaseKeyForActivity(activityToken, key, overrideDisplayId != null);
                    resources = createResourcesForActivity(activityToken, key, initialOverrideConfig, overrideDisplayId, classLoader2, assetsSupplier);
                } else {
                    resources = createResources(key, classLoader2, assetsSupplier);
                }
                Trace.traceEnd(8192L);
                return resources;
            } catch (Throwable th) {
                th = th;
                Trace.traceEnd(8192L);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateResourcesForActivity(IBinder activityToken, Configuration overrideConfig, int displayId) {
        ResourcesKey newKey;
        ResourcesImpl resourcesImpl;
        try {
            Trace.traceBegin(8192L, "ResourcesManager#updateResourcesForActivity");
            if (displayId == -1) {
                throw new IllegalArgumentException("displayId can not be INVALID_DISPLAY");
            }
            synchronized (this.mLock) {
                ActivityResources activityResources = getOrCreateActivityResourcesStructLocked(activityToken);
                boolean movedToDifferentDisplay = activityResources.overrideDisplayId != displayId;
                if (Objects.equals(activityResources.overrideConfig, overrideConfig) && !movedToDifferentDisplay) {
                    return;
                }
                new Configuration(activityResources.overrideConfig);
                if (overrideConfig != null) {
                    activityResources.overrideConfig.setTo(overrideConfig);
                } else {
                    activityResources.overrideConfig.unset();
                }
                activityResources.overrideDisplayId = displayId;
                applyAllPendingAppInfoUpdates();
                int refCount = activityResources.activityResources.size();
                for (int i = 0; i < refCount; i++) {
                    ActivityResource activityResource = activityResources.activityResources.get(i);
                    Resources resources = activityResource.resources.get();
                    if (resources != null && (newKey = rebaseActivityOverrideConfig(activityResource, overrideConfig, displayId)) != null && (resourcesImpl = findOrCreateResourcesImplForKeyLocked(newKey)) != null && resourcesImpl != resources.getImpl()) {
                        resources.setImpl(resourcesImpl);
                    }
                }
            }
        } finally {
            Trace.traceEnd(8192L);
        }
    }

    private ResourcesKey rebaseActivityOverrideConfig(ActivityResource activityResource, Configuration newOverrideConfig, int displayId) {
        Resources resources = activityResource.resources.get();
        if (resources != null) {
            ResourcesKey oldKey = findKeyForResourceImplLocked(resources.getImpl());
            if (oldKey == null) {
                Slog.m96e(TAG, "can't find ResourcesKey for resources impl=" + resources.getImpl());
                return null;
            }
            Configuration rebasedOverrideConfig = new Configuration();
            if (newOverrideConfig != null) {
                rebasedOverrideConfig.setTo(newOverrideConfig);
            }
            Integer overrideDisplayId = activityResource.overrideDisplayId;
            if (overrideDisplayId != null) {
                DisplayAdjustments displayAdjustments = new DisplayAdjustments(rebasedOverrideConfig);
                displayAdjustments.getConfiguration().setTo(activityResource.overrideConfig);
                displayAdjustments.setCompatibilityInfo(oldKey.mCompatInfo);
                DisplayMetrics dm = getDisplayMetrics(overrideDisplayId.intValue(), displayAdjustments);
                applyDisplayMetricsToConfiguration(dm, rebasedOverrideConfig);
            }
            boolean hasOverrideConfig = !activityResource.overrideConfig.equals(Configuration.EMPTY);
            if (hasOverrideConfig) {
                rebasedOverrideConfig.updateFrom(activityResource.overrideConfig);
            }
            if (activityResource.overrideDisplayId != null && activityResource.overrideConfig.windowConfiguration.getAppBounds() == null) {
                rebasedOverrideConfig.windowConfiguration.setAppBounds(null);
            }
            ResourcesKey newKey = new ResourcesKey(oldKey.mResDir, oldKey.mSplitResDirs, oldKey.mOverlayPaths, oldKey.mLibDirs, overrideDisplayId != null ? overrideDisplayId.intValue() : displayId, rebasedOverrideConfig, oldKey.mCompatInfo, oldKey.mLoaders);
            return newKey;
        }
        return null;
    }

    public void appendPendingAppInfoUpdate(String[] oldSourceDirs, ApplicationInfo appInfo) {
        synchronized (this.mLock) {
            if (this.mPendingAppInfoUpdates == null) {
                this.mPendingAppInfoUpdates = new ArrayList<>();
            }
            for (int i = this.mPendingAppInfoUpdates.size() - 1; i >= 0; i--) {
                if (ArrayUtils.containsAll(oldSourceDirs, this.mPendingAppInfoUpdates.get(i).first)) {
                    this.mPendingAppInfoUpdates.remove(i);
                }
            }
            this.mPendingAppInfoUpdates.add(new Pair<>(oldSourceDirs, appInfo));
        }
    }

    public final void applyAllPendingAppInfoUpdates() {
        synchronized (this.mLock) {
            ArrayList<Pair<String[], ApplicationInfo>> arrayList = this.mPendingAppInfoUpdates;
            if (arrayList != null) {
                int n = arrayList.size();
                for (int i = 0; i < n; i++) {
                    Pair<String[], ApplicationInfo> appInfo = this.mPendingAppInfoUpdates.get(i);
                    applyNewResourceDirsLocked(appInfo.first, appInfo.second);
                }
                this.mPendingAppInfoUpdates = null;
            }
        }
    }

    public final boolean applyConfigurationToResources(Configuration config, CompatibilityInfo compat) {
        int i;
        CompatibilityInfo compatibilityInfo;
        synchronized (this.mLock) {
            Trace.traceBegin(8192L, "ResourcesManager#applyConfigurationToResources");
            if (this.mResConfiguration.isOtherSeqNewer(config) || compat != null) {
                int changes = this.mResConfiguration.updateFrom(config);
                if (compat != null && ((compatibilityInfo = this.mResCompatibilityInfo) == null || !compatibilityInfo.equals(compat))) {
                    this.mResCompatibilityInfo = compat;
                    changes |= 3328;
                }
                if ((Integer.MIN_VALUE & changes) != 0) {
                    applyAllPendingAppInfoUpdates();
                }
                DisplayMetrics displayMetrics = getDisplayMetrics(config);
                Resources.updateSystemConfiguration(config, displayMetrics, compat);
                ApplicationPackageManager.configurationChanged();
                Configuration tmpConfig = new Configuration();
                int i2 = this.mResourceImpls.size() - 1;
                while (i2 >= 0) {
                    ResourcesKey key = this.mResourceImpls.keyAt(i2);
                    WeakReference<ResourcesImpl> weakImplRef = this.mResourceImpls.valueAt(i2);
                    ResourcesImpl r = weakImplRef != null ? weakImplRef.get() : null;
                    if (r != null) {
                        i = i2;
                        applyConfigurationToResourcesLocked(config, compat, tmpConfig, key, r);
                    } else {
                        i = i2;
                        this.mResourceImpls.removeAt(i);
                    }
                    i2 = i - 1;
                }
                boolean z = changes != 0;
                Trace.traceEnd(8192L);
                return z;
            }
            Trace.traceEnd(8192L);
            return false;
        }
    }

    private void applyConfigurationToResourcesLocked(Configuration config, CompatibilityInfo compat, Configuration tmpConfig, ResourcesKey key, ResourcesImpl resourcesImpl) {
        tmpConfig.setTo(config);
        if (key.hasOverrideConfiguration()) {
            tmpConfig.updateFrom(key.mOverrideConfiguration);
        }
        DisplayAdjustments daj = resourcesImpl.getDisplayAdjustments();
        if (compat != null) {
            daj = new DisplayAdjustments(daj);
            daj.setCompatibilityInfo(compat);
        }
        daj.setConfiguration(tmpConfig);
        DisplayMetrics dm = getDisplayMetrics(generateDisplayId(key), daj);
        resourcesImpl.updateConfiguration(tmpConfig, dm, compat);
    }

    public void appendLibAssetForMainAssetPath(String assetPath, String libAsset) {
        appendLibAssetsForMainAssetPath(assetPath, new String[]{libAsset});
    }

    public void appendLibAssetsForMainAssetPath(String assetPath, String[] libAssets) {
        int implCount;
        String[] strArr = libAssets;
        synchronized (this.mLock) {
            try {
                ArrayMap<ResourcesImpl, ResourcesKey> updatedResourceKeys = new ArrayMap<>();
                int implCount2 = this.mResourceImpls.size();
                int i = 0;
                while (i < implCount2) {
                    ResourcesKey key = this.mResourceImpls.keyAt(i);
                    WeakReference<ResourcesImpl> weakImplRef = this.mResourceImpls.valueAt(i);
                    ResourcesImpl impl = weakImplRef != null ? weakImplRef.get() : null;
                    if (impl != null) {
                        try {
                            if (Objects.equals(key.mResDir, assetPath)) {
                                String[] newLibAssets = key.mLibDirs;
                                for (String libAsset : strArr) {
                                    newLibAssets = (String[]) ArrayUtils.appendElement(String.class, newLibAssets, libAsset);
                                }
                                if (Arrays.equals(newLibAssets, key.mLibDirs)) {
                                    implCount = implCount2;
                                } else {
                                    implCount = implCount2;
                                    updatedResourceKeys.put(impl, new ResourcesKey(key.mResDir, key.mSplitResDirs, key.mOverlayPaths, newLibAssets, key.mDisplayId, key.mOverrideConfiguration, key.mCompatInfo, key.mLoaders));
                                }
                                i++;
                                strArr = libAssets;
                                implCount2 = implCount;
                            }
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                    implCount = implCount2;
                    i++;
                    strArr = libAssets;
                    implCount2 = implCount;
                }
                redirectResourcesToNewImplLocked(updatedResourceKeys);
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    private void applyNewResourceDirsLocked(String[] oldSourceDirs, ApplicationInfo appInfo) {
        String[] strArr;
        String baseCodePath;
        int i;
        int implCount;
        ArrayMap<ResourcesImpl, ResourcesKey> updatedResourceKeys;
        String[] copiedSplitDirs;
        int myUid;
        try {
            Trace.traceBegin(8192L, "ResourcesManager#applyNewResourceDirsLocked");
            String baseCodePath2 = appInfo.getBaseCodePath();
            int myUid2 = Process.myUid();
            if (appInfo.uid == myUid2) {
                strArr = appInfo.splitSourceDirs;
            } else {
                strArr = appInfo.splitPublicSourceDirs;
            }
            String[] newSplitDirs = strArr;
            String[] copiedSplitDirs2 = (String[]) ArrayUtils.cloneOrNull(newSplitDirs);
            String[] copiedResourceDirs = combinedOverlayPaths(appInfo.resourceDirs, appInfo.overlayPaths);
            if (appInfo.uid == myUid2) {
                addApplicationPathsLocked(baseCodePath2, copiedSplitDirs2);
            }
            ArrayMap<ResourcesImpl, ResourcesKey> updatedResourceKeys2 = new ArrayMap<>();
            int implCount2 = this.mResourceImpls.size();
            int i2 = 0;
            while (i2 < implCount2) {
                ResourcesKey key = this.mResourceImpls.keyAt(i2);
                WeakReference<ResourcesImpl> weakImplRef = this.mResourceImpls.valueAt(i2);
                ResourcesImpl impl = weakImplRef != null ? weakImplRef.get() : null;
                if (impl == null) {
                    baseCodePath = baseCodePath2;
                    i = i2;
                    implCount = implCount2;
                    updatedResourceKeys = updatedResourceKeys2;
                    copiedSplitDirs = copiedSplitDirs2;
                    myUid = myUid2;
                } else {
                    if (key.mResDir != null) {
                        try {
                            if (!key.mResDir.equals(baseCodePath2) && !ArrayUtils.contains(oldSourceDirs, key.mResDir)) {
                                baseCodePath = baseCodePath2;
                                i = i2;
                                implCount = implCount2;
                                updatedResourceKeys = updatedResourceKeys2;
                                copiedSplitDirs = copiedSplitDirs2;
                                myUid = myUid2;
                            }
                        } catch (Throwable th) {
                            th = th;
                            Trace.traceEnd(8192L);
                            throw th;
                        }
                    }
                    String[] strArr2 = key.mLibDirs;
                    int implCount3 = key.mDisplayId;
                    myUid = myUid2;
                    baseCodePath = baseCodePath2;
                    i = i2;
                    implCount = implCount2;
                    updatedResourceKeys = updatedResourceKeys2;
                    copiedSplitDirs = copiedSplitDirs2;
                    updatedResourceKeys.put(impl, new ResourcesKey(baseCodePath2, copiedSplitDirs2, copiedResourceDirs, strArr2, implCount3, key.mOverrideConfiguration, key.mCompatInfo, key.mLoaders));
                }
                i2 = i + 1;
                updatedResourceKeys2 = updatedResourceKeys;
                implCount2 = implCount;
                copiedSplitDirs2 = copiedSplitDirs;
                myUid2 = myUid;
                baseCodePath2 = baseCodePath;
            }
            redirectResourcesToNewImplLocked(updatedResourceKeys2);
            Trace.traceEnd(8192L);
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static String[] combinedOverlayPaths(String[] resourceDirs, String[] overlayPaths) {
        if (resourceDirs == null) {
            return (String[]) ArrayUtils.cloneOrNull(overlayPaths);
        }
        if (overlayPaths == null) {
            return (String[]) ArrayUtils.cloneOrNull(resourceDirs);
        }
        ArrayList<String> paths = new ArrayList<>();
        for (String path : overlayPaths) {
            paths.add(path);
        }
        for (String path2 : resourceDirs) {
            if (!paths.contains(path2)) {
                paths.add(path2);
            }
        }
        return (String[]) paths.toArray(new String[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void redirectResourcesToNewImplLocked(ArrayMap<ResourcesImpl, ResourcesKey> updatedResourceKeys) {
        ResourcesKey key;
        ResourcesKey key2;
        if (updatedResourceKeys.isEmpty()) {
            return;
        }
        int resourcesCount = this.mResourceReferences.size();
        int i = 0;
        while (true) {
            if (i < resourcesCount) {
                WeakReference<Resources> ref = this.mResourceReferences.get(i);
                Resources r = ref != null ? ref.get() : null;
                if (r != null && (key2 = updatedResourceKeys.get(r.getImpl())) != null) {
                    ResourcesImpl impl = findOrCreateResourcesImplForKeyLocked(key2);
                    if (impl == null) {
                        throw new Resources.NotFoundException("failed to redirect ResourcesImpl");
                    }
                    r.setImpl(impl);
                }
                i++;
            } else {
                for (ActivityResources activityResources : this.mActivityResourceReferences.values()) {
                    int resCount = activityResources.activityResources.size();
                    for (int i2 = 0; i2 < resCount; i2++) {
                        ActivityResource activityResource = activityResources.activityResources.get(i2);
                        Resources r2 = activityResource != null ? activityResource.resources.get() : null;
                        if (r2 != null && (key = updatedResourceKeys.get(r2.getImpl())) != null) {
                            ResourcesImpl impl2 = findOrCreateResourcesImplForKeyLocked(key);
                            if (impl2 == null) {
                                throw new Resources.NotFoundException("failed to redirect ResourcesImpl");
                            }
                            r2.setImpl(impl2);
                        }
                    }
                }
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UpdateHandler implements Resources.UpdateCallbacks {
        private UpdateHandler() {
        }

        @Override // android.content.res.Resources.UpdateCallbacks
        public void onLoadersChanged(Resources resources, List<ResourcesLoader> newLoader) {
            synchronized (ResourcesManager.this.mLock) {
                ResourcesKey oldKey = ResourcesManager.this.findKeyForResourceImplLocked(resources.getImpl());
                if (oldKey == null) {
                    throw new IllegalArgumentException("Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager");
                }
                ResourcesKey newKey = new ResourcesKey(oldKey.mResDir, oldKey.mSplitResDirs, oldKey.mOverlayPaths, oldKey.mLibDirs, oldKey.mDisplayId, oldKey.mOverrideConfiguration, oldKey.mCompatInfo, (ResourcesLoader[]) newLoader.toArray(new ResourcesLoader[0]));
                ResourcesImpl impl = ResourcesManager.this.findOrCreateResourcesImplForKeyLocked(newKey);
                resources.setImpl(impl);
            }
        }

        @Override // android.content.res.loader.ResourcesLoader.UpdateCallbacks
        public void onLoaderUpdated(ResourcesLoader loader) {
            synchronized (ResourcesManager.this.mLock) {
                ArrayMap<ResourcesImpl, ResourcesKey> updatedResourceImplKeys = new ArrayMap<>();
                for (int i = ResourcesManager.this.mResourceImpls.size() - 1; i >= 0; i--) {
                    ResourcesKey key = (ResourcesKey) ResourcesManager.this.mResourceImpls.keyAt(i);
                    WeakReference<ResourcesImpl> impl = (WeakReference) ResourcesManager.this.mResourceImpls.valueAt(i);
                    if (impl != null && !impl.refersTo(null) && ArrayUtils.contains(key.mLoaders, loader)) {
                        ResourcesManager.this.mResourceImpls.remove(key);
                        updatedResourceImplKeys.put(impl.get(), key);
                    }
                }
                ResourcesManager.this.redirectResourcesToNewImplLocked(updatedResourceImplKeys);
            }
        }
    }
}
