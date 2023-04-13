package android.service.assist.classification;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.p008os.BaseBundle;
import android.p008os.Build;
import android.p008os.CancellationSignal;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.OutcomeReceiver;
import android.p008os.RemoteException;
import android.service.assist.classification.IFieldClassificationService;
import android.util.Log;
@SystemApi
/* loaded from: classes3.dex */
public abstract class FieldClassificationService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.assist.classification.FieldClassificationService";
    private static final String TAG = FieldClassificationService.class.getSimpleName();
    static boolean sDebug = !Build.IS_USER;
    static boolean sVerbose = false;
    private ComponentName mServiceComponentName;

    public abstract void onClassificationRequest(FieldClassificationRequest fieldClassificationRequest, CancellationSignal cancellationSignal, OutcomeReceiver<FieldClassificationResponse, Exception> outcomeReceiver);

    /* loaded from: classes3.dex */
    private final class FieldClassificationServiceImpl extends IFieldClassificationService.Stub {
        private FieldClassificationServiceImpl() {
        }

        @Override // android.service.assist.classification.IFieldClassificationService
        public void onConnected(boolean debug, boolean verbose) {
            FieldClassificationService.this.handleOnConnected(debug, verbose);
        }

        @Override // android.service.assist.classification.IFieldClassificationService
        public void onDisconnected() {
            FieldClassificationService.this.handleOnDisconnected();
        }

        @Override // android.service.assist.classification.IFieldClassificationService
        public void onFieldClassificationRequest(FieldClassificationRequest request, IFieldClassificationCallback callback) {
            FieldClassificationService.this.handleOnClassificationRequest(request, callback);
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        BaseBundle.setShouldDefuse(true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            this.mServiceComponentName = intent.getComponent();
            return new FieldClassificationServiceImpl().asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.assist.classification.FieldClassificationService: " + intent);
        return null;
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnConnected(boolean debug, boolean verbose) {
        if (sDebug || debug) {
            Log.m112d(TAG, "handleOnConnected(): debug=" + debug + ", verbose=" + verbose);
        }
        sDebug = debug;
        sVerbose = verbose;
        onConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDisconnected() {
        onDisconnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnClassificationRequest(FieldClassificationRequest request, final IFieldClassificationCallback callback) {
        ICancellationSignal transport = CancellationSignal.createTransport();
        CancellationSignal cancellationSignal = CancellationSignal.fromTransport(transport);
        onClassificationRequest(request, cancellationSignal, new OutcomeReceiver<FieldClassificationResponse, Exception>() { // from class: android.service.assist.classification.FieldClassificationService.1
            @Override // android.p008os.OutcomeReceiver
            public void onResult(FieldClassificationResponse result) {
                try {
                    callback.onSuccess(result);
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }

            @Override // android.p008os.OutcomeReceiver
            public void onError(Exception e) {
                try {
                    callback.onFailure();
                } catch (RemoteException ex) {
                    ex.rethrowFromSystemServer();
                }
            }
        });
    }
}
