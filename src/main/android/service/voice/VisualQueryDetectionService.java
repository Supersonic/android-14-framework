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
import android.service.voice.ISandboxedDetectionService;
import android.speech.IRecognitionServiceManager;
import android.util.Log;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.IContentCaptureManager;
import java.util.Objects;
import java.util.function.IntConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class VisualQueryDetectionService extends Service implements SandboxedDetectionInitializer {
    public static final String KEY_INITIALIZATION_STATUS = "initialization_status";
    public static final String SERVICE_INTERFACE = "android.service.voice.VisualQueryDetectionService";
    private static final String TAG = VisualQueryDetectionService.class.getSimpleName();
    private static final long UPDATE_TIMEOUT_MILLIS = 20000;
    private ContentCaptureManager mContentCaptureManager;
    private IRecognitionServiceManager mIRecognitionServiceManager;
    private IDetectorSessionVisualQueryDetectionCallback mRemoteCallback = null;
    private final ISandboxedDetectionService mInterface = new ISandboxedDetectionService.Stub() { // from class: android.service.voice.VisualQueryDetectionService.1
        @Override // android.service.voice.ISandboxedDetectionService
        public void detectWithVisualSignals(IDetectorSessionVisualQueryDetectionCallback callback) {
            Log.m106v(VisualQueryDetectionService.TAG, "#detectWithVisualSignals");
            VisualQueryDetectionService.this.mRemoteCallback = callback;
            VisualQueryDetectionService.this.onStartDetection();
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void stopDetection() {
            Log.m106v(VisualQueryDetectionService.TAG, "#stopDetection");
            VisualQueryDetectionService.this.onStopDetection();
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateState(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) throws RemoteException {
            Log.m106v(VisualQueryDetectionService.TAG, "#updateState" + (callback != null ? " with callback" : ""));
            VisualQueryDetectionService.this.onUpdateStateInternal(options, sharedMemory, callback);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void ping(IRemoteCallback callback) throws RemoteException {
            callback.sendResult(null);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent event, AudioFormat audioFormat, long timeoutMillis, IDspHotwordDetectionCallback callback) {
            throw new UnsupportedOperationException("Not supported by VisualQueryDetectionService");
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void detectFromMicrophoneSource(ParcelFileDescriptor audioStream, int audioSource, AudioFormat audioFormat, PersistableBundle options, IDspHotwordDetectionCallback callback) {
            throw new UnsupportedOperationException("Not supported by VisualQueryDetectionService");
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateAudioFlinger(IBinder audioFlinger) {
            AudioSystem.setAudioFlingerBinder(audioFlinger);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateContentCaptureManager(IContentCaptureManager manager, ContentCaptureOptions options) {
            VisualQueryDetectionService.this.mContentCaptureManager = new ContentCaptureManager(VisualQueryDetectionService.this, manager, options);
        }

        @Override // android.service.voice.ISandboxedDetectionService
        public void updateRecognitionServiceManager(IRecognitionServiceManager manager) {
            VisualQueryDetectionService.this.mIRecognitionServiceManager = manager;
        }
    };

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

    @Override // android.service.voice.SandboxedDetectionInitializer
    @SystemApi
    public void onUpdateState(PersistableBundle options, SharedMemory sharedMemory, long callbackTimeoutMillis, IntConsumer statusCallback) {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return this.mInterface.asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.voice.VisualQueryDetectionService: " + intent);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateStateInternal(PersistableBundle options, SharedMemory sharedMemory, IRemoteCallback callback) {
        IntConsumer intConsumer = SandboxedDetectionInitializer.createInitializationStatusConsumer(callback);
        onUpdateState(options, sharedMemory, UPDATE_TIMEOUT_MILLIS, intConsumer);
    }

    public void onStartDetection() {
        throw new UnsupportedOperationException();
    }

    public void onStopDetection() {
    }

    public final void gainedAttention() {
        try {
            this.mRemoteCallback.onAttentionGained();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final void lostAttention() {
        try {
            this.mRemoteCallback.onAttentionLost();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final void streamQuery(String partialQuery) throws IllegalStateException {
        Objects.requireNonNull(partialQuery);
        try {
            this.mRemoteCallback.onQueryDetected(partialQuery);
        } catch (RemoteException e) {
            throw new IllegalStateException("#streamQuery must be only be triggered after calling #gainedAttention to be in the attention gained state.");
        }
    }

    public final void rejectQuery() throws IllegalStateException {
        try {
            this.mRemoteCallback.onQueryRejected();
        } catch (RemoteException e) {
            throw new IllegalStateException("#rejectQuery must be only be triggered after calling #streamQuery to be in the query streaming state.");
        }
    }

    public final void finishQuery() throws IllegalStateException {
        try {
            this.mRemoteCallback.onQueryFinished();
        } catch (RemoteException e) {
            throw new IllegalStateException("#finishQuery must be only be triggered after calling #streamQuery to be in the query streaming state.");
        }
    }
}
