package com.android.server.voiceinteraction;

import android.content.Context;
import android.media.AudioFormat;
import android.media.permission.Identity;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.service.voice.HotwordDetectedResult;
import android.service.voice.HotwordDetectionServiceFailure;
import android.service.voice.HotwordRejectedResult;
import android.service.voice.IDspHotwordDetectionCallback;
import android.service.voice.IMicrophoneHotwordDetectionVoiceInteractionCallback;
import android.service.voice.ISandboxedDetectionService;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IHotwordRecognitionStatusCallback;
import com.android.internal.infra.ServiceConnector;
import com.android.server.voiceinteraction.HotwordDetectionConnection;
import com.android.server.voiceinteraction.VoiceInteractionManagerServiceImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes2.dex */
public final class SoftwareTrustedHotwordDetectorSession extends DetectorSession {
    @GuardedBy({"mLock"})
    public boolean mPerformingSoftwareHotwordDetection;
    public IMicrophoneHotwordDetectionVoiceInteractionCallback mSoftwareCallback;

    public SoftwareTrustedHotwordDetectorSession(HotwordDetectionConnection.ServiceConnection serviceConnection, Object obj, Context context, IBinder iBinder, IHotwordRecognitionStatusCallback iHotwordRecognitionStatusCallback, int i, Identity identity, ScheduledExecutorService scheduledExecutorService, boolean z, VoiceInteractionManagerServiceImpl.DetectorRemoteExceptionListener detectorRemoteExceptionListener) {
        super(serviceConnection, obj, context, iBinder, iHotwordRecognitionStatusCallback, i, identity, scheduledExecutorService, z, detectorRemoteExceptionListener);
    }

    public void startListeningFromMicLocked(AudioFormat audioFormat, IMicrophoneHotwordDetectionVoiceInteractionCallback iMicrophoneHotwordDetectionVoiceInteractionCallback) {
        this.mSoftwareCallback = iMicrophoneHotwordDetectionVoiceInteractionCallback;
        if (this.mPerformingSoftwareHotwordDetection) {
            Slog.i("SoftwareTrustedHotwordDetectorSession", "Hotword validation is already in progress, ignoring.");
            return;
        }
        this.mPerformingSoftwareHotwordDetection = true;
        startListeningFromMicLocked();
    }

    public final void startListeningFromMicLocked() {
        final IDspHotwordDetectionCallback.Stub stub = new IDspHotwordDetectionCallback.Stub() { // from class: com.android.server.voiceinteraction.SoftwareTrustedHotwordDetectorSession.1
            public void onDetected(HotwordDetectedResult hotwordDetectedResult) throws RemoteException {
                synchronized (SoftwareTrustedHotwordDetectorSession.this.mLock) {
                    HotwordMetricsLogger.writeKeyphraseTriggerEvent(2, 5, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                    if (!SoftwareTrustedHotwordDetectorSession.this.mPerformingSoftwareHotwordDetection) {
                        Slog.i("SoftwareTrustedHotwordDetectorSession", "Hotword detection has already completed");
                        HotwordMetricsLogger.writeKeyphraseTriggerEvent(2, 7, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        return;
                    }
                    SoftwareTrustedHotwordDetectorSession.this.mPerformingSoftwareHotwordDetection = false;
                    try {
                        SoftwareTrustedHotwordDetectorSession.this.enforcePermissionsForDataDelivery();
                        SoftwareTrustedHotwordDetectorSession.this.saveProximityValueToBundle(hotwordDetectedResult);
                        try {
                            HotwordDetectedResult startCopyingAudioStreams = SoftwareTrustedHotwordDetectorSession.this.mHotwordAudioStreamCopier.startCopyingAudioStreams(hotwordDetectedResult);
                            try {
                                SoftwareTrustedHotwordDetectorSession.this.mSoftwareCallback.onDetected(startCopyingAudioStreams, (AudioFormat) null, (ParcelFileDescriptor) null);
                                Slog.i("SoftwareTrustedHotwordDetectorSession", "Egressed " + HotwordDetectedResult.getUsageSize(startCopyingAudioStreams) + " bits from hotword trusted process");
                                if (SoftwareTrustedHotwordDetectorSession.this.mDebugHotwordLogging) {
                                    Slog.i("SoftwareTrustedHotwordDetectorSession", "Egressed detected result: " + startCopyingAudioStreams);
                                }
                            } catch (RemoteException e) {
                                SoftwareTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                                HotwordMetricsLogger.writeDetectorEvent(2, 17, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                                throw e;
                            }
                        } catch (IOException e2) {
                            Slog.w("SoftwareTrustedHotwordDetectorSession", "Ignoring #onDetected due to a IOException", e2);
                            try {
                                SoftwareTrustedHotwordDetectorSession.this.mSoftwareCallback.onError(new HotwordDetectionServiceFailure(6, "Copy audio stream failure."));
                            } catch (RemoteException e3) {
                                SoftwareTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                                HotwordMetricsLogger.writeDetectorEvent(2, 15, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                                throw e3;
                            }
                        }
                    } catch (SecurityException e4) {
                        Slog.w("SoftwareTrustedHotwordDetectorSession", "Ignoring #onDetected due to a SecurityException", e4);
                        HotwordMetricsLogger.writeKeyphraseTriggerEvent(2, 8, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                        try {
                            SoftwareTrustedHotwordDetectorSession.this.mSoftwareCallback.onError(new HotwordDetectionServiceFailure(5, "Security exception occurs in #onDetected method."));
                        } catch (RemoteException e5) {
                            SoftwareTrustedHotwordDetectorSession.this.notifyOnDetectorRemoteException();
                            HotwordMetricsLogger.writeDetectorEvent(2, 15, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
                            throw e5;
                        }
                    }
                }
            }

            public void onRejected(HotwordRejectedResult hotwordRejectedResult) throws RemoteException {
                HotwordMetricsLogger.writeKeyphraseTriggerEvent(2, 6, SoftwareTrustedHotwordDetectorSession.this.mVoiceInteractionServiceUid);
            }
        };
        this.mRemoteDetectionService.run(new ServiceConnector.VoidJob() { // from class: com.android.server.voiceinteraction.SoftwareTrustedHotwordDetectorSession$$ExternalSyntheticLambda1
            public final void runNoResult(Object obj) {
                ((ISandboxedDetectionService) obj).detectFromMicrophoneSource(null, 1, null, null, stub);
            }
        });
        HotwordMetricsLogger.writeDetectorEvent(2, 9, this.mVoiceInteractionServiceUid);
    }

    public void stopListeningFromMicLocked() {
        if (!this.mPerformingSoftwareHotwordDetection) {
            Slog.i("SoftwareTrustedHotwordDetectorSession", "Hotword detection is not running");
            return;
        }
        this.mPerformingSoftwareHotwordDetection = false;
        this.mRemoteDetectionService.run(new SoftwareTrustedHotwordDetectorSession$$ExternalSyntheticLambda0());
        closeExternalAudioStreamLocked("stopping requested");
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void informRestartProcessLocked() {
        Slog.v("SoftwareTrustedHotwordDetectorSession", "informRestartProcessLocked");
        this.mUpdateStateAfterStartFinished.set(false);
        try {
            this.mCallback.onProcessRestarted();
        } catch (RemoteException e) {
            Slog.w("SoftwareTrustedHotwordDetectorSession", "Failed to communicate #onProcessRestarted", e);
            HotwordMetricsLogger.writeDetectorEvent(2, 18, this.mVoiceInteractionServiceUid);
            notifyOnDetectorRemoteException();
        }
        if (this.mPerformingSoftwareHotwordDetection) {
            Slog.i("SoftwareTrustedHotwordDetectorSession", "Process restarted: calling startRecognition() again");
            startListeningFromMicLocked();
        }
        this.mPerformingExternalSourceHotwordDetection = false;
        closeExternalAudioStreamLocked("process restarted");
    }

    @Override // com.android.server.voiceinteraction.DetectorSession
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
        printWriter.print(str);
        printWriter.print("mPerformingSoftwareHotwordDetection=");
        printWriter.println(this.mPerformingSoftwareHotwordDetection);
    }
}
