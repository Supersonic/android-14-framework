package android.service.contentcapture;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Intent;
import android.content.p001pm.ParceledListSlice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.service.contentcapture.IContentCaptureService;
import android.service.contentcapture.IContentCaptureServiceCallback;
import android.service.contentcapture.IDataShareReadAdapter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.contentcapture.ContentCaptureCondition;
import android.view.contentcapture.ContentCaptureContext;
import android.view.contentcapture.ContentCaptureEvent;
import android.view.contentcapture.ContentCaptureHelper;
import android.view.contentcapture.ContentCaptureSessionId;
import android.view.contentcapture.DataRemovalRequest;
import android.view.contentcapture.DataShareRequest;
import android.view.contentcapture.IContentCaptureDirectManager;
import com.android.internal.p028os.IResultReceiver;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.HexConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class ContentCaptureService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.contentcapture.ContentCaptureService";
    public static final String SERVICE_META_DATA = "android.content_capture";
    private static final String TAG = ContentCaptureService.class.getSimpleName();
    private IContentCaptureServiceCallback mCallback;
    private Handler mHandler;
    private long mLastCallerMismatchLog;
    private final LocalDataShareAdapterResourceManager mDataShareAdapterResourceManager = new LocalDataShareAdapterResourceManager();
    private long mCallerMismatchTimeout = 1000;
    private final IContentCaptureService mServerInterface = new BinderC24931();
    private final IContentCaptureDirectManager mClientInterface = new BinderC24942();
    private final SparseIntArray mSessionUids = new SparseIntArray();

    /* renamed from: android.service.contentcapture.ContentCaptureService$1 */
    /* loaded from: classes3.dex */
    class BinderC24931 extends IContentCaptureService.Stub {
        BinderC24931() {
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onConnected(IBinder callback, boolean verbose, boolean debug) {
            ContentCaptureHelper.sVerbose = verbose;
            ContentCaptureHelper.sDebug = debug;
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda4
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ContentCaptureService) obj).handleOnConnected((IBinder) obj2);
                }
            }, ContentCaptureService.this, callback));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDisconnected() {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ContentCaptureService) obj).handleOnDisconnected();
                }
            }, ContentCaptureService.this));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onSessionStarted(ContentCaptureContext context, int sessionId, int uid, IResultReceiver clientReceiver, int initialState) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new HexConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.HexConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                    ((ContentCaptureService) obj).handleOnCreateSession((ContentCaptureContext) obj2, ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), (IResultReceiver) obj5, ((Integer) obj6).intValue());
                }
            }, ContentCaptureService.this, context, Integer.valueOf(sessionId), Integer.valueOf(uid), clientReceiver, Integer.valueOf(initialState)));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onActivitySnapshot(int sessionId, SnapshotData snapshotData) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((ContentCaptureService) obj).handleOnActivitySnapshot(((Integer) obj2).intValue(), (SnapshotData) obj3);
                }
            }, ContentCaptureService.this, Integer.valueOf(sessionId), snapshotData));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onSessionFinished(int sessionId) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda5
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ContentCaptureService) obj).handleFinishSession(((Integer) obj2).intValue());
                }
            }, ContentCaptureService.this, Integer.valueOf(sessionId)));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDataRemovalRequest(DataRemovalRequest request) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda7
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ContentCaptureService) obj).handleOnDataRemovalRequest((DataRemovalRequest) obj2);
                }
            }, ContentCaptureService.this, request));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onDataShared(DataShareRequest request, IDataShareCallback callback) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((ContentCaptureService) obj).handleOnDataShared((DataShareRequest) obj2, (IDataShareCallback) obj3);
                }
            }, ContentCaptureService.this, request, callback));
        }

        @Override // android.service.contentcapture.IContentCaptureService
        public void onActivityEvent(ActivityEvent event) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.contentcapture.ContentCaptureService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((ContentCaptureService) obj).handleOnActivityEvent((ActivityEvent) obj2);
                }
            }, ContentCaptureService.this, event));
        }
    }

    /* renamed from: android.service.contentcapture.ContentCaptureService$2 */
    /* loaded from: classes3.dex */
    class BinderC24942 extends IContentCaptureDirectManager.Stub {
        BinderC24942() {
        }

        @Override // android.view.contentcapture.IContentCaptureDirectManager
        public void sendEvents(ParceledListSlice events, int reason, ContentCaptureOptions options) {
            ContentCaptureService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: android.service.contentcapture.ContentCaptureService$2$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.QuintConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((ContentCaptureService) obj).handleSendEvents(((Integer) obj2).intValue(), (ParceledListSlice) obj3, ((Integer) obj4).intValue(), (ContentCaptureOptions) obj5);
                }
            }, ContentCaptureService.this, Integer.valueOf(Binder.getCallingUid()), events, Integer.valueOf(reason), options));
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mServerInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.contentcapture.ContentCaptureService: " + intent);
        return null;
    }

    public final void setContentCaptureWhitelist(Set<String> packages, Set<ComponentName> activities) {
        IContentCaptureServiceCallback callback = this.mCallback;
        if (callback == null) {
            Log.m104w(TAG, "setContentCaptureWhitelist(): no server callback");
            return;
        }
        try {
            callback.setContentCaptureWhitelist(ContentCaptureHelper.toList(packages), ContentCaptureHelper.toList(activities));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void setContentCaptureConditions(String packageName, Set<ContentCaptureCondition> conditions) {
        IContentCaptureServiceCallback callback = this.mCallback;
        if (callback == null) {
            Log.m104w(TAG, "setContentCaptureConditions(): no server callback");
            return;
        }
        try {
            callback.setContentCaptureConditions(packageName, ContentCaptureHelper.toList(conditions));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void onConnected() {
        Slog.m94i(TAG, "bound to " + getClass().getName());
    }

    public void onCreateContentCaptureSession(ContentCaptureContext context, ContentCaptureSessionId sessionId) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onCreateContentCaptureSession(id=" + sessionId + ", ctx=" + context + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public void onContentCaptureEvent(ContentCaptureSessionId sessionId, ContentCaptureEvent event) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onContentCaptureEventsRequest(id=" + sessionId + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public void onDataRemovalRequest(DataRemovalRequest request) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onDataRemovalRequest()");
        }
    }

    @SystemApi
    public void onDataShareRequest(DataShareRequest request, DataShareCallback callback) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onDataShareRequest()");
        }
    }

    public void onActivitySnapshot(ContentCaptureSessionId sessionId, SnapshotData snapshotData) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onActivitySnapshot(id=" + sessionId + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public void onActivityEvent(ActivityEvent event) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onActivityEvent(): " + event);
        }
    }

    public void onDestroyContentCaptureSession(ContentCaptureSessionId sessionId) {
        if (ContentCaptureHelper.sVerbose) {
            Log.m106v(TAG, "onDestroyContentCaptureSession(id=" + sessionId + NavigationBarInflaterView.KEY_CODE_END);
        }
    }

    public final void disableSelf() {
        if (ContentCaptureHelper.sDebug) {
            Log.m112d(TAG, "disableSelf()");
        }
        IContentCaptureServiceCallback callback = this.mCallback;
        if (callback == null) {
            Log.m104w(TAG, "disableSelf(): no server callback");
            return;
        }
        try {
            callback.disableSelf();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void onDisconnected() {
        Slog.m94i(TAG, "unbinding from " + getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("Debug: ");
        pw.print(ContentCaptureHelper.sDebug);
        pw.print(" Verbose: ");
        pw.println(ContentCaptureHelper.sVerbose);
        int size = this.mSessionUids.size();
        pw.print("Number sessions: ");
        pw.println(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                pw.print("  ");
                pw.print(this.mSessionUids.keyAt(i));
                pw.print(": uid=");
                pw.println(this.mSessionUids.valueAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnConnected(IBinder callback) {
        this.mCallback = IContentCaptureServiceCallback.Stub.asInterface(callback);
        onConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDisconnected() {
        onDisconnected();
        this.mCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnCreateSession(ContentCaptureContext context, int sessionId, int uid, IResultReceiver clientReceiver, int initialState) {
        int stateFlags;
        this.mSessionUids.put(sessionId, uid);
        onCreateContentCaptureSession(context, new ContentCaptureSessionId(sessionId));
        int clientFlags = context.getFlags();
        int stateFlags2 = 0;
        if ((clientFlags & 2) != 0) {
            stateFlags2 = 0 | 32;
        }
        if ((clientFlags & 1) != 0) {
            stateFlags2 |= 64;
        }
        if (stateFlags2 == 0) {
            stateFlags = initialState;
        } else {
            stateFlags = stateFlags2 | 4;
        }
        setClientState(clientReceiver, stateFlags, this.mClientInterface.asBinder());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSendEvents(int uid, ParceledListSlice<ContentCaptureEvent> parceledEvents, int reason, ContentCaptureOptions options) {
        List<ContentCaptureEvent> events = parceledEvents.getList();
        if (events.isEmpty()) {
            Log.m104w(TAG, "handleSendEvents() received empty list of events");
            return;
        }
        FlushMetrics metrics = new FlushMetrics();
        ComponentName activityComponent = null;
        int lastSessionId = 0;
        ContentCaptureSessionId sessionId = null;
        for (int i = 0; i < events.size(); i++) {
            ContentCaptureEvent event = events.get(i);
            if (handleIsRightCallerFor(event, uid)) {
                int sessionIdInt = event.getSessionId();
                if (sessionIdInt != lastSessionId) {
                    sessionId = new ContentCaptureSessionId(sessionIdInt);
                    if (i != 0) {
                        writeFlushMetrics(sessionIdInt, activityComponent, metrics, options, reason);
                        metrics.reset();
                    }
                    lastSessionId = sessionIdInt;
                }
                ContentCaptureContext clientContext = event.getContentCaptureContext();
                if (activityComponent == null && clientContext != null) {
                    activityComponent = clientContext.getActivityComponent();
                }
                switch (event.getType()) {
                    case -2:
                        this.mSessionUids.delete(sessionIdInt);
                        onDestroyContentCaptureSession(sessionId);
                        metrics.sessionFinished++;
                        continue;
                    case -1:
                        clientContext.setParentSessionId(event.getParentSessionId());
                        this.mSessionUids.put(sessionIdInt, uid);
                        onCreateContentCaptureSession(clientContext, sessionId);
                        metrics.sessionStarted++;
                        continue;
                    case 0:
                    default:
                        onContentCaptureEvent(sessionId, event);
                        continue;
                    case 1:
                        onContentCaptureEvent(sessionId, event);
                        metrics.viewAppearedCount++;
                        continue;
                    case 2:
                        onContentCaptureEvent(sessionId, event);
                        metrics.viewDisappearedCount++;
                        continue;
                    case 3:
                        onContentCaptureEvent(sessionId, event);
                        metrics.viewTextChangedCount++;
                        continue;
                }
            }
        }
        writeFlushMetrics(lastSessionId, activityComponent, metrics, options, reason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnActivitySnapshot(int sessionId, SnapshotData snapshotData) {
        onActivitySnapshot(new ContentCaptureSessionId(sessionId), snapshotData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFinishSession(int sessionId) {
        this.mSessionUids.delete(sessionId);
        onDestroyContentCaptureSession(new ContentCaptureSessionId(sessionId));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDataRemovalRequest(DataRemovalRequest request) {
        onDataRemovalRequest(request);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDataShared(DataShareRequest request, final IDataShareCallback callback) {
        onDataShareRequest(request, new DataShareCallback() { // from class: android.service.contentcapture.ContentCaptureService.3
            @Override // android.service.contentcapture.DataShareCallback
            public void onAccept(Executor executor, DataShareReadAdapter adapter) {
                Objects.requireNonNull(adapter);
                Objects.requireNonNull(executor);
                DataShareReadAdapterDelegate delegate = new DataShareReadAdapterDelegate(executor, adapter, ContentCaptureService.this.mDataShareAdapterResourceManager);
                try {
                    callback.accept(delegate);
                } catch (RemoteException e) {
                    Slog.m95e(ContentCaptureService.TAG, "Failed to accept data sharing", e);
                }
            }

            @Override // android.service.contentcapture.DataShareCallback
            public void onReject() {
                try {
                    callback.reject();
                } catch (RemoteException e) {
                    Slog.m95e(ContentCaptureService.TAG, "Failed to reject data sharing", e);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnActivityEvent(ActivityEvent event) {
        onActivityEvent(event);
    }

    private boolean handleIsRightCallerFor(ContentCaptureEvent event, int uid) {
        int sessionId;
        switch (event.getType()) {
            case -2:
            case -1:
                sessionId = event.getParentSessionId();
                break;
            default:
                sessionId = event.getSessionId();
                break;
        }
        if (this.mSessionUids.indexOfKey(sessionId) < 0) {
            if (ContentCaptureHelper.sVerbose) {
                Log.m106v(TAG, "handleIsRightCallerFor(" + event + "): no session for " + sessionId + ": " + this.mSessionUids);
            }
            return false;
        }
        int rightUid = this.mSessionUids.get(sessionId);
        if (rightUid != uid) {
            Log.m110e(TAG, "invalid call from UID " + uid + ": session " + sessionId + " belongs to " + rightUid);
            long now = System.currentTimeMillis();
            if (now - this.mLastCallerMismatchLog > this.mCallerMismatchTimeout) {
                FrameworkStatsLog.write(206, getPackageManager().getNameForUid(rightUid), getPackageManager().getNameForUid(uid));
                this.mLastCallerMismatchLog = now;
            }
            return false;
        }
        return true;
    }

    public static void setClientState(IResultReceiver clientReceiver, int sessionState, IBinder binder) {
        Bundle extras;
        if (binder != null) {
            try {
                extras = new Bundle();
                extras.putBinder("binder", binder);
            } catch (RemoteException e) {
                Slog.m90w(TAG, "Error async reporting result to client: " + e);
                return;
            }
        } else {
            extras = null;
        }
        clientReceiver.send(sessionState, extras);
    }

    private void writeFlushMetrics(int sessionId, ComponentName app, FlushMetrics flushMetrics, ContentCaptureOptions options, int flushReason) {
        IContentCaptureServiceCallback iContentCaptureServiceCallback = this.mCallback;
        if (iContentCaptureServiceCallback == null) {
            Log.m104w(TAG, "writeSessionFlush(): no server callback");
            return;
        }
        try {
            iContentCaptureServiceCallback.writeSessionFlush(sessionId, app, flushMetrics, options, flushReason);
        } catch (RemoteException e) {
            Log.m110e(TAG, "failed to write flush metrics: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class DataShareReadAdapterDelegate extends IDataShareReadAdapter.Stub {
        private final Object mLock = new Object();
        private final WeakReference<LocalDataShareAdapterResourceManager> mResourceManagerReference;

        DataShareReadAdapterDelegate(Executor executor, DataShareReadAdapter adapter, LocalDataShareAdapterResourceManager resourceManager) {
            Objects.requireNonNull(executor);
            Objects.requireNonNull(adapter);
            Objects.requireNonNull(resourceManager);
            resourceManager.initializeForDelegate(this, adapter, executor);
            this.mResourceManagerReference = new WeakReference<>(resourceManager);
        }

        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void start(final ParcelFileDescriptor fd) throws RemoteException {
            synchronized (this.mLock) {
                executeAdapterMethodLocked(new Consumer() { // from class: android.service.contentcapture.ContentCaptureService$DataShareReadAdapterDelegate$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((DataShareReadAdapter) obj).onStart(ParcelFileDescriptor.this);
                    }
                }, "onStart");
            }
        }

        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void error(final int errorCode) throws RemoteException {
            synchronized (this.mLock) {
                executeAdapterMethodLocked(new Consumer() { // from class: android.service.contentcapture.ContentCaptureService$DataShareReadAdapterDelegate$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((DataShareReadAdapter) obj).onError(errorCode);
                    }
                }, "onError");
                clearHardReferences();
            }
        }

        @Override // android.service.contentcapture.IDataShareReadAdapter
        public void finish() throws RemoteException {
            synchronized (this.mLock) {
                clearHardReferences();
            }
        }

        private void executeAdapterMethodLocked(final Consumer<DataShareReadAdapter> adapterFn, String methodName) {
            LocalDataShareAdapterResourceManager resourceManager = this.mResourceManagerReference.get();
            if (resourceManager == null) {
                Slog.m90w(ContentCaptureService.TAG, "Can't execute " + methodName + "(), resource manager has been GC'ed");
                return;
            }
            final DataShareReadAdapter adapter = resourceManager.getAdapter(this);
            Executor executor = resourceManager.getExecutor(this);
            if (adapter == null || executor == null) {
                Slog.m90w(ContentCaptureService.TAG, "Can't execute " + methodName + "(), references are null");
                return;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                executor.execute(new Runnable() { // from class: android.service.contentcapture.ContentCaptureService$DataShareReadAdapterDelegate$$ExternalSyntheticLambda1
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
                Slog.m90w(ContentCaptureService.TAG, "Can't clear references, resource manager has been GC'ed");
            } else {
                resourceManager.clearHardReferences(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class LocalDataShareAdapterResourceManager {
        private Map<DataShareReadAdapterDelegate, DataShareReadAdapter> mDataShareReadAdapterHardReferences;
        private Map<DataShareReadAdapterDelegate, Executor> mExecutorHardReferences;

        private LocalDataShareAdapterResourceManager() {
            this.mDataShareReadAdapterHardReferences = new HashMap();
            this.mExecutorHardReferences = new HashMap();
        }

        void initializeForDelegate(DataShareReadAdapterDelegate delegate, DataShareReadAdapter adapter, Executor executor) {
            this.mDataShareReadAdapterHardReferences.put(delegate, adapter);
            this.mExecutorHardReferences.put(delegate, executor);
        }

        Executor getExecutor(DataShareReadAdapterDelegate delegate) {
            return this.mExecutorHardReferences.get(delegate);
        }

        DataShareReadAdapter getAdapter(DataShareReadAdapterDelegate delegate) {
            return this.mDataShareReadAdapterHardReferences.get(delegate);
        }

        void clearHardReferences(DataShareReadAdapterDelegate delegate) {
            this.mDataShareReadAdapterHardReferences.remove(delegate);
            this.mExecutorHardReferences.remove(delegate);
        }
    }
}
