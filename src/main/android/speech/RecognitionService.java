package android.speech;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Service;
import android.content.AttributionSource;
import android.content.Context;
import android.content.ContextParams;
import android.content.Intent;
import android.content.PermissionChecker;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.speech.IRecognitionService;
import android.util.Log;
import android.util.Pair;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public abstract class RecognitionService extends Service {
    private static final boolean DBG = false;
    private static final int DEFAULT_MAX_CONCURRENT_SESSIONS_COUNT = 1;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_CHECK_RECOGNITION_SUPPORT = 5;
    private static final int MSG_CLEAR_MODEL_DOWNLOAD_LISTENER = 8;
    private static final int MSG_RESET = 4;
    private static final int MSG_SET_MODEL_DOWNLOAD_LISTENER = 7;
    private static final int MSG_START_LISTENING = 1;
    private static final int MSG_STOP_LISTENING = 2;
    private static final int MSG_TRIGGER_MODEL_DOWNLOAD = 6;
    public static final String SERVICE_INTERFACE = "android.speech.RecognitionService";
    public static final String SERVICE_META_DATA = "android.speech";
    private static final String TAG = "RecognitionService";
    private final Map<IBinder, SessionState> mSessions = new HashMap();
    private final RecognitionServiceBinder mBinder = new RecognitionServiceBinder(this);
    private final Handler mHandler = new Handler() { // from class: android.speech.RecognitionService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    StartListeningArgs args = (StartListeningArgs) msg.obj;
                    RecognitionService.this.dispatchStartListening(args.mIntent, args.mListener, args.mAttributionSource);
                    return;
                case 2:
                    RecognitionService.this.dispatchStopListening((IRecognitionListener) msg.obj);
                    return;
                case 3:
                    RecognitionService.this.dispatchCancel((IRecognitionListener) msg.obj);
                    return;
                case 4:
                    RecognitionService.this.dispatchClearCallback((IRecognitionListener) msg.obj);
                    return;
                case 5:
                    CheckRecognitionSupportArgs checkArgs = (CheckRecognitionSupportArgs) msg.obj;
                    RecognitionService.this.dispatchCheckRecognitionSupport(checkArgs.mIntent, checkArgs.callback, checkArgs.mAttributionSource);
                    return;
                case 6:
                    Pair<Intent, AttributionSource> params = (Pair) msg.obj;
                    RecognitionService.this.dispatchTriggerModelDownload((Intent) params.first, (AttributionSource) params.second);
                    return;
                case 7:
                    ModelDownloadListenerArgs dListenerArgs = (ModelDownloadListenerArgs) msg.obj;
                    RecognitionService.this.dispatchSetModelDownloadListener(dListenerArgs.mIntent, dListenerArgs.mListener, dListenerArgs.mAttributionSource);
                    return;
                case 8:
                    Pair<Intent, AttributionSource> clearDlPair = (Pair) msg.obj;
                    RecognitionService.this.dispatchClearModelDownloadListener((Intent) clearDlPair.first, (AttributionSource) clearDlPair.second);
                    return;
                default:
                    return;
            }
        }
    };

    protected abstract void onCancel(Callback callback);

    protected abstract void onStartListening(Intent intent, Callback callback);

    protected abstract void onStopListening(Callback callback);

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:16:0x003c A[Catch: RemoteException -> 0x0086, TryCatch #0 {RemoteException -> 0x0086, blocks: (B:4:0x0011, B:6:0x001d, B:8:0x0028, B:10:0x0030, B:16:0x003c, B:18:0x0057, B:20:0x005d, B:22:0x0064, B:23:0x0076, B:25:0x007c), top: B:30:0x000f }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0064 A[Catch: RemoteException -> 0x0086, TryCatch #0 {RemoteException -> 0x0086, blocks: (B:4:0x0011, B:6:0x001d, B:8:0x0028, B:10:0x0030, B:16:0x003c, B:18:0x0057, B:20:0x005d, B:22:0x0064, B:23:0x0076, B:25:0x007c), top: B:30:0x000f }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dispatchStartListening(Intent intent, IRecognitionListener listener, AttributionSource attributionSource) {
        boolean preflightPermissionCheckPassed;
        Callback currentCallback = null;
        SessionState sessionState = this.mSessions.get(listener.asBinder());
        try {
            if (sessionState == null) {
                if (this.mSessions.size() >= getMaxConcurrentSessionsCount()) {
                    listener.onError(8);
                    Log.m108i(TAG, "#startListening received when the service's capacity is full - ignoring this call.");
                    return;
                }
                if (!intent.hasExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE) && !checkPermissionForPreflightNotHardDenied(attributionSource)) {
                    preflightPermissionCheckPassed = false;
                    if (preflightPermissionCheckPassed) {
                        currentCallback = new Callback(listener, attributionSource);
                        sessionState = new SessionState(currentCallback);
                        this.mSessions.put(listener.asBinder(), sessionState);
                        onStartListening(intent, currentCallback);
                    }
                    if (preflightPermissionCheckPassed || !checkPermissionAndStartDataDelivery(sessionState)) {
                        listener.onError(9);
                        if (preflightPermissionCheckPassed) {
                            onCancel(currentCallback);
                            this.mSessions.remove(listener.asBinder());
                            finishDataDelivery(sessionState);
                            sessionState.reset();
                        }
                        Log.m108i(TAG, "#startListening received from a caller without permission android.permission.RECORD_AUDIO.");
                    }
                    return;
                }
                preflightPermissionCheckPassed = true;
                if (preflightPermissionCheckPassed) {
                }
                if (preflightPermissionCheckPassed) {
                }
                listener.onError(9);
                if (preflightPermissionCheckPassed) {
                }
                Log.m108i(TAG, "#startListening received from a caller without permission android.permission.RECORD_AUDIO.");
                return;
            }
            listener.onError(5);
            Log.m108i(TAG, "#startListening received for a listener which is already in session - ignoring this call.");
        } catch (RemoteException e) {
            Log.m112d(TAG, "#onError call from #startListening failed.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchStopListening(IRecognitionListener listener) {
        SessionState sessionState = this.mSessions.get(listener.asBinder());
        if (sessionState == null) {
            try {
                listener.onError(5);
            } catch (RemoteException e) {
                Log.m112d(TAG, "#onError call from #stopListening failed.");
            }
            Log.m104w(TAG, "#stopListening received for a listener which has not started a session - ignoring this call.");
            return;
        }
        onStopListening(sessionState.mCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchCancel(IRecognitionListener listener) {
        SessionState sessionState = this.mSessions.get(listener.asBinder());
        if (sessionState == null) {
            Log.m104w(TAG, "#cancel received for a listener which has not started a session - ignoring this call.");
            return;
        }
        onCancel(sessionState.mCallback);
        dispatchClearCallback(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchClearCallback(IRecognitionListener listener) {
        SessionState sessionState = this.mSessions.remove(listener.asBinder());
        if (sessionState != null) {
            finishDataDelivery(sessionState);
            sessionState.reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchCheckRecognitionSupport(Intent intent, IRecognitionSupportCallback callback, AttributionSource attributionSource) {
        onCheckRecognitionSupport(intent, attributionSource, new SupportCallback(callback));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchTriggerModelDownload(Intent intent, AttributionSource attributionSource) {
        onTriggerModelDownload(intent, attributionSource);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchSetModelDownloadListener(Intent intent, final IModelDownloadListener listener, AttributionSource attributionSource) {
        setModelDownloadListener(intent, attributionSource, new ModelDownloadListener() { // from class: android.speech.RecognitionService.2
            @Override // android.speech.ModelDownloadListener
            public void onProgress(int completedPercent) {
                try {
                    listener.onProgress(completedPercent);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }

            @Override // android.speech.ModelDownloadListener
            public void onSuccess() {
                try {
                    listener.onSuccess();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }

            @Override // android.speech.ModelDownloadListener
            public void onScheduled() {
                try {
                    listener.onScheduled();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }

            @Override // android.speech.ModelDownloadListener
            public void onError(int error) {
                try {
                    listener.onError(error);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchClearModelDownloadListener(Intent intent, AttributionSource attributionSource) {
        clearModelDownloadListener(intent, attributionSource);
    }

    /* loaded from: classes3.dex */
    private static class StartListeningArgs {
        public final AttributionSource mAttributionSource;
        public final Intent mIntent;
        public final IRecognitionListener mListener;

        public StartListeningArgs(Intent intent, IRecognitionListener listener, AttributionSource attributionSource) {
            this.mIntent = intent;
            this.mListener = listener;
            this.mAttributionSource = attributionSource;
        }
    }

    /* loaded from: classes3.dex */
    private static class CheckRecognitionSupportArgs {
        public final IRecognitionSupportCallback callback;
        public final AttributionSource mAttributionSource;
        public final Intent mIntent;

        private CheckRecognitionSupportArgs(Intent intent, IRecognitionSupportCallback callback, AttributionSource attributionSource) {
            this.mIntent = intent;
            this.callback = callback;
            this.mAttributionSource = attributionSource;
        }
    }

    /* loaded from: classes3.dex */
    private static class ModelDownloadListenerArgs {
        final AttributionSource mAttributionSource;
        final Intent mIntent;
        final IModelDownloadListener mListener;

        private ModelDownloadListenerArgs(Intent intent, IModelDownloadListener listener, AttributionSource attributionSource) {
            this.mIntent = intent;
            this.mListener = listener;
            this.mAttributionSource = attributionSource;
        }
    }

    public void onCheckRecognitionSupport(Intent recognizerIntent, SupportCallback supportCallback) {
        supportCallback.onError(14);
    }

    public void onCheckRecognitionSupport(Intent recognizerIntent, AttributionSource attributionSource, SupportCallback supportCallback) {
        onCheckRecognitionSupport(recognizerIntent, supportCallback);
    }

    public void onTriggerModelDownload(Intent recognizerIntent) {
    }

    public void onTriggerModelDownload(Intent recognizerIntent, AttributionSource attributionSource) {
        onTriggerModelDownload(recognizerIntent);
    }

    public void setModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource, ModelDownloadListener modelDownloadListener) {
        modelDownloadListener.onError(15);
    }

    public void clearModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource) {
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Context createContext(ContextParams contextParams) {
        if (contextParams.getNextAttributionSource() != null) {
            if (this.mHandler.getLooper().equals(Looper.myLooper())) {
                handleAttributionContextCreation(contextParams.getNextAttributionSource());
            } else {
                this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.speech.RecognitionService$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        RecognitionService.this.handleAttributionContextCreation((AttributionSource) obj);
                    }
                }, contextParams.getNextAttributionSource()));
            }
        }
        return super.createContext(contextParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAttributionContextCreation(AttributionSource attributionSource) {
        for (SessionState sessionState : this.mSessions.values()) {
            Callback currentCallback = sessionState.mCallback;
            if (currentCallback != null && currentCallback.mCallingAttributionSource.equals(attributionSource)) {
                currentCallback.mAttributionContextCreated = true;
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onDestroy() {
        for (SessionState sessionState : this.mSessions.values()) {
            finishDataDelivery(sessionState);
            sessionState.reset();
        }
        this.mSessions.clear();
        this.mBinder.clearReference();
        super.onDestroy();
    }

    public int getMaxConcurrentSessionsCount() {
        return 1;
    }

    /* loaded from: classes3.dex */
    public class Callback {
        private Context mAttributionContext;
        private boolean mAttributionContextCreated;
        private final AttributionSource mCallingAttributionSource;
        private final IRecognitionListener mListener;

        private Callback(IRecognitionListener listener, AttributionSource attributionSource) {
            this.mListener = listener;
            this.mCallingAttributionSource = attributionSource;
        }

        public void beginningOfSpeech() throws RemoteException {
            this.mListener.onBeginningOfSpeech();
        }

        public void bufferReceived(byte[] buffer) throws RemoteException {
            this.mListener.onBufferReceived(buffer);
        }

        public void endOfSpeech() throws RemoteException {
            this.mListener.onEndOfSpeech();
        }

        public void error(int error) throws RemoteException {
            Message.obtain(RecognitionService.this.mHandler, 4, this.mListener).sendToTarget();
            this.mListener.onError(error);
        }

        public void partialResults(Bundle partialResults) throws RemoteException {
            this.mListener.onPartialResults(partialResults);
        }

        public void readyForSpeech(Bundle params) throws RemoteException {
            this.mListener.onReadyForSpeech(params);
        }

        public void results(Bundle results) throws RemoteException {
            Message.obtain(RecognitionService.this.mHandler, 4, this.mListener).sendToTarget();
            this.mListener.onResults(results);
        }

        public void rmsChanged(float rmsdB) throws RemoteException {
            this.mListener.onRmsChanged(rmsdB);
        }

        public void segmentResults(Bundle results) throws RemoteException {
            this.mListener.onSegmentResults(results);
        }

        public void endOfSegmentedSession() throws RemoteException {
            Message.obtain(RecognitionService.this.mHandler, 4, this.mListener).sendToTarget();
            this.mListener.onEndOfSegmentedSession();
        }

        public void languageDetection(Bundle results) {
            try {
                this.mListener.onLanguageDetection(results);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public int getCallingUid() {
            return this.mCallingAttributionSource.getUid();
        }

        public AttributionSource getCallingAttributionSource() {
            return this.mCallingAttributionSource;
        }

        Context getAttributionContextForCaller() {
            if (this.mAttributionContext == null) {
                this.mAttributionContext = RecognitionService.this.createContext(new ContextParams.Builder().setNextAttributionSource(this.mCallingAttributionSource).build());
            }
            return this.mAttributionContext;
        }
    }

    /* loaded from: classes3.dex */
    public static class SupportCallback {
        private final IRecognitionSupportCallback mCallback;

        private SupportCallback(IRecognitionSupportCallback callback) {
            this.mCallback = callback;
        }

        public void onSupportResult(RecognitionSupport recognitionSupport) {
            try {
                this.mCallback.onSupportResult(recognitionSupport);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void onError(int errorCode) {
            try {
                this.mCallback.onError(errorCode);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* loaded from: classes3.dex */
    private static final class RecognitionServiceBinder extends IRecognitionService.Stub {
        private final WeakReference<RecognitionService> mServiceRef;

        public RecognitionServiceBinder(RecognitionService service) {
            this.mServiceRef = new WeakReference<>(service);
        }

        @Override // android.speech.IRecognitionService
        public void startListening(Intent recognizerIntent, IRecognitionListener listener, AttributionSource attributionSource) {
            Objects.requireNonNull(attributionSource);
            attributionSource.enforceCallingUid();
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 1, new StartListeningArgs(recognizerIntent, listener, attributionSource)));
            }
        }

        @Override // android.speech.IRecognitionService
        public void stopListening(IRecognitionListener listener) {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 2, listener));
            }
        }

        @Override // android.speech.IRecognitionService
        public void cancel(IRecognitionListener listener, boolean isShutdown) {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 3, listener));
            }
        }

        @Override // android.speech.IRecognitionService
        public void checkRecognitionSupport(Intent recognizerIntent, AttributionSource attributionSource, IRecognitionSupportCallback callback) {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 5, new CheckRecognitionSupportArgs(recognizerIntent, callback, attributionSource)));
            }
        }

        @Override // android.speech.IRecognitionService
        public void triggerModelDownload(Intent recognizerIntent, AttributionSource attributionSource) {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 6, Pair.create(recognizerIntent, attributionSource)));
            }
        }

        @Override // android.speech.IRecognitionService
        public void setModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource, IModelDownloadListener listener) throws RemoteException {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 7, new ModelDownloadListenerArgs(recognizerIntent, listener, attributionSource)));
            }
        }

        @Override // android.speech.IRecognitionService
        public void clearModelDownloadListener(Intent recognizerIntent, AttributionSource attributionSource) throws RemoteException {
            RecognitionService service = this.mServiceRef.get();
            if (service != null) {
                service.mHandler.sendMessage(Message.obtain(service.mHandler, 8, Pair.create(recognizerIntent, attributionSource)));
            }
        }

        public void clearReference() {
            this.mServiceRef.clear();
        }
    }

    private boolean checkPermissionAndStartDataDelivery(SessionState sessionState) {
        if (sessionState.mCallback.mAttributionContextCreated) {
            return true;
        }
        if (PermissionChecker.checkPermissionAndStartDataDelivery(this, Manifest.C0000permission.RECORD_AUDIO, sessionState.mCallback.getAttributionContextForCaller().getAttributionSource(), null) == 0) {
            sessionState.mStartedDataDelivery = true;
        }
        return sessionState.mStartedDataDelivery;
    }

    private boolean checkPermissionForPreflightNotHardDenied(AttributionSource attributionSource) {
        int result = PermissionChecker.checkPermissionForPreflight(this, Manifest.C0000permission.RECORD_AUDIO, attributionSource);
        return result == 0 || result == 1;
    }

    void finishDataDelivery(SessionState sessionState) {
        if (sessionState.mStartedDataDelivery) {
            sessionState.mStartedDataDelivery = false;
            String op = AppOpsManager.permissionToOp(Manifest.C0000permission.RECORD_AUDIO);
            PermissionChecker.finishDataDelivery(this, op, sessionState.mCallback.getAttributionContextForCaller().getAttributionSource());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SessionState {
        private Callback mCallback;
        private boolean mStartedDataDelivery;

        SessionState(Callback callback, boolean startedDataDelivery) {
            this.mCallback = callback;
            this.mStartedDataDelivery = startedDataDelivery;
        }

        SessionState(Callback currentCallback) {
            this(currentCallback, false);
        }

        void reset() {
            this.mCallback = null;
            this.mStartedDataDelivery = false;
        }
    }
}
