package android.hardware.location;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.Context;
import android.hardware.location.ContextHubManager;
import android.hardware.location.ContextHubTransaction;
import android.hardware.location.IContextHubCallback;
import android.hardware.location.IContextHubClientCallback;
import android.hardware.location.IContextHubService;
import android.hardware.location.IContextHubTransactionCallback;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes2.dex */
public final class ContextHubManager {
    public static final int AUTHORIZATION_DENIED = 0;
    public static final int AUTHORIZATION_DENIED_GRACE_PERIOD = 1;
    public static final int AUTHORIZATION_GRANTED = 2;
    public static final int EVENT_CLIENT_AUTHORIZATION = 7;
    public static final int EVENT_HUB_RESET = 6;
    public static final int EVENT_NANOAPP_ABORTED = 4;
    public static final int EVENT_NANOAPP_DISABLED = 3;
    public static final int EVENT_NANOAPP_ENABLED = 2;
    public static final int EVENT_NANOAPP_LOADED = 0;
    public static final int EVENT_NANOAPP_MESSAGE = 5;
    public static final int EVENT_NANOAPP_UNLOADED = 1;
    public static final String EXTRA_CLIENT_AUTHORIZATION_STATE = "android.hardware.location.extra.CLIENT_AUTHORIZATION_STATE";
    public static final String EXTRA_CONTEXT_HUB_INFO = "android.hardware.location.extra.CONTEXT_HUB_INFO";
    public static final String EXTRA_EVENT_TYPE = "android.hardware.location.extra.EVENT_TYPE";
    public static final String EXTRA_MESSAGE = "android.hardware.location.extra.MESSAGE";
    public static final String EXTRA_NANOAPP_ABORT_CODE = "android.hardware.location.extra.NANOAPP_ABORT_CODE";
    public static final String EXTRA_NANOAPP_ID = "android.hardware.location.extra.NANOAPP_ID";
    private static final String TAG = "ContextHubManager";
    private Callback mCallback;
    private Handler mCallbackHandler;
    private final IContextHubCallback.Stub mClientCallback;
    @Deprecated
    private ICallback mLocalCallback;
    private final Looper mMainLooper;
    private final IContextHubService mService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AuthorizationState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Event {
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public interface ICallback {
        void onMessageReceipt(int i, int i2, ContextHubMessage contextHubMessage);
    }

    @Deprecated
    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public abstract void onMessageReceipt(int i, int i2, ContextHubMessage contextHubMessage);

        protected Callback() {
        }
    }

    @Deprecated
    public int[] getContextHubHandles() {
        try {
            return this.mService.getContextHubHandles();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public ContextHubInfo getContextHubInfo(int hubHandle) {
        try {
            return this.mService.getContextHubInfo(hubHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int loadNanoApp(int hubHandle, NanoApp app) {
        try {
            return this.mService.loadNanoApp(hubHandle, app);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int unloadNanoApp(int nanoAppHandle) {
        try {
            return this.mService.unloadNanoApp(nanoAppHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public NanoAppInstanceInfo getNanoAppInstanceInfo(int nanoAppHandle) {
        try {
            return this.mService.getNanoAppInstanceInfo(nanoAppHandle);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int[] findNanoAppOnHub(int hubHandle, NanoAppFilter filter) {
        try {
            return this.mService.findNanoAppOnHub(hubHandle, filter);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int sendMessage(int hubHandle, int nanoAppHandle, ContextHubMessage message) {
        try {
            return this.mService.sendMessage(hubHandle, nanoAppHandle, message);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ContextHubInfo> getContextHubs() {
        try {
            return this.mService.getContextHubs();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private IContextHubTransactionCallback createTransactionCallback(final ContextHubTransaction<Void> transaction) {
        return new IContextHubTransactionCallback.Stub() { // from class: android.hardware.location.ContextHubManager.1
            @Override // android.hardware.location.IContextHubTransactionCallback
            public void onQueryResponse(int result, List<NanoAppState> nanoappList) {
                Log.m110e(ContextHubManager.TAG, "Received a query callback on a non-query request");
                transaction.setResponse(new ContextHubTransaction.Response(7, null));
            }

            @Override // android.hardware.location.IContextHubTransactionCallback
            public void onTransactionComplete(int result) {
                transaction.setResponse(new ContextHubTransaction.Response(result, null));
            }
        };
    }

    private IContextHubTransactionCallback createQueryCallback(final ContextHubTransaction<List<NanoAppState>> transaction) {
        return new IContextHubTransactionCallback.Stub() { // from class: android.hardware.location.ContextHubManager.2
            @Override // android.hardware.location.IContextHubTransactionCallback
            public void onQueryResponse(int result, List<NanoAppState> nanoappList) {
                transaction.setResponse(new ContextHubTransaction.Response(result, nanoappList));
            }

            @Override // android.hardware.location.IContextHubTransactionCallback
            public void onTransactionComplete(int result) {
                Log.m110e(ContextHubManager.TAG, "Received a non-query callback on a query request");
                transaction.setResponse(new ContextHubTransaction.Response(7, null));
            }
        };
    }

    public ContextHubTransaction<Void> loadNanoApp(ContextHubInfo hubInfo, NanoAppBinary appBinary) {
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        Objects.requireNonNull(appBinary, "NanoAppBinary cannot be null");
        ContextHubTransaction<Void> transaction = new ContextHubTransaction<>(0);
        IContextHubTransactionCallback callback = createTransactionCallback(transaction);
        try {
            this.mService.loadNanoAppOnHub(hubInfo.getId(), callback, appBinary);
            return transaction;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubTransaction<Void> unloadNanoApp(ContextHubInfo hubInfo, long nanoAppId) {
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        ContextHubTransaction<Void> transaction = new ContextHubTransaction<>(1);
        IContextHubTransactionCallback callback = createTransactionCallback(transaction);
        try {
            this.mService.unloadNanoAppFromHub(hubInfo.getId(), callback, nanoAppId);
            return transaction;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubTransaction<Void> enableNanoApp(ContextHubInfo hubInfo, long nanoAppId) {
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        ContextHubTransaction<Void> transaction = new ContextHubTransaction<>(2);
        IContextHubTransactionCallback callback = createTransactionCallback(transaction);
        try {
            this.mService.enableNanoApp(hubInfo.getId(), callback, nanoAppId);
            return transaction;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubTransaction<Void> disableNanoApp(ContextHubInfo hubInfo, long nanoAppId) {
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        ContextHubTransaction<Void> transaction = new ContextHubTransaction<>(3);
        IContextHubTransactionCallback callback = createTransactionCallback(transaction);
        try {
            this.mService.disableNanoApp(hubInfo.getId(), callback, nanoAppId);
            return transaction;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubTransaction<List<NanoAppState>> queryNanoApps(ContextHubInfo hubInfo) {
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        ContextHubTransaction<List<NanoAppState>> transaction = new ContextHubTransaction<>(4);
        IContextHubTransactionCallback callback = createQueryCallback(transaction);
        try {
            this.mService.queryNanoApps(hubInfo.getId(), callback);
            return transaction;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int registerCallback(Callback callback) {
        return registerCallback(callback, null);
    }

    @Deprecated
    public int registerCallback(ICallback callback) {
        if (this.mLocalCallback != null) {
            Log.m104w(TAG, "Max number of local callbacks reached!");
            return -1;
        }
        this.mLocalCallback = callback;
        return 0;
    }

    @Deprecated
    public int registerCallback(Callback callback, Handler handler) {
        synchronized (this) {
            if (this.mCallback != null) {
                Log.m104w(TAG, "Max number of callbacks reached!");
                return -1;
            }
            this.mCallback = callback;
            this.mCallbackHandler = handler == null ? new Handler(this.mMainLooper) : handler;
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.hardware.location.ContextHubManager$3 */
    /* loaded from: classes2.dex */
    public class BinderC12023 extends IContextHubClientCallback.Stub {
        final /* synthetic */ ContextHubClientCallback val$callback;
        final /* synthetic */ ContextHubClient val$client;
        final /* synthetic */ Executor val$executor;

        BinderC12023(Executor executor, ContextHubClientCallback contextHubClientCallback, ContextHubClient contextHubClient) {
            this.val$executor = executor;
            this.val$callback = contextHubClientCallback;
            this.val$client = contextHubClient;
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onMessageFromNanoApp(final NanoAppMessage message) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onMessageFromNanoApp$0(ContextHubClientCallback.this, contextHubClient, message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onMessageFromNanoApp$0(ContextHubClientCallback callback, ContextHubClient client, NanoAppMessage message) {
            callback.onMessageFromNanoApp(client, message);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onHubReset() {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onHubReset$1(ContextHubClientCallback.this, contextHubClient);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onHubReset$1(ContextHubClientCallback callback, ContextHubClient client) {
            callback.onHubReset(client);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppAborted(final long nanoAppId, final int abortCode) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onNanoAppAborted$2(ContextHubClientCallback.this, contextHubClient, nanoAppId, abortCode);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onNanoAppAborted$2(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId, int abortCode) {
            callback.onNanoAppAborted(client, nanoAppId, abortCode);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppLoaded(final long nanoAppId) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onNanoAppLoaded$3(ContextHubClientCallback.this, contextHubClient, nanoAppId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onNanoAppLoaded$3(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId) {
            callback.onNanoAppLoaded(client, nanoAppId);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppUnloaded(final long nanoAppId) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onNanoAppUnloaded$4(ContextHubClientCallback.this, contextHubClient, nanoAppId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onNanoAppUnloaded$4(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId) {
            callback.onNanoAppUnloaded(client, nanoAppId);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppEnabled(final long nanoAppId) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onNanoAppEnabled$5(ContextHubClientCallback.this, contextHubClient, nanoAppId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onNanoAppEnabled$5(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId) {
            callback.onNanoAppEnabled(client, nanoAppId);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppDisabled(final long nanoAppId) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onNanoAppDisabled$6(ContextHubClientCallback.this, contextHubClient, nanoAppId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onNanoAppDisabled$6(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId) {
            callback.onNanoAppDisabled(client, nanoAppId);
            client.callbackFinished();
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onClientAuthorizationChanged(final long nanoAppId, final int authorization) {
            Executor executor = this.val$executor;
            final ContextHubClientCallback contextHubClientCallback = this.val$callback;
            final ContextHubClient contextHubClient = this.val$client;
            executor.execute(new Runnable() { // from class: android.hardware.location.ContextHubManager$3$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ContextHubManager.BinderC12023.lambda$onClientAuthorizationChanged$7(ContextHubClientCallback.this, contextHubClient, nanoAppId, authorization);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onClientAuthorizationChanged$7(ContextHubClientCallback callback, ContextHubClient client, long nanoAppId, int authorization) {
            callback.onClientAuthorizationChanged(client, nanoAppId, authorization);
            client.callbackFinished();
        }
    }

    private IContextHubClientCallback createClientCallback(ContextHubClient client, ContextHubClientCallback callback, Executor executor) {
        return new BinderC12023(executor, callback, client);
    }

    public ContextHubClient createClient(Context context, ContextHubInfo hubInfo, Executor executor, ContextHubClientCallback callback) {
        String packageName;
        Objects.requireNonNull(callback, "Callback cannot be null");
        Objects.requireNonNull(hubInfo, "ContextHubInfo cannot be null");
        Objects.requireNonNull(executor, "Executor cannot be null");
        ContextHubClient client = new ContextHubClient(hubInfo, false);
        IContextHubClientCallback clientInterface = createClientCallback(client, callback, executor);
        String attributionTag = null;
        if (context != null) {
            attributionTag = context.getAttributionTag();
        }
        if (context != null) {
            packageName = context.getPackageName();
        } else {
            packageName = ActivityThread.currentPackageName();
        }
        try {
            IContextHubClient clientProxy = this.mService.createClient(hubInfo.getId(), clientInterface, attributionTag, packageName);
            client.setClientProxy(clientProxy);
            return client;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubClient createClient(ContextHubInfo hubInfo, ContextHubClientCallback callback, Executor executor) {
        return createClient((Context) null, hubInfo, executor, callback);
    }

    public ContextHubClient createClient(ContextHubInfo hubInfo, ContextHubClientCallback callback) {
        return createClient((Context) null, hubInfo, new HandlerExecutor(Handler.getMain()), callback);
    }

    public ContextHubClient createClient(Context context, ContextHubInfo hubInfo, PendingIntent pendingIntent, long nanoAppId) {
        Objects.requireNonNull(pendingIntent);
        Objects.requireNonNull(hubInfo);
        if (pendingIntent.isImmutable()) {
            throw new IllegalArgumentException("PendingIntent must be mutable");
        }
        ContextHubClient client = new ContextHubClient(hubInfo, true);
        String attributionTag = null;
        if (context != null) {
            attributionTag = context.getAttributionTag();
        }
        try {
            IContextHubClient clientProxy = this.mService.createPendingIntentClient(hubInfo.getId(), pendingIntent, nanoAppId, attributionTag);
            client.setClientProxy(clientProxy);
            return client;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ContextHubClient createClient(ContextHubInfo hubInfo, PendingIntent pendingIntent, long nanoAppId) {
        return createClient((Context) null, hubInfo, pendingIntent, nanoAppId);
    }

    public long[] getPreloadedNanoAppIds(ContextHubInfo hubInfo) {
        Objects.requireNonNull(hubInfo, "hubInfo cannot be null");
        try {
            long[] nanoappIds = this.mService.getPreloadedNanoAppIds(hubInfo);
            return nanoappIds == null ? new long[0] : nanoappIds;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean enableTestMode() {
        try {
            return this.mService.setTestMode(true);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean disableTestMode() {
        try {
            return this.mService.setTestMode(false);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public int unregisterCallback(Callback callback) {
        synchronized (this) {
            if (callback != this.mCallback) {
                Log.m104w(TAG, "Cannot recognize callback!");
                return -1;
            }
            this.mCallback = null;
            this.mCallbackHandler = null;
            return 0;
        }
    }

    @Deprecated
    public synchronized int unregisterCallback(ICallback callback) {
        if (callback != this.mLocalCallback) {
            Log.m104w(TAG, "Cannot recognize local callback!");
            return -1;
        }
        this.mLocalCallback = null;
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void invokeOnMessageReceiptCallback(int hubId, int nanoAppId, ContextHubMessage message) {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onMessageReceipt(hubId, nanoAppId, message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.hardware.location.ContextHubManager$4 */
    /* loaded from: classes2.dex */
    public class BinderC12034 extends IContextHubCallback.Stub {
        BinderC12034() {
        }

        @Override // android.hardware.location.IContextHubCallback
        public void onMessageReceipt(final int hubId, final int nanoAppId, final ContextHubMessage message) {
            synchronized (ContextHubManager.this) {
                if (ContextHubManager.this.mCallback != null) {
                    ContextHubManager.this.mCallbackHandler.post(new Runnable() { // from class: android.hardware.location.ContextHubManager$4$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ContextHubManager.BinderC12034.this.lambda$onMessageReceipt$0(hubId, nanoAppId, message);
                        }
                    });
                } else if (ContextHubManager.this.mLocalCallback != null) {
                    ContextHubManager.this.mLocalCallback.onMessageReceipt(hubId, nanoAppId, message);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMessageReceipt$0(int hubId, int nanoAppId, ContextHubMessage message) {
            ContextHubManager.this.invokeOnMessageReceiptCallback(hubId, nanoAppId, message);
        }
    }

    public ContextHubManager(Context context, Looper mainLooper) throws ServiceManager.ServiceNotFoundException {
        BinderC12034 binderC12034 = new BinderC12034();
        this.mClientCallback = binderC12034;
        this.mMainLooper = mainLooper;
        IContextHubService asInterface = IContextHubService.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.CONTEXTHUB_SERVICE));
        this.mService = asInterface;
        try {
            asInterface.registerCallback(binderC12034);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
