package android.view.translation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IRemoteCallback;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.SynchronousResultReceiver;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.view.translation.TranslationManager;
import android.view.translation.Translator;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.SyncResultReceiver;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes4.dex */
public final class TranslationManager {
    public static final String EXTRA_CAPABILITIES = "translation_capabilities";
    public static final int STATUS_SYNC_CALL_FAIL = 2;
    public static final int STATUS_SYNC_CALL_SUCCESS = 1;
    static final int SYNC_CALLS_TIMEOUT_MS = 60000;
    private static final String TAG = "TranslationManager";
    private final Context mContext;
    private final ITranslationManager mService;
    private static final SecureRandom ID_GENERATOR = new SecureRandom();
    private static final AtomicInteger sAvailableRequestId = new AtomicInteger(1);
    private final ArrayMap<Pair<Integer, Integer>, ArrayList<PendingIntent>> mTranslationCapabilityUpdateListeners = new ArrayMap<>();
    private final Map<Consumer<TranslationCapability>, IRemoteCallback> mCapabilityCallbacks = new ArrayMap();
    private final Object mLock = new Object();
    private final IntArray mTranslatorIds = new IntArray();
    private final Handler mHandler = Handler.createAsync(Looper.getMainLooper());

    public TranslationManager(Context context, ITranslationManager service) {
        this.mContext = (Context) Objects.requireNonNull(context, "context cannot be null");
        this.mService = service;
    }

    public void createOnDeviceTranslator(TranslationContext translationContext, final Executor executor, final Consumer<Translator> callback) {
        Objects.requireNonNull(translationContext, "translationContext cannot be null");
        Objects.requireNonNull(executor, "executor cannot be null");
        Objects.requireNonNull(callback, "callback cannot be null");
        synchronized (this.mLock) {
            while (true) {
                final int translatorId = Math.abs(ID_GENERATOR.nextInt());
                if (translatorId != 0 && this.mTranslatorIds.indexOf(translatorId) < 0) {
                    new Translator(this.mContext, translationContext, translatorId, this, this.mHandler, this.mService, new Consumer() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda3
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            TranslationManager.this.lambda$createOnDeviceTranslator$4(executor, callback, translatorId, (Translator) obj);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOnDeviceTranslator$4(final Executor executor, final Consumer callback, int tId, final Translator translator) {
        if (translator == null) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    executor.execute(new Runnable() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            r1.accept(null);
                        }
                    });
                }
            });
            return;
        }
        synchronized (this.mLock) {
            this.mTranslatorIds.add(tId);
        }
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda1
            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
            public final void runOrThrow() {
                executor.execute(new Runnable() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        r1.accept(r2);
                    }
                });
            }
        });
    }

    @Deprecated
    public Translator createOnDeviceTranslator(TranslationContext translationContext) {
        int translatorId;
        Objects.requireNonNull(translationContext, "translationContext cannot be null");
        synchronized (this.mLock) {
            while (true) {
                translatorId = Math.abs(ID_GENERATOR.nextInt());
                if (translatorId != 0 && this.mTranslatorIds.indexOf(translatorId) < 0) {
                    break;
                }
            }
            Translator newTranslator = new Translator(this.mContext, translationContext, translatorId, this, this.mHandler, this.mService);
            newTranslator.start();
            try {
                if (!newTranslator.isSessionCreated()) {
                    return null;
                }
                this.mTranslatorIds.add(translatorId);
                return newTranslator;
            } catch (Translator.ServiceBinderReceiver.TimeoutException e) {
                Log.m110e(TAG, "Timed out getting create session: " + e);
                return null;
            }
        }
    }

    @Deprecated
    public Translator createTranslator(TranslationContext translationContext) {
        return createOnDeviceTranslator(translationContext);
    }

    public Set<TranslationCapability> getOnDeviceTranslationCapabilities(int sourceFormat, int targetFormat) {
        try {
            SynchronousResultReceiver receiver = new SynchronousResultReceiver();
            this.mService.onTranslationCapabilitiesRequest(sourceFormat, targetFormat, receiver, this.mContext.getUserId());
            SynchronousResultReceiver.Result result = receiver.awaitResult(60000L);
            if (result.resultCode != 1) {
                return Collections.emptySet();
            }
            ParceledListSlice<TranslationCapability> listSlice = (ParceledListSlice) result.bundle.getParcelable(EXTRA_CAPABILITIES, ParceledListSlice.class);
            ArraySet<TranslationCapability> capabilities = new ArraySet<>(listSlice == null ? null : listSlice.getList());
            return capabilities;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        } catch (TimeoutException e2) {
            Log.m110e(TAG, "Timed out getting supported translation capabilities: " + e2);
            return Collections.emptySet();
        }
    }

    @Deprecated
    public Set<TranslationCapability> getTranslationCapabilities(int sourceFormat, int targetFormat) {
        return getOnDeviceTranslationCapabilities(sourceFormat, targetFormat);
    }

    public void addOnDeviceTranslationCapabilityUpdateListener(Executor executor, Consumer<TranslationCapability> capabilityListener) {
        Objects.requireNonNull(executor, "executor should not be null");
        Objects.requireNonNull(capabilityListener, "capability listener should not be null");
        synchronized (this.mLock) {
            if (this.mCapabilityCallbacks.containsKey(capabilityListener)) {
                Log.m104w(TAG, "addOnDeviceTranslationCapabilityUpdateListener: the listener for " + capabilityListener + " already registered; ignoring.");
                return;
            }
            IRemoteCallback remoteCallback = new TranslationCapabilityRemoteCallback(executor, capabilityListener);
            try {
                this.mService.registerTranslationCapabilityCallback(remoteCallback, this.mContext.getUserId());
                this.mCapabilityCallbacks.put(capabilityListener, remoteCallback);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public void addOnDeviceTranslationCapabilityUpdateListener(int sourceFormat, int targetFormat, PendingIntent pendingIntent) {
        Objects.requireNonNull(pendingIntent, "pending intent should not be null");
        synchronized (this.mLock) {
            Pair<Integer, Integer> formatPair = new Pair<>(Integer.valueOf(sourceFormat), Integer.valueOf(targetFormat));
            this.mTranslationCapabilityUpdateListeners.computeIfAbsent(formatPair, new Function() { // from class: android.view.translation.TranslationManager$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return TranslationManager.lambda$addOnDeviceTranslationCapabilityUpdateListener$5((Pair) obj);
                }
            }).add(pendingIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ArrayList lambda$addOnDeviceTranslationCapabilityUpdateListener$5(Pair formats) {
        return new ArrayList();
    }

    @Deprecated
    public void addTranslationCapabilityUpdateListener(int sourceFormat, int targetFormat, PendingIntent pendingIntent) {
        addOnDeviceTranslationCapabilityUpdateListener(sourceFormat, targetFormat, pendingIntent);
    }

    public void removeOnDeviceTranslationCapabilityUpdateListener(Consumer<TranslationCapability> capabilityListener) {
        Objects.requireNonNull(capabilityListener, "capability callback should not be null");
        synchronized (this.mLock) {
            IRemoteCallback remoteCallback = this.mCapabilityCallbacks.get(capabilityListener);
            if (remoteCallback == null) {
                Log.m104w(TAG, "removeOnDeviceTranslationCapabilityUpdateListener: the capability listener not found; ignoring.");
                return;
            }
            try {
                this.mService.unregisterTranslationCapabilityCallback(remoteCallback, this.mContext.getUserId());
                this.mCapabilityCallbacks.remove(capabilityListener);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    @Deprecated
    public void removeOnDeviceTranslationCapabilityUpdateListener(int sourceFormat, int targetFormat, PendingIntent pendingIntent) {
        Objects.requireNonNull(pendingIntent, "pending intent should not be null");
        synchronized (this.mLock) {
            Pair<Integer, Integer> formatPair = new Pair<>(Integer.valueOf(sourceFormat), Integer.valueOf(targetFormat));
            if (this.mTranslationCapabilityUpdateListeners.containsKey(formatPair)) {
                ArrayList<PendingIntent> intents = this.mTranslationCapabilityUpdateListeners.get(formatPair);
                if (intents.contains(pendingIntent)) {
                    intents.remove(pendingIntent);
                } else {
                    Log.m104w(TAG, "pending intent=" + pendingIntent + " does not exist in mTranslationCapabilityUpdateListeners");
                }
            } else {
                Log.m104w(TAG, "format pair=" + formatPair + " does not exist in mTranslationCapabilityUpdateListeners");
            }
        }
    }

    @Deprecated
    public void removeTranslationCapabilityUpdateListener(int sourceFormat, int targetFormat, PendingIntent pendingIntent) {
        removeOnDeviceTranslationCapabilityUpdateListener(sourceFormat, targetFormat, pendingIntent);
    }

    public PendingIntent getOnDeviceTranslationSettingsActivityIntent() {
        SyncResultReceiver resultReceiver = new SyncResultReceiver(60000);
        try {
            this.mService.getServiceSettingsActivity(resultReceiver, this.mContext.getUserId());
            try {
                return (PendingIntent) resultReceiver.getParcelableResult();
            } catch (SyncResultReceiver.TimeoutException e) {
                Log.m110e(TAG, "Fail to get translation service settings activity.");
                return null;
            }
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public PendingIntent getTranslationSettingsActivityIntent() {
        return getOnDeviceTranslationSettingsActivityIntent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeTranslator(int id) {
        synchronized (this.mLock) {
            int index = this.mTranslatorIds.indexOf(id);
            if (index >= 0) {
                this.mTranslatorIds.remove(index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AtomicInteger getAvailableRequestId() {
        AtomicInteger atomicInteger;
        synchronized (this.mLock) {
            atomicInteger = sAvailableRequestId;
        }
        return atomicInteger;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TranslationCapabilityRemoteCallback extends IRemoteCallback.Stub {
        private final Executor mExecutor;
        private final Consumer<TranslationCapability> mListener;

        TranslationCapabilityRemoteCallback(Executor executor, Consumer<TranslationCapability> listener) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        @Override // android.p008os.IRemoteCallback
        public void sendResult(final Bundle bundle) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.view.translation.TranslationManager$TranslationCapabilityRemoteCallback$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    TranslationManager.TranslationCapabilityRemoteCallback.this.lambda$sendResult$1(bundle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendResult$1(final Bundle bundle) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.view.translation.TranslationManager$TranslationCapabilityRemoteCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TranslationManager.TranslationCapabilityRemoteCallback.this.lambda$sendResult$0(bundle);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: onTranslationCapabilityUpdate */
        public void lambda$sendResult$0(Bundle bundle) {
            TranslationCapability capability = (TranslationCapability) bundle.getParcelable(TranslationManager.EXTRA_CAPABILITIES, TranslationCapability.class);
            this.mListener.accept(capability);
        }
    }
}
