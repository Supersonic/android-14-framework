package android.telephony;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.mbms.DownloadProgressListener;
import android.telephony.mbms.DownloadRequest;
import android.telephony.mbms.DownloadStatusListener;
import android.telephony.mbms.FileInfo;
import android.telephony.mbms.InternalDownloadProgressListener;
import android.telephony.mbms.InternalDownloadSessionCallback;
import android.telephony.mbms.InternalDownloadStatusListener;
import android.telephony.mbms.MbmsDownloadReceiver;
import android.telephony.mbms.MbmsDownloadSessionCallback;
import android.telephony.mbms.MbmsTempFileProvider;
import android.telephony.mbms.MbmsUtils;
import android.telephony.mbms.vendor.IMbmsDownloadService;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class MbmsDownloadSession implements AutoCloseable {
    public static final String DEFAULT_TOP_LEVEL_TEMP_DIRECTORY = "androidMbmsTempFileRoot";
    private static final String DESTINATION_SANITY_CHECK_FILE_NAME = "destinationSanityCheckFile";
    public static final String EXTRA_MBMS_COMPLETED_FILE_URI = "android.telephony.extra.MBMS_COMPLETED_FILE_URI";
    public static final String EXTRA_MBMS_DOWNLOAD_REQUEST = "android.telephony.extra.MBMS_DOWNLOAD_REQUEST";
    public static final String EXTRA_MBMS_DOWNLOAD_RESULT = "android.telephony.extra.MBMS_DOWNLOAD_RESULT";
    public static final String EXTRA_MBMS_FILE_INFO = "android.telephony.extra.MBMS_FILE_INFO";
    private static final int MAX_SERVICE_ANNOUNCEMENT_SIZE = 10240;
    @SystemApi
    public static final String MBMS_DOWNLOAD_SERVICE_ACTION = "android.telephony.action.EmbmsDownload";
    public static final String MBMS_DOWNLOAD_SERVICE_OVERRIDE_METADATA = "mbms-download-service-override";
    public static final int RESULT_CANCELLED = 2;
    public static final int RESULT_DOWNLOAD_FAILURE = 6;
    public static final int RESULT_EXPIRED = 3;
    public static final int RESULT_FILE_ROOT_UNREACHABLE = 8;
    public static final int RESULT_IO_ERROR = 4;
    public static final int RESULT_OUT_OF_STORAGE = 7;
    public static final int RESULT_SERVICE_ID_NOT_DEFINED = 5;
    public static final int RESULT_SUCCESSFUL = 1;
    public static final int STATUS_ACTIVELY_DOWNLOADING = 1;
    public static final int STATUS_PENDING_DOWNLOAD = 2;
    public static final int STATUS_PENDING_DOWNLOAD_WINDOW = 4;
    public static final int STATUS_PENDING_REPAIR = 3;
    public static final int STATUS_UNKNOWN = 0;
    private final Context mContext;
    private final InternalDownloadSessionCallback mInternalCallback;
    private ServiceConnection mServiceConnection;
    private int mSubscriptionId;
    private static final String LOG_TAG = MbmsDownloadSession.class.getSimpleName();
    private static AtomicBoolean sIsInitialized = new AtomicBoolean(false);
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() { // from class: android.telephony.MbmsDownloadSession.1
        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            MbmsDownloadSession.this.sendErrorToApp(3, "Received death notification");
        }
    };
    private AtomicReference<IMbmsDownloadService> mService = new AtomicReference<>(null);
    private final Map<DownloadStatusListener, InternalDownloadStatusListener> mInternalDownloadStatusListeners = new HashMap();
    private final Map<DownloadProgressListener, InternalDownloadProgressListener> mInternalDownloadProgressListeners = new HashMap();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DownloadResultCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface DownloadStatus {
    }

    private MbmsDownloadSession(Context context, Executor executor, int subscriptionId, MbmsDownloadSessionCallback callback) {
        this.mSubscriptionId = -1;
        this.mContext = context;
        this.mSubscriptionId = subscriptionId;
        this.mInternalCallback = new InternalDownloadSessionCallback(callback, executor);
    }

    public static MbmsDownloadSession create(Context context, Executor executor, MbmsDownloadSessionCallback callback) {
        return create(context, executor, SubscriptionManager.getDefaultSubscriptionId(), callback);
    }

    public static MbmsDownloadSession create(Context context, Executor executor, int subscriptionId, final MbmsDownloadSessionCallback callback) {
        if (!sIsInitialized.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot have two active instances");
        }
        MbmsDownloadSession session = new MbmsDownloadSession(context, executor, subscriptionId, callback);
        final int result = session.bindAndInitialize();
        if (result != 0) {
            sIsInitialized.set(false);
            executor.execute(new Runnable() { // from class: android.telephony.MbmsDownloadSession.2
                @Override // java.lang.Runnable
                public void run() {
                    MbmsDownloadSessionCallback.this.onError(result, null);
                }
            });
            return null;
        }
        return session;
    }

    public static int getMaximumServiceAnnouncementSize() {
        return 10240;
    }

    private int bindAndInitialize() {
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: android.telephony.MbmsDownloadSession.3
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMbmsDownloadService downloadService = IMbmsDownloadService.Stub.asInterface(service);
                try {
                    int result = downloadService.initialize(MbmsDownloadSession.this.mSubscriptionId, MbmsDownloadSession.this.mInternalCallback);
                    if (result == -1) {
                        MbmsDownloadSession.this.close();
                        throw new IllegalStateException("Middleware must not return an unknown error code");
                    } else if (result == 0) {
                        try {
                            downloadService.asBinder().linkToDeath(MbmsDownloadSession.this.mDeathRecipient, 0);
                            MbmsDownloadSession.this.mService.set(downloadService);
                        } catch (RemoteException e) {
                            MbmsDownloadSession.this.sendErrorToApp(3, "Middleware lost during initialization");
                            MbmsDownloadSession.sIsInitialized.set(false);
                        }
                    } else {
                        MbmsDownloadSession.this.sendErrorToApp(result, "Error returned during initialization");
                        MbmsDownloadSession.sIsInitialized.set(false);
                    }
                } catch (RemoteException e2) {
                    Log.m110e(MbmsDownloadSession.LOG_TAG, "Service died before initialization");
                    MbmsDownloadSession.sIsInitialized.set(false);
                } catch (RuntimeException e3) {
                    Log.m110e(MbmsDownloadSession.LOG_TAG, "Runtime exception during initialization");
                    MbmsDownloadSession.this.sendErrorToApp(103, e3.toString());
                    MbmsDownloadSession.sIsInitialized.set(false);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName name) {
                Log.m104w(MbmsDownloadSession.LOG_TAG, "bindAndInitialize: Remote service disconnected");
                MbmsDownloadSession.sIsInitialized.set(false);
                MbmsDownloadSession.this.mService.set(null);
            }

            @Override // android.content.ServiceConnection
            public void onNullBinding(ComponentName name) {
                Log.m104w(MbmsDownloadSession.LOG_TAG, "bindAndInitialize: Remote service returned null");
                MbmsDownloadSession.this.sendErrorToApp(3, "Middleware service binding returned null");
                MbmsDownloadSession.sIsInitialized.set(false);
                MbmsDownloadSession.this.mService.set(null);
                MbmsDownloadSession.this.mContext.unbindService(this);
            }
        };
        this.mServiceConnection = serviceConnection;
        return MbmsUtils.startBinding(this.mContext, MBMS_DOWNLOAD_SERVICE_ACTION, serviceConnection);
    }

    public void requestUpdateFileServices(List<String> classList) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            int returnCode = downloadService.requestUpdateFileServices(this.mSubscriptionId, classList);
            if (returnCode == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (returnCode != 0) {
                sendErrorToApp(returnCode, null);
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote process died");
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void addServiceAnnouncement(byte[] contents) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        if (contents.length > 10240) {
            throw new IllegalArgumentException("File too large");
        }
        try {
            int returnCode = downloadService.addServiceAnnouncement(this.mSubscriptionId, contents);
            if (returnCode == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (returnCode != 0) {
                sendErrorToApp(returnCode, null);
            }
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, "Remote process died");
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void setTempFileRootDirectory(File tempFileRootDirectory) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            validateTempFileRootSanity(tempFileRootDirectory);
            try {
                String filePath = tempFileRootDirectory.getCanonicalPath();
                try {
                    int result = downloadService.setTempFileRootDirectory(this.mSubscriptionId, filePath);
                    if (result == -1) {
                        close();
                        throw new IllegalStateException("Middleware must not return an unknown error code");
                    } else if (result == 0) {
                        SharedPreferences prefs = this.mContext.getSharedPreferences(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_FILE_NAME, 0);
                        prefs.edit().putString(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_NAME, filePath).apply();
                    } else {
                        sendErrorToApp(result, null);
                    }
                } catch (RemoteException e) {
                    this.mService.set(null);
                    sIsInitialized.set(false);
                    sendErrorToApp(3, null);
                }
            } catch (IOException e2) {
                throw new IllegalArgumentException("Unable to canonicalize the provided path: " + e2);
            }
        } catch (IOException e3) {
            throw new IllegalStateException("Got IOException checking directory sanity");
        }
    }

    private void validateTempFileRootSanity(File tempFileRootDirectory) throws IOException {
        if (!tempFileRootDirectory.exists()) {
            throw new IllegalArgumentException("Provided directory does not exist");
        }
        if (!tempFileRootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Provided File is not a directory");
        }
        String canonicalTempFilePath = tempFileRootDirectory.getCanonicalPath();
        if (this.mContext.getDataDir().getCanonicalPath().equals(canonicalTempFilePath)) {
            throw new IllegalArgumentException("Temp file root cannot be your data dir");
        }
        if (this.mContext.getCacheDir().getCanonicalPath().equals(canonicalTempFilePath)) {
            throw new IllegalArgumentException("Temp file root cannot be your cache dir");
        }
        if (this.mContext.getFilesDir().getCanonicalPath().equals(canonicalTempFilePath)) {
            throw new IllegalArgumentException("Temp file root cannot be your files dir");
        }
    }

    public File getTempFileRootDirectory() {
        SharedPreferences prefs = this.mContext.getSharedPreferences(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_FILE_NAME, 0);
        String path = prefs.getString(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_NAME, null);
        if (path != null) {
            return new File(path);
        }
        return null;
    }

    public void download(DownloadRequest request) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        SharedPreferences prefs = this.mContext.getSharedPreferences(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_FILE_NAME, 0);
        if (prefs.getString(MbmsTempFileProvider.TEMP_FILE_ROOT_PREF_NAME, null) == null) {
            File tempRootDirectory = new File(this.mContext.getFilesDir(), DEFAULT_TOP_LEVEL_TEMP_DIRECTORY);
            tempRootDirectory.mkdirs();
            setTempFileRootDirectory(tempRootDirectory);
        }
        checkDownloadRequestDestination(request);
        try {
            int result = downloadService.download(request);
            if (result == 0) {
                writeDownloadRequestToken(request);
            } else if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            } else {
                sendErrorToApp(result, null);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public List<DownloadRequest> listPendingDownloads() {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            return downloadService.listPendingDownloads(this.mSubscriptionId);
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
            return Collections.emptyList();
        }
    }

    public void addStatusListener(DownloadRequest request, Executor executor, DownloadStatusListener listener) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        InternalDownloadStatusListener internalListener = new InternalDownloadStatusListener(listener, executor);
        try {
            int result = downloadService.addStatusListener(request, internalListener);
            if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            } else if (result != 0) {
                if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                }
                sendErrorToApp(result, null);
            } else {
                this.mInternalDownloadStatusListeners.put(listener, internalListener);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void removeStatusListener(DownloadRequest request, DownloadStatusListener listener) {
        try {
            IMbmsDownloadService downloadService = this.mService.get();
            if (downloadService == null) {
                throw new IllegalStateException("Middleware not yet bound");
            }
            InternalDownloadStatusListener internalListener = this.mInternalDownloadStatusListeners.get(listener);
            if (internalListener == null) {
                throw new IllegalArgumentException("Provided listener was never registered");
            }
            try {
                int result = downloadService.removeStatusListener(request, internalListener);
                if (result == -1) {
                    close();
                    throw new IllegalStateException("Middleware must not return an unknown error code");
                } else if (result == 0) {
                    InternalDownloadStatusListener internalCallback = this.mInternalDownloadStatusListeners.remove(listener);
                    if (internalCallback != null) {
                        internalCallback.stop();
                    }
                } else if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                } else {
                    sendErrorToApp(result, null);
                    InternalDownloadStatusListener internalCallback2 = this.mInternalDownloadStatusListeners.remove(listener);
                    if (internalCallback2 != null) {
                        internalCallback2.stop();
                    }
                }
            } catch (RemoteException e) {
                this.mService.set(null);
                sIsInitialized.set(false);
                sendErrorToApp(3, null);
                InternalDownloadStatusListener internalCallback3 = this.mInternalDownloadStatusListeners.remove(listener);
                if (internalCallback3 != null) {
                    internalCallback3.stop();
                }
            }
        } catch (Throwable th) {
            InternalDownloadStatusListener internalCallback4 = this.mInternalDownloadStatusListeners.remove(listener);
            if (internalCallback4 != null) {
                internalCallback4.stop();
            }
            throw th;
        }
    }

    public void addProgressListener(DownloadRequest request, Executor executor, DownloadProgressListener listener) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        InternalDownloadProgressListener internalListener = new InternalDownloadProgressListener(listener, executor);
        try {
            int result = downloadService.addProgressListener(request, internalListener);
            if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            } else if (result != 0) {
                if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                }
                sendErrorToApp(result, null);
            } else {
                this.mInternalDownloadProgressListeners.put(listener, internalListener);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void removeProgressListener(DownloadRequest request, DownloadProgressListener listener) {
        try {
            IMbmsDownloadService downloadService = this.mService.get();
            if (downloadService == null) {
                throw new IllegalStateException("Middleware not yet bound");
            }
            InternalDownloadProgressListener internalListener = this.mInternalDownloadProgressListeners.get(listener);
            if (internalListener == null) {
                throw new IllegalArgumentException("Provided listener was never registered");
            }
            try {
                int result = downloadService.removeProgressListener(request, internalListener);
                if (result == -1) {
                    close();
                    throw new IllegalStateException("Middleware must not return an unknown error code");
                } else if (result == 0) {
                    InternalDownloadProgressListener internalCallback = this.mInternalDownloadProgressListeners.remove(listener);
                    if (internalCallback != null) {
                        internalCallback.stop();
                    }
                } else if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                } else {
                    sendErrorToApp(result, null);
                    InternalDownloadProgressListener internalCallback2 = this.mInternalDownloadProgressListeners.remove(listener);
                    if (internalCallback2 != null) {
                        internalCallback2.stop();
                    }
                }
            } catch (RemoteException e) {
                this.mService.set(null);
                sIsInitialized.set(false);
                sendErrorToApp(3, null);
                InternalDownloadProgressListener internalCallback3 = this.mInternalDownloadProgressListeners.remove(listener);
                if (internalCallback3 != null) {
                    internalCallback3.stop();
                }
            }
        } catch (Throwable th) {
            InternalDownloadProgressListener internalCallback4 = this.mInternalDownloadProgressListeners.remove(listener);
            if (internalCallback4 != null) {
                internalCallback4.stop();
            }
            throw th;
        }
    }

    public void cancelDownload(DownloadRequest downloadRequest) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            int result = downloadService.cancelDownload(downloadRequest);
            if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (result != 0) {
                sendErrorToApp(result, null);
            } else {
                deleteDownloadRequestToken(downloadRequest);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void requestDownloadState(DownloadRequest downloadRequest, FileInfo fileInfo) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            int result = downloadService.requestDownloadState(downloadRequest, fileInfo);
            if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (result != 0) {
                if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                }
                if (result == 403) {
                    throw new IllegalArgumentException("Unknown file.");
                }
                sendErrorToApp(result, null);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    public void resetDownloadKnowledge(DownloadRequest downloadRequest) {
        IMbmsDownloadService downloadService = this.mService.get();
        if (downloadService == null) {
            throw new IllegalStateException("Middleware not yet bound");
        }
        try {
            int result = downloadService.resetDownloadKnowledge(downloadRequest);
            if (result == -1) {
                close();
                throw new IllegalStateException("Middleware must not return an unknown error code");
            }
            if (result != 0) {
                if (result == 402) {
                    throw new IllegalArgumentException("Unknown download request.");
                }
                sendErrorToApp(result, null);
            }
        } catch (RemoteException e) {
            this.mService.set(null);
            sIsInitialized.set(false);
            sendErrorToApp(3, null);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        IMbmsDownloadService downloadService;
        try {
            try {
                downloadService = this.mService.get();
            } catch (RemoteException e) {
                Log.m108i(LOG_TAG, "Remote exception while disposing of service");
            }
            if (downloadService != null && this.mServiceConnection != null) {
                downloadService.dispose(this.mSubscriptionId);
                this.mContext.unbindService(this.mServiceConnection);
                return;
            }
            Log.m108i(LOG_TAG, "Service already dead");
        } finally {
            this.mService.set(null);
            sIsInitialized.set(false);
            this.mServiceConnection = null;
            this.mInternalCallback.stop();
        }
    }

    private void writeDownloadRequestToken(DownloadRequest request) {
        File token = getDownloadRequestTokenPath(request);
        if (!token.getParentFile().exists()) {
            token.getParentFile().mkdirs();
        }
        if (token.exists()) {
            Log.m104w(LOG_TAG, "Download token " + token.getName() + " already exists");
            return;
        }
        try {
            if (!token.createNewFile()) {
                throw new RuntimeException("Failed to create download token for request " + request + ". Token location is " + token.getPath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create download token for request " + request + " due to IOException " + e + ". Attempted to write to " + token.getPath());
        }
    }

    private void deleteDownloadRequestToken(DownloadRequest request) {
        File token = getDownloadRequestTokenPath(request);
        if (!token.isFile()) {
            Log.m104w(LOG_TAG, "Attempting to delete non-existent download token at " + token);
        } else if (!token.delete()) {
            Log.m104w(LOG_TAG, "Couldn't delete download token at " + token);
        }
    }

    private void checkDownloadRequestDestination(DownloadRequest request) {
        File downloadRequestDestination = new File(request.getDestinationUri().getPath());
        if (!downloadRequestDestination.isDirectory()) {
            throw new IllegalArgumentException("The destination path must be a directory");
        }
        File testFile = new File(MbmsTempFileProvider.getEmbmsTempFileDir(this.mContext), DESTINATION_SANITY_CHECK_FILE_NAME);
        File testFileDestination = new File(downloadRequestDestination, DESTINATION_SANITY_CHECK_FILE_NAME);
        try {
            try {
                if (!testFile.exists()) {
                    testFile.createNewFile();
                }
                if (!testFile.renameTo(testFileDestination)) {
                    throw new IllegalArgumentException("Destination provided in the download request is invalid -- files in the temp file directory cannot be directly moved there.");
                }
            } catch (IOException e) {
                throw new IllegalStateException("Got IOException while testing out the destination: " + e);
            }
        } finally {
            testFile.delete();
            testFileDestination.delete();
        }
    }

    private File getDownloadRequestTokenPath(DownloadRequest request) {
        File tempFileLocation = MbmsUtils.getEmbmsTempFileDirForService(this.mContext, request.getFileServiceId());
        String downloadTokenFileName = request.getHash() + MbmsDownloadReceiver.DOWNLOAD_TOKEN_SUFFIX;
        return new File(tempFileLocation, downloadTokenFileName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendErrorToApp(int errorCode, String message) {
        this.mInternalCallback.onError(errorCode, message);
    }
}
