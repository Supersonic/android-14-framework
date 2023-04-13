package android.service.appprediction;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.AppTargetId;
import android.app.prediction.IPredictionCallback;
import android.content.Intent;
import android.content.p001pm.ParceledListSlice;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.service.appprediction.AppPredictionService;
import android.service.appprediction.IPredictionService;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AppPredictionService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.appprediction.AppPredictionService";
    private static final String TAG = "AppPredictionService";
    private Handler mHandler;
    private final ArrayMap<AppPredictionSessionId, ArrayList<CallbackWrapper>> mSessionCallbacks = new ArrayMap<>();
    private final IPredictionService mInterface = new BinderC24321();

    public abstract void onAppTargetEvent(AppPredictionSessionId appPredictionSessionId, AppTargetEvent appTargetEvent);

    public abstract void onLaunchLocationShown(AppPredictionSessionId appPredictionSessionId, String str, List<AppTargetId> list);

    public abstract void onRequestPredictionUpdate(AppPredictionSessionId appPredictionSessionId);

    public abstract void onSortAppTargets(AppPredictionSessionId appPredictionSessionId, List<AppTarget> list, CancellationSignal cancellationSignal, Consumer<List<AppTarget>> consumer);

    /* renamed from: android.service.appprediction.AppPredictionService$1 */
    /* loaded from: classes3.dex */
    class BinderC24321 extends IPredictionService.Stub {
        BinderC24321() {
        }

        @Override // android.service.appprediction.IPredictionService
        public void onCreatePredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda5
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AppPredictionService) obj).doCreatePredictionSession((AppPredictionContext) obj2, (AppPredictionSessionId) obj3);
                }
            }, AppPredictionService.this, context, sessionId));
        }

        @Override // android.service.appprediction.IPredictionService
        public void notifyAppTargetEvent(AppPredictionSessionId sessionId, AppTargetEvent event) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AppPredictionService) obj).onAppTargetEvent((AppPredictionSessionId) obj2, (AppTargetEvent) obj3);
                }
            }, AppPredictionService.this, sessionId, event));
        }

        @Override // android.service.appprediction.IPredictionService
        public void notifyLaunchLocationShown(AppPredictionSessionId sessionId, String launchLocation, ParceledListSlice targetIds) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda6
                @Override // com.android.internal.util.function.QuadConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((AppPredictionService) obj).onLaunchLocationShown((AppPredictionSessionId) obj2, (String) obj3, (List) obj4);
                }
            }, AppPredictionService.this, sessionId, launchLocation, targetIds.getList()));
        }

        @Override // android.service.appprediction.IPredictionService
        public void sortAppTargets(AppPredictionSessionId sessionId, ParceledListSlice targets, IPredictionCallback callback) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new QuintConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.function.QuintConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((AppPredictionService) obj).onSortAppTargets((AppPredictionSessionId) obj2, (List) obj3, (CancellationSignal) obj4, (AppPredictionService.CallbackWrapper) obj5);
                }
            }, AppPredictionService.this, sessionId, targets.getList(), null, new CallbackWrapper(callback, null)));
        }

        @Override // android.service.appprediction.IPredictionService
        public void registerPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda7
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AppPredictionService) obj).doRegisterPredictionUpdates((AppPredictionSessionId) obj2, (IPredictionCallback) obj3);
                }
            }, AppPredictionService.this, sessionId, callback));
        }

        @Override // android.service.appprediction.IPredictionService
        public void unregisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AppPredictionService) obj).doUnregisterPredictionUpdates((AppPredictionSessionId) obj2, (IPredictionCallback) obj3);
                }
            }, AppPredictionService.this, sessionId, callback));
        }

        @Override // android.service.appprediction.IPredictionService
        public void requestPredictionUpdate(AppPredictionSessionId sessionId) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda3
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((AppPredictionService) obj).doRequestPredictionUpdate((AppPredictionSessionId) obj2);
                }
            }, AppPredictionService.this, sessionId));
        }

        @Override // android.service.appprediction.IPredictionService
        public void onDestroyPredictionSession(AppPredictionSessionId sessionId) {
            AppPredictionService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.appprediction.AppPredictionService$1$$ExternalSyntheticLambda4
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((AppPredictionService) obj).doDestroyPredictionSession((AppPredictionSessionId) obj2);
                }
            }, AppPredictionService.this, sessionId));
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
            return this.mInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.appprediction.AppPredictionService: " + intent);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doCreatePredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId) {
        this.mSessionCallbacks.put(sessionId, new ArrayList<>());
        onCreatePredictionSession(context, sessionId);
    }

    public void onCreatePredictionSession(AppPredictionContext context, AppPredictionSessionId sessionId) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRegisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
        final ArrayList<CallbackWrapper> callbacks = this.mSessionCallbacks.get(sessionId);
        if (callbacks == null) {
            Slog.m96e(TAG, "Failed to register for updates for unknown session: " + sessionId);
            return;
        }
        CallbackWrapper wrapper = findCallbackWrapper(callbacks, callback);
        if (wrapper == null) {
            callbacks.add(new CallbackWrapper(callback, new Consumer() { // from class: android.service.appprediction.AppPredictionService$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppPredictionService.this.lambda$doRegisterPredictionUpdates$1(callbacks, (AppPredictionService.CallbackWrapper) obj);
                }
            }));
            if (callbacks.size() == 1) {
                onStartPredictionUpdates();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$doRegisterPredictionUpdates$1(final ArrayList callbacks, final CallbackWrapper callbackWrapper) {
        this.mHandler.post(new Runnable() { // from class: android.service.appprediction.AppPredictionService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppPredictionService.this.lambda$doRegisterPredictionUpdates$0(callbacks, callbackWrapper);
            }
        });
    }

    public void onStartPredictionUpdates() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doUnregisterPredictionUpdates(AppPredictionSessionId sessionId, IPredictionCallback callback) {
        ArrayList<CallbackWrapper> callbacks = this.mSessionCallbacks.get(sessionId);
        if (callbacks == null) {
            Slog.m96e(TAG, "Failed to unregister for updates for unknown session: " + sessionId);
            return;
        }
        CallbackWrapper wrapper = findCallbackWrapper(callbacks, callback);
        lambda$doRegisterPredictionUpdates$0(callbacks, wrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: removeCallbackWrapper */
    public void lambda$doRegisterPredictionUpdates$0(ArrayList<CallbackWrapper> callbacks, CallbackWrapper wrapper) {
        if (callbacks == null || wrapper == null) {
            return;
        }
        callbacks.remove(wrapper);
        wrapper.destroy();
        if (callbacks.isEmpty()) {
            onStopPredictionUpdates();
        }
    }

    public void onStopPredictionUpdates() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRequestPredictionUpdate(AppPredictionSessionId sessionId) {
        ArrayList<CallbackWrapper> callbacks = this.mSessionCallbacks.get(sessionId);
        if (callbacks != null && !callbacks.isEmpty()) {
            onRequestPredictionUpdate(sessionId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doDestroyPredictionSession(AppPredictionSessionId sessionId) {
        ArrayList<CallbackWrapper> callbacks = this.mSessionCallbacks.remove(sessionId);
        if (callbacks != null) {
            callbacks.forEach(new Consumer() { // from class: android.service.appprediction.AppPredictionService$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionService.CallbackWrapper) obj).destroy();
                }
            });
        }
        onDestroyPredictionSession(sessionId);
    }

    public void onDestroyPredictionSession(AppPredictionSessionId sessionId) {
    }

    public final void updatePredictions(AppPredictionSessionId sessionId, List<AppTarget> targets) {
        List<CallbackWrapper> callbacks = this.mSessionCallbacks.get(sessionId);
        if (callbacks != null) {
            for (CallbackWrapper callback : callbacks) {
                callback.accept(targets);
            }
        }
    }

    private CallbackWrapper findCallbackWrapper(ArrayList<CallbackWrapper> callbacks, IPredictionCallback callback) {
        for (int i = callbacks.size() - 1; i >= 0; i--) {
            if (callbacks.get(i).isCallback(callback)) {
                return callbacks.get(i);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class CallbackWrapper implements Consumer<List<AppTarget>>, IBinder.DeathRecipient {
        private IPredictionCallback mCallback;
        private final Consumer<CallbackWrapper> mOnBinderDied;

        CallbackWrapper(IPredictionCallback callback, Consumer<CallbackWrapper> onBinderDied) {
            this.mCallback = callback;
            this.mOnBinderDied = onBinderDied;
            if (onBinderDied != null) {
                try {
                    callback.asBinder().linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Slog.m96e(AppPredictionService.TAG, "Failed to link to death: " + e);
                }
            }
        }

        public boolean isCallback(IPredictionCallback callback) {
            IPredictionCallback iPredictionCallback = this.mCallback;
            if (iPredictionCallback == null) {
                Slog.m96e(AppPredictionService.TAG, "Callback is null, likely the binder has died.");
                return false;
            }
            return iPredictionCallback.asBinder().equals(callback.asBinder());
        }

        public void destroy() {
            IPredictionCallback iPredictionCallback = this.mCallback;
            if (iPredictionCallback != null && this.mOnBinderDied != null) {
                iPredictionCallback.asBinder().unlinkToDeath(this, 0);
            }
        }

        @Override // java.util.function.Consumer
        public void accept(List<AppTarget> ts) {
            try {
                IPredictionCallback iPredictionCallback = this.mCallback;
                if (iPredictionCallback != null) {
                    iPredictionCallback.onResult(new ParceledListSlice(ts));
                }
            } catch (RemoteException e) {
                Slog.m96e(AppPredictionService.TAG, "Error sending result:" + e);
            }
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            destroy();
            this.mCallback = null;
            Consumer<CallbackWrapper> consumer = this.mOnBinderDied;
            if (consumer != null) {
                consumer.accept(this);
            }
        }
    }
}
