package android.service.credentials;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.credentials.ClearCredentialStateException;
import android.credentials.CreateCredentialException;
import android.credentials.GetCredentialException;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Looper;
import android.p008os.OutcomeReceiver;
import android.p008os.RemoteException;
import android.service.credentials.CredentialProviderService;
import android.service.credentials.ICredentialProviderService;
import android.util.Log;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.Objects;
/* loaded from: classes3.dex */
public abstract class CredentialProviderService extends Service {
    public static final String CAPABILITY_META_DATA_KEY = "android.credentials.capabilities";
    public static final String EXTRA_BEGIN_GET_CREDENTIAL_REQUEST = "android.service.credentials.extra.BEGIN_GET_CREDENTIAL_REQUEST";
    public static final String EXTRA_BEGIN_GET_CREDENTIAL_RESPONSE = "android.service.credentials.extra.BEGIN_GET_CREDENTIAL_RESPONSE";
    public static final String EXTRA_CREATE_CREDENTIAL_EXCEPTION = "android.service.credentials.extra.CREATE_CREDENTIAL_EXCEPTION";
    public static final String EXTRA_CREATE_CREDENTIAL_REQUEST = "android.service.credentials.extra.CREATE_CREDENTIAL_REQUEST";
    public static final String EXTRA_CREATE_CREDENTIAL_RESPONSE = "android.service.credentials.extra.CREATE_CREDENTIAL_RESPONSE";
    public static final String EXTRA_GET_CREDENTIAL_EXCEPTION = "android.service.credentials.extra.GET_CREDENTIAL_EXCEPTION";
    public static final String EXTRA_GET_CREDENTIAL_REQUEST = "android.service.credentials.extra.GET_CREDENTIAL_REQUEST";
    public static final String EXTRA_GET_CREDENTIAL_RESPONSE = "android.service.credentials.extra.GET_CREDENTIAL_RESPONSE";
    public static final String SERVICE_INTERFACE = "android.service.credentials.CredentialProviderService";
    public static final String SYSTEM_SERVICE_INTERFACE = "android.service.credentials.system.CredentialProviderService";
    private static final String TAG = "CredProviderService";
    public static final String TEST_SYSTEM_PROVIDER_META_DATA_KEY = "android.credentials.testsystemprovider";
    private Handler mHandler;
    private final ICredentialProviderService mInterface = new ICredentialProviderService.Stub() { // from class: android.service.credentials.CredentialProviderService.1
        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onBeginGetCredential(BeginGetCredentialRequest request, final IBeginGetCredentialCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            ICancellationSignal transport = CancellationSignal.createTransport();
            CredentialProviderService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.credentials.CredentialProviderService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((CredentialProviderService) obj).onBeginGetCredential((BeginGetCredentialRequest) obj2, (CancellationSignal) obj3, (CredentialProviderService.BinderC25211.C25221) obj4);
                }
            }, CredentialProviderService.this, request, CancellationSignal.fromTransport(transport), new OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException>() { // from class: android.service.credentials.CredentialProviderService.1.1
                @Override // android.p008os.OutcomeReceiver
                public void onResult(BeginGetCredentialResponse result) {
                    if (result.getRemoteCredentialEntry() != null) {
                        enforceRemoteEntryPermission();
                    }
                    try {
                        callback.onSuccess(result);
                    } catch (RemoteException e) {
                        e.rethrowFromSystemServer();
                    }
                }

                @Override // android.p008os.OutcomeReceiver
                public void onError(GetCredentialException e) {
                    try {
                        callback.onFailure(e.getType(), e.getMessage());
                    } catch (RemoteException ex) {
                        ex.rethrowFromSystemServer();
                    }
                }
            }));
            return transport;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void enforceRemoteEntryPermission() {
            CredentialProviderService.this.getApplicationContext().enforceCallingOrSelfPermission(Manifest.C0000permission.PROVIDE_REMOTE_CREDENTIALS, String.format("Provider must have %s, in order to set a remote entry", Manifest.C0000permission.PROVIDE_REMOTE_CREDENTIALS));
        }

        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onBeginCreateCredential(BeginCreateCredentialRequest request, final IBeginCreateCredentialCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            ICancellationSignal transport = CancellationSignal.createTransport();
            CredentialProviderService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.credentials.CredentialProviderService$1$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((CredentialProviderService) obj).onBeginCreateCredential((BeginCreateCredentialRequest) obj2, (CancellationSignal) obj3, (CredentialProviderService.BinderC25211.C25232) obj4);
                }
            }, CredentialProviderService.this, request, CancellationSignal.fromTransport(transport), new OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException>() { // from class: android.service.credentials.CredentialProviderService.1.2
                @Override // android.p008os.OutcomeReceiver
                public void onResult(BeginCreateCredentialResponse result) {
                    if (result.getRemoteCreateEntry() != null) {
                        enforceRemoteEntryPermission();
                    }
                    try {
                        callback.onSuccess(result);
                    } catch (RemoteException e) {
                        e.rethrowFromSystemServer();
                    }
                }

                @Override // android.p008os.OutcomeReceiver
                public void onError(CreateCredentialException e) {
                    try {
                        callback.onFailure(e.getType(), e.getMessage());
                    } catch (RemoteException ex) {
                        ex.rethrowFromSystemServer();
                    }
                }
            }));
            return transport;
        }

        @Override // android.service.credentials.ICredentialProviderService
        public ICancellationSignal onClearCredentialState(ClearCredentialStateRequest request, final IClearCredentialStateCallback callback) {
            Objects.requireNonNull(request);
            Objects.requireNonNull(callback);
            ICancellationSignal transport = CancellationSignal.createTransport();
            CredentialProviderService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.credentials.CredentialProviderService$1$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((CredentialProviderService) obj).onClearCredentialState((ClearCredentialStateRequest) obj2, (CancellationSignal) obj3, (CredentialProviderService.BinderC25211.C25243) obj4);
                }
            }, CredentialProviderService.this, request, CancellationSignal.fromTransport(transport), new OutcomeReceiver<Void, ClearCredentialStateException>() { // from class: android.service.credentials.CredentialProviderService.1.3
                @Override // android.p008os.OutcomeReceiver
                public void onResult(Void result) {
                    try {
                        callback.onSuccess();
                    } catch (RemoteException e) {
                        e.rethrowFromSystemServer();
                    }
                }

                @Override // android.p008os.OutcomeReceiver
                public void onError(ClearCredentialStateException e) {
                    try {
                        callback.onFailure(e.getType(), e.getMessage());
                    } catch (RemoteException ex) {
                        ex.rethrowFromSystemServer();
                    }
                }
            }));
            return transport;
        }
    };

    public abstract void onBeginCreateCredential(BeginCreateCredentialRequest beginCreateCredentialRequest, CancellationSignal cancellationSignal, OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException> outcomeReceiver);

    public abstract void onBeginGetCredential(BeginGetCredentialRequest beginGetCredentialRequest, CancellationSignal cancellationSignal, OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException> outcomeReceiver);

    public abstract void onClearCredentialState(ClearCredentialStateRequest clearCredentialStateRequest, CancellationSignal cancellationSignal, OutcomeReceiver<Void, ClearCredentialStateException> outcomeReceiver);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Log.m108i(TAG, "Failed to bind with intent: " + intent);
        return null;
    }
}
