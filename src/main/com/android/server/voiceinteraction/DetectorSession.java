package com.android.server.voiceinteraction;

import android.annotation.RequiresPermission;
import android.app.AppOpsManager;
import android.app.compat.CompatChanges;
import android.attention.AttentionManagerInternal;
import android.content.Context;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.media.permission.Identity;
import android.media.permission.PermissionUtil;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.service.voice.HotwordDetectedResult;
import android.service.voice.HotwordDetectionService;
import android.service.voice.HotwordDetectionServiceFailure;
import android.service.voice.HotwordDetector;
import android.service.voice.HotwordRejectedResult;
import android.service.voice.IDspHotwordDetectionCallback;
import android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback;
import android.service.voice.ISandboxedDetectionService;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.infra.ServiceConnector;
import com.android.internal.util.FunctionalUtils;
import com.android.server.LocalServices;
import com.android.server.voiceinteraction.DetectorSession;
import com.android.server.voiceinteraction.HotwordDetectionConnection;
import com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public abstract class DetectorSession {
    public static final Duration MAX_UPDATE_TIMEOUT_DURATION = Duration.ofMillis(30000);
    public final AppOpsManager mAppOpsManager;
    public AttentionManagerInternal mAttentionManagerInternal;
    public final IHotwordRecognitionStatusCallback mCallback;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public ParcelFileDescriptor mCurrentAudioSink;
    public boolean mDebugHotwordLogging;
    @GuardedBy({"mLock"})
    public boolean mDestroyed;
    public final HotwordAudioStreamCopier mHotwordAudioStreamCopier;
    @GuardedBy({"mLock"})
    public boolean mInitialized;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public boolean mPerformingExternalSourceHotwordDetection;
    public final AttentionManagerInternal.ProximityUpdateCallbackInternal mProximityCallbackInternal;
    @GuardedBy({"mLock"})
    public double mProximityMeters;
    @GuardedBy({"mLock"})
    public HotwordDetectionConnection.ServiceConnection mRemoteDetectionService;
    public VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener mRemoteExceptionListener;
    public final ScheduledExecutorService mScheduledExecutorService;
    public final IBinder mToken;
    public final int mVoiceInteractionServiceUid;
    public final Identity mVoiceInteractorIdentity;
    public final Executor mAudioCopyExecutor = Executors.newCachedThreadPool();
    public final AtomicBoolean mUpdateStateAfterStartFinished = new AtomicBoolean(false);

    public abstract void informRestartProcessLocked();

    public DetectorSession(HotwordDetectionConnection.ServiceConnection serviceConnection, Object obj, Context context, IBinder iBinder, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, int i, Identity identity, ScheduledExecutorService scheduledExecutorService, boolean z, VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener) {
        this.mAttentionManagerInternal = null;
        AttentionManagerInternal.ProximityUpdateCallbackInternal proximityUpdateCallbackInternal = new AttentionManagerInternal.ProximityUpdateCallbackInternal() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda2
            public final void onProximityUpdate(double d) {
                DetectorSession.this.setProximityValue(d);
            }
        };
        this.mProximityCallbackInternal = proximityUpdateCallbackInternal;
        this.mDebugHotwordLogging = false;
        this.mProximityMeters = -1.0d;
        this.mInitialized = false;
        this.mDestroyed = false;
        this.mRemoteExceptionListener = detectorRemoteExceptionListener;
        this.mRemoteDetectionService = serviceConnection;
        this.mLock = obj;
        this.mContext = context;
        this.mToken = iBinder;
        this.mCallback = iHotwordRecognitionStatusCallback;
        this.mVoiceInteractionServiceUid = i;
        this.mVoiceInteractorIdentity = identity;
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mAppOpsManager = appOpsManager;
        if (getDetectorType() != 3) {
            this.mHotwordAudioStreamCopier = new HotwordAudioStreamCopier(appOpsManager, getDetectorType(), identity.uid, identity.packageName, identity.attributionTag);
        } else {
            this.mHotwordAudioStreamCopier = null;
        }
        this.mScheduledExecutorService = scheduledExecutorService;
        this.mDebugHotwordLogging = z;
        AttentionManagerInternal attentionManagerInternal = (AttentionManagerInternal) LocalServices.getService(AttentionManagerInternal.class);
        this.mAttentionManagerInternal = attentionManagerInternal;
        if (attentionManagerInternal != null) {
            attentionManagerInternal.onStartProximityUpdates(proximityUpdateCallbackInternal);
        }
    }

    public void notifyOnDetectorRemoteException() {
        Slog.d("DetectorSession", "notifyOnDetectorRemoteException: mRemoteExceptionListener=" + this.mRemoteExceptionListener);
        VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener = this.mRemoteExceptionListener;
        if (detectorRemoteExceptionListener != null) {
            detectorRemoteExceptionListener.onDetectorRemoteException(this.mToken, getDetectorType());
        }
    }

    public final void updateStateAfterProcessStartLocked(final PersistableBundle persistableBundle, final SharedMemory sharedMemory) {
        if (this.mRemoteDetectionService.postAsync(new ServiceConnector.Job() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda3
            public final Object run(Object obj) {
                CompletableFuture lambda$updateStateAfterProcessStartLocked$0;
                lambda$updateStateAfterProcessStartLocked$0 = DetectorSession.this.lambda$updateStateAfterProcessStartLocked$0(persistableBundle, sharedMemory, (ISandboxedDetectionService) obj);
                return lambda$updateStateAfterProcessStartLocked$0;
            }
        }).whenComplete(new BiConsumer() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                DetectorSession.this.lambda$updateStateAfterProcessStartLocked$1((Void) obj, (Throwable) obj2);
            }
        }) == null) {
            Slog.w("DetectorSession", "Failed to create AndroidFuture");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CompletableFuture lambda$updateStateAfterProcessStartLocked$0(PersistableBundle persistableBundle, SharedMemory sharedMemory, ISandboxedDetectionService iSandboxedDetectionService) throws Exception {
        final AndroidFuture androidFuture = new AndroidFuture();
        try {
            iSandboxedDetectionService.updateState(persistableBundle, sharedMemory, new IRemoteCallback.Stub() { // from class: com.android.server.voiceinteraction.DetectorSession.1
                public void sendResult(Bundle bundle) throws RemoteException {
                    androidFuture.complete((Object) null);
                    if (DetectorSession.this.mUpdateStateAfterStartFinished.getAndSet(true)) {
                        Slog.w("DetectorSession", "call callback after timeout");
                        if (DetectorSession.this.getDetectorType() != 3) {
                            HotwordMetricsLogger.writeDetectorEvent(DetectorSession.this.getDetectorType(), 5, DetectorSession.this.mVoiceInteractionServiceUid);
                            return;
                        }
                        return;
                    }
                    Pair initStatusAndMetricsResult = DetectorSession.getInitStatusAndMetricsResult(bundle);
                    int intValue = ((Integer) initStatusAndMetricsResult.first).intValue();
                    int intValue2 = ((Integer) initStatusAndMetricsResult.second).intValue();
                    try {
                        DetectorSession.this.mCallback.onStatusReported(intValue);
                        if (DetectorSession.this.getDetectorType() != 3) {
                            HotwordMetricsLogger.writeServiceInitResultEvent(DetectorSession.this.getDetectorType(), intValue2, DetectorSession.this.mVoiceInteractionServiceUid);
                        }
                    } catch (RemoteException e) {
                        Slog.w("DetectorSession", "Failed to report initialization status: " + e);
                        if (DetectorSession.this.getDetectorType() != 3) {
                            HotwordMetricsLogger.writeDetectorEvent(DetectorSession.this.getDetectorType(), 14, DetectorSession.this.mVoiceInteractionServiceUid);
                        }
                        DetectorSession.this.notifyOnDetectorRemoteException();
                    }
                }
            });
            if (getDetectorType() != 3) {
                HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 4, this.mVoiceInteractionServiceUid);
            }
        } catch (RemoteException e) {
            Slog.w("DetectorSession", "Failed to updateState for HotwordDetectionService", e);
            if (getDetectorType() != 3) {
                HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 19, this.mVoiceInteractionServiceUid);
            }
        }
        return androidFuture.orTimeout(30000L, TimeUnit.MILLISECONDS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStateAfterProcessStartLocked$1(Void r4, Throwable th) {
        if (!(th instanceof TimeoutException)) {
            if (th != null) {
                Slog.w("DetectorSession", "Failed to update state: " + th);
                return;
            }
            return;
        }
        Slog.w("DetectorSession", "updateState timed out");
        if (this.mUpdateStateAfterStartFinished.getAndSet(true)) {
            return;
        }
        try {
            this.mCallback.onStatusReported(100);
            if (getDetectorType() != 3) {
                HotwordMetricsLogger.writeServiceInitResultEvent(getDetectorType(), 4, this.mVoiceInteractionServiceUid);
            }
        } catch (RemoteException e) {
            Slog.w("DetectorSession", "Failed to report initialization status UNKNOWN", e);
            if (getDetectorType() != 3) {
                HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 14, this.mVoiceInteractionServiceUid);
            }
            notifyOnDetectorRemoteException();
        }
    }

    public static Pair<Integer, Integer> getInitStatusAndMetricsResult(Bundle bundle) {
        if (bundle == null) {
            return new Pair<>(100, 2);
        }
        int i = bundle.getInt("initialization_status", 100);
        if (i > HotwordDetectionService.getMaxCustomInitializationStatus()) {
            return new Pair<>(100, Integer.valueOf(i != 100 ? 3 : 2));
        }
        return new Pair<>(Integer.valueOf(i), Integer.valueOf(i == 0 ? 0 : 1));
    }

    public void updateStateLocked(final PersistableBundle persistableBundle, final SharedMemory sharedMemory, Instant instant) {
        if (getDetectorType() != 3) {
            HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 8, this.mVoiceInteractionServiceUid);
        }
        if (!this.mUpdateStateAfterStartFinished.get() && Instant.now().minus((TemporalAmount) MAX_UPDATE_TIMEOUT_DURATION).isBefore(instant)) {
            Slog.v("DetectorSession", "call updateStateAfterProcessStartLocked");
            updateStateAfterProcessStartLocked(persistableBundle, sharedMemory);
            return;
        }
        this.mRemoteDetectionService.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                ((ISandboxedDetectionService) obj).updateState(persistableBundle, sharedMemory, null);
            }
        });
    }

    public void startListeningFromExternalSourceLocked(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        handleExternalSourceHotwordDetectionLocked(parcelFileDescriptor, audioFormat, persistableBundle, iMicrophoneHotwordDetectionVoiceInteractionCallback);
    }

    public final void handleExternalSourceHotwordDetectionLocked(ParcelFileDescriptor parcelFileDescriptor, final AudioFormat audioFormat, final PersistableBundle persistableBundle, final IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        if (this.mPerformingExternalSourceHotwordDetection) {
            Slog.i("DetectorSession", "Hotword validation is already in progress for external source.");
            return;
        }
        final ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
        Pair<ParcelFileDescriptor, ParcelFileDescriptor> createPipe = createPipe();
        if (createPipe == null) {
            return;
        }
        final ParcelFileDescriptor parcelFileDescriptor2 = (ParcelFileDescriptor) createPipe.second;
        final ParcelFileDescriptor parcelFileDescriptor3 = (ParcelFileDescriptor) createPipe.first;
        this.mCurrentAudioSink = parcelFileDescriptor2;
        this.mPerformingExternalSourceHotwordDetection = true;
        this.mAudioCopyExecutor.execute(new Runnable() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                DetectorSession.this.lambda$handleExternalSourceHotwordDetectionLocked$3(autoCloseInputStream, parcelFileDescriptor2, iMicrophoneHotwordDetectionVoiceInteractionCallback);
            }
        });
        this.mRemoteDetectionService.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda6
            public final void runNoResult(Object obj) {
                DetectorSession.this.lambda$handleExternalSourceHotwordDetectionLocked$4(parcelFileDescriptor3, audioFormat, persistableBundle, parcelFileDescriptor2, autoCloseInputStream, iMicrophoneHotwordDetectionVoiceInteractionCallback, (ISandboxedDetectionService) obj);
            }
        });
        HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 10, this.mVoiceInteractionServiceUid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleExternalSourceHotwordDetectionLocked$3(InputStream inputStream, ParcelFileDescriptor parcelFileDescriptor, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        try {
            try {
                try {
                    ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = inputStream.read(bArr, 0, 1024);
                        if (read < 0) {
                            Slog.i("DetectorSession", "Reached end of stream for external hotword");
                            autoCloseOutputStream.close();
                            inputStream.close();
                            synchronized (this.mLock) {
                                this.mPerformingExternalSourceHotwordDetection = false;
                                closeExternalAudioStreamLocked("start external source");
                            }
                            return;
                        }
                        autoCloseOutputStream.write(bArr, 0, read);
                    }
                } catch (IOException e) {
                    Slog.w("DetectorSession", "Failed supplying audio data to validator", e);
                    try {
                        iMicrophoneHotwordDetectionVoiceInteractionCallback.onError(new HotwordDetectionServiceFailure(3, "Copy audio data failure for external source detection."));
                    } catch (RemoteException e2) {
                        Slog.w("DetectorSession", "Failed to report onError status: " + e2);
                        if (getDetectorType() != 3) {
                            HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 15, this.mVoiceInteractionServiceUid);
                        }
                        notifyOnDetectorRemoteException();
                    }
                    synchronized (this.mLock) {
                        this.mPerformingExternalSourceHotwordDetection = false;
                        closeExternalAudioStreamLocked("start external source");
                    }
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            synchronized (this.mLock) {
                this.mPerformingExternalSourceHotwordDetection = false;
                closeExternalAudioStreamLocked("start external source");
                throw th3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleExternalSourceHotwordDetectionLocked$4(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle, ParcelFileDescriptor parcelFileDescriptor2, InputStream inputStream, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback, ISandboxedDetectionService iSandboxedDetectionService) throws Exception {
        iSandboxedDetectionService.detectFromMicrophoneSource(parcelFileDescriptor, 2, audioFormat, persistableBundle, new C17702(parcelFileDescriptor2, inputStream, iMicrophoneHotwordDetectionVoiceInteractionCallback));
        bestEffortClose(parcelFileDescriptor);
    }

    /* renamed from: com.android.server.voiceinteraction.DetectorSession$2 */
    /* loaded from: classes2.dex */
    public class C17702 extends IDspHotwordDetectionCallback.Stub {
        public final /* synthetic */ InputStream val$audioSource;
        public final /* synthetic */ IMicrophoneHotwordDetectionVoiceInteractionCallback val$callback;
        public final /* synthetic */ ParcelFileDescriptor val$serviceAudioSink;

        public C17702(ParcelFileDescriptor parcelFileDescriptor, InputStream inputStream, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
            this.val$serviceAudioSink = parcelFileDescriptor;
            this.val$audioSource = inputStream;
            this.val$callback = iMicrophoneHotwordDetectionVoiceInteractionCallback;
        }

        public void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException {
            synchronized (DetectorSession.this.mLock) {
                DetectorSession detectorSession = DetectorSession.this;
                detectorSession.mPerformingExternalSourceHotwordDetection = false;
                HotwordMetricsLogger.writeDetectorEvent(detectorSession.getDetectorType(), 12, DetectorSession.this.mVoiceInteractionServiceUid);
                ScheduledExecutorService scheduledExecutorService = DetectorSession.this.mScheduledExecutorService;
                final ParcelFileDescriptor parcelFileDescriptor = this.val$serviceAudioSink;
                final InputStream inputStream = this.val$audioSource;
                scheduledExecutorService.schedule(new Runnable() { // from class: com.android.server.voiceinteraction.DetectorSession$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DetectorSession.C17702.lambda$onRejected$0(parcelFileDescriptor, inputStream);
                    }
                }, 2000L, TimeUnit.MILLISECONDS);
                try {
                    this.val$callback.onRejected(hotwordRejectedResult);
                    if (hotwordRejectedResult != null) {
                        Slog.i("DetectorSession", "Egressed 'hotword rejected result' from hotword trusted process");
                        if (DetectorSession.this.mDebugHotwordLogging) {
                            Slog.i("DetectorSession", "Egressed detected result: " + hotwordRejectedResult);
                        }
                    }
                } catch (RemoteException e) {
                    DetectorSession.this.notifyOnDetectorRemoteException();
                    throw e;
                }
            }
        }

        public static /* synthetic */ void lambda$onRejected$0(ParcelFileDescriptor parcelFileDescriptor, InputStream inputStream) {
            DetectorSession.bestEffortClose(parcelFileDescriptor, inputStream);
        }

        public void onDetected(HotwordDetectedResult hotwordDetectedResult) throws RemoteException {
            synchronized (DetectorSession.this.mLock) {
                DetectorSession detectorSession = DetectorSession.this;
                detectorSession.mPerformingExternalSourceHotwordDetection = false;
                HotwordMetricsLogger.writeDetectorEvent(detectorSession.getDetectorType(), 11, DetectorSession.this.mVoiceInteractionServiceUid);
                ScheduledExecutorService scheduledExecutorService = DetectorSession.this.mScheduledExecutorService;
                final ParcelFileDescriptor parcelFileDescriptor = this.val$serviceAudioSink;
                final InputStream inputStream = this.val$audioSource;
                scheduledExecutorService.schedule(new Runnable() { // from class: com.android.server.voiceinteraction.DetectorSession$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DetectorSession.C17702.lambda$onDetected$1(parcelFileDescriptor, inputStream);
                    }
                }, 2000L, TimeUnit.MILLISECONDS);
                try {
                    DetectorSession.this.enforcePermissionsForDataDelivery();
                    try {
                        HotwordDetectedResult startCopyingAudioStreams = DetectorSession.this.mHotwordAudioStreamCopier.startCopyingAudioStreams(hotwordDetectedResult);
                        try {
                            this.val$callback.onDetected(startCopyingAudioStreams, (AudioFormat) null, (ParcelFileDescriptor) null);
                            Slog.i("DetectorSession", "Egressed " + HotwordDetectedResult.getUsageSize(startCopyingAudioStreams) + " bits from hotword trusted process");
                            if (DetectorSession.this.mDebugHotwordLogging) {
                                Slog.i("DetectorSession", "Egressed detected result: " + startCopyingAudioStreams);
                            }
                        } catch (RemoteException e) {
                            DetectorSession.this.notifyOnDetectorRemoteException();
                            throw e;
                        }
                    } catch (IOException e2) {
                        Slog.w("DetectorSession", "Ignoring #onDetected due to a IOException", e2);
                        try {
                            this.val$callback.onError(new HotwordDetectionServiceFailure(6, "Copy audio stream failure."));
                        } catch (RemoteException e3) {
                            DetectorSession.this.notifyOnDetectorRemoteException();
                            throw e3;
                        }
                    }
                } catch (SecurityException e4) {
                    Slog.w("DetectorSession", "Ignoring #onDetected due to a SecurityException", e4);
                    HotwordMetricsLogger.writeDetectorEvent(DetectorSession.this.getDetectorType(), 13, DetectorSession.this.mVoiceInteractionServiceUid);
                    try {
                        this.val$callback.onError(new HotwordDetectionServiceFailure(5, "Security exception occurs in #onDetected method."));
                    } catch (RemoteException e5) {
                        DetectorSession.this.notifyOnDetectorRemoteException();
                        throw e5;
                    }
                }
            }
        }

        public static /* synthetic */ void lambda$onDetected$1(ParcelFileDescriptor parcelFileDescriptor, InputStream inputStream) {
            DetectorSession.bestEffortClose(parcelFileDescriptor, inputStream);
        }
    }

    public void initialize(PersistableBundle persistableBundle, SharedMemory sharedMemory) {
        synchronized (this.mLock) {
            if (!this.mInitialized && !this.mDestroyed) {
                updateStateAfterProcessStartLocked(persistableBundle, sharedMemory);
                this.mInitialized = true;
            }
        }
    }

    public void destroyLocked() {
        this.mDestroyed = true;
        this.mDebugHotwordLogging = false;
        this.mRemoteDetectionService = null;
        this.mRemoteExceptionListener = null;
        AttentionManagerInternal attentionManagerInternal = this.mAttentionManagerInternal;
        if (attentionManagerInternal != null) {
            attentionManagerInternal.onStopProximityUpdates(this.mProximityCallbackInternal);
        }
    }

    public void setDebugHotwordLoggingLocked(boolean z) {
        Slog.v("DetectorSession", "setDebugHotwordLoggingLocked: " + z);
        this.mDebugHotwordLogging = z;
    }

    public void updateRemoteSandboxedDetectionServiceLocked(HotwordDetectionConnection.ServiceConnection serviceConnection) {
        this.mRemoteDetectionService = serviceConnection;
    }

    public void reportErrorLocked(int i, String str) {
        try {
            this.mCallback.onDetectionFailure(new HotwordDetectionServiceFailure(i, str));
        } catch (RemoteException e) {
            Slog.w("DetectorSession", "Failed to report onError status: " + e);
            if (getDetectorType() != 3) {
                HotwordMetricsLogger.writeDetectorEvent(getDetectorType(), 15, this.mVoiceInteractionServiceUid);
            }
            notifyOnDetectorRemoteException();
        }
    }

    public boolean isSameCallback(IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) {
        synchronized (this.mLock) {
            if (iHotwordRecognitionStatusCallback == null) {
                return false;
            }
            return this.mCallback.asBinder().equals(iHotwordRecognitionStatusCallback.asBinder());
        }
    }

    public boolean isSameToken(IBinder iBinder) {
        synchronized (this.mLock) {
            if (iBinder == null) {
                return false;
            }
            return this.mToken == iBinder;
        }
    }

    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDestroyed;
        }
        return z;
    }

    public static Pair<ParcelFileDescriptor, ParcelFileDescriptor> createPipe() {
        try {
            ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
            return Pair.create(createPipe[0], createPipe[1]);
        } catch (IOException e) {
            Slog.e("DetectorSession", "Failed to create audio stream pipe", e);
            return null;
        }
    }

    public void saveProximityValueToBundle(HotwordDetectedResult hotwordDetectedResult) {
        synchronized (this.mLock) {
            if (hotwordDetectedResult != null) {
                double d = this.mProximityMeters;
                if (d != -1.0d) {
                    hotwordDetectedResult.setProximity(d);
                }
            }
        }
    }

    public final void setProximityValue(double d) {
        synchronized (this.mLock) {
            this.mProximityMeters = d;
        }
    }

    public void closeExternalAudioStreamLocked(String str) {
        if (this.mCurrentAudioSink != null) {
            Slog.i("DetectorSession", "Closing external audio stream to hotword detector: " + str);
            bestEffortClose(this.mCurrentAudioSink);
            this.mCurrentAudioSink = null;
        }
    }

    public static void bestEffortClose(Closeable... closeableArr) {
        for (Closeable closeable : closeableArr) {
            bestEffortClose(closeable);
        }
    }

    public static void bestEffortClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException unused) {
        }
    }

    public void enforcePermissionsForDataDelivery() {
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.voiceinteraction.DetectorSession$$ExternalSyntheticLambda0
            public final void runOrThrow() {
                DetectorSession.this.lambda$enforcePermissionsForDataDelivery$5();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$enforcePermissionsForDataDelivery$5() throws Exception {
        synchronized (this.mLock) {
            SoundTriggerSessionPermissionsDecorator.enforcePermissionForPreflight(this.mContext, this.mVoiceInteractorIdentity, "android.permission.RECORD_AUDIO");
            int strOpToOp = AppOpsManager.strOpToOp("android:record_audio_hotword");
            AppOpsManager appOpsManager = this.mAppOpsManager;
            Identity identity = this.mVoiceInteractorIdentity;
            appOpsManager.noteOpNoThrow(strOpToOp, identity.uid, identity.packageName, identity.attributionTag, "Providing hotword detection result to VoiceInteractionService");
            enforcePermissionForDataDelivery(this.mContext, this.mVoiceInteractorIdentity, "android.permission.CAPTURE_AUDIO_HOTWORD", "Providing hotword detection result to VoiceInteractionService");
        }
    }

    public static void enforcePermissionForDataDelivery(Context context, Identity identity, String str, String str2) {
        if (PermissionUtil.checkPermissionForDataDelivery(context, identity, str, str2) != 0) {
            throw new SecurityException(TextUtils.formatSimple("Failed to obtain permission %s for identity %s", new Object[]{str, SoundTriggerSessionPermissionsDecorator.toString(identity)}));
        }
    }

    @RequiresPermission(allOf = {"android.permission.READ_COMPAT_CHANGE_CONFIG", "android.permission.LOG_COMPAT_CHANGE"})
    public void enforceExtraKeyphraseIdNotLeaked(HotwordDetectedResult hotwordDetectedResult, SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) {
        if (CompatChanges.isChangeEnabled(215066299L, this.mVoiceInteractionServiceUid)) {
            for (SoundTrigger.KeyphraseRecognitionExtra keyphraseRecognitionExtra : keyphraseRecognitionEvent.keyphraseExtras) {
                if (keyphraseRecognitionExtra.getKeyphraseId() == hotwordDetectedResult.getHotwordPhraseId()) {
                    return;
                }
            }
            throw new SecurityException("Ignoring #onDetected due to trusted service sharing a keyphrase ID which the DSP did not detect");
        }
    }

    public final int getDetectorType() {
        if (this instanceof DspTrustedHotwordDetectorSession) {
            return 1;
        }
        if (this instanceof SoftwareTrustedHotwordDetectorSession) {
            return 2;
        }
        if (this instanceof VisualQueryDetectorSession) {
            return 3;
        }
        Slog.v("DetectorSession", "Unexpected detector type");
        return -1;
    }

    public void dumpLocked(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("mCallback=");
        printWriter.println(this.mCallback);
        printWriter.print(str);
        printWriter.print("mUpdateStateAfterStartFinished=");
        printWriter.println(this.mUpdateStateAfterStartFinished);
        printWriter.print(str);
        printWriter.print("mInitialized=");
        printWriter.println(this.mInitialized);
        printWriter.print(str);
        printWriter.print("mDestroyed=");
        printWriter.println(this.mDestroyed);
        printWriter.print(str);
        printWriter.print("DetectorType=");
        printWriter.println(HotwordDetector.detectorTypeToString(getDetectorType()));
        printWriter.print(str);
        printWriter.print("mPerformingExternalSourceHotwordDetection=");
        printWriter.println(this.mPerformingExternalSourceHotwordDetection);
    }
}
