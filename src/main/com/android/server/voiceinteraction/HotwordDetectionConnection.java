package com.android.server.voiceinteraction;

import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.AudioFormat;
import android.media.AudioManagerInternal;
import android.media.permission.Identity;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SharedMemory;
import android.provider.DeviceConfig;
import android.service.voice.HotwordDetectedResult;
import android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback;
import android.service.voice.ISandboxedDetectionService;
import android.service.voice.IVisualQueryDetectionVoiceInteractionCallback;
import android.service.voice.VoiceInteractionManagerInternal;
import android.speech.IRecognitionServiceManager;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.contentcapture.IContentCaptureManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.app.IVisualQueryDetectionAttentionListener;
import com.android.internal.infra.ServiceConnector;
import com.android.server.LocalServices;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.voiceinteraction.HotwordDetectionConnection;
import com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes2.dex */
public final class HotwordDetectionConnection {
    public IBinder mAudioFlinger;
    public final IBinder.DeathRecipient mAudioServerDeathRecipient;
    public final ScheduledFuture<?> mCancellationTaskFuture;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public boolean mDebugHotwordLogging;
    public ScheduledFuture<?> mDebugHotwordLoggingTimeoutFuture;
    @GuardedBy({"mLock"})
    public final SparseArray<DetectorSession> mDetectorSessions;
    public int mDetectorType;
    public final ComponentName mHotwordDetectionComponentName;
    public final ServiceConnectionFactory mHotwordDetectionServiceConnectionFactory;
    public volatile VoiceInteractionManagerInternal.HotwordDetectionServiceIdentity mIdentity;
    public Instant mLastRestartInstant;
    public final Object mLock;
    public final int mReStartPeriodSeconds;
    public VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener mRemoteExceptionListener;
    public ServiceConnection mRemoteHotwordDetectionService;
    public ServiceConnection mRemoteVisualQueryDetectionService;
    public int mRestartCount;
    public final ScheduledExecutorService mScheduledExecutorService;
    public final int mUser;
    public final ComponentName mVisualQueryDetectionComponentName;
    public final ServiceConnectionFactory mVisualQueryDetectionServiceConnectionFactory;
    public final int mVoiceInteractionServiceUid;
    @GuardedBy({"mLock"})
    public final Identity mVoiceInteractorIdentity;

    public HotwordDetectionConnection(Object obj, Context context, int i, Identity identity, ComponentName componentName, ComponentName componentName2, int i2, boolean z, int i3, VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener) {
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.mScheduledExecutorService = newSingleThreadScheduledExecutor;
        this.mAudioServerDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda6
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                HotwordDetectionConnection.this.audioServerDied();
            }
        };
        this.mDebugHotwordLoggingTimeoutFuture = null;
        this.mRestartCount = 0;
        this.mDebugHotwordLogging = false;
        this.mDetectorSessions = new SparseArray<>();
        this.mLock = obj;
        this.mContext = context;
        this.mVoiceInteractionServiceUid = i;
        this.mVoiceInteractorIdentity = identity;
        this.mHotwordDetectionComponentName = componentName;
        this.mVisualQueryDetectionComponentName = componentName2;
        this.mUser = i2;
        this.mDetectorType = i3;
        this.mRemoteExceptionListener = detectorRemoteExceptionListener;
        int i4 = DeviceConfig.getInt("voice_interaction", "restart_period_in_seconds", 0);
        this.mReStartPeriodSeconds = i4;
        Intent intent = new Intent("android.service.voice.HotwordDetectionService");
        intent.setComponent(componentName);
        Intent intent2 = new Intent("android.service.voice.VisualQueryDetectionService");
        intent2.setComponent(componentName2);
        initAudioFlingerLocked();
        this.mHotwordDetectionServiceConnectionFactory = new ServiceConnectionFactory(intent, z);
        this.mVisualQueryDetectionServiceConnectionFactory = new ServiceConnectionFactory(intent2, z);
        this.mLastRestartInstant = Instant.now();
        if (i4 <= 0) {
            this.mCancellationTaskFuture = null;
        } else {
            this.mCancellationTaskFuture = newSingleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    HotwordDetectionConnection.this.lambda$new$0();
                }
            }, i4, i4, TimeUnit.SECONDS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Slog.v("HotwordDetectionConnection", "Time to restart the process, TTL has passed");
        synchronized (this.mLock) {
            restartProcessLocked();
            int i = this.mDetectorType;
            if (i != 3) {
                HotwordMetricsLogger.writeServiceRestartEvent(i, 2, this.mVoiceInteractionServiceUid);
            }
        }
    }

    public final void initAudioFlingerLocked() {
        IBinder waitForService = ServiceManager.waitForService("media.audio_flinger");
        this.mAudioFlinger = waitForService;
        if (waitForService == null) {
            throw new IllegalStateException("Service media.audio_flinger wasn't found.");
        }
        try {
            waitForService.linkToDeath(this.mAudioServerDeathRecipient, 0);
        } catch (RemoteException e) {
            Slog.w("HotwordDetectionConnection", "Audio server died before we registered a DeathRecipient; retrying init.", e);
            initAudioFlingerLocked();
        }
    }

    public final void audioServerDied() {
        Slog.w("HotwordDetectionConnection", "Audio server died; restarting the HotwordDetectionService.");
        synchronized (this.mLock) {
            initAudioFlingerLocked();
            restartProcessLocked();
            int i = this.mDetectorType;
            if (i != 3) {
                HotwordMetricsLogger.writeServiceRestartEvent(i, 1, this.mVoiceInteractionServiceUid);
            }
        }
    }

    public void cancelLocked() {
        Slog.v("HotwordDetectionConnection", "cancelLocked");
        clearDebugHotwordLoggingTimeoutLocked();
        this.mRemoteExceptionListener = null;
        runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DetectorSession) obj).destroyLocked();
            }
        });
        this.mDetectorSessions.clear();
        this.mDebugHotwordLogging = false;
        unbindVisualQueryDetectionService();
        unbindHotwordDetectionService();
        ScheduledFuture<?> scheduledFuture = this.mCancellationTaskFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        IBinder iBinder = this.mAudioFlinger;
        if (iBinder != null) {
            iBinder.unlinkToDeath(this.mAudioServerDeathRecipient, 0);
        }
    }

    public final void unbindVisualQueryDetectionService() {
        ServiceConnection serviceConnection = this.mRemoteVisualQueryDetectionService;
        if (serviceConnection != null) {
            serviceConnection.unbind();
            this.mRemoteVisualQueryDetectionService = null;
        }
        resetDetectionProcessIdentityIfEmptyLocked();
    }

    public final void unbindHotwordDetectionService() {
        ServiceConnection serviceConnection = this.mRemoteHotwordDetectionService;
        if (serviceConnection != null) {
            serviceConnection.unbind();
            this.mRemoteHotwordDetectionService = null;
        }
        resetDetectionProcessIdentityIfEmptyLocked();
    }

    @GuardedBy({"mLock"})
    public final void resetDetectionProcessIdentityIfEmptyLocked() {
        if (this.mRemoteHotwordDetectionService == null && this.mRemoteVisualQueryDetectionService == null) {
            ((PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class)).setHotwordDetectionServiceProvider(null);
            if (this.mIdentity != null) {
                removeServiceUidForAudioPolicy(this.mIdentity.getIsolatedUid());
            }
            this.mIdentity = null;
        }
    }

    public void updateStateLocked(PersistableBundle persistableBundle, SharedMemory sharedMemory, IBinder iBinder) {
        DetectorSession detectorSessionByTokenLocked = getDetectorSessionByTokenLocked(iBinder);
        if (detectorSessionByTokenLocked == null) {
            Slog.v("HotwordDetectionConnection", "Not found the detector by token");
        } else {
            detectorSessionByTokenLocked.updateStateLocked(persistableBundle, sharedMemory, this.mLastRestartInstant);
        }
    }

    public void startListeningFromMicLocked(AudioFormat audioFormat, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        SoftwareTrustedHotwordDetectorSession softwareTrustedHotwordDetectorSessionLocked = getSoftwareTrustedHotwordDetectorSessionLocked();
        if (softwareTrustedHotwordDetectorSessionLocked == null) {
            return;
        }
        softwareTrustedHotwordDetectorSessionLocked.startListeningFromMicLocked(audioFormat, iMicrophoneHotwordDetectionVoiceInteractionCallback);
    }

    public void setVisualQueryDetectionAttentionListenerLocked(IVisualQueryDetectionAttentionListener iVisualQueryDetectionAttentionListener) {
        VisualQueryDetectorSession visualQueryDetectorSessionLocked = getVisualQueryDetectorSessionLocked();
        if (visualQueryDetectorSessionLocked == null) {
            return;
        }
        visualQueryDetectorSessionLocked.setVisualQueryDetectionAttentionListenerLocked(iVisualQueryDetectionAttentionListener);
    }

    public void startPerceivingLocked(IVisualQueryDetectionVoiceInteractionCallback iVisualQueryDetectionVoiceInteractionCallback) {
        VisualQueryDetectorSession visualQueryDetectorSessionLocked = getVisualQueryDetectorSessionLocked();
        if (visualQueryDetectorSessionLocked == null) {
            return;
        }
        visualQueryDetectorSessionLocked.startPerceivingLocked(iVisualQueryDetectionVoiceInteractionCallback);
    }

    public void stopPerceivingLocked() {
        VisualQueryDetectorSession visualQueryDetectorSessionLocked = getVisualQueryDetectorSessionLocked();
        if (visualQueryDetectorSessionLocked == null) {
            return;
        }
        visualQueryDetectorSessionLocked.stopPerceivingLocked();
    }

    public void startListeningFromExternalSourceLocked(ParcelFileDescriptor parcelFileDescriptor, AudioFormat audioFormat, PersistableBundle persistableBundle, IBinder iBinder, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        DetectorSession detectorSessionByTokenLocked = getDetectorSessionByTokenLocked(iBinder);
        if (detectorSessionByTokenLocked == null) {
            Slog.v("HotwordDetectionConnection", "Not found the detector by token");
        } else {
            detectorSessionByTokenLocked.startListeningFromExternalSourceLocked(parcelFileDescriptor, audioFormat, persistableBundle, iMicrophoneHotwordDetectionVoiceInteractionCallback);
        }
    }

    public void stopListeningFromMicLocked() {
        SoftwareTrustedHotwordDetectorSession softwareTrustedHotwordDetectorSessionLocked = getSoftwareTrustedHotwordDetectorSessionLocked();
        if (softwareTrustedHotwordDetectorSessionLocked == null) {
            return;
        }
        softwareTrustedHotwordDetectorSessionLocked.stopListeningFromMicLocked();
    }

    public void triggerHardwareRecognitionEventForTestLocked(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) {
        detectFromDspSource(keyphraseRecognitionEvent, iHotwordRecognitionStatusCallback);
    }

    public final void detectFromDspSource(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) {
        synchronized (this.mLock) {
            DspTrustedHotwordDetectorSession dspTrustedHotwordDetectorSessionLocked = getDspTrustedHotwordDetectorSessionLocked();
            if (dspTrustedHotwordDetectorSessionLocked != null && dspTrustedHotwordDetectorSessionLocked.isSameCallback(iHotwordRecognitionStatusCallback)) {
                dspTrustedHotwordDetectorSessionLocked.detectFromDspSourceLocked(keyphraseRecognitionEvent, iHotwordRecognitionStatusCallback);
                return;
            }
            Slog.v("HotwordDetectionConnection", "Not found the Dsp detector by callback");
        }
    }

    public void forceRestart() {
        Slog.v("HotwordDetectionConnection", "Requested to restart the service internally. Performing the restart");
        synchronized (this.mLock) {
            restartProcessLocked();
        }
    }

    public void setDebugHotwordLoggingLocked(final boolean z) {
        Slog.v("HotwordDetectionConnection", "setDebugHotwordLoggingLocked: " + z);
        clearDebugHotwordLoggingTimeoutLocked();
        this.mDebugHotwordLogging = z;
        runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DetectorSession) obj).setDebugHotwordLoggingLocked(z);
            }
        });
        if (z) {
            this.mDebugHotwordLoggingTimeoutFuture = this.mScheduledExecutorService.schedule(new Runnable() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    HotwordDetectionConnection.this.lambda$setDebugHotwordLoggingLocked$4();
                }
            }, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setDebugHotwordLoggingLocked$4() {
        Slog.v("HotwordDetectionConnection", "Timeout to reset mDebugHotwordLogging to false");
        synchronized (this.mLock) {
            this.mDebugHotwordLogging = false;
            runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((DetectorSession) obj).setDebugHotwordLoggingLocked(false);
                }
            });
        }
    }

    public void setDetectorType(int i) {
        this.mDetectorType = i;
    }

    public final void clearDebugHotwordLoggingTimeoutLocked() {
        ScheduledFuture<?> scheduledFuture = this.mDebugHotwordLoggingTimeoutFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            this.mDebugHotwordLoggingTimeoutFuture = null;
        }
    }

    public final void restartProcessLocked() {
        Slog.v("HotwordDetectionConnection", "Restarting hotword detection process");
        ServiceConnection serviceConnection = this.mRemoteHotwordDetectionService;
        ServiceConnection serviceConnection2 = this.mRemoteVisualQueryDetectionService;
        VoiceInteractionManagerInternal.HotwordDetectionServiceIdentity hotwordDetectionServiceIdentity = this.mIdentity;
        this.mLastRestartInstant = Instant.now();
        this.mRestartCount++;
        if (serviceConnection != null) {
            this.mRemoteHotwordDetectionService = this.mHotwordDetectionServiceConnectionFactory.createLocked();
        }
        if (serviceConnection2 != null) {
            this.mRemoteVisualQueryDetectionService = this.mVisualQueryDetectionServiceConnectionFactory.createLocked();
        }
        Slog.v("HotwordDetectionConnection", "Started the new process, dispatching processRestarted to detector");
        runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                HotwordDetectionConnection.this.lambda$restartProcessLocked$5((DetectorSession) obj);
            }
        });
        if (serviceConnection != null) {
            serviceConnection.ignoreConnectionStatusEvents();
            serviceConnection.unbind();
        }
        if (serviceConnection2 != null) {
            serviceConnection2.ignoreConnectionStatusEvents();
            serviceConnection2.unbind();
        }
        if (hotwordDetectionServiceIdentity != null) {
            removeServiceUidForAudioPolicy(hotwordDetectionServiceIdentity.getIsolatedUid());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restartProcessLocked$5(DetectorSession detectorSession) {
        detectorSession.updateRemoteSandboxedDetectionServiceLocked(detectorSession instanceof VisualQueryDetectorSession ? this.mRemoteVisualQueryDetectionService : this.mRemoteHotwordDetectionService);
        detectorSession.informRestartProcessLocked();
    }

    /* loaded from: classes2.dex */
    public static final class SoundTriggerCallback extends IRecognitionStatusCallback.Stub {
        public final IHotwordRecognitionStatusCallback mExternalCallback;
        public final HotwordDetectionConnection mHotwordDetectionConnection;
        public final int mVoiceInteractionServiceUid;

        public SoundTriggerCallback(IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, HotwordDetectionConnection hotwordDetectionConnection, int i) {
            this.mHotwordDetectionConnection = hotwordDetectionConnection;
            this.mExternalCallback = iHotwordRecognitionStatusCallback;
            this.mVoiceInteractionServiceUid = i;
        }

        public void onKeyphraseDetected(SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent) throws RemoteException {
            if (this.mHotwordDetectionConnection != null) {
                HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 0, this.mVoiceInteractionServiceUid);
                this.mHotwordDetectionConnection.detectFromDspSource(keyphraseRecognitionEvent, this.mExternalCallback);
                return;
            }
            HotwordMetricsLogger.writeKeyphraseTriggerEvent(0, 0, this.mVoiceInteractionServiceUid);
            this.mExternalCallback.onKeyphraseDetected(keyphraseRecognitionEvent, (HotwordDetectedResult) null);
        }

        public void onGenericSoundTriggerDetected(SoundTrigger.GenericRecognitionEvent genericRecognitionEvent) throws RemoteException {
            this.mExternalCallback.onGenericSoundTriggerDetected(genericRecognitionEvent);
        }

        public void onError(int i) throws RemoteException {
            this.mExternalCallback.onError(i);
        }

        public void onRecognitionPaused() throws RemoteException {
            this.mExternalCallback.onRecognitionPaused();
        }

        public void onRecognitionResumed() throws RemoteException {
            this.mExternalCallback.onRecognitionResumed();
        }
    }

    public void dump(final String str, final PrintWriter printWriter) {
        ServiceConnection serviceConnection;
        synchronized (this.mLock) {
            printWriter.print(str);
            printWriter.print("mReStartPeriodSeconds=");
            printWriter.println(this.mReStartPeriodSeconds);
            printWriter.print(str);
            printWriter.print("bound for HotwordDetectionService=");
            ServiceConnection serviceConnection2 = this.mRemoteHotwordDetectionService;
            boolean z = true;
            printWriter.println(serviceConnection2 != null && serviceConnection2.isBound());
            printWriter.print(str);
            printWriter.print("bound for VisualQueryDetectionService=");
            if (this.mRemoteVisualQueryDetectionService == null || (serviceConnection = this.mRemoteHotwordDetectionService) == null || !serviceConnection.isBound()) {
                z = false;
            }
            printWriter.println(z);
            printWriter.print(str);
            printWriter.print("mRestartCount=");
            printWriter.println(this.mRestartCount);
            printWriter.print(str);
            printWriter.print("mLastRestartInstant=");
            printWriter.println(this.mLastRestartInstant);
            printWriter.print(str);
            printWriter.println("DetectorSession(s):");
            printWriter.print(str);
            printWriter.print("Num of DetectorSession(s)=");
            printWriter.println(this.mDetectorSessions.size());
            runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((DetectorSession) obj).dumpLocked(str, printWriter);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public class ServiceConnectionFactory {
        public final int mBindingFlags;
        public final Intent mIntent;

        public ServiceConnectionFactory(Intent intent, boolean z) {
            this.mIntent = intent;
            this.mBindingFlags = z ? 4194304 : 0;
        }

        public ServiceConnection createLocked() {
            HotwordDetectionConnection hotwordDetectionConnection = HotwordDetectionConnection.this;
            ServiceConnection serviceConnection = new ServiceConnection(hotwordDetectionConnection.mContext, this.mIntent, this.mBindingFlags, hotwordDetectionConnection.mUser, new Function() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$ServiceConnectionFactory$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ISandboxedDetectionService.Stub.asInterface((IBinder) obj);
                }
            }, HotwordDetectionConnection.this.mRestartCount % 10);
            serviceConnection.connect();
            HotwordDetectionConnection.updateAudioFlinger(serviceConnection, HotwordDetectionConnection.this.mAudioFlinger);
            HotwordDetectionConnection.updateContentCaptureManager(serviceConnection);
            HotwordDetectionConnection.updateSpeechService(serviceConnection);
            HotwordDetectionConnection.this.updateServiceIdentity(serviceConnection);
            return serviceConnection;
        }
    }

    /* loaded from: classes2.dex */
    public class ServiceConnection extends ServiceConnector.Impl<ISandboxedDetectionService> {
        private final int mBindingFlags;
        private final int mInstanceNumber;
        private final Intent mIntent;
        private boolean mIsBound;
        private boolean mIsLoggedFirstConnect;
        private final Object mLock;
        private boolean mRespectServiceConnectionStatusChanged;

        public long getAutoDisconnectTimeoutMs() {
            return -1L;
        }

        public ServiceConnection(Context context, Intent intent, int i, int i2, Function<IBinder, ISandboxedDetectionService> function, int i3) {
            super(context, intent, i, i2, function);
            this.mLock = new Object();
            this.mRespectServiceConnectionStatusChanged = true;
            this.mIsBound = false;
            this.mIsLoggedFirstConnect = false;
            this.mIntent = intent;
            this.mBindingFlags = i;
            this.mInstanceNumber = i3;
        }

        public void onServiceConnectionStatusChanged(ISandboxedDetectionService iSandboxedDetectionService, boolean z) {
            synchronized (this.mLock) {
                if (!this.mRespectServiceConnectionStatusChanged) {
                    Slog.v("HotwordDetectionConnection", "Ignored onServiceConnectionStatusChanged event");
                    return;
                }
                this.mIsBound = z;
                if (!z) {
                    if (HotwordDetectionConnection.this.mDetectorType != 3) {
                        HotwordMetricsLogger.writeDetectorEvent(HotwordDetectionConnection.this.mDetectorType, 7, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                    }
                } else if (!this.mIsLoggedFirstConnect) {
                    this.mIsLoggedFirstConnect = true;
                    if (HotwordDetectionConnection.this.mDetectorType != 3) {
                        HotwordMetricsLogger.writeDetectorEvent(HotwordDetectionConnection.this.mDetectorType, 2, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                    }
                }
            }
        }

        public void binderDied() {
            super.binderDied();
            Slog.w("HotwordDetectionConnection", "binderDied");
            synchronized (this.mLock) {
                if (!this.mRespectServiceConnectionStatusChanged) {
                    Slog.v("HotwordDetectionConnection", "Ignored #binderDied event");
                    return;
                }
                synchronized (HotwordDetectionConnection.this.mLock) {
                    HotwordDetectionConnection.this.runForEachDetectorSessionLocked(new Consumer() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$ServiceConnection$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((DetectorSession) obj).reportErrorLocked(2, "Detection service is dead.");
                        }
                    });
                }
                if (HotwordDetectionConnection.this.mDetectorType != 3) {
                    HotwordMetricsLogger.writeKeyphraseTriggerEvent(HotwordDetectionConnection.this.mDetectorType, 4, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                }
            }
        }

        public boolean bindService(android.content.ServiceConnection serviceConnection) {
            try {
                if (HotwordDetectionConnection.this.mDetectorType != 3) {
                    HotwordMetricsLogger.writeDetectorEvent(HotwordDetectionConnection.this.mDetectorType, 1, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                }
                boolean bindIsolatedService = ((ServiceConnector.Impl) this).mContext.bindIsolatedService(this.mIntent, this.mBindingFlags | 67108865, "hotword_detector_" + this.mInstanceNumber, ((ServiceConnector.Impl) this).mExecutor, serviceConnection);
                if (!bindIsolatedService && HotwordDetectionConnection.this.mDetectorType != 3) {
                    HotwordMetricsLogger.writeDetectorEvent(HotwordDetectionConnection.this.mDetectorType, 3, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                }
                return bindIsolatedService;
            } catch (IllegalArgumentException e) {
                if (HotwordDetectionConnection.this.mDetectorType != 3) {
                    HotwordMetricsLogger.writeDetectorEvent(HotwordDetectionConnection.this.mDetectorType, 3, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
                }
                Slog.wtf("HotwordDetectionConnection", "Can't bind to the hotword detection service!", e);
                return false;
            }
        }

        public boolean isBound() {
            boolean z;
            synchronized (this.mLock) {
                z = this.mIsBound;
            }
            return z;
        }

        public void ignoreConnectionStatusEvents() {
            synchronized (this.mLock) {
                this.mRespectServiceConnectionStatusChanged = false;
            }
        }
    }

    public void createDetectorLocked(PersistableBundle persistableBundle, SharedMemory sharedMemory, IBinder iBinder, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, int i) {
        DetectorSession softwareTrustedHotwordDetectorSession;
        DetectorSession detectorSession = this.mDetectorSessions.get(i);
        if (detectorSession != null) {
            detectorSession.destroyLocked();
            this.mDetectorSessions.remove(i);
        }
        if (i == 1) {
            if (this.mRemoteHotwordDetectionService == null) {
                this.mRemoteHotwordDetectionService = this.mHotwordDetectionServiceConnectionFactory.createLocked();
            }
            softwareTrustedHotwordDetectorSession = new DspTrustedHotwordDetectorSession(this.mRemoteHotwordDetectionService, this.mLock, this.mContext, iBinder, iHotwordRecognitionStatusCallback, this.mVoiceInteractionServiceUid, this.mVoiceInteractorIdentity, this.mScheduledExecutorService, this.mDebugHotwordLogging, this.mRemoteExceptionListener);
        } else if (i == 3) {
            if (this.mRemoteVisualQueryDetectionService == null) {
                this.mRemoteVisualQueryDetectionService = this.mVisualQueryDetectionServiceConnectionFactory.createLocked();
            }
            softwareTrustedHotwordDetectorSession = new VisualQueryDetectorSession(this.mRemoteVisualQueryDetectionService, this.mLock, this.mContext, iBinder, iHotwordRecognitionStatusCallback, this.mVoiceInteractionServiceUid, this.mVoiceInteractorIdentity, this.mScheduledExecutorService, this.mDebugHotwordLogging, this.mRemoteExceptionListener);
        } else {
            if (this.mRemoteHotwordDetectionService == null) {
                this.mRemoteHotwordDetectionService = this.mHotwordDetectionServiceConnectionFactory.createLocked();
            }
            softwareTrustedHotwordDetectorSession = new SoftwareTrustedHotwordDetectorSession(this.mRemoteHotwordDetectionService, this.mLock, this.mContext, iBinder, iHotwordRecognitionStatusCallback, this.mVoiceInteractionServiceUid, this.mVoiceInteractorIdentity, this.mScheduledExecutorService, this.mDebugHotwordLogging, this.mRemoteExceptionListener);
        }
        this.mDetectorSessions.put(i, softwareTrustedHotwordDetectorSession);
        softwareTrustedHotwordDetectorSession.initialize(persistableBundle, sharedMemory);
    }

    public void destroyDetectorLocked(IBinder iBinder) {
        DetectorSession detectorSessionByTokenLocked = getDetectorSessionByTokenLocked(iBinder);
        if (detectorSessionByTokenLocked == null) {
            return;
        }
        detectorSessionByTokenLocked.destroyLocked();
        int indexOfValue = this.mDetectorSessions.indexOfValue(detectorSessionByTokenLocked);
        if (indexOfValue < 0 || indexOfValue > this.mDetectorSessions.size() - 1) {
            return;
        }
        this.mDetectorSessions.removeAt(indexOfValue);
        if (detectorSessionByTokenLocked instanceof VisualQueryDetectorSession) {
            unbindVisualQueryDetectionService();
        }
        if (this.mDetectorSessions.size() == 1 && (this.mDetectorSessions.get(0) instanceof VisualQueryDetectorSession)) {
            unbindHotwordDetectionService();
        }
    }

    public final DetectorSession getDetectorSessionByTokenLocked(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        for (int i = 0; i < this.mDetectorSessions.size(); i++) {
            DetectorSession valueAt = this.mDetectorSessions.valueAt(i);
            if (!valueAt.isDestroyed() && valueAt.isSameToken(iBinder)) {
                return valueAt;
            }
        }
        return null;
    }

    public final DspTrustedHotwordDetectorSession getDspTrustedHotwordDetectorSessionLocked() {
        DetectorSession detectorSession = this.mDetectorSessions.get(1);
        if (detectorSession == null || detectorSession.isDestroyed()) {
            Slog.v("HotwordDetectionConnection", "Not found the Dsp detector");
            return null;
        }
        return (DspTrustedHotwordDetectorSession) detectorSession;
    }

    public final SoftwareTrustedHotwordDetectorSession getSoftwareTrustedHotwordDetectorSessionLocked() {
        DetectorSession detectorSession = this.mDetectorSessions.get(2);
        if (detectorSession == null || detectorSession.isDestroyed()) {
            Slog.v("HotwordDetectionConnection", "Not found the software detector");
            return null;
        }
        return (SoftwareTrustedHotwordDetectorSession) detectorSession;
    }

    public final VisualQueryDetectorSession getVisualQueryDetectorSessionLocked() {
        DetectorSession detectorSession = this.mDetectorSessions.get(3);
        if (detectorSession == null || detectorSession.isDestroyed()) {
            Slog.v("HotwordDetectionConnection", "Not found the look and talk perceiver");
            return null;
        }
        return (VisualQueryDetectorSession) detectorSession;
    }

    public final void runForEachDetectorSessionLocked(Consumer<DetectorSession> consumer) {
        for (int i = 0; i < this.mDetectorSessions.size(); i++) {
            consumer.accept(this.mDetectorSessions.valueAt(i));
        }
    }

    public static void updateAudioFlinger(ServiceConnection serviceConnection, final IBinder iBinder) {
        serviceConnection.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda10
            public final void runNoResult(Object obj) {
                ((ISandboxedDetectionService) obj).updateAudioFlinger(iBinder);
            }
        });
    }

    public static void updateContentCaptureManager(ServiceConnection serviceConnection) {
        final IContentCaptureManager asInterface = IContentCaptureManager.Stub.asInterface(ServiceManager.getService("content_capture"));
        serviceConnection.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda13
            public final void runNoResult(Object obj) {
                HotwordDetectionConnection.lambda$updateContentCaptureManager$8(asInterface, (ISandboxedDetectionService) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$updateContentCaptureManager$8(IContentCaptureManager iContentCaptureManager, ISandboxedDetectionService iSandboxedDetectionService) throws Exception {
        iSandboxedDetectionService.updateContentCaptureManager(iContentCaptureManager, new ContentCaptureOptions((ArraySet) null));
    }

    public static void updateSpeechService(ServiceConnection serviceConnection) {
        final IRecognitionServiceManager asInterface = IRecognitionServiceManager.Stub.asInterface(ServiceManager.getService("speech_recognition"));
        serviceConnection.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda12
            public final void runNoResult(Object obj) {
                ((ISandboxedDetectionService) obj).updateRecognitionServiceManager(asInterface);
            }
        });
    }

    /* renamed from: com.android.server.voiceinteraction.HotwordDetectionConnection$1 */
    /* loaded from: classes2.dex */
    public class IRemoteCallback$StubC17721 extends IRemoteCallback.Stub {
        public static /* synthetic */ int lambda$sendResult$0(int i) {
            return i;
        }

        public IRemoteCallback$StubC17721() {
        }

        public void sendResult(Bundle bundle) throws RemoteException {
            final int callingUid = Binder.getCallingUid();
            ((PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class)).setHotwordDetectionServiceProvider(new PermissionManagerServiceInternal.HotwordDetectionServiceProvider() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$1$$ExternalSyntheticLambda0
                @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal.HotwordDetectionServiceProvider
                public final int getUid() {
                    int lambda$sendResult$0;
                    lambda$sendResult$0 = HotwordDetectionConnection.IRemoteCallback$StubC17721.lambda$sendResult$0(callingUid);
                    return lambda$sendResult$0;
                }
            });
            HotwordDetectionConnection.this.mIdentity = new VoiceInteractionManagerInternal.HotwordDetectionServiceIdentity(callingUid, HotwordDetectionConnection.this.mVoiceInteractionServiceUid);
            HotwordDetectionConnection.this.addServiceUidForAudioPolicy(callingUid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateServiceIdentity$10(ISandboxedDetectionService iSandboxedDetectionService) throws Exception {
        iSandboxedDetectionService.ping(new IRemoteCallback$StubC17721());
    }

    public final void updateServiceIdentity(ServiceConnection serviceConnection) {
        serviceConnection.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda11
            public final void runNoResult(Object obj) {
                HotwordDetectionConnection.this.lambda$updateServiceIdentity$10((ISandboxedDetectionService) obj);
            }
        });
    }

    public final void addServiceUidForAudioPolicy(final int i) {
        this.mScheduledExecutorService.execute(new Runnable() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HotwordDetectionConnection.lambda$addServiceUidForAudioPolicy$11(i);
            }
        });
    }

    public static /* synthetic */ void lambda$addServiceUidForAudioPolicy$11(int i) {
        AudioManagerInternal audioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
        if (audioManagerInternal != null) {
            audioManagerInternal.addAssistantServiceUid(i);
        }
    }

    public final void removeServiceUidForAudioPolicy(final int i) {
        this.mScheduledExecutorService.execute(new Runnable() { // from class: com.android.server.voiceinteraction.HotwordDetectionConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                HotwordDetectionConnection.lambda$removeServiceUidForAudioPolicy$12(i);
            }
        });
    }

    public static /* synthetic */ void lambda$removeServiceUidForAudioPolicy$12(int i) {
        AudioManagerInternal audioManagerInternal = (AudioManagerInternal) LocalServices.getService(AudioManagerInternal.class);
        if (audioManagerInternal != null) {
            audioManagerInternal.removeAssistantServiceUid(i);
        }
    }
}
