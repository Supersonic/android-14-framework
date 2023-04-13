package android.service.translation;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.content.p001pm.ParceledListSlice;
import android.p008os.BaseBundle;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.service.translation.ITranslationService;
import android.util.Log;
import android.view.translation.ITranslationDirectManager;
import android.view.translation.ITranslationServiceCallback;
import android.view.translation.TranslationCapability;
import android.view.translation.TranslationContext;
import android.view.translation.TranslationManager;
import android.view.translation.TranslationRequest;
import android.view.translation.TranslationResponse;
import com.android.internal.p028os.IResultReceiver;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class TranslationService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.translation.TranslationService";
    public static final String SERVICE_META_DATA = "android.translation_service";
    private static final String TAG = "TranslationService";
    private ITranslationServiceCallback mCallback;
    private Handler mHandler;
    private final ITranslationService mInterface = new BinderC26561();
    private final ITranslationDirectManager mClientInterface = new ITranslationDirectManager.Stub() { // from class: android.service.translation.TranslationService.2
        @Override // android.view.translation.ITranslationDirectManager
        public void onTranslationRequest(TranslationRequest request, int sessionId, ICancellationSignal transport, ITranslationCallback callback) throws RemoteException {
            Consumer<TranslationResponse> consumer = new OnTranslationResultCallbackWrapper(callback);
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: android.service.translation.TranslationService$2$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.QuintConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((TranslationService) obj).onTranslationRequest((TranslationRequest) obj2, ((Integer) obj3).intValue(), (CancellationSignal) obj4, (Consumer) obj5);
                }
            }, TranslationService.this, request, Integer.valueOf(sessionId), CancellationSignal.fromTransport(transport), consumer));
        }

        @Override // android.view.translation.ITranslationDirectManager
        public void onFinishTranslationSession(int sessionId) throws RemoteException {
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.translation.TranslationService$2$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((TranslationService) obj).onFinishTranslationSession(((Integer) obj2).intValue());
                }
            }, TranslationService.this, Integer.valueOf(sessionId)));
        }
    };

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnTranslationResultCallback {
        @Deprecated
        void onError();

        void onTranslationSuccess(TranslationResponse translationResponse);
    }

    public abstract void onCreateTranslationSession(TranslationContext translationContext, int i, Consumer<Boolean> consumer);

    public abstract void onFinishTranslationSession(int i);

    public abstract void onTranslationCapabilitiesRequest(int i, int i2, Consumer<Set<TranslationCapability>> consumer);

    public abstract void onTranslationRequest(TranslationRequest translationRequest, int i, CancellationSignal cancellationSignal, Consumer<TranslationResponse> consumer);

    /* renamed from: android.service.translation.TranslationService$1 */
    /* loaded from: classes3.dex */
    class BinderC26561 extends ITranslationService.Stub {
        BinderC26561() {
        }

        @Override // android.service.translation.ITranslationService
        public void onConnected(IBinder callback) {
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.translation.TranslationService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((TranslationService) obj).handleOnConnected((IBinder) obj2);
                }
            }, TranslationService.this, callback));
        }

        @Override // android.service.translation.ITranslationService
        public void onDisconnected() {
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.translation.TranslationService$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((TranslationService) obj).onDisconnected();
                }
            }, TranslationService.this));
        }

        @Override // android.service.translation.ITranslationService
        public void onCreateTranslationSession(TranslationContext translationContext, int sessionId, IResultReceiver receiver) throws RemoteException {
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.translation.TranslationService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((TranslationService) obj).handleOnCreateTranslationSession((TranslationContext) obj2, ((Integer) obj3).intValue(), (IResultReceiver) obj4);
                }
            }, TranslationService.this, translationContext, Integer.valueOf(sessionId), receiver));
        }

        @Override // android.service.translation.ITranslationService
        public void onTranslationCapabilitiesRequest(int sourceFormat, int targetFormat, ResultReceiver resultReceiver) throws RemoteException {
            TranslationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.translation.TranslationService$1$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((TranslationService) obj).handleOnTranslationCapabilitiesRequest(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (ResultReceiver) obj4);
                }
            }, TranslationService.this, Integer.valueOf(sourceFormat), Integer.valueOf(targetFormat), resultReceiver));
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
        BaseBundle.setShouldDefuse(true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.translation.TranslationService: " + intent);
        return null;
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    @Deprecated
    public void onCreateTranslationSession(TranslationContext translationContext, int sessionId) {
    }

    @Deprecated
    public void onTranslationRequest(TranslationRequest request, int sessionId, CancellationSignal cancellationSignal, OnTranslationResultCallback callback) {
    }

    public final void updateTranslationCapability(TranslationCapability capability) {
        Objects.requireNonNull(capability, "translation capability should not be null");
        ITranslationServiceCallback callback = this.mCallback;
        if (callback == null) {
            Log.m104w(TAG, "updateTranslationCapability(): no server callback");
            return;
        }
        try {
            callback.updateTranslationCapability(capability);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnConnected(IBinder callback) {
        this.mCallback = ITranslationServiceCallback.Stub.asInterface(callback);
        onConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnCreateTranslationSession(final TranslationContext translationContext, final int sessionId, final IResultReceiver resultReceiver) {
        onCreateTranslationSession(translationContext, sessionId, new Consumer<Boolean>() { // from class: android.service.translation.TranslationService.3
            @Override // java.util.function.Consumer
            public void accept(Boolean created) {
                try {
                    if (!created.booleanValue()) {
                        Log.m104w(TranslationService.TAG, "handleOnCreateTranslationSession(): context=" + translationContext + " not supported by service.");
                        resultReceiver.send(2, null);
                        return;
                    }
                    Bundle extras = new Bundle();
                    extras.putBinder("binder", TranslationService.this.mClientInterface.asBinder());
                    extras.putInt("sessionId", sessionId);
                    resultReceiver.send(1, extras);
                } catch (RemoteException e) {
                    Log.m104w(TranslationService.TAG, "RemoteException sending client interface: " + e);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnTranslationCapabilitiesRequest(final int sourceFormat, final int targetFormat, final ResultReceiver resultReceiver) {
        onTranslationCapabilitiesRequest(sourceFormat, targetFormat, new Consumer<Set<TranslationCapability>>() { // from class: android.service.translation.TranslationService.4
            @Override // java.util.function.Consumer
            public void accept(Set<TranslationCapability> values) {
                if (!TranslationService.this.isValidCapabilities(sourceFormat, targetFormat, values)) {
                    throw new IllegalStateException("Invalid capabilities and format compatibility");
                }
                Bundle bundle = new Bundle();
                ParceledListSlice<TranslationCapability> listSlice = new ParceledListSlice<>(Arrays.asList((TranslationCapability[]) values.toArray(new TranslationCapability[0])));
                bundle.putParcelable(TranslationManager.EXTRA_CAPABILITIES, listSlice);
                resultReceiver.send(1, bundle);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidCapabilities(int sourceFormat, int targetFormat, Set<TranslationCapability> capabilities) {
        if (sourceFormat != 1 && targetFormat != 1) {
            return true;
        }
        for (TranslationCapability capability : capabilities) {
            if (capability.getState() == 1000) {
                return false;
            }
        }
        return true;
    }
}
