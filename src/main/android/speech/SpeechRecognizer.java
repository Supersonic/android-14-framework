package android.speech;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.provider.Settings;
import android.speech.IModelDownloadListener;
import android.speech.IRecognitionListener;
import android.speech.IRecognitionServiceManager;
import android.speech.IRecognitionServiceManagerCallback;
import android.speech.IRecognitionSupportCallback;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.C4057R;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
/* loaded from: classes3.dex */
public class SpeechRecognizer {
    public static final String CONFIDENCE_SCORES = "confidence_scores";
    private static final boolean DBG = false;
    public static final String DETECTED_LANGUAGE = "detected_language";
    public static final int ERROR_AUDIO = 3;
    public static final int ERROR_CANNOT_CHECK_SUPPORT = 14;
    public static final int ERROR_CANNOT_LISTEN_TO_DOWNLOAD_EVENTS = 15;
    public static final int ERROR_CLIENT = 5;
    public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
    public static final int ERROR_LANGUAGE_NOT_SUPPORTED = 12;
    public static final int ERROR_LANGUAGE_UNAVAILABLE = 13;
    public static final int ERROR_NETWORK = 2;
    public static final int ERROR_NETWORK_TIMEOUT = 1;
    public static final int ERROR_NO_MATCH = 7;
    public static final int ERROR_RECOGNIZER_BUSY = 8;
    public static final int ERROR_SERVER = 4;
    public static final int ERROR_SERVER_DISCONNECTED = 11;
    public static final int ERROR_SPEECH_TIMEOUT = 6;
    public static final int ERROR_TOO_MANY_REQUESTS = 10;
    public static final String LANGUAGE_DETECTION_CONFIDENCE_LEVEL = "language_detection_confidence_level";
    public static final int LANGUAGE_DETECTION_CONFIDENCE_LEVEL_CONFIDENT = 2;
    public static final int LANGUAGE_DETECTION_CONFIDENCE_LEVEL_HIGHLY_CONFIDENT = 3;
    public static final int LANGUAGE_DETECTION_CONFIDENCE_LEVEL_NOT_CONFIDENT = 1;
    public static final int LANGUAGE_DETECTION_CONFIDENCE_LEVEL_UNKNOWN = 0;
    public static final String LANGUAGE_SWITCH_RESULT = "language_switch_result";
    public static final int LANGUAGE_SWITCH_RESULT_FAILED = 2;
    public static final int LANGUAGE_SWITCH_RESULT_NOT_ATTEMPTED = 0;
    public static final int LANGUAGE_SWITCH_RESULT_SKIPPED_NO_MODEL = 3;
    public static final int LANGUAGE_SWITCH_RESULT_SUCCEEDED = 1;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_CHANGE_LISTENER = 4;
    private static final int MSG_CHECK_RECOGNITION_SUPPORT = 6;
    private static final int MSG_CLEAR_MODEL_DOWNLOAD_LISTENER = 9;
    private static final int MSG_SET_MODEL_DOWNLOAD_LISTENER = 8;
    private static final int MSG_SET_TEMPORARY_ON_DEVICE_COMPONENT = 5;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_TRIGGER_MODEL_DOWNLOAD = 7;
    public static final String RECOGNITION_PARTS = "recognition_parts";
    public static final String RESULTS_ALTERNATIVES = "results_alternatives";
    public static final String RESULTS_RECOGNITION = "results_recognition";
    private static final String TAG = "SpeechRecognizer";
    public static final String TOP_LOCALE_ALTERNATIVES = "top_locale_alternatives";
    private final IBinder mClientToken;
    private final Context mContext;
    private Handler mHandler;
    private final InternalRecognitionListener mListener;
    private IRecognitionServiceManager mManagerService;
    private final boolean mOnDevice;
    private final Queue<Message> mPendingTasks;
    private IRecognitionService mService;
    private final ComponentName mServiceComponent;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LanguageDetectionConfidenceLevel {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LanguageSwitchResult {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RecognitionError {
    }

    private SpeechRecognizer(Context context, ComponentName serviceComponent) {
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: android.speech.SpeechRecognizer.1
            @Override // android.p008os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        SpeechRecognizer.this.handleStartListening((Intent) msg.obj);
                        return;
                    case 2:
                        SpeechRecognizer.this.handleStopMessage();
                        return;
                    case 3:
                        SpeechRecognizer.this.handleCancelMessage();
                        return;
                    case 4:
                        SpeechRecognizer.this.handleChangeListener((RecognitionListener) msg.obj);
                        return;
                    case 5:
                        SpeechRecognizer.this.handleSetTemporaryComponent((ComponentName) msg.obj);
                        return;
                    case 6:
                        CheckRecognitionSupportArgs args = (CheckRecognitionSupportArgs) msg.obj;
                        SpeechRecognizer.this.handleCheckRecognitionSupport(args.mIntent, args.mCallbackExecutor, args.mCallback);
                        return;
                    case 7:
                        SpeechRecognizer.this.handleTriggerModelDownload((Intent) msg.obj);
                        return;
                    case 8:
                        ModelDownloadListenerArgs modelDownloadListenerArgs = (ModelDownloadListenerArgs) msg.obj;
                        SpeechRecognizer.this.handleSetModelDownloadListener(modelDownloadListenerArgs.mIntent, modelDownloadListenerArgs.mExecutor, modelDownloadListenerArgs.mModelDownloadListener);
                        return;
                    case 9:
                        SpeechRecognizer.this.handleClearModelDownloadListener((Intent) msg.obj);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mPendingTasks = new LinkedBlockingQueue();
        this.mListener = new InternalRecognitionListener();
        this.mClientToken = new Binder();
        this.mContext = context;
        this.mServiceComponent = serviceComponent;
        this.mOnDevice = false;
    }

    private SpeechRecognizer(Context context, boolean onDevice) {
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: android.speech.SpeechRecognizer.1
            @Override // android.p008os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        SpeechRecognizer.this.handleStartListening((Intent) msg.obj);
                        return;
                    case 2:
                        SpeechRecognizer.this.handleStopMessage();
                        return;
                    case 3:
                        SpeechRecognizer.this.handleCancelMessage();
                        return;
                    case 4:
                        SpeechRecognizer.this.handleChangeListener((RecognitionListener) msg.obj);
                        return;
                    case 5:
                        SpeechRecognizer.this.handleSetTemporaryComponent((ComponentName) msg.obj);
                        return;
                    case 6:
                        CheckRecognitionSupportArgs args = (CheckRecognitionSupportArgs) msg.obj;
                        SpeechRecognizer.this.handleCheckRecognitionSupport(args.mIntent, args.mCallbackExecutor, args.mCallback);
                        return;
                    case 7:
                        SpeechRecognizer.this.handleTriggerModelDownload((Intent) msg.obj);
                        return;
                    case 8:
                        ModelDownloadListenerArgs modelDownloadListenerArgs = (ModelDownloadListenerArgs) msg.obj;
                        SpeechRecognizer.this.handleSetModelDownloadListener(modelDownloadListenerArgs.mIntent, modelDownloadListenerArgs.mExecutor, modelDownloadListenerArgs.mModelDownloadListener);
                        return;
                    case 9:
                        SpeechRecognizer.this.handleClearModelDownloadListener((Intent) msg.obj);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mPendingTasks = new LinkedBlockingQueue();
        this.mListener = new InternalRecognitionListener();
        this.mClientToken = new Binder();
        this.mContext = context;
        this.mServiceComponent = null;
        this.mOnDevice = onDevice;
    }

    public static boolean isRecognitionAvailable(Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentServices(new Intent(RecognitionService.SERVICE_INTERFACE), 0);
        return (list == null || list.size() == 0) ? false : true;
    }

    public static boolean isOnDeviceRecognitionAvailable(Context context) {
        ComponentName componentName = ComponentName.unflattenFromString(context.getString(C4057R.string.config_defaultOnDeviceSpeechRecognitionService));
        return componentName != null;
    }

    public static SpeechRecognizer createSpeechRecognizer(Context context) {
        return createSpeechRecognizer(context, null);
    }

    public static SpeechRecognizer createSpeechRecognizer(Context context, ComponentName serviceComponent) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        checkIsCalledFromMainThread();
        return new SpeechRecognizer(context, serviceComponent);
    }

    public static SpeechRecognizer createOnDeviceSpeechRecognizer(Context context) {
        if (!isOnDeviceRecognitionAvailable(context)) {
            throw new UnsupportedOperationException("On-device recognition is not available");
        }
        return lenientlyCreateOnDeviceSpeechRecognizer(context);
    }

    public static SpeechRecognizer createOnDeviceTestingSpeechRecognizer(Context context) {
        return lenientlyCreateOnDeviceSpeechRecognizer(context);
    }

    private static SpeechRecognizer lenientlyCreateOnDeviceSpeechRecognizer(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        checkIsCalledFromMainThread();
        return new SpeechRecognizer(context, true);
    }

    public void setRecognitionListener(RecognitionListener listener) {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 4, listener));
    }

    public void startListening(Intent recognizerIntent) {
        if (recognizerIntent == null) {
            throw new IllegalArgumentException("intent must not be null");
        }
        checkIsCalledFromMainThread();
        if (this.mService == null) {
            connectToSystemService();
        }
        putMessage(Message.obtain(this.mHandler, 1, recognizerIntent));
    }

    public void stopListening() {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 2));
    }

    public void cancel() {
        checkIsCalledFromMainThread();
        putMessage(Message.obtain(this.mHandler, 3));
    }

    public void checkRecognitionSupport(Intent recognizerIntent, Executor executor, RecognitionSupportCallback supportListener) {
        Objects.requireNonNull(recognizerIntent, "intent must not be null");
        Objects.requireNonNull(supportListener, "listener must not be null");
        if (this.mService == null) {
            connectToSystemService();
        }
        putMessage(Message.obtain(this.mHandler, 6, new CheckRecognitionSupportArgs(recognizerIntent, executor, supportListener)));
    }

    public void triggerModelDownload(Intent recognizerIntent) {
        Objects.requireNonNull(recognizerIntent, "intent must not be null");
        if (this.mService == null) {
            connectToSystemService();
        }
        putMessage(Message.obtain(this.mHandler, 7, recognizerIntent));
    }

    public void setModelDownloadListener(Intent recognizerIntent, Executor executor, ModelDownloadListener listener) {
        Objects.requireNonNull(recognizerIntent, "intent must not be null");
        if (this.mService == null) {
            connectToSystemService();
        }
        putMessage(Message.obtain(this.mHandler, 8, new ModelDownloadListenerArgs(recognizerIntent, executor, listener)));
    }

    public void clearModelDownloadListener(Intent recognizerIntent) {
        Objects.requireNonNull(recognizerIntent, "intent must not be null");
        if (this.mService == null) {
            connectToSystemService();
        }
        putMessage(Message.obtain(this.mHandler, 9, recognizerIntent));
    }

    public void setTemporaryOnDeviceRecognizer(ComponentName componentName) {
        Handler handler = this.mHandler;
        handler.sendMessage(Message.obtain(handler, 5, componentName));
    }

    private static void checkIsCalledFromMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("SpeechRecognizer should be used only from the application's main thread");
        }
    }

    private void putMessage(Message msg) {
        if (this.mService == null) {
            this.mPendingTasks.offer(msg);
        } else {
            this.mHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStartListening(Intent recognizerIntent) {
        if (!checkOpenConnection()) {
            return;
        }
        try {
            this.mService.startListening(recognizerIntent, this.mListener, this.mContext.getAttributionSource());
        } catch (RemoteException e) {
            Log.m109e(TAG, "startListening() failed", e);
            this.mListener.onError(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStopMessage() {
        if (!checkOpenConnection()) {
            return;
        }
        try {
            this.mService.stopListening(this.mListener);
        } catch (RemoteException e) {
            Log.m109e(TAG, "stopListening() failed", e);
            this.mListener.onError(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCancelMessage() {
        if (!checkOpenConnection()) {
            return;
        }
        try {
            this.mService.cancel(this.mListener, false);
        } catch (RemoteException e) {
            Log.m109e(TAG, "cancel() failed", e);
            this.mListener.onError(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetTemporaryComponent(ComponentName componentName) {
        if (!maybeInitializeManagerService()) {
            return;
        }
        try {
            this.mManagerService.setTemporaryComponent(componentName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCheckRecognitionSupport(Intent recognizerIntent, Executor callbackExecutor, final RecognitionSupportCallback recognitionSupportCallback) {
        if (!maybeInitializeManagerService()) {
            return;
        }
        try {
            this.mService.checkRecognitionSupport(recognizerIntent, this.mContext.getAttributionSource(), new InternalSupportCallback(callbackExecutor, recognitionSupportCallback));
        } catch (RemoteException e) {
            Log.m109e(TAG, "checkRecognitionSupport() failed", e);
            callbackExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RecognitionSupportCallback.this.onError(5);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTriggerModelDownload(Intent recognizerIntent) {
        if (!maybeInitializeManagerService()) {
            return;
        }
        try {
            this.mService.triggerModelDownload(recognizerIntent, this.mContext.getAttributionSource());
        } catch (RemoteException e) {
            Log.m109e(TAG, "downloadModel() failed", e);
            this.mListener.onError(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetModelDownloadListener(Intent recognizerIntent, Executor callbackExecutor, final ModelDownloadListener modelDownloadListener) {
        if (!maybeInitializeManagerService()) {
            return;
        }
        InternalModelDownloadListener listener = null;
        if (modelDownloadListener != null) {
            try {
                listener = new InternalModelDownloadListener(callbackExecutor, modelDownloadListener);
            } catch (RemoteException e) {
                Log.m109e(TAG, "setModelDownloadListener() failed", e);
                callbackExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ModelDownloadListener.this.onError(5);
                    }
                });
                return;
            }
        }
        this.mService.setModelDownloadListener(recognizerIntent, this.mContext.getAttributionSource(), listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClearModelDownloadListener(Intent recognizerIntent) {
        if (!maybeInitializeManagerService()) {
            return;
        }
        try {
            this.mService.clearModelDownloadListener(recognizerIntent, this.mContext.getAttributionSource());
        } catch (RemoteException e) {
            Log.m109e(TAG, "clearModelDownloadListener() failed", e);
        }
    }

    private boolean checkOpenConnection() {
        if (this.mService != null) {
            return true;
        }
        this.mListener.onError(5);
        Log.m110e(TAG, "not connected to the recognition service");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChangeListener(RecognitionListener listener) {
        this.mListener.mInternalListener = listener;
    }

    public void destroy() {
        IRecognitionService iRecognitionService = this.mService;
        if (iRecognitionService != null) {
            try {
                iRecognitionService.cancel(this.mListener, true);
            } catch (RemoteException e) {
            }
        }
        this.mService = null;
        this.mPendingTasks.clear();
        this.mListener.mInternalListener = null;
    }

    private void connectToSystemService() {
        if (!maybeInitializeManagerService()) {
            return;
        }
        ComponentName componentName = getSpeechRecognizerComponentName();
        boolean z = this.mOnDevice;
        if (!z && componentName == null) {
            this.mListener.onError(5);
            return;
        }
        try {
            this.mManagerService.createSession(componentName, this.mClientToken, z, new IRecognitionServiceManagerCallback.Stub() { // from class: android.speech.SpeechRecognizer.2
                @Override // android.speech.IRecognitionServiceManagerCallback
                public void onSuccess(IRecognitionService service) throws RemoteException {
                    SpeechRecognizer.this.mService = service;
                    while (!SpeechRecognizer.this.mPendingTasks.isEmpty()) {
                        SpeechRecognizer.this.mHandler.sendMessage((Message) SpeechRecognizer.this.mPendingTasks.poll());
                    }
                }

                @Override // android.speech.IRecognitionServiceManagerCallback
                public void onError(int errorCode) throws RemoteException {
                    Log.m110e(SpeechRecognizer.TAG, "Bind to system recognition service failed with error " + errorCode);
                    SpeechRecognizer.this.mListener.onError(errorCode);
                }
            });
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    private synchronized boolean maybeInitializeManagerService() {
        if (this.mManagerService != null) {
            return true;
        }
        IBinder service = ServiceManager.getService(Context.SPEECH_RECOGNITION_SERVICE);
        if (service == null && this.mOnDevice) {
            service = (IBinder) this.mContext.getSystemService(Context.SPEECH_RECOGNITION_SERVICE);
        }
        IRecognitionServiceManager asInterface = IRecognitionServiceManager.Stub.asInterface(service);
        this.mManagerService = asInterface;
        if (asInterface == null) {
            InternalRecognitionListener internalRecognitionListener = this.mListener;
            if (internalRecognitionListener != null) {
                internalRecognitionListener.onError(5);
            }
            return false;
        }
        return true;
    }

    private ComponentName getSpeechRecognizerComponentName() {
        if (this.mOnDevice) {
            return null;
        }
        ComponentName componentName = this.mServiceComponent;
        if (componentName != null) {
            return componentName;
        }
        String serviceComponent = Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.VOICE_RECOGNITION_SERVICE);
        if (TextUtils.isEmpty(serviceComponent)) {
            Log.m110e(TAG, "no selected voice recognition service");
            this.mListener.onError(5);
            return null;
        }
        return ComponentName.unflattenFromString(serviceComponent);
    }

    /* loaded from: classes3.dex */
    private static class CheckRecognitionSupportArgs {
        final RecognitionSupportCallback mCallback;
        final Executor mCallbackExecutor;
        final Intent mIntent;

        private CheckRecognitionSupportArgs(Intent intent, Executor callbackExecutor, RecognitionSupportCallback callback) {
            this.mIntent = intent;
            this.mCallbackExecutor = callbackExecutor;
            this.mCallback = callback;
        }
    }

    /* loaded from: classes3.dex */
    private static class ModelDownloadListenerArgs {
        final Executor mExecutor;
        final Intent mIntent;
        final ModelDownloadListener mModelDownloadListener;

        private ModelDownloadListenerArgs(Intent intent, Executor executor, ModelDownloadListener modelDownloadListener) {
            this.mIntent = intent;
            this.mExecutor = executor;
            this.mModelDownloadListener = modelDownloadListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InternalRecognitionListener extends IRecognitionListener.Stub {
        private static final int MSG_BEGINNING_OF_SPEECH = 1;
        private static final int MSG_BUFFER_RECEIVED = 2;
        private static final int MSG_END_OF_SPEECH = 3;
        private static final int MSG_ERROR = 4;
        private static final int MSG_LANGUAGE_DETECTION = 12;
        private static final int MSG_ON_EVENT = 9;
        private static final int MSG_PARTIAL_RESULTS = 7;
        private static final int MSG_READY_FOR_SPEECH = 5;
        private static final int MSG_RESULTS = 6;
        private static final int MSG_RMS_CHANGED = 8;
        private static final int MSG_SEGMENT_END_SESSION = 11;
        private static final int MSG_SEGMENT_RESULTS = 10;
        private final Handler mInternalHandler;
        private RecognitionListener mInternalListener;

        private InternalRecognitionListener() {
            this.mInternalHandler = new Handler(Looper.getMainLooper()) { // from class: android.speech.SpeechRecognizer.InternalRecognitionListener.1
                @Override // android.p008os.Handler
                public void handleMessage(Message msg) {
                    if (InternalRecognitionListener.this.mInternalListener == null) {
                        return;
                    }
                    switch (msg.what) {
                        case 1:
                            InternalRecognitionListener.this.mInternalListener.onBeginningOfSpeech();
                            return;
                        case 2:
                            InternalRecognitionListener.this.mInternalListener.onBufferReceived((byte[]) msg.obj);
                            return;
                        case 3:
                            InternalRecognitionListener.this.mInternalListener.onEndOfSpeech();
                            return;
                        case 4:
                            InternalRecognitionListener.this.mInternalListener.onError(((Integer) msg.obj).intValue());
                            return;
                        case 5:
                            InternalRecognitionListener.this.mInternalListener.onReadyForSpeech((Bundle) msg.obj);
                            return;
                        case 6:
                            InternalRecognitionListener.this.mInternalListener.onResults((Bundle) msg.obj);
                            return;
                        case 7:
                            InternalRecognitionListener.this.mInternalListener.onPartialResults((Bundle) msg.obj);
                            return;
                        case 8:
                            InternalRecognitionListener.this.mInternalListener.onRmsChanged(((Float) msg.obj).floatValue());
                            return;
                        case 9:
                            InternalRecognitionListener.this.mInternalListener.onEvent(msg.arg1, (Bundle) msg.obj);
                            return;
                        case 10:
                            InternalRecognitionListener.this.mInternalListener.onSegmentResults((Bundle) msg.obj);
                            return;
                        case 11:
                            InternalRecognitionListener.this.mInternalListener.onEndOfSegmentedSession();
                            return;
                        case 12:
                            InternalRecognitionListener.this.mInternalListener.onLanguageDetection((Bundle) msg.obj);
                            return;
                        default:
                            return;
                    }
                }
            };
        }

        @Override // android.speech.IRecognitionListener
        public void onBeginningOfSpeech() {
            Message.obtain(this.mInternalHandler, 1).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onBufferReceived(byte[] buffer) {
            Message.obtain(this.mInternalHandler, 2, buffer).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onEndOfSpeech() {
            Message.obtain(this.mInternalHandler, 3).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onError(int error) {
            Message.obtain(this.mInternalHandler, 4, Integer.valueOf(error)).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onReadyForSpeech(Bundle noiseParams) {
            Message.obtain(this.mInternalHandler, 5, noiseParams).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onResults(Bundle results) {
            Message.obtain(this.mInternalHandler, 6, results).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onPartialResults(Bundle results) {
            Message.obtain(this.mInternalHandler, 7, results).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onRmsChanged(float rmsdB) {
            Message.obtain(this.mInternalHandler, 8, Float.valueOf(rmsdB)).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onSegmentResults(Bundle bundle) {
            Message.obtain(this.mInternalHandler, 10, bundle).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onEndOfSegmentedSession() {
            Message.obtain(this.mInternalHandler, 11).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onLanguageDetection(Bundle results) {
            Message.obtain(this.mInternalHandler, 12, results).sendToTarget();
        }

        @Override // android.speech.IRecognitionListener
        public void onEvent(int eventType, Bundle params) {
            Message.obtain(this.mInternalHandler, 9, eventType, eventType, params).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InternalSupportCallback extends IRecognitionSupportCallback.Stub {
        private final RecognitionSupportCallback mCallback;
        private final Executor mExecutor;

        private InternalSupportCallback(Executor executor, RecognitionSupportCallback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSupportResult$0(RecognitionSupport recognitionSupport) {
            this.mCallback.onSupportResult(recognitionSupport);
        }

        @Override // android.speech.IRecognitionSupportCallback
        public void onSupportResult(final RecognitionSupport recognitionSupport) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalSupportCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalSupportCallback.this.lambda$onSupportResult$0(recognitionSupport);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1(int errorCode) {
            this.mCallback.onError(errorCode);
        }

        @Override // android.speech.IRecognitionSupportCallback
        public void onError(final int errorCode) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalSupportCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalSupportCallback.this.lambda$onError$1(errorCode);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InternalModelDownloadListener extends IModelDownloadListener.Stub {
        private final Executor mExecutor;
        private final ModelDownloadListener mModelDownloadListener;

        private InternalModelDownloadListener(Executor executor, ModelDownloadListener modelDownloadListener) {
            this.mExecutor = executor;
            this.mModelDownloadListener = modelDownloadListener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProgress$0(int completedPercent) {
            this.mModelDownloadListener.onProgress(completedPercent);
        }

        @Override // android.speech.IModelDownloadListener
        public void onProgress(final int completedPercent) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalModelDownloadListener$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalModelDownloadListener.this.lambda$onProgress$0(completedPercent);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSuccess$1() {
            this.mModelDownloadListener.onSuccess();
        }

        @Override // android.speech.IModelDownloadListener
        public void onSuccess() throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalModelDownloadListener$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalModelDownloadListener.this.lambda$onSuccess$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onScheduled$2() {
            this.mModelDownloadListener.onScheduled();
        }

        @Override // android.speech.IModelDownloadListener
        public void onScheduled() throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalModelDownloadListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalModelDownloadListener.this.lambda$onScheduled$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(int error) {
            this.mModelDownloadListener.onError(error);
        }

        @Override // android.speech.IModelDownloadListener
        public void onError(final int error) throws RemoteException {
            this.mExecutor.execute(new Runnable() { // from class: android.speech.SpeechRecognizer$InternalModelDownloadListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechRecognizer.InternalModelDownloadListener.this.lambda$onError$3(error);
                }
            });
        }
    }
}
