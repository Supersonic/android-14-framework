package android.service.voice;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.media.AudioSystem;
import android.p008os.IBinder;
import android.p008os.IRemoteCallback;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.ISandboxedDetectionService;
import android.speech.IRecognitionServiceManager;
import android.util.Log;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.IContentCaptureManager;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.function.IntConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class HotwordDetectionService extends Service implements SandboxedDetectionInitializer {
    public static final int AUDIO_SOURCE_EXTERNAL = 2;
    public static final int AUDIO_SOURCE_MICROPHONE = 1;
    private static final boolean DBG = false;
    public static final boolean ENABLE_PROXIMITY_RESULT = true;
    @Deprecated
    public static final int INITIALIZATION_STATUS_SUCCESS = 0;
    @Deprecated
    public static final int INITIALIZATION_STATUS_UNKNOWN = 100;
    public static final String SERVICE_INTERFACE = "android.service.voice.HotwordDetectionService";
    private static final String TAG = "HotwordDetectionService";
    private static final long UPDATE_TIMEOUT_MILLIS = 20000;
    private ContentCaptureManager mContentCaptureManager;
    private IRecognitionServiceManager mIRecognitionServiceManager;
    private final ISandboxedDetectionService mInterface = new ISandboxedDetectionService.Stub() { // from class: android.service.voice.HotwordDetectionService.1
        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent event, AudioFormat audioFormat, long timeoutMillis, IDspHotwordDetectionCallback callback) throws RemoteException {
            HotwordDetectionService.this.onDetect(new AlwaysOnHotwordDetector.EventPayload.Builder(event).build(), timeoutMillis, new Callback(callback));
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateState(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) throws RemoteException {
            Log.m106v(HotwordDetectionService.TAG, "#updateState" + (callback != null ? " with callback" : ""));
            HotwordDetectionService.this.onUpdateStateInternal(options, sharedMemory, callback);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromMicrophoneSource(ParcelFileDescriptor audioStream, int audioSource, AudioFormat audioFormat, PersistableBundle options, IDspHotwordDetectionCallback callback) throws RemoteException {
            switch (audioSource) {
                case 1:
                    HotwordDetectionService.this.onDetect(new Callback(callback));
                    return;
                case 2:
                    HotwordDetectionService.this.onDetect(audioStream, audioFormat, options, new Callback(callback));
                    return;
                default:
                    Log.m108i(HotwordDetectionService.TAG, "Unsupported audio source " + audioSource);
                    return;
            }
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectWithVisualSignals(IDetectorSessionVisualQueryDetectionCallback callback) {
            throw new UnsupportedOperationException("Not supported by HotwordDetectionService");
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateAudioFlinger(IBinder audioFlinger) {
            AudioSystem.setAudioFlingerBinder(audioFlinger);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateContentCaptureManager(IContentCaptureManager manager, ContentCaptureOptions options) {
            HotwordDetectionService.this.mContentCaptureManager = new ContentCaptureManager(HotwordDetectionService.this, manager, options);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateRecognitionServiceManager(IRecognitionServiceManager manager) {
            HotwordDetectionService.this.mIRecognitionServiceManager = manager;
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void ping(IRemoteCallback callback) throws RemoteException {
            callback.sendResult(null);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void stopDetection() {
            HotwordDetectionService.this.onStopDetection();
        }
    };

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    @interface AudioSource {
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.voice.HotwordDetectionService: " + intent);
        return null;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        IRecognitionServiceManager iRecognitionServiceManager;
        if (Context.CONTENT_CAPTURE_MANAGER_SERVICE.equals(name)) {
            return this.mContentCaptureManager;
        }
        if (Context.SPEECH_RECOGNITION_SERVICE.equals(name) && (iRecognitionServiceManager = this.mIRecognitionServiceManager) != null) {
            return iRecognitionServiceManager.asBinder();
        }
        return super.getSystemService(name);
    }

    @SystemApi
    @Deprecated
    public static int getMaxCustomInitializationStatus() {
        return 2;
    }

    @SystemApi
    public void onDetect(AlwaysOnHotwordDetector.EventPayload eventPayload, long timeoutMillis, Callback callback) {
        throw new UnsupportedOperationException();
    }

    @Override // android.service.voice.SandboxedDetectionInitializer
    @SystemApi
    public void onUpdateState(PersistableBundle options, SharedMemory sharedMemory, long callbackTimeoutMillis, IntConsumer statusCallback) {
    }

    public void onDetect(Callback callback) {
        throw new UnsupportedOperationException();
    }

    public void onDetect(ParcelFileDescriptor audioStream, AudioFormat audioFormat, PersistableBundle options, Callback callback) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateStateInternal(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) {
        IntConsumer intConsumer = SandboxedDetectionInitializer.createInitializationStatusConsumer(callback);
        onUpdateState(options, sharedMemory, UPDATE_TIMEOUT_MILLIS, intConsumer);
    }

    public void onStopDetection() {
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class Callback {
        private final IDspHotwordDetectionCallback mRemoteCallback;

        private Callback(IDspHotwordDetectionCallback remoteCallback) {
            this.mRemoteCallback = remoteCallback;
        }

        public void onDetected(HotwordDetectedResult result) {
            Objects.requireNonNull(result);
            PersistableBundle persistableBundle = result.getExtras();
            if (!persistableBundle.isEmpty() && HotwordDetectedResult.getParcelableSize(persistableBundle) > HotwordDetectedResult.getMaxBundleSize()) {
                throw new IllegalArgumentException("The bundle size of result is larger than max bundle size (" + HotwordDetectedResult.getMaxBundleSize() + ") of HotwordDetectedResult");
            }
            try {
                this.mRemoteCallback.onDetected(result);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }

        public void onRejected(HotwordRejectedResult result) {
            Objects.requireNonNull(result);
            try {
                this.mRemoteCallback.onRejected(result);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }
}
