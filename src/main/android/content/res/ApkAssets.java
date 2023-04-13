package android.content.res;

import android.content.p000om.OverlayableInfo;
import android.content.res.loader.AssetsProvider;
import android.text.TextUtils;
import dalvik.annotation.optimization.CriticalNative;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ApkAssets {
    private static final int FORMAT_APK = 0;
    private static final int FORMAT_ARSC = 2;
    private static final int FORMAT_DIR = 3;
    private static final int FORMAT_IDMAP = 1;
    public static final int PROPERTY_DISABLE_INCREMENTAL_HARDENING = 16;
    public static final int PROPERTY_DYNAMIC = 2;
    public static final int PROPERTY_LOADER = 4;
    private static final int PROPERTY_OVERLAY = 8;
    public static final int PROPERTY_SYSTEM = 1;
    private final AssetsProvider mAssets;
    private final int mFlags;
    private long mNativePtr;
    private final StringBlock mStringBlock;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FormatType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PropertyFlags {
    }

    private static native boolean nativeDefinesOverlayable(long j) throws IOException;

    private static native void nativeDestroy(long j);

    private static native String nativeGetAssetPath(long j);

    private static native String nativeGetDebugName(long j);

    private static native OverlayableInfo nativeGetOverlayableInfo(long j, String str) throws IOException;

    private static native long nativeGetStringBlock(long j);

    @CriticalNative
    private static native boolean nativeIsUpToDate(long j);

    private static native long nativeLoad(int i, String str, int i2, AssetsProvider assetsProvider) throws IOException;

    private static native long nativeLoadEmpty(int i, AssetsProvider assetsProvider);

    private static native long nativeLoadFd(int i, FileDescriptor fileDescriptor, String str, int i2, AssetsProvider assetsProvider) throws IOException;

    private static native long nativeLoadFdOffsets(int i, FileDescriptor fileDescriptor, String str, long j, long j2, int i2, AssetsProvider assetsProvider) throws IOException;

    private static native long nativeOpenXml(long j, String str) throws IOException;

    public static ApkAssets loadFromPath(String path) throws IOException {
        return loadFromPath(path, 0);
    }

    public static ApkAssets loadFromPath(String path, int flags) throws IOException {
        return new ApkAssets(0, path, flags, null);
    }

    public static ApkAssets loadFromPath(String path, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(0, path, flags, assets);
    }

    public static ApkAssets loadFromFd(FileDescriptor fd, String friendlyName, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(0, fd, friendlyName, flags, assets);
    }

    public static ApkAssets loadFromFd(FileDescriptor fd, String friendlyName, long offset, long length, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(0, fd, friendlyName, offset, length, flags, assets);
    }

    public static ApkAssets loadOverlayFromPath(String idmapPath, int flags) throws IOException {
        return new ApkAssets(1, idmapPath, flags, null);
    }

    public static ApkAssets loadTableFromFd(FileDescriptor fd, String friendlyName, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(2, fd, friendlyName, flags, assets);
    }

    public static ApkAssets loadTableFromFd(FileDescriptor fd, String friendlyName, long offset, long length, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(2, fd, friendlyName, offset, length, flags, assets);
    }

    public static ApkAssets loadFromDir(String path, int flags, AssetsProvider assets) throws IOException {
        return new ApkAssets(3, path, flags, assets);
    }

    public static ApkAssets loadEmptyForLoader(int flags, AssetsProvider assets) {
        return new ApkAssets(flags, assets);
    }

    private ApkAssets(int format, String path, int flags, AssetsProvider assets) throws IOException {
        Objects.requireNonNull(path, "path");
        this.mFlags = flags;
        long nativeLoad = nativeLoad(format, path, flags, assets);
        this.mNativePtr = nativeLoad;
        this.mStringBlock = new StringBlock(nativeGetStringBlock(nativeLoad), true);
        this.mAssets = assets;
    }

    private ApkAssets(int format, FileDescriptor fd, String friendlyName, int flags, AssetsProvider assets) throws IOException {
        Objects.requireNonNull(fd, "fd");
        Objects.requireNonNull(friendlyName, "friendlyName");
        this.mFlags = flags;
        long nativeLoadFd = nativeLoadFd(format, fd, friendlyName, flags, assets);
        this.mNativePtr = nativeLoadFd;
        this.mStringBlock = new StringBlock(nativeGetStringBlock(nativeLoadFd), true);
        this.mAssets = assets;
    }

    private ApkAssets(int format, FileDescriptor fd, String friendlyName, long offset, long length, int flags, AssetsProvider assets) throws IOException {
        Objects.requireNonNull(fd, "fd");
        Objects.requireNonNull(friendlyName, "friendlyName");
        this.mFlags = flags;
        long nativeLoadFdOffsets = nativeLoadFdOffsets(format, fd, friendlyName, offset, length, flags, assets);
        this.mNativePtr = nativeLoadFdOffsets;
        this.mStringBlock = new StringBlock(nativeGetStringBlock(nativeLoadFdOffsets), true);
        this.mAssets = assets;
    }

    private ApkAssets(int flags, AssetsProvider assets) {
        this.mFlags = flags;
        this.mNativePtr = nativeLoadEmpty(flags, assets);
        this.mStringBlock = null;
        this.mAssets = assets;
    }

    public String getAssetPath() {
        String emptyIfNull;
        synchronized (this) {
            emptyIfNull = TextUtils.emptyIfNull(nativeGetAssetPath(this.mNativePtr));
        }
        return emptyIfNull;
    }

    public String getDebugName() {
        String nativeGetDebugName;
        synchronized (this) {
            nativeGetDebugName = nativeGetDebugName(this.mNativePtr);
        }
        return nativeGetDebugName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getStringFromPool(int idx) {
        CharSequence sequence;
        if (this.mStringBlock == null) {
            return null;
        }
        synchronized (this) {
            sequence = this.mStringBlock.getSequence(idx);
        }
        return sequence;
    }

    public boolean isForLoader() {
        return (this.mFlags & 4) != 0;
    }

    public AssetsProvider getAssetsProvider() {
        return this.mAssets;
    }

    public XmlResourceParser openXml(String fileName) throws IOException {
        XmlResourceParser parser;
        Objects.requireNonNull(fileName, "fileName");
        synchronized (this) {
            long nativeXmlPtr = nativeOpenXml(this.mNativePtr, fileName);
            XmlBlock block = new XmlBlock(null, nativeXmlPtr);
            parser = block.newParser();
            if (parser == null) {
                throw new AssertionError("block.newParser() returned a null parser");
            }
            block.close();
        }
        return parser;
    }

    public OverlayableInfo getOverlayableInfo(String overlayableName) throws IOException {
        OverlayableInfo nativeGetOverlayableInfo;
        synchronized (this) {
            nativeGetOverlayableInfo = nativeGetOverlayableInfo(this.mNativePtr, overlayableName);
        }
        return nativeGetOverlayableInfo;
    }

    public boolean definesOverlayable() throws IOException {
        boolean nativeDefinesOverlayable;
        synchronized (this) {
            nativeDefinesOverlayable = nativeDefinesOverlayable(this.mNativePtr);
        }
        return nativeDefinesOverlayable;
    }

    public boolean isUpToDate() {
        boolean nativeIsUpToDate;
        synchronized (this) {
            nativeIsUpToDate = nativeIsUpToDate(this.mNativePtr);
        }
        return nativeIsUpToDate;
    }

    public String toString() {
        return "ApkAssets{path=" + getDebugName() + "}";
    }

    protected void finalize() throws Throwable {
        close();
    }

    public void close() {
        synchronized (this) {
            if (this.mNativePtr != 0) {
                StringBlock stringBlock = this.mStringBlock;
                if (stringBlock != null) {
                    stringBlock.close();
                }
                nativeDestroy(this.mNativePtr);
                this.mNativePtr = 0L;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "class=" + getClass());
        pw.println(prefix + "debugName=" + getDebugName());
        pw.println(prefix + "assetPath=" + getAssetPath());
    }
}
