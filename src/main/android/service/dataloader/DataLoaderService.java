package android.service.dataloader;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.content.p001pm.DataLoaderParams;
import android.content.p001pm.DataLoaderParamsParcel;
import android.content.p001pm.FileSystemControlParcel;
import android.content.p001pm.IDataLoader;
import android.content.p001pm.IDataLoaderStatusListener;
import android.content.p001pm.InstallationFile;
import android.content.p001pm.InstallationFileParcel;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.util.ExceptionUtils;
import android.util.Slog;
import java.io.IOException;
import java.util.Collection;
import libcore.io.IoUtils;
@SystemApi
/* loaded from: classes3.dex */
public abstract class DataLoaderService extends Service {
    private static final String TAG = "DataLoaderService";
    private final DataLoaderBinderService mBinder = new DataLoaderBinderService();

    @SystemApi
    /* loaded from: classes3.dex */
    public interface DataLoader {
        boolean onCreate(DataLoaderParams dataLoaderParams, FileSystemConnector fileSystemConnector);

        boolean onPrepareImage(Collection<InstallationFile> collection, Collection<String> collection2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeCreateDataLoader(int i, FileSystemControlParcel fileSystemControlParcel, DataLoaderParamsParcel dataLoaderParamsParcel, IDataLoaderStatusListener iDataLoaderStatusListener);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeDestroyDataLoader(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativePrepareImage(int i, InstallationFileParcel[] installationFileParcelArr, String[] strArr);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeStartDataLoader(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean nativeStopDataLoader(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeWriteData(long j, String str, long j2, long j3, ParcelFileDescriptor parcelFileDescriptor);

    @SystemApi
    public DataLoader onCreateDataLoader(DataLoaderParams dataLoaderParams) {
        return null;
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    /* loaded from: classes3.dex */
    private class DataLoaderBinderService extends IDataLoader.Stub {
        private DataLoaderBinderService() {
        }

        /* JADX WARN: Finally extract failed */
        @Override // android.content.p001pm.IDataLoader
        public void create(int id, DataLoaderParamsParcel params, FileSystemControlParcel control, IDataLoaderStatusListener listener) throws RuntimeException {
            try {
                try {
                    DataLoaderService.this.nativeCreateDataLoader(id, control, params, listener);
                    if (control.incremental != null) {
                        IoUtils.closeQuietly(control.incremental.cmd);
                        IoUtils.closeQuietly(control.incremental.pendingReads);
                        IoUtils.closeQuietly(control.incremental.log);
                        IoUtils.closeQuietly(control.incremental.blocksWritten);
                    }
                } catch (Exception ex) {
                    Slog.m95e(DataLoaderService.TAG, "Failed to create native loader for " + id, ex);
                    destroy(id);
                    throw new RuntimeException(ex);
                }
            } catch (Throwable th) {
                if (control.incremental != null) {
                    IoUtils.closeQuietly(control.incremental.cmd);
                    IoUtils.closeQuietly(control.incremental.pendingReads);
                    IoUtils.closeQuietly(control.incremental.log);
                    IoUtils.closeQuietly(control.incremental.blocksWritten);
                }
                throw th;
            }
        }

        @Override // android.content.p001pm.IDataLoader
        public void start(int id) {
            if (!DataLoaderService.this.nativeStartDataLoader(id)) {
                Slog.m96e(DataLoaderService.TAG, "Failed to start loader: " + id);
            }
        }

        @Override // android.content.p001pm.IDataLoader
        public void stop(int id) {
            if (!DataLoaderService.this.nativeStopDataLoader(id)) {
                Slog.m90w(DataLoaderService.TAG, "Failed to stop loader: " + id);
            }
        }

        @Override // android.content.p001pm.IDataLoader
        public void destroy(int id) {
            if (!DataLoaderService.this.nativeDestroyDataLoader(id)) {
                Slog.m90w(DataLoaderService.TAG, "Failed to destroy loader: " + id);
            }
        }

        @Override // android.content.p001pm.IDataLoader
        public void prepareImage(int id, InstallationFileParcel[] addedFiles, String[] removedFiles) {
            if (!DataLoaderService.this.nativePrepareImage(id, addedFiles, removedFiles)) {
                Slog.m90w(DataLoaderService.TAG, "Failed to prepare image for data loader: " + id);
            }
        }
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class FileSystemConnector {
        private final long mNativeInstance;

        FileSystemConnector(long nativeInstance) {
            this.mNativeInstance = nativeInstance;
        }

        public void writeData(String name, long offsetBytes, long lengthBytes, ParcelFileDescriptor incomingFd) throws IOException {
            try {
                DataLoaderService.nativeWriteData(this.mNativeInstance, name, offsetBytes, lengthBytes, incomingFd);
            } catch (RuntimeException e) {
                ExceptionUtils.maybeUnwrapIOException(e);
                throw e;
            }
        }
    }
}
