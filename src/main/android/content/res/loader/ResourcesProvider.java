package android.content.res.loader;

import android.content.Context;
import android.content.p000om.OverlayInfo;
import android.content.p001pm.ApplicationInfo;
import android.content.res.ApkAssets;
import android.p008os.ParcelFileDescriptor;
import android.util.Log;
import com.android.internal.content.p014om.OverlayManagerImpl;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
/* loaded from: classes.dex */
public class ResourcesProvider implements AutoCloseable, Closeable {
    private static final String TAG = "ResourcesProvider";
    private final ApkAssets mApkAssets;
    private final Object mLock = new Object();
    private boolean mOpen = true;
    private int mOpenCount = 0;

    public static ResourcesProvider empty(AssetsProvider assetsProvider) {
        return new ResourcesProvider(ApkAssets.loadEmptyForLoader(4, assetsProvider));
    }

    public static ResourcesProvider loadOverlay(OverlayInfo overlayInfo) throws IOException {
        Objects.requireNonNull(overlayInfo);
        Preconditions.checkArgument(overlayInfo.isFabricated(), "Not accepted overlay");
        Preconditions.checkStringNotEmpty(overlayInfo.getTargetOverlayableName(), "Without overlayable name");
        String overlayName = OverlayManagerImpl.checkOverlayNameValid(overlayInfo.getOverlayName());
        String path = (String) Preconditions.checkStringNotEmpty(overlayInfo.getBaseCodePath(), "Invalid base path");
        Path frroPath = Path.of(path, new String[0]);
        if (!Files.isRegularFile(frroPath, new LinkOption[0])) {
            throw new FileNotFoundException("The frro file not found");
        }
        Path idmapPath = frroPath.getParent().resolve(overlayName + ".idmap");
        if (!Files.isRegularFile(idmapPath, new LinkOption[0])) {
            throw new FileNotFoundException("The idmap file not found");
        }
        return new ResourcesProvider(ApkAssets.loadOverlayFromPath(idmapPath.toString(), 0));
    }

    public static ResourcesProvider loadFromApk(ParcelFileDescriptor fileDescriptor) throws IOException {
        return loadFromApk(fileDescriptor, null);
    }

    public static ResourcesProvider loadFromApk(ParcelFileDescriptor fileDescriptor, AssetsProvider assetsProvider) throws IOException {
        return new ResourcesProvider(ApkAssets.loadFromFd(fileDescriptor.getFileDescriptor(), fileDescriptor.toString(), 4, assetsProvider));
    }

    public static ResourcesProvider loadFromApk(ParcelFileDescriptor fileDescriptor, long offset, long length, AssetsProvider assetsProvider) throws IOException {
        return new ResourcesProvider(ApkAssets.loadFromFd(fileDescriptor.getFileDescriptor(), fileDescriptor.toString(), offset, length, 4, assetsProvider));
    }

    public static ResourcesProvider loadFromTable(ParcelFileDescriptor fileDescriptor, AssetsProvider assetsProvider) throws IOException {
        return new ResourcesProvider(ApkAssets.loadTableFromFd(fileDescriptor.getFileDescriptor(), fileDescriptor.toString(), 4, assetsProvider));
    }

    public static ResourcesProvider loadFromTable(ParcelFileDescriptor fileDescriptor, long offset, long length, AssetsProvider assetsProvider) throws IOException {
        return new ResourcesProvider(ApkAssets.loadTableFromFd(fileDescriptor.getFileDescriptor(), fileDescriptor.toString(), offset, length, 4, assetsProvider));
    }

    public static ResourcesProvider loadFromSplit(Context context, String splitName) throws IOException {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int splitIndex = ArrayUtils.indexOf(appInfo.splitNames, splitName);
        if (splitIndex < 0) {
            throw new IllegalArgumentException("Split " + splitName + " not found");
        }
        String splitPath = appInfo.getSplitCodePaths()[splitIndex];
        return new ResourcesProvider(ApkAssets.loadFromPath(splitPath, 4, null));
    }

    public static ResourcesProvider loadFromDirectory(String path, AssetsProvider assetsProvider) throws IOException {
        return new ResourcesProvider(ApkAssets.loadFromDir(path, 4, assetsProvider));
    }

    private ResourcesProvider(ApkAssets apkAssets) {
        this.mApkAssets = apkAssets;
    }

    public ApkAssets getApkAssets() {
        return this.mApkAssets;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void incrementRefCount() {
        synchronized (this.mLock) {
            if (!this.mOpen) {
                throw new IllegalStateException("Operation failed: resources provider is closed");
            }
            this.mOpenCount++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void decrementRefCount() {
        synchronized (this.mLock) {
            this.mOpenCount--;
        }
    }

    @Override // java.lang.AutoCloseable, java.io.Closeable
    public void close() {
        synchronized (this.mLock) {
            if (this.mOpen) {
                if (this.mOpenCount != 0) {
                    throw new IllegalStateException("Failed to close provider used by " + this.mOpenCount + " ResourcesLoader instances");
                }
                this.mOpen = false;
                try {
                    this.mApkAssets.close();
                } catch (Throwable th) {
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        synchronized (this.mLock) {
            if (this.mOpenCount != 0) {
                Log.m104w(TAG, "ResourcesProvider " + this + " finalized with non-zero refs: " + this.mOpenCount);
            }
        }
    }
}
