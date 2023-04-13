package android.service.voice;

import android.annotation.SystemApi;
import android.hardware.soundtrigger.SoundTrigger;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioFormat;
import android.p008os.Binder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import android.service.voice.HotwordDetector;
import android.service.voice.IVisualQueryDetectionVoiceInteractionCallback;
import android.service.voice.VisualQueryDetector;
import android.util.Slog;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.util.FunctionalUtils;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public class VisualQueryDetector {
    private static final boolean DEBUG = false;
    private static final String TAG = VisualQueryDetector.class.getSimpleName();
    private final Callback mCallback;
    private final Executor mExecutor;
    private final VisualQueryDetectorInitializationDelegate mInitializationDelegate = new VisualQueryDetectorInitializationDelegate();
    private final IVoiceInteractionManagerService mManagerService;

    /* loaded from: classes3.dex */
    public interface Callback {
        void onFailure(DetectorFailure detectorFailure);

        void onQueryDetected(String str);

        void onQueryFinished();

        void onQueryRejected();

        void onVisualQueryDetectionServiceInitialized(int i);

        void onVisualQueryDetectionServiceRestarted();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VisualQueryDetector(IVoiceInteractionManagerService managerService, Executor executor, Callback callback) {
        this.mManagerService = managerService;
        this.mCallback = callback;
        this.mExecutor = executor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(PersistableBundle options, SharedMemory sharedMemory) {
        this.mInitializationDelegate.initialize(options, sharedMemory);
    }

    public void updateState(PersistableBundle options, SharedMemory sharedMemory) throws HotwordDetector.IllegalDetectorStateException {
        this.mInitializationDelegate.updateState(options, sharedMemory);
    }

    public boolean startRecognition() throws HotwordDetector.IllegalDetectorStateException {
        this.mInitializationDelegate.startRecognition();
        try {
            this.mManagerService.startPerceiving(new BinderCallback(this.mExecutor, this.mCallback));
            return true;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return true;
        } catch (SecurityException e2) {
            Slog.m96e(TAG, "startRecognition failed: " + e2);
            return false;
        }
    }

    public boolean stopRecognition() throws HotwordDetector.IllegalDetectorStateException {
        this.mInitializationDelegate.startRecognition();
        try {
            this.mManagerService.stopPerceiving();
            return true;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return true;
        }
    }

    public void destroy() {
        this.mInitializationDelegate.destroy();
    }

    public void dump(String prefix, PrintWriter pw) {
    }

    public HotwordDetector getInitializationDelegate() {
        return this.mInitializationDelegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerOnDestroyListener(Consumer<AbstractDetector> onDestroyListener) {
        this.mInitializationDelegate.registerOnDestroyListener(onDestroyListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class VisualQueryDetectorInitializationDelegate extends AbstractDetector {
        VisualQueryDetectorInitializationDelegate() {
            super(VisualQueryDetector.this.mManagerService, VisualQueryDetector.this.mExecutor, null);
        }

        @Override // android.service.voice.AbstractDetector
        void initialize(PersistableBundle options, SharedMemory sharedMemory) {
            initAndVerifyDetector(options, sharedMemory, new InitializationStateListener(VisualQueryDetector.this.mExecutor, VisualQueryDetector.this.mCallback), 3);
        }

        @Override // android.service.voice.HotwordDetector
        public boolean stopRecognition() throws HotwordDetector.IllegalDetectorStateException {
            throwIfDetectorIsNoLongerActive();
            return true;
        }

        @Override // android.service.voice.HotwordDetector
        public boolean startRecognition() throws HotwordDetector.IllegalDetectorStateException {
            throwIfDetectorIsNoLongerActive();
            return true;
        }

        @Override // android.service.voice.AbstractDetector, android.service.voice.HotwordDetector
        public final boolean startRecognition(ParcelFileDescriptor audioStream, AudioFormat audioFormat, PersistableBundle options) throws HotwordDetector.IllegalDetectorStateException {
            return false;
        }

        @Override // android.service.voice.HotwordDetector
        public boolean isUsingSandboxedDetectionService() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class BinderCallback extends IVisualQueryDetectionVoiceInteractionCallback.Stub {
        private final Callback mCallback;
        private final Executor mExecutor;

        BinderCallback(Executor executor, Callback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryDetected(final String partialQuery) {
            Slog.m92v(VisualQueryDetector.TAG, "BinderCallback#onQueryDetected");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryDetected$1(partialQuery);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryDetected$1(final String partialQuery) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryDetected$0(partialQuery);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryDetected$0(String partialQuery) {
            this.mCallback.onQueryDetected(partialQuery);
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryFinished() {
            Slog.m92v(VisualQueryDetector.TAG, "BinderCallback#onQueryFinished");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda7
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryFinished$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryFinished$3() throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryFinished$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryFinished$2() {
            this.mCallback.onQueryFinished();
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onQueryRejected() {
            Slog.m92v(VisualQueryDetector.TAG, "BinderCallback#onQueryRejected");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryRejected$5();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryRejected$5() throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.BinderCallback.this.lambda$onQueryRejected$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQueryRejected$4() {
            this.mCallback.onQueryRejected();
        }

        @Override // android.service.voice.IVisualQueryDetectionVoiceInteractionCallback
        public void onDetectionFailure(final DetectorFailure detectorFailure) {
            Slog.m92v(VisualQueryDetector.TAG, "BinderCallback#onDetectionFailure");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda6
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.BinderCallback.this.lambda$onDetectionFailure$7(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetectionFailure$7(final DetectorFailure detectorFailure) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$BinderCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.BinderCallback.this.lambda$onDetectionFailure$6(detectorFailure);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDetectionFailure$6(DetectorFailure detectorFailure) {
            this.mCallback.onFailure(detectorFailure);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InitializationStateListener extends IHotwordRecognitionStatusCallback.Stub {
        private final Callback mCallback;
        private final Executor mExecutor;

        InitializationStateListener(Executor executor, Callback callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
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
        public void onRecognitionPaused() throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onRecognitionResumed() throws RemoteException {
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onStatusReported(final int status) {
            Slog.m92v(VisualQueryDetector.TAG, "onStatusReported");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$InitializationStateListener$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.InitializationStateListener.this.lambda$onStatusReported$1(status);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusReported$1(final int status) throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$InitializationStateListener$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.InitializationStateListener.this.lambda$onStatusReported$0(status);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStatusReported$0(int status) {
            this.mCallback.onVisualQueryDetectionServiceInitialized(status);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onProcessRestarted() throws RemoteException {
            Slog.m92v(VisualQueryDetector.TAG, "onProcessRestarted()");
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VisualQueryDetector$InitializationStateListener$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                public final void runOrThrow() {
                    VisualQueryDetector.InitializationStateListener.this.lambda$onProcessRestarted$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProcessRestarted$3() throws Exception {
            this.mExecutor.execute(new Runnable() { // from class: android.service.voice.VisualQueryDetector$InitializationStateListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VisualQueryDetector.InitializationStateListener.this.lambda$onProcessRestarted$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProcessRestarted$2() {
            this.mCallback.onVisualQueryDetectionServiceRestarted();
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onError(int status) throws RemoteException {
            Slog.m92v(VisualQueryDetector.TAG, "Initialization Error: (" + status + NavigationBarInflaterView.KEY_CODE_END);
        }

        @Override // com.android.internal.app.IHotwordRecognitionStatusCallback
        public void onDetectionFailure(DetectorFailure detectorFailure) throws RemoteException {
        }
    }
}
