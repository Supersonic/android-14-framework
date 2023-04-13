package android.service.voice;

import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.service.voice.AlwaysOnHotwordDetector;
import android.service.voice.HotwordDetector;
import android.service.voice.HotwordRejectedResult;
import android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback;
import android.service.voice.SoftwareHotwordDetector;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.util.FunctionalUtils;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class SoftwareHotwordDetector extends AbstractDetector {
    private static final boolean DEBUG = false;
    private static final String TAG = SoftwareHotwordDetector.class.getSimpleName();
    private final AudioFormat mAudioFormat;
    private final HotwordDetector.Callback mCallback;
    private final Executor mExecutor;
    private final IVoiceInteractionManagerService mManagerService;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SoftwareHotwordDetector(IVoiceInteractionManagerService managerService, AudioFormat audioFormat, Executor executor, HotwordDetector.Callback callback) {
        super(managerService, executor, callback);
        this.mManagerService = managerService;
        this.mAudioFormat = audioFormat;
        this.mCallback = callback;
        this.mExecutor = executor != null ? executor : new HandlerExecutor(new Handler(Looper.getMainLooper()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.voice.AbstractDetector
    public void initialize(PersistableBundle options, SharedMemory sharedMemory) {
        initAndVerifyDetector(options, sharedMemory, new InitializationStateListener(this.mExecutor, this.mCallback), 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDetectorRemoteException$1() throws Exception {
        this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SoftwareHotwordDetector.this.lambda$onDetectorRemoteException$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDetectorRemoteException() {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$$ExternalSyntheticLambda0
            @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
            public final void runOrThrow() {
                SoftwareHotwordDetector.this.lambda$onDetectorRemoteException$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDetectorRemoteException$0() {
        this.mCallback.onFailure(new HotwordDetectionServiceFailure(7, "Detector remote exception occurs"));
    }

    @Override // android.service.voice.HotwordDetector
    public boolean startRecognition() throws HotwordDetector.IllegalDetectorStateException {
        throwIfDetectorIsNoLongerActive();
        maybeCloseExistingSession();
        try {
            this.mManagerService.startListeningFromMic(this.mAudioFormat, new BinderCallback(this.mExecutor, this.mCallback));
            return true;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return true;
        } catch (SecurityException e2) {
            Slog.m96e(TAG, "startRecognition failed: " + e2);
            return false;
        }
    }

    @Override // android.service.voice.HotwordDetector
    public boolean stopRecognition() throws HotwordDetector.IllegalDetectorStateException {
        throwIfDetectorIsNoLongerActive();
        try {
            this.mManagerService.stopListeningFromMic();
            return true;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return true;
        }
    }

    @Override // android.service.voice.AbstractDetector, android.service.voice.HotwordDetector
    public void destroy() {
        try {
            stopRecognition();
        } catch (Exception e) {
            Log.m107i(TAG, "failed to stopRecognition in destroy", e);
        }
        maybeCloseExistingSession();
        super.destroy();
    }

    @Override // android.service.voice.HotwordDetector
    public boolean isUsingSandboxedDetectionService() {
        return true;
    }

    private void maybeCloseExistingSession() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class BinderCallback extends IMicrophoneHotwordDetectionVoiceInteractionCallback.Stub {
        private final HotwordDetector.Callback mCallback;
        private final Executor mExecutor;

        BinderCallback(Executor executor, HotwordDetector.Callback callback) {
            this.mCallback = callback;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetected$1(final AudioFormat audioFormat, final ParcelFileDescriptor audioStream, final HotwordDetectedResult hotwordDetectedResult) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onDetected$0(audioFormat, audioStream, hotwordDetectedResult);
                }
            });
        }

        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onDetected(final HotwordDetectedResult hotwordDetectedResult, final AudioFormat audioFormat, final ParcelFileDescriptor audioStream) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onDetected$1(audioFormat, audioStream, hotwordDetectedResult);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetected$0(AudioFormat audioFormat, ParcelFileDescriptor audioStream, HotwordDetectedResult hotwordDetectedResult) {
            this.mCallback.onDetected(new AlwaysOnHotwordDetector.EventPayload.Builder().setCaptureAudioFormat(audioFormat).setAudioStream(audioStream).setHotwordDetectedResult(hotwordDetectedResult).build());
        }

        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onError(final DetectorFailure detectorFailure) {
            Slog.m92v(SoftwareHotwordDetector.TAG, "BinderCallback#onError detectorFailure: " + detectorFailure);
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onError$3(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(final DetectorFailure detectorFailure) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onError$2(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$2(DetectorFailure detectorFailure) {
            this.mCallback.onFailure(detectorFailure != null ? detectorFailure : new UnknownFailure("Error data is null"));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRejected$5(final HotwordRejectedResult result) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onRejected$4(result);
                }
            });
        }

        @Override // android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback
        public void onRejected(final HotwordRejectedResult result) {
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$BinderCallback$$ExternalSyntheticLambda5
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.BinderCallback.this.lambda$onRejected$5(result);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRejected$4(HotwordRejectedResult result) {
            this.mCallback.onRejected(result != null ? result : new HotwordRejectedResult.Builder().build());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InitializationStateListener extends IHotwordRecognitionStatusCallback.Stub {
        private final HotwordDetector.Callback mCallback;
        private final Executor mExecutor;

        InitializationStateListener(Executor executor, HotwordDetector.Callback callback) {
            this.mCallback = callback;
            this.mExecutor = executor;
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent recognitionEvent, HotwordDetectedResult result) {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent recognitionEvent) throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRejected(HotwordRejectedResult result) throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onError(int status) throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onDetectionFailure(final DetectorFailure detectorFailure) throws RemoteException {
            Slog.m92v(SoftwareHotwordDetector.TAG, "onDetectionFailure detectorFailure: " + detectorFailure);
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onDetectionFailure$1(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetectionFailure$1(final DetectorFailure detectorFailure) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onDetectionFailure$0(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetectionFailure$0(DetectorFailure detectorFailure) {
            this.mCallback.onFailure(detectorFailure != null ? detectorFailure : new UnknownFailure("Error data is null"));
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRecognitionPaused() throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRecognitionResumed() throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onStatusReported(final int status) {
            Slog.m92v(SoftwareHotwordDetector.TAG, "onStatusReported");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onStatusReported$3(status);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusReported$3(final int status) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onStatusReported$2(status);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusReported$2(int status) {
            this.mCallback.onHotwordDetectionServiceInitialized(status);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onProcessRestarted() throws RemoteException {
            Slog.m92v(SoftwareHotwordDetector.TAG, "onProcessRestarted()");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onProcessRestarted$5();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProcessRestarted$5() throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.SoftwareHotwordDetector$InitializationStateListener$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SoftwareHotwordDetector.InitializationStateListener.this.lambda$onProcessRestarted$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProcessRestarted$4() {
            this.mCallback.onHotwordDetectionServiceRestarted();
        }
    }

    @Override // android.service.voice.HotwordDetector
    public void dump(String prefix, PrintWriter pw) {
    }
}
