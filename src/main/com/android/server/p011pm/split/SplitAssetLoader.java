package com.android.server.p011pm.split;

import android.content.res.ApkAssets;
import android.content.res.AssetManager;
/* renamed from: com.android.server.pm.split.SplitAssetLoader */
/* loaded from: classes2.dex */
public interface SplitAssetLoader extends AutoCloseable {
    ApkAssets getBaseApkAssets();

    AssetManager getBaseAssetManager() throws IllegalArgumentException;

    AssetManager getSplitAssetManager(int i) throws IllegalArgumentException;
}
