package android.companion;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.companion.CompanionDeviceManager;
import android.companion.IAssociationRequestCallback;
import android.companion.IOnAssociationsChangedListener;
import android.companion.IOnMessageReceivedListener;
import android.companion.IOnTransportsChangedListener;
import android.companion.ISystemDataTransferCallback;
import android.companion.utils.FeatureUtils;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.net.MacAddress;
import android.p008os.Handler;
import android.p008os.OutcomeReceiver;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.ExceptionUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.CollectionUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public final class CompanionDeviceManager {
    public static final String COMPANION_DEVICE_DISCOVERY_PACKAGE_NAME = "com.android.companiondevicemanager";
    private static final boolean DEBUG = false;
    public static final String EXTRA_ASSOCIATION = "android.companion.extra.ASSOCIATION";
    @Deprecated
    public static final String EXTRA_DEVICE = "android.companion.extra.DEVICE";
    public static final int FLAG_CALL_METADATA = 1;
    private static final String LOG_TAG = "CDM_CompanionDeviceManager";
    public static final String REASON_CANCELED = "canceled";
    public static final String REASON_DISCOVERY_TIMEOUT = "discovery_timeout";
    public static final String REASON_INTERNAL_ERROR = "internal_error";
    public static final String REASON_USER_REJECTED = "user_rejected";
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_DISCOVERY_TIMEOUT = 2;
    public static final int RESULT_INTERNAL_ERROR = 3;
    public static final int RESULT_OK = -1;
    public static final int RESULT_USER_REJECTED = 1;
    private Context mContext;
    private final ICompanionDeviceManager mService;
    private final ArrayList<OnAssociationsChangedListenerProxy> mListeners = new ArrayList<>();
    private final SparseArray<Transport> mTransports = new SparseArray<>();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DataSyncTypes {
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface OnAssociationsChangedListener {
        void onAssociationsChanged(List<AssociationInfo> list);
    }

    /* loaded from: classes.dex */
    public interface OnMessageReceivedListener {
        void onMessageReceived(int i, byte[] bArr);
    }

    /* loaded from: classes.dex */
    public interface OnTransportsChangedListener {
        void onTransportsChanged(List<AssociationInfo> list);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ResultCode {
    }

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public abstract void onFailure(CharSequence charSequence);

        @Deprecated
        public void onDeviceFound(IntentSender intentSender) {
        }

        public void onAssociationPending(IntentSender intentSender) {
            onDeviceFound(intentSender);
        }

        public void onAssociationCreated(AssociationInfo associationInfo) {
        }
    }

    public CompanionDeviceManager(ICompanionDeviceManager service, Context context) {
        this.mService = service;
        this.mContext = context;
    }

    public void associate(AssociationRequest request, Callback callback, Handler handler) {
        if (checkFeaturePresent()) {
            Objects.requireNonNull(request, "Request cannot be null");
            Objects.requireNonNull(callback, "Callback cannot be null");
            try {
                this.mService.associate(request, new AssociationRequestCallbackProxy(Handler.mainIfNull(handler), callback), this.mContext.getOpPackageName(), this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void associate(AssociationRequest request, Executor executor, Callback callback) {
        if (checkFeaturePresent()) {
            Objects.requireNonNull(request, "Request cannot be null");
            Objects.requireNonNull(executor, "Executor cannot be null");
            Objects.requireNonNull(callback, "Callback cannot be null");
            try {
                this.mService.associate(request, new AssociationRequestCallbackProxy(executor, callback), this.mContext.getOpPackageName(), this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public IntentSender buildAssociationCancellationIntent() {
        if (checkFeaturePresent()) {
            try {
                PendingIntent pendingIntent = this.mService.buildAssociationCancellationIntent(this.mContext.getOpPackageName(), this.mContext.getUserId());
                return pendingIntent.getIntentSender();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public void enableSystemDataSyncForTypes(int associationId, int flags) {
        if (!checkFeaturePresent()) {
            return;
        }
        try {
            this.mService.enableSystemDataSync(associationId, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void disableSystemDataSyncForTypes(int associationId, int flags) {
        if (!checkFeaturePresent()) {
            return;
        }
        try {
            this.mService.disableSystemDataSync(associationId, flags);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public List<String> getAssociations() {
        return CollectionUtils.mapNotNull(getMyAssociations(), new Function() { // from class: android.companion.CompanionDeviceManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return CompanionDeviceManager.lambda$getAssociations$0((AssociationInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String lambda$getAssociations$0(AssociationInfo a) {
        if (a.isSelfManaged()) {
            return null;
        }
        return a.getDeviceMacAddressAsString();
    }

    public List<AssociationInfo> getMyAssociations() {
        if (checkFeaturePresent()) {
            try {
                return this.mService.getAssociations(this.mContext.getOpPackageName(), this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    @Deprecated
    public void disassociate(String deviceMacAddress) {
        if (checkFeaturePresent()) {
            try {
                this.mService.legacyDisassociate(deviceMacAddress, this.mContext.getOpPackageName(), this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void disassociate(int associationId) {
        if (checkFeaturePresent()) {
            try {
                this.mService.disassociate(associationId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void requestNotificationAccess(ComponentName component) {
        if (!checkFeaturePresent()) {
            return;
        }
        try {
            IntentSender intentSender = this.mService.requestNotificationAccess(component, this.mContext.getUserId()).getIntentSender();
            this.mContext.startIntentSender(intentSender, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public boolean hasNotificationAccess(ComponentName component) {
        if (!checkFeaturePresent()) {
            return false;
        }
        try {
            return this.mService.hasNotificationAccess(component);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isDeviceAssociatedForWifiConnection(String packageName, MacAddress macAddress, UserHandle user) {
        if (checkFeaturePresent()) {
            Objects.requireNonNull(packageName, "package name cannot be null");
            Objects.requireNonNull(macAddress, "mac address cannot be null");
            Objects.requireNonNull(user, "user cannot be null");
            try {
                return this.mService.isDeviceAssociatedForWifiConnection(packageName, macAddress.toString(), user.getIdentifier());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    @SystemApi
    public List<AssociationInfo> getAllAssociations() {
        if (checkFeaturePresent()) {
            try {
                return this.mService.getAllAssociationsForUser(this.mContext.getUserId());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    @SystemApi
    public void addOnAssociationsChangedListener(Executor executor, OnAssociationsChangedListener listener) {
        if (checkFeaturePresent()) {
            synchronized (this.mListeners) {
                OnAssociationsChangedListenerProxy proxy = new OnAssociationsChangedListenerProxy(executor, listener);
                try {
                    this.mService.addOnAssociationsChangedListener(proxy, this.mContext.getUserId());
                    this.mListeners.add(proxy);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @SystemApi
    public void removeOnAssociationsChangedListener(OnAssociationsChangedListener listener) {
        if (checkFeaturePresent()) {
            synchronized (this.mListeners) {
                Iterator<OnAssociationsChangedListenerProxy> iterator = this.mListeners.iterator();
                while (iterator.hasNext()) {
                    OnAssociationsChangedListenerProxy proxy = iterator.next();
                    if (proxy.mListener == listener) {
                        try {
                            this.mService.removeOnAssociationsChangedListener(proxy, this.mContext.getUserId());
                            iterator.remove();
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
            }
        }
    }

    public void addOnTransportsChangedListener(Executor executor, OnTransportsChangedListener listener) {
        OnTransportsChangedListenerProxy proxy = new OnTransportsChangedListenerProxy(executor, listener);
        try {
            this.mService.addOnTransportsChangedListener(proxy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeOnTransportsChangedListener(OnTransportsChangedListener listener) {
        OnTransportsChangedListenerProxy proxy = new OnTransportsChangedListenerProxy(null, listener);
        try {
            this.mService.removeOnTransportsChangedListener(proxy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void sendMessage(int messageType, byte[] data, int[] associationIds) {
        try {
            this.mService.sendMessage(messageType, data, associationIds);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addOnMessageReceivedListener(Executor executor, int messageType, OnMessageReceivedListener listener) {
        OnMessageReceivedListenerProxy proxy = new OnMessageReceivedListenerProxy(executor, listener);
        try {
            this.mService.addOnMessageReceivedListener(messageType, proxy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeOnMessageReceivedListener(int messageType, OnMessageReceivedListener listener) {
        OnMessageReceivedListenerProxy proxy = new OnMessageReceivedListenerProxy(null, listener);
        try {
            this.mService.removeOnMessageReceivedListener(messageType, proxy);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean canPairWithoutPrompt(String packageName, String deviceMacAddress, UserHandle user) {
        if (!checkFeaturePresent()) {
            return false;
        }
        Objects.requireNonNull(packageName, "package name cannot be null");
        Objects.requireNonNull(deviceMacAddress, "device mac address cannot be null");
        Objects.requireNonNull(user, "user handle cannot be null");
        try {
            return this.mService.canPairWithoutPrompt(packageName, deviceMacAddress, user.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void startObservingDevicePresence(String deviceAddress) throws DeviceNotAssociatedException {
        if (!checkFeaturePresent()) {
            return;
        }
        Objects.requireNonNull(deviceAddress, "address cannot be null");
        try {
            this.mService.registerDevicePresenceListenerService(deviceAddress, this.mContext.getOpPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            ExceptionUtils.propagateIfInstanceOf(e.getCause(), DeviceNotAssociatedException.class);
            throw e.rethrowFromSystemServer();
        }
    }

    public void stopObservingDevicePresence(String deviceAddress) throws DeviceNotAssociatedException {
        if (!checkFeaturePresent()) {
            return;
        }
        Objects.requireNonNull(deviceAddress, "address cannot be null");
        try {
            this.mService.unregisterDevicePresenceListenerService(deviceAddress, this.mContext.getPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            ExceptionUtils.propagateIfInstanceOf(e.getCause(), DeviceNotAssociatedException.class);
        }
    }

    @Deprecated
    public void dispatchMessage(int messageId, int associationId, byte[] message) throws DeviceNotAssociatedException {
        Log.m104w(LOG_TAG, "dispatchMessage replaced by attachSystemDataTransport");
    }

    public void attachSystemDataTransport(int associationId, InputStream in, OutputStream out) throws DeviceNotAssociatedException {
        synchronized (this.mTransports) {
            if (this.mTransports.contains(associationId)) {
                detachSystemDataTransport(associationId);
            }
            try {
                Transport transport = new Transport(associationId, in, out);
                this.mTransports.put(associationId, transport);
                transport.start();
            } catch (IOException e) {
                throw new RuntimeException("Failed to attach transport", e);
            }
        }
    }

    public void detachSystemDataTransport(int associationId) throws DeviceNotAssociatedException {
        synchronized (this.mTransports) {
            Transport transport = this.mTransports.get(associationId);
            if (transport != null) {
                this.mTransports.delete(associationId);
                transport.stop();
            }
        }
    }

    @SystemApi
    public void associate(String packageName, MacAddress macAddress, byte[] certificate) {
        if (!checkFeaturePresent()) {
            return;
        }
        Objects.requireNonNull(packageName, "package name cannot be null");
        Objects.requireNonNull(macAddress, "mac address cannot be null");
        UserHandle user = Process.myUserHandle();
        try {
            this.mService.createAssociation(packageName, macAddress.toString(), user.getIdentifier(), certificate);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyDeviceAppeared(int associationId) {
        try {
            this.mService.notifyDeviceAppeared(associationId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void notifyDeviceDisappeared(int associationId) {
        try {
            this.mService.notifyDeviceDisappeared(associationId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IntentSender buildPermissionTransferUserConsentIntent(int associationId) throws DeviceNotAssociatedException {
        if (!FeatureUtils.isPermSyncEnabled()) {
            throw new UnsupportedOperationException("Calling buildPermissionTransferUserConsentIntent, but this API is disabled by the system.");
        }
        try {
            PendingIntent pendingIntent = this.mService.buildPermissionTransferUserConsentIntent(this.mContext.getOpPackageName(), this.mContext.getUserId(), associationId);
            if (pendingIntent == null) {
                return null;
            }
            return pendingIntent.getIntentSender();
        } catch (RemoteException e) {
            ExceptionUtils.propagateIfInstanceOf(e.getCause(), DeviceNotAssociatedException.class);
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void startSystemDataTransfer(int associationId) throws DeviceNotAssociatedException {
        if (!FeatureUtils.isPermSyncEnabled()) {
            throw new UnsupportedOperationException("Calling startSystemDataTransfer, but this API is disabled by the system.");
        }
        try {
            this.mService.startSystemDataTransfer(this.mContext.getOpPackageName(), this.mContext.getUserId(), associationId, null);
        } catch (RemoteException e) {
            ExceptionUtils.propagateIfInstanceOf(e.getCause(), DeviceNotAssociatedException.class);
            throw e.rethrowFromSystemServer();
        }
    }

    public void startSystemDataTransfer(int associationId, Executor executor, OutcomeReceiver<Void, CompanionException> result) throws DeviceNotAssociatedException {
        if (!FeatureUtils.isPermSyncEnabled()) {
            throw new UnsupportedOperationException("Calling startSystemDataTransfer, but this API is disabled by the system.");
        }
        try {
            this.mService.startSystemDataTransfer(this.mContext.getOpPackageName(), this.mContext.getUserId(), associationId, new SystemDataTransferCallbackProxy(executor, result));
        } catch (RemoteException e) {
            ExceptionUtils.propagateIfInstanceOf(e.getCause(), DeviceNotAssociatedException.class);
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isCompanionApplicationBound() {
        try {
            return this.mService.isCompanionApplicationBound(this.mContext.getOpPackageName(), this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void enableSecureTransport(boolean enabled) {
        try {
            this.mService.enableSecureTransport(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private boolean checkFeaturePresent() {
        return this.mService != null;
    }

    /* loaded from: classes.dex */
    private static class AssociationRequestCallbackProxy extends IAssociationRequestCallback.Stub {
        private final Callback mCallback;
        private final Executor mExecutor;
        private final Handler mHandler;

        private AssociationRequestCallbackProxy(Executor executor, Callback callback) {
            this.mExecutor = executor;
            this.mHandler = null;
            this.mCallback = callback;
        }

        private AssociationRequestCallbackProxy(Handler handler, Callback callback) {
            this.mHandler = handler;
            this.mExecutor = null;
            this.mCallback = callback;
        }

        @Override // android.companion.IAssociationRequestCallback
        public void onAssociationPending(PendingIntent pi) {
            final Callback callback = this.mCallback;
            Objects.requireNonNull(callback);
            execute(new Consumer() { // from class: android.companion.CompanionDeviceManager$AssociationRequestCallbackProxy$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    CompanionDeviceManager.Callback.this.onAssociationPending((IntentSender) obj);
                }
            }, pi.getIntentSender());
        }

        @Override // android.companion.IAssociationRequestCallback
        public void onAssociationCreated(AssociationInfo association) {
            final Callback callback = this.mCallback;
            Objects.requireNonNull(callback);
            execute(new Consumer() { // from class: android.companion.CompanionDeviceManager$AssociationRequestCallbackProxy$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    CompanionDeviceManager.Callback.this.onAssociationCreated((AssociationInfo) obj);
                }
            }, association);
        }

        @Override // android.companion.IAssociationRequestCallback
        public void onFailure(CharSequence error) throws RemoteException {
            final Callback callback = this.mCallback;
            Objects.requireNonNull(callback);
            execute(new Consumer() { // from class: android.companion.CompanionDeviceManager$AssociationRequestCallbackProxy$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    CompanionDeviceManager.Callback.this.onFailure((CharSequence) obj);
                }
            }, error);
        }

        private <T> void execute(final Consumer<T> callback, final T arg) {
            Executor executor = this.mExecutor;
            if (executor != null) {
                executor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$AssociationRequestCallbackProxy$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.accept(arg);
                    }
                });
            } else {
                this.mHandler.post(new Runnable() { // from class: android.companion.CompanionDeviceManager$AssociationRequestCallbackProxy$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback.accept(arg);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OnAssociationsChangedListenerProxy extends IOnAssociationsChangedListener.Stub {
        private final Executor mExecutor;
        private final OnAssociationsChangedListener mListener;

        private OnAssociationsChangedListenerProxy(Executor executor, OnAssociationsChangedListener listener) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAssociationsChanged$0(List associations) {
            this.mListener.onAssociationsChanged(associations);
        }

        @Override // android.companion.IOnAssociationsChangedListener
        public void onAssociationsChanged(final List<AssociationInfo> associations) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$OnAssociationsChangedListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceManager.OnAssociationsChangedListenerProxy.this.lambda$onAssociationsChanged$0(associations);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OnTransportsChangedListenerProxy extends IOnTransportsChangedListener.Stub {
        private final Executor mExecutor;
        private final OnTransportsChangedListener mListener;

        private OnTransportsChangedListenerProxy(Executor executor, OnTransportsChangedListener listener) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTransportsChanged$0(List associations) {
            this.mListener.onTransportsChanged(associations);
        }

        @Override // android.companion.IOnTransportsChangedListener
        public void onTransportsChanged(final List<AssociationInfo> associations) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$OnTransportsChangedListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceManager.OnTransportsChangedListenerProxy.this.lambda$onTransportsChanged$0(associations);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OnMessageReceivedListenerProxy extends IOnMessageReceivedListener.Stub {
        private final Executor mExecutor;
        private final OnMessageReceivedListener mListener;

        private OnMessageReceivedListenerProxy(Executor executor, OnMessageReceivedListener listener) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMessageReceived$0(int associationId, byte[] data) {
            this.mListener.onMessageReceived(associationId, data);
        }

        @Override // android.companion.IOnMessageReceivedListener
        public void onMessageReceived(final int associationId, final byte[] data) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$OnMessageReceivedListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceManager.OnMessageReceivedListenerProxy.this.lambda$onMessageReceived$0(associationId, data);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SystemDataTransferCallbackProxy extends ISystemDataTransferCallback.Stub {
        private final OutcomeReceiver<Void, CompanionException> mCallback;
        private final Executor mExecutor;

        private SystemDataTransferCallbackProxy(Executor executor, OutcomeReceiver<Void, CompanionException> callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResult$0() {
            this.mCallback.onResult(null);
        }

        @Override // android.companion.ISystemDataTransferCallback
        public void onResult() {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$SystemDataTransferCallbackProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceManager.SystemDataTransferCallbackProxy.this.lambda$onResult$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(String error) {
            this.mCallback.onError(new CompanionException(error));
        }

        @Override // android.companion.ISystemDataTransferCallback
        public void onError(final String error) {
            this.mExecutor.execute(new Runnable() { // from class: android.companion.CompanionDeviceManager$SystemDataTransferCallbackProxy$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceManager.SystemDataTransferCallbackProxy.this.lambda$onError$1(error);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Transport {
        private final int mAssociationId;
        private InputStream mLocalIn;
        private OutputStream mLocalOut;
        private final InputStream mRemoteIn;
        private final OutputStream mRemoteOut;
        private volatile boolean mStopped;

        public Transport(int associationId, InputStream remoteIn, OutputStream remoteOut) {
            this.mAssociationId = associationId;
            this.mRemoteIn = remoteIn;
            this.mRemoteOut = remoteOut;
        }

        public void start() throws IOException {
            ParcelFileDescriptor[] pair = ParcelFileDescriptor.createSocketPair();
            ParcelFileDescriptor localFd = pair[0];
            ParcelFileDescriptor remoteFd = pair[1];
            this.mLocalIn = new ParcelFileDescriptor.AutoCloseInputStream(localFd);
            this.mLocalOut = new ParcelFileDescriptor.AutoCloseOutputStream(localFd);
            try {
                CompanionDeviceManager.this.mService.attachSystemDataTransport(CompanionDeviceManager.this.mContext.getPackageName(), CompanionDeviceManager.this.mContext.getUserId(), this.mAssociationId, remoteFd);
                new Thread(new Runnable() { // from class: android.companion.CompanionDeviceManager$Transport$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CompanionDeviceManager.Transport.this.lambda$start$0();
                    }
                }).start();
                new Thread(new Runnable() { // from class: android.companion.CompanionDeviceManager$Transport$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        CompanionDeviceManager.Transport.this.lambda$start$1();
                    }
                }).start();
            } catch (RemoteException e) {
                throw new IOException("Failed to configure transport", e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$0() {
            try {
                copyWithFlushing(this.mLocalIn, this.mRemoteOut);
            } catch (IOException e) {
                if (!this.mStopped) {
                    Log.m103w(CompanionDeviceManager.LOG_TAG, "Trouble during outgoing transport", e);
                    stop();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$1() {
            try {
                copyWithFlushing(this.mRemoteIn, this.mLocalOut);
            } catch (IOException e) {
                if (!this.mStopped) {
                    Log.m103w(CompanionDeviceManager.LOG_TAG, "Trouble during incoming transport", e);
                    stop();
                }
            }
        }

        public void stop() {
            this.mStopped = true;
            try {
                CompanionDeviceManager.this.mService.detachSystemDataTransport(CompanionDeviceManager.this.mContext.getPackageName(), CompanionDeviceManager.this.mContext.getUserId(), this.mAssociationId);
            } catch (RemoteException e) {
                Log.m103w(CompanionDeviceManager.LOG_TAG, "Failed to detach transport", e);
            }
            IoUtils.closeQuietly(this.mRemoteIn);
            IoUtils.closeQuietly(this.mRemoteOut);
            IoUtils.closeQuietly(this.mLocalIn);
            IoUtils.closeQuietly(this.mLocalOut);
        }

        private void copyWithFlushing(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[8192];
            while (true) {
                int c = in.read(buffer);
                if (c != -1) {
                    out.write(buffer, 0, c);
                    out.flush();
                } else {
                    return;
                }
            }
        }
    }
}
