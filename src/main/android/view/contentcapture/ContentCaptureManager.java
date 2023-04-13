package android.view.contentcapture;

import android.annotation.SystemApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Dumpable;
import android.util.Log;
import android.util.Slog;
import android.view.WindowManager;
import android.view.contentcapture.IContentCaptureManager;
import android.view.contentcapture.IDataShareWriteAdapter;
import com.android.internal.util.SyncResultReceiver;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public final class ContentCaptureManager {
    public static final int DATA_SHARE_ERROR_CONCURRENT_REQUEST = 2;
    public static final int DATA_SHARE_ERROR_TIMEOUT_INTERRUPTED = 3;
    public static final int DATA_SHARE_ERROR_UNKNOWN = 1;
    public static final boolean DEBUG = false;
    public static final boolean DEFAULT_DISABLE_FLUSH_FOR_VIEW_TREE_APPEARING = false;
    public static final int DEFAULT_IDLE_FLUSHING_FREQUENCY_MS = 5000;
    public static final int DEFAULT_LOG_HISTORY_SIZE = 10;
    public static final int DEFAULT_MAX_BUFFER_SIZE = 500;
    public static final int DEFAULT_TEXT_CHANGE_FLUSHING_FREQUENCY_MS = 1000;
    public static final String DEVICE_CONFIG_PROPERTY_DISABLE_FLUSH_FOR_VIEW_TREE_APPEARING = "disable_flush_for_view_tree_appearing";
    public static final String DEVICE_CONFIG_PROPERTY_IDLE_FLUSH_FREQUENCY = "idle_flush_frequency";
    public static final String DEVICE_CONFIG_PROPERTY_IDLE_UNBIND_TIMEOUT = "idle_unbind_timeout";
    public static final String DEVICE_CONFIG_PROPERTY_LOGGING_LEVEL = "logging_level";
    public static final String DEVICE_CONFIG_PROPERTY_LOG_HISTORY_SIZE = "log_history_size";
    public static final String DEVICE_CONFIG_PROPERTY_MAX_BUFFER_SIZE = "max_buffer_size";
    public static final String DEVICE_CONFIG_PROPERTY_REPORT_LIST_VIEW_CHILDREN = "report_list_view_children";
    public static final String DEVICE_CONFIG_PROPERTY_SERVICE_EXPLICITLY_ENABLED = "service_explicitly_enabled";
    public static final String DEVICE_CONFIG_PROPERTY_TEXT_CHANGE_FLUSH_FREQUENCY = "text_change_flush_frequency";
    public static final String DUMPABLE_NAME = "ContentCaptureManager";
    public static final int LOGGING_LEVEL_DEBUG = 1;
    public static final int LOGGING_LEVEL_OFF = 0;
    public static final int LOGGING_LEVEL_VERBOSE = 2;
    @SystemApi
    public static final int NO_SESSION_ID = 0;
    public static final int RESULT_CODE_FALSE = 2;
    public static final int RESULT_CODE_OK = 0;
    public static final int RESULT_CODE_SECURITY_EXCEPTION = -1;
    public static final int RESULT_CODE_TRUE = 1;
    private static final int SYNC_CALLS_TIMEOUT_MS = 5000;
    private static final String TAG = ContentCaptureManager.class.getSimpleName();
    private final StrippedContext mContext;
    private final LocalDataShareAdapterResourceManager mDataShareAdapterResourceManager;
    private Dumper mDumpable;
    private int mFlags;
    private final Handler mHandler;
    private final Object mLock = new Object();
    private MainContentCaptureSession mMainSession;
    final ContentCaptureOptions mOptions;
    private final IContentCaptureManager mService;

    /* loaded from: classes4.dex */
    public interface ContentCaptureClient {
        ComponentName contentCaptureClientGetComponentName();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface DataShareError {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface LoggingLevel {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface MyRunnable {
        void run(SyncResultReceiver syncResultReceiver) throws RemoteException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class StrippedContext {
        final String mContext;
        final String mPackageName;
        final int mUserId;

        private StrippedContext(Context context) {
            this.mPackageName = context.getPackageName();
            this.mContext = context.toString();
            this.mUserId = context.getUserId();
        }

        public String toString() {
            return this.mContext;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public int getUserId() {
            return this.mUserId;
        }
    }

    public ContentCaptureManager(Context context, IContentCaptureManager service, ContentCaptureOptions options) {
        Objects.requireNonNull(context, "context cannot be null");
        this.mContext = new StrippedContext(context);
        this.mService = (IContentCaptureManager) Objects.requireNonNull(service, "service cannot be null");
        ContentCaptureOptions contentCaptureOptions = (ContentCaptureOptions) Objects.requireNonNull(options, "options cannot be null");
        this.mOptions = contentCaptureOptions;
        ContentCaptureHelper.setLoggingLevel(contentCaptureOptions.loggingLevel);
        setFlushViewTreeAppearingEventDisabled(contentCaptureOptions.disableFlushForViewTreeAppearing);
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "Constructor for " + context.getPackageName());
        }
        this.mHandler = Handler.createAsync(Looper.getMainLooper());
        this.mDataShareAdapterResourceManager = new LocalDataShareAdapterResourceManager();
    }

    public MainContentCaptureSession getMainContentCaptureSession() {
        MainContentCaptureSession mainContentCaptureSession;
        synchronized (this.mLock) {
            if (this.mMainSession == null) {
                this.mMainSession = new MainContentCaptureSession(this.mContext, this, this.mHandler, this.mService);
                if (ContentCaptureHelper.sVerbose) {
                    Log.m106v(TAG, "getMainContentCaptureSession(): created " + this.mMainSession);
                }
            }
            mainContentCaptureSession = this.mMainSession;
        }
        return mainContentCaptureSession;
    }

    public void onActivityCreated(IBinder applicationToken, IBinder shareableActivityToken, ComponentName activityComponent) {
        if (this.mOptions.lite) {
            return;
        }
        synchronized (this.mLock) {
            getMainContentCaptureSession().start(applicationToken, shareableActivityToken, activityComponent, this.mFlags);
        }
    }

    public void onActivityResumed() {
        if (this.mOptions.lite) {
            return;
        }
        getMainContentCaptureSession().notifySessionResumed();
    }

    public void onActivityPaused() {
        if (this.mOptions.lite) {
            return;
        }
        getMainContentCaptureSession().notifySessionPaused();
    }

    public void onActivityDestroyed() {
        if (this.mOptions.lite) {
            return;
        }
        getMainContentCaptureSession().destroy();
    }

    public void flush(int reason) {
        if (this.mOptions.lite) {
            return;
        }
        getMainContentCaptureSession().flush(reason);
    }

    public ComponentName getServiceComponentName() {
        if (isContentCaptureEnabled() || this.mOptions.lite) {
            SyncResultReceiver resultReceiver = new SyncResultReceiver(5000);
            try {
                this.mService.getServiceComponentName(resultReceiver);
                return (ComponentName) resultReceiver.getParcelableResult();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (SyncResultReceiver.TimeoutException e2) {
                throw new RuntimeException("Fail to get service componentName.");
            }
        }
        return null;
    }

    public static ComponentName getServiceSettingsComponentName() {
        IBinder binder = ServiceManager.checkService(Context.CONTENT_CAPTURE_MANAGER_SERVICE);
        if (binder == null) {
            return null;
        }
        IContentCaptureManager service = IContentCaptureManager.Stub.asInterface(binder);
        SyncResultReceiver resultReceiver = new SyncResultReceiver(5000);
        try {
            service.getServiceSettingsActivity(resultReceiver);
            int resultCode = resultReceiver.getIntResult();
            if (resultCode == -1) {
                throw new SecurityException(resultReceiver.getStringResult());
            }
            return (ComponentName) resultReceiver.getParcelableResult();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (SyncResultReceiver.TimeoutException e2) {
            Log.m110e(TAG, "Fail to get service settings componentName: " + e2);
            return null;
        }
    }

    public boolean isContentCaptureEnabled() {
        MainContentCaptureSession mainSession;
        if (this.mOptions.lite) {
            return false;
        }
        synchronized (this.mLock) {
            mainSession = this.mMainSession;
        }
        return mainSession == null || !mainSession.isDisabled();
    }

    public Set<ContentCaptureCondition> getContentCaptureConditions() {
        if (isContentCaptureEnabled() || this.mOptions.lite) {
            SyncResultReceiver resultReceiver = syncRun(new MyRunnable() { // from class: android.view.contentcapture.ContentCaptureManager$$ExternalSyntheticLambda0
                @Override // android.view.contentcapture.ContentCaptureManager.MyRunnable
                public final void run(SyncResultReceiver syncResultReceiver) {
                    ContentCaptureManager.this.lambda$getContentCaptureConditions$0(syncResultReceiver);
                }
            });
            try {
                ArrayList<ContentCaptureCondition> result = resultReceiver.getParcelableListResult();
                return ContentCaptureHelper.toSet(result);
            } catch (SyncResultReceiver.TimeoutException e) {
                throw new RuntimeException("Fail to get content capture conditions.");
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getContentCaptureConditions$0(SyncResultReceiver r) throws RemoteException {
        this.mService.getContentCaptureConditions(this.mContext.getPackageName(), r);
    }

    public void setContentCaptureEnabled(boolean enabled) {
        MainContentCaptureSession mainSession;
        if (ContentCaptureHelper.sDebug) {
            Log.m112d(TAG, "setContentCaptureEnabled(): setting to " + enabled + " for " + this.mContext);
        }
        synchronized (this.mLock) {
            if (enabled) {
                this.mFlags &= -2;
            } else {
                this.mFlags |= 1;
            }
            mainSession = this.mMainSession;
        }
        if (mainSession != null) {
            mainSession.setDisabled(!enabled);
        }
    }

    public void updateWindowAttributes(WindowManager.LayoutParams params) {
        MainContentCaptureSession mainSession;
        if (ContentCaptureHelper.sDebug) {
            Log.m112d(TAG, "updateWindowAttributes(): window flags=" + params.flags);
        }
        boolean flagSecureEnabled = (params.flags & 8192) != 0;
        synchronized (this.mLock) {
            if (flagSecureEnabled) {
                this.mFlags |= 2;
            } else {
                this.mFlags &= -3;
            }
            mainSession = this.mMainSession;
        }
        if (mainSession != null) {
            mainSession.setDisabled(flagSecureEnabled);
        }
    }

    public void setFlushViewTreeAppearingEventDisabled(boolean disabled) {
        if (ContentCaptureHelper.sDebug) {
            Log.m112d(TAG, "setFlushViewTreeAppearingEventDisabled(): setting to " + disabled);
        }
        synchronized (this.mLock) {
            if (disabled) {
                this.mFlags |= 8;
            } else {
                this.mFlags &= -9;
            }
        }
    }

    public boolean getFlushViewTreeAppearingEventDisabled() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mFlags & 8) != 0;
        }
        return z;
    }

    @SystemApi
    public boolean isContentCaptureFeatureEnabled() {
        SyncResultReceiver resultReceiver = syncRun(new MyRunnable() { // from class: android.view.contentcapture.ContentCaptureManager$$ExternalSyntheticLambda1
            @Override // android.view.contentcapture.ContentCaptureManager.MyRunnable
            public final void run(SyncResultReceiver syncResultReceiver) {
                ContentCaptureManager.this.lambda$isContentCaptureFeatureEnabled$1(syncResultReceiver);
            }
        });
        try {
            int resultCode = resultReceiver.getIntResult();
            switch (resultCode) {
                case 1:
                    return true;
                case 2:
                    return false;
                default:
                    Log.wtf(TAG, "received invalid result: " + resultCode);
                    return false;
            }
        } catch (SyncResultReceiver.TimeoutException e) {
            Log.m110e(TAG, "Fail to get content capture feature enable status: " + e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isContentCaptureFeatureEnabled$1(SyncResultReceiver r) throws RemoteException {
        this.mService.isContentCaptureFeatureEnabled(r);
    }

    public void removeData(DataRemovalRequest request) {
        Objects.requireNonNull(request);
        try {
            this.mService.removeData(request);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void shareData(DataShareRequest request, Executor executor, DataShareWriteAdapter dataShareWriteAdapter) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(dataShareWriteAdapter);
        Objects.requireNonNull(executor);
        try {
            this.mService.shareData(request, new DataShareAdapterDelegate(executor, dataShareWriteAdapter, this.mDataShareAdapterResourceManager));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private SyncResultReceiver syncRun(MyRunnable r) {
        SyncResultReceiver resultReceiver = new SyncResultReceiver(5000);
        try {
            r.run(resultReceiver);
            int resultCode = resultReceiver.getIntResult();
            if (resultCode == -1) {
                throw new SecurityException(resultReceiver.getStringResult());
            }
            return resultReceiver;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (SyncResultReceiver.TimeoutException e2) {
            throw new RuntimeException("Fail to get syn run result from SyncResultReceiver.");
        }
    }

    public void addDumpable(Activity activity) {
        if (this.mDumpable == null) {
            this.mDumpable = new Dumper();
        }
        activity.addDumpable(this.mDumpable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class Dumper implements Dumpable {
        private Dumper() {
        }

        @Override // android.util.Dumpable
        public void dump(PrintWriter pw, String[] args) {
            pw.print("");
            pw.println(ContentCaptureManager.DUMPABLE_NAME);
            String prefix2 = "  ";
            synchronized (ContentCaptureManager.this.mLock) {
                pw.print(prefix2);
                pw.print("isContentCaptureEnabled(): ");
                pw.println(ContentCaptureManager.this.isContentCaptureEnabled());
                pw.print(prefix2);
                pw.print("Debug: ");
                pw.print(ContentCaptureHelper.sDebug);
                pw.print(" Verbose: ");
                pw.println(ContentCaptureHelper.sVerbose);
                pw.print(prefix2);
                pw.print("Context: ");
                pw.println(ContentCaptureManager.this.mContext);
                pw.print(prefix2);
                pw.print("User: ");
                pw.println(ContentCaptureManager.this.mContext.getUserId());
                pw.print(prefix2);
                pw.print("Service: ");
                pw.println(ContentCaptureManager.this.mService);
                pw.print(prefix2);
                pw.print("Flags: ");
                pw.println(ContentCaptureManager.this.mFlags);
                pw.print(prefix2);
                pw.print("Options: ");
                ContentCaptureManager.this.mOptions.dumpShort(pw);
                pw.println();
                if (ContentCaptureManager.this.mMainSession != null) {
                    String prefix3 = prefix2 + "  ";
                    pw.print(prefix2);
                    pw.println("Main session:");
                    ContentCaptureManager.this.mMainSession.dump(prefix3, pw);
                } else {
                    pw.print(prefix2);
                    pw.println("No sessions");
                }
            }
        }

        @Override // android.util.Dumpable
        public String getDumpableName() {
            return ContentCaptureManager.DUMPABLE_NAME;
        }
    }

    public static void resetTemporaryService(int userId) {
        IContentCaptureManager service = getService();
        if (service == null) {
            Log.m110e(TAG, "IContentCaptureManager is null");
        }
        try {
            service.resetTemporaryService(userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void setTemporaryService(int userId, String serviceName, int duration) {
        IContentCaptureManager service = getService();
        if (service == null) {
            Log.m110e(TAG, "IContentCaptureManager is null");
        }
        try {
            service.setTemporaryService(userId, serviceName, duration);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static void setDefaultServiceEnabled(int userId, boolean enabled) {
        IContentCaptureManager service = getService();
        if (service == null) {
            Log.m110e(TAG, "IContentCaptureManager is null");
        }
        try {
            service.setDefaultServiceEnabled(userId, enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static IContentCaptureManager getService() {
        return IContentCaptureManager.Stub.asInterface(ServiceManager.getService(Context.CONTENT_CAPTURE_MANAGER_SERVICE));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class DataShareAdapterDelegate extends IDataShareWriteAdapter.Stub {
        private final WeakReference<LocalDataShareAdapterResourceManager> mResourceManagerReference;

        private DataShareAdapterDelegate(Executor executor, DataShareWriteAdapter adapter, LocalDataShareAdapterResourceManager resourceManager) {
            Objects.requireNonNull(executor);
            Objects.requireNonNull(adapter);
            Objects.requireNonNull(resourceManager);
            resourceManager.initializeForDelegate(this, adapter, executor);
            this.mResourceManagerReference = new WeakReference<>(resourceManager);
        }

        @Override // android.view.contentcapture.IDataShareWriteAdapter
        public void write(final ParcelFileDescriptor destination) throws RemoteException {
            executeAdapterMethodLocked(new Consumer() { // from class: android.view.contentcapture.ContentCaptureManager$DataShareAdapterDelegate$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((DataShareWriteAdapter) obj).onWrite(ParcelFileDescriptor.this);
                }
            }, "onWrite");
        }

        @Override // android.view.contentcapture.IDataShareWriteAdapter
        public void error(final int errorCode) throws RemoteException {
            executeAdapterMethodLocked(new Consumer() { // from class: android.view.contentcapture.ContentCaptureManager$DataShareAdapterDelegate$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((DataShareWriteAdapter) obj).onError(errorCode);
                }
            }, "onError");
            clearHardReferences();
        }

        @Override // android.view.contentcapture.IDataShareWriteAdapter
        public void rejected() throws RemoteException {
            executeAdapterMethodLocked(new Consumer() { // from class: android.view.contentcapture.ContentCaptureManager$DataShareAdapterDelegate$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((DataShareWriteAdapter) obj).onRejected();
                }
            }, "onRejected");
            clearHardReferences();
        }

        @Override // android.view.contentcapture.IDataShareWriteAdapter
        public void finish() throws RemoteException {
            clearHardReferences();
        }

        private void executeAdapterMethodLocked(final Consumer<DataShareWriteAdapter> adapterFn, String methodName) {
            LocalDataShareAdapterResourceManager resourceManager = this.mResourceManagerReference.get();
            if (resourceManager == null) {
                Slog.m90w(ContentCaptureManager.TAG, "Can't execute " + methodName + "(), resource manager has been GC'ed");
                return;
            }
            final DataShareWriteAdapter adapter = resourceManager.getAdapter(this);
            Executor executor = resourceManager.getExecutor(this);
            if (adapter == null || executor == null) {
                Slog.m90w(ContentCaptureManager.TAG, "Can't execute " + methodName + "(), references are null");
                return;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                executor.execute(new Runnable() { // from class: android.view.contentcapture.ContentCaptureManager$DataShareAdapterDelegate$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        adapterFn.accept(adapter);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        private void clearHardReferences() {
            LocalDataShareAdapterResourceManager resourceManager = this.mResourceManagerReference.get();
            if (resourceManager == null) {
                Slog.m90w(ContentCaptureManager.TAG, "Can't clear references, resource manager has been GC'ed");
            } else {
                resourceManager.clearHardReferences(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class LocalDataShareAdapterResourceManager {
        private Map<DataShareAdapterDelegate, Executor> mExecutorHardReferences;
        private Map<DataShareAdapterDelegate, DataShareWriteAdapter> mWriteAdapterHardReferences;

        private LocalDataShareAdapterResourceManager() {
            this.mWriteAdapterHardReferences = new HashMap();
            this.mExecutorHardReferences = new HashMap();
        }

        void initializeForDelegate(DataShareAdapterDelegate delegate, DataShareWriteAdapter adapter, Executor executor) {
            this.mWriteAdapterHardReferences.put(delegate, adapter);
            this.mExecutorHardReferences.put(delegate, executor);
        }

        Executor getExecutor(DataShareAdapterDelegate delegate) {
            return this.mExecutorHardReferences.get(delegate);
        }

        DataShareWriteAdapter getAdapter(DataShareAdapterDelegate delegate) {
            return this.mWriteAdapterHardReferences.get(delegate);
        }

        void clearHardReferences(DataShareAdapterDelegate delegate) {
            this.mWriteAdapterHardReferences.remove(delegate);
            this.mExecutorHardReferences.remove(delegate);
        }
    }
}
