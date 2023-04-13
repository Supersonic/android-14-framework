package android.credentials;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentSender;
import android.credentials.CredentialManager;
import android.credentials.IClearCredentialStateCallback;
import android.credentials.ICreateCredentialCallback;
import android.credentials.IGetCredentialCallback;
import android.credentials.ISetEnabledProvidersCallback;
import android.p008os.CancellationSignal;
import android.p008os.ICancellationSignal;
import android.p008os.OutcomeReceiver;
import android.p008os.RemoteException;
import android.provider.DeviceConfig;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class CredentialManager {
    private static final String DEVICE_CONFIG_ENABLE_CREDENTIAL_DESC_API = "enable_credential_description_api";
    public static final String DEVICE_CONFIG_ENABLE_CREDENTIAL_MANAGER = "enable_credential_manager";
    public static final int PROVIDER_FILTER_ALL_PROVIDERS = 0;
    public static final int PROVIDER_FILTER_SYSTEM_PROVIDERS_ONLY = 1;
    public static final int PROVIDER_FILTER_USER_PROVIDERS_ONLY = 2;
    private static final String TAG = "CredentialManager";
    private final Context mContext;
    private final ICredentialManager mService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProviderFilter {
    }

    public CredentialManager(Context context, ICredentialManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public void getCredential(GetCredentialRequest request, Activity activity, CancellationSignal cancellationSignal, Executor executor, OutcomeReceiver<GetCredentialResponse, GetCredentialException> callback) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(activity, "activity must not be null");
        Objects.requireNonNull(executor, "executor must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        if (cancellationSignal != null && cancellationSignal.isCanceled()) {
            Log.m104w(TAG, "getCredential already canceled");
            return;
        }
        ICancellationSignal cancelRemote = null;
        try {
            cancelRemote = this.mService.executeGetCredential(request, new GetCredentialTransport(activity, executor, callback), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        if (cancellationSignal != null && cancelRemote != null) {
            cancellationSignal.setRemote(cancelRemote);
        }
    }

    public void createCredential(CreateCredentialRequest request, Activity activity, CancellationSignal cancellationSignal, Executor executor, OutcomeReceiver<CreateCredentialResponse, CreateCredentialException> callback) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(activity, "activity must not be null");
        Objects.requireNonNull(executor, "executor must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        if (cancellationSignal != null && cancellationSignal.isCanceled()) {
            Log.m104w(TAG, "createCredential already canceled");
            return;
        }
        ICancellationSignal cancelRemote = null;
        try {
            cancelRemote = this.mService.executeCreateCredential(request, new CreateCredentialTransport(activity, executor, callback), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        if (cancellationSignal != null && cancelRemote != null) {
            cancellationSignal.setRemote(cancelRemote);
        }
    }

    public void clearCredentialState(ClearCredentialStateRequest request, CancellationSignal cancellationSignal, Executor executor, OutcomeReceiver<Void, ClearCredentialStateException> callback) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(executor, "executor must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        if (cancellationSignal != null && cancellationSignal.isCanceled()) {
            Log.m104w(TAG, "clearCredentialState already canceled");
            return;
        }
        ICancellationSignal cancelRemote = null;
        try {
            cancelRemote = this.mService.clearCredentialState(request, new ClearCredentialStateTransport(executor, callback), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        if (cancellationSignal != null && cancelRemote != null) {
            cancellationSignal.setRemote(cancelRemote);
        }
    }

    public void setEnabledProviders(List<String> providers, int userId, Executor executor, OutcomeReceiver<Void, SetEnabledProvidersException> callback) {
        Objects.requireNonNull(executor, "executor must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        Objects.requireNonNull(providers, "providers must not be null");
        try {
            this.mService.setEnabledProviders(providers, userId, new SetEnabledProvidersTransport(executor, callback));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public boolean isEnabledCredentialProviderService(ComponentName componentName) {
        Objects.requireNonNull(componentName, "componentName must not be null");
        try {
            return this.mService.isEnabledCredentialProviderService(componentName, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<CredentialProviderInfo> getCredentialProviderServicesForTesting(int providerFilter) {
        try {
            return this.mService.getCredentialProviderServicesForTesting(providerFilter);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<CredentialProviderInfo> getCredentialProviderServices(int userId, int providerFilter) {
        try {
            return this.mService.getCredentialProviderServices(userId, providerFilter);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean isServiceEnabled(Context context) {
        CredentialManager credentialManager;
        Objects.requireNonNull(context, "context must not be null");
        if (context == null || (credentialManager = (CredentialManager) context.getSystemService(Context.CREDENTIAL_SERVICE)) == null) {
            return false;
        }
        return credentialManager.isServiceEnabled();
    }

    private boolean isServiceEnabled() {
        return DeviceConfig.getBoolean("credential_manager", DEVICE_CONFIG_ENABLE_CREDENTIAL_MANAGER, true);
    }

    public static boolean isCredentialDescriptionApiEnabled(Context context) {
        CredentialManager credentialManager;
        if (context == null || (credentialManager = (CredentialManager) context.getSystemService(Context.CREDENTIAL_SERVICE)) == null) {
            return false;
        }
        return credentialManager.isCredentialDescriptionApiEnabled();
    }

    private boolean isCredentialDescriptionApiEnabled() {
        return DeviceConfig.getBoolean("credential_manager", DEVICE_CONFIG_ENABLE_CREDENTIAL_DESC_API, false);
    }

    public void registerCredentialDescription(RegisterCredentialDescriptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        try {
            this.mService.registerCredentialDescription(request, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void unregisterCredentialDescription(UnregisterCredentialDescriptionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        try {
            this.mService.unregisterCredentialDescription(request, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GetCredentialTransport extends IGetCredentialCallback.Stub {
        private final Activity mActivity;
        private final OutcomeReceiver<GetCredentialResponse, GetCredentialException> mCallback;
        private final Executor mExecutor;

        private GetCredentialTransport(Activity activity, Executor executor, OutcomeReceiver<GetCredentialResponse, GetCredentialException> callback) {
            this.mActivity = activity;
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.credentials.IGetCredentialCallback
        public void onPendingIntent(PendingIntent pendingIntent) {
            try {
                this.mActivity.startIntentSender(pendingIntent.getIntentSender(), null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.m109e(CredentialManager.TAG, "startIntentSender() failed for intent:" + pendingIntent.getIntentSender(), e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResponse$0(GetCredentialResponse response) {
            this.mCallback.onResult(response);
        }

        @Override // android.credentials.IGetCredentialCallback
        public void onResponse(final GetCredentialResponse response) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$GetCredentialTransport$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.GetCredentialTransport.this.lambda$onResponse$0(response);
                }
            });
        }

        @Override // android.credentials.IGetCredentialCallback
        public void onError(final String errorType, final String message) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$GetCredentialTransport$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.GetCredentialTransport.this.lambda$onError$1(errorType, message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(String errorType, String message) {
            this.mCallback.onError(new GetCredentialException(errorType, message));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CreateCredentialTransport extends ICreateCredentialCallback.Stub {
        private final Activity mActivity;
        private final OutcomeReceiver<CreateCredentialResponse, CreateCredentialException> mCallback;
        private final Executor mExecutor;

        private CreateCredentialTransport(Activity activity, Executor executor, OutcomeReceiver<CreateCredentialResponse, CreateCredentialException> callback) {
            this.mActivity = activity;
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.credentials.ICreateCredentialCallback
        public void onPendingIntent(PendingIntent pendingIntent) {
            try {
                this.mActivity.startIntentSender(pendingIntent.getIntentSender(), null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.m109e(CredentialManager.TAG, "startIntentSender() failed for intent:" + pendingIntent.getIntentSender(), e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResponse$0(CreateCredentialResponse response) {
            this.mCallback.onResult(response);
        }

        @Override // android.credentials.ICreateCredentialCallback
        public void onResponse(final CreateCredentialResponse response) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$CreateCredentialTransport$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.CreateCredentialTransport.this.lambda$onResponse$0(response);
                }
            });
        }

        @Override // android.credentials.ICreateCredentialCallback
        public void onError(final String errorType, final String message) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$CreateCredentialTransport$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.CreateCredentialTransport.this.lambda$onError$1(errorType, message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(String errorType, String message) {
            this.mCallback.onError(new CreateCredentialException(errorType, message));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ClearCredentialStateTransport extends IClearCredentialStateCallback.Stub {
        private final OutcomeReceiver<Void, ClearCredentialStateException> mCallback;
        private final Executor mExecutor;

        private ClearCredentialStateTransport(Executor executor, OutcomeReceiver<Void, ClearCredentialStateException> callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.credentials.IClearCredentialStateCallback
        public void onSuccess() {
            this.mCallback.onResult(null);
        }

        @Override // android.credentials.IClearCredentialStateCallback
        public void onError(final String errorType, final String message) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$ClearCredentialStateTransport$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.ClearCredentialStateTransport.this.lambda$onError$0(errorType, message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$0(String errorType, String message) {
            this.mCallback.onError(new ClearCredentialStateException(errorType, message));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SetEnabledProvidersTransport extends ISetEnabledProvidersCallback.Stub {
        private final OutcomeReceiver<Void, SetEnabledProvidersException> mCallback;
        private final Executor mExecutor;

        private SetEnabledProvidersTransport(Executor executor, OutcomeReceiver<Void, SetEnabledProvidersException> callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResponse$0(Void result) {
            this.mCallback.onResult(result);
        }

        public void onResponse(final Void result) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$SetEnabledProvidersTransport$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.SetEnabledProvidersTransport.this.lambda$onResponse$0(result);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onResponse$1() {
            this.mCallback.onResult(null);
        }

        @Override // android.credentials.ISetEnabledProvidersCallback
        public void onResponse() {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$SetEnabledProvidersTransport$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.SetEnabledProvidersTransport.this.lambda$onResponse$1();
                }
            });
        }

        @Override // android.credentials.ISetEnabledProvidersCallback
        public void onError(final String errorType, final String message) {
            this.mExecutor.execute(new Runnable() { // from class: android.credentials.CredentialManager$SetEnabledProvidersTransport$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CredentialManager.SetEnabledProvidersTransport.this.lambda$onError$2(errorType, message);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$2(String errorType, String message) {
            this.mCallback.onError(new SetEnabledProvidersException(errorType, message));
        }
    }
}
