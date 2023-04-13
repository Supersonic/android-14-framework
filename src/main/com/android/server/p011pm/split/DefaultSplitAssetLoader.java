package com.android.server.p011pm.split;

import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.PackageLite;
import android.content.res.ApkAssets;
import android.content.res.AssetManager;
import android.os.Build;
import com.android.internal.util.ArrayUtils;
import java.io.IOException;
import libcore.io.IoUtils;
/* renamed from: com.android.server.pm.split.DefaultSplitAssetLoader */
/* loaded from: classes2.dex */
public class DefaultSplitAssetLoader implements SplitAssetLoader {
    public ApkAssets mBaseApkAssets;
    public final String mBaseApkPath;
    public AssetManager mCachedAssetManager;
    public final int mFlags;
    public final String[] mSplitApkPaths;

    public DefaultSplitAssetLoader(PackageLite packageLite, int i) {
        this.mBaseApkPath = packageLite.getBaseApkPath();
        this.mSplitApkPaths = packageLite.getSplitApkPaths();
        this.mFlags = i;
    }

    public static ApkAssets loadApkAssets(String str, int i) throws IllegalArgumentException {
        if ((i & 1) != 0 && !ApkLiteParseUtils.isApkPath(str)) {
            throw new IllegalArgumentException("Invalid package file: " + str);
        }
        try {
            return ApkAssets.loadFromPath(str);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load APK at path " + str, e);
        }
    }

    @Override // com.android.server.p011pm.split.SplitAssetLoader
    public AssetManager getBaseAssetManager() throws IllegalArgumentException {
        AssetManager assetManager = this.mCachedAssetManager;
        if (assetManager != null) {
            return assetManager;
        }
        String[] strArr = this.mSplitApkPaths;
        int i = 1;
        ApkAssets[] apkAssetsArr = new ApkAssets[(strArr != null ? strArr.length : 0) + 1];
        ApkAssets loadApkAssets = loadApkAssets(this.mBaseApkPath, this.mFlags);
        this.mBaseApkAssets = loadApkAssets;
        apkAssetsArr[0] = loadApkAssets;
        if (!ArrayUtils.isEmpty(this.mSplitApkPaths)) {
            String[] strArr2 = this.mSplitApkPaths;
            int length = strArr2.length;
            int i2 = 0;
            while (i2 < length) {
                apkAssetsArr[i] = loadApkAssets(strArr2[i2], this.mFlags);
                i2++;
                i++;
            }
        }
        AssetManager assetManager2 = new AssetManager();
        assetManager2.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Build.VERSION.RESOURCES_SDK_INT);
        assetManager2.setApkAssets(apkAssetsArr, false);
        this.mCachedAssetManager = assetManager2;
        return assetManager2;
    }

    @Override // com.android.server.p011pm.split.SplitAssetLoader
    public AssetManager getSplitAssetManager(int i) throws IllegalArgumentException {
        return getBaseAssetManager();
    }

    @Override // com.android.server.p011pm.split.SplitAssetLoader
    public ApkAssets getBaseApkAssets() {
        return this.mBaseApkAssets;
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        IoUtils.closeQuietly(this.mCachedAssetManager);
    }
}
