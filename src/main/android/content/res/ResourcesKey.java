package android.content.res;

import android.content.res.loader.ResourcesLoader;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ResourcesKey {
    public final CompatibilityInfo mCompatInfo;
    public int mDisplayId;
    private final int mHash;
    public final String[] mLibDirs;
    public final ResourcesLoader[] mLoaders;
    public final String[] mOverlayPaths;
    public final Configuration mOverrideConfiguration;
    public final String mResDir;
    public final String[] mSplitResDirs;

    public ResourcesKey(String resDir, String[] splitResDirs, String[] overlayPaths, String[] libDirs, int overrideDisplayId, Configuration overrideConfig, CompatibilityInfo compatInfo, ResourcesLoader[] loader) {
        this.mResDir = resDir;
        this.mSplitResDirs = splitResDirs;
        this.mOverlayPaths = overlayPaths;
        this.mLibDirs = libDirs;
        ResourcesLoader[] resourcesLoaderArr = (loader == null || loader.length != 0) ? loader : null;
        this.mLoaders = resourcesLoaderArr;
        this.mDisplayId = overrideDisplayId;
        Configuration configuration = new Configuration(overrideConfig != null ? overrideConfig : Configuration.EMPTY);
        this.mOverrideConfiguration = configuration;
        CompatibilityInfo compatibilityInfo = compatInfo != null ? compatInfo : CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mCompatInfo = compatibilityInfo;
        int hash = (17 * 31) + Objects.hashCode(resDir);
        this.mHash = (((((((((((((hash * 31) + Arrays.hashCode(splitResDirs)) * 31) + Arrays.hashCode(overlayPaths)) * 31) + Arrays.hashCode(libDirs)) * 31) + Objects.hashCode(Integer.valueOf(this.mDisplayId))) * 31) + Objects.hashCode(configuration)) * 31) + Objects.hashCode(compatibilityInfo)) * 31) + Arrays.hashCode(resourcesLoaderArr);
    }

    public ResourcesKey(String resDir, String[] splitResDirs, String[] overlayPaths, String[] libDirs, int displayId, Configuration overrideConfig, CompatibilityInfo compatInfo) {
        this(resDir, splitResDirs, overlayPaths, libDirs, displayId, overrideConfig, compatInfo, null);
    }

    public boolean hasOverrideConfiguration() {
        return !Configuration.EMPTY.equals(this.mOverrideConfiguration);
    }

    public boolean isPathReferenced(String path) {
        String str = this.mResDir;
        return (str != null && str.startsWith(path)) || anyStartsWith(this.mSplitResDirs, path) || anyStartsWith(this.mOverlayPaths, path) || anyStartsWith(this.mLibDirs, path);
    }

    private static boolean anyStartsWith(String[] list, String prefix) {
        if (list != null) {
            for (String s : list) {
                if (s != null && s.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mHash;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ResourcesKey) {
            ResourcesKey peer = (ResourcesKey) obj;
            return this.mHash == peer.mHash && Objects.equals(this.mResDir, peer.mResDir) && Arrays.equals(this.mSplitResDirs, peer.mSplitResDirs) && Arrays.equals(this.mOverlayPaths, peer.mOverlayPaths) && Arrays.equals(this.mLibDirs, peer.mLibDirs) && this.mDisplayId == peer.mDisplayId && Objects.equals(this.mOverrideConfiguration, peer.mOverrideConfiguration) && Objects.equals(this.mCompatInfo, peer.mCompatInfo) && Arrays.equals(this.mLoaders, peer.mLoaders);
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append("ResourcesKey{");
        builder.append(" mHash=").append(Integer.toHexString(this.mHash));
        builder.append(" mResDir=").append(this.mResDir);
        builder.append(" mSplitDirs=[");
        String[] strArr = this.mSplitResDirs;
        if (strArr != null) {
            builder.append(TextUtils.join(",", strArr));
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        builder.append(" mOverlayDirs=[");
        String[] strArr2 = this.mOverlayPaths;
        if (strArr2 != null) {
            builder.append(TextUtils.join(",", strArr2));
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        builder.append(" mLibDirs=[");
        String[] strArr3 = this.mLibDirs;
        if (strArr3 != null) {
            builder.append(TextUtils.join(",", strArr3));
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        builder.append(" mDisplayId=").append(this.mDisplayId);
        builder.append(" mOverrideConfig=").append(Configuration.resourceQualifierString(this.mOverrideConfiguration));
        builder.append(" mCompatInfo=").append(this.mCompatInfo);
        builder.append(" mLoaders=[");
        ResourcesLoader[] resourcesLoaderArr = this.mLoaders;
        if (resourcesLoaderArr != null) {
            builder.append(TextUtils.join(",", resourcesLoaderArr));
        }
        builder.append("]}");
        return builder.toString();
    }
}
