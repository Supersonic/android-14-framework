package android.p008os.storage;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.IPackageMoveObserver;
import android.content.res.ObbInfo;
import android.content.res.ObbScanner;
import android.database.Cursor;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Environment;
import android.p008os.FileUtils;
import android.p008os.Handler;
import android.p008os.IVoldTaskListener;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.ParcelableException;
import android.p008os.PersistableBundle;
import android.p008os.Process;
import android.p008os.ProxyFileDescriptorCallback;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemProperties;
import android.p008os.UserHandle;
import android.p008os.storage.IObbActionListener;
import android.p008os.storage.IStorageEventListener;
import android.p008os.storage.IStorageManager;
import android.p008os.storage.StorageManager;
import android.provider.DeviceConfig;
import android.provider.MediaStore;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.DataUnit;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.p028os.AppFuseMount;
import com.android.internal.p028os.FuseAppLoop;
import com.android.internal.p028os.FuseUnavailableMountException;
import com.android.internal.p028os.RoSystemProperties;
import com.android.internal.util.Preconditions;
import dalvik.system.BlockGuard;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* renamed from: android.os.storage.StorageManager */
/* loaded from: classes3.dex */
public class StorageManager {
    public static final String ACTION_CLEAR_APP_CACHE = "android.os.storage.action.CLEAR_APP_CACHE";
    public static final String ACTION_MANAGE_STORAGE = "android.os.storage.action.MANAGE_STORAGE";
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int APP_IO_BLOCKED_REASON_TRANSCODING = 1;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int APP_IO_BLOCKED_REASON_UNKNOWN = 0;
    public static final String CACHE_RESERVE_PERCENT_HIGH_KEY = "cache_reserve_percent_high";
    public static final String CACHE_RESERVE_PERCENT_LOW_KEY = "cache_reserve_percent_low";
    public static final int CRYPT_TYPE_DEFAULT = 1;
    public static final int CRYPT_TYPE_PASSWORD = 0;
    public static final int DEBUG_ADOPTABLE_FORCE_OFF = 2;
    public static final int DEBUG_ADOPTABLE_FORCE_ON = 1;
    public static final int DEBUG_SDCARDFS_FORCE_OFF = 8;
    public static final int DEBUG_SDCARDFS_FORCE_ON = 4;
    public static final int DEBUG_VIRTUAL_DISK = 16;
    public static final int DEFAULT_CACHE_RESERVE_PERCENT_HIGH = 10;
    public static final int DEFAULT_CACHE_RESERVE_PERCENT_LOW = 2;
    public static final int DEFAULT_STORAGE_THRESHOLD_PERCENT_HIGH = 20;
    public static final int DEFAULT_STORAGE_THRESHOLD_PERCENT_LOW = 5;
    public static final int ENCRYPTION_STATE_NONE = 1;
    public static final String EXTRA_REQUESTED_BYTES = "android.os.storage.extra.REQUESTED_BYTES";
    public static final String EXTRA_UUID = "android.os.storage.extra.UUID";
    private static final String FAT_UUID_PREFIX = "fafafafa-fafa-5afa-8afa-fafa";
    @SystemApi
    public static final int FLAG_ALLOCATE_AGGRESSIVE = 1;
    public static final int FLAG_ALLOCATE_CACHE_ONLY = 16;
    public static final int FLAG_ALLOCATE_DEFY_ALL_RESERVED = 2;
    public static final int FLAG_ALLOCATE_DEFY_HALF_RESERVED = 4;
    public static final int FLAG_ALLOCATE_NON_CACHE_ONLY = 8;
    public static final int FLAG_FOR_WRITE = 256;
    public static final int FLAG_INCLUDE_INVISIBLE = 1024;
    public static final int FLAG_INCLUDE_RECENT = 2048;
    public static final int FLAG_INCLUDE_SHARED_PROFILE = 4096;
    public static final int FLAG_REAL_STATE = 512;
    public static final int FLAG_STORAGE_CE = 2;
    public static final int FLAG_STORAGE_DE = 1;
    public static final int FLAG_STORAGE_EXTERNAL = 4;
    public static final int FLAG_STORAGE_SDK = 8;
    public static final int FSTRIM_FLAG_DEEP = 1;
    @SystemApi
    public static final int MOUNT_MODE_EXTERNAL_ANDROID_WRITABLE = 4;
    @SystemApi
    public static final int MOUNT_MODE_EXTERNAL_DEFAULT = 1;
    @SystemApi
    public static final int MOUNT_MODE_EXTERNAL_INSTALLER = 2;
    @SystemApi
    public static final int MOUNT_MODE_EXTERNAL_NONE = 0;
    @SystemApi
    public static final int MOUNT_MODE_EXTERNAL_PASS_THROUGH = 3;
    public static final int PROJECT_ID_EXT_DEFAULT = 1000;
    public static final int PROJECT_ID_EXT_MEDIA_AUDIO = 1001;
    public static final int PROJECT_ID_EXT_MEDIA_IMAGE = 1003;
    public static final int PROJECT_ID_EXT_MEDIA_VIDEO = 1002;
    public static final String PROP_ADOPTABLE = "persist.sys.adoptable";
    public static final String PROP_FORCED_SCOPED_STORAGE_WHITELIST = "forced_scoped_storage_whitelist";
    public static final String PROP_HAS_ADOPTABLE = "vold.has_adoptable";
    public static final String PROP_HAS_RESERVED = "vold.has_reserved";
    public static final String PROP_PRIMARY_PHYSICAL = "ro.vold.primary_physical";
    public static final String PROP_SDCARDFS = "persist.sys.sdcardfs";
    public static final String PROP_VIRTUAL_DISK = "persist.sys.virtual_disk";
    @SystemApi
    public static final int QUOTA_TYPE_MEDIA_AUDIO = 2;
    @SystemApi
    public static final int QUOTA_TYPE_MEDIA_IMAGE = 1;
    @SystemApi
    public static final int QUOTA_TYPE_MEDIA_NONE = 0;
    @SystemApi
    public static final int QUOTA_TYPE_MEDIA_VIDEO = 3;
    public static final String STORAGE_THRESHOLD_PERCENT_HIGH_KEY = "storage_threshold_percent_high";
    public static final String UUID_PRIMARY_PHYSICAL = "primary_physical";
    public static final String UUID_SYSTEM = "system";
    private static final String XATTR_CACHE_GROUP = "user.cache_group";
    private static final String XATTR_CACHE_TOMBSTONE = "user.cache_tombstone";
    private final AppOpsManager mAppOps;
    private final Context mContext;
    private final Looper mLooper;
    private final ContentResolver mResolver;
    private static final String TAG = "StorageManager";
    private static final boolean LOCAL_LOGV = Log.isLoggable(TAG, 2);
    public static final String UUID_PRIVATE_INTERNAL = null;
    public static final UUID UUID_DEFAULT = UUID.fromString("41217664-9172-527a-b3d5-edabb50a7d69");
    public static final UUID UUID_PRIMARY_PHYSICAL_ = UUID.fromString("0f95a519-dae7-5abf-9519-fbd6209e05fd");
    public static final UUID UUID_SYSTEM_ = UUID.fromString("5d258386-e60d-59e3-826d-0089cdd42cc0");
    private static volatile IStorageManager sStorageManager = null;
    private static final long DEFAULT_THRESHOLD_MAX_BYTES = DataUnit.MEBIBYTES.toBytes(500);
    private static final long DEFAULT_FULL_THRESHOLD_BYTES = DataUnit.MEBIBYTES.toBytes(1);
    private final AtomicInteger mNextNonce = new AtomicInteger(0);
    private final ArrayList<StorageEventListenerDelegate> mDelegates = new ArrayList<>();
    private final ObbActionListener mObbActionListener = new ObbActionListener();
    private final Object mFuseAppLoopLock = new Object();
    private FuseAppLoop mFuseAppLoop = null;
    private final IStorageManager mStorageManager = IStorageManager.Stub.asInterface(ServiceManager.getServiceOrThrow("mount"));

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.storage.StorageManager$AllocateFlags */
    /* loaded from: classes3.dex */
    public @interface AllocateFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.storage.StorageManager$AppIoBlockedReason */
    /* loaded from: classes3.dex */
    public @interface AppIoBlockedReason {
    }

    /* renamed from: android.os.storage.StorageManager$MountMode */
    /* loaded from: classes3.dex */
    public @interface MountMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.storage.StorageManager$QuotaType */
    /* loaded from: classes3.dex */
    public @interface QuotaType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.storage.StorageManager$StorageFlags */
    /* loaded from: classes3.dex */
    public @interface StorageFlags {
    }

    private static native boolean setQuotaProjectId(String str, long j);

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.storage.StorageManager$StorageEventListenerDelegate */
    /* loaded from: classes3.dex */
    public class StorageEventListenerDelegate extends IStorageEventListener.Stub {
        final StorageVolumeCallback mCallback;
        final Executor mExecutor;
        final StorageEventListener mListener;

        public StorageEventListenerDelegate(Executor executor, StorageEventListener listener, StorageVolumeCallback callback) {
            this.mExecutor = executor;
            this.mListener = listener;
            this.mCallback = callback;
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onUsbMassStorageConnectionChanged(final boolean connected) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onUsbMassStorageConnectionChanged$0(connected);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUsbMassStorageConnectionChanged$0(boolean connected) {
            this.mListener.onUsbMassStorageConnectionChanged(connected);
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onStorageStateChanged(final String path, final String oldState, final String newState) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onStorageStateChanged$1(path, oldState, newState);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStorageStateChanged$1(String path, String oldState, String newState) {
            this.mListener.onStorageStateChanged(path, oldState, newState);
            if (path != null) {
                for (StorageVolume sv : StorageManager.this.getStorageVolumes()) {
                    if (Objects.equals(path, sv.getPath())) {
                        this.mCallback.onStateChanged(sv);
                    }
                }
            }
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeStateChanged(final VolumeInfo vol, final int oldState, final int newState) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onVolumeStateChanged$2(vol, oldState, newState);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVolumeStateChanged$2(VolumeInfo vol, int oldState, int newState) {
            this.mListener.onVolumeStateChanged(vol, oldState, newState);
            File path = vol.getPathForUser(UserHandle.myUserId());
            if (path != null) {
                for (StorageVolume sv : StorageManager.this.getStorageVolumes()) {
                    if (Objects.equals(path.getAbsolutePath(), sv.getPath())) {
                        this.mCallback.onStateChanged(sv);
                    }
                }
            }
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeRecordChanged(final VolumeRecord rec) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onVolumeRecordChanged$3(rec);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVolumeRecordChanged$3(VolumeRecord rec) {
            this.mListener.onVolumeRecordChanged(rec);
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onVolumeForgotten(final String fsUuid) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onVolumeForgotten$4(fsUuid);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVolumeForgotten$4(String fsUuid) {
            this.mListener.onVolumeForgotten(fsUuid);
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onDiskScanned(final DiskInfo disk, final int volumeCount) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onDiskScanned$5(disk, volumeCount);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDiskScanned$5(DiskInfo disk, int volumeCount) {
            this.mListener.onDiskScanned(disk, volumeCount);
        }

        @Override // android.p008os.storage.IStorageEventListener
        public void onDiskDestroyed(final DiskInfo disk) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.os.storage.StorageManager$StorageEventListenerDelegate$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManager.StorageEventListenerDelegate.this.lambda$onDiskDestroyed$6(disk);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDiskDestroyed$6(DiskInfo disk) {
            this.mListener.onDiskDestroyed(disk);
        }
    }

    /* renamed from: android.os.storage.StorageManager$ObbActionListener */
    /* loaded from: classes3.dex */
    private class ObbActionListener extends IObbActionListener.Stub {
        private SparseArray<ObbListenerDelegate> mListeners;

        private ObbActionListener() {
            this.mListeners = new SparseArray<>();
        }

        @Override // android.p008os.storage.IObbActionListener
        public void onObbResult(String filename, int nonce, int status) {
            ObbListenerDelegate delegate;
            synchronized (this.mListeners) {
                delegate = this.mListeners.get(nonce);
                if (delegate != null) {
                    this.mListeners.remove(nonce);
                }
            }
            if (delegate != null) {
                delegate.sendObbStateChanged(filename, status);
            }
        }

        public int addListener(OnObbStateChangeListener listener) {
            ObbListenerDelegate delegate = new ObbListenerDelegate(listener);
            synchronized (this.mListeners) {
                this.mListeners.put(delegate.nonce, delegate);
            }
            return delegate.nonce;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNextNonce() {
        return this.mNextNonce.getAndIncrement();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.storage.StorageManager$ObbListenerDelegate */
    /* loaded from: classes3.dex */
    public class ObbListenerDelegate {
        private final Handler mHandler;
        private final WeakReference<OnObbStateChangeListener> mObbEventListenerRef;
        private final int nonce;

        ObbListenerDelegate(OnObbStateChangeListener listener) {
            this.nonce = StorageManager.this.getNextNonce();
            this.mObbEventListenerRef = new WeakReference<>(listener);
            this.mHandler = new Handler(StorageManager.this.mLooper) { // from class: android.os.storage.StorageManager.ObbListenerDelegate.1
                @Override // android.p008os.Handler
                public void handleMessage(Message msg) {
                    OnObbStateChangeListener changeListener = ObbListenerDelegate.this.getListener();
                    if (changeListener == null) {
                        return;
                    }
                    changeListener.onObbStateChange((String) msg.obj, msg.arg1);
                }
            };
        }

        OnObbStateChangeListener getListener() {
            WeakReference<OnObbStateChangeListener> weakReference = this.mObbEventListenerRef;
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }

        void sendObbStateChanged(String path, int state) {
            this.mHandler.obtainMessage(0, state, 0, path).sendToTarget();
        }
    }

    @Deprecated
    public static StorageManager from(Context context) {
        return (StorageManager) context.getSystemService(StorageManager.class);
    }

    public StorageManager(Context context, Looper looper) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
        this.mResolver = context.getContentResolver();
        this.mLooper = looper;
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    public void registerListener(StorageEventListener listener) {
        synchronized (this.mDelegates) {
            StorageEventListenerDelegate delegate = new StorageEventListenerDelegate(this.mContext.getMainExecutor(), listener, new StorageVolumeCallback());
            try {
                this.mStorageManager.registerListener(delegate);
                this.mDelegates.add(delegate);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterListener(StorageEventListener listener) {
        synchronized (this.mDelegates) {
            Iterator<StorageEventListenerDelegate> i = this.mDelegates.iterator();
            while (i.hasNext()) {
                StorageEventListenerDelegate delegate = i.next();
                if (delegate.mListener == listener) {
                    try {
                        this.mStorageManager.unregisterListener(delegate);
                        i.remove();
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    /* renamed from: android.os.storage.StorageManager$StorageVolumeCallback */
    /* loaded from: classes3.dex */
    public static class StorageVolumeCallback {
        public void onStateChanged(StorageVolume volume) {
        }
    }

    public void registerStorageVolumeCallback(Executor executor, StorageVolumeCallback callback) {
        synchronized (this.mDelegates) {
            StorageEventListenerDelegate delegate = new StorageEventListenerDelegate(executor, new StorageEventListener(), callback);
            try {
                this.mStorageManager.registerListener(delegate);
                this.mDelegates.add(delegate);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterStorageVolumeCallback(StorageVolumeCallback callback) {
        synchronized (this.mDelegates) {
            Iterator<StorageEventListenerDelegate> i = this.mDelegates.iterator();
            while (i.hasNext()) {
                StorageEventListenerDelegate delegate = i.next();
                if (delegate.mCallback == callback) {
                    try {
                        this.mStorageManager.unregisterListener(delegate);
                        i.remove();
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    @Deprecated
    public void enableUsbMassStorage() {
    }

    @Deprecated
    public void disableUsbMassStorage() {
    }

    @Deprecated
    public boolean isUsbMassStorageConnected() {
        return false;
    }

    @Deprecated
    public boolean isUsbMassStorageEnabled() {
        return false;
    }

    public boolean mountObb(String rawPath, String key, OnObbStateChangeListener listener) {
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        Preconditions.checkArgument(key == null, "mounting encrypted OBBs is no longer supported");
        Preconditions.checkNotNull(listener, "listener cannot be null");
        try {
            String canonicalPath = new File(rawPath).getCanonicalPath();
            int nonce = this.mObbActionListener.addListener(listener);
            this.mStorageManager.mountObb(rawPath, canonicalPath, this.mObbActionListener, nonce, getObbInfo(canonicalPath));
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (IOException e2) {
            throw new IllegalArgumentException("Failed to resolve path: " + rawPath, e2);
        }
    }

    public PendingIntent getManageSpaceActivityIntent(String packageName, int requestCode) {
        try {
            return this.mStorageManager.getManageSpaceActivityIntent(packageName, requestCode);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private ObbInfo getObbInfo(String canonicalPath) {
        try {
            ObbInfo obbInfo = ObbScanner.getObbInfo(canonicalPath);
            return obbInfo;
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't get OBB info for " + canonicalPath, e);
        }
    }

    public boolean unmountObb(String rawPath, boolean force, OnObbStateChangeListener listener) {
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        Preconditions.checkNotNull(listener, "listener cannot be null");
        try {
            int nonce = this.mObbActionListener.addListener(listener);
            this.mStorageManager.unmountObb(rawPath, force, this.mObbActionListener, nonce);
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isObbMounted(String rawPath) {
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        try {
            return this.mStorageManager.isObbMounted(rawPath);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getMountedObbPath(String rawPath) {
        Preconditions.checkNotNull(rawPath, "rawPath cannot be null");
        try {
            return this.mStorageManager.getMountedObbPath(rawPath);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<DiskInfo> getDisks() {
        try {
            return Arrays.asList(this.mStorageManager.getDisks());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public DiskInfo findDiskById(String id) {
        Preconditions.checkNotNull(id);
        for (DiskInfo disk : getDisks()) {
            if (Objects.equals(disk.f329id, id)) {
                return disk;
            }
        }
        return null;
    }

    public VolumeInfo findVolumeById(String id) {
        Preconditions.checkNotNull(id);
        for (VolumeInfo vol : getVolumes()) {
            if (Objects.equals(vol.f330id, id)) {
                return vol;
            }
        }
        return null;
    }

    public VolumeInfo findVolumeByUuid(String fsUuid) {
        Preconditions.checkNotNull(fsUuid);
        for (VolumeInfo vol : getVolumes()) {
            if (Objects.equals(vol.fsUuid, fsUuid)) {
                return vol;
            }
        }
        return null;
    }

    public VolumeRecord findRecordByUuid(String fsUuid) {
        Preconditions.checkNotNull(fsUuid);
        for (VolumeRecord rec : getVolumeRecords()) {
            if (Objects.equals(rec.fsUuid, fsUuid)) {
                return rec;
            }
        }
        return null;
    }

    public VolumeInfo findPrivateForEmulated(VolumeInfo emulatedVol) {
        if (emulatedVol != null) {
            String id = emulatedVol.getId();
            int idx = id.indexOf(NavigationBarInflaterView.GRAVITY_SEPARATOR);
            if (idx != -1) {
                id = id.substring(0, idx);
            }
            return findVolumeById(id.replace(VolumeInfo.ID_EMULATED_INTERNAL, VolumeInfo.ID_PRIVATE_INTERNAL));
        }
        return null;
    }

    public VolumeInfo findEmulatedForPrivate(VolumeInfo privateVol) {
        if (privateVol != null) {
            return findVolumeById(privateVol.getId().replace(VolumeInfo.ID_PRIVATE_INTERNAL, VolumeInfo.ID_EMULATED_INTERNAL) + NavigationBarInflaterView.GRAVITY_SEPARATOR + this.mContext.getUserId());
        }
        return null;
    }

    public VolumeInfo findVolumeByQualifiedUuid(String volumeUuid) {
        if (Objects.equals(UUID_PRIVATE_INTERNAL, volumeUuid)) {
            return findVolumeById(VolumeInfo.ID_PRIVATE_INTERNAL);
        }
        if (Objects.equals(UUID_PRIMARY_PHYSICAL, volumeUuid)) {
            return getPrimaryPhysicalVolume();
        }
        return findVolumeByUuid(volumeUuid);
    }

    public UUID getUuidForPath(File path) throws IOException {
        Preconditions.checkNotNull(path);
        String pathString = path.getCanonicalPath();
        if (FileUtils.contains(Environment.getDataDirectory().getAbsolutePath(), pathString)) {
            return UUID_DEFAULT;
        }
        try {
            VolumeInfo[] volumes = this.mStorageManager.getVolumes(0);
            int length = volumes.length;
            for (int i = 0; i < length; i++) {
                VolumeInfo vol = volumes[i];
                if (vol.path != null && FileUtils.contains(vol.path, pathString) && vol.type != 0 && vol.type != 5) {
                    try {
                        return convert(vol.fsUuid);
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
            throw new FileNotFoundException("Failed to find a storage device for " + path);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public File findPathForUuid(String volumeUuid) throws FileNotFoundException {
        VolumeInfo vol = findVolumeByQualifiedUuid(volumeUuid);
        if (vol != null) {
            return vol.getPath();
        }
        throw new FileNotFoundException("Failed to find a storage device for " + volumeUuid);
    }

    public boolean isAllocationSupported(FileDescriptor fd) {
        try {
            getUuidForPath(ParcelFileDescriptor.getFile(fd));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<VolumeInfo> getVolumes() {
        try {
            return Arrays.asList(this.mStorageManager.getVolumes(0));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<VolumeInfo> getWritablePrivateVolumes() {
        VolumeInfo[] volumes;
        try {
            ArrayList<VolumeInfo> res = new ArrayList<>();
            for (VolumeInfo vol : this.mStorageManager.getVolumes(0)) {
                if (vol.getType() == 1 && vol.isMountedWritable()) {
                    res.add(vol);
                }
            }
            return res;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<VolumeRecord> getVolumeRecords() {
        try {
            return Arrays.asList(this.mStorageManager.getVolumeRecords(0));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getBestVolumeDescription(VolumeInfo vol) {
        VolumeRecord rec;
        if (vol == null) {
            return null;
        }
        if (!TextUtils.isEmpty(vol.fsUuid) && (rec = findRecordByUuid(vol.fsUuid)) != null && !TextUtils.isEmpty(rec.nickname)) {
            return rec.nickname;
        }
        if (!TextUtils.isEmpty(vol.getDescription())) {
            return vol.getDescription();
        }
        if (vol.disk == null) {
            return null;
        }
        return vol.disk.getDescription();
    }

    public VolumeInfo getPrimaryPhysicalVolume() {
        List<VolumeInfo> vols = getVolumes();
        for (VolumeInfo vol : vols) {
            if (vol.isPrimaryPhysical()) {
                return vol;
            }
        }
        return null;
    }

    public void mount(String volId) {
        try {
            this.mStorageManager.mount(volId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unmount(String volId) {
        try {
            this.mStorageManager.unmount(volId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void format(String volId) {
        try {
            this.mStorageManager.format(volId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public long benchmark(String volId) {
        final CompletableFuture<PersistableBundle> result = new CompletableFuture<>();
        benchmark(volId, new IVoldTaskListener.Stub() { // from class: android.os.storage.StorageManager.1
            @Override // android.p008os.IVoldTaskListener
            public void onStatus(int status, PersistableBundle extras) {
            }

            @Override // android.p008os.IVoldTaskListener
            public void onFinished(int status, PersistableBundle extras) {
                result.complete(extras);
            }
        });
        try {
            return result.get(3L, TimeUnit.MINUTES).getLong("run", Long.MAX_VALUE) * 1000000;
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    public void benchmark(String volId, IVoldTaskListener listener) {
        try {
            this.mStorageManager.benchmark(volId, listener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void partitionPublic(String diskId) {
        try {
            this.mStorageManager.partitionPublic(diskId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void partitionPrivate(String diskId) {
        try {
            this.mStorageManager.partitionPrivate(diskId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void partitionMixed(String diskId, int ratio) {
        try {
            this.mStorageManager.partitionMixed(diskId, ratio);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void wipeAdoptableDisks() {
        List<DiskInfo> disks = getDisks();
        for (DiskInfo disk : disks) {
            String diskId = disk.getId();
            if (disk.isAdoptable()) {
                Slog.m98d(TAG, "Found adoptable " + diskId + "; wiping");
                try {
                    this.mStorageManager.partitionPublic(diskId);
                } catch (Exception e) {
                    Slog.m89w(TAG, "Failed to wipe " + diskId + ", but soldiering onward", e);
                }
            } else {
                Slog.m98d(TAG, "Ignorning non-adoptable disk " + disk.getId());
            }
        }
    }

    public void setVolumeNickname(String fsUuid, String nickname) {
        try {
            this.mStorageManager.setVolumeNickname(fsUuid, nickname);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setVolumeInited(String fsUuid, boolean inited) {
        try {
            this.mStorageManager.setVolumeUserFlags(fsUuid, inited ? 1 : 0, 1);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setVolumeSnoozed(String fsUuid, boolean snoozed) {
        try {
            this.mStorageManager.setVolumeUserFlags(fsUuid, snoozed ? 2 : 0, 2);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void forgetVolume(String fsUuid) {
        try {
            this.mStorageManager.forgetVolume(fsUuid);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getPrimaryStorageUuid() {
        try {
            return this.mStorageManager.getPrimaryStorageUuid();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setPrimaryStorageUuid(String volumeUuid, IPackageMoveObserver callback) {
        try {
            this.mStorageManager.setPrimaryStorageUuid(volumeUuid, callback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public StorageVolume getStorageVolume(File file) {
        return getStorageVolume(getVolumeList(), file);
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x004a, code lost:
        if (r0.equals("external_primary") != false) goto L25;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public StorageVolume getStorageVolume(Uri uri) {
        String volumeName = MediaStore.getVolumeName(uri);
        char c = 0;
        if (Objects.equals(volumeName, "external")) {
            Cursor c2 = this.mContext.getContentResolver().query(uri, new String[]{"volume_name"}, null, null);
            try {
                if (c2.moveToFirst()) {
                    volumeName = c2.getString(0);
                }
                if (c2 != null) {
                    c2.close();
                }
            } catch (Throwable th) {
                if (c2 != null) {
                    try {
                        c2.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        switch (volumeName.hashCode()) {
            case -1921573490:
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return getPrimaryStorageVolume();
            default:
                for (StorageVolume vol : getStorageVolumes()) {
                    if (Objects.equals(vol.getMediaStoreVolumeName(), volumeName)) {
                        return vol;
                    }
                }
                throw new IllegalStateException("Unknown volume for " + uri);
        }
    }

    public static StorageVolume getStorageVolume(File file, int userId) {
        return getStorageVolume(getVolumeList(userId, 0), file);
    }

    private static StorageVolume getStorageVolume(StorageVolume[] volumes, File file) {
        if (file == null) {
            return null;
        }
        String path = file.getAbsolutePath();
        if (path.startsWith(ContentResolver.DEPRECATE_DATA_PREFIX)) {
            Uri uri = ContentResolver.translateDeprecatedDataPath(path);
            return ((StorageManager) AppGlobals.getInitialApplication().getSystemService(StorageManager.class)).getStorageVolume(uri);
        }
        try {
            File file2 = file.getCanonicalFile();
            for (StorageVolume volume : volumes) {
                File volumeFile = volume.getPathFile();
                if (FileUtils.contains(volumeFile.getCanonicalFile(), file2)) {
                    return volume;
                }
            }
            return null;
        } catch (IOException e) {
            Slog.m98d(TAG, "Could not get canonical path for " + file);
            return null;
        }
    }

    @Deprecated
    public String getVolumeState(String mountPoint) {
        StorageVolume vol = getStorageVolume(new File(mountPoint));
        if (vol != null) {
            return vol.getState();
        }
        return "unknown";
    }

    public List<StorageVolume> getStorageVolumes() {
        ArrayList<StorageVolume> res = new ArrayList<>();
        Collections.addAll(res, getVolumeList(this.mContext.getUserId(), 1536));
        return res;
    }

    public List<StorageVolume> getStorageVolumesIncludingSharedProfiles() {
        ArrayList<StorageVolume> res = new ArrayList<>();
        Collections.addAll(res, getVolumeList(this.mContext.getUserId(), 5632));
        return res;
    }

    public List<StorageVolume> getRecentStorageVolumes() {
        ArrayList<StorageVolume> res = new ArrayList<>();
        Collections.addAll(res, getVolumeList(this.mContext.getUserId(), 3584));
        return res;
    }

    public StorageVolume getPrimaryStorageVolume() {
        return getVolumeList(this.mContext.getUserId(), 1536)[0];
    }

    public static Pair<String, Long> getPrimaryStoragePathAndSize() {
        return Pair.create(null, Long.valueOf(FileUtils.roundStorageSize(Environment.getDataDirectory().getTotalSpace() + Environment.getRootDirectory().getTotalSpace())));
    }

    public long getPrimaryStorageSize() {
        return FileUtils.roundStorageSize(Environment.getDataDirectory().getTotalSpace() + Environment.getRootDirectory().getTotalSpace());
    }

    public void mkdirs(File file) {
        BlockGuard.getVmPolicy().onPathAccess(file.getAbsolutePath());
        try {
            this.mStorageManager.mkdirs(this.mContext.getOpPackageName(), file.getAbsolutePath());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public StorageVolume[] getVolumeList() {
        return getVolumeList(this.mContext.getUserId(), 0);
    }

    public static StorageVolume[] getVolumeList(int userId, int flags) {
        IStorageManager storageManager = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        try {
            String packageName = ActivityThread.currentOpPackageName();
            if (packageName == null) {
                String[] packageNames = ActivityThread.getPackageManager().getPackagesForUid(Process.myUid());
                if (packageNames != null && packageNames.length > 0) {
                    packageName = packageNames[0];
                }
                Log.m104w(TAG, "Missing package names; no storage volumes available");
                return new StorageVolume[0];
            }
            return storageManager.getVolumeList(userId, packageName, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public String[] getVolumePaths() {
        StorageVolume[] volumes = getVolumeList();
        int count = volumes.length;
        String[] paths = new String[count];
        for (int i = 0; i < count; i++) {
            paths[i] = volumes[i].getPath();
        }
        return paths;
    }

    public StorageVolume getPrimaryVolume() {
        return getPrimaryVolume(getVolumeList());
    }

    public static StorageVolume getPrimaryVolume(StorageVolume[] volumes) {
        for (StorageVolume volume : volumes) {
            if (volume.isPrimary()) {
                return volume;
            }
        }
        throw new IllegalStateException("Missing primary storage");
    }

    public long getStorageBytesUntilLow(File path) {
        return path.getUsableSpace() - getStorageFullBytes(path);
    }

    public long getStorageLowBytes(File path) {
        long lowPercent = Settings.Global.getInt(this.mResolver, Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE, 5);
        long lowBytes = (path.getTotalSpace() * lowPercent) / 100;
        long maxLowBytes = Settings.Global.getLong(this.mResolver, Settings.Global.SYS_STORAGE_THRESHOLD_MAX_BYTES, DEFAULT_THRESHOLD_MAX_BYTES);
        return Math.min(lowBytes, maxLowBytes);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public long computeStorageCacheBytes(File path) {
        int storageThresholdPercentHigh = DeviceConfig.getInt("storage_native_boot", STORAGE_THRESHOLD_PERCENT_HIGH_KEY, 20);
        int cacheReservePercentHigh = DeviceConfig.getInt("storage_native_boot", CACHE_RESERVE_PERCENT_HIGH_KEY, 10);
        int cacheReservePercentLow = DeviceConfig.getInt("storage_native_boot", CACHE_RESERVE_PERCENT_LOW_KEY, 2);
        long totalBytes = path.getTotalSpace();
        long usableBytes = path.getUsableSpace();
        long storageThresholdHighBytes = (storageThresholdPercentHigh * totalBytes) / 100;
        long storageThresholdLowBytes = getStorageLowBytes(path);
        if (usableBytes > storageThresholdHighBytes) {
            long result = (cacheReservePercentHigh * totalBytes) / 100;
            return result;
        } else if (usableBytes < storageThresholdLowBytes) {
            long result2 = (cacheReservePercentLow * totalBytes) / 100;
            return result2;
        } else {
            double slope = ((cacheReservePercentHigh - cacheReservePercentLow) * totalBytes) / ((storageThresholdHighBytes - storageThresholdLowBytes) * 100.0d);
            double intercept = ((cacheReservePercentLow * totalBytes) / 100.0d) - (storageThresholdLowBytes * slope);
            long result3 = Math.round((usableBytes * slope) + intercept);
            return result3;
        }
    }

    public long getStorageCacheBytes(File path, int flags) {
        if ((flags & 1) == 0 && (flags & 2) == 0) {
            if ((flags & 4) != 0) {
                return computeStorageCacheBytes(path) / 2;
            }
            return computeStorageCacheBytes(path);
        }
        return 0L;
    }

    public long getStorageFullBytes(File path) {
        return Settings.Global.getLong(this.mResolver, Settings.Global.SYS_STORAGE_FULL_THRESHOLD_BYTES, DEFAULT_FULL_THRESHOLD_BYTES);
    }

    public void createUserKey(int userId, int serialNumber, boolean ephemeral) {
        try {
            this.mStorageManager.createUserKey(userId, serialNumber, ephemeral);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void destroyUserKey(int userId) {
        try {
            this.mStorageManager.destroyUserKey(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void lockUserKey(int userId) {
        try {
            this.mStorageManager.lockUserKey(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void prepareUserStorage(String volumeUuid, int userId, int serialNumber, int flags) {
        try {
            this.mStorageManager.prepareUserStorage(volumeUuid, userId, serialNumber, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void destroyUserStorage(String volumeUuid, int userId, int flags) {
        try {
            this.mStorageManager.destroyUserStorage(volumeUuid, userId, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean isUserKeyUnlocked(int userId) {
        if (sStorageManager == null) {
            sStorageManager = IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        }
        if (sStorageManager == null) {
            Slog.m90w(TAG, "Early during boot, assuming locked");
            return false;
        }
        long token = Binder.clearCallingIdentity();
        try {
            try {
                return sStorageManager.isUserKeyUnlocked(userId);
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public boolean isEncrypted(File file) {
        if (FileUtils.contains(Environment.getDataDirectory(), file)) {
            return isEncrypted();
        }
        if (FileUtils.contains(Environment.getExpandDirectory(), file)) {
            return true;
        }
        return false;
    }

    public static boolean isEncrypted() {
        return RoSystemProperties.CRYPTO_ENCRYPTED;
    }

    public static boolean isFileEncrypted() {
        if (!isEncrypted()) {
            return false;
        }
        return RoSystemProperties.CRYPTO_FILE_ENCRYPTED;
    }

    @Deprecated
    public static boolean isFileEncryptedNativeOnly() {
        return isFileEncrypted();
    }

    @Deprecated
    public static boolean isFileEncryptedNativeOrEmulated() {
        return isFileEncrypted();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static boolean hasAdoptable() {
        char c;
        String str = SystemProperties.get(PROP_ADOPTABLE);
        switch (str.hashCode()) {
            case 464944051:
                if (str.equals("force_on")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1528363547:
                if (str.equals("force_off")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return true;
            case 1:
                return false;
            default:
                return SystemProperties.getBoolean(PROP_HAS_ADOPTABLE, false);
        }
    }

    @SystemApi
    public static boolean hasIsolatedStorage() {
        return false;
    }

    @Deprecated
    public static File maybeTranslateEmulatedPathToInternal(File path) {
        return path;
    }

    public File translateAppToSystem(File file, int pid, int uid) {
        return file;
    }

    public File translateSystemToApp(File file, int pid, int uid) {
        return file;
    }

    public static boolean checkPermissionAndAppOp(Context context, boolean enforce, int pid, int uid, String packageName, String featureId, String permission, int op) {
        return checkPermissionAndAppOp(context, enforce, pid, uid, packageName, featureId, permission, op, true);
    }

    public static boolean checkPermissionAndCheckOp(Context context, boolean enforce, int pid, int uid, String packageName, String permission, int op) {
        return checkPermissionAndAppOp(context, enforce, pid, uid, packageName, null, permission, op, false);
    }

    private static boolean checkPermissionAndAppOp(Context context, boolean enforce, int pid, int uid, String packageName, String featureId, String permission, int op, boolean note) {
        int mode;
        if (context.checkPermission(permission, pid, uid) != 0) {
            if (enforce) {
                throw new SecurityException("Permission " + permission + " denied for package " + packageName);
            }
            return false;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        if (note) {
            mode = appOps.noteOpNoThrow(op, uid, packageName, featureId, (String) null);
        } else {
            try {
                appOps.checkPackage(uid, packageName);
                mode = appOps.checkOpNoThrow(op, uid, packageName);
            } catch (SecurityException e) {
                if (enforce) {
                    throw e;
                }
                return false;
            }
        }
        switch (mode) {
            case 0:
                return true;
            case 1:
            case 2:
            case 3:
                if (enforce) {
                    throw new SecurityException("Op " + AppOpsManager.opToName(op) + " " + AppOpsManager.modeToName(mode) + " for package " + packageName);
                }
                return false;
            default:
                throw new IllegalStateException(AppOpsManager.opToName(op) + " has unknown mode " + AppOpsManager.modeToName(mode));
        }
    }

    private boolean checkPermissionAndAppOp(boolean enforce, int pid, int uid, String packageName, String featureId, String permission, int op) {
        return checkPermissionAndAppOp(this.mContext, enforce, pid, uid, packageName, featureId, permission, op);
    }

    private boolean noteAppOpAllowingLegacy(boolean enforce, int pid, int uid, String packageName, String featureId, int op) {
        int mode = this.mAppOps.noteOpNoThrow(op, uid, packageName, featureId, (String) null);
        switch (mode) {
            case 0:
                return true;
            case 1:
            case 2:
            case 3:
                if (this.mAppOps.checkOpNoThrow(87, uid, packageName) == 0) {
                    return true;
                }
                if (enforce) {
                    throw new SecurityException("Op " + AppOpsManager.opToName(op) + " " + AppOpsManager.modeToName(mode) + " for package " + packageName);
                }
                return false;
            default:
                throw new IllegalStateException(AppOpsManager.opToName(op) + " has unknown mode " + AppOpsManager.modeToName(mode));
        }
    }

    @Deprecated
    public boolean checkPermissionReadImages(boolean enforce, int pid, int uid, String packageName, String featureId) {
        if (!checkExternalStoragePermissionAndAppOp(enforce, pid, uid, packageName, featureId, Manifest.C0000permission.READ_EXTERNAL_STORAGE, 59)) {
            return false;
        }
        return noteAppOpAllowingLegacy(enforce, pid, uid, packageName, featureId, 85);
    }

    private boolean checkExternalStoragePermissionAndAppOp(boolean enforce, int pid, int uid, String packageName, String featureId, String permission, int op) {
        int mode = this.mAppOps.noteOpNoThrow(92, uid, packageName, featureId, (String) null);
        if (mode == 0) {
            return true;
        }
        if (mode == 3 && this.mContext.checkPermission(Manifest.C0000permission.MANAGE_EXTERNAL_STORAGE, pid, uid) == 0) {
            return true;
        }
        return checkPermissionAndAppOp(enforce, pid, uid, packageName, featureId, permission, op);
    }

    public ParcelFileDescriptor openProxyFileDescriptor(int mode, ProxyFileDescriptorCallback callback, Handler handler, ThreadFactory factory) throws IOException {
        ParcelFileDescriptor pfd;
        Preconditions.checkNotNull(callback);
        MetricsLogger.count(this.mContext, "storage_open_proxy_file_descriptor", 1);
        while (true) {
            try {
                synchronized (this.mFuseAppLoopLock) {
                    boolean newlyCreated = false;
                    if (this.mFuseAppLoop == null) {
                        AppFuseMount mount = this.mStorageManager.mountProxyFileDescriptorBridge();
                        if (mount == null) {
                            throw new IOException("Failed to mount proxy bridge");
                        }
                        this.mFuseAppLoop = new FuseAppLoop(mount.mountPointId, mount.f902fd, factory);
                        newlyCreated = true;
                    }
                    if (handler == null) {
                        handler = new Handler(Looper.getMainLooper());
                    }
                    try {
                        int fileId = this.mFuseAppLoop.registerCallback(callback, handler);
                        pfd = this.mStorageManager.openProxyFileDescriptor(this.mFuseAppLoop.getMountPointId(), fileId, mode);
                        if (pfd == null) {
                            this.mFuseAppLoop.unregisterCallback(fileId);
                            throw new FuseUnavailableMountException(this.mFuseAppLoop.getMountPointId());
                            break;
                        }
                    } catch (FuseUnavailableMountException exception) {
                        if (newlyCreated) {
                            throw new IOException(exception);
                        }
                        this.mFuseAppLoop = null;
                    }
                }
                return pfd;
            } catch (RemoteException e) {
                throw new IOException(e);
            }
        }
    }

    public ParcelFileDescriptor openProxyFileDescriptor(int mode, ProxyFileDescriptorCallback callback) throws IOException {
        return openProxyFileDescriptor(mode, callback, null, null);
    }

    public ParcelFileDescriptor openProxyFileDescriptor(int mode, ProxyFileDescriptorCallback callback, Handler handler) throws IOException {
        Preconditions.checkNotNull(handler);
        return openProxyFileDescriptor(mode, callback, handler, null);
    }

    public int getProxyFileDescriptorMountPointId() {
        int mountPointId;
        synchronized (this.mFuseAppLoopLock) {
            FuseAppLoop fuseAppLoop = this.mFuseAppLoop;
            mountPointId = fuseAppLoop != null ? fuseAppLoop.getMountPointId() : -1;
        }
        return mountPointId;
    }

    public long getCacheQuotaBytes(UUID storageUuid) throws IOException {
        try {
            ApplicationInfo app = this.mContext.getApplicationInfo();
            return this.mStorageManager.getCacheQuotaBytes(convert(storageUuid), app.uid);
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public long getCacheSizeBytes(UUID storageUuid) throws IOException {
        try {
            ApplicationInfo app = this.mContext.getApplicationInfo();
            return this.mStorageManager.getCacheSizeBytes(convert(storageUuid), app.uid);
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public long getAllocatableBytes(UUID storageUuid) throws IOException {
        return getAllocatableBytes(storageUuid, 0);
    }

    @SystemApi
    public long getAllocatableBytes(UUID storageUuid, int flags) throws IOException {
        try {
            return this.mStorageManager.getAllocatableBytes(convert(storageUuid), flags, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    public void allocateBytes(UUID storageUuid, long bytes) throws IOException {
        allocateBytes(storageUuid, bytes, 0);
    }

    @SystemApi
    public void allocateBytes(UUID storageUuid, long bytes, int flags) throws IOException {
        try {
            this.mStorageManager.allocateBytes(convert(storageUuid), bytes, flags, this.mContext.getOpPackageName());
        } catch (ParcelableException e) {
            e.maybeRethrow(IOException.class);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getExternalStorageMountMode(int uid, String packageName) {
        try {
            return this.mStorageManager.getExternalStorageMountMode(uid, packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void allocateBytes(FileDescriptor fd, long bytes) throws IOException {
        allocateBytes(fd, bytes, 0);
    }

    @SystemApi
    public void allocateBytes(FileDescriptor fd, long bytes, int flags) throws IOException {
        File file = ParcelFileDescriptor.getFile(fd);
        UUID uuid = getUuidForPath(file);
        for (int i = 0; i < 3; i++) {
            try {
                long haveBytes = Os.fstat(fd).st_blocks * 512;
                long needBytes = bytes - haveBytes;
                if (needBytes > 0) {
                    allocateBytes(uuid, needBytes, flags);
                }
                try {
                    Os.posix_fallocate(fd, 0L, bytes);
                    return;
                } catch (ErrnoException e) {
                    if (e.errno != OsConstants.ENOSYS && e.errno != OsConstants.ENOTSUP) {
                        throw e;
                    }
                    Log.m104w(TAG, "fallocate() not supported; falling back to ftruncate()");
                    Os.ftruncate(fd, bytes);
                    return;
                }
            } catch (ErrnoException e2) {
                if (e2.errno == OsConstants.ENOSPC) {
                    Log.m104w(TAG, "Odd, not enough space; let's try again?");
                } else {
                    throw e2.rethrowAsIOException();
                }
            }
        }
        throw new IOException("Well this is embarassing; we can't allocate " + bytes + " for " + file);
    }

    private static long getProjectIdForUser(int userId, int projectId) {
        return (100000 * userId) + projectId;
    }

    @SystemApi
    public void updateExternalStorageFileQuotaType(File path, int quotaType) throws IOException {
        long projectId;
        String filePath = path.getCanonicalPath();
        int volFlags = 1536;
        if (this.mContext.checkSelfPermission(Manifest.C0000permission.MANAGE_EXTERNAL_STORAGE) == 0) {
            volFlags = 1536 | 4096;
        }
        StorageVolume[] availableVolumes = getVolumeList(this.mContext.getUserId(), volFlags);
        StorageVolume volume = getStorageVolume(availableVolumes, path);
        if (volume == null) {
            Log.m104w(TAG, "Failed to update quota type for " + filePath);
        } else if (!volume.isEmulated()) {
        } else {
            int userId = volume.getOwner().getIdentifier();
            if (userId < 0) {
                throw new IllegalStateException("Failed to update quota type for " + filePath);
            }
            switch (quotaType) {
                case 0:
                    projectId = getProjectIdForUser(userId, 1000);
                    break;
                case 1:
                    projectId = getProjectIdForUser(userId, 1003);
                    break;
                case 2:
                    projectId = getProjectIdForUser(userId, 1001);
                    break;
                case 3:
                    projectId = getProjectIdForUser(userId, 1002);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid quota type: " + quotaType);
            }
            if (!setQuotaProjectId(filePath, projectId)) {
                throw new IOException("Failed to update quota type for " + filePath);
            }
        }
    }

    public void fixupAppDir(File path) {
        try {
            this.mStorageManager.fixupAppDir(path.getCanonicalPath());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (IOException e2) {
            Log.m109e(TAG, "Failed to get canonical path for " + path.getPath(), e2);
        }
    }

    private static void setCacheBehavior(File path, String name, boolean enabled) throws IOException {
        if (!path.isDirectory()) {
            throw new IOException("Cache behavior can only be set on directories");
        }
        if (enabled) {
            try {
                Os.setxattr(path.getAbsolutePath(), name, "1".getBytes(StandardCharsets.UTF_8), 0);
                return;
            } catch (ErrnoException e) {
                throw e.rethrowAsIOException();
            }
        }
        try {
            Os.removexattr(path.getAbsolutePath(), name);
        } catch (ErrnoException e2) {
            if (e2.errno != OsConstants.ENODATA) {
                throw e2.rethrowAsIOException();
            }
        }
    }

    private static boolean isCacheBehavior(File path, String name) throws IOException {
        try {
            Os.getxattr(path.getAbsolutePath(), name);
            return true;
        } catch (ErrnoException e) {
            if (e.errno != OsConstants.ENODATA) {
                throw e.rethrowAsIOException();
            }
            return false;
        }
    }

    public void setCacheBehaviorGroup(File path, boolean group) throws IOException {
        setCacheBehavior(path, XATTR_CACHE_GROUP, group);
    }

    public boolean isCacheBehaviorGroup(File path) throws IOException {
        return isCacheBehavior(path, XATTR_CACHE_GROUP);
    }

    public void setCacheBehaviorTombstone(File path, boolean tombstone) throws IOException {
        setCacheBehavior(path, XATTR_CACHE_TOMBSTONE, tombstone);
    }

    public boolean isCacheBehaviorTombstone(File path) throws IOException {
        return isCacheBehavior(path, XATTR_CACHE_TOMBSTONE);
    }

    private static boolean isFatVolumeIdentifier(String uuid) {
        return uuid.length() == 9 && uuid.charAt(4) == '-';
    }

    public static UUID convert(String uuid) {
        if (Objects.equals(uuid, UUID_PRIVATE_INTERNAL)) {
            return UUID_DEFAULT;
        }
        if (Objects.equals(uuid, UUID_PRIMARY_PHYSICAL)) {
            return UUID_PRIMARY_PHYSICAL_;
        }
        if (Objects.equals(uuid, "system")) {
            return UUID_SYSTEM_;
        }
        if (isFatVolumeIdentifier(uuid)) {
            return UUID.fromString(FAT_UUID_PREFIX + uuid.replace(NativeLibraryHelper.CLEAR_ABI_OVERRIDE, ""));
        }
        return UUID.fromString(uuid);
    }

    public static String convert(UUID storageUuid) {
        if (UUID_DEFAULT.equals(storageUuid)) {
            return UUID_PRIVATE_INTERNAL;
        }
        if (UUID_PRIMARY_PHYSICAL_.equals(storageUuid)) {
            return UUID_PRIMARY_PHYSICAL;
        }
        if (UUID_SYSTEM_.equals(storageUuid)) {
            return "system";
        }
        String uuidString = storageUuid.toString();
        if (uuidString.startsWith(FAT_UUID_PREFIX)) {
            String fatStr = uuidString.substring(FAT_UUID_PREFIX.length()).toUpperCase(Locale.US);
            return fatStr.substring(0, 4) + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + fatStr.substring(4);
        }
        return storageUuid.toString();
    }

    public boolean isCheckpointSupported() {
        try {
            return this.mStorageManager.supportsCheckpoint();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void notifyAppIoBlocked(UUID volumeUuid, int uid, int tid, int reason) {
        Objects.requireNonNull(volumeUuid);
        try {
            this.mStorageManager.notifyAppIoBlocked(convert(volumeUuid), uid, tid, reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void notifyAppIoResumed(UUID volumeUuid, int uid, int tid, int reason) {
        Objects.requireNonNull(volumeUuid);
        try {
            this.mStorageManager.notifyAppIoResumed(convert(volumeUuid), uid, tid, reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isAppIoBlocked(UUID volumeUuid, int uid, int tid, int reason) {
        Objects.requireNonNull(volumeUuid);
        try {
            return this.mStorageManager.isAppIoBlocked(convert(volumeUuid), uid, tid, reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void setCloudMediaProvider(String authority) {
        try {
            this.mStorageManager.setCloudMediaProvider(authority);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public String getCloudMediaProvider() {
        try {
            return this.mStorageManager.getCloudMediaProvider();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
