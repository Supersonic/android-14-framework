package com.android.server.voiceinteraction;

import android.content.Context;
import android.hardware.soundtrigger.SoundTrigger;
import android.media.permission.Identity;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.voice.HotwordDetectedResult;
import android.service.voice.HotwordDetectionServiceFailure;
import android.service.voice.HotwordRejectedResult;
import android.service.voice.IDspHotwordDetectionCallback;
import android.service.voice.ISandboxedDetectionService;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.infra.ServiceConnector;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.voiceinteraction.HotwordDetectionConnection;
import com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes2.dex */
public final class DspTrustedHotwordDetectorSession extends DetectorSession {
    @GuardedBy({"mLock"})
    public ScheduledFuture<?> mCancellationKeyPhraseDetectionFuture;
    @GuardedBy({"mLock"})
    public HotwordRejectedResult mLastHotwordRejectedResult;
    @GuardedBy({"mLock"})
    public boolean mValidatingDspTrigger;

    public DspTrustedHotwordDetectorSession(HotwordDetectionConnection.ServiceConnection serviceConnection, Object obj, Context context, IBinder iBinder, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, int i, Identity identity, ScheduledExecutorService scheduledExecutorService, boolean z, VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener) {
        super(serviceConnection, obj, context, iBinder, iHotwordRecognitionStatusCallback, i, identity, scheduledExecutorService, z, detectorRemoteExceptionListener);
        this.mValidatingDspTrigger = false;
        this.mLastHotwordRejectedResult = null;
    }

    public void detectFromDspSourceLocked(final SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent, final IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final IDspHotwordDetectionCallback.Stub stub = new IDspHotwordDetectionCallback.Stub() { // from class: com.android.server.voiceinteraction.DspTrustedHotwordDetectorSession.1
            public void onDetected(HotwordDetectedResult hotwordDetectedResult) throws RemoteException {
                synchronized (DspTrustedHotwordDetectorSession.this.mLock) {
                    if (DspTrustedHotwordDetectorSession.this.mCancellationKeyPhraseDetectionFuture != null) {
                        DspTrustedHotwordDetectorSession.this.mCancellationKeyPhraseDetectionFuture.cancel(true);
                    }
                    if (atomicBoolean.get()) {
                        return;
                    }
                    HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 5, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                    if (!DspTrustedHotwordDetectorSession.this.mValidatingDspTrigger) {
                        Slog.i("DspTrustedHotwordDetectorSession", "Ignoring #onDetected due to a process restart or previous #onRejected result = " + DspTrustedHotwordDetectorSession.this.mLastHotwordRejectedResult);
                        HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 7, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        return;
                    }
                    DspTrustedHotwordDetectorSession.this.mValidatingDspTrigger = false;
                    try {
                        DspTrustedHotwordDetectorSession.this.enforcePermissionsForDataDelivery();
                        DspTrustedHotwordDetectorSession.this.enforceExtraKeyphraseIdNotLeaked(hotwordDetectedResult, keyphraseRecognitionEvent);
                        DspTrustedHotwordDetectorSession.this.saveProximityValueToBundle(hotwordDetectedResult);
                        try {
                            HotwordDetectedResult startCopyingAudioStreams = DspTrustedHotwordDetectorSession.this.mHotwordAudioStreamCopier.startCopyingAudioStreams(hotwordDetectedResult);
                            try {
                                iHotwordRecognitionStatusCallback.onKeyphraseDetected(keyphraseRecognitionEvent, startCopyingAudioStreams);
                                Slog.i("DspTrustedHotwordDetectorSession", "Egressed " + HotwordDetectedResult.getUsageSize(startCopyingAudioStreams) + " bits from hotword trusted process");
                                if (DspTrustedHotwordDetectorSession.this.mDebugHotwordLogging) {
                                    Slog.i("DspTrustedHotwordDetectorSession", "Egressed detected result: " + startCopyingAudioStreams);
                                }
                            } catch (RemoteException e) {
                                DspTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                                HotwordMetricsLogger.writeDetectorEvent(1, 17, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                                throw e;
                            }
                        } catch (IOException e2) {
                            try {
                                Slog.w("DspTrustedHotwordDetectorSession", "Ignoring #onDetected due to a IOException", e2);
                                iHotwordRecognitionStatusCallback.onDetectionFailure(new HotwordDetectionServiceFailure(6, "Copy audio stream failure."));
                            } catch (RemoteException e3) {
                                DspTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                                throw e3;
                            }
                        }
                    } catch (SecurityException e4) {
                        Slog.w("DspTrustedHotwordDetectorSession", "Ignoring #onDetected due to a SecurityException", e4);
                        HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 8, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        try {
                            iHotwordRecognitionStatusCallback.onDetectionFailure(new HotwordDetectionServiceFailure(5, "Security exception occurs in #onDetected method."));
                        } catch (RemoteException e5) {
                            DspTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                            HotwordMetricsLogger.writeDetectorEvent(1, 15, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                            throw e5;
                        }
                    }
                }
            }

            public void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException {
                synchronized (DspTrustedHotwordDetectorSession.this.mLock) {
                    if (DspTrustedHotwordDetectorSession.this.mCancellationKeyPhraseDetectionFuture != null) {
                        DspTrustedHotwordDetectorSession.this.mCancellationKeyPhraseDetectionFuture.cancel(true);
                    }
                    if (atomicBoolean.get()) {
                        return;
                    }
                    HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 6, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                    if (!DspTrustedHotwordDetectorSession.this.mValidatingDspTrigger) {
                        Slog.i("DspTrustedHotwordDetectorSession", "Ignoring #onRejected due to a process restart");
                        HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 9, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        return;
                    }
                    DspTrustedHotwordDetectorSession.this.mValidatingDspTrigger = false;
                    try {
                        iHotwordRecognitionStatusCallback.onRejected(hotwordRejectedResult);
                        DspTrustedHotwordDetectorSession.this.mLastHotwordRejectedResult = hotwordRejectedResult;
                        if (DspTrustedHotwordDetectorSession.this.mDebugHotwordLogging && hotwordRejectedResult != null) {
                            Slog.i("DspTrustedHotwordDetectorSession", "Egressed rejected result: " + hotwordRejectedResult);
                        }
                    } catch (RemoteException e) {
                        DspTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                        HotwordMetricsLogger.writeDetectorEvent(1, 16, DspTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        throw e;
                    }
                }
            }
        };
        this.mValidatingDspTrigger = true;
        this.mLastHotwordRejectedResult = null;
        this.mRemoteDetectionService.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.DspTrustedHotwordDetectorSession$$ExternalSyntheticLambda0
            public final void runNoResult(Object obj) {
                DspTrustedHotwordDetectorSession.this.lambda$detectFromDspSourceLocked$1(atomicBoolean, iHotwordRecognitionStatusCallback, keyphraseRecognitionEvent, stub, (ISandboxedDetectionService) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFromDspSourceLocked$1(final AtomicBoolean atomicBoolean, final IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, SoundTrigger.KeyphraseRecognitionEvent keyphraseRecognitionEvent, IDspHotwordDetectionCallback iDspHotwordDetectionCallback, ISandboxedDetectionService iSandboxedDetectionService) throws Exception {
        this.mCancellationKeyPhraseDetectionFuture = this.mScheduledExecutorService.schedule(new Runnable() { // from class: com.android.server.voiceinteraction.DspTrustedHotwordDetectorSession$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DspTrustedHotwordDetectorSession.this.lambda$detectFromDspSourceLocked$0(atomicBoolean, iHotwordRecognitionStatusCallback);
            }
        }, 4000L, TimeUnit.MILLISECONDS);
        iSandboxedDetectionService.detectFromDspSource(keyphraseRecognitionEvent, keyphraseRecognitionEvent.getCaptureFormat(), (long) BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS, iDspHotwordDetectionCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFromDspSourceLocked$0(AtomicBoolean atomicBoolean, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback) {
        atomicBoolean.set(true);
        Slog.w("DspTrustedHotwordDetectorSession", "Timed out on #detectFromDspSource");
        HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 2, this.mVoiceInteractionServiceUid);
        try {
            iHotwordRecognitionStatusCallback.onDetectionFailure(new HotwordDetectionServiceFailure(4, "Timeout to response to the detection result."));
        } catch (RemoteException e) {
            Slog.w("DspTrustedHotwordDetectorSession", "Failed to report onError status: ", e);
            HotwordMetricsLogger.writeDetectorEvent(1, 15, this.mVoiceInteractionServiceUid);
            notifyOnDetectorRemoteException();
        }
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void informRestartProcessLocked() {
        Slog.v("DspTrustedHotwordDetectorSession", "informRestartProcessLocked");
        if (this.mValidatingDspTrigger) {
            try {
                this.mCallback.onRejected(new HotwordRejectedResult.Builder().build());
                HotwordMetricsLogger.writeKeyphraseTriggerEvent(1, 10, this.mVoiceInteractionServiceUid);
            } catch (RemoteException unused) {
                Slog.w("DspTrustedHotwordDetectorSession", "Failed to call #rejected");
                HotwordMetricsLogger.writeDetectorEvent(1, 16, this.mVoiceInteractionServiceUid);
                notifyOnDetectorRemoteException();
            }
            this.mValidatingDspTrigger = false;
        }
        this.mUpdateStateAfterStartFinished.set(false);
        try {
            this.mCallback.onProcessRestarted();
        } catch (RemoteException e) {
            Slog.w("DspTrustedHotwordDetectorSession", "Failed to communicate #onProcessRestarted", e);
            HotwordMetricsLogger.writeDetectorEvent(1, 18, this.mVoiceInteractionServiceUid);
            notifyOnDetectorRemoteException();
        }
        this.mPerformingExternalSourceHotwordDetection = false;
        closeExternalAudioStreamLocked("process restarted");
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
        printWriter.print(str);
        printWriter.print("mValidatingDspTrigger=");
        printWriter.println(this.mValidatingDspTrigger);
    }
}
