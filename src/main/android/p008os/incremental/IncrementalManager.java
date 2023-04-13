package android.p008os.incremental;

import android.content.p001pm.DataLoaderParams;
import android.content.p001pm.IPackageLoadingProgressCallback;
import android.p008os.RemoteCallbackList;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.p008os.incremental.IStorageLoadingProgressListener;
import android.system.ErrnoException;
import android.system.Os;
import android.system.StructStat;
import android.util.Slog;
import android.util.SparseArray;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
/* renamed from: android.os.incremental.IncrementalManager */
/* loaded from: classes3.dex */
public final class IncrementalManager {
    private static final String ALLOWED_PROPERTY = "incremental.allowed";
    public static final int CREATE_MODE_CREATE = 4;
    public static final int CREATE_MODE_OPEN_EXISTING = 8;
    public static final int CREATE_MODE_PERMANENT_BIND = 2;
    public static final int CREATE_MODE_TEMPORARY_BIND = 1;
    public static final int MIN_VERSION_TO_SUPPORT_FSVERITY = 2;
    private static final String TAG = "IncrementalManager";
    private final LoadingProgressCallbacks mLoadingProgressCallbacks = new LoadingProgressCallbacks();
    private final IIncrementalService mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.incremental.IncrementalManager$CreateMode */
    /* loaded from: classes3.dex */
    public @interface CreateMode {
    }

    private static native boolean nativeIsEnabled();

    private static native boolean nativeIsIncrementalFd(int i);

    private static native boolean nativeIsIncrementalPath(String str);

    private static native boolean nativeIsV2Available();

    private static native byte[] nativeUnsafeGetFileSignature(String str);

    public IncrementalManager(IIncrementalService service) {
        this.mService = service;
    }

    public IncrementalStorage createStorage(String path, DataLoaderParams params, int createMode) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);
        try {
            int id = this.mService.createStorage(path, params.getData(), createMode);
            if (id < 0) {
                return null;
            }
            return new IncrementalStorage(this.mService, id);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IncrementalStorage openStorage(String path) {
        try {
            int id = this.mService.openStorage(path);
            if (id < 0) {
                return null;
            }
            IncrementalStorage storage = new IncrementalStorage(this.mService, id);
            return storage;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IncrementalStorage createStorage(String path, IncrementalStorage linkedStorage, int createMode) {
        int id = -1;
        try {
            StructStat st = Os.stat(path);
            id = this.mService.createLinkedStorage(path, linkedStorage.getId(), createMode);
            if (id < 0) {
                return null;
            }
            Os.chmod(path, st.st_mode & 4095);
            return new IncrementalStorage(this.mService, id);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (ErrnoException e2) {
            if (id >= 0) {
                try {
                    this.mService.deleteStorage(id);
                } catch (RemoteException re) {
                    throw re.rethrowFromSystemServer();
                }
            }
            throw new RuntimeException(e2);
        }
    }

    public void linkCodePath(File beforeCodeFile, File afterCodeFile) throws IllegalArgumentException, IOException {
        File beforeCodeAbsolute = beforeCodeFile.getAbsoluteFile();
        IncrementalStorage apkStorage = openStorage(beforeCodeAbsolute.toString());
        if (apkStorage == null) {
            throw new IllegalArgumentException("Not an Incremental path: " + beforeCodeAbsolute);
        }
        String targetStorageDir = afterCodeFile.getAbsoluteFile().getParent();
        IncrementalStorage linkedApkStorage = createStorage(targetStorageDir, apkStorage, 6);
        if (linkedApkStorage == null) {
            throw new IOException("Failed to create linked storage at dir: " + targetStorageDir);
        }
        try {
            String afterCodePathName = afterCodeFile.getName();
            linkFiles(apkStorage, beforeCodeAbsolute, "", linkedApkStorage, afterCodePathName);
        } catch (Exception e) {
            linkedApkStorage.unBind(targetStorageDir);
            throw e;
        }
    }

    private void linkFiles(final IncrementalStorage sourceStorage, File sourceAbsolutePath, String sourceRelativePath, final IncrementalStorage targetStorage, String targetRelativePath) throws IOException {
        final Path sourceBase = sourceAbsolutePath.toPath().resolve(sourceRelativePath);
        final Path targetRelative = Paths.get(targetRelativePath, new String[0]);
        Files.walkFileTree(sourceAbsolutePath.toPath(), new SimpleFileVisitor<Path>() { // from class: android.os.incremental.IncrementalManager.1
            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativeDir = sourceBase.relativize(dir);
                targetStorage.makeDirectory(targetRelative.resolve(relativeDir).toString());
                return FileVisitResult.CONTINUE;
            }

            @Override // java.nio.file.SimpleFileVisitor, java.nio.file.FileVisitor
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativeFile = sourceBase.relativize(file);
                sourceStorage.makeLink(file.toAbsolutePath().toString(), targetStorage, targetRelative.resolve(relativeFile).toString());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static boolean isFeatureEnabled() {
        return nativeIsEnabled();
    }

    public static int getVersion() {
        if (nativeIsEnabled()) {
            return nativeIsV2Available() ? 2 : 1;
        }
        return 0;
    }

    public static boolean isAllowed() {
        return isFeatureEnabled() && SystemProperties.getBoolean(ALLOWED_PROPERTY, true);
    }

    public static boolean isIncrementalPath(String path) {
        return nativeIsIncrementalPath(path);
    }

    public static boolean isIncrementalFileFd(FileDescriptor fd) {
        return nativeIsIncrementalFd(fd.getInt$());
    }

    public static byte[] unsafeGetFileSignature(String path) {
        return nativeUnsafeGetFileSignature(path);
    }

    public void rmPackageDir(File codeFile) {
        try {
            String codePath = codeFile.getAbsolutePath();
            IncrementalStorage storage = openStorage(codePath);
            if (storage == null) {
                return;
            }
            this.mLoadingProgressCallbacks.cleanUpCallbacks(storage);
            storage.unBind(codePath);
        } catch (IOException e) {
            Slog.m89w(TAG, "Failed to remove code path", e);
        }
    }

    public boolean registerLoadingProgressCallback(String codePath, IPackageLoadingProgressCallback callback) {
        IncrementalStorage storage = openStorage(codePath);
        if (storage == null) {
            return false;
        }
        return this.mLoadingProgressCallbacks.registerCallback(storage, callback);
    }

    public void unregisterLoadingProgressCallbacks(String codePath) {
        IncrementalStorage storage = openStorage(codePath);
        if (storage == null) {
            return;
        }
        this.mLoadingProgressCallbacks.cleanUpCallbacks(storage);
    }

    /* renamed from: android.os.incremental.IncrementalManager$LoadingProgressCallbacks */
    /* loaded from: classes3.dex */
    private static class LoadingProgressCallbacks extends IStorageLoadingProgressListener.Stub {
        private final SparseArray<RemoteCallbackList<IPackageLoadingProgressCallback>> mCallbacks;

        private LoadingProgressCallbacks() {
            this.mCallbacks = new SparseArray<>();
        }

        public void cleanUpCallbacks(IncrementalStorage storage) {
            RemoteCallbackList<IPackageLoadingProgressCallback> callbacksForStorage;
            int storageId = storage.getId();
            synchronized (this.mCallbacks) {
                callbacksForStorage = this.mCallbacks.removeReturnOld(storageId);
            }
            if (callbacksForStorage == null) {
                return;
            }
            callbacksForStorage.kill();
            storage.unregisterLoadingProgressListener();
        }

        public boolean registerCallback(IncrementalStorage storage, IPackageLoadingProgressCallback callback) {
            int storageId = storage.getId();
            synchronized (this.mCallbacks) {
                RemoteCallbackList<IPackageLoadingProgressCallback> callbacksForStorage = this.mCallbacks.get(storageId);
                if (callbacksForStorage == null) {
                    callbacksForStorage = new RemoteCallbackList<>();
                    this.mCallbacks.put(storageId, callbacksForStorage);
                }
                callbacksForStorage.register(callback);
                if (callbacksForStorage.getRegisteredCallbackCount() > 1) {
                    return true;
                }
                return storage.registerLoadingProgressListener(this);
            }
        }

        @Override // android.p008os.incremental.IStorageLoadingProgressListener
        public void onStorageLoadingProgressChanged(int storageId, float progress) {
            RemoteCallbackList<IPackageLoadingProgressCallback> callbacksForStorage;
            synchronized (this.mCallbacks) {
                callbacksForStorage = this.mCallbacks.get(storageId);
            }
            if (callbacksForStorage == null) {
                return;
            }
            int n = callbacksForStorage.beginBroadcast();
            for (int i = 0; i < n; i++) {
                IPackageLoadingProgressCallback callback = callbacksForStorage.getBroadcastItem(i);
                try {
                    callback.onPackageLoadingProgressChanged(progress);
                } catch (RemoteException e) {
                }
            }
            callbacksForStorage.finishBroadcast();
        }
    }

    public IncrementalMetrics getMetrics(String codePath) {
        IncrementalStorage storage = openStorage(codePath);
        if (storage == null) {
            return null;
        }
        return new IncrementalMetrics(storage.getMetrics());
    }
}
