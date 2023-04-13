package android.p008os.incremental;

import android.content.p001pm.DataLoaderParams;
import android.content.p001pm.IDataLoaderStatusListener;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.incremental.V4Signature;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
/* renamed from: android.os.incremental.IncrementalStorage */
/* loaded from: classes3.dex */
public final class IncrementalStorage {
    private static final int INCFS_MAX_ADD_DATA_SIZE = 128;
    private static final int INCFS_MAX_HASH_SIZE = 32;
    private static final String TAG = "IncrementalStorage";
    private static final int UUID_BYTE_SIZE = 16;
    private final int mId;
    private final IIncrementalService mService;

    public IncrementalStorage(IIncrementalService is, int id) {
        this.mService = is;
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public void bind(String targetPath) throws IOException {
        bind("", targetPath);
    }

    public void bind(String sourcePath, String targetPath) throws IOException {
        try {
            int res = this.mService.makeBindMount(this.mId, sourcePath, targetPath, 0);
            if (res < 0) {
                throw new IOException("bind() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void bindPermanent(String targetPath) throws IOException {
        bindPermanent("", targetPath);
    }

    public void bindPermanent(String sourcePath, String targetPath) throws IOException {
        try {
            int res = this.mService.makeBindMount(this.mId, sourcePath, targetPath, 1);
            if (res < 0) {
                throw new IOException("bind() permanent failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void unBind(String targetPath) throws IOException {
        try {
            int res = this.mService.deleteBindMount(this.mId, targetPath);
            if (res < 0) {
                throw new IOException("unbind() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeDirectory(String path) throws IOException {
        try {
            int res = this.mService.makeDirectory(this.mId, path);
            if (res < 0) {
                throw new IOException("makeDirectory() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeDirectories(String path) throws IOException {
        try {
            int res = this.mService.makeDirectories(this.mId, path);
            if (res < 0) {
                throw new IOException("makeDirectory() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeFile(String path, long size, int mode, UUID id, byte[] metadata, byte[] v4signatureBytes, byte[] content) throws IOException {
        try {
            if (id == null && metadata == null) {
                throw new IOException("File ID and metadata cannot both be null");
            }
            validateV4Signature(v4signatureBytes);
            IncrementalNewFileParams params = new IncrementalNewFileParams();
            params.size = size;
            params.metadata = metadata == null ? new byte[0] : metadata;
            params.fileId = idToBytes(id);
            params.signature = v4signatureBytes;
            int res = this.mService.makeFile(this.mId, path, mode, params, content);
            if (res != 0) {
                throw new IOException("makeFile() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeFileFromRange(String destPath, String sourcePath, long rangeStart, long rangeEnd) throws IOException {
        try {
            int res = this.mService.makeFileFromRange(this.mId, destPath, sourcePath, rangeStart, rangeEnd);
            if (res < 0) {
                throw new IOException("makeFileFromRange() failed, errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void makeLink(String sourcePath, IncrementalStorage destStorage, String destPath) throws IOException {
        try {
            int res = this.mService.makeLink(this.mId, sourcePath, destStorage.getId(), destPath);
            if (res < 0) {
                throw new IOException("makeLink() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void unlink(String path) throws IOException {
        try {
            int res = this.mService.unlink(this.mId, path);
            if (res < 0) {
                throw new IOException("unlink() failed with errno " + (-res));
            }
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void moveFile(String sourcepath, String destpath) throws IOException {
        int res;
        try {
            IIncrementalService iIncrementalService = this.mService;
            int i = this.mId;
            res = iIncrementalService.makeLink(i, sourcepath, i, destpath);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        if (res < 0) {
            throw new IOException("moveFile() failed at makeLink(), errno " + (-res));
        }
        try {
            this.mService.unlink(this.mId, sourcepath);
        } catch (RemoteException e2) {
        }
    }

    public void moveDir(String sourcePath, String destPath) throws IOException {
        int res;
        if (!new File(destPath).exists()) {
            throw new IOException("moveDir() requires that destination dir already exists.");
        }
        try {
            res = this.mService.makeBindMount(this.mId, sourcePath, destPath, 1);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        if (res < 0) {
            throw new IOException("moveDir() failed at making bind mount, errno " + (-res));
        }
        try {
            this.mService.deleteBindMount(this.mId, sourcePath);
        } catch (RemoteException e2) {
        }
    }

    public boolean isFileFullyLoaded(String path) throws IOException {
        try {
            int res = this.mService.isFileFullyLoaded(this.mId, path);
            if (res >= 0) {
                return res == 0;
            }
            throw new IOException("isFileFullyLoaded() failed, errno " + (-res));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean isFullyLoaded() throws IOException {
        try {
            int res = this.mService.isFullyLoaded(this.mId);
            if (res >= 0) {
                return res == 0;
            }
            throw new IOException("isFullyLoaded() failed at querying loading progress, errno " + (-res));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public float getLoadingProgress() throws IOException {
        try {
            float res = this.mService.getLoadingProgress(this.mId);
            if (res < 0.0f) {
                throw new IOException("getLoadingProgress() failed at querying loading progress, errno " + (-res));
            }
            return res;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return 0.0f;
        }
    }

    public byte[] getFileMetadata(String path) {
        try {
            return this.mService.getMetadataByPath(this.mId, path);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public byte[] getFileMetadata(UUID id) {
        try {
            byte[] rawId = idToBytes(id);
            return this.mService.getMetadataById(this.mId, rawId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public boolean startLoading(DataLoaderParams dataLoaderParams, IDataLoaderStatusListener statusListener, StorageHealthCheckParams healthCheckParams, IStorageHealthListener healthListener, PerUidReadTimeouts[] perUidReadTimeouts) {
        Objects.requireNonNull(perUidReadTimeouts);
        try {
            return this.mService.startLoading(this.mId, dataLoaderParams.getData(), statusListener, healthCheckParams, healthListener, perUidReadTimeouts);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public void onInstallationComplete() {
        try {
            this.mService.onInstallationComplete(this.mId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static byte[] idToBytes(UUID id) {
        if (id == null) {
            return new byte[0];
        }
        ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
        buf.putLong(id.getMostSignificantBits());
        buf.putLong(id.getLeastSignificantBits());
        return buf.array();
    }

    public static UUID bytesToId(byte[] bytes) throws IllegalArgumentException {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Expected array of size 16, got " + bytes.length);
        }
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        long msb = buf.getLong();
        long lsb = buf.getLong();
        return new UUID(msb, lsb);
    }

    public void disallowReadLogs() {
        try {
            this.mService.disallowReadLogs(this.mId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    private static void validateV4Signature(byte[] v4signatureBytes) throws IOException {
        if (v4signatureBytes == null || v4signatureBytes.length == 0) {
            return;
        }
        try {
            V4Signature signature = V4Signature.readFrom(v4signatureBytes);
            if (!signature.isVersionSupported()) {
                throw new IOException("v4 signature version " + signature.version + " is not supported");
            }
            V4Signature.HashingInfo hashingInfo = V4Signature.HashingInfo.fromByteArray(signature.hashingInfo);
            V4Signature.SigningInfos signingInfos = V4Signature.SigningInfos.fromByteArray(signature.signingInfos);
            if (hashingInfo.hashAlgorithm != 1) {
                throw new IOException("Unsupported hashAlgorithm: " + hashingInfo.hashAlgorithm);
            }
            if (hashingInfo.log2BlockSize != 12) {
                throw new IOException("Unsupported log2BlockSize: " + ((int) hashingInfo.log2BlockSize));
            }
            if (hashingInfo.salt != null && hashingInfo.salt.length > 0) {
                throw new IOException("Unsupported salt: " + Arrays.toString(hashingInfo.salt));
            }
            if (hashingInfo.rawRootHash.length != 32) {
                throw new IOException("rawRootHash has to be 32 bytes");
            }
            if (signingInfos.signingInfo.additionalData.length > 128) {
                throw new IOException("additionalData has to be at most 128 bytes");
            }
        } catch (IOException e) {
            throw new IOException("Failed to read v4 signature:", e);
        }
    }

    public boolean configureNativeBinaries(String apkFullPath, String libDirRelativePath, String abi, boolean extractNativeLibs) {
        try {
            return this.mService.configureNativeBinaries(this.mId, apkFullPath, libDirRelativePath, abi, extractNativeLibs);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean waitForNativeBinariesExtraction() {
        try {
            return this.mService.waitForNativeBinariesExtraction(this.mId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean registerLoadingProgressListener(IStorageLoadingProgressListener listener) {
        try {
            return this.mService.registerLoadingProgressListener(this.mId, listener);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public boolean unregisterLoadingProgressListener() {
        try {
            return this.mService.unregisterLoadingProgressListener(this.mId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public PersistableBundle getMetrics() {
        try {
            return this.mService.getMetrics(this.mId);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }
}
