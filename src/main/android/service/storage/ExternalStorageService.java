package android.service.storage;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ParcelableException;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.storage.StorageManager;
import android.p008os.storage.StorageVolume;
import android.service.storage.ExternalStorageService;
import android.service.storage.IExternalStorageService;
import com.android.internal.p028os.BackgroundThread;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;
@SystemApi
/* loaded from: classes3.dex */
public abstract class ExternalStorageService extends Service {
    public static final String EXTRA_ERROR = "android.service.storage.extra.error";
    public static final String EXTRA_PACKAGE_NAME = "android.service.storage.extra.package_name";
    public static final String EXTRA_SESSION_ID = "android.service.storage.extra.session_id";
    public static final int FLAG_SESSION_ATTRIBUTE_INDEXABLE = 2;
    public static final int FLAG_SESSION_TYPE_FUSE = 1;
    public static final String SERVICE_INTERFACE = "android.service.storage.ExternalStorageService";
    private final ExternalStorageServiceWrapper mWrapper = new ExternalStorageServiceWrapper();
    private final Handler mHandler = BackgroundThread.getHandler();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SessionFlag {
    }

    public abstract void onEndSession(String str) throws IOException;

    public abstract void onStartSession(String str, int i, ParcelFileDescriptor parcelFileDescriptor, File file, File file2) throws IOException;

    public abstract void onVolumeStateChanged(StorageVolume storageVolume) throws IOException;

    public void onFreeCache(UUID volumeUuid, long bytes) throws IOException {
        throw new UnsupportedOperationException("onFreeCacheRequested not implemented");
    }

    public void onAnrDelayStarted(String packageName, int uid, int tid, int reason) {
        throw new UnsupportedOperationException("onAnrDelayStarted not implemented");
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ExternalStorageServiceWrapper extends IExternalStorageService.Stub {
        private ExternalStorageServiceWrapper() {
        }

        @Override // android.service.storage.IExternalStorageService
        public void startSession(final String sessionId, final int flag, final ParcelFileDescriptor deviceFd, final String upperPath, final String lowerPath, final RemoteCallback callback) throws RemoteException {
            ExternalStorageService.this.mHandler.post(new Runnable() { // from class: android.service.storage.ExternalStorageService$ExternalStorageServiceWrapper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ExternalStorageService.ExternalStorageServiceWrapper.this.lambda$startSession$0(sessionId, flag, deviceFd, upperPath, lowerPath, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startSession$0(String sessionId, int flag, ParcelFileDescriptor deviceFd, String upperPath, String lowerPath, RemoteCallback callback) {
            try {
                ExternalStorageService.this.onStartSession(sessionId, flag, deviceFd, new File(upperPath), new File(lowerPath));
                sendResult(sessionId, null, callback);
            } catch (Throwable t) {
                sendResult(sessionId, t, callback);
            }
        }

        @Override // android.service.storage.IExternalStorageService
        public void notifyVolumeStateChanged(final String sessionId, final StorageVolume vol, final RemoteCallback callback) {
            ExternalStorageService.this.mHandler.post(new Runnable() { // from class: android.service.storage.ExternalStorageService$ExternalStorageServiceWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ExternalStorageService.ExternalStorageServiceWrapper.this.lambda$notifyVolumeStateChanged$1(vol, sessionId, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyVolumeStateChanged$1(StorageVolume vol, String sessionId, RemoteCallback callback) {
            try {
                ExternalStorageService.this.onVolumeStateChanged(vol);
                sendResult(sessionId, null, callback);
            } catch (Throwable t) {
                sendResult(sessionId, t, callback);
            }
        }

        @Override // android.service.storage.IExternalStorageService
        public void freeCache(final String sessionId, final String volumeUuid, final long bytes, final RemoteCallback callback) {
            ExternalStorageService.this.mHandler.post(new Runnable() { // from class: android.service.storage.ExternalStorageService$ExternalStorageServiceWrapper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ExternalStorageService.ExternalStorageServiceWrapper.this.lambda$freeCache$2(volumeUuid, bytes, sessionId, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$freeCache$2(String volumeUuid, long bytes, String sessionId, RemoteCallback callback) {
            try {
                ExternalStorageService.this.onFreeCache(StorageManager.convert(volumeUuid), bytes);
                sendResult(sessionId, null, callback);
            } catch (Throwable t) {
                sendResult(sessionId, t, callback);
            }
        }

        @Override // android.service.storage.IExternalStorageService
        public void endSession(final String sessionId, final RemoteCallback callback) throws RemoteException {
            ExternalStorageService.this.mHandler.post(new Runnable() { // from class: android.service.storage.ExternalStorageService$ExternalStorageServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ExternalStorageService.ExternalStorageServiceWrapper.this.lambda$endSession$3(sessionId, callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$endSession$3(String sessionId, RemoteCallback callback) {
            try {
                ExternalStorageService.this.onEndSession(sessionId);
                sendResult(sessionId, null, callback);
            } catch (Throwable t) {
                sendResult(sessionId, t, callback);
            }
        }

        @Override // android.service.storage.IExternalStorageService
        public void notifyAnrDelayStarted(final String packageName, final int uid, final int tid, final int reason) throws RemoteException {
            ExternalStorageService.this.mHandler.post(new Runnable() { // from class: android.service.storage.ExternalStorageService$ExternalStorageServiceWrapper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ExternalStorageService.ExternalStorageServiceWrapper.this.lambda$notifyAnrDelayStarted$4(packageName, uid, tid, reason);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyAnrDelayStarted$4(String packageName, int uid, int tid, int reason) {
            try {
                ExternalStorageService.this.onAnrDelayStarted(packageName, uid, tid, reason);
            } catch (Throwable th) {
            }
        }

        private void sendResult(String sessionId, Throwable throwable, RemoteCallback callback) {
            Bundle bundle = new Bundle();
            bundle.putString(ExternalStorageService.EXTRA_SESSION_ID, sessionId);
            if (throwable != null) {
                bundle.putParcelable(ExternalStorageService.EXTRA_ERROR, new ParcelableException(throwable));
            }
            callback.sendResult(bundle);
        }
    }
}
